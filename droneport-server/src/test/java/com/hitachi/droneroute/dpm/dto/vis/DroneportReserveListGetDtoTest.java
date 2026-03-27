package com.hitachi.droneroute.dpm.dto.vis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * クラス名: DroneportReserveListGetDtoTest<br>
 * 説明: DroneportReserveListGetDtoクラスのテストを行う<br>
 */
class DroneportReserveListGetDtoTest {

  /**
   * メソッド名: getDroneportId_setDroneportId<br>
   * 試験名: ドローンポートIDの設定と取得確認<br>
   * 条件: setterによりドローンポートIDを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDroneportId_setDroneportId() {
    // ●準備
    DroneportReserveListGetDto target = new DroneportReserveListGetDto();
    String expected = "testDroneportId";

    // ●実行
    target.setDroneportReservationId(expected);
    String actual = target.getDroneportReservationId();

    // ●検証
    assertEquals(expected, actual);
  }

  /**
   * メソッド名: setDroneportId_nullValue<br>
   * 試験名: ドローンポートIDにnullを設定した場合の確認<br>
   * 条件: setterによりドローンポートIDにnullを設定し、getterで取得する<br>
   * 結果: nullが取得されること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void setDroneportId_nullValue() {
    // ●準備
    DroneportReserveListGetDto target = new DroneportReserveListGetDto();

    // ●実行
    target.setDroneportReservationId(null);
    String actual = target.getDroneportReservationId();

    // ●検証
    assertEquals(null, actual);
  }
}
