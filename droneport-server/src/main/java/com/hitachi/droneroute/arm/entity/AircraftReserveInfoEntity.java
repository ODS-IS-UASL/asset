package com.hitachi.droneroute.arm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.hitachi.droneroute.cmn.entity.CommonEntity;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;

/**
 * 機体予約情報Entity
 * 
 * @author ikkan.suzuki
 * @since 2024/08/29
 */
@Setter
@Getter
@Table(name = "aircraft_reserve_info")
@Entity
@NamedEntityGraph(
		name = "aircraft_reserve_info_join",
		includeAllAttributes = true
		)
public class AircraftReserveInfoEntity extends CommonEntity {
    /**
     * 機体予約ID
     */
    @Id
    @Column(name = "aircraft_reserve_id", length = 12)
    private UUID aircraftReservationId;
    
    /**
     * 機体ID
     */
    @Column(name = "aircraft_id", length = 12)
    private UUID aircraftId;
    
	/**
	 * 予約日時範囲
	 */
	@Type(PostgreSQLRangeType.class)
	@Column(name = "reservation_time", columnDefinition = "tsrange")
	private Range<LocalDateTime> reservationTime;
	
//	/**
//	 * オペレータID
//	 */
//	@Column(name ="operator_id")
//	private String operatorId;
//	
//    /**
//     * 更新者ID
//     */
//    @Column(name = "update_user_id")
//    private String updateUserId;
//    
//    /**
//     * 登録日時
//     */
//    @Column(name = "create_time")
//    private Timestamp createTime; 
//    
//    /**
//     * 更新日時
//     */
//    @Column(name = "update_time")
//    private Timestamp updateTime;
//    
//    /**
//     * 削除フラグ
//     */
//    @Column(name = "delete_flag")
//    private Boolean deleteFlag;

    // sprint2:機体名追加対応
    // MVP1指摘対応 機体名検索のためにJOIN変更
    /**
     * 対応する機体情報
     */
	@ManyToOne
    @JoinColumn(name = "aircraft_id", insertable = false, updatable = false)
	@Fetch(FetchMode.JOIN)
    private AircraftInfoEntity aircraftEntity;
}
