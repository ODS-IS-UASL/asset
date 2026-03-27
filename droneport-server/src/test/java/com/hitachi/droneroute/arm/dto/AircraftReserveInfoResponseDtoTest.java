package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** AircraftReserveInfoResponseDtoのテストクラス */
public class AircraftReserveInfoResponseDtoTest {

  private AircraftReserveInfoResponseDto dto;

  @BeforeEach
  public void setUp() {
    dto = new AircraftReserveInfoResponseDto();
  }

  /**
   * メソッド名: getAircraftReservationId<br>
   * 試験名: 初期状態でのaircraftReservationIdの値がnullであることを確認する<br>
   * 条件: 初期状態でgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetAircraftReservationId_initialState() {
    assertNull(dto.getAircraftReservationId());
  }

  /**
   * メソッド名: setAircraftReservationId<br>
   * 試験名: aircraftReservationIdに値を設定し、getterで取得できることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定した値が取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetAircraftReservationId() {
    String expectedId = "test-id";
    dto.setAircraftReservationId(expectedId);
    assertEquals(expectedId, dto.getAircraftReservationId());
  }

  /**
   * メソッド名: setAircraftReservationId<br>
   * 試験名: aircraftReservationIdにnullを設定し、getterでnullが取得できることを確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: nullが取得できること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testSetAircraftReservationId_null() {
    dto.setAircraftReservationId(null);
    assertNull(dto.getAircraftReservationId());
  }

  /**
   * メソッド名: setAircraftReservationId<br>
   * 試験名: aircraftReservationIdに空文字を設定し、getterで空文字が取得できることを確認する<br>
   * 条件: setterで空文字を設定し、getterで取得する<br>
   * 結果: 空文字が取得できること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetAircraftReservationId_emptyString() {
    String expectedId = "";
    dto.setAircraftReservationId(expectedId);
    assertEquals(expectedId, dto.getAircraftReservationId());
  }

  /**
   * メソッド名: setAircraftReservationId<br>
   * 試験名: aircraftReservationIdに長い文字列を設定し、getterで同じ文字列が取得できることを確認する<br>
   * 条件: setterで長い文字列を設定し、getterで取得する<br>
   * 結果: 設定した長い文字列が取得できること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetAircraftReservationId_longString() {
    String expectedId = "a".repeat(1000);
    dto.setAircraftReservationId(expectedId);
    assertEquals(expectedId, dto.getAircraftReservationId());
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
    dto.setAircraftReservationId("aircraftReservationId");

    String expected = "AircraftReserveInfoResponseDto(aircraftReservationId=aircraftReservationId)";
    assertEquals(expected, dto.toString());
  }
}
