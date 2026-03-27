package com.hitachi.droneroute.cmn.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 文字列を扱うユーティリティクラス */
public class StringUtils {

  /**
   * 数字のみのカンマ区切り文字列を整数配列に変換する。<br>
   * 単一の数字のみの場合は、要素が１つの配列に変換する。
   *
   * @param str 数字のみのカンマ区切り文字列
   * @return 整数配列
   */
  public static Integer[] stringToIntegerArray(String str) {
    return !org.springframework.util.StringUtils.hasText(str)
        ? null
        : Stream.of(str.split(","))
            .map(e -> Integer.parseInt(e))
            .collect(Collectors.toList())
            .toArray(Integer[]::new);
  }

  /**
   * 文字のみのカンマ区切り文字列を文字列配列に変換する。<br>
   * 単一の文字のみの場合は、要素が１つの配列に変換する。
   *
   * @param str 文字のみのカンマ区切り文字列
   * @return 文字列配列
   */
  public static String[] stringToStringArray(String str) {
    return !org.springframework.util.StringUtils.hasText(str)
        ? null
        : Stream.of(str.split(",")).collect(Collectors.toList()).toArray(String[]::new);
  }

  /**
   * 日時文字列(ISO 8601)をTimestamp型(UTC)に変換する
   *
   * @param str 日時文字列(ISO 8601)
   * @return Timestamp(UTC)
   */
  public static Timestamp parseDatetimeString(String str) {
    return Objects.isNull(str) ? null : Timestamp.valueOf(parseDatetimeStringToLocalDateTime(str));
  }

  /**
   * 日時文字列(ISO 8601)をLocalDateTime型(UTC)に変換する
   *
   * @param str 日時文字列(ISO 8601)
   * @return LocalDateTime(UTC)
   */
  public static LocalDateTime parseDatetimeStringToLocalDateTime(String str) {
    return Objects.isNull(str)
        ? null
        : ZonedDateTime.parse(str).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
  }

  /**
   * LocalDateTime(UTCを想定)をUTCのタイムゾーン付きの文字列(ISO 8601形式)に変換する
   *
   * @param localDateTime UTC日時
   * @return UTCタイムゾーン付き文字列(ISO 8601形式)
   */
  public static String toUtcDateTimeString(LocalDateTime localDateTime) {
    return Objects.isNull(localDateTime)
        ? null
        : ZonedDateTime.of(localDateTime, ZoneId.of("UTC"))
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

  /**
   * 文字列に不正な制御文字（ヌルバイト等）が含まれているかチェックする
   *
   * <p>AppScanで検出されたヌルバイト（%00）脆弱性対策として、<br>
   * SQL Injectionや予期しない動作を引き起こす可能性のある制御文字を検出します。
   *
   * <p>検出対象：<br>
   * - ヌルバイト（\u0000）<br>
   * - その他の危険な制御文字（\u0001-\u0008, \u000B-\u000C, \u000E-\u001F）<br>
   * - DELETE + C1制御文字（\u007F-\u009F）
   *
   * <p>許可する文字：<br>
   * - タブ（\t / \u0009）<br>
   * - 改行（\n / \u000A）<br>
   * - キャリッジリターン（\r / \u000D）
   *
   * @param str チェック対象の文字列
   * @return 不正な文字が含まれている場合true
   */
  public static boolean containsInvalidCharacters(String str) {
    if (str == null || str.isEmpty()) {
      return false;
    }

    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      // タブ・改行・CR以外のC0制御文字（\u0000-\u001F）をチェック
      if ((c <= '\u001F' && c != '\t' && c != '\n' && c != '\r')
          || (c >= '\u007F' && c <= '\u009F')) {
        return true;
      }
    }
    return false;
  }
}
