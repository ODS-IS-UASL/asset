package com.hitachi.droneroute.arm.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoRequestDto;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** AircraftReserveInfoValidatorクラスの単体テスト */
public class AircraftReserveInfoValidatorTest {

  private AircraftReserveInfoValidator validator;

  @BeforeEach
  public void setUp() {
    validator = new AircraftReserveInfoValidator();
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 機体予約情報登録APIパラメータチェックの正常系テスト<br>
   * 条件: 正常なAircraftReserveInfoRequestDtoを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForRegist_Normal() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    dto.setAircraftReservationId(UUID.randomUUID().toString());
    dto.setGroupReservationId(UUID.randomUUID().toString());
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom(ZonedDateTime.now().toString());
    dto.setReservationTimeTo(ZonedDateTime.now().toString());
    // dto.setOperatorId("operatorId");

    assertDoesNotThrow(() -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 機体予約情報登録APIパラメータチェックの異常系テスト<br>
   * 条件: AircraftIdがnullのAircraftReserveInfoRequestDtoを渡す<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForRegist_AircraftIdNull() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    dto.setReservationTimeFrom(ZonedDateTime.now().toString());
    dto.setReservationTimeTo(ZonedDateTime.now().toString());

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 機体予約情報登録APIパラメータチェックの異常系テスト<br>
   * 条件: GroupReservationIdがnullのAircraftReserveInfoRequestDtoを渡す<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForRegist_GroupReservationIdNull() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    dto.setAircraftReservationId(UUID.randomUUID().toString());
    dto.setGroupReservationId(null);
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom(ZonedDateTime.now().toString());
    dto.setReservationTimeTo(ZonedDateTime.now().toString());

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));

