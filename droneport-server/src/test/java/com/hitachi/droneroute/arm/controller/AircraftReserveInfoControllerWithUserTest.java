package com.hitachi.droneroute.arm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoResponseDto;
import com.hitachi.droneroute.arm.service.AircraftReserveInfoService;
import com.hitachi.droneroute.arm.validator.AircraftInfoValidator;
import com.hitachi.droneroute.arm.validator.AircraftReserveInfoValidator;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.config.dto.RoleInfoDto;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** AircraftReserveInfoControllerクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
public class AircraftReserveInfoControllerWithUserTest {

  @Value("${droneroute.basepath}/aircraft/reserve")
  String basePath;

  @Autowired MockMvc mockMvc;

  @MockBean private AircraftReserveInfoService service;

  @MockBean private AircraftReserveInfoValidator validator;

  @MockBean private AircraftInfoValidator aircraftInfoValidator;

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    // テスト用UserInfoDtoをSecurityContextに設定
    List<RoleInfoDto> roles = new ArrayList<>();
    RoleInfoDto roleInfoDto = new RoleInfoDto();
    roleInfoDto.setRoleId("10");
    roleInfoDto.setRoleName("航路運営者_責任者");
    roles.add(roleInfoDto);

    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setUserOperatorId("ope01");
    testUserInfo.setRoles(roles);
    testUserInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf41");

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_10"));

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  /**
   * メソッド名: post<br>
   * 試験名: 機体予約情報登録処理が正しく行われる<br>
   * 条件: 正常な AircraftReserveInfoRequestDto を渡す<br>
   * 結果: 処理結果(機体予約ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void post_正常() throws Exception {
    AircraftReserveInfoRequestDto requestDto = new AircraftReserveInfoRequestDto();
    String aircraftId = UUID.randomUUID().toString();
    String aircraftReserveId = UUID.randomUUID().toString();
    requestDto.setAircraftId(aircraftId);
    requestDto.setAircraftReservationId(aircraftReserveId);
    requestDto.setReservationTimeFrom(ZonedDateTime.now().toString());
    requestDto.setReservationTimeTo(ZonedDateTime.now().toString());

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doReturn(responseDto).when(service).postData(any(), any());
    doNothing().when(validator).validateForRegist(requestDto);

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftReservationId").value(aircraftReserveId))
            .andReturn();

    {
      ArgumentCaptor<AircraftReserveInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(
          requestDto.getAircraftReservationId(), dtoCaptor.getValue().getAircraftReservationId());
      assertEquals(
          requestDto.getReservationTimeFrom(), dtoCaptor.getValue().getReservationTimeFrom());
      assertEquals(requestDto.getReservationTimeTo(), dtoCaptor.getValue().getReservationTimeTo());
    }
    {
      ArgumentCaptor<AircraftReserveInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(
          requestDto.getAircraftReservationId(), dtoCaptor.getValue().getAircraftReservationId());
      assertEquals(
          requestDto.getReservationTimeFrom(), dtoCaptor.getValue().getReservationTimeFrom());
      assertEquals(requestDto.getReservationTimeTo(), dtoCaptor.getValue().getReservationTimeTo());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void post_入力チェックエラー発生() throws Exception {
    AircraftReserveInfoRequestDto requestDto = new AircraftReserveInfoRequestDto();
    String aircraftId = UUID.randomUUID().toString();
    String aircraftReserveId = UUID.randomUUID().toString();
    requestDto.setAircraftId(aircraftId);
    requestDto.setAircraftReservationId(aircraftReserveId);
    requestDto.setReservationTimeFrom(ZonedDateTime.now().toString());
    requestDto.setReservationTimeTo(ZonedDateTime.now().toString());

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doReturn(responseDto).when(service).postData(any(), any());
    doThrow(new ValidationErrorException("dummyMessage")).when(validator).validateForRegist(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(service, times(0)).postData(any(), any());
    verify(validator, times(1)).validateForRegist(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void post_サービスエラー発生() throws Exception {
    AircraftReserveInfoRequestDto requestDto = new AircraftReserveInfoRequestDto();
    String aircraftId = UUID.randomUUID().toString();
    String aircraftReserveId = UUID.randomUUID().toString();
    requestDto.setAircraftId(aircraftId);
    requestDto.setAircraftReservationId(aircraftReserveId);
    requestDto.setReservationTimeFrom(ZonedDateTime.now().toString());
    requestDto.setReservationTimeTo(ZonedDateTime.now().toString());

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doThrow(new ServiceErrorException("dummyMessage")).when(service).postData(any(), any());
    doNothing().when(validator).validateForRegist(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(service, times(1)).postData(any(), any());
    verify(validator, times(1)).validateForRegist(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 機体予約情報更新APIが呼び出されない<br>
   * 条件: リクエストボディを未設定<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void post_リクエストボディなし() throws Exception {
    String aircraftReserveId = UUID.randomUUID().toString();

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doReturn(responseDto).when(service).postData(any(), any());
    doNothing().when(validator).validateForRegist(any());

    MvcResult response =
        mockMvc
            .perform(
                post(basePath).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).postData(any(), any());
    verify(validator, times(0)).validateForRegist(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 機体予約情報登録処理が正しく行われる<br>
   * 条件: 正常な AircraftReserveInfoRequestDto を渡す<br>
   * 結果: 処理結果(機体予約ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void post_ユーザー情報_一括予約ID() throws Exception {
    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf11";
    AircraftReserveInfoRequestDto requestDto = createAircraftReserveInfoRequestDto();

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doReturn(responseDto).when(service).postData(any(), any());
    doNothing().when(validator).validateForRegist(requestDto);

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftReservationId").value(aircraftReserveId))
            .andReturn();

    verify(validator, times(1)).validateForRegist(any());
    {
      ArgumentCaptor<AircraftReserveInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(
          requestDto.getAircraftReservationId(), dtoCaptor.getValue().getAircraftReservationId());
      assertEquals(
          requestDto.getGroupReservationId(), dtoCaptor.getValue().getGroupReservationId());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(
          requestDto.getReservationTimeFrom(), dtoCaptor.getValue().getReservationTimeFrom());
      assertEquals(requestDto.getReservationTimeTo(), dtoCaptor.getValue().getReservationTimeTo());
    }
    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).postData(any(), userInfoCaptor.capture());
      UserInfoDto capturedUserInfo = userInfoCaptor.getValue();
      assertEquals("ope01", capturedUserInfo.getUserOperatorId());
      assertEquals("10", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("航路運営者_責任者", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "0a0711a5-ff74-4164-9309-8888b433cf41", capturedUserInfo.getAffiliatedOperatorId());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 機体予約情報更新処理が正しく行われる<br>
   * 条件: 正常な AircraftReserveInfoRequestDto を渡す<br>
   * 結果: 処理結果(機体予約ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void put_正常() throws Exception {
    AircraftReserveInfoRequestDto requestDto = new AircraftReserveInfoRequestDto();
    String aircraftId = UUID.randomUUID().toString();
    String aircraftReserveId = UUID.randomUUID().toString();
    requestDto.setAircraftId(aircraftId);
    requestDto.setAircraftReservationId(aircraftReserveId);
    requestDto.setReservationTimeFrom(ZonedDateTime.now().toString());
    requestDto.setReservationTimeTo(ZonedDateTime.now().toString());

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doReturn(responseDto).when(service).putData(any(), any());
    doNothing().when(validator).validateForUpdate(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftReservationId").value(aircraftReserveId))
            .andReturn();

    {
      ArgumentCaptor<AircraftReserveInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(
          requestDto.getAircraftReservationId(), dtoCaptor.getValue().getAircraftReservationId());
      assertEquals(
          requestDto.getReservationTimeFrom(), dtoCaptor.getValue().getReservationTimeFrom());
      assertEquals(requestDto.getReservationTimeTo(), dtoCaptor.getValue().getReservationTimeTo());
    }
    {
      ArgumentCaptor<AircraftReserveInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(
          requestDto.getAircraftReservationId(), dtoCaptor.getValue().getAircraftReservationId());
      assertEquals(
          requestDto.getReservationTimeFrom(), dtoCaptor.getValue().getReservationTimeFrom());
      assertEquals(requestDto.getReservationTimeTo(), dtoCaptor.getValue().getReservationTimeTo());
    }
    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void put_入力チェックエラー発生() throws Exception {
    AircraftReserveInfoRequestDto requestDto = new AircraftReserveInfoRequestDto();
    String aircraftId = UUID.randomUUID().toString();
    String aircraftReserveId = UUID.randomUUID().toString();
    requestDto.setAircraftId(aircraftId);
    requestDto.setAircraftReservationId(aircraftReserveId);
    requestDto.setReservationTimeFrom(ZonedDateTime.now().toString());
    requestDto.setReservationTimeTo(ZonedDateTime.now().toString());

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doReturn(responseDto).when(service).putData(any(), any());
    doThrow(new ValidationErrorException("dummyMessage")).when(validator).validateForUpdate(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(service, times(0)).putData(any(), any());
    verify(validator, times(1)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void put_サービスエラー発生() throws Exception {
    AircraftReserveInfoRequestDto requestDto = new AircraftReserveInfoRequestDto();
    String aircraftId = UUID.randomUUID().toString();
    String aircraftReserveId = UUID.randomUUID().toString();
    requestDto.setAircraftId(aircraftId);
    requestDto.setAircraftReservationId(aircraftReserveId);
    requestDto.setReservationTimeFrom(ZonedDateTime.now().toString());
    requestDto.setReservationTimeTo(ZonedDateTime.now().toString());

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doThrow(new ServiceErrorException("dummyMessage")).when(service).putData(any(), any());
    doNothing().when(validator).validateForUpdate(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(service, times(1)).putData(any(), any());
    verify(validator, times(1)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 機体予約情報更新APIが呼び出されない<br>
   * 条件: リクエストボディを未設定<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void put_リクエストボディなし() throws Exception {
    String aircraftReserveId = UUID.randomUUID().toString();

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doReturn(responseDto).when(service).putData(any(), any());
    doNothing().when(validator).validateForUpdate(any());

    MvcResult response =
        mockMvc
            .perform(
                put(basePath).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).putData(any(), any());
    verify(validator, times(0)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 機体予約情報更新処理が正しく行われる<br>
   * 条件: 正常な AircraftReserveInfoRequestDto を渡す<br>
   * 結果: 処理結果(機体予約ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void put_ユーザー情報_一括予約ID() throws Exception {
    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf11";
    AircraftReserveInfoRequestDto requestDto = createAircraftReserveInfoRequestDto();

    AircraftReserveInfoResponseDto responseDto = new AircraftReserveInfoResponseDto();
    responseDto.setAircraftReservationId(aircraftReserveId);

    doReturn(responseDto).when(service).putData(any(), any());
    doNothing().when(validator).validateForRegist(requestDto);

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftReservationId").value(aircraftReserveId))
            .andReturn();

    verify(validator, times(1)).validateForUpdate(any());
    {
      ArgumentCaptor<AircraftReserveInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftReserveInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(
          requestDto.getAircraftReservationId(), dtoCaptor.getValue().getAircraftReservationId());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(
          requestDto.getGroupReservationId(), dtoCaptor.getValue().getGroupReservationId());
      assertEquals(
          requestDto.getReservationTimeFrom(), dtoCaptor.getValue().getReservationTimeFrom());
      assertEquals(requestDto.getReservationTimeTo(), dtoCaptor.getValue().getReservationTimeTo());
    }
    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).putData(any(), userInfoCaptor.capture());
      UserInfoDto capturedUserInfo = userInfoCaptor.getValue();
      assertEquals("ope01", capturedUserInfo.getUserOperatorId());
      assertEquals("10", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("航路運営者_責任者", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "0a0711a5-ff74-4164-9309-8888b433cf41", capturedUserInfo.getAffiliatedOperatorId());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 機体予約情報削除が正しく行われる<br>
   * 条件: 任意機体予約IDをURLに設定。入力チェックが正常。削除処理が正常。<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void delete_正常() throws Exception {
    String aircraftReserveId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftReserveId);
    doNothing().when(service).deleteData(eq(aircraftReserveId), any(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + aircraftReserveId))
            .andExpect(status().isOk())
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftReserveId);
    {
      ArgumentCaptor<AircraftInfoDeleteRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoDeleteRequestDto.class);
      verify(aircraftInfoValidator, times(0)).validateForDelete(dtoCaptor.capture());
    }
    {
      ArgumentCaptor<UserInfoDto> dtoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).deleteData(eq(aircraftReserveId), eq(false), dtoCaptor.capture());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void delete_入力チェックエラー発生() throws Exception {
    String aircraftReserveId = UUID.randomUUID().toString();

    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForDetail(aircraftReserveId);
    doNothing().when(service).deleteData(eq(aircraftReserveId), any(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + aircraftReserveId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftReserveId);
    verify(aircraftInfoValidator, times(0))
        .validateForDelete(any(AircraftInfoDeleteRequestDto.class));
    verify(service, times(0)).deleteData(anyString(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void delete_サービスエラー発生() throws Exception {
    String aircraftReserveId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftReserveId);
    doThrow(new ServiceErrorException("ServiceErrorMessage"))
        .when(service)
        .deleteData(eq(aircraftReserveId), any(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + aircraftReserveId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorMessage"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftReserveId);
    verify(service, times(1)).deleteData(eq(aircraftReserveId), any(), any(UserInfoDto.class));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 機体予約情報削除APIが呼び出されない<br>
   * 条件: 機体予約IDを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void delete_機体ID未設定() throws Exception {
    doNothing().when(validator).validateForDetail(anyString());
    doNothing().when(aircraftInfoValidator).validateForDelete(any());
    doNothing().when(service).deleteData(anyString(), any(), any());

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/"))
            .andExpect(status().isInternalServerError())
            .andReturn();

    verify(validator, times(0)).validateForDetail(anyString());
    verify(aircraftInfoValidator, times(0)).validateForDelete(any());
    verify(service, times(0)).deleteData(anyString(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 機体予約情報削除が正しく行われる<br>
   * 条件: 任意機体予約IDをURLに設定。入力チェックが正常。削除処理が正常。<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void delete_ユーザー情報() throws Exception {
    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf11";

    doNothing().when(validator).validateForDetail(aircraftReserveId);
    doNothing().when(service).deleteData(eq(aircraftReserveId), any(), any(UserInfoDto.class));

    mockMvc.perform(delete(basePath + "/" + aircraftReserveId)).andExpect(status().isOk());

    verify(validator, times(1)).validateForDetail(aircraftReserveId);
    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).deleteData(any(), eq(false), userInfoCaptor.capture());
      UserInfoDto capturedUserInfo = userInfoCaptor.getValue();
      assertEquals("ope01", capturedUserInfo.getUserOperatorId());
      assertEquals("10", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("航路運営者_責任者", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "0a0711a5-ff74-4164-9309-8888b433cf41", capturedUserInfo.getAffiliatedOperatorId());
    }
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 機体予約情報削除が正しく行われる<br>
   * 条件: 任意機体予約IDをURLに設定。入力チェックが正常。削除処理が正常。<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void delete_ユーザー情報_機体予約ID使用フラグfalse() throws Exception {
    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf11";

    doNothing().when(validator).validateForDetail(aircraftReserveId);
    doNothing().when(service).deleteData(eq(aircraftReserveId), any(), any(UserInfoDto.class));

    mockMvc
        .perform(delete(basePath + "/" + aircraftReserveId + "?aircraftReservationIdFlag=false"))
        .andExpect(status().isOk())
        .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftReserveId);
    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).deleteData(any(), eq(false), userInfoCaptor.capture());
      UserInfoDto capturedUserInfo = userInfoCaptor.getValue();
      assertEquals("ope01", capturedUserInfo.getUserOperatorId());
      assertEquals("10", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("航路運営者_責任者", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "0a0711a5-ff74-4164-9309-8888b433cf41", capturedUserInfo.getAffiliatedOperatorId());
    }
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 機体予約情報削除が正しく行われる<br>
   * 条件: 任意機体予約IDをURLに設定。入力チェックが正常。削除処理が正常。<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void delete_ユーザー情報_機体予約ID使用フラグtrue() throws Exception {
    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf11";

    doNothing().when(validator).validateForDetail(aircraftReserveId);
    doNothing().when(service).deleteData(eq(aircraftReserveId), any(), any(UserInfoDto.class));

    mockMvc
        .perform(delete(basePath + "/" + aircraftReserveId + "?aircraftReservationIdFlag=true"))
        .andExpect(status().isOk())
        .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftReserveId);
    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).deleteData(any(), eq(true), userInfoCaptor.capture());
      UserInfoDto capturedUserInfo = userInfoCaptor.getValue();
      assertEquals("ope01", capturedUserInfo.getUserOperatorId());
      assertEquals("10", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("航路運営者_責任者", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "0a0711a5-ff74-4164-9309-8888b433cf41", capturedUserInfo.getAffiliatedOperatorId());
    }
  }

  /** データテンプレート ■登録更新リクエスト 機体予約登録更新_テンプレート */
  private static AircraftReserveInfoRequestDto createAircraftReserveInfoRequestDto() {
    AircraftReserveInfoRequestDto ret = new AircraftReserveInfoRequestDto();
    ret.setAircraftReservationId("0a0711a5-ff74-4164-9309-8888b433cf11");
    ret.setGroupReservationId("0a0711a5-ff74-4164-9309-8888b433cf21");
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf31");
    ret.setReservationTimeFrom("2026-01-01T10:00:00+09:00");
    ret.setReservationTimeTo("2026-01-01T12:00:00+09:00");
    return ret;
  }
}
