package com.hitachi.droneroute.dpm.dto.vis;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 予約情報
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class ReservationInfo {

	/**
	 * 予約管理ID
	 */
	@JsonProperty("reservation_id")
	private String reservationId;
	
	/**
	 * 予約開始日時
	 */
	@JsonProperty("start_time")
	private String startTime;
	
	/**
	 * 予約終了日時
	 */
	@JsonProperty("end_time")
	private String endTime;
}
