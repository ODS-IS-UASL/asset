package com.hitachi.droneroute.config.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.cmn.exception.AuthorizationException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.JwtTokenUtil;
import com.hitachi.droneroute.config.dto.RoleInfoDto;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.config.properties.ResourceRolesProperties;
import com.hitachi.droneroute.config.properties.ResourceRolesProperties.ResourceRoleRule;
import com.hitachi.droneroute.config.service.UserInfoService;
import jakarta.servlet.FilterChain;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

/** RoleAuthorizationFilterの単体テスト */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.yml")
class RoleAuthorizationFilterTest {

  @InjectMocks private RoleAuthorizationFilter filter;

  @Mock private ResourceRolesProperties securityProperties;

  @Mock private SystemSettings systemSettings;

  @Mock private UserInfoService userInfoService;

  @Mock private RequestMatcher batchApiMatcher;

  @Mock private FilterChain filterChain;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private UserInfoDto userInfo;

  private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String TEST_AFFILIATED_OPERATOR_ID = "123e4567-e89b-12d3-a456-426614174100";
  private static final String TEST_OTHER_OPERATOR_ID = "123e4567-e89b-12d3-a456-426614174300";
  private static final String TEST_ROLE_ID = "11";
  private static final String TEST_ROLE_NAME = "航路運営者_担当者";
  private static final String TEST_AUTHORIZATION_HEADER =
      "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTc2OTA0NzIwMCwiYXV0aF90aW1lIjoxNzYwNjg3OTY1LCJqdGkiOiJvbnJ0YWM6YzI5NDE5MmYtMGUwNC02MjNhLTQ2OTktYjk4ZGNiMGM2Y2MyIiwiaXNzIjoiaHR0cHM6Ly9kdHMtb2RzLWF1dGgtaWQuZGV2LmR0cy1vZHMuY29tL3JlYWxtcy90ZXN0b2RzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjRlMGRmMjI2LTAyMmItNDVhZC05N2Q1LWY2MGMxNWIzZmFmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6IkF1dGhDb2RlQ2xpZW50MDAxIiwic2lkIjoiNTBiODFlMGQtN2FjYy00Zjk1LWFlNDktNjJmMDY1YTczNmYyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy10ZXN0b2RzIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm9wZXJhdG9yX2lkIjoiIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiNHFueXk1bTRqZCIsImVtYWlsIjoibG9naW5fdXNlckBleGFtcGxlLmNvbSJ9.FiwNadanhzBJhghR1wivcBCaFmGljlpnUBdzpNuONnBMocOpuDiOoTw-xuN748O3L3Am6aJnZIpvKdHRLqYbpD15WlvMlFx9V8_mrgppIe33mUFmLN4gMG90_3kBoYgPV0lr4ZgXRKfy8Na6inqmMCJLBf0rmSJk1SDlrdngSBjwpmhMES8mY6-LL2cUVKpnTCrqdmHs6Wlu-TXJfijw90nHNqXRmJqebt0zd1ZKREELr0nEr4whfvYnDTo-2Ii4720vtcRRHrvAflBGGi_SmPmzaPjumMabR5mstxp-M_YrLhbCPozoHqM_xLQ-yljwqanicMkc_ff94FqvyyF8zw";

  // テスト用ResourceRoleRuleテンプレート（application-test.ymlの設定を参考）
  private static final ResourceRoleRule RULE_DRONEPORT_INFO_POST;
  private static final ResourceRoleRule RULE_DRONEPORT_INFO_PUT;
  private static final ResourceRoleRule RULE_AIRCRAFT_INFO_POST;
  private static final ResourceRoleRule RULE_DRONEPORT_INFO_POST_REQUIRE_SYSTEM_OP;
  private static final ResourceRoleRule RULE_DRONEPORT_WILDCARD;

  static {
    // ドローンポート情報登録（POST） - requireSystemOperator=false
    RULE_DRONEPORT_INFO_POST = new ResourceRoleRule();
    RULE_DRONEPORT_INFO_POST.setPath("/droneport/info");
    RULE_DRONEPORT_INFO_POST.setMethod("POST");
    RULE_DRONEPORT_INFO_POST.setRequireSystemOperator(false);

    // ドローンポート情報更新（PUT） - requireSystemOperator=false
    RULE_DRONEPORT_INFO_PUT = new ResourceRoleRule();
    RULE_DRONEPORT_INFO_PUT.setPath("/droneport/info");
    RULE_DRONEPORT_INFO_PUT.setMethod("PUT");
    RULE_DRONEPORT_INFO_PUT.setRequireSystemOperator(false);

    // 機体情報登録（POST） - requireSystemOperator=false
    RULE_AIRCRAFT_INFO_POST = new ResourceRoleRule();
    RULE_AIRCRAFT_INFO_POST.setPath("/aircraft/info");
    RULE_AIRCRAFT_INFO_POST.setMethod("POST");
    RULE_AIRCRAFT_INFO_POST.setRequireSystemOperator(false);

    // ドローンポート情報登録（POST） - requireSystemOperator=true（テスト用）
    RULE_DRONEPORT_INFO_POST_REQUIRE_SYSTEM_OP = new ResourceRoleRule();
    RULE_DRONEPORT_INFO_POST_REQUIRE_SYSTEM_OP.setPath("/droneport/info");
    RULE_DRONEPORT_INFO_POST_REQUIRE_SYSTEM_OP.setMethod("POST");
    RULE_DRONEPORT_INFO_POST_REQUIRE_SYSTEM_OP.setRequireSystemOperator(true);

    // ドローンポート情報削除（DELETE） - ワイルドカードパターン（ID指定）
    RULE_DRONEPORT_WILDCARD = new ResourceRoleRule();
    RULE_DRONEPORT_WILDCARD.setPath("/droneport/info/*");
    RULE_DRONEPORT_WILDCARD.setMethod("DELETE");
    RULE_DRONEPORT_WILDCARD.setRequireSystemOperator(false);
  }

