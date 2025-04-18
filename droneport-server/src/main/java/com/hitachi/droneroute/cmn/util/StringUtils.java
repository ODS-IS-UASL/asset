package com.hitachi.droneroute.cmn.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文字列を扱うユーティリティクラス
 * @author Hiroshi Toyoda
 *
 */
public class StringUtils {

	/**
	 * 数字のみのカンマ区切り文字列を整数配列に変換する。<br>
	 * 単一の数字のみの場合は、要素が１つの配列に変換する。
	 * @param str 数字のみのカンマ区切り文字列
	 * @return 整数配列
	 */
	public static Integer[] stringToIntegerArray(String str) {
		return !org.springframework.util.StringUtils.hasText(str) ? null : 
			Stream.of(str.split(","))
				.map(e -> Integer.parseInt(e))
				.collect(Collectors.toList())
				.toArray(Integer[]::new);
	}
	
	/**
	 * 日時文字列(ISO 8601)をTimestamp型(UTC)に変換する
	 * @param str 日時文字列(ISO 8601)
	 * @return Timestamp(UTC)
	 */
	public static Timestamp parseDatetimeString(String str) {
		return Objects.isNull(str) ? null : Timestamp.valueOf(parseDatetimeStringToLocalDateTime(str));
	}
	
	/**
	 * 日時文字列(ISO 8601)をLocalDateTime型(UTC)に変換する
	 * @param str 日時文字列(ISO 8601)
	 * @return LocalDateTime(UTC)
	 */
	public static LocalDateTime parseDatetimeStringToLocalDateTime(String str) {
		return Objects.isNull(str) ? null : ZonedDateTime.parse(str)
				.withZoneSameInstant(ZoneId.of("UTC"))
				.toLocalDateTime();
	}
	
	/**
	 * LocalDateTime(UTCを想定)をUTCのタイムゾーン付きの文字列(ISO 8601形式)に変換する
	 * @param localDateTime UTC日時
	 * @return UTCタイムゾーン付き文字列(ISO 8601形式)
	 */
	public static String toUtcDateTimeString(LocalDateTime localDateTime) {
		return Objects.isNull(localDateTime) ? null
				: ZonedDateTime.of(localDateTime, ZoneId.of("UTC"))
				.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}
}
