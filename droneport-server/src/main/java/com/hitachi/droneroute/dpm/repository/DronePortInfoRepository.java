package com.hitachi.droneroute.dpm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import com.hitachi.droneroute.dpm.entity.DronePortInfoEntity;

/**
 *  ドローンポート情報リポジトリ
 * @author Hiroshi Toyoda
 *
 */
public interface DronePortInfoRepository extends 
	JpaRepository<DronePortInfoEntity, String>, JpaSpecificationExecutor<DronePortInfoEntity> {

	/**
	 * ドローンポート情報を検索する(削除済みレコードは除く)
	 * @param dronePortId ドローンポートID
	 * @return ドローンポート情報
	 */
	public Optional<DronePortInfoEntity> findByDronePortIdAndDeleteFlagFalse(String dronePortId);
	
	/**
	 * ドローンポート情報を検索する(削除済みレコードを含む)
	 * @param dronePortId ドローンポートID
	 * @return ドローンポート情報
	 */
	public Optional<DronePortInfoEntity> findByDronePortId(String dronePortId);
	
	/**
	 * ドローンポート情報を一覧検索する
	 * @param spec 検索条件
	 * @return ドローンポート情報リスト
	 */
	@EntityGraph(value = "droneport_info_join", type = EntityGraph.EntityGraphType.FETCH)
	public List<DronePortInfoEntity> findAll(@Nullable Specification<DronePortInfoEntity> spec);
	
	/**
	 * ドローンポート情報を一覧検索する
	 * @param spec 検索条件
	 * @param sort ソート条件
	 * @return ドローンポート情報リスト
	 */
	@EntityGraph(value = "droneport_info_join", type = EntityGraph.EntityGraphType.FETCH)
	public List<DronePortInfoEntity> findAll(
			@Nullable Specification<DronePortInfoEntity> spec, 
			@Nullable Sort sort);
	
	/**
	 * ドローンポート情報を一覧検索する
	 * @param spec 検索条件
	 * @param pageable ページ条件
	 * @return ページ情報(検索結果含む)
	 */
	@EntityGraph(value = "droneport_info_join", type = EntityGraph.EntityGraphType.FETCH)
	public Page<DronePortInfoEntity> findAll(
			@Nullable Specification<DronePortInfoEntity> spec, 
			@Nullable Pageable pageable);
	
	/**
	 * ドローンポートIDの連番に設定する番号を取得する
	 * @return
	 */
	@Query(value = "SELECT NEXTVAL('droneport_id_sequence')", nativeQuery = true)
	public Long getNextSequenceValue();
	
}