  @BeforeEach
  void setUp() {
    // basepathフィールドを設定
    ReflectionTestUtils.setField(filter, "basePath", "/asset/api");

    // SecurityContextをクリア
    SecurityContextHolder.clearContext();

    // MockHttpServletRequestとResponseを初期化
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();

    // 共通のリクエスト設定
    request.addHeader("Authorization", TEST_AUTHORIZATION_HEADER);
    request.setRequestURI("/asset/api/droneport/info");
    request.setMethod("POST");

    // 共通のユーザー情報作成
    userInfo = new UserInfoDto();
    userInfo.setUserOperatorId(TEST_USER_ID);
    userInfo.setRoles(List.of(new RoleInfoDto(TEST_ROLE_ID, TEST_ROLE_NAME)));
    userInfo.setAffiliatedOperatorId(TEST_AFFILIATED_OPERATOR_ID);

    // batchApiMatcherのデフォルト動作：バッチAPI以外と判定（通常のJWT認証が実行される）
    // lenient()を使用して、使われない場合でもエラーにならないようにする
    lenient().when(batchApiMatcher.matches(any())).thenReturn(false);
  }

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: 除外パス設定の網羅的検証<br>
   * 条件: AWSヘルスチェック、Actuatorエンドポイント、通常APIの各パスでフィルタ除外判定を実行<br>
   * 結果: 除外対象パスはtrue、通常APIはfalseを返す<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testShouldNotFilter_除外フィルタ網羅() {
    // Arrange - バッチAPIではない通常リクエストとしてモック設定
    MockHttpServletRequest awshealth = new MockHttpServletRequest("GET", "/awshealth/check.html");
    MockHttpServletRequest actuatorHealth = new MockHttpServletRequest("GET", "/actuator/health");
    MockHttpServletRequest actuatorInfo = new MockHttpServletRequest("GET", "/actuator/info");
    MockHttpServletRequest normal = new MockHttpServletRequest("GET", "/asset/api/droneport/info");
    MockHttpServletRequest notActuator = new MockHttpServletRequest("GET", "/actuator/healthcheck");

    // batchApiMatcherは全てfalseを返す（バッチAPIではない）
    when(batchApiMatcher.matches(any())).thenReturn(false);

    // 除外対象: /awshealth/** - AWSヘルスチェック用エンドポイント
    awshealth.setServletPath("/awshealth/check.html");
    assertTrue(filter.shouldNotFilter(awshealth), "/awshealth/check.htmlは除外される");

    // 除外対象: /actuator/health - Spring Actuatorヘルスチェック
    actuatorHealth.setServletPath("/actuator/health");
    assertTrue(filter.shouldNotFilter(actuatorHealth), "/actuator/healthは除外される");

    // 除外対象: /actuator/info - Spring Actuator情報エンドポイント
    actuatorInfo.setServletPath("/actuator/info");
    assertTrue(filter.shouldNotFilter(actuatorInfo), "/actuator/infoは除外される");

    // 除外対象外: 通常API - ドローンポート情報取得API
    normal.setServletPath("/asset/api/droneport/info");
    assertFalse(filter.shouldNotFilter(normal), "/asset/api/droneport/infoは除外されない");

    // 除外対象外: 境界値 - /actuator/healthに似ているが完全一致しない
    notActuator.setServletPath("/actuator/healthcheck");
    assertFalse(filter.shouldNotFilter(notActuator), "/actuator/healthcheckは除外されない");
  }

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: バッチAPIのフィルタ除外動作検証<br>
   * 条件: batchApiMatcherがtrueを返すリクエストでフィルタ除外判定を実行<br>
   * 結果: trueを返し、フィルタがスキップされる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testShouldNotFilter_バッチAPIは除外される() {
    // Arrange
    MockHttpServletRequest batchRequest =
        new MockHttpServletRequest("POST", "/asset/api/droneport/batch");
    batchRequest.setServletPath("/asset/api/droneport/batch");

    // batchApiMatcherがtrueを返す（バッチAPIである）
    when(batchApiMatcher.matches(batchRequest)).thenReturn(true);

    // Act & Assert
    assertTrue(filter.shouldNotFilter(batchRequest), "バッチAPIはこのフィルタから除外される");
  }

