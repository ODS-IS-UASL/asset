package com.hitachi.droneroute.dpm.repository;

import com.hitachi.droneroute.dpm.entity.DronePortReserveInfoEntity;
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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/** 離着陸場予約情報リポジトリクラス */
public interface DronePortReserveInfoRepository
    extends JpaRepository<DronePortReserveInfoEntity, UUID>,
        JpaSpecificationExecutor<DronePortReserveInfoEntity> {

  /**
   * 離着陸場予約情報を検索する(削除済みレコードは除く)
   *
   * @param dronePortId 離着陸場予約ID
   * @return 離着陸場予約情報
   */
  Optional<DronePortReserveInfoEntity> findByDronePortReservationIdAndDeleteFlagFalse(
      UUID dronePortId);

  /**
   * 離着陸場予約情報を一覧検索する
   *
   * @param spec 検索条件
   * @return 離着陸場予約情報リスト
   */
  @EntityGraph(value = "droneport_reserve_info_join", type = EntityGraph.EntityGraphType.FETCH)
  @NonNull
  List<DronePortReserveInfoEntity> findAll(
      @Nullable Specification<DronePortReserveInfoEntity> spec);

  /**
   * 離着陸場予約情報を一覧検索する
   *
   * @param spec 検索条件
   * @param sort ソート条件
   * @return 離着陸場予約情報リスト
   */
  @EntityGraph(value = "droneport_reserve_info_join", type = EntityGraph.EntityGraphType.FETCH)
  @NonNull
  List<DronePortReserveInfoEntity> findAll(
      @Nullable Specification<DronePortReserveInfoEntity> spec, @Nullable Sort sort);

  /**
   * 離着陸場予約情報を一覧検索する
   *
   * @param spec 検索条件
   * @param pageable ページ条件
   * @return ページ情報(検索結果含む)
   */
  @EntityGraph(value = "droneport_reserve_info_join", type = EntityGraph.EntityGraphType.FETCH)
  @NonNull
  Page<DronePortReserveInfoEntity> findAll(
      @Nullable Specification<DronePortReserveInfoEntity> spec, @Nullable Pageable pageable);
}
