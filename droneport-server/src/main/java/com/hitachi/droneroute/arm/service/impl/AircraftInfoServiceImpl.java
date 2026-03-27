package com.hitachi.droneroute.arm.service.impl;

import com.hitachi.droneroute.arm.constants.AircraftConstants;
import com.hitachi.droneroute.arm.dto.AircraftInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementRes;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoListElementRes;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoSearchListElement;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListElement;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListResponseDto;
import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.arm.entity.FileInfoEntity;
import com.hitachi.droneroute.arm.entity.PayloadInfoEntity;
import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
import com.hitachi.droneroute.arm.repository.FileInfoRepository;
import com.hitachi.droneroute.arm.repository.PayloadInfoRepository;
import com.hitachi.droneroute.arm.service.AircraftInfoService;
import com.hitachi.droneroute.arm.specification.AircraftInfoSpecification;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.service.DroneRouteCommonService;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.Base64Utils;
import com.hitachi.droneroute.cmn.util.DateTimeUtils;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
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
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
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

/** 機体情報サービス実装クラス */
@Service
@RequiredArgsConstructor
public class AircraftInfoServiceImpl implements AircraftInfoService, DroneRouteCommonService {

  /** 機体情報リポジトリ */
  private final AircraftInfoRepository aircraftInfoRepository;

  /** ファイル情報リポジトリ */
  private final FileInfoRepository fileInfoRepository;

  /** ペイロード情報リポジトリ */
  private final PayloadInfoRepository payloadInfoRepository;

  /** システムセッティング */
  private final SystemSettings systemSettings;

  /** ロガー */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /** 料金管理サービス */
  private final PriceInfoServiceImpl priceInfoService;

  /** 料金管理リポジトリ */
  private final PriceInfoRepository priceInfoRepository;

  /** 料金管理検索サービス */
  private final PriceInfoSearchListService priceInfoSearchService;

  @Transactional
  @Override
  /**
   * 機体情報登録サービス
   *
   * @param request 登録する機体情報
   * @param user API呼び出しユーザー情報
   * @return 登録された機体情報のIDを含むレスポンスDTO
   * @throws ValidationErrorException 入力値エラーがある場合にスローされる例外
   * @throws ServiceErrorException 登録処理に失敗した場合にスローされる例外
   */
  public AircraftInfoResponseDto postData(AircraftInfoRequestDto request, UserInfoDto user) {

    // 補足情報の上限確認
    checkPayloadCount(request);
    checkFileCount(request);

    // 機体情報登録更新Entity
    AircraftInfoEntity newEntity = new AircraftInfoEntity();

    // 登録データ作成(機体)
    setEntity(newEntity, request);
    newEntity.setAircraftId(UUID.randomUUID());
    newEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
    newEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    newEntity.setOperatorId(user.getUserOperatorId());
    newEntity.setDeleteFlag(false);

    // DB登録(機体)
    AircraftInfoEntity aircraftInfoEntity = aircraftInfoRepository.save(newEntity);

    // 処理結果(機体)
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    if (Objects.nonNull(aircraftInfoEntity.getAircraftId())) {
      responseDto.setAircraftId(aircraftInfoEntity.getAircraftId().toString());
    } else {
      throw new ServiceErrorException("機体情報の生成に失敗しました。");
    }

    // 登録/更新/削除データ作成 ＆ DB登録(補足資料情報ファイル)
    processFileEntityChanges(request, aircraftInfoEntity.getAircraftId(), user);

    // 登録/更新/削除データ作成 ＆ DB登録(ペイロード情報)
    processPayloadEntityChanges(request, aircraftInfoEntity.getAircraftId(), user);

    // 料金管理登録
    List<PriceInfoRequestDto> priceInfoDtoList = request.getPriceInfos();
    if (priceInfoDtoList != null && !priceInfoDtoList.isEmpty()) {
      for (int i = 0; i < priceInfoDtoList.size(); i++) {
        // 料金情報リクエストのリソースIDを機体IDで補完
        priceInfoDtoList.get(i).setResourceId(responseDto.getAircraftId());
        priceInfoDtoList.get(i).setResourceType(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT);
        priceInfoDtoList.get(i).setOperatorId(user.getUserOperatorId());
      }
      priceInfoService.process(priceInfoDtoList);
    } else {
      // 処理なし
    }

    return responseDto;
  }

  @Transactional
  @Override
  /**
   * 機体情報更新サービス
   *
   * @param request 更新する機体情報
   * @param user API呼び出しユーザー情報
   * @return 更新された機体情報のIDを含むレスポンスDTO
   * @throws ValidationErrorException 入力値エラーがある場合にスローされる例外
   * @throws NotFoundException 更新対象の機体情報が見つからない場合にスローされる例外
   * @throws ServiceErrorException 更新処理に失敗した場合にスローされる例外
   */
  public AircraftInfoResponseDto putData(AircraftInfoRequestDto request, UserInfoDto user) {

    // 補足情報の上限確認
    checkPayloadCount(request);
    checkFileCount(request);

    // 既存レコード検索(機体)
    Optional<AircraftInfoEntity> optEntity =
        aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(
            UUID.fromString(request.getAircraftId()));

    if (optEntity.isEmpty()) {
      throw new NotFoundException(
          MessageFormat.format("機体IDが見つかりません。機体ID:{0}", request.getAircraftId()));
    }

    // 機体情報登録更新Entity(機体)
    AircraftInfoEntity updateEntity = optEntity.get();

    // 更新データ作成(機体)
    setEntity(updateEntity, request);
    updateEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    updateEntity.setOperatorId(user.getUserOperatorId());

    // DB登録(機体)
    AircraftInfoEntity aircraftInfoEntity = aircraftInfoRepository.save(updateEntity);

    // 処理結果(機体)
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    if (Objects.nonNull(aircraftInfoEntity.getAircraftId())) {
      responseDto.setAircraftId(aircraftInfoEntity.getAircraftId().toString());
    } else {
      throw new ServiceErrorException("機体情報の更新に失敗しました。");
    }

    // 登録/更新/削除データ作成 ＆ DB登録(補足資料情報ファイル)
    processFileEntityChanges(request, aircraftInfoEntity.getAircraftId(), user);

    // 登録/更新/削除データ作成 ＆ DB登録(ペイロード情報)
    processPayloadEntityChanges(request, aircraftInfoEntity.getAircraftId(), user);

    // 料金管理登録
    List<PriceInfoRequestDto> priceInfoDtoList = request.getPriceInfos();
    if (priceInfoDtoList != null && !priceInfoDtoList.isEmpty()) {
      for (int i = 0; i < priceInfoDtoList.size(); i++) {
        // 料金情報リクエストのリソースIDを機体IDで補完
        priceInfoDtoList.get(i).setResourceId(request.getAircraftId());
        priceInfoDtoList.get(i).setResourceType(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT);
        priceInfoDtoList.get(i).setOperatorId(user.getUserOperatorId());
      }
      priceInfoService.process(priceInfoDtoList);
    }

    return responseDto;
  }

