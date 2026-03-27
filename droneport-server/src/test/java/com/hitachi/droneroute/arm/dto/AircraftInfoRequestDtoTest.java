package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** AircraftInfoRequestDtoのテストクラス. */
public class AircraftInfoRequestDtoTest {

  /**
   * メソッド名: getAircraftId<br>
   * 試験名: AircraftIdのgetterが正しく動作することを確認する<br>
   * 条件: aircraftIdに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftId() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftId("A123");
    assertEquals("A123", dto.getAircraftId());
  }

  /**
   * メソッド名: getAircraftName<br>
   * 試験名: AircraftNameのgetterが正しく動作することを確認する<br>
   * 条件: aircraftNameに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftName() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftName("DroneX");
    assertEquals("DroneX", dto.getAircraftName());
  }

  /**
   * メソッド名: getManufacturer<br>
   * 試験名: Manufacturerのgetterが正しく動作することを確認する<br>
   * 条件: manufacturerに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getManufacturer() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setManufacturer("Hitachi");
    assertEquals("Hitachi", dto.getManufacturer());
  }

  /**
   * メソッド名: getManufacturingNumber<br>
   * 試験名: ManufacturingNumberのgetterが正しく動作することを確認する<br>
   * 条件: manufacturingNumberに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getManufacturingNumber() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setManufacturingNumber("MN12345");
    assertEquals("MN12345", dto.getManufacturingNumber());
  }

  /**
   * メソッド名: getAircraftType<br>
   * 試験名: AircraftTypeのgetterが正しく動作することを確認する<br>
   * 条件: aircraftTypeに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftType() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftType(1);
    assertEquals(1, dto.getAircraftType());
  }

  /**
   * メソッド名: getMaxTakeoffWeight<br>
   * 試験名: MaxTakeoffWeightのgetterが正しく動作することを確認する<br>
   * 条件: maxTakeoffWeightに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getMaxTakeoffWeight() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setMaxTakeoffWeight(100.5);
    assertEquals(100.5, dto.getMaxTakeoffWeight());
  }

  /**
   * メソッド名: getBodyWeight<br>
   * 試験名: BodyWeightのgetterが正しく動作することを確認する<br>
   * 条件: bodyWeightに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getBodyWeight() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setBodyWeight(50.0);
    assertEquals(50.0, dto.getBodyWeight());
  }

  /**
   * メソッド名: getMaxFlightSpeed<br>
   * 試験名: MaxFlightSpeedのgetterが正しく動作することを確認する<br>
   * 条件: maxFlightSpeedに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getMaxFlightSpeed() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setMaxFlightSpeed(200.0);
    assertEquals(200.0, dto.getMaxFlightSpeed());
  }

  /**
   * メソッド名: getMaxFlightTime<br>
   * 試験名: MaxFlightTimeのgetterが正しく動作することを確認する<br>
   * 条件: maxFlightTimeに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getMaxFlightTime() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setMaxFlightTime(2.5);
    assertEquals(2.5, dto.getMaxFlightTime());
  }

  /**
   * メソッド名: getCertification<br>
   * 試験名: Certificationのgetterが正しく動作することを確認する<br>
   * 条件: certificationに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getCertification() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setCertification(true);
    assertTrue(dto.getCertification());
  }

  /**
   * メソッド名: getDipsRegistrationCode<br>
   * 試験名: DipsRegistrationCodeのgetterが正しく動作することを確認する<br>
   * 条件: dipsRegistrationCodeに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDipsRegistrationCode() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setDipsRegistrationCode("DIPS123");
    assertEquals("DIPS123", dto.getDipsRegistrationCode());
  }

  /**
   * メソッド名: getOwnerType<br>
   * 試験名: OwnerTypeのgetterが正しく動作することを確認する<br>
   * 条件: ownerTypeに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getOwnerType() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setOwnerType(2);
    assertEquals(2, dto.getOwnerType());
  }

  /**
   * メソッド名: getOwnerId<br>
   * 試験名: OwnerIdのgetterが正しく動作することを確認する<br>
   * 条件: ownerIdに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getOwnerId() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setOwnerId("Owner123");
    assertEquals("Owner123", dto.getOwnerId());
  }

  /**
   * メソッド名: getImageBinary<br>
   * 試験名: ImageBinaryのgetterが正しく動作することを確認する<br>
   * 条件: imageBinaryに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getImageBinary() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    byte[] image = {1, 2, 3};
    dto.setImageBinary(image);
    assertArrayEquals(image, dto.getImageBinary());
  }

  /**
   * メソッド名: getImageData<br>
   * 試験名: ImageDataのgetterが正しく動作することを確認する<br>
   * 条件: imageDataに値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getImageData() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setImageData("base64ImageData");
    assertEquals("base64ImageData", dto.getImageData());
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
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();

    dto.setAircraftId("aircraftId");
    dto.setAircraftName("aircraftName");
    dto.setAircraftType(1);
    dto.setBodyWeight(10.0);
    dto.setCertification(true);
    dto.setDipsRegistrationCode("12345");
    dto.setManufacturer("manufacurer");
    dto.setManufacturingNumber("12345");
    dto.setMaxFlightSpeed(5.0);
    dto.setMaxFlightTime(3.0);
    dto.setLat(30.0);
    dto.setLon(150.0);
    dto.setMaxTakeoffWeight(10.0);
    dto.setOwnerId("ownerId");
    dto.setOwnerType(1);
    dto.setImageData("imageData");
    dto.setImageBinary(new byte[] {1, 2, 3, 4});

    String expected =
        "AircraftInfoRequestDto(aircraftId=aircraftId, aircraftName=aircraftName, manufacturer=manufacurer, modelNumber=null, modelName=null, manufacturingNumber=12345, aircraftType=1, maxTakeoffWeight=10.0, bodyWeight=10.0, maxFlightSpeed=5.0, maxFlightTime=3.0, lat=30.0, lon=150.0, certification=true, dipsRegistrationCode=12345, ownerType=1, ownerId=ownerId, publicFlag=null, fileInfos=null, payloadInfos=null, priceInfos=null)";
    assertEquals(expected, dto.toString());
  }
}
