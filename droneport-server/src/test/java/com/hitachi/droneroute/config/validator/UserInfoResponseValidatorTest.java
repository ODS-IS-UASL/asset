package com.hitachi.droneroute.config.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.config.dto.UserAttributeApiResponseDto;
import com.hitachi.droneroute.config.dto.UserAttributeApiResponseDto.Attribute;
import com.hitachi.droneroute.config.dto.UserAttributeApiResponseDto.AttributeDto;
import com.hitachi.droneroute.config.dto.UserAttributeApiResponseDto.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;

/** UserInfoResponseValidatorの単体テスト */
class UserInfoResponseValidatorTest {

  private UserInfoResponseValidator validator;
  private Logger logger;
  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    validator = new UserInfoResponseValidator();

    // ログキャプチャの設定
    logger = (Logger) LoggerFactory.getLogger(UserInfoResponseValidator.class);
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

  /** 正常系テストケース */
  static Stream<Arguments> validCases() {
    return Stream.of(
        Arguments.of(
            "全項目正常", (Supplier<UserAttributeApiResponseDto>) () -> createValidResponseDto()),
        Arguments.of(
            "roleNameがnull",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).getAttribute().getRoles().get(0).setRoleName(null);
                  return dto;
                }),
        Arguments.of(
            "roleNameが空文字",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).getAttribute().getRoles().get(0).setRoleName("");
                  return dto;
                }),
        Arguments.of(
            "operatorIdがnull",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).getAttribute().setOperatorId(null);
                  return dto;
                }),
        Arguments.of(
            "operatorIdが空文字",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).getAttribute().setOperatorId("");
                  return dto;
                }));
  }

  /** 異常系テストケース */
  static Stream<Arguments> invalidCases() {
    return Stream.of(
        Arguments.of(
            "responseDtoがnull",
            (Supplier<UserAttributeApiResponseDto>) () -> null,
            "ユーザ属性取得結果が空です"),
        Arguments.of(
            "attributeListがnull",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = new UserAttributeApiResponseDto();
                  dto.setAttributeList(null);
                  return dto;
                },
            "ユーザ属性取得結果が空です"),
        Arguments.of(
            "attributeListが空",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = new UserAttributeApiResponseDto();
                  dto.setAttributeList(new ArrayList<>());
                  return dto;
                },
            "ユーザ属性取得結果が空です"),
        Arguments.of(
            "userIdがnull",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).setUser_id(null);
                  return dto;
                },
            "ユーザ属性取得結果のユーザのオペレーターID"),
        Arguments.of(
            "userIdが空文字",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).setUser_id("");
                  return dto;
                },
            "ユーザ属性取得結果のユーザのオペレーターID"),
        Arguments.of(
            "userIdがUUID以外",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).setUser_id("invalid-uuid");
                  return dto;
                },
            "ユーザ属性取得結果のユーザのオペレーターID"),
        Arguments.of(
            "attributeがnull",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).setAttribute(null);
                  return dto;
                },
            "ユーザ属性取得結果の付加情報"),
        Arguments.of(
            "operatorIdがUUID以外",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).getAttribute().setOperatorId("invalid-uuid");
                  return dto;
                },
            "ユーザ属性取得結果の所属事業者のオペレーターID"),
        Arguments.of(
            "rolesがnull",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).getAttribute().setRoles(null);
                  return dto;
                },
            "ユーザ属性取得結果のロール情報"),
        Arguments.of(
            "rolesが空リスト",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).getAttribute().setRoles(new ArrayList<>());
                  return dto;
                },
            "ユーザ属性取得結果のロール情報が空です"),
        Arguments.of(
            "roles[0]のroleIdがnull",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).getAttribute().getRoles().get(0).setRoleId(null);
                  return dto;
                },
            "ユーザ属性取得結果のロール[0]のロールID"),
        Arguments.of(
            "roles[0]のroleIdが空文字",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  dto.getAttributeList().get(0).getAttribute().getRoles().get(0).setRoleId("");
                  return dto;
                },
            "ユーザ属性取得結果のロール[0]のロールID"),

        // ロールが複数ある場合の異常系: 要素1のroleIdがnull, 要素2のroleIdが空文字
        Arguments.of(
            "roles[0]のroleIdがnull, roles[1]のroleIdが空文字",
            (Supplier<UserAttributeApiResponseDto>)
                () -> {
                  UserAttributeApiResponseDto dto = createValidResponseDto();
                  List<Role> roles = new ArrayList<>();
                  Role nullRoleId = new Role();
                  nullRoleId.setRoleId(null);
                  nullRoleId.setRoleName("テストロール1");
                  roles.add(nullRoleId); // 要素1: roleId=null
                  Role emptyRoleId = new Role();
                  emptyRoleId.setRoleId("");
                  emptyRoleId.setRoleName("テストロール2");
                  roles.add(emptyRoleId); // 要素2: roleId=""
                  dto.getAttributeList().get(0).getAttribute().setRoles(roles);
                  return dto;
                },
            "ユーザ属性取得結果のロール[0]のロールID"));
  }

  /**
   * メソッド名: validateResponse<br>
   * 試験名: 正常系の様々なパターンでのバリデーション動作検証<br>
   * 条件: 必須項目を含む正常なレスポンスDTO（オプション項目はnullや空文字を含む）<br>
   * 結果: 例外が発生せず、正常に処理が完了する<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("validCases")
  void testValidateResponse_正常系(String caseName, Supplier<UserAttributeApiResponseDto> supplier) {
    // Arrange
    UserAttributeApiResponseDto responseDto = supplier.get();

    // Act & Assert - 例外が発生しないことを確認
    assertDoesNotThrow(() -> validator.validateResponse(responseDto), "例外が発生しないこと");

    // Assert - ログが出力されていないことを確認
    List<ILoggingEvent> logsList = listAppender.list;
    assertEquals(0, logsList.size(), "ログが出力されていないこと");
  }

  /**
   * メソッド名: validateResponse<br>
   * 試験名: 異常系の様々なパターンでのバリデーションエラー動作検証<br>
   * 条件: 必須項目が欠落または不正な値を含むレスポンスDTO<br>
   * 結果: ServiceErrorExceptionがスローされ、エラーメッセージとログが出力される<br>
   * テストパターン: 異常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("invalidCases")
  void testValidateResponse_異常系(
      String caseName, Supplier<UserAttributeApiResponseDto> supplier, String expectedLogMessage) {
    // Arrange
    UserAttributeApiResponseDto responseDto = supplier.get();

    // Act & Assert - ServiceErrorExceptionがスローされることを確認
    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> validator.validateResponse(responseDto),
            "ServiceErrorExceptionがスローされること");

    assertEquals("ユーザ情報の取得に失敗しました", exception.getMessage(), "エラーメッセージが正しいこと");

    // Assert - ログ検証
    List<ILoggingEvent> logsList = listAppender.list;
    assertTrue(logsList.size() >= 1, "ログが1件以上出力されること");
    boolean found =
        logsList.stream()
            .anyMatch(
                log ->
                    log.getLevel() == Level.WARN
                        && log.getFormattedMessage().contains(expectedLogMessage));
    assertTrue(found, "ログメッセージに「" + expectedLogMessage + "」が含まれること");
  }

  /** 正常なレスポンスDTOを作成する */
  private static UserAttributeApiResponseDto createValidResponseDto() {
    UserAttributeApiResponseDto responseDto = new UserAttributeApiResponseDto();

    AttributeDto attributeDto = new AttributeDto();
    attributeDto.setUser_id("123e4567-e89b-12d3-a456-426614174000");
    attributeDto.setUser_login_id("login_dammy");
    attributeDto.setOperator_name("operator_dammy");
    attributeDto.setDipsAccountId("DIPS-ACCT-00012345");
    attributeDto.setDipsAccountName("DIPS管理者アカウント");
    attributeDto.setPhone("999-9999-9999");
    attributeDto.setUpdateDatetime("20260120-123456");

    Attribute attribute = new Attribute();
    attribute.setOperatorId("123e4567-e89b-12d3-a456-426614174100");

    Role role = new Role();
    role.setRoleId("11");
    role.setRoleName("航路運営者_担当者");

    attribute.setRoles(List.of(role));
    attributeDto.setAttribute(attribute);

    List<AttributeDto> attributeList = new ArrayList<>();
    attributeList.add(attributeDto);
    responseDto.setAttributeList(attributeList);

    return responseDto;
  }
}
