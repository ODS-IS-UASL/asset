package com.hitachi.droneroute.dpm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;

/**
 *  ドローンポート状態リポジトリ
 * @author Hiroshi Toyoda
 *
 */
public interface DronePortStatusRepository extends JpaRepository<DronePortStatusEntity, String> {

	/**
	 * ドローンポート状態を検索する(削除済みレコードは除く)
	 * @param dronePortId ドローンポートID
	 * @return ドローンポート状態
	 */
	public Optional<DronePortStatusEntity> findByDronePortIdAndDeleteFlagFalse(String dronePortId);
	
	/**
	 * ドローンポート状態を一覧検索する
	 * @param spec 検索条件
	 * @return ドローンポート状態リスト
	 */
	public List<DronePortStatusEntity> findAll(Specification<DronePortStatusEntity> spec);
	
}
