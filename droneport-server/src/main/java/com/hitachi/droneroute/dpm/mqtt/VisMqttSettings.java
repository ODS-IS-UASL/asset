package com.hitachi.droneroute.dpm.mqtt;

import org.springframework.stereotype.Component;

import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;

import lombok.RequiredArgsConstructor;

/**
 * システム設定を参照してMQTTに関連する情報を取得する
 * @author Hiroshi Toyoda
 *
 */
@RequiredArgsConstructor
@Component
public class VisMqttSettings {

	private final SystemSettings systemSettings;
	
	/**
	 * テレメトリ情報のsubscribeの有効無効設定を取得
	 * @return
	 */
	public boolean enableTelemetry() {
		return systemSettings.getBoolean(
				DronePortConstants.SETTINGS_VIS_CONNECT, 
				DronePortConstants.SETTINGS_VIS_ENABLE_TELEMETRY);
	}
	
	/**
	 * 予約照会のsubscribeの有効無効設定を取得
	 * @return
	 */
	public boolean enableQueryReservation() {
		return systemSettings.getBoolean(
				DronePortConstants.SETTINGS_VIS_CONNECT, 
				DronePortConstants.SETTINGS_VIS_ENABLE_QUERY_RESERVATION_REQ);
	}
	
	/**
	 * テレメトリ情報をsubscribeするURIを取得する
	 * @return
	 */
	public String getTelemetrySubscribeUri() {
		String topic = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT,
				DronePortConstants.SETTINGS_VIS_TOPIC_TELEMETRY);
		return getSubscribeUri(topic);
	}
	
	/**
	 * 予約照会をsubscribeするURIを取得する
	 * @return
	 */
	public String getQueryReservationSubscribeUri() {
		String topic = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT,
				DronePortConstants.SETTINGS_VIS_TOPIC_QUERY_RESERVATION_REQ);
		return getSubscribeUri(topic);
	}
	
	private String getSubscribeUri(String topic) {
		String brokerUrl = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT,
				DronePortConstants.SETTINGS_VIS_BROKER_URL);
		String userName = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT,
				DronePortConstants.SETTINGS_VIS_USERNAME);
		String password = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT,
				DronePortConstants.SETTINGS_VIS_PASSWORD);
		String qos = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT,
				DronePortConstants.SETTINGS_VIS_QOS);
		String clientId = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT,
				DronePortConstants.SETTINGS_VIS_CLIENT_ID);
		String uriFormat = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT,
				DronePortConstants.SETTINGS_VIS_MQTT_BASE_URI);
		return String.format(uriFormat, topic, brokerUrl, userName, password, qos, clientId);
	}
}
