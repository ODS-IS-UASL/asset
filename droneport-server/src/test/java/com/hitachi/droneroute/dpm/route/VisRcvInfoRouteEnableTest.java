package com.hitachi.droneroute.dpm.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.dto.vis.TelemetryInfo;
import com.hitachi.droneroute.dpm.entity.VisTelemetryInfoEntity;
import com.hitachi.droneroute.dpm.repository.VisTelemetryInfoRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextLifecycle;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.paho.PahoConstants;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/** VisRcvInfoRouteのテストクラス(ルート有効) */
@SpringBootTest(
    properties = "systemsettings.settingclass.visConnectInfo.enableSubscribeTelemetryInfo=true")
@CamelSpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith
public class VisRcvInfoRouteEnableTest {

  @MockBean VisTelemetryInfoRepository visTelemetryInfoRepository;

  @SpyBean private SystemSettings systemSettings;

  @Autowired private CamelContext camelContext;

  @Autowired private CamelContextLifecycle camelContextLifecycle;

  @Produce("direct:start")
  private ProducerTemplate producerTemplate;

  @EndpointInject("mock:result")
  private MockEndpoint mockEndpoint;

  private ObjectMapper objectMapper = new ObjectMapper();

  private TelemetryInfo createTestTelemetryInfo(String dateTime) {
    TelemetryInfo telemetryInfo = new TelemetryInfo();

    telemetryInfo.setDroneportId("DP001");
    telemetryInfo.setDroneportIpAddress("192.168.1.1");
    telemetryInfo.setDroneportName("Drone Port A");
    telemetryInfo.setDroneportStatus("Active");
    telemetryInfo.setVisStatus("Operational");
    telemetryInfo.setDroneportLat(35.6895);
    telemetryInfo.setDroneportLon(139.6917);
    telemetryInfo.setDroneportAlt(50.0);
    telemetryInfo.setWindDirection(180.0);
    telemetryInfo.setWindSpeed(5.5);
    telemetryInfo.setMaxinstWindDirection(190.0);
    telemetryInfo.setMaxinstWindSpeed(10.0);
    telemetryInfo.setRainfall(0.0);
    telemetryInfo.setTemp(25.5);
    telemetryInfo.setHumidity(60.0);
    telemetryInfo.setPressure(1013.0);
    telemetryInfo.setIlluminance(500.0);
    telemetryInfo.setUltraviolet(3.0);
    telemetryInfo.setObservationTime(dateTime);
    telemetryInfo.setInvasionFlag(false);
    telemetryInfo.setInvasionCategory("None");
    telemetryInfo.setThresholdWindSpeed(15.0);
    telemetryInfo.setBaseId("B001");
    telemetryInfo.setBaseAddress("123 Base St, City, Country");
    telemetryInfo.setBaseName("Base A");
    telemetryInfo.setBaseStatus("Operational");
    telemetryInfo.setUsage(1);
    telemetryInfo.setErrorCode("E001");
    telemetryInfo.setErrorReason("No Error");

    return telemetryInfo;
  }

