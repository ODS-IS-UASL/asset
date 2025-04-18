package com.hitachi.droneroute.dpm.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ドローンポート予約情報登録要求
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortReserveInfoRegisterListRequestDto {
	
	/**
	 * ドローンポート予約情報リスト
	 */
	private List<Element> data;
	
	/**
	 * オペレータID
	 */
	private String operatorId;
	
	@NoArgsConstructor
	@Setter
	@Getter
	@ToString
	public static class Element {
		/**
		 * ドローンポートID
		 */
		private String dronePortId;
		
		/**
		 * 使用機体ID
		 */
		private String aircraftId;
		
		/**
		 * 航路予約ID
		 */
		private String routeReservationId;
		
		/**
		 * 利用形態
		 */
		private Integer usageType;
		
		/**
		 * 予約開始日時
		 */
		private String reservationTimeFrom;
		
		/**
		 * 予約終了日時
		 */
		private String reservationTimeTo;
	}

}
