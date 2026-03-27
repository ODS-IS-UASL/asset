package com.hitachi.droneroute.config.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.cmn.constants.CommonConstants;
import com.hitachi.droneroute.cmn.exception.AuthorizationException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.SecurityConfig;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import jakarta.servlet.FilterChain;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;

/** BatchApiKeyAuthFilterの単体テスト */
@ExtendWith(MockitoExtension.class)
class BatchApiKeyAuthFilterTest {

  private BatchApiKeyAuthFilter filter;

  @Mock private SystemSettings systemSettings;

  @Mock private FilterChain filterChain;

  private SecurityConfig securityConfig;
  private RequestMatcher batchApiMatcher;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  // 実際の運用設定値を使用
  // API-Key元文字列
  private static final String TEST_API_KEY =
      "gUjT3oUSUg14XETFU4Q8MhX5zGN6rlReZRHqeSmCz4F4kAaMmFKN12E81g8nO4-u";
  // SHA-256ハッシュ値: a7c04a2c3ad99d3370060e006951b5c8541dfbdb1cb12c6f728a364948f90847
  private static final String TEST_API_KEY_HASH = calculateHash(TEST_API_KEY);
  private static final String INVALID_API_KEY = "invalid-api-key";

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    SecurityContextHolder.clearContext();

    // SecurityConfigの実態を使用してbatchApiMatcherを生成
    securityConfig = new SecurityConfig();
    when(systemSettings.getStringValueArray(
            CommonConstants.BATCH_SETTINGS, CommonConstants.ASSET_BATCH_API_API_LIST))
        .thenReturn(
            new String[] {
              "/asset/api/droneport/info/publicDataExtract",
              "/asset/api/droneport/reserve/publicDataExtract",
              "/asset/api/aircraft/info/publicDataExtract",
              "/asset/api/aircraft/reserve/publicDataExtract"
            });
    batchApiMatcher = securityConfig.batchApiMatcher(systemSettings);

