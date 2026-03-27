package com.hitachi.droneroute.arm.specification;

import com.hitachi.droneroute.arm.dto.AircraftInfoModelInfoListElementReq;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * 機体情報テーブルの検索条件設定クラス
 *
 * @param <T> AircraftInfoEntityを指定する
 */
public class AircraftInfoSpecification<T> {

  /**
   * 機体名で検索
   *
   * @param aircraftName 機体名
   * @return Specification
   */
  public Specification<T> aircraftNameContains(String aircraftName) {
    return !StringUtils.hasText(aircraftName)
        ? null
        : (root, query, builder) -> {
          return builder.like(root.get("aircraftName"), "%" + aircraftName + "%");
        };
  }

  /**
   * 製造メーカーで検索
   *
   * @param manufacturer 製造メーカー
   * @return Specification
   */
  public Specification<T> manufacturerContains(String manufacturer) {
    return !StringUtils.hasText(manufacturer)
        ? null
        : (root, query, builder) -> {
          return builder.like(root.get("manufacturer"), "%" + manufacturer + "%"); // IT-0002 スペル修正
        };
  }

  /**
   * 型式番号で検索
   *
   * @param modelNumber 型式番号
   * @return Specification
   */
  public Specification<T> modelNumberContains(String modelNumber) {
    return !StringUtils.hasText(modelNumber)
        ? null
        : (root, query, builder) -> {
          return builder.like(root.get("modelNumber"), "%" + modelNumber + "%");
        };
  }

  /**
   * モデル情報リスト(製造メーカー + 型式番号のペア)でOR検索
   *
   * @param modelInfos モデル情報リスト
   * @return Specification
   */
  public Specification<T> modelInfosMatch(List<AircraftInfoModelInfoListElementReq> modelInfos) {
    return modelInfos == null || modelInfos.isEmpty()
        ? null
        : (root, query, builder) -> {
          List<Predicate> predicates = new ArrayList<>();
          for (AircraftInfoModelInfoListElementReq element : modelInfos) {
            if (element != null) {
              Predicate manufacturerPredicate =
                  builder.equal(root.get("manufacturer"), element.getManufacturer());
              Predicate modelNumberPredicate =
                  builder.equal(root.get("modelNumber"), element.getModelNumber());
              predicates.add(builder.and(manufacturerPredicate, modelNumberPredicate));
            }
          }
          // 全ペアをORで結合
          return predicates.isEmpty()
              ? builder.disjunction()
              : builder.or(predicates.toArray(new Predicate[0]));
        };
  }

  /**
   * 機種名で検索
   *
   * @param modelName 機種名
   * @return Specification
   */
  public Specification<T> modelNameContains(String modelName) {
    return !StringUtils.hasText(modelName)
        ? null
        : (root, query, builder) -> {
          return builder.like(root.get("modelName"), "%" + modelName + "%");
        };
  }

  /**
   * 製造番号で検索
   *
   * @param manufacturingNumber 製造番号
   * @return Specification
   */
  public Specification<T> manufacturingNumberContains(String manufacturingNumber) {
    return !StringUtils.hasText(manufacturingNumber)
        ? null
        : (root, query, builder) -> {
          return builder.like(
              root.get("manufacturingNumber"), "%" + manufacturingNumber + "%"); // IT-0002 スペル修正
        };
  }

  /**
   * 機体の種類で検索
   *
   * @param aircraftType 機体の種類
   * @return Specification
   */
  public Specification<T> aircraftTypeContains(Integer[] aircraftType) {
    return aircraftType == null || aircraftType.length == 0
        ? null
        : (root, query, builder) -> {
          return builder.or(
              Stream.of(aircraftType)
                  .map(e -> builder.equal(root.get("aircraftType"), e))
                  .collect(Collectors.toList())
                  .toArray(Predicate[]::new));
        };
  }

  /**
   * 機体認証の有無で検索
   *
   * @param certification 機体認証の有無
   * @return Specification
   */
  public Specification<T> certiticationEqual(Boolean certification) {
    return certification == null
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("certification"), certification);
        };
  }

  /**
   * DIPS登録記号で検索
   *
   * @param dipsRegistrationCode DIPS登録記号
   * @return Specification
   */
  public Specification<T> dipsRegistrationCodeContains(String dipsRegistrationCode) {
    return !StringUtils.hasText(dipsRegistrationCode)
        ? null
        : (root, query, builder) -> {
          return builder.like(root.get("dipsRegistrationCode"), "%" + dipsRegistrationCode + "%");
        };
  }

  /**
   * 機体所有種別 で検索
   *
   * @param ownerType 機体所有種別
   * @return Specification
   */
  public Specification<T> ownerTypeContains(Integer[] ownerType) {
    return ownerType == null || ownerType.length == 0
        ? null
        : (root, query, builder) -> {
          return builder.or(
              Stream.of(ownerType)
                  .map(e -> builder.equal(root.get("ownerType"), e))
                  .collect(Collectors.toList())
                  .toArray(Predicate[]::new));
        };
  }

  /**
   * 所有者IDで検索
   *
   * @param ownerId 所有者ID
   * @return Specification
   */
  public Specification<T> ownerIdEquals(UUID ownerId) {
    return Objects.isNull(ownerId)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("ownerId"), ownerId);
        };
  }

  /**
   * 公開可否フラグで検索
   *
   * @param publicFlag 公開可否フラグ
   * @return Specification
   */
  public Specification<T> publicFlagEqual(Boolean publicFlag) {
    return publicFlag == null
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("publicFlag"), publicFlag);
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

  /**
   * 最小緯度(南側)で検索
   *
   * @param minlat 最小緯度
   * @return Specification
   */
  public Specification<T> startLatGreaterThanEqual(Double minlat) {
    return minlat == null
        ? null
        : (root, query, builder) -> {
          return builder.greaterThanOrEqualTo(root.get("lat"), minlat);
        };
  }

  /**
   * 最大緯度(北側)で検索
   *
   * @param maxlat 最大緯度
   * @return Specification
   */
  public Specification<T> endLatLessThanEqual(Double maxlat) {
    return maxlat == null
        ? null
        : (root, query, builder) -> {
          return builder.lessThanOrEqualTo(root.get("lat"), maxlat);
        };
  }

  /**
   * 最小経度(西側)で検索
   *
   * @param minlon 最小経度
   * @return Specification
   */
  public Specification<T> startLonGreaterThanEqual(Double minlon) {
    return minlon == null
        ? null
        : (root, query, builder) -> {
          return builder.greaterThanOrEqualTo(root.get("lon"), minlon);
        };
  }

  /**
   * 最大経度(東側)で検索
   *
   * @param maxlon 最大経度
   * @return Specification
   */
  public Specification<T> endLonLessThanEqual(Double maxlon) {
    return maxlon == null
        ? null
        : (root, query, builder) -> {
          return builder.lessThanOrEqualTo(root.get("lon"), maxlon);
        };
  }
}
