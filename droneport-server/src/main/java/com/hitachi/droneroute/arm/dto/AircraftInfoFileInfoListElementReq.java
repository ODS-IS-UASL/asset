package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 補足資料情報登録更新要求要素. */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AircraftInfoFileInfoListElementReq {

  /** 処理種別 */
  private Integer processingType;

  /** 補足資料ID */
  private String fileId;

  /** 補足資料名称(ファイル論理名) */
  private String fileLogicalName;

  /** ファイル物理名 */
  private String filePhysicalName;

  /** ファイル(Base64エンコード文字列) */
  @ToString.Exclude private String fileData;

  /** ファイル(バイト型) */
  @ToString.Exclude private byte[] fileBinary;
}
