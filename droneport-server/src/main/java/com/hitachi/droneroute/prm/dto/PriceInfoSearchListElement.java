package com.hitachi.droneroute.prm.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 料金情報リソース応答要素. */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PriceInfoSearchListElement {
  /** リソースID */
  private String resourceId;

  /** リソース種別 */
  private Integer resourceType;

  /** 料金情報 */
  private List<PriceInfoSearchListDetailElement> priceInfos;
}
