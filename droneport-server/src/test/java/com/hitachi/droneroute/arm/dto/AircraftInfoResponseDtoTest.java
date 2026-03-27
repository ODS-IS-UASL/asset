package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** AircraftInfoResponseDtoのテストクラス. */
class AircraftInfoResponseDtoTest {

  /**
   * メソッド名: getAircraftId<br>
   * 試験名: getAircraftIdメソッドの正常動作を確認する<br>
   * 条件: aircraftIdに値を設定し、getAircraftIdで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftId_Normal() {
    AircraftInfoResponseDto dto = new AircraftInfoResponseDto();
    dto.setAircraftId("testAircraftId");
    assertEquals("testAircraftId", dto.getAircraftId());
  }

  /**
   * メソッド名: setAircraftId<br>
   * 試験名: setAircraftIdメソッドの正常動作を確認する<br>
   * 条件: aircraftIdに値を設定し、getAircraftIdで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void setAircraftId_Normal() {
    AircraftInfoResponseDto dto = new AircraftInfoResponseDto();
    dto.setAircraftId("testAircraftId");
    assertEquals("testAircraftId", dto.getAircraftId());
  }

  /**
   * メソッド名: getAircraftId<br>
   * 試験名: getAircraftIdメソッドの境界値テストを確認する<br>
   * 条件: aircraftIdに空文字を設定し、getAircraftIdで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：境界値テスト<br>
   */
  @Test
  void getAircraftId_EmptyString() {
    AircraftInfoResponseDto dto = new AircraftInfoResponseDto();
    dto.setAircraftId("");
    assertEquals("", dto.getAircraftId());
  }

  /**
   * メソッド名: setAircraftId<br>
   * 試験名: setAircraftIdメソッドの境界値テストを確認する<br>
   * 条件: aircraftIdに空文字を設定し、getAircraftIdで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：境界値テスト<br>
   */
  @Test
  void setAircraftId_EmptyString() {
    AircraftInfoResponseDto dto = new AircraftInfoResponseDto();
    dto.setAircraftId("");
    assertEquals("", dto.getAircraftId());
  }

  /**
   * メソッド名: getAircraftId<br>
   * 試験名: getAircraftIdメソッドの異常系テストを確認する<br>
   * 条件: aircraftIdにnullを設定し、getAircraftIdで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getAircraftId_Null() {
    AircraftInfoResponseDto dto = new AircraftInfoResponseDto();
    dto.setAircraftId(null);
    assertNull(dto.getAircraftId());
  }

  /**
   * メソッド名: setAircraftId<br>
   * 試験名: setAircraftIdメソッドの異常系テストを確認する<br>
   * 条件: aircraftIdにnullを設定し、getAircraftIdで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void setAircraftId_Null() {
    AircraftInfoResponseDto dto = new AircraftInfoResponseDto();
    dto.setAircraftId(null);
    assertNull(dto.getAircraftId());
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
    AircraftInfoResponseDto dto = new AircraftInfoResponseDto();

    dto.setAircraftId("aircraftId");

    String expected = "AircraftInfoResponseDto(aircraftId=aircraftId)";
    assertEquals(expected, dto.toString());
  }
}
