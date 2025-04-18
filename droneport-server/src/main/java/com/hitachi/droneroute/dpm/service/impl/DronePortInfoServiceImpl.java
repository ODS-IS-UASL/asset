package com.hitachi.droneroute.dpm.service.impl;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.service.DroneRouteCommonService;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.Base64Utils;
import com.hitachi.droneroute.cmn.util.DateTimeUtils;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortEnvironmentInfoResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseElement;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterResponseDto;
import com.hitachi.droneroute.dpm.entity.DronePortInfoEntity;
import com.hitachi.droneroute.dpm.entity.DronePortReserveInfoEntity;
import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;
import com.hitachi.droneroute.dpm.entity.VisTelemetryInfoEntity;
import com.hitachi.droneroute.dpm.repository.DronePortInfoRepository;
import com.hitachi.droneroute.dpm.repository.DronePortReserveInfoRepository;
import com.hitachi.droneroute.dpm.repository.DronePortStatusRepository;
import com.hitachi.droneroute.dpm.repository.VisTelemetryInfoRepository;
import com.hitachi.droneroute.dpm.service.DronePortInfoService;
import com.hitachi.droneroute.dpm.specification.DronePortInfoSpecification;
import com.hitachi.droneroute.dpm.specification.DronePortReserveInfoSpecification;

import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.RequiredArgsConstructor;

/**
 *  ドローンポート情報サービス実装クラス
 * @author Hiroshi Toyoda
 *
 */
