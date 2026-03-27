package com.hitachi.droneroute.config.filter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** CachedBodyHttpServletRequestの単体テスト */
@ExtendWith(MockitoExtension.class)
class CachedBodyHttpServletRequestTest {

  /**
   * メソッド名: getCachedBody<br>
   * 試験名: ボディのキャッシュと返却<br>
   * 条件: リクエストボディを持つMockリクエストを作成する<br>
   * 結果: キャッシュされたボディが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetCachedBody_ボディをキャッシュして返す() throws Exception {
    String body = "test body";
    HttpServletRequest mockRequest = createMockRequest(body);

    CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(mockRequest);

    assertArrayEquals(body.getBytes(), cachedRequest.getCachedBody());
  }

  /**
   * メソッド名: getInputStream<br>
   * 試験名: 複数回の読み込み可能性<br>
   * 条件: キャッシュされたリクエストから複数回ストリームを取得する<br>
   * 結果: 独立して複数回読み込みができる<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetInputStream_複数回読み込み可能() throws Exception {
    String body = "test content";
    HttpServletRequest mockRequest = createMockRequest(body);
    CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(mockRequest);

    // 1回目
    ServletInputStream stream1 = cachedRequest.getInputStream();
    assertEquals('t', stream1.read());
    assertEquals(body, "t" + new String(stream1.readAllBytes()));

    // 2回目（独立して読める）
    ServletInputStream stream2 = cachedRequest.getInputStream();
    assertEquals(body, new String(stream2.readAllBytes()));
  }

  /**
   * メソッド名: getReader<br>
   * 試験名: 複数回の読み込み可能性<br>
   * 条件: キャッシュされたリクエストから複数回Readerを取得する<br>
   * 結果: 独立して複数回読み込みができる<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetReader_複数回読み込み可能() throws Exception {
    String body = "line1\nline2";
    HttpServletRequest mockRequest = createMockRequest(body);
    CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(mockRequest);

    // 1回目
    BufferedReader reader1 = cachedRequest.getReader();
    assertEquals("line1", reader1.readLine());

    // 2回目（独立して読める）
    BufferedReader reader2 = cachedRequest.getReader();
    assertEquals("line1", reader2.readLine());
    assertEquals("line2", reader2.readLine());
  }

  /**
   * メソッド名: isFinished<br>
   * 試験名: 読み込み完了判定<br>
   * 条件: ストリームを最後まで読み込む<br>
   * 結果: 読み込み前はfalse、完了後はtrueが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testInputStream_isFinished_読み込み完了判定() throws Exception {
    String body = "ab";
    HttpServletRequest mockRequest = createMockRequest(body);
    CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(mockRequest);

    ServletInputStream inputStream = cachedRequest.getInputStream();

    assertFalse(inputStream.isFinished());
    inputStream.read();
    inputStream.read();
    assertTrue(inputStream.isFinished());
  }

  /**
   * メソッド名: isReady<br>
   * 試験名: 常にtrueを返却<br>
   * 条件: ストリームを取得する<br>
   * 結果: trueが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testInputStream_isReady_常にtrue() throws Exception {
    String body = "test";
    HttpServletRequest mockRequest = createMockRequest(body);
    CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(mockRequest);

    ServletInputStream inputStream = cachedRequest.getInputStream();

    assertTrue(inputStream.isReady());
  }

  /**
   * メソッド名: setReadListener<br>
   * 試験名: UnsupportedOperationExceptionのスロー<br>
   * 条件: setReadListenerを呼び出す<br>
   * 結果: UnsupportedOperationExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testInputStream_setReadListener_UnsupportedOperationException() throws Exception {
    String body = "test";
    HttpServletRequest mockRequest = createMockRequest(body);
    CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(mockRequest);

    ServletInputStream inputStream = cachedRequest.getInputStream();

    assertThrows(UnsupportedOperationException.class, () -> inputStream.setReadListener(null));
  }

  /** モックリクエストを作成 */
  private HttpServletRequest createMockRequest(String body) throws IOException {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    byte[] bodyBytes = body.getBytes();
    ServletInputStream inputStream =
        new ServletInputStream() {
          private final ByteArrayInputStream byteStream = new ByteArrayInputStream(bodyBytes);

          @Override
          public boolean isFinished() {
            return byteStream.available() == 0;
          }

          @Override
          public boolean isReady() {
            return true;
          }

          @Override
          public void setReadListener(jakarta.servlet.ReadListener readListener) {}

          @Override
          public int read() {
            return byteStream.read();
          }
        };
    when(mockRequest.getInputStream()).thenReturn(inputStream);
    return mockRequest;
  }
}
