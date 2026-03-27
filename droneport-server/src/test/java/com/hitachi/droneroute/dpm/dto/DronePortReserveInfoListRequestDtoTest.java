package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * クラス名: DronePortReserveInfoListRequestDtoTest<br>
 * 試験名: DronePortReserveInfoListRequestDtoクラスのテスト<br>
 */
class DronePortReserveInfoListRequestDtoTest {

  /**
   * メソッド名: getAircraftId_setAircraftId<br>
   * 試験名: aircraftIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getAircraftId_setAircraftId() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "aircraft123";
    dto.setAircraftId(expected);
    assertEquals(expected, dto.getAircraftId());
  }

  /**
   * メソッド名: getReservationRouteId_setReservationRouteId<br>
   * 試験名: routeReservationIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getReservationRouteId_setReservationRouteId() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "route456";
    dto.setRouteReservationId(expected);
    assertEquals(expected, dto.getRouteReservationId());
  }

  /**
   * メソッド名: getTimeFrom_setTimeFrom<br>
   * 試験名: timeFromの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getTimeFrom_setTimeFrom() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "2023-01-01T00:00:00";
    dto.setTimeFrom(expected);
    assertEquals(expected, dto.getTimeFrom());
  }

  /**
   * メソッド名: getTimeTo_setTimeTo<br>
   * 試験名: timeToの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getTimeTo_setTimeTo() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "2023-01-01T23:59:59";
    dto.setTimeTo(expected);
    assertEquals(expected, dto.getTimeTo());
  }

  /**
   * メソッド名: getDronePortId_setDronePortId<br>
   * 試験名: dronePortIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getDronePortId_setDronePortId() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "dronePort";
    dto.setDronePortId(expected);
    assertEquals(expected, dto.getDronePortId());
  }

  /**
   * メソッド名: getDronePortName_setDronePortName<br>
   * 試験名: dronePortNameの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getDronePortName_setDronePortName() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "dronePortName";
    dto.setDronePortName(expected);
    assertEquals(expected, dto.getDronePortName());
  }

  /**
   * メソッド名: getPerPage_setPerPage<br>
   * 試験名: getPerPageとsetPerPageの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getPerPage_setPerPage() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "10";
    dto.setPerPage(expected);
    assertEquals(expected, dto.getPerPage());
  }

  /**
   * メソッド名: getPage_setPage<br>
   * 試験名: getPageとsetPageの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getPage_setPage() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "1";
    dto.setPage(expected);
    assertEquals(expected, dto.getPage());
  }

  /**
   * メソッド名: getSortOrders_setSortOrders<br>
   * 試験名: getSortOrdersとsetSortOrdersの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getSortOrders_setSortOrders() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "asc";
    dto.setSortOrders(expected);
    assertEquals(expected, dto.getSortOrders());
  }

  /**
   * メソッド名: getSortColumns_setSortColumns<br>
   * 試験名: getSortColumnsとsetSortColumnsの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getSortColumns_setSortColumns() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    String expected = "dronePortName";
    dto.setSortColumns(expected);
    assertEquals(expected, dto.getSortColumns());
  }

  /**
   * メソッド名: toStringTest<br>
   * 試験名: toStringメソッドが正しく動作することを確認する<br>
   * 条件: 全てのフィールドに値を設定し、toStringメソッドの出力を確認する<br>
   * 結果: toStringメソッドの出力が期待される形式であること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void toStringTest() {
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    dto.setAircraftId("aircraft123");
    dto.setRouteReservationId("route456");
    dto.setTimeFrom("2023-01-01T00:00:00");
    dto.setTimeTo("2023-01-01T23:59:59");
    dto.setGroupReservationId("groupReservationId");
    dto.setDronePortId("dummyPortId");
    dto.setDronePortName("dummyDronePortName");
    dto.setPerPage("10");
    dto.setPage("1");
    dto.setSortOrders("asc");
    dto.setSortColumns("dronePortName");
    dto.setReserveProviderId("reserveProviderId");
    // dto.setOperatorId("operator123");

    String expected =
        "DronePortReserveInfoListRequestDto(groupReservationId=groupReservationId, dronePortId=dummyPortId, dronePortName=dummyDronePortName, aircraftId=aircraft123, routeReservationId=route456, timeFrom=2023-01-01T00:00:00, timeTo=2023-01-01T23:59:59, perPage=10, page=1, sortOrders=asc, sortColumns=dronePortName, reserveProviderId=reserveProviderId)";
    assertEquals(expected, dto.toString());
  }
}
