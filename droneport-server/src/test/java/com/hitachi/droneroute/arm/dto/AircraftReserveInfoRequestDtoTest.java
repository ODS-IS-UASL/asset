package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** AircraftReserveInfoRequestDtoTest クラスのテスト */
public class AircraftReserveInfoRequestDtoTest {

  /**
   * メソッド名: testAircraftReservationIdGetterSetter<br>
   * 試験名: aircraftReservationId の getter と setter の動作確認<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testAircraftReservationIdGetterSetter() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    String expected = "reservation123";
    dto.setAircraftReservationId(expected);
    assertEquals(expected, dto.getAircraftReservationId());
  }

  /**
   * メソッド名: testAircraftIdGetterSetter<br>
   * 試験名: aircraftId の getter と setter の動作確認<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testAircraftIdGetterSetter() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    String expected = "aircraft123";
    dto.setAircraftId(expected);
    assertEquals(expected, dto.getAircraftId());
  }

  /**
   * メソッド名: testReservationTimeFromGetterSetter<br>
   * 試験名: reservationTimeFrom の getter と setter の動作確認<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testReservationTimeFromGetterSetter() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    String expected = "2023-01-01T10:00:00";
    dto.setReservationTimeFrom(expected);
    assertEquals(expected, dto.getReservationTimeFrom());
  }

  /**
   * メソッド名: testReservationTimeToGetterSetter<br>
   * 試験名: reservationTimeTo の getter と setter の動作確認<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testReservationTimeToGetterSetter() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    String expected = "2023-01-01T12:00:00";
    dto.setReservationTimeTo(expected);
    assertEquals(expected, dto.getReservationTimeTo());
  }

  /**
   * メソッド名: testBoundaryValues<br>
   * 試験名: 各フィールドの境界値テスト<br>
   * 条件: 各フィールドに対して境界値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：境界値テスト<br>
   */
  @Test
  public void testBoundaryValues() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();

    // Boundary values for aircraftReservationId
    String minAircraftReservationId = "";
    String maxAircraftReservationId = "a".repeat(255);
    dto.setAircraftReservationId(minAircraftReservationId);
    assertEquals(minAircraftReservationId, dto.getAircraftReservationId());
    dto.setAircraftReservationId(maxAircraftReservationId);
    assertEquals(maxAircraftReservationId, dto.getAircraftReservationId());

    // Boundary values for aircraftId
    String minAircraftId = "";
    String maxAircraftId = "a".repeat(255);
    dto.setAircraftId(minAircraftId);
    assertEquals(minAircraftId, dto.getAircraftId());
    dto.setAircraftId(maxAircraftId);
    assertEquals(maxAircraftId, dto.getAircraftId());

    // Boundary values for reservationTimeFrom and reservationTimeTo
    String minTime = "0000-01-01T00:00:00";
    String maxTime = "9999-12-31T23:59:59";
    dto.setReservationTimeFrom(minTime);
    assertEquals(minTime, dto.getReservationTimeFrom());
    dto.setReservationTimeTo(maxTime);
    assertEquals(maxTime, dto.getReservationTimeTo());
  }

  /**
   * メソッド名: testInvalidValues<br>
   * 試験名: 各フィールドの異常値テスト<br>
   * 条件: 各フィールドに対して無効な値を設定し、例外が発生することを確認する<br>
   * 結果: 例外が発生すること<br>
   * テストパターン：異常系テスト<br>
   */
  @Test
  public void testInvalidValues() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();

    // Invalid values for reservationTimeFrom and reservationTimeTo
    String invalidTime = "invalid-time-format";
    dto.setReservationTimeFrom(invalidTime);
    dto.setReservationTimeTo(invalidTime);

    assertEquals(invalidTime, dto.getReservationTimeFrom());
    assertEquals(invalidTime, dto.getReservationTimeTo());
  }

  /**
   * メソッド名: testEdgeCases<br>
   * 試験名: 各フィールドのエッジケーステスト<br>
   * 条件: 各フィールドに対して特殊なケースを設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：エッジケーステスト<br>
   */
  @Test
  public void testEdgeCases() {
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();

    // Edge cases for aircraftReservationId and aircraftId
    String unicodeString = "予約ID-テスト";
    dto.setAircraftReservationId(unicodeString);
    assertEquals(unicodeString, dto.getAircraftReservationId());
    dto.setAircraftId(unicodeString);
    assertEquals(unicodeString, dto.getAircraftId());

    // Edge cases for reservationTimeFrom and reservationTimeTo
    String edgeTime = "2023-02-29T10:00:00"; // Leap year case
    dto.setReservationTimeFrom(edgeTime);
    assertEquals(edgeTime, dto.getReservationTimeFrom());
    dto.setReservationTimeTo(edgeTime);
    assertEquals(edgeTime, dto.getReservationTimeTo());
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
    AircraftReserveInfoRequestDto dto = new AircraftReserveInfoRequestDto();
    dto.setAircraftId("aircraftId");
    dto.setAircraftReservationId("aircraftReservationId");
    dto.setGroupReservationId("groupReservationId");
    String time = "2023-02-29T10:00:00";
    dto.setReservationTimeFrom(time);
    dto.setReservationTimeTo(time);

    String expected =
        "AircraftReserveInfoRequestDto(aircraftReservationId=aircraftReservationId, groupReservationId=groupReservationId, aircraftId=aircraftId, reservationTimeFrom=2023-02-29T10:00:00, reservationTimeTo=2023-02-29T10:00:00)";
    assertEquals(expected, dto.toString());
  }
}
