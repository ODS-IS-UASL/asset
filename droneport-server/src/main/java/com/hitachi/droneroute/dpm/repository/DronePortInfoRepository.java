package com.hitachi.droneroute.dpm.repository;

import com.hitachi.droneroute.dpm.entity.DronePortInfoEntity;
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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/** 離着陸場情報リポジトリ */
public interface DronePortInfoRepository
    extends JpaRepository<DronePortInfoEntity, String>,
        JpaSpecificationExecutor<DronePortInfoEntity> {

  /**
   * 離着陸場情報を検索する(削除済みレコードは除く)
   *
   * @param dronePortId 離着陸場ID
   * @return 離着陸場情報
   */
  Optional<DronePortInfoEntity> findByDronePortIdAndDeleteFlagFalse(String dronePortId);

  /**
   * 離着陸場情報を検索する(削除済みレコードを含む)
   *
   * @param dronePortId 離着陸場ID
   * @return 離着陸場情報
   */
  Optional<DronePortInfoEntity> findByDronePortId(String dronePortId);

  /**
   * 離着陸場情報を一覧検索する
   *
   * @param spec 検索条件
   * @return 離着陸場情報リスト
   */
  @EntityGraph(value = "droneport_info_join", type = EntityGraph.EntityGraphType.FETCH)
  @NonNull
  List<DronePortInfoEntity> findAll(@Nullable Specification<DronePortInfoEntity> spec);

  /**
   * 離着陸場情報を一覧検索する
   *
   * @param spec 検索条件
   * @param sort ソート条件
   * @return 離着陸場情報リスト
   */
  @EntityGraph(value = "droneport_info_join", type = EntityGraph.EntityGraphType.FETCH)
  @NonNull
  List<DronePortInfoEntity> findAll(
      @Nullable Specification<DronePortInfoEntity> spec, @Nullable Sort sort);

  /**
   * 離着陸場情報を一覧検索する
   *
   * @param spec 検索条件
   * @param pageable ページ条件
   * @return ページ情報(検索結果含む)
   */
  @EntityGraph(value = "droneport_info_join", type = EntityGraph.EntityGraphType.FETCH)
  @NonNull
  Page<DronePortInfoEntity> findAll(
      @Nullable Specification<DronePortInfoEntity> spec, @Nullable Pageable pageable);

  /**
   * 離着陸場IDの連番に設定する番号を取得する
   *
   * @return 次に使用する番号
   */
  @Query(value = "SELECT NEXTVAL('droneport_id_sequence')", nativeQuery = true)
  Long getNextSequenceValue();
}
