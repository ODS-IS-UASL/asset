package com.hitachi.droneroute.cmn.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** ServiceErrorExceptionクラスの単体テスト */
public class ServiceErrorExceptionTest {

  /**
   * メソッド名: ServiceErrorException<br>
   * 試験名: メッセージのみの例外生成<br>
   * 条件: メッセージを引数にして例外を生成する<br>
   * 結果: 例外のメッセージが設定したメッセージと一致する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testServiceErrorExceptionWithMessage() {
    // 入力値の設定
    String expectedMessage = "Test message";

    // 実行
    ServiceErrorException exception = new ServiceErrorException(expectedMessage);

    // 検証
    assertEquals(expectedMessage, exception.getMessage());
    assertNull(exception.getCause());
  }

  /**
   * メソッド名: ServiceErrorException<br>
   * 試験名: メッセージと原因の例外生成<br>
   * 条件: メッセージと原因を引数にして例外を生成する<br>
   * 結果: 例外のメッセージと原因が設定したものと一致する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testServiceErrorExceptionWithMessageAndCause() {
    // 入力値の設定
    String expectedMessage = "Test message";
    Throwable expectedCause = new Throwable("Cause");

    // 実行
    ServiceErrorException exception = new ServiceErrorException(expectedMessage, expectedCause);

    // 検証
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(expectedCause, exception.getCause());
  }

  /**
   * メソッド名: ServiceErrorException<br>
   * 試験名: メッセージがnullの例外生成<br>
   * 条件: メッセージにnullを設定して例外を生成する<br>
   * 結果: 例外のメッセージがnullである<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testServiceErrorExceptionWithNullMessage() {
    // 入力値の設定
    String expectedMessage = null;

    // 実行
    ServiceErrorException exception = new ServiceErrorException(expectedMessage);

    // 検証
    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  /**
   * メソッド名: ServiceErrorException<br>
   * 試験名: 原因がnullの例外生成<br>
   * 条件: 原因にnullを設定して例外を生成する<br>
   * 結果: 例外の原因がnullである<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testServiceErrorExceptionWithNullCause() {
    // 入力値の設定
    String expectedMessage = "Test message";
    Throwable expectedCause = null;

    // 実行
    ServiceErrorException exception = new ServiceErrorException(expectedMessage, expectedCause);

    // 検証
    assertEquals(expectedMessage, exception.getMessage());
    assertNull(exception.getCause());
  }
}
