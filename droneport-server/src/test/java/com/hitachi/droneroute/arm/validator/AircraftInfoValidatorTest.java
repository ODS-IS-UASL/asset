package com.hitachi.droneroute.arm.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import com.hitachi.droneroute.arm.constants.AircraftConstants;
import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoModelInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoModelSearchRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

/** AircraftInfoValidatorクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
public class AircraftInfoValidatorTest {

  @Autowired private AircraftInfoValidator validator;

  @SpyBean private CodeMaster codeMaster;

  @SpyBean private SystemSettings systemSettings;

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 機体情報登録APIパラメータチェックの正常系テスト<br>
   * 条件: 正常なAircraftInfoRequestDtoを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForRegist_Normal() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setOwnerType(1);
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType(3);
    dto.setMaxTakeoffWeight(500.0);
    dto.setBodyWeight(300.0);
    dto.setMaxFlightSpeed(50.0);
    dto.setMaxFlightTime(60.0);
    dto.setLat(38.0);
    dto.setLon(130.0);
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setImageData("data:image/png;base64," + "testdata");
    dto.setImageBinary(createBinaryData(2097152));
    dto.setPublicFlag(true);

    assertDoesNotThrow(() -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 機体情報登録APIパラメータチェックの異常系テスト<br>
   * 条件: 画像データにデータURLプレフィックスが含まれない<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForRegist_画像データにデータURLなし() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setOwnerType(1);
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType(3);
    dto.setMaxTakeoffWeight(500.0);
    dto.setBodyWeight(300.0);
    dto.setMaxFlightSpeed(50.0);
    dto.setMaxFlightTime(60.0);
    dto.setLat(38.0);
    dto.setLon(130.0);
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setImageData("testdata");
    dto.setImageBinary(createBinaryData(2097152));

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
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
   * 試験名: 機体情報登録APIパラメータチェックの異常系テスト<br>
   * 条件: 必須フィールドの緯度経度が未設定<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForRegist_MissingLatLonFields() {
    // 必須緯度経度フィールドを設定しない
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setOwnerType(1);
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType(3);
    dto.setMaxTakeoffWeight(500.0);
    dto.setBodyWeight(300.0);
    dto.setMaxFlightSpeed(50.0);
    dto.setMaxFlightTime(60.0);
    //    dto.setLat(38.0);
    //    dto.setLon(130.0);
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setImageData("testdata");
    dto.setImageBinary(createBinaryData(2097152));

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 機体情報登録APIパラメータチェックの異常系テスト<br>
   * 条件: 必須フィールドが未設定<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForRegist_MissingRequiredFields() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    // 必須フィールドを設定しない

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * 試験名: 機体情報登録APIパラメータチェックの異常系テスト<br>
   * 条件: 画像バイナリサイズが最大値を超過<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForRegist_画像最大長超過() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setOwnerType(1);
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType(3);
    dto.setMaxTakeoffWeight(500.0);
    dto.setBodyWeight(300.0);
    dto.setMaxFlightSpeed(50.0);
    dto.setMaxFlightTime(60.0);
    dto.setLat(38.0);
    dto.setLon(130.0);
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setImageBinary(createBinaryData(2097153));

    assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 機体情報更新APIパラメータチェックの正常系テスト<br>
   * 条件: 正常なAircraftInfoRequestDtoを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForUpdate_Normal() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setAircraftName("Test Aircraft");
    dto.setOwnerType(1);
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType(3);
    dto.setMaxTakeoffWeight(500.0);
    dto.setBodyWeight(300.0);
    dto.setMaxFlightSpeed(50.0);
    dto.setMaxFlightTime(60.0);
    dto.setLat(38.0);
    dto.setLon(130.0);
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");

    assertDoesNotThrow(() -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 機体情報更新APIパラメータチェックの異常系テスト<br>
   * 条件: 必須フィールドが未設定<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForUpdate_MissingRequiredFields() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    // 必須フィールドを設定しない

    assertThrows(ValidationErrorException.class, () -> validator.validateForUpdate(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   * 第3引数にファイルバイトチェックの設定値(モックの設定用)<br>
   */
  static Stream<Arguments> nmlCase() {
    return Stream.of(
        Arguments.of(
            "補足資料情報1つ",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  return dto;
                },
            28),
        Arguments.of(
            "補足資料情報3つ",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku3();
                  return dto;
                },
            28),
        Arguments.of(
            "補足資料空配列",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_empList();
                  return dto;
                },
            28),
        Arguments.of(
            "補足資料null",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosokuNull();
                  return dto;
                },
            28),
        Arguments.of(
            "補足資料リストの要素がnull",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().set(0, null);
                  return dto;
                },
            28),
        Arguments.of(
            "処理種別：登録、補足資料IDがUUIDではない、ファイル(バイト型)のバイト数が設定値と同じ",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFileId("notUUID");
                  byte[] byteData = new byte[2];
                  new Random().nextBytes(byteData);
                  dto.getFileInfos().get(0).setFileBinary(byteData);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：登録、補足資料IDがnull、ファイル(バイト型)がnull",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFileId(null);
                  dto.getFileInfos().get(0).setFileBinary(null);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：登録、補足資料IDが空文字、ファイル(バイト型)が要素0の配列",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFileId("");
                  dto.getFileInfos().get(0).setFileBinary(new byte[0]);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：更新、ファイル(バイト型)のバイト数が設定値と同じ",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  byte[] byteData = new byte[2];
                  new Random().nextBytes(byteData);
                  dto.getFileInfos().get(0).setFileBinary(byteData);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：更新、ファイル(バイト型)がnull",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFileBinary(null);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：更新、ファイル(バイト型)が要素0の配列",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFileBinary(new byte[0]);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：削除、補足資料名称・ファイル物理名・ファイル(Base64エンコード文字列)がnull、ファイル(バイト型)のバイト数が設定値と同じ",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(3);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFileLogicalName(null);
                  dto.getFileInfos().get(0).setFilePhysicalName(null);
                  dto.getFileInfos().get(0).setFileData(null);
                  byte[] byteData = new byte[2];
                  new Random().nextBytes(byteData);
                  dto.getFileInfos().get(0).setFileBinary(byteData);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：削除、補足資料名称・ファイル物理名・ファイル(Base64エンコード文字列)が空文字、ファイル(バイト型)のバイト数が設定値より1バイト多い",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(3);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFileLogicalName("");
                  dto.getFileInfos().get(0).setFilePhysicalName("");
                  dto.getFileInfos().get(0).setFileData("");
                  byte[] byteData = new byte[3];
                  new Random().nextBytes(byteData);
                  dto.getFileInfos().get(0).setFileBinary(byteData);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：削除、ファイル(Base64エンコード文字列)がMIMEタイプ欠落1、ファイル(バイト型)がnull",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(3);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos()
                      .get(0)
                      .setFileData("data:plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  dto.getFileInfos().get(0).setFileBinary(null);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：削除、ファイル(Base64エンコード文字列)がMIMEタイプ欠落2、ファイル(バイト型)が要素0の配列",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(3);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos()
                      .get(0)
                      .setFileData("data:text;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  dto.getFileInfos().get(0).setFileBinary(new byte[0]);
                  return dto;
                },
            2),
        Arguments.of(
            "処理種別：削除、ファイル(Base64エンコード文字列)がサポート外MIMEタイプ",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(3);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos()
                      .get(0)
                      .setFileData(
                          "data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            28),
        Arguments.of(
            "処理種別：削除、ファイル(Base64エンコード文字列)がフォーマット不正",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(3);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos()
                      .get(0)
                      .setFileData(
                          "data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            28),
        Arguments.of(
            "処理種別：更新、補足資料名称がnull",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFileLogicalName(null);
                  return dto;
                },
            28),
        Arguments.of(
            "処理種別：更新、補足資料名称が空文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFileLogicalName("");
                  return dto;
                },
            28),
        Arguments.of(
            "処理種別：更新、ファイル物理名がnull",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFilePhysicalName(null);
                  return dto;
                },
            28),
        Arguments.of(
            "処理種別：更新、ファイル物理名が空文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFilePhysicalName("");
                  return dto;
                },
            28),
        Arguments.of(
            "処理種別：更新、ファイル(Base64エンコード文字列)がnull",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFileData(null);
                  return dto;
                },
            28),
        Arguments.of(
            "処理種別：更新、ファイル(Base64エンコード文字列)が空文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos().get(0).setFileData("");
                  return dto;
                },
            28),
        Arguments.of(
            "型式番号:NULL",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.setModelNumber(null);
                  return dto;
                },
            28),
        Arguments.of(
            "型式番号:空文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.setModelNumber("");
                  return dto;
                },
            28),
        Arguments.of(
            "型式番号:20",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.setModelNumber("abcdefghijklmnopqrst");
                  return dto;
                },
            28),
        Arguments.of(
            "機種名:更新後機種名",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  return dto;
                },
            28),
        Arguments.of(
            "製造メーカー:200文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setManufacturer(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  return dto;
                },
            28),
        Arguments.of(
            "型式番号:200文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setModelNumber(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  return dto;
                },
            28),
        Arguments.of(
            "機種名:null",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setModelName(null);
                  return dto;
                },
            28),
        Arguments.of(
            "機種名:空文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setModelName("");
                  return dto;
                },
            28),
        Arguments.of(
            "機種名:200文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setModelName(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  return dto;
                },
            28),
        Arguments.of(
            "公開可否フラグ:true",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setPublicFlag(true);
                  return dto;
                },
            28),
        Arguments.of(
            "公開可否フラグ:false",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setPublicFlag(false);
                  return dto;
                },
            28));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 正常系の様々なパターンでのバリデーション動作検証<br>
   * 条件: 必須項目を含む正常なリクエストDTO（補足資料の各パターン）<br>
   * 結果: 例外が発生せず、正常に処理が完了する<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("nmlCase")
  public void testValidateForUpdate_nmlCase(
      String caseName, Supplier<AircraftInfoRequestDto> sDto, int maxSize) {
    doReturn(maxSize)
        .when(systemSettings)
        .getIntegerValue(
            AircraftConstants.SETTINGS_FILE_DATA, AircraftConstants.SETTINGS_MAX_FILE_BINARY_SIZE);
    AircraftInfoRequestDto dto = sDto.get();
    assertDoesNotThrow(() -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForRegist<br>
   * ParameterizedTest用の引数準備メソッド<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   * 第3引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> errCase() {
    return Stream.of(
        Arguments.of(
            "処理種別が4の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(4);
                  return dto;
                },
            "[1番目のファイルの処理種別の値が不正です。\n範囲[1, 2, 3]]"),
        Arguments.of(
            "処理種別がnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(null);
                  return dto;
                },
            "[1番目のファイルの処理種別に値が設定されていません。]"),
        Arguments.of(
            "処理種別：登録、補足資料名称null",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFileLogicalName(null);
                  return dto;
                },
            "[1番目のファイルの補足資料名称に値が設定されていません。]"),
        Arguments.of(
            "処理種別：登録、補足資料名称空文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFileLogicalName("");
                  return dto;
                },
            "[1番目のファイルの補足資料名称に値が設定されていません。]"),
        Arguments.of(
            "処理種別：登録、ファイル物理名null",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFilePhysicalName(null);
                  return dto;
                },
            "[1番目のファイルのファイル物理名に値が設定されていません。]"),
        Arguments.of(
            "処理種別：登録、ファイル物理名null",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFilePhysicalName("");
                  return dto;
                },
            "[1番目のファイルのファイル物理名に値が設定されていません。]"),
        Arguments.of(
            "処理種別：登録、ファイル(Base64エンコード文字列)null",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFileData(null);
                  return dto;
                },
            "[1番目のファイルのファイルデータに値が設定されていません。]"),
        Arguments.of(
            "処理種別：登録、ファイル(Base64エンコード文字列)空文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFileData("");
                  return dto;
                },
            "[1番目のファイルのファイルデータに値が設定されていません。]"),
        Arguments.of(
            "処理種別：登録、ファイル(Base64エンコード文字列)MIMEタイプ欠落1",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos()
                      .get(0)
                      .setFileData("data:plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "処理種別：登録、ファイル(Base64エンコード文字列)MIMEタイプ欠落2",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos()
                      .get(0)
                      .setFileData("data:text;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "処理種別：登録、ファイル(Base64エンコード文字列)サポート外MIMEタイプ",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos()
                      .get(0)
                      .setFileData(
                          "data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "処理種別：登録、ファイル(Base64エンコード文字列)フォーマット不正",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos()
                      .get(0)
                      .setFileData(
                          "date:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "処理種別：登録、ファイル(バイト型)のサイズが設定値より大きい",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  byte[] byteData = new byte[29];
                  new Random().nextBytes(byteData);
                  dto.getFileInfos().get(0).setFileBinary(byteData);
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)の長さが不正です。\n最大長(28)]"),
        Arguments.of(
            "処理種別：更新、補足資料IDがUUIDではない",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("notUUID");
                  return dto;
                },
            "[1番目のファイルの補足資料IDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "処理種別：更新、補足資料IDがnull",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId(null);
                  return dto;
                },
            "[1番目のファイルの補足資料IDに値が設定されていません。]"),
        Arguments.of(
            "処理種別：更新、補足資料IDが空文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("");
                  return dto;
                },
            "[1番目のファイルの補足資料IDに値が設定されていません。]"),
        Arguments.of(
            "処理種別：更新、ファイル(Base64エンコード文字列)がMIMEタイプ欠落1",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos()
                      .get(0)
                      .setFileData("data:plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "処理種別：更新、ファイル(Base64エンコード文字列)がMIMEタイプ欠落2",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos()
                      .get(0)
                      .setFileData("data:text;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "処理種別：更新、ファイル(Base64エンコード文字列)がサポート外MIMEタイプ",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos()
                      .get(0)
                      .setFileData(
                          "data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "処理種別：更新、ファイル(Base64エンコード文字列)がフォーマット不正",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(2);
                  dto.getFileInfos().get(0).setFileId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getFileInfos()
                      .get(0)
                      .setFileData(
                          "date:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "処理種別：更新、ファイル(バイト型)のサイズが設定値より大きい\"",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  byte[] byteData = new byte[29];
                  new Random().nextBytes(byteData);
                  dto.getFileInfos().get(0).setFileBinary(byteData);
                  return dto;
                },
            "[hosoku.txt(1番目のファイル)の長さが不正です。\n最大長(28)]"),
        Arguments.of(
            "処理種別：削除、補足資料IDがUUIDではない",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(3);
                  dto.getFileInfos().get(0).setFileId("notUUID");
                  return dto;
                },
            "[1番目のファイルの補足資料IDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "処理種別：削除、補足資料IDがnull",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(3);
                  dto.getFileInfos().get(0).setFileId(null);
                  return dto;
                },
            "[1番目のファイルの補足資料IDに値が設定されていません。]"),
        Arguments.of(
            "処理種別：削除、補足資料IDが空文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(3);
                  dto.getFileInfos().get(0).setFileId("");
                  return dto;
                },
            "[1番目のファイルの補足資料IDに値が設定されていません。]"),
        Arguments.of(
            "複数項目チェックエラー",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(0).setFileLogicalName(null);
                  dto.getFileInfos().get(0).setFilePhysicalName(null);
                  return dto;
                },
            "[1番目のファイルの補足資料名称に値が設定されていません。, 1番目のファイルのファイル物理名に値が設定されていません。]"),
        Arguments.of(
            "N番目のファイルのチェックエラー",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku3();
                  dto.getFileInfos().get(0).setProcessingType(1);
                  dto.getFileInfos().get(1).setProcessingType(1);
                  dto.getFileInfos().get(1).setFileLogicalName(null);
                  dto.getFileInfos().get(2).setProcessingType(1);
                  dto.getFileInfos().get(2).setFileLogicalName(null);
                  return dto;
                },
            "[2番目のファイルの補足資料名称に値が設定されていません。, 3番目のファイルの補足資料名称に値が設定されていません。]"),
        Arguments.of(
            "緯度:NULL",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.setLat(null);
                  return dto;
                },
            "[位置情報（緯度）に値が設定されていません。]"),
        Arguments.of(
            "経度:NULL",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.setLon(null);
                  return dto;
                },
            "[位置情報（経度）に値が設定されていません。]"),
        Arguments.of(
            "緯度経度:NULL",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.setLat(null);
                  dto.setLon(null);
                  return dto;
                },
            "[位置情報（緯度）に値が設定されていません。, 位置情報（経度）に値が設定されていません。]"),
        Arguments.of(
            "型式番号:201文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.setModelNumber(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[型式番号の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "緯度経度:NULL, 型式番号201文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
                  dto.setLat(null);
                  dto.setLon(null);
                  dto.setModelNumber(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[位置情報（緯度）に値が設定されていません。, 位置情報（経度）に値が設定されていません。, 型式番号の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "製造メーカー:201文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setManufacturer(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[製造メーカーの長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "型式番号:201文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setModelNumber(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[型式番号の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "機種名:201文字",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setModelName(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[機種名の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "公開可否フラグ:null",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
                  dto.setPublicFlag(null);
                  return dto;
                },
            "[公開可否フラグに値が設定されていません。]"));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 異常系の様々なパターンでのバリデーションエラー動作検証<br>
   * 条件: 必須項目が欠落または不正な値を含むリクエストDTO（補足資料のエラーケース）<br>
   * 結果: ValidationErrorExceptionがスローされ、エラーメッセージが検証される<br>
   * テストパターン: 異常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("errCase")
  public void testValidateForUpdate_errCase(
      String caseName, Supplier<AircraftInfoRequestDto> sDto, String msg) {
    doReturn(28)
        .when(systemSettings)
        .getIntegerValue(
            AircraftConstants.SETTINGS_FILE_DATA, AircraftConstants.SETTINGS_MAX_FILE_BINARY_SIZE);
    AircraftInfoRequestDto dto = sDto.get();
    Exception ex =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
    assertEquals(msg, ex.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   * 第3引数にファイルバイトチェックの設定値(モックの設定用)<br>
   */
  static Stream<Arguments> nmlPayloadCase() {
    return Stream.of(
        Arguments.of(
            "登録：ペイロード情報1つの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  return dto;
                },
            28),
        Arguments.of(
            "登録：ペイロード情報が3つの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload3();
                  return dto;
                },
            28),
        Arguments.of(
            "登録：ペイロード情報が空配列の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadEmpList();
                  return dto;
                },
            28),
        Arguments.of(
            "登録：ペイロード情報がnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadNull();
                  return dto;
                },
            28),
        Arguments.of(
            "登録：ペイロード情報リストの要素がnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().set(0, null);
                  return dto;
                },
            28),
        Arguments.of(
            "登録：必須項目(ペイロード名以外)がnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(0).setPayloadId(null);
                  dto.getPayloadInfos().get(0).setPayloadDetailText(null);
                  dto.getPayloadInfos().get(0).setImageData(null);
                  dto.getPayloadInfos().get(0).setImageBinary(null);
                  dto.getPayloadInfos().get(0).setFilePhysicalName(null);
                  dto.getPayloadInfos().get(0).setFileData(null);
                  dto.getPayloadInfos().get(0).setFileBinary(null);
                  return dto;
                },
            28),
        Arguments.of(
            "登録：必須項目(ペイロード名以外)が空文字、Binaryが0の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(0).setPayloadId("");
                  dto.getPayloadInfos().get(0).setPayloadDetailText("");
                  dto.getPayloadInfos().get(0).setImageData("");
                  byte[] imageData = new byte[0];
                  new Random().nextBytes(imageData);
                  dto.getPayloadInfos().get(0).setImageBinary(imageData);
                  dto.getPayloadInfos().get(0).setFilePhysicalName("");
                  dto.getPayloadInfos().get(0).setFileData("");
                  byte[] fileData = new byte[0];
                  new Random().nextBytes(fileData);
                  dto.getPayloadInfos().get(0).setFileBinary(fileData);
                  return dto;
                },
            28),
        Arguments.of(
            "登録：ノーマル",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(0).setPayloadId(null);
                  return dto;
                },
            28),
        Arguments.of(
            "登録：ペイロードID:UUID以外、ペイロード名:100文字、ペイロード詳細テキスト:1000文字,画像バイト:2B、ファイル物理名:200文字、ファイルバイト:2Bの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(0).setPayloadId("notUUID");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙy");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadDetailText(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsりネ空Ｔtるハ星Ｕuれフ月Ｖvろヘ山Ｗwわホ川Ｘxをマ空Ｙyんミ花Ｚzあユ風Ａaいラ雪Ｂbうリ鳥Ｃcえワ雨Ｄdおル森Ｅeかネ空Ｆfきハ星Ｇgくフ月Ｈhけヘ山Ｉiこホ川Ｊjさマ空Ｋkしミ星Ｌlすユ風Ｍmせラ雪Ｎnそリ鳥Ｏoたワ雨Ｐpちル森Ｑqつネ空Ｒrてハ星Ｓsとフ月Ｔtなヘ山Ｕuにホ川Ｖvぬマ空Ｗwねミ星Ｘxのユ風Ｙyはラ雪Ｚzひリ鳥Ａaふワ雨Ｂbへル森Ｃcほネ空Ｄdまハ星Ｅeみフ月Ｆfむヘ山Ｇgめホ川Ｈhもマ空Ｉiやミ星Ｊjゆユ風Ｋkよラ雪Ｌlらリ鳥Ｍmりワ雨Ｎnるル森Ｏoれネ空Ｐpろハ星Ｑqわフ月Ｒrをヘ山Ｓsんホ川Ｔtあマ空Ｕuいミ花Ｖvうユ風Ｗwえラ雪Ｘxおリ鳥Ｙyかワ雨Ｚzきル森Ａaくネ空Ｂbけハ星Ｃcこフ月Ｄdさヘ山Ｅeしホ川Ｆfすマ空Ｇgせミ星Ｈhそユ風Ｉiたラ雪Ｊjちリ鳥Ｋkつワ雨Ｌlてル森Ｍmとネ空Ｎnなハ星Ｏoにフ月Ｐpぬヘ山Ｑqねホ川Ｒrのマ空Ｓsはミ星Ｔtひユ風Ｕuふラ雪Ｖvへリ鳥Ｗwほワ雨Ｘxまル森Ｙyみネ空Ｚzむハ星Ａaめフ月Ｂbもヘ山Ｃcやホ川Ｄdゆマ空Ｅeよミ星Ｆfらユ風Ｇgりラ雪Ｈhるリ鳥Ｉiれワ雨Ｊjろル森Ｋkわネ空Ｌlをハ星Ｍmんフ月Ｎnあヘ山Ｏoいホ川Ｐpうマ空Ｑqえミ星Ｒrおユ風Ｓsかラ雪Ｔtきリ鳥Ｕuくワ雨Ｖvけル森Ｗwこネ空Ｘxさハ星Ｙyしフ月Ｚzすヘ山Ａaせホ川Ｂbそマ空Ｃcたミ星Ｄdちユ風Ｅeつラ雪Ｆfてリ鳥Ｇgとワ雨Ｈhなル森Ｉiにネ空Ｊjぬハ星Ｋkねフ月Ｌlのヘ山Ｍmはホ川Ｎnひマ空Ｏoふミ星Ｐpへユ風Ｑqほラ雪Ｒrまリ鳥Ｓsみワ雨Ｔtむル森Ｕuめネ空Ｖvもハ星Ｗwやフ月Ｘxゆヘ山Ｙyよホ川Ｚzらマ空Ａaりミ星Ｂbるユ風Ｃcれラ雪Ｄdろリ鳥Ｅeわワ雨Ｆfをル森Ｇgんネ空Ｈhあハ星Ｉiいフ月Ｊjうヘ山Ｋkえホ川Ｌlおマ空Ｍmかミ星Ｎnきユ風Ｏoくラ雪Ｐpけリ鳥Ｑqこワ雨Ｒrさル森Ｓsしネ空Ｔtすハ星Ｕuせフ月Ｖvそヘ山Ｗw");
                  byte[] imageData = new byte[2];
                  new Random().nextBytes(imageData);
                  dto.getPayloadInfos().get(0).setImageBinary(imageData);
                  dto.getPayloadInfos()
                      .get(0)
                      .setFilePhysicalName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  byte[] fileData = new byte[2];
                  new Random().nextBytes(fileData);
                  dto.getPayloadInfos().get(0).setFileBinary(fileData);
                  return dto;
                },
            2),
        Arguments.of(
            "更新：必須項目(ペイロードID)以外がnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos().get(0).setPayloadName(null);
                  dto.getPayloadInfos().get(0).setPayloadDetailText(null);
                  dto.getPayloadInfos().get(0).setImageData(null);
                  dto.getPayloadInfos().get(0).setImageBinary(null);
                  dto.getPayloadInfos().get(0).setFilePhysicalName(null);
                  dto.getPayloadInfos().get(0).setFileData(null);
                  dto.getPayloadInfos().get(0).setFileBinary(null);
                  return dto;
                },
            28),
        Arguments.of(
            "更新：必須項目(ペイロードID)以外が空文字、Binaryが0の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos().get(0).setPayloadName("");
                  dto.getPayloadInfos().get(0).setPayloadDetailText("");
                  dto.getPayloadInfos().get(0).setImageData("");
                  byte[] imageData = new byte[0];
                  new Random().nextBytes(imageData);
                  dto.getPayloadInfos().get(0).setImageBinary(imageData);
                  dto.getPayloadInfos().get(0).setFilePhysicalName("");
                  dto.getPayloadInfos().get(0).setFileData("");
                  byte[] fileData = new byte[0];
                  new Random().nextBytes(fileData);
                  dto.getPayloadInfos().get(0).setFileBinary(fileData);
                  return dto;
                },
            28),
        Arguments.of(
            "更新：ノーマル",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  return dto;
                },
            28),
        Arguments.of(
            "更新：ペイロード名:100文字、ペイロード詳細テキスト:1000文字,画像バイト:2B、ファイル物理名:200文字、ファイルバイト:2Bの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙy");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadDetailText(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsりネ空Ｔtるハ星Ｕuれフ月Ｖvろヘ山Ｗwわホ川Ｘxをマ空Ｙyんミ花Ｚzあユ風Ａaいラ雪Ｂbうリ鳥Ｃcえワ雨Ｄdおル森Ｅeかネ空Ｆfきハ星Ｇgくフ月Ｈhけヘ山Ｉiこホ川Ｊjさマ空Ｋkしミ星Ｌlすユ風Ｍmせラ雪Ｎnそリ鳥Ｏoたワ雨Ｐpちル森Ｑqつネ空Ｒrてハ星Ｓsとフ月Ｔtなヘ山Ｕuにホ川Ｖvぬマ空Ｗwねミ星Ｘxのユ風Ｙyはラ雪Ｚzひリ鳥Ａaふワ雨Ｂbへル森Ｃcほネ空Ｄdまハ星Ｅeみフ月Ｆfむヘ山Ｇgめホ川Ｈhもマ空Ｉiやミ星Ｊjゆユ風Ｋkよラ雪Ｌlらリ鳥Ｍmりワ雨Ｎnるル森Ｏoれネ空Ｐpろハ星Ｑqわフ月Ｒrをヘ山Ｓsんホ川Ｔtあマ空Ｕuいミ花Ｖvうユ風Ｗwえラ雪Ｘxおリ鳥Ｙyかワ雨Ｚzきル森Ａaくネ空Ｂbけハ星Ｃcこフ月Ｄdさヘ山Ｅeしホ川Ｆfすマ空Ｇgせミ星Ｈhそユ風Ｉiたラ雪Ｊjちリ鳥Ｋkつワ雨Ｌlてル森Ｍmとネ空Ｎnなハ星Ｏoにフ月Ｐpぬヘ山Ｑqねホ川Ｒrのマ空Ｓsはミ星Ｔtひユ風Ｕuふラ雪Ｖvへリ鳥Ｗwほワ雨Ｘxまル森Ｙyみネ空Ｚzむハ星Ａaめフ月Ｂbもヘ山Ｃcやホ川Ｄdゆマ空Ｅeよミ星Ｆfらユ風Ｇgりラ雪Ｈhるリ鳥Ｉiれワ雨Ｊjろル森Ｋkわネ空Ｌlをハ星Ｍmんフ月Ｎnあヘ山Ｏoいホ川Ｐpうマ空Ｑqえミ星Ｒrおユ風Ｓsかラ雪Ｔtきリ鳥Ｕuくワ雨Ｖvけル森Ｗwこネ空Ｘxさハ星Ｙyしフ月Ｚzすヘ山Ａaせホ川Ｂbそマ空Ｃcたミ星Ｄdちユ風Ｅeつラ雪Ｆfてリ鳥Ｇgとワ雨Ｈhなル森Ｉiにネ空Ｊjぬハ星Ｋkねフ月Ｌlのヘ山Ｍmはホ川Ｎnひマ空Ｏoふミ星Ｐpへユ風Ｑqほラ雪Ｒrまリ鳥Ｓsみワ雨Ｔtむル森Ｕuめネ空Ｖvもハ星Ｗwやフ月Ｘxゆヘ山Ｙyよホ川Ｚzらマ空Ａaりミ星Ｂbるユ風Ｃcれラ雪Ｄdろリ鳥Ｅeわワ雨Ｆfをル森Ｇgんネ空Ｈhあハ星Ｉiいフ月Ｊjうヘ山Ｋkえホ川Ｌlおマ空Ｍmかミ星Ｎnきユ風Ｏoくラ雪Ｐpけリ鳥Ｑqこワ雨Ｒrさル森Ｓsしネ空Ｔtすハ星Ｕuせフ月Ｖvそヘ山Ｗw");
                  byte[] imageData = new byte[2];
                  new Random().nextBytes(imageData);
                  dto.getPayloadInfos().get(0).setImageBinary(imageData);
                  dto.getPayloadInfos()
                      .get(0)
                      .setFilePhysicalName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  byte[] fileData = new byte[2];
                  new Random().nextBytes(fileData);
                  dto.getPayloadInfos().get(0).setFileBinary(fileData);
                  return dto;
                },
            2),
        Arguments.of(
            "削除：必須項目(ペイロードID以外)がnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(3);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos().get(0).setPayloadName(null);
                  dto.getPayloadInfos().get(0).setPayloadDetailText(null);
                  dto.getPayloadInfos().get(0).setImageData(null);
                  dto.getPayloadInfos().get(0).setImageBinary(null);
                  dto.getPayloadInfos().get(0).setFilePhysicalName(null);
                  dto.getPayloadInfos().get(0).setFileData(null);
                  dto.getPayloadInfos().get(0).setFileBinary(null);
                  return dto;
                },
            28),
        Arguments.of(
            "削除：必須項目(ペイロードID)以外が空文字、Binaryが0の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(3);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos().get(0).setPayloadName("");
                  dto.getPayloadInfos().get(0).setPayloadDetailText("");
                  dto.getPayloadInfos().get(0).setImageData("");
                  byte[] imageData = new byte[0];
                  new Random().nextBytes(imageData);
                  dto.getPayloadInfos().get(0).setImageBinary(imageData);
                  dto.getPayloadInfos().get(0).setFilePhysicalName("");
                  dto.getPayloadInfos().get(0).setFileData("");
                  byte[] fileData = new byte[0];
                  new Random().nextBytes(fileData);
                  dto.getPayloadInfos().get(0).setFileBinary(fileData);
                  return dto;
                },
            2),
        Arguments.of(
            "削除：全項目入力の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(3);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  return dto;
                },
            28),
        Arguments.of(
            "更新：ペイロードID:null、ペイロード名:100文字、ペイロード詳細テキスト:1000文字,画像バイト:2B、ファイル物理名:200文字、ファイルバイト:2Bの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(3);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙy");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadDetailText(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsりネ空Ｔtるハ星Ｕuれフ月Ｖvろヘ山Ｗwわホ川Ｘxをマ空Ｙyんミ花Ｚzあユ風Ａaいラ雪Ｂbうリ鳥Ｃcえワ雨Ｄdおル森Ｅeかネ空Ｆfきハ星Ｇgくフ月Ｈhけヘ山Ｉiこホ川Ｊjさマ空Ｋkしミ星Ｌlすユ風Ｍmせラ雪Ｎnそリ鳥Ｏoたワ雨Ｐpちル森Ｑqつネ空Ｒrてハ星Ｓsとフ月Ｔtなヘ山Ｕuにホ川Ｖvぬマ空Ｗwねミ星Ｘxのユ風Ｙyはラ雪Ｚzひリ鳥Ａaふワ雨Ｂbへル森Ｃcほネ空Ｄdまハ星Ｅeみフ月Ｆfむヘ山Ｇgめホ川Ｈhもマ空Ｉiやミ星Ｊjゆユ風Ｋkよラ雪Ｌlらリ鳥Ｍmりワ雨Ｎnるル森Ｏoれネ空Ｐpろハ星Ｑqわフ月Ｒrをヘ山Ｓsんホ川Ｔtあマ空Ｕuいミ花Ｖvうユ風Ｗwえラ雪Ｘxおリ鳥Ｙyかワ雨Ｚzきル森Ａaくネ空Ｂbけハ星Ｃcこフ月Ｄdさヘ山Ｅeしホ川Ｆfすマ空Ｇgせミ星Ｈhそユ風Ｉiたラ雪Ｊjちリ鳥Ｋkつワ雨Ｌlてル森Ｍmとネ空Ｎnなハ星Ｏoにフ月Ｐpぬヘ山Ｑqねホ川Ｒrのマ空Ｓsはミ星Ｔtひユ風Ｕuふラ雪Ｖvへリ鳥Ｗwほワ雨Ｘxまル森Ｙyみネ空Ｚzむハ星Ａaめフ月Ｂbもヘ山Ｃcやホ川Ｄdゆマ空Ｅeよミ星Ｆfらユ風Ｇgりラ雪Ｈhるリ鳥Ｉiれワ雨Ｊjろル森Ｋkわネ空Ｌlをハ星Ｍmんフ月Ｎnあヘ山Ｏoいホ川Ｐpうマ空Ｑqえミ星Ｒrおユ風Ｓsかラ雪Ｔtきリ鳥Ｕuくワ雨Ｖvけル森Ｗwこネ空Ｘxさハ星Ｙyしフ月Ｚzすヘ山Ａaせホ川Ｂbそマ空Ｃcたミ星Ｄdちユ風Ｅeつラ雪Ｆfてリ鳥Ｇgとワ雨Ｈhなル森Ｉiにネ空Ｊjぬハ星Ｋkねフ月Ｌlのヘ山Ｍmはホ川Ｎnひマ空Ｏoふミ星Ｐpへユ風Ｑqほラ雪Ｒrまリ鳥Ｓsみワ雨Ｔtむル森Ｕuめネ空Ｖvもハ星Ｗwやフ月Ｘxゆヘ山Ｙyよホ川Ｚzらマ空Ａaりミ星Ｂbるユ風Ｃcれラ雪Ｄdろリ鳥Ｅeわワ雨Ｆfをル森Ｇgんネ空Ｈhあハ星Ｉiいフ月Ｊjうヘ山Ｋkえホ川Ｌlおマ空Ｍmかミ星Ｎnきユ風Ｏoくラ雪Ｐpけリ鳥Ｑqこワ雨Ｒrさル森Ｓsしネ空Ｔtすハ星Ｕuせフ月Ｖvそヘ山Ｗw");
                  byte[] imageData = new byte[2];
                  new Random().nextBytes(imageData);
                  dto.getPayloadInfos().get(0).setImageBinary(imageData);
                  dto.getPayloadInfos()
                      .get(0)
                      .setFilePhysicalName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  byte[] fileData = new byte[2];
                  new Random().nextBytes(fileData);
                  dto.getPayloadInfos().get(0).setFileBinary(fileData);
                  return dto;
                },
            2),
        Arguments.of(
            "更新：ペイロード名:101文字、ペイロード詳細テキスト:1001文字,画像バイト:3B、ファイル物理名:201文字、ファイルバイト:3Bの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(3);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつ");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadDetailText(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsりネ空Ｔtるハ星Ｕuれフ月Ｖvろヘ山Ｗwわホ川Ｘxをマ空Ｙyんミ花Ｚzあユ風Ａaいラ雪Ｂbうリ鳥Ｃcえワ雨Ｄdおル森Ｅeかネ空Ｆfきハ星Ｇgくフ月Ｈhけヘ山Ｉiこホ川Ｊjさマ空Ｋkしミ星Ｌlすユ風Ｍmせラ雪Ｎnそリ鳥Ｏoたワ雨Ｐpちル森Ｑqつネ空Ｒrてハ星Ｓsとフ月Ｔtなヘ山Ｕuにホ川Ｖvぬマ空Ｗwねミ星Ｘxのユ風Ｙyはラ雪Ｚzひリ鳥Ａaふワ雨Ｂbへル森Ｃcほネ空Ｄdまハ星Ｅeみフ月Ｆfむヘ山Ｇgめホ川Ｈhもマ空Ｉiやミ星Ｊjゆユ風Ｋkよラ雪Ｌlらリ鳥Ｍmりワ雨Ｎnるル森Ｏoれネ空Ｐpろハ星Ｑqわフ月Ｒrをヘ山Ｓsんホ川Ｔtあマ空Ｕuいミ花Ｖvうユ風Ｗwえラ雪Ｘxおリ鳥Ｙyかワ雨Ｚzきル森Ａaくネ空Ｂbけハ星Ｃcこフ月Ｄdさヘ山Ｅeしホ川Ｆfすマ空Ｇgせミ星Ｈhそユ風Ｉiたラ雪Ｊjちリ鳥Ｋkつワ雨Ｌlてル森Ｍmとネ空Ｎnなハ星Ｏoにフ月Ｐpぬヘ山Ｑqねホ川Ｒrのマ空Ｓsはミ星Ｔtひユ風Ｕuふラ雪Ｖvへリ鳥Ｗwほワ雨Ｘxまル森Ｙyみネ空Ｚzむハ星Ａaめフ月Ｂbもヘ山Ｃcやホ川Ｄdゆマ空Ｅeよミ星Ｆfらユ風Ｇgりラ雪Ｈhるリ鳥Ｉiれワ雨Ｊjろル森Ｋkわネ空Ｌlをハ星Ｍmんフ月Ｎnあヘ山Ｏoいホ川Ｐpうマ空Ｑqえミ星Ｒrおユ風Ｓsかラ雪Ｔtきリ鳥Ｕuくワ雨Ｖvけル森Ｗwこネ空Ｘxさハ星Ｙyしフ月Ｚzすヘ山Ａaせホ川Ｂbそマ空Ｃcたミ星Ｄdちユ風Ｅeつラ雪Ｆfてリ鳥Ｇgとワ雨Ｈhなル森Ｉiにネ空Ｊjぬハ星Ｋkねフ月Ｌlのヘ山Ｍmはホ川Ｎnひマ空Ｏoふミ星Ｐpへユ風Ｑqほラ雪Ｒrまリ鳥Ｓsみワ雨Ｔtむル森Ｕuめネ空Ｖvもハ星Ｗwやフ月Ｘxゆヘ山Ｙyよホ川Ｚzらマ空Ａaりミ星Ｂbるユ風Ｃcれラ雪Ｄdろリ鳥Ｅeわワ雨Ｆfをル森Ｇgんネ空Ｈhあハ星Ｉiいフ月Ｊjうヘ山Ｋkえホ川Ｌlおマ空Ｍmかミ星Ｎnきユ風Ｏoくラ雪Ｐpけリ鳥Ｑqこワ雨Ｒrさル森Ｓsしネ空Ｔtすハ星Ｕuせフ月Ｖvそヘ山Ｗw");
                  byte[] imageData = new byte[3];
                  new Random().nextBytes(imageData);
                  dto.getPayloadInfos().get(0).setImageBinary(imageData);
                  dto.getPayloadInfos()
                      .get(0)
                      .setFilePhysicalName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  byte[] fileData = new byte[3];
                  new Random().nextBytes(fileData);
                  dto.getPayloadInfos().get(0).setFileBinary(fileData);
                  return dto;
                },
            2),
        Arguments.of(
            "登録：ペイロード情報ペイロードファイル、イメージのバイナリがnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setImageBinary(null);
                  dto.getPayloadInfos().get(0).setFileBinary(null);
                  return dto;
                },
            28));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 正常系の様々なパターンでのバリデーション動作検証<br>
   * 条件: 必須項目を含む正常なリクエストDTO（ペイロード情報の各パターン）<br>
   * 結果: 例外が発生せず、正常に処理が完了する<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("nmlPayloadCase")
  public void testValidateForUpdate_nmlPayloadCase(
      String caseName, Supplier<AircraftInfoRequestDto> sDto, int maxSize) {
    doReturn(maxSize)
        .when(systemSettings)
        .getIntegerValue(
            AircraftConstants.SETTINGS_FILE_DATA, AircraftConstants.SETTINGS_MAX_FILE_BINARY_SIZE);
    AircraftInfoRequestDto dto = sDto.get();
    assertDoesNotThrow(() -> validator.validateForRegist(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * ParameterizedTest用の引数準備メソッド<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   * 第3引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> errPayloadCase() {
    return Stream.of(
        Arguments.of(
            "処理種別が4の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(4);
                  return dto;
                },
            "[1番目のペイロード情報の処理種別の値が不正です。\n範囲[1, 2, 3]]"),
        Arguments.of(
            "処理種別がnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(null);
                  return dto;
                },
            "[1番目のペイロード情報の処理種別に値が設定されていません。]"),
        Arguments.of(
            "登録：ペイロード名がnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(0).setPayloadName(null);
                  return dto;
                },
            "[1番目のペイロード情報のペイロード名に値が設定されていません。]"),
        Arguments.of(
            "登録：ペイロード名が空文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(0).setPayloadName("");
                  return dto;
                },
            "[1番目のペイロード情報のペイロード名に値が設定されていません。]"),
        Arguments.of(
            "登録：ペイロード名が101文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつ");
                  return dto;
                },
            "[1番目のペイロード情報のペイロード名の長さが不正です。\n最大長(100)]"),
        Arguments.of(
            "登録：ペイロード詳細テキストが1001文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadDetailText(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsりネ空Ｔtるハ星Ｕuれフ月Ｖvろヘ山Ｗwわホ川Ｘxをマ空Ｙyんミ花Ｚzあユ風Ａaいラ雪Ｂbうリ鳥Ｃcえワ雨Ｄdおル森Ｅeかネ空Ｆfきハ星Ｇgくフ月Ｈhけヘ山Ｉiこホ川Ｊjさマ空Ｋkしミ星Ｌlすユ風Ｍmせラ雪Ｎnそリ鳥Ｏoたワ雨Ｐpちル森Ｑqつネ空Ｒrてハ星Ｓsとフ月Ｔtなヘ山Ｕuにホ川Ｖvぬマ空Ｗwねミ星Ｘxのユ風Ｙyはラ雪Ｚzひリ鳥Ａaふワ雨Ｂbへル森Ｃcほネ空Ｄdまハ星Ｅeみフ月Ｆfむヘ山Ｇgめホ川Ｈhもマ空Ｉiやミ星Ｊjゆユ風Ｋkよラ雪Ｌlらリ鳥Ｍmりワ雨Ｎnるル森Ｏoれネ空Ｐpろハ星Ｑqわフ月Ｒrをヘ山Ｓsんホ川Ｔtあマ空Ｕuいミ花Ｖvうユ風Ｗwえラ雪Ｘxおリ鳥Ｙyかワ雨Ｚzきル森Ａaくネ空Ｂbけハ星Ｃcこフ月Ｄdさヘ山Ｅeしホ川Ｆfすマ空Ｇgせミ星Ｈhそユ風Ｉiたラ雪Ｊjちリ鳥Ｋkつワ雨Ｌlてル森Ｍmとネ空Ｎnなハ星Ｏoにフ月Ｐpぬヘ山Ｑqねホ川Ｒrのマ空Ｓsはミ星Ｔtひユ風Ｕuふラ雪Ｖvへリ鳥Ｗwほワ雨Ｘxまル森Ｙyみネ空Ｚzむハ星Ａaめフ月Ｂbもヘ山Ｃcやホ川Ｄdゆマ空Ｅeよミ星Ｆfらユ風Ｇgりラ雪Ｈhるリ鳥Ｉiれワ雨Ｊjろル森Ｋkわネ空Ｌlをハ星Ｍmんフ月Ｎnあヘ山Ｏoいホ川Ｐpうマ空Ｑqえミ星Ｒrおユ風Ｓsかラ雪Ｔtきリ鳥Ｕuくワ雨Ｖvけル森Ｗwこネ空Ｘxさハ星Ｙyしフ月Ｚzすヘ山Ａaせホ川Ｂbそマ空Ｃcたミ星Ｄdちユ風Ｅeつラ雪Ｆfてリ鳥Ｇgとワ雨Ｈhなル森Ｉiにネ空Ｊjぬハ星Ｋkねフ月Ｌlのヘ山Ｍmはホ川Ｎnひマ空Ｏoふミ星Ｐpへユ風Ｑqほラ雪Ｒrまリ鳥Ｓsみワ雨Ｔtむル森Ｕuめネ空Ｖvもハ星Ｗwやフ月Ｘxゆヘ山Ｙyよホ川Ｚzらマ空Ａaりミ星Ｂbるユ風Ｃcれラ雪Ｄdろリ鳥Ｅeわワ雨Ｆfをル森Ｇgんネ空Ｈhあハ星Ｉiいフ月Ｊjうヘ山Ｋkえホ川Ｌlおマ空Ｍmかミ星Ｎnきユ風Ｏoくラ雪Ｐpけリ鳥Ｑqこワ雨Ｒrさル森Ｓsしネ空Ｔtすハ星Ｕuせフ月Ｖvそヘ山Ｗwた");
                  return dto;
                },
            "[1番目のペイロード情報のペイロード詳細テキストの長さが不正です。\n最大長(1,000)]"),
        Arguments.of(
            "登録：画像がMIMEタイプ欠落の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setImageData("data:image;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[1番目のペイロード情報の画像形式が不正です。]"),
        Arguments.of(
            "登録：画像がサポート外MIMEタイプの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setImageData(
                          "data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[1番目のペイロード情報の画像形式が不正です。]"),
        Arguments.of(
            "登録：画像がフォーマット不正の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setImageData(
                          "date:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[1番目のペイロード情報の画像形式が不正です。]"),
        Arguments.of(
            "登録：画像が設定値より1バイト多い場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  byte[] imageData = new byte[29];
                  new Random().nextBytes(imageData);
                  dto.getPayloadInfos().get(0).setImageBinary(imageData);
                  return dto;
                },
            "[1番目のペイロード情報の画像の長さが不正です。\n最大長(28)]"),
        Arguments.of(
            "登録：ファイル物理名がnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(0).setFilePhysicalName(null);
                  return dto;
                },
            "[1番目のペイロード情報のファイル物理名に値が設定されていません。]"),
        Arguments.of(
            "登録：ファイル物理名が空文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(0).setFilePhysicalName("");
                  return dto;
                },
            "[1番目のペイロード情報のファイル物理名に値が設定されていません。]"),
        Arguments.of(
            "登録：ファイル物理名が201文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setFilePhysicalName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[1番目のペイロード情報のファイル物理名の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "登録：ファイルがMIMEタイプ欠落の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setFileData("data:plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "登録：ファイルがMIMEタイプ欠落の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setFileData("data:text;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "登録：ファイルがサポート外MIMEタイプの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setFileData(
                          "data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "登録：ファイルがサポート外MIMEタイプの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos()
                      .get(0)
                      .setFileData(
                          "date:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "登録：ファイルが設定値より1バイト多い場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  byte[] fileData = new byte[29];
                  new Random().nextBytes(fileData);
                  dto.getPayloadInfos().get(0).setFileBinary(fileData);
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)の長さが不正です。\n最大長(28)]"),
        Arguments.of(
            "更新：ペイロードIDがnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId(null);
                  return dto;
                },
            "[1番目のペイロード情報のペイロードIDに値が設定されていません。]"),
        Arguments.of(
            "更新：ペイロードIDが空文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("");
                  return dto;
                },
            "[1番目のペイロード情報のペイロードIDに値が設定されていません。]"),
        Arguments.of(
            "更新：ペイロードIDがUUID以外の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("notUUID");
                  return dto;
                },
            "[1番目のペイロード情報のペイロードIDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "更新：ペイロード名が101文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつ");
                  return dto;
                },
            "[1番目のペイロード情報のペイロード名の長さが不正です。\n最大長(100)]"),
        Arguments.of(
            "更新：ペイロード詳細テキストが1001文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setPayloadDetailText(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsりネ空Ｔtるハ星Ｕuれフ月Ｖvろヘ山Ｗwわホ川Ｘxをマ空Ｙyんミ花Ｚzあユ風Ａaいラ雪Ｂbうリ鳥Ｃcえワ雨Ｄdおル森Ｅeかネ空Ｆfきハ星Ｇgくフ月Ｈhけヘ山Ｉiこホ川Ｊjさマ空Ｋkしミ星Ｌlすユ風Ｍmせラ雪Ｎnそリ鳥Ｏoたワ雨Ｐpちル森Ｑqつネ空Ｒrてハ星Ｓsとフ月Ｔtなヘ山Ｕuにホ川Ｖvぬマ空Ｗwねミ星Ｘxのユ風Ｙyはラ雪Ｚzひリ鳥Ａaふワ雨Ｂbへル森Ｃcほネ空Ｄdまハ星Ｅeみフ月Ｆfむヘ山Ｇgめホ川Ｈhもマ空Ｉiやミ星Ｊjゆユ風Ｋkよラ雪Ｌlらリ鳥Ｍmりワ雨Ｎnるル森Ｏoれネ空Ｐpろハ星Ｑqわフ月Ｒrをヘ山Ｓsんホ川Ｔtあマ空Ｕuいミ花Ｖvうユ風Ｗwえラ雪Ｘxおリ鳥Ｙyかワ雨Ｚzきル森Ａaくネ空Ｂbけハ星Ｃcこフ月Ｄdさヘ山Ｅeしホ川Ｆfすマ空Ｇgせミ星Ｈhそユ風Ｉiたラ雪Ｊjちリ鳥Ｋkつワ雨Ｌlてル森Ｍmとネ空Ｎnなハ星Ｏoにフ月Ｐpぬヘ山Ｑqねホ川Ｒrのマ空Ｓsはミ星Ｔtひユ風Ｕuふラ雪Ｖvへリ鳥Ｗwほワ雨Ｘxまル森Ｙyみネ空Ｚzむハ星Ａaめフ月Ｂbもヘ山Ｃcやホ川Ｄdゆマ空Ｅeよミ星Ｆfらユ風Ｇgりラ雪Ｈhるリ鳥Ｉiれワ雨Ｊjろル森Ｋkわネ空Ｌlをハ星Ｍmんフ月Ｎnあヘ山Ｏoいホ川Ｐpうマ空Ｑqえミ星Ｒrおユ風Ｓsかラ雪Ｔtきリ鳥Ｕuくワ雨Ｖvけル森Ｗwこネ空Ｘxさハ星Ｙyしフ月Ｚzすヘ山Ａaせホ川Ｂbそマ空Ｃcたミ星Ｄdちユ風Ｅeつラ雪Ｆfてリ鳥Ｇgとワ雨Ｈhなル森Ｉiにネ空Ｊjぬハ星Ｋkねフ月Ｌlのヘ山Ｍmはホ川Ｎnひマ空Ｏoふミ星Ｐpへユ風Ｑqほラ雪Ｒrまリ鳥Ｓsみワ雨Ｔtむル森Ｕuめネ空Ｖvもハ星Ｗwやフ月Ｘxゆヘ山Ｙyよホ川Ｚzらマ空Ａaりミ星Ｂbるユ風Ｃcれラ雪Ｄdろリ鳥Ｅeわワ雨Ｆfをル森Ｇgんネ空Ｈhあハ星Ｉiいフ月Ｊjうヘ山Ｋkえホ川Ｌlおマ空Ｍmかミ星Ｎnきユ風Ｏoくラ雪Ｐpけリ鳥Ｑqこワ雨Ｒrさル森Ｓsしネ空Ｔtすハ星Ｕuせフ月Ｖvそヘ山Ｗwた");
                  return dto;
                },
            "[1番目のペイロード情報のペイロード詳細テキストの長さが不正です。\n最大長(1,000)]"),
        Arguments.of(
            "更新：画像がMIMEタイプ欠落の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setImageData("data:image;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[1番目のペイロード情報の画像形式が不正です。]"),
        Arguments.of(
            "更新：画像がサポート外MIMEタイプの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setImageData(
                          "data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[1番目のペイロード情報の画像形式が不正です。]"),
        Arguments.of(
            "更新：画像がフォーマット不正の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setImageData(
                          "date:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[1番目のペイロード情報の画像形式が不正です。]"),
        Arguments.of(
            "更新：画像が設定値より1バイト多い場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  byte[] imageData = new byte[29];
                  new Random().nextBytes(imageData);
                  dto.getPayloadInfos().get(0).setImageBinary(imageData);
                  return dto;
                },
            "[1番目のペイロード情報の画像の長さが不正です。\n最大長(28)]"),
        Arguments.of(
            "更新：ファイル物理名が201文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setFilePhysicalName(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[1番目のペイロード情報のファイル物理名の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "更新：ファイルがMIMEタイプ欠落の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setFileData("data:plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "更新：ファイルがMIMEタイプ欠落の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setFileData("data:text;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "更新：ファイルがサポート外MIMEタイプの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setFileData(
                          "data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "更新：ファイルがサポート外MIMEタイプの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  dto.getPayloadInfos()
                      .get(0)
                      .setFileData(
                          "date:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)はサポート対象外のファイル形式です。]"),
        Arguments.of(
            "更新：ファイルが設定値より1バイト多い場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(2);
                  dto.getPayloadInfos().get(0).setPayloadId("7ed6002d-a68f-4e2d-a530-3cd281b5093e");
                  byte[] fileData = new byte[29];
                  new Random().nextBytes(fileData);
                  dto.getPayloadInfos().get(0).setFileBinary(fileData);
                  return dto;
                },
            "[payload_hosoku.txt(1番目のペイロード情報のファイル)の長さが不正です。\n最大長(28)]"),
        Arguments.of(
            "削除：ペイロードIDがnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(3);
                  dto.getPayloadInfos().get(0).setPayloadId(null);
                  return dto;
                },
            "[1番目のペイロード情報のペイロードIDに値が設定されていません。]"),
        Arguments.of(
            "削除：ペイロードIDが空文字の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(3);
                  dto.getPayloadInfos().get(0).setPayloadId("");
                  return dto;
                },
            "[1番目のペイロード情報のペイロードIDに値が設定されていません。]"),
        Arguments.of(
            "削除：ペイロードIDがUUID以外の場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(3);
                  dto.getPayloadInfos().get(0).setPayloadId("notUUID");
                  return dto;
                },
            "[1番目のペイロード情報のペイロードIDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "削除：ペイロード名がnull、ペイロード詳細テキストがnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(0).setPayloadName(null);
                  dto.getPayloadInfos().get(0).setFilePhysicalName(null);
                  return dto;
                },
            "[1番目のペイロード情報のファイル物理名に値が設定されていません。, 1番目のペイロード情報のペイロード名に値が設定されていません。]"),
        Arguments.of(
            "削除：ペイロード名がnull、ペイロード詳細テキストがnullの場合",
            (Supplier<AircraftInfoRequestDto>)
                () -> {
                  AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload3();
                  dto.getPayloadInfos().get(0).setProcessingType(1);
                  dto.getPayloadInfos().get(1).setProcessingType(1);
                  dto.getPayloadInfos().get(1).setPayloadName(null);
                  dto.getPayloadInfos().get(2).setProcessingType(1);
                  dto.getPayloadInfos().get(2).setPayloadName(null);
                  return dto;
                },
            "[2番目のペイロード情報のペイロード名に値が設定されていません。, 3番目のペイロード情報のペイロード名に値が設定されていません。]"));
  }

  /**
   * メソッド名: validateForUpdate<br>
   * 試験名: 異常系の様々なパターンでのバリデーションエラー動作検証<br>
   * 条件: 必須項目が欠落または不正な値を含むリクエストDTO（ペイロード情報のエラーケース）<br>
   * 結果: ValidationErrorExceptionがスローされ、エラーメッセージが検証される<br>
   * テストパターン: 異常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("errPayloadCase")
  public void testValidateForUpdate_errPayloadCase(
      String caseName, Supplier<AircraftInfoRequestDto> sDto, String msg) {
    doReturn(28)
        .when(systemSettings)
        .getIntegerValue(
            AircraftConstants.SETTINGS_FILE_DATA, AircraftConstants.SETTINGS_MAX_FILE_BINARY_SIZE);
    doReturn(28)
        .when(systemSettings)
        .getIntegerValue(
            AircraftConstants.SETTINGS_IMAGE_DATA, AircraftConstants.SETTINGS_BINARY_SIZE);
    AircraftInfoRequestDto dto = sDto.get();
    Exception ex =
        assertThrows(ValidationErrorException.class, () -> validator.validateForRegist(dto));
    assertEquals(msg, ex.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 正常なAircraftInfoSearchListRequestDtoを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_Normal() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setPerPage("1");
    dto.setPage("1");
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 矩形情報なしのAircraftInfoSearchListRequestDtoを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_Normal_NoLatLon() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 最小緯度が未設定<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_NG_MinLat() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("false");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    //        dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 最小経度が未設定<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_NG_MinLon() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("false");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    //        dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 最大緯度のみ設定<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_NG_MaxLatOnly() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("false");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    //        dto.setMinLat(38.00);
    //        dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    //        dto.setMaxLon(139.0);

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 最大経度のみ設定<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_NG_MaxLonOnly() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("false");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    //        dto.setMinLat(38.00);
    //        dto.setMinLon(138.00);
    //        dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 検索条件情報のみAircraftInfoSearchListRequestDtoを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_Nodata() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 型式番号がnull<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_NullModelNumber() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setModelNumber(null);
    dto.setManufacturingNumber("1234567890");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 型式番号が空文字<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_EmptyModelNumber() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setModelNumber("");
    dto.setManufacturingNumber("1234567890");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 型式番号20文字<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_LimitModelNumber() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setModelNumber("abcdefghijklmnopqrst");
    dto.setManufacturingNumber("1234567890");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 不正な機体認証の有無を渡す<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_NG_Certification() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("XXXX");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 型式番号が201文字<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_NG_LimitModelNumber() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setModelNumber(
        "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
    dto.setManufacturingNumber("1234567890");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(
        MessageFormat.format("[{0}の長さが不正です。\n最大長({1})]", "型式番号", 200), exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 機体名と型式番号が最大長超過<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_NG_LimitAircraftNameAndModelNumber() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("1234567890123456789012345");
    dto.setManufacturer("Test Manufacturer");
    dto.setModelNumber(
        "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
    dto.setManufacturingNumber("1234567890");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(
        MessageFormat.format(
            "[{0}の長さが不正です。\n最大長({1}), {2}の長さが不正です。\n最大長({3})]", "機体名", 24, "型式番号", 200),
        exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: ソート順序とソートカラムの数が不一致<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_NG_NotEqual_OrderAndColumns() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setPerPage("1");
    dto.setPage("1");
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId, aircraftName");

    assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 1ページ当たりの件数が範囲外<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_page1_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
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
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 現在ページ番号が範囲外<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_page2_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
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
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 1ページ当たりの件数未設定、現在ページ番号設定済み<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_page3_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage(null);
    dto.setPage("1");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(MessageFormat.format("[{0}に値が設定されていません。]", "1ページ当たりの件数"), exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 1ページ当たりの件数設定済み、現在ページ番号未設定<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_page4_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage("1");
    dto.setPage(null);

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals(MessageFormat.format("[{0}に値が設定されていません。]", "現在ページ番号"), exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 機体種別が範囲外の数値<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_AircraftType1_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("1,6,99999999999999999999999999999");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage("1");
    dto.setPage("1");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[機体の種別の値が不正です。\n最小値(1)、最大値(6)]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 機体種別に文字が含まれる<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_AircraftType2_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("1,6,A");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage("1");
    dto.setPage("1");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[機体の種別が不正です。\n入力値:A, 機体の種別の値が不正です。\n最小値(1)、最大値(6)]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 機体種別がnull<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_AircraftType3_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType(null);
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage("1");
    dto.setPage("1");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 機体種別が空文字<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_AircraftType4_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage("1");
    dto.setPage("1");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 機体所有種別が範囲外の数値<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_OwnerType1_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("1,2,3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1,99999999999999999999999999999");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage("1");
    dto.setPage("1");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[機体所有種別の値が不正です。\n最小値(1)、最大値(2)]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの異常系テスト<br>
   * 条件: 機体所有種別に文字が含まれる<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForGetList_OwnerType2_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("1,2,3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("1,A");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage("1");
    dto.setPage("1");

    ValidationErrorException exception =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));

    assertEquals("[機体所有種別が不正です。\n入力値:A, 機体所有種別の値が不正です。\n最小値(1)、最大値(2)]", exception.getMessage());
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 機体所有種別がnull<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_OwnerType3_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("1,2,3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType(null);
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage("1");
    dto.setPage("1");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 機体情報一覧APIパラメータチェックの正常系テスト<br>
   * 条件: 機体所有種別が空文字<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForGetList_OwnerType4_throwsExcepion() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("Test Aircraft");
    dto.setManufacturer("Test Manufacturer");
    dto.setManufacturingNumber("1234567890");
    dto.setAircraftType("1,2,3");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("DIPS123456");
    dto.setOwnerType("");
    dto.setOwnerId("123e4567-e89b-12d3-a456-426614174000");
    dto.setMinLat(38.00);
    dto.setMinLon(138.00);
    dto.setMaxLat(39.0);
    dto.setMaxLon(139.0);
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");
    dto.setPerPage("1");
    dto.setPage("1");

    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   */
  static Stream<Arguments> nmlListCase() {
    return Stream.of(
        Arguments.of(
            "ペイロード情報要否がnull",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPayloadInfo(null);
                  return dto;
                }),
        Arguments.of(
            "ペイロード情報要否が空文字",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPayloadInfo("");
                  return dto;
                }),
        Arguments.of(
            "ペイロード情報要否がtrue",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPayloadInfo("true");
                  return dto;
                }),
        Arguments.of(
            "ペイロード情報要否がtrue",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPayloadInfo("false");
                  return dto;
                }),
        Arguments.of(
            "ペイロード情報要否が1",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPayloadInfo("1");
                  return dto;
                }),
        Arguments.of(
            "料金情報要否がnull",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPriceInfo(null);
                  return dto;
                }),
        Arguments.of(
            "料金情報要否が空文字",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPriceInfo("");
                  return dto;
                }),
        Arguments.of(
            "料金情報要否がtrue",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPriceInfo("true");
                  return dto;
                }),
        Arguments.of(
            "料金情報要否がtrue",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPriceInfo("false");
                  return dto;
                }),
        Arguments.of(
            "料金情報要否が1",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPriceInfo("1");
                  return dto;
                }),
        Arguments.of(
            "製造メーカー:null、型式番号:null",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setManufacturer(null);
                  dto.setModelNumber(null);
                  return dto;
                }),
        Arguments.of(
            "製造メーカー:空文字",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setManufacturer("");
                  dto.setModelNumber("");
                  return dto;
                }),
        Arguments.of(
            "製造メーカー:200文字",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setManufacturer(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  dto.setModelNumber(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  return dto;
                }),
        Arguments.of(
            "機種名:null",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setModelName(null);
                  return dto;
                }),
        Arguments.of(
            "機種名:空文字",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setModelName("");
                  return dto;
                }),
        Arguments.of(
            "機種名:200文字",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setModelName(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  return dto;
                }),
        Arguments.of(
            "公開可否フラグ:null",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setPublicFlag(null);
                  return dto;
                }),
        Arguments.of(
            "公開可否フラグ:空文字",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setPublicFlag("");
                  return dto;
                }),
        Arguments.of(
            "公開可否フラグ:true",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setPublicFlag("true");
                  return dto;
                }),
        Arguments.of(
            "公開可否フラグ:false",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setPublicFlag("false");
                  return dto;
                }));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 正常系の様々なパターンでのバリデーション動作検証<br>
   * 条件: ペイロード・製造メーカー・機種名・フラグの各パターンを含むリクエストDTO<br>
   * 結果: 例外が発生せず、正常に処理が完了する<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("nmlListCase")
  public void testValidateForGetList_nmlListCase(
      String caseName, Supplier<AircraftInfoSearchListRequestDto> sDto) {
    AircraftInfoSearchListRequestDto dto = sDto.get();
    assertDoesNotThrow(() -> validator.validateForGetList(dto));
  }

  /**
   * メソッド名: validateForGetList<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に機体ID<br>
   * 第3引数に補足資料ID<br>
   * 第4引数に検証対象のdto<br>
   * 第5引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> errListCase() {
    return Stream.of(
        Arguments.of(
            "ペイロード情報要否がTRUE",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPayloadInfo("TRUE");
                  return dto;
                },
            "[ペイロード情報要否が不正です。\n入力値:TRUE]"),
        Arguments.of(
            "料金情報要否がTRUE",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setIsRequiredPriceInfo("TRUE");
                  return dto;
                },
            "[料金情報要否が不正です。\n入力値:TRUE]"),
        Arguments.of(
            "製造メーカー:201文字、型式番号:201文字",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setManufacturer(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  dto.setModelNumber(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[製造メーカーの長さが不正です。\n最大長(200), 型式番号の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "製造メーカー:(200文字,200文字)、型式番号:(200文字,200文字)",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setManufacturer(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs,あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  dto.setModelNumber(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs,あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  return dto;
                },
            "[製造メーカーの長さが不正です。\n最大長(200), 型式番号の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "機種名:201文字",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setModelName(
                      "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[機種名の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "公開可否フラグ：TRUE",
            (Supplier<AircraftInfoSearchListRequestDto>)
                () -> {
                  AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
                  dto.setPublicFlag("TRUE");
                  return dto;
                },
            "[公開可否フラグが不正です。\n入力値:TRUE]"));
  }

  /**
   * メソッド名: validateForGetList<br>
   * 試験名: 異常系の様々なパターンでのバリデーションエラー動作検証<br>
   * 条件: ペイロード・製造メーカー・機種名・フラグに不正な値を含むリクエストDTO<br>
   * 結果: ValidationErrorExceptionがスローされ、エラーメッセージが検証される<br>
   * テストパターン: 異常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("errListCase")
  public void testValidateForGetList_errListCase(
      String caseName, Supplier<AircraftInfoSearchListRequestDto> sDto, String msg) {
    AircraftInfoSearchListRequestDto dto = sDto.get();
    Exception ex =
        assertThrows(ValidationErrorException.class, () -> validator.validateForGetList(dto));
    assertEquals(msg, ex.getMessage());
  }

  /**
   * メソッド名: validateForModelSearch<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   */
  static Stream<Arguments> nmlModelSearchCase() {
    return Stream.of(
        Arguments.of(
            "Nomal",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  return dto;
                }),
        Arguments.of(
            "製造メーカー:200文字",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos()
                      .get(1)
                      .setManufacturer(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  return dto;
                }),
        Arguments.of(
            "型式番号:200文字",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos()
                      .get(1)
                      .setModelNumber(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓs");
                  return dto;
                }),
        Arguments.of(
            "ペイロード情報要否:false",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setIsRequiredPayloadInfo("false");
                  return dto;
                }),
        Arguments.of(
            "ペイロード情報要否:null",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setIsRequiredPayloadInfo(null);
                  return dto;
                }),
        Arguments.of(
            "ペイロード情報要否:空文字",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setIsRequiredPayloadInfo("");
                  return dto;
                }),
        Arguments.of(
            "料金情報要否:false",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setIsRequiredPriceInfo("false");
                  return dto;
                }),
        Arguments.of(
            "料金情報要否:null",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setIsRequiredPriceInfo(null);
                  return dto;
                }),
        Arguments.of(
            "料金情報要否:空文字",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setIsRequiredPriceInfo("");
                  return dto;
                }));
  }

  /**
   * メソッド名: validateForModelSearch<br>
   * 試験名: 正常系の様々なパターンでのバリデーション動作検証<br>
   * 条件: モデル情報リストの各パターンを含むリクエストDTO<br>
   * 結果: 例外が発生せず、正常に処理が完了する<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("nmlModelSearchCase")
  public void testValidateForGetList_nmlModelSearchCase(
      String caseName, Supplier<AircraftInfoModelSearchRequestDto> sDto) {
    AircraftInfoModelSearchRequestDto dto = sDto.get();
    assertDoesNotThrow(() -> validator.validateForModelSearch(dto));
  }

  /**
   * メソッド名: validateForModelSearch<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に検証対象のdto<br>
   * 第3引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> errModelSearchCase() {
    return Stream.of(
        Arguments.of(
            "モデル情報リストがnull",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setModelInfos(null);
                  return dto;
                },
            "[モデル情報リストに値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストが空配列",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setModelInfos(Collections.emptyList());
                  return dto;
                },
            "[モデル情報リストに値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストの要素1がnull",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos().set(0, null);
                  return dto;
                },
            "[1番目のモデル情報に値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストの要素2がnull",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos().set(1, null);
                  return dto;
                },
            "[2番目のモデル情報に値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストの要素3がnull",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos().set(2, null);
                  return dto;
                },
            "[3番目のモデル情報に値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストの要素1~3がnull",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos().set(0, null);
                  dto.getModelInfos().set(1, null);
                  dto.getModelInfos().set(2, null);
                  return dto;
                },
            "[1番目のモデル情報に値が設定されていません。, 2番目のモデル情報に値が設定されていません。, 3番目のモデル情報に値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストの要素2の製造メーカーがnull",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos().get(1).setManufacturer(null);
                  return dto;
                },
            "[2番目のモデル情報の製造メーカーに値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストの要素2の製造メーカーが空文字",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos().get(1).setManufacturer("");
                  return dto;
                },
            "[2番目のモデル情報の製造メーカーに値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストの要素2の製造メーカーが201文字",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos()
                      .get(1)
                      .setManufacturer(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[2番目のモデル情報の製造メーカーの長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "モデル情報リストの要素2の型式番号がnull",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos().get(1).setModelNumber(null);
                  return dto;
                },
            "[2番目のモデル情報の型式番号に値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストの要素2の型式番号が空文字",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos().get(1).setModelNumber("");
                  return dto;
                },
            "[2番目のモデル情報の型式番号に値が設定されていません。]"),
        Arguments.of(
            "モデル情報リストの要素2の型式番号が201文字",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.getModelInfos()
                      .get(1)
                      .setModelNumber(
                          "あカ山Ｂcねタ川Ｄeひホ空Ｆgもケ星Ｈhやサ月Ｉjうト森Ｋkえナ海Ｌlおミ花Ｍmかユ風Ｎnきラ雪Ｏoくリ鳥Ｐpけワ雨Ｑqこル森Ｒrさネ空Ｓsしハ星Ｔtすフ月Ｕuせヘ山Ｖvそホ川Ｗwたマ空Ｘxちミ星Ｙyつオ森Ｚzなカ海Ａaにサ花Ｂbぬト風Ｃcねラ雪Ｄdのリ鳥Ｅeはワ雨Ｆfひル森Ｇgふネ空Ｈhへハ星Ｉiほフ月Ｊjまヘ山Ｋkみホ川Ｌlむマ空Ｍmめミ星Ｎnもユ風Ｏoやラ雪Ｐpゆリ鳥Ｑqよワ雨Ｒrらル森Ｓsり");
                  return dto;
                },
            "[2番目のモデル情報の型式番号の長さが不正です。\n最大長(200)]"),
        Arguments.of(
            "ペイロード情報要否がTRUE",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setIsRequiredPayloadInfo("TRUE");
                  return dto;
                },
            "[ペイロード情報要否が不正です。\n入力値:TRUE]"),
        Arguments.of(
            "料金情報要否がTRUE",
            (Supplier<AircraftInfoModelSearchRequestDto>)
                () -> {
                  AircraftInfoModelSearchRequestDto dto = createAircraftInfoModelSearchRequestDto();
                  dto.setIsRequiredPriceInfo("TRUE");
                  return dto;
                },
            "[料金情報要否が不正です。\n入力値:TRUE]"));
  }

  /**
   * メソッド名: validateForModelSearch<br>
   * 試験名: 異常系の様々なパターンでのバリデーションエラー動作検証<br>
   * 条件: モデル情報リストに不正な値を含むリクエストDTO<br>
   * 結果: ValidationErrorExceptionがスローされ、エラーメッセージが検証される<br>
   * テストパターン: 異常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("errModelSearchCase")
  public void testvalidateForModelSearch_errModelSearchCase(
      String caseName, Supplier<AircraftInfoModelSearchRequestDto> sDto, String msg) {
    AircraftInfoModelSearchRequestDto dto = sDto.get();
    Exception ex =
        assertThrows(ValidationErrorException.class, () -> validator.validateForModelSearch(dto));
    assertEquals(msg, ex.getMessage());
  }

  /**
   * メソッド名: validateForDetail<br>
   * 試験名: 機体情報詳細APIパラメータチェックの正常系テスト<br>
   * 条件: 正常なaircraftIdを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForDetail_Normal() {
    String aircraftId = UUID.randomUUID().toString();

    assertDoesNotThrow(() -> validator.validateForDetail(aircraftId));
  }

  /**
   * メソッド名: validateForDetail<br>
   * 試験名: 機体情報詳細APIパラメータチェックの異常系テスト<br>
   * 条件: 無効なaircraftIdを渡す<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void testValidateForDetail_InvalidAircraftId() {
    String aircraftId = "invalid-uuid";

    assertThrows(ValidationErrorException.class, () -> validator.validateForDetail(aircraftId));
  }

  /**
   * メソッド名: validateForDelete<br>
   * 試験名: 機体情報削除APIパラメータチェックの正常系テスト<br>
   * 条件: 正常な削除リクエストを渡す<br>
   * 結果: 例外が発生しない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testValidateForDelete_Normal() {
    AircraftInfoDeleteRequestDto dto = new AircraftInfoDeleteRequestDto();
    dto.setOperatorId("dummyOperator");

    assertDoesNotThrow(() -> validator.validateForDelete(dto));
  }

  /**
   * メソッド名: validateForDownloadFile<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に機体ID<br>
   * 第3引数に補足資料ID<br>
   * 第4引数に検証対象のdto<br>
   */
  static Stream<Arguments> nmlDlCase() {
    return Stream.of(
        Arguments.of(
            "補足資料情報1つ",
            "0a0711a5-ff74-4164-9309-8888b433cf22",
            "7ed6002d-a68f-4e2d-a530-3cd281b5093e"));
  }

  /**
   * メソッド名: validateForDownloadFile<br>
   * 試験名: 正常系の様々なパターンでのバリデーション動作検証<br>
   * 条件: 機体IDと補足資料IDの各パターン<br>
   * 結果: 例外が発生せず、正常に処理が完了する<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("nmlDlCase")
  public void testValidateForUpdate_nmlDlCase(String caseName, String aircraftId, String fileId) {
    assertDoesNotThrow(() -> validator.validateForDownloadFile(aircraftId, fileId));
  }

  /**
   * メソッド名: validateForDownloadFile<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に機体ID<br>
   * 第3引数に補足資料ID<br>
   * 第4引数に検証対象のdto<br>
   * 第5引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> errDlCase() {
    return Stream.of(
        Arguments.of(
            "機体IDがUUIDではない",
            "notUUID",
            "7ed6002d-a68f-4e2d-a530-3cd281b5093e",
            "[機体IDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "機体IDがnull", null, "7ed6002d-a68f-4e2d-a530-3cd281b5093e", "[機体IDに値が設定されていません。]"),
        Arguments.of("機体IDが空文字", "", "7ed6002d-a68f-4e2d-a530-3cd281b5093e", "[機体IDに値が設定されていません。]"),
        Arguments.of(
            "補足資料IDがUUIDではない",
            "0a0711a5-ff74-4164-9309-8888b433cf22",
            "notUUID",
            "[補足資料IDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of(
            "補足資料IDがnull", "0a0711a5-ff74-4164-9309-8888b433cf22", null, "[補足資料IDに値が設定されていません。]"),
        Arguments.of(
            "補足資料IDが空文字", "0a0711a5-ff74-4164-9309-8888b433cf22", "", "[補足資料IDに値が設定されていません。]"),
        Arguments.of("複数チェックエラー", null, null, "[機体IDに値が設定されていません。, 補足資料IDに値が設定されていません。]"));
  }

  /**
   * メソッド名: validateForDownloadFile<br>
   * 試験名: 異常系の様々なパターンでのバリデーションエラー動作検証<br>
   * 条件: 機体IDまたは補足資料IDが欠落または不正な値<br>
   * 結果: ValidationErrorExceptionがスローされ、エラーメッセージが検証される<br>
   * テストパターン: 異常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("errDlCase")
  public void testValidateForUpdate_errDlCase(
      String caseName, String aircraftId, String fileId, String msg) {
    Exception ex =
        assertThrows(
            ValidationErrorException.class,
            () -> validator.validateForDownloadFile(aircraftId, fileId));
    assertEquals(msg, ex.getMessage());
  }

  /**
   * メソッド名: validateForDownloadPayloadFile<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に機体ID<br>
   * 第4引数に検証対象のdto<br>
   */
  static Stream<Arguments> nmlDlPayloadCase() {
    return Stream.of(Arguments.of("正常", "ed6002d-a68f-4e2d-a530-3cd281b5093e"));
  }

  /**
   * メソッド名: validateForDownloadPayloadFile<br>
   * 試験名: 正常系の様々なパターンでのバリデーション動作検証<br>
   * 条件: ペイロードIDの各パターン<br>
   * 結果: 例外が発生せず、正常に処理が完了する<br>
   * テストパターン: 正常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("nmlDlPayloadCase")
  public void testValidateForUpdate_nmlDlPayloadCase(String caseName, String payloadId) {
    assertDoesNotThrow(() -> validator.validateForDownloadPayloadFile(payloadId));
  }

  /**
   * メソッド名: validateForDownloadPayloadFile<br>
   * ParameterizedTest用の引数準備メソッド(正常系用)<br>
   * 以下テストケース毎に以下の引数を設定したストリーム配列を返却する<br>
   * 第1引数にテストケース名<br>
   * 第2引数に機体ID<br>
   * 第3引数に補足資料ID<br>
   * 第4引数に検証対象のdto<br>
   * 第5引数に期待するエラーメッセージ<br>
   */
  static Stream<Arguments> errDlPayloadCase() {
    return Stream.of(
        Arguments.of("ペイロードIDがUUIDではない", "notUUID", "[ペイロードIDがUUIDではありません。\n入力値:notUUID]"),
        Arguments.of("ペイロードIDがnull", null, "[ペイロードIDに値が設定されていません。]"),
        Arguments.of("ペイロードIDが空文字", "", "[ペイロードIDに値が設定されていません。]"));
  }

  /**
   * メソッド名: validateForDownloadPayloadFile<br>
   * 試験名: 異常系の様々なパターンでのバリデーションエラー動作検証<br>
   * 条件: ペイロードIDが欠落または不正な値<br>
   * 結果: ValidationErrorExceptionがスローされ、エラーメッセージが検証される<br>
   * テストパターン: 異常系<br>
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("errDlPayloadCase")
  public void testValidateForUpdate_errDlPayloadCase(
      String caseName, String payloadId, String msg) {
    Exception ex =
        assertThrows(
            ValidationErrorException.class,
            () -> validator.validateForDownloadPayloadFile(payloadId));
    assertEquals(msg, ex.getMessage());
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料1つ) */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto_hosoku1() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setModelNumber("M12345678");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setPublicFlag(true);
    ret.setImageData(
        "data:image/png;base64,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");
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);

    List<AircraftInfoFileInfoListElementReq> fileInfos =
        new ArrayList<AircraftInfoFileInfoListElementReq>();
    AircraftInfoFileInfoListElementReq file1 = new AircraftInfoFileInfoListElementReq();
    file1.setProcessingType(1);
    file1.setFileId(null);
    file1.setFileLogicalName("補足資料論理名補足資料論理名");
    file1.setFilePhysicalName("hosoku.txt");
    file1.setFileData("data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65");
    byte[] file1Byetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    file1.setFileBinary(file1Byetes);
    fileInfos.add(file1);
    ret.setFileInfos(fileInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料3つ) */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto_hosoku3() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setModelNumber("M12345678");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);

    List<AircraftInfoFileInfoListElementReq> fileInfos =
        new ArrayList<AircraftInfoFileInfoListElementReq>();

    AircraftInfoFileInfoListElementReq file1 = new AircraftInfoFileInfoListElementReq();
    file1.setProcessingType(1);
    file1.setFileId(null);
    file1.setFileLogicalName("1補足資料論理名補足資料論理名");
    file1.setFilePhysicalName("1hosoku.txt");
    file1.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file1Byetes = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    file1.setFileBinary(file1Byetes);
    fileInfos.add(file1);

    AircraftInfoFileInfoListElementReq file2 = new AircraftInfoFileInfoListElementReq();
    file2.setProcessingType(1);
    file2.setFileId(null);
    file2.setFileLogicalName("2補足資料論理名補足資料論理名");
    file2.setFilePhysicalName("2hosoku.txt");
    file2.setFileData("data:text/plain;base64,MuijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file2Byetes = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    file2.setFileBinary(file2Byetes);
    fileInfos.add(file2);

    AircraftInfoFileInfoListElementReq file3 = new AircraftInfoFileInfoListElementReq();
    file3.setProcessingType(1);
    file3.setFileId(null);
    file3.setFileLogicalName("3補足資料論理名補足資料論理名");
    file3.setFilePhysicalName("3hosoku.txt");
    file3.setFileData("data:text/plain;base64,M+ijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file3Byetes = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    file3.setFileBinary(file3Byetes);
    fileInfos.add(file3);

    ret.setFileInfos(fileInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料空配列) */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto_empList() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setModelNumber("M12345678");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);
    List<AircraftInfoFileInfoListElementReq> fileInfos =
        new ArrayList<AircraftInfoFileInfoListElementReq>();
    ret.setFileInfos(fileInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料項目なし) */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto_hosokuNull() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setModelNumber("M12345678");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);
    ret.setFileInfos(null);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(ペイロード情報1つ) */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto_payload1() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    ret.setImageBinary(null);

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos =
        new ArrayList<AircraftInfoPayloadInfoListElementReq>();
    AircraftInfoPayloadInfoListElementReq payload1 = new AircraftInfoPayloadInfoListElementReq();
    payload1.setProcessingType(1);
    payload1.setPayloadId(null);
    payload1.setPayloadName("テストペイロード");
    payload1.setPayloadDetailText("テストのペイロード情報を記載");
    payload1.setImageData(
        "data:image/png;base64,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");
    byte[] payload1Bytes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71, -83
    };
    payload1.setImageBinary(payload1Bytes);
    payload1.setFilePhysicalName("payload_hosoku.txt");
    payload1.setFileData("data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65");
    byte[] file1Byetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71, -27
    };
    payload1.setFileBinary(file1Byetes);
    payloadInfos.add(payload1);
    ret.setPayloadInfos(payloadInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(ペイロード情報3つ) */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto_payload3() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    ret.setImageBinary(null);

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos =
        new ArrayList<AircraftInfoPayloadInfoListElementReq>();
    AircraftInfoPayloadInfoListElementReq payload1 = new AircraftInfoPayloadInfoListElementReq();
    payload1.setProcessingType(1);
    payload1.setPayloadId(null);
    payload1.setPayloadName("テストペイロード1");
    payload1.setPayloadDetailText("テストのペイロード情報を記載1");
    payload1.setImageData(
        "data:image/png;base64,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");
    byte[] payload1Bytes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    payload1.setImageBinary(payload1Bytes);
    payload1.setFilePhysicalName("payload_hosoku1.txt");
    payload1.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file1Byetes = {
      49, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96, -79,
      -29, -127, -82, -27, -122, -123, -27, -82, -71, -27
    };
    payload1.setFileBinary(file1Byetes);
    payloadInfos.add(payload1);

    AircraftInfoPayloadInfoListElementReq payload2 = new AircraftInfoPayloadInfoListElementReq();
    payload2.setProcessingType(1);
    payload2.setPayloadId(null);
    payload2.setPayloadName("テストペイロード2");
    payload2.setPayloadDetailText("テストのペイロード情報を記載2");
    payload2.setImageData(
        "data:image/png;base64,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");
    byte[] payload2Bytes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    payload2.setImageBinary(payload2Bytes);
    payload2.setFilePhysicalName("payload_hosoku2.txt");
    payload2.setFileData("data:text/plain;base64,MuijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file2Byetes = {
      50, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96, -79,
      -29, -127, -82, -27, -122, -123, -27, -82, -71, -27
    };
    payload2.setFileBinary(file2Byetes);
    payloadInfos.add(payload2);

    AircraftInfoPayloadInfoListElementReq payload3 = new AircraftInfoPayloadInfoListElementReq();
    payload3.setProcessingType(1);
    payload3.setPayloadId(null);
    payload3.setPayloadName("テストペイロード3");
    payload3.setPayloadDetailText("テストのペイロード情報を記載3");
    payload3.setImageData(
        "data:image/png;base64,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");
    byte[] payload3Bytes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    payload3.setImageBinary(payload3Bytes);
    payload3.setFilePhysicalName("payload_hosoku3.txt");
    payload3.setFileData("data:text/plain;base64,M+ijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file3Byetes = {
      51, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96, -79,
      -29, -127, -82, -27, -122, -123, -27, -82, -71, -27
    };
    payload3.setFileBinary(file3Byetes);
    payloadInfos.add(payload3);

    ret.setPayloadInfos(payloadInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(ペイロード情報空配列) */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto_payloadEmpList() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    ret.setImageBinary(null);
    List<AircraftInfoPayloadInfoListElementReq> payloadInfos =
        new ArrayList<AircraftInfoPayloadInfoListElementReq>();
    ret.setPayloadInfos(payloadInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(ペイロード情報項目なし) */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto_payloadNull() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPUAAABuCAYAAAD/EFXlAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAcdSURBVHhe7d0hVOw6FwXgkUgkEolEIpFIJHIkEolDIpFIJBKJRCKRSCQSiexd+76b+84c0rntNE2Tnf2tlbX+978n5nSyJ2mSllUnIlRW/v8Qkbop1CJkFGoRMgq1CBmFWoSMQi1CRqEWIaNQi5BRqEXIKNQiZBRqETIKtQgZhVqEjEItQkahFiGjUIuQUailWc/Pz93p6Wn38PDg/1XVFGpp1sHBQbdarbq9vT3/r6qmUEuzEOjQmHBVIzKCQi1CRqEWIaNQi5BpPtRY/j88PNy4ELU0fG62bQuZzvYRJoOrqTXQobFtW8h0tn8wGVzN7e3tj6DU1C4vL31J0jjbP5hwVSMygkItQkahFiGjUIuQUahlJzVsBeLz3d/f+49Oz14DJlzVFKj0QNt2c3PjPz41WzsTrmoKVONWYCuHdWzNTLiqkdG+v7+7s7OzH8Fu4bCOQi20EOz1ev0j2OxYa+WqRiZj7egxrLVyVSOTsXb0GNZauaqRyVg7egxrrVzVyGSsHT2GtVauamQS7FOzdvQY1lqTVVPKySl8houLi98npN7f3/3HlB4+0LiG7Gy9TJJVU0KgpzbUcH19/XuLpyU+0Ni3buEa2JqZJKumxpNTfe3o6Kh7fX31JVJqNdBg62bCVU3XdW9vb93d3V13fn7e7e/v/wjs2MZ8ZLLlQIOtnQlXNRMhvLEfAsYjk60HGhTqRnx+fv44C832fjPMZFoPNCjUQuHp6UmB/kOhrgDrnyZNBesNuJUIHRnXqtVAg0JdAdY/TZrCx8fHxrYj/vfX15f/z5qiUFeA9UuaCqPx8fHx32uDxUAdzOHtLzTVoOOyfklTYXsvXBfMYl5eXvx/0iTW/kJTjV3Rxagk/7m6utrovC2+YNAKx5n9CUgmFNVglA730+q4//NbVwh463yYQwvPDGCRFduaNaMI9ePj498vB+FueUU38CM0puAy/DgzZnt4DqDG48IUobaLQBidWoYfNHsPHTqofug2+f36bQ0LizX1q+pDbb8cLAK13HkxbbQ/cGg4XNL61lUfe50wImNk9tfPtlrei159qHGAIlz0lu8ZcbDE3y+yHW9NzV4rCz+OuLfGPbZ/FqCGYFcdanTkcLExSte+wLErrOj6zlfTdHEpfaG2MPPzzwKUHuz+aipg7x1bHZUwotgOhx833JLIvw0JNdQW7O3VFAwnouxFxqjdGh9orPy3eB12NTTUUFOw/11NoeyWTYvbNT7QWODB+W4ZbkyoIRZszBBLu+0bVk1hcBHt00atjU6xQGuFe7yxoYZYsNEXMciUEu7h1RTEjtKtHQlVoNPZJdQQOwsQwl3CiyvHVVMIu3XT0qKQAp3WrqEO0Pdi+9pLv7hyt2oWhClOuHjYxmmFP96oQE83NdRBX7iXmpJPq2YB9gQZDp6ww2OS+OUfE+gxf1ih1Xedg70OKfS9uDJc51wPGqWpJiP7FkzmE2T4hceJJt85Tk5OtgYahgbatqWnjEuw9acSe3GlbTm2wdJVk4ldoMDTWYxwGsz/4uOfMQUfwk/Vx7SlpoxLsHWnhhmWPcJs29DvcVfpq5mR38pifCWPfx83GkbsuYIWmzKWtkUzF1vznPw22Nzv0Ju3msTYt7Jwb2s7GqbEOV491DdlZA93rlCDf93WnH/5Zf5qEvGjNNNWFmYcuFe2X/oS7+PumzKyhjtnqMH233Bd55CnmgRYR2ncX/kve4lAW9vCPff9YE65Q+3XOuZ6CClPNROhk7GN0rHRubTQ9O2/5ljBzSF3qHMpvho8pGBfKogg1AwjcGx0Rl2lLvzFwo2Z05KziRQU6gWg09jRDOGu+b4udpCktNG5j1/BRat9b1uhXsB6vd7o/LV2oG0HSUodnWP6HmSo9USaQp2Zf2d1riN2KeGRUExT/T7wmIMkJYrtbeMHqrbnuRXqjPzTSHOtEs4hBLnvqOacB0lyiu1tI+g1LWIq1JnYF/OjLb29MxRG3r4go+U6SJKbn1Gh4baphtsKhToTu9JdQ6BjW1OhoRbMMhjDbGGtw35voeG6YNZV6neoUM8s9rhgqZ0B947orBiR/NZUK0H28ORY7MCKbXMejdyFQj0zH+iSLrQNcexzotWyNTU3jNqxHzt7nUpRYl9LoZhqch2hGyI2a9jWatuaygEjN3Ys/L78kt+rZz8XE65qEondH9qG0Qb3+/ghqnXvXBTqpijEbVCoG2LvB4WXQt2QcH9f0v2fpKdQi5BRqEXIKNQiZOzaCdNhIYVammUfh8W5hFJPMI6lUEuz8KSZfYSUZWFUoZam2cd8SzrCOoVCLc1jWzDjqEJkAoVahIxCLULGbm0xvNNcoZbm2b/+glb7c/EKtTTPv9O89lVwhVrkT7DDYRS8uaVmCrUIGYVahIxCLUJGoRYho1CLkFGoRcgo1CJkFGoRMgq1CJlfMf/5w1FFkZsAAAAASUVORK5CYII=");
    ret.setPublicFlag(true);
    ret.setImageBinary(null);
    ret.setPayloadInfos(null);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf21");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setModelNumber("MD12345V1");
    ret.setModelName("更新後機種名");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);

    return ret;
  }

  /** データテンプレート ■モデル検索リクエスト モデル検索リクエストボディ */
  private static AircraftInfoModelSearchRequestDto createAircraftInfoModelSearchRequestDto() {
    AircraftInfoModelSearchRequestDto dto = new AircraftInfoModelSearchRequestDto();

    List<AircraftInfoModelInfoListElementReq> list = new ArrayList<>();
    AircraftInfoModelInfoListElementReq ele1 = new AircraftInfoModelInfoListElementReq();
    ele1.setManufacturer("製造メーカー1");
    ele1.setModelNumber("MD12345V1");
    list.add(ele1);
    AircraftInfoModelInfoListElementReq ele2 = new AircraftInfoModelInfoListElementReq();
    ele2.setManufacturer("製造メーカー1");
    ele2.setModelNumber("MD12345V2");
    list.add(ele2);
    AircraftInfoModelInfoListElementReq ele3 = new AircraftInfoModelInfoListElementReq();
    ele3.setManufacturer("製造メーカー2");
    ele3.setModelNumber("MD12345V1");
    list.add(ele3);

    dto.setModelInfos(list);
    dto.setIsRequiredPayloadInfo("true");
    dto.setIsRequiredPriceInfo("true");
    return dto;
  }
}
