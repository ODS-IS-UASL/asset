package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * クラス名: DronePortReserveInfoRegisterRequestDtoTest<br>
 * 試験名: DronePortReserveInfoRegisterRequestDtoクラスのテスト<br>
 */
class DronePortReserveInfoUpdateRequestDtoTest {

  /**
   * メソッド名: getDronePortReservationId_setDronePortReservationId<br>
   * 試験名: dronePortReservationIdの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDronePortReservationId_setDronePortReservationId() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    String expected = "reservation123";
    dto.setDronePortReservationId(expected);
    assertEquals(expected, dto.getDronePortReservationId());
  }

  /**
   * メソッド名: getDronePortId_setDronePortId<br>
   * 試験名: dronePortIdの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDronePortId_setDronePortId() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    String expected = "port123";
    dto.setDronePortId(expected);
    assertEquals(expected, dto.getDronePortId());
  }

  /**
   * メソッド名: getAircraftId_setAircraftId<br>
   * 試験名: aircraftIdの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftId_setAircraftId() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    String expected = "aircraft123";
    dto.setAircraftId(expected);
    assertEquals(expected, dto.getAircraftId());
  }

  /**
   * メソッド名: getRouteId_setRouteId<br>
   * 試験名: routeIdの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getRouteId_setRouteId() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    String expected = "route123";
    dto.setRouteReservationId(expected);
    assertEquals(expected, dto.getRouteReservationId());
  }

  /**
   * メソッド名: getUsageType_setUsageType<br>
   * 試験名: usageTypeの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getUsageType_setUsageType() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    Integer expected = 1;
    dto.setUsageType(expected);
    assertEquals(expected, dto.getUsageType());
  }

  /**
   * メソッド名: getReservationTimeFrom_setReservationTimeFrom<br>
   * 試験名: reservationTimeFromの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getReservationTimeFrom_setReservationTimeFrom() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    String expected = "2023-10-01T10:00:00";
    dto.setReservationTimeFrom(expected);
    assertEquals(expected, dto.getReservationTimeFrom());
  }

  /**
   * メソッド名: getReservationTimeTo_setReservationTimeTo<br>
   * 試験名: reservationTimeToの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getReservationTimeTo_setReservationTimeTo() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    String expected = "2023-10-01T12:00:00";
    dto.setReservationTimeTo(expected);
    assertEquals(expected, dto.getReservationTimeTo());
  }

  /**
   * メソッド名: toStringTest<br>
   * 試験名: toStringメソッドの戻り値確認<br>
   * 条件: 全ての項目に値を設定すること<br>
   * 結果: toStringにより取得したオブジェクトの内容が設定値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void toStringTest() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortReservationId("reservation123");
    dto.setDronePortId("port123");
    dto.setAircraftId("aircraft123");
    dto.setRouteReservationId("route123");
    dto.setUsageType(1);
    dto.setReservationTimeFrom("2023-10-01T10:00:00");
    dto.setReservationTimeTo("2023-10-01T12:00:00");
    // dto.setOperatorId("operator123");

    String expected =
        "DronePortReserveInfoUpdateRequestDto(dronePortReservationId=reservation123, dronePortId=port123, aircraftId=aircraft123, routeReservationId=route123, usageType=1, reservationTimeFrom=2023-10-01T10:00:00, reservationTimeTo=2023-10-01T12:00:00)";
    assertEquals(expected, dto.toString());
  }

  /**
   * メソッド名: setDronePortReservationId_nullValue<br>
   * 試験名: dronePortReservationIdにnullを設定する<br>
   * 条件: setterにnullを設定すること<br>
   * 結果: nullが設定されること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void setDronePortReservationId_nullValue() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortReservationId(null);
    assertNull(dto.getDronePortReservationId());
  }

  /**
   * メソッド名: setDronePortId_emptyString<br>
   * 試験名: dronePortIdに空文字を設定する<br>
   * 条件: setterに空文字を設定すること<br>
   * 結果: 空文字が設定されること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void setDronePortId_emptyString() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortId("");
    assertEquals("", dto.getDronePortId());
  }

  /**
   * メソッド名: setAircraftId_unicode<br>
   * 試験名: aircraftIdにUnicode文字を設定する<br>
   * 条件: setterにUnicode文字を設定すること<br>
   * 結果: Unicode文字が設定されること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void setAircraftId_unicode() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    String unicodeString = "機体123";
    dto.setAircraftId(unicodeString);
    assertEquals(unicodeString, dto.getAircraftId());
  }

  /**
   * メソッド名: setUsageType_boundaryValues<br>
   * 試験名: usageTypeの境界値テスト<br>
   * 条件: setterに境界値を設定すること<br>
   * 結果: 境界値が設定されること<br>
   * テストパターン：境界値テスト<br>
   */
  @Test
  void setUsageType_boundaryValues() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setUsageType(Integer.MIN_VALUE);
    assertEquals(Integer.MIN_VALUE, dto.getUsageType());

    dto.setUsageType(Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, dto.getUsageType());
  }

  /**
   * メソッド名: setReservationTimeFrom_invalidFormat<br>
   * 試験名: reservationTimeFromに無効なフォーマットを設定する<br>
   * 条件: setterに無効なフォーマットを設定すること<br>
   * 結果: 無効なフォーマットが設定されること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void setReservationTimeFrom_invalidFormat() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    String invalidFormat = "invalid-date";
    dto.setReservationTimeFrom(invalidFormat);
    assertEquals(invalidFormat, dto.getReservationTimeFrom());
  }

  /**
   * メソッド名: setReservationTimeTo_extremeValue<br>
   * 試験名: reservationTimeToに極端な値を設定する<br>
   * 条件: setterに極端な値を設定すること<br>
   * 結果: 極端な値が設定されること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void setReservationTimeTo_extremeValue() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    String extremeValue = "9999-12-31T23:59:59";
    dto.setReservationTimeTo(extremeValue);
    assertEquals(extremeValue, dto.getReservationTimeTo());
  }
}
