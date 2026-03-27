package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** テストクラス: DronePortReserveInfoListResponseDtoTest<br> */
public class DronePortReserveInfoListResponseDtoTest {

  /**
   * メソッド名: getData_setData<br>
   * 試験名: getDataとsetDataの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getData_setData() {
    // ●準備
    DronePortReserveInfoListResponseDto dto = new DronePortReserveInfoListResponseDto();
    List<DronePortReserveInfoListElement> expected = new ArrayList<>();
    expected.add(new DronePortReserveInfoListElement());

    // ●実行
    dto.setData(expected);
    List<DronePortReserveInfoListElement> actual = dto.getData();

    // ●検証
    assertEquals(expected, actual);
  }

  /**
   * メソッド名: getPerPage_setPerPage<br>
   * 試験名: perPageフィールドの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getPerPage_setPerPage() {
    // ●準備
    DronePortReserveInfoListResponseDto dto = new DronePortReserveInfoListResponseDto();
    Integer expectedPerPage = 10;

    // ●実行
    dto.setPerPage(expectedPerPage);
    Integer actualPerPage = dto.getPerPage();

    // ●検証
    assertEquals(expectedPerPage, actualPerPage);
  }

  /**
   * メソッド名: getCurrentPage_setCurrentPage<br>
   * 試験名: currentPageフィールドの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getCurrentPage_setCurrentPage() {
    // ●準備
    DronePortReserveInfoListResponseDto dto = new DronePortReserveInfoListResponseDto();
    Integer expectedCurrentPage = 1;

    // ●実行
    dto.setCurrentPage(expectedCurrentPage);
    Integer actualCurrentPage = dto.getCurrentPage();

    // ●検証
    assertEquals(expectedCurrentPage, actualCurrentPage);
  }

  /**
   * メソッド名: getLastPage_setLastPage<br>
   * 試験名: lastPageフィールドの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getLastPage_setLastPage() {
    // ●準備
    DronePortReserveInfoListResponseDto dto = new DronePortReserveInfoListResponseDto();
    Integer expectedLastPage = 5;

    // ●実行
    dto.setLastPage(expectedLastPage);
    Integer actualLastPage = dto.getLastPage();

    // ●検証
    assertEquals(expectedLastPage, actualLastPage);
  }

  /**
   * メソッド名: getTotal_setTotal<br>
   * 試験名: totalフィールドの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getTotal_setTotal() {
    // ●準備
    DronePortReserveInfoListResponseDto dto = new DronePortReserveInfoListResponseDto();
    Integer expectedTotal = 50;

    // ●実行
    dto.setTotal(expectedTotal);
    Integer actualTotal = dto.getTotal();

    // ●検証
    assertEquals(expectedTotal, actualTotal);
  }

  /**
   * メソッド名: toString<br>
   * 試験名: toStringの動作確認<br>
   * 条件: オブジェクトの内容をtoStringで取得する<br>
   * 結果: 取得した文字列が期待される形式であること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void toStringTest() {
    // ●準備
    DronePortReserveInfoListResponseDto dto = new DronePortReserveInfoListResponseDto();
    List<DronePortReserveInfoListElement> data = new ArrayList<>();
    data.add(newItem());
    dto.setData(data);
    dto.setPerPage(10);
    dto.setCurrentPage(3);
    dto.setLastPage(15);
    dto.setTotal(99);

    // ●実行
    String actual = dto.toString();

    // ●検証
    assertNotNull(actual);
    assertEquals(
        "DronePortReserveInfoListResponseDto(data=["
            + "DronePortReserveInfoListElement(dronePortReservationId=reservation123, groupReservationId=groupReservation123, dronePortId=port123, aircraftId=aircraft123, routeReservationId=route123, usageType=1, reservationTimeFrom=2023-01-01 10:00:00, reservationTimeTo=2023-01-01 12:00:00, dronePortName=dummyDronePort, aircraftName=dummyAircraft, visDronePortCompanyId=dummyVisDronePortCompanyId, reservationActiveFlag=true, inactiveTimeFrom=2023-01-01 08:00:00, inactiveTimeTo=2023-01-01 09:00:00, reserveProviderId=provider123, operatorId=operator123)"
            + "], perPage=10, currentPage=3, lastPage=15, total=99)",
        actual);
  }

  DronePortReserveInfoListElement newItem() {
    DronePortReserveInfoListElement dto = new DronePortReserveInfoListElement();
    dto.setDronePortReservationId("reservation123");
    dto.setGroupReservationId("groupReservation123");
    dto.setDronePortId("port123");
    dto.setAircraftId("aircraft123");
    dto.setRouteReservationId("route123");
    dto.setUsageType(1);
    dto.setReservationTimeFrom("2023-01-01 10:00:00");
    dto.setReservationTimeTo("2023-01-01 12:00:00");
    dto.setDronePortName("dummyDronePort");
    dto.setAircraftName("dummyAircraft");
    dto.setVisDronePortCompanyId("dummyVisDronePortCompanyId");
    dto.setReservationActiveFlag(true);
    dto.setInactiveTimeFrom("2023-01-01 08:00:00");
    dto.setInactiveTimeTo("2023-01-01 09:00:00");
    dto.setReserveProviderId("provider123");
    dto.setOperatorId("operator123");
    return dto;
  }

  /**
   * メソッド名: setData_null<br>
   * 試験名: setDataにnullを設定した場合の動作確認<br>
   * 条件: setterにnullを設定する<br>
   * 結果: 例外が発生しないこと<br>
   * テストパターン：異常系<br>
   */
  @Test
  void setData_null() {
    // ●準備
    DronePortReserveInfoListResponseDto dto = new DronePortReserveInfoListResponseDto();

    // ●実行 & 検証
    dto.setData(null);
    assertEquals(null, dto.getData());
  }

  /**
   * メソッド名: setData_emptyList<br>
   * 試験名: setDataに空のリストを設定した場合の動作確認<br>
   * 条件: setterに空のリストを設定する<br>
   * 結果: 例外が発生しないこと<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void setData_emptyList() {
    // ●準備
    DronePortReserveInfoListResponseDto dto = new DronePortReserveInfoListResponseDto();
    List<DronePortReserveInfoListElement> emptyList = new ArrayList<>();

    // ●実行
    dto.setData(emptyList);
    List<DronePortReserveInfoListElement> actual = dto.getData();

    // ●検証
    assertEquals(emptyList, actual);
  }
}
