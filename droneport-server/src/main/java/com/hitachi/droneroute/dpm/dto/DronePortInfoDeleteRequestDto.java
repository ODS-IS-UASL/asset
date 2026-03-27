package com.hitachi.droneroute.dpm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 離着陸場情報削除要求DTO(離着陸場予約情報削除要求DTOも兼ねる) */
@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DronePortInfoDeleteRequestDto {

  /** オペレータID */
  private String operatorId;
}
