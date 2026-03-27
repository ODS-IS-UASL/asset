package com.hitachi.droneroute.prm.service.impl;

import com.hitachi.droneroute.cmn.service.DroneRouteCommonService;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.prm.constants.PriceInfoConstants;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListDetailElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListResponseDto;
import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
import com.hitachi.droneroute.prm.service.PriceInfoSearchListService;
import com.hitachi.droneroute.prm.specification.PriceInfoSpecification;
import com.hitachi.droneroute.prm.validator.PriceInfoValidator;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 料金情報検索サービス実装クラス */
@Service
@RequiredArgsConstructor
public class PriceInfoSearchListServiceImpl
    implements PriceInfoSearchListService, DroneRouteCommonService {

  // 料金情報リポジトリ
  private final PriceInfoRepository PriceInfoRepository;

  // ロガー
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  // 料金情報APIパラメータチェック
  private final PriceInfoValidator validator;

  // システム設定
  private final SystemSettings systemSettings;

  /**
   * 料金情報検索共通処理
   *
   * <p>外部（Controller）および内部（Service）の両方から呼び出される。 バリデーション、デフォルト値設定を含む完全な検索処理を実行する。
   *
   * @param request 検索条件
   * @return 検索結果
   */
  @Transactional(readOnly = true)
  public PriceInfoSearchListResponseDto getPriceInfoList(PriceInfoSearchListRequestDto request) {

    logger.info("料金情報一覧:  ===== START =====");
    logger.debug(request.toString());

    // 入力チェック
    validator.validateForGetList(request);

    // ソート
    // クエリパラメータにソート順が設定されていない場合は、システム設定のデフォルト値を設定する
    if (!org.springframework.util.StringUtils.hasText(request.getSortOrders())) {
      request.setSortOrders(
          systemSettings.getString(
              PriceInfoConstants.SETTINGS_PRICE_INFOLIST_DEFAULT,
              DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    }

    // クエリパラメータにソート対象列名が設定されていない場合は、システム設定のデフォルト値を設定する
    if (!org.springframework.util.StringUtils.hasText(request.getSortColumns())) {
      request.setSortColumns(
          systemSettings.getString(
              PriceInfoConstants.SETTINGS_PRICE_INFOLIST_DEFAULT,
              DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    }

    PriceInfoSearchListResponseDto responseDto = getList(request);

    logger.info("料金情報一覧:  ===== END =====");

    return responseDto;
  }

  /**
   * 料金情報一覧取得（検索処理コア）
   *
   * <p>getPriceInfoList() から呼び出される内部メソッド。 バリデーション済み、デフォルト値設定済みの条件で検索を実行する。
   *
   * @param request 検索条件
   * @return 検索結果
   */
  @Transactional(readOnly = true)
  public PriceInfoSearchListResponseDto getList(PriceInfoSearchListRequestDto request) {

    // ソート機能追加、ページ制御
    Sort sort = createSort(request.getSortOrders(), request.getSortColumns(), logger);

    // データ取得
    Specification<PriceInfoEntity> spec = createSpecification(request);

    List<PriceInfoEntity> entityList = null;
    // ソート
    if (Objects.isNull(sort)) {
      // ソートなし
      entityList = PriceInfoRepository.findAll(spec);
    } else {
      // ソートあり
      entityList = PriceInfoRepository.findAll(spec, sort);
    }

    // レスポンスDTOを作成
    PriceInfoSearchListResponseDto responseDto = convertToGroupedResponse(entityList);

    return responseDto;
  }

  /**
   * 料金情報エンティティリストをレスポンスDTOに変換 リソースIDでグルーピングして返却する
   *
   * @param entityList 料金情報エンティティリスト
   * @return レスポンスDTO
   */
  private PriceInfoSearchListResponseDto convertToGroupedResponse(
      List<PriceInfoEntity> entityList) {

    // リソースIDでグルーピング（エンティティリストの順序を維持）
    Map<String, List<PriceInfoEntity>> groupedByResource =
        entityList.stream()
            .collect(
                Collectors.groupingBy(
                    PriceInfoEntity::getResourceId,
                    java.util.LinkedHashMap::new,
                    Collectors.toList()));

    // レスポンス要素リストを作成
    List<PriceInfoSearchListElement> resources = new ArrayList<>();

    for (Map.Entry<String, List<PriceInfoEntity>> entry : groupedByResource.entrySet()) {
      String resourceId = entry.getKey();
      List<PriceInfoEntity> priceEntities = entry.getValue();

      // リソース要素を作成
      PriceInfoSearchListElement resourceElement = new PriceInfoSearchListElement();
      resourceElement.setResourceId(resourceId);

      // 最初のエンティティからリソース種別を取得（同じリソースIDなら同じ種別）
      resourceElement.setResourceType(priceEntities.get(0).getResourceType());

      // 料金リストを作成
      List<PriceInfoSearchListDetailElement> priceList = new ArrayList<>();
      for (PriceInfoEntity entity : priceEntities) {
        PriceInfoSearchListDetailElement priceDetail = new PriceInfoSearchListDetailElement();
        BeanUtils.copyProperties(entity, priceDetail);
        priceDetail.setPriceId(entity.getPriceId().toString());
        priceDetail.setEffectiveStartTime(
            StringUtils.toUtcDateTimeString(entity.getEffectiveTime().lower()));
        priceDetail.setEffectiveEndTime(
            StringUtils.toUtcDateTimeString(entity.getEffectiveTime().upper()));

        priceList.add(priceDetail);
      }

      resourceElement.setPriceInfos(priceList);
      resources.add(resourceElement);
    }

    // レスポンスDTOを作成
    PriceInfoSearchListResponseDto responseDto = new PriceInfoSearchListResponseDto();
    responseDto.setResources(resources);

    return responseDto;
  }

  /**
   * 検索条件オブジェクトを生成する
   *
   * @param request 料金情報一覧取得リクエストDTO
   * @return 検索条件オブジェクト
   */
  private Specification<PriceInfoEntity> createSpecification(
      PriceInfoSearchListRequestDto request) {
    PriceInfoSpecification<PriceInfoEntity> spec = new PriceInfoSpecification<>();
    UUID priceId = null;

    // カンマ区切りのリソースIDを配列に変換
    List<String> resourceIds = null;
    if (org.springframework.util.StringUtils.hasText(request.getResourceId())) {
      resourceIds =
          Arrays.stream(request.getResourceId().split(","))
              .map(String::trim) // 前後の空白を除去
              .filter(s -> !s.isEmpty()) // 空文字を除外
              .collect(Collectors.toList());
    }

    if (request.getPriceId() != null) {
      if (!request.getPriceId().isBlank()) {
        priceId = UUID.fromString(request.getPriceId());
      }
    }

    Timestamp timeFrom = null;
    String strTimeFrom = request.getEffectiveStartTime();
    if (strTimeFrom != null) {
      if (!strTimeFrom.isBlank()) {
        timeFrom = StringUtils.parseDatetimeString(strTimeFrom);
      }
    }

    Timestamp timeTo = null;
    String strTimeTo = request.getEffectiveEndTime();
    if (strTimeTo != null) {
      if (!strTimeTo.isBlank()) {
        timeTo = StringUtils.parseDatetimeString(strTimeTo);
      }
    }

    return Specification.where(spec.priceIdEquals(priceId))
        .and(spec.resourceIdIn(resourceIds))
        .and(spec.resourceTypeEquals(request.getResourceType()))
        .and(spec.primaryRouteOperatorIdEquals(request.getPrimaryRouteOperatorId()))
        .and(spec.priceTypeEquals(request.getPriceType()))
        .and(spec.startPricePerUnitGreaterThanEqual(request.getPricePerUnitFrom()))
        .and(spec.endPricePerUnitLessThanEqual(request.getPricePerUnitTo()))
        .and(spec.startPriceGreaterThanEqual(request.getPriceFrom()))
        .and(spec.endPriceLessThanEqual(request.getPriceTo()))
        .and(spec.tsrangeOverlap(timeFrom, timeTo))
        .and(spec.deleteFlagEqual(false));
  }
}
