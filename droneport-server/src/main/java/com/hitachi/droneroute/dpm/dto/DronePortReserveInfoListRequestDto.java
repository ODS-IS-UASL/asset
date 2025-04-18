package com.hitachi.droneroute.dpm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ドローンポート予約情報一覧取得要求
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortReserveInfoListRequestDto {

	/**
	 * ドローンポートID
	 */
	private String dronePortId;
	
	/**
	 * ドローンポート名
	 */
	private String dronePortName;
	
	/**
	 * 使用機体ID
	 */
	private String aircraftId;
	
	/**
	 * 航路予約ID
	 */
	private String routeReservationId;
	
	/**
	 * 日時条件(開始)
	 */
	private String timeFrom;
	
	/**
	 * 日時条件(終了)
	 */
	private String timeTo;

	/**
	 * 1ページ当たりの件数
	 */
	private String perPage;
	
	/**
	 * 現在ページ番号
	 */
	private String page;
	
	/**
	 * ソート順
	 */
	private String sortOrders;
	
	/**
	 * ソート対象列名
	 */
	private String sortColumns;

	/**
	 * オペレータID
	 */
	private String operatorId;
	
}
