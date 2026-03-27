package com.hitachi.droneroute.arm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListDetailElement;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 機体情報一覧応答要素. */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AircraftInfoSearchListElement {
  /** 機体ID */
  private String aircraftId;

  /** 機体名 */
  private String aircraftName;

  /** 製造メーカー */
  private String manufacturer;

  /** 型式番号 */
  private String modelNumber;

  /** 機種名 */
  private String modelName;

  /** 製造番号 */
  private String manufacturingNumber;

  /** 機体の種類 */
  private Integer aircraftType;

  /** 最大離陸重量 */
  private Double maxTakeoffWeight;

  /** 重量 */
  private Double bodyWeight;

  /** 最大速度 */
  private Double maxFlightSpeed;

  /** 最大飛行時間 */
  private Double maxFlightTime;

  /** 機体認証の有無 */
  private Boolean certification;

  /** 位置情報（緯度） */
  private Double lat;

  /** 位置情報（経度） */
  private Double lon;

  /** DIPS登録記号 */
  private String dipsRegistrationCode;

  /** 機体所有種別 */
  private Integer ownerType;

  /** 所有者ID */
  private String ownerId;

  /** オペレータID */
  private String operatorId;

  /** 公開可否フラグ */
  private Boolean publicFlag;

  /** ペイロード情報リスト */
  @JsonInclude(Include.NON_NULL)
  private List<AircraftInfoPayloadInfoSearchListElement> payloadInfos;

  /** 料金情報 */
  private List<PriceInfoSearchListDetailElement> priceInfos;
}
