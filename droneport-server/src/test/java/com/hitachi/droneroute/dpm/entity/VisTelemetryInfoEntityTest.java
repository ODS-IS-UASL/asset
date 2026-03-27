package com.hitachi.droneroute.dpm.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * VisTelemetryInfoEntityTest クラス<br>
 * VisTelemetryInfoEntity クラスのテストを行う
 */
public class VisTelemetryInfoEntityTest {

  private VisTelemetryInfoEntity entity;

  @BeforeEach
  public void setUp() {
    entity = new VisTelemetryInfoEntity();
  }

  /**
   * メソッド名: testDroneportId<br>
   * 試験名: droneportId フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testDroneportId() {
    String expected = "DP001";
    entity.setDroneportId(expected);
    assertEquals(expected, entity.getDroneportId());
  }

  /**
   * メソッド名: testDroneportIpAddress<br>
   * 試験名: droneportIpAddress フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testDroneportIpAddress() {
    String expected = "192.168.1.1";
    entity.setDroneportIpAddress(expected);
    assertEquals(expected, entity.getDroneportIpAddress());
  }

  /**
   * メソッド名: testDroneportName<br>
   * 試験名: droneportName フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testDroneportName() {
    String expected = "Drone Port 1";
    entity.setDroneportName(expected);
    assertEquals(expected, entity.getDroneportName());
  }

  /**
   * メソッド名: testDroneportStatus<br>
   * 試験名: droneportStatus フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testDroneportStatus() {
    String expected = "Active";
    entity.setDroneportStatus(expected);
    assertEquals(expected, entity.getDroneportStatus());
  }

  /**
   * メソッド名: testVisStatus<br>
   * 試験名: visStatus フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testVisStatus() {
    String expected = "Operational";
    entity.setVisStatus(expected);
    assertEquals(expected, entity.getVisStatus());
  }

  /**
   * メソッド名: testDroneportLat<br>
   * 試験名: droneportLat フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testDroneportLat() {
    Double expected = 35.6895;
    entity.setDroneportLat(expected);
    assertEquals(expected, entity.getDroneportLat());
  }

  /**
   * メソッド名: testDroneportLon<br>
   * 試験名: droneportLon フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testDroneportLon() {
    Double expected = 139.6917;
    entity.setDroneportLon(expected);
    assertEquals(expected, entity.getDroneportLon());
  }

  /**
   * メソッド名: testDroneportAlt<br>
   * 試験名: droneportAlt フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testDroneportAlt() {
    Double expected = 50.0;
    entity.setDroneportAlt(expected);
    assertEquals(expected, entity.getDroneportAlt());
  }

  /**
   * メソッド名: testWindDirection<br>
   * 試験名: windDirection フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testWindDirection() {
    Double expected = 180.0;
    entity.setWindDirection(expected);
    assertEquals(expected, entity.getWindDirection());
  }

  /**
   * メソッド名: testWindSpeed<br>
   * 試験名: windSpeed フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testWindSpeed() {
    Double expected = 5.5;
    entity.setWindSpeed(expected);
    assertEquals(expected, entity.getWindSpeed());
  }

  /**
   * メソッド名: testMaxinstWindDirection<br>
   * 試験名: maxinstWindDirection フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testMaxinstWindDirection() {
    Double expected = 200.0;
    entity.setMaxinstWindDirection(expected);
    assertEquals(expected, entity.getMaxinstWindDirection());
  }

  /**
   * メソッド名: testMaxinstWindSpeed<br>
   * 試験名: maxinstWindSpeed フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testMaxinstWindSpeed() {
    Double expected = 10.0;
    entity.setMaxinstWindSpeed(expected);
    assertEquals(expected, entity.getMaxinstWindSpeed());
  }

  /**
   * メソッド名: testRainfall<br>
   * 試験名: rainfall フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testRainfall() {
    Double expected = 0.0;
    entity.setRainfall(expected);
    assertEquals(expected, entity.getRainfall());
  }

  /**
   * メソッド名: testTemp<br>
   * 試験名: temp フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testTemp() {
    Double expected = 25.0;
    entity.setTemp(expected);
    assertEquals(expected, entity.getTemp());
  }

  /**
   * メソッド名: testHumidity<br>
   * 試験名: humidity フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testHumidity() {
    Double expected = 60.0;
    entity.setHumidity(expected);
    assertEquals(expected, entity.getHumidity());
  }

  /**
   * メソッド名: testPressure<br>
   * 試験名: pressure フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testPressure() {
    Double expected = 1013.0;
    entity.setPressure(expected);
    assertEquals(expected, entity.getPressure());
  }

  /**
   * メソッド名: testIlluminance<br>
   * 試験名: illuminance フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testIlluminance() {
    Double expected = 1000.0;
    entity.setIlluminance(expected);
    assertEquals(expected, entity.getIlluminance());
  }

  /**
   * メソッド名: testUltraviolet<br>
   * 試験名: ultraviolet フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testUltraviolet() {
    Double expected = 5.0;
    entity.setUltraviolet(expected);
    assertEquals(expected, entity.getUltraviolet());
  }

  /**
   * メソッド名: testObservationTime<br>
   * 試験名: observationTime フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testObservationTime() {
    Timestamp expected = new Timestamp(System.currentTimeMillis());
    entity.setObservationTime(expected);
    assertEquals(expected, entity.getObservationTime());
  }

  /**
   * メソッド名: testInvasionFlag<br>
   * 試験名: invasionFlag フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testInvasionFlag() {
    boolean expected = true;
    entity.setInvasionFlag(expected);
    assertTrue(entity.getInvasionFlag());
  }

  /**
   * メソッド名: testInvasionCategory<br>
   * 試験名: invasionCategory フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testInvasionCategory() {
    String expected = "Animal";
    entity.setInvasionCategory(expected);
    assertEquals(expected, entity.getInvasionCategory());
  }

  /**
   * メソッド名: testThresholdWindSpeed<br>
   * 試験名: thresholdWindSpeed フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testThresholdWindSpeed() {
    Double expected = 15.0;
    entity.setThresholdWindSpeed(expected);
    assertEquals(expected, entity.getThresholdWindSpeed());
  }

  /**
   * メソッド名: testBaseId<br>
   * 試験名: baseId フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testBaseId() {
    String expected = "Base001";
    entity.setBaseId(expected);
    assertEquals(expected, entity.getBaseId());
  }

  /**
   * メソッド名: testBaseAddress<br>
   * 試験名: baseAddress フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testBaseAddress() {
    String expected = "Tokyo, Japan";
    entity.setBaseAddress(expected);
    assertEquals(expected, entity.getBaseAddress());
  }

  /**
   * メソッド名: testBaseName<br>
   * 試験名: baseName フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testBaseName() {
    String expected = "Main Base";
    entity.setBaseName(expected);
    assertEquals(expected, entity.getBaseName());
  }

  /**
   * メソッド名: testBaseStatus<br>
   * 試験名: baseStatus フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testBaseStatus() {
    String expected = "Operational";
    entity.setBaseStatus(expected);
    assertEquals(expected, entity.getBaseStatus());
  }

  /**
   * メソッド名: testUsage<br>
   * 試験名: usage フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testUsage() {
    Integer expected = 1;
    entity.setUsage(expected);
    assertEquals(expected, entity.getUsage());
  }

  /**
   * メソッド名: testErrorCode<br>
   * 試験名: errorCode フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testErrorCode() {
    String expected = "E001";
    entity.setErrorCode(expected);
    assertEquals(expected, entity.getErrorCode());
  }

  /**
   * メソッド名: testErrorReason<br>
   * 試験名: errorReason フィールドの getter/setter のテスト<br>
   * 条件: setter で値を設定し、getter で取得する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系
   */
  @Test
  public void testErrorReason() {
    String expected = "Network Error";
    entity.setErrorReason(expected);
    assertEquals(expected, entity.getErrorReason());
  }
}
