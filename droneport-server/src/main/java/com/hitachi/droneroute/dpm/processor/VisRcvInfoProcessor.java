package com.hitachi.droneroute.dpm.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.dpm.dto.vis.TelemetryInfo;
import com.hitachi.droneroute.dpm.entity.VisTelemetryInfoEntity;
import com.hitachi.droneroute.dpm.repository.VisTelemetryInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.paho.PahoConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** テレメトリ情報受信処理本体 */
@RequiredArgsConstructor
@Component
public class VisRcvInfoProcessor implements Processor {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final VisTelemetryInfoRepository visTelemetryInfoRepository;

  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * VISから受信したテレメトリ情報を離着陸場管理テーブルに登録する
   *
   * @param exchange CamelのExchangeオブジェクト
   */
  @Override
  public void process(Exchange exchange) throws Exception {
    String topic = (String) exchange.getIn().getHeader(PahoConstants.MQTT_TOPIC);
    String rawData = exchange.getIn().getBody(String.class);
    logger.debug("トピック名: " + topic);
    logger.debug("テレメトリ情報受信: " + rawData);

    TelemetryInfo telemetry = mapper.readValue(rawData, TelemetryInfo.class);
    if (!isValid(telemetry)) {
      logger.warn("テレメトリ情報データが不正です");
      // 不正なデータは後続処理を行わないので、bodyにnullを設定
      exchange.getIn().setBody(null);
      return;
    }

    // エンティティを生成
    VisTelemetryInfoEntity entity = new VisTelemetryInfoEntity();
    // 受信した値を設定
    BeanUtils.copyProperties(telemetry, entity);
    entity.setObservationTime(
        com.hitachi.droneroute.cmn.util.StringUtils.parseDatetimeString(
            telemetry.getObservationTime()));

    // DBに登録/更新
    visTelemetryInfoRepository.save(entity);

    logger.debug("離着陸場情報登録/更新完了");
  }

  /**
   * テレメトリ情報の設定内容をチェックする
   *
   * @param body テレメトリ情報
   * @return true:設定内容は正常、false:設定内容に不正あり
   */
  private boolean isValid(TelemetryInfo body) {
    // 必須項目
    if (!StringUtils.hasText(body.getDroneportId())) {
      return false;
    }
    return true;
  }
}
