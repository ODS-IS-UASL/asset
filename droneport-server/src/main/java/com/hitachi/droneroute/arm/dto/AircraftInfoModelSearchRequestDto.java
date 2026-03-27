package com.hitachi.droneroute.arm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 機体情報モデル検索要求のDTO. */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AircraftInfoModelSearchRequestDto {
  /** モデル情報リスト */
  private List<AircraftInfoModelInfoListElementReq> modelInfos;

  /** ペイロード情報要否 */
  private String isRequiredPayloadInfo;

  /** 料金情報要否 */
  private String isRequiredPriceInfo;
}