  // ========================================
  // shouldNotFilter() のテスト - 全APIパス網羅検証
  // ========================================

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: 通常APIパスの網羅的フィルタ実行検証<br>
   * 条件: 全25個の通常APIパス（離着陸場情報、機体情報、予約情報等）でフィルタ除外判定を実行<br>
   * 結果: 全てfalseを返し、フィルタが実行される<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideNormalApiPaths")
  void testShouldNotFilter_通常APIは実行される(String displayName, String path, String method) {
    // Arrange
    MockHttpServletRequest normalRequest = new MockHttpServletRequest(method, path);
    normalRequest.setServletPath(path);

    // batchApiMatcherがfalseを返す（バッチAPIではない）
    when(batchApiMatcher.matches(normalRequest)).thenReturn(false);

    // Act
    boolean result = filter.shouldNotFilter(normalRequest);

    // Assert
    assertFalse(result, displayName + " は通常APIなのでフィルタが実行される（false）");
  }

  /**
   * メソッド名: shouldNotFilter<br>
   * 試験名: バッチAPIパスの網羅的フィルタスキップ検証<br>
   * 条件: 全4個のバッチAPIパス（公開データ抽出API）でフィルタ除外判定を実行<br>
   * 結果: 全てtrueを返し、フィルタがスキップされる<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("provideBatchApiPaths")
  void testShouldNotFilter_バッチAPIはスキップされる(String displayName, String path) {
    // Arrange
    MockHttpServletRequest batchRequest = new MockHttpServletRequest("GET", path);
    batchRequest.setServletPath(path);

    // batchApiMatcherがtrueを返す（バッチAPIである）
    when(batchApiMatcher.matches(batchRequest)).thenReturn(true);

    // Act
    boolean result = filter.shouldNotFilter(batchRequest);

    // Assert
    assertTrue(result, displayName + " はバッチAPIなのでフィルタがスキップされる（true）");
  }

  /** 通常APIのテストデータ（25個） */
  static Stream<Arguments> provideNormalApiPaths() {
    return Stream.of(
        // 離着陸場情報API（5個）
        Arguments.of("通常API: 離着陸場情報登録 POST", "/asset/api/droneport/info", "POST"),
        Arguments.of("通常API: 離着陸場情報更新 PUT", "/asset/api/droneport/info", "PUT"),
        Arguments.of(
            "通常API: 離着陸場情報削除 DELETE",
            "/asset/api/droneport/info/123e4567-e89b-12d3-a456-426614174001",
            "DELETE"),
        Arguments.of("通常API: 離着陸場情報一覧取得 GET", "/asset/api/droneport/info/list", "GET"),
        Arguments.of(
            "通常API: 離着陸場情報詳細取得 GET",
            "/asset/api/droneport/info/detail/123e4567-e89b-12d3-a456-426614174001",
            "GET"),

        // 離着陸場予約情報API（5個）
        Arguments.of("通常API: 離着陸場予約情報登録 POST", "/asset/api/droneport/reserve", "POST"),
        Arguments.of("通常API: 離着陸場予約情報更新 PUT", "/asset/api/droneport/reserve", "PUT"),
        Arguments.of(
            "通常API: 離着陸場予約情報削除 DELETE",
            "/asset/api/droneport/reserve/123e4567-e89b-12d3-a456-426614174002",
            "DELETE"),
        Arguments.of("通常API: 離着陸場予約情報一覧取得 GET", "/asset/api/droneport/reserve/list", "GET"),
        Arguments.of(
            "通常API: 離着陸場予約情報詳細取得 GET",
            "/asset/api/droneport/reserve/detail/123e4567-e89b-12d3-a456-426614174002",
            "GET"),

        // 離着陸場周辺情報API（1個）
        Arguments.of(
            "通常API: 離着陸場周辺情報取得 GET",
            "/asset/api/droneport/environment/123e4567-e89b-12d3-a456-426614174001",
            "GET"),

        // 機体情報API（8個）
        Arguments.of("通常API: 機体情報登録 POST", "/asset/api/aircraft/info", "POST"),
        Arguments.of("通常API: 機体情報更新 PUT", "/asset/api/aircraft/info", "PUT"),
        Arguments.of(
            "通常API: 機体情報削除 DELETE",
            "/asset/api/aircraft/info/123e4567-e89b-12d3-a456-426614174003",
            "DELETE"),
        Arguments.of("通常API: 機体情報一覧取得 GET", "/asset/api/aircraft/info/list", "GET"),
        Arguments.of("通常API: 機体情報モデル検索 POST", "/asset/api/aircraft/info/modelSearch", "POST"),
        Arguments.of(
            "通常API: 機体情報詳細取得 GET",
            "/asset/api/aircraft/info/detail/123e4567-e89b-12d3-a456-426614174003",
            "GET"),
        Arguments.of(
            "通常API: 機体補足資料取得 GET",
            "/asset/api/aircraft/info/detail/123e4567-e89b-12d3-a456-426614174003/123e4567-e89b-12d3-a456-426614174004",
            "GET"),
        Arguments.of(
            "通常API: ペイロード添付ファイル取得 GET",
            "/asset/api/aircraft/info/payload/123e4567-e89b-12d3-a456-426614174005",
            "GET"),

        // 機体予約情報API（5個）
        Arguments.of("通常API: 機体予約情報登録 POST", "/asset/api/aircraft/reserve", "POST"),
        Arguments.of("通常API: 機体予約情報更新 PUT", "/asset/api/aircraft/reserve", "PUT"),
        Arguments.of(
            "通常API: 機体予約情報削除 DELETE",
            "/asset/api/aircraft/reserve/123e4567-e89b-12d3-a456-426614174006",
            "DELETE"),
        Arguments.of("通常API: 機体予約情報一覧取得 GET", "/asset/api/aircraft/reserve/list", "GET"),
        Arguments.of(
            "通常API: 機体予約情報詳細取得 GET",
            "/asset/api/aircraft/reserve/detail/123e4567-e89b-12d3-a456-426614174006",
            "GET"),

        // リソース料金情報API（1個）
        Arguments.of("通常API: リソース料金情報検索 GET", "/asset/api/price/info/resourcePriceList", "GET"));
  }

