package com.hitachi.droneroute.dpm.service.impl;

import java.text.MessageFormat;
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
import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.DateTimeUtils;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListElement;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateResponseDto;
import com.hitachi.droneroute.dpm.entity.DronePortInfoEntity;
import com.hitachi.droneroute.dpm.entity.DronePortReserveInfoEntity;
import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;
import com.hitachi.droneroute.dpm.repository.DronePortInfoRepository;
import com.hitachi.droneroute.dpm.repository.DronePortReserveInfoRepository;
import com.hitachi.droneroute.dpm.repository.DronePortStatusRepository;
import com.hitachi.droneroute.dpm.service.DronePortReserveInfoService;
import com.hitachi.droneroute.dpm.specification.DronePortReserveInfoSpecification;
import com.hitachi.droneroute.dpm.specification.DronePortStatusSpecification;

import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.RequiredArgsConstructor;

/**
 *  ドローンポート予約情報サービス実装クラス
 * @author Hiroshi Toyoda
 *
 */
@RequiredArgsConstructor
@Service
public class DronePortReserveInfoServiceImpl implements DronePortReserveInfoService, DroneRouteCommonService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final DronePortInfoRepository dronePortInfoRepository;
	private final DronePortStatusRepository dronePortStatusRepository;
	private final DronePortReserveInfoRepository dronePortReserveInfoRepository;
	private final AircraftInfoRepository aircraftInfoRepository;
	//private final AircraftReserveInfoRepository aircraftReserveInfoRepository;
	
	private final CodeMaster codeMaster;
	private final SystemSettings systemSettings;
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public DronePortReserveInfoRegisterListResponseDto register(DronePortReserveInfoRegisterListRequestDto dto) {
		DronePortReserveInfoRegisterListResponseDto responseDto = new DronePortReserveInfoRegisterListResponseDto();
		responseDto.setDronePortReservationIds(new ArrayList<String>());
		for (DronePortReserveInfoRegisterListRequestDto.Element elem : dto.getData()) {
			// 機体の存在と、機体予約をチェックする
			checkAircraftReservationValid(elem.getAircraftId());
			// 対象となるドローンポートの存在と、予約に空きがあるかをチェックする。エラー時は例外発生。
			checkDronePortReservationValid(
					elem.getDronePortId(), null, 
					elem.getReservationTimeFrom(), elem.getReservationTimeTo());
			
			DronePortReserveInfoEntity registeredEntity;
			registeredEntity = registerInternal(elem, dto.getOperatorId());

			// 処理結果編集
			responseDto.getDronePortReservationIds().add(registeredEntity.getDronePortReservationId().toString());
		}
		return responseDto;
	}
	
	private DronePortReserveInfoEntity registerInternal(
			DronePortReserveInfoRegisterListRequestDto.Element dto,
			String operatorId) {
		// エンティティ作成
		DronePortReserveInfoEntity newEntity = new DronePortReserveInfoEntity();
		// 登録する値を設定
		setEntity(dto.getDronePortId(), dto.getAircraftId(), 
				dto.getRouteReservationId(), dto.getUsageType(), 
				dto.getReservationTimeFrom(), dto.getReservationTimeTo(), 
				newEntity);
		// ドローンポート予約IDを採番
		newEntity.setDronePortReservationId(UUID.randomUUID());
		newEntity.setReservationActiveFlag(true);
		newEntity.setCreateTime(DateTimeUtils.getUtcCurrentTimestamp());
		newEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
		newEntity.setOperatorId(operatorId);
		newEntity.setDeleteFlag(false);
		// DB登録呼び出し
		DronePortReserveInfoEntity registeredEntity = dronePortReserveInfoRepository.save(newEntity);
		return registeredEntity;
	}
	
	/**
	 * 動作状況が、使用不可、メンテナンス中に、予約時間が使用不可期間と重なっていないかチェックする。
	 * @param statusEntity ドローンポート状況エンティティ
	 * @param reservationTimeFrom 予約開始日時
	 * @param reservationTimeTo 予約終了日時
	 * @return true:予約時間が使用不可期間と重なっていない(予約可能), false:予約時間が使用不可期間と重なっている(予約不可)
	 */
	private boolean checkDronePortInactiveTime(
			DronePortStatusEntity statusEntity, 
			String reservationTimeFrom, String reservationTimeTo) {
		boolean checkResult = false;
		if (Objects.nonNull(statusEntity.getInactiveStatus())) {
			// 動作状況(使用不可)が設定されている場合(使用不可、またはメンテナンス中)に、予約日時が使用不可日時範囲に重なっていないかチェックする
			// ドローンポート状況エンティティの設定値ではなく、DB検索結果で判定する
			DronePortStatusSpecification spec = new DronePortStatusSpecification();
			List<DronePortStatusEntity> statusEntityList = dronePortStatusRepository.findAll(
					Specification
					.where(spec.dronePortIdEqual(statusEntity.getDronePortId()))
					.and(spec.tsrangeOverlap(
							StringUtils.parseDatetimeString(reservationTimeFrom), 
							StringUtils.parseDatetimeString(reservationTimeTo)))
					.and(spec.deleteFlagEqual(false))
					); 
			if (statusEntityList.isEmpty()) {
				// 予約開始日時、予約終了日時の両方がドローンポートの使用不可日時範囲に入ってなければOK
				checkResult = true;
			}				
		} else {
			checkResult = true;
		}
		return checkResult;
	}
	
	/**
	 * 予約対象のドローンポートIDの存在チェック
	 * @param dronePortId
	 * @param dronePortReservationId
	 * @param timeFrom
	 * @param timeTo
	 * @return
	 */
	private DronePortInfoEntity checkDronePortReservationValid(
			String dronePortId, String dronePortReservationId, String reservationTimeFrom, String reservationTimeTo) {
		// ドローンポートIDの存在チェック
		Optional<DronePortInfoEntity> optDronePortInfoEntity = dronePortInfoRepository
				.findByDronePortIdAndDeleteFlagFalse(dronePortId);
		if (optDronePortInfoEntity.isEmpty()) {
			throw new ServiceErrorException("ドローンポートが存在しません:ドローンポートID:" + dronePortId);
		}
		DronePortInfoEntity dronePortInfoEntity = optDronePortInfoEntity.get();
		if (Objects.isNull(dronePortInfoEntity.getDronePortStatusEntity())) {
			throw new ServiceErrorException("ドローンポート状態が存在しません:ドローンポートID:" + dronePortId);
		}
		// ポート形状をチェックする
		if (!checkReservablePortType(dronePortInfoEntity.getPortType())) {
			throw new ServiceErrorException(
					"ドローンポートのポート形状が予約可能ではありません:ポート形状:" + dronePortInfoEntity.getPortType());
		}
		if (org.springframework.util.StringUtils.hasText(reservationTimeFrom)
				&& org.springframework.util.StringUtils.hasText(reservationTimeTo)) {
			// 予約時間が設定されている場合
			// ドローンポートの動作状況と使用可能時間をチェックする
			DronePortStatusEntity statusEntity = dronePortInfoEntity.getDronePortStatusEntity();
			if (checkDronePortInactiveTime(statusEntity, reservationTimeFrom, reservationTimeTo)) {
				// 空き時間をチェックする(登録時は常に設定あり)
				// ドローンポート予約情報の検索条件作成クラスを生成
				DronePortReserveInfoSpecification<DronePortReserveInfoEntity> spec = new DronePortReserveInfoSpecification<>();
				// ドローンポート予約情報を検索
				List<DronePortReserveInfoEntity> entityList = dronePortReserveInfoRepository.findAll(
						Specification
						.where(spec.dronePortIdEqual(dronePortId))
						.and(spec.dronePortReserveIdNotEqual(
								// ドローンポート予約IDが設定されている場合(更新の場合)、これを含めずに空き時間を検索する
								Objects.nonNull(dronePortReservationId) ? 
										UUID.fromString(dronePortReservationId) : null)
						)
						.and(spec.tsrangeOverlap(
								StringUtils.parseDatetimeString(reservationTimeFrom), 
								StringUtils.parseDatetimeString(reservationTimeTo)))
						.and(spec.reservationActiveFlag(true))		// 取り消された予約は対象外
						.and(spec.deleteFlagEqual(false))
						);
				if (!entityList.isEmpty()) {
					// 他の予約と被っている
					throw new ServiceErrorException("他の予約と被っているため、予約できません");
				}
			} else {
				// ドローンポートが使用不可な期間と被っている
				String activeStatus = codeMaster.getString(DronePortConstants.CODE_MASTER_ACTIVE_STATUS, statusEntity.getInactiveStatus());
				String lower = StringUtils.toUtcDateTimeString(statusEntity.getInactiveTime().lower());
				String upper = StringUtils.toUtcDateTimeString(statusEntity.getInactiveTime().upper());
				throw new ServiceErrorException(
						MessageFormat.format("ドローンポートが使用できないため、予約できません。{0}({1}～{2})", 
								activeStatus, 
								Objects.isNull(lower) ? "" : lower, 
								Objects.isNull(upper) ? "" : upper));
			}
		}
		return dronePortInfoEntity;
		
	}
	
	/**
	 * ポート形状の予約可能設定に含まれるかチェックする
	 * @param portType ポート形状
	 * @return true:予約可能(設定に含まれる)、false:予約不可(設定に含まれない)
	 */
	private boolean checkReservablePortType(Integer portType) {
		List<Integer> reservablePortTypes = Arrays.asList(
				systemSettings.getIntegerValueArray(
						DronePortConstants.SETTINGS_DRONEPORT_RESERVATION, 
						DronePortConstants.SETTINGS_DRONEPORT_RESERVATION_PORT_TPYE)
				);
		return reservablePortTypes.contains(portType);
	}
	
	/**
	 * 機体IDの存在チェックを行う。存在しない場合は例外発生。
	 * @param aircraftId 機体ID
	 */
	private void checkAircraftReservationValid(String aircraftId) {
		if (!org.springframework.util.StringUtils.hasText(aircraftId)) {
			// 機体IDがnullか未設定の場合はチェックしない
			return;
		}
		// 機体情報の存在チェック
		Optional<AircraftInfoEntity> optAircraftInfoEntity = aircraftInfoRepository
				.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(aircraftId));
		if (optAircraftInfoEntity.isEmpty()) {
			throw new ServiceErrorException("機体情報が存在しません:機体ID:" + aircraftId);
		}