    // BatchApiKeyAuthFilterを手動でインスタンス化
    filter = new BatchApiKeyAuthFilter(systemSettings, batchApiMatcher);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  /** SHA-256ハッシュ値を計算するヘルパーメソッド */
  private static String calculateHash(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance(CommonConstants.HASH_ALGORITHM_SHA256);
      byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // ========================================
  // shouldNotFilter() のテスト - 境界値・エッジケース
  // ========================================

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: ヘルスチェックパスのフィルタスキップ動作<br>
   * 条件: ヘルスチェックパス(/awshealth/check.html)を設定する<br>
   * 結果: trueが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testShouldNotFilter_ヘルスチェックパスはスキップ対象() {
    // Arrange
    request.setServletPath("/awshealth/check.html");

    // Act
    boolean result = filter.shouldNotFilter(request);

    // Assert
    assertTrue(result, "ヘルスチェックパスはフィルタをスキップする（true）");
  }

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: 通常のAPIパスのフィルタスキップ動作<br>
   * 条件: 通常のAPIパス(/asset/api/droneport/info)を設定する<br>
   * 結果: trueが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testShouldNotFilter_通常のAPIパスはスキップ対象() {
    // Arrange
    request.setServletPath("/asset/api/droneport/info");

    // Act
    boolean result = filter.shouldNotFilter(request);

    // Assert
    assertTrue(result, "通常のAPIパスはフィルタをスキップする（true）");
  }

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: バッチAPI設定リスト1番目との完全一致時のフィルタ実行動作<br>
   * 条件: バッチAPI設定リスト1番目のパス(/asset/api/droneport/info/publicDataExtract)を設定する<br>
   * 結果: falseが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testShouldNotFilter_バッチAPI設定リスト1番目と完全一致でフィルタ実行() {
    // Arrange: /asset/api/droneport/info/publicDataExtract
    request.setServletPath("/asset/api/droneport/info/publicDataExtract");

    // Act
    boolean result = filter.shouldNotFilter(request);

    // Assert
    assertFalse(result, "バッチAPI設定リスト1番目と完全一致の場合はフィルタを実行する（false）");
  }

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: バッチAPI設定リスト最後との完全一致時のフィルタ実行動作<br>
   * 条件: バッチAPI設定リスト最後のパス(/asset/api/aircraft/reserve/publicDataExtract)を設定する<br>
   * 結果: falseが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testShouldNotFilter_バッチAPI設定リスト最後と完全一致でフィルタ実行() {
    // Arrange: /asset/api/aircraft/reserve/publicDataExtract
    request.setServletPath("/asset/api/aircraft/reserve/publicDataExtract");

    // Act
    boolean result = filter.shouldNotFilter(request);

    // Assert
    assertFalse(result, "バッチAPI設定リスト最後と完全一致の場合はフィルタを実行する（false）");
  }

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: バッチAPI設定とパス不一致時のフィルタスキップ動作<br>
   * 条件: バッチAPI設定に存在しないパス(/asset/api/droneport/info/sample)を設定する<br>
   * 結果: trueが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testShouldNotFilter_バッチAPI設定とパス不一致でスキップ() {
    // Arrange: /asset/api/droneport/info/sample（バッチAPI設定に存在しない）
    request.setServletPath("/asset/api/droneport/info/sample");

    // Act
    boolean result = filter.shouldNotFilter(request);

    // Assert
    assertTrue(result, "バッチAPI設定とパス不一致の場合はフィルタをスキップする（true）");
  }

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: バッチAPI設定と途中まで一致時のフィルタスキップ動作<br>
   * 条件: バッチAPI設定と途中まで一致するパス(/asset/api/droneport/info/publicDataExtract2)を設定する<br>
   * 結果: trueが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testShouldNotFilter_バッチAPI設定と途中まで一致でスキップ() {
    // Arrange: /asset/api/droneport/info/publicDataExtract2（途中まで一致だが完全一致しない）
    request.setServletPath("/asset/api/droneport/info/publicDataExtract2");

    // Act
    boolean result = filter.shouldNotFilter(request);

    // Assert
    assertTrue(result, "バッチAPI設定と途中まで一致の場合はフィルタをスキップする（true）");
  }

  // ========================================
  // shouldNotFilter() のテスト - 全APIパス網羅検証
  // ========================================

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: バッチAPIパスに対する網羅的なフィルタ実行動作<br>
   * 条件: バッチAPIパスを設定する<br>
   * 結果: falseが返される<br>
   * テストパターン：正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideBatchApiPaths")
  void testShouldNotFilter_バッチAPIは実行される(String displayName, String path) {
    // Arrange
    request.setServletPath(path);

    // Act
    boolean result = filter.shouldNotFilter(request);

    // Assert
    assertFalse(result, displayName + " はバッチAPIなのでフィルタが実行される（false）");
  }

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: 通常APIパスに対する網羅的なフィルタスキップ動作<br>
   * 条件: 通常APIパスを設定する<br>
   * 結果: trueが返される<br>
   * テストパターン：正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideNormalApiPaths")
  void testShouldNotFilter_通常APIはスキップされる(String displayName, String path) {
    // Arrange
    request.setServletPath(path);

    // Act
    boolean result = filter.shouldNotFilter(request);

    // Assert
    assertTrue(result, displayName + " は通常APIなのでフィルタがスキップされる（true）");
  }

  /** バッチAPIのテストデータ（4個） */
  static Stream<Arguments> provideBatchApiPaths() {
    return Stream.of(
        Arguments.of("バッチAPI: 離着陸場情報公開データ抽出", "/asset/api/droneport/info/publicDataExtract"),
        Arguments.of("バッチAPI: 離着陸場予約情報公開データ抽出", "/asset/api/droneport/reserve/publicDataExtract"),
        Arguments.of("バッチAPI: 機体情報公開データ抽出", "/asset/api/aircraft/info/publicDataExtract"),
        Arguments.of("バッチAPI: 機体予約情報公開データ抽出", "/asset/api/aircraft/reserve/publicDataExtract"));
  }

  /** 通常APIのテストデータ（25個） */
  static Stream<Arguments> provideNormalApiPaths() {
    return Stream.of(
        // 離着陸場情報API（5個）
        Arguments.of("通常API: 離着陸場情報登録 POST", "/asset/api/droneport/info"),
        Arguments.of("通常API: 離着陸場情報更新 PUT", "/asset/api/droneport/info"),
        Arguments.of(
            "通常API: 離着陸場情報削除 DELETE",
            "/asset/api/droneport/info/123e4567-e89b-12d3-a456-426614174001"),
        Arguments.of("通常API: 離着陸場情報一覧取得 GET", "/asset/api/droneport/info/list"),
        Arguments.of(
            "通常API: 離着陸場情報詳細取得 GET",
            "/asset/api/droneport/info/detail/123e4567-e89b-12d3-a456-426614174001"),

        // 離着陸場予約情報API（5個）
        Arguments.of("通常API: 離着陸場予約情報登録 POST", "/asset/api/droneport/reserve"),
        Arguments.of("通常API: 離着陸場予約情報更新 PUT", "/asset/api/droneport/reserve"),
        Arguments.of(
            "通常API: 離着陸場予約情報削除 DELETE",
            "/asset/api/droneport/reserve/123e4567-e89b-12d3-a456-426614174002"),
        Arguments.of("通常API: 離着陸場予約情報一覧取得 GET", "/asset/api/droneport/reserve/list"),
        Arguments.of(
            "通常API: 離着陸場予約情報詳細取得 GET",
            "/asset/api/droneport/reserve/detail/123e4567-e89b-12d3-a456-426614174002"),

        // 離着陸場周辺情報API（1個）
        Arguments.of(
            "通常API: 離着陸場周辺情報取得 GET",
            "/asset/api/droneport/environment/123e4567-e89b-12d3-a456-426614174001"),

        // 機体情報API（8個）
        Arguments.of("通常API: 機体情報登録 POST", "/asset/api/aircraft/info"),
        Arguments.of("通常API: 機体情報更新 PUT", "/asset/api/aircraft/info"),
        Arguments.of(
            "通常API: 機体情報削除 DELETE",
            "/asset/api/aircraft/info/123e4567-e89b-12d3-a456-426614174003"),
        Arguments.of("通常API: 機体情報一覧取得 GET", "/asset/api/aircraft/info/list"),
        Arguments.of("通常API: 機体情報モデル検索 POST", "/asset/api/aircraft/info/modelSearch"),
        Arguments.of(
            "通常API: 機体情報詳細取得 GET",
            "/asset/api/aircraft/info/detail/123e4567-e89b-12d3-a456-426614174003"),
        Arguments.of(
            "通常API: 機体補足資料取得 GET",
            "/asset/api/aircraft/info/detail/123e4567-e89b-12d3-a456-426614174003/123e4567-e89b-12d3-a456-426614174004"),
        Arguments.of(
            "通常API: ペイロード添付ファイル取得 GET",
            "/asset/api/aircraft/info/payload/123e4567-e89b-12d3-a456-426614174005"),

        // 機体予約情報API（5個）
        Arguments.of("通常API: 機体予約情報登録 POST", "/asset/api/aircraft/reserve"),
        Arguments.of("通常API: 機体予約情報更新 PUT", "/asset/api/aircraft/reserve"),
        Arguments.of(
            "通常API: 機体予約情報削除 DELETE",
            "/asset/api/aircraft/reserve/123e4567-e89b-12d3-a456-426614174006"),
        Arguments.of("通常API: 機体予約情報一覧取得 GET", "/asset/api/aircraft/reserve/list"),
        Arguments.of(
            "通常API: 機体予約情報詳細取得 GET",
            "/asset/api/aircraft/reserve/detail/123e4567-e89b-12d3-a456-426614174006"),

        // リソース料金情報API（1個）
        Arguments.of("通常API: リソース料金情報検索 GET", "/asset/api/price/info/resourcePriceList"));
  }

  // ========================================
  // doFilterInternal() - 正常系
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: API-Key認証成功時のSecurityContext設定<br>
   * 条件: 有効なAPI-Keyヘッダーを設定する<br>
   * 結果: SecurityContextに認証情報が設定され、filterChainが実行される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testDoFilterInternal_正常系_API_Key認証成功でSecurityContext設定() throws Exception {
    // Arrange
    request.addHeader(CommonConstants.ASSET_API_KEY_HEADER_NAME, TEST_API_KEY);
    when(systemSettings.getString(
            CommonConstants.BATCH_SETTINGS, CommonConstants.SETTINGS_ASSET_API_KEY))
        .thenReturn(TEST_API_KEY_HASH);

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert - filterChainが呼ばれたことを確認
    verify(filterChain, times(1)).doFilter(request, response);

    // Assert - SecurityContextに認証情報が設定されていることを確認
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication, "SecurityContextに認証情報が設定されている");
    assertTrue(authentication.isAuthenticated(), "認証済みである");

    // Assert - UserInfoDtoのDummyUserFlagがtrueに設定されていることを確認
    Object principal = authentication.getPrincipal();
    assertInstanceOf(UserInfoDto.class, principal, "PrincipalがUserInfoDto型である");
    UserInfoDto userInfo = (UserInfoDto) principal;
    assertTrue(userInfo.isDummyUserFlag(), "DummyUserFlagがtrueに設定されている");
  }

