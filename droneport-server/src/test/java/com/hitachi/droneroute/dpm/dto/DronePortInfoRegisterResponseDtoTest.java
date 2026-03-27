package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * テストクラス: DronePortInfoRegisterResponseDtoTest<br>
 * 対象クラス: DronePortInfoRegisterResponseDto<br>
 */
public class DronePortInfoRegisterResponseDtoTest {

  /**
   * メソッド名: getDronePortId_setDronePortId<br>
   * 試験名: dronePortIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getDronePortId_setDronePortId() {
    // ●準備
    DronePortInfoRegisterResponseDto dto = new DronePortInfoRegisterResponseDto();
    String expected = "testId";

    // ●実行
    dto.setDronePortId(expected);
    String actual = dto.getDronePortId();

    // ●検証
    assertEquals(expected, actual);
  }

  /**
   * メソッド名: toString<br>
   * 試験名: toStringメソッドが正しく動作することを確認する<br>
   * 条件: 全ての項目に値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 設定値が含まれる文字列が返されること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void toStringTest() {
    // ●準備
    DronePortInfoRegisterResponseDto dto = new DronePortInfoRegisterResponseDto();
    dto.setDronePortId("testId");

    // ●実行
    String actual = dto.toString();

    // ●検証
    assertTrue(actual.contains("testId"));
  }

  /**
   * メソッド名: getDronePortId_setDronePortId<br>
   * 試験名: dronePortIdにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void getDronePortId_setDronePortId_withNull() {
    // ●準備
    DronePortInfoRegisterResponseDto dto = new DronePortInfoRegisterResponseDto();

    // ●実行
    dto.setDronePortId(null);
    String actual = dto.getDronePortId();

    // ●検証
    assertNull(actual);
  }

  /**
   * メソッド名: getDronePortId_setDronePortId<br>
   * 試験名: dronePortIdに空文字を設定した場合の動作を確認する<br>
   * 条件: setterで空文字を設定し、getterで取得する<br>
   * 結果: 取得値が空文字であること<br>
   * テストパターン: エッジケース<br>
   */
  @Test
  void getDronePortId_setDronePortId_withEmptyString() {
    // ●準備
    DronePortInfoRegisterResponseDto dto = new DronePortInfoRegisterResponseDto();
    String expected = "";

    // ●実行
    dto.setDronePortId(expected);
    String actual = dto.getDronePortId();

    // ●検証
    assertEquals(expected, actual);
  }
}
