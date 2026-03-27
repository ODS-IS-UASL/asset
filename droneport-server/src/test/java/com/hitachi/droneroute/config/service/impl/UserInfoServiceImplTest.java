package com.hitachi.droneroute.config.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.config.validator.UserInfoResponseValidator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** UserInfoServiceImplの単体テスト */
@ExtendWith(MockitoExtension.class)
class UserInfoServiceImplTest {

  @Mock private SystemSettings systemSettings;

  @Mock private UserInfoResponseValidator validator;

  private UserInfoServiceImpl userInfoService;
  private ObjectMapper objectMapper;
  private AtomicReference<ClientRequest> capturedRequest;
  private Logger logger;
  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() throws Exception {
    objectMapper = new ObjectMapper();
    capturedRequest = new AtomicReference<>();

    // SystemSettingsのモック設定
    when(systemSettings.getString("user-attribute-api", "url"))
        .thenReturn("http://localhost/api/user-info");
    when(systemSettings.getString("user-attribute-api", "method")).thenReturn("POST");

    // ログキャプチャの設定
    logger = (Logger) LoggerFactory.getLogger(UserInfoServiceImpl.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @AfterEach
  void tearDown() {
    // ログキャプチャのクリーンアップ
    if (logger != null && listAppender != null) {
      logger.detachAppender(listAppender);
    }
  }

  /**
   * メソッド名: getUserInfo<br>
   * 試験名: 外部APIが正常応答を返す基本ケースの動作検証<br>
   * 条件: 外部APIが正常なユーザー情報レスポンスを返す<br>
   * 結果: UserInfoDtoが正しくマッピングされて返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetUserInfo_正常系_外部APIが正常応答で正常終了() throws Exception {
    // Arrange
    String accessToken = "Bearer test-access-token";
    String userId = "123e4567-e89b-12d3-a456-426614174000";

    // レスポンスボディの準備
    String mockResponseBody =
        """
        {
          "attributeList": [
            {
              "user_id": "123e4567-e89b-12d3-a456-426614174000",
              "user_login_id": "login_dammy",
              "operator_name": "operator_dammy",
              "dipsAccountId": "DIPS-ACCT-00012345",
              "dipsAccountName": "DIPS管理者アカウント",
              "phone": "999-9999-9999",
              "updateDatetime": "20260120-123456",
              "attribute": {
                "operatorId": "123e4567-e89b-12d3-a456-426614174100",
                "roles": [
                  {
                    "roleId": "11",
                    "roleName": "航路運営者_担当者"
                  }
                ]
              }
            }
          ]
        }
        """;

    // ExchangeFunctionでレスポンスをスタブ
    ExchangeFunction exchangeFunction =
        request -> {
          capturedRequest.set(request); // リクエストをキャプチャ
          ClientResponse response =
              ClientResponse.create(HttpStatus.OK)
                  .header("Content-Type", "application/json")
                  .body(mockResponseBody)
                  .build();
          return Mono.just(response);
        };

    // WebClientの作成とサービスの初期化
    WebClient.Builder webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction);
    userInfoService =
        new UserInfoServiceImpl(systemSettings, webClientBuilder, objectMapper, validator);

    // Act
    UserInfoDto result = userInfoService.getUserInfo(accessToken, userId);

    // Assert - レスポンスの検証
    assertNotNull(result, "結果がnullでないこと");
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174000", result.getUserOperatorId(), "userOperatorIdが正しいこと");
    assertNotNull(result.getRoles(), "rolesが設定されていること");
    assertFalse(result.getRoles().isEmpty(), "rolesが空でないこと");
    assertEquals("11", result.getRoles().get(0).getRoleId(), "roleIdが正しいこと");
    assertEquals("航路運営者_担当者", result.getRoles().get(0).getRoleName(), "roleNameが正しいこと");
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174100",
        result.getAffiliatedOperatorId(),
        "affiliatedOperatorIdが正しいこと");
    assertFalse(result.isDummyUserFlag(), "DummyUserFlagがfalseであること");

    // Assert - リクエストの検証
    ClientRequest request = capturedRequest.get();
    assertNotNull(request, "リクエストがキャプチャされていること");
    assertEquals("POST", request.method().name(), "HTTPメソッドがPOSTであること");
    assertEquals(
        accessToken, request.headers().getFirst("Authorization"), "Authorizationヘッダーが正しいこと");

    // Assert - Validatorが呼ばれたことを確認
    verify(validator, times(1)).validateResponse(any());
  }

  /**
   * メソッド名: getUserInfo<br>
   * 試験名: 外部APIレスポンスに定義外項目が含まれる場合の動作検証<br>
   * 条件: 外郦APIレスポンスに定義外の項目（test）が含まれる<br>
   * 結果: 定義外項目を無視して正常にUserInfoDtoが返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetUserInfo_正常系_外部APIが正常応答で定義外項目ありで正常終了() throws Exception {
    // Arrange
    String accessToken = "Bearer test-access-token";
    String userId = "123e4567-e89b-12d3-a456-426614174000";

    // レスポンスボディの準備（定義外項目"test"を含む）
    String mockResponseBody =
        """
        {
          "attributeList": [
            {
              "user_id": "123e4567-e89b-12d3-a456-426614174000",
              "user_login_id": "login_dammy",
              "operator_name": "operator_dammy",
              "dipsAccountId": "DIPS-ACCT-00012345",
              "dipsAccountName": "DIPS管理者アカウント",
              "phone": "999-9999-9999",
              "updateDatetime": "20260120-123456",
              "test": "XXXXXX",
              "attribute": {
                "operatorId": "123e4567-e89b-12d3-a456-426614174100",
                "roles": [
                  {
                    "roleId": "11",
                    "roleName": "航路運営者_担当者"
                  }
                ]
              }
            }
          ]
        }
        """;

    // ExchangeFunctionでレスポンスをスタブ
    ExchangeFunction exchangeFunction =
        request -> {
          ClientResponse response =
              ClientResponse.create(HttpStatus.OK)
                  .header("Content-Type", "application/json")
                  .body(mockResponseBody)
                  .build();
          return Mono.just(response);
        };

    // WebClientの作成とサービスの初期化
    WebClient.Builder webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction);
    userInfoService =
        new UserInfoServiceImpl(systemSettings, webClientBuilder, objectMapper, validator);

    // Act
    UserInfoDto result = userInfoService.getUserInfo(accessToken, userId);

    // Assert - レスポンスの検証
    assertNotNull(result, "結果がnullでないこと");
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174000", result.getUserOperatorId(), "userOperatorIdが正しいこと");
    assertNotNull(result.getRoles(), "rolesが設定されていること");
    assertFalse(result.getRoles().isEmpty(), "rolesが空でないこと");
    assertEquals("11", result.getRoles().get(0).getRoleId(), "roleIdが正しいこと");
    assertEquals("航路運営者_担当者", result.getRoles().get(0).getRoleName(), "roleNameが正しいこと");
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174100",
        result.getAffiliatedOperatorId(),
        "affiliatedOperatorIdが正しいこと");
    assertFalse(result.isDummyUserFlag(), "DummyUserFlagがfalseであること");

    // Assert - Validatorが呼ばれたことを確認
    verify(validator, times(1)).validateResponse(any());
  }

  /**
   * メソッド名: getUserInfo<br>
   * 試験名: 所属事業者IDが空文字の場合のuserOperatorId代替動作検証<br>
   * 条件: 外部APIレスポンスのoperatorIdが空文字<br>
   * 結果: affiliatedOperatorIdにuserOperatorIdが設定される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetUserInfo_正常系_外部APIが正常応答で所属事業者IDが空で正常終了() throws Exception {
    // Arrange
    String accessToken = "Bearer test-access-token";
    String userId = "123e4567-e89b-12d3-a456-426614174000";

    // レスポンスボディの準備（operatorIdが空文字）
    String mockResponseBody =
        """
        {
          "attributeList": [
            {
              "user_id": "123e4567-e89b-12d3-a456-426614174000",
              "user_login_id": "login_dammy",
              "operator_name": "operator_dammy",
              "dipsAccountId": "DIPS-ACCT-00012345",
              "dipsAccountName": "DIPS管理者アカウント",
              "phone": "999-9999-9999",
              "updateDatetime": "20260120-123456",
              "attribute": {
                "operatorId": "",
                "roles": [
                  {
                    "roleId": "11",
                    "roleName": "航路運営者_担当者"
                  }
                ]
              }
            }
          ]
        }
        """;

    // ExchangeFunctionでレスポンスをスタブ
    ExchangeFunction exchangeFunction =
        request -> {
          ClientResponse response =
              ClientResponse.create(HttpStatus.OK)
                  .header("Content-Type", "application/json")
                  .body(mockResponseBody)
                  .build();
          return Mono.just(response);
        };

    // WebClientの作成とサービスの初期化
    WebClient.Builder webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction);
    userInfoService =
        new UserInfoServiceImpl(systemSettings, webClientBuilder, objectMapper, validator);

    // Act
    UserInfoDto result = userInfoService.getUserInfo(accessToken, userId);

    // Assert - レスポンスの検証
    assertNotNull(result, "結果がnullでないこと");
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174000", result.getUserOperatorId(), "userOperatorIdが正しいこと");
    assertNotNull(result.getRoles(), "rolesが設定されていること");
    assertFalse(result.getRoles().isEmpty(), "rolesが空でないこと");
    assertEquals("11", result.getRoles().get(0).getRoleId(), "roleIdが正しいこと");
    assertEquals("航路運営者_担当者", result.getRoles().get(0).getRoleName(), "roleNameが正しいこと");
    // operatorIdが空文字の場合、userOperatorIdが使用される
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174000",
        result.getAffiliatedOperatorId(),
        "affiliatedOperatorIdがuserOperatorIdと同じであること");
    assertFalse(result.isDummyUserFlag(), "DummyUserFlagがfalseであること");

    // Assert - Validatorが呼ばれたことを確認
    verify(validator, times(1)).validateResponse(any());
  }

  /**
   * メソッド名: getUserInfo<br>
   * 試験名: 所属事業者IDがnullの場合のuserOperatorId代替動作検証<br>
   * 条件: 外部APIレスポンスのoperatorIdがnull<br>
   * 結果: affiliatedOperatorIdにuserOperatorIdが設定される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetUserInfo_正常系_外部APIが正常応答で所属事業者IDがnullで正常終了() throws Exception {
    // Arrange
    String accessToken = "Bearer test-access-token";
    String userId = "123e4567-e89b-12d3-a456-426614174000";

    // レスポンスボディの準備（operatorIdが明示的にnull）
    String mockResponseBody =
        """
        {
          "attributeList": [
            {
              "user_id": "123e4567-e89b-12d3-a456-426614174000",
              "user_login_id": "login_dammy",
              "operator_name": "operator_dammy",
              "dipsAccountId": "DIPS-ACCT-00012345",
              "dipsAccountName": "DIPS管理者アカウント",
              "phone": "999-9999-9999",
              "updateDatetime": "20260120-123456",
              "attribute": {
                "operatorId": null,
                "roles": [
                  {
                    "roleId": "11",
                    "roleName": "航路運営者_担当者"
                  }
                ]
              }
            }
          ]
        }
        """;

    // ExchangeFunctionでレスポンスをスタブ
    ExchangeFunction exchangeFunction =
        request -> {
          ClientResponse response =
              ClientResponse.create(HttpStatus.OK)
                  .header("Content-Type", "application/json")
                  .body(mockResponseBody)
                  .build();
          return Mono.just(response);
        };

    // WebClientの作成とサービスの初期化
    WebClient.Builder webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction);
    userInfoService =
        new UserInfoServiceImpl(systemSettings, webClientBuilder, objectMapper, validator);

    // Act
    UserInfoDto result = userInfoService.getUserInfo(accessToken, userId);

    // Assert - レスポンスの検証
    assertNotNull(result, "結果がnullでないこと");
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174000", result.getUserOperatorId(), "userOperatorIdが正しいこと");
    assertNotNull(result.getRoles(), "rolesが設定されていること");
    assertFalse(result.getRoles().isEmpty(), "rolesが空でないこと");
    assertEquals("11", result.getRoles().get(0).getRoleId(), "roleIdが正しいこと");
    assertEquals("航路運営者_担当者", result.getRoles().get(0).getRoleName(), "roleNameが正しいこと");
    // operatorIdがnullの場合、userOperatorIdが使用される
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174000",
        result.getAffiliatedOperatorId(),
        "affiliatedOperatorIdがuserOperatorIdと同じであること");
    assertFalse(result.isDummyUserFlag(), "DummyUserFlagがfalseであること");

    // Assert - Validatorが呼ばれたことを確認
    verify(validator, times(1)).validateResponse(any());
  }

  /**
   * メソッド名: getUserInfo<br>
   * 試験名: 所属事業者IDの項目が存在しない場合のuserOperatorId代替動作検証<br>
   * 条件: 外部APIレスポンスにoperatorId項目が存在しない<br>
   * 結果: affiliatedOperatorIdにuserOperatorIdが設定される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetUserInfo_正常系_外部APIが正常応答で所属事業者IDの項目なしで正常終了() throws Exception {
    // Arrange
    String accessToken = "Bearer test-access-token";
    String userId = "123e4567-e89b-12d3-a456-426614174000";

    // レスポンスボディの準備（operatorIdの項目なし）
    String mockResponseBody =
        """
        {
          "attributeList": [
            {
              "user_id": "123e4567-e89b-12d3-a456-426614174000",
              "user_login_id": "login_dammy",
              "operator_name": "operator_dammy",
              "dipsAccountId": "DIPS-ACCT-00012345",
              "dipsAccountName": "DIPS管理者アカウント",
              "phone": "999-9999-9999",
              "updateDatetime": "20260120-123456",
              "attribute": {
                "roles": [
                  {
                    "roleId": "11",
                    "roleName": "航路運営者_担当者"
                  }
                ]
              }
            }
          ]
        }
        """;

    // ExchangeFunctionでレスポンスをスタブ
    ExchangeFunction exchangeFunction =
        request -> {
          ClientResponse response =
              ClientResponse.create(HttpStatus.OK)
                  .header("Content-Type", "application/json")
                  .body(mockResponseBody)
                  .build();
          return Mono.just(response);
        };

    // WebClientの作成とサービスの初期化
    WebClient.Builder webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction);
    userInfoService =
        new UserInfoServiceImpl(systemSettings, webClientBuilder, objectMapper, validator);

    // Act
    UserInfoDto result = userInfoService.getUserInfo(accessToken, userId);

    // Assert - レスポンスの検証
    assertNotNull(result, "結果がnullでないこと");
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174000", result.getUserOperatorId(), "userOperatorIdが正しいこと");
    assertNotNull(result.getRoles(), "rolesが設定されていること");
    assertFalse(result.getRoles().isEmpty(), "rolesが空でないこと");
    assertEquals("11", result.getRoles().get(0).getRoleId(), "roleIdが正しいこと");
    assertEquals("航路運営者_担当者", result.getRoles().get(0).getRoleName(), "roleNameが正しいこと");
    // operatorIdの項目がない場合、userOperatorIdが使用される
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174000",
        result.getAffiliatedOperatorId(),
        "affiliatedOperatorIdがuserOperatorIdと同じであること");
    assertFalse(result.isDummyUserFlag(), "DummyUserFlagがfalseであること");

    // Assert - Validatorが呼ばれたことを確認
    verify(validator, times(1)).validateResponse(any());
  }

  /**
   * メソッド名: getUserInfo<br>
   * 試験名: 必須項目の欠落によるバリデーションエラー発生時の動作検証<br>
   * 条件: 外部APIレスポンスに必須項目（user_id）が欠落し、validatorが例外をスロー<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testGetUserInfo_異常系_外部APIが正常応答で必須項目なしで異常終了() throws Exception {
    // Arrange
    String accessToken = "Bearer test-access-token";
    String userId = "123e4567-e89b-12d3-a456-426614174000";

    // レスポンスボディの準備（user_idの項目なし）
    String mockResponseBody =
        """
        {
          "attributeList": [
            {
              "user_login_id": "login_dammy",
              "operator_name": "operator_dammy",
              "dipsAccountId": "DIPS-ACCT-00012345",
              "dipsAccountName": "DIPS管理者アカウント",
              "phone": "999-9999-9999",
              "updateDatetime": "20260120-123456",
              "attribute": {
                "operatorId": "123e4567-e89b-12d3-a456-426614174100",
                "roles": [
                  {
                    "roleId": "11",
                    "roleName": "航路運営者_担当者"
                  }
                ]
              }
            }
          ]
        }
        """;

    // Validatorがエラーをスローするように設定
    doThrow(new ServiceErrorException("ユーザ情報の取得に失敗しました")).when(validator).validateResponse(any());

    // ExchangeFunctionでレスポンスをスタブ
    ExchangeFunction exchangeFunction =
        request -> {
          ClientResponse response =
              ClientResponse.create(HttpStatus.OK)
                  .header("Content-Type", "application/json")
                  .body(mockResponseBody)
                  .build();
          return Mono.just(response);
        };

    // WebClientの作成とサービスの初期化
    WebClient.Builder webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction);
    userInfoService =
        new UserInfoServiceImpl(systemSettings, webClientBuilder, objectMapper, validator);

    // Act & Assert - ServiceErrorExceptionがスローされることを確認
    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> userInfoService.getUserInfo(accessToken, userId),
            "ServiceErrorExceptionがスローされること");

    assertEquals("ユーザ情報の取得に失敗しました", exception.getMessage(), "エラーメッセージが正しいこと");

    // Assert - Validatorが呼ばれたことを確認
    verify(validator, times(1)).validateResponse(any());
  }

  /**
   * メソッド名: getUserInfo<br>
   * 試験名: 外部APIがエラーレスポンスを返す場合のエラーハンドリング検証<br>
   * 条件: 外部APIがHTTPステータス400とエラー情報を返す<br>
   * 結果: ServiceErrorExceptionがスローされ、エラー情報がログに記録される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testGetUserInfo_異常系_外部APIが異常応答で異常終了() throws Exception {
    // Arrange
    String accessToken = "Bearer test-access-token";
    String userId = "123e4567-e89b-12d3-a456-426614174000";

    // エラーレスポンスボディの準備
    String errorResponseBody =
        """
        {
          "code": "ERR400",
          "errorMessage": "エラーレスポンスのメッセージです。"
        }
        """;

    // ExchangeFunctionでエラーレスポンスをスタブ
    ExchangeFunction exchangeFunction =
        request -> {
          ClientResponse response =
              ClientResponse.create(HttpStatus.BAD_REQUEST)
                  .header("Content-Type", "application/json")
                  .body(errorResponseBody)
                  .build();
          return Mono.just(response);
        };

    // WebClientの作成とサービスの初期化
    WebClient.Builder webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction);
    userInfoService =
        new UserInfoServiceImpl(systemSettings, webClientBuilder, objectMapper, validator);

    // Act & Assert - ServiceErrorExceptionがスローされることを確認
    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> userInfoService.getUserInfo(accessToken, userId),
            "ServiceErrorExceptionがスローされること");

    assertEquals("ユーザ情報の取得に失敗しました", exception.getMessage(), "エラーメッセージが正しいこと");

    // Assert - ログ検証
    List<ILoggingEvent> logsList = listAppender.list;
    assertEquals(1, logsList.size(), "ログが1件出力されること");
    assertEquals(Level.WARN, logsList.get(0).getLevel(), "ログレベルがWARNであること");
    assertTrue(
        logsList.get(0).getFormattedMessage().contains("ユーザー属性API呼び出し失敗"),
        "ログメッセージに「ユーザー属性API呼び出し失敗」が含まれること");
    assertTrue(
        logsList.get(0).getFormattedMessage().contains("ERR400"), "ログメッセージにエラーコード「ERR400」が含まれること");
    assertTrue(
        logsList.get(0).getFormattedMessage().contains("エラーレスポンスのメッセージです。"),
        "ログメッセージにエラーメッセージが含まれること");
  }

  /**
   * メソッド名: getUserInfo<br>
   * 試験名: 外部APIのエラーレスポンスが仕様と異なる場合のエラーハンドリング検証<br>
   * 条件: 外部APIがHTTPステータス400を返すが、エラー情報の項目が存在しない<br>
   * 結果: ServiceErrorExceptionがスローされ、汎用エラーメッセージがログに記録される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void testGetUserInfo_異常系_外部APIが異常応答で応答内容が仕様通りでなく異常終了() throws Exception {
    // Arrange
    String accessToken = "Bearer test-access-token";
    String userId = "123e4567-e89b-12d3-a456-426614174000";

    // エラーレスポンスボディの準備（項目なし）
    String errorResponseBody = "{}";

    // ExchangeFunctionでエラーレスポンスをスタブ
    ExchangeFunction exchangeFunction =
        request -> {
          ClientResponse response =
              ClientResponse.create(HttpStatus.BAD_REQUEST)
                  .header("Content-Type", "application/json")
                  .body(errorResponseBody)
                  .build();
          return Mono.just(response);
        };

    // WebClientの作成とサービスの初期化
    WebClient.Builder webClientBuilder = WebClient.builder().exchangeFunction(exchangeFunction);
    userInfoService =
        new UserInfoServiceImpl(systemSettings, webClientBuilder, objectMapper, validator);

    // Act & Assert - ServiceErrorExceptionがスローされることを確認
    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> userInfoService.getUserInfo(accessToken, userId),
            "ServiceErrorExceptionがスローされること");

    assertEquals("ユーザ情報の取得に失敗しました", exception.getMessage(), "エラーメッセージが正しいこと");

    // Assert - ログ検証
    List<ILoggingEvent> logsList = listAppender.list;
    assertEquals(1, logsList.size(), "ログが1件出力されること");
    assertEquals(Level.WARN, logsList.get(0).getLevel(), "ログレベルがWARNであること");
    assertTrue(
        logsList.get(0).getFormattedMessage().contains("ユーザー属性API呼び出し失敗"),
        "ログメッセージに「ユーザー属性API呼び出し失敗」が含まれること");
  }

  /**
   * メソッド名: getUserInfo<br>
   * 試験名: ユーザーが複数のロールを持つ場合のマッピング動作検証<br>
   * 条件: 外部APIレスポンスに3つのロールが含まれる<br>
   * 結果: 全てのロールが正しくUserInfoDtoにマッピングされる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetUserInfo_正常系_外部APIが正常応答でロールが複数() throws Exception {
    // Arrange
    String accessToken = "Bearer test-access-token";
    String userId = "123e4567-e89b-12d3-a456-426614174000";

    // レスポンスボディの準備（ロールが複数）
    String mockResponseBody =
        """
        {
          "attributeList": [
            {
              "user_id": "123e4567-e89b-12d3-a456-426614174000",
              "user_login_id": "login_dammy",
              "operator_name": "operator_dammy",
              "dipsAccountId": "DIPS-ACCT-00012345",
              "dipsAccountName": "DIPS管理者アカウント",
              "phone": "999-9999-9999",
              "updateDatetime": "20260120-123456",
              "attribute": {
                "operatorId": "123e4567-e89b-12d3-a456-426614174100",
                "roles": [
                  { "roleId": "1", "roleName": "航路運営者" },
                  { "roleId": "11", "roleName": "航路運営者_担当者" },
                  { "roleId": "21", "roleName": "運航事業者_担当者" }
                ]
              }
            }
          ]
        }
        """;

    // ExchangeFunctionでレスポンスをスタブ
    ExchangeFunction exchangeFunctionMulti =
        request -> {
          ClientResponse response =
              ClientResponse.create(HttpStatus.OK)
                  .header("Content-Type", "application/json")
                  .body(mockResponseBody)
                  .build();
          return Mono.just(response);
        };

    // WebClientの作成とサービスの初期化
    WebClient.Builder webClientBuilderMulti =
        WebClient.builder().exchangeFunction(exchangeFunctionMulti);
    userInfoService =
        new UserInfoServiceImpl(systemSettings, webClientBuilderMulti, objectMapper, validator);

    // Act
    UserInfoDto result = userInfoService.getUserInfo(accessToken, userId);

    // Assert - レスポンスの検証
    assertNotNull(result, "結果がnullでないこと");
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174000", result.getUserOperatorId(), "userOperatorIdが正しいこと");
    assertNotNull(result.getRoles(), "rolesが設定されていること");
    assertEquals(3, result.getRoles().size(), "rolesが3件であること");
    assertEquals("1", result.getRoles().get(0).getRoleId(), "roleId1が正しいこと");
    assertEquals("航路運営者", result.getRoles().get(0).getRoleName(), "roleName1が正しいこと");
    assertEquals("11", result.getRoles().get(1).getRoleId(), "roleId2が正しいこと");
    assertEquals("航路運営者_担当者", result.getRoles().get(1).getRoleName(), "roleName2が正しいこと");
    assertEquals("21", result.getRoles().get(2).getRoleId(), "roleId3が正しいこと");
    assertEquals("運航事業者_担当者", result.getRoles().get(2).getRoleName(), "roleName3が正しいこと");
    assertEquals(
        "123e4567-e89b-12d3-a456-426614174100",
        result.getAffiliatedOperatorId(),
        "affiliatedOperatorIdが正しいこと");
    assertFalse(result.isDummyUserFlag(), "DummyUserFlagがfalseであること");

    // Assert - Validatorが呼ばれたことを確認
    verify(validator, times(1)).validateResponse(any());
  }
}
