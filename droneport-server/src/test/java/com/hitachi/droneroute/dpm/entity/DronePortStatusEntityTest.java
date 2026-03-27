package com.hitachi.droneroute.dpm.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.hypersistence.utils.hibernate.type.range.Range;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** DronePortStatusEntityのテストクラス<br> */
class DronePortStatusEntityTest {

  private DronePortStatusEntity entity;

  @BeforeEach
  void setUp() {
    entity = new DronePortStatusEntity();
  }

  /**
   * メソッド名: getDronePortId, setDronePortId<br>
   * 試験名: DronePortIdの設定と取得確認<br>
   * 条件: setterによりStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortId() {
    String expected = "port123";
    entity.setDronePortId(expected);
    assertEquals(expected, entity.getDronePortId());
  }

  /**
   * メソッド名: getStoredAircraftId, setStoredAircraftId<br>
   * 試験名: StoredAircraftIdの設定と取得確認<br>
   * 条件: setterによりStringを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetStoredAircraftId() {
    UUID expected = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    entity.setStoredAircraftId(expected);
    assertEquals(expected, entity.getStoredAircraftId());
  }

  /**
   * メソッド名: getActiveStatus, setActiveStatus<br>
   * 試験名: ActiveStatusの設定と取得確認<br>
   * 条件: setterによりIntegerを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetActiveStatus() {
    Integer expected = 1;
    entity.setActiveStatus(expected);
    assertEquals(expected, entity.getActiveStatus());
  }

  /**
   * メソッド名: getInactiveTime, setInactiveTime<br>
   * 試験名: InactiveTimeの設定と取得確認<br>
   * 条件: setterによりRange<LocalDateTime>を設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetInactiveTime() {
    Range<LocalDateTime> expected =
        Range.closed(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
    entity.setInactiveTime(expected);
    assertEquals(expected, entity.getInactiveTime());
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
}
