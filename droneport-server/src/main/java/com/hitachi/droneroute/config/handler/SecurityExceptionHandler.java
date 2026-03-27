package com.hitachi.droneroute.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.dto.ErrorResponse;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Spring Securityフィルタレベルの例外ハンドラー
 *
 * <p>AccessDeniedHandlerとAuthenticationEntryPointを実装し、 フィルタで発生した例外をプロジェクト標準のErrorResponse形式で処理する。
 */
@Component
public class SecurityExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * アクセス拒否例外のハンドリング（403 Forbidden）
   *
   * @param request HTTPリクエスト
   * @param response HTTPレスポンス
   * @param accessDeniedException アクセス拒否例外
   * @throws IOException フィルタ処理中に入出力例外が発生した場合にスローされる例外
   * @throws ServletException フィルタ処理中にサーブレット例外が発生した場合にスローされる例外
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {

    logger.warn("アクセス拒否: {}", accessDeniedException.getMessage());
    sendErrorResponse(response, accessDeniedException.getMessage(), HttpStatus.FORBIDDEN);
  }

  /**
   * 認証例外のハンドリング（401 Unauthorized または 500 Internal Server Error）
   *
   * <p>例外の種類によってステータスコードとログレベルを分ける：
   *
   * <ul>
   *   <li>ServiceErrorException: 500、warnログ（外部API障害、想定内）
   *   <li>causeがある想定外エラー: 500、errorログ（バグの可能性）
   *   <li>causeなし（JWT検証失敗など）: 401、warnログ（認証失敗、よくある）
   * </ul>
   *
   * @param request HTTPリクエスト
   * @param response HTTPレスポンス
   * @param authException 認証例外
   * @throws IOException フィルタ処理中に入出力例外が発生した場合にスローされる例外
   * @throws ServletException フィルタ処理中にサーブレット例外が発生した場合にスローされる例外
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {

    Throwable cause = authException.getCause();
    HttpStatus status;
    String responseMessage;

    if (cause instanceof ServiceErrorException) {
      // ServiceErrorException: 500、簡潔なログ（内部ログで詳細は出ている）
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      responseMessage = authException.getMessage();
      logger.warn("ServiceErrorException発生: {}", responseMessage);
    } else if (cause != null) {
      // causeがある想定外エラー: 500、errorログ（スタックトレース付き）
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      responseMessage = authException.getMessage();
      logger.error("想定外エラー発生", authException);
    } else {
      // causeなし（JWT検証失敗など）: 401、固定メッセージ
      status = HttpStatus.UNAUTHORIZED;
      responseMessage = "認証に失敗しました。";
      logger.warn("認証失敗: {}", authException.getMessage());
    }

    sendErrorResponse(response, responseMessage, status);
  }

  /**
   * エラーレスポンスを送信
   *
   * @param response HTTPレスポンス
   * @param message エラーメッセージ
   * @param status HTTPステータス
   * @throws IOException フィルタ処理中に入出力例外が発生した場合にスローされる例外
   */
  private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status)
      throws IOException {
    ErrorResponse errorResponse = new ErrorResponse(message);
    response.setStatus(status.value());
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
