package com.hitachi.droneroute.arm.controller;

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
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListResponseDto;
import com.hitachi.droneroute.arm.service.AircraftReserveInfoService;
import com.hitachi.droneroute.arm.validator.AircraftInfoValidator;
import com.hitachi.droneroute.arm.validator.AircraftReserveInfoValidator;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
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

/** AircraftReserveInfoControllerクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class AircraftReserveInfoControllerTest {

  MockMvc mockMvc;

  @Value("${droneroute.basepath}/aircraft/reserve")
  String basePath;

  @Autowired WebApplicationContext webApplicationContext;

  @MockBean private AircraftReserveInfoService service;

  @MockBean private AircraftReserveInfoValidator validator;

  @MockBean private AircraftInfoValidator aircraftInfoValidator;

  @SpyBean private SystemSettings systemSettings;

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報一覧取得が正しく行われる<br>
   * 条件: 正常な AircraftReserveInfoListRequestDto を渡す<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getList_正常系() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    AircraftReserveInfoListResponseDto responseDto = new AircraftReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    AircraftReserveInfoListRequestDto requestDto = new AircraftReserveInfoListRequestDto();
    UUID aircraftId = UUID.randomUUID();
    String aircraftName = "aircraftName";
    String timeFrom = ZonedDateTime.now().toString();
    String timeTo = ZonedDateTime.now().toString();
    String perPage = "1";
    String page = "1";
    String sortOrders = "1,0";
    String sortColumns = "aircraftId,aircraftName";
    requestDto.setAircraftId(aircraftId.toString());
    requestDto.setAircraftName(aircraftName);
    requestDto.setTimeFrom(timeFrom);
    requestDto.setTimeTo(timeTo);
    requestDto.setPerPage(perPage);
    requestDto.setPage(page);
    requestDto.setSortOrders(sortOrders);
    requestDto.setSortColumns(sortColumns);

    String queryParam =
        "aircraftId="
            + requestDto.getAircraftId()
            + "&aircraftName="
            + requestDto.getAircraftName()
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
      ArgumentCaptor<AircraftReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(requestDto.getAircraftName(), dtoCaptor.getValue().getAircraftName());
      assertEquals(requestDto.getTimeFrom(), dtoCaptor.getValue().getTimeFrom());
      assertEquals(requestDto.getTimeTo(), dtoCaptor.getValue().getTimeTo());
      assertEquals(requestDto.getPerPage(), dtoCaptor.getValue().getPerPage());
      assertEquals(requestDto.getPage(), dtoCaptor.getValue().getPage());
      assertEquals(requestDto.getSortOrders(), dtoCaptor.getValue().getSortOrders());
      assertEquals(requestDto.getSortColumns(), dtoCaptor.getValue().getSortColumns());
    }
    {
      ArgumentCaptor<AircraftReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(requestDto.getAircraftName(), dtoCaptor.getValue().getAircraftName());
      assertEquals(requestDto.getTimeFrom(), dtoCaptor.getValue().getTimeFrom());
      assertEquals(requestDto.getTimeTo(), dtoCaptor.getValue().getTimeTo());
      assertEquals(requestDto.getPerPage(), dtoCaptor.getValue().getPerPage());
      assertEquals(requestDto.getPage(), dtoCaptor.getValue().getPage());
      assertEquals(requestDto.getSortOrders(), dtoCaptor.getValue().getSortOrders());
      assertEquals(requestDto.getSortColumns(), dtoCaptor.getValue().getSortColumns());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void getList_入力チェックエラー発生() throws Exception {
    doThrow(new ValidationErrorException("dummyMessage")).when(validator).validateForGetList(any());

    AircraftReserveInfoListResponseDto responseDto = new AircraftReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    String queryParam = "timeFrom=" + "1";

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
   * テストパターン: 異常系<br>
   */
  @Test
  public void getList_サービスエラー発生() throws Exception {
    doNothing().when(validator).validateForGetList(any());
    doThrow(new ServiceErrorException("dummyMessage")).when(service).getList(any());

    String queryParam = "timeFrom=" + "1";

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
   * 試験名: 機体予約情報一覧取得が正しく行われる<br>
   * 条件: クエリパラメータなし<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void getList_クエリパラメータ未設定() throws Exception {
    doNothing().when(validator).validateForGetList(any());

    AircraftReserveInfoListResponseDto responseDto = new AircraftReserveInfoListResponseDto();
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
   * 試験名: 機体予約情報一覧取得が正しく行われる<br>
   * 条件: 正常な AircraftReserveInfoListRequestDto を渡す<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常系() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    AircraftReserveInfoListResponseDto responseDto = new AircraftReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    AircraftReserveInfoListRequestDto requestDto = new AircraftReserveInfoListRequestDto();
    String groupReservationId = "0a0711a5-ff74-4164-9309-8888b433cf21";
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf31";
    String aircraftName = "機体1";
    String timeFrom = "2026-01-01T10:00:00+09:00";
    String timeTo = "2026-01-01T12:00:00+09:00";
    requestDto.setGroupReservationId(groupReservationId);
    requestDto.setAircraftId(aircraftId);
    requestDto.setAircraftName(aircraftName);
    requestDto.setTimeFrom(timeFrom);
    requestDto.setTimeTo(timeTo);
    requestDto.setPerPage(null);
    requestDto.setPage(null);
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));

    String queryParam =
        "groupReservationId="
            + requestDto.getGroupReservationId()
            + "&aircraftId="
            + requestDto.getAircraftId()
            + "&aircraftName="
            + requestDto.getAircraftName()
            + "&timeFrom="
            + requestDto.getTimeFrom()
            + "&timeTo="
            + requestDto.getTimeTo();

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<AircraftReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(
          requestDto.getGroupReservationId(), dtoCaptor.getValue().getGroupReservationId());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(requestDto.getAircraftName(), dtoCaptor.getValue().getAircraftName());
      assertEquals(requestDto.getTimeFrom(), dtoCaptor.getValue().getTimeFrom());
      assertEquals(requestDto.getTimeTo(), dtoCaptor.getValue().getTimeTo());
    }
    {
      ArgumentCaptor<AircraftReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture());
      assertEquals(
          requestDto.getGroupReservationId(), dtoCaptor.getValue().getGroupReservationId());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(requestDto.getAircraftName(), dtoCaptor.getValue().getAircraftName());
      assertEquals(requestDto.getTimeFrom(), dtoCaptor.getValue().getTimeFrom());
      assertEquals(requestDto.getTimeTo(), dtoCaptor.getValue().getTimeTo());
      assertEquals(requestDto.getPerPage(), dtoCaptor.getValue().getPerPage());
      assertEquals(requestDto.getPage(), dtoCaptor.getValue().getPage());
      assertEquals(requestDto.getSortOrders(), dtoCaptor.getValue().getSortOrders());
      assertEquals(requestDto.getSortColumns(), dtoCaptor.getValue().getSortColumns());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 機体予約情報一覧取得が正しく行われる<br>
   * 条件: 定義外項目が存在する AircraftReserveInfoListRequestDto を渡す<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常系_定義外項目あり() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    AircraftReserveInfoListResponseDto responseDto = new AircraftReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    AircraftReserveInfoListRequestDto requestDto = new AircraftReserveInfoListRequestDto();
    String groupReservationId = "0a0711a5-ff74-4164-9309-8888b433cf21";
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf31";
    String aircraftName = "機体1";
    String timeFrom = "2026-01-01T10:00:00+09:00";
    String timeTo = "2026-01-01T12:00:00+09:00";
    requestDto.setGroupReservationId(groupReservationId);
    requestDto.setAircraftId(aircraftId);
    requestDto.setAircraftName(aircraftName);
    requestDto.setTimeFrom(timeFrom);
    requestDto.setTimeTo(timeTo);
    requestDto.setPerPage(null);
    requestDto.setPage(null);
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));

    String queryParam =
        "groupReservationId="
            + requestDto.getGroupReservationId()
            + "&aircraftId="
            + requestDto.getAircraftId()
            + "&aircraftName="
            + requestDto.getAircraftName()
            + "&timeFrom="
            + requestDto.getTimeFrom()
            + "&timeTo="
            + requestDto.getTimeTo()
            + "&teigigai="
            + "teigigai";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<AircraftReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(
          requestDto.getGroupReservationId(), dtoCaptor.getValue().getGroupReservationId());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(requestDto.getAircraftName(), dtoCaptor.getValue().getAircraftName());
      assertEquals(requestDto.getTimeFrom(), dtoCaptor.getValue().getTimeFrom());
      assertEquals(requestDto.getTimeTo(), dtoCaptor.getValue().getTimeTo());
    }
    {
      ArgumentCaptor<AircraftReserveInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture());
      assertEquals(
          requestDto.getGroupReservationId(), dtoCaptor.getValue().getGroupReservationId());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(requestDto.getAircraftName(), dtoCaptor.getValue().getAircraftName());
      assertEquals(requestDto.getTimeFrom(), dtoCaptor.getValue().getTimeFrom());
      assertEquals(requestDto.getTimeTo(), dtoCaptor.getValue().getTimeTo());
      assertEquals(requestDto.getPerPage(), dtoCaptor.getValue().getPerPage());
      assertEquals(requestDto.getPage(), dtoCaptor.getValue().getPage());
      assertEquals(requestDto.getSortOrders(), dtoCaptor.getValue().getSortOrders());
      assertEquals(requestDto.getSortColumns(), dtoCaptor.getValue().getSortColumns());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 機体予約情報一覧取得が正しく行われる<br>
   * 条件: クエリパラメータなし<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void getPublicDataExtract_クエリパラメータ未設定() throws Exception {
    doNothing().when(validator).validateForGetList(any());

    AircraftReserveInfoListResponseDto responseDto = new AircraftReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(1)).getList(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void getPublicDataExtract_入力チェックエラー発生() throws Exception {
    doThrow(new ValidationErrorException("dummyMessage")).when(validator).validateForGetList(any());

    AircraftReserveInfoListResponseDto responseDto = new AircraftReserveInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any())).thenReturn(responseDto);

    String queryParam = "timeFrom=" + "1";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract?" + queryParam))
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
   * テストパターン: 異常系<br>
   */
  @Test
  public void getPublicDataExtract_サービスエラー発生() throws Exception {
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
   * 試験名: 予期せぬエラー発生時の動作<br>
   * 条件: サービスでIllegalArgumentExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void getPublicDataExtract_予期せぬエラー発生() throws Exception {
    doNothing().when(validator).validateForGetList(any());
    doThrow(new IllegalArgumentException("dummyMessage")).when(service).getList(any());

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
   * メソッド名: getDetail<br>
   * 試験名: 機体予約情報詳細取得が正しく行われる<br>
   * 条件: 正常な機体予約IDを渡す<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getDetail_正常() throws Exception {
    String aircraftReserveId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftReserveId);

    AircraftReserveInfoDetailResponseDto responseDto = new AircraftReserveInfoDetailResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);
    when(service.getDetail(aircraftReserveId)).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftReserveId))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.aircraftReservationId").value(aircraftReserveId))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftReserveId);
    verify(service, times(1)).getDetail(aircraftReserveId);

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void getDetail_入力チェックエラー発生() throws Exception {
    String aircraftReserveId = "dummyId";

    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForDetail(aircraftReserveId);

    AircraftReserveInfoDetailResponseDto responseDto = new AircraftReserveInfoDetailResponseDto();
    when(service.getDetail(aircraftReserveId)).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftReserveId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftReserveId);
    verify(service, times(0)).getDetail(anyString());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void getDetail_サービスエラー発生() throws Exception {
    String aircraftReserveId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftReserveId);
    doThrow(new ServiceErrorException("ServiceErrorMessage"))
        .when(service)
        .getDetail(aircraftReserveId);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftReserveId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorMessage"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftReserveId);
    verify(service, times(1)).getDetail(anyString());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体予約情報詳細取得APIが呼び出されない<br>
   * 条件: 機体予約IDを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void getDetail_機体予約ID未設定() throws Exception {
    String aircraftReserveId = "";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftReserveId))
            .andExpect(status().isInternalServerError())
            .andReturn();

    verify(validator, times(0)).validateForDetail(anyString());
    verify(service, times(0)).getDetail(anyString());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }
}
