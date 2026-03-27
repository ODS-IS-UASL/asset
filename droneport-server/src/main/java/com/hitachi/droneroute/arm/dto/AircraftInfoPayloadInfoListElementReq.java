package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** ペイロード情報　一覧検索の応答要素. */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AircraftInfoPayloadInfoListElementReq {

  /** 処理種別 */
  private Integer processingType;

  /** ペイロードID */
  private String payloadId;

  /** ペイロード名 */
  private String payloadName;

  /** ペイロード詳細テキスト */
  private String payloadDetailText;

  /** 画像(Base64エンコード文字列) */
  @ToString.Exclude private String imageData;

  /** 画像(バイト型) */
  @ToString.Exclude private byte[] imageBinary;

  /** ファイル物理名 */
  private String filePhysicalName;

  /** ファイル(Base64エンコード文字列) */
  @ToString.Exclude private String fileData;

  /** ファイル(バイト型) */
  @ToString.Exclude private byte[] fileBinary;
}
