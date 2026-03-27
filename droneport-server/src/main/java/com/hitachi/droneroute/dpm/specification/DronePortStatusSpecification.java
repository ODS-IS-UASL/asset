package com.hitachi.droneroute.dpm.specification;

import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;
import java.sql.Timestamp;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

/** 離着陸場状態テーブルの検索条件設定クラス */
public class DronePortStatusSpecification {

  /**
   * 離着陸場IDで検索
   *
   * @param dronePortId 離着陸場ID
   * @return Specification
   */
  public Specification<DronePortStatusEntity> dronePortIdEqual(String dronePortId) {
    return Objects.isNull(dronePortId)
        ? null
        : (root, query, builder) -> {
          return builder.equal(root.get("dronePortId"), dronePortId);
        };
  }

  /**
   * 使用不可日時範囲で検索
   *
   * @param timeFrom 使用不可開始日時
   * @param timeTo 使用不可終了日時
   * @return Specification
   */
  public Specification<DronePortStatusEntity> tsrangeOverlap(Timestamp timeFrom, Timestamp timeTo) {
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
                  root.get("inactiveTime"),
                  builder.literal(condition)));
        };
  }

  /**
   * 削除フラグで検索
   *
   * @param flag 削除フラグの検索条件
   * @return Specification
   */
  public Specification<DronePortStatusEntity> deleteFlagEqual(boolean flag) {
    return (root, query, builder) -> {
      return builder.equal(root.get("deleteFlag"), flag);
    };
  }
}
