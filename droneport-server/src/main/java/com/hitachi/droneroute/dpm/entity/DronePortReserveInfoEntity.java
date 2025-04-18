package com.hitachi.droneroute.dpm.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.cmn.entity.CommonEntity;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *  ドローンポート予約情報エンティティクラス
 * @author Hiroshi Toyoda
 *
 */
@Setter
@Getter
@Table(name = "droneport_reserve_info")
@Entity
@NamedEntityGraph(
		name = "droneport_reserve_info_join",
		includeAllAttributes = true
		)
public class DronePortReserveInfoEntity extends CommonEntity {
	
	/**
	 * ドローンポート予約ID
	 */
	@Id
	@Column(name = "droneport_reservation_id")
	private UUID dronePortReservationId;
	
	/**
	 * ドローンポートID
	 */
	@Column(name = "droneport_id")
	private String dronePortId;
	
	/**
	 * 使用機体ID
	 */
	@Column(name = "aircraft_id")
	private UUID aircraftId;
	
	/**
	 * 航路予約ID
	 */
	@Column(name = "route_reservation_id")
	private UUID routeReservationId;
	
	/**
	 * 利用形態
	 */
	@Column(name = "usage_type")
	private Integer usageType;
	
	/**
	 * 予約日時範囲
	 */
	@Type(PostgreSQLRangeType.class)
	@Column(name = "reservation_time", columnDefinition = "tsrange")
	private Range<LocalDateTime> reservationTime;
	
	/**
	 * 予約有効フラグ
	 */
	@Column(name = "reservation_active_flag")
	private Boolean reservationActiveFlag;
	
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

    /**
     * 対応する機体情報
     */
	@ManyToOne
	@JoinColumn(name = "aircraft_id", referencedColumnName = "aircraft_id", insertable=false, updatable=false)
	@Fetch(FetchMode.JOIN)
    private AircraftInfoEntity aircraftInfoEntity;

    /**
     * 対応するドローンポート情報
     */
	@ManyToOne
	@JoinColumn(name = "droneport_id", referencedColumnName = "droneport_id", insertable=false, updatable=false)
	@Fetch(FetchMode.JOIN)
    private DronePortInfoEntity dronePortInfoEntity;
	
    /**
     * 対応するドローンポート状態
     */
	@ManyToOne
	@JoinColumn(name = "droneport_id", referencedColumnName = "droneport_id", insertable=false, updatable=false)
	@Fetch(FetchMode.JOIN)
    private DronePortStatusEntity dronePortStatusEntity;
	
}
