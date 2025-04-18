package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 機体予約情報登録更新要求のDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AircraftReserveInfoRequestDto {
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
	 * オペレータID
	 */
	private String operatorId;
	
}
