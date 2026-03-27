package com.hitachi.droneroute.arm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** ペイロード情報詳細応答DTO. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AircraftInfoPayloadInfoListElementRes {

  /** ペイロードID */
  private String payloadId;

  /** ペイロード名 */
  private String payloadName;

  /** ペイロード詳細テキスト */
  private String payloadDetailText;

  /** 画像 */
  private String imageData;

  /** ファイル物理名 */
  private String filePhysicalName;
}
