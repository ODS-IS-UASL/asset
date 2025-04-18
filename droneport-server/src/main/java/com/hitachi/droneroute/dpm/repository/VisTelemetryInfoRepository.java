package com.hitachi.droneroute.dpm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hitachi.droneroute.dpm.entity.VisTelemetryInfoEntity;

/**
 * VISテレメトリ情報リポジトリクラス
 * @author Hiroshi Toyoda
 *
 */
public interface VisTelemetryInfoRepository extends JpaRepository<VisTelemetryInfoEntity, String> {
	
	/**
	 * VISテレメトリ情報を検索する
	 * @param dronePortId ドローンポートID
	 * @return VISテレメトリ情報
	 */
	public Optional<VisTelemetryInfoEntity> findByDroneportId(String dronePortId);

}
