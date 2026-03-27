package com.hitachi.droneroute.dpm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListResponseDto;
import com.hitachi.droneroute.dpm.service.DronePortReserveInfoService;
import com.hitachi.droneroute.dpm.validator.DronePortInfoValidator;
import com.hitachi.droneroute.dpm.validator.DronePortReserveInfoValidator;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/** DronePortReserveInfoControllerクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class DronePortReserveInfoControllerTest {
  @Value("${droneroute.basepath}/droneport/reserve")
  String basePath;

  MockMvc mockMvc;

  @Autowired WebApplicationContext webApplicationContext;

  @MockBean DronePortReserveInfoValidator validator;

  @MockBean DronePortInfoValidator dronePortInfoValidator;

  @MockBean DronePortReserveInfoService service;

  @SpyBean SystemSettings systemSettings;

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場予約情報詳細取得が正しく行われる<br>
   * 条件: 正常な離着陸場予約IDを渡す<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getDetail_正常() throws Exception {
    String dronePortReservationId = UUID.randomUUID().toString();
    String dronePortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(dronePortReservationId);

    DronePortReserveInfoDetailResponseDto responseDto = new DronePortReserveInfoDetailResponseDto();
    responseDto.setDronePortReservationId(dronePortReservationId);
    responseDto.setDronePortId(dronePortId);
    when(service.getDetail(dronePortReservationId)).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + dronePortReservationId))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.dronePortId").value(dronePortId),
                jsonPath("$.dronePortReservationId").value(dronePortReservationId))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortReservationId);
    verify(service, times(1)).getDetail(dronePortReservationId);

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getDetail_入力チェックエラー発生() throws Exception {
    String dronePortReservationId = "dummyuuid";

    // 入力エラーを意図的に発生させる
    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForGetDetail(dronePortReservationId);

    DronePortReserveInfoDetailResponseDto responseDto = new DronePortReserveInfoDetailResponseDto();
    when(service.getDetail(dronePortReservationId)).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + dronePortReservationId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortReservationId);
    verify(service, times(0)).getDetail(anyString());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getDetail_サービスエラー発生() throws Exception {
    String dronePortReservationId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(dronePortReservationId);
    doThrow(new ServiceErrorException("ServiceErrorMessage"))
        .when(service)
        .getDetail(dronePortReservationId);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + dronePortReservationId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortReservationId);
    verify(service, times(1)).getDetail(anyString());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報詳細取得APIが呼び出されない<br>
   * 条件: 離着陸場予約IDを未設定<br>
   * 結果: NotFoundが返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void getDetail_離着陸場予約ID未設定() throws Exception {
    String droneReservationPortId = "";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + droneReservationPortId))
            .andExpect(status().isInternalServerError())
            .andReturn();

    verify(validator, times(0)).validateForGetDetail(anyString());
    verify(service, times(0)).getDetail(anyString());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  private DronePortReserveInfoListRequestDto createDronePortReserveInfoListRequestDto() {
    DronePortReserveInfoListRequestDto ret = new DronePortReserveInfoListRequestDto();
    ret.setAircraftId("dummyAircraftId");
    ret.setRouteReservationId("dummyRouteId");
    ret.setTimeFrom("2024-09-01T12:34:56");
    ret.setTimeTo("2024-09-01T12:45:00");
    ret.setPerPage("50");
    ret.setPage("1");
    ret.setSortOrders("1,0");
    ret.setSortColumns("reservationTime,dronePortId");

    return ret;
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場予約情報一覧取得が正しく行われる<br>
   * 条件: 正常な DronePortInfoListRequestDto を渡す<br>
   * 　　　　　クエリパラメータにソート関連項目の設定なし<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getList_正常() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    DronePortReserveInfoListResponseDto responseDto = new DronePortReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    DronePortReserveInfoListRequestDto requestDto = createDronePortReserveInfoListRequestDto();
    String queryParam =
        "dronePortType="
            + "&aircraftId="
            + requestDto.getAircraftId()
            + "&routeReservationId="
            + requestDto.getRouteReservationId()
            + "&timeFrom="
            + requestDto.getTimeFrom()
            + "&timeTo="
            + requestDto.getTimeTo()
            + "&perPage="
            + requestDto.getPerPage()
            + "&page="
            + requestDto.getPage();

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場予約情報一覧取得が正しく行われる<br>
   * 条件: 正常な DronePortInfoListRequestDto を渡す<br>
   * 　　　　　クエリパラメータにページ、ソート関連項目の設定あり<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getList_正常2() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    DronePortReserveInfoListResponseDto responseDto = new DronePortReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    DronePortReserveInfoListRequestDto requestDto = createDronePortReserveInfoListRequestDto();
    requestDto.setPage("2");
    requestDto.setPerPage("12");
    requestDto.setSortOrders("0,0,1");
    requestDto.setSortColumns("a,b,c");
    String queryParam =
        "dronePortType="
            + "&aircraftId="
            + requestDto.getAircraftId()
            + "&routeReservationId="
            + requestDto.getRouteReservationId()
            + "&timeFrom="
            + requestDto.getTimeFrom()
            + "&timeTo="
            + requestDto.getTimeTo()
            + "&perPage="
            + requestDto.getPerPage()
            + "&page="
            + requestDto.getPage()
            + "&sortOrders="
            + requestDto.getSortOrders()
            + "&sortColumns="
            + requestDto.getSortColumns();

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getList_入力チェックエラー発生() throws Exception {

    doThrow(new ValidationErrorException("dummyMessage")).when(validator).validateForGetList(any());

    DronePortReserveInfoListResponseDto responseDto = new DronePortReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    // String queryParam = "drone_port_type=" + "1";
    String queryParam = "dronePortType=" + "1";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list?" + queryParam))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(0)).getList(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getList_サービスエラー発生() throws Exception {

    doNothing().when(validator).validateForGetList(any());
    doThrow(new ServiceErrorException("dummyMessage")).when(service).getList(any());

    String queryParam = "dronePortType=" + "1";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list?" + queryParam))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(1)).getList(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報一覧取得が正しく行われる<br>
   * 条件: クエリパラメータなし<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void getList_クエリパラメータ未設定() throws Exception {

    doNothing().when(validator).validateForGetList(any());
    DronePortReserveInfoListResponseDto responseDto = new DronePortReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list"))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(1)).getList(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 離着陸場予約情報公開データ抽出が正しく行われる<br>
   * 条件: 正常な DronePortInfoListRequestDto を渡す<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    DronePortReserveInfoListResponseDto responseDto = new DronePortReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    DronePortReserveInfoListRequestDto requestDto = new DronePortReserveInfoListRequestDto();
    requestDto.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
    requestDto.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e31");
    requestDto.setDronePortName("離着陸場1");
    requestDto.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    requestDto.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    requestDto.setTimeFrom("2026-01-01T10:00:00+09:00");
    requestDto.setTimeTo("2026-01-01T12:00:00+09:00");
    requestDto.setReserveProviderId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61");
    String queryParam =
        "groupReservationId="
            + requestDto.getGroupReservationId()
            + "&dronePortId="
            + requestDto.getDronePortId()
            + "&dronePortName="
            + requestDto.getDronePortName()
            + "&aircraftId="
            + requestDto.getAircraftId()
            + "&routeReservationId="
            + requestDto.getRouteReservationId()
            + "&timeFrom="
            + requestDto.getTimeFrom()
            + "&timeTo="
            + requestDto.getTimeTo()
            + "&reserveProviderId="
            + requestDto.getReserveProviderId();

    // 結果確認のため固定値設定項目を上書き
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setPage(null);
    requestDto.setPerPage(null);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 離着陸場予約情報公開データ抽出が正しく行われる<br>
   * 条件: 定義外項目が存在する DronePortInfoListRequestDto を渡す<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常_定義外項目あり() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    DronePortReserveInfoListResponseDto responseDto = new DronePortReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    DronePortReserveInfoListRequestDto requestDto = new DronePortReserveInfoListRequestDto();
    requestDto.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
    requestDto.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e31");
    requestDto.setDronePortName("離着陸場1");
    requestDto.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    requestDto.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    requestDto.setTimeFrom("2026-01-01T10:00:00+09:00");
    requestDto.setTimeTo("2026-01-01T12:00:00+09:00");
    requestDto.setReserveProviderId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61");
    String queryParam =
        "groupReservationId="
            + requestDto.getGroupReservationId()
            + "&dronePortId="
            + requestDto.getDronePortId()
            + "&dronePortName="
            + requestDto.getDronePortName()
            + "&aircraftId="
            + requestDto.getAircraftId()
            + "&routeReservationId="
            + requestDto.getRouteReservationId()
            + "&timeFrom="
            + requestDto.getTimeFrom()
            + "&timeTo="
            + requestDto.getTimeTo()
            + "&reserveProviderId="
            + requestDto.getReserveProviderId()
            + "&teigigai="
            + "teigigai";

    // 結果確認のため固定値設定項目を上書き
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setPage(null);
    requestDto.setPerPage(null);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 離着陸場予約情報公開データ抽出が正しく行われる<br>
   * 条件: 正クエリ未設定で実施<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常_クエリ未設定() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    DronePortReserveInfoListResponseDto responseDto = new DronePortReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    DronePortReserveInfoListRequestDto requestDto = new DronePortReserveInfoListRequestDto();

    // 結果確認のため固定値設定項目を上書き
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setPage(null);
    requestDto.setPerPage(null);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getPublicDataExtract_チェックエラー() throws Exception {

    doThrow(new ValidationErrorException("dummyMessage")).when(validator).validateForGetList(any());

    DronePortReserveInfoListResponseDto responseDto = new DronePortReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(0)).getList(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getPublicDataExtract_サービスエラー() throws Exception {

    doNothing().when(validator).validateForGetList(any());
    doThrow(new ServiceErrorException("dummyMessage")).when(service).getList(any());

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(1)).getList(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getPublicDataExtract_予期せぬエラー() throws Exception {

    doNothing().when(validator).validateForGetList(any());
    doThrow(new IllegalArgumentException("予期せぬ例外が発生")).when(service).getList(any());

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("予期せぬ例外が発生"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(1)).getList(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }
}
