package com.hitachi.droneroute.arm.entity;

import com.hitachi.droneroute.cmn.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** 機体情報Entity */
@Setter
@Getter
@Entity
@Table(name = "aircraft_info")
public class AircraftInfoEntity extends CommonEntity {
  /** 機体ID */
  @Id
  @Column(name = "aircraft_id", length = 12)
  private UUID aircraftId;

  /** 機体名 */
  @Column(name = "aircraft_name", length = 24)
  private String aircraftName;

  /** 製造メーカー */
  @Column(name = "manufacturer", length = 200)
  private String manufacturer;

  /** 型式番号 */
  @Column(name = "model_number", length = 200)
  private String modelNumber;

  /** 機種名 */
  @Column(name = "model_name", length = 200)
  private String modelName;

  /** 製造番号 */
  @Column(name = "manufacturing_number", length = 20)
  private String manufacturingNumber;

  /** 機体の種類 */
  @Column(name = "aircraft_type")
  private Integer aircraftType;

  /** 最大離陸重量 */
  @Column(name = "max_takeoff_weight")
  private Double maxTakeoffWeight;

  /** 重量 */
  @Column(name = "body_weight")
  private Double bodyWeight;

  /** 最大速度 */
  @Column(name = "max_flight_speed")
  private Double maxFlightSpeed;

  /** 最大飛行時間 */
  @Column(name = "max_flight_time")
  private Double maxFlightTime;

  /** 位置情報（緯度） */
  @Column(name = "lat")
  private Double lat;

  /** 位置情報（経度） */
  @Column(name = "lon")
  private Double lon;

  /** 機体認証の有無 */
  @Column(name = "certification")
  private Boolean certification;

  /** DIPS登録記号 */
  @Column(name = "dips_registration_code", length = 12)
  private String dipsRegistrationCode;

  /** 機体所有種別 */
  @Column(name = "owner_type")
  private Integer ownerType;

  /** 所有者ID */
  @Column(name = "owner_id", length = 12)
  private UUID ownerId;

  /** 画像 */
  @Column(name = "image_data", length = 2097152)
  private byte[] imageBinary;

  /** 画像フォーマット */
  @Column(name = "image_format")
  private String imageFormat;

  /** 公開可否フラグ */
  @Column(name = "public_flag")
  private Boolean publicFlag;
}
