package com.hitachi.droneroute.cmn.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** ValidationErrorExceptionクラスの単体テスト */
public class ValidationErrorExceptionTest {

  /**
   * メソッド名: ValidationErrorException<br>
   * 試験名: ValidationErrorExceptionのコンストラクタが正しく動作すること<br>
   * 条件: コンストラクタにメッセージを渡し、getMessage()で取得する<br>
   * 結果: 設定したメッセージが取得できる<br>
   * テストパターン：正常系<br>
   */
  @Test
  void constructor_shouldSetMessage() {
    // ●準備
    String expectedMessage = "Validation error occurred";

    // ●実行
    ValidationErrorException exception = new ValidationErrorException(expectedMessage);

    // ●検証
    assertEquals(expectedMessage, exception.getMessage());
  }

  /**
   * メソッド名: ValidationErrorException<br>
   * 試験名: ValidationErrorExceptionのコンストラクタがnullメッセージを正しく処理すること<br>
   * 条件: コンストラクタにnullを渡し、getMessage()で取得する<br>
   * 結果: nullが取得できる<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void constructor_shouldHandleNullMessage() {
    // ●準備
    String expectedMessage = null;

    // ●実行
    ValidationErrorException exception = new ValidationErrorException(expectedMessage);

    // ●検証
    assertNull(exception.getMessage());
  }

  /**
   * メソッド名: ValidationErrorException<br>
   * 試験名: ValidationErrorExceptionのコンストラクタが空文字メッセージを正しく処理すること<br>
   * 条件: コンストラクタに空文字を渡し、getMessage()で取得する<br>
   * 結果: 空文字が取得できる<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void constructor_shouldHandleEmptyMessage() {
    // ●準備
    String expectedMessage = "";

    // ●実行
    ValidationErrorException exception = new ValidationErrorException(expectedMessage);

    // ●検証
    assertEquals(expectedMessage, exception.getMessage());
  }

  /**
   * メソッド名: ValidationErrorException<br>
   * 試験名: ValidationErrorExceptionのコンストラクタがUnicode文字を正しく処理すること<br>
   * 条件: コンストラクタにUnicode文字を渡し、getMessage()で取得する<br>
   * 結果: Unicode文字が取得できる<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void constructor_shouldHandleUnicodeMessage() {
    // ●準備
    String expectedMessage = "エラーが発生しました";

    // ●実行
    ValidationErrorException exception = new ValidationErrorException(expectedMessage);

    // ●検証
    assertEquals(expectedMessage, exception.getMessage());
  }
}
