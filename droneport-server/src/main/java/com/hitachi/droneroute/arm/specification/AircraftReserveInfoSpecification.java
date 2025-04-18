package com.hitachi.droneroute.arm.specification;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * 機体予約情報テーブルの検索条件設定クラス
 * @author Ikkan Suzuki
 *
 * @param <T> AircraftReserveInfoEntityを指定する
 */
public class AircraftReserveInfoSpecification<T> {

	/**
	 * 機体IDで検索
	 */
	// CT-016 検索方法修正
	public Specification<T> aircraftIdEqual(UUID aircraftId) {
		return Objects.isNull(aircraftId) ? null : (root, query, builder) -> {
			return builder.equal(root.get("aircraftId"), aircraftId);
		};
	}
	
	/**
	 * 予約日時範囲
	 * @param timeFrom
	 * @param timeTo
	 * @return
	 */
	public Specification<T> tsrangeOverlap(Timestamp timeFrom, Timestamp timeTo) {
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
					root.get("reservationTime"),
					builder.literal(condition)
					));
		};
	}

	/**
	 * 予約日時範囲に含まれる(引数の予約時間が、登録済みの予約時間に完全に含まれていればOK)
	 * @param timeFrom
	 * @param timeTo
	 * @return
	 */
	public Specification<T> tsrangeInclude(Timestamp timeFrom, Timestamp timeTo) {
		return !(Objects.nonNull(timeFrom) && Objects.nonNull(timeTo)) ? null : (root, query, builder) -> {
			String condition = "[";
			condition += timeFrom.toString();
			condition += ",";
			condition += timeTo.toString();
			condition += ")";
			return builder.isTrue(builder.function(
					"fn_tsrange_include", Boolean.class,
					root.get("reservationTime"),
					builder.literal(condition)
					));
		};
	}
	
	/**
	 * 予約日時範囲で検索(引数の検索条件が、予約時間を完全に含んでいる場合だけヒットする)
	 * @param timeFrom
	 * @param timeTo
	 * @return
	 */
	public Specification<T> tsrangeInclude2(Timestamp timeFrom, Timestamp timeTo) {
		return (Objects.isNull(timeFrom) && Objects.isNull(timeTo))  ? null : (root, query, builder) -> {
			String condition = "[";
			if (Objects.nonNull(timeFrom)) {
				condition += timeFrom.toString();
			}
			condition += ",";
			if (Objects.nonNull(timeTo)) {
				condition += timeTo.toString();
			}
			condition += "]";
			return builder.isTrue(builder.function(
					"fn_tsrange_include2", Boolean.class,
					builder.literal(condition),
					root.get("reservationTime")
					));
		};
	}
	
	/**
	 * 削除フラグがfalse
	 * @return
	 */
	public Specification<T> deleteFlagEqual(boolean flag) {
		return (root, query, builder) -> {
			return builder.equal(root.get("deleteFlag"), flag);
		};
	}
	
	// MVP1指摘対応 #17 機体名による検索追加
	/**
	 * 機体名で検索
	 */
	public Specification<T> aircraftNameContains(String aircraftName) {
		return !StringUtils.hasText(aircraftName) ? null : (root, query, builder) -> {
			return builder.like(root.get("aircraftEntity").get("aircraftName"), "%" + aircraftName + "%");
		};
	}
}
