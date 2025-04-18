package com.hitachi.droneroute.dpm.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ドローンポート予約情報登録応答
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortReserveInfoRegisterListResponseDto {
	
	/**
	 * ドローンポート予約IDリスト
	 */
	private List<String> dronePortReservationIds;

}
