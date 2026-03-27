package com.hitachi.droneroute.dpm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 離着陸場予約情報更新要求DTO */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortReserveInfoUpdateRequestDto {

  /** 離着陸場予約ID */
  private String dronePortReservationId;

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
}
