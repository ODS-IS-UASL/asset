package com.hitachi.droneroute.dpm.repository;

import com.hitachi.droneroute.dpm.entity.VisTelemetryInfoEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** VISテレメトリ情報リポジトリクラス */
public interface VisTelemetryInfoRepository extends JpaRepository<VisTelemetryInfoEntity, String> {

  /**
   * VISテレメトリ情報を検索する
   *
   * @param dronePortId 離着陸場ID
   * @return VISテレメトリ情報
   */
  Optional<VisTelemetryInfoEntity> findByDroneportId(String dronePortId);
}
