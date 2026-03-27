package com.hitachi.droneroute.dpm.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
import java.text.MessageFormat;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

/** DronePortInfoValidatorクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
class DronePortInfoValidatorTest {

  @Autowired private DronePortInfoValidator validator;

  @SpyBean private CodeMaster codeMaster;

  @SpyBean private SystemSettings systemSettings;

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　ポート形状:1でVIS離着陸場IDの必須チェック<br>
   * 　　　　動作状況:3で使用不可時間の必須入力あり<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForRegist_validInput_noException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(1);
    dto.setDronePortManufacturerId("dummyManufacturerId");
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(3); // 使用不可
    dto.setInactiveTimeFrom("2023-01-01T00:00:00Z");
    dto.setPublicFlag(true);

    assertDoesNotThrow(() -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　　ポート形状:2で必須チェックなし<br>
   * 　　　　　動作状況:4で使用不可日時の必須入力あり<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForRegist_validInput_noException2() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(2);
    dto.setDronePortManufacturerId("dummyManufacturerId");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(4); // メンテナンス中
    dto.setInactiveTimeFrom("2023-01-01T00:00:00Z");
    dto.setInactiveTimeTo("2023-01-02T00:00:00Z");
    dto.setPublicFlag(true);

    assertDoesNotThrow(() -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　　ポート形状:2で必須チェックなし<br>
   * 　　　　　動作状況:1で使用不可日時の入力なし<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForRegist_validInput_noException3() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(2);
    dto.setDronePortManufacturerId("dummyManufacturerId");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(1); // 準備中
    dto.setPublicFlag(true);

    assertDoesNotThrow(() -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　公開可否フラグ:true必須チェック<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForRegist_validInput_noException4() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("離着陸場名");
    dto.setAddress("設置場所住所");
    dto.setManufacturer("製造メーカー");
    dto.setSerialNumber("製造番号");
    dto.setDronePortManufacturerId("離着陸場メーカーID");
    dto.setPortType(1);
    dto.setVisDronePortCompanyId("VIS離着陸場事業者ID");
    dto.setStoredAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setLat(11.0);
    dto.setLon(22.0);
    dto.setAlt(33.0);
    dto.setSupportDroneType("対応機体");
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(1);
    dto.setPublicFlag(true);

    assertDoesNotThrow(() -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　公開可否フラグ:false必須チェック<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForRegist_validInput_noException5() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("離着陸場名");
    dto.setAddress("設置場所住所");
    dto.setManufacturer("製造メーカー");
    dto.setSerialNumber("製造番号");
    dto.setDronePortManufacturerId("離着陸場メーカーID");
    dto.setPortType(1);
    dto.setVisDronePortCompanyId("VIS離着陸場事業者ID");
    dto.setStoredAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setLat(11.0);
    dto.setLon(22.0);
    dto.setAlt(33.0);
    dto.setSupportDroneType("対応機体");
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(1);
    dto.setPublicFlag(true);

    assertDoesNotThrow(() -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 動作状況:1で使用不可日時の入力あり(入力不可エラー)<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegist_動作状況_準備中_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(2);
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(1); // 準備中
    dto.setInactiveTimeFrom("2023-01-01T00:00:00Z");
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 動作状況:2で使用不可日時の入力あり(入力不可エラー)<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegist_動作状況_使用可_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(2);
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(2); // 使用可
    dto.setInactiveTimeTo("2023-01-02T00:00:00Z");
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 動作状況:nullで使用不可日時の入力あり(入力不可エラー)<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegist_動作状況_null1_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(2);
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(null); // 動作状況:null
    dto.setInactiveTimeFrom("2023-01-01T00:00:00Z");
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 動作状況:nullで使用不可日時の入力あり(入力不可エラー)<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegist_動作状況_null2_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(2);
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(null); // 動作状況:null
    dto.setInactiveTimeTo("2023-01-02T00:00:00Z");
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 画像データにデータURLなし<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegist_画像データにデータURLなし() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(1);
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageData("testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　　ポート形状:2で必須チェックなし<br>
   * 　　　　　動作状況:4で使用不可日時の必須入力あり<br>
   * 　　　　　使用不可開始日時 > 使用不可終了日時(開始終了日時が逆転している)<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegist_compareDateTime_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(2);
    dto.setDronePortManufacturerId("dummyManufacturerId");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(4); // メンテナンス中
    dto.setInactiveTimeFrom("2023-01-01T00:00:00Z");
    dto.setInactiveTimeTo("2023-01-01T08:00:00+09:00");
    dto.setPublicFlag(true);

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
    assertEquals(
        "[使用不可開始日時が使用不可終了日時よりも未来の日時になっています。使用不可開始日時(2023-01-01T00:00:00Z),使用不可終了日時(2023-01-01T08:00:00+09:00)]",
        exception.getMessage());
  }

  private byte[] createBinaryData(int size) {
    byte[] ret = new byte[size];
    Random rand = new Random(System.currentTimeMillis());
    for (int i = 0; i < ret.length; ++i) {
      ret[i] = (byte) rand.nextInt(256);
    }
    return ret;
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 必須項目がnull<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegist_missingRequiredFields_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 画像データが最大の2MBを超過<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegist_画像最大長超過() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(1);
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setImageBinary(createBinaryData(2097153));
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 離着陸場情報登録APIのパラメータチェック<br>
   * 条件: 公開可否フラグ<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForRegist_Exception_公開可否フラグnull() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("離着陸場名");
    dto.setAddress("設置場所住所");
    dto.setManufacturer("製造メーカー");
    dto.setSerialNumber("製造番号");
    dto.setDronePortManufacturerId("離着陸場メーカーID");
    dto.setPortType(1);
    dto.setVisDronePortCompanyId("VIS離着陸場事業者ID");
    dto.setStoredAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setLat(11.0);
    dto.setLon(22.0);
    dto.setAlt(33.0);
    dto.setSupportDroneType("対応機体");
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setActiveStatus(1);
    dto.setPublicFlag(null);

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
    assertEquals("[公開可否フラグに値が設定されていません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　　ポート形状:1、格納中機体IDあり 　　　　　動作状況:3、 使用不可開始日時あり、使用不可終了日時なし<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForUpdate_validInput_noException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("ValidName");
    dto.setPortType(1);
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setActiveStatus(3); // 使用不可
    dto.setInactiveTimeFrom("2023-01-01T00:00:00Z");

    assertDoesNotThrow(() -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　　ポート形状:0、格納中機体IDなし 　　　　　動作状況:4、 使用不可開始日時あり、使用不可終了日時あり<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForUpdate_validInput_noException2() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("ValidName");
    dto.setPortType(0);
    dto.setStoredAircraftId(null);
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setActiveStatus(4); // メンテナンス中
    dto.setInactiveTimeFrom("2023-01-01T00:00:00Z");
    dto.setInactiveTimeTo("2023-01-02T00:00:00Z");
    dto.setPublicFlag(true);

    assertDoesNotThrow(() -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する ポート形状:null 離着陸場名:null<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForUpdate_validInput_noException3() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setActiveStatus(1); // 準備中
    dto.setPublicFlag(true);

    assertDoesNotThrow(() -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 動作状況:1で使用不可日時の入力あり(入力不可エラー)<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_動作状況_準備中_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("ValidName");
    dto.setPortType(1);
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setActiveStatus(1); // 準備中
    dto.setInactiveTimeFrom("2023-01-01T00:00:00Z");
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 動作状況:1で使用不可日時の入力あり(入力不可エラー)<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_動作状況_使用可_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("ValidName");
    dto.setPortType(1);
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setActiveStatus(2); // 使用可
    dto.setInactiveTimeTo("2023-01-02T00:00:00Z");
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 動作状況:nullで使用不可日時の入力あり(入力不可エラー)<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_動作状況_null1_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("ValidName");
    dto.setPortType(1);
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setActiveStatus(null); // 動作状況:null
    dto.setInactiveTimeFrom("2023-01-01T00:00:00Z");
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 動作状況:nullで使用不可日時の入力あり(入力不可エラー)<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_動作状況_null2_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("ValidName");
    dto.setPortType(1);
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setActiveStatus(null); // 動作状況:null
    dto.setInactiveTimeTo("2023-01-02T00:00:00Z");
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 離着陸場IDがnull<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_missingDronePortId_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("ValidName");
    dto.setPortType(1);
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: ポート形状:1以外で、格納中機体IDあり<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_VisDronePortIdNotEmpty_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("ValidName");
    dto.setPortType(2);
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 離着陸場名に空文字を設定<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_DronePortNameIsNull_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("");
    dto.setPortType(1);
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setPublicFlag(true);

    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 離着陸場情報更新APIのパラメータチェック<br>
   * 条件: 離着陸場メーカーIDに値を設定<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForUpdate_DronePortManufacturer_throwsException() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("dronePortName");
    dto.setDronePortManufacturerId("dummyId");
    dto.setPortType(1);
    dto.setStoredAircraftId("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    dto.setLat(35.0);
    dto.setLon(135.0);
    dto.setAlt(10.1);
    dto.setPublicFlag(true);

    ValidationErrorException result =
        assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));

    assertEquals("[ドローンポートメーカーIDは入力できません。]", result.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　　ソート順に設定あり、ソート対象列名に設定ありで、設定個数が一致する<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_validInput_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPerPage("1");
    dto.setPage("1");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 　　　　　ソート順、ソート対象列名設定なし<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_validInput2_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders(null);
    dto.setSortColumns(null);

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 矩形範囲の１つが欠けている場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_missingLatLon1_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    // dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 矩形範囲の２つが欠けている場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_missingLatLon2_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 矩形範囲の３つが欠けている場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_missingLatLon3_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 矩形範囲の４つが欠けている場合(矩形範囲条件なしは正常)<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_missingLatLon4_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 公開可否フラグの設定値が空文字の場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_PublicFlag1_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPublicFlag(null);

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 公開可否フラグの設定値が空文字の場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_PublicFlag2_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPublicFlag("");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 公開可否フラグの設定値がtrueの場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_PublicFlag3_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPublicFlag("true");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 公開可否フラグの設定値がfalseの場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_PublicFlag4_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPublicFlag("false");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 料金情報要否の設定値が空文字の場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_IsRequiredPriceInfo1_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPublicFlag("false");
    dto.setIsRequiredPriceInfo("");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 料金情報要否の設定値がtrueの場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_IsRequiredPriceInfo2_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPublicFlag("false");
    dto.setIsRequiredPriceInfo("true");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 料金情報要否の設定値がfalseの場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_IsRequiredPriceInfo3_noException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPublicFlag("false");
    dto.setIsRequiredPriceInfo("false");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
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
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
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
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
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
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
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
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
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
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: ソート順に設定あり、ソート対象列名に設定ありで、設定個数が不一致の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_sort1_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders("1");
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ソート順とソート対象列の設定数が一致しません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: ソート順に設定なし、ソート対象列名に設定ありで、設定個数が不一致の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_sort2_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders(null);
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ソート順とソート対象列の設定数が一致しません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: ソート順に設定あり、ソート対象列名に設定なしで、設定個数が不一致の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_sort3_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders("0,0,0");
    dto.setSortColumns(null);

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ソート順とソート対象列の設定数が一致しません。]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: ソート順の設定値が範囲外の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_sort4_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders("1,0,2");
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ソート順の値が不正です。\n範囲[0, 1]]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 離着陸場種類の設定値が範囲外(数値)の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_PortType1_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,99999999999999999999999999999");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ポート形状の値が不正です。\n最小値(0)、最大値(2)]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 離着陸場種類の設定値が範囲外(文字)の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_PortType2_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,A");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[ポート形状が不正です。\n入力値:A, ポート形状の値が不正です。\n最小値(0)、最大値(2)]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 離着陸場種類の設定値がnullの場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_PortType3_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType(null);
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 離着陸場種類の設定値が空文字の場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_PortType4_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,3");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 動作状況の設定値が範囲外(数値)の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_ActiveStatus1_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,4,99999999999999999999999999999");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[動作状況の値が不正です。\n最小値(1)、最大値(4)]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 動作状況の設定値が範囲外(文字)の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_ActiveStatus2_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("1,2,A");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[動作状況が不正です。\n入力値:A, 動作状況の値が不正です。\n最小値(1)、最大値(4)]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 動作状況の設定値がnullの場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_ActiveStatus3_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus(null);
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 動作状況の設定値が空文字の場合<br>
   * 結果: 正常終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetList_ActiveStatus4_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 料金情報要否の設定値がTRUEの場合<br>
   * 結果: 異常終了する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_IsRequiredPriceInfo_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPublicFlag("false");
    dto.setIsRequiredPriceInfo("TRUE");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
    assertEquals(
        MessageFormat.format("[{0}が不正です。\n入力値:{1}]", "料金情報要否", dto.getIsRequiredPriceInfo()),
        exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 離着陸場情報一覧取得APIのパラメータチェック<br>
   * 条件: 公開可否フラグの設定値が想定外文字列の場合<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetList_PublicFlag_throwsException() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("ValidName");
    dto.setAddress("ValidAddress");
    dto.setManufacturer("ValidManufacturer");
    dto.setSerialNumber("ValidSerialNumber");
    dto.setPortType("0,1,2");
    dto.setMinLat(-90.0);
    dto.setMinLon(-180.0);
    dto.setMaxLat(90.0);
    dto.setMaxLon(180.0);
    dto.setSupportDroneType("ValidType");
    dto.setActiveStatus("");
    dto.setSortOrders("1,0,1");
    dto.setSortColumns("a,b,c");
    dto.setPublicFlag("TRUE");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[公開可否フラグが不正です。\n入力値:TRUE]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetDetail<br>
   * 試験名: 離着陸場情報詳細取得APIのパラメータチェック<br>
   * 条件: 正常な入力値を設定する<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void validateForGetDetail_validInput_noException() {
    String dronePortId = UUID.randomUUID().toString();

    assertDoesNotThrow(() -> validator.validateForGetDetail(dronePortId));
  }

  /**
   * メソッド名: validateForGetDetail<br>
   * 試験名: 離着陸場情報詳細取得APIのパラメータチェック<br>
   * 条件: 離着陸場IDがnull<br>
   * 結果: 例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void validateForGetDetail_missingDronePortId_throwsException() {
    assertThrows(ValidationErrorException.class, () -> validator.validateForGetDetail(null));
  }

  /**
   * メソッド名: validateForDelete<br>
   * 試験名: 離着陸場情報削除APIパラメータチェックの正常系テスト<br>
   * 条件: 正常な離着陸場情報削除を渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForDelete_Normal() {
    DronePortInfoDeleteRequestDto dto = new DronePortInfoDeleteRequestDto();
    dto.setOperatorId("dummyOperator");

    assertDoesNotThrow(() -> validator.validateForDelete(dto));
  }
}
