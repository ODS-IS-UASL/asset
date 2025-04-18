package com.hitachi.droneroute.dpm.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Type;

import com.hitachi.droneroute.cmn.entity.CommonEntity;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *  ドローンポート状態エンティティクラス
 * @author Hiroshi Toyoda
 *
 */
@Setter
@Getter
@Table(name = "droneport_status")
@Entity
public class DronePortStatusEntity extends CommonEntity {
	/**
	 * ドローンポートID
	 */
	@Id
	@Column(name = "droneport_id")
	private String dronePortId;
	
	/**
	 * 動作状況(使用可)
	 */
	@Column(name = "active_status")
	private Integer activeStatus;

	/**
	 * 動作状況(使用不可)
	 */
	@Column(name = "inactive_status")
	private Integer inactiveStatus;

	/**
	 * 使用不可日時範囲
	 */
	@Type(PostgreSQLRangeType.class)
	@Column(name = "inactive_time", columnDefinition = "tsrange")
	private Range<LocalDateTime> inactiveTime;
	
	/**
	 * 格納中機体ID
	 */
	@Column(name = "stored_aircraft_id")
	private UUID storedAircraftId;
	
//	/**
//	 * オペレータID
//	 */
//	@Column(name ="operator_id")
//	private String operatorId;
//	
//	/**
//	 * 更新者ID
//	 */
//	@Column(name = "update_user_id")
//	private String updateUserId;
//	
//	/**
//	 * 登録日時
//	 */
//	@Column(name = "create_time")
//	private Timestamp createTime;
//	
//	/**
//	 * 更新日時
//	 */
//	@Column(name = "update_time")
//	private Timestamp updateTime;
//	
//	/**
//	 * 削除フラグ
//	 */
//	@Column(name = "delete_flag")
//	private Boolean deleteFlag;
//
}
