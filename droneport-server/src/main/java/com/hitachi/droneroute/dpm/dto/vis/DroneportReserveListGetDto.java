package com.hitachi.droneroute.dpm.dto.vis;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 予約照会
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class DroneportReserveListGetDto {

	/**
	* ドローンポート予約ID
	*/
	@JsonProperty("droneport_reservation_id")
	private String droneportReservationId;

}
