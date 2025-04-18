package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 機体予約情報詳細応答のDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AircraftReserveInfoDetailResponseDto {
    /**
     * 機体予約ID
     */
    private String aircraftReservationId;

    /**
     * 機体ID
     */
    private String aircraftId;
    
    /**
     * 予約開始時間
     */
    private String reservationTimeFrom;

    /**
     * 予約終了時間
     */
    private String reservationTimeTo;

    /**
     * 機体名
     */
    private String aircraftName;
    
	/**
	 * オペレータID
	 */
	private String operatorId;
	
}
