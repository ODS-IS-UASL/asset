package com.hitachi.droneroute.dpm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ドローンポート周辺情報取得応答
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortEnvironmentInfoResponseDto {

	/**
	 * ドローンポートID
	 */
	private String dronePortId;
	
	/**
	 * 風速
	 */
	private Double windSpeed;
	
	/**
	 * 風向
	 */
	private Double windDirection;
	
	/**
	 * 雨量
	 */
	private Double rainfall;
	
	/**
	 * 気温
	 */
	private Double temp;
	
	/**
	 * 気圧
	 */
	private Double pressure;
	
	/**
	 * 障害物
	 */
	private Boolean obstacleDetected;
	
	/**
	 * 取得時刻
	 */
	private String observationTime;
}
