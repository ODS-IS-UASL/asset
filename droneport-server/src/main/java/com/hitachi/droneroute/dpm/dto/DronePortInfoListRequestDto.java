package com.hitachi.droneroute.dpm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ドローンポート情報一覧取得要求
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortInfoListRequestDto {

	/**
	 * ドローンポート名
	 */
	private String dronePortName;
	
	/**
	 * 設置場所住所
	 */
	private String address;
	
	/**
	 * 製造メーカー
	 */
	private String manufacturer;

	/**
	 * 製造番号
	 */
	private String serialNumber;
	
	/**
	 * ポート形状
	 */
	private String portType;
	
	/**
	 * 最小緯度(南側)
	 */
	private Double minLat;
	
	/**
	 * 最小経度(西側)
	 */
	private Double minLon;
	
	/**
	 * 最大緯度(北側)
	 */
	private Double maxLat;
	
	/**
	 * 最大経度(東側)
	 */
	private Double maxLon;
	
	/**
	 * 対応機体
	 */
	private String supportDroneType;
	
	/**
	 * 動作状況
	 */
	private String activeStatus;
	
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
