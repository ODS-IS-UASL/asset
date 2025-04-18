package com.hitachi.droneroute.arm.service.impl;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
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

import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoResponseDto;
import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.arm.entity.AircraftReserveInfoEntity;
import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
import com.hitachi.droneroute.arm.repository.AircraftReserveInfoRepository;
import com.hitachi.droneroute.arm.service.AircraftReserveInfoService;
import com.hitachi.droneroute.arm.specification.AircraftReserveInfoSpecification;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.service.DroneRouteCommonService;
import com.hitachi.droneroute.cmn.util.StringUtils;

import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.RequiredArgsConstructor;

/**
 * 機体予約情報サービス実装クラス
 * @author Ikkan Suzuki
 */
@Service
@RequiredArgsConstructor
public class AircraftReserveInfoServiceImpl implements AircraftReserveInfoService, DroneRouteCommonService{

	private final AircraftReserveInfoRepository aircraftReserveInfoRepository;
	
	private final AircraftInfoRepository aircraftInfoRepository;

	// ロガー
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public AircraftReserveInfoResponseDto postData(AircraftReserveInfoRequestDto request) {
		// 機体情報確認
		checkReserveRegest(request);

		// 機体予約情報登録更新Entity
		AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
		
		// 登録データ作成
		setEntity(entity, request);
		entity.setAircraftReservationId(UUID.randomUUID());
		entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
		entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		entity.setOperatorId(request.getOperatorId());
		entity.setDeleteFlag(false);

    	// DB登録
		AircraftReserveInfoEntity aircraftReserveInfoEntity = aircraftReserveInfoRepository.save(entity);
		
		// 編集
		AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
		if(Objects.nonNull(aircraftReserveInfoEntity.getAircraftReservationId())) {
			responseDto.setAircraftReservationId(aircraftReserveInfoEntity.getAircraftReservationId().toString());
		} else {
			throw new ServiceErrorException("機体予約IDの生成に失敗しました。");
		}
		return responseDto;
	}

