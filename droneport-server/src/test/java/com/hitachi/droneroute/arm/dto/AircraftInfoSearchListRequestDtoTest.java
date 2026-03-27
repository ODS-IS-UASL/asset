package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** AircraftInfoSearchListRequestDtoのテストクラス. */
public class AircraftInfoSearchListRequestDtoTest {

  private AircraftInfoSearchListRequestDto dto;

  @BeforeEach
  public void setUp() {
    dto = new AircraftInfoSearchListRequestDto();
  }

  /**
   * メソッド名: getAircraftName<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetAircraftName_initialValue() {
    assertNull(dto.getAircraftName());
  }

  /**
   * メソッド名: setAircraftName<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetAircraftName() {
    String aircraftName = "Test Aircraft";
    dto.setAircraftName(aircraftName);
    assertEquals(aircraftName, dto.getAircraftName());
  }

  /**
   * メソッド名: getManufacturer<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetManufacturer_initialValue() {
    assertNull(dto.getManufacturer());
  }

  /**
   * メソッド名: setManufacturer<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetManufacturer() {
    String manufacturer = "Test Manufacturer";
    dto.setManufacturer(manufacturer);
    assertEquals(manufacturer, dto.getManufacturer());
  }

  /**
   * メソッド名: getManufacturingNumber<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetManufacturingNumber_initialValue() {
    assertNull(dto.getManufacturingNumber());
  }

  /**
   * メソッド名: setManufacturingNumber<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetManufacturingNumber() {
    String manufacturingNumber = "123456";
    dto.setManufacturingNumber(manufacturingNumber);
    assertEquals(manufacturingNumber, dto.getManufacturingNumber());
  }

  /**
   * メソッド名: getAircraftType<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetAircraftType_initialValue() {
    assertNull(dto.getAircraftType());
  }

  /**
   * メソッド名: setAircraftType<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetAircraftType() {
    String aircraftType = "1";
    dto.setAircraftType(aircraftType);
    assertEquals(aircraftType, dto.getAircraftType());
  }

  /**
   * メソッド名: getCertification<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetCertification_initialValue() {
    assertNull(dto.getCertification());
  }

  /**
   * メソッド名: setCertification<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetCertification() {
    Boolean certification = true;
    dto.setCertification(String.valueOf(certification));
    assertEquals(String.valueOf(certification), dto.getCertification());
  }

  /**
   * メソッド名: getDipsRegistrationCode<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetDipsRegistrationCode_initialValue() {
    assertNull(dto.getDipsRegistrationCode());
  }

  /**
   * メソッド名: setDipsRegistrationCode<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetDipsRegistrationCode() {
    String dipsRegistrationCode = "DIPS123";
    dto.setDipsRegistrationCode(dipsRegistrationCode);
    assertEquals(dipsRegistrationCode, dto.getDipsRegistrationCode());
  }

  /**
   * メソッド名: getOwnerType<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetOwnerType_initialValue() {
    assertNull(dto.getOwnerType());
  }

  /**
   * メソッド名: setOwnerType<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetOwnerType() {
    String ownerType = "2";
    dto.setOwnerType(ownerType);
    assertEquals(ownerType, dto.getOwnerType());
  }

  /**
   * メソッド名: getOwnerId<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetOwnerId_initialValue() {
    assertNull(dto.getOwnerId());
  }

  /**
   * メソッド名: setOwnerId<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetOwnerId() {
    String ownerId = "Owner123";
    dto.setOwnerId(ownerId);
    assertEquals(ownerId, dto.getOwnerId());
  }

  /**
   * メソッド名: getPerPage<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetPerPage_initialValue() {
    assertNull(dto.getPerPage());
  }

  /**
   * メソッド名: setPerPage<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetPerPage() {
    String perPage = "1";
    dto.setPerPage(perPage);
    assertEquals(perPage, dto.getPerPage());
  }

  /**
   * メソッド名: getPage<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetPage_initialValue() {
    assertNull(dto.getPage());
  }

  /**
   * メソッド名: setPage<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetPage() {
    String page = "1";
    dto.setPage(page);
    assertEquals(page, dto.getPage());
  }

  /**
   * メソッド名: getSortOrders<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetSortOrders_initialValue() {
    assertNull(dto.getSortOrders());
  }

  /**
   * メソッド名: setSortOrders<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetSortOrders() {
    String sortOrders = "1";
    dto.setSortOrders(sortOrders);
    assertEquals(sortOrders, dto.getSortOrders());
  }

  /**
   * メソッド名: getSortColumns<br>
   * 試験名: 初期値がnullであることを確認する<br>
   * 条件: 初期化直後のインスタンスに対してgetterを呼び出す<br>
   * 結果: nullが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetSortColumns_initialValue() {
    assertNull(dto.getSortColumns());
  }

  /**
   * メソッド名: setSortColumns<br>
   * 試験名: 値が正しく設定されることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSetSortColumns() {
    String sortColumns = "1";
    dto.setSortColumns(sortColumns);
    assertEquals(sortColumns, dto.getSortColumns());
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

    dto.setAircraftName("aircraftName");
    dto.setAircraftType("1");
    dto.setCertification("true");
    dto.setDipsRegistrationCode("12345");
    dto.setManufacturer("manufacturer");
    dto.setManufacturingNumber("12345");
    dto.setOwnerId("ownerId");
    dto.setOwnerType("1");
    dto.setMinLat(30.0);
    dto.setMinLon(130.0);
    dto.setMaxLat(40.0);
    dto.setMaxLon(140.0);
    dto.setPerPage("1");
    dto.setPage("1");
    dto.setSortOrders("1");
    dto.setSortColumns("aircraftId");

    String expected =
        "AircraftInfoSearchListRequestDto(aircraftName=aircraftName, manufacturer=manufacturer, modelNumber=null, modelName=null, manufacturingNumber=12345, aircraftType=1, certification=true, dipsRegistrationCode=12345, ownerType=1, ownerId=ownerId, minLat=30.0, minLon=130.0, maxLat=40.0, maxLon=140.0, perPage=1, page=1, publicFlag=null, sortOrders=1, sortColumns=aircraftId, modelInfos=null, isRequiredPayloadInfo=null, isRequiredPriceInfo=null)";
    assertEquals(expected, dto.toString());
  }
}