  /** バッチAPIのテストデータ（4個） */
  static Stream<Arguments> provideBatchApiPaths() {
    return Stream.of(
        Arguments.of("バッチAPI: 離着陸場情報公開データ抽出", "/asset/api/droneport/info/publicDataExtract"),
        Arguments.of("バッチAPI: 離着陸場予約情報公開データ抽出", "/asset/api/droneport/reserve/publicDataExtract"),
        Arguments.of("バッチAPI: 機体情報公開データ抽出", "/asset/api/aircraft/info/publicDataExtract"),
        Arguments.of("バッチAPI: 機体予約情報公開データ抽出", "/asset/api/aircraft/reserve/publicDataExtract"));
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: 正常な認証・認可処理の完了検証<br>
   * 条件: 有効なJWTトークンとユーザー情報で認証・認可処理を実行<br>
   * 結果: SecurityContextに認証情報が設定され、filterChainが呼ばれる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testDoFilterInternal_正常系_認可エラーなし() throws Exception {
    // Arrange

    // モック設定
    // 注：authorizeRequestメソッドの認可チェックは別テストで実施するため、ここではスキップ
    when(securityProperties.getCheckTargets()).thenReturn(Collections.emptyList());
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID)).thenReturn(userInfo);

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act
      filter.doFilterInternal(request, response, filterChain);

      // Assert - SecurityContextの確認
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      assertNotNull(auth, "SecurityContextに認証情報が設定されていること");
      assertTrue(auth.isAuthenticated(), "認証済みであること");

      // プリンシパルの確認
      Object principal = auth.getPrincipal();
      assertTrue(principal instanceof UserInfoDto, "プリンシパルがUserInfoDto型であること");

      UserInfoDto contextUserInfo = (UserInfoDto) principal;
      assertEquals(TEST_USER_ID, contextUserInfo.getUserOperatorId(), "userOperatorIdが一致すること");
      assertNotNull(contextUserInfo.getRoles(), "rolesが設定されていること");
      assertFalse(contextUserInfo.getRoles().isEmpty(), "rolesが空でないこと");
      assertEquals(TEST_ROLE_ID, contextUserInfo.getRoles().get(0).getRoleId(), "roleIdが一致すること");
      assertEquals(
          TEST_ROLE_NAME, contextUserInfo.getRoles().get(0).getRoleName(), "roleNameが一致すること");
      assertEquals(
          TEST_AFFILIATED_OPERATOR_ID,
          contextUserInfo.getAffiliatedOperatorId(),
          "affiliatedOperatorIdが一致すること");

