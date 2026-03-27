package com.hitachi.droneroute.dpm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 離着陸場予約情報更新応答DTO */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortReserveInfoUpdateResponseDto {

  /**
   * 離着陸場予約ID<br>
   * 登録の場合は新たに付与したIDを返却する。<br>
   * 更新の場合は要求で指定されたIDを返却する。<br>
   */
  private String dronePortReservationId;
}
