package com.hitachi.droneroute.dpm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ドローンポート情報登録更新応答
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortInfoRegisterResponseDto {
	
	/**
	 * ドローンポート情報ID<br>
	 * 登録の場合は新たに付与したIDを返却する。<br>
	 * 更新の場合は要求で指定されたIDを返却する。<br>
	 */
	private String dronePortId;

}
