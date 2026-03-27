package com.hitachi.droneroute.prm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 料金情報　登録更新削除要求のDTO. */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceInfoRequestDto {
  /** 処理種別 */
  private Integer processingType;

  /** 料金ID */
  private String priceId;

  /** リソースID */
  private String resourceId;

  /** リソース種別 */
  private Integer resourceType;

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

  /** レコード番号 エラーメッセージ出力で使用する */
  private Integer rowNumber;
}
