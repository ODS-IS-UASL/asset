package com.hitachi.droneroute.dpm.specification;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.hitachi.droneroute.dpm.constants.DronePortConstants;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

// 参考
// https://qiita.com/tamorieeeen/items/be3f8c46dfa725014008

/**
 * ドローンポート情報テーブルの検索条件設定クラス
 * @author Hiroshi Toyoda
 *
 * @param <T> DronePortInfoEntityを指定する
 */
public class DronePortInfoSpecification<T> {
	
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
					.map(e -> builder.equal(root.get("dronePortType"), e))
					.collect(Collectors.toList())
					.toArray(Predicate[]::new));	
			
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
			return builder.like(root.get("dronePortName"), "%" + dronePortName + "%");
		};
	}
	
	/**
	 * 設置場所住所で検索
	 * @param address 設置場所住所
	 * @return Specification
	 */
	public Specification<T> addressContains(String address) {
		return !StringUtils.hasText(address) ? null : (root, query, builder) -> {
			return builder.like(root.get("address"), "%" + address + "%");
		};
	}
	
	/**
	 * 製造メーカーで検索
	 * @param manufacturer 製造メーカー
	 * @return Specification
	 */
	public Specification<T> manufacturerContains(String manufacturer) {
		return !StringUtils.hasText(manufacturer) ? null : (root, query, builder) -> {
			return builder.like(root.get("manufacturer"), "%" + manufacturer + "%");
		};
	}
	
	/**
	 * 製造番号で検索
	 * @param serialNumber 製造番号
	 * @return Specification
	 */
	public Specification<T> serialNumberContains(String serialNumber) {
		return !StringUtils.hasText(serialNumber) ? null : (root, query, builder) -> {
			return builder.like(root.get("serialNumber"), "%" + serialNumber + "%");
		};
	}
	
	/**
	 * ポート形状
	 * @param portType ポート形状
	 * @return Specification
	 */
	public Specification<T> portTypeContains(Integer[] portTypes) {
		return portTypes == null 
				|| portTypes.length == 0 ? null : (root, query, builder) -> {
			return builder.or(
					Stream.of(portTypes)
					.map(e -> builder.equal(root.get("portType"), e))
					.collect(Collectors.toList())
					.toArray(Predicate[]::new));	
			
		};
	}
	
	/**
	 * 最小緯度(南側)
	 * @param minlat 最小緯度
	 * @return Specification
	 */
	public Specification<T> startLatGreaterThanEqual(Double minlat) {
		return minlat == null ? null : (root, query, builder) -> {
			return builder.greaterThanOrEqualTo(root.get("lat"), minlat);
		};
	}
	
	/**
	 * 最大緯度(北側)
	 * @param maxlat 最大緯度
	 * @return Specification
	 */
	public Specification<T> endLatLessThanEqual(Double maxlat) {
		return maxlat == null ? null : (root, query, builder) -> {
			return builder.lessThanOrEqualTo(root.get("lat"), maxlat);
		};
	}
	
	/**
	 * 最小経度(西側)
	 * @param minlon 最小経度
	 * @return Specification
	 */
	public Specification<T> startLonGreaterThanEqual(Double minlon) {
		return minlon == null ? null : (root, query, builder) -> {
			return builder.greaterThanOrEqualTo(root.get("lon"), minlon);
		};
	}
	
	/**
	 * 最大経度(東側)
	 * @param maxlon 最大経度
	 * @return Specification
	 */
	public Specification<T> endLonLessThanEqual(Double maxlon) {
		return maxlon == null ? null : (root, query, builder) -> {
			return builder.lessThanOrEqualTo(root.get("lon"), maxlon);
		};
	}

	/**
	 * 対応機体
	 * @param supportDroneType 対応機体
	 * @return
	 */
	public Specification<T> supportDroneTypeContains(String supportDroneType) {
		return !StringUtils.hasText(supportDroneType) ? null : (root, query, builder) -> {
			return builder.like(root.get("supportDroneType"), "%" + supportDroneType + "%");
		};
	}
	
	/**
	 * 動作状況(使用可)に一致する(複数値)
	 * @param activeStatus 動作状況
	 * @return Specification
	 */
	public Specification<T> activeStatusContains(Integer[] activeStatus) {
		return activeStatus == null 
				|| activeStatus.length == 0 ? null : (root, query, builder) -> {
			return builder.or(
					Stream.of(activeStatus)
					.map(e -> builder.equal(root.get("dronePortStatusEntity").get("activeStatus"), e))
					.collect(Collectors.toList())
					.toArray(Predicate[]::new));	
			
		};
	}
	
	/**
	 * 削除フラグがfalse
	 * @return
	 */
	public Specification<T> deleteFlagEqual(boolean flag) {
		return (root, query, builder) -> builder.equal(root.get("deleteFlag"), flag);
	}
	
	private Predicate activeStatusContains(Root<T> root, CriteriaBuilder builder, List<Integer> activeStatus) {
		return builder.or(
				activeStatus.stream()
				.map(e -> builder.equal(root.get("dronePortStatusEntity").get("activeStatus"), e))
				.collect(Collectors.toList())
				.toArray(Predicate[]::new));
	}
	
	/**
	 * 指定日時における動作状況(使用可)の検索条件1<br>
	 * 動作状況(使用可): 1:準備中、または2:使用可<br>
	 * 動作状況(使用不可): null<br>
	 * 使用不可日時範囲: null
	 * @param status　動作状況
	 * @param current 指定日時
	 * @return Specification　検索条件
	 */
	public Specification<T> activeStatusInner1(List<Integer> statuses) {
		return (root, query, builder) ->
		builder.and(
			activeStatusContains(root, builder, statuses), 
			builder.isNull(root.get("dronePortStatusEntity").get("inactiveStatus")), 
			builder.isNull(root.get("dronePortStatusEntity").get("inactiveTime"))
			);
	}
	
	/**
	 * 指定日時における動作状況(使用可)の検索条件2<br>
	 * 動作状況(使用可): 1:準備中、または2:使用可<br>
	 * 動作状況(使用不可): 3:使用不可<br>
	 * 使用不可日時範囲: lower > 検索実行日時
	 * @param status　動作状況
	 * @param current 指定日時
	 * @return Specification　検索条件
	 */
	public Specification<T> activeStatusInner2(List<Integer> statuses, Timestamp current) {
		return (root, query, builder) ->
		builder.and(
			activeStatusContains(root, builder, statuses),  
			builder.equal(root.get("dronePortStatusEntity").get("inactiveStatus"), DronePortConstants.ACTIVE_STATUS_UNAVAILABLE), 
			builder.greaterThan(
					builder.function(
							"tsrange_lower", 
							Timestamp.class, 
							root.get("dronePortStatusEntity").get("inactiveTime")), 
					current)
			);
	}
	
	/**
	 * 指定日時における動作状況(使用可)の検索条件3<br>
	 * 動作状況(使用可): 1:準備中、または2:使用可<br>
	 * 動作状況(使用不可): 4:メンテナンス中<br>
	 * 使用不可日時範囲: lower > 検索実行日時 or upper < 検索実行日時
	 * @param status　動作状況
	 * @param current 指定日時
	 * @return Specification　検索条件
	 */
	public Specification<T> activeStatusInner3(List<Integer> statuses, Timestamp current) {
		return (root, query, builder) ->
		builder.and(
			activeStatusContains(root, builder, statuses),  
			builder.equal(root.get("dronePortStatusEntity").get("inactiveStatus"), DronePortConstants.ACTIVE_STATUS_MAINTENANCE), 
			builder.or(
					builder.greaterThan(builder.function("tsrange_lower", Timestamp.class, root.get("dronePortStatusEntity").get("inactiveTime")), current),
					builder.lessThan(builder.function("tsrange_upper", Timestamp.class, root.get("dronePortStatusEntity").get("inactiveTime")), current)
				)
			);
	}
	
	/**
	 * 指定日時における動作状況(使用不可):"使用不可"の検索条件<br>
	 * 動作状況(使用不可): 3:使用不可<br>
	 * 使用不可日時範囲: lower <= 検索実行日時
	 * @param current 指定日時
	 * @return 検索条件
	 */
	public Specification<T> unavailableStatus(Timestamp current) {
		return (root, query, builder) ->
			builder.and(
					builder.equal(root.get("dronePortStatusEntity").get("inactiveStatus"), DronePortConstants.ACTIVE_STATUS_UNAVAILABLE),
					builder.lessThanOrEqualTo(builder.function("tsrange_lower", Timestamp.class, root.get("dronePortStatusEntity").get("inactiveTime")), current)
					);
	}
	
	/**
	 * 指定日時における動作状況(使用不可):"メンテナンス中"の検索条件<br>
	 * 動作状況(使用不可): 4:メンテナンス中<br>
	 * 使用不可日時範囲: lower <= 検索実行日時 かつ upper > 検索実行日時
	 * @param current 指定日時
	 * @return 検索条件
	 */
	public Specification<T> maintenanceStatus(Timestamp current) {
		return (root, query, builder) ->
		builder.and(
				builder.equal(root.get("dronePortStatusEntity").get("inactiveStatus"), DronePortConstants.ACTIVE_STATUS_MAINTENANCE),
				builder.lessThanOrEqualTo(builder.function("tsrange_lower", Timestamp.class, root.get("dronePortStatusEntity").get("inactiveTime")), current),
				builder.greaterThan(builder.function("tsrange_upper", Timestamp.class, root.get("dronePortStatusEntity").get("inactiveTime")), current)
				);
	}
	
}
