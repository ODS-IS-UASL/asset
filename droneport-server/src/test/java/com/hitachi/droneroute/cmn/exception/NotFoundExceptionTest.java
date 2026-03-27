package com.hitachi.droneroute.cmn.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** NotFoundExceptionクラスの単体テスト */
public class NotFoundExceptionTest {

  /**
   * メソッド名: NotFoundException<br>
   * 試験名: NotFoundExceptionのメッセージが正しく設定されること<br>
   * 条件: コンストラクタの引数でメッセージを設定し、getMessageで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testNotFoundExceptionMessage() {
    // ●準備
    String expectedMessage = "Resource not found";

    // ●実行
    NotFoundException exception = new NotFoundException(expectedMessage);

    // ●検証
    assertEquals(expectedMessage, exception.getMessage());
  }

  /**
   * メソッド名: NotFoundException<br>
   * 試験名: NotFoundExceptionのメッセージがnullでも例外が発生しないこと<br>
   * 条件: コンストラクタの引数でnullを設定し、getMessageで取得する。取得値がnullであること<br>
   * 結果: 取得値がnullである<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testNotFoundExceptionNullMessage() {
    // ●準備
    String expectedMessage = null;

    // ●実行
    NotFoundException exception = new NotFoundException(expectedMessage);

    // ●検証
    assertEquals(expectedMessage, exception.getMessage());
  }

  /**
   * メソッド名: NotFoundException<br>
   * 試験名: NotFoundExceptionのメッセージが空文字でも例外が発生しないこと<br>
   * 条件: コンストラクタの引数で空文字を設定し、getMessageで取得する。取得値が空文字であること<br>
   * 結果: 取得値が空文字である<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void testNotFoundExceptionEmptyMessage() {
    // ●準備
    String expectedMessage = "";

    // ●実行
    NotFoundException exception = new NotFoundException(expectedMessage);

    // ●検証
    assertEquals(expectedMessage, exception.getMessage());
  }
}
