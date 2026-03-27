package com.hitachi.droneroute.dpm.mqtt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** VisMqttSettingsクラスの単体テスト */
public class VisMqttSettingsTest {

  @Mock private SystemSettings systemSettings;

  private VisMqttSettings visMqttSettings;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    visMqttSettings = new VisMqttSettings(systemSettings);
  }

  /**
   * メソッド名: enableTelemetry<br>
   * 試験名: テレメトリ情報のsubscribeの有効無効設定を取得する<br>
   * 条件: systemSettingsがtrueを返す<br>
   * 結果: trueが返されること<br>
   * テストパターン：正常系
   */
  @Test
  public void testEnableTelemetry_true() {
    when(systemSettings.getBoolean(
            DronePortConstants.SETTINGS_VIS_CONNECT,
            DronePortConstants.SETTINGS_VIS_ENABLE_TELEMETRY))
        .thenReturn(true);
    assertTrue(visMqttSettings.enableTelemetry());
  }

  /**
   * メソッド名: enableTelemetry<br>
   * 試験名: テレメトリ情報のsubscribeの有効無効設定を取得する<br>
   * 条件: systemSettingsがfalseを返す<br>
   * 結果: falseが返されること<br>
   * テストパターン：正常系
   */
  @Test
  public void testEnableTelemetry_false() {
    when(systemSettings.getBoolean(
            DronePortConstants.SETTINGS_VIS_CONNECT,
            DronePortConstants.SETTINGS_VIS_ENABLE_TELEMETRY))
        .thenReturn(false);
    assertFalse(visMqttSettings.enableTelemetry());
  }

  /**
   * メソッド名: enableQueryReservation<br>
   * 試験名: 予約照会のsubscribeの有効無効設定を取得する<br>
   * 条件: systemSettingsがtrueを返す<br>
   * 結果: trueが返されること<br>
   * テストパターン：正常系
   */
  @Test
  public void testEnableQueryReservation_true() {
    when(systemSettings.getBoolean(
            DronePortConstants.SETTINGS_VIS_CONNECT,
            DronePortConstants.SETTINGS_VIS_ENABLE_QUERY_RESERVATION_REQ))
        .thenReturn(true);
    assertTrue(visMqttSettings.enableQueryReservation());
  }

  /**
   * メソッド名: enableQueryReservation<br>
   * 試験名: 予約照会のsubscribeの有効無効設定を取得する<br>
   * 条件: systemSettingsがfalseを返す<br>
   * 結果: falseが返されること<br>
   * テストパターン：正常系
   */
  @Test
  public void testEnableQueryReservation_false() {
    when(systemSettings.getBoolean(
            DronePortConstants.SETTINGS_VIS_CONNECT,
            DronePortConstants.SETTINGS_VIS_ENABLE_QUERY_RESERVATION_REQ))
        .thenReturn(false);
    assertFalse(visMqttSettings.enableQueryReservation());
  }

  /**
   * メソッド名: getTelemetrySubscribeUri<br>
   * 試験名: テレメトリ情報をsubscribeするURIを取得する<br>
   * 条件: systemSettingsが適切な値を返す<br>
   * 結果: 正しいURIが返されること<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetTelemetrySubscribeUri() {
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT,
            DronePortConstants.SETTINGS_VIS_TOPIC_TELEMETRY))
        .thenReturn("topic");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_BROKER_URL))
        .thenReturn("brokerUrl");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_USERNAME))
        .thenReturn("userName");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_PASSWORD))
        .thenReturn("password");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_QOS))
        .thenReturn("0");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_MQTT_BASE_URI))
        .thenReturn("paho:%s?brokerUrl=%s&userName=%s&password=%s&qos=%s");

    String expectedUri = "paho:topic?brokerUrl=brokerUrl&userName=userName&password=password&qos=0";
    assertEquals(expectedUri, visMqttSettings.getTelemetrySubscribeUri());
  }

  /**
   * メソッド名: getQueryReservationSubscribeUri<br>
   * 試験名: 予約照会をsubscribeするURIを取得する<br>
   * 条件: systemSettingsが適切な値を返す<br>
   * 結果: 正しいURIが返されること<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetQueryReservationSubscribeUri() {
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT,
            DronePortConstants.SETTINGS_VIS_TOPIC_QUERY_RESERVATION_REQ))
        .thenReturn("topic");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_BROKER_URL))
        .thenReturn("brokerUrl");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_USERNAME))
        .thenReturn("userName");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_PASSWORD))
        .thenReturn("password");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_QOS))
        .thenReturn("0");
    when(systemSettings.getString(
            DronePortConstants.SETTINGS_VIS_CONNECT, DronePortConstants.SETTINGS_VIS_MQTT_BASE_URI))
        .thenReturn("paho:%s?brokerUrl=%s&userName=%s&password=%s&qos=%s");

    String expectedUri = "paho:topic?brokerUrl=brokerUrl&userName=userName&password=password&qos=0";
    assertEquals(expectedUri, visMqttSettings.getQueryReservationSubscribeUri());
  }
}
