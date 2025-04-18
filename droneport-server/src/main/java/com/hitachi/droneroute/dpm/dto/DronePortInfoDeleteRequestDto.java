package com.hitachi.droneroute.dpm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ドローンポート情報削除要求(ドローンポート予約情報削除要求も兼ねる)
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DronePortInfoDeleteRequestDto {

	/**
	 * オペレータID
	 */
	private String operatorId;
	
}
