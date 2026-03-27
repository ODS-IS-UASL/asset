package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import org.junit.jupiter.api.Test;

/** AircraftReserveInfoDetailResponseDtoのテストクラス */
public class AircraftReserveInfoDetailResponseDtoTest {

  /**
   * メソッド名: getAircraftReservationId<br>
   * 試験名: AircraftReservationIdの取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetAircraftReservationId() {
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();
    String expected = "reservation123";
    dto.setAircraftReservationId(expected);
    assertEquals(expected, dto.getAircraftReservationId());
  }

  /**
   * メソッド名: getAircraftId<br>
   * 試験名: AircraftIdの取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetAircraftId() {
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();
    String expected = "aircraft123";
    dto.setAircraftId(expected);
    assertEquals(expected, dto.getAircraftId());
  }

  /**
   * メソッド名: getReservationTimeFrom<br>
   * 試験名: ReservationTimeFromの取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetReservationTimeFrom() {
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();
    Timestamp expected = Timestamp.valueOf("2023-01-01 10:00:00");
    dto.setReservationTimeFrom(expected.toString());
    assertEquals(expected.toString(), dto.getReservationTimeFrom());
  }

  /**
   * メソッド名: getReservationTimeTo<br>
   * 試験名: ReservationTimeToの取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetReservationTimeTo() {
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();
    Timestamp expected = Timestamp.valueOf("2023-01-01 12:00:00");
    dto.setReservationTimeTo(expected.toString());
    assertEquals(expected.toString(), dto.getReservationTimeTo());
  }

  /**
   * メソッド名: setAircraftReservationId<br>
   * 試験名: AircraftReservationIdの設定が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testSetAircraftReservationId() {
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();
    String expected = "reservation123";
    dto.setAircraftReservationId(expected);
    assertEquals(expected, dto.getAircraftReservationId());
  }

  /**
   * メソッド名: setAircraftId<br>
   * 試験名: AircraftIdの設定が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testSetAircraftId() {
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();
    String expected = "aircraft123";
    dto.setAircraftId(expected);
    assertEquals(expected, dto.getAircraftId());
  }

  /**
   * メソッド名: setReservationTimeFrom<br>
   * 試験名: ReservationTimeFromの設定が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testSetReservationTimeFrom() {
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();
    Timestamp expected = Timestamp.valueOf("2023-01-01 10:00:00");
    dto.setReservationTimeFrom(expected.toString());
    assertEquals(expected.toString(), dto.getReservationTimeFrom());
  }

  /**
   * メソッド名: setReservationTimeTo<br>
   * 試験名: ReservationTimeToの設定が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testSetReservationTimeTo() {
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();
    Timestamp expected = Timestamp.valueOf("2023-01-01 12:00:00");
    dto.setReservationTimeTo(expected.toString());
    assertEquals(expected.toString(), dto.getReservationTimeTo());
  }

  /**
   * メソッド名: setReservationTimeTo<br>
   * 試験名: ReservationTimeToの設定が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testSetAndGetOperatorId() {
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();
    String value = "operator123";
    dto.setOperatorId(value);
    assertEquals(value, dto.getOperatorId());
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
    AircraftReserveInfoDetailResponseDto dto = new AircraftReserveInfoDetailResponseDto();

    dto.setAircraftId("aircraftId");
    dto.setAircraftReservationId("aircraftReservationId");
    dto.setGroupReservationId("groupReservationId");
    Timestamp timestamp = Timestamp.valueOf("2023-01-01 12:00:00");
    dto.setReservationTimeFrom(timestamp.toString());
    dto.setReservationTimeTo(timestamp.toString());
    dto.setAircraftName("aircraftName");
    dto.setReserveProviderId("reserveProviderId");
    dto.setOperatorId("operator123");

    String expected =
        "AircraftReserveInfoDetailResponseDto(aircraftReservationId=aircraftReservationId, groupReservationId=groupReservationId, aircraftId=aircraftId, reservationTimeFrom=2023-01-01 12:00:00.0, reservationTimeTo=2023-01-01 12:00:00.0, aircraftName=aircraftName, reserveProviderId=reserveProviderId, operatorId=operator123)";
    assertEquals(expected, dto.toString());
  }
}
