package com.hitachi.droneroute.cmn.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * コントローラーのメソッドの前処理、後処理を行うInterceptorクラス
 * @author Hiroshi Toyoda
 *
 */
@Component
public class LoggingHandlerInterceptor implements HandlerInterceptor {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 受信したリクエストの情報をログ出力する。<br>
	 * コントローラのメソッド実行前に呼び出される。
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (checkWriteLog(request.getRequestURI())) {
			String message = String.format("[%s] %4s, %s", 
					request.getRemoteAddr(), request.getMethod(), request.getRequestURI());
			logger.info(message);
		}
		return true;
	}

	/**
	 * 受信したリクエストの情報と、コントローラ実行後のHTTPレスポンスをログ出力する。<br>
	 * コントローラのメソッド実行後に呼び出される。
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (checkWriteLog(request.getRequestURI())) {
			String message = String.format("[%s] %4s, %s, %03d", 
					request.getRemoteAddr(), request.getMethod(), request.getRequestURI(), response.getStatus());
			logger.info(message);
		}
	}

	/**
	 * リクエストURIからログ出力を判定する
	 * @param requestUri リクエストURI
	 * @return true:ログ出力する, false:ログ出力しない
	 */
	private boolean checkWriteLog(String requestUri) {
		return StringUtils.hasText(requestUri) && !requestUri.contains("awshealth");
	}
}
