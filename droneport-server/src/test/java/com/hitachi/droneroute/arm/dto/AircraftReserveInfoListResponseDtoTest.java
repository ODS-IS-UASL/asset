package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** AircraftReserveInfoListResponseDtoのテストクラス */
public class AircraftReserveInfoListResponseDtoTest {

  /**
   * メソッド名: getData<br>
   * 試験名: getDataメソッドが正しく動作することを確認する<br>
   * 条件: デフォルトコンストラクタで初期化し、getDataメソッドを呼び出す<br>
   * 結果: 初期値としてnullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetData_DefaultConstructor() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    assertNull(dto.getData(), "初期値としてnullが返されること");
  }

  /**
   * メソッド名: setData<br>
   * 試験名: setDataメソッドが正しく動作することを確認する<br>
   * 条件: setDataメソッドでリストを設定し、getDataメソッドで取得する<br>
   * 結果: 設定したリストが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetData_Normal() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    List<AircraftReserveInfoDetailResponseDto> dataList = new ArrayList<>();
    dto.setData(dataList);
    assertEquals(dataList, dto.getData(), "設定したリストが正しく取得できること");
  }

  /**
   * メソッド名: setData<br>
   * 試験名: setDataメソッドがnullを設定できることを確認する<br>
   * 条件: setDataメソッドでnullを設定し、getDataメソッドで取得する<br>
   * 結果: nullが正しく取得できること<br>
   * テストパターン：境界値テスト<br>
   */
  @Test
  public void testSetData_Null() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    dto.setData(null);
    assertNull(dto.getData(), "nullが正しく取得できること");
  }

  /**
   * メソッド名: setData<br>
   * 試験名: setDataメソッドが空のリストを設定できることを確認する<br>
   * 条件: setDataメソッドで空のリストを設定し、getDataメソッドで取得する<br>
   * 結果: 空のリストが正しく取得できること<br>
   * テストパターン：エッジケーステスト<br>
   */
  @Test
  public void testSetData_EmptyList() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    List<AircraftReserveInfoDetailResponseDto> emptyList = new ArrayList<>();
    dto.setData(emptyList);
    assertTrue(dto.getData().isEmpty(), "空のリストが正しく取得できること");
  }

  /**
   * メソッド名: setData<br>
   * 試験名: setDataメソッドが大きなリストを設定できることを確認する<br>
   * 条件: setDataメソッドで大きなリストを設定し、getDataメソッドで取得する<br>
   * 結果: 大きなリストが正しく取得できること<br>
   * テストパターン：エッジケーステスト<br>
   */
  @Test
  public void testSetData_LargeList() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    List<AircraftReserveInfoDetailResponseDto> largeList = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      largeList.add(new AircraftReserveInfoDetailResponseDto());
    }
    dto.setData(largeList);
    assertEquals(largeList, dto.getData(), "大きなリストが正しく取得できること");
  }

  /**
   * メソッド名: getPerPage<br>
   * 試験名: getPerPageメソッドが正しく動作することを確認する<br>
   * 条件: デフォルトコンストラクタで初期化し、getPerPageメソッドを呼び出す<br>
   * 結果: 初期値としてnullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetPerPage_DefaultIsNull() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    assertNull(dto.getPerPage());
  }

  /**
   * メソッド名: setPerPage<br>
   * 試験名: setPerPageメソッドが正しく動作することを確認する<br>
   * 条件: setPerPageメソッドでリストを設定し、getPerPageメソッドで取得する<br>
   * 結果: 設定したリストが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetPerPage_Normal() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    Integer perPage = 1;
    dto.setPerPage(perPage);
    assertEquals(perPage, dto.getPerPage());
  }

  /**
   * メソッド名: setPerPage<br>
   * 試験名: setPerPageソッドがnullを設定できることを確認する<br>
   * 条件: setPerPageメソッドでnullを設定し、getPerPageメソッドで取得する<br>
   * 結果: nullが正しく取得できること<br>
   * テストパターン：境界値テスト<br>
   */
  @Test
  public void testSetPerPage_Null() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    dto.setPerPage(null);
    assertNull(dto.getPerPage());
  }

  /**
   * メソッド名: getCurrentPage<br>
   * 試験名: getCurrentPageメソッドが正しく動作することを確認する<br>
   * 条件: デフォルトコンストラクタで初期化し、getCurrentPageメソッドを呼び出す<br>
   * 結果: 初期値としてnullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetCurrentPage_DefaultIsNull() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    assertNull(dto.getCurrentPage());
  }

  /**
   * メソッド名: setCurrentPage<br>
   * 試験名: setCurrentPageメソッドが正しく動作することを確認する<br>
   * 条件: setCurrentPageメソッドでリストを設定し、getCurrentPageメソッドで取得する<br>
   * 結果: 設定したリストが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetCurrentPage_Normal() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    Integer currentPage = 1;
    dto.setCurrentPage(currentPage);
    assertEquals(currentPage, dto.getCurrentPage());
  }

  /**
   * メソッド名: setCurrentPage<br>
   * 試験名: setCurrentPageソッドがnullを設定できることを確認する<br>
   * 条件: setCurrentPageメソッドでnullを設定し、getCurrentPageメソッドで取得する<br>
   * 結果: nullが正しく取得できること<br>
   * テストパターン：境界値テスト<br>
   */
  @Test
  public void testSetCurrentPage_Null() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    dto.setCurrentPage(null);
    assertNull(dto.getCurrentPage());
  }

  /**
   * メソッド名: getlastPage<br>
   * 試験名: getlastPageメソッドが正しく動作することを確認する<br>
   * 条件: デフォルトコンストラクタで初期化し、getlastPageメソッドを呼び出す<br>
   * 結果: 初期値としてnullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetlastPage_DefaultIsNull() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    assertNull(dto.getLastPage());
  }

  /**
   * メソッド名: setLastPage<br>
   * 試験名: setLastPageメソッドが正しく動作することを確認する<br>
   * 条件: setLastPageメソッドでリストを設定し、getLastPageメソッドで取得する<br>
   * 結果: 設定したリストが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetLastPage_Normal() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    Integer lastPage = 1;
    dto.setLastPage(lastPage);
    assertEquals(lastPage, dto.getLastPage());
  }

  /**
   * メソッド名: setLastPage<br>
   * 試験名: setLastPageソッドがnullを設定できることを確認する<br>
   * 条件: setLastPageメソッドでnullを設定し、getLastPageメソッドで取得する<br>
   * 結果: nullが正しく取得できること<br>
   * テストパターン：境界値テスト<br>
   */
  @Test
  public void testSetLastPage_Null() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    dto.setLastPage(null);
    assertNull(dto.getLastPage());
  }

  /**
   * メソッド名: getTotal<br>
   * 試験名: getTotalメソッドが正しく動作することを確認する<br>
   * 条件: デフォルトコンストラクタで初期化し、getTotalメソッドを呼び出す<br>
   * 結果: 初期値としてnullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetTotal_DefaultIsNull() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    assertNull(dto.getTotal());
  }

  /**
   * メソッド名: setTotal<br>
   * 試験名: setTotalメソッドが正しく動作することを確認する<br>
   * 条件: setTotalメソッドでリストを設定し、getTotalメソッドで取得する<br>
   * 結果: 設定したリストが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetTotal_Normal() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    Integer total = 1;
    dto.setTotal(total);
    assertEquals(total, dto.getTotal());
  }

  /**
   * メソッド名: setTotal<br>
   * 試験名: setTotalソッドがnullを設定できることを確認する<br>
   * 条件: setTotalメソッドでnullを設定し、getTotalメソッドで取得する<br>
   * 結果: nullが正しく取得できること<br>
   * テストパターン：境界値テスト<br>
   */
  @Test
  public void testSetTotal_Null() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    dto.setTotal(null);
    assertNull(dto.getTotal());
  }

  /**
   * メソッド名: toString<br>
   * 試験名: toStringメソッドが正しく動作することを確認する<br>
   * 条件: 全ての項目に値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 設定値が含まれる文字列が返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testToString() {
    AircraftReserveInfoListResponseDto dto = new AircraftReserveInfoListResponseDto();
    List<AircraftReserveInfoDetailResponseDto> data = new ArrayList<>();
    AircraftReserveInfoDetailResponseDto detailDto = new AircraftReserveInfoDetailResponseDto();
    detailDto.setAircraftId("aircraftId");
    detailDto.setAircraftReservationId("aircraftReservationId");
    detailDto.setGroupReservationId("groupReservationId");
    Timestamp timestamp = Timestamp.valueOf("2023-01-01 12:00:00");
    detailDto.setReservationTimeFrom(timestamp.toString());
    detailDto.setReservationTimeTo(timestamp.toString());
    detailDto.setAircraftName("aircraftName");
    detailDto.setReserveProviderId("reserveProviderId");
    detailDto.setOperatorId("operator123");
    data.add(detailDto);
    dto.setData(data);
    dto.setPerPage(1);
    dto.setCurrentPage(1);
    dto.setLastPage(1);
    dto.setTotal(1);

    String expected =
        "AircraftReserveInfoListResponseDto(data=[AircraftReserveInfoDetailResponseDto(aircraftReservationId=aircraftReservationId, groupReservationId=groupReservationId, aircraftId=aircraftId, reservationTimeFrom=2023-01-01 12:00:00.0, reservationTimeTo=2023-01-01 12:00:00.0, aircraftName=aircraftName, reserveProviderId=reserveProviderId, operatorId=operator123)], perPage=1, currentPage=1, lastPage=1, total=1)";
    assertEquals(expected, dto.toString());
  }
}
