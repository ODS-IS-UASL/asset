package com.hitachi.droneroute.cmn.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** AppErrorExceptionクラスの単体テスト */
public class AppErrorExceptionTest {

  /**
   * メソッド名: AppErrorException<br>
   * 試験名: AppErrorExceptionのメッセージが正しく設定されること<br>
   * 条件: コンストラクタの引数でメッセージを設定し、getMessageで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testAppErrorExceptionMessage() {
    // ●準備
    String expectedMessage = "Test error message";

    // ●実行
    AppErrorException exception = new AppErrorException(expectedMessage);

    // ●検証
    assertEquals(expectedMessage, exception.getMessage());
  }

  /**
   * メソッド名: AppErrorException<br>
   * 試験名: AppErrorExceptionのメッセージがnullの場合に正しく処理されること<br>
   * 条件: コンストラクタの引数でnullを設定し、getMessageで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致する<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testAppErrorExceptionNullMessage() {
    // ●準備
    String expectedMessage = null;

    // ●実行
    AppErrorException exception = new AppErrorException(expectedMessage);

    // ●検証
    assertEquals(expectedMessage, exception.getMessage());
  }

  /**
   * メソッド名: AppErrorException<br>
   * 試験名: AppErrorExceptionのメッセージが空文字の場合に正しく処理されること<br>
   * 条件: コンストラクタの引数で空文字を設定し、getMessageで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致する<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testAppErrorExceptionEmptyMessage() {
    // ●準備
    String expectedMessage = "";

    // ●実行
    AppErrorException exception = new AppErrorException(expectedMessage);

    // ●検証
    assertEquals(expectedMessage, exception.getMessage());
  }

  /**
   * メソッド名: AppErrorException<br>
   * 試験名: AppErrorExceptionが例外をスローすること<br>
   * 条件: コンストラクタの引数でメッセージを設定し、例外がスローされること<br>
   * 結果: 例外がスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testAppErrorExceptionWithException() {
    // ●準備
    String expectedMessage = "Test error message";

    // ●実行 & 検証
    assertThrows(
        AppErrorException.class,
        () -> {
          throw new AppErrorException(expectedMessage);
        });
  }
}
