package com.hitachi.droneroute.dpm.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hitachi.droneroute.arm.repository.AircraftReserveInfoRepository;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

/** DronePortReserveInfoValidatorクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class DronePortReserveInfoValidatorTest {

  @Autowired private DronePortReserveInfoValidator validator;

  @SpyBean private CodeMaster codeMaster;

  @SpyBean private SystemSettings systemSettings;

  @MockBean private AircraftReserveInfoRepository aircraftReserveInfoRepository;

  /**
   * メソッド名: validateForRegister<br>
   * 試験名: validateForRegisterメソッドの正常系テスト<br>
   * 条件: 全ての必須項目が設定されている<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForRegister_validInput_noException() {
    DronePortReserveInfoRegisterListRequestDto dto = createValidRegisterDto();
    // dto.setOperatorId("dummyOperator");
    assertDoesNotThrow(() -> validator.validateForRegister(dto));
  }

  /**
   * メソッド名: validateForRegister<br>
   * 試験名: validateForRegisterメソッドの異常系テスト<br>
   * 条件: 必須項目が設定されていない<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegister_missingRequiredFields_throwsException() {
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    assertThrows(ValidationErrorException.class, () -> validator.validateForRegister(dto));
  }

  /**
   * メソッド名: validateForRegister<br>
   * 試験名: validateForRegisterメソッドの異常系テスト<br>
   * 条件: 予約開始日時 > 予約終了日時となっている<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegister_compareDateTime_throwsException() {
    DronePortReserveInfoRegisterListRequestDto dto = createInValidRegisterDto();
    // dto.setOperatorId("dummyOperator");
    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegister(dto));

    log.info(exception.getMessage());
  }

  /**
   * メソッド名: validateForRegister<br>
   * 試験名: validateForRegisterメソッドの正常系テスト<br>
   * 条件: 一括予約IDが設定されている<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForRegister_groupReservationId_noException() {
    DronePortReserveInfoRegisterListRequestDto dto = createDronePortReserveRegisterDto();

    assertDoesNotThrow(() -> validator.validateForRegister(dto));
  }

  /**
   * メソッド名: validateForRegister<br>
   * 試験名: validateForRegisterメソッドの異常系テスト<br>
   * 条件: 一括予約IDにnullが設定されている<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegister_groupReservationIdnull_throwsException() {
    DronePortReserveInfoRegisterListRequestDto dto = createDronePortReserveRegisterDto();
    dto.getData().get(0).setGroupReservationId(null);
    dto.getData().get(2).setGroupReservationId(null);

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegister(dto));

    assertEquals("[一括予約ID[0]に値が設定されていません。, 一括予約ID[2]に値が設定されていません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForRegister<br>
   * 試験名: validateForRegisterメソッドの異常系テスト<br>
   * 条件: 一括予約IDに空文字が設定されている<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegister_groupReservationId空文字_throwsException() {
    DronePortReserveInfoRegisterListRequestDto dto = createDronePortReserveRegisterDto();
    dto.getData().get(0).setGroupReservationId("");
    dto.getData().get(2).setGroupReservationId("");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegister(dto));

    assertEquals("[一括予約ID[0]に値が設定されていません。, 一括予約ID[2]に値が設定されていません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForRegister<br>
   * 試験名: validateForRegisterメソッドの異常系テスト<br>
   * 条件: 一括予約IDにUUID以外の文字列が設定されている<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegister_groupReservationIdnotUUID_throwsException() {
    DronePortReserveInfoRegisterListRequestDto dto = createDronePortReserveRegisterDto();
    dto.getData().get(0).setGroupReservationId("notUUID");
    dto.getData().get(2).setGroupReservationId("notUUID");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegister(dto));

    assertEquals(
        "[一括予約ID[0]がUUIDではありません。\n入力値:notUUID, 一括予約ID[2]がUUIDではありません。\n入力値:notUUID]",
        exception.getMessage());
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: validateForUpdateメソッドの正常系テスト<br>
   * 条件: 全ての必須項目が設定されている<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForUpdate_validInput_noException() {
    DronePortReserveInfoUpdateRequestDto dto = createValidUpdateDto();
    dto.setDronePortReservationId(UUID.randomUUID().toString());
    assertDoesNotThrow(() -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: validateForUpdateメソッドの異常系テスト<br>
   * 条件: 登録時必須項目が空文字<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_missingRequiredFields_throwsException() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortReservationId(UUID.randomUUID().toString());
    dto.setDronePortId("");
    dto.setReservationTimeFrom("");
    dto.setReservationTimeTo("");
    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: validateForUpdateメソッドの異常系テスト<br>
   * 条件: 必須項目が設定されていない。離着陸場予約ID未設定。<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_missingRequiredFields_throwsException2() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: validateForUpdateメソッドの異常系テスト<br>
   * 条件: 予約開始日時 > 予約終了日時となっている<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_compareDateTime_throwsException() {
    DronePortReserveInfoUpdateRequestDto dto = createInValidUpdateDto();
    dto.setDronePortReservationId(UUID.randomUUID().toString());
    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));

    log.info(exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの正常系テスト<br>
   * 条件: 全ての必須項目が設定されている<br>
   * 　　　　　ソート順に設定あり、ソート対象列名に設定ありで、設定個数が一致する<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_validInput_noException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPerPage("1");
    dto.setPage("1");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの正常系テスト<br>
   * 条件: 全ての必須項目が設定されている<br>
   * 　　　　　ソート順、ソート対象列名設定なし<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_validInput2_noException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders(null);
    dto.setSortColumns(null);

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの異常系テスト<br>
   * 条件: ソート順に設定あり、ソート対象列名に設定ありで、設定個数が不一致の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_sort1_throwsException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders("1");
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ソート順とソート対象列の設定数が一致しません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの異常系テスト<br>
   * 条件: ソート順に設定なし、ソート対象列名に設定ありで、設定個数が不一致の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_sort2_throwsException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders(null);
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ソート順とソート対象列の設定数が一致しません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの異常系テスト<br>
   * 条件: ソート順に設定あり、ソート対象列名に設定なしで、設定個数が不一致の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_sort3_throwsException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders("0,0,0");
    dto.setSortColumns(null);

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ソート順とソート対象列の設定数が一致しません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの異常系テスト<br>
   * 条件: ソート順の設定値が範囲外の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_sort4_throwsException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders("1,0,2");
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ソート順の値が不正です。\n範囲[0, 1]]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 1ページ当たりの件数が、入力可能値の範囲外の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_page1_throwsException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
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
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 現在ページ番号が、入力可能値の範囲外の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_page2_throwsException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
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
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 1ページ当たりの件数が未設定、現在ページ番号が設定ありの場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_page3_throwsException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPerPage(null);
    dto.setPage("1");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(MessageFormat.format("[{0}に値が設定されていません。]", "1ページ当たりの件数"), exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 1ページ当たりの件数が設定あり、現在ページ番号が未設定の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_page4_throwsException() {
    DronePortReserveInfoListRequestDto dto = createValidListDto();
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPerPage("1");
    dto.setPage(null);

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(MessageFormat.format("[{0}に値が設定されていません。]", "現在ページ番号"), exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの正常系テスト<br>
   * 条件: 一括予約ID、予約事業者IDが設定されている<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_groupReservationIdreserveProviderId_noException() {
    DronePortReserveInfoListRequestDto dto = createDronePortReserveInfoListRequestDto();

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの正常系テスト<br>
   * 条件: 一括予約IDにnullが設定されている<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_groupReservationIdnull_noException() {
    DronePortReserveInfoListRequestDto dto = createDronePortReserveInfoListRequestDto();
    dto.setGroupReservationId(null);

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの正常系テスト<br>
   * 条件: 一括予約IDに空文字が設定されている<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_groupReservationId空文字_noException() {
    DronePortReserveInfoListRequestDto dto = createDronePortReserveInfoListRequestDto();
    dto.setGroupReservationId("");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの異常系テスト<br>
   * 条件: 一括予約IDにUUID以外の文字列が設定されている<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_groupReservationIdnotUUID_throwsException() {
    DronePortReserveInfoListRequestDto dto = createDronePortReserveInfoListRequestDto();
    dto.setGroupReservationId("notUUID");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[一括予約IDがUUIDではありません。\n入力値:notUUID]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの正常系テスト<br>
   * 条件: 予約事業者IDにnullが設定されている<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_reserveProviderIdnull_noException() {
    DronePortReserveInfoListRequestDto dto = createDronePortReserveInfoListRequestDto();
    dto.setReserveProviderId(null);

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの正常系テスト<br>
   * 条件: 予約事業者IDに空文字が設定されている<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_reserveProviderId空文字_noException() {
    DronePortReserveInfoListRequestDto dto = createDronePortReserveInfoListRequestDto();
    dto.setReserveProviderId("");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: validateForGetListメソッドの異常系テスト<br>
   * 条件: 予約事業者IDにUUID以外の文字列が設定されている<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_reserveProviderIdnotUUID_throwsException() {
    DronePortReserveInfoListRequestDto dto = createDronePortReserveInfoListRequestDto();
    dto.setReserveProviderId("notUUID");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[予約事業者IDがUUIDではありません。\n入力値:notUUID]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetDetail<br>
   * 試験名: validateForGetDetailメソッドの正常系テスト<br>
   * 条件: 有効な予約IDが設定されている<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetDetail_validInput_noException() {
    String reserveId = UUID.randomUUID().toString();
    assertDoesNotThrow(() -> validator.validateForGetDetail(reserveId));
  }

  /**
   * メソッド名: validateForGetDetail<br>
   * 試験名: validateForGetDetailメソッドの異常系テスト<br>
   * 条件: 無効な予約IDが設定されている<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetDetail_invalidInput_throwsException() {
    String reserveId = "";
    assertThrows(ValidationErrorException.class, () -> validator.validateForGetDetail(reserveId));
  }

  private DronePortReserveInfoRegisterListRequestDto createValidRegisterDto() {
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setGroupReservationId(UUID.randomUUID().toString());
    element.setDronePortId(UUID.randomUUID().toString());
    element.setAircraftId(UUID.randomUUID().toString());
    element.setRouteReservationId(UUID.randomUUID().toString());
    element.setUsageType(1);
    element.setReservationTimeFrom(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
    element.setReservationTimeTo(
        LocalDateTime.now().plusHours(1).atZone(ZoneId.of("UTC")).toString());
    dto.setData(Arrays.asList(element));
    return dto;
  }

  private DronePortReserveInfoRegisterListRequestDto createInValidRegisterDto() {
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId(UUID.randomUUID().toString());
    element.setAircraftId(UUID.randomUUID().toString());
    element.setRouteReservationId(UUID.randomUUID().toString());
    element.setUsageType(1);
    element.setReservationTimeFrom(
        LocalDateTime.now().plusHours(1).atZone(ZoneId.of("UTC")).toString());
    element.setReservationTimeTo(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
    dto.setData(Arrays.asList(element));
    return dto;
  }

  private DronePortReserveInfoUpdateRequestDto createValidUpdateDto() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setRouteReservationId(UUID.randomUUID().toString());
    dto.setUsageType(1);
    dto.setReservationTimeFrom(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
    dto.setReservationTimeTo(LocalDateTime.now().plusHours(1).atZone(ZoneId.of("UTC")).toString());
    return dto;
  }

  private DronePortReserveInfoUpdateRequestDto createInValidUpdateDto() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setRouteReservationId(UUID.randomUUID().toString());
    dto.setUsageType(1);
    dto.setReservationTimeFrom(
        LocalDateTime.now().plusHours(1).atZone(ZoneId.of("UTC")).toString());
    dto.setReservationTimeTo(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
    return dto;
  }

  private DronePortReserveInfoListRequestDto createValidListDto() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setRouteReservationId(UUID.randomUUID().toString());
    dto.setTimeFrom(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
    dto.setTimeTo(LocalDateTime.now().plusHours(1).atZone(ZoneId.of("UTC")).toString());
    return dto;
  }

  /** データテンプレート ■登録リクエスト 離着陸場予約登録_テンプレート */
  private DronePortReserveInfoRegisterListRequestDto createDronePortReserveRegisterDto() {
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();

    DronePortReserveInfoRegisterListRequestDto.Element element1 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element1.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
    element1.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e31");
    element1.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    element1.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    element1.setUsageType(1);
    element1.setReservationTimeFrom("2026-01-01T10:00:00+09:00");
    element1.setReservationTimeTo("2026-01-01T12:00:00+09:00");

    DronePortReserveInfoRegisterListRequestDto.Element element2 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element2.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
    element2.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
    element2.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    element2.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    element2.setUsageType(1);
    element2.setReservationTimeFrom("2026-01-01T12:00:00+09:00");
    element2.setReservationTimeTo("2026-01-01T14:00:00+09:00");

    DronePortReserveInfoRegisterListRequestDto.Element element3 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element3.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
    element3.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e33");
    element3.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    element3.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    element3.setUsageType(1);
    element3.setReservationTimeFrom("2026-01-01T14:00:00+09:00");
    element3.setReservationTimeTo("2026-01-01T16:00:00+09:00");

    dto.setData(Arrays.asList(element1, element2, element3));
    return dto;
  }

  /** データテンプレート ■一覧要求リクエスト 離着陸場予約一覧要求_テンプレート */
  private DronePortReserveInfoListRequestDto createDronePortReserveInfoListRequestDto() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    dto.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
    dto.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e31");
    dto.setDronePortName("離着陸場1");
    dto.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    dto.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    dto.setTimeFrom("2026-01-01T10:00:00+09:00");
    dto.setTimeTo("2026-01-01T12:00:00+09:00");
    dto.setReserveProviderId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61");

    return dto;
  }
}
