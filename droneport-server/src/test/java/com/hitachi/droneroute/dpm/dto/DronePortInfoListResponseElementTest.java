package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** DronePortInfoListResponseElementクラスの単体テスト */
public class DronePortInfoListResponseElementTest {

  /**
   * メソッド名: getDronePortId, setDronePortId<br>
   * 試験名: 離着陸場IDの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortId() {
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "DP001";
    element.setDronePortId(expected);
    assertEquals(expected, element.getDronePortId());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "Port A";
    element.setDronePortName(expected);
    assertEquals(expected, element.getDronePortName());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "123 Main St";
    element.setAddress(expected);
    assertEquals(expected, element.getAddress());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "Hitachi";
    element.setManufacturer(expected);
    assertEquals(expected, element.getManufacturer());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "SN123456";
    element.setSerialNumber(expected);
    assertEquals(expected, element.getSerialNumber());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    Integer expected = 1;
    element.setPortType(expected);
    assertEquals(expected, element.getPortType());
  }

  /**
   * メソッド名: getVisDronePortCompanyId, setVisDronePortCompanyId<br>
   * 試験名: visDronePortCompanyIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetVisDronePortCompanyId() {
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "Company123";
    element.setVisDronePortCompanyId(expected);
    assertEquals(expected, element.getVisDronePortCompanyId());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "Aircraft123";
    element.setStoredAircraftId(expected);
    assertEquals(expected, element.getStoredAircraftId());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    Double expected = 35.6895;
    element.setLat(expected);
    assertEquals(expected, element.getLat());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    Double expected = 139.6917;
    element.setLon(expected);
    assertEquals(expected, element.getLon());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    Double expected = 100.1;
    element.setAlt(expected);
    assertEquals(expected, element.getAlt());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "Type A";
    element.setSupportDroneType(expected);
    assertEquals(expected, element.getSupportDroneType());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    Integer expected = 1;
    element.setActiveStatus(expected);
    assertEquals(expected, element.getActiveStatus());
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
   * メソッド名: getInactiveTimeFrom, setInactiveTimeFrom<br>
   * 試験名: 使用不可開始日時の設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetInactiveTimeFrom() {
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "2023-10-01T10:00:00";
    element.setInactiveTimeFrom(expected);
    assertEquals(expected, element.getInactiveTimeFrom());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "2023-10-01T12:00:00";
    element.setInactiveTimeTo(expected);
    assertEquals(expected, element.getInactiveTimeTo());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "2023-10-01T15:00:00";
    element.setUpdateTime(expected);
    assertEquals(expected, element.getUpdateTime());
  }

  /**
   * メソッド名: getOperatorId, setOperator<br>
   * 試験名: オペレータIDの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetOperatorId() {
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    String expected = "operator123";
    element.setOperatorId(expected);
    assertEquals(expected, element.getOperatorId());
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
    DronePortInfoListResponseElement element = new DronePortInfoListResponseElement();
    element.setDronePortId("DP001");
    element.setDronePortName("Port A");
    element.setAddress("123 Main St");
    element.setManufacturer("Hitachi");
    element.setSerialNumber("SN123456");
    element.setPortType(1);
    element.setVisDronePortCompanyId("Company123");
    element.setStoredAircraftId("aircraft456");
    element.setLat(35.6895);
    element.setLon(139.6917);
    element.setAlt(100.1);
    element.setSupportDroneType("Type A");
    element.setActiveStatus(99);
    element.setScheduledStatus(98);
    element.setInactiveTimeFrom("2023-10-01T10:00:00");
    element.setInactiveTimeTo("2023-10-01T12:00:00");
    element.setOperatorId("operator123");
    element.setUpdateTime("2023-10-01T15:00:00");

    String expected =
        "DronePortInfoListResponseElement(dronePortId=DP001, dronePortName=Port A, address=123 Main St, manufacturer=Hitachi, serialNumber=SN123456, portType=1, visDronePortCompanyId=Company123, storedAircraftId=aircraft456, lat=35.6895, lon=139.6917, alt=100.1, supportDroneType=Type A, activeStatus=99, scheduledStatus=98, inactiveTimeFrom=2023-10-01T10:00:00, inactiveTimeTo=2023-10-01T12:00:00, operatorId=operator123, publicFlag=null, updateTime=2023-10-01T15:00:00, priceInfos=null)";
    assertEquals(expected, element.toString());
  }
}
