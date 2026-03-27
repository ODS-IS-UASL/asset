package com.hitachi.droneroute.cmn.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** StringUtilsクラスの単体テスト */
public class StringUtilsTest {

  /**
   * メソッド名: stringToIntegerArray<br>
   * 試験名: 正常なカンマ区切り文字列を整数配列に変換する<br>
   * 条件: "1,2,3" を入力する<br>
   * 結果: [1, 2, 3] が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testStringToIntegerArray_Normal() {
    String input = "1,2,3";
    Integer[] expected = {1, 2, 3};
    Integer[] actual = StringUtils.stringToIntegerArray(input);
    assertArrayEquals(expected, actual);
  }

  /**
   * メソッド名: stringToIntegerArray<br>
   * 試験名: 空の文字列を入力した場合の動作<br>
   * 条件: "" を入力する<br>
   * 結果: 空の配列が返される<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testStringToIntegerArray_EmptyString() {
    String input = "";
    Integer[] actual = StringUtils.stringToIntegerArray(input);
    assertNull(actual);
  }

  /**
   * メソッド名: stringToIntegerArray<br>
   * 試験名: 無効な整数値を含む文字列を入力した場合の動作<br>
   * 条件: "1,a,3" を入力する<br>
   * 結果: NumberFormatException がスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testStringToIntegerArray_InvalidNumber() {
    String input = "1,a,3";
    assertThrows(
        NumberFormatException.class,
        () -> {
          StringUtils.stringToIntegerArray(input);
        });
  }

  /**
   * メソッド名: stringToStringArray<br>
   * 試験名: 正常なカンマ区切り文字列を整数配列に変換する<br>
   * 条件: "1,2,3" を入力する<br>
   * 結果: [1, 2, 3] が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testStringToStringArray_Normal() {
    String input = "1,2,3";
    String[] expected = {"1", "2", "3"};
    String[] actual = StringUtils.stringToStringArray(input);
    assertArrayEquals(expected, actual);
  }

  /**
   * メソッド名: stringToStringArray<br>
   * 試験名: 空の文字列を入力した場合の動作<br>
   * 条件: "" を入力する<br>
   * 結果: 空の配列が返される<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testStringToStringArray_EmptyString() {
    String input = "";
    String[] actual = StringUtils.stringToStringArray(input);
    assertNull(actual);
  }

  /**
   * メソッド名: stringToStringArray<br>
   * 試験名: 空の文字列を入力した場合の動作<br>
   * 条件: Null を入力する<br>
   * 結果: 空の配列が返される<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testStringToStringArray_Null() {
    String input = null;
    String[] actual = StringUtils.stringToStringArray(input);
    assertNull(actual);
  }

  /**
   * メソッド名: parseDatetimeString<br>
   * 試験名: 正常な日時文字列を Timestamp に変換する<br>
   * 条件: "2023-10-01T10:15:30+09:00" を入力する<br>
   * 結果: "2023-10-01 10:15:30.0" の Timestamp が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testParseDatetimeString_Normal() {
    String input = "2023-10-01T10:15:30+09:00";
    LocalDateTime localDateTime =
        ZonedDateTime.parse(input).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

    Timestamp expected = Timestamp.valueOf(localDateTime);
    Timestamp actual = StringUtils.parseDatetimeString(input);
    assertEquals(expected, actual);
  }

  /**
   * メソッド名: parseDatetimeString<br>
   * 試験名: 無効な日時文字列を入力した場合の動作<br>
   * 条件: "invalid-datetime" を入力する<br>
   * 結果: DateTimeParseException がスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testParseDatetimeString_InvalidDatetime() {
    String input = "invalid-datetime";
    assertThrows(
        DateTimeParseException.class,
        () -> {
          StringUtils.parseDatetimeString(input);
        });
  }

  /**
   * メソッド名: parseDatetimeString<br>
   * 試験名: 空の文字列を入力した場合の動作<br>
   * 条件: "" を入力する<br>
   * 結果: DateTimeParseException がスローされる<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testParseDatetimeString_EmptyString() {
    String input = "";
    assertThrows(
        DateTimeParseException.class,
        () -> {
          StringUtils.parseDatetimeString(input);
        });
  }

  /**
   * メソッド名: parseDatetimeString<br>
   * 試験名: nullを入力した場合の動作<br>
   * 条件: nullを入力する<br>
   * 結果: nullが返却される<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testParseDatetimeString_null() {
    assertNull(StringUtils.parseDatetimeString(null));
  }

  /**
   * メソッド名: parseDatetimeStringToLocalDateTime<br>
   * 試験名: 正常な日時文字列を LocalDateTime に変換する<br>
   * 条件: "2023-10-01T10:15:30+09:00" を入力する<br>
   * 結果: "2023-10-01 10:15:30.0" の LocalDateTime が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testParseDatetimeStringToLocalDateTime_Normal() {
    new StringUtils();
    String input = "2023-10-01T10:15:30+09:00";
    LocalDateTime expected =
        ZonedDateTime.parse(input).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    LocalDateTime actual = StringUtils.parseDatetimeStringToLocalDateTime(input);
    assertEquals(expected, actual);
  }

  /**
   * メソッド名: parseDatetimeStringToLocalDateTime<br>
   * 試験名: nullを入力した場合の動作<br>
   * 条件: nullを入力する<br>
   * 結果: nullが返却される<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testParseDatetimeStringToLocalDateTime_null() {
    assertNull(StringUtils.parseDatetimeStringToLocalDateTime(null));
  }

  /**
   * メソッド名: toUtcDateTimeString<br>
   * 試験名: 正常なLocalDateTimeをUTCのタイムゾーン付き文字列に変換する<br>
   * 条件: LocalDateTime.of(2023, 10, 1, 10, 15, 30) を入力する<br>
   * 結果: "2023-10-01T10:15:30Z" が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testToUtcDateTimeString_Normal() {
    LocalDateTime input = LocalDateTime.of(2023, 10, 1, 10, 15, 30);
    String expected = "2023-10-01T10:15:30Z";
    String actual = StringUtils.toUtcDateTimeString(input);
    assertEquals(expected, actual);
  }

  /**
   * メソッド名: toUtcDateTimeString<br>
   * 試験名: nullを入力した場合の動作<br>
   * 条件: nullを入力する<br>
   * 結果: NullPointerExceptionがスローされる<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testToUtcDateTimeString_Null() {
    String actual = StringUtils.toUtcDateTimeString(null);
    assertNull(actual);
  }

  /**
   * メソッド名: containsInvalidCharacters<br>
   * 試験名: null/空文字列入力の場合の動作<br>
   * 条件: null または空文字列を入力する<br>
   * 結果: false が返されること（str == null || str.isEmpty() の分岐カバレッジ）<br>
   * テストパターン：エッジケース<br>
   */
  @ParameterizedTest
  @NullAndEmptySource
  void testContainsInvalidCharacters_NullOrEmpty(String input) {
    assertFalse(StringUtils.containsInvalidCharacters(input));
  }

