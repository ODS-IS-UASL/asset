package com.hitachi.droneroute.arm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 機体情報登録更新要求のDTO. */
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AircraftInfoRequestDto {
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

  /** 位置情報（緯度） */
  private Double lat;

  /** 位置情報（経度） */
  private Double lon;

  /** 機体認証の有無 */
  private Boolean certification;

  /** DIPS登録記号 */
  private String dipsRegistrationCode;

  /** 機体所有種別 */
  private Integer ownerType;

  /** 所有者ID */
  private String ownerId;

  /** 画像(バイト型) */
  @ToString.Exclude private byte[] imageBinary;

  /** 画像(base64) */
  @ToString.Exclude private String imageData;

  /** 公開可否フラグ */
  private Boolean publicFlag;

  /** 補足資料リスト */
  private List<AircraftInfoFileInfoListElementReq> fileInfos;

  /** ペイロード情報 */
  private List<AircraftInfoPayloadInfoListElementReq> payloadInfos;

  /** 料金情報 */
  private List<PriceInfoRequestDto> priceInfos;
}
