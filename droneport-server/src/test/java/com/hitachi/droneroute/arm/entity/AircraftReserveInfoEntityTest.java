package com.hitachi.droneroute.arm.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.hypersistence.utils.hibernate.type.range.Range;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** AircraftReserveInfoEntityのテストクラス */
public class AircraftReserveInfoEntityTest {

  private AircraftReserveInfoEntity entity;

  @BeforeEach
  void setUp() {
    entity = new AircraftReserveInfoEntity();
  }

  /**
   * メソッド名: getAircraftReservationId<br>
   * 試験名: AircraftReservationIdの取得が正しく行われることを確認する<br>
   * 条件: AircraftReservationIdを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetAircraftReservationId() {
    UUID id = UUID.randomUUID();
    entity.setAircraftReservationId(id);
    assertEquals(id, entity.getAircraftReservationId());
  }

  /**
   * メソッド名: getAircraftId<br>
   * 試験名: AircraftIdの取得が正しく行われることを確認する<br>
   * 条件: AircraftIdを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetAircraftId() {
    UUID id = UUID.randomUUID();
    entity.setAircraftId(id);
    assertEquals(id, entity.getAircraftId());
  }

  /**
   * メソッド名: getReservationTime<br>
   * 試験名: ReservationTimeの取得が正しく行われることを確認する<br>
   * 条件: ReservationTimeを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetReservationTime() {
    Timestamp timeFrom = new Timestamp(System.currentTimeMillis());
    Timestamp timeTo = new Timestamp(System.currentTimeMillis());
    Range<LocalDateTime> rLocaldatetime =
        Range.localDateTimeRange(String.format("[%s,%s)", timeFrom, timeTo));
    entity.setReservationTime(rLocaldatetime);
    assertEquals(rLocaldatetime, entity.getReservationTime());
  }

  /**
   * メソッド名: getOperatorId<br>
   * 試験名: OperatorIdの取得が正しく行われることを確認する<br>
   * 条件: OperatorIdを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetOperatorId() {
    String userId = "testUser";
    entity.setOperatorId(userId);
    assertEquals(userId, entity.getOperatorId());
  }

  /**
   * メソッド名: getUpdateUserId<br>
   * 試験名: UpdateUserIdの取得が正しく行われることを確認する<br>
   * 条件: UpdateUserIdを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetUpdateUserId() {
    String userId = "testUser";
    entity.setUpdateUserId(userId);
    assertEquals(userId, entity.getUpdateUserId());
  }

  /**
   * メソッド名: getCreateTime<br>
   * 試験名: CreateTimeの取得が正しく行われることを確認する<br>
   * 条件: CreateTimeを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetCreateTime() {
    Timestamp time = new Timestamp(System.currentTimeMillis());
    entity.setCreateTime(time);
    assertEquals(time, entity.getCreateTime());
  }

  /**
   * メソッド名: getUpdateTime<br>
   * 試験名: UpdateTimeの取得が正しく行われることを確認する<br>
   * 条件: UpdateTimeを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetUpdateTime() {
    Timestamp time = new Timestamp(System.currentTimeMillis());
    entity.setUpdateTime(time);
    assertEquals(time, entity.getUpdateTime());
  }

  /**
   * メソッド名: getDeleteFlag<br>
   * 試験名: DeleteFlagの取得が正しく行われることを確認する<br>
   * 条件: DeleteFlagを設定し、getterで取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetDeleteFlag() {
    Boolean flag = true;
    entity.setDeleteFlag(flag);
    assertEquals(flag, entity.getDeleteFlag());
  }
}
