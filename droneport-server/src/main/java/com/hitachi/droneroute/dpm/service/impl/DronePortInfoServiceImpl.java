package com.hitachi.droneroute.dpm.service.impl;

import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.service.DroneRouteCommonService;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.Base64Utils;
import com.hitachi.droneroute.cmn.util.DateTimeUtils;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortEnvironmentInfoResponseDto;
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
import com.hitachi.droneroute.prm.constants.PriceInfoConstants;
import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListDetailElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListResponseDto;
import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
import com.hitachi.droneroute.prm.service.PriceInfoSearchListService;
import com.hitachi.droneroute.prm.service.impl.PriceInfoServiceImpl;
import com.hitachi.droneroute.prm.specification.PriceInfoSpecification;
import io.hypersistence.utils.hibernate.type.range.Range;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 離着陸場情報サービス実装クラス */
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

  // 料金管理
  private final PriceInfoServiceImpl priceInfoService;
  private final PriceInfoRepository priceInfoRepository;
  private final PriceInfoSearchListService priceInfoSearchService;

  /** {@inheritDoc} */
  @Transactional
  @Override
  public DronePortInfoRegisterResponseDto register(
      DronePortInfoRegisterRequestDto dto, UserInfoDto user) {

    // 格納中機体IDの存在チェック
    checkStoredAircraftIdValid(dto.getStoredAircraftId());
    // エンティティ作成
    DronePortInfoEntity newEntity = new DronePortInfoEntity();
    // 登録する値を設定
    String newDronePortId = createDronePortId(dto.getDronePortManufacturerId());
    Optional<DronePortInfoEntity> optIdCheckEntity =
        dronePortInfoRepository.findByDronePortId(newDronePortId);
    if (optIdCheckEntity.isPresent()) {
      // 新たに採番した離着陸場IDが存在する場合は、ID重複でエラーとする
      throw new ServiceErrorException(
          MessageFormat.format("離着陸場IDが重複しています。離着陸場ID:{0}", newDronePortId));
    }
    setEntity(dto, newEntity);
    newEntity.setDronePortId(newDronePortId);
    newEntity.setCreateTime(DateTimeUtils.getUtcCurrentTimestamp());
    newEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
    newEntity.setOperatorId(user.getUserOperatorId());

    // DB登録呼び出し
    DronePortInfoEntity registeredEntity = dronePortInfoRepository.save(newEntity);

    // 離着陸場状態テーブル登録
    DronePortStatusEntity newStatusEntity = new DronePortStatusEntity();
    setRegisterEntity(dto, newStatusEntity);
    newStatusEntity.setDronePortId(newDronePortId);
    newStatusEntity.setCreateTime(DateTimeUtils.getUtcCurrentTimestamp());
    newStatusEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
    newStatusEntity.setOperatorId(user.getUserOperatorId());
    // DB登録呼出
    dronePortStatusRepository.save(newStatusEntity);

    // 新規の登録なので予約解除は不要

    // 処理結果編集
    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId(registeredEntity.getDronePortId());

    // 料金管理登録
    List<PriceInfoRequestDto> priceInfoDtoList = dto.getPriceInfos();
    if (priceInfoDtoList != null && !priceInfoDtoList.isEmpty()) {
      for (int i = 0; i < priceInfoDtoList.size(); i++) {
        // 料金情報リクエストのリソースIDを離着陸場IDで補完
        priceInfoDtoList.get(i).setResourceId(responseDto.getDronePortId());
        priceInfoDtoList.get(i).setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
        priceInfoDtoList.get(i).setOperatorId(user.getUserOperatorId());
      }
      priceInfoService.process(priceInfoDtoList);
    } else {
      // 処理なし
    }

    return responseDto;
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public DronePortInfoRegisterResponseDto update(
      DronePortInfoRegisterRequestDto dto, UserInfoDto user) {
    // 格納中機体IDの存在チェック
    checkStoredAircraftIdValid(dto.getStoredAircraftId());

    // 離着陸場情報を更新
    updateDronePortInfo(dto, user);

    // 離着陸場状態を更新、予約を取消
    updateDronePortStatus(dto, user);

    // 処理結果編集
    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId(dto.getDronePortId());

    // 料金管理登録
    List<PriceInfoRequestDto> priceInfoDtoList = dto.getPriceInfos();
    if (priceInfoDtoList != null && !priceInfoDtoList.isEmpty()) {
      for (int i = 0; i < priceInfoDtoList.size(); i++) {
        // 料金情報リクエストのリソースIDを離着陸場IDで補完
        priceInfoDtoList.get(i).setResourceId(dto.getDronePortId());
        priceInfoDtoList.get(i).setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
        priceInfoDtoList.get(i).setOperatorId(user.getUserOperatorId());
      }
      priceInfoService.process(priceInfoDtoList);
    }

    return responseDto;
  }

  /**
   * 離着陸場情報を更新する
   *
   * @param dto 離着陸場予約情報登録更新要求
   * @param user 認可ユーザー情報
   */
  private void updateDronePortInfo(DronePortInfoRegisterRequestDto dto, UserInfoDto user) {
    // 既存レコードを検索
    Optional<DronePortInfoEntity> optInfoEntity =
        dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dto.getDronePortId());
    if (optInfoEntity.isEmpty()) {
      throw new NotFoundException(
          MessageFormat.format("離着陸場情報が見つかりません。離着陸場ID:{0}", dto.getDronePortId()));
    }
    // 既存エンティティに更新する値を設定する
    DronePortInfoEntity entity = optInfoEntity.get();

    if (!isNullDronePortInfo(dto)) {
      // 離着陸場情報テーブルに更新する項目がある場合
      setEntity(dto, entity);
      entity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
      entity.setOperatorId(user.getUserOperatorId());
      // DB更新呼出
      dronePortInfoRepository.save(entity);
    }
  }

  /**
   * 離着陸場状態を更新、予約情報を取り消す
   *
   * @param dto 離着陸場予約情報登録更新要求
   * @param user 認可ユーザー情報
   */
  private void updateDronePortStatus(DronePortInfoRegisterRequestDto dto, UserInfoDto user) {
    Optional<DronePortStatusEntity> optStatusEntity =
        dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dto.getDronePortId());
    if (optStatusEntity.isEmpty()) {
      throw new NotFoundException(
          MessageFormat.format("離着陸場状態が見つかりません。離着陸場ID:{0}", dto.getDronePortId()));
    }

    DronePortStatusEntity entity = optStatusEntity.get();

    // 離着陸場状態について、変更差分から離着陸場状態更新、予約取消を行う
    if (!isNullDronePortStatus(dto)) {
      // 離着陸場状態テーブルに更新する項目がある場合

      // 使用不可(既存)から、メンテナンス中に変更はエラーとする
      if (Objects.nonNull(entity.getInactiveStatus())
          && entity.getInactiveStatus() == DronePortConstants.ACTIVE_STATUS_UNAVAILABLE
          && dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_MAINTENANCE) {
        throw new ServiceErrorException("動作状況を、使用不可からメンテナンス中に変更することはできません。");
      }
      // 既存エンティティに更新する値を設定する
      setUpdateEntity(dto, entity);
      entity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
      entity.setOperatorId(user.getUserOperatorId());
      // DB更新呼出
      dronePortStatusRepository.save(entity);

      if (Objects.nonNull(dto.getActiveStatus())
          && (dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_MAINTENANCE
              || dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_UNAVAILABLE)) {
        // 使用不可日時範囲に重なる予約情報を検索
        List<DronePortReserveInfoEntity> overlappedReservation =
            findOverlapedReservation(
                dto.getDronePortId(), dto.getInactiveTimeFrom(), dto.getInactiveTimeTo());
        // 予約取消を行う
        overlappedReservation.forEach(
            e -> {
              e.setReservationActiveFlag(false);
              e.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
              dronePortReserveInfoRepository.save(e);
            });
      }
    }
  }

  /** {@inheritDoc} */
  @Transactional
  @Override
  public void delete(String dronePortId, UserInfoDto user) {
    // 既存レコードを検索
    Optional<DronePortInfoEntity> optInfoEntity =
        dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId);
    if (optInfoEntity.isEmpty()) {
      throw new NotFoundException(MessageFormat.format("離着陸場情報が見つかりません。離着陸場ID:{0}", dronePortId));
    }
    Optional<DronePortStatusEntity> optStatusEntity =
        dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId);
    if (optStatusEntity.isEmpty()) {
      throw new NotFoundException(MessageFormat.format("離着陸場状態が見つかりません。離着陸場ID:{0}", dronePortId));
    }
    DronePortInfoEntity entity = optInfoEntity.get();
    DronePortStatusEntity statusEntity = optStatusEntity.get();

    // 離着陸場情報テーブルを更新
    entity.setDeleteFlag(true);
    entity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
    entity.setOperatorId(user.getUserOperatorId());
    // DB更新呼出
    dronePortInfoRepository.save(entity);
    // 離着陸場情報テーブルを更新
    statusEntity.setDeleteFlag(true);
    statusEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
    statusEntity.setOperatorId(user.getUserOperatorId());
    // DB更新呼出
    dronePortStatusRepository.save(statusEntity);

    // リソースIDに紐づく料金情報を削除
    PriceInfoSpecification<PriceInfoEntity> spec = new PriceInfoSpecification<>();
    // 料金情報を検索
    List<PriceInfoEntity> priceInfoentityList =
        priceInfoRepository.findAll(Specification.where(spec.resourceIdIn(List.of(dronePortId))));
    if (!priceInfoentityList.isEmpty()) {
      for (PriceInfoEntity priceInfoEntity : priceInfoentityList) {
        priceInfoEntity.setDeleteFlag(true);
        priceInfoEntity.setUpdateTime(DateTimeUtils.getUtcCurrentTimestamp());
        priceInfoEntity.setOperatorId(user.getUserOperatorId());
        // DB更新呼出
        priceInfoRepository.save(priceInfoEntity);
        // 料金情報履歴登録
        priceInfoService.registerPriceHistoryInfo(priceInfoEntity);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public DronePortInfoListResponseDto getList(DronePortInfoListRequestDto dto, UserInfoDto user) {
    // 現在日時を取得する
    LocalDateTime currentDateTime = LocalDateTime.now();
    // ソート、ページ、検索条件を生成する
    Sort sort = createSort(dto.getSortOrders(), dto.getSortColumns(), logger);
    Pageable pageable = createPageRequest(dto.getPerPage(), dto.getPage(), sort);
    Specification<DronePortInfoEntity> spec =
        createSpecification(dto, Timestamp.valueOf(currentDateTime), user);
    // 離着陸場情報を検索
    Page<DronePortInfoEntity> pageResult = null;
    List<DronePortInfoEntity> entityListResult = null;
    if (Objects.isNull(pageable)) {
      // ページ制御なし
      if (Objects.isNull(sort)) {
        // ソートなし
        entityListResult = dronePortInfoRepository.findAll(spec);
      } else {
        // ソートあり
        entityListResult = dronePortInfoRepository.findAll(spec, sort);
      }
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

    // 料金情報取得
    boolean requirePayload = Boolean.valueOf(dto.getIsRequiredPriceInfo()) == true;
    if (requirePayload && !detailList.isEmpty()) {
      PriceInfoSearchListRequestDto priceInfoReq = new PriceInfoSearchListRequestDto();
      // ResourceId をカンマ区切りで抜き出し
      String ResourceIds =
          detailList.stream()
              .map(DronePortInfoListResponseElement::getDronePortId)
              .collect(Collectors.joining(","));
      priceInfoReq.setResourceId(ResourceIds);
      priceInfoReq.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
      PriceInfoSearchListResponseDto priceInfoRes =
          priceInfoSearchService.getPriceInfoList(priceInfoReq);

      // 料金情報をマッピング（リソースIDをキーとしたマップを作成）
      Map<String, List<PriceInfoSearchListDetailElement>> priceInfoMap =
          priceInfoRes.getResources().stream()
              .collect(
                  Collectors.toMap(
                      PriceInfoSearchListElement::getResourceId,
                      PriceInfoSearchListElement::getPriceInfos));

      // 各DronePortに対応する料金情報をセット
      for (DronePortInfoListResponseElement detailDto : detailList) {
        List<PriceInfoSearchListDetailElement> priceInfoList =
            priceInfoMap.getOrDefault(detailDto.getDronePortId(), new ArrayList<>());
        detailDto.setPriceInfos(priceInfoList); // 料金情報をセット
      }
    } else {
      // 各DronePortに対応する料金情報をセット(空リスト)
      for (DronePortInfoListResponseElement detailDto : detailList) {
        detailDto.setPriceInfos(Collections.emptyList());
      }
    }

    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(detailList);
    if (Objects.nonNull(pageResult)) {
      // ページ情報の結果を設定する
      responseDto.setPerPage(pageResult.getSize());
      responseDto.setCurrentPage(pageResult.getNumber() + 1);
      responseDto.setLastPage(pageResult.getTotalPages());
      responseDto.setTotal((int) pageResult.getTotalElements());
    }
    return responseDto;
  }

  /** {@inheritDoc} */
  @Override
  public DronePortInfoDetailResponseDto getDetail(
      String dronePortId, Boolean isRequiredPriceInfo, UserInfoDto user) {
    // 現在日時を取得する
    LocalDateTime currentDateTime = LocalDateTime.now();
    // 自事業者のオペレーターIDと比較し公開可否フラグ条件の制御
    String systemOperatorId =
        systemSettings.getString(
            DronePortConstants.SETTING_OPERATOR_INFO,
            DronePortConstants.SETTING_SYSTEM_OPERATOR_ID);
    boolean isOwnOperator = systemOperatorId.equals(user.getAffiliatedOperatorId());
    Optional<DronePortInfoEntity> optEntity;
    if (isOwnOperator) {
      // 自事業者の場合：通常検索
      optEntity = dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId);
    } else {
      // 他事業者の場合:他事業者提供可否フラグTrueのみ取得
      DronePortInfoSpecification<DronePortInfoEntity> spec = new DronePortInfoSpecification<>();
      optEntity =
          dronePortInfoRepository.findOne(
              Specification.where(spec.deleteFlagEqual(false))
                  .and(Specification.where(spec.dronePortIdEqual(dronePortId)))
                  .and(spec.publicFlagEqual(true)));
    }
    if (optEntity.isEmpty()) {
      throw new NotFoundException(MessageFormat.format("離着陸場情報が見つかりません。離着陸場ID:{0}", dronePortId));
    }
    // 処理結果編集
    DronePortInfoEntity entity = optEntity.get();
    DronePortInfoDetailResponseDto responseDto = new DronePortInfoDetailResponseDto();
    setDetailResponseDto(currentDateTime, entity, responseDto);

    // 料金情報取得
    if (Boolean.TRUE.equals(isRequiredPriceInfo)) {
      PriceInfoSearchListRequestDto priceInfoReq = new PriceInfoSearchListRequestDto();
      priceInfoReq.setResourceId(dronePortId);
      priceInfoReq.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
      PriceInfoSearchListResponseDto priceInfoRes =
          priceInfoSearchService.getPriceInfoList(priceInfoReq);

      // 結果が存在する場合は料金情報をセット、なければ空リスト
      if (!priceInfoRes.getResources().isEmpty()) {
        responseDto.setPriceInfos(priceInfoRes.getResources().get(0).getPriceInfos());
      } else {
        responseDto.setPriceInfos(Collections.emptyList());
      }
    } else {
      // 料金情報をセット(空リスト)
      responseDto.setPriceInfos(Collections.emptyList());
    }
    return responseDto;
  }

  /** {@inheritDoc} */
  @Override
  public DronePortEnvironmentInfoResponseDto getEnvironment(String dronePortId, UserInfoDto user) {
    // 自事業者のオペレーターIDと比較し公開可否フラグ条件の制御
    String systemOperatorId =
        systemSettings.getString(
            DronePortConstants.SETTING_OPERATOR_INFO,
            DronePortConstants.SETTING_SYSTEM_OPERATOR_ID);
    boolean isOwnOperator = systemOperatorId.equals(user.getAffiliatedOperatorId());
    Optional<DronePortInfoEntity> optEntity;
    if (isOwnOperator) {
      // 自事業者の場合：通常検索
      optEntity = dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId);
    } else {
      // 他事業者の場合:他事業者提供可否フラグTrueのみ取得
      DronePortInfoSpecification<DronePortInfoEntity> spec = new DronePortInfoSpecification<>();
      optEntity =
          dronePortInfoRepository.findOne(
              Specification.where(spec.deleteFlagEqual(false))
                  .and(Specification.where(spec.dronePortIdEqual(dronePortId)))
                  .and(spec.publicFlagEqual(true)));
    }
    if (optEntity.isEmpty()) {
      throw new NotFoundException(MessageFormat.format("離着陸場情報が見つかりません。離着陸場ID:{0}", dronePortId));
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
        Objects.isNull(visTelm.getObservationTime())
            ? null
            : StringUtils.toUtcDateTimeString(visTelm.getObservationTime().toLocalDateTime()));
    return responseDto;
  }

  /**
   * base64をバイト型にデコード<br>
   * 入力チェックの前に実施すること
   *
   * @param dto 離着陸場情報登録更新要求
   */
  @Override
  public void decodeBinary(DronePortInfoRegisterRequestDto dto) {
    if (dto.getImageData() != null) {
      if (dto.getImageData().length() == 0) {
        dto.setImageBinary(new byte[] {});
      } else {
        Base64Utils util =
            new Base64Utils(
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
   * エンティティを離着陸場情報詳細取得応答に設定
   *
   * @param currentDateTime 現在日時
   * @param entity エンティティ
   * @param dto 離着陸場情報詳細取得応答
   */
  private void setDetailResponseDto(
      LocalDateTime currentDateTime,
      DronePortInfoEntity entity,
      DronePortInfoDetailResponseDto dto) {
    BeanUtils.copyProperties(entity, dto);
    // 現在の動作状況を設定
    dto.setActiveStatus(getCurrentStatus(currentDateTime, entity.getDronePortStatusEntity()));
    // 予定された動作状態を設定
    Object[] scheduledStatus =
        getScheduledStatus(currentDateTime, entity.getDronePortStatusEntity());
    dto.setScheduledStatus((Integer) scheduledStatus[0]);
    dto.setInactiveTimeFrom((String) scheduledStatus[1]);
    dto.setInactiveTimeTo((String) scheduledStatus[2]);

    dto.setUpdateTime(getUpdateTime(entity));
    if (Objects.nonNull(entity.getImageBinary()) && entity.getImageBinary().length > 0) {
      Base64Utils util =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  DronePortConstants.SETTINGS_IMAGE_DATA,
                  DronePortConstants.SETTINGS_SUPPORT_FORMAT));
      // データURI付きのbase64文字列を設定する
      dto.setImageData(
          util.createDataUriWithBase64(entity.getImageFormat(), entity.getImageBinary()));
    }
    dto.setStoredAircraftId(
        Objects.isNull(entity.getDronePortStatusEntity().getStoredAircraftId())
            ? null
            : entity.getDronePortStatusEntity().getStoredAircraftId().toString());
  }

  /**
   * 検索で取得した離着陸場情報を、離着陸場情報一覧取得応答要素に設定する
   *
   * @param currentDateTime 現在日時
   * @param entity (離着陸場状態がjoinされている)
   * @param dto 離着陸場情報一覧取得応答要素
   */
  private void setDetailResponseDto(
      LocalDateTime currentDateTime,
      DronePortInfoEntity entity,
      DronePortInfoListResponseElement dto) {
    BeanUtils.copyProperties(entity, dto);
    // 現在の動作状況を設定
    dto.setActiveStatus(getCurrentStatus(currentDateTime, entity.getDronePortStatusEntity()));
    // 予定された動作状態を設定
    Object[] scheduledStatus =
        getScheduledStatus(currentDateTime, entity.getDronePortStatusEntity());
    dto.setScheduledStatus((Integer) scheduledStatus[0]);
    dto.setInactiveTimeFrom((String) scheduledStatus[1]);
    dto.setInactiveTimeTo((String) scheduledStatus[2]);

    dto.setUpdateTime(getUpdateTime(entity));
    dto.setStoredAircraftId(
        Objects.isNull(entity.getDronePortStatusEntity().getStoredAircraftId())
            ? null
            : entity.getDronePortStatusEntity().getStoredAircraftId().toString());
  }

  /**
   * 現在の動作状況を取得
   *
   * @param currentDateTime 現在日時
   * @param entity 離着陸場状態エンティティ
   * @return 現在の動作状況
   */
  private Integer getCurrentStatus(LocalDateTime currentDateTime, DronePortStatusEntity entity) {
    Integer result = null;

    Range<LocalDateTime> t = entity.getInactiveTime();
    if (Objects.nonNull(t)
        && ((currentDateTime.isAfter(t.lower()) || currentDateTime.isEqual(t.lower()))
            && (t.hasUpperBound() ? currentDateTime.isBefore(t.upper()) : true))) {
      result = entity.getInactiveStatus();
    } else {
      result = entity.getActiveStatus();
    }

    return result;
  }

  /**
   * 予定された動作状態と使用不可時間を取得
   *
   * @param currentDateTime 現在日時
   * @param entity 離着陸場状態エンティティ
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
    return new Object[] {status, from, to};
  }

  /**
   * 離着陸場情報と離着陸場状態の更新日時を比較して、最新値を返却する
   *
   * @param entity 離着陸場情報(離着陸場状態がjoinされている)
   * @return 離着陸場情報と離着陸場状態の更新日時のうち、最新の日時をUTCの日時文字列で返却する 例）2024-01-01T00:00:00Z
   */
  private String getUpdateTime(DronePortInfoEntity entity) {
    String wkTimestamp = null;
    if (entity.getUpdateTime().compareTo(entity.getDronePortStatusEntity().getUpdateTime()) > 0) {
      // 離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合
      wkTimestamp = StringUtils.toUtcDateTimeString(entity.getUpdateTime().toLocalDateTime());
    } else {
      wkTimestamp =
          StringUtils.toUtcDateTimeString(
              entity.getDronePortStatusEntity().getUpdateTime().toLocalDateTime());
    }
    return wkTimestamp;
  }

  /**
   * 離着陸場情報登録更新要求を離着陸場情報エンティティに設定
   *
   * @param dto 離着陸場情報登録更新要求
   * @param entity 離着陸場情報エンティティ
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
      Base64Utils util =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  DronePortConstants.SETTINGS_IMAGE_DATA,
                  DronePortConstants.SETTINGS_SUPPORT_FORMAT));
      // データURIから画像フォーマットを取得する
      entity.setImageFormat(util.getSubtype(dto.getImageData()));
      // 事前にbase64からバイナリ変換を行っていること。
      entity.setImageBinary(dto.getImageBinary());
    }
    if (dto.getPublicFlag() != null) {
      entity.setPublicFlag(dto.getPublicFlag());
    }
    entity.setDeleteFlag(false);
  }

  /**
   * 離着陸場情報登録更新要求を離着陸場状態エンティティに設定(登録用)
   *
   * @param dto 離着陸場情報登録更新要求
   * @param entity 離着陸場状態エンティティ
   */
  private void setRegisterEntity(
      DronePortInfoRegisterRequestDto dto, DronePortStatusEntity entity) {
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
      entity.setInactiveTime(
          Range.localDateTimeRange(
              String.format(
                  "[%s,%s)",
                  StringUtils.parseDatetimeStringToLocalDateTime(dto.getInactiveTimeFrom())
                      .toString(),
                  org.springframework.util.StringUtils.hasText(dto.getInactiveTimeTo())
                      ? StringUtils.parseDatetimeStringToLocalDateTime(dto.getInactiveTimeTo())
                          .toString()
                      : "")));
    }
    if (Objects.nonNull(dto.getStoredAircraftId())) {
      entity.setStoredAircraftId(UUID.fromString(dto.getStoredAircraftId()));
    }
    entity.setDeleteFlag(false);
  }

  /**
   * 離着陸場情報登録更新要求を離着陸場状態エンティティに設定(更新用)
   *
   * @param dto 離着陸場情報登録更新要求
   * @param entity 離着陸場状態エンティティ
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
      entity.setInactiveTime(
          Range.localDateTimeRange(
              String.format(
                  "[%s,%s)",
                  StringUtils.parseDatetimeStringToLocalDateTime(dto.getInactiveTimeFrom())
                      .toString(),
                  org.springframework.util.StringUtils.hasText(dto.getInactiveTimeTo())
                      ? StringUtils.parseDatetimeStringToLocalDateTime(dto.getInactiveTimeTo())
                          .toString()
                      : "")));
    }
    if (Objects.nonNull(dto.getStoredAircraftId())) {
      entity.setStoredAircraftId(UUID.fromString(dto.getStoredAircraftId()));
    }
    // 削除フラグは更新しない
  }

  /**
   * 離着陸場IDを採番する
   *
   * @param mfr 離着陸場メーカーID
   * @return 離着陸場ID
   */
  private String createDronePortId(String mfr) {
    Long seq = dronePortInfoRepository.getNextSequenceValue();
    String opr =
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_ID, DronePortConstants.SETTINGS_DRONEPORT_ID_OPR);
    String format =
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_ID,
            DronePortConstants.SETTINGS_DRONEPORT_ID_FORMAT);
    return String.format(format, opr, mfr, seq);
  }

  /**
   * 離着陸場情報の更新対象項目のnull状態を判定する
   *
   * @param dto 離着陸場情報登録更新要求
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
        && Objects.isNull(dto.getImageData())
        && Objects.isNull(dto.getPublicFlag());
  }

  /**
   * 離着陸場状態の更新対象項目のnull状態を判定する
   *
   * @param dto 離着陸場情報登録更新要求
   * @return　true:更新項目が全てnull, false:null以外の項目が1つ以上ある
   */
  private boolean isNullDronePortStatus(DronePortInfoRegisterRequestDto dto) {
    return Objects.isNull(dto.getActiveStatus())
        && Objects.isNull(dto.getInactiveTimeFrom())
        && Objects.isNull(dto.getInactiveTimeTo())
        && Objects.isNull(dto.getStoredAircraftId());
  }

  /**
   * 日時範囲に少しでも重なる予約情報を検索する
   *
   * @param timeFrom 日時範囲条件の開始時間
   * @param timeTo 日時範囲条件の終了時間
   * @return 検索結果
   */
  private List<DronePortReserveInfoEntity> findOverlapedReservation(
      String dronePortId, String timeFrom, String timeTo) {
    DronePortReserveInfoSpecification<DronePortReserveInfoEntity> spec =
        new DronePortReserveInfoSpecification<>();
    // 離着陸場予約情報を検索
    List<DronePortReserveInfoEntity> entityList =
        dronePortReserveInfoRepository.findAll(
            Specification.where(spec.dronePortIdEqual(dronePortId))
                .and(
                    spec.tsrangeOverlap(
                        StringUtils.parseDatetimeString(timeFrom),
                        StringUtils.parseDatetimeString(timeTo)))
                .and(spec.reservationActiveFlag(true)) // 過去に取消を行った予約は対象外
                .and(spec.deleteFlagEqual(false)));
    return entityList;
  }

  /**
   * 検索条件オブジェクトを生成する
   *
   * @param dto 離着陸場情報一覧取得リクエストDTO
   * @param currentTime 現在日時
   * @param user 認可ユーザー情報
   * @return 検索条件オブジェクト
   */
  private Specification<DronePortInfoEntity> createSpecification(
      DronePortInfoListRequestDto dto, Timestamp currentTime, UserInfoDto user) {
    List<Integer> statuses = new ArrayList<>();
    Integer[] intStatuses = StringUtils.stringToIntegerArray(dto.getActiveStatus());
    if (Objects.nonNull(intStatuses)) {
      statuses = Arrays.asList(intStatuses);
    }

    // 自事業者のオペレーターIDと比較し公開可否フラグ条件の制御
    String systemOperatorId =
        systemSettings.getString(
            DronePortConstants.SETTING_OPERATOR_INFO,
            DronePortConstants.SETTING_SYSTEM_OPERATOR_ID);
    DronePortInfoSpecification<DronePortInfoEntity> spec = new DronePortInfoSpecification<>();
    boolean isOwnOperator = systemOperatorId.equals(user.getAffiliatedOperatorId());
    // 公開可否フラグ
    Boolean publicFlag = null;
    if (isOwnOperator) {
      // 自事業者の場合:リクエストから公開可否フラグ条件に設定
      if (org.springframework.util.StringUtils.hasText(dto.getPublicFlag())) {
        publicFlag = Boolean.valueOf(dto.getPublicFlag());
      } else {
        // 条件設定なし(公開可否問わず取得)
      }
    } else {
      // 他事業者の場合:trueを公開可否フラグ条件に設定
      publicFlag = true;
    }

    return Specification.where(spec.dronePortNameContains(dto.getDronePortName()))
        .and(spec.addressContains(dto.getAddress()))
        .and(spec.manufacturerContains(dto.getManufacturer()))
        .and(spec.serialNumberContains(dto.getSerialNumber()))
        .and(spec.portTypeContains(StringUtils.stringToIntegerArray(dto.getPortType())))
        .and(spec.startLatGreaterThanEqual(dto.getMinLat()))
        .and(spec.endLatLessThanEqual(dto.getMaxLat()))
        .and(spec.startLonGreaterThanEqual(dto.getMinLon()))
        .and(spec.endLonLessThanEqual(dto.getMaxLon()))
        .and(spec.supportDroneTypeContains(dto.getSupportDroneType()))
        .and(createActiveStatusSpecification(statuses, currentTime))
        .and(spec.publicFlagEqual(publicFlag))
        .and(spec.deleteFlagEqual(false));
  }

  /**
   * 離着陸場の動作状況に基づく検索条件を生成する
   *
   * @param statuses ステータスリスト
   * @param currentTime 現在日時
   * @return 検索条件オブジェクト
   */
  private Specification<DronePortInfoEntity> createActiveStatusSpecification(
      List<Integer> statuses, Timestamp currentTime) {
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

  /**
   * 動作状況が準備中、使用可の条件を生成する
   *
   * @param statuses ステータスリスト
   * @param currentTime 現在日時
   * @return 検索条件オブジェクト
   */
  private Specification<DronePortInfoEntity> createActiveStatusInnerSpec(
      List<Integer> statuses, Timestamp currentTime) {
    DronePortInfoSpecification<DronePortInfoEntity> spec = new DronePortInfoSpecification<>();
    Specification<DronePortInfoEntity> innerSpec = Specification.where(null);

    innerSpec = innerSpec.or(spec.activeStatusInner1(statuses));
    innerSpec = innerSpec.or(spec.activeStatusInner2(statuses, currentTime));
    innerSpec = innerSpec.or(spec.activeStatusInner3(statuses, currentTime));

    return innerSpec;
  }

  /**
   * 格納中機体IDの存在をチェックする。存在しない場合は例外発生する。
   *
   * @param aircraftId 格納中機体ID
   */
  private void checkStoredAircraftIdValid(String aircraftId) {
    if (org.springframework.util.StringUtils.hasText(aircraftId)) {
      Optional<AircraftInfoEntity> optEntity =
          aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(aircraftId));
      if (optEntity.isEmpty()) {
        // 機体IDが存在しない場合はエラー
        throw new NotFoundException(MessageFormat.format("機体情報が見つかりません。格納中機体ID:{0}", aircraftId));
      }
    }
  }
}
