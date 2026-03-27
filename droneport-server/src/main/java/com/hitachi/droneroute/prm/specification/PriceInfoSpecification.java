package com.hitachi.droneroute.prm.specification;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * 料金情報テーブルの検索条件設定クラス
 *
 * @param <T> PriceInfoEntityを指定する
 */
public class PriceInfoSpecification<T> {

  /**
   * 料金IDで検索
   *
   * @param priceId 料金ID
   * @return Specification
   */
  public Specification<T> priceIdEquals(UUID priceId) {
    return Objects.isNull(priceId)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("priceId"), priceId);
        };
  }

  /**
   * 料金IDを含まないで検索
   *
   * @param priceId 料金ID
   * @return Specification
   */
  public Specification<T> priceIdNotEquals(UUID priceId) {
    return Objects.isNull(priceId)
        ? null
        : (root, query, builder) -> {
          return builder.notEqual(root.get("priceId"), priceId);
        };
  }

  /**
   * リソースIDで検索（IN句）
   *
   * @param resourceIds リソースIDのリスト
   * @return Specification
   */
  public Specification<T> resourceIdIn(List<String> resourceIds) {
    return (resourceIds == null || resourceIds.isEmpty())
        ? null
        : (root, query, builder) -> {
          return root.get("resourceId").in(resourceIds);
        };
  }

  /**
   * リソース種別で検索
   *
   * @param resourceType リソース種別
   * @return Specification
   */
  public Specification<T> resourceTypeEquals(BigInteger resourceType) {
    return Objects.isNull(resourceType)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("resourceType"), resourceType);
        };
  }

  /**
   * 主管航路事業者IDで検索
   *
   * @param primaryRouteOperatorId 主管航路事業者ID
   * @return Specification
   */
  public Specification<T> primaryRouteOperatorIdEquals(String primaryRouteOperatorId) {
    return !StringUtils.hasText(primaryRouteOperatorId)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("primaryRouteOperatorId"), primaryRouteOperatorId);
        };
  }

  /**
   * 料金タイプで検索
   *
   * @param priceType 料金タイプ
   * @return Specification
   */
  public Specification<T> priceTypeEquals(BigInteger priceType) {
    return Objects.isNull(priceType)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("priceType"), priceType);
        };
  }

  /**
   * 料金単位の下限値検索条件を作成する 指定された料金単位以上のレコードを検索する
   *
   * @param pricePerUnitFrom 料金単位の下限値
   * @return Specification
   */
  public Specification<T> startPricePerUnitGreaterThanEqual(BigInteger pricePerUnitFrom) {
    return pricePerUnitFrom == null
        ? null
        : (root, query, builder) -> {
          return builder.greaterThanOrEqualTo(root.get("pricePerUnit"), pricePerUnitFrom);
        };
  }

  /**
   * 料金単位の上限値検索条件を作成する 指定された料金単位以下のレコードを検索する
   *
   * @param pricePerUnitTo 料金単位の上限値
   * @return Specification
   */
  public Specification<T> endPricePerUnitLessThanEqual(BigInteger pricePerUnitTo) {
    return pricePerUnitTo == null
        ? null
        : (root, query, builder) -> {
          return builder.lessThanOrEqualTo(root.get("pricePerUnit"), pricePerUnitTo);
        };
  }

  /**
   * 料金の下限値検索条件を作成する 指定された料金以上のレコードを検索する
   *
   * @param priceFrom 料金の下限値
   * @return Specification
   */
  public Specification<T> startPriceGreaterThanEqual(BigInteger priceFrom) {
    return priceFrom == null
        ? null
        : (root, query, builder) -> {
          return builder.greaterThanOrEqualTo(root.get("price"), priceFrom);
        };
  }

  /**
   * 料金の上限値検索条件を作成する 指定された料金以下のレコードを検索する
   *
   * @param priceTo 料金の上限値
   * @return Specification
   */
  public Specification<T> endPriceLessThanEqual(BigInteger priceTo) {
    return priceTo == null
        ? null
        : (root, query, builder) -> {
          return builder.lessThanOrEqualTo(root.get("price"), priceTo);
        };
  }

  /**
   * 適用日時範囲
   *
   * @param timeFrom 適用日時の開始
   * @param timeTo 適用日時の終了
   * @return Specification
   */
  public Specification<T> tsrangeOverlap(Timestamp timeFrom, Timestamp timeTo) {
    return (Objects.isNull(timeFrom) && Objects.isNull(timeTo))
        ? null
        : (root, query, builder) -> {
          String condition = "[";
          if (Objects.nonNull(timeFrom)) {
            condition += timeFrom.toString();
          }
          condition += ",";
          if (Objects.nonNull(timeTo)) {
            condition += timeTo.toString();
          }
          condition += ")";
          return builder.isTrue(
              builder.function(
                  "fn_tsrange_overlap",
                  Boolean.class,
                  root.get("effectiveTime"),
                  builder.literal(condition)));
        };
  }

  /**
   * 優先度で検索
   *
   * @param priority 優先度
   * @return Specification
   */
  public Specification<T> priorityEquals(Integer priority) {
    return Objects.isNull(priority)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("priority"), priority);
        };
  }

  /**
   * 削除フラグで検索
   *
   * @param flag 削除フラグ
   * @return Specification
   */
  public Specification<T> deleteFlagEqual(boolean flag) {
    return (root, query, builder) -> {
      return builder.equal(root.get("deleteFlag"), flag);
    };
  }
}
