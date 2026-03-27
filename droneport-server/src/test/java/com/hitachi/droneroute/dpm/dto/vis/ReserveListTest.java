package com.hitachi.droneroute.dpm.dto.vis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

/** ReserveListクラスのテスト<br> */
class ReserveListTest {

  /**
   * メソッド名: getDroneportId, setDroneportId<br>
   * 試験名: droneportIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDroneportId() {
    ReserveList reserveList = new ReserveList();
    String expected = "droneport123";
    reserveList.setDroneportId(expected);
    assertEquals(expected, reserveList.getDroneportId());
  }

  /**
   * メソッド名: getReservationInfo, setReservationInfo<br>
   * 試験名: reservationInfoの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetReservationInfo() {
    ReserveList reserveList = new ReserveList();
    List<ReservationInfo> expected = List.of(new ReservationInfo(), new ReservationInfo());
    reserveList.setReservationInfo(expected);
    assertEquals(expected, reserveList.getReservationInfo());
  }

  /**
   * メソッド名: getResponseStatus, setResponseStatus<br>
   * 試験名: responseStatusの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetResponseStatus() {
    ReserveList reserveList = new ReserveList();
    boolean expected = true;
    reserveList.setResponseStatus(expected);
    assertEquals(expected, reserveList.isResponseStatus());
  }

  /**
   * メソッド名: getInfo, setInfo<br>
   * 試験名: infoの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetInfo() {
    ReserveList reserveList = new ReserveList();
    String expected = "Some info";
    reserveList.setInfo(expected);
    assertEquals(expected, reserveList.getInfo());
  }

  /**
   * メソッド名: setDroneportId<br>
   * 試験名: droneportIdにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testSetDroneportIdNull() {
    ReserveList reserveList = new ReserveList();
    reserveList.setDroneportId(null);
    assertNull(reserveList.getDroneportId());
  }

  /**
   * メソッド名: setReservationInfo<br>
   * 試験名: reservationInfoにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testSetReservationInfoNull() {
    ReserveList reserveList = new ReserveList();
    reserveList.setReservationInfo(null);
    assertNull(reserveList.getReservationInfo());
  }

  /**
   * メソッド名: setInfo<br>
   * 試験名: infoにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testSetInfoNull() {
    ReserveList reserveList = new ReserveList();
    reserveList.setInfo(null);
    assertNull(reserveList.getInfo());
  }
}
