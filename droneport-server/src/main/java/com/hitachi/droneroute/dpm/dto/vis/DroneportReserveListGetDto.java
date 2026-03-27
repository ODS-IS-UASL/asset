package com.hitachi.droneroute.dpm.dto.vis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 予約照会APIのリクエストDTO */
@NoArgsConstructor
@Getter
@Setter
public class DroneportReserveListGetDto {

  /** 離着陸場予約ID */
  @JsonProperty("droneport_reservation_id")
  private String droneportReservationId;
}
