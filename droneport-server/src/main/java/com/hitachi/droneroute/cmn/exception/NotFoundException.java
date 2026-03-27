package com.hitachi.droneroute.cmn.exception;

/** 検索結果が該当なしの場合の例外クラス */
public class NotFoundException extends RuntimeException {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ
   *
   * @param message エラーメッセージ
   */
  public NotFoundException(String message) {
    super(message);
  }
}
