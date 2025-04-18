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

import com.hitachi.droneroute.arm.constants.AircraftConstants;
import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListElement;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListResponseDto;
import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
import com.hitachi.droneroute.arm.service.AircraftInfoService;
import com.hitachi.droneroute.arm.specification.AircraftInfoSpecification;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.service.DroneRouteCommonService;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.Base64Utils;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;

import lombok.RequiredArgsConstructor;

/**
 * 機体情報サービス実装クラス
 * @author Ikkan Suzuki
 */
@Service
@RequiredArgsConstructor
public class AircraftInfoServiceImpl implements AircraftInfoService, DroneRouteCommonService {

    /**
     * 機体情報リポジトリ
     */
    private final AircraftInfoRepository aircraftInfoRepository;
    
    // システムセッティング
	private final SystemSettings systemSettings;

	// ロガー
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    @Override
    public AircraftInfoResponseDto postData(AircraftInfoRequestDto request) {
    	// 機体情報登録更新Entity
    	AircraftInfoEntity newEntity = new AircraftInfoEntity();
    	
    	// 登録データ作成
    	setEntity(newEntity, request);
    	newEntity.setAircraftId(UUID.randomUUID());
    	newEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
    	newEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    	newEntity.setOperatorId(request.getOperatorId());
    	newEntity.setDeleteFlag(false);
    	
    	// DB登録
    	AircraftInfoEntity aircraftInfoEntity = aircraftInfoRepository.save(newEntity);
    	
    	// 処理結果
    	AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    	if(Objects.nonNull(aircraftInfoEntity.getAircraftId())) {
    		responseDto.setAircraftId(aircraftInfoEntity.getAircraftId().toString());
    	} else {
			throw new ServiceErrorException("機体情報の生成に失敗しました。");
    	}
    	
        return responseDto;
    }

