package com.hitachi.droneroute.dpm.processor;

import java.util.Arrays;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.paho.PahoConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.vis.DroneportReserveListGetDto;
import com.hitachi.droneroute.dpm.dto.vis.ReservationInfo;
import com.hitachi.droneroute.dpm.dto.vis.ReserveList;
import com.hitachi.droneroute.dpm.mqtt.VisMqttPublisher;
import com.hitachi.droneroute.dpm.service.DronePortReserveInfoService;

import lombok.RequiredArgsConstructor;

/**
 * 予約照会受信処理本体
 * @author Hiroshi Toyoda
 *
 */
@RequiredArgsConstructor
@Component
public class VisRcvQueryReservationRequestProcessor implements Processor {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final DronePortReserveInfoService service;
	
	private final VisMqttPublisher mqttPublisher;
	
	private final ObjectMapper mapper = new ObjectMapper();

	private String getLastElement(String topic) {
		String[] array = topic.split("/");
		return array[array.length - 1];
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String topic = (String)exchange.getIn().getHeader(PahoConstants.MQTT_TOPIC);
		String visDronePortId = getLastElement(topic);
		String rawData = exchange.getIn().getBody(String.class);
		logger.debug("トピック名: " + topic);
		logger.debug("VISドローンポートID: " + visDronePortId);
		logger.debug("予約照会受信: " + rawData);
		
		ReserveList responseDto = new ReserveList();
		
		DroneportReserveListGetDto requestDto = mapper.readValue(rawData, DroneportReserveListGetDto.class);
		if (!StringUtils.hasText(requestDto.getDroneportReservationId())) {
			String errMsg = "ドローンポート予約IDが未設定です";
			logger.warn(errMsg);
			// 不正なデータは後続処理を行わないので、bodyにnullを設定
			exchange.getIn().setBody(null);
			
			// 照会応答をエラーでpublishする
			responseDto.setReservationInfo(null);
			responseDto.setResponseStatus(false);
			responseDto.setInfo(errMsg);
		} else {
			try {
				// 予約情報を検索
				DronePortReserveInfoDetailResponseDto detailDto = 
						service.getDetail(requestDto.getDroneportReservationId());
				// 照会応答を編集
				ReservationInfo reservationInfo = new ReservationInfo();
				reservationInfo.setReservationId(detailDto.getDronePortReservationId());
				reservationInfo.setStartTime(detailDto.getReservationTimeFrom());
				reservationInfo.setEndTime(detailDto.getReservationTimeTo());
				responseDto.setReservationInfo(Arrays.asList(reservationInfo));
				responseDto.setResponseStatus(true);
				responseDto.setDroneportId(detailDto.getDronePortId());
			} catch (NotFoundException e) {
				// 予約情報見つからず
				responseDto.setReservationInfo(null);
				responseDto.setResponseStatus(false);
				responseDto.setInfo(e.getMessage());
			}
		}
		// 照会応答をpublish
		mqttPublisher.publishQueryReservationResponse(visDronePortId, responseDto);
	}
}