  @Transactional
  @Override
  /**
   * 機体情報削除サービス
   *
   * @param aircraftId 削除する機体情報のID
   * @param user API呼び出しユーザー情報
   * @throws NotFoundException 削除対象の機体情報が見つからない場合にスローされる例外
   * @throws ServiceErrorException 削除処理に失敗した場合にスローされる例外
   */
  public void deleteData(String aircraftId, UserInfoDto user) {
    // 既存レコード検索
    Optional<AircraftInfoEntity> optEntity =
        aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(aircraftId));

    if (optEntity.isEmpty()) {
      throw new NotFoundException(MessageFormat.format("機体IDが見つかりません。機体ID:{0}", aircraftId));
    }

    AircraftInfoEntity entity = optEntity.get();

    // 処理
    entity.setDeleteFlag(true);
    entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    entity.setOperatorId(user.getUserOperatorId());

    // 機体情報論理削除
    aircraftInfoRepository.save(entity);

    // 機体に紐づく補足情報ファイルの論理削除(0件もエラーとしない)
    fileInfoRepository.deleteByAircraftId(UUID.fromString(aircraftId), user.getUserOperatorId());

    // 機体に紐づくペイロード情報の論理削除(0件もエラーとしない)
    payloadInfoRepository.deleteByAircraftId(UUID.fromString(aircraftId), user.getUserOperatorId());

    // リソースIDに紐づく料金情報を削除
    PriceInfoSpecification<PriceInfoEntity> spec = new PriceInfoSpecification<>();
    // 料金情報を検索
    List<PriceInfoEntity> priceInfoentityList =
        priceInfoRepository.findAll(Specification.where(spec.resourceIdIn(List.of(aircraftId))));
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

  @Transactional
  @Override
  /**
   * 機体情報一覧取得サービス
   *
   * @param request 取得する機体情報の検索条件を含むリクエストDTO
   * @param user API呼び出しユーザー情報
   * @return 検索条件に合致する機体情報のリストを含むレスポンスDTO
   * @throws ValidationErrorException 入力値エラーがある場合にスローされる例外
   * @throws ServiceErrorException 検索処理に失敗した場合にスローされる例外
   */
  public AircraftInfoSearchListResponseDto getList(
      AircraftInfoSearchListRequestDto request, UserInfoDto user) {

    // ソート制御設定、ページ制御設定
    Sort sort = createSort(request.getSortOrders(), request.getSortColumns(), logger);
    Pageable pageable = createPageRequest(request.getPerPage(), request.getPage(), sort);

    // データ取得
    Specification<AircraftInfoEntity> spec = createSpecification(request, user);

    // ソート制御設定、ページ制御設定を指定してデータ取得
    Page<AircraftInfoEntity> pageResult = null;
    List<AircraftInfoEntity> entityList = null;
    if (Objects.isNull(pageable)) {
      // ページ制御なし
      if (Objects.isNull(sort)) {
        // ソートなし
        entityList = aircraftInfoRepository.findAll(spec);
      } else {
        // ソートあり
        entityList = aircraftInfoRepository.findAll(spec, sort);
      }
    } else {
      // ページ制御あり
      pageResult = aircraftInfoRepository.findAll(spec, pageable);
      entityList = pageResult.getContent();
      logger.debug("search result:" + pageResult.toString());
    }

    // ペイロード情報取得
    boolean requirePayload = Boolean.valueOf(request.getIsRequiredPayloadInfo()) == true;

    // ペイロードを取得、aircraftId 毎にグルーピング
    java.util.Map<UUID, List<AircraftInfoPayloadInfoSearchListElement>> payloadsByAircraft =
        new java.util.HashMap<>();
    // リクエストでペイロード取得が必要か判定
    if (requirePayload) {
      List<PayloadInfoEntity> payloadEntitylist = null;
      List<UUID> ids =
          entityList.stream().map(AircraftInfoEntity::getAircraftId).collect(Collectors.toList());
      // ペイロード取得
      payloadEntitylist = payloadInfoRepository.findAllByAircraftIdInAndDeleteFlagFalse(ids);

      if (!payloadEntitylist.isEmpty()) {
        // aircraftId ごとに DTO に変換してグルーピングする
        payloadsByAircraft =
            payloadEntitylist.stream()
                .collect(
                    Collectors.groupingBy(
                        PayloadInfoEntity::getAircraftId,
                        Collectors.mapping(
                            entity -> {
                              AircraftInfoPayloadInfoSearchListElement dto =
                                  new AircraftInfoPayloadInfoSearchListElement();
                              setPayloadInfoNoImageNoFileResponseDto(dto, entity);
                              return dto;
                            },
                            Collectors.toList())));
      }
    }

    // 一覧応答の画像イメージを削除
    List<AircraftInfoSearchListElement> detailList = new ArrayList<>();
    for (AircraftInfoEntity entity : entityList) {
      AircraftInfoSearchListElement detail = new AircraftInfoSearchListElement();
      setAircraftInfoNoImageResponseDto(detail, entity);
      // ペイロード情報格納
      if (requirePayload) {
        // payloadList をセット（ペイロード未取得または該当なしの場合は空リスト）
        List<AircraftInfoPayloadInfoSearchListElement> pl = new ArrayList<>();
        pl = payloadsByAircraft.getOrDefault(entity.getAircraftId(), new ArrayList<>());
        detail.setPayloadInfos(pl);
      } else {
        if (user.isDummyUserFlag()) {
          // API-Key認可の場合ペイロード情報はnull
          detail.setPayloadInfos(null);
        } else {
          detail.setPayloadInfos(Collections.emptyList());
        }
      }
      detailList.add(detail);
    }

    // 料金情報取得
    boolean requirePrice = Boolean.valueOf(request.getIsRequiredPriceInfo()) == true;
    if (requirePrice && !detailList.isEmpty()) {
      PriceInfoSearchListRequestDto priceInfoReq = new PriceInfoSearchListRequestDto();
      // ResourceId をカンマ区切りで抜き出し
      String ResourceIds =
          detailList.stream()
              .map(AircraftInfoSearchListElement::getAircraftId)
              .collect(Collectors.joining(","));
      priceInfoReq.setResourceId(ResourceIds);
      priceInfoReq.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT));
      PriceInfoSearchListResponseDto priceInfoRes =
          priceInfoSearchService.getPriceInfoList(priceInfoReq);

