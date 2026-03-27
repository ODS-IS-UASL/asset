package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * DronePortReserveInfoRegisterListRequestDtoTest<br>
 * このクラスはDronePortReserveInfoRegisterListRequestDtoクラスをテストします。
 */
public class DronePortReserveInfoRegisterListRequestDtoTest {

  /**
   * メソッド名: setData, getData<br>
   * 試験名: データリストの設定と取得が正しく行われることを確認する<br>
   * 条件: 有効なデータリストを設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetData() {
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    List<DronePortReserveInfoRegisterListRequestDto.Element> dataList = new ArrayList<>();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId("DP001");
    element.setAircraftId("AC001");
    element.setRouteReservationId("RR001");
    element.setUsageType(1);
    element.setReservationTimeFrom("2023-01-01T10:00:00");
    element.setReservationTimeTo("2023-01-01T12:00:00");
    dataList.add(element);
    dto.setData(dataList);
    assertEquals(dataList, dto.getData());
  }

  /**
   * メソッド名: setDronePortId, getDronePortId<br>
   * 試験名: 離着陸場IDの設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な離着陸場IDを設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetDronePortId() {
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    String dronePortId = "DP001";
    element.setDronePortId(dronePortId);
    assertEquals(dronePortId, element.getDronePortId());
  }

  /**
   * メソッド名: setAircraftId, getAircraftId<br>
   * 試験名: 使用機体IDの設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な使用機体IDを設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetAircraftId() {
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    String aircraftId = "AC001";
    element.setAircraftId(aircraftId);
    assertEquals(aircraftId, element.getAircraftId());
  }

  /**
   * メソッド名: setRouteReservationId, getRouteReservationId<br>
   * 試験名: 航路予約IDの設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な航路予約IDを設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetRouteReservationId() {
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    String routeReservationId = "RR001";
    element.setRouteReservationId(routeReservationId);
    assertEquals(routeReservationId, element.getRouteReservationId());
  }

  /**
   * メソッド名: setUsageType, getUsageType<br>
   * 試験名: 利用形態の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な利用形態を設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetUsageType() {
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    Integer usageType = 1;
    element.setUsageType(usageType);
    assertEquals(usageType, element.getUsageType());
  }

  /**
   * メソッド名: setReservationTimeFrom, getReservationTimeFrom<br>
   * 試験名: 予約開始日時の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な予約開始日時を設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetReservationTimeFrom() {
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    String reservationTimeFrom = "2023-01-01T10:00:00";
    element.setReservationTimeFrom(reservationTimeFrom);
    assertEquals(reservationTimeFrom, element.getReservationTimeFrom());
  }

  /**
   * メソッド名: setReservationTimeTo, getReservationTimeTo<br>
   * 試験名: 予約終了日時の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な予約終了日時を設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetReservationTimeTo() {
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    String reservationTimeTo = "2023-01-01T12:00:00";
    element.setReservationTimeTo(reservationTimeTo);
    assertEquals(reservationTimeTo, element.getReservationTimeTo());
  }

  /**
   * メソッド名: toString<br>
   * 試験名: ElementクラスのtoStringメソッドが正しい文字列を返すことを確認する<br>
   * 条件: 全てのフィールドに値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 期待される文字列が返されること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testElementToString() {
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setGroupReservationId("GR001");
    element.setDronePortId("DP001");
    element.setAircraftId("AC001");
    element.setRouteReservationId("RR001");
    element.setUsageType(1);
    element.setReservationTimeFrom("2023-01-01T10:00:00");
    element.setReservationTimeTo("2023-01-01T12:00:00");

    String expectedString =
        "DronePortReserveInfoRegisterListRequestDto.Element(groupReservationId=GR001, dronePortId=DP001, aircraftId=AC001, routeReservationId=RR001, usageType=1, reservationTimeFrom=2023-01-01T10:00:00, reservationTimeTo=2023-01-01T12:00:00)";
    assertEquals(expectedString, element.toString());
  }

  /**
   * メソッド名: toString<br>
   * 試験名: DronePortReserveInfoRegisterListRequestDtoクラスのtoStringメソッドが正しい文字列を返すことを確認する<br>
   * 条件: 全てのフィールドに値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 期待される文字列が返されること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testDtoToString() {
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    List<DronePortReserveInfoRegisterListRequestDto.Element> dataList = new ArrayList<>();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setGroupReservationId("GR001");
    element.setDronePortId("DP001");
    element.setAircraftId("AC001");
    element.setRouteReservationId("RR001");
    element.setUsageType(1);
    element.setReservationTimeFrom("2023-01-01T10:00:00");
    element.setReservationTimeTo("2023-01-01T12:00:00");
    dataList.add(element);
    dto.setData(dataList);
    // dto.setOperatorId("operator123");

    String expectedString =
        "DronePortReserveInfoRegisterListRequestDto(data=[DronePortReserveInfoRegisterListRequestDto.Element(groupReservationId=GR001, dronePortId=DP001, aircraftId=AC001, routeReservationId=RR001, usageType=1, reservationTimeFrom=2023-01-01T10:00:00, reservationTimeTo=2023-01-01T12:00:00)])";
    assertEquals(expectedString, dto.toString());
  }
}