    assertEquals("[一括予約IDに値が設定されていません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 機体予約情報登録APIパラメータチェックの異常系テスト<br>
   * 条件: GroupReservationIdが空文字のAircraftReserveInfoRequestDtoを渡す<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForRegist_GroupReservationId空文字() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    dto.setAircraftReservationId(UUID.randomUUID().toString());
    dto.setGroupReservationId("");
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom(ZonedDateTime.now().toString());
    dto.setReservationTimeTo(ZonedDateTime.now().toString());

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));

    assertEquals("[一括予約IDに値が設定されていません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 機体予約情報登録APIパラメータチェックの異常系テスト<br>
   * 条件: GroupReservationIdがUUID以外のAircraftReserveInfoRequestDtoを渡す<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForRegist_GroupReservationIdUUID以外() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    dto.setAircraftReservationId(UUID.randomUUID().toString());
    dto.setGroupReservationId("notUUID");
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom(ZonedDateTime.now().toString());
    dto.setReservationTimeTo(ZonedDateTime.now().toString());

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));

    assertEquals("[一括予約IDがUUIDではありません。\n入力値:notUUID]", exception.getMessage());
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 機体予約情報更新APIパラメータチェックの正常系テスト<br>
   * 条件: 正常なAircraftReserveInfoRequestDtoを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForUpdate_Normal() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    dto.setAircraftReservationId(UUID.randomUUID().toString());
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom(ZonedDateTime.now().toString());
    dto.setReservationTimeTo(ZonedDateTime.now().toString());
    // dto.setOperatorId("operatorId");

    assertDoesNotThrow(() -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 機体予約情報更新APIパラメータチェックの異常系テスト<br>
   * 条件: AircraftReservationIdがnullのAircraftReserveInfoRequestDtoを渡す<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForUpdate_AircraftReservationIdNull() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom(ZonedDateTime.now().toString());
    dto.setReservationTimeTo(ZonedDateTime.now().toString());

    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体予約情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 正常なAircraftReserveInfoListRequestDtoを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_Normal() {
    AircraftReserveInfoListRequestDto dto = new AircraftReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setAircraftName("TestName");
    dto.setTimeFrom(ZonedDateTime.now().toString());
    dto.setTimeTo(ZonedDateTime.now().toString());

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体予約情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: ページング系パラメータを含む正常なAircraftReserveInfoListRequestDtoを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_Normal2() {
    AircraftReserveInfoListRequestDto dto = new AircraftReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setAircraftName("TestName");
    dto.setTimeFrom(ZonedDateTime.now().toString());
    dto.setTimeTo(ZonedDateTime.now().toString());
    dto.setPerPage("1");
    dto.setPage("1");
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体予約情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: ソート順とソートカラムの数が一致しないAircraftReserveInfoListRequestDtoを渡す<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_NG_NotEqual_OrderAndColumns() {
    AircraftReserveInfoListRequestDto dto = new AircraftReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setAircraftName("TestName");
    dto.setTimeFrom(ZonedDateTime.now().toString());
    dto.setTimeTo(ZonedDateTime.now().toString());
    dto.setPerPage("1");
    dto.setPage("1");
    dto.setSortOrders("1, 0");
    dto.setSortColumns("aircraftId");

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体予約情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 1ページ当たりの件数が入力可能値の範囲外の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_page1_throwsException() {
    AircraftReserveInfoListRequestDto dto = new AircraftReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setAircraftName("TestName");
    dto.setTimeFrom(ZonedDateTime.now().toString());
    dto.setTimeTo(ZonedDateTime.now().toString());
    dto.setPerPage("101");
    dto.setPage("1");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(
        MessageFormat.format("[{0}の値が不正です。\n最小値({1})、最大値({2})]", "1ページ当たりの件数", 1, 100),
        exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体予約情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 現在ページ番号が入力可能値の範囲外の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_page2_throwsException() {
    AircraftReserveInfoListRequestDto dto = new AircraftReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setAircraftName("TestName");
    dto.setTimeFrom(ZonedDateTime.now().toString());
    dto.setTimeTo(ZonedDateTime.now().toString());
    dto.setPerPage("1");
    dto.setPage("0");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(
        MessageFormat.format("[{0}の値が不正です。\n最小値({1})、最大値({2})]", "現在ページ番号", 1, Integer.MAX_VALUE),
        exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体予約情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 1ページ当たりの件数が未設定、現在ページ番号が設定ありの場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_page3_throwsException() {
    AircraftReserveInfoListRequestDto dto = new AircraftReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setAircraftName("TestName");
    dto.setTimeFrom(ZonedDateTime.now().toString());
    dto.setTimeTo(ZonedDateTime.now().toString());
    dto.setPerPage(null);
    dto.setPage("1");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(MessageFormat.format("[{0}に値が設定されていません。]", "1ページ当たりの件数"), exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体予約情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 1ページ当たりの件数が設定あり、現在ページ番号が未設定の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_page4_throwsException() {
    AircraftReserveInfoListRequestDto dto = new AircraftReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setAircraftName("TestName");
    dto.setTimeFrom(ZonedDateTime.now().toString());
    dto.setTimeTo(ZonedDateTime.now().toString());
    dto.setPerPage("1");
    dto.setPage(null);

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(MessageFormat.format("[{0}に値が設定されていません。]", "現在ページ番号"), exception.getMessage());
  }

  /**
   * メソッド名: validateForRegist<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   */
  static Stream<Arguments> nmlDlCase() {
    return Stream.of(
        Arguments.of(
            "Nomal",
            (Supplier<AircraftReserveInfoListRequestDto>)
                () -> {
                  AircraftReserveInfoListRequestDto dto = createAircraftReserveInfoListRequestDto();
                  return dto;
                }),
        Arguments.of(
            "一括予約IDnull",
            (Supplier<AircraftReserveInfoListRequestDto>)
                () -> {
                  AircraftReserveInfoListRequestDto dto = createAircraftReserveInfoListRequestDto();
                  dto.setGroupReservationId(null);
                  return dto;
                }),
        Arguments.of(
            "一括予約ID空文字",
            (Supplier<AircraftReserveInfoListRequestDto>)
                () -> {
                  AircraftReserveInfoListRequestDto dto = createAircraftReserveInfoListRequestDto();
                  dto.setGroupReservationId("");
                  return dto;
                }),
        Arguments.of(
            "予約事業者IDnull",
            (Supplier<AircraftReserveInfoListRequestDto>)
                () -> {
                  AircraftReserveInfoListRequestDto dto = createAircraftReserveInfoListRequestDto();
                  dto.setReserveProviderId(null);
                  return dto;
                }),
        Arguments.of(
            "予約事業者ID空文字",
            (Supplier<AircraftReserveInfoListRequestDto>)
                () -> {
                  AircraftReserveInfoListRequestDto dto = createAircraftReserveInfoListRequestDto();
                  dto.setReserveProviderId("");
                  return dto;
                }));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 正常系の様々なパターンでのバリデーション動作検証<br>
   * 条件: 一括予約IDまたは予約事業者IDがnull/空文字を含むリクエストDTO<br>
   * 結果: 例外が発生せず、正常に処理が完了する<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("nmlDlCase")
  public void testValidateForUpdate_nmlDlCase(
      String caseName, Supplier<AircraftReserveInfoListRequestDto> sDto) {
    AircraftReserveInfoListRequestDto dto = sDto.get();
    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForDownloadFile<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第4引数に検証対象のdto<br>
   * 第3引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> errDlCase() {
    return Stream.of(
        Arguments.of(
            "一括予約IDUUID以外",
            (Supplier<AircraftReserveInfoListRequestDto>)
                () -> {
                  AircraftReserveInfoListRequestDto dto = createAircraftReserveInfoListRequestDto();
                  dto.setGroupReservationId("notUUID");
                  return dto;
                },
            "[一括予約IDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "予約事業者IDUUID以外",
            (Supplier<AircraftReserveInfoListRequestDto>)
                () -> {
                  AircraftReserveInfoListRequestDto dto = createAircraftReserveInfoListRequestDto();
                  dto.setReserveProviderId("notUUID");
                  return dto;
                },
            "[予約事業者IDがUUIDではありません。\n入力値:notUUID]"));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 異常系の様々なパターンでのバリデーションエラー動作検証<br>
   * 条件: 一括予約IDまたは予約事業者IDにUUID以外の値を含むリクエストDTO<br>
   * 結果: ValidationErrorExceptionがスローされ、エラーメッセージが検証される<br>
   * テストパターン: 異常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("errDlCase")
  public void testValidateForUpdate_errDlCase(
      String caseName, Supplier<AircraftReserveInfoListRequestDto> sDto, String msg) {
    AircraftReserveInfoListRequestDto dto = sDto.get();
    Exception ex =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
    assertEquals(msg, ex.getMessage());
  }

  /**
   * メソッド名: validateForDetail<br>
   * 試験名: 機体予約情報詳細APIパラメータチェックの正常系テスト<br>
   * 条件: 正常なaircraftReserveIdを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForDetail_Normal() {
    String aircraftReserveId = UUID.randomUUID().toString();

    assertDoesNotThrow(() -> validator.validateForDetail(aircraftReserveId));
  }

  /**
   * メソッド名: validateForDetail<br>
   * 試験名: 機体予約情報詳細APIパラメータチェックの異常系テスト<br>
   * 条件: aircraftReserveIdがnull<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForDetail_AircraftReserveIdNull() {
    String aircraftReserveId = null;

    assertThrows(
        ValidationErrorException.class, () -> validator.validateForDetail(aircraftReserveId));
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ */
  private static AircraftReserveInfoRequestDto createAircraftReserveInfoRequestDto() {
    AircraftReserveInfoRequestDto ret = new AircraftReserveInfoRequestDto();
    ret.setAircraftReservationId("0a0711a5-ff74-4164-9309-8888b433cf11");
    ret.setGroupReservationId("0a0711a5-ff74-4164-9309-8888b433cf21");
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf31");
    ret.setReservationTimeFrom("2026-01-01T10:00:00+09:00");
    ret.setReservationTimeTo("2026-01-01T12:00:00+09:00");

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ */
  private static AircraftReserveInfoListRequestDto createAircraftReserveInfoListRequestDto() {
    AircraftReserveInfoListRequestDto dto = new AircraftReserveInfoListRequestDto();
    dto.setGroupReservationId("0a0711a5-ff74-4164-9309-8888b433cf21");
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf31");
    dto.setAircraftName("機体1");
    dto.setTimeFrom("2026-01-01T10:00:00+09:00");
    dto.setTimeTo("2026-01-01T12:00:00+09:00");
    dto.setReserveProviderId("0a0711a5-ff74-4164-9309-8888b433cf41");

    return dto;
  }
}
