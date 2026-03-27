package com.hitachi.droneroute.dpm.specification;

import jakarta.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * 離着陸場予約情報テーブルの検索条件設定クラス
 *
 * @param <T> DronePortReserveInfoEntityを指定する
 */
public class DronePortReserveInfoSpecification<T> {

  /**
   * 離着陸場種別で検索
   *
   * @param dronePortTypes 離着陸場種別
   * @return Specification
   */
  public Specification<T> dronePortTypeContains(Integer[] dronePortTypes) {
    return dronePortTypes == null || dronePortTypes.length == 0
        ? null
        : (root, query, builder) -> {
          return builder.or(
              Stream.of(dronePortTypes)
                  .map(e -> builder.equal(root.get("dronePortInfoEntity").get("dronePortType"), e))
                  .collect(Collectors.toList())
                  .toArray(Predicate[]::new));
        };
  }

  /**
   * 離着陸場予約IDを含まないで検索
   *
   * @param dronePortReserveId 離着陸場予約ID
   * @return Specification
   */
  public Specification<T> dronePortReserveIdNotEqual(UUID dronePortReserveId) {
    return Objects.isNull(dronePortReserveId)
        ? null
        : (root, query, builder) -> {
          return builder.notEqual(root.get("dronePortReservationId"), dronePortReserveId);
        };
  }

  /**
   * 一括予約IDで検索
   *
   * @param groupReservationId 一括予約ID
   * @return Specification
   */
  public Specification<T> groupReservationIdEqual(UUID groupReservationId) {
    return Objects.isNull(groupReservationId)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("groupReservationId"), groupReservationId);
        };
  }

  /**
   * 離着陸場IDで検索
   *
   * @param dronePortId 離着陸場ID
   * @return Specification
   */
  public Specification<T> dronePortIdEqual(String dronePortId) {
    return Objects.isNull(dronePortId)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("dronePortId"), dronePortId);
        };
  }

  /**
   * 離着陸場名で検索
   *
   * @param dronePortName 離着陸場名
   * @return Specification
   */
  public Specification<T> dronePortNameContains(String dronePortName) {
    return !StringUtils.hasText(dronePortName)
        ? null
        : (root, query, builder) -> {
          return builder.like(
              root.get("dronePortInfoEntity").get("dronePortName"), "%" + dronePortName + "%");
        };
  }

  /**
   * 使用機体IDで検索
   *
   * @param aircraftId 使用機体ID
   * @return Specification
   */
  public Specification<T> aircraftIdEqual(UUID aircraftId) {
    return Objects.isNull(aircraftId)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("aircraftId"), aircraftId);
        };
  }

  /**
   * 航路予約IDで検索
   *
   * @param routeReservationId 航路予約ID
   * @return Specification
   */
  public Specification<T> routeReservationIdEqual(UUID routeReservationId) {
    return Objects.isNull(routeReservationId)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("routeReservationId"), routeReservationId);
        };
  }

  /**
   * 予約日時範囲で検索
   *
   * @param timeFrom 予約開始日時
   * @param timeTo 予約終了日時
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
                  root.get("reservationTime"),
                  builder.literal(condition)));
        };
  }

  /**
   * 予約日時範囲で検索(引数の検索条件が、予約時間を完全に含んでいる場合だけヒットする)
   *
   * @param timeFrom 予約開始日時
   * @param timeTo 予約終了日時
   * @return Specification
   */
  public Specification<T> tsrangeInclude2(Timestamp timeFrom, Timestamp timeTo) {
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
          condition += "]";
          return builder.isTrue(
              builder.function(
                  "fn_tsrange_include2",
                  Boolean.class,
                  builder.literal(condition),
                  root.get("reservationTime")));
        };
  }

  /**
   * 予約事業者IDで検索
   *
   * @param reserveProviderId 予約事業者ID
   * @return Specification
   */
  public Specification<T> reserveProviderIdEqual(UUID reserveProviderId) {
    return Objects.isNull(reserveProviderId)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("reserveProviderId"), reserveProviderId);
        };
  }

  /**
   * 予約有効フラグで検索
   *
   * @param flag 予約有効フラグの検索条件
   * @return Specification
   */
  public Specification<T> reservationActiveFlag(boolean flag) {
    return (root, query, builder) -> {
      return builder.equal(root.get("reservationActiveFlag"), flag);
    };
  }

  /**
   * 削除フラグで検索
   *
   * @param flag 削除フラグの検索条件
   * @return Specification
   */
  public Specification<T> deleteFlagEqual(boolean flag) {
    return (root, query, builder) -> {
      return builder.equal(root.get("deleteFlag"), flag);
    };
  }
}
