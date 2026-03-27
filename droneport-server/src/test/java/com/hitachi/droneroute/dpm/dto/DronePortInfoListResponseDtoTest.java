package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * DronePortInfoListResponseDtoTest<br>
 * <br>
 * テストクラス: DronePortInfoListResponseDto<br>
 */
public class DronePortInfoListResponseDtoTest {

  /**
   * メソッド名: getData_setData<br>
   * 試験名: dataフィールドの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getData_setData() {
    // ●準備
    DronePortInfoListResponseDto dto = new DronePortInfoListResponseDto();
    List<DronePortInfoListResponseElement> expectedData = new ArrayList<>();
    expectedData.add(new DronePortInfoListResponseElement());

    // ●実行
    dto.setData(expectedData);
    List<DronePortInfoListResponseElement> actualData = dto.getData();

    // ●検証
    assertEquals(expectedData, actualData);
  }

  /**
   * メソッド名: setData_null<br>
   * 試験名: dataフィールドにnullを設定した場合の動作を確認する<br>
   * 条件: setterでnullを設定し、getterで取得する<br>
   * 結果: 取得値がnullであること<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void setData_null() {
    // ●準備
    DronePortInfoListResponseDto dto = new DronePortInfoListResponseDto();

    // ●実行
    dto.setData(null);
    List<DronePortInfoListResponseElement> actualData = dto.getData();

    // ●検証
    assertEquals(null, actualData);
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
    DronePortInfoListResponseDto dto = new DronePortInfoListResponseDto();
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
    DronePortInfoListResponseDto dto = new DronePortInfoListResponseDto();
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
    DronePortInfoListResponseDto dto = new DronePortInfoListResponseDto();
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
    DronePortInfoListResponseDto dto = new DronePortInfoListResponseDto();
    Integer expectedTotal = 50;

    // ●実行
    dto.setTotal(expectedTotal);
    Integer actualTotal = dto.getTotal();

    // ●検証
    assertEquals(expectedTotal, actualTotal);
  }

  /**
   * メソッド名: toStringTest<br>
   * 試験名: toStringメソッドの動作を確認する<br>
   * 条件: dataフィールドに値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 期待される文字列が返されること<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void toStringTest() {
    // ●準備
    DronePortInfoListResponseDto dto = new DronePortInfoListResponseDto();
    List<DronePortInfoListResponseElement> data = new ArrayList<>();
    data.add(newItem());
    dto.setData(data);
    dto.setPerPage(10);
    dto.setCurrentPage(3);
    dto.setLastPage(15);
    dto.setTotal(99);

    // ●実行
    String actualString = dto.toString();

    // ●検証
    String expectedString =
        "DronePortInfoListResponseDto(data=["
            + "DronePortInfoListResponseElement(dronePortId=DP001, dronePortName=Port A, address=123 Main St, manufacturer=Hitachi, serialNumber=SN123456, portType=1, visDronePortCompanyId=dummyCompanyId, storedAircraftId=dummyStoredAircraftId, lat=35.6895, lon=139.6917, alt=100.1, supportDroneType=Type A, activeStatus=99, scheduledStatus=98, inactiveTimeFrom=2023-10-01T10:00:00, inactiveTimeTo=2023-10-01T12:00:00, operatorId=operator123, publicFlag=null, updateTime=2023-10-01T15:00:00, priceInfos=null)"
            + "], perPage=10, currentPage=3, lastPage=15, total=99)";
    assertEquals(expectedString, actualString);
  }

  DronePortInfoListResponseElement newItem() {
    DronePortInfoListResponseElement dto = new DronePortInfoListResponseElement();
    dto.setDronePortId("DP001");
    dto.setDronePortName("Port A");
    dto.setAddress("123 Main St");
    dto.setManufacturer("Hitachi");
    dto.setSerialNumber("SN123456");
    dto.setPortType(1);
    dto.setLat(35.6895);
    dto.setLon(139.6917);
    dto.setAlt(100.1);
    dto.setSupportDroneType("Type A");
    dto.setActiveStatus(99);
    dto.setScheduledStatus(98);
    dto.setVisDronePortCompanyId("dummyCompanyId");
    dto.setStoredAircraftId("dummyStoredAircraftId");
    dto.setInactiveTimeFrom("2023-10-01T10:00:00");
    dto.setInactiveTimeTo("2023-10-01T12:00:00");
    dto.setOperatorId("operator123");
    dto.setUpdateTime("2023-10-01T15:00:00");
    return dto;
  }
}
