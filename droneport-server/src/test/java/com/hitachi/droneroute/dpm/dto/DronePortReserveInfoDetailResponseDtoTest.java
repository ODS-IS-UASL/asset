package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** DronePortReserveInfoDetailResponseDtoのテストクラス<br> */
class DronePortReserveInfoDetailResponseDtoTest {

  /**
   * メソッド名: getDronePortReservationId_setDronePortReservationId<br>
   * 試験名: dronePortReservationIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDronePortReservationId_setDronePortReservationId() {
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "reservation123";
    dto.setDronePortReservationId(expected);
    assertEquals(expected, dto.getDronePortReservationId());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "port123";
    dto.setDronePortId(expected);
    assertEquals(expected, dto.getDronePortId());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "aircraft123";
    dto.setAircraftId(expected);
    assertEquals(expected, dto.getAircraftId());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "route123";
    dto.setRouteReservationId(expected);
    assertEquals(expected, dto.getRouteReservationId());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    Integer expected = 1;
    dto.setUsageType(expected);
    assertEquals(expected, dto.getUsageType());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "2023-01-01 10:00:00";
    dto.setReservationTimeFrom(expected);
    assertEquals(expected, dto.getReservationTimeFrom());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "2023-01-01 12:00:00";
    dto.setReservationTimeTo(expected);
    assertEquals(expected, dto.getReservationTimeTo());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "Port A";
    dto.setDronePortName(expected);
    assertEquals(expected, dto.getDronePortName());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "Aircraft A";
    dto.setAircraftName(expected);
    assertEquals(expected, dto.getAircraftName());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "company123";
    dto.setVisDronePortCompanyId(expected);
    assertEquals(expected, dto.getVisDronePortCompanyId());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    Boolean expected = true;
    dto.setReservationActiveFlag(expected);
    assertEquals(expected, dto.getReservationActiveFlag());
  }

  /**
   * メソッド名: getOperatorId_setOperatorId<br>
   * 試験名: operatorIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getOperatorId_setOperatorId() {
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    String expected = "operator123";
    dto.setOperatorId(expected);
    assertEquals(expected, dto.getOperatorId());
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
    DronePortReserveInfoDetailResponseDto dto = new DronePortReserveInfoDetailResponseDto();
    dto.setDronePortReservationId("reservation123");
    dto.setDronePortId("port123");
    dto.setGroupReservationId("groupReservationId");
    dto.setAircraftId("aircraft123");
    dto.setRouteReservationId("route123");
    dto.setUsageType(1);
    dto.setReservationTimeFrom("2023-01-01 10:00:00");
    dto.setReservationTimeTo("2023-01-01 12:00:00");
    dto.setAircraftName("dummyAircraft");
    dto.setDronePortName("dummyDronePort");
    dto.setVisDronePortCompanyId("dummyCompanyId");
    dto.setReservationActiveFlag(true);
    dto.setReserveProviderId("reserveProviderId");
    dto.setOperatorId("operator123");

    String expected =
        "DronePortReserveInfoDetailResponseDto(dronePortReservationId=reservation123, groupReservationId=groupReservationId, dronePortId=port123, aircraftId=aircraft123, routeReservationId=route123, usageType=1, reservationTimeFrom=2023-01-01 10:00:00, reservationTimeTo=2023-01-01 12:00:00, dronePortName=dummyDronePort, aircraftName=dummyAircraft, visDronePortCompanyId=dummyCompanyId, reservationActiveFlag=true, reserveProviderId=reserveProviderId, operatorId=operator123)";
    assertEquals(expected, dto.toString());
  }
}
