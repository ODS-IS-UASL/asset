package com.hitachi.droneroute.dpm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 離着陸場情報登録更新要求DTO */
@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DronePortInfoRegisterRequestDto {

  /** 離着陸場ID */
  private String dronePortId;

  /** 離着陸場名 */
  private String dronePortName;

  /** 設置場所住所 */
  private String address;

  /** 製造メーカー */
  private String manufacturer;

  /** 製造番号 */
  private String serialNumber;

  /** 離着陸場メーカーID */
  private String dronePortManufacturerId;

  /** ポート形状 */
  private Integer portType;

  /** VIS離着陸場事業者ID */
  private String visDronePortCompanyId;

  /** 格納中機体ID */
  private String storedAircraftId;

  /** 緯度 */
  private Double lat;

  /** 経度 */
  private Double lon;

  /** 着陸面対地高度 */
  private Double alt;

  /** 対応機体 */
  private String supportDroneType;

  /** 動作状況 */
  private Integer activeStatus;

  /** 使用不可開始日時 */
  private String inactiveTimeFrom;

  /** 使用不可終了日時 */
  private String inactiveTimeTo;

  /** 画像(base64) */
  @ToString.Exclude private String imageData;

  /** 画像(バイト型) */
  @ToString.Exclude private byte[] imageBinary;

  /** 公開可否フラグ */
  private Boolean publicFlag;

  /** 料金情報 */
  private List<PriceInfoRequestDto> priceInfos;
}
