package com.hitachi.droneroute.arm.service.impl;

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
import com.hitachi.droneroute.cmn.constants.CommonConstants;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.service.DroneRouteCommonService;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import io.hypersistence.utils.hibernate.type.range.Range;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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

/** 機体予約情報サービス実装クラス */
@Service
@RequiredArgsConstructor
public class AircraftReserveInfoServiceImpl
    implements AircraftReserveInfoService, DroneRouteCommonService {

  /** 機体予約情報リポジトリ */
  private final AircraftReserveInfoRepository aircraftReserveInfoRepository;

  /** 機体情報リポジトリ */
  private final AircraftInfoRepository aircraftInfoRepository;

  /** システムセッティング */
  private final SystemSettings systemSettings;

  /** ロガー */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  /**
   * 機体予約情報登録
   *
   * @param request 予約するデータ
   * @param userInfo ユーザ情報
   * @return AircraftReserveInfoResponseDto
   * @throws ServiceErrorException 予約に失敗した場合にスローされる例外
   */
  public AircraftReserveInfoResponseDto postData(
      AircraftReserveInfoRequestDto request, UserInfoDto userInfo) {
    // 機体情報確認
    checkReserveRegest(request);

    // 機体予約情報登録更新Entity
    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();

    // 登録データ作成
    setEntity(entity, request);
    entity.setAircraftReservationId(UUID.randomUUID());
    entity.setCreateTime(new Timestamp(System.currentTimeMillis()));
    entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    entity.setOperatorId(userInfo.getUserOperatorId());
    entity.setReserveProviderId(UUID.fromString(userInfo.getAffiliatedOperatorId()));
    entity.setDeleteFlag(false);

    // DB登録
    AircraftReserveInfoEntity aircraftReserveInfoEntity =
        aircraftReserveInfoRepository.save(entity);

    // 編集
    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    if (Objects.nonNull(aircraftReserveInfoEntity.getAircraftReservationId())) {
      responseDto.setAircraftReservationId(
          aircraftReserveInfoEntity.getAircraftReservationId().toString());
    } else {
      throw new ServiceErrorException("機体予約IDの生成に失敗しました。");
    }
    return responseDto;
  }

  @Override
  /**
   * 機体予約情報更新
   *
   * @param request 予約するデータ
   * @param userInfo ユーザ情報
   * @return AircraftReserveInfoResponseDto
   * @throws NotFoundException 更新対象の機体予約情報が見つからない場合にスローされる例外
   * @throws ServiceErrorException 更新に失敗した場合にスローされる例外
   */
  public AircraftReserveInfoResponseDto putData(
      AircraftReserveInfoRequestDto request, UserInfoDto userInfo) {
    // 既存レコード検索
    Optional<AircraftReserveInfoEntity> optEntity =
        aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            UUID.fromString(request.getAircraftReservationId()));
    if (optEntity.isEmpty()) {
      throw new NotFoundException(
          MessageFormat.format("機体予約IDが見つかりません。機体予約ID:{0}", request.getAircraftReservationId()));
    }

    AircraftReserveInfoEntity entity = optEntity.get();

    // 航路運営者ではない場合、予約事業者IDチェック
    if (!isRouteOperator(userInfo)) {
      checkUpdatePermissionByAffiliatedOperatorId(
          entity.getReserveProviderId(), userInfo.getAffiliatedOperatorId());
    }

    // 重複する予約の確認
    checkReserveUpdate(request, entity);

    // 更新データ作成
    setEntity(entity, request);
    entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    entity.setOperatorId(userInfo.getUserOperatorId());

    // DB登録
    AircraftReserveInfoEntity aircraftReserveInfoEntity =
        aircraftReserveInfoRepository.save(entity);

    // 編集
    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    if (Objects.nonNull(aircraftReserveInfoEntity.getAircraftReservationId())) {
      responseDto.setAircraftReservationId(
          aircraftReserveInfoEntity.getAircraftReservationId().toString());
    } else {
      throw new ServiceErrorException("機体予約情報の更新に失敗しました。");
    }
    return responseDto;
  }

  @Override
  /**
   * 機体予約情報削除
   *
   * @param reservationId 機体予約IDまたは一括予約ID
   * @param aircraftReservationIdFlag 個別予約ID使用フラグ
   * @param userInfo ユーザ情報
   * @throws NotFoundException 削除対象の機体予約情報が見つからない場合にスローされる例外
   * @throws ServiceErrorException 削除に失敗した場合にスローされる例外
   */
  public void deleteData(
      String reservationId, Boolean aircraftReservationIdFlag, UserInfoDto userInfo) {
    AircraftReserveInfoEntity entity = null;

    if (Boolean.TRUE.equals(aircraftReservationIdFlag)) {
      // 個別予約ID使用フラグがtrueの場合、機体予約IDで検索
      Optional<AircraftReserveInfoEntity> optEntity =
          aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
              UUID.fromString(reservationId));
      if (optEntity.isEmpty()) {
        throw new NotFoundException(
            MessageFormat.format("機体予約情報が見つかりません。機体予約ID:{0}", reservationId));
      }
      entity = optEntity.get();
    } else {
      // 個別予約ID使用フラグがfalse(デフォルト)の場合、一括予約IDで検索
      Optional<AircraftReserveInfoEntity> optEntity =
          aircraftReserveInfoRepository.findByGroupReservationIdAndDeleteFlagFalse(
              UUID.fromString(reservationId));
      if (optEntity.isEmpty()) {
        throw new NotFoundException(
            MessageFormat.format("機体予約情報が見つかりません。一括予約ID:{0}", reservationId));
      }
      entity = optEntity.get();
    }

    // 航路運営者ではない場合、予約事業者IDチェック
    if (!isRouteOperator(userInfo)) {
      checkUpdatePermissionByAffiliatedOperatorId(
          entity.getReserveProviderId(), userInfo.getAffiliatedOperatorId());
    }

    entity.setDeleteFlag(true);
    entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    entity.setOperatorId(userInfo.getUserOperatorId());

    // DB登録
    aircraftReserveInfoRepository.save(entity);
  }

  @Transactional
  @Override
  /**
   * 機体予約情報一覧取得
   *
   * @param request 取得する機体予約情報
   * @return 検索結果
   */
  public AircraftReserveInfoListResponseDto getList(AircraftReserveInfoListRequestDto request) {

    // ソート機能設定、ページ制御作成
    Sort sort = createSort(request.getSortOrders(), request.getSortColumns(), logger);
    Pageable pageable = createPageRequest(request.getPerPage(), request.getPage(), sort);

    // データ取得
    Specification<AircraftReserveInfoEntity> spec = createSpecification(request);

    // ソート設定、ページ制御設定
    Page<AircraftReserveInfoEntity> pageResult = null;
    List<AircraftReserveInfoEntity> entityList = null;
    if (Objects.isNull(pageable)) {
      // ページ制御なし
      if (Objects.isNull(sort)) {
        entityList = aircraftReserveInfoRepository.findAll(spec);
      } else {
        entityList = aircraftReserveInfoRepository.findAll(spec, sort);
      }
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
    if (Objects.nonNull(pageResult)) {
      // ページ情報の結果を設定する
      responseDto.setPerPage(pageResult.getSize());
      responseDto.setCurrentPage(pageResult.getNumber() + 1);
      responseDto.setLastPage(pageResult.getTotalPages());
      responseDto.setTotal((int) pageResult.getTotalElements());
    }
    return responseDto;
  }

  @Override
  /**
   * 機体予約情報詳細取得
   *
   * @param aircraftRevservationId 機体予約ID
   * @return 検索結果
   * @throws NotFoundException 機体予約情報が見つからない場合にスローされる例外
   * @throws ServiceErrorException 機体予約情報の取得に失敗した場合にスローされる例外
   */
  public AircraftReserveInfoDetailResponseDto getDetail(String aircraftRevservationId) {
    // 既存レコード検索
    Optional<AircraftReserveInfoEntity> optEntity =
        aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            UUID.fromString(aircraftRevservationId));

    if (optEntity.isEmpty()) {
      throw new NotFoundException(
          MessageFormat.format("機体予約IDが見つかりません。機体予約ID:{0}", aircraftRevservationId));
    }

    // 機体予約情報登録更新Entity
    AircraftReserveInfoEntity entity = optEntity.get();
    AircraftReserveInfoDetailResponseDto detail = new AircraftReserveInfoDetailResponseDto();
    setAircraftReserveInfoDetailResponseDto(detail, entity);

    return detail;
  }

  /**
   * エンティティにリクエスト情報を設定
   *
   * @param aircraftReserveInfoEntity 機体予約情報エンティティ
   * @param request 機体予約情報リクエストDTO
   */
  private void setEntity(
      AircraftReserveInfoEntity aircraftReserveInfoEntity, AircraftReserveInfoRequestDto request) {
    // 機体予約ID
    if (request.getAircraftReservationId() != null
        && !request.getAircraftReservationId().isBlank()) {
      aircraftReserveInfoEntity.setAircraftReservationId(
          UUID.fromString(request.getAircraftReservationId()));
    }

    // 一括予約ID
    if (aircraftReserveInfoEntity.getGroupReservationId() == null
        && request.getGroupReservationId() != null
        && !request.getGroupReservationId().isBlank()) {
      aircraftReserveInfoEntity.setGroupReservationId(
          UUID.fromString(request.getGroupReservationId()));
    }

    // 機体ID
    if (request.getAircraftId() != null && !request.getAircraftId().isBlank()) {
      aircraftReserveInfoEntity.setAircraftId(UUID.fromString(request.getAircraftId()));
    }

    // 予約日時範囲
    if (Objects.nonNull(request.getReservationTimeFrom())
        && Objects.nonNull(request.getReservationTimeTo())) {
      aircraftReserveInfoEntity.setReservationTime(
          Range.localDateTimeRange(
              String.format(
                  "[%s,%s)",
                  StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeFrom())
                      .toString(),
                  StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeTo())
                      .toString())));
    }
  }

  /**
   * 機体予約情報詳細DTOにエンティティからのデータを設定
   *
   * @param detail 機体予約情報詳細DTO
   * @param entity 機体予約情報エンティティ
   */
  private void setAircraftReserveInfoDetailResponseDto(
      AircraftReserveInfoDetailResponseDto detail, AircraftReserveInfoEntity entity) {
    BeanUtils.copyProperties(entity, detail);
    if (Objects.nonNull(entity.getAircraftId())) {
      detail.setAircraftId(entity.getAircraftId().toString());
    } else {
      detail.setAircraftId(null);
    }
    if (Objects.nonNull(entity.getAircraftReservationId())) {
      detail.setAircraftReservationId(entity.getAircraftReservationId().toString());
    } else {
      throw new ServiceErrorException("機体予約情報の取得に失敗しました。");
    }
    if (Objects.nonNull(entity.getGroupReservationId())) {
      detail.setGroupReservationId(entity.getGroupReservationId().toString());
    } else {
      detail.setGroupReservationId(null);
    }
    detail.setReservationTimeFrom(
        StringUtils.toUtcDateTimeString(entity.getReservationTime().lower()));
    detail.setReservationTimeTo(
        StringUtils.toUtcDateTimeString(entity.getReservationTime().upper()));
    detail.setAircraftName(entity.getAircraftEntity().getAircraftName());
    if (Objects.nonNull(entity.getReserveProviderId())) {
      detail.setReserveProviderId(entity.getReserveProviderId().toString());
    } else {
      detail.setReserveProviderId(null);
    }
  }

  /**
   * 予約情報登録前確認
   *
   * @param dto 機体予約情報登録APIのリクエストDTO
   * @throws ServiceErrorException 予約に失敗した場合にスローされる例外
   */
  private void checkReserveRegest(AircraftReserveInfoRequestDto dto) {

    // 機体IDの存在確認
    if (dto.getAircraftId() != null && !dto.getAircraftId().isBlank()) {
      Optional<AircraftInfoEntity> optEntity =
          aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(
              UUID.fromString(dto.getAircraftId()));
      if (optEntity.isEmpty()) {
        throw new ServiceErrorException("機体IDが存在しません:機体ID:" + dto.getAircraftId());
      }
    } else {
      throw new ServiceErrorException("機体IDが入力されていません。");
    }

    // 機体予約情報検索条件作成
    AircraftReserveInfoSpecification<AircraftReserveInfoEntity> spec =
        new AircraftReserveInfoSpecification<>();
    // 機体予約情報検索
    List<AircraftReserveInfoEntity> entityList =
        aircraftReserveInfoRepository.findAll(
            Specification.where(spec.aircraftIdEqual(UUID.fromString(dto.getAircraftId())))
                .and(
                    spec.tsrangeOverlap(
                        StringUtils.parseDatetimeString(dto.getReservationTimeFrom()),
                        StringUtils.parseDatetimeString(dto.getReservationTimeTo())))
                .and(spec.deleteFlagEqual(false)));

    // 重複確認
    if (!entityList.isEmpty()) {
      throw new ServiceErrorException("他の予約と被っているため、予約できません");
    }
  }

  /**
   * 予約情報重複確認
   *
   * @param dto 機体予約情報更新APIのリクエストDTO
   * @param entity 更新前の機体予約情報エンティティ
   * @throws ServiceErrorException 予約に失敗した場合にスローされる例外
   */
  private void checkReserveUpdate(
      AircraftReserveInfoRequestDto dto, AircraftReserveInfoEntity entity) {
    String aircraftId = dto.getAircraftId();
    if (!org.springframework.util.StringUtils.hasText(aircraftId)) {
      // DTOに機体IDが含まれていなかったら変更前の機体IDを使用する
      aircraftId = entity.getAircraftId().toString();
    }

    // 機体IDの存在確認
    Optional<AircraftInfoEntity> optEntity =
        aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(UUID.fromString(aircraftId));
    if (optEntity.isEmpty()) {
      throw new ServiceErrorException("機体IDが存在しません:機体ID:" + aircraftId);
    }

    // 予約開始時間
    String timeFrom = dto.getReservationTimeFrom();
    if (!org.springframework.util.StringUtils.hasText(timeFrom)) {
      // 予約開始時間が入っていない場合は元の開始時間を使用する。
      timeFrom = entity.getReservationTime().lower().toString() + "Z";
    }

    // 予約終了時間
    String timeTo = dto.getReservationTimeTo();
    if (!org.springframework.util.StringUtils.hasText(timeTo)) {
      // 予約終了時間が入っていない場合は元の終了時間を使用する。
      timeTo = entity.getReservationTime().upper().toString() + "Z";
    }

    // 予約時間に機体予約が存在することをチェックする
    // 機体予約情報の検索条件作成クラスを生成
    AircraftReserveInfoSpecification<AircraftReserveInfoEntity> spec =
        new AircraftReserveInfoSpecification<>();
    // 機体予約情報を検索
    List<AircraftReserveInfoEntity> entityList =
        aircraftReserveInfoRepository.findAll(
            Specification.where(spec.deleteFlagEqual(false))
                .and(spec.aircraftIdEqual(UUID.fromString(aircraftId)))
                .and(
                    spec.tsrangeOverlap(
                        StringUtils.parseDatetimeString(timeFrom),
                        StringUtils.parseDatetimeString(timeTo))));
    boolean ckFlag = false;
    if (!entityList.isEmpty()) {
      // 重複する予約が見つかった場合
      if (entityList.size() == 1) {
        // 1件のみの場合（更新しようとしているデータの可能性）
        for (AircraftReserveInfoEntity tmpEntity : entityList) {
          if (tmpEntity
              .getAircraftReservationId()
              .toString()
              .equals(dto.getAircraftReservationId().toString())) {
            // 同じ機体予約IDの場合は時間変更であると判断し、更新を許可する。
            ckFlag = true;
            break;
          }
        }
      }
      if (!ckFlag) {
        // 重複する予約あり
        throw new ServiceErrorException("他の予約と被っているため、予約できません");
      }
    }
  }

  /**
   * 検索条件オブジェクトを生成する
   *
   * @param request 機体予約情報一覧取得リクエストDTO
   * @return 検索条件オブジェクト
   * @throws ServiceErrorException 検索条件の生成に失敗した場合にスローされる例外
   */
  private Specification<AircraftReserveInfoEntity> createSpecification(
      AircraftReserveInfoListRequestDto request) {
    AircraftReserveInfoSpecification<AircraftReserveInfoEntity> spec =
        new AircraftReserveInfoSpecification<>();

    // 一括予約ID
    UUID groupReservationId = null;
    String strGroupReservationId = request.getGroupReservationId();
    if (strGroupReservationId != null) {
      if (!strGroupReservationId.isBlank()) {
        groupReservationId = UUID.fromString(strGroupReservationId);
      }
    }

    // 機体ID
    UUID aircraftId = null;
    String strAircraftId = request.getAircraftId();
    if (strAircraftId != null) {
      if (!strAircraftId.isBlank()) {
        aircraftId = UUID.fromString(strAircraftId);
      }
    }

    // 機体名
    String aircraftName = request.getAircraftName();

    // 予約日時範囲(開始)
    Timestamp timeFrom = null;
    String strTimeFrom = request.getTimeFrom();
    if (strTimeFrom != null) {
      if (!strTimeFrom.isBlank()) {
        timeFrom = StringUtils.parseDatetimeString(strTimeFrom);
      }
    }

    // 予約日時範囲(終了)
    Timestamp timeTo = null;
    String strTimeTo = request.getTimeTo();
    if (strTimeTo != null) {
      if (!strTimeTo.isBlank()) {
        timeTo = StringUtils.parseDatetimeString(strTimeTo);
      }
    }

    // 予約事業者ID
    UUID reserveProviderId = null;
    String strReserveProviderId = request.getReserveProviderId();
    if (strReserveProviderId != null) {
      if (!strReserveProviderId.isBlank()) {
        reserveProviderId = UUID.fromString(strReserveProviderId);
      }
    }

    return Specification.where(spec.groupReservationIdEqual(groupReservationId))
        .and(spec.aircraftIdEqual(aircraftId))
        .and(spec.aircraftNameContains(aircraftName))
        .and(spec.tsrangeInclude2(timeFrom, timeTo))
        .and(spec.reserveProviderIdEqual(reserveProviderId))
        .and(spec.deleteFlagEqual(false));
  }

  /**
   * 航路運営者であるか判定する rolesのroleIdに航路運営者を含む場合にtrueを返す
   *
   * @param userInfo ユーザー情報
   * @return 航路運営者を含む場合true
   * @throws ServiceErrorException 判定に失敗した場合にスローされる例外
   */
  private boolean isRouteOperator(UserInfoDto userInfo) {
    String routeOperator =
        systemSettings.getString(CommonConstants.ROLE_ID, CommonConstants.ROUTE_OPERATOR);
    String routeOperatorManager =
        systemSettings.getString(CommonConstants.ROLE_ID, CommonConstants.ROUTE_OPERATOR_MANAGER);
    String routeOperatorAssignee =
        systemSettings.getString(CommonConstants.ROLE_ID, CommonConstants.ROUTE_OPERATOR_ASSIGNEE);

    // 航路運営者系のロールがあるかチェック
    boolean hasOperatorRole =
        userInfo.getRoles().stream()
            .anyMatch(
                role ->
                    Objects.equals(role.getRoleId(), routeOperator)
                        || Objects.equals(role.getRoleId(), routeOperatorManager)
                        || Objects.equals(role.getRoleId(), routeOperatorAssignee));

    // すべてが航路運営者ではない場合、運航事業者と判定
    return hasOperatorRole;
  }
}
