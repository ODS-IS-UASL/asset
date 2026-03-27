package com.hitachi.droneroute.dpm.route;

import com.hitachi.droneroute.dpm.mqtt.VisMqttSettings;
import com.hitachi.droneroute.dpm.processor.VisRcvInfoProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** テレメトリ情報受信ルート定義 */
@RequiredArgsConstructor
@Component
public class VisRcvInfoRoute extends RouteBuilder {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final VisRcvInfoProcessor processor;

  private final VisMqttSettings visMqttSettings;

  @Override
  /** テレメトリ情報受信の設定を行う */
  public void configure() throws Exception {
    boolean enableSubscribe = visMqttSettings.enableTelemetry();
    logger.info("テレメトリ受信有効無効設定:" + enableSubscribe);
    if (!enableSubscribe) {
      return;
    }

    // テレメトリ情報をsubscribeする
    from(visMqttSettings.getTelemetrySubscribeUri())
        .routeId(this.getClass().getSimpleName())
        // 受信したデータを登録する
        .process(processor);
  }
}
