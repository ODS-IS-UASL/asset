package com.hitachi.droneroute.cmn.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Validatorクラスの単体テスト */
public class ValidatorTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    validator = new Validator();
  }

  /**
   * メソッド名: notNull<br>
   * 試験名: 文字列がnullまたは空でないこと<br>
   * 条件: nullまたは空の文字列を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testNotNullString() {
    // Null case
    validator.notNull("testField", (String) null);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Empty string case
    validator = new Validator();
    validator.notNull("testField", "");
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid string case
    validator = new Validator();
    validator.notNull("testField", "valid");
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: notNull<br>
   * 試験名: オブジェクトがnullでないこと<br>
   * 条件: nullのオブジェクトを入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testNotNullObject() {
    // Null case
    validator.notNull("testField", (Object) null);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid object case
    validator = new Validator();
    validator.notNull("testField", new Object());
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkLength<br>
   * 試験名: 文字列の長さが指定された最大長を超えないこと<br>
   * 条件: 最大長を超える文字列を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckLength() {
    // Exceeding length case
    validator.checkLength("testField", "exceedingLength", 5);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid length case
    validator = new Validator();
    validator.checkLength("testField", "valid", 5);
    assertDoesNotThrow(() -> validator.validate());

    // Null string case
    validator = new Validator();
    validator.checkLength("testField", null, 5);
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkLengthFixed<br>
   * 試験名: 文字列の長さが指定された固定長であること<br>
   * 条件: 固定長以外の文字列を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckLengthFixed() {
    // Not enough length case
    validator.checkLengthFixed("testField", "1234", 5);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Too many length case
    validator.checkLengthFixed("testField", "123456", 5);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid length case
    validator = new Validator();
    validator.checkLengthFixed("testField", "12345", 5);
    assertDoesNotThrow(() -> validator.validate());

    // Null string case
    validator = new Validator();
    validator.checkLengthFixed("testField", null, 5);
    assertDoesNotThrow(() -> validator.validate());

    // Empty string case
    validator = new Validator();
    validator.checkLengthFixed("testField", "", 5);
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkRange<br>
   * 試験名: 整数値が指定された範囲内にあること<br>
   * 条件: 範囲外の整数値を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckRangeInteger() {
    // Below minimum case
    validator.checkRange("testField", 1, 5, 10);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Above maximum case
    validator = new Validator();
    validator.checkRange("testField", 15, 5, 10);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // 最大値、最小値が不正
    validator = new Validator();
    validator.checkRange("testField", 15, 10, 5);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid range case
    validator = new Validator();
    validator.checkRange("testField", 7, 5, 10);
    assertDoesNotThrow(() -> validator.validate());

    // Null value case
    validator = new Validator();
    validator.checkRange("testField", (Integer) null, 5, 10);
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkRangeInteger<br>
   * 試験名: 整数値(文字列)が指定された範囲内にあること<br>
   * 条件: 範囲外の整数値を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckRangeInteger2() {
    // 数字以外を入力した場合
    validator.checkRangeInteger("testField", "abc", 5, 10);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Below minimum case
    validator.checkRangeInteger("testField", "1", 5, 10);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Above maximum case
    validator = new Validator();
    validator.checkRangeInteger("testField", "15", 5, 10);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // 最大値、最小値が不正
    validator = new Validator();
    validator.checkRangeInteger("testField", "15", 10, 5);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid range case
    validator = new Validator();
    validator.checkRangeInteger("testField", "7", 5, 10);
    assertDoesNotThrow(() -> validator.validate());

    // Null value case
    validator = new Validator();
    validator.checkRangeInteger("testField", (String) null, 5, 10);
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkRange<br>
   * 試験名: 長整数値が指定された範囲内にあること<br>
   * 条件: 範囲外の長整数値を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckRangeLong() {
    // Below minimum case
    validator.checkRange("testField", 1L, 5L, 10L);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Above maximum case
    validator = new Validator();
    validator.checkRange("testField", 15L, 5L, 10L);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // 最大値、最小値が不正
    validator = new Validator();
    validator.checkRange("testField", 15L, 10L, 5L);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid range case
    validator = new Validator();
    validator.checkRange("testField", 7L, 5L, 10L);
    assertDoesNotThrow(() -> validator.validate());

    // Null value case
    validator = new Validator();
    validator.checkRange("testField", (Long) null, 5L, 10L);
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkRange<br>
   * 試験名: 浮動小数点数値が指定された範囲内にあること<br>
   * 条件: 範囲外の浮動小数点数値を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckRangeDouble() {
    // Below minimum case
    validator.checkRange("testField", 1.0, 5.0, 10.0);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Above maximum case
    validator = new Validator();
    validator.checkRange("testField", 15.0, 5.0, 10.0);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // 最大値、最小値が不正
    validator = new Validator();
    validator.checkRange("testField", 15.0, 10.0, 5.0);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid range case
    validator = new Validator();
    validator.checkRange("testField", 7.0, 5.0, 10.0);
    assertDoesNotThrow(() -> validator.validate());

    // Null value case
    validator = new Validator();
    validator.checkRange("testField", null, 5.0, 10.0);
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkRange<br>
   * 試験名: BigInteger値が指定された範囲内にあること<br>
   * 条件: 範囲外のBigInteger値を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckRangeBigInteger() {
    // Below minimum case
    validator.checkRange(
        "testField", new BigInteger("1"), new BigInteger("5"), new BigInteger("10"));
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Above maximum case
    validator = new Validator();
    validator.checkRange(
        "testField", new BigInteger("15"), new BigInteger("5"), new BigInteger("10"));
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // 最大値、最小値が不正
    validator = new Validator();
    validator.checkRange(
        "testField", new BigInteger("15"), new BigInteger("10"), new BigInteger("5"));
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid range case
    validator = new Validator();
    validator.checkRange(
        "testField", new BigInteger("7"), new BigInteger("5"), new BigInteger("10"));
    assertDoesNotThrow(() -> validator.validate());

    // Null value case
    validator = new Validator();
    validator.checkRange("testField", (BigInteger) null, new BigInteger("5"), new BigInteger("10"));
    assertDoesNotThrow(() -> validator.validate());

    // Integer配列指定版: value.compareTo(minInt) < 0 のケース（Integer.MIN_VALUE未満）
    validator = new Validator();
    BigInteger belowIntMin = BigInteger.valueOf(Integer.MIN_VALUE).subtract(BigInteger.ONE);
    validator.checkRange("testField", belowIntMin, new Integer[] {1, 2, 3, 4, 5});
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Integer配列指定版: value.compareTo(maxInt) > 0 のケース（Integer.MAX_VALUE超過）
    validator = new Validator();
    BigInteger aboveIntMax = BigInteger.valueOf(Integer.MAX_VALUE).add(BigInteger.ONE);
    validator.checkRange("testField", aboveIntMax, new Integer[] {1, 2, 3, 4, 5});
    assertThrows(ValidationErrorException.class, () -> validator.validate());
  }

  /**
   * メソッド名: checkValue<br>
   * 試験名: 文字列が指定された正規表現パターンに一致すること<br>
   * 条件: パターンに一致しない文字列を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckValue() {
    Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    // Invalid pattern case
    validator.checkValue("testField", "12345", pattern);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // patterがnull
    validator.checkValue("testField", "12345", null);
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid pattern case
    validator = new Validator();
    validator.checkValue("testField", "valid", pattern);
    assertDoesNotThrow(() -> validator.validate());

    // Null value case
    validator = new Validator();
    validator.checkValue("testField", null, pattern);
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkUUID<br>
   * 試験名: 文字列がUUID形式であること<br>
   * 条件: UUID形式でない文字列を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckUUID() {
    // Invalid UUID case
    validator.checkUUID("testField", "invalid-uuid");
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid UUID case
    validator = new Validator();
    validator.checkUUID("testField", UUID.randomUUID().toString());
    assertDoesNotThrow(() -> validator.validate());

    // Null value case
    validator = new Validator();
    validator.checkUUID("testField", null);
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkDateTime<br>
   * 試験名: 文字列がZonedDateTime形式であること<br>
   * 条件: ZonedDateTime形式でない文字列を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testCheckDateTime() {
    // Invalid DateTime case
    validator.checkDateTime("testField", "invalid-datetime");
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Valid DateTime case
    validator = new Validator();
    validator.checkDateTime("testField", ZonedDateTime.now().toString());
    assertDoesNotThrow(() -> validator.validate());

    // Null value case
    validator = new Validator();
    validator.checkDateTime("testField", null);
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: compareDateTime<br>
   * 試験名: 2つの日時(文字列でZonedDateTime形式)の前後関係<br>
   * 条件: upper > lower, upper = lower, upper < lowerの日時を入力する<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン：正常系、異常系<br>
   */
  @Test
  void testCompareDateTime() {
    // upper > lower(valid case)
    validator.compareDateTime(
        "lower",
        "upper",
        ZonedDateTime.now().toString(),
        ZonedDateTime.now().plusHours(1).toString());
    assertDoesNotThrow(() -> validator.validate());

    // upper = lower (valid case)
    validator = new Validator();
    validator.compareDateTime(
        "lower", "upper", ZonedDateTime.now().toString(), ZonedDateTime.now().toString());
    assertDoesNotThrow(() -> validator.validate());

    // upper < lower
    validator = new Validator();
    validator.compareDateTime(
        "lower",
        "upper",
        ZonedDateTime.now().plusHours(1).toString(),
        ZonedDateTime.now().toString());
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // no check(invalid date format)
    validator = new Validator();
    validator.compareDateTime("lower", "upper", "invalid-datetime", "invalid-datetime");
    assertDoesNotThrow(() -> validator.validate());

    // no check
    validator = new Validator();
    validator.compareDateTime("lower", "upper", null, null);
    assertDoesNotThrow(() -> validator.validate());

    // no check
    validator = new Validator();
    validator.compareDateTime("lower", "upper", "invalid-datetime", null);
    assertDoesNotThrow(() -> validator.validate());

    // no check
    validator = new Validator();
    validator.compareDateTime("lower", "upper", null, "invalid-datetime");
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkRangeIntegerArray<br>
   * 試験名: チェック対象の整数値が整数配列内に含まれること<br>
   * 条件: 整数配列に含まれる整数値、および含まれない整数値を入力する<br>
   * 結果: 整数配列に含まれる整数値の場合は例外発生しない、<br>
   * 含まれない整数値の場合はValidationErrorExceptionが発生すること<br>
   * テストパターン：正常系、異常系<br>
   */
  @Test
  void testCheckRangeIntegerArray() {
    // Value not in range
    validator.checkRange("testField", 7, new Integer[] {1, 2, 3, 4, 5});
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Value in range
    validator = new Validator();
    validator.checkRange("testField", 3, new Integer[] {1, 2, 3, 4, 5});
    assertDoesNotThrow(() -> validator.validate());

    // Null value
    validator = new Validator();
    validator.checkRange("testField", (Integer) null, new Integer[] {1, 2, 3, 4, 5});
    assertDoesNotThrow(() -> validator.validate());
  }

  /**
   * メソッド名: checkRangeCommaDelimited<br>
   * 試験名: チェック対象の文字列がカンマ区切りの数字であること、かつ整数配列内に含まれること<br>
   * 条件: カンマ区切りの数字、数字が整数配列に含まれる、または含まれない文字列を入力する<br>
   * 結果: カンマ区切りの数字、かつ整数配列に含まれる整数値の場合は例外発生しない、<br>
   * 含まれない整数値の場合はValidationErrorExceptionが発生すること<br>
   * テストパターン：正常系、異常系<br>
   */
  @Test
  void testCheckRangeCommaDelimited() {
    // Value not in range
    validator.checkRange("testField", "7,8", new Integer[] {1, 2, 3, 4, 5});
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Value in range
    validator = new Validator();
    validator.checkRange("testField", "3,4", new Integer[] {1, 2, 3, 4, 5});
    assertDoesNotThrow(() -> validator.validate());

    // Invalid format
    validator = new Validator();
    validator.checkRange("testField", "3,a", new Integer[] {1, 2, 3, 4, 5});
    assertThrows(ValidationErrorException.class, () -> validator.validate());

    // Null value
    validator = new Validator();
    validator.checkRange("testField", (String) null, new Integer[] {1, 2, 3, 4, 5});
    assertDoesNotThrow(() -> validator.validate());
  }
}
