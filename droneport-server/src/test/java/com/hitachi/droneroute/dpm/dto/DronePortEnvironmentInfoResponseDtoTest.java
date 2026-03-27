package com.hitachi.droneroute.dpm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * DronePortEnvironmentInfoResponseDtoTest<br>
 * このクラスはDronePortEnvironmentInfoResponseDtoクラスをテストします。
 */
public class DronePortEnvironmentInfoResponseDtoTest {

  /**
   * メソッド名: setDronePortId, getDronePortId<br>
   * 試験名: 離着陸場IDの設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な離着陸場IDを設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetDronePortId() {
    DronePortEnvironmentInfoResponseDto dto = new DronePortEnvironmentInfoResponseDto();
    String dronePortId = "DP001";
    dto.setDronePortId(dronePortId);
    assertEquals(dronePortId, dto.getDronePortId());
  }

  /**
   * メソッド名: setWindSpeed, getWindSpeed<br>
   * 試験名: 風速の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な風速を設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetWindSpeed() {
    DronePortEnvironmentInfoResponseDto dto = new DronePortEnvironmentInfoResponseDto();
    Double windSpeed = 10.5;
    dto.setWindSpeed(windSpeed);
    assertEquals(windSpeed, dto.getWindSpeed());
  }

  /**
   * メソッド名: setWindDirection, getWindDirection<br>
   * 試験名: 風向の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な風向を設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetWindDirection() {
    DronePortEnvironmentInfoResponseDto dto = new DronePortEnvironmentInfoResponseDto();
    Double windDirection = 180.0;
    dto.setWindDirection(windDirection);
    assertEquals(windDirection, dto.getWindDirection());
  }

  /**
   * メソッド名: setRainfall, getRainfall<br>
   * 試験名: 降水量の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な降水量を設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetRainfall() {
    DronePortEnvironmentInfoResponseDto dto = new DronePortEnvironmentInfoResponseDto();
    Double rainfall = 5.0;
    dto.setRainfall(rainfall);
    assertEquals(rainfall, dto.getRainfall());
  }

  /**
   * メソッド名: setTemp, getTemp<br>
   * 試験名: 気温の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な気温を設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetTemp() {
    DronePortEnvironmentInfoResponseDto dto = new DronePortEnvironmentInfoResponseDto();
    Double temp = 25.0;
    dto.setTemp(temp);
    assertEquals(temp, dto.getTemp());
  }

  /**
   * メソッド名: setPressure, getPressure<br>
   * 試験名: 気圧の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な気圧を設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetPressure() {
    DronePortEnvironmentInfoResponseDto dto = new DronePortEnvironmentInfoResponseDto();
    Double pressure = 1013.25;
    dto.setPressure(pressure);
    assertEquals(pressure, dto.getPressure());
  }

  /**
   * メソッド名: setObstacleDetected, getObstacleDetected<br>
   * 試験名: 障害物検知の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な障害物検知フラグを設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetObstacleDetected() {
    DronePortEnvironmentInfoResponseDto dto = new DronePortEnvironmentInfoResponseDto();
    Boolean obstacleDetected = true;
    dto.setObstacleDetected(obstacleDetected);
    assertEquals(obstacleDetected, dto.getObstacleDetected());
  }

  /**
   * メソッド名: setObservationTime, getObservationTime<br>
   * 試験名: 観測時間の設定と取得が正しく行われることを確認する<br>
   * 条件: 有効な観測時間を設定し、それを取得する<br>
   * 結果: 設定した値が取得した値と一致すること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testSetAndGetObservationTime() {
    DronePortEnvironmentInfoResponseDto dto = new DronePortEnvironmentInfoResponseDto();
    String observationTime = "2024-11-14T12:34:56Z";
    dto.setObservationTime(observationTime);
    assertEquals(observationTime, dto.getObservationTime());
  }

  /**
   * メソッド名: toString<br>
   * 試験名: toStringメソッドが正しい文字列を返すことを確認する<br>
   * 条件: 全てのフィールドに値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 期待される文字列が返されること<br>
   * テストパターン: 正常系
   */
  @Test
  public void testToString() {
    DronePortEnvironmentInfoResponseDto dto = new DronePortEnvironmentInfoResponseDto();
    dto.setDronePortId("DP001");
    dto.setWindSpeed(10.5);
    dto.setWindDirection(180.0);
    dto.setRainfall(5.0);
    dto.setTemp(25.0);
    dto.setPressure(1013.25);
    dto.setObstacleDetected(true);
    dto.setObservationTime("2024-11-14T12:34:56Z");

    String expectedString =
        "DronePortEnvironmentInfoResponseDto(dronePortId=DP001, windSpeed=10.5, windDirection=180.0, rainfall=5.0, temp=25.0, pressure=1013.25, obstacleDetected=true, observationTime=2024-11-14T12:34:56Z)";
    assertEquals(expectedString, dto.toString());
  }
}
