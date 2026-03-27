package com.hitachi.droneroute.cmn.exception;

/** アプリケーション内でエラー発生時の例外クラス */
public class AppErrorException extends RuntimeException {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ
   *
   * @param message エラーメッセージ
   */
  public AppErrorException(String message) {
    super(message);
  }
}
