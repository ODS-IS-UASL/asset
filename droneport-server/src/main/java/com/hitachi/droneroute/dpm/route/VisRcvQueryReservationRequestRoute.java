package com.hitachi.droneroute.dpm.route;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hitachi.droneroute.dpm.mqtt.VisMqttSettings;
import com.hitachi.droneroute.dpm.processor.VisRcvQueryReservationRequestProcessor;

import lombok.RequiredArgsConstructor;

/**
 * 予約照会受信ルート定義
 * @author Hiroshi Toyoda
 *
 */
@RequiredArgsConstructor
@Component
public class VisRcvQueryReservationRequestRoute extends RouteBuilder {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final VisRcvQueryReservationRequestProcessor processor;

	private final VisMqttSettings visMqttSettings;
	
	@Override
	public void configure() throws Exception {
		boolean enableSubscribe = visMqttSettings.enableQueryReservation();
		logger.info("予約照会受信有効無効設定:" + enableSubscribe);
		if (!enableSubscribe) {
			return;
		}
		
		// 予約照会をsubscribeする	
		from(visMqttSettings.getQueryReservationSubscribeUri())
		.routeId(this.getClass().getSimpleName())
		// 受信したデータを登録する
		.process(processor)
		;
		
	}
}
