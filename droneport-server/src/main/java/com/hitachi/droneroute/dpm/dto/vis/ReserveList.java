package com.hitachi.droneroute.dpm.dto.vis;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 照会応答DTO */
@NoArgsConstructor
@Getter
@Setter
public class ReserveList {
  /** 離着陸場ID */
  @JsonProperty("droneport_id")
  private String droneportId;

  /** 予約情報 */
  @JsonProperty("reservation_info")
  private List<ReservationInfo> reservationInfo;

  /** 応答ステータス */
  @JsonProperty("response_status")
  private boolean responseStatus;

  /** 情報（否認時の理由) */
  @JsonProperty("info")
  private String info;
}
