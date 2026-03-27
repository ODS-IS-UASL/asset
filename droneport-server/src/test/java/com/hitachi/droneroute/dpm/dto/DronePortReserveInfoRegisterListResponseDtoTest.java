package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * DronePortReserveInfoRegisterListResponseDtoTest<br>
 * このクラスはDronePortReserveInfoRegisterListResponseDtoクラスをテストします。
 */
public class DronePortReserveInfoRegisterListResponseDtoTest {

  /**
   * メソッド名: setDronePortReservationIds, getDronePortReservationIds<br>
   * 試験名: 離着陸場予約IDリストの設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な離着陸場予約IDリストを設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetDronePortReservationIds() {
    DronePortReserveInfoRegisterListResponseDto dto =
        new DronePortReserveInfoRegisterListResponseDto();
    List<String> reservationIds = new ArrayList<>();
    reservationIds.add("RES001");
    reservationIds.add("RES002");
    dto.setDronePortReservationIds(reservationIds);
    assertEquals(reservationIds, dto.getDronePortReservationIds());
  }

  /**
   * メソッド名: toString<br>
   * 試験名: toStringメソッドが正しい文字列を返すことを確認する<br>
   * 条件: 全てのフィールドに値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 期待される文字列が返されること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testToString() {
    DronePortReserveInfoRegisterListResponseDto dto =
        new DronePortReserveInfoRegisterListResponseDto();
    List<String> reservationIds = new ArrayList<>();
    reservationIds.add("RES001");
    reservationIds.add("RES002");
    dto.setDronePortReservationIds(reservationIds);

    String expectedString =
        "DronePortReserveInfoRegisterListResponseDto(dronePortReservationIds=[RES001, RES002])";
    assertEquals(expectedString, dto.toString());
  }
}
