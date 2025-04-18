package com.hitachi.droneroute.dpm.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.vis.ReserveList;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class VisMqttPublisher {
	
	private final SystemSettings systemSettings;

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 照会応答をpublishする
	 * @param dto 照会応答DTO
	 */
	public void publishQueryReservationResponse(String visDronePortId, ReserveList dto) {
		String json;
		try {
			json = objectMapper.writeValueAsString(dto);
		} catch (JsonProcessingException e) {
			throw new ServiceErrorException("照会応答DTOのJSON化に失敗しました", e);
		}
		String topic = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT, 
				DronePortConstants.SETTINGS_VIS_TOPIC_QUERY_RESERVATION_RES);
		publish(topic + "/" + visDronePortId, json);
	}
	
	/**
	 * メッセージをpublishする
	 * @param publishTopic　トピック名
	 * @param publishMessage 送信するメッセージ
	 */
	private void publish(String publishTopic, String publishMessage) {
		String brokerHostName = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT, 
				DronePortConstants.SETTINGS_VIS_BROKER_URL);
		String clientId = systemSettings.getString(
				DronePortConstants.SETTINGS_VIS_CONNECT, 
				DronePortConstants.SETTINGS_VIS_CLIENT_ID);
		int qos = systemSettings.getIntegerValue(
				DronePortConstants.SETTINGS_VIS_CONNECT, 
				DronePortConstants.SETTINGS_VIS_QOS);

        try {
    		MqttClient mqttClient = new MqttClient(brokerHostName, clientId, new MemoryPersistence());
    		
    		MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);

            mqttClient.connect(connOpts);

            MqttMessage message = new MqttMessage(publishMessage.getBytes());
            message.setQos(qos);
            mqttClient.publish(publishTopic, message);

            mqttClient.disconnect();
            mqttClient.close();
            
        } catch(MqttException e) {
        	throw new ServiceErrorException("publishに失敗しました", e);
        }
	}
	
}
