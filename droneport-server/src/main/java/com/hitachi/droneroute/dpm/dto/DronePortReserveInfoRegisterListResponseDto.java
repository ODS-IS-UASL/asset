package com.hitachi.droneroute.dpm.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 離着陸場予約情報登録応答DTO */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortReserveInfoRegisterListResponseDto {

  /** 離着陸場予約IDリスト */
  private List<String> dronePortReservationIds;
}
