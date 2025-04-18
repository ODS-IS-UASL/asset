package com.hitachi.droneroute.arm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;

/**
 * 機体情報のリポジトリ
 */
@Repository
public interface AircraftInfoRepository extends
	JpaRepository<AircraftInfoEntity, UUID>, JpaSpecificationExecutor<AircraftInfoEntity>{
	
	/**
	 * 機体IDによる機体情報の検索(削除済みレコードを除く)
	 * @param aircraftId
	 * @return 機体情報
	 */
	Optional<AircraftInfoEntity> findByAircraftIdAndDeleteFlagFalse(UUID aircraftId);

	/**
	 * 検索条件による機体情報一覧取得
	 * @param spec　検索条件
	 * @return　機体情報リスト
	 */
	public List<AircraftInfoEntity> findAll(@Nullable Specification<AircraftInfoEntity> spec);
	
	/**
	 * 検索条件による機体情報一覧取得
	 * @param spec　検索条件
	 * @param sort　ソート条件
	 * @return　機体情報リスト
	 */
	public List<AircraftInfoEntity> findAll(
			@Nullable Specification<AircraftInfoEntity> spec,
			@Nullable Sort sort);

	/**
	 * 検索条件による機体情報一覧取得
	 * @param spec　検索条件
	 * @param pageable ページ条件
	 * @return ページ情報(検索結果含む)
	 */
	public Page<AircraftInfoEntity> findAll(
			@Nullable Specification<AircraftInfoEntity> spec,
			@Nullable Pageable pageable);

}
