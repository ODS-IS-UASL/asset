package com.hitachi.droneroute.arm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.hitachi.droneroute.arm.entity.AircraftReserveInfoEntity;

/**
 * 機体予約情報のリポジトリ
 */
@Repository
public interface AircraftReserveInfoRepository
		extends JpaRepository<AircraftReserveInfoEntity, UUID>, JpaSpecificationExecutor<AircraftReserveInfoEntity> {

	/**
	 * 機体予約IDによる機体予約情報検索(削除済みレコードを除く)
	 * @param aircraftReservationId 機体予約ID
	 * @return 機体予約情報
	 */
	Optional<AircraftReserveInfoEntity> findByAircraftReservationIdAndDeleteFlagFalse(UUID aircraftReservationId);

	/**
	 * 検索条件による機体予約情報一覧取得
	 * @param spec　検索条件
	 * @return　機体予約情報リスト
	 */
	@EntityGraph(value = "aircraft_reserve_info_join", type = EntityGraph.EntityGraphType.FETCH)
	public List<AircraftReserveInfoEntity> findAll(@Nullable Specification<AircraftReserveInfoEntity> spec);
	
	/**
	 * 検索条件による機体予約情報一覧取得
	 * @param spec　検索条件
	 * @param sort　ソート条件
	 * @return　機体予約情報リスト
	 */
	@EntityGraph(value = "aircraft_reserve_info_join", type = EntityGraph.EntityGraphType.FETCH)
	public List<AircraftReserveInfoEntity> findAll(
			@Nullable Specification<AircraftReserveInfoEntity> spec,
			@Nullable Sort sort);

	/**
	 * 検索条件による機体予約情報一覧取得
	 * @param spec　検索条件
	 * @param pageable ページ条件
	 * @return ページ情報(検索結果含む)
	 */
	@EntityGraph(value = "aircraft_reserve_info_join", type = EntityGraph.EntityGraphType.FETCH)
	public Page<AircraftReserveInfoEntity> findAll(
			@Nullable Specification<AircraftReserveInfoEntity> spec,
			@Nullable Pageable pageable);

}
