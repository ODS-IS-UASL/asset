package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** DronePortReserveInfoListElementのテストクラス<br> */
class DronePortReserveInfoListElementTest {

  /**
   * メソッド名: getDronePortReservationId_setDronePortReservationId<br>
   * 試験名: dronePortReservationIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDronePortReservationId_setDronePortReservationId() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "reservation123";
    element.setDronePortReservationId(expected);
    assertEquals(expected, element.getDronePortReservationId());
  }

  /**
   * メソッド名: getDronePortId_setDronePortId<br>
   * 試験名: dronePortIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDronePortId_setDronePortId() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "port123";
    element.setDronePortId(expected);
    assertEquals(expected, element.getDronePortId());
  }

  /**
   * メソッド名: getAircraftId_setAircraftId<br>
   * 試験名: aircraftIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftId_setAircraftId() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "aircraft123";
    element.setAircraftId(expected);
    assertEquals(expected, element.getAircraftId());
  }

  /**
   * メソッド名: getRouteReservationId_setRouteReservationId<br>
   * 試験名: routeReservationIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getRouteReservationId_setRouteReservationId() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "route123";
    element.setRouteReservationId(expected);
    assertEquals(expected, element.getRouteReservationId());
  }

  /**
   * メソッド名: getUsageType_setUsageType<br>
   * 試験名: usageTypeの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getUsageType_setUsageType() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    Integer expected = 1;
    element.setUsageType(expected);
    assertEquals(expected, element.getUsageType());
  }

  /**
   * メソッド名: getReservationTimeFrom_setReservationTimeFrom<br>
   * 試験名: reservationTimeFromの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getReservationTimeFrom_setReservationTimeFrom() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "2023-01-01 10:00:00";
    element.setReservationTimeFrom(expected);
    assertEquals(expected, element.getReservationTimeFrom());
  }

  /**
   * メソッド名: getReservationTimeTo_setReservationTimeTo<br>
   * 試験名: reservationTimeToの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getReservationTimeTo_setReservationTimeTo() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "2023-01-01 12:00:00";
    element.setReservationTimeTo(expected);
    assertEquals(expected, element.getReservationTimeTo());
  }

  /**
   * メソッド名: getDronePortName_setDronePortName<br>
   * 試験名: DronePortNameの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDronePortName_setDronePortName() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "Port A";
    element.setDronePortName(expected);
    assertEquals(expected, element.getDronePortName());
  }

  /**
   * メソッド名: getAircraftName_setAircraftName<br>
   * 試験名: AircraftNameの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftName_setAircraftName() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "Aircraft A";
    element.setAircraftName(expected);
    assertEquals(expected, element.getAircraftName());
  }

  /**
   * メソッド名: getReservationActiveFlag_setReservationActiveFlag<br>
   * 試験名: reservationActiveFlagの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getReservationActiveFlag_setReservationActiveFlag() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    Boolean expected = true;
    element.setReservationActiveFlag(expected);
    assertEquals(expected, element.getReservationActiveFlag());
  }

  /**
   * メソッド名: getInactiveTimeFrom_setInactiveTimeFrom<br>
   * 試験名: inactiveTimeFromの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getInactiveTimeFrom_setInactiveTimeFrom() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "2023-01-01 08:00:00";
    element.setInactiveTimeFrom(expected);
    assertEquals(expected, element.getInactiveTimeFrom());
  }

  /**
   * メソッド名: getInactiveTimeTo_setInactiveTimeTo<br>
   * 試験名: inactiveTimeToの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getInactiveTimeTo_setInactiveTimeTo() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "2023-01-01 09:00:00";
    element.setInactiveTimeTo(expected);
    assertEquals(expected, element.getInactiveTimeTo());
  }

  /**
   * メソッド名: getVisDronePortCompanyId_setVisDronePortCompanyId<br>
   * 試験名: VisDronePortCompanyIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getVisDronePortCompanyId_setVisDronePortCompanyId() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "company123";
    element.setVisDronePortCompanyId(expected);
    assertEquals(expected, element.getVisDronePortCompanyId());
  }

  /**
   * メソッド名: getOperatorId_setOperatorId<br>
   * 試験名: OperatorIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getOperatorId_setOperatorId() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    String expected = "operator";
    element.setOperatorId(expected);
    assertEquals(expected, element.getOperatorId());
  }

  /**
   * メソッド名: toString<br>
   * 試験名: toStringメソッドが正しく動作することを確認する<br>
   * 条件: 全ての項目に値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 期待される文字列が返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void toStringTest() {
    DronePortReserveInfoListElement element = new DronePortReserveInfoListElement();
    element.setDronePortReservationId("reservation123");
    element.setDronePortId("port123");
    element.setGroupReservationId("groupReservationId");
    element.setAircraftId("aircraft123");
    element.setRouteReservationId("route123");
    element.setUsageType(1);
    element.setReservationTimeFrom("2023-01-01 10:00:00");
    element.setReservationTimeTo("2023-01-01 12:00:00");
    element.setDronePortName("dummyDronePort");
    element.setAircraftName("dummyAircraft");
    element.setVisDronePortCompanyId("dummyVisDronePortCompanyId");
    element.setReservationActiveFlag(true);
    element.setInactiveTimeFrom("2023-01-01 08:00:00");
    element.setInactiveTimeTo("2023-01-01 09:00:00");
    element.setReserveProviderId("reserveProviderId");
    element.setOperatorId("operator123");

    String expected =
        "DronePortReserveInfoListElement(dronePortReservationId=reservation123, groupReservationId=groupReservationId, dronePortId=port123, aircraftId=aircraft123, routeReservationId=route123, usageType=1, reservationTimeFrom=2023-01-01 10:00:00, reservationTimeTo=2023-01-01 12:00:00, dronePortName=dummyDronePort, aircraftName=dummyAircraft, visDronePortCompanyId=dummyVisDronePortCompanyId, reservationActiveFlag=true, inactiveTimeFrom=2023-01-01 08:00:00, inactiveTimeTo=2023-01-01 09:00:00, reserveProviderId=reserveProviderId, operatorId=operator123)";
    assertEquals(expected, element.toString());
  }
}
