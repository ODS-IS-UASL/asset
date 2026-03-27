package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** DronePortInfoRegisterRequestDtoクラスの単体テスト */
class DronePortInfoRegisterRequestDtoTest {

  /**
   * メソッド名: getDronePortId、setDronePortId<br>
   * 試験名: DronePortIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortId() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "DP001";
    dto.setDronePortId(expected);
    assertEquals(expected, dto.getDronePortId());
  }

  /**
   * メソッド名: getDronePortName、setDronePortName<br>
   * 試験名: DronePortNameの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortName() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "Port A";
    dto.setDronePortName(expected);
    assertEquals(expected, dto.getDronePortName());
  }

  /**
   * メソッド名: getAddress、setAddress<br>
   * 試験名: Addressの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetAddress() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "123 Main St";
    dto.setAddress(expected);
    assertEquals(expected, dto.getAddress());
  }

  /**
   * メソッド名: getManufacturer、setManufacturer<br>
   * 試験名: Manufacturerの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetManufacturer() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "Hitachi";
    dto.setManufacturer(expected);
    assertEquals(expected, dto.getManufacturer());
  }

  /**
   * メソッド名: getSerialNumber、setSerialNumber<br>
   * 試験名: SerialNumberの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetSerialNumber() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "SN123456";
    dto.setSerialNumber(expected);
    assertEquals(expected, dto.getSerialNumber());
  }

  /**
   * メソッド名: getPortType、setPortType<br>
   * 試験名: PortTypeの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetPortType() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    Integer expected = 2;
    dto.setPortType(expected);
    assertEquals(expected, dto.getPortType());
  }

  /**
   * メソッド名: getDronePortManufacturerId, setDronePortManufacturerId<br>
   * 試験名: 格納中機体IDの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortManufacturerId() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "manufacturer123";
    dto.setDronePortManufacturerId(expected);
    assertEquals(expected, dto.getDronePortManufacturerId());
  }

  /**
   * メソッド名: getLat、setLat<br>
   * 試験名: Latの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetLat() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    Double expected = 35.6895;
    dto.setLat(expected);
    assertEquals(expected, dto.getLat());
  }

  /**
   * メソッド名: getLon、setLon<br>
   * 試験名: Lonの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetLon() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    Double expected = 139.6917;
    dto.setLon(expected);
    assertEquals(expected, dto.getLon());
  }

  /**
   * メソッド名: getAlt、setAlt<br>
   * 試験名: Altの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetAlt() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    Double expected = 100.1d;
    dto.setAlt(expected);
    assertEquals(expected, dto.getAlt());
  }

  /**
   * メソッド名: getSupportDroneType、setSupportDroneType<br>
   * 試験名: SupportDroneTypeの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetSupportDroneType() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "Type A";
    dto.setSupportDroneType(expected);
    assertEquals(expected, dto.getSupportDroneType());
  }

  /**
   * <br>
   * メソッド名: getImageData、setImageData<br>
   * 試験名: ImageDataの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetImageData() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "base64ImageData";
    dto.setImageData(expected);
    assertEquals(expected, dto.getImageData());
  }

  /**
   * メソッド名: getImageBinary、setImageBinary<br>
   * 試験名: ImageBinaryの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetImageBinary() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    byte[] expected = {1, 2, 3, 4, 5};
    dto.setImageBinary(expected);
    assertArrayEquals(expected, dto.getImageBinary());
  }

  /**
   * メソッド名: getVisDronePortCompanyId, setVisDronePortCompanyId<br>
   * 試験名: VIS離着陸場事業者IDの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetVisDronePortCompanyId() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "DP001";
    dto.setVisDronePortCompanyId(expected);
    assertEquals(expected, dto.getVisDronePortCompanyId());
  }

  /**
   * メソッド名: getStoredAircraftId, setStoredAircraftId<br>
   * 試験名: 格納中機体IDの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetStoredAircraftId() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "Aircraft123";
    dto.setStoredAircraftId(expected);
    assertEquals(expected, dto.getStoredAircraftId());
  }

  /**
   * メソッド名: getActiveStatus、setActiveStatus<br>
   * 試験名: ActiveStatusの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetActiveStatus() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    Integer expected = 1;
    dto.setActiveStatus(expected);
    assertEquals(expected, dto.getActiveStatus());
  }

  /**
   * メソッド名: getInactiveTimeFrom、setInactiveTimeFrom<br>
   * 試験名: InactiveTimeFromの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetInactiveTimeFrom() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "2023-01-01T00:00:00";
    dto.setInactiveTimeFrom(expected);
    assertEquals(expected, dto.getInactiveTimeFrom());
  }

  /**
   * メソッド名: getInactiveTimeTo、setInactiveTimeTo<br>
   * 試験名: InactiveTimeToの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetInactiveTimeTo() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    String expected = "2023-01-02T00:00:00";
    dto.setInactiveTimeTo(expected);
    assertEquals(expected, dto.getInactiveTimeTo());
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
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId("DP001");
    dto.setDronePortName("Port A");
    dto.setAddress("123 Main St");
    dto.setManufacturer("Hitachi");
    dto.setSerialNumber("SN123456");
    dto.setDronePortManufacturerId("manufacturer123");
    dto.setPortType(1);
    dto.setLat(35.6895);
    dto.setLon(139.6917);
    dto.setAlt(100.1d);
    dto.setSupportDroneType("Type A");
    dto.setActiveStatus(1);
    dto.setInactiveTimeFrom("2023-01-01T00:00:00");
    dto.setInactiveTimeTo("2023-01-02T00:00:00");
    dto.setImageData("imageData");
    dto.setImageBinary(new byte[] {1, 2, 3, 4});
    dto.setVisDronePortCompanyId("dummyVisDronePortCompanyId");
    dto.setStoredAircraftId("aircraft123");
    dto.setPublicFlag(true);

    String expected =
        "DronePortInfoRegisterRequestDto(dronePortId=DP001, dronePortName=Port A, address=123 Main St, manufacturer=Hitachi, serialNumber=SN123456, dronePortManufacturerId=manufacturer123, portType=1, visDronePortCompanyId=dummyVisDronePortCompanyId, storedAircraftId=aircraft123, lat=35.6895, lon=139.6917, alt=100.1, supportDroneType=Type A, activeStatus=1, inactiveTimeFrom=2023-01-01T00:00:00, inactiveTimeTo=2023-01-02T00:00:00, publicFlag=true, priceInfos=null)";
    assertEquals(expected, dto.toString());
  }
}
