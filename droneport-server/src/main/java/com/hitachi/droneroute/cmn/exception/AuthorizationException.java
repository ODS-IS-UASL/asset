package com.hitachi.droneroute.cmn.exception;

import org.springframework.security.access.AccessDeniedException;

/**
 * 認可エラー例外クラス
 *
 * <p>Spring SecurityのAccessDeniedExceptionを継承し、 自前のロジックで発生した認可エラーを表す。 Spring
 * Security内部で発生するAccessDeniedExceptionと区別するために使用する。
 */
public class AuthorizationException extends AccessDeniedException {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /**
   * コンストラクタ
   *
   * @param message エラーメッセージ
   */
  public AuthorizationException(String message) {
    super(message);
  }
}