      // メソッド呼び出し回数の確認
      mockedStatic.verify(
          () -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"),
          times(1));
      verify(userInfoService, times(1)).getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID);
      verify(filterChain, times(1)).doFilter(request, response);
    }
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: 不正なJWTトークンによる認証エラー検証<br>
   * 条件: JWTトークンからoperator_idが抽出できず、nullが返される<br>
   * 結果: AuthorizationExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testDoFilterInternal_異常系_アクセストークン不正でAuthorizationExceptionが発生() throws Exception {
    // Arrange

    // JwtTokenUtilのstaticメソッドをモック化 - userIdがnullを返す
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(null);

      // Act & Assert
      AuthorizationException exception =
          assertThrows(
              AuthorizationException.class,
              () -> filter.doFilterInternal(request, response, filterChain),
              "AuthorizationExceptionがスローされること");

      // 例外メッセージの検証
      assertEquals("認可エラー。アクセストークンが不正です。", exception.getMessage(), "エラーメッセージが正しいこと");

      // メソッド呼び出し回数の確認
      mockedStatic.verify(
          () -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"),
          times(1));
      // userInfoServiceは呼ばれないこと
      verify(userInfoService, never()).getUserInfo(anyString(), anyString());
      // filterChainも呼ばれないこと
      verify(filterChain, never()).doFilter(any(), any());
    }
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: ユーザー情報取得時のServiceErrorExceptionエラーハンドリング検証<br>
   * 条件: userInfoService.getUserInfo()がServiceErrorExceptionをスロー<br>
   * 結果: InternalAuthenticationServiceExceptionでラップされてスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testDoFilterInternal_異常系_ユーザ属性取得でServiceErrorExceptionが発生() throws Exception {
    // Arrange
    ServiceErrorException serviceError = new ServiceErrorException("ServiceErrorExceptionが発生");

    // モック設定
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID))
        .thenThrow(serviceError);

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act & Assert
      InternalAuthenticationServiceException exception =
          assertThrows(
              InternalAuthenticationServiceException.class,
              () -> filter.doFilterInternal(request, response, filterChain),
              "InternalAuthenticationServiceExceptionがスローされること");

      // 例外メッセージの検証
      assertEquals("ServiceErrorExceptionが発生", exception.getMessage(), "エラーメッセージが正しいこと");

      // causeがServiceErrorExceptionであることを確認
      assertNotNull(exception.getCause(), "causeが設定されていること");
      assertTrue(
          exception.getCause() instanceof ServiceErrorException,
          "causeがServiceErrorExceptionであること");
      assertSame(serviceError, exception.getCause(), "causeが元の例外と同一であること");

      // メソッド呼び出し回数の確認
      mockedStatic.verify(
          () -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"),
          times(1));
      verify(userInfoService, times(1)).getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID);
      // filterChainは呼ばれないこと
      verify(filterChain, never()).doFilter(any(), any());
    }
  }

  /**
   * メソッド名: doFilterInternal<br>
   * 試験名: ユーザー情報取得時の想定外エラーハンドリング検証<br>
   * 条件: userInfoService.getUserInfo()がRuntimeExceptionをスロー<br>
   * 結果: InternalAuthenticationServiceExceptionでラップされてスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testDoFilterInternal_異常系_ユーザ属性取得で想定外のエラーが発生() throws Exception {
    // Arrange
    RuntimeException unexpectedError = new RuntimeException("想定外のエラーが発生");

    // モック設定
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID))
        .thenThrow(unexpectedError);

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act & Assert
      InternalAuthenticationServiceException exception =
          assertThrows(
              InternalAuthenticationServiceException.class,
              () -> filter.doFilterInternal(request, response, filterChain),
              "InternalAuthenticationServiceExceptionがスローされること");

      // 例外メッセージの検証
      assertEquals("想定外のエラーが発生", exception.getMessage(), "エラーメッセージが正しいこと");

      // causeがRuntimeExceptionであることを確認
      assertNotNull(exception.getCause(), "causeが設定されていること");
      assertTrue(exception.getCause() instanceof RuntimeException, "causeがRuntimeExceptionであること");
      assertSame(unexpectedError, exception.getCause(), "causeが元の例外と同一であること");

      // メソッド呼び出し回数の確認
      mockedStatic.verify(
          () -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"),
          times(1));
      verify(userInfoService, times(1)).getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID);
      // filterChainは呼ばれないこと
      verify(filterChain, never()).doFilter(any(), any());
    }
  }

  /**
   * メソッド名: authorizeRequest<br>
   * 試験名: 認可チェック設定なしでのスキップ動作検証<br>
   * 条件: securityProperties.getCheckTargets()が空リストを返す<br>
   * 結果: 認可チェックがスキップされ、正常に処理が完了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testAuthorizeRequest_正常系_自事業者チェック設定なしでスキップ() throws Exception {
    // Arrange

    // モック設定 - 該当APIの設定なし（空リスト）
    when(securityProperties.getCheckTargets()).thenReturn(Collections.emptyList());
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID)).thenReturn(userInfo);

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act
      filter.doFilterInternal(request, response, filterChain);

      // Assert
      // 自事業者チェック設定なしの場合、正常に処理が完了することを確認
      verify(filterChain, times(1)).doFilter(request, response);
    }
  }

  /**
   * メソッド名: authorizeRequest<br>
   * 試験名: requireSystemOperatorがfalseの場合の認可動作検証<br>
   * 条件: requireSystemOperator=falseのルールで認可チェックを実行<br>
   * 結果: 自システム事業者チェックがスキップされ、正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testAuthorizeRequest_正常系_自システムチェックフラグFALSEで正常終了() throws Exception {
    // Arrange

    // モック設定 - application-test.ymlの実際の設定値を使用
    when(securityProperties.getCheckTargets()).thenReturn(List.of(RULE_DRONEPORT_INFO_POST));
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID)).thenReturn(userInfo);

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act
      filter.doFilterInternal(request, response, filterChain);

      // Assert
      // requireSystemOperator=falseの場合、自事業者チェックがスキップされ正常終了することを確認
      verify(filterChain, times(1)).doFilter(request, response);
    }
  }

  /**
   * メソッド名: authorizeRequest<br>
   * 試験名: requireSystemOperatorがfalseで他事業者の場合の認可動作検証<br>
   * 条件: requireSystemOperator=falseのルールで他事業者のユーザーがアクセス<br>
   * 結果: 自システム事業者チェックがスキップされ、正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testAuthorizeRequest_正常系_自システムチェックフラグFALSEで他事業者でも正常終了() throws Exception {
    // Arrange
    // 他事業者のユーザー情報を作成
    UserInfoDto otherOperatorUserInfo = new UserInfoDto();
    otherOperatorUserInfo.setUserOperatorId(TEST_USER_ID);
    otherOperatorUserInfo.setRoles(List.of(new RoleInfoDto(TEST_ROLE_ID, TEST_ROLE_NAME)));
    otherOperatorUserInfo.setAffiliatedOperatorId(TEST_OTHER_OPERATOR_ID);

    // モック設定 - application-test.ymlの実際の設定値を使用
    when(securityProperties.getCheckTargets()).thenReturn(List.of(RULE_DRONEPORT_INFO_POST));
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID))
        .thenReturn(otherOperatorUserInfo);

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act
      filter.doFilterInternal(request, response, filterChain);

      // Assert
      // requireSystemOperator=falseの場合、他事業者でも自事業者チェックがスキップされ正常終了することを確認
      verify(filterChain, times(1)).doFilter(request, response);
    }
  }

  /**
   * メソッド名: authorizeRequest<br>
   * 試験名: requireSystemOperatorがtrueで自システム事業者の場合の認可動作検証<br>
   * 条件: requireSystemOperator=trueのルールで自システム事業者がアクセス<br>
   * 結果: 自システム事業者チェックを通過し、正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testAuthorizeRequest_正常系_自システムチェックフラグTRUEで自システム事業者が正常終了() throws Exception {
    // Arrange
    // モック設定
    when(securityProperties.getCheckTargets())
        .thenReturn(List.of(RULE_DRONEPORT_INFO_POST_REQUIRE_SYSTEM_OP));
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID)).thenReturn(userInfo);
    // systemSettingsで自システムの事業者IDを返す
    when(systemSettings.getString("operatorInfo", "systemOperatorId"))
        .thenReturn(TEST_AFFILIATED_OPERATOR_ID);

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act
      filter.doFilterInternal(request, response, filterChain);

      // Assert
      // requireSystemOperator=trueで所属事業者が自システムの場合、正常終了することを確認
      verify(filterChain, times(1)).doFilter(request, response);
    }
  }

  /**
   * メソッド名: authorizeRequest<br>
   * 試験名: requireSystemOperatorがtrueで他事業者の場合の認可エラー検証<br>
   * 条件: requireSystemOperator=trueのルールで他事業者がアクセス<br>
   * 結果: AuthorizationExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testAuthorizeRequest_異常系_自システムチェックフラグTRUEで他事業者がAuthorizationExceptionが発生() throws Exception {
    // Arrange
    // 他事業者のユーザー情報を作成
    UserInfoDto otherOperatorUserInfo = new UserInfoDto();
    otherOperatorUserInfo.setUserOperatorId(TEST_USER_ID);
    otherOperatorUserInfo.setRoles(List.of(new RoleInfoDto(TEST_ROLE_ID, TEST_ROLE_NAME)));
    otherOperatorUserInfo.setAffiliatedOperatorId(TEST_OTHER_OPERATOR_ID);

    // モック設定
    when(securityProperties.getCheckTargets())
        .thenReturn(List.of(RULE_DRONEPORT_INFO_POST_REQUIRE_SYSTEM_OP));
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID))
        .thenReturn(otherOperatorUserInfo);
    // systemSettingsで自システムの事業者IDを返す
    when(systemSettings.getString("operatorInfo", "systemOperatorId"))
        .thenReturn(TEST_AFFILIATED_OPERATOR_ID);

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act & Assert
      AuthorizationException exception =
          assertThrows(
              AuthorizationException.class,
              () -> filter.doFilterInternal(request, response, filterChain),
              "AuthorizationExceptionがスローされること");

      // 例外メッセージの検証
      assertEquals("自システムの事業者のみアクセス可能です", exception.getMessage(), "エラーメッセージが正しいこと");

      // filterChainは呼ばれないこと
      verify(filterChain, never()).doFilter(any(), any());
    }
  }

  /**
   * メソッド名: authorizeRequest<br>
   * 試験名: requireSystemOperatorがtrueで事業者IDがnullの場合の認可エラー検証<br>
   * 条件: requireSystemOperator=trueのルールでユーザーの所属事業者IDがnull<br>
   * 結果: AuthorizationExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testAuthorizeRequest_異常系_自システムチェックフラグTRUEで事業者IDなし_AuthorizationExceptionが発生()
      throws Exception {
    // Arrange
    // 他事業者のユーザー情報を作成
    UserInfoDto otherOperatorUserInfo = new UserInfoDto();
    otherOperatorUserInfo.setUserOperatorId(TEST_USER_ID);
    otherOperatorUserInfo.setRoles(List.of(new RoleInfoDto(TEST_ROLE_ID, TEST_ROLE_NAME)));
    otherOperatorUserInfo.setAffiliatedOperatorId(null);

    // モック設定
    when(securityProperties.getCheckTargets())
        .thenReturn(List.of(RULE_DRONEPORT_INFO_POST_REQUIRE_SYSTEM_OP));
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID))
        .thenReturn(otherOperatorUserInfo);
    // systemSettingsで自システムの事業者IDを返す
    when(systemSettings.getString("operatorInfo", "systemOperatorId"))
        .thenReturn(TEST_AFFILIATED_OPERATOR_ID);

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act & Assert
      AuthorizationException exception =
          assertThrows(
              AuthorizationException.class,
              () -> filter.doFilterInternal(request, response, filterChain),
              "AuthorizationExceptionがスローされること");

      // 例外メッセージの検証
      assertEquals("自システムの事業者のみアクセス可能です", exception.getMessage(), "エラーメッセージが正しいこと");

      // filterChainは呼ばれないこと
      verify(filterChain, never()).doFilter(any(), any());
    }
  }

  /**
   * メソッド名: authorizeRequest<br>
   * 試験名: requireSystemOperatorがtrueでシステム設定の事業者IDが空文字の場合の認可エラー検証<br>
   * 条件: requireSystemOperator=trueのルールでsystemSettingsが空文字を返す<br>
   * 結果: AuthorizationExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testAuthorizeRequest_異常系_自システムチェックフラグTRUEでシステムの事業者IDなし_AuthorizationExceptionが発生()
      throws Exception {
    // Arrange
    // 他事業者のユーザー情報を作成
    UserInfoDto otherOperatorUserInfo = new UserInfoDto();
    otherOperatorUserInfo.setUserOperatorId(TEST_USER_ID);
    otherOperatorUserInfo.setRoles(List.of(new RoleInfoDto(TEST_ROLE_ID, TEST_ROLE_NAME)));
    otherOperatorUserInfo.setAffiliatedOperatorId(TEST_OTHER_OPERATOR_ID);

    // モック設定
    when(securityProperties.getCheckTargets())
        .thenReturn(List.of(RULE_DRONEPORT_INFO_POST_REQUIRE_SYSTEM_OP));
    when(userInfoService.getUserInfo(TEST_AUTHORIZATION_HEADER, TEST_USER_ID))
        .thenReturn(otherOperatorUserInfo);
    // systemSettingsで自システムの事業者IDを返す
    when(systemSettings.getString("operatorInfo", "systemOperatorId")).thenReturn("");

    // JwtTokenUtilのstaticメソッドをモック化
    try (MockedStatic<JwtTokenUtil> mockedStatic = mockStatic(JwtTokenUtil.class)) {
      mockedStatic
          .when(() -> JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, "operator_id"))
          .thenReturn(TEST_USER_ID);

      // Act & Assert
      AuthorizationException exception =
          assertThrows(
              AuthorizationException.class,
              () -> filter.doFilterInternal(request, response, filterChain),
              "AuthorizationExceptionがスローされること");

      // 例外メッセージの検証
      assertEquals("自システムの事業者のみアクセス可能です", exception.getMessage(), "エラーメッセージが正しいこと");

      // filterChainは呼ばれないこと
      verify(filterChain, never()).doFilter(any(), any());
    }
  }

  /**
   * メソッド名: findMatchingRule<br>
   * 試験名: パスとメソッドが完全一致するルール検索検証<br>
   * 条件: リクエストパスとHTTPメソッドが設定ルールと完全一致<br>
   * 結果: 一致するルールが返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testFindMatchingRule_正常系_パスとメソッドが完全一致でルールを返却() throws Exception {
    // Arrange
    String requestPath = "/asset/api/droneport/info";
    String requestMethod = "POST";

    // モック設定 - 複数のルールを設定
    when(securityProperties.getCheckTargets())
        .thenReturn(
            List.of(RULE_DRONEPORT_INFO_POST, RULE_DRONEPORT_INFO_PUT, RULE_AIRCRAFT_INFO_POST));

    // Act
    ResourceRoleRule result =
        ReflectionTestUtils.invokeMethod(filter, "findMatchingRule", requestPath, requestMethod);

    // Assert
    assertNotNull(result, "ルールが見つかること");
    assertEquals("/droneport/info", result.getPath(), "パスが一致すること");
    assertEquals("POST", result.getMethod(), "メソッドが一致すること");
    assertEquals(false, result.getRequireSystemOperator(), "requireSystemOperatorが一致すること");
    assertSame(RULE_DRONEPORT_INFO_POST, result, "返却されたルールが期待通りのインスタンスであること");
  }

  /**
   * メソッド名: findMatchingRule<br>
   * 試験名: ワイルドカードパスに一致するルール検索検証<br>
   * 条件: リクエストパスがワイルドカードパターン（/*）にマッチ<br>
   * 結果: ワイルドカードルールが返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testFindMatchingRule_正常系_ワイルドカード付きパスで一致してルールを返却() throws Exception {
    // Arrange
    String requestPath = "/asset/api/droneport/info/123e4567-e89b-12d3-a456-426614174000";
    String requestMethod = "DELETE";

    // モック設定 - ワイルドカードルールを含む
    when(securityProperties.getCheckTargets())
        .thenReturn(
            List.of(RULE_DRONEPORT_INFO_POST, RULE_DRONEPORT_WILDCARD, RULE_AIRCRAFT_INFO_POST));

    // Act
    ResourceRoleRule result =
        ReflectionTestUtils.invokeMethod(filter, "findMatchingRule", requestPath, requestMethod);

    // Assert
    assertNotNull(result, "ルールが見つかること");
    assertEquals("/droneport/info/*", result.getPath(), "ワイルドカードパスが一致すること");
    assertEquals("DELETE", result.getMethod(), "メソッドが一致すること");
    assertEquals(false, result.getRequireSystemOperator(), "requireSystemOperatorが一致すること");
    assertSame(RULE_DRONEPORT_WILDCARD, result, "返却されたルールが期待通りのインスタンスであること");
  }

  /**
   * メソッド名: findMatchingRule<br>
   * 試験名: ワイルドカードが階層深いパスにマッチしない仕様の検証<br>
   * 条件: リクエストパスがワイルドカードよりも2階層以上深い<br>
   * 結果: nullが返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testFindMatchingRule_正常系_ワイルドカード付きパスで階層が深くnullを返却() throws Exception {
    // Arrange
    String requestPath = "/asset/api/droneport/info/test/123e4567-e89b-12d3-a456-426614174000";
    String requestMethod = "DELETE";

    // モック設定 - ワイルドカードルールを含む（/droneport/info/*は1階層のみマッチ）
    when(securityProperties.getCheckTargets())
        .thenReturn(
            List.of(RULE_DRONEPORT_INFO_POST, RULE_DRONEPORT_WILDCARD, RULE_AIRCRAFT_INFO_POST));

    // Act
    ResourceRoleRule result =
        ReflectionTestUtils.invokeMethod(filter, "findMatchingRule", requestPath, requestMethod);

    // Assert
    assertNull(result, "ワイルドカード（*）は1階層のみマッチするため、階層が深い場合はnullを返却すること");
  }

  /**
   * メソッド名: findMatchingRule<br>
   * 試験名: パスが不一致の場合のルール検索検証<br>
   * 条件: リクエストパスが設定ルールのいずれとも一致しない<br>
   * 結果: nullが返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testFindMatchingRule_正常系_パスが不一致でnullを返却() throws Exception {
    // Arrange
    String requestPath = "/asset/api/droneport/info2";
    String requestMethod = "POST";

    // モック設定 - /droneport/infoの設定はあるが、/droneport/info2にはマッチしない
    when(securityProperties.getCheckTargets())
        .thenReturn(
            List.of(RULE_DRONEPORT_INFO_POST, RULE_DRONEPORT_WILDCARD, RULE_AIRCRAFT_INFO_POST));

    // Act
    ResourceRoleRule result =
        ReflectionTestUtils.invokeMethod(filter, "findMatchingRule", requestPath, requestMethod);

    // Assert
    assertNull(result, "パスが一致しない場合はnullを返却すること");
  }

  /**
   * メソッド名: findMatchingRule<br>
   * 試験名: パスは一致するがHTTPメソッドが不一致の場合のルール検索検証<br>
   * 条件: リクエストパスは一致するがHTTPメソッドが設定ルールと異なる<br>
   * 結果: nullが返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testFindMatchingRule_正常系_パスは一致するがメソッドが不一致でnullを返却() throws Exception {
    // Arrange
    String requestPath = "/asset/api/droneport/info";
    String requestMethod = "DELETE";

    // モック設定 - /droneport/infoのPOST・PUTの設定はあるが、DELETEはない
    when(securityProperties.getCheckTargets())
        .thenReturn(
            List.of(RULE_DRONEPORT_INFO_POST, RULE_DRONEPORT_INFO_PUT, RULE_AIRCRAFT_INFO_POST));

    // Act
    ResourceRoleRule result =
        ReflectionTestUtils.invokeMethod(filter, "findMatchingRule", requestPath, requestMethod);

    // Assert
    assertNull(result, "パスは一致するがメソッドが一致しない場合はnullを返却すること");
  }
}