@RequiredArgsConstructor
@Service
public class DronePortInfoServiceImpl implements DronePortInfoService, DroneRouteCommonService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final DronePortInfoRepository dronePortInfoRepository;
	
	private final VisTelemetryInfoRepository visTelemetryInfoRepository;
	
	private final DronePortStatusRepository dronePortStatusRepository;
	
	private final DronePortReserveInfoRepository dronePortReserveInfoRepository;
	
	private final AircraftInfoRepository aircraftInfoRepository;
	
	private final SystemSettings systemSettings;
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public DronePortInfoRegisterResponseDto register(DronePortInfoRegisterRequestDto dto) {
		// 格納中機体IDの存在チェック
		checkStoredAircraftIdValid(dto.getStoredAircraftId());
		// エンティティ作成
		DronePortInfoEntity newEntity = new DronePortInfoEntity();
		// 登録する値を設定
		String newDronePortId = createDronePortId(dto.getDronePortManufacturerId());
		Optional<DronePortInfoEntity> optIdCheckEntity = dronePortInfoRepository.findByDronePortId(newDronePortId);
		if (optIdCheckEntity.isPresent()) {
			// 新たに採番したドローンポートIDが存在する場合は、ID重複でエラーとする
			throw new ServiceErrorException(
					MessageFormat.format(
							"ドローンポートIDが重複しています。ドローンポートID:{0}",
							newDronePortId));
		}
		setEntity(dto, newEntity);
		newEntity.setDronePortId(newDronePortId);
		newEntity.setCreateTime(DateTimeUtils.getUtcCurrentTimestamp());
		newEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
		newEntity.setOperatorId(dto.getOperatorId());
		// DB登録呼び出し
		DronePortInfoEntity registeredEntity = dronePortInfoRepository.save(newEntity);
		
		// ドローンポート状態テーブル登録
		DronePortStatusEntity newStatusEntity = new DronePortStatusEntity();
		setRegisterEntity(dto, newStatusEntity);
		newStatusEntity.setDronePortId(newDronePortId);
		newStatusEntity.setCreateTime(DateTimeUtils.getUtcCurrentTimestamp());
		newStatusEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
		newStatusEntity.setOperatorId(dto.getOperatorId());
		// DB登録呼出
		dronePortStatusRepository.save(newStatusEntity);
		
		// 新規の登録なので予約解除は不要
		
		// 処理結果編集
		DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
		responseDto.setDronePortId(registeredEntity.getDronePortId());
		
		return responseDto;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public DronePortInfoRegisterResponseDto update(	DronePortInfoRegisterRequestDto dto) {
		// 格納中機体IDの存在チェック
		checkStoredAircraftIdValid(dto.getStoredAircraftId());
		
		// ドローンポート情報を更新
		updateDronePortInfo(dto);
		
		// ドローンポート状態を更新、予約を取消
		updateDronePortStatus(dto);
		
		// 処理結果編集
		DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
		responseDto.setDronePortId(dto.getDronePortId());
		
		return responseDto;
	}
	
	/**
	 * ドローンポート情報を更新する
	 * @param dto
	 */
	private void updateDronePortInfo(DronePortInfoRegisterRequestDto dto) {
		// 既存レコードを検索
		Optional<DronePortInfoEntity> optInfoEntity = 
				dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(
						dto.getDronePortId());
		if (optInfoEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"ドローンポート情報が見つかりません。ドローンポートID:{0}", 
							dto.getDronePortId())
					);
		}
		// 既存エンティティに更新する値を設定する
		DronePortInfoEntity entity = optInfoEntity.get();
		// オペレータIDをチェック
		checkUpdatePermissionByOperatorId(entity, dto.getOperatorId());
		
		if (!isNullDronePortInfo(dto)) {
			// ドローンポート情報テーブルに更新する項目がある場合
			setEntity(dto, entity);
			entity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
			// DB更新呼出
			dronePortInfoRepository.save(entity);
		}
	}
	
	/**
	 * ドローンポート状態を更新、予約情報を取り消す
	 * @param dto
	 */
	private void updateDronePortStatus(DronePortInfoRegisterRequestDto dto) {
		Optional<DronePortStatusEntity> optStatusEntity =
				dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(
						dto.getDronePortId());
		if (optStatusEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"ドローンポート状態が見つかりません。ドローンポートID:{0}", 
							dto.getDronePortId())
					);
		}
		
		DronePortStatusEntity entity = optStatusEntity.get();
		// オペレータIDをチェック
		checkUpdatePermissionByOperatorId(entity, dto.getOperatorId());
		
		// ドローンポート状態について、変更差分からドローンポート状態更新、予約取消を行う
		if (!isNullDronePortStatus(dto)) {
			// ドローンポート状態テーブルに更新する項目がある場合
			
			// 使用不可(既存)から、メンテナンス中に変更はエラーとする
			if (Objects.nonNull(entity.getInactiveStatus()) 
					&& entity.getInactiveStatus() == DronePortConstants.ACTIVE_STATUS_UNAVAILABLE
					&& dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_MAINTENANCE) {
				throw new ServiceErrorException("動作状況を、使用不可からメンテナンス中に変更することはできません。");
			}
			// 既存エンティティに更新する値を設定する
			setUpdateEntity(dto, entity);
			entity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
			// DB更新呼出
			dronePortStatusRepository.save(entity);

			if (Objects.nonNull(dto.getActiveStatus()) && (
					dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_MAINTENANCE
					|| dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_UNAVAILABLE)) {
				// 使用不可日時範囲に重なる予約情報を検索
				List<DronePortReserveInfoEntity> overlappedReservation 
				= findOverlapedReservation(dto.getDronePortId(), 
						dto.getInactiveTimeFrom(), dto.getInactiveTimeTo());
				// 予約取消を行う
				overlappedReservation.forEach(e -> {
					e.setReservationActiveFlag(false);
					e.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
					dronePortReserveInfoRepository.save(e);
				});
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public void delete(String dronePortId, DronePortInfoDeleteRequestDto dto) {
		// 既存レコードを検索
		Optional<DronePortInfoEntity> optInfoEntity = 
				dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(
						dronePortId);
		if (optInfoEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"ドローンポート情報が見つかりません。ドローンポートID:{0}", 
							dronePortId)
					);
		}
		Optional<DronePortStatusEntity> optStatusEntity =
				dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(
						dronePortId);
		if (optStatusEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"ドローンポート状態が見つかりません。ドローンポートID:{0}", 
							dronePortId)
					);
		}
		DronePortInfoEntity entity = optInfoEntity.get();
		// オペレータIDをチェック
		checkUpdatePermissionByOperatorId(entity, dto.getOperatorId());
		DronePortStatusEntity statusEntity = optStatusEntity.get();
		// オペレータIDをチェック
		checkUpdatePermissionByOperatorId(statusEntity, dto.getOperatorId());
		
		// ドローンポート情報テーブルを更新
		entity.setDeleteFlag(true);
		entity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
		// DB更新呼出
		dronePortInfoRepository.save(entity);
		// ドローンポート情報テーブルを更新
		statusEntity.setDeleteFlag(true);
		statusEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
		// DB更新呼出
		dronePortStatusRepository.save(statusEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DronePortInfoListResponseDto getList(DronePortInfoListRequestDto dto) {
		// 現在日時を取得する
		LocalDateTime currentDateTime = LocalDateTime.now();
		// ソート、ページ、検索条件を生成する
		Sort sort = createSort(dto.getSortOrders(), dto.getSortColumns(), logger);
		Pageable pageable = createPageRequest(dto.getPerPage(), dto.getPage(), sort);
		Specification<DronePortInfoEntity> spec = createSpecification(dto, Timestamp.valueOf(currentDateTime));
		// ドローンポート情報を検索
		Page<DronePortInfoEntity> pageResult = null;
		List<DronePortInfoEntity> entityListResult = null;
		if (Objects.isNull(pageable)) {
			// CT-005 Start
			// ページ制御なし
			if(Objects.isNull(sort)) {
				// ソートなし
				entityListResult = dronePortInfoRepository.findAll(spec);
			} else {
				// ソートあり
				entityListResult = dronePortInfoRepository.findAll(spec, sort);
			}
			// CT-005 End
		} else {
			// ページ制御あり
			pageResult = dronePortInfoRepository.findAll(spec, pageable);
			entityListResult = pageResult.getContent();
			logger.debug("search result:" + pageResult.toString());
		}
		// 処理結果編集
		List<DronePortInfoListResponseElement> detailList = new ArrayList<>();
		for (DronePortInfoEntity entity : entityListResult) {
			DronePortInfoListResponseElement detailDto = new DronePortInfoListResponseElement();
			setDetailResponseDto(currentDateTime, entity, detailDto);
			detailList.add(detailDto);
		}
		DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
		responseDto.setData(detailList);
		if (Objects.nonNull(pageResult)) {
			// ページ情報の結果を設定する
			responseDto.setPerPage(pageResult.getSize());
			responseDto.setCurrentPage(pageResult.getNumber()+1);
			responseDto.setLastPage(pageResult.getTotalPages());
			responseDto.setTotal((int)pageResult.getTotalElements());
		}
		return responseDto;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DronePortInfoDetailResponseDto getDetail(String dronePortId) {
		// 現在日時を取得する
		LocalDateTime currentDateTime = LocalDateTime.now();
		// 既存レコードを検索
		Optional<DronePortInfoEntity> optEntity = 
				dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(
						dronePortId);
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"ドローンポート情報が見つかりません。ドローンポートID:{0}", 
							dronePortId)
					);
		}
		// 処理結果編集
		DronePortInfoEntity entity = optEntity.get();
		DronePortInfoDetailResponseDto responseDto = new DronePortInfoDetailResponseDto();
		setDetailResponseDto(currentDateTime, entity, responseDto);
		return responseDto;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public DronePortEnvironmentInfoResponseDto getEnvironment(String dronePortId) {
		Optional<DronePortInfoEntity> optEntity = 
				dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId);
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"ドローンポート情報が見つかりません。ドローンポートID:{0}", 
							dronePortId)
					);
		}
		
		VisTelemetryInfoEntity visTelm = new VisTelemetryInfoEntity();
		
		// VISテレメトリ情報テーブルを検索
		Optional<VisTelemetryInfoEntity> optVisTelm =
				visTelemetryInfoRepository.findByDroneportId(dronePortId);
		if (optVisTelm.isPresent()) {
			visTelm = optVisTelm.get();
		}
		// 処理結果編集
		DronePortEnvironmentInfoResponseDto responseDto = new DronePortEnvironmentInfoResponseDto();
		responseDto.setDronePortId(dronePortId);
		responseDto.setWindSpeed(visTelm.getWindSpeed());
		responseDto.setWindDirection(visTelm.getWindDirection());
		responseDto.setTemp(visTelm.getTemp());
		responseDto.setPressure(visTelm.getPressure());
		responseDto.setRainfall(visTelm.getRainfall());
		responseDto.setObstacleDetected(visTelm.getInvasionFlag());
		responseDto.setObservationTime(
				Objects.isNull(visTelm.getObservationTime()) ? null : 
					StringUtils.toUtcDateTimeString(
						visTelm.getObservationTime().toLocalDateTime()));
		return responseDto;
	}
	
	/**
	 * base64をバイト型にでコード<br>
	 * 入力チェックの前に実施すること
	 * @param dto ドローンポート情報登録更新要求
	 */
	@Override
	public void decodeBinary(DronePortInfoRegisterRequestDto dto) {
		if (dto.getImageData() != null) {
			if (dto.getImageData().length() == 0) {
				dto.setImageBinary(new byte[] {});
			} else {
				Base64Utils util = new Base64Utils(
						systemSettings.getStringValueArray(
								DronePortConstants.SETTINGS_IMAGE_DATA, 
								DronePortConstants.SETTINGS_SUPPORT_FORMAT));
				if (util.checkSubtype(dto.getImageData())) {
					dto.setImageBinary(util.getBinaryData(dto.getImageData()));
				}
			}
		}
	}
	
	/**
	 * エンティティをドローンポート情報詳細取得応答に設定
	 * @param entity エンティティ
	 * @param dto ドローンポート情報詳細取得応答
	 */
	private void setDetailResponseDto(
			LocalDateTime currentDateTime,
			DronePortInfoEntity entity, DronePortInfoDetailResponseDto dto) {
		BeanUtils.copyProperties(entity, dto);
		// 現在の動作状況を設定
		dto.setActiveStatus(getCurrentStatus(currentDateTime, entity.getDronePortStatusEntity()));
		// 予定された動作状態を設定
		Object[] scheduledStatus = getScheduledStatus(currentDateTime, entity.getDronePortStatusEntity());
		dto.setScheduledStatus((Integer)scheduledStatus[0]);
		dto.setInactiveTimeFrom((String)scheduledStatus[1]);
		dto.setInactiveTimeTo((String)scheduledStatus[2]);
				
		dto.setUpdateTime(getUpdateTime(entity));
		if (Objects.nonNull(entity.getImageBinary())
				&& entity.getImageBinary().length > 0) {
			Base64Utils util = new Base64Utils(
					systemSettings.getStringValueArray(
							DronePortConstants.SETTINGS_IMAGE_DATA, 
							DronePortConstants.SETTINGS_SUPPORT_FORMAT));
			// データURI付きのbase64文字列を設定する
			dto.setImageData(
					util.createDataUriWithBase64(
							entity.getImageFormat(), entity.getImageBinary()));
		}
		dto.setStoredAircraftId(
				Objects.isNull(entity.getDronePortStatusEntity().getStoredAircraftId()) ? 
						null : entity.getDronePortStatusEntity().getStoredAircraftId().toString());
	}
	
	/**
	 * 検索で取得したドローンポート情報を、ドローンポート情報一覧取得応答要素に設定する
	 * @param entity (ドローンポート状態がjoinされている)
	 * @param dto ドローンポート情報一覧取得応答要素
	 */
	private void setDetailResponseDto(
			LocalDateTime currentDateTime,
			DronePortInfoEntity entity, DronePortInfoListResponseElement dto) {
		BeanUtils.copyProperties(entity, dto);
		// 現在の動作状況を設定
		dto.setActiveStatus(getCurrentStatus(currentDateTime, entity.getDronePortStatusEntity()));
		// 予定された動作状態を設定
		Object[] scheduledStatus = getScheduledStatus(currentDateTime, entity.getDronePortStatusEntity());
		dto.setScheduledStatus((Integer)scheduledStatus[0]);
		dto.setInactiveTimeFrom((String)scheduledStatus[1]);
		dto.setInactiveTimeTo((String)scheduledStatus[2]);
		
		dto.setUpdateTime(getUpdateTime(entity));
		dto.setStoredAircraftId(
				Objects.isNull(entity.getDronePortStatusEntity().getStoredAircraftId()) ? 
						null : entity.getDronePortStatusEntity().getStoredAircraftId().toString());
	}
	
	/**
	 * 現在の動作状況を取得
	 * @param currentDateTime 現在日時
	 * @param entity ドローンポート状態エンティティ
	 * @return 現在の動作状況
	 */
	private Integer getCurrentStatus(LocalDateTime currentDateTime, DronePortStatusEntity entity) {
		Integer result = null;
		
		Range<LocalDateTime> t = entity.getInactiveTime();
		if (Objects.nonNull(t)
				&& ((currentDateTime.isAfter(t.lower()) || currentDateTime.isEqual(t.lower()))
						&& (t.hasUpperBound() ? currentDateTime.isBefore(t.upper()) : true ))) {
			result = entity.getInactiveStatus();
		} else {
			result = entity.getActiveStatus();
		}
		
		return result;
	}

	/**
	 * 予定された動作状態と使用不可時間を取得
	 * @param currentDateTime 現在日時
	 * @param entity ドローンポート状態エンティティ
	 * @return 予定された動作状態と使用不可時間
	 */
	private Object[] getScheduledStatus(LocalDateTime currentDateTime, DronePortStatusEntity entity) {
		Range<LocalDateTime> t = entity.getInactiveTime();
		Integer status = null;
		String from = null;
		String to = null;
		if (Objects.nonNull(t)) {
			if (currentDateTime.isBefore(t.lower())) {
				status = entity.getInactiveStatus();
				from = StringUtils.toUtcDateTimeString(t.lower());
				to = StringUtils.toUtcDateTimeString(t.upper());
			}
			if (((currentDateTime.isAfter(t.lower()) || currentDateTime.isEqual(t.lower()))
					&& (t.hasUpperBound() ? currentDateTime.isBefore(t.upper()) : true))) {
				from = StringUtils.toUtcDateTimeString(t.lower());
				to = StringUtils.toUtcDateTimeString(t.upper());
			}
		}
		return new Object[] {status, from , to};
	}
	
	/**
	 * ドローンポート情報とドローンポート状態の更新日時を比較して、最新値を返却する
	 * @param entity ドローンポート情報(ドローンポート状態がjoinされている)
	 * @return
	 */
	private String getUpdateTime(DronePortInfoEntity entity) {
		String wkTimestamp = null;
		if (entity.getUpdateTime().compareTo(entity.getDronePortStatusEntity().getUpdateTime()) > 0) {
			// ドローンポート情報の更新日の方が、ドローンポート状態の更新日より新しい場合
			wkTimestamp = StringUtils.toUtcDateTimeString(
					entity.getUpdateTime().toLocalDateTime());
		} else {
			wkTimestamp = StringUtils.toUtcDateTimeString(
					entity.getDronePortStatusEntity().getUpdateTime().toLocalDateTime());
		}
		return wkTimestamp;
	}

	/**
	 * ドローンポート情報登録更新要求をドローンポート情報エンティティに設定
	 * @param dto ドローンポート情報登録更新要求
	 * @param entity ドローンポート情報エンティティ
	 */
	private void setEntity(DronePortInfoRegisterRequestDto dto, DronePortInfoEntity entity) {
		if (dto.getDronePortName() != null) {
			entity.setDronePortName(dto.getDronePortName());
		}
		if (dto.getAddress() != null) {
			entity.setAddress(dto.getAddress());
		}
		if (dto.getManufacturer() != null) {
			entity.setManufacturer(dto.getManufacturer());
		}
		if (dto.getSerialNumber() != null) {
			entity.setSerialNumber(dto.getSerialNumber());
		}
		if (dto.getPortType() != null) {
			entity.setPortType(dto.getPortType());
		}
		if (dto.getVisDronePortCompanyId() != null) {
			entity.setVisDronePortCompanyId(dto.getVisDronePortCompanyId());
		}
		if (dto.getLon() != null) {
			entity.setLon(dto.getLon());
		}
		if (dto.getLat() != null) {
			entity.setLat(dto.getLat());
		}
		if (dto.getAlt() != null) {
			entity.setAlt(Double.valueOf(dto.getAlt().doubleValue()));
		}
		if (dto.getSupportDroneType() != null) {
			entity.setSupportDroneType(dto.getSupportDroneType());
		}
		if (dto.getImageBinary() != null) {
			Base64Utils util = new Base64Utils(
					systemSettings.getStringValueArray(
							DronePortConstants.SETTINGS_IMAGE_DATA, 
							DronePortConstants.SETTINGS_SUPPORT_FORMAT));
			// データURIから画像フォーマットを取得する
			entity.setImageFormat(util.getSubtype(dto.getImageData()));
			// 事前にbase64からバイナリ変換を行っていること。
			entity.setImageBinary(dto.getImageBinary());
		}
		entity.setDeleteFlag(false);
	}
	
	/**
	 * ドローンポート情報登録更新要求をドローンポート状態エンティティに設定(登録用)
	 * @param dto ドローンポート情報登録更新要求
	 * @param entity ドローンポート状態エンティティ
	 */
	private void setRegisterEntity(DronePortInfoRegisterRequestDto dto, DronePortStatusEntity entity) {
		// 登録時は動作状況は必須
		if (dto.getActiveStatus().equals(DronePortConstants.ACTIVE_STATUS_PREPARING) 
				|| dto.getActiveStatus().equals(DronePortConstants.ACTIVE_STATUS_AVAILABLE)) {
			entity.setActiveStatus(dto.getActiveStatus());
			// 登録時に動作状況:準備中、使用可にする場合は、動作状況(使用不可):未設定とする
			entity.setInactiveStatus(null);
			entity.setInactiveTime(null);
		} else if (dto.getActiveStatus().equals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE) 
				|| dto.getActiveStatus().equals(DronePortConstants.ACTIVE_STATUS_MAINTENANCE)) {
			// 登録時に動作状況:使用不可、メンテナンス中にする場合は、動作状況(使用可):準備中とする
			entity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
			entity.setInactiveStatus(dto.getActiveStatus());
		}
		if (Objects.nonNull(dto.getInactiveTimeFrom())) {
			// 動作状況が使用不可、メンテナンスの時だけ、使用不可日時が設定される(入力チェック済み)
			entity.setInactiveTime(Range.localDateTimeRange(
					String.format("[%s,%s)", 
							StringUtils.parseDatetimeStringToLocalDateTime(dto.getInactiveTimeFrom()).toString(),
							org.springframework.util.StringUtils.hasText(dto.getInactiveTimeTo()) ? 
									StringUtils.parseDatetimeStringToLocalDateTime(
											dto.getInactiveTimeTo()).toString() 
									: ""
							)
					));
		}
		if (Objects.nonNull(dto.getStoredAircraftId())) {
			entity.setStoredAircraftId(UUID.fromString(dto.getStoredAircraftId()));
		}
		entity.setDeleteFlag(false);
	}
	
	/**
	 * ドローンポート情報登録更新要求をドローンポート状態エンティティに設定(更新用)
	 * @param dto ドローンポート情報登録更新要求
	 * @param entity ドローンポート状態エンティティ
	 */
	private void setUpdateEntity(DronePortInfoRegisterRequestDto dto, DronePortStatusEntity entity) {
		if (Objects.nonNull(dto.getActiveStatus())) {
			// 登録時は動作状況は必須なので、常にここを通る
			if (dto.getActiveStatus().equals(DronePortConstants.ACTIVE_STATUS_PREPARING) 
					|| dto.getActiveStatus().equals(DronePortConstants.ACTIVE_STATUS_AVAILABLE)) {
				entity.setActiveStatus(dto.getActiveStatus());
				entity.setInactiveStatus(null);
				// 動作状況:準備中、使用可に更新する場合は、必ず使用不可時間は未入力なので、DBはnullに上書きする
				entity.setInactiveTime(null);
			} else if (dto.getActiveStatus().equals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE) 
					|| dto.getActiveStatus().equals(DronePortConstants.ACTIVE_STATUS_MAINTENANCE)) {
				// 動作状況(使用可)は更新しない
				entity.setInactiveStatus(dto.getActiveStatus());
			}
		}
		if (Objects.nonNull(dto.getInactiveTimeFrom())) {
			// 動作状況が使用不可、メンテナンスの時だけ、使用不可日時が設定される(入力チェック済み)
			entity.setInactiveTime(Range.localDateTimeRange(
					String.format("[%s,%s)", 
							StringUtils.parseDatetimeStringToLocalDateTime(dto.getInactiveTimeFrom()).toString(),
							org.springframework.util.StringUtils.hasText(dto.getInactiveTimeTo()) ? 
									StringUtils.parseDatetimeStringToLocalDateTime(
											dto.getInactiveTimeTo()).toString() 
									: ""
							)
					));
		}
		if (Objects.nonNull(dto.getStoredAircraftId())) {
			entity.setStoredAircraftId(UUID.fromString(dto.getStoredAircraftId()));
		}
		// 削除フラグは更新しない
	}
	
	/**
	 * ドローンポートIDを採番する
	 * @param mfr ドローンポートメーカーID
	 * @return
	 */
	private String createDronePortId(String mfr) {
		Long seq = dronePortInfoRepository.getNextSequenceValue();
		String opr = systemSettings.getString(
				DronePortConstants.SETTINGS_DRONEPORT_ID, 
				DronePortConstants.SETTINGS_DRONEPORT_ID_OPR);
		String format = systemSettings.getString(
				DronePortConstants.SETTINGS_DRONEPORT_ID, 
				DronePortConstants.SETTINGS_DRONEPORT_ID_FORMAT);
		return String.format(format, opr, mfr, seq);
	}
	
	/**
	 * ドローンポート情報の更新対象項目のnull状態を判定する
	 * @param dto ドローンポート情報登録更新要求
	 * @return true:更新項目が全てnull, false:null以外の項目が1つ以上ある
	 */
	private boolean isNullDronePortInfo(DronePortInfoRegisterRequestDto dto) {
		return Objects.isNull(dto.getDronePortName())
				&& Objects.isNull(dto.getAddress())
				&& Objects.isNull(dto.getManufacturer())
				&& Objects.isNull(dto.getSerialNumber())
				&& Objects.isNull(dto.getPortType())
				&& Objects.isNull(dto.getVisDronePortCompanyId())
				&& Objects.isNull(dto.getLat())
				&& Objects.isNull(dto.getLon())
				&& Objects.isNull(dto.getAlt())
				&& Objects.isNull(dto.getSupportDroneType())
				&& Objects.isNull(dto.getImageData());
	}
	
	/**
	 * ドローンポート状態の更新対象項目のnull状態を判定する
	 * @param dto ドローンポート情報登録更新要求
	 * @return　true:更新項目が全てnull, false:null以外の項目が1つ以上ある
	 */
	private boolean isNullDronePortStatus(DronePortInfoRegisterRequestDto dto) {
		return Objects.isNull(dto.getActiveStatus())
				&& Objects.isNull(dto.getInactiveTimeFrom())
				&& Objects.isNull(dto.getInactiveTimeTo())
				&& Objects.isNull(dto.getStoredAircraftId())
				;
	}
	
	/**
	 * 日時範囲に少しでも重なる予約情報を検索する
	 * @param timeFrom 日時範囲条件の開始時間
	 * @param timeTo 日時範囲条件の終了時間
	 * @return 検索結果
	 */
	private List<DronePortReserveInfoEntity> findOverlapedReservation(String dronePortId, String timeFrom, String timeTo) {
		DronePortReserveInfoSpecification<DronePortReserveInfoEntity> spec = new DronePortReserveInfoSpecification<>();
		// ドローンポート予約情報を検索
		List<DronePortReserveInfoEntity> entityList = dronePortReserveInfoRepository.findAll(
				Specification
				.where(spec.dronePortIdEqual(dronePortId))
				.and(spec.tsrangeOverlap(
						StringUtils.parseDatetimeString(timeFrom), 
						StringUtils.parseDatetimeString(timeTo)))
				.and(spec.reservationActiveFlag(true))	// 過去に取消を行った予約は対象外
				.and(spec.deleteFlagEqual(false))
				); 
		return entityList;
	}
	
	/**
	 * 検索条件オブジェクトを生成する
	 * @param dto ドローンポート情報一覧取得リクエストDTO
	 * @return 検索条件オブジェクト
	 */
	private Specification<DronePortInfoEntity> createSpecification(DronePortInfoListRequestDto dto, Timestamp currentTime) {
		List<Integer> statuses = new ArrayList<>();
		Integer[] intStatuses = StringUtils.stringToIntegerArray(dto.getActiveStatus());
		if (Objects.nonNull(intStatuses)) {
			statuses = Arrays.asList(intStatuses);
		}
		
		DronePortInfoSpecification<DronePortInfoEntity> spec = new DronePortInfoSpecification<>();
		return Specification
				.where(spec.dronePortNameContains(dto.getDronePortName()))
				.and(spec.addressContains(dto.getAddress()))
				.and(spec.manufacturerContains(dto.getManufacturer()))
				.and(spec.serialNumberContains(dto.getSerialNumber()))
				.and(spec.portTypeContains(StringUtils.stringToIntegerArray(dto.getPortType())))
				.and(spec.startLatGreaterThanEqual(dto.getMinLat()))
				.and(spec.endLatLessThanEqual(dto.getMaxLat()))
				.and(spec.startLonGreaterThanEqual(dto.getMinLon()))
				.and(spec.endLonLessThanEqual(dto.getMaxLon()))
				.and(spec.supportDroneTypeContains(dto.getSupportDroneType()))
				//.and(spec.activeStatusContains(StringUtils.stringToIntegerArray(dto.getActiveStatus())))
				.and(createActiveStatusSpecification(statuses, currentTime))
				.and(spec.deleteFlagEqual(false));
	}
	
	private Specification<DronePortInfoEntity> createActiveStatusSpecification(List<Integer> statuses, Timestamp currentTime) {
		DronePortInfoSpecification<DronePortInfoEntity> spec = new DronePortInfoSpecification<>();
		Specification<DronePortInfoEntity> innerSpec = Specification.where(null);
		List<Integer> activeStatusValues = new ArrayList<>();
		if (statuses.contains(DronePortConstants.ACTIVE_STATUS_PREPARING)) {
			activeStatusValues.add(DronePortConstants.ACTIVE_STATUS_PREPARING);
		}
		if (statuses.contains(DronePortConstants.ACTIVE_STATUS_AVAILABLE)) {
			activeStatusValues.add(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
		}
		if (!activeStatusValues.isEmpty()) {
			innerSpec = innerSpec.or(createActiveStatusInnerSpec(activeStatusValues, currentTime));
		}
		if (statuses.contains(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE)) {
			innerSpec = innerSpec.or(spec.unavailableStatus(currentTime));
		}
		if (statuses.contains(DronePortConstants.ACTIVE_STATUS_MAINTENANCE)) {
			innerSpec = innerSpec.or(spec.maintenanceStatus(currentTime));
		}
		return innerSpec;
	}
	
	private Specification<DronePortInfoEntity> createActiveStatusInnerSpec(List<Integer> statuses, Timestamp currentTime) {
		DronePortInfoSpecification<DronePortInfoEntity> spec = new DronePortInfoSpecification<>();
		Specification<DronePortInfoEntity> innerSpec = Specification.where(null);
		
		innerSpec = innerSpec.or(spec.activeStatusInner1(statuses));
		innerSpec = innerSpec.or(spec.activeStatusInner2(statuses, currentTime));
		innerSpec = innerSpec.or(spec.activeStatusInner3(statuses, currentTime));
		
		return innerSpec;
	}
	
	/**
	 * 格納中機体IDの存在をチェックする。存在しない場合は例外発生する。
	 * @param aircraftId 格納中機体ID
	 */
	private void checkStoredAircraftIdValid(String aircraftId) {
		if (org.springframework.util.StringUtils.hasText(aircraftId)) {
			Optional<AircraftInfoEntity> optEntity = aircraftInfoRepository.
					findByAircraftIdAndDeleteFlagFalse(UUID.fromString(aircraftId));
			if (optEntity.isEmpty()) {
				// 機体IDが存在しない場合はエラー
				throw new NotFoundException(
						MessageFormat.format(
								"機体情報が見つかりません。格納中機体ID:{0}", 
								aircraftId)
						);
			}
		}
	}
	
}
