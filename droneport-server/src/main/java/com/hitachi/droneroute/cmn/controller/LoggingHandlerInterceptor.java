package com.hitachi.droneroute.cmn.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/** コントローラーのメソッドの前処理、後処理を行うInterceptorクラス */
@Component
public class LoggingHandlerInterceptor implements HandlerInterceptor {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 受信したリクエストの情報をログ出力する。<br>
   * コントローラのメソッド実行前に呼び出される。
   *
   * @param request 受信したHTTPリクエスト
   * @param response HTTPレスポンス
   * @param handler ハンドラーオブジェクト
   * @return true: 続行, false: 処理中断
   * @throws Exception 例外が発生した場合にスローされる
   */
  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler)
      throws Exception {
    if (checkWriteLog(request.getRequestURI())) {
      String message =
          String.format(
              "[%s] %4s, %s",
              request.getRemoteAddr(), request.getMethod(), request.getRequestURI());
      logger.info(message);
    }
    return true;
  }

  /**
   * 受信したリクエストの情報と、コントローラ実行後のHTTPレスポンスをログ出力する。<br>
   * コントローラのメソッド実行後に呼び出される。
   *
   * @param request 受信したHTTPリクエスト
   * @param response HTTPレスポンス
   * @param handler ハンドラーオブジェクト
   * @param ex 発生した例外（存在する場合）
   * @throws Exception 例外が発生した場合にスローされる
   */
  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      @Nullable Exception ex)
      throws Exception {
    if (checkWriteLog(request.getRequestURI())) {
      String message =
          String.format(
              "[%s] %4s, %s, %03d",
              request.getRemoteAddr(),
              request.getMethod(),
              request.getRequestURI(),
              response.getStatus());
      logger.info(message);
    }
  }

  /**
   * リクエストURIからログ出力を判定する
   *
   * @param requestUri リクエストURI
   * @return true:ログ出力する, false:ログ出力しない
   */
  private boolean checkWriteLog(String requestUri) {
    return StringUtils.hasText(requestUri) && !requestUri.contains("awshealth");
  }
}
