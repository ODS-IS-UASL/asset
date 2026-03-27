package com.hitachi.droneroute.cmn.exception;

/** サービスクラスでエラー発生時の例外クラス */
public class ServiceErrorException extends RuntimeException {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ
   *
   * @param message エラーメッセージ
   */
  public ServiceErrorException(String message) {
    super(message);
  }

  /**
   * コンストラクタ
   *
   * @param message エラーメッセージ
   * @param cause 原因となった例外
   */
  public ServiceErrorException(String message, Throwable cause) {
    super(message, cause);
  }
}
