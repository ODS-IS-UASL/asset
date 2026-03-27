package com.hitachi.droneroute.cmn.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/** LoggingHandlerInterceptorクラスの単体テスト */
public class LoggingHandlerInterceptorTest {

  /**
   * メソッド名: preHandle<br>
   * 試験名: 受信したリクエストの情報をログ出力が正常終了する<br>
   * 条件: 正常な引数を渡す<br>
   * 結果: 正常終了<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testPreHandleAndCheckWriteLog() throws Exception {
    LoggingHandlerInterceptor interceptor = new LoggingHandlerInterceptor();

    MockHttpServletRequest req = new MockHttpServletRequest();
    req.setRequestURI("awshealth");
    MockHttpServletResponse res = new MockHttpServletResponse();
    Object handler = new Object();

    boolean result = interceptor.preHandle(req, res, handler);
    assertEquals(result, true);
  }

  /**
   * メソッド名: preHandle<br>
   * 試験名: 受信したリクエストの情報をログ出力が正常終了する<br>
   * 条件: 正常な引数を渡す<br>
   * 結果: 正常終了<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testPreHandleAndCheckWriteLog_URI_null() throws Exception {
    LoggingHandlerInterceptor interceptor = new LoggingHandlerInterceptor();

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    Object handler = new Object();

    boolean result = interceptor.preHandle(req, res, handler);
    assertEquals(result, true);
  }
}
