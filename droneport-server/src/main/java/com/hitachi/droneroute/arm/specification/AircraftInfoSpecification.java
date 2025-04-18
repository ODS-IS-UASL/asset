package com.hitachi.droneroute.arm.specification;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;

/**
 * 機体情報テーブルの検索条件設定クラス
 * @author Ikkan Suzuki
 *
 * @param <T> AircraftInfoEntityを指定する
 */
public class AircraftInfoSpecification<T> {

	/**
	 * 機体名で検索
	 */
	public Specification<T> aircraftNameContains(String aircraftName) {
		return !StringUtils.hasText(aircraftName) ? null : (root, query, builder) -> {
			return builder.like(root.get("aircraftName"), "%" + aircraftName + "%");
		};
	}
	
	/**
	 * 製造メーカーで検索
	 */
	public Specification<T> manufacturerContains(String manufacturer) {
		return !StringUtils.hasText(manufacturer) ? null : (root, query, builder) -> {
			return builder.like(root.get("manufacturer"), "%" + manufacturer + "%");	// IT-0002 スペル修正
		};
	}

	/**
	 * 製造番号で検索
	 */
	public Specification<T> manufacturingNumberContains(String manufacturingNumber) {
		return !StringUtils.hasText(manufacturingNumber) ? null : (root, query, builder) -> {
			return builder.like(root.get("manufacturingNumber"), "%" + manufacturingNumber + "%");	// IT-0002 スペル修正
		};
	}
	
	/**
	 * 機体の種類で検索
	 */
	// IT-0002 検索方法修正
	public Specification<T> aircraftTypeContains(Integer[] aircraftType) {
		return aircraftType == null 
				|| aircraftType.length == 0 ? null : (root, query, builder) -> {
			return builder.or(
					Stream.of(aircraftType)
					.map(e -> builder.equal(root.get("aircraftType"), e))
					.collect(Collectors.toList())
					.toArray(Predicate[]::new));
		};
	}
	
	/**
	 * 機体認証の有無で検索
	 */
	// IT-0002 検索方法修正
	public Specification<T> certiticationEqual(Boolean certification) {
		return certification == null ? null  : (root, query, builder) -> {
			return builder.equal(root.get("certification"), certification);
		};
	}

	/**
	 * DIPS登録記号で検索
	 */
	public Specification<T> dipsRegistrationCodeContains(String dipsRegistrationCode) {
		return !StringUtils.hasText(dipsRegistrationCode) ? null : (root, query, builder) -> {
			return builder.like(root.get("dipsRegistrationCode"), "%" + dipsRegistrationCode + "%");
		};
	}

	/**
	 * 機体所有種別
	 */
	// IT-0002 検索方法修正
	public Specification<T> ownerTypeContains(Integer[] ownerType) {
		return ownerType == null 
				|| ownerType.length == 0 ? null : (root, query, builder) -> {
			return builder.or(
					Stream.of(ownerType)
					.map(e -> builder.equal(root.get("ownerType"), e))
					.collect(Collectors.toList())
					.toArray(Predicate[]::new));
		};
	}

	/**
	 * 所有者IDで検索
	 */
	// IT-0002 検索方法修正
	public Specification<T> ownerIdEquals(UUID ownerId) {
		return Objects.isNull(ownerId) ? null : (root, query, builder) -> {
			return builder.equal(root.get("ownerId"), ownerId);
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
}