  /**
   * メソッド名: containsInvalidCharacters<br>
   * 試験名: 不正な制御文字を含む文字列の動作<br>
   * 条件: 各種制御文字を含む文字列を入力する<br>
   * 結果: true が返されること（制御文字検出の分岐カバレッジ）<br>
   * テストパターン：異常系<br>
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "test\u0000data", // ヌルバイト
        "test\u0001data", // C0制御文字
        "test\u007Fdata", // DELETE文字
        "test\u009Fdata" // C1制御文字
      })
  void testContainsInvalidCharacters_InvalidCharacters(String input) {
    assertTrue(StringUtils.containsInvalidCharacters(input));
  }

  /**
   * メソッド名: containsInvalidCharacters<br>
   * 試験名: 許可文字を含む文字列の動作<br>
   * 条件: タブ、改行、CR、正常文字を含む文字列を入力する<br>
   * 結果: false が返されること（c != '\t' && c != '\n' && c != '\r' の分岐カバレッジ）<br>
   * テストパターン：正常系<br>
   */
  @ParameterizedTest
  @ValueSource(
      strings = {
        "test\tdata", // タブ
        "test\ndata", // 改行
        "test\rdata", // キャリッジリターン
        "test data" // 正常文字
      })
  void testContainsInvalidCharacters_AllowedCharacters(String input) {
    assertFalse(StringUtils.containsInvalidCharacters(input));
  }

  /**
   * メソッド名: containsInvalidCharacters<br>
   * 試験名: 境界値の動作<br>
   * 条件: 制御文字範囲の境界値を入力する<br>
   * 結果: 期待値（true/false）が返される<br>
   * テストパターン：境界値<br>
   */
  @ParameterizedTest
  @CsvSource({
    "test\u001Fdata, true", // \u001F: 制御文字範囲内（最後のC0制御文字）
    "test\u0020data, false", // \u0020: スペース、範囲外
    "test\u007Edata, false", // \u007E: ~、範囲外
    "test\u00A0data, false" // \u00A0: ノーブレークスペース、範囲外
  })
  void testContainsInvalidCharacters_BoundaryValues(String input, boolean expected) {
    assertEquals(expected, StringUtils.containsInvalidCharacters(input));
  }
}
