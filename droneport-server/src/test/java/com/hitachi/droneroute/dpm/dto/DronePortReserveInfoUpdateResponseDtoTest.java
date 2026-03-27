package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * テストクラス: DronePortReserveInfoRegisterResponseDtoTest<br>
 * 対象クラス: DronePortReserveInfoRegisterResponseDto<br>
 */
public class DronePortReserveInfoUpdateResponseDtoTest {

  /**
   * メソッド名: getDronePortReservationId<br>
   * 試験名: dronePortReservationIdの取得が正しく行われることを確認する<br>
   * 条件: dronePortReservationIdに値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDronePortReservationId() {
    // 準備
    DronePortReserveInfoUpdateResponseDto dto = new DronePortReserveInfoUpdateResponseDto();
    String expected = "testId";
    dto.setDronePortReservationId(expected);

    // 実行
    String actual = dto.getDronePortReservationId();

    // 検証
    assertEquals(expected, actual);
  }

  /**
   * メソッド名: setDronePortReservationId<br>
   * 試験名: dronePortReservationIdの設定が正しく行われることを確認する<br>
   * 条件: dronePortReservationIdに値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void setDronePortReservationId() {
    // 準備
    DronePortReserveInfoUpdateResponseDto dto = new DronePortReserveInfoUpdateResponseDto();
    String expected = "testId";

    // 実行
    dto.setDronePortReservationId(expected);
    String actual = dto.getDronePortReservationId();

    // 検証
    assertEquals(expected, actual);
  }

  /**
   * メソッド名: toString<br>
   * 試験名: toStringメソッドが正しく動作することを確認する<br>
   * 条件: dronePortReservationIdに値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 期待される文字列が返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testToString() {
    // 準備
    DronePortReserveInfoUpdateResponseDto dto = new DronePortReserveInfoUpdateResponseDto();
    dto.setDronePortReservationId("testId");

    // 実行
    String actual = dto.toString();

    // 検証
    assertTrue(actual.contains("testId"));
  }

  /**
   * メソッド名: setDronePortReservationId<br>
   * 試験名: dronePortReservationIdにnullを設定した場合の動作を確認する<br>
   * 条件: dronePortReservationIdにnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void setDronePortReservationId_null() {
    // 準備
    DronePortReserveInfoUpdateResponseDto dto = new DronePortReserveInfoUpdateResponseDto();

    // 実行
    dto.setDronePortReservationId(null);
    String actual = dto.getDronePortReservationId();

    // 検証
    assertNull(actual);
  }

  /**
   * メソッド名: setDronePortReservationId<br>
   * 試験名: dronePortReservationIdに空文字を設定した場合の動作を確認する<br>
   * 条件: dronePortReservationIdに空文字を設定し、getterで取得する<br>
   * 結果: 取得値が空文字であること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void setDronePortReservationId_emptyString() {
    // 準備
    DronePortReserveInfoUpdateResponseDto dto = new DronePortReserveInfoUpdateResponseDto();
    String expected = "";

    // 実行
    dto.setDronePortReservationId(expected);
    String actual = dto.getDronePortReservationId();

    // 検証
    assertEquals(expected, actual);
  }
}