  // ========================================
  // doFilterInternal() - 異常系
  // ========================================

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: API-Keyヘッダーなし時の認証失敗<br>
   * 条件: API-Keyヘッダーを設定しない<br>
   * 結果: AuthorizationExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testDoFilterInternal_異常系_API_Keyヘッダーなしで認証失敗() {
    // Arrange
    // API-Keyヘッダーを設定しない
    when(systemSettings.getString(
            CommonConstants.BATCH_SETTINGS, CommonConstants.SETTINGS_ASSET_API_KEY))
        .thenReturn(TEST_API_KEY_HASH);

    // Act & Assert
    AuthorizationException exception =
        assertThrows(
            AuthorizationException.class,
            () -> filter.doFilterInternal(request, response, filterChain),
            "API-Keyヘッダーがない場合はAuthorizationExceptionがスローされる");

    assertEquals("認可エラー。ASSET-API-Keyが不正です。", exception.getMessage());

    // Assert - filterChainが呼ばれていないことを確認
    verifyNoInteractions(filterChain);
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: ハッシュ値不一致時の認証失敗<br>
   * 条件: 無効なAPI-Keyヘッダーを設定する<br>
   * 結果: AuthorizationExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testDoFilterInternal_異常系_ハッシュ値不一致で認証失敗() {
    // Arrange
    request.addHeader(CommonConstants.ASSET_API_KEY_HEADER_NAME, INVALID_API_KEY);
    when(systemSettings.getString(
            CommonConstants.BATCH_SETTINGS, CommonConstants.SETTINGS_ASSET_API_KEY))
        .thenReturn(TEST_API_KEY_HASH);

    // Act & Assert
    AuthorizationException exception =
        assertThrows(
            AuthorizationException.class,
            () -> filter.doFilterInternal(request, response, filterChain),
            "ハッシュ値が不一致の場合はAuthorizationExceptionがスローされる");

    assertEquals("認可エラー。ASSET-API-Keyが不正です。", exception.getMessage());

    // Assert - filterChainが呼ばれていないことを確認
    verifyNoInteractions(filterChain);
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: validApiKeyHashがnull時の認証失敗<br>
   * 条件: systemSettingsからnullが返される<br>
   * 結果: AuthorizationExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testDoFilterInternal_異常系_validApiKeyHashがnullで認証失敗() {
    // Arrange
    request.addHeader(CommonConstants.ASSET_API_KEY_HEADER_NAME, TEST_API_KEY);
    when(systemSettings.getString(
            CommonConstants.BATCH_SETTINGS, CommonConstants.SETTINGS_ASSET_API_KEY))
        .thenReturn(null);

    // Act & Assert
    AuthorizationException exception =
        assertThrows(
            AuthorizationException.class,
            () -> filter.doFilterInternal(request, response, filterChain),
            "validApiKeyHashがnullの場合はAuthorizationExceptionがスローされる");

    assertEquals("認可エラー。ASSET-API-Keyが不正です。", exception.getMessage());

    // Assert - filterChainが呼ばれていないことを確認
    verifyNoInteractions(filterChain);
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: ハッシュ化処理で例外発生時の認証失敗<br>
   * 条件: MessageDigest.getInstance()でNoSuchAlgorithmExceptionをスローする<br>
   * 結果: AuthorizationExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testDoFilterInternal_異常系_ハッシュ化処理で例外発生() throws Exception {
    // Arrange
    request.addHeader(CommonConstants.ASSET_API_KEY_HEADER_NAME, TEST_API_KEY);
    when(systemSettings.getString(
            CommonConstants.BATCH_SETTINGS, CommonConstants.SETTINGS_ASSET_API_KEY))
        .thenReturn(TEST_API_KEY_HASH);

    // MessageDigest.getInstance()をモック化してNoSuchAlgorithmExceptionをスロー
    try (MockedStatic<MessageDigest> mockedMessageDigest = mockStatic(MessageDigest.class)) {
      mockedMessageDigest
          .when(() -> MessageDigest.getInstance(CommonConstants.HASH_ALGORITHM_SHA256))
          .thenThrow(new NoSuchAlgorithmException("SHA-256 algorithm not available"));

      // Act & Assert
      AuthorizationException exception =
          assertThrows(
              AuthorizationException.class,
              () -> filter.doFilterInternal(request, response, filterChain),
              "ハッシュ化処理で例外が発生した場合はAuthorizationExceptionがスローされる");

      assertEquals("認可エラー。ASSET-API-Key認証処理に失敗しました。", exception.getMessage());

      // Assert - filterChainが呼ばれていないことを確認
      verifyNoInteractions(filterChain);
    }
  }
}
