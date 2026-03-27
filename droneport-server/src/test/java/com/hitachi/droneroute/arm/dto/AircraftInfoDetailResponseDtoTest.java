package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** AircraftInfoDetailResponseDtoのテストクラス. */
class AircraftInfoDetailResponseDtoTest {

  /**
   * メソッド名: getAndSetAircraftId<br>
   * 試験名: AircraftIdのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetAircraftId() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    String expected = "A123";
    dto.setAircraftId(expected);
    assertEquals(expected, dto.getAircraftId());
  }

  /**
   * メソッド名: getAndSetAircraftName<br>
   * 試験名: AircraftNameのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetAircraftName() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    String expected = "DroneX";
    dto.setAircraftName(expected);
    assertEquals(expected, dto.getAircraftName());
  }

  /**
   * メソッド名: getAndSetManufacturer<br>
   * 試験名: Manufacturerのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetManufacturer() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    String expected = "Hitachi";
    dto.setManufacturer(expected);
    assertEquals(expected, dto.getManufacturer());
  }

  /**
   * メソッド名: getAndSetManufacturingNumber<br>
   * 試験名: ManufacturingNumberのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetManufacturingNumber() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    String expected = "MN123456";
    dto.setManufacturingNumber(expected);
    assertEquals(expected, dto.getManufacturingNumber());
  }

  /**
   * メソッド名: getAndSetAircraftType<br>
   * 試験名: AircraftTypeのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetAircraftType() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    Integer expected = 1;
    dto.setAircraftType(expected);
    assertEquals(expected, dto.getAircraftType());
  }

  /**
   * メソッド名: getAndSetMaxTakeoffWeight<br>
   * 試験名: MaxTakeoffWeightのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetMaxTakeoffWeight() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    Double expected = 25.0;
    dto.setMaxTakeoffWeight(expected);
    assertEquals(expected, dto.getMaxTakeoffWeight());
  }

  /**
   * メソッド名: getAndSetBodyWeight<br>
   * 試験名: BodyWeightのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetBodyWeight() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    Double expected = 10.0;
    dto.setBodyWeight(expected);
    assertEquals(expected, dto.getBodyWeight());
  }

  /**
   * メソッド名: getAndSetMaxFlightSpeed<br>
   * 試験名: MaxFlightSpeedのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetMaxFlightSpeed() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    Double expected = 100.0;
    dto.setMaxFlightSpeed(expected);
    assertEquals(expected, dto.getMaxFlightSpeed());
  }

  /**
   * メソッド名: getAndSetMaxFlightTime<br>
   * 試験名: MaxFlightTimeのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetMaxFlightTime() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    Double expected = 2.0;
    dto.setMaxFlightTime(expected);
    assertEquals(expected, dto.getMaxFlightTime());
  }

  /**
   * メソッド名: getAndSetCertification<br>
   * 試験名: Certificationのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetCertification() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    Boolean expected = true;
    dto.setCertification(expected);
    assertEquals(expected, dto.getCertification());
  }

  /**
   * メソッド名: getAndSetDipsRegistrationCode<br>
   * 試験名: DipsRegistrationCodeのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetDipsRegistrationCode() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    String expected = "DIPS123";
    dto.setDipsRegistrationCode(expected);
    assertEquals(expected, dto.getDipsRegistrationCode());
  }

  /**
   * メソッド名: getAndSetOwnerType<br>
   * 試験名: OwnerTypeのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetOwnerType() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    Integer expected = 1;
    dto.setOwnerType(expected);
    assertEquals(expected, dto.getOwnerType());
  }

  /**
   * メソッド名: getAndSetOwnerId<br>
   * 試験名: OwnerIdのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetOwnerId() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    String expected = "Owner123";
    dto.setOwnerId(expected);
    assertEquals(expected, dto.getOwnerId());
  }

  /**
   * メソッド名: getAndSetOperatorId<br>
   * 試験名: OperatorIdのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetOperatorId() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    String expected = "Operator123";
    dto.setOperatorId(expected);
    assertEquals(expected, dto.getOperatorId());
  }

  /**
   * メソッド名: getAndSetImageData<br>
   * 試験名: ImageDataのgetterとsetterの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAndSetImageData() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    String expected = "imageData";
    dto.setImageData(expected);
    assertEquals(expected, dto.getImageData());
  }

  /**
   * メソッド名: toString<br>
   * 試験名: toStringメソッドが正しく動作することを確認する<br>
   * 条件: 全ての項目に値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 設定値が含まれる文字列が返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testToString() {
    AircraftInfoDetailResponseDto dto = new AircraftInfoDetailResponseDto();
    dto.setAircraftId("aircraftId");
    dto.setAircraftName("aircraftName");
    dto.setAircraftType(1);
    dto.setBodyWeight(10.0);
    dto.setCertification(true);
    dto.setDipsRegistrationCode("123456");
    dto.setManufacturer("manufacturer");
    dto.setManufacturingNumber("123456");
    dto.setMaxFlightSpeed(50.0);
    dto.setMaxFlightTime(3.0);
    dto.setLat(30.0);
    dto.setLon(130.0);
    dto.setMaxTakeoffWeight(10.0);
    dto.setOwnerId("ownerId");
    dto.setOwnerType(1);
    dto.setOperatorId("operator123");
    dto.setImageData("imageData");

    String expected =
        "AircraftInfoDetailResponseDto(aircraftId=aircraftId, aircraftName=aircraftName, manufacturer=manufacturer, modelNumber=null, modelName=null, manufacturingNumber=123456, aircraftType=1, maxTakeoffWeight=10.0, bodyWeight=10.0, maxFlightSpeed=50.0, maxFlightTime=3.0, certification=true, lat=30.0, lon=130.0, dipsRegistrationCode=123456, ownerType=1, ownerId=ownerId, operatorId=operator123, imageData=imageData, publicFlag=null, fileInfos=null, payloadInfos=null, priceInfos=null)";
    assertEquals(expected, dto.toString());
  }
}
