package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 機体情報一覧要求のDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AircraftInfoSearchListRequestDto {
    /**
     * 機体名
     */
    private String aircraftName;
     
    /**
     * 製造メーカー
     */
    private String manufacturer;

    /**
     * 製造番号
     */
    private String manufacturingNumber;
     
    /**
     * 機体の種類
     */
    private String aircraftType;
    
    /**
     * 機体認証の有無
     */
    private String certification;

    /**
     * DIPS登録記号
     */
    private String dipsRegistrationCode;
    
    /**
     * 機体所有種別
     */
    private String ownerType;
    
    /**
     * 所有者ID
     */
    private String ownerId;
    
    /**
     * 最小緯度（南側）
     */
    private Double minLat;
    
    /**
     * 最小経度（西側）
     */
    private Double minLon;
    
    /**
     * 最大緯度（北側）
     */
    private Double maxLat;
    
    /**
     * 最大経度（東側）
     */
    private Double maxLon;
    
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