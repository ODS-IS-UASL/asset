package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** ペイロード情報一覧応答要素. */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AircraftInfoPayloadInfoSearchListElement {

  /** ペイロードID */
  private String payloadId;

  /** ペイロード名 */
  private String payloadName;

  /** ペイロード詳細テキスト */
  private String payloadDetailText;

  /** ファイル物理名 */
  private String filePhysicalName;

  /** オペレータID */
  private String operatorId;
}
