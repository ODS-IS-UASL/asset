package com.hitachi.droneroute.arm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 機体情報削除要求DTO(機体予約情報削除要求も兼ねる) */
@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AircraftInfoDeleteRequestDto {

  /** オペレータID */
  private String operatorId;
}
