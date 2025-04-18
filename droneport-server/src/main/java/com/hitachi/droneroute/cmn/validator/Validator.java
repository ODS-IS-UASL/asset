package com.hitachi.droneroute.cmn.validator;

import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.hitachi.droneroute.cmn.exception.ValidationErrorException;

/**
 * コントローラー類のパラメータチェック用基底クラス.
 * 
 * @author Hiroshi Toyoda
 *
 */
public class Validator {
	
	protected final String errorMessage_notNull = "{0}に値が設定されていません。";
	protected final String errorMessage_checkLength = "{0}の長さが不正です。\n最大長({1})";
	protected final String errorMessage_checkRange = "{0}の値が不正です。\\n最小値({1})、最大値({2})";
	protected final String errorMessage_checkRange2 = "{0}の値が不正です。\\n範囲{1}";
	protected final String errorMessage_checkValue = "{0}が不正です。\n入力値:{1}";
	protected final String errorMessage_checkUUID = "{0}がUUIDではありません。\n入力値:{1}";
	protected final String errorMessage_checkDateTime = "{0}がサポートされていない形式です。\n入力値:{1}";
	protected final String errorMessage_checkDateTime_compare = "{0}が{1}よりも未来の日時になっています。{0}({2}),{1}({3})";
	protected final String errorMessage_checkBase64 = "画像形式が不正です。";
	protected final String errorMessage_checkInactiveTime = "使用不可開始/終了日時を入力する場合は、動作状況に3:使用不可、または4:メンテナンス中を入力してください。";
	protected final String errorMessage_sort = "ソート順とソート対象列の設定数が一致しません。";
	protected final String errorMessage_storedAircraftId = "ポート形状:{0}の場合は、格納中機体IDを入力できません。";
	protected final String errorMessage_dronePortManufacturer = "ドローンポートメーカーIDは入力できません。";
	
	protected List<String> details = new ArrayList<>();
	
	protected void validate() throws ValidationErrorException {
		if (!details.isEmpty()) {
			throw new ValidationErrorException(Arrays.toString(details.toArray()));
		}
	}

	/**
	 * 項目の必須チェック.
	 *
	 * @param object チェック対象
	 * @param name   チェック対象の名称
	 */
	protected void notNull(String name, String str) {
		if (!StringUtils.hasText(str)) {
			details.add(MessageFormat.format(errorMessage_notNull, name));
		}
	}
	
	/**
	 * 項目の必須チェック.
	 *
	 * @param object チェック対象
	 * @param name   チェック対象の名称
	 */
	protected void notNull(String name, Object object) {
		if (Objects.isNull(object)) {
			details.add(MessageFormat.format(errorMessage_notNull, name));
		}
	}
	
	/**
	 * 文字長のチェック.
	 *
	 * @param str       チェック文字
	 * @param name      チェック対象の名称
	 * @param maxLength チェック対象の最大長
	 */
	protected void checkLength(String name, String str, int maxLength) {
		if (StringUtils.hasText(str) && str.length() > maxLength) {
			details.add(MessageFormat.format(errorMessage_checkLength, name, maxLength));
		}
	}
	
	/**
	 * 入力数値上下限チェック(int)
	 *
	 * @param name  チェック対象名称
	 * @param value チェック対象
	 * @param min   最小値
	 * @param max   最大値
	 */
	protected void checkRange(String name, Integer value, int min, int max) {
		if (value != null && (min > max || min > value || value > max)) {
			this.details.add(MessageFormat.format(errorMessage_checkRange, name, min, max));
		}
	}
	
	/**
	 * 入力数値上下限チェック(String -> int)
	 * 
	 * @param name  チェック対象名称
	 * @param value チェック対象(String型)
	 * @param min   最小値
	 * @param max   最大値
	 */
	protected void checkRangeInteger(String name, String value, int min, int max) {
		if (StringUtils.hasText(value)) {
			try {
				Integer intValue = Integer.valueOf(value);
				checkRange(name, intValue, min, max);
			} catch (NumberFormatException e) {
				this.details.add(MessageFormat.format(errorMessage_checkRange, name, min, max));
			}
		}
	}

