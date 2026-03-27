package com.hitachi.droneroute.cmn.exception;

/** 入力パラメータエラー発生時の例外クラス */
public class ValidationErrorException extends RuntimeException {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ
   *
   * @param message エラーメッセージ
   */
  public ValidationErrorException(String message) {
    super(message);
  }
}
