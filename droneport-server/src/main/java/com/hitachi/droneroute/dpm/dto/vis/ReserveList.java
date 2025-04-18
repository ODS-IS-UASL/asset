package com.hitachi.droneroute.dpm.dto.vis;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 照会応答
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class ReserveList {
	/**
	 * ドローンポートID
	 */
	@JsonProperty("droneport_id")
	private String droneportId;
	
	/**
	 * 予約情報
	 */
	@JsonProperty("reservation_info")
	private List<ReservationInfo> reservationInfo; 
	
	/**
	 * 応答ステータス
	 */
	@JsonProperty("response_status")
	private boolean responseStatus;
	
	/**
	 * 情報（否認時の理由)
	 */
	@JsonProperty("info")
	private String info;

}