	@Override
	public AircraftReserveInfoResponseDto putData(AircraftReserveInfoRequestDto request) {
		// 既存レコード検索
		Optional<AircraftReserveInfoEntity> optEntity = aircraftReserveInfoRepository
				.findByAircraftReservationIdAndDeleteFlagFalse(UUID.fromString(request.getAircraftReservationId()));
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"機体予約IDが見つかりません。機体予約ID:{0}", 
							request.getAircraftReservationId())
					);
		}
		
		// 機体予約情報登録更新Entity
		// CT-021　修正
		AircraftReserveInfoEntity entity = optEntity.get();
		// オペレータIDをチェック
		checkUpdatePermissionByOperatorId(entity, request.getOperatorId());
		
		// 重複する予約の確認
		checkReserveUpdate(request, entity);
		
		// 更新データ作成
    	setEntity(entity, request);
    	entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    	
    	// DB登録
		AircraftReserveInfoEntity aircraftReserveInfoEntity = aircraftReserveInfoRepository.save(entity);
		
		// 編集
		AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
		if(Objects.nonNull(aircraftReserveInfoEntity.getAircraftReservationId())) {
			responseDto.setAircraftReservationId(aircraftReserveInfoEntity.getAircraftReservationId().toString());
		} else {
			throw new ServiceErrorException("機体予約情報の更新に失敗しました。");
		}		
		return responseDto;
	}

	@Override
	public void deleteData(String aircraftRevservationId, AircraftInfoDeleteRequestDto dto) {
		// 既存レコード検索
		Optional<AircraftReserveInfoEntity> optEntity = aircraftReserveInfoRepository
				.findByAircraftReservationIdAndDeleteFlagFalse(UUID.fromString(aircraftRevservationId));
		
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"機体予約IDが見つかりません。機体予約ID:{0}", 
							aircraftRevservationId)
					);
		}
		
		// 機体予約情報登録更新Entity
		AircraftReserveInfoEntity entity = optEntity.get();
		// オペレータIDをチェック
		checkUpdatePermissionByOperatorId(entity, dto.getOperatorId());
		
		entity.setDeleteFlag(true);
		entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		
    	// DB登録
		aircraftReserveInfoRepository.save(entity);

	}

    @Transactional
	@Override
	public AircraftReserveInfoListResponseDto getList(AircraftReserveInfoListRequestDto request) {
    	
    	// MVP1指摘対応 ソート機能追加、ページ制御追加
		Sort sort = createSort(request.getSortOrders(), request.getSortColumns(), logger);
		Pageable pageable = createPageRequest(request.getPerPage(), request.getPage(), sort);

		// データ取得
		Specification<AircraftReserveInfoEntity> spec = createSpecification(request);
		
    	// MVP1指摘対応 ソート機能追加、ページ制御追加
		Page<AircraftReserveInfoEntity> pageResult = null;
		List<AircraftReserveInfoEntity> entityList = null;
		if (Objects.isNull(pageable)) {
			// CT-005 Start
			// ページ制御なし
			if(Objects.isNull(sort)) {
				entityList = aircraftReserveInfoRepository.findAll(spec);
			} else {
				entityList = aircraftReserveInfoRepository.findAll(spec, sort);
			}
			// CT-005 End
		} else {
			// ページ制御あり
			pageResult = aircraftReserveInfoRepository.findAll(spec, pageable);
			entityList = pageResult.getContent();
			logger.debug("search result:" + pageResult.toString());
		}

		List<AircraftReserveInfoDetailResponseDto> detailList = new ArrayList<>();
		for (AircraftReserveInfoEntity entity : entityList) {
			AircraftReserveInfoDetailResponseDto detail = new AircraftReserveInfoDetailResponseDto();
			setAircraftReserveInfoDetailResponseDto(detail, entity);
			detailList.add(detail);
		}
		AircraftReserveInfoListResponseDto responseDto = new AircraftReserveInfoListResponseDto();
		responseDto.setData(detailList);
    	// MVP1指摘対応 ページ制御追加
		if (Objects.nonNull(pageResult)) {
			// ページ情報の結果を設定する
			responseDto.setPerPage(pageResult.getSize());
			responseDto.setCurrentPage(pageResult.getNumber()+1);
			responseDto.setLastPage(pageResult.getTotalPages());
			responseDto.setTotal((int)pageResult.getTotalElements());
		}
		return responseDto;
	}

	@Override
	public AircraftReserveInfoDetailResponseDto getDetail(String aircraftRevservationId) {
		// 既存レコード検索
		Optional<AircraftReserveInfoEntity> optEntity = aircraftReserveInfoRepository
				.findByAircraftReservationIdAndDeleteFlagFalse(UUID.fromString(aircraftRevservationId));
		
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"機体予約IDが見つかりません。機体予約ID:{0}", 
							aircraftRevservationId)
					);
		}
		
		// 機体予約情報登録更新Entity
		AircraftReserveInfoEntity entity = optEntity.get();
		AircraftReserveInfoDetailResponseDto detail = new AircraftReserveInfoDetailResponseDto();
		setAircraftReserveInfoDetailResponseDto(detail, entity);
		
		return detail;
	}

	/**
	 * エンティティにリクエスト情報を設定
	 * @param aircraftReserveInfoEntity
	 * @param request
	 */
	private void setEntity(AircraftReserveInfoEntity aircraftReserveInfoEntity, AircraftReserveInfoRequestDto request) {
		if (request.getAircraftReservationId() != null && !request.getAircraftReservationId().isBlank()) {
			aircraftReserveInfoEntity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
		}
		
		if(request.getAircraftId() != null && !request.getAircraftId().isBlank()) {
			aircraftReserveInfoEntity.setAircraftId(UUID.fromString(request.getAircraftId()));
		}
		
		if (Objects.nonNull(request.getReservationTimeFrom()) 
				&& Objects.nonNull(request.getReservationTimeTo())) {
			aircraftReserveInfoEntity.setReservationTime(Range.localDateTimeRange(
					String.format("[%s,%s)",
							StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeFrom()).toString(),
							StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeTo()).toString())));
		}
	}

	/**
	 * 機体予約情報詳細DTOにエンティティからのデータを登録
	 * @param detail
	 * @param entity
	 */
	private void setAircraftReserveInfoDetailResponseDto(AircraftReserveInfoDetailResponseDto detail,
			AircraftReserveInfoEntity entity) {
		BeanUtils.copyProperties(entity, detail);
		if(Objects.nonNull(entity.getAircraftId())) {
			detail.setAircraftId(entity.getAircraftId().toString());
		} else {
			detail.setAircraftId(null);
		}
		if(Objects.nonNull(entity.getAircraftReservationId())) {
			detail.setAircraftReservationId(entity.getAircraftReservationId().toString());
		} else {
			throw new ServiceErrorException("機体予約情報の取得に失敗しました。");
		}
		detail.setReservationTimeFrom(StringUtils.toUtcDateTimeString(entity.getReservationTime().lower()));
		detail.setReservationTimeTo(StringUtils.toUtcDateTimeString(entity.getReservationTime().upper()));
		detail.setAircraftName(entity.getAircraftEntity().getAircraftName());	// sprint2:機体名追加
	}
	
    /**
     * 予約情報登録確認
     * @param dto
     */
	private void checkReserveRegest(AircraftReserveInfoRequestDto dto) {
		
		// 機体IDの存在確認
		if(dto.getAircraftId() != null && !dto.getAircraftId().isBlank()) {
			Optional<AircraftInfoEntity> optEntity = aircraftInfoRepository
					.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(dto.getAircraftId()));
			if(optEntity.isEmpty()) {
				throw new ServiceErrorException("機体IDが存在しません:機体ID:" + dto.getAircraftId());
			}
		} else {
			throw new ServiceErrorException("機体IDが入力されていません。");
		}
		
		// 機体予約情報検索条件作成
		AircraftReserveInfoSpecification<AircraftReserveInfoEntity> spec = new AircraftReserveInfoSpecification<>();
		// 機体予約情報検索
		List<AircraftReserveInfoEntity> entityList = aircraftReserveInfoRepository.findAll(
				Specification
					.where(spec.aircraftIdEqual(UUID.fromString(dto.getAircraftId())))	// CT-016 検索方法修正
					.and(spec.tsrangeOverlap(StringUtils.parseDatetimeString(dto.getReservationTimeFrom()), StringUtils.parseDatetimeString(dto.getReservationTimeTo())))
					.and(spec.deleteFlagEqual(false))
				);
		
		// 重複確認
		if(!entityList.isEmpty()) {
			throw new ServiceErrorException("他の予約と被っているため、予約できません");
		}
	}
	
    /**
     * 予約情報重複確認
     * @param dto
     * @param entity
     */
	private void checkReserveUpdate(AircraftReserveInfoRequestDto dto, AircraftReserveInfoEntity entity) {
		String aircraftId = dto.getAircraftId();
		if(!org.springframework.util.StringUtils.hasText(aircraftId)) {
			// DTOに機体IDが含まれていなかったら変更前の機体IDを使用する
			aircraftId = entity.getAircraftId().toString();
		}
		
		// 機体IDの存在確認
		Optional<AircraftInfoEntity> optEntity = aircraftInfoRepository
				.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(aircraftId));
		if(optEntity.isEmpty()) {
			throw new ServiceErrorException("機体IDが存在しません:機体ID:" + aircraftId);
		}

		// 予約開始時間
		String timeFrom = dto.getReservationTimeFrom();
		if (!org.springframework.util.StringUtils.hasText(timeFrom)) {
			// 予約開始時間が入っていない場合は元の開始時間を使用する。
			timeFrom = entity.getReservationTime().lower().toString()+"Z";
		}
		
		// 予約終了時間
		String timeTo = dto.getReservationTimeTo();		
		if (!org.springframework.util.StringUtils.hasText(timeTo)) {
			// 予約終了時間が入っていない場合は元の終了時間を使用する。
			timeTo = entity.getReservationTime().upper().toString()+"Z";
		}

		// 予約時間に機体予約が存在することをチェックする
		// 機体予約情報の検索条件作成クラスを生成
		AircraftReserveInfoSpecification<AircraftReserveInfoEntity> spec = new AircraftReserveInfoSpecification<>();
		// 機体予約情報を検索
		List<AircraftReserveInfoEntity> entityList = aircraftReserveInfoRepository.findAll(
				Specification
				.where(spec.deleteFlagEqual(false))
				.and(spec.aircraftIdEqual(UUID.fromString(aircraftId)))
				.and(spec.tsrangeOverlap(
						StringUtils.parseDatetimeString(timeFrom), 
						StringUtils.parseDatetimeString(timeTo)))
				);
		boolean ckFlag = false;
		if (!entityList.isEmpty()) {
			// 重複する予約が見つかった場合
			if(entityList.size() == 1) {
				// 1件のみの場合（更新しようとしているデータの可能性）
				for (AircraftReserveInfoEntity tmpEntity : entityList) {
					if (tmpEntity.getAircraftReservationId().toString()
							.equals(dto.getAircraftReservationId().toString())) {
						// 同じ機体予約IDの場合は時間変更であると判断し、更新を許可する。
						ckFlag = true;
						break;
					}
				}				
			}
			if(!ckFlag) {
				// 重複する予約あり
				throw new ServiceErrorException("他の予約と被っているため、予約できません");
			}
		}
	}

	// MVP1指摘対応 ソート機能追加、ページ制御追加
	/**
	 * 検索条件オブジェクトを生成する
	 * @param request 機体予約情報一覧取得リクエストDTO
	 * @return 検索条件オブジェクト
	 */
	private Specification<AircraftReserveInfoEntity> createSpecification(AircraftReserveInfoListRequestDto request) {
		AircraftReserveInfoSpecification<AircraftReserveInfoEntity> spec = new AircraftReserveInfoSpecification<>();
		UUID aircraftId = null;
		String strAircraftId = request.getAircraftId();
		if(strAircraftId != null) {
			if(!strAircraftId.isBlank()) {
				aircraftId = UUID.fromString(strAircraftId);
			}
		}
		
		// MVP1指摘対応 #17 機体名による検索追加
		String aircraftName = request.getAircraftName();

		Timestamp timeFrom = null;
		String strTimeFrom = request.getTimeFrom();
		if(strTimeFrom != null) {
			if(!strTimeFrom.isBlank()) {
				timeFrom = StringUtils.parseDatetimeString(strTimeFrom);
			}
		}
		
		Timestamp timeTo = null;
		String strTimeTo = request.getTimeTo();
		if(strTimeTo != null) {
			if(!strTimeTo.isBlank()) {
				timeTo = StringUtils.parseDatetimeString(strTimeTo);
			}
		}
		
		return Specification
						.where(spec.aircraftIdEqual(aircraftId))	// CT-016 検索方法修正
						.and(spec.aircraftNameContains(aircraftName))	// MVP1指摘対応 #17 機体名による検索追加
						.and(spec.tsrangeInclude2(timeFrom, timeTo))
						.and(spec.deleteFlagEqual(false));
	}
}