// 機体予約の存在チェックは行わない
//		if (org.springframework.util.StringUtils.hasText(dto.getReservationTimeFrom())
//				&& org.springframework.util.StringUtils.hasText(dto.getReservationTimeTo())) {
//			// 予約時間に機体予約が存在することをチェックする
//			// 機体予約情報の検索条件作成クラスを生成
//			AircraftReserveInfoSpecification<AircraftReserveInfoEntity> spec = new AircraftReserveInfoSpecification<>();
//			// 機体予約情報を検索
//			List<AircraftReserveInfoEntity> entityList = aircraftReserveInfoRepository.findAll(
//					Specification
//					.where(spec.deleteFlagFalse(false))
//					.and(spec.tsrangeInclude(
//							StringUtils.parseDatetimeString(dto.getReservationTimeFrom()), 
//							StringUtils.parseDatetimeString(dto.getReservationTimeTo())))
//					);
//			if (entityList.isEmpty()) {
//				// 機体予約なし
//				throw new ServiceErrorException("指定された時間帯に機体予約が存在しないため、予約できません");
//			}
//		}		
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public DronePortReserveInfoUpdateResponseDto update(DronePortReserveInfoUpdateRequestDto dto) {
		// 既存レコードを検索
		Optional<DronePortReserveInfoEntity> optEntity = 
				dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
						UUID.fromString(dto.getDronePortReservationId()));
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"ドローンポート予約情報が見つかりません。ドローンポート予約ID:{0}", 
							dto.getDronePortReservationId())
					);
		}
		DronePortReserveInfoEntity reserveEntity = optEntity.get();
		// オペレータIDをチェック
		checkUpdatePermissionByOperatorId(reserveEntity, dto.getOperatorId());
		// 予約有効フラグをチェック
		if (Objects.nonNull(reserveEntity.getReservationActiveFlag()) 
				&& !reserveEntity.getReservationActiveFlag()) {
			throw new ServiceErrorException(
					MessageFormat.format(
							"取消済み予約を更新することはできません。ドローンポート予約ID:{0}", 
							dto.getDronePortReservationId())
					);
		}
		if (Objects.isNull(dto.getAircraftId()) 
				&& Objects.nonNull(reserveEntity.getAircraftId())) {
			// 使用機体IDが更新対象でない場合は、既存予約情報の値を設定する。
			dto.setAircraftId(reserveEntity.getAircraftId().toString());
		}
		// 機体の存在と、機体予約をチェックする
		checkAircraftReservationValid(dto.getAircraftId());
		if (!org.springframework.util.StringUtils.hasText(dto.getDronePortId())) {
			// ドローンポートIDが更新対象でない場合は、既存予約情報の値を設定する。
			dto.setDronePortId(reserveEntity.getDronePortId().toString());
		}
		if (!org.springframework.util.StringUtils.hasText(dto.getReservationTimeFrom())) {
			// 予約開始日時が更新対象でない場合は、既存予約情報の値を設定する。
			dto.setReservationTimeFrom(StringUtils.toUtcDateTimeString(reserveEntity.getReservationTime().lower()));
		}
		if (!org.springframework.util.StringUtils.hasText(dto.getReservationTimeTo())) {
			// 予約終了日時が更新対象でない場合は、既存予約情報の値を設定する。
			dto.setReservationTimeTo(StringUtils.toUtcDateTimeString(reserveEntity.getReservationTime().upper()));
		}
		
		// 対象となるドローンポートの存在と、予約に空きがあるかをチェックする。エラー時は例外発生。
		checkDronePortReservationValid(
				dto.getDronePortId(), dto.getDronePortReservationId(), 
				dto.getReservationTimeFrom(), dto.getReservationTimeTo());
		
		DronePortReserveInfoEntity reserveInfoEntity = optEntity.get();
		DronePortReserveInfoEntity updatedEntity = null;
		// 既存エンティティに更新する値を設定する
		setEntity(dto.getDronePortId(), dto.getAircraftId(), 
				dto.getRouteReservationId(), dto.getUsageType(), 
				dto.getReservationTimeFrom(), dto.getReservationTimeTo(), 
				reserveInfoEntity);
		reserveInfoEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
		// DB更新呼出
		updatedEntity = dronePortReserveInfoRepository.save(reserveInfoEntity);
		
		// 処理結果編集
		DronePortReserveInfoUpdateResponseDto responseDto = new DronePortReserveInfoUpdateResponseDto();
		responseDto.setDronePortReservationId(updatedEntity.getDronePortReservationId().toString());
		
		return responseDto;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public void delete(String dronePortReservationId, DronePortInfoDeleteRequestDto dto) {
		// 既存レコードを検索
		Optional<DronePortReserveInfoEntity> optEntity = 
				dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
						UUID.fromString(dronePortReservationId));
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"ドローンポート予約情報が見つかりません。ドローンポート予約ID:{0}", 
							dronePortReservationId)
					);
		}
		DronePortReserveInfoEntity reserveInfoEntity = optEntity.get();
		// オペレータIDをチェック
		checkUpdatePermissionByOperatorId(reserveInfoEntity, dto.getOperatorId());
	
		// 対象となるドローンポート情報を検索する
		// ドローンポートIDの存在チェック
		Optional<DronePortInfoEntity> optDronePortInfoEntity 
			= dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(reserveInfoEntity.getDronePortId());
		if (optDronePortInfoEntity.isEmpty()) {
			throw new ServiceErrorException("ドローンポートが存在しません:ドローンポートID:" + reserveInfoEntity.getDronePortId());
		}
		// 削除フラグを立てる
		reserveInfoEntity.setDeleteFlag(true);
		reserveInfoEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
		// reserveInfoEntity更新呼出
		dronePortReserveInfoRepository.save(reserveInfoEntity);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DronePortReserveInfoListResponseDto getList(DronePortReserveInfoListRequestDto dto) {
		// ソート、ページ、検索条件を生成する
		Sort sort = createSort(dto.getSortOrders(), dto.getSortColumns(), logger);
		Pageable pageable = createPageRequest(dto.getPerPage(), dto.getPage(), sort);
		Specification<DronePortReserveInfoEntity> spec = createSpecification(dto);
		// ドローンポート予約情報を検索
		Page<DronePortReserveInfoEntity> pageResult = null;
		List<DronePortReserveInfoEntity> entityListResult = null;
		if (Objects.isNull(pageable)) {
			// CT-005 Start
			// ページ制御なし
			if(Objects.isNull(sort)) {
				entityListResult = dronePortReserveInfoRepository.findAll(spec);
			} else {
				entityListResult = dronePortReserveInfoRepository.findAll(spec, sort);
			}
			// CT-005 End
		} else {
			pageResult = dronePortReserveInfoRepository.findAll(spec, pageable);
			entityListResult = pageResult.getContent();
			logger.debug("search result:" + pageResult.toString());
		}
		// 処理結果編集
		List<DronePortReserveInfoListElement> detailList = new ArrayList<>();
		for (DronePortReserveInfoEntity entity : entityListResult) {
			DronePortReserveInfoListElement detailDto = new DronePortReserveInfoListElement();
			setDetailResponseDto(entity, detailDto);
			detailList.add(detailDto);
		}
		DronePortReserveInfoListResponseDto responseDto = new DronePortReserveInfoListResponseDto();
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
	public DronePortReserveInfoDetailResponseDto getDetail(String dronePortReservationId) {
		// 既存レコードを検索
		Optional<DronePortReserveInfoEntity> optEntity = 
				dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
						UUID.fromString(dronePortReservationId));
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"ドローンポート予約情報が見つかりません。ドローンポート予約ID:{0}", 
							dronePortReservationId)
					);
		}
		// 処理結果編集
		DronePortReserveInfoEntity entity = optEntity.get();
		DronePortReserveInfoDetailResponseDto responseDto = new DronePortReserveInfoDetailResponseDto();
		setDetailResponseDto(entity, responseDto);
		return responseDto;
	}
	
	/**
	 * エンティティをドローンポート予約情報詳細取得応答に設定
	 * @param entity エンティティ
	 * @param dto ドローンポート予約情報詳細取得応答
	 */
	private void setDetailResponseDto(DronePortReserveInfoEntity entity, DronePortReserveInfoDetailResponseDto dto) {
		BeanUtils.copyProperties(entity, dto);
		dto.setDronePortReservationId(entity.getDronePortReservationId().toString());
		dto.setDronePortId(entity.getDronePortId().toString());
		dto.setAircraftId(
				Objects.isNull(entity.getAircraftId()) ? 
						null : entity.getAircraftId().toString());
		dto.setRouteReservationId(
				Objects.isNull(entity.getRouteReservationId()) ? 
						null : entity.getRouteReservationId().toString());
		dto.setAircraftName(
				Objects.isNull(entity.getAircraftInfoEntity()) ? 
						null : entity.getAircraftInfoEntity().getAircraftName());
		dto.setDronePortName(entity.getDronePortInfoEntity().getDronePortName());
		dto.setVisDronePortCompanyId(entity.getDronePortInfoEntity().getVisDronePortCompanyId());
		// 予約時間を取得
		dto.setReservationTimeFrom(StringUtils.toUtcDateTimeString(entity.getReservationTime().lower()));
		dto.setReservationTimeTo(StringUtils.toUtcDateTimeString(entity.getReservationTime().upper()));
		// 予約有効フラグを取得
		dto.setReservationActiveFlag(entity.getReservationActiveFlag());
	}
	
	/**
	 * エンティティをドローンポート予約情報一覧要素に設定
	 * @param entity エンティティ
	 * @param dto ドローンポート予約情報一覧要素
	 */
	private void setDetailResponseDto(DronePortReserveInfoEntity entity, DronePortReserveInfoListElement dto) {
		BeanUtils.copyProperties(entity, dto);
		dto.setDronePortReservationId(entity.getDronePortReservationId().toString());
		dto.setDronePortId(entity.getDronePortId().toString());
		dto.setAircraftId(
				Objects.isNull(entity.getAircraftId()) ? 
						null : entity.getAircraftId().toString());
		dto.setRouteReservationId(
				Objects.isNull(entity.getRouteReservationId()) ? 
						null : entity.getRouteReservationId().toString());
		dto.setAircraftName(
				Objects.isNull(entity.getAircraftInfoEntity()) ? 
						null : entity.getAircraftInfoEntity().getAircraftName());
		dto.setDronePortName(entity.getDronePortInfoEntity().getDronePortName());
		dto.setVisDronePortCompanyId(entity.getDronePortInfoEntity().getVisDronePortCompanyId());
		// 予約時間を取得
		dto.setReservationTimeFrom(StringUtils.toUtcDateTimeString(entity.getReservationTime().lower()));
		dto.setReservationTimeTo(StringUtils.toUtcDateTimeString(entity.getReservationTime().upper()));
		// ドローンポートの使用不可時間を設定
		dto.setInactiveTimeFrom(
				Objects.nonNull(entity.getDronePortStatusEntity().getInactiveTime()) ? 
						StringUtils.toUtcDateTimeString(
								entity.getDronePortStatusEntity().getInactiveTime().lower())
						: null);
		dto.setInactiveTimeTo(
				Objects.nonNull(entity.getDronePortStatusEntity().getInactiveTime()) ? 
						StringUtils.toUtcDateTimeString(
								entity.getDronePortStatusEntity().getInactiveTime().upper())
						: null);
	}
	
	/**
	 * ドローンポート予約情報をエンティティに設定
	 * @param dronePortId ドローンポートID
	 * @param aircraftId 機体ID
	 * @param routeReservationId 航路予約ID
	 * @param usageType 利用形態
	 * @param entity ドローンポート予約情報エンティティ
	 */
	private void setEntity(
			String dronePortId, String aircraftId, String routeReservationId, 
			Integer usageType, String timeFrom, String timeTo, 
			DronePortReserveInfoEntity entity) {
		if (dronePortId != null) {
			entity.setDronePortId(dronePortId);
		}
		if (aircraftId != null) {
			if (aircraftId.isBlank()) {
				// 更新時に未設定の場合はnullに更新する
				entity.setAircraftId(null);
			} else {
				entity.setAircraftId(UUID.fromString(aircraftId));
			}
		}
		if (routeReservationId != null) {
			if (routeReservationId.isBlank()) {
				// 更新時に未設定の場合はnullに更新する
				entity.setRouteReservationId(null);
			} else {
				entity.setRouteReservationId(UUID.fromString(routeReservationId));
			}
		}
		if (usageType != null) {
			entity.setUsageType(usageType);
		}
		if (Objects.nonNull(timeFrom) 
				&& Objects.nonNull(timeTo)) {
			entity.setReservationTime(Range.localDateTimeRange(
					String.format("[%s,%s)", 
							StringUtils.parseDatetimeStringToLocalDateTime(timeFrom).toString(), 
							StringUtils.parseDatetimeStringToLocalDateTime(timeTo).toString()
							)
					));
		}
	}
	
	/**
	 * 検索条件オブジェクトを生成する
	 * @param dto ドローンポート予約情報一覧取得リクエストDTO
	 * @return 検索条件オブジェクト
	 */
	private Specification<DronePortReserveInfoEntity> createSpecification(DronePortReserveInfoListRequestDto dto) {
		DronePortReserveInfoSpecification<DronePortReserveInfoEntity> spec = new DronePortReserveInfoSpecification<>();
		return Specification
				.where(spec.dronePortIdEqual(dto.getDronePortId()))
				.and(spec.dronePortNameContains(dto.getDronePortName()))
				.and(spec.aircraftIdEqual(
						Objects.nonNull(dto.getAircraftId()) ? UUID.fromString(dto.getAircraftId()) : null)
					)
				.and(spec.routeReservationIdEqual(
						Objects.nonNull(dto.getRouteReservationId()) ? UUID.fromString(dto.getRouteReservationId()) : null)
					)
				.and(spec.tsrangeInclude2(
						StringUtils.parseDatetimeString(dto.getTimeFrom()), 
						StringUtils.parseDatetimeString(dto.getTimeTo())))
				.and(spec.deleteFlagEqual(false));
	}
	
}
