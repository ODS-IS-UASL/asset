package com.hitachi.droneroute.arm.entity;

import com.hitachi.droneroute.cmn.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** 補足資料情報Entity */
@Setter
@Getter
@Entity
@Table(name = "file_info")
public class FileInfoEntity extends CommonEntity {

  /** 補足資料ID */
  @Id
  @Column(name = "file_id")
  private UUID fileId;

  /** 機体ID */
  @Column(name = "aircraft_id")
  private UUID aircraftId;

  /** ファイル番号 */
  @Column(name = "file_number")
  private int fileNumber;

  /** 補足資料名称 */
  @Column(name = "file_logical_name")
  private String fileLogicalName;

  /** ファイル物理名 */
  @Column(name = "file_physical_name")
  private String filePhysicalName;

  /** ファイルデータ */
  @Column(name = "file_data")
  private byte[] fileData;

  /** ファイルフォーマット */
  @Column(name = "file_format")
  private String fileFormat;
}
