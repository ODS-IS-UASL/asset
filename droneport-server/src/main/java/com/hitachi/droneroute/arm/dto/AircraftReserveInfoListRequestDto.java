package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 機体予約情報一覧要求のDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AircraftReserveInfoListRequestDto {
    /**
     * 機体ID
     */
    private String aircraftId;
    
    // MVP1指摘対応 #17 機体名検索追加
    /**
     * 機体名
     */
    private String aircraftName;
    
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
