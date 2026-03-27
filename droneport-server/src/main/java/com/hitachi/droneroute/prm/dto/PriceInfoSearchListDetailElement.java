package com.hitachi.droneroute.prm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 料金情報一覧応答要素. */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PriceInfoSearchListDetailElement {
  /** 料金ID */
  private String priceId;

  /** 主管航路事業者ID */
  private String primaryRouteOperatorId;

  /** 料金タイプ */
  private Integer priceType;

  /** 料金単位 */
  private Integer pricePerUnit;

  /** 料金 */
  private Integer price;

  /** 適用開始日時 */
  private String effectiveStartTime;

  /** 適用終了日時 */
  private String effectiveEndTime;

  /** 優先度 */
  private Integer priority;

  /** オペレータID */
  private String operatorId;
}
