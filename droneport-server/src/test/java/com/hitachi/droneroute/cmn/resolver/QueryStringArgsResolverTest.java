package com.hitachi.droneroute.cmn.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.request.ServletWebRequest;

/** QueryStringArgsResolverクラスの単体テスト */
@ActiveProfiles("test")
public class QueryStringArgsResolverTest {

  private QueryStringArgsResolver resolver = new QueryStringArgsResolver(new ObjectMapper());

  @MockBean HttpServletRequest servletReq;

  @Getter
  @Setter
  static class DummyClass {
    private String param;
  }

  static class DummyController {
    public void handle(@QueryStringArgs DummyClass dummyParam) {}
  }

  /**
   * メソッド名: resolveArgument<br>
   * 試験名: クエリパラメータが対象のクラスにマッピングされること<br>
   * 条件: 正常なクエリパラメータを渡す<br>
   * 結果: 対象のクラスにパラメータが正しくマッピングされる<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testResolver() throws Exception {
    Method method = DummyController.class.getMethod("handle", DummyClass.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    ServletWebRequest webRequest = new ServletWebRequest(req, res);

    String value = "value";
    req.setParameter("param", value);

    Object result = resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertTrue(result instanceof DummyClass);
    DummyClass dc = (DummyClass) result;
    assertEquals(value, dc.getParam());
  }

  /**
   * メソッド名: resolveArgument<br>
   * 試験名: クエリパラメータが空の場合に正常終了すること<br>
   * 条件: 空の値のクエリパラメータを渡す<br>
   * 結果: 正常終了する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testResolver_emptyValue() throws Exception {
    Method method = DummyController.class.getMethod("handle", DummyClass.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    ServletWebRequest webRequest = new ServletWebRequest(req, res);

    req.setParameter("param", new String[0]);

    Object result = resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertTrue(result instanceof DummyClass);
    DummyClass dc = (DummyClass) result;
    assertNull(dc.getParam());
  }

  /**
   * メソッド名: resolveArgument<br>
   * 試験名: クエリパラメータが空の場合に正常終了すること<br>
   * 条件: nullのクエリパラメータを渡す<br>
   * 結果: 正常終了する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testResolver_nullValue() throws Exception {
    Method method = DummyController.class.getMethod("handle", DummyClass.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    MockHttpServletRequest req = new MockHttpServletRequest();
    MockHttpServletResponse res = new MockHttpServletResponse();
    ServletWebRequest webRequest = new ServletWebRequest(req, res);

    String[] params = null;
    req.setParameter("param", params);

    Object result = resolver.resolveArgument(parameter, null, webRequest, null);

    assertNotNull(result);
    assertTrue(result instanceof DummyClass);
    DummyClass dc = (DummyClass) result;
    assertNull(dc.getParam());
  }
}
