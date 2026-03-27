package com.hitachi.droneroute.dpm.mqtt;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.dto.vis.ReservationInfo;
import com.hitachi.droneroute.dpm.dto.vis.ReserveList;
import java.util.UUID;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@CamelSpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VisMqttPublisherTest {

  @SpyBean private ObjectMapper objectMapper;

  @SpyBean private SystemSettings systemSettings;

  /**
   * メソッド名: publishQueryReservationResponse<br>
   * 試験名: 正常に照会応答をpublishすることを確認する<br>
   * 条件: 正常な照会応答DTOを渡す<br>
   * 結果: 正常終了すること<br>
   * テストパターン：正常系
   */
  @Test
  void testPublishQueryReservationResponse() throws Exception {
    String visPortId = "visPortId";
    ReserveList reserveList = new ReserveList();
    reserveList.setDroneportId(visPortId);
    reserveList.setResponseStatus(true);
    ReservationInfo reserveInfo = new ReservationInfo();
    reserveInfo.setReservationId(UUID.randomUUID().toString());
    reserveInfo.setStartTime("2024-10-15T08:16:26Z");
    reserveInfo.setEndTime("2024-10-15T08:17:26Z");

    String json = objectMapper.writeValueAsString(reserveList);

    try (MockedConstruction<MqttClient> mocked =
        mockConstruction(
            MqttClient.class,
            (mock, context) -> {
              when(mock.isConnected()).thenReturn(true);
              doNothing().when(mock).connect(any(MqttConnectOptions.class));
              doNothing()
                  .when(mock)
                  .publish(anyString(), any(byte[].class), anyInt(), anyBoolean());
            })) {
      VisMqttPublisher publisher = new VisMqttPublisher(systemSettings);
      publisher.publishQueryReservationResponse(visPortId, reserveList);

      MqttClient mock = mocked.constructed().get(0); // 生成されたモックにアクセス

      verify(mock).connect(any(MqttConnectOptions.class));
      ArgumentCaptor<MqttMessage> msgCap = ArgumentCaptor.forClass(MqttMessage.class);
      verify(mock).publish(any(), msgCap.capture());
      MqttMessage capMsg = msgCap.getValue();
      assertArrayEquals(json.getBytes(), capMsg.getPayload());
      verify(mock).disconnect();
      verify(mock).close();
    }
  }

  /**
   * メソッド名: publishQueryReservationResponse<br>
   * 試験名: JsonProcessingException発生時に適切にエラーハンドリングされることを確認する<br>
   * 条件: 照会応答DTOをJSONに変換する際にJsonProcessingExceptionを発生させる<br>
   * 結果: 期待した内容のExceptionがthrowされることと<br>
   * テストパターン：異常系
   */
  @Test
  void testPublishQueryReservationResponse_throw_changeJson() throws Exception {
    String visPortId = "visPortId";
    ReserveList reserveList = new ReserveList();
    reserveList.setDroneportId(visPortId);
    reserveList.setResponseStatus(true);
    ReservationInfo reserveInfo = new ReservationInfo();
    reserveInfo.setReservationId(UUID.randomUUID().toString());
    reserveInfo.setStartTime("2024-10-15T08:16:26Z");
    reserveInfo.setEndTime("2024-10-15T08:17:26Z");

    try (MockedConstruction<ObjectMapper> mocked =
        mockConstruction(
            ObjectMapper.class,
            (mock, context) -> {
              when(mock.writeValueAsString(any())).thenThrow(new JsonProcessingException("") {});
            })) {
      VisMqttPublisher publisher = new VisMqttPublisher(systemSettings);
      ServiceErrorException exception =
          assertThrows(
              ServiceErrorException.class,
              () -> publisher.publishQueryReservationResponse(visPortId, reserveList));
      assertEquals("照会応答DTOのJSON化に失敗しました", exception.getMessage());
    }
  }

  /**
   * メソッド名: publishQueryReservationResponse<br>
   * 試験名: MqttException発生時に適切にエラーハンドリングされることを確認する<br>
   * 条件: MqttClientの接続時にMqttExceptionを発生させる<br>
   * 結果: 期待した内容のExceptionがthrowされることと<br>
   * テストパターン：異常系
   */
  @Test
  void testPublishQueryReservationResponse_throw_publish() throws Exception {
    String visPortId = "visPortId";
    ReserveList reserveList = new ReserveList();
    reserveList.setDroneportId(visPortId);
    reserveList.setResponseStatus(true);
    ReservationInfo reserveInfo = new ReservationInfo();
    reserveInfo.setReservationId(UUID.randomUUID().toString());
    reserveInfo.setStartTime("2024-10-15T08:16:26Z");
    reserveInfo.setEndTime("2024-10-15T08:17:26Z");

    try (MockedConstruction<MqttClient> mocked =
        mockConstruction(
            MqttClient.class,
            (mock, context) -> {
              when(mock.isConnected()).thenReturn(true);
              doThrow(new MqttException(0)).when(mock).connect(any(MqttConnectOptions.class));
            })) {
      VisMqttPublisher publisher = new VisMqttPublisher(systemSettings);
      ServiceErrorException exception =
          assertThrows(
              ServiceErrorException.class,
              () -> publisher.publishQueryReservationResponse(visPortId, reserveList));
      assertEquals("publishに失敗しました", exception.getMessage());
    }
  }
}
