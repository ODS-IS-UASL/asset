package com.hitachi.droneroute.cmn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

/** DateTimeUtilsクラスの単体テスト */
public class DateTimeUtilsTest {

  /**
   * メソッド名: getUtcCurrentTimestamp<br>
   * 試験名: 現在のUTCタイムスタンプを取得する<br>
   * 条件: メソッドを呼び出す<br>
   * 結果: 現在のUTCタイムスタンプが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetUtcCurrentTimestamp_Normal() {
    new DateTimeUtils();
    // 現在のUTCタイムスタンプを取得
    Timestamp actual = DateTimeUtils.getUtcCurrentTimestamp();

    // 期待されるタイムスタンプを取得
    LocalDateTime expectedLocalDateTime = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
    Timestamp expected = Timestamp.valueOf(expectedLocalDateTime);

    // タイムスタンプの秒単位での比較
    assertEquals(expected.toLocalDateTime().getSecond(), actual.toLocalDateTime().getSecond());
  }
}
