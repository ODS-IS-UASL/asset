package com.hitachi.droneroute.arm.specification;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * 機体予約情報テーブルの検索条件設定クラス
 *
 * @param <T> AircraftReserveInfoEntityを指定する
 */
public class AircraftReserveInfoSpecification<T> {
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
   * 機体IDで検索
   *
   * @param aircraftId 機体ID
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
   * 予約日時範囲で検索(引数の検索条件と予約時間が重なっていればヒットする)
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
   * 予約日時範囲に含まれる(引数の予約時間が、登録済みの予約時間に完全に含まれていればOK)
   *
   * @param timeFrom 予約開始日時
   * @param timeTo 予約終了日時
   * @return Specification
   */
  public Specification<T> tsrangeInclude(Timestamp timeFrom, Timestamp timeTo) {
    return !(Objects.nonNull(timeFrom) && Objects.nonNull(timeTo))
        ? null
        : (root, query, builder) -> {
          String condition = "[";
          condition += timeFrom.toString();
          condition += ",";
          condition += timeTo.toString();
          condition += ")";
          return builder.isTrue(
              builder.function(
                  "fn_tsrange_include",
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
   * 機体名で検索
   *
   * @param aircraftName 機体名
   * @return Specification
   */
  public Specification<T> aircraftNameContains(String aircraftName) {
    return !StringUtils.hasText(aircraftName)
        ? null
        : (root, query, builder) -> {
          return builder.like(
              root.get("aircraftEntity").get("aircraftName"), "%" + aircraftName + "%");
        };
  }
}