      // 料金情報をマッピング（リソースIDをキーとしたマップを作成）
      Map<String, List<PriceInfoSearchListDetailElement>> priceInfoMap =
          priceInfoRes.getResources().stream()
              .collect(
                  Collectors.toMap(
                      PriceInfoSearchListElement::getResourceId,
                      PriceInfoSearchListElement::getPriceInfos));

      // 各機体IDに対応する料金情報をセット
      for (AircraftInfoSearchListElement detailDto : detailList) {
        List<PriceInfoSearchListDetailElement> priceInfoList =
            priceInfoMap.getOrDefault(detailDto.getAircraftId(), new ArrayList<>());
        detailDto.setPriceInfos(priceInfoList);
      }
    } else {
      // 各DronePortに対応する料金情報をセット(空リスト)
      for (AircraftInfoSearchListElement detailDto : detailList) {
        detailDto.setPriceInfos(Collections.emptyList());
      }
    }

    AircraftInfoSearchListResponseDto responseDto = new AircraftInfoSearchListResponseDto();
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

  @Transactional
  @Override
  /**
   * 機体情報詳細取得サービス
   *
   * @param aircraftId 取得する機体情報のID
   * @param isRequiredPayloadInfo ペイロード情報の取得要否
   * @param isRequiredPriceInfo 料金情報の取得要否
   * @param user API呼び出しユーザー情報
   * @return 指定された機体IDに対応する機体情報の詳細を含むレスポンスDTO
   * @throws NotFoundException 指定された機体IDに対応する機体情報が見つからない場合にスローされる例外
   * @throws ServiceErrorException 詳細情報の取得処理に失敗した場合にスローされる例外
   */
  public AircraftInfoDetailResponseDto getDetail(
      String aircraftId,
      Boolean isRequiredPayloadInfo,
      Boolean isRequiredPriceInfo,
      UserInfoDto user) {
    // 自事業者のオペレーターIDと比較し公開可否フラグ条件の制御
    String systemOperatorId =
        systemSettings.getString(
            DronePortConstants.SETTING_OPERATOR_INFO,
            DronePortConstants.SETTING_SYSTEM_OPERATOR_ID);
    boolean isOwnOperator = systemOperatorId.equals(user.getAffiliatedOperatorId());
    // 機体既存レコード検索
    Optional<AircraftInfoEntity> optEntity = null;
    if (isOwnOperator) {
      // 自事業者の場合：
      optEntity =
          aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(aircraftId));
    } else {
      // 他事業者の場合：条件として公開可否フラグがtrueであることを追加
      optEntity =
          aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalseAndPublicFlagTrue(
              UUID.fromString(aircraftId));
    }

    if (optEntity.isEmpty()) {
      throw new NotFoundException(MessageFormat.format("機体IDが見つかりません。機体ID:{0}", aircraftId));
    }

    // 補足資料情報検索
    List<AircraftInfoFileInfoListElementRes> fileInfos =
        fileInfoRepository.findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(
            UUID.fromString(aircraftId));

    // 処理
    AircraftInfoEntity entity = optEntity.get();
    AircraftInfoDetailResponseDto responseDto = new AircraftInfoDetailResponseDto();
    // 補足資料リスト設定
    responseDto.setFileInfos(fileInfos);

    if (Boolean.TRUE.equals(isRequiredPayloadInfo)) {
      // ペイロード情報検索
      List<PayloadInfoEntity> payloadInfos =
          payloadInfoRepository.findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(
              UUID.fromString(aircraftId));
      // ペイロード情報リスト設定
      List<AircraftInfoPayloadInfoListElementRes> req = new ArrayList<>();
      setPayloadInfoDetailResponseDto(req, payloadInfos);
      responseDto.setPayloadInfos(req);
    } else {
      // ペイロード情報をセット(空リスト)
      responseDto.setPayloadInfos(Collections.emptyList());
    }

    // 料金情報取得
    if (Boolean.TRUE.equals(isRequiredPriceInfo)) {
      PriceInfoSearchListRequestDto priceInfoReq = new PriceInfoSearchListRequestDto();
      priceInfoReq.setResourceId(aircraftId);
      priceInfoReq.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT));
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
    // 機体情報設定
    setAircraftInfoDetailResponseDto(responseDto, entity);

    return responseDto;
  }

  @Transactional(readOnly = true)
  @Override
  /**
   * 補足資料情報ファイルダウンロードサービス 指定された補足資料IDに該当するファイル情報を取得する。
   *
   * @param fileId 補足資料ID
   * @param user API呼び出しユーザー情報
   * @return ダウンロードするファイルの情報を含むFileInfoEntity
   * @throws NotFoundException 指定された補足資料IDに対応するファイル情報が見つからない場合にスローされる例外
   * @throws ServiceErrorException ファイル情報の取得処理に失敗した場合にスローされる例外
   */
  public FileInfoEntity downloadFile(String fileId, UserInfoDto user) {
    // 既存レコード検索
    Optional<FileInfoEntity> optEntity =
        fileInfoRepository.findByFileIdAndDeleteFlagFalse(UUID.fromString(fileId));

    if (optEntity.isEmpty()) {
      throw new NotFoundException(MessageFormat.format("補足資料IDが見つかりません。補足資料ID:{0}", fileId));
    }
    // 自事業者のオペレーターIDと比較し公開可否フラグ条件の制御
    String systemOperatorId =
        systemSettings.getString(
            AircraftConstants.SETTING_OPERATOR_INFO, AircraftConstants.SETTING_SYSTEM_OPERATOR_ID);
    boolean isOwnOperator = systemOperatorId.equals(user.getAffiliatedOperatorId());
    if (!isOwnOperator) {
      // 他事業者の場合：紐づく機体情報の公開可否フラグを確認する
      Optional<AircraftInfoEntity> aircraftEntity =
          aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(
              optEntity.get().getAircraftId());
      if (aircraftEntity.isEmpty() || aircraftEntity.get().getPublicFlag() == false) {
        // 非公開状態の場合：資料情報未取得扱い
        throw new NotFoundException(MessageFormat.format("補足資料IDが見つかりません。補足資料ID:{0}", fileId));
      }
    }
    return optEntity.get();
  }

  @Transactional(readOnly = true)
  @Override
  /**
   * ペイロード添付ファイルダウンロードサービス 指定されたペイロードIDに該当するファイル情報を取得する。
   *
   * @param payloadId ペイロードID
   * @param user API呼び出しユーザー情報
   * @return ダウンロードするファイルの情報を含むPayloadInfoEntity
   * @throws NotFoundException 指定されたペイロードIDに対応するファイル情報が見つからない場合にスローされる例外
   * @throws ServiceErrorException ファイル情報の取得処理に失敗した場合にスローされる例外
   */
  public PayloadInfoEntity downloadPayloadFile(String payloadId, UserInfoDto user) {
    // 既存レコード検索
    Optional<PayloadInfoEntity> optEntity =
        payloadInfoRepository.findByPayloadIdAndDeleteFlagFalse(UUID.fromString(payloadId));

    if (optEntity.isEmpty()) {
      throw new NotFoundException(MessageFormat.format("ペイロードIDが見つかりません。ペイロードID:{0}", payloadId));
    }
    // 自事業者のオペレーターIDと比較し公開可否フラグ条件の制御
    String systemOperatorId =
        systemSettings.getString(
            AircraftConstants.SETTING_OPERATOR_INFO, AircraftConstants.SETTING_SYSTEM_OPERATOR_ID);
    boolean isOwnOperator = systemOperatorId.equals(user.getAffiliatedOperatorId());
    if (!isOwnOperator) {
      // 他事業者の場合：紐づく機体情報の公開可否フラグを確認する
      Optional<AircraftInfoEntity> aircraftEntity =
          aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(
              optEntity.get().getAircraftId());
      if (aircraftEntity.isEmpty() || aircraftEntity.get().getPublicFlag() == false) {
        // 非公開状態の場合：資料情報未取得扱い
        throw new NotFoundException(MessageFormat.format("ペイロードIDが見つかりません。ペイロードID:{0}", payloadId));
      }
    }
    return optEntity.get();
  }

  /**
   * base64をバイト型にデコード
   *
   * @param request 機体情報登録更新要求
   * @throws ServiceErrorException デコード処理に失敗した場合にスローされる例外
   */
  @Override
  public void decodeBinary(AircraftInfoRequestDto request) {
    // 機体の画像データ変換
    if (request.getImageData() != null) {
      if (request.getImageData().length() == 0) {
        request.setImageBinary(new byte[] {});
      } else {
        Base64Utils util =
            new Base64Utils(
                systemSettings.getStringValueArray(
                    AircraftConstants.SETTINGS_IMAGE_DATA,
                    AircraftConstants.SETTINGS_SUPPORT_FORMAT));
        if (util.checkSubtype(request.getImageData())) {
          request.setImageBinary(util.getBinaryData(request.getImageData()));
        }
      }
    }
    // ペイロードの画像データ変換
    if (request.getPayloadInfos() != null) {
      Base64Utils util =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  AircraftConstants.SETTINGS_IMAGE_DATA,
                  AircraftConstants.SETTINGS_SUPPORT_FORMAT));
      // ファイル単位でデコード
      for (AircraftInfoPayloadInfoListElementReq payloadInfo : request.getPayloadInfos()) {
        if (payloadInfo.getImageData() != null) {
          if (payloadInfo.getImageData().length() == 0) {
            payloadInfo.setImageBinary(new byte[] {});
          } else if (util.checkSubtype(payloadInfo.getImageData())) {
            payloadInfo.setImageBinary(util.getBinaryData(payloadInfo.getImageData()));
          } else {
            continue;
          }
        } else {
          continue;
        }
      }
    } else {
      // nullの場合は処理なし
    }
  }

  /**
   * 補足情報ファイルのbase64をバイト型にデコード
   *
   * @param request 機体情報登録更新要求
   * @throws ServiceErrorException デコード処理に失敗した場合にスローされる例外
   */
  @Override
  public void decodeFileData(AircraftInfoRequestDto request) {
    // サポートするファイル形式を取得
    String[] supportedFileTypes =
        systemSettings.getStringValueArray(
            AircraftConstants.SETTINGS_FILE_DATA, AircraftConstants.SETTINGS_SUPPORT_FILE_MIME);
    // Base64ユーティリティ生成
    Base64Utils util = new Base64Utils(supportedFileTypes);
    // 補足資料情報
    if (request.getFileInfos() != null) {
      // ファイル単位でデコード
      for (AircraftInfoFileInfoListElementReq fileInfo : request.getFileInfos()) {
        if (fileInfo == null) {
          // もし配列にNull要素があった場合はスキップ
          continue;
        } else if (fileInfo.getFileData() != null && fileInfo.getFileData().length() != 0) {
          // ファイルデータが設定されている場合にデコード処理を実施
          // サポートするMIMEタイプかチェック
          if (util.checkMimeType(fileInfo.getFileData())) {
            // デコード処理実施
            fileInfo.setFileBinary(util.getAllMimeBinaryData(fileInfo.getFileData()));
          } else {
            // サポートしないMIMEタイプの場合は処理をスキップ(バリデータでエラーになる)
            continue;
          }
        } else {
          // ファイルデータがNULLの場合には処理スキップ(バリデータでエラーになる)
          continue;
        }
      }
    } else {
      // nullの場合は処理なし
    }
    // ペイロード情報
    if (request.getPayloadInfos() != null) {
      // ファイル単位でデコード
      for (AircraftInfoPayloadInfoListElementReq payloadInfo : request.getPayloadInfos()) {
        if (payloadInfo == null) {
          // もし配列にNull要素があった場合はスキップ
          continue;
        } else if (payloadInfo.getFileData() != null && payloadInfo.getFileData().length() != 0) {
          // ファイルデータが設定されている場合にデコード処理を実施
          // サポートするMIMEタイプかチェック
          if (util.checkMimeType(payloadInfo.getFileData())) {
            // デコード処理実施
            payloadInfo.setFileBinary(util.getAllMimeBinaryData(payloadInfo.getFileData()));
          } else {
            // サポートしないMIMEタイプの場合は処理をスキップ(バリデータでエラーになる)
            continue;
          }
        } else {
          // ファイルデータがNULLの場合には処理スキップ(バリデータでエラーになる)
          continue;
        }
      }
    } else {
      // nullの場合は処理なし
    }
  }

  /**
   * エンティティにリクエスト情報を設定
   *
   * @param aircraftInfoEntity 機体情報エンティティ
   * @param request 機体情報登録更新要求DTO
   * @throws ServiceErrorException データの設定に失敗した場合にスローされる例外
   */
  private void setEntity(AircraftInfoEntity aircraftInfoEntity, AircraftInfoRequestDto request) {
    if (request.getAircraftId() != null && !request.getAircraftId().isBlank()) {
      aircraftInfoEntity.setAircraftId(UUID.fromString(request.getAircraftId()));
    }

    if (request.getAircraftName() != null) {
      aircraftInfoEntity.setAircraftName(request.getAircraftName());
    }

    if (request.getManufacturer() != null) {
      aircraftInfoEntity.setManufacturer(request.getManufacturer());
    }

    if (request.getModelNumber() != null) {
      aircraftInfoEntity.setModelNumber(request.getModelNumber());
    }

    if (request.getModelName() != null) {
      aircraftInfoEntity.setModelName(request.getModelName());
    }

    if (request.getManufacturingNumber() != null) {
      aircraftInfoEntity.setManufacturingNumber(request.getManufacturingNumber());
    }

    if (request.getAircraftType() != null) {
      aircraftInfoEntity.setAircraftType(request.getAircraftType());
    }

    if (request.getMaxTakeoffWeight() != null) {
      aircraftInfoEntity.setMaxTakeoffWeight(request.getMaxTakeoffWeight());
    }

    if (request.getBodyWeight() != null) {
      aircraftInfoEntity.setBodyWeight(request.getBodyWeight());
    }

    if (request.getMaxFlightSpeed() != null) {
      aircraftInfoEntity.setMaxFlightSpeed(request.getMaxFlightSpeed());
    }

    if (request.getMaxFlightTime() != null) {
      aircraftInfoEntity.setMaxFlightTime(request.getMaxFlightTime());
    }

    if (request.getLat() != null) {
      aircraftInfoEntity.setLat(request.getLat());
    }

    if (request.getLon() != null) {
      aircraftInfoEntity.setLon(request.getLon());
    }

    if (request.getCertification() != null) {
      aircraftInfoEntity.setCertification(request.getCertification());
    }

    if (request.getDipsRegistrationCode() != null) {
      aircraftInfoEntity.setDipsRegistrationCode(request.getDipsRegistrationCode());
    }

    if (request.getOwnerType() != null) {
      aircraftInfoEntity.setOwnerType(request.getOwnerType());
    }

    if (request.getOwnerId() != null && !request.getOwnerId().isBlank()) {
      aircraftInfoEntity.setOwnerId(UUID.fromString(request.getOwnerId()));
    }

    if (request.getPublicFlag() != null) {
      aircraftInfoEntity.setPublicFlag(request.getPublicFlag());
    }

    if (request.getImageBinary() != null) {
      Base64Utils util =
          new Base64Utils(
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
   * 機体情報詳細応答用DTOにエンティティからのデータを設定
   *
   * @param responseDto 機体情報詳細応答用DTO
   * @param entity 機体情報エンティティ
   * @throws ServiceErrorException データの設定に失敗した場合にスローされる例外
   */
  private void setAircraftInfoDetailResponseDto(
      AircraftInfoDetailResponseDto responseDto, AircraftInfoEntity entity) {
    BeanUtils.copyProperties(entity, responseDto);
    if (Objects.nonNull(entity.getAircraftId())) {
      responseDto.setAircraftId(entity.getAircraftId().toString());
    } else {
      throw new ServiceErrorException("機体情報の取得に失敗しました。");
    }
    if (Objects.nonNull(entity.getOwnerId())) {
      responseDto.setOwnerId(entity.getOwnerId().toString());
    }
    if (Objects.nonNull(entity.getImageBinary()) && entity.getImageBinary().length > 0) {
      Base64Utils util =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  AircraftConstants.SETTINGS_IMAGE_DATA,
                  AircraftConstants.SETTINGS_SUPPORT_FORMAT));
      // データURI付きのbase64文字列を設定する
      responseDto.setImageData(
          util.createDataUriWithBase64(entity.getImageFormat(), entity.getImageBinary()));
    }
  }

  /**
   * ペイロード情報詳細応答用DTOにエンティティリストからのデータを設定
   *
   * @param entities ペイロードエンティティリスト
   * @return AircraftInfoPayloadInfoListElementReq のリスト
   * @throws ServiceErrorException データの設定に失敗した場合にスローされる例外
   */
  private void setPayloadInfoDetailResponseDto(
      List<AircraftInfoPayloadInfoListElementRes> req, List<PayloadInfoEntity> entities) {
    if (entities == null || entities.isEmpty()) {
      req = new ArrayList<>();
      return;
    }

    Base64Utils util =
        new Base64Utils(
            systemSettings.getStringValueArray(
                AircraftConstants.SETTINGS_IMAGE_DATA, AircraftConstants.SETTINGS_SUPPORT_FORMAT));

    for (PayloadInfoEntity entity : entities) {
      AircraftInfoPayloadInfoListElementRes dto = new AircraftInfoPayloadInfoListElementRes();
      BeanUtils.copyProperties(entity, dto);
      if (Objects.nonNull(entity.getPayloadId())) {
        dto.setPayloadId(entity.getPayloadId().toString());
      } else {
        throw new ServiceErrorException("ペイロード情報の取得に失敗しました。");
      }
      if (Objects.nonNull(entity.getImageData()) && entity.getImageData().length > 0) {
        dto.setImageData(
            util.createDataUriWithBase64(entity.getImageFormat(), entity.getImageData()));
      }
      req.add(dto);
    }
  }

  /**
   * 機体情報一覧応答用にエンティティからのデータを画像なしで設定
   *
   * @param responseDto 機体情報一覧応答用DTO
   * @param entity 機体情報エンティティ
   * @throws ServiceErrorException データの設定に失敗した場合にスローされる例外
   */
  private void setAircraftInfoNoImageResponseDto(
      AircraftInfoSearchListElement responseDto, AircraftInfoEntity entity) {
    BeanUtils.copyProperties(entity, responseDto);
    if (Objects.nonNull(entity.getAircraftId())) {
      responseDto.setAircraftId(entity.getAircraftId().toString());
    } else {
      throw new ServiceErrorException("機体情報の取得に失敗しました。");
    }
    if (Objects.nonNull(entity.getOwnerId())) {
      responseDto.setOwnerId(entity.getOwnerId().toString());
    }
  }

  /**
   * ペイロード情報応答用にエンティティからのデータを画像なし、添付ファイルなしで設定
   *
   * @param responseDto ペイロード情報応答用DTO
   * @param entity ペイロード情報エンティティ
   * @throws ServiceErrorException データの設定に失敗した場合にスローされる例外
   */
  private void setPayloadInfoNoImageNoFileResponseDto(
      AircraftInfoPayloadInfoSearchListElement responseDto, PayloadInfoEntity entity) {
    BeanUtils.copyProperties(entity, responseDto);
    if (Objects.nonNull(entity.getPayloadId())) {
      responseDto.setPayloadId(entity.getPayloadId().toString());
    } else {
      throw new ServiceErrorException("ペイロード情報の取得に失敗しました。");
    }
  }

  /**
   * 検索条件オブジェクトを生成する
   *
   * @param request 機体情報一覧取得リクエストDTO
   * @param user ユーザ情報
   * @return 検索条件オブジェクト
   * @throws ServiceErrorException 検索条件の生成に失敗した場合にスローされる例外
   */
  private Specification<AircraftInfoEntity> createSpecification(
      AircraftInfoSearchListRequestDto request, UserInfoDto user) {
    AircraftInfoSpecification<AircraftInfoEntity> spec = new AircraftInfoSpecification<>();

    // 公開可否フラグ
    Boolean publicFlag = null;
    // 自事業者のオペレーターIDと比較し公開可否フラグ条件の制御
    String systemOperatorId =
        systemSettings.getString(
            AircraftConstants.SETTING_OPERATOR_INFO, AircraftConstants.SETTING_SYSTEM_OPERATOR_ID);
    boolean isOwnOperator = systemOperatorId.equals(user.getAffiliatedOperatorId());
    if (isOwnOperator) {
      // 自事業者の場合:リクエストから公開可否フラグ条件に設定
      if (org.springframework.util.StringUtils.hasText(request.getPublicFlag())) {
        publicFlag = Boolean.valueOf(request.getPublicFlag());
      } else {
        // 条件設定なし(公開可否問わず取得)
      }
    } else {
      // 他事業者の場合:trueを公開可否フラグ条件に設定
      publicFlag = true;
    }

    UUID ownerId = null;
    if (request.getOwnerId() != null) {
      if (!request.getOwnerId().isBlank()) {
        ownerId = UUID.fromString(request.getOwnerId());
      }
    }
    Boolean certification = null;
    if (request.getCertification() != null) {
      if (!request.getCertification().isBlank()) {
        certification = Boolean.valueOf(request.getCertification());
      }
    }

    // モデル情報リストに値がある場合
    if (request.getModelInfos() != null && !request.getModelInfos().isEmpty()) {
      return Specification.where(spec.modelInfosMatch(request.getModelInfos()))
          .and(spec.publicFlagEqual(publicFlag))
          .and(spec.deleteFlagEqual(false));
    }

    return Specification.where(spec.aircraftNameContains(request.getAircraftName()))
        .and(spec.manufacturerContains(request.getManufacturer()))
        .and(spec.modelNumberContains(request.getModelNumber()))
        .and(spec.modelNameContains(request.getModelName()))
        .and(spec.manufacturingNumberContains(request.getManufacturingNumber()))
        .and(spec.aircraftTypeContains(StringUtils.stringToIntegerArray(request.getAircraftType())))
        .and(spec.certiticationEqual(certification))
        .and(spec.dipsRegistrationCodeContains(request.getDipsRegistrationCode()))
        .and(spec.ownerTypeContains(StringUtils.stringToIntegerArray(request.getOwnerType())))
        .and(spec.ownerIdEquals(ownerId))
        .and(spec.publicFlagEqual(publicFlag))
        .and(spec.startLatGreaterThanEqual(request.getMinLat()))
        .and(spec.endLatLessThanEqual(request.getMaxLat()))
        .and(spec.startLonGreaterThanEqual(request.getMinLon()))
        .and(spec.endLonLessThanEqual(request.getMaxLon()))
        .and(spec.deleteFlagEqual(false));
  }

  /**
   * 補足資料情報登録/更新/削除処理
   *
   * @param request 機体情報リクエストDTO
   * @param aircraftId 機体ID
   * @param user ユーザー情報DTO
   * @throws NotFoundException 指定された補足資料IDが存在しない場合にスローされる例外
   * @throws ServiceErrorException 補足資料情報の登録/更新に失敗した場合にスローされる例外
   */
  private void processFileEntityChanges(
      AircraftInfoRequestDto request, UUID aircraftId, UserInfoDto user) {
    // 登録/更新/削除データ作成 ＆ DB登録(補足資料情報ファイル)
    if (request.getFileInfos() == null) {
      // 補足資料情報ファイルがnullの場合は処理なし
      return;
    } else {
      // 現在登録されている補足資料情報ファイルの最大ファイル番号を取得
      Optional<Integer> maxFileNumberOpt =
          fileInfoRepository.findMaxFileNumberByAircraftIdAndDeleteFlagFalse(aircraftId);
      int fileNumber = maxFileNumberOpt.orElse(0);

      // ループ内で使うBase64Utilsのインスタンス作成
      Base64Utils util =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  AircraftConstants.SETTINGS_FILE_DATA,
                  AircraftConstants.SETTINGS_SUPPORT_FILE_MIME));

      // ファイル単位でループ
      for (AircraftInfoFileInfoListElementReq fileInfoReq : request.getFileInfos()) {
        FileInfoEntity fileInfoEntity;
        FileInfoEntity savedFileInfoEntity;
        if (fileInfoReq == null
            || fileInfoReq.getProcessingType() == null
            || !(AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER
                    == fileInfoReq.getProcessingType().intValue()
                || AircraftConstants.AIRCRAFT_PROCESSING_TYPE_UPDATE
                    == fileInfoReq.getProcessingType().intValue()
                || AircraftConstants.AIRCRAFT_PROCESSING_TYPE_DELETE
                    == fileInfoReq.getProcessingType().intValue())) {
          // イレギュラーな要素は無視(バリエーションチェックでエラーになるはず)
          continue;
        } else {
          int procType = fileInfoReq.getProcessingType().intValue();
          if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER == procType) {
            // 処理種別が登録の場合
            // ファイル番号インクリメント
            fileNumber++;
            // 登録データ作成(補足資料情報ファイル)
            fileInfoEntity = new FileInfoEntity();
            fileInfoEntity.setFileId(UUID.randomUUID());
            fileInfoEntity.setAircraftId(aircraftId);
            fileInfoEntity.setFileNumber(fileNumber);
            fileInfoEntity.setFileLogicalName(fileInfoReq.getFileLogicalName());
            fileInfoEntity.setFilePhysicalName(fileInfoReq.getFilePhysicalName());
            fileInfoEntity.setFileData(fileInfoReq.getFileBinary());
            fileInfoEntity.setFileFormat(util.getMimeType(fileInfoReq.getFileData()));
            fileInfoEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
            fileInfoEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            fileInfoEntity.setOperatorId(user.getUserOperatorId());
            fileInfoEntity.setDeleteFlag(false);
          } else {
            // 処理種別が登録以外(更新または削除)の場合
            // 既存レコード検索(補足資料情報ファイル)
            Optional<FileInfoEntity> optFileEntity =
                fileInfoRepository.findByFileIdAndDeleteFlagFalse(
                    UUID.fromString(fileInfoReq.getFileId()));
            if (optFileEntity.isEmpty()) {
              throw new NotFoundException(
                  MessageFormat.format("補足資料IDが見つかりません。補足資料ID:{0}", fileInfoReq.getFileId()));
            }
            // 更新/削除データ作成(補足資料情報ファイル)
            fileInfoEntity = optFileEntity.get();

            if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_UPDATE == procType) {
              // 処理種別が更新の場合
              if (org.springframework.util.StringUtils.hasText(fileInfoReq.getFileLogicalName())) {
                fileInfoEntity.setFileLogicalName(fileInfoReq.getFileLogicalName());
              }
              if (org.springframework.util.StringUtils.hasText(fileInfoReq.getFilePhysicalName())) {
                fileInfoEntity.setFilePhysicalName(fileInfoReq.getFilePhysicalName());
              }
              if (org.springframework.util.StringUtils.hasText(fileInfoReq.getFileData())) {
                fileInfoEntity.setFileData(fileInfoReq.getFileBinary());
              }
              if (org.springframework.util.StringUtils.hasText(fileInfoReq.getFileData())) {
                fileInfoEntity.setFileFormat(util.getMimeType(fileInfoReq.getFileData()));
              }
              fileInfoEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
              fileInfoEntity.setOperatorId(user.getUserOperatorId());
            } else {
              // 処理種別が削除の場合
              // 論理削除データ設定(補足資料情報ファイル)
              fileInfoEntity.setDeleteFlag(true);
              fileInfoEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
              fileInfoEntity.setOperatorId(user.getUserOperatorId());
            }
          }
          // DB更新(補足資料情報ファイル)
          savedFileInfoEntity = fileInfoRepository.save(fileInfoEntity);

          // 処理結果(補足資料情報ファイル)
          if (Objects.isNull(savedFileInfoEntity.getFileId())) {
            throw new ServiceErrorException("補足資料情報の更新に失敗しました。");
          } else {
            // 登録成功時は次のデータへ
            continue;
          }
        }
      }
    }
  }

  /**
   * ペイロード情報登録/更新/削除処理
   *
   * @param request 機体情報リクエストDTO
   * @param aircraftId 機体ID
   * @param user ユーザー情報DTO
   * @throws NotFoundException 指定されたペイロードIDが存在しない場合にスローされる例外
   * @throws ServiceErrorException ペイロード情報の登録/更新に失敗した場合にスローされる例外
   */
  private void processPayloadEntityChanges(
      AircraftInfoRequestDto request, UUID aircraftId, UserInfoDto user) {
    // 登録/更新/削除データ作成 ＆ DB登録(ペイロード情報)
    if (request.getPayloadInfos() == null) {
      // ペイロード情報がnullの場合は処理なし
      return;
    } else {
      // 現在登録されている補足資料情報ファイルの最大ファイル番号を取得
      Optional<Integer> maxFileNumberOpt =
          payloadInfoRepository.findMaxPayloadNumberByAircraftIdAndDeleteFlagFalse(aircraftId);
      int fileNumber = maxFileNumberOpt.orElse(0);
      // ループ内で使うBase64Utilsのインスタンス作成
      Base64Utils utilImage =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  AircraftConstants.SETTINGS_IMAGE_DATA,
                  AircraftConstants.SETTINGS_SUPPORT_FORMAT));
      Base64Utils utilFile =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  AircraftConstants.SETTINGS_FILE_DATA,
                  AircraftConstants.SETTINGS_SUPPORT_FILE_MIME));

      // ペイロード情報単位でループ
      for (AircraftInfoPayloadInfoListElementReq payloadInfoReq : request.getPayloadInfos()) {
        PayloadInfoEntity payloadInfoEntity;
        PayloadInfoEntity savedPayloadInfoEntity;
        if (payloadInfoReq == null
            || payloadInfoReq.getProcessingType() == null
            || !(AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER
                    == payloadInfoReq.getProcessingType().intValue()
                || AircraftConstants.AIRCRAFT_PROCESSING_TYPE_UPDATE
                    == payloadInfoReq.getProcessingType().intValue()
                || AircraftConstants.AIRCRAFT_PROCESSING_TYPE_DELETE
                    == payloadInfoReq.getProcessingType().intValue())) {
          // イレギュラーな要素は無視(バリエーションチェックでエラーになるはず)
          continue;
        } else {
          int procType = payloadInfoReq.getProcessingType().intValue();
          if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER == procType) {
            // 処理種別が登録の場合
            // ファイル番号インクリメント
            fileNumber++;
            // 登録データ作成(ペイロード情報)
            payloadInfoEntity = new PayloadInfoEntity();
            payloadInfoEntity.setPayloadId(UUID.randomUUID());
            payloadInfoEntity.setAircraftId(aircraftId);
            payloadInfoEntity.setPayloadNumber(fileNumber);
            payloadInfoEntity.setPayloadName(payloadInfoReq.getPayloadName());
            if (org.springframework.util.StringUtils.hasText(
                payloadInfoReq.getPayloadDetailText())) {
              payloadInfoEntity.setPayloadDetailText(payloadInfoReq.getPayloadDetailText());
            }
            if (payloadInfoReq.getImageBinary() != null
                && payloadInfoReq.getImageBinary().length > 0) {
              payloadInfoEntity.setImageData(payloadInfoReq.getImageBinary());
            }
            if (org.springframework.util.StringUtils.hasText(payloadInfoReq.getImageData())) {
              payloadInfoEntity.setImageFormat(utilImage.getSubtype(payloadInfoReq.getImageData()));
            }
            if (org.springframework.util.StringUtils.hasText(
                payloadInfoReq.getFilePhysicalName())) {
              payloadInfoEntity.setFilePhysicalName(payloadInfoReq.getFilePhysicalName());
            }
            if (org.springframework.util.StringUtils.hasText(payloadInfoReq.getFileData())) {
              payloadInfoEntity.setFileData(payloadInfoReq.getFileBinary());
            }
            if (org.springframework.util.StringUtils.hasText(payloadInfoReq.getFileData())) {
              payloadInfoEntity.setFileFormat(utilFile.getMimeType(payloadInfoReq.getFileData()));
            }
            payloadInfoEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
            payloadInfoEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            payloadInfoEntity.setOperatorId(user.getUserOperatorId());
            payloadInfoEntity.setDeleteFlag(false);
          } else {
            // 処理種別が登録以外(更新または削除)の場合
            // 既存レコード検索(補足資料情報ファイル)
            Optional<PayloadInfoEntity> optPayloadInfoEntity =
                payloadInfoRepository.findByPayloadIdAndDeleteFlagFalse(
                    UUID.fromString(payloadInfoReq.getPayloadId()));
            if (optPayloadInfoEntity.isEmpty()) {
              throw new NotFoundException(
                  MessageFormat.format(
                      "ペイロードIDが見つかりません。ペイロードID:{0}", payloadInfoReq.getPayloadId()));
            }
            // 更新/削除データ作成(ペイロード情報)
            payloadInfoEntity = optPayloadInfoEntity.get();

            if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_UPDATE == procType) {
              // 処理種別が更新の場合
              // 更新データ設定(ペイロード情報)
              if (org.springframework.util.StringUtils.hasText(payloadInfoReq.getPayloadName())) {
                payloadInfoEntity.setPayloadName(payloadInfoReq.getPayloadName());
              }
              if (org.springframework.util.StringUtils.hasText(
                  payloadInfoReq.getPayloadDetailText())) {
                payloadInfoEntity.setPayloadDetailText(payloadInfoReq.getPayloadDetailText());
              }
              if (org.springframework.util.StringUtils.hasText(payloadInfoReq.getFileData())) {
                payloadInfoEntity.setImageData(payloadInfoReq.getImageBinary());
              }
              if (org.springframework.util.StringUtils.hasText(payloadInfoReq.getImageData())) {
                payloadInfoEntity.setImageFormat(
                    utilImage.getSubtype(payloadInfoReq.getImageData()));
              }
              if (org.springframework.util.StringUtils.hasText(
                  payloadInfoReq.getFilePhysicalName())) {
                payloadInfoEntity.setFilePhysicalName(payloadInfoReq.getFilePhysicalName());
              }
              if (payloadInfoReq.getFileBinary() != null
                  && payloadInfoReq.getFileBinary().length > 0) {
                payloadInfoEntity.setFileData(payloadInfoReq.getFileBinary());
              }
              if (org.springframework.util.StringUtils.hasText(payloadInfoReq.getFileData())) {
                payloadInfoEntity.setFileFormat(utilFile.getMimeType(payloadInfoReq.getFileData()));
              }
              payloadInfoEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
              payloadInfoEntity.setOperatorId(user.getUserOperatorId());
            } else {
              // 処理種別が削除の場合
              // 論理削除データ設定(ペイロード情報)
              payloadInfoEntity.setDeleteFlag(true);
              payloadInfoEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
              payloadInfoEntity.setOperatorId(user.getUserOperatorId());
            }
          }
          // DB更新(ペイロード情報)
          savedPayloadInfoEntity = payloadInfoRepository.save(payloadInfoEntity);

          // 処理結果(ペイロード情報)
          if (Objects.isNull(savedPayloadInfoEntity.getPayloadId())) {
            throw new ServiceErrorException("ペイロード情報の更新に失敗しました。");
          } else {
            // 登録成功時は次のデータへ
            continue;
          }
        }
      }
    }
  }

  /**
   * ペイロード情報上限数確認
   *
   * @param request 機体情報登録/更新リクエストDTO
   * @throws ValidationErrorException ペイロード情報の上限数を超えた場合にスローされる例外
   */
  private void checkPayloadCount(AircraftInfoRequestDto request) {

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos = request.getPayloadInfos();
    if (payloadInfos != null && !payloadInfos.isEmpty()) {
      // ペイロード情報ありの場合
      // 登録件数取得
      long countRegist =
          payloadInfos.stream()
              .filter(Objects::nonNull)
              .filter(
                  x ->
                      x.getProcessingType() != null
                          && AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER
                              == x.getProcessingType())
              .count();
      if (countRegist == 0) {
        // 登録件数0件の場合
        return;
      }

      // 削除件数取得
      long countDelete =
          payloadInfos.stream()
              .filter(Objects::nonNull)
              .filter(
                  x ->
                      x.getProcessingType() != null
                          && AircraftConstants.AIRCRAFT_PROCESSING_TYPE_DELETE
                              == x.getProcessingType())
              .count();

      // 増減計算
      long countDiff = countRegist - countDelete;
      // ペイロード情報上限数
      int maxPayloadCount =
          systemSettings.getIntegerValue(
              AircraftConstants.SETTINGS_PAYLOAD_INFOS,
              AircraftConstants.SETTINGS_MAX_PAYLOAD_COUNT);

      if (countDiff > maxPayloadCount) {
        // 登録件数が上限数を超えている場合
        throw new ValidationErrorException(
            MessageFormat.format("ペイロード情報の数が上限数({0})を超えています。", maxPayloadCount));
      } else {
        // 機体ID有無確認
        if (request.getAircraftId() != null) {
          // 登録済み機体への更新処理の場合
          UUID aircraftId = UUID.fromString(request.getAircraftId());
          if (countDiff > 0) {
            // 登録件数がプラスの場合
            int countRegisted =
                payloadInfoRepository.countByAircraftIdAndDeleteFlagFalse(aircraftId);
            long countTotal = 0;

            countTotal = countRegisted + countDiff;
            if (countTotal > maxPayloadCount) {
              // 更新後のファイル数が上限数を超える場合
              throw new ValidationErrorException(
                  MessageFormat.format("ペイロード情報の数が上限数({0})を超えています。", maxPayloadCount));
            }
          }
        }
      }
    } else {
      // 処理なし
    }
  }

  /**
   * 補足資料情報上限数確認
   *
   * @param request 機体情報登録/更新リクエストDTO
   * @throws ValidationErrorException 補足資料情報の上限数を超えた場合にスローされる例外
   */
  private void checkFileCount(AircraftInfoRequestDto request) {

    List<AircraftInfoFileInfoListElementReq> fileInfos = request.getFileInfos();
    if (fileInfos != null && !fileInfos.isEmpty()) {
      // 補足資料ファイルありの場合
      // 登録件数取得
      long countRegist =
          fileInfos.stream()
              .filter(Objects::nonNull)
              .filter(
                  x ->
                      x.getProcessingType() != null
                          && AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER
                              == x.getProcessingType())
              .count();
      if (countRegist == 0) {
        // 登録件数0件の場合
        return;
      }

      // 削除件数取得
      long countDelete =
          fileInfos.stream()
              .filter(Objects::nonNull)
              .filter(
                  x ->
                      x.getProcessingType() != null
                          && AircraftConstants.AIRCRAFT_PROCESSING_TYPE_DELETE
                              == x.getProcessingType())
              .count();

      // 増減計算
      long countDiff = countRegist - countDelete;
      // 補足資料情報上限数
      int maxFileCount =
          systemSettings.getIntegerValue(
              AircraftConstants.SETTINGS_FILE_INFOS, AircraftConstants.SETTINGS_MAX_FILE_COUNT);

      if (countDiff > maxFileCount) {
        // 登録件数が上限数を超えている場合
        throw new ValidationErrorException(
            MessageFormat.format("補足資料情報の数が上限数({0})を超えています。", maxFileCount));
      } else {
        // 機体ID有無確認
        if (request.getAircraftId() != null) {
          // 登録済み機体への更新処理の場合
          UUID aircraftId = UUID.fromString(request.getAircraftId());
          if (countDiff > 0) {
            // 登録件数がプラスの場合
            int countRegisted = fileInfoRepository.countByAircraftIdAndDeleteFlagFalse(aircraftId);
            long countTotal = 0;

            countTotal = countRegisted + countDiff;
            if (countTotal > maxFileCount) {
              // 更新後のファイル数が上限数を超える場合
              throw new ValidationErrorException(
                  MessageFormat.format("補足資料情報の数が上限数({0})を超えています。", maxFileCount));
            }
          }
        }
      }
    } else {
      // 処理なし
    }
  }
}
