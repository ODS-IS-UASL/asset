package com.hitachi.droneroute.dpm.dto.vis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** テストクラス: TelemetryInfoTest<br> */
class TelemetryInfoTest {

  private TelemetryInfo telemetryInfo;

  @BeforeEach
  void setUp() {
    telemetryInfo = new TelemetryInfo();
  }

  /**
   * メソッド名: getDroneportId, setDroneportId<br>
   * 試験名: ドローンポートIDの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDroneportId() {
    String expected = "droneport123";
    telemetryInfo.setDroneportId(expected);
    assertEquals(expected, telemetryInfo.getDroneportId());
  }

  /**
   * メソッド名: getDroneportIpAddress, setDroneportIpAddress<br>
   * 試験名: ドローンポートIPアドレスの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDroneportIpAddress() {
    String expected = "192.168.1.1";
    telemetryInfo.setDroneportIpAddress(expected);
    assertEquals(expected, telemetryInfo.getDroneportIpAddress());
  }

  /**
   * メソッド名: getDroneportName, setDroneportName<br>
   * 試験名: ドローンポート名の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDroneportName() {
    String expected = "Droneport A";
    telemetryInfo.setDroneportName(expected);
    assertEquals(expected, telemetryInfo.getDroneportName());
  }

  /**
   * メソッド名: getDroneportStatus, setDroneportStatus<br>
   * 試験名: ドローンポートステータスの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDroneportStatus() {
    String expected = "active";
    telemetryInfo.setDroneportStatus(expected);
    assertEquals(expected, telemetryInfo.getDroneportStatus());
  }

  /**
   * メソッド名: getVisStatus, setVisStatus<br>
   * 試験名: VISステータスの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetVisStatus() {
    String expected = "operational";
    telemetryInfo.setVisStatus(expected);
    assertEquals(expected, telemetryInfo.getVisStatus());
  }

  /**
   * メソッド名: getDroneportLat, setDroneportLat<br>
   * 試験名: 緯度の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDroneportLat() {
    Double expected = 35.6895;
    telemetryInfo.setDroneportLat(expected);
    assertEquals(expected, telemetryInfo.getDroneportLat());
  }

  /**
   * メソッド名: getDroneportLon, setDroneportLon<br>
   * 試験名: 経度の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDroneportLon() {
    Double expected = 139.6917;
    telemetryInfo.setDroneportLon(expected);
    assertEquals(expected, telemetryInfo.getDroneportLon());
  }

  /**
   * メソッド名: getDroneportAlt, setDroneportAlt<br>
   * 試験名: 着地面対地高度の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDroneportAlt() {
    Double expected = 50.0;
    telemetryInfo.setDroneportAlt(expected);
    assertEquals(expected, telemetryInfo.getDroneportAlt());
  }

  /**
   * メソッド名: getWindDirection, setWindDirection<br>
   * 試験名: 風向の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetWindDirection() {
    Double expected = 180.0;
    telemetryInfo.setWindDirection(expected);
    assertEquals(expected, telemetryInfo.getWindDirection());
  }

  /**
   * メソッド名: getWindSpeed, setWindSpeed<br>
   * 試験名: 風速の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetWindSpeed() {
    Double expected = 5.5;
    telemetryInfo.setWindSpeed(expected);
    assertEquals(expected, telemetryInfo.getWindSpeed());
  }

  /**
   * メソッド名: getMaxinstWindDirection, setMaxinstWindDirection<br>
   * 試験名: 最大風速時風向の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetMaxinstWindDirection() {
    Double expected = 200.0;
    telemetryInfo.setMaxinstWindDirection(expected);
    assertEquals(expected, telemetryInfo.getMaxinstWindDirection());
  }

  /**
   * メソッド名: getMaxinstWindSpeed, setMaxinstWindSpeed<br>
   * 試験名: 最大風速の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetMaxinstWindSpeed() {
    Double expected = 10.0;
    telemetryInfo.setMaxinstWindSpeed(expected);
    assertEquals(expected, telemetryInfo.getMaxinstWindSpeed());
  }

  /**
   * メソッド名: getRainfall, setRainfall<br>
   * 試験名: 雨量の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetRainfall() {
    Double expected = 0.0;
    telemetryInfo.setRainfall(expected);
    assertEquals(expected, telemetryInfo.getRainfall());
  }

  /**
   * メソッド名: getTemp, setTemp<br>
   * 試験名: 気温の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetTemp() {
    Double expected = 25.0;
    telemetryInfo.setTemp(expected);
    assertEquals(expected, telemetryInfo.getTemp());
  }

  /**
   * メソッド名: getHumidity, setHumidity<br>
   * 試験名: 湿度の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetHumidity() {
    Double expected = 60.0;
    telemetryInfo.setHumidity(expected);
    assertEquals(expected, telemetryInfo.getHumidity());
  }

  /**
   * メソッド名: getPressure, setPressure<br>
   * 試験名: 気圧の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetPressure() {
    Double expected = 1013.0;
    telemetryInfo.setPressure(expected);
    assertEquals(expected, telemetryInfo.getPressure());
  }

  /**
   * メソッド名: getIlluminance, setIlluminance<br>
   * 試験名: 照度の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetIlluminance() {
    Double expected = 500.0;
    telemetryInfo.setIlluminance(expected);
    assertEquals(expected, telemetryInfo.getIlluminance());
  }

  /**
   * メソッド名: getUltraviolet, setUltraviolet<br>
   * 試験名: 紫外線の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetUltraviolet() {
    Double expected = 3.0;
    telemetryInfo.setUltraviolet(expected);
    assertEquals(expected, telemetryInfo.getUltraviolet());
  }

  /**
   * メソッド名: getObservationTime, setObservationTime<br>
   * 試験名: 観測時間の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetObservationTime() {
    String expected = "2023-10-01T12:00:00Z";
    telemetryInfo.setObservationTime(expected);
    assertEquals(expected, telemetryInfo.getObservationTime());
  }

  /**
   * メソッド名: isInvasionFlag, setInvasionFlag<br>
   * 試験名: 侵入検知有無の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testIsSetInvasionFlag() {
    boolean expected = true;
    telemetryInfo.setInvasionFlag(expected);
    assertEquals(expected, telemetryInfo.isInvasionFlag());
  }

  /**
   * メソッド名: getInvasionCategory, setInvasionCategory<br>
   * 試験名: 検知物カテゴリの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetInvasionCategory() {
    String expected = "animal";
    telemetryInfo.setInvasionCategory(expected);
    assertEquals(expected, telemetryInfo.getInvasionCategory());
  }

  /**
   * メソッド名: getThresholdWindSpeed, setThresholdWindSpeed<br>
   * 試験名: 閾値（風速）の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetThresholdWindSpeed() {
    Double expected = 15.0;
    telemetryInfo.setThresholdWindSpeed(expected);
    assertEquals(expected, telemetryInfo.getThresholdWindSpeed());
  }

  /**
   * メソッド名: getBaseId, setBaseId<br>
   * 試験名: 拠点IDの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetBaseId() {
    String expected = "base123";
    telemetryInfo.setBaseId(expected);
    assertEquals(expected, telemetryInfo.getBaseId());
  }

  /**
   * メソッド名: getBaseAddress, setBaseAddress<br>
   * 試験名: 拠点住所の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetBaseAddress() {
    String expected = "123 Main St, Tokyo";
    telemetryInfo.setBaseAddress(expected);
    assertEquals(expected, telemetryInfo.getBaseAddress());
  }

  /**
   * メソッド名: getBaseName, setBaseName<br>
   * 試験名: 拠点名称の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetBaseName() {
    String expected = "Main Base";
    telemetryInfo.setBaseName(expected);
    assertEquals(expected, telemetryInfo.getBaseName());
  }

  /**
   * メソッド名: getBaseStatus, setBaseStatus<br>
   * 試験名: 拠点ステータスの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetBaseStatus() {
    String expected = "operational";
    telemetryInfo.setBaseStatus(expected);
    assertEquals(expected, telemetryInfo.getBaseStatus());
  }

  /**
   * メソッド名: getUsage, setUsage<br>
   * 試験名: ドローンポート状態の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetUsage() {
    Integer expected = 1;
    telemetryInfo.setUsage(expected);
    assertEquals(expected, telemetryInfo.getUsage());
  }

  /**
   * メソッド名: getErrorCode, setErrorCode<br>
   * 試験名: エラーコードの設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetErrorCode() {
    String expected = "E001";
    telemetryInfo.setErrorCode(expected);
    assertEquals(expected, telemetryInfo.getErrorCode());
  }

  /**
   * メソッド名: getErrorReason, setErrorReason<br>
   * 試験名: エラー内容の設定と取得確認<br>
   * 条件: setterにより設定すること<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetErrorReason() {
    String expected = "Network error";
    telemetryInfo.setErrorReason(expected);
    assertEquals(expected, telemetryInfo.getErrorReason());
  }
}
