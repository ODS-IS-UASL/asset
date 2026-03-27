package com.hitachi.droneroute.dpm.entity;

import com.hitachi.droneroute.cmn.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/** 離着陸場情報エンティティクラス */
@Setter
@Getter
@Table(name = "droneport_info")
@Entity
@NamedEntityGraph(name = "droneport_info_join", includeAllAttributes = true)
public class DronePortInfoEntity extends CommonEntity {

  /** 離着陸場ID */
  @Id
  @Column(name = "droneport_id")
  private String dronePortId;

  /** 離着陸場名 */
  @Column(name = "droneport_name")
  private String dronePortName;

  /** 設置場所住所 */
  @Column(name = "address")
  private String address;

  /** 製造メーカー */
  @Column(name = "manufacturer")
  private String manufacturer;

  /** 製造番号 */
  @Column(name = "serial_number")
  private String serialNumber;

  /** ポート形状 */
  @Column(name = "port_type")
  private Integer portType;

  /** VIS離着陸場事業者ID */
  @Column(name = "vis_droneport_company_id")
  private String visDronePortCompanyId;

  /** 緯度(設置位置) */
  @Column(name = "lat")
  private Double lat;

  /** 経度(設置位置) */
  @Column(name = "lon")
  private Double lon;

  /** 着陸面対地高度 */
  @Column(name = "alt")
  private Double alt;

  /** 対応機体 */
  @Column(name = "support_drone_type")
  private String supportDroneType;

  /** 画像フォーマット */
  @Column(name = "image_format")
  private String imageFormat;

  /** 画像 */
  @Column(name = "image_data", length = 2097152)
  private byte[] imageBinary;

  /** 公開可否フラグ */
  @Column(name = "public_flag")
  private Boolean publicFlag;

  /** 対応する離着陸場情報 */
  @OneToOne
  @JoinColumn(
      name = "droneport_id",
      referencedColumnName = "droneport_id",
      insertable = false,
      updatable = false)
  @Fetch(FetchMode.JOIN)
  private DronePortStatusEntity dronePortStatusEntity;
}
