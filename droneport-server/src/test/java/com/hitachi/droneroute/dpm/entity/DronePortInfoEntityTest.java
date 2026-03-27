package com.hitachi.droneroute.dpm.entity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * メソッド名: DronePortInfoEntityTest<br>
 * 試験名: DronePortInfoEntityクラスの各メソッドの動作確認<br>
 */
class DronePortInfoEntityTest {

  private DronePortInfoEntity entity;

  @BeforeEach
  void setUp() {
    entity = new DronePortInfoEntity();
  }

  /**
   * メソッド名: getDronePortId, setDronePortId<br>
   * 試験名: DronePortIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでUUIDを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortId() {
    String expected = UUID.randomUUID().toString();
    entity.setDronePortId(expected);
    assertEquals(expected, entity.getDronePortId());
  }

  /**
   * メソッド名: getDronePortName, setDronePortName<br>
   * 試験名: DronePortNameの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortName() {
    String expected = "TestPort";
    entity.setDronePortName(expected);
    assertEquals(expected, entity.getDronePortName());
  }

  /**
   * メソッド名: getAddress, setAddress<br>
   * 試験名: Addressの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetAddress() {
    String expected = "Test Address";
    entity.setAddress(expected);
    assertEquals(expected, entity.getAddress());
  }

  /**
   * メソッド名: getManufacturer, setManufacturer<br>
   * 試験名: Manufacturerの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetManufacturer() {
    String expected = "Test Manufacturer";
    entity.setManufacturer(expected);
    assertEquals(expected, entity.getManufacturer());
  }

  /**
   * メソッド名: getSerialNumber, setSerialNumber<br>
   * 試験名: SerialNumberの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetSerialNumber() {
    String expected = "123456789";
    entity.setSerialNumber(expected);
    assertEquals(expected, entity.getSerialNumber());
  }

  /**
   * メソッド名: getPortType, setPortType<br>
   * 試験名: PortTypeの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでIntegerを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetPortType() {
    Integer expected = 2;
    entity.setPortType(expected);
    assertEquals(expected, entity.getPortType());
  }

  /**
   * メソッド名: getLat, setLat<br>
   * 試験名: Latの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでDoubleを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetLat() {
    Double expected = 35.6895;
    entity.setLat(expected);
    assertEquals(expected, entity.getLat());
  }

  /**
   * メソッド名: getLon, setLon<br>
   * 試験名: Lonの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでDoubleを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetLon() {
    Double expected = 139.6917;
    entity.setLon(expected);
    assertEquals(expected, entity.getLon());
  }

  /**
   * メソッド名: getAlt, setAlt<br>
   * 試験名: Altの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでDoubleを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetAlt() {
    Double expected = 50.0;
    entity.setAlt(expected);
    assertEquals(expected, entity.getAlt());
  }

  /**
   * メソッド名: getSupportDroneType, setSupportDroneType<br>
   * 試験名: SupportDroneTypeの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetSupportDroneType() {
    String expected = "TypeA";
    entity.setSupportDroneType(expected);
    assertEquals(expected, entity.getSupportDroneType());
  }

  /**
   * メソッド名: getImageBinary, setImageBinary<br>
   * 試験名: ImageBinaryの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでbyte[]を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetImageBinary() {
    byte[] expected = new byte[] {1, 2, 3};
    entity.setImageBinary(expected);
    assertArrayEquals(expected, entity.getImageBinary());
  }

  /**
   * メソッド名: getOperatorId, setOperatorId<br>
   * 試験名: OperatorIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetOperatorId() {
    String expected = "creator";
    entity.setOperatorId(expected);
    assertEquals(expected, entity.getOperatorId());
  }

  /**
   * メソッド名: getUpdateUserId, setUpdateUserId<br>
   * 試験名: UpdateUserIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetUpdateUserId() {
    String expected = "updater";
    entity.setUpdateUserId(expected);
    assertEquals(expected, entity.getUpdateUserId());
  }

  /**
   * メソッド名: getCreateTime, setCreateTime<br>
   * 試験名: CreateTimeの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでTimestampを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetCreateTime() {
    Timestamp expected = new Timestamp(System.currentTimeMillis());
    entity.setCreateTime(expected);
    assertEquals(expected, entity.getCreateTime());
  }

  /**
   * メソッド名: getUpdateTime, setUpdateTime<br>
   * 試験名: UpdateTimeの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでTimestampを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetUpdateTime() {
    Timestamp expected = new Timestamp(System.currentTimeMillis());
    entity.setUpdateTime(expected);
    assertEquals(expected, entity.getUpdateTime());
  }

  /**
   * メソッド名: getDeleteFlag, setDeleteFlag<br>
   * 試験名: DeleteFlagの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでBooleanを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDeleteFlag() {
    Boolean expected = true;
    entity.setDeleteFlag(expected);
    assertEquals(expected, entity.getDeleteFlag());
  }

  /**
   * メソッド名: setLat<br>
   * 試験名: Latの設定時に境界値を確認する<br>
   * 条件: setterで緯度の最小値、最大値を設定する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void testSetLat_Boundary() {
    Double minLat = -90.0;
    Double maxLat = 90.0;
    entity.setLat(minLat);
    assertEquals(minLat, entity.getLat());
    entity.setLat(maxLat);
    assertEquals(maxLat, entity.getLat());
  }

  /**
   * メソッド名: setLon<br>
   * 試験名: Lonの設定時に境界値を確認する<br>
   * 条件: setterで経度の最小値、最大値を設定する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void testSetLon_Boundary() {
    Double minLon = -180.0;
    Double maxLon = 180.0;
    entity.setLon(minLon);
    assertEquals(minLon, entity.getLon());
    entity.setLon(maxLon);
    assertEquals(maxLon, entity.getLon());
  }

  /**
   * メソッド名: getImageFormat, setImageFormat<br>
   * 試験名: ImageFormatの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetImageFormat() {
    String expected = "JPEG";
    entity.setImageFormat(expected);
    assertEquals(expected, entity.getImageFormat());
  }

  /**
   * メソッド名: getVisDronePortCompanyId, setVisDronePortCompanyId<br>
   * 試験名: VisDronePortCompanyIdの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetVisDronePortCompanyId() {
    String expected = "Company123";
    entity.setVisDronePortCompanyId(expected);
    assertEquals(expected, entity.getVisDronePortCompanyId());
  }

  /**
   * メソッド名: getDronePortStatusEntity, setDronePortStatusEntity<br>
   * 試験名: DronePortStatusEntityの設定と取得が正しく行われることを確認する<br>
   * 条件: setterでDronePortStatusEntityを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortStatusEntity() {
    DronePortStatusEntity expected = new DronePortStatusEntity();
    entity.setDronePortStatusEntity(expected);
    assertEquals(expected, entity.getDronePortStatusEntity());
  }
}
