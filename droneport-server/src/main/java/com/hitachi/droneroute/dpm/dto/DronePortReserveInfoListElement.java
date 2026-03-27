package com.hitachi.droneroute.dpm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 離着陸場予約情報一覧取得応答要素DTO */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortReserveInfoListElement {

  /** 離着陸場予約ID */
  private String dronePortReservationId;

  /** 一括予約ID */
  private String groupReservationId;

  /** 離着陸場ID */
  private String dronePortId;

  /** 使用機体ID */
  private String aircraftId;

  /** 航路予約ID */
  private String routeReservationId;

  /** 利用形態 */
  private Integer usageType;

  /** 予約開始日時 */
  private String reservationTimeFrom;

  /** 予約終了日時 */
  private String reservationTimeTo;

  /** 離着陸場名 */
  private String dronePortName;

  /** 機体名 */
  private String aircraftName;

  /** VIS離着陸場事業者ID */
  private String visDronePortCompanyId;

  /** 予約有効フラグ */
  private Boolean reservationActiveFlag;

  /** 使用不可開始日時 */
  private String inactiveTimeFrom;

  /** 使用不可終了日時 */
  private String inactiveTimeTo;

  /** 予約事業者ID */
  private String reserveProviderId;

  /** オペレータID */
  private String operatorId;
}
