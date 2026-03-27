package com.hitachi.droneroute.arm.entity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** AircraftInfoEntityのテストクラス */
class AircraftInfoEntityTest {

  private AircraftInfoEntity aircraftInfoEntity;

  @BeforeEach
  void setUp() {
    aircraftInfoEntity = new AircraftInfoEntity();
  }

  /**
   * メソッド名: getAircraftId<br>
   * 試験名: AircraftIdの取得が正しく行われることを確認する<br>
   * 条件: AircraftIdを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftId() {
    UUID aircraftId = UUID.randomUUID();
    aircraftInfoEntity.setAircraftId(aircraftId);
    assertEquals(aircraftId, aircraftInfoEntity.getAircraftId());
  }

  /**
   * メソッド名: getAircraftName<br>
   * 試験名: AircraftNameの取得が正しく行われることを確認する<br>
   * 条件: AircraftNameを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftName() {
    String aircraftName = "Test Aircraft";
    aircraftInfoEntity.setAircraftName(aircraftName);
    assertEquals(aircraftName, aircraftInfoEntity.getAircraftName());
  }

  /**
   * メソッド名: getManufacturer<br>
   * 試験名: Manufacturerの取得が正しく行われることを確認する<br>
   * 条件: Manufacturerを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getManufacturer() {
    String manufacturer = "Test Manufacturer";
    aircraftInfoEntity.setManufacturer(manufacturer);
    assertEquals(manufacturer, aircraftInfoEntity.getManufacturer());
  }

  /**
   * メソッド名: getManufacturingNumber<br>
   * 試験名: ManufacturingNumberの取得が正しく行われることを確認する<br>
   * 条件: ManufacturingNumberを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getManufacturingNumber() {
    String manufacturingNumber = "1234567890";
    aircraftInfoEntity.setManufacturingNumber(manufacturingNumber);
    assertEquals(manufacturingNumber, aircraftInfoEntity.getManufacturingNumber());
  }

  /**
   * メソッド名: getAircraftType<br>
   * 試験名: AircraftTypeの取得が正しく行われることを確認する<br>
   * 条件: AircraftTypeを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getAircraftType() {
    Integer aircraftType = 1;
    aircraftInfoEntity.setAircraftType(aircraftType);
    assertEquals(aircraftType, aircraftInfoEntity.getAircraftType());
  }

  /**
   * メソッド名: getMaxTakeoffWeight<br>
   * 試験名: MaxTakeoffWeightの取得が正しく行われることを確認する<br>
   * 条件: MaxTakeoffWeightを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getMaxTakeoffWeight() {
    Double maxTakeoffWeight = 100.0;
    aircraftInfoEntity.setMaxTakeoffWeight(maxTakeoffWeight);
    assertEquals(maxTakeoffWeight, aircraftInfoEntity.getMaxTakeoffWeight());
  }

  /**
   * メソッド名: getBodyWeight<br>
   * 試験名: BodyWeightの取得が正しく行われることを確認する<br>
   * 条件: BodyWeightを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getBodyWeight() {
    Double bodyWeight = 50.0;
    aircraftInfoEntity.setBodyWeight(bodyWeight);
    assertEquals(bodyWeight, aircraftInfoEntity.getBodyWeight());
  }

  /**
   * メソッド名: getMaxFlightSpeed<br>
   * 試験名: MaxFlightSpeedの取得が正しく行われることを確認する<br>
   * 条件: MaxFlightSpeedを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getMaxFlightSpeed() {
    Double maxFlightSpeed = 200.0;
    aircraftInfoEntity.setMaxFlightSpeed(maxFlightSpeed);
    assertEquals(maxFlightSpeed, aircraftInfoEntity.getMaxFlightSpeed());
  }

  /**
   * メソッド名: getMaxFlightTime<br>
   * 試験名: MaxFlightTimeの取得が正しく行われることを確認する<br>
   * 条件: MaxFlightTimeを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getMaxFlightTime() {
    Double maxFlightTime = 2.0;
    aircraftInfoEntity.setMaxFlightTime(maxFlightTime);
    assertEquals(maxFlightTime, aircraftInfoEntity.getMaxFlightTime());
  }

  /**
   * メソッド名: getCertification<br>
   * 試験名: Certificationの取得が正しく行われることを確認する<br>
   * 条件: Certificationを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getCertification() {
    Boolean certification = true;
    aircraftInfoEntity.setCertification(certification);
    assertEquals(certification, aircraftInfoEntity.getCertification());
  }

  /**
   * メソッド名: getDipsRegistrationCode<br>
   * 試験名: DipsRegistrationCodeの取得が正しく行われることを確認する<br>
   * 条件: DipsRegistrationCodeを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDipsRegistrationCode() {
    String dipsRegistrationCode = "DIPS123456";
    aircraftInfoEntity.setDipsRegistrationCode(dipsRegistrationCode);
    assertEquals(dipsRegistrationCode, aircraftInfoEntity.getDipsRegistrationCode());
  }

  /**
   * メソッド名: getOwnerType<br>
   * 試験名: OwnerTypeの取得が正しく行われることを確認する<br>
   * 条件: OwnerTypeを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getOwnerType() {
    Integer ownerType = 1;
    aircraftInfoEntity.setOwnerType(ownerType);
    assertEquals(ownerType, aircraftInfoEntity.getOwnerType());
  }

  /**
   * メソッド名: getOwnerId<br>
   * 試験名: OwnerIdの取得が正しく行われることを確認する<br>
   * 条件: OwnerIdを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getOwnerId() {
    UUID ownerId = UUID.randomUUID();
    aircraftInfoEntity.setOwnerId(ownerId);
    assertEquals(ownerId, aircraftInfoEntity.getOwnerId());
  }

  /**
   * メソッド名: getImageBinary<br>
   * 試験名: ImageBinaryの取得が正しく行われることを確認する<br>
   * 条件: ImageBinaryを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getImageBinary() {
    byte[] imageBinary = new byte[] {1, 2, 3};
    aircraftInfoEntity.setImageBinary(imageBinary);
    assertArrayEquals(imageBinary, aircraftInfoEntity.getImageBinary());
  }

  /**
   * メソッド名: getOperatorId<br>
   * 試験名: OperatorIdの取得が正しく行われることを確認する<br>
   * 条件: OperatorIdを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getOperatorId() {
    String createUserId = "USER123456";
    aircraftInfoEntity.setOperatorId(createUserId);
    assertEquals(createUserId, aircraftInfoEntity.getOperatorId());
  }

  /**
   * メソッド名: getUpdateUserId<br>
   * 試験名: UpdateUserIdの取得が正しく行われることを確認する<br>
   * 条件: UpdateUserIdを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getUpdateUserId() {
    String updateUserId = "USER654321";
    aircraftInfoEntity.setUpdateUserId(updateUserId);
    assertEquals(updateUserId, aircraftInfoEntity.getUpdateUserId());
  }

  /**
   * メソッド名: getCreateTime<br>
   * 試験名: CreateTimeの取得が正しく行われることを確認する<br>
   * 条件: CreateTimeを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getCreateTime() {
    Timestamp createTime = new Timestamp(System.currentTimeMillis());
    aircraftInfoEntity.setCreateTime(createTime);
    assertEquals(createTime, aircraftInfoEntity.getCreateTime());
  }

  /**
   * メソッド名: getUpdateTime<br>
   * 試験名: UpdateTimeの取得が正しく行われることを確認する<br>
   * 条件: UpdateTimeを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getUpdateTime() {
    Timestamp updateTime = new Timestamp(System.currentTimeMillis());
    aircraftInfoEntity.setUpdateTime(updateTime);
    assertEquals(updateTime, aircraftInfoEntity.getUpdateTime());
  }

  /**
   * メソッド名: getDeleteFlag<br>
   * 試験名: DeleteFlagの取得が正しく行われることを確認する<br>
   * 条件: DeleteFlagを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDeleteFlag() {
    Boolean deleteFlag = true;
    aircraftInfoEntity.setDeleteFlag(deleteFlag);
    assertEquals(deleteFlag, aircraftInfoEntity.getDeleteFlag());
  }
}
