package com.hitachi.droneroute.dpm.dto.vis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * ReservationInfoTestクラス<br>
 * ReservationInfoクラスの各メソッドをテストする<br>
 */
class ReservationInfoTest {

  /**
   * メソッド名: getReservationId_setReservationId<br>
   * 試験名: bookingIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getReservationId_setReservationId() {
    ReservationInfo reservationInfo = new ReservationInfo();
    String expected = "12345";
    reservationInfo.setReservationId(expected);
    assertEquals(expected, reservationInfo.getReservationId());
  }

  /**
   * メソッド名: getStartTime_setStartTime<br>
   * 試験名: startTimeの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getStartTime_setStartTime() {
    ReservationInfo reservationInfo = new ReservationInfo();
    String expected = "2023-10-01T10:00:00Z";
    reservationInfo.setStartTime(expected);
    assertEquals(expected, reservationInfo.getStartTime());
  }

  /**
   * メソッド名: getEndTime_setEndTime<br>
   * 試験名: endTimeの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getEndTime_setEndTime() {
    ReservationInfo reservationInfo = new ReservationInfo();
    String expected = "2023-10-01T12:00:00Z";
    reservationInfo.setEndTime(expected);
    assertEquals(expected, reservationInfo.getEndTime());
  }

  /**
   * メソッド名: setReservationId_nullValue<br>
   * 試験名: reservationIdにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void setReservationId_nullValue() {
    ReservationInfo reservationInfo = new ReservationInfo();
    reservationInfo.setReservationId(null);
    assertEquals(null, reservationInfo.getReservationId());
  }
}
