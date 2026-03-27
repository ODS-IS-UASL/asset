package com.hitachi.droneroute.prm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 料金情報取得要求のDTO. */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceInfoSearchListRequestDto {
  /** 料金ID */
  private String priceId;

  /** リソースID */
  private String resourceId;

  /** リソース種別 */
  private BigInteger resourceType;

  /** 主管航路事業者ID */
  private String primaryRouteOperatorId;

  /** 料金タイプ */
  private BigInteger priceType;

  /** 料金単位(以上) */
  private BigInteger pricePerUnitFrom;

  /** 料金単位(以下) */
  private BigInteger pricePerUnitTo;

  /** 料金(以上) */
  private BigInteger priceFrom;

  /** 料金(以下) */
  private BigInteger priceTo;

  /** 日時条件(開始) */
  private String effectiveStartTime;

  /** 日時条件(終了) */
  private String effectiveEndTime;

  /** ソート順 */
  private String sortOrders;

  /** ソート対象列名 */
  private String sortColumns;
}
