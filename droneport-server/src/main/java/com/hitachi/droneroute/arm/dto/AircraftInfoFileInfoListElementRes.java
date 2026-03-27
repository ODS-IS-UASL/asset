package com.hitachi.droneroute.arm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 補足資料情報の検索結果要素(一覧用). */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AircraftInfoFileInfoListElementRes {

  /** 補足資料名称(ファイル論理名) */
  private String fileLogicalName;

  /** ファイル物理名 */
  private String filePhysicalName;

  /** 補足資料ID */
  private String fileId;
}