	/**
	 * 入力数値範囲チェック(int)
	 *
	 * @param name  チェック対象名称
	 * @param value チェック対象
	 * @param range 範囲
	 */
	protected void checkRange(String name, Integer value, Integer[] range) {
		if (value != null && Arrays.binarySearch(range, value) < 0) {
			this.details.add(MessageFormat.format(errorMessage_checkRange2, name, Arrays.toString(range)));
		}
	}
	/**
	 * 入力数値範囲チェック(int)
	 *
	 * @param name  チェック対象名称
	 * @param commaDelimited チェック対象(カンマ区切りの数字)
	 * @param range 範囲
	 */
	protected void checkRange(String name, String commaDelimited, Integer[] range) {
		if (StringUtils.hasText(commaDelimited)) {
			Pattern pattern = Pattern.compile("([\\d],)*[\\d]+");
			checkValue(name, commaDelimited, pattern);
			if (pattern.matcher(commaDelimited).matches()) {
				for (String s : commaDelimited.split(",")) {
					Integer v = Integer.parseInt(s);
					checkRange(name, v, range);
				}
			}
		}
	}
	
	/**
	 * 入力数値上下限チェック(long)
	 *
	 * @param name  チェック対象名称
	 * @param value チェック対象
	 * @param min   最小値
	 * @param max   最大値
	 */
	protected void checkRange(String name, Long value, long min, long max) {
		if (value != null && (min > max || min > value || value > max)) {
			this.details.add(MessageFormat.format(errorMessage_checkRange, name, min, max));
		}
	}
	
	/**
	 * 入力数値上下限チェック(double)
	 *
	 * @param name  チェック対象名称
	 * @param value チェック対象
	 * @param min   最小値
	 * @param max   最大値
	 */
	protected void checkRange(String name, Double value, double min, double max) {
		if (value != null && (min > max || min > value || value > max)) {
			this.details.add(MessageFormat.format(errorMessage_checkRange, name, min, max));
		}
	}

	/**
	 * 入力値チェック(未入力は許容する)
	 * @param name 項目名
	 * @param value 入力値
	 * @param pattern 正規表現
	 */
	protected void checkValue(String name, String value, Pattern pattern) {
		if (StringUtils.hasText(value) 
				&& pattern != null && !pattern.matcher(value).matches()) {
			this.details.add(MessageFormat.format(errorMessage_checkValue, name, value));
		}
	}
	
	/**
	 * 入力値(文字列)がUUIDであることをチェックする
	 * @param name 項目名
	 * @param value 入力値
	 */
	protected void checkUUID(String name, String value) {
		if (StringUtils.hasText(value)) {
			try {
				UUID.fromString(value);
			} catch (IllegalArgumentException e) {
				this.details.add(MessageFormat.format(errorMessage_checkUUID, name, value));
			}
		}
	}
	
	/**
	 * 入力値が日時文字列(ISO 8601)であることをチェックする
	 * @param name 項目名
	 * @param value 入力値
	 */
	protected void checkDateTime(String name, String value) {
		if (StringUtils.hasText(value)) {
			try {
				ZonedDateTime.parse(value);
			} catch (DateTimeParseException e) {
				this.details.add(MessageFormat.format(errorMessage_checkDateTime, name, value));
			}
		}
	}
	
	/**
	 * ２つの日時の前後関係をチェックする
	 * @param lowerName 開始日時項目名
	 * @param upperName 終了日時項目名
	 * @param lower 開始日時(文字列)
	 * @param upper 終了日時(文字列)
	 */
	protected void compareDateTime(String lowerName, String upperName, String lower, String upper) {
		if (StringUtils.hasText(lower) && StringUtils.hasText(upper)) {
			try {
				ZonedDateTime lowerDt = ZonedDateTime.parse(lower);
				ZonedDateTime upperDt = ZonedDateTime.parse(upper);
				if (lowerDt.compareTo(upperDt) > 0) {
					this.details.add(MessageFormat.format(
							errorMessage_checkDateTime_compare, 
							lowerName, upperName,
							lower, upper));
				}
			} catch (DateTimeParseException e) {
				; // 日時文字列が不正な場合は何もしない。(書式チェックは別に行う)
			}
		}
	}
}