  /**
   * メソッド名: configure_enableTelemetryTrue<br>
   * 試験名: enableTelemetryがtrueの場合のルート設定を確認する<br>
   * 条件: enableTelemetryがtrueの場合<br>
   * 結果: ルートが設定されること<br>
   * テストパターン：正常系
   */
  @Test
  public void configure_enableTelemetryTrue() throws Exception {
    String strZonedDateTime = "2023-10-01T12:00:00Z";
    String strLocalDateTime = "2023-10-01T12:00:00";
    Timestamp timeStamp = Timestamp.valueOf(LocalDateTime.parse(strLocalDateTime));

    // DB登録をモック化
    doReturn(null).when(visTelemetryInfoRepository).save(any(VisTelemetryInfoEntity.class));

    // VisRcvInfoRouteのルートを書き換える
    AdviceWith.adviceWith(
        camelContext,
        VisRcvInfoRoute.class.getSimpleName(),
        a -> {
          a.replaceFromWith("direct:start"); // fromの定義を書き換える
          a.weaveAddLast().to("mock:result"); // ルートの最後に追加
        });

    // コンテキストを開始
    camelContextLifecycle.start();

    // ルートが設定されていることを確認
    assertNotNull(camelContext.getRoute(VisRcvInfoRoute.class.getSimpleName()));

    // VISから受信したテレメトリ情報を作成する(これが実施後の期待値となる)
    TelemetryInfo sendTelemetry = createTestTelemetryInfo(strZonedDateTime);
    String sendBody = objectMapper.writeValueAsString(sendTelemetry);
    // メッセージを送信してテストを実施
    producerTemplate.sendBodyAndHeader(
        "direct:start", sendBody, PahoConstants.MQTT_TOPIC, "dummyTopic");

    // 最終ルートに到達したことを確認
    mockEndpoint.assertIsSatisfied();

    // DB登録の引数を確認
    ArgumentCaptor<VisTelemetryInfoEntity> saveCaptor =
        ArgumentCaptor.forClass(VisTelemetryInfoEntity.class);
    verify(visTelemetryInfoRepository, times(1)).save(saveCaptor.capture());
    VisTelemetryInfoEntity actualEntity = saveCaptor.getValue();
    // 期待値と比較
    assertEquals(sendTelemetry.getDroneportId(), actualEntity.getDroneportId());
    assertEquals(sendTelemetry.getDroneportIpAddress(), actualEntity.getDroneportIpAddress());
    assertEquals(sendTelemetry.getDroneportName(), actualEntity.getDroneportName());
    assertEquals(sendTelemetry.getDroneportStatus(), actualEntity.getDroneportStatus());
    assertEquals(sendTelemetry.getVisStatus(), actualEntity.getVisStatus());
    assertEquals(sendTelemetry.getDroneportLat(), actualEntity.getDroneportLat());
    assertEquals(sendTelemetry.getDroneportLon(), actualEntity.getDroneportLon());
    assertEquals(sendTelemetry.getDroneportAlt(), actualEntity.getDroneportAlt());
    assertEquals(sendTelemetry.getWindDirection(), actualEntity.getWindDirection());
    assertEquals(sendTelemetry.getWindSpeed(), actualEntity.getWindSpeed());
    assertEquals(sendTelemetry.getMaxinstWindDirection(), actualEntity.getMaxinstWindDirection());
    assertEquals(sendTelemetry.getMaxinstWindSpeed(), actualEntity.getMaxinstWindSpeed());
    assertEquals(sendTelemetry.getRainfall(), actualEntity.getRainfall());
    assertEquals(sendTelemetry.getTemp(), actualEntity.getTemp());
    assertEquals(sendTelemetry.getHumidity(), actualEntity.getHumidity());
    assertEquals(sendTelemetry.getPressure(), actualEntity.getPressure());
    assertEquals(sendTelemetry.getIlluminance(), actualEntity.getIlluminance());
    assertEquals(sendTelemetry.getUltraviolet(), actualEntity.getUltraviolet());
    assertEquals(timeStamp, actualEntity.getObservationTime());
    assertEquals(sendTelemetry.isInvasionFlag(), actualEntity.getInvasionFlag());
    assertEquals(sendTelemetry.getInvasionCategory(), actualEntity.getInvasionCategory());
    assertEquals(sendTelemetry.getThresholdWindSpeed(), actualEntity.getThresholdWindSpeed());
    assertEquals(sendTelemetry.getBaseId(), actualEntity.getBaseId());
    assertEquals(sendTelemetry.getBaseAddress(), actualEntity.getBaseAddress());
    assertEquals(sendTelemetry.getBaseName(), actualEntity.getBaseName());
    assertEquals(sendTelemetry.getBaseStatus(), actualEntity.getBaseStatus());
    assertEquals(sendTelemetry.getUsage(), actualEntity.getUsage());
    assertEquals(sendTelemetry.getErrorCode(), actualEntity.getErrorCode());
    assertEquals(sendTelemetry.getErrorReason(), actualEntity.getErrorReason());
  }

  /**
   * メソッド名: configure_Telemetry_invalid<br>
   * 試験名: テレメトリ情報が不正の場合の動作を確認する<br>
   * 条件: テレメトリ情報の離着陸場ID:null<br>
   * 結果: DB登録が呼び出されないこと<br>
   * テストパターン：異常系
   */
  @Test
  public void configure_Telemetry_invalid() throws Exception {
    String strZonedDateTime = "2023-10-01T12:00:00Z";

    // DB登録をモック化
    doReturn(null).when(visTelemetryInfoRepository).save(any(VisTelemetryInfoEntity.class));

    // VisRcvInfoRouteのルートを書き換える
    AdviceWith.adviceWith(
        camelContext,
        VisRcvInfoRoute.class.getSimpleName(),
        a -> {
          a.replaceFromWith("direct:start"); // fromの定義を書き換える
          a.weaveAddLast().to("mock:result"); // ルートの最後に追加
        });

    // コンテキストを開始
    camelContextLifecycle.start();

    // ルートが設定されていることを確認
    assertNotNull(camelContext.getRoute(VisRcvInfoRoute.class.getSimpleName()));

    // VISから受信したテレメトリ情報を作成する(これが実施後の期待値となる)
    TelemetryInfo sendTelemetry = createTestTelemetryInfo(strZonedDateTime);
    sendTelemetry.setDroneportId(null);
    String sendBody = objectMapper.writeValueAsString(sendTelemetry);
    // メッセージを送信してテストを実施
    producerTemplate.sendBodyAndHeader(
        "direct:start", sendBody, PahoConstants.MQTT_TOPIC, "dummyTopic");

    // 最終ルートに到達したことを確認
    mockEndpoint.assertIsSatisfied();

    // DB登録の呼出を確認
    verify(visTelemetryInfoRepository, times(0)).save(any());
  }
}
