package com.hitachi.droneroute.dpm.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.vis.DroneportReserveListGetDto;
import com.hitachi.droneroute.dpm.dto.vis.ReserveList;
import com.hitachi.droneroute.dpm.mqtt.VisMqttPublisher;
import com.hitachi.droneroute.dpm.service.DronePortReserveInfoService;
import java.util.UUID;
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
    properties =
        "systemsettings.settingclass.visConnectInfo.enableSubscribeQueryReservationRequest=true")
@CamelSpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseAdviceWith
public class VisRcvQueryReservationRequestRouteEnableTest {

  @MockBean VisMqttPublisher mqttPublisher;

  @MockBean DronePortReserveInfoService service;

  @SpyBean private SystemSettings systemSettings;

  @Autowired private CamelContext camelContext;

  @Autowired private CamelContextLifecycle camelContextLifecycle;

  @Produce("direct:start")
  private ProducerTemplate producerTemplate;

  @EndpointInject("mock:result")
  private MockEndpoint mockEndpoint;

  private ObjectMapper objectMapper = new ObjectMapper();

  private DroneportReserveListGetDto createDroneportReserveListGetDto() {
    String reservationId = UUID.randomUUID().toString();
    DroneportReserveListGetDto ret = new DroneportReserveListGetDto();
    ret.setDroneportReservationId(reservationId);

    return ret;
  }

  private DronePortReserveInfoDetailResponseDto createDronePortReserveInfoDetailResponseDto() {
    DronePortReserveInfoDetailResponseDto detail = new DronePortReserveInfoDetailResponseDto();

    detail.setDronePortReservationId("DPR001");
    detail.setDronePortId("DP001");
    detail.setGroupReservationId("GR001");
    detail.setAircraftId("AC001");
    detail.setRouteReservationId("RR001");
    detail.setUsageType(1);
    detail.setReservationTimeFrom("");
    detail.setReservationTimeTo("");
    detail.setDronePortName("DronePortA");
    detail.setAircraftName("AircraftA");
    detail.setVisDronePortCompanyId("VDP001");
    detail.setReservationActiveFlag(true);
    detail.setReserveProviderId("RP001");
    detail.setOperatorId("Ope001");

    return detail;
  }

  /**
   * メソッド名: configure_enableSubscribeQueryReservationRequestTrue<br>
   * 試験名: enableSubscribeQueryReservationRequestがtrueの場合のルート設定を確認する<br>
   * 条件: enableSubscribeQueryReservationRequestがtrueの場合<br>
   * 結果: ルートが設定されること<br>
   * テストパターン：正常系
   */
  @Test
  public void configure_enableSubscribeQueryReservationRequestTrue() throws Exception {

    // 予約情報検索をモック化
    DronePortReserveInfoDetailResponseDto detailDto = createDronePortReserveInfoDetailResponseDto();
    when(service.getDetail(any())).thenReturn(detailDto);

    // VisRcvQueryReservationRequestRouteのルートを書き換える
    AdviceWith.adviceWith(
        camelContext,
        VisRcvQueryReservationRequestRoute.class.getSimpleName(),
        a -> {
          a.replaceFromWith("direct:start"); // fromの定義を書き換える
          a.weaveAddLast().to("mock:result"); // ルートの最後に追加
        });

    // コンテキストを開始
    camelContextLifecycle.start();

    // ルートが設定されていることを確認
    assertNotNull(camelContext.getRoute(VisRcvQueryReservationRequestRoute.class.getSimpleName()));

    DroneportReserveListGetDto sendDto = createDroneportReserveListGetDto();
    // VISから受信した予約照会を作成する
    String sendBody = objectMapper.writeValueAsString(sendDto);
    // メッセージを送信してテストを実施
    producerTemplate.sendBodyAndHeader(
        "direct:start", sendBody, PahoConstants.MQTT_TOPIC, "dummyTopic");

    // 最終ルートに到達したことを確認
    mockEndpoint.assertIsSatisfied();
    ArgumentCaptor<ReserveList> resDtoCaptor = ArgumentCaptor.forClass(ReserveList.class);
    verify(mqttPublisher).publishQueryReservationResponse(any(), resDtoCaptor.capture());
    ReserveList capResDto = resDtoCaptor.getValue();
    assertEquals(
        detailDto.getDronePortReservationId(),
        capResDto.getReservationInfo().get(0).getReservationId());
    assertEquals(detailDto.getDronePortId(), capResDto.getDroneportId());
    assertTrue(capResDto.isResponseStatus());
  }

