package com.hitachi.droneroute.config.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

/** SecurityExceptionHandlerの単体テスト */
@ExtendWith(MockitoExtension.class)
class SecurityExceptionHandlerTest {

  @InjectMocks private SecurityExceptionHandler securityExceptionHandler;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  private StringWriter stringWriter;
  private PrintWriter printWriter;
  private ObjectMapper objectMapper;
  private Logger logger;
  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() throws Exception {
    stringWriter = new StringWriter();
    printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);
    objectMapper = new ObjectMapper();

    // ログキャプチャの設定
    logger = (Logger) LoggerFactory.getLogger(SecurityExceptionHandler.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @AfterEach
  void tearDown() {
    // ログキャプチャのクリーンアップ
    logger.detachAppender(listAppender);
  }

  /**
   * メソッド名: handle<br>
   * 試験名: AccessDeniedException発生時の403レスポンス返却動作検証<br>
   * 条件: AccessDeniedExceptionがスローされる<br>
   * 結果: HTTPステータス403が返却され、エラーメッセージがJSONで出力される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testHandle_異常系_AccessDeniedExceptionで403を返却() throws Exception {
    // Arrange
    String errorMessage = "自システムの事業者のみアクセス可能です";
    AccessDeniedException exception = new AccessDeniedException(errorMessage);

    // Act
    securityExceptionHandler.handle(request, response, exception);

    // Assert - レスポンス検証
    verify(response).setStatus(HttpStatus.FORBIDDEN.value());
    verify(response).setContentType("application/json;charset=UTF-8");

    printWriter.flush();
    String jsonResponse = stringWriter.toString();
    JsonNode jsonNode = objectMapper.readTree(jsonResponse);

    assertTrue(jsonNode.has("errorDetail"), "errorDetailフィールドが存在すること");
    assertEquals(errorMessage, jsonNode.get("errorDetail").asText(), "エラーメッセージが正しいこと");

    // Assert - ログ検証
    List<ILoggingEvent> logsList = listAppender.list;
    assertEquals(1, logsList.size(), "ログが1件出力されること");
    assertEquals(Level.WARN, logsList.get(0).getLevel(), "ログレベルがWARNであること");
    assertTrue(logsList.get(0).getFormattedMessage().contains("アクセス拒否"), "ログメッセージに「アクセス拒否」が含まれること");
    assertTrue(
        logsList.get(0).getFormattedMessage().contains(errorMessage), "ログメッセージにエラーメッセージが含まれること");
  }

  /**
   * メソッド名: commence<br>
   * 試験名: AuthenticationException単独発生時の401レスポンス返却動作検証<br>
   * 条件: cause（原因例外）を持たないAuthenticationExceptionがスローされる<br>
   * 結果: HTTPステータス401が返却され、固定エラーメッセージがJSONで出力される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testCommence_異常系_AuthenticationExceptionでcauseなしで401を返却() throws Exception {
    // Arrange
    String errorMessage = "認証処理エラー";
    AuthenticationException exception = new AuthenticationException(errorMessage) {};

    // Act
    securityExceptionHandler.commence(request, response, exception);

    // Assert - レスポンス検証
    verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
    verify(response).setContentType("application/json;charset=UTF-8");

    printWriter.flush();
    String jsonResponse = stringWriter.toString();
    JsonNode jsonNode = objectMapper.readTree(jsonResponse);

    assertTrue(jsonNode.has("errorDetail"), "errorDetailフィールドが存在すること");
    assertEquals("認証に失敗しました。", jsonNode.get("errorDetail").asText(), "エラーメッセージが固定値であること");

    // Assert - ログ検証
    List<ILoggingEvent> logsList = listAppender.list;
    assertEquals(1, logsList.size(), "ログが1件出力されること");
    assertEquals(Level.WARN, logsList.get(0).getLevel(), "ログレベルがWARNであること");
    assertTrue(logsList.get(0).getFormattedMessage().contains("認証失敗"), "ログメッセージに「認証失敗」が含まれること");
    assertTrue(
        logsList.get(0).getFormattedMessage().contains(errorMessage), "ログメッセージに元のエラーメッセージが含まれること");
  }

  /**
   * メソッド名: commence<br>
   * 試験名: ServiceErrorExceptionを原因とするAuthenticationException発生時の500レスポンス返却動作検証<br>
   * 条件: ServiceErrorExceptionをcauseとして持つAuthenticationExceptionがスローされる<br>
   * 結果: HTTPステータス500が返却され、エラーメッセージがJSONで出力される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testCommence_異常系_AuthenticationExceptionでServiceErrorExceptionが原因で500を返却() throws Exception {
    // Arrange
    String errorMessage = "ユーザー情報取得に失敗しました";
    ServiceErrorException cause = new ServiceErrorException("外部API呼び出しエラー");
    AuthenticationException exception = new AuthenticationException(errorMessage, cause) {};

    // Act
    securityExceptionHandler.commence(request, response, exception);

    // Assert - レスポンス検証
    verify(response).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    verify(response).setContentType("application/json;charset=UTF-8");

    printWriter.flush();
    String jsonResponse = stringWriter.toString();
    JsonNode jsonNode = objectMapper.readTree(jsonResponse);

    assertTrue(jsonNode.has("errorDetail"), "errorDetailフィールドが存在すること");
    assertEquals(errorMessage, jsonNode.get("errorDetail").asText(), "エラーメッセージが正しいこと");

    // Assert - ログ検証
    List<ILoggingEvent> logsList = listAppender.list;
    assertEquals(1, logsList.size(), "ログが1件出力されること");
    assertEquals(Level.WARN, logsList.get(0).getLevel(), "ログレベルがWARNであること");
    assertTrue(
        logsList.get(0).getFormattedMessage().contains("ServiceErrorException発生"),
        "ログメッセージに「ServiceErrorException発生」が含まれること");
    assertTrue(
        logsList.get(0).getFormattedMessage().contains(errorMessage), "ログメッセージにエラーメッセージが含まれること");
  }

  /**
   * メソッド名: commence<br>
   * 試験名: 想定外例外を原因とするAuthenticationException発生時の500レスポンス返却動作検証<br>
   * 条件: RuntimeExceptionをcauseとして持つAuthenticationExceptionがスローされる<br>
   * 結果: HTTPステータス500が返却され、エラーメッセージがJSONで出力され、ERRORログが記録される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testCommence_異常系_AuthenticationExceptionで想定外エラーが原因で500を返却() throws Exception {
    // Arrange
    String errorMessage = "想定外エラー";
    RuntimeException cause = new RuntimeException("予期しないエラーが発生しました");
    AuthenticationException exception = new AuthenticationException(errorMessage, cause) {};

    // Act
    securityExceptionHandler.commence(request, response, exception);

    // Assert - レスポンス検証
    verify(response).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    verify(response).setContentType("application/json;charset=UTF-8");

    printWriter.flush();
    String jsonResponse = stringWriter.toString();
    JsonNode jsonNode = objectMapper.readTree(jsonResponse);

    assertTrue(jsonNode.has("errorDetail"), "errorDetailフィールドが存在すること");
    assertEquals(errorMessage, jsonNode.get("errorDetail").asText(), "エラーメッセージが正しいこと");

    // Assert - ログ検証
    List<ILoggingEvent> logsList = listAppender.list;
    assertEquals(1, logsList.size(), "ログが1件出力されること");
    assertEquals(Level.ERROR, logsList.get(0).getLevel(), "ログレベルがERRORであること");
    assertTrue(
        logsList.get(0).getFormattedMessage().contains("想定外エラー発生"), "ログメッセージに「想定外エラー発生」が含まれること");
  }
}
