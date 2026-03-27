package com.hitachi.droneroute.config.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.MimeType;

/**
 * RequestValidationFilterの単体テスト
 *
 * <p>AppScanヌルバイト（%00）脆弱性対策の動作確認
 */
@ExtendWith(MockitoExtension.class)
class RequestValidationFilterTest {

  private RequestValidationFilter filter;

  @Mock private FilterChain filterChain;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    filter = new RequestValidationFilter(objectMapper);
  }

  // ========================================
  // doFilterInternal() のテスト - クエリパラメータ検証
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: クエリパラメータのヌルバイト検証<br>
   * 条件: クエリパラメータにヌルバイトが含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testQueryParameter_ヌルバイト含む_BadRequestを返す() throws Exception {
    // GETリクエストのクエリパラメータにヌルバイトが含まれる場合
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setParameter("dronePortName", "\u0000test");
    request.setParameter("perPage", "10");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertTrue(response.getContentAsString().contains("クエリパラメータに不正な文字が含まれています"));
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: クエリパラメータの制御文字検証<br>
   * 条件: タブ・改行・CR以外の制御文字が含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testQueryParameter_制御文字含む_BadRequestを返す() throws Exception {
    // タブ・改行・CR以外の制御文字（0x01）
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setParameter("address", "test\u0001data");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertTrue(response.getContentAsString().contains("クエリパラメータに不正な文字が含まれています"));
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: クエリパラメータのDELETE文字検証<br>
   * 条件: DELETE文字が含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testQueryParameter_DELETE文字含む_BadRequestを返す() throws Exception {
    // DELETE文字（0x7F）
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setParameter("manufacturer", "test\u007Fdata");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: クエリパラメータの許可文字検証<br>
   * 条件: タブ・改行・CRが含まれる<br>
   * 結果: 正常に通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testQueryParameter_許可文字含む_正常に通過() throws Exception {
    // タブ・改行・CRは許可される
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setParameter("dronePortName", "Port\tName");
    request.setParameter("address", "Line1\nLine2\rLine3");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: クエリパラメータの正常動作検証<br>
   * 条件: 正常なクエリパラメータ<br>
   * 結果: 正常に通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testQueryParameter_正常_正常に通過() throws Exception {
    // 正常なクエリパラメータ
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setParameter("dronePortName", "TestPort");
    request.setParameter("address", "Tokyo");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  // ========================================
  // doFilterInternal() のテスト - JSONボディ検証
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: JSONボディのエスケープされたヌルバイト検証<br>
   * 条件: エスケープされたヌルバイトが含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testJsonBody_エスケープされたヌルバイト含む_BadRequestを返す() throws Exception {
    // JSONエスケープシーケンス \u0000（バイト配列には0x00なし、Jackson解析後に検出）
    String jsonBody =
        """
        {
          "modelInfos": [
            {"manufacturer": "foo\\u0000bar", "modelNumber": "DJI-Air3"}
          ],
          "isRequiredPayloadInfo": true
        }
        """;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(jsonBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertTrue(response.getContentAsString().contains("リクエストボディに不正な文字が含まれています"));
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: JSONボディのエスケープされた制御文字検証<br>
   * 条件: エスケープされた制御文字が含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testJsonBody_エスケープされた制御文字含む_BadRequestを返す() throws Exception {
    // 制御文字エスケープ \u0001
    String jsonBody =
        """
        {
          "modelInfos": [
            {"manufacturer": "test\\u0001data", "modelNumber": "Air3"}
          ]
        }
        """;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(jsonBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: JSONボディのネスト構造におけるエスケープヌルバイト検証<br>
   * 条件: ネストした構造にエスケープヌルバイトが含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testJsonBody_ネストしたエスケープヌルバイト含む_BadRequestを返す() throws Exception {
    // ネストしたJSON内のエスケープシーケンス
    String jsonBody =
        """
        {
          "modelInfos": [{
            "manufacturer": "DJI",
            "specs": {"camera": {"sensor": "test\\u0000data"}}
          }]
        }
        """;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(jsonBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: JSONボディの許可文字検証<br>
   * 条件: タブ・改行が含まれる<br>
   * 結果: 正常に通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testJsonBody_許可文字含む_正常に通過() throws Exception {
    // タブ・改行は許可される
    String jsonBody =
        """
        {
          "modelInfos": [{
            "manufacturer": "DJI\\tCorporation",
            "description": "Line1\\nLine2\\rLine3"
          }]
        }
        """;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(jsonBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: JSONボディの正常動作検証<br>
   * 条件: 正常なJSONボディ<br>
   * 結果: 正常に通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testJsonBody_正常_正常に通過() throws Exception {
    // 正常なJSONボディ
    String jsonBody =
        """
        {
          "modelInfos": [
            {"manufacturer": "DJI", "modelNumber": "DJI-Air3"}
          ],
          "isRequiredPayloadInfo": true
        }
        """;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(jsonBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  // ========================================
  // doFilterInternal() のテスト - バイト配列検証
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: バイト配列のヌルバイト検証<br>
   * 条件: ヌルバイトが含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testRawByteBody_ヌルバイト含む_BadRequestを返す() throws Exception {
    // JSON形式で制御文字を含む有効なJSON（\u0000エスケープシーケンス）
    String jsonBody = "{\"name\":\"test\\u0000data\"}";
    byte[] bodyBytes = jsonBody.getBytes();

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(bodyBytes);

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    verify(filterChain, never()).doFilter(any(), any());
  }

  // ========================================
  // doFilterInternal() のテスト - URLエンコード検証
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: URLエンコード形式でのヌルバイト検証<br>
   * 条件: ヌルバイトが含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testFormUrlEncoded_ヌルバイト含む_BadRequestを返す() throws Exception {
    // application/x-www-form-urlencodedでヌルバイトを含む
    String formData = "manufacturer=\u0000DJI&modelNumber=Air3";

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    request.setContent(formData.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    verify(filterChain, never()).doFilter(any(), any());
  }

  // ========================================
  // doFilterInternal() のテスト - HTTPメソッド別動作
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: GETメソッドでのボディ検証スキップ動作<br>
   * 条件: GETメソッド<br>
   * 結果: ボディ検証をスキップ<br>
   * テストパターン: 正常系
   */
  @Test
  void testGetMethod_ボディ検証スキップ() throws Exception {
    // GETメソッドはボディ検証をスキップ（クエリパラメータのみ検証）
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setParameter("dronePortName", "TestPort");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: HEADメソッドでのボディ検証スキップ動作<br>
   * 条件: HEADメソッド<br>
   * 結果: ボディ検証をスキップ<br>
   * テストパターン: 正常系
   */
  @Test
  void testHeadMethod_ボディ検証スキップ() throws Exception {
    // HEADメソッドもボディ検証をスキップ
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("HEAD");
    request.setParameter("dronePortName", "TestPort");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  // ========================================
  // doFilterInternal() のテスト - Content-Type別動作
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: multipart/form-data形式での検証スキップ動作<br>
   * 条件: multipart/form-data<br>
   * 結果: 検証をスキップ<br>
   * テストパターン: 正常系
   */
  @Test
  void testMultipartFormData_検証スキップ() throws Exception {
    // multipart/form-dataはバイナリデータなので検証スキップ
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("multipart/form-data");
    request.setContent("test\u0000data".getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: バイナリコンテンツでの検証スキップ動作<br>
   * 条件: バイナリコンテンツ<br>
   * 結果: 検証をスキップ<br>
   * テストパターン: 正常系
   */
  @Test
  void testBinaryContent_検証スキップ() throws Exception {
    // 画像などのバイナリコンテンツは検証スキップ
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.IMAGE_JPEG_VALUE);
    request.setContent("fake\u0000image\u0000data".getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: Content-Typeがnullの場合の検証スキップ動作<br>
   * 条件: Content-Typeが不明<br>
   * 結果: 検証をスキップ<br>
   * テストパターン: 正常系
   */
  @Test
  void testUnknownContentType_検証スキップ() throws Exception {
    // Content-Type不明の場合は検証スキップ（contentType == null の分岐カバレッジ）
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContent("test\u0000data".getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: Content-Typeが空文字列の場合の検証スキップ動作<br>
   * 条件: Content-Typeが空文字列<br>
   * 結果: 検証をスキップ<br>
   * テストパターン: 正常系
   */
  @Test
  void testEmptyContentType_検証スキップ() throws Exception {
    // Content-Typeが空文字列の場合は検証スキップ（contentType.isBlank() の分岐カバレッジ）
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("");
    request.setContent("test\u0000data".getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: Content-Typeが空白のみの場合の検証スキップ動作<br>
   * 条件: Content-Typeが空白のみ<br>
   * 結果: 検証をスキップ<br>
   * テストパターン: 正常系
   */
  @Test
  void testBlankContentType_検証スキップ() throws Exception {
    // Content-Typeが空白のみの場合は検証スキップ（contentType.isBlank() の分岐カバレッジ）
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("   ");
    request.setContent("test\u0000data".getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  // ========================================
  // doFilterInternal() のテスト - DoS対策
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: ボディサイズの上限超過検証<br>
   * 条件: ボディサイズが10MBを超過<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testBodySize_上限超過_BadRequestを返す() throws Exception {
    // 10MBを超えるボディ
    StringBuilder largeBody = new StringBuilder("{\"data\":\"");
    for (int i = 0; i < 10 * 1024 * 1024; i++) {
      largeBody.append("a");
    }
    largeBody.append("\"}");

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(largeBody.toString().getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertTrue(response.getContentAsString().contains("リクエストボディが大きすぎます"));
    verify(filterChain, never()).doFilter(any(), any());
  }

  // ========================================
  // doFilterInternal() のテスト - 複合パターン
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: クエリパラメータとボディの両方不正時の優先順位検証<br>
   * 条件: クエリとボディ両方が不正<br>
   * 結果: クエリが先に拒否される<br>
   * テストパターン: 異常系
   */
  @Test
  void testQueryAndBody_両方不正_クエリが先に拒否() throws Exception {
    // クエリパラメータとボディ両方NGの場合、クエリが先に拒否される
    String jsonBody = "{\"data\":\"foo\\u0000bar\"}";

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setParameter("test", "\u0000invalid");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(jsonBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertTrue(response.getContentAsString().contains("クエリパラメータに不正な文字が含まれています"));
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: クエリ不正かつボディ正常時の検証動作<br>
   * 条件: クエリが不正、ボディは正常<br>
   * 結果: クエリのみ拒否される<br>
   * テストパターン: 異常系
   */
  @Test
  void testQueryInvalid_ボディ正常_クエリのみ拒否() throws Exception {
    String jsonBody = "{\"data\":\"valid\"}";

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setParameter("filter", "test\u0000data");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(jsonBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertTrue(response.getContentAsString().contains("クエリパラメータに不正な文字が含まれています"));
    verify(filterChain, never()).doFilter(any(), any());
  }

  // ========================================
  // doFilterInternal() のテスト - エッジケース
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: 空のクエリパラメータの検証動作<br>
   * 条件: 空のクエリパラメータ<br>
   * 結果: 正常に通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testEmptyQueryParameter_正常に通過() throws Exception {
    // 空のクエリパラメータは検証通過
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");
    request.setParameter("dronePortName", "");
    request.setParameter("perPage", "10");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: null値のクエリパラメータの検証動作<br>
   * 条件: nullのクエリパラメータ値<br>
   * 結果: 正常に通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testNullQueryParameterValue_正常に通過() throws Exception {
    // クエリパラメータのvalueがnullの場合は検証スキップ（value != null の分岐カバレッジ）
    NullParameterValueRequest request = new NullParameterValueRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("GET");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: 空のJSONボディの検証動作<br>
   * 条件: 空のJSONボディ<br>
   * 結果: 正常に通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testEmptyJsonBody_正常に通過() throws Exception {
    // 空のJSONボディ
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent("{}".getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: 不正なJSON構文への対応動作<br>
   * 条件: 不正なJSON構文<br>
   * 結果: 検証は通過、後続で失敗<br>
   * テストパターン: 正常系
   */
  @Test
  void testInvalidJson_検証通過_後続で失敗() throws Exception {
    // 構文的に不正なJSONはこのフィルタでは検証せず後続に任せる
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent("{invalid json".getBytes());

    filter.doFilter(request, response, filterChain);

    // バリデーションは通過し、後続に渡される
    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  // ========================================
  // doFilterInternal() のテスト - 網羅性検証
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: MimeTypeパース失敗時の検証スキップ動作<br>
   * 条件: 不正なContent-Type形式<br>
   * 結果: 検証をスキップ<br>
   * テストパターン: 正常系
   */
  @Test
  void testMimeTypeParsing_不正なContentType_検証スキップ() throws Exception {
    // MimeType解析失敗時は検証をスキップして後続に渡す
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("invalid/content/type/format");
    request.setContent("test\u0000data".getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: 特定不能なContent-Typeでの検証スキップ動作<br>
   * 条件: テキスト系でもバイナリでもないContent-Type<br>
   * 結果: 検証をスキップ<br>
   * テストパターン: 正常系
   */
  @Test
  void testNonTextualContent_検証スキップ() throws Exception {
    // テキスト系でもバイナリでもないContent-Type（application/unknown等）
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("application/unknown");
    request.setContent("test\u0000data".getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: text/plain形式でのヌルバイト検証<br>
   * 条件: text/plainでヌルバイトが含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testTextPlain_ヌルバイト含む_BadRequestを返す() throws Exception {
    // JSON以外のテキスト形式（text/plain）でヌルバイトを含む
    String textBody = "test\u0000data";

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("text/plain");
    request.setContent(textBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: application/xml形式での制御文字検証<br>
   * 条件: application/xmlで制御文字が含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testApplicationXml_制御文字含む_BadRequestを返す() throws Exception {
    // JSON以外のテキスト形式（application/xml）で制御文字を含む
    String xmlBody = "<?xml version=\"1.0\"?><root>test\u0001data</root>";

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("application/xml");
    request.setContent(xmlBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: text/csv形式での正常動作検証<br>
   * 条件: text/csvで正常なデータ<br>
   * 結果: 正常に通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testTextCsv_正常_正常に通過() throws Exception {
    // JSON以外のテキスト形式（text/csv）で正常なデータ
    String csvBody = "name,value\ntest1,100\ntest2,200";

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("text/csv");
    request.setContent(csvBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: text/html形式での制御文字検証<br>
   * 条件: text/htmlで制御文字が含まれる<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testTextHtml_制御文字含む_BadRequestを返す() throws Exception {
    // text/*系のContent-Type（text/html）で制御文字を含む
    String htmlBody = "<html><body>test\u0000data</body></html>";

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("text/html");
    request.setContent(htmlBody.getBytes());

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    verify(filterChain, never()).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: 長さ0の空ボディへの対応動作<br>
   * 条件: 長さ0の空ボディ<br>
   * 結果: 検証は通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testJsonBody_空のボディ_検証通過() throws Exception {
    // 空のJSONボディ（長さ0）の場合、readTree失敗→catch→検証スキップ
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("application/json");
    request.setContent(new byte[0]);

    filter.doFilter(request, response, filterChain);

    // 検証は通過し、後続に渡される
    assertEquals(HttpStatus.OK.value(), response.getStatus());
    verify(filterChain, times(1)).doFilter(any(), any());
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: ボディ読み取り失敗時のエラーハンドリング<br>
   * 条件: ボディ読み取り失敗<br>
   * 結果: BadRequestを返す<br>
   * テストパターン: 異常系
   */
  @Test
  void testBodyReadFailure_読み取り失敗_BadRequestを返す() throws Exception {
    // カスタムWrapperでボディ読み取り失敗時のcatch処理
    FailingInputStreamRequest request = new FailingInputStreamRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    request.setMethod("POST");
    request.setContentType("application/json");

    filter.doFilter(request, response, filterChain);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    assertTrue(response.getContentAsString().contains("リクエストボディの読み取りに失敗しました"));
    verify(filterChain, never()).doFilter(any(), any());
  }

  // ========================================
  // validateJsonNode() のテスト
  // ========================================

  /**
   * メソッド名: validateJsonNode<br>
   * 試験名: nodeがnullの場合の処理動作<br>
   * 条件: nodeがnull<br>
   * 結果: nullを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testValidateJsonNode_nodeがnull_nullを返す() throws Exception {
    // nodeがnullの場合はnullを返す（防御的プログラミング）
    Method method =
        RequestValidationFilter.class.getDeclaredMethod(
            "validateJsonNode", JsonNode.class, String.class);
    method.setAccessible(true);
    String result = (String) method.invoke(filter, null, "$");
    assertNull(result);
  }

  /**
   * メソッド名: validateJsonNode<br>
   * 試験名: 空文字列の検証動作<br>
   * 条件: 空文字列<br>
   * 結果: 正常通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testValidateJsonNode_空文字列_正常通過() throws Exception {
    // 空文字列は制御文字を含まないため正常通過
    JsonNode node = objectMapper.readTree("{\"name\":\"\"}");
    Method method =
        RequestValidationFilter.class.getDeclaredMethod(
            "validateJsonNode", JsonNode.class, String.class);
    method.setAccessible(true);
    String result = (String) method.invoke(filter, node.get("name"), "$.name");
    assertNull(result);
  }

  /**
   * メソッド名: validateJsonNode<br>
   * 試験名: null値を含むJSONの検証動作<br>
   * 条件: null値を含む<br>
   * 結果: 正常通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testValidateJsonNode_null値を含む_正常通過() throws Exception {
    // JSONのnull値はスキップされる
    JsonNode node = objectMapper.readTree("{\"name\":null}");
    Method method =
        RequestValidationFilter.class.getDeclaredMethod(
            "validateJsonNode", JsonNode.class, String.class);
    method.setAccessible(true);
    String result = (String) method.invoke(filter, node.get("name"), "$.name");
    assertNull(result);
  }

  /**
   * メソッド名: validateJsonNode<br>
   * 試験名: 配列内のnull値の検証動作<br>
   * 条件: 配列内にnull値<br>
   * 結果: 正常通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testValidateJsonNode_配列内のnull_正常通過() throws Exception {
    // 配列内のnull値はスキップされる
    JsonNode node = objectMapper.readTree("[\"value1\", null, \"value2\"]");
    Method method =
        RequestValidationFilter.class.getDeclaredMethod(
            "validateJsonNode", JsonNode.class, String.class);
    method.setAccessible(true);
    String result = (String) method.invoke(filter, node, "$.items");
    assertNull(result);
  }

  /**
   * メソッド名: validateJsonNode<br>
   * 試験名: 空の配列の検証動作<br>
   * 条件: 空の配列<br>
   * 結果: 正常通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testValidateJsonNode_空の配列_正常通過() throws Exception {
    // 空の配列は正常通過
    JsonNode node = objectMapper.readTree("[]");
    Method method =
        RequestValidationFilter.class.getDeclaredMethod(
            "validateJsonNode", JsonNode.class, String.class);
    method.setAccessible(true);
    String result = (String) method.invoke(filter, node, "$.items");
    assertNull(result);
  }

  /**
   * メソッド名: validateJsonNode<br>
   * 試験名: 空のオブジェクトの検証動作<br>
   * 条件: 空のオブジェクト<br>
   * 結果: 正常通過<br>
   * テストパターン: 正常系
   */
  @Test
  void testValidateJsonNode_空のオブジェクト_正常通過() throws Exception {
    // 空のオブジェクトは正常通過
    JsonNode node = objectMapper.readTree("{}");
    Method method =
        RequestValidationFilter.class.getDeclaredMethod(
            "validateJsonNode", JsonNode.class, String.class);
    method.setAccessible(true);
    String result = (String) method.invoke(filter, node, "$");
    assertNull(result);
  }

  // ========================================
  // isJson()、isTextual()、isLikelyBinary() のテスト
  // ========================================

  /**
   * メソッド名: isJson<br>
   * 試験名: 標準的なapplication/jsonの判定動作<br>
   * 条件: 標準的なapplication/json<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsJson_標準JSON_trueを返す() throws Exception {
    Method method = RequestValidationFilter.class.getDeclaredMethod("isJson", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("application/json");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isJson<br>
   * 試験名: +jsonサフィックスを持つContent-Typeの判定動作<br>
   * 条件: +jsonサフィックスを持つContent-Type<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsJson_ベンダーJSON_trueを返す() throws Exception {
    // +json サフィックスを持つContent-TypeもJSON扱いされる
    Method method = RequestValidationFilter.class.getDeclaredMethod("isJson", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("application/vnd.api+json");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isTextual<br>
   * 試験名: +xmlサフィックスを持つContent-Typeの判定動作<br>
   * 条件: +xmlサフィックスを持つContent-Type<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsTextual_ベンダーXML_trueを返す() throws Exception {
    // +xml サフィックスを持つContent-Typeもテキスト扱いされる
    Method method = RequestValidationFilter.class.getDeclaredMethod("isTextual", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("application/vnd.api+xml");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isTextual<br>
   * 試験名: +jsonサフィックスを持つContent-Typeのテキスト判定動作<br>
   * 条件: +jsonサフィックスを持つContent-Type<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsTextual_ベンダーJSON_trueを返す() throws Exception {
    // +json サフィックスを持つContent-Typeもテキスト扱いされる
    Method method = RequestValidationFilter.class.getDeclaredMethod("isTextual", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("application/hal+json");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isLikelyBinary<br>
   * 試験名: application/octet-streamの判定動作<br>
   * 条件: application/octet-stream<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsLikelyBinary_OctetStream_trueを返す() throws Exception {
    Method method =
        RequestValidationFilter.class.getDeclaredMethod("isLikelyBinary", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("application/octet-stream");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isLikelyBinary<br>
   * 試験名: application/zipの判定動作<br>
   * 条件: application/zip<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsLikelyBinary_ZIP_trueを返す() throws Exception {
    Method method =
        RequestValidationFilter.class.getDeclaredMethod("isLikelyBinary", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("application/zip");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isLikelyBinary<br>
   * 試験名: application/pdfの判定動作<br>
   * 条件: application/pdf<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsLikelyBinary_PDF_trueを返す() throws Exception {
    Method method =
        RequestValidationFilter.class.getDeclaredMethod("isLikelyBinary", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("application/pdf");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isLikelyBinary<br>
   * 試験名: application/protobufの判定動作<br>
   * 条件: application/protobuf<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsLikelyBinary_Protobuf_trueを返す() throws Exception {
    Method method =
        RequestValidationFilter.class.getDeclaredMethod("isLikelyBinary", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("application/protobuf");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isLikelyBinary<br>
   * 試験名: application/msgpackの判定動作<br>
   * 条件: application/msgpack<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsLikelyBinary_MessagePack_trueを返す() throws Exception {
    Method method =
        RequestValidationFilter.class.getDeclaredMethod("isLikelyBinary", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("application/msgpack");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isLikelyBinary<br>
   * 試験名: audio/*のContent-Typeの判定動作<br>
   * 条件: audio/*のContent-Type<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsLikelyBinary_Audio_trueを返す() throws Exception {
    Method method =
        RequestValidationFilter.class.getDeclaredMethod("isLikelyBinary", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("audio/mpeg");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /**
   * メソッド名: isLikelyBinary<br>
   * 試験名: video/*のContent-Typeの判定動作<br>
   * 条件: video/*のContent-Type<br>
   * 結果: trueを返す<br>
   * テストパターン: 正常系
   */
  @Test
  void testIsLikelyBinary_Video_trueを返す() throws Exception {
    Method method =
        RequestValidationFilter.class.getDeclaredMethod("isLikelyBinary", MimeType.class);
    method.setAccessible(true);
    MimeType mimeType = MimeType.valueOf("video/mp4");
    boolean result = (boolean) method.invoke(filter, mimeType);
    assertTrue(result);
  }

  /** null値を持つクエリパラメータを返すテスト用リクエスト */
  private static class NullParameterValueRequest extends MockHttpServletRequest {
    @Override
    public Map<String, String[]> getParameterMap() {
      Map<String, String[]> params = new java.util.HashMap<>();
      params.put("dronePortName", new String[] {null});
      params.put("perPage", new String[] {"10"});
      return params;
    }
  }

  /** getInputStream()がIOExceptionをスローするServletInputStreamを返すテスト用リクエスト */
  private static class FailingInputStreamRequest extends MockHttpServletRequest {
    @Override
    public jakarta.servlet.ServletInputStream getInputStream() {
      return new jakarta.servlet.ServletInputStream() {
        @Override
        public int read() throws IOException {
          throw new IOException("Stream read error");
        }

        @Override
        public boolean isFinished() {
          return false;
        }

        @Override
        public boolean isReady() {
          return true;
        }

        @Override
        public void setReadListener(jakarta.servlet.ReadListener readListener) {}
      };
    }
  }
}
