package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** DronePortInfoDetailResponseDtoクラスの単体テスト */
public class DronePortInfoDetailResponseDtoTest {

  /**
   * メソッド名: getDronePortId, setDronePortId<br>
   * 試験名: 離着陸場IDの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortId() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "DP001";
    dto.setDronePortId(expected);
    assertEquals(expected, dto.getDronePortId());
  }

  /**
   * メソッド名: getDronePortName, setDronePortName<br>
   * 試験名: 離着陸場名の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortName() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "Port A";
    dto.setDronePortName(expected);
    assertEquals(expected, dto.getDronePortName());
  }

  /**
   * メソッド名: getAddress, setAddress<br>
   * 試験名: 設置場所住所の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetAddress() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "123 Main St";
    dto.setAddress(expected);
    assertEquals(expected, dto.getAddress());
  }

  /**
   * メソッド名: getManufacturer, setManufacturer<br>
   * 試験名: 製造メーカーの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetManufacturer() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "Hitachi";
    dto.setManufacturer(expected);
    assertEquals(expected, dto.getManufacturer());
  }

  /**
   * メソッド名: getSerialNumber, setSerialNumber<br>
   * 試験名: 製造番号の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetSerialNumber() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "SN123456";
    dto.setSerialNumber(expected);
    assertEquals(expected, dto.getSerialNumber());
  }

  /**
   * メソッド名: getPortType, setPortType<br>
   * 試験名: ポート形状の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetPortType() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    Integer expected = 1;
    dto.setPortType(expected);
    assertEquals(expected, dto.getPortType());
  }

  /**
   * メソッド名: getLat, setLat<br>
   * 試験名: 緯度の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetLat() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    Double expected = 35.6895;
    dto.setLat(expected);
    assertEquals(expected, dto.getLat());
  }

  /**
   * メソッド名: getLon, setLon<br>
   * 試験名: 経度の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetLon() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    Double expected = 139.6917;
    dto.setLon(expected);
    assertEquals(expected, dto.getLon());
  }

  /**
   * メソッド名: getAlt, setAlt<br>
   * 試験名: 着陸面対地高度の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetAlt() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    Double expected = 100.1;
    dto.setAlt(expected);
    assertEquals(expected, dto.getAlt());
  }

  /**
   * メソッド名: getSupportDroneType, setSupportDroneType<br>
   * 試験名: 対応機体の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetSupportDroneType() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "Type A";
    dto.setSupportDroneType(expected);
    assertEquals(expected, dto.getSupportDroneType());
  }

  /**
   * メソッド名: getActiveStatus, setActiveStatus<br>
   * 試験名: 動作状況の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetActiveStatus() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    Integer expected = 1;
    dto.setActiveStatus(expected);
    assertEquals(expected, dto.getActiveStatus());
  }

  /**
   * メソッド名: getScheduledStatus, setScheduledStatus<br>
   * 試験名: 予定された動作状態の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetScheduledStatus() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    Integer expected = 1;
    dto.setScheduledStatus(expected);
    assertEquals(expected, dto.getScheduledStatus());
  }

  /**
   * メソッド名: getImageData, setImageData<br>
   * 試験名: 画像データの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetImageData() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "imageData";
    dto.setImageData(expected);
    assertEquals(expected, dto.getImageData());
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
    String expected = "DP001";
    dto.setStoredAircraftId(expected);
    assertEquals(expected, dto.getStoredAircraftId());
  }

  /**
   * メソッド名: getInactiveTimeFrom, setInactiveTimeFrom<br>
   * 試験名: 使用不可開始日時の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetInactiveTimeFrom() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "2023-10-01T10:00:00";
    dto.setInactiveTimeFrom(expected);
    assertEquals(expected, dto.getInactiveTimeFrom());
  }

  /**
   * メソッド名: getInactiveTimeTo, setInactiveTimeTo<br>
   * 試験名: 使用不可終了日時の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetInactiveTimeTo() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "2023-10-01T12:00:00";
    dto.setInactiveTimeTo(expected);
    assertEquals(expected, dto.getInactiveTimeTo());
  }

  /**
   * メソッド名: getOperatorId, setOperatorId<br>
   * 試験名: オペレータIDの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetOperatorId() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "operator123";
    dto.setOperatorId(expected);
    assertEquals(expected, dto.getOperatorId());
  }

  /**
   * メソッド名: getUpdateTime, setUpdateTime<br>
   * 試験名: 更新日時の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetUpdateTime() {
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    String expected = "2023-10-01T15:00:00";
    dto.setUpdateTime(expected);
    assertEquals(expected, dto.getUpdateTime());
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
    DronePortInfoDetailResponseDto dto = new DronePortInfoDetailResponseDto();
    dto.setDronePortId("DP001");
    dto.setDronePortName("Port A");
    dto.setAddress("123 Main St");
    dto.setManufacturer("Hitachi");
    dto.setSerialNumber("SN123456");
    dto.setPortType(1);
    dto.setLat(35.6895);
    dto.setLon(139.6917);
    dto.setAlt(100.1);
    dto.setSupportDroneType("Type A");
    dto.setActiveStatus(99);
    dto.setScheduledStatus(98);
    dto.setImageData("imageData");
    dto.setVisDronePortCompanyId("dummyVisDronePortCompanyId");
    dto.setStoredAircraftId("dummyStoredAircraftId");
    dto.setInactiveTimeFrom("2023-10-01T10:00:00");
    dto.setInactiveTimeTo("2023-10-01T12:00:00");
    dto.setUpdateTime("2023-10-01T15:00:00");
    dto.setOperatorId("operator123");

    String expected =
        "DronePortInfoDetailResponseDto(dronePortId=DP001, dronePortName=Port A, address=123 Main St, manufacturer=Hitachi, serialNumber=SN123456, portType=1, visDronePortCompanyId=dummyVisDronePortCompanyId, storedAircraftId=dummyStoredAircraftId, lat=35.6895, lon=139.6917, alt=100.1, supportDroneType=Type A, activeStatus=99, scheduledStatus=98, inactiveTimeFrom=2023-10-01T10:00:00, inactiveTimeTo=2023-10-01T12:00:00, operatorId=operator123, publicFlag=null, updateTime=2023-10-01T15:00:00, priceInfos=null)";
    assertEquals(expected, dto.toString());
  }
}
