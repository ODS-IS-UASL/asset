package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** AircraftReserveInfoListRequestDtoのテストクラス */
public class AircraftReserveInfoListRequestDtoTest {

  private AircraftReserveInfoListRequestDto dto;

  @BeforeEach
  public void setUp() {
    dto = new AircraftReserveInfoListRequestDto();
  }

  /**
   * メソッド名: setAircraftId<br>
   * 試験名: AircraftIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetAndGetAircraftId() {
    String aircraftId = "test-aircraft-id";
    dto.setAircraftId(aircraftId);
    assertEquals(aircraftId, dto.getAircraftId());
  }

  /**
   * メソッド名: setAircraftName<br>
   * 試験名: AircraftNameの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetAndGetAircraftName() {
    String aircraftName = "test-aircraft_name";
    dto.setAircraftName(aircraftName);
    assertEquals(aircraftName, dto.getAircraftName());
  }

  /**
   * メソッド名: setTimeFrom<br>
   * 試験名: TimeFromの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetAndGetTimeFrom() {
    String timeFrom = "2023-01-01T00:00:00";
    dto.setTimeFrom(timeFrom);
    assertEquals(timeFrom, dto.getTimeFrom());
  }

  /**
   * メソッド名: setTimeTo<br>
   * 試験名: TimeToの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetAndGetTimeTo() {
    String timeTo = "2023-01-01T23:59:59";
    dto.setTimeTo(timeTo);
    assertEquals(timeTo, dto.getTimeTo());
  }

  /**
   * メソッド名: setPerPage<br>
   * 試験名: perPageの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetAndGetPerPage() {
    String perPage = "1";
    dto.setPerPage(perPage);
    assertEquals(perPage, dto.getPerPage());
  }

  /**
   * メソッド名: setPage<br>
   * 試験名: pageの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetAndGetPage() {
    String page = "1";
    dto.setPage(page);
    assertEquals(page, dto.getPage());
  }

  /**
   * メソッド名: setSortOrders<br>
   * 試験名: perPageの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetSortOrders() {
    String sortOrders = "1,0";
    dto.setSortOrders(sortOrders);
    assertEquals(sortOrders, dto.getSortOrders());
  }

  /**
   * メソッド名: setSortColumns<br>
   * 試験名: perPageの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetSortColumns() {
    String sortColumns = "aircraftId,aircraftName";
    dto.setSortColumns(sortColumns);
    assertEquals(sortColumns, dto.getSortColumns());
  }

  /**
   * メソッド名: setAircraftId<br>
   * 試験名: AircraftIdにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testSetAircraftIdNull() {
    dto.setAircraftId(null);
    assertEquals(null, dto.getAircraftId());
  }

  /**
   * メソッド名: setAircraftName<br>
   * 試験名: AircraftNameにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testSetAircraftNameNull() {
    dto.setAircraftName(null);
    assertEquals(null, dto.getAircraftName());
  }

  /**
   * メソッド名: setTimeFrom<br>
   * 試験名: TimeFromにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testSetTimeFromNull() {
    dto.setTimeFrom(null);
    assertEquals(null, dto.getTimeFrom());
  }

  /**
   * メソッド名: setTimeTo<br>
   * 試験名: TimeToにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testSetTimeToNull() {
    dto.setTimeTo(null);
    assertEquals(null, dto.getTimeTo());
  }

  /**
   * メソッド名: setPerPage<br>
   * 試験名: perPageにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testSetPerPageNull() {
    dto.setPerPage(null);
    assertEquals(null, dto.getPerPage());
  }

  /**
   * メソッド名: setPage<br>
   * 試験名: pageにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testSetPageNull() {
    dto.setPage(null);
    assertEquals(null, dto.getPage());
  }

  /**
   * メソッド名: setSortOrders<br>
   * 試験名: sortOrdersにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testSetSortOrdersNull() {
    dto.setSortOrders(null);
    assertEquals(null, dto.getSortOrders());
  }

  /**
   * メソッド名: setSortColumns<br>
   * 試験名: sortColumnsにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testSetSortColumnsNull() {
    dto.setSortColumns(null);
    assertEquals(null, dto.getSortColumns());
  }

  /**
   * メソッド名: setAircraftId<br>
   * 試験名: AircraftIdに空文字を設定した場合の動作を確認する<br>
   * 条件: setterで空文字を設定し、getterで取得する<br>
   * 結果: 取得値が空文字であること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetAircraftIdEmpty() {
    dto.setAircraftId("");
    assertEquals("", dto.getAircraftId());
  }

  /**
   * メソッド名: setAircraftName<br>
   * 試験名: AircraftNameに空文字を設定した場合の動作を確認する<br>
   * 条件: setterで空文字を設定し、getterで取得する<br>
   * 結果: 取得値が空文字であること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetAircraftNameEmpty() {
    dto.setAircraftName("");
    assertEquals("", dto.getAircraftName());
  }

  /**
   * メソッド名: setTimeFrom<br>
   * 試験名: TimeFromに空文字を設定した場合の動作を確認する<br>
   * 条件: setterで空文字を設定し、getterで取得する<br>
   * 結果: 取得値が空文字であること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetTimeFromEmpty() {
    dto.setTimeFrom("");
    assertEquals("", dto.getTimeFrom());
  }

  /**
   * メソッド名: setTimeTo<br>
   * 試験名: TimeToに空文字を設定した場合の動作を確認する<br>
   * 条件: setterで空文字を設定し、getterで取得する<br>
   * 結果: 取得値が空文字であること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetTimeToEmpty() {
    dto.setTimeTo("");
    assertEquals("", dto.getTimeTo());
  }

  /**
   * メソッド名: setSortOrders<br>
   * 試験名: sortOrdersに空文字を設定した場合の動作を確認する<br>
   * 条件: setterで空文字を設定し、getterで取得する<br>
   * 結果: 取得値が空文字であること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetSortOrdersEmpty() {
    dto.setSortOrders("");
    assertEquals("", dto.getSortOrders());
  }

  /**
   * メソッド名: setSortColumns<br>
   * 試験名: sortColumnsに空文字を設定した場合の動作を確認する<br>
   * 条件: setterで空文字を設定し、getterで取得する<br>
   * 結果: 取得値が空文字であること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetSortColumnsEmpty() {
    dto.setSortColumns("");
    assertEquals("", dto.getSortColumns());
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
    String time = "2023-01-01T23:59:59";
    dto.setGroupReservationId("groupReservationId");
    dto.setAircraftId("aircraftId");
    dto.setAircraftName("aircraftName");
    dto.setTimeFrom(time);
    dto.setTimeTo(time);
    dto.setReserveProviderId("reserveProviderId");
    dto.setPage("1");
    dto.setPerPage("1");
    dto.setSortOrders("1");
    dto.setSortColumns("columns");
    // dto.setOperatorId("operator123");

    String expected =
        "AircraftReserveInfoListRequestDto(groupReservationId=groupReservationId, aircraftId=aircraftId, aircraftName=aircraftName, timeFrom=2023-01-01T23:59:59, timeTo=2023-01-01T23:59:59, reserveProviderId=reserveProviderId, perPage=1, page=1, sortOrders=1, sortColumns=columns)";
    assertEquals(expected, dto.toString());
  }
}
