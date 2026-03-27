package com.hitachi.droneroute.dpm.repository;

import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/** 離着陸場状態リポジトリ */
public interface DronePortStatusRepository extends JpaRepository<DronePortStatusEntity, String> {

  /**
   * 離着陸場状態を検索する(削除済みレコードは除く)
   *
   * @param dronePortId 離着陸場ID
   * @return 離着陸場状態
   */
  Optional<DronePortStatusEntity> findByDronePortIdAndDeleteFlagFalse(String dronePortId);

  /**
   * 離着陸場状態を一覧検索する
   *
   * @param spec 検索条件
   * @return 離着陸場状態リスト
   */
  List<DronePortStatusEntity> findAll(Specification<DronePortStatusEntity> spec);
}
