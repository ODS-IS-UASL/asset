package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** モデル情報検索要求要素. */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class AircraftInfoModelInfoListElementReq {
  /** 製造メーカー */
  private String manufacturer;

  /** 型式番号 */
  private String modelNumber;
}
