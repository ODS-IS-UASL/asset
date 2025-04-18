package com.hitachi.droneroute.dpm.specification;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

/**
 * ドローンポート予約情報テーブルの検索条件設定クラス
 * @author Hiroshi Toyoda
 *
 * @param <T> DronePortReserveInfoEntityを指定する
 */
public class DronePortReserveInfoSpecification<T> {
	
	/**
	 * ドローンポート種別で検索
	 * @param dronePortType ドローンポート種別
	 * @return Specification
	 */
	public Specification<T> dronePortTypeContains(Integer[] dronePortTypes) {
		return dronePortTypes == null 
				|| dronePortTypes.length == 0 ? null : (root, query, builder) -> {
			return builder.or(
					Stream.of(dronePortTypes)
					.map(e -> builder.equal(root.get("dronePortInfoEntity").get("dronePortType"), e))
					.collect(Collectors.toList())
					.toArray(Predicate[]::new));	
		};
	}
	
	/**
	 * ドローンポート予約IDを含まない
	 */
	public Specification<T> dronePortReserveIdNotEqual(UUID dronePortReserveId) {
		return Objects.isNull(dronePortReserveId) ? null : (root, query, builder) -> {
			return builder.notEqual(root.get("dronePortReservationId"), dronePortReserveId);
		};
	}
	
	/**
	 * ドローンポートIDで検索
	 */
	public Specification<T> dronePortIdEqual(String dronePortId) {
		return Objects.isNull(dronePortId) ? null : (root, query, builder) -> {
			return builder.equal(root.get("dronePortId"), dronePortId);
		};
	}
	
	/**
	 * ドローンポート名で検索
	 * @param dronePortName ドローンポート名
	 * @return Specification
	 *  
	 */
	public Specification<T> dronePortNameContains(String dronePortName) {
		return !StringUtils.hasText(dronePortName) ? null : (root, query, builder) -> {
			return builder.like(root.get("dronePortInfoEntity").get("dronePortName"), "%" + dronePortName + "%");
		};
	}
	
	/** 
	 * 使用機体IDで検索
	 */
	public Specification<T> aircraftIdEqual(UUID aircraftId) {
		return Objects.isNull(aircraftId) ? null : (root, query, builder) -> {
			return builder.equal(root.get("aircraftId"), aircraftId);
		};
	}
	
	/**
	 * 航路予約ID
	 * @param routeReservationId
	 * @return
	 */
	public Specification<T> routeReservationIdEqual(UUID routeReservationId) {
		return Objects.isNull(routeReservationId) ? null : (root, query, builder) -> {
			return builder.equal(root.get("routeReservationId"), routeReservationId);
		};
	}
	
	/**
	 * 予約日時範囲
	 * @param timeFrom
	 * @param timeTo
	 * @return
	 */
//	public Specification<T> tsrangeOverlap(Timestamp timeFrom, Timestamp timeTo) {
//		return (timeFrom == null || timeTo == null) ? null : (root, query, builder) -> {
//			return builder.isTrue(builder.function(
//					"fn_tsrange_overlap", Boolean.class,
//					root.get("reservation_time"),
//					builder.literal("[" + timeFrom + "," + timeTo + ")")
//					));
//		};
//	}
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
	 * 予約有効フラグで検索
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
	 * @param flag 削除フラグの検索条件
	 * @return Specification
	 */
	public Specification<T> deleteFlagEqual(boolean flag) {
		return (root, query, builder) -> {
			return builder.equal(root.get("deleteFlag"), flag);
		};
	}

}
