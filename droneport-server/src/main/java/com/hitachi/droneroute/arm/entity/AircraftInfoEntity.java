package com.hitachi.droneroute.arm.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import com.hitachi.droneroute.cmn.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 機体情報Entity
 * 
 * @author ikkan.suzuki
 * @since 2024/08/27
 */
@Setter
@Getter
@Entity
@Table(name = "aircraft_info")
public class AircraftInfoEntity extends CommonEntity {
    /**
     * 機体ID
     */
    @Id
    @Column(name = "aircraft_id", length = 12)
    private UUID aircraftId;
    
    /**
     * 機体名
     */
    @Column(name = "aircraft_name", length = 24)
    private String aircraftName;
    
    /**
     * 製造メーカー
     */
    @Column(name = "manufacturer", length = 24)
    private String manufacturer;
    
    /**
     * 製造番号
     */
    @Column(name = "manufacturing_number", length = 20)
    private String manufacturingNumber;
    
    /**
     * 機体の種類
     */
    @Column(name = "aircraft_type")
    private Integer aircraftType;

    /**
     * 最大離陸重量
     */
    @Column(name = "max_takeoff_weight")
    private Double maxTakeoffWeight;

    /**
     * 重量
     */
    @Column(name = "body_weight")
    private Double bodyWeight;
    
    /**
     * 最大速度
     */
    @Column(name = "max_flight_speed")
    private Double maxFlightSpeed;
    
    /**
     * 最大飛行時間
     */
    @Column(name = "max_flight_time")
    private Double maxFlightTime;
    
    /**
     * 位置情報（緯度）
     */
    @Column(name = "lat")
    private Double lat;
    
    /**
     * 位置情報（経度）
     */
    @Column(name = "lon")
    private Double lon;
    
    /**
     * 機体認証の有無
     */
    @Column(name = "certification")
    private Boolean certification;
    
    /**
     * DIPS登録記号
     */
    @Column(name = "dips_registration_code", length = 12)
    private String dipsRegistrationCode;
    
    /**
     * 機体所有種別
     */
    @Column(name = "owner_type")
    private Integer ownerType;
    
    /**
     * 所有者ID
     */
    @Column(name = "owner_id", length = 12)
    private UUID ownerId;
    
    /**
     * 画像
     */
    @Column(name = "image_data", length = 2097152)
    private byte[] imageBinary;
    
    /**
     * 画像フォーマット
     */
    @Column(name = "image_format")
    private String imageFormat;
    
//    /**
//     * オペレータID
//     */
//    @Column(name ="operator_id")
//    private String operatorId;
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
    
}
