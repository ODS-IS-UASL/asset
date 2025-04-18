package com.hitachi.droneroute.arm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 機体情報登録更新応答のDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AircraftInfoResponseDto {
    /**
     * 機体ID
     */
    private String aircraftId;

}
