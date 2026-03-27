package com.hitachi.droneroute.dpm.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import io.hypersistence.utils.hibernate.type.range.Range;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** DronePortReserveInfoEntityのテストクラス<br> */
class DronePortReserveInfoEntityTest {

  private DronePortReserveInfoEntity entity;

  @BeforeEach
  void setUp() {
    entity = new DronePortReserveInfoEntity();
  }

  /**
   * メソッド名: getDronePortReservationId, setDronePortReservationId<br>
   * 試験名: DronePortReservationIdの設定と取得確認<br>
   * 条件: setterによりUUIDを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortReservationId() {
    UUID expected = UUID.randomUUID();
    entity.setDronePortReservationId(expected);
    assertEquals(expected, entity.getDronePortReservationId());
  }

  /**
   * メソッド名: getDronePortId, setDronePortId<br>
   * 試験名: DronePortIdの設定と取得確認<br>
   * 条件: setterによりUUIDを設定し、getterで取得する<br>
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
   * メソッド名: getAircraftId, setAircraftId<br>
   * 試験名: AircraftIdの設定と取得確認<br>
   * 条件: setterによりUUIDを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetAircraftId() {
    UUID expected = UUID.randomUUID();
    entity.setAircraftId(expected);
    assertEquals(expected, entity.getAircraftId());
  }

  /**
   * メソッド名: getRouteReservationId, setRouteReservationId<br>
   * 試験名: RouteIdの設定と取得確認<br>
   * 条件: setterによりUUIDを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetRouteId() {
    UUID expected = UUID.randomUUID();
    entity.setRouteReservationId(expected);
    assertEquals(expected, entity.getRouteReservationId());
  }

  /**
   * メソッド名: getUsageType, setUsageType<br>
   * 試験名: UsageTypeの設定と取得確認<br>
   * 条件: setterによりIntegerを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetUsageType() {
    Integer expected = 1;
    entity.setUsageType(expected);
    assertEquals(expected, entity.getUsageType());
  }

  /**
   * メソッド名: getReservationTime, setReservationTime<br>
   * 試験名: ReservationTimeの設定と取得確認<br>
   * 条件: setterによりRange<LocalDateTime>を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetReservationTime() {
    Range<LocalDateTime> expected =
        Range.closed(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
    entity.setReservationTime(expected);
    assertEquals(expected, entity.getReservationTime());
  }

  /**
   * メソッド名: getOperatorId, setOperatorId<br>
   * 試験名: OperatorIdの設定と取得確認<br>
   * 条件: setterによりStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetOperatorId() {
    String expected = "user123";
    entity.setOperatorId(expected);
    assertEquals(expected, entity.getOperatorId());
  }

  /**
   * メソッド名: getUpdateUserId, setUpdateUserId<br>
   * 試験名: UpdateUserIdの設定と取得確認<br>
   * 条件: setterによりStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetUpdateUserId() {
    String expected = "user456";
    entity.setUpdateUserId(expected);
    assertEquals(expected, entity.getUpdateUserId());
  }

  /**
   * メソッド名: getCreateTime, setCreateTime<br>
   * 試験名: CreateTimeの設定と取得確認<br>
   * 条件: setterによりTimestampを設定し、getterで取得する<br>
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
   * 試験名: UpdateTimeの設定と取得確認<br>
   * 条件: setterによりTimestampを設定し、getterで取得する<br>
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
   * 試験名: DeleteFlagの設定と取得確認<br>
   * 条件: setterによりBooleanを設定し、getterで取得する<br>
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
   * メソッド名: getReservationActiveFlag, setReservationActiveFlag<br>
   * 試験名: ReservationActiveFlagの設定と取得確認<br>
   * 条件: setterによりBooleanを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetReservationActiveFlag() {
    Boolean expected = true;
    entity.setReservationActiveFlag(expected);
    assertEquals(expected, entity.getReservationActiveFlag());
  }

  /**
   * メソッド名: getDronePortStatusEntity, setDronePortStatusEntity<br>
   * 試験名: DronePortStatusEntityの設定と取得確認<br>
   * 条件: setterによりDronePortStatusEntityを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortStatusEntity() {
    DronePortStatusEntity expected = new DronePortStatusEntity();
    entity.setDronePortStatusEntity(expected);
    assertEquals(expected, entity.getDronePortStatusEntity());
  }

  /**
   * メソッド名: getAircraftInfoEntity, setAircraftInfoEntity<br>
   * 試験名: AircraftInfoEntityの設定と取得確認<br>
   * 条件: setterによりAircraftInfoEntityを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetAircraftInfoEntity() {
    AircraftInfoEntity expected = new AircraftInfoEntity();
    entity.setAircraftInfoEntity(expected);
    assertEquals(expected, entity.getAircraftInfoEntity());
  }

  /**
   * メソッド名: getDronePortInfoEntity, setDronePortInfoEntity<br>
   * 試験名: DronePortInfoEntityの設定と取得確認<br>
   * 条件: setterによりDronePortInfoEntityを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortInfoEntity() {
    DronePortInfoEntity expected = new DronePortInfoEntity();
    entity.setDronePortInfoEntity(expected);
    assertEquals(expected, entity.getDronePortInfoEntity());
  }
}
