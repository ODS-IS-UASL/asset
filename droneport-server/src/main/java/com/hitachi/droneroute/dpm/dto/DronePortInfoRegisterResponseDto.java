package com.hitachi.droneroute.dpm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 離着陸場情報登録更新応答DTO */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class DronePortInfoRegisterResponseDto {

  /**
   * 離着陸場情報ID<br>
   * 登録の場合は新たに付与したIDを返却する。<br>
   * 更新の場合は要求で指定されたIDを返却する。<br>
   */
  private String dronePortId;
}
