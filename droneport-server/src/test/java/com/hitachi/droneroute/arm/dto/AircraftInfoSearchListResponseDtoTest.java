package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** AircraftInfoSearchListResponseDtoのテストクラス. */
public class AircraftInfoSearchListResponseDtoTest {

  private AircraftInfoSearchListResponseDto dto;

  @BeforeEach
  public void setUp() {
    dto = new AircraftInfoSearchListResponseDto();
  }

  /**
   * メソッド名: getData<br>
   * 試験名: デフォルトのデータがnullであることを確認する<br>
   * 条件: デフォルトコンストラクタを使用してインスタンスを生成する<br>
   * 結果: デフォルトのデータがnullであること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetData_DefaultIsNull() {
    assertNull(dto.getData());
  }

  /**
   * メソッド名: setData<br>
   * 試験名: データを設定し、正しく取得できることを確認する<br>
   * 条件: データを設定し、getterで取得する<br>
   * 結果: 設定したデータが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetData() {
    List<AircraftInfoSearchListElement> data = new ArrayList<>();
    dto.setData(data);
    assertEquals(data, dto.getData());
  }

  /**
   * メソッド名: setData<br>
   * 試験名: nullを設定し、正しく取得できることを確認する<br>
   * 条件: nullを設定し、getterで取得する<br>
   * 結果: 設定したnullが正しく取得できること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetData_Null() {
    dto.setData(null);
    assertNull(dto.getData());
  }

  /**
   * メソッド名: setData<br>
   * 試験名: 空のリストを設定し、正しく取得できることを確認する<br>
   * 条件: 空のリストを設定し、getterで取得する<br>
   * 結果: 設定した空のリストが正しく取得できること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetData_EmptyList() {
    List<AircraftInfoSearchListElement> data = new ArrayList<>();
    dto.setData(data);
    assertNotNull(dto.getData());
    assertEquals(0, dto.getData().size());
  }

  /**
   * メソッド名: setData<br>
   * 試験名: データを設定し、正しく取得できることを確認する<br>
   * 条件: データを設定し、getterで取得する<br>
   * 結果: 設定したデータが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetData_WithElements() {
    List<AircraftInfoSearchListElement> data = new ArrayList<>();
    data.add(new AircraftInfoSearchListElement());
    dto.setData(data);
    assertNotNull(dto.getData());
    assertEquals(1, dto.getData().size());
  }

  /**
   * メソッド名: getPerPage<br>
   * 試験名: デフォルトのデータがnullであることを確認する<br>
   * 条件: デフォルトコンストラクタを使用してインスタンスを生成する<br>
   * 結果: デフォルトのデータがnullであること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetPerPage_DefaultIsNull() {
    assertNull(dto.getPerPage());
  }

  /**
   * メソッド名: setPerPage<br>
   * 試験名: データを設定し、正しく取得できることを確認する<br>
   * 条件: データを設定し、getterで取得する<br>
   * 結果: 設定したデータが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetPerPage() {
    Integer perPage = 1;
    dto.setPerPage(perPage);
    assertEquals(perPage, dto.getPerPage());
  }

  /**
   * メソッド名: setPerPage<br>
   * 試験名: nullを設定し、正しく取得できることを確認する<br>
   * 条件: nullを設定し、getterで取得する<br>
   * 結果: 設定したnullが正しく取得できること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetPerPage_Null() {
    dto.setPerPage(null);
    assertNull(dto.getPerPage());
  }

  /**
   * メソッド名: getCurrentPage<br>
   * 試験名: デフォルトのデータがnullであることを確認する<br>
   * 条件: デフォルトコンストラクタを使用してインスタンスを生成する<br>
   * 結果: デフォルトのデータがnullであること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetCurrentPage_DefaultIsNull() {
    assertNull(dto.getCurrentPage());
  }

  /**
   * メソッド名: setCurrentPage<br>
   * 試験名: データを設定し、正しく取得できることを確認する<br>
   * 条件: データを設定し、getterで取得する<br>
   * 結果: 設定したデータが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetCurrentPage() {
    Integer correntPage = 1;
    dto.setCurrentPage(correntPage);
    assertEquals(correntPage, dto.getCurrentPage());
  }

  /**
   * メソッド名: setCurrentPage<br>
   * 試験名: nullを設定し、正しく取得できることを確認する<br>
   * 条件: nullを設定し、getterで取得する<br>
   * 結果: 設定したnullが正しく取得できること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetCurrentPage_Null() {
    dto.setCurrentPage(null);
    assertNull(dto.getCurrentPage());
  }

  /**
   * メソッド名: getlastPage<br>
   * 試験名: デフォルトのデータがnullであることを確認する<br>
   * 条件: デフォルトコンストラクタを使用してインスタンスを生成する<br>
   * 結果: デフォルトのデータがnullであること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetlastPage_DefaultIsNull() {
    assertNull(dto.getLastPage());
  }

  /**
   * メソッド名: setLastPage<br>
   * 試験名: データを設定し、正しく取得できることを確認する<br>
   * 条件: データを設定し、getterで取得する<br>
   * 結果: 設定したデータが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetLastPage() {
    Integer lastPage = 1;
    dto.setLastPage(lastPage);
    assertEquals(lastPage, dto.getLastPage());
  }

  /**
   * メソッド名: setLastPage<br>
   * 試験名: nullを設定し、正しく取得できることを確認する<br>
   * 条件: nullを設定し、getterで取得する<br>
   * 結果: 設定したnullが正しく取得できること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetLastPage_Null() {
    dto.setLastPage(null);
    assertNull(dto.getCurrentPage());
  }

  /**
   * メソッド名: getTotal<br>
   * 試験名: デフォルトのデータがnullであることを確認する<br>
   * 条件: デフォルトコンストラクタを使用してインスタンスを生成する<br>
   * 結果: デフォルトのデータがnullであること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetTotal_DefaultIsNull() {
    assertNull(dto.getTotal());
  }

  /**
   * メソッド名: setTotal<br>
   * 試験名: データを設定し、正しく取得できることを確認する<br>
   * 条件: データを設定し、getterで取得する<br>
   * 結果: 設定したデータが正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetTotal() {
    Integer total = 1;
    dto.setTotal(total);
    assertEquals(total, dto.getTotal());
  }

  /**
   * メソッド名: setTotal<br>
   * 試験名: nullを設定し、正しく取得できることを確認する<br>
   * 条件: nullを設定し、getterで取得する<br>
   * 結果: 設定したnullが正しく取得できること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  public void testSetTotal_Null() {
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
  void testToString() {
    List<AircraftInfoSearchListElement> data = new ArrayList<>();
    AircraftInfoSearchListElement detailDto = new AircraftInfoSearchListElement();
    detailDto.setAircraftId("aircraftId");
    detailDto.setAircraftName("aircraftName");
    detailDto.setAircraftType(1);
    detailDto.setBodyWeight(10.0);
    detailDto.setCertification(true);
    detailDto.setDipsRegistrationCode("123456");
    detailDto.setManufacturer("manufacturer");
    detailDto.setManufacturingNumber("123456");
    detailDto.setMaxFlightSpeed(50.0);
    detailDto.setMaxFlightTime(3.0);
    detailDto.setLat(38.0);
    detailDto.setLon(140.0);
    detailDto.setMaxTakeoffWeight(10.0);
    detailDto.setOwnerId("ownerId");
    detailDto.setOperatorId("operator123");
    detailDto.setOwnerType(1);
    data.add(detailDto);
    dto.setData(data);
    dto.setPerPage(1);
    dto.setCurrentPage(1);
    dto.setLastPage(1);
    dto.setTotal(1);

    String expected =
        "AircraftInfoSearchListResponseDto(data=[AircraftInfoSearchListElement(aircraftId=aircraftId, aircraftName=aircraftName, manufacturer=manufacturer, modelNumber=null, modelName=null, manufacturingNumber=123456, aircraftType=1, maxTakeoffWeight=10.0, bodyWeight=10.0, maxFlightSpeed=50.0, maxFlightTime=3.0, certification=true, lat=38.0, lon=140.0, dipsRegistrationCode=123456, ownerType=1, ownerId=ownerId, operatorId=operator123, publicFlag=null, payloadInfos=null, priceInfos=null)], perPage=1, currentPage=1, lastPage=1, total=1)";
    assertEquals(expected, dto.toString());
  }
}
