package com.hitachi.droneroute.prm.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 料金情報一覧応答のDTO. */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PriceInfoSearchListResponseDto {

  /** 料金設定リソース */
  private List<PriceInfoSearchListElement> resources;
}
