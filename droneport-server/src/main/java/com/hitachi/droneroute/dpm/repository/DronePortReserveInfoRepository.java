package com.hitachi.droneroute.dpm.repository;

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

import com.hitachi.droneroute.dpm.entity.DronePortReserveInfoEntity;

/**
 *  ドローンポート予約情報リポジトリクラス
 * @author Hiroshi Toyoda
 *
 */
public interface DronePortReserveInfoRepository extends 
				JpaRepository<DronePortReserveInfoEntity, UUID>, JpaSpecificationExecutor<DronePortReserveInfoEntity> {

	/**
	 * ドローンポート予約情報を検索する
	 * @param dronePortId ドローンポート予約ID
	 * @return ドローンポート予約情報
	 */
	public Optional<DronePortReserveInfoEntity> findByDronePortReservationIdAndDeleteFlagFalse(UUID dronePortId);
	
	/**
	 * ドローンポート予約情報を一覧検索する
	 * @param spec 検索条件
	 * @return ドローンポート予約情報リスト
	 */
	@EntityGraph(value = "droneport_reserve_info_join", type = EntityGraph.EntityGraphType.FETCH)
	public List<DronePortReserveInfoEntity> findAll(@Nullable Specification<DronePortReserveInfoEntity> spec);
	
	/**
	 * ドローンポート予約情報を一覧検索する
	 * @param spec 検索条件
	 * @param sort ソート条件
	 * @return ドローンポート予約情報リスト
	 */
	@EntityGraph(value = "droneport_reserve_info_join", type = EntityGraph.EntityGraphType.FETCH)
	public List<DronePortReserveInfoEntity> findAll(
			@Nullable Specification<DronePortReserveInfoEntity> spec,
			@Nullable Sort sort);
	
	/**
	 * ドローンポート予約情報を一覧検索する
	 * @param pageable ページ条件
	 * @return ページ情報(検索結果含む)
	 */
	@EntityGraph(value = "droneport_reserve_info_join", type = EntityGraph.EntityGraphType.FETCH)
	public Page<DronePortReserveInfoEntity> findAll(
			@Nullable Specification<DronePortReserveInfoEntity> spec,
			@Nullable Pageable pageable);

}
