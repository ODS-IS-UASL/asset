package com.hitachi.droneroute.arm.entity;

import com.hitachi.droneroute.cmn.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** ペイロード情報Entity */
@Setter
@Getter
@Entity
@Table(name = "payload_info")
public class PayloadInfoEntity extends CommonEntity {

  /** ペイロードID */
  @Id
  @Column(name = "payload_id")
  private UUID payloadId;

  /** 機体ID */
  @Column(name = "aircraft_id")
  private UUID aircraftId;

  /** ペイロード番号 */
  @Column(name = "payload_number")
  private int payloadNumber;

  /** ペイロード名 */
  @Column(name = "payload_name", length = 100)
  private String payloadName;

  /** ペイロード詳細テキスト */
  @Column(name = "payload_detail_text", length = 1000)
  private String payloadDetailText;

  /** 画像データ */
  @Column(name = "image_data", length = 2097152)
  private byte[] imageData;

  /** 画像フォーマット */
  @Column(name = "image_format")
  private String imageFormat;

  /** ファイル物理名 */
  @Column(name = "file_physical_name", length = 200)
  private String filePhysicalName;

  /** ファイルデータ */
  @Column(name = "file_data")
  private byte[] fileData;

  /** ファイルフォーマット */
  @Column(name = "file_format")
  private String fileFormat;
}
