package com.hitachi.droneroute.dpm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ドローンポート情報登録更新要求
 * @author Hiroshi Toyoda
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DronePortInfoRegisterRequestDto {

	/**
	 * ドローンポートID
	 */
	private String dronePortId;
	
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
	 * ドローンポートメーカーID
	 */
	private String dronePortManufacturerId;
	
	/**
	 * ポート形状
	 */
	private Integer portType;
	
	/**
	 * VISドローンポート事業者ID
	 */
	private String visDronePortCompanyId;

	/**
	 * 格納中機体ID
	 */
	private String storedAircraftId;
	
	/**
	 * 緯度
	 */
	private Double lat;
	
	/**
	 * 経度
	 */
	private Double lon;
	
	/**
	 * 着陸面対地高度
	 */
	private Double alt;
	
	/**
	 * 対応機体
	 */
	private String supportDroneType;
	
	/**
	 * 動作状況
	 */
	private Integer activeStatus;
	
	/**
	 * 使用不可開始日時
	 */
	private String inactiveTimeFrom;
	
	/**
	 * 使用不可終了日時
	 */
	private String inactiveTimeTo;
	
	/**
	 * 画像(base64)
	 */
	@ToString.Exclude
	private String imageData;
	
	/**
	 * 画像(バイト型)
	 */
	@ToString.Exclude
	private byte[] imageBinary;
	
	/**
	 * オペレータID
	 */
	private String operatorId;
	
}
