package com.hitachi.droneroute.config.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** ロール情報DTO */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class RoleInfoDto {
  /** ロールID */
  private String roleId;

  /** ロール名 */
  private String roleName;
}
