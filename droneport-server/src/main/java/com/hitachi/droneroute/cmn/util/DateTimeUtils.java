package com.hitachi.droneroute.cmn.util;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/** 日時関連のユーティリティクラス */
public class DateTimeUtils {

  /**
   * 現在のUTC日時をTimestamp形式で取得する
   *
   * @return 現在のUTC日時のTimestamp
   */
  public static Timestamp getUtcCurrentTimestamp() {
    return Timestamp.valueOf(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime());
  }
}
