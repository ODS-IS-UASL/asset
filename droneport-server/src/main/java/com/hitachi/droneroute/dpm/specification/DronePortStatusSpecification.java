package com.hitachi.droneroute.dpm.specification;

import java.sql.Timestamp;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;

/**
 * ドローンポート状態テーブルの検索条件設定クラス
 * @author Hiroshi Toyoda
 *
 * @param <DronePortStatusEntity>
 */
public class DronePortStatusSpecification {

	/**
	 * ドローンポートIDで検索
	 */
	public Specification<DronePortStatusEntity> dronePortIdEqual(String dronePortId) {
		return Objects.isNull(dronePortId) ? null : (root, query, builder) -> {
			return builder.equal(root.get("dronePortId"), dronePortId);
		};
	}
	
	/**
	 * 使用不可日時範囲
	 * @param timeFrom
	 * @param timeTo
	 * @return
	 */
	public Specification<DronePortStatusEntity> tsrangeOverlap(Timestamp timeFrom, Timestamp timeTo) {
		return (Objects.isNull(timeFrom) && Objects.isNull(timeTo)) ? null : (root, query, builder) -> {
			String condition = "[";
			if (Objects.nonNull(timeFrom)) {
				condition += timeFrom.toString();
			}
			condition += ",";
			if (Objects.nonNull(timeTo)) {
				condition += timeTo.toString();
			}
			condition += ")";
			return builder.isTrue(builder.function(
					"fn_tsrange_overlap", Boolean.class,
					root.get("inactiveTime"),
					builder.literal(condition)
					));
		};
	}
	
	/**
	 * 削除フラグで検索
	 * @param flag 削除フラグの検索条件
	 * @return Specification
	 */
	public Specification<DronePortStatusEntity> deleteFlagEqual(boolean flag) {
		return (root, query, builder) -> {
			return builder.equal(root.get("deleteFlag"), flag);
		};
	}
}