  /**
   * メソッド名: configure_enableSubscribeQueryReservationRequestTrue<br>
   * 試験名: reservationIdがnullの場合のルート設定を確認する<br>
   * 条件: reservationIdがnullの場合<br>
   * 結果: ルートが設定されること<br>
   * テストパターン：正常系
   */
  @Test
  public void configure_enableSubscribeQueryReservationRequestTrue_() throws Exception {

    // 予約情報検索をモック化
    DronePortReserveInfoDetailResponseDto detailDto = createDronePortReserveInfoDetailResponseDto();
    when(service.getDetail(any())).thenReturn(detailDto);

    // VisRcvQueryReservationRequestRouteのルートを書き換える
    AdviceWith.adviceWith(
        camelContext,
        VisRcvQueryReservationRequestRoute.class.getSimpleName(),
        a -> {
          a.replaceFromWith("direct:start"); // fromの定義を書き換える
          a.weaveAddLast().to("mock:result"); // ルートの最後に追加
        });

    // コンテキストを開始
    camelContextLifecycle.start();

    // ルートが設定されていることを確認
    assertNotNull(camelContext.getRoute(VisRcvQueryReservationRequestRoute.class.getSimpleName()));

    DroneportReserveListGetDto sendDto = new DroneportReserveListGetDto();
    sendDto.setDroneportReservationId(null);
    // VISから受信した予約照会を作成する
    String sendBody = objectMapper.writeValueAsString(sendDto);
    // メッセージを送信してテストを実施
    producerTemplate.sendBodyAndHeader(
        "direct:start", sendBody, PahoConstants.MQTT_TOPIC, "dummyTopic");

    // 最終ルートに到達したことを確認
    mockEndpoint.assertIsSatisfied();
    ArgumentCaptor<ReserveList> resDtoCaptor = ArgumentCaptor.forClass(ReserveList.class);
    verify(mqttPublisher).publishQueryReservationResponse(any(), resDtoCaptor.capture());
    ReserveList capResDto = resDtoCaptor.getValue();
    assertEquals("離着陸場予約IDが未設定です", capResDto.getInfo());
    assertNull(capResDto.getReservationInfo());
    assertFalse(capResDto.isResponseStatus());
  }

  /**
   * メソッド名: configure_enableSubscribeQueryReservationRequestTrue<br>
   * 試験名: 予約情報検索でNotFoundExceptionが発生する場合のルート設定を確認する<br>
   * 条件: 予約情報検索でNotFoundExceptionが発生する場合<br>
   * 結果: ルートが設定されること<br>
   * テストパターン：正常系
   */
  @Test
  public void configure_enableSubscribeQueryReservationRequestTrue_NotFound() throws Exception {

    // 予約情報検索をモック化
    String errMsg = "DB検索エラー";
    when(service.getDetail(any())).thenThrow(new NotFoundException(errMsg));

    // VisRcvQueryReservationRequestRouteのルートを書き換える
    AdviceWith.adviceWith(
        camelContext,
        VisRcvQueryReservationRequestRoute.class.getSimpleName(),
        a -> {
          a.replaceFromWith("direct:start"); // fromの定義を書き換える
          a.weaveAddLast().to("mock:result"); // ルートの最後に追加
        });

    // コンテキストを開始
    camelContextLifecycle.start();

    // ルートが設定されていることを確認
    assertNotNull(camelContext.getRoute(VisRcvQueryReservationRequestRoute.class.getSimpleName()));

    DroneportReserveListGetDto sendDto = createDroneportReserveListGetDto();
    // VISから受信した予約照会を作成する
    String sendBody = objectMapper.writeValueAsString(sendDto);
    // メッセージを送信してテストを実施
    producerTemplate.sendBodyAndHeader(
        "direct:start", sendBody, PahoConstants.MQTT_TOPIC, "dummyTopic");

    // 最終ルートに到達したことを確認
    mockEndpoint.assertIsSatisfied();
    ArgumentCaptor<ReserveList> resDtoCaptor = ArgumentCaptor.forClass(ReserveList.class);
    verify(mqttPublisher).publishQueryReservationResponse(any(), resDtoCaptor.capture());
    ReserveList capResDto = resDtoCaptor.getValue();
    assertEquals(errMsg, capResDto.getInfo());
    assertNull(capResDto.getReservationInfo());
    assertFalse(capResDto.isResponseStatus());
  }
}
