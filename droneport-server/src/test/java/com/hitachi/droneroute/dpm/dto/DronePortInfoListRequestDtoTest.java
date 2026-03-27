package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * クラス名: DronePortInfoListRequestDtoTest<br>
 * 対象クラス: DronePortInfoListRequestDto
 */
class DronePortInfoListRequestDtoTest {

  /**
   * メソッド名: getDronePortName_setDronePortName<br>
   * 試験名: getDronePortNameとsetDronePortNameの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getDronePortName_setDronePortName() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "PortNameA";
    dto.setDronePortName(expected);
    assertEquals(expected, dto.getDronePortName());
  }

  /**
   * メソッド名: getAddress_setAddress<br>
   * 試験名: getAddressとsetAddressの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getAddress_setAddress() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "Tokyo";
    dto.setAddress(expected);
    assertEquals(expected, dto.getAddress());
  }

  /**
   * メソッド名: getManufacturer_setManufacturer<br>
   * 試験名: getManufacturerとsetManufacturerの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getManufacturer_setManufacturer() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "Hitachi";
    dto.setManufacturer(expected);
    assertEquals(expected, dto.getManufacturer());
  }

  /**
   * メソッド名: getSerialNumber_setSerialNumber<br>
   * 試験名: getSerialNumberとsetSerialNumberの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getSerialNumber_setSerialNumber() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "SN123456";
    dto.setSerialNumber(expected);
    assertEquals(expected, dto.getSerialNumber());
  }

  /**
   * メソッド名: getPortType_setPortType<br>
   * 試験名: getPortTypeとsetPortTypeの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getPortType_setPortType() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "TypeB";
    dto.setPortType(expected);
    assertEquals(expected, dto.getPortType());
  }

  /**
   * メソッド名: getMinLat_setMinLat<br>
   * 試験名: getMinLatとsetMinLatの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getMinLat_setMinLat() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    Double expected = 35.0;
    dto.setMinLat(expected);
    assertEquals(expected, dto.getMinLat());
  }

  /**
   * メソッド名: getMinLon_setMinLon<br>
   * 試験名: getMinLonとsetMinLonの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getMinLon_setMinLon() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    Double expected = 139.0;
    dto.setMinLon(expected);
    assertEquals(expected, dto.getMinLon());
  }

  /**
   * メソッド名: getMaxLat_setMaxLat<br>
   * 試験名: getMaxLatとsetMaxLatの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getMaxLat_setMaxLat() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    Double expected = 36.0;
    dto.setMaxLat(expected);
    assertEquals(expected, dto.getMaxLat());
  }

  /**
   * メソッド名: getMaxLon_setMaxLon<br>
   * 試験名: getMaxLonとsetMaxLonの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getMaxLon_setMaxLon() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    Double expected = 140.0;
    dto.setMaxLon(expected);
    assertEquals(expected, dto.getMaxLon());
  }

  /**
   * メソッド名: getSupportDroneType_setSupportDroneType<br>
   * 試験名: getSupportDroneTypeとsetSupportDroneTypeの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getSupportDroneType_setSupportDroneType() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "DroneTypeA";
    dto.setSupportDroneType(expected);
    assertEquals(expected, dto.getSupportDroneType());
  }

  /**
   * メソッド名: getActiveStatus_setActiveStatus<br>
   * 試験名: getActiveStatusとsetActiveStatusの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getActiveStatus_setActiveStatus() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "Active";
    dto.setActiveStatus(expected);
    assertEquals(expected, dto.getActiveStatus());
  }

  /**
   * メソッド名: getPerPage_setPerPage<br>
   * 試験名: getPerPageとsetPerPageの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getPerPage_setPerPage() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "10";
    dto.setPerPage(expected);
    assertEquals(expected, dto.getPerPage());
  }

  /**
   * メソッド名: getPage_setPage<br>
   * 試験名: getPageとsetPageの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getPage_setPage() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "1";
    dto.setPage(expected);
    assertEquals(expected, dto.getPage());
  }

  /**
   * メソッド名: getSortOrders_setSortOrders<br>
   * 試験名: getSortOrdersとsetSortOrdersの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getSortOrders_setSortOrders() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "asc";
    dto.setSortOrders(expected);
    assertEquals(expected, dto.getSortOrders());
  }

  /**
   * メソッド名: getSortColumns_setSortColumns<br>
   * 試験名: getSortColumnsとsetSortColumnsの動作確認<br>
   * 条件: setterで値を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getSortColumns_setSortColumns() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    String expected = "dronePortName";
    dto.setSortColumns(expected);
    assertEquals(expected, dto.getSortColumns());
  }

  /**
   * メソッド名: toStringTest<br>
   * 試験名: toStringメソッドの動作確認<br>
   * 条件: 全ての項目に値を設定し、toStringでオブジェクトの内容を取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void toStringTest() {
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setDronePortName("PortNameA");
    dto.setAddress("Tokyo");
    dto.setManufacturer("Hitachi");
    dto.setSerialNumber("SN123456");
    dto.setPortType("TypeB");
    dto.setMinLat(35.0);
    dto.setMinLon(139.0);
    dto.setMaxLat(36.0);
    dto.setMaxLon(140.0);
    dto.setSupportDroneType("DroneTypeA");
    dto.setActiveStatus("Active");
    dto.setPerPage("10");
    dto.setPage("1");
    dto.setSortOrders("asc");
    dto.setSortColumns("dronePortName");
    dto.setPublicFlag("true");

    String expected =
        "DronePortInfoListRequestDto(dronePortName=PortNameA, address=Tokyo, manufacturer=Hitachi, serialNumber=SN123456, portType=TypeB, minLat=35.0, minLon=139.0, maxLat=36.0, maxLon=140.0, supportDroneType=DroneTypeA, activeStatus=Active, perPage=10, page=1, sortOrders=asc, sortColumns=dronePortName, isRequiredPriceInfo=null, publicFlag=true)";
    assertEquals(expected, dto.toString());
  }
}