    @Transactional
    @Override
    public AircraftInfoResponseDto putData(AircraftInfoRequestDto request) {
    	// 既存レコード検索
		Optional<AircraftInfoEntity> optEntity = aircraftInfoRepository
				.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(request.getAircraftId()));
    	
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"機体IDが見つかりません。機体ID:{0}", 
							request.getAircraftId())
					);
		}
		
    	// 機体情報登録更新Entity
    	AircraftInfoEntity updateEntity = optEntity.get();
    	// オペレータIDをチェック
    	checkUpdatePermissionByOperatorId(updateEntity, request.getOperatorId());
    	
    	// 更新データ作成
    	setEntity(updateEntity, request);
    	updateEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    	
    	// DB登録
    	AircraftInfoEntity aircraftInfoEntity = aircraftInfoRepository.save(updateEntity);

    	// 処理結果
    	AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    	if(Objects.nonNull(aircraftInfoEntity.getAircraftId())) {
    		responseDto.setAircraftId(aircraftInfoEntity.getAircraftId().toString());
    	} else {
			throw new ServiceErrorException("機体情報の更新に失敗しました。");
    	}
    	
        return responseDto;
     }

    @Transactional
    @Override
    public void deleteData(String aircraftId, AircraftInfoDeleteRequestDto dto) {
    	// 既存レコード検索
		Optional<AircraftInfoEntity> optEntity = aircraftInfoRepository
				.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(aircraftId));
    	
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"機体IDが見つかりません。機体ID:{0}", 
							aircraftId)
					);
		}
        
		AircraftInfoEntity entity = optEntity.get();
		// オペレータIDをチェック
		checkUpdatePermissionByOperatorId(entity, dto.getOperatorId());
		// 処理
		entity.setDeleteFlag(true);
		entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		
		// DB更新
		aircraftInfoRepository.save(entity);
		
    }

    @Transactional
    @Override
    public AircraftInfoSearchListResponseDto getList(AircraftInfoSearchListRequestDto request) {
    	
    	// MVP1指摘対応 ソート機能追加、ページ制御追加
		Sort sort = createSort(request.getSortOrders(), request.getSortColumns(), logger);
		Pageable pageable = createPageRequest(request.getPerPage(), request.getPage(), sort);
    	
        // データ取得
		Specification<AircraftInfoEntity> spec = createSpecification(request);
    	
    	// MVP1指摘対応 ソート機能追加、ページ制御追加
		Page<AircraftInfoEntity> pageResult = null;
		List<AircraftInfoEntity> entityList = null;
		if (Objects.isNull(pageable)) {
			// CT-005 Start
			// ページ制御なし
			if(Objects.isNull(sort)) {
				// ソートなし
				entityList = aircraftInfoRepository.findAll(spec);
			} else {
				// ソートあり
				entityList = aircraftInfoRepository.findAll(spec, sort);
			}
			// CT-005 End
		} else {
			// ページ制御あり
			pageResult = aircraftInfoRepository.findAll(spec, pageable);
			entityList = pageResult.getContent();
			logger.debug("search result:" + pageResult.toString());
		}
		
		// MVP1指摘対応 #10 一覧応答の画像イメージを削除　Start
    	List<AircraftInfoSearchListElement> detailList = new ArrayList<>();
    	for(AircraftInfoEntity entity: entityList) {
    		AircraftInfoSearchListElement detail = new AircraftInfoSearchListElement();
    		setAircraftInfoNoImageResponseDto(detail, entity);
    		detailList.add(detail);
    	}
    	// MVP1指摘対応 #10 一覧応答の画像イメージを削除 End
    	AircraftInfoSearchListResponseDto responseDto = new AircraftInfoSearchListResponseDto();
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

    @Transactional
    @Override
    public AircraftInfoDetailResponseDto getDetail(String aircraftId) {
    	// 既存レコード検索
		Optional<AircraftInfoEntity> optEntity = aircraftInfoRepository
				.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(aircraftId));
		
		if (optEntity.isEmpty()) {
			throw new NotFoundException(
					MessageFormat.format(
							"機体IDが見つかりません。機体ID:{0}", 
							aircraftId)
					);
		}

		// 処理
		AircraftInfoEntity entity = optEntity.get();
		AircraftInfoDetailResponseDto responseDto = new AircraftInfoDetailResponseDto();
		setAircraftInfoDetailResponseDto(responseDto, entity);
		
        return responseDto;
    }
    
	/**
	 * base64をバイト型にでコード
	 * @param dto ドローンポート情報登録更新要求
	 */
	@Override
	public void decodeBinary(AircraftInfoRequestDto request) {
		if (request.getImageData() != null) {
			if (request.getImageData().length() == 0) {
				request.setImageBinary(new byte[] {});
			} else {
				Base64Utils util = new Base64Utils(
				systemSettings.getStringValueArray(
						AircraftConstants.SETTINGS_IMAGE_DATA, 
						AircraftConstants.SETTINGS_SUPPORT_FORMAT));
				if (util.checkSubtype(request.getImageData())) {
					request.setImageBinary(util.getBinaryData(request.getImageData()));
				}
			}
		}
	}

	/**
	 * エンティティにリクエスト情報を設定
	 * @param aircraftInfoEntity
	 * @param request
	 */
    private void setEntity(AircraftInfoEntity aircraftInfoEntity, AircraftInfoRequestDto request) {
    	if(request.getAircraftId() != null && !request.getAircraftId().isBlank()) {
    		aircraftInfoEntity.setAircraftId(UUID.fromString(request.getAircraftId()));
    	}
    	
    	if(request.getAircraftName() != null) {
    		aircraftInfoEntity.setAircraftName(request.getAircraftName());
    	}
    	
    	if(request.getManufacturer() != null) {
    		aircraftInfoEntity.setManufacturer(request.getManufacturer());
    	}
    	
    	if(request.getManufacturingNumber() != null) {
    		aircraftInfoEntity.setManufacturingNumber(request.getManufacturingNumber());
    	}
    	
    	if(request.getAircraftType() != null) {
    		aircraftInfoEntity.setAircraftType(request.getAircraftType());
    	}
    	
    	if(request.getMaxTakeoffWeight() != null) {
    		aircraftInfoEntity.setMaxTakeoffWeight(request.getMaxTakeoffWeight());
    	}
    	
    	if(request.getBodyWeight() != null) {
    		aircraftInfoEntity.setBodyWeight(request.getBodyWeight());
    	}
    	
    	if(request.getMaxFlightSpeed() != null) {
    		aircraftInfoEntity.setMaxFlightSpeed(request.getMaxFlightSpeed());
    	}
    	
    	if(request.getMaxFlightTime() != null) {
    		aircraftInfoEntity.setMaxFlightTime(request.getMaxFlightTime());
    	}
    	
    	if(request.getLat() != null) {
    		aircraftInfoEntity.setLat(request.getLat());
    	}
    	
    	if(request.getLon() != null) {
    		aircraftInfoEntity.setLon(request.getLon());
    	}
    	
    	if (request.getCertification() != null) {
    		aircraftInfoEntity.setCertification(request.getCertification());
    	}
    	
    	if(request.getDipsRegistrationCode() != null) {
    		aircraftInfoEntity.setDipsRegistrationCode(request.getDipsRegistrationCode());
    	}
    	
    	if(request.getOwnerType() != null) {
    		aircraftInfoEntity.setOwnerType(request.getOwnerType());
    	}
    	
    	if(request.getOwnerId() != null && !request.getOwnerId().isBlank()) {
    		aircraftInfoEntity.setOwnerId(UUID.fromString(request.getOwnerId()));
    	}
    	
		if (request.getImageBinary() != null) {
			Base64Utils util = new Base64Utils(
					systemSettings.getStringValueArray(
							DronePortConstants.SETTINGS_IMAGE_DATA, 
							DronePortConstants.SETTINGS_SUPPORT_FORMAT));
			// データURIから画像フォーマットを取得する
			aircraftInfoEntity.setImageFormat(util.getSubtype(request.getImageData()));
			// 事前にbase64からバイナリ変換を行っていること。
			aircraftInfoEntity.setImageBinary(request.getImageBinary());
		}

    }
    
    /**
     * 機体情報詳細応答用DTOにエンティティからのデータを登録
     * @param responseDto
     * @param entity
     */
	private void setAircraftInfoDetailResponseDto(AircraftInfoDetailResponseDto responseDto,
			AircraftInfoEntity entity) {
		BeanUtils.copyProperties(entity, responseDto);
		if(Objects.nonNull(entity.getAircraftId())) {
			responseDto.setAircraftId(entity.getAircraftId().toString());
		} else {
			throw new ServiceErrorException("機体情報の取得に失敗しました。");
		}
		if(Objects.nonNull(entity.getOwnerId())) {
			responseDto.setOwnerId(entity.getOwnerId().toString());
		}
		if (Objects.nonNull(entity.getImageBinary())
				&& entity.getImageBinary().length > 0) {
			Base64Utils util = new Base64Utils(
			systemSettings.getStringValueArray(
					AircraftConstants.SETTINGS_IMAGE_DATA, 
					AircraftConstants.SETTINGS_SUPPORT_FORMAT));
			// データURI付きのbase64文字列を設定する
			responseDto.setImageData(
					util.createDataUriWithBase64(
							entity.getImageFormat(), entity.getImageBinary()));
		}

	}

	// MVP1指摘対応 #10 一覧応答の画像イメージを削除
    /**
     * 機体情報一覧応答用にエンティティからのデータを画像なしで登録
     * @param responseDto
     * @param entity
     */
	private void setAircraftInfoNoImageResponseDto(AircraftInfoSearchListElement responseDto,
			AircraftInfoEntity entity) {
		BeanUtils.copyProperties(entity, responseDto);
		if(Objects.nonNull(entity.getAircraftId())) {
			responseDto.setAircraftId(entity.getAircraftId().toString());
		} else {
			throw new ServiceErrorException("機体情報の取得に失敗しました。");
		}
		if(Objects.nonNull(entity.getOwnerId())) {
			responseDto.setOwnerId(entity.getOwnerId().toString());
		}
	}
	
	// MVP1指摘対応 ソート機能追加、ページ制御追加
	/**
	 * 検索条件オブジェクトを生成する
	 * @param request 機体情報一覧取得リクエストDTO
	 * @return 検索条件オブジェクト
	 */
	private Specification<AircraftInfoEntity> createSpecification(AircraftInfoSearchListRequestDto request) {
		AircraftInfoSpecification<AircraftInfoEntity> spec = new AircraftInfoSpecification<>();
    	UUID ownerId = null;
    	if(request.getOwnerId() != null) {
	    	if(!request.getOwnerId().isBlank()) {
	    		ownerId = UUID.fromString(request.getOwnerId());
	    	}
    	}
    	Boolean certification = null;
    	if(request.getCertification() != null) {
	    	if(!request.getCertification().isBlank()) {
	    		certification = Boolean.valueOf(request.getCertification());
	    	}
    	}
    	
		return Specification
				.where(spec.aircraftNameContains(request.getAircraftName()))
				.and(spec.manufacturerContains(request.getManufacturer()))
				.and(spec.manufacturingNumberContains(request.getManufacturingNumber()))
				.and(spec.aircraftTypeContains(StringUtils.stringToIntegerArray(request.getAircraftType())))	// IT-0002 検索方法修正
				.and(spec.certiticationEqual(certification))
				.and(spec.dipsRegistrationCodeContains(request.getDipsRegistrationCode()))
				.and(spec.ownerTypeContains(StringUtils.stringToIntegerArray(request.getOwnerType())))	// IT-0002 検索方法修正
				.and(spec.ownerIdEquals(ownerId))	// IT-0002 検索方法修正
				.and(spec.startLatGreaterThanEqual(request.getMinLat()))	// sprint2で追加
				.and(spec.endLatLessThanEqual(request.getMaxLat()))			// sprint2で追加
				.and(spec.startLonGreaterThanEqual(request.getMinLon()))	// sprint2で追加
				.and(spec.endLonLessThanEqual(request.getMaxLon()))			// sprint2で追加
				.and(spec.deleteFlagEqual(false));
	}
}
