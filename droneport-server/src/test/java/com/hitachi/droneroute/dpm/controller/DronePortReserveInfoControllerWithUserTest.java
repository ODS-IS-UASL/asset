package com.hitachi.droneroute.dpm.controller;

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
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.RoleInfoDto;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateResponseDto;
import com.hitachi.droneroute.dpm.service.DronePortReserveInfoService;
import com.hitachi.droneroute.dpm.validator.DronePortInfoValidator;
import com.hitachi.droneroute.dpm.validator.DronePortReserveInfoValidator;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** DronePortReserveInfoControllerクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
public class DronePortReserveInfoControllerWithUserTest {

  @Value("${droneroute.basepath}/droneport/reserve")
  String basePath;

  @Autowired MockMvc mockMvc;

  @MockBean DronePortReserveInfoService service;

  @MockBean DronePortReserveInfoValidator validator;

  @MockBean DronePortInfoValidator dronePortInfoValidator;

  @SpyBean SystemSettings systemSettings;

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
   * 試験名: 離着陸場予約情報登録処理が正しく行われる<br>
   * 条件: 正常な DronePortInfoReserveRegisterRequestDto を渡す<br>
   * 結果: 処理結果(離着陸場予約ID)が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void post_正常() throws Exception {
    DronePortReserveInfoRegisterListRequestDto requestDto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId("dummyDronePortId");
    requestDto.setData(Arrays.asList(element));

    DronePortReserveInfoRegisterListResponseDto responseDto =
        new DronePortReserveInfoRegisterListResponseDto();
    responseDto.setDronePortReservationIds(Arrays.asList("responseReservationId"));

    doReturn(responseDto).when(service).register(any(), any());
    doNothing().when(validator).validateForRegister(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dronePortReservationIds[0]").value("responseReservationId"))
            .andReturn();

    {
      ArgumentCaptor<DronePortReserveInfoRegisterListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoRegisterListRequestDto.class);
      verify(service, times(1)).register(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortReserveInfoRegisterListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoRegisterListRequestDto.class);
      verify(validator, times(1)).validateForRegister(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void post_入力チェックエラー発生() throws Exception {
    DronePortReserveInfoRegisterListRequestDto requestDto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId("dummyDronePortId");
    requestDto.setData(Arrays.asList(element));

    DronePortReserveInfoRegisterListResponseDto responseDto =
        new DronePortReserveInfoRegisterListResponseDto();
    responseDto.setDronePortReservationIds(Arrays.asList("responseReservationId"));

    doReturn(responseDto).when(service).register(any(), any());
    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForRegister(any());

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

    verify(service, times(0)).register(any(), any());
    verify(validator, times(1)).validateForRegister(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void post_サービスエラー発生() throws Exception {
    DronePortReserveInfoUpdateRequestDto requestDto = new DronePortReserveInfoUpdateRequestDto();
    requestDto.setDronePortReservationId("dummyReservationId");
    requestDto.setDronePortId("dummyDronePortId");

    DronePortReserveInfoUpdateResponseDto responseDto = new DronePortReserveInfoUpdateResponseDto();
    responseDto.setDronePortReservationId("responseReservationId");

    doThrow(new ServiceErrorException("dummyMessage")).when(service).register(any(), any());
    doNothing().when(validator).validateForRegister(any());

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

    verify(service, times(1)).register(any(), any());
    verify(validator, times(1)).validateForRegister(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 離着陸場予約情報登録APIが呼び出されない<br>
   * 条件: リクエストボディを未設定<br>
   * 結果: BadRequestが返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void post_リクエストボディなし() throws Exception {
    DronePortReserveInfoRegisterListResponseDto responseDto =
        new DronePortReserveInfoRegisterListResponseDto();
    responseDto.setDronePortReservationIds(Arrays.asList("responseReservationId"));

    doReturn(responseDto).when(service).register(any(), any());
    doNothing().when(validator).validateForRegister(any());

    MvcResult response =
        mockMvc
            .perform(
                post(basePath).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).register(any(), any());
    verify(validator, times(0)).validateForRegister(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 離着陸場予約情報登録処理が正しく行われる<br>
   * 条件: 正常な DronePortInfoReserveRegisterRequestDto を渡す<br>
   * 結果: 処理結果(離着陸場予約ID)が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void post_ユーザー情報_一括予約ID() throws Exception {
    DronePortReserveInfoRegisterListRequestDto requestDto = createDronePortReserveRegisterDto();

    DronePortReserveInfoRegisterListResponseDto responseDto =
        new DronePortReserveInfoRegisterListResponseDto();
    List<String> addList =
        Arrays.asList(
            "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e11",
            "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12",
            "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e13");
    responseDto.setDronePortReservationIds(addList);

    doReturn(responseDto).when(service).register(any(), any());
    doNothing().when(validator).validateForRegister(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(
                jsonPath("$.dronePortReservationIds[0]")
                    .value("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e11"))
            .andExpect(
                jsonPath("$.dronePortReservationIds[1]")
                    .value("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12"))
            .andExpect(
                jsonPath("$.dronePortReservationIds[2]")
                    .value("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e13"))
            .andReturn();

    verify(validator, times(1)).validateForRegister(any());
    {
      ArgumentCaptor<DronePortReserveInfoRegisterListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoRegisterListRequestDto.class);
      verify(service, times(1)).register(dtoCaptor.capture(), any());
      assertEquals(
          requestDto.getData().get(0).getGroupReservationId(),
          dtoCaptor.getValue().getData().get(0).getGroupReservationId());
      assertEquals(
          requestDto.getData().get(0).getDronePortId(),
          dtoCaptor.getValue().getData().get(0).getDronePortId());
      assertEquals(
          requestDto.getData().get(0).getAircraftId(),
          dtoCaptor.getValue().getData().get(0).getAircraftId());
      assertEquals(
          requestDto.getData().get(0).getRouteReservationId(),
          dtoCaptor.getValue().getData().get(0).getRouteReservationId());
      assertEquals(
          requestDto.getData().get(0).getUsageType(),
          dtoCaptor.getValue().getData().get(0).getUsageType());
      assertEquals(
          requestDto.getData().get(0).getReservationTimeFrom(),
          dtoCaptor.getValue().getData().get(0).getReservationTimeFrom());
      assertEquals(
          requestDto.getData().get(0).getReservationTimeTo(),
          dtoCaptor.getValue().getData().get(0).getReservationTimeTo());
      assertEquals(
          requestDto.getData().get(1).getGroupReservationId(),
          dtoCaptor.getValue().getData().get(1).getGroupReservationId());
      assertEquals(
          requestDto.getData().get(1).getDronePortId(),
          dtoCaptor.getValue().getData().get(1).getDronePortId());
      assertEquals(
          requestDto.getData().get(1).getAircraftId(),
          dtoCaptor.getValue().getData().get(1).getAircraftId());
      assertEquals(
          requestDto.getData().get(1).getRouteReservationId(),
          dtoCaptor.getValue().getData().get(1).getRouteReservationId());
      assertEquals(
          requestDto.getData().get(1).getUsageType(),
          dtoCaptor.getValue().getData().get(1).getUsageType());
      assertEquals(
          requestDto.getData().get(1).getReservationTimeFrom(),
          dtoCaptor.getValue().getData().get(1).getReservationTimeFrom());
      assertEquals(
          requestDto.getData().get(1).getReservationTimeTo(),
          dtoCaptor.getValue().getData().get(1).getReservationTimeTo());
      assertEquals(
          requestDto.getData().get(2).getGroupReservationId(),
          dtoCaptor.getValue().getData().get(2).getGroupReservationId());
      assertEquals(
          requestDto.getData().get(2).getDronePortId(),
          dtoCaptor.getValue().getData().get(2).getDronePortId());
      assertEquals(
          requestDto.getData().get(2).getAircraftId(),
          dtoCaptor.getValue().getData().get(2).getAircraftId());
      assertEquals(
          requestDto.getData().get(2).getRouteReservationId(),
          dtoCaptor.getValue().getData().get(2).getRouteReservationId());
      assertEquals(
          requestDto.getData().get(2).getUsageType(),
          dtoCaptor.getValue().getData().get(2).getUsageType());
      assertEquals(
          requestDto.getData().get(2).getReservationTimeFrom(),
          dtoCaptor.getValue().getData().get(2).getReservationTimeFrom());
      assertEquals(
          requestDto.getData().get(2).getReservationTimeTo(),
          dtoCaptor.getValue().getData().get(2).getReservationTimeTo());
    }
    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).register(any(), userInfoCaptor.capture());
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
   * 試験名: 離着陸場予約情報更新が正しく行われる<br>
   * 条件: 正常な離着陸場予約IDを渡す<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void put_正常() throws Exception {
    DronePortReserveInfoUpdateRequestDto requestDto = new DronePortReserveInfoUpdateRequestDto();
    requestDto.setDronePortReservationId("dummyReservationId");
    requestDto.setDronePortId("dummyDronePortId");

    DronePortReserveInfoUpdateResponseDto responseDto = new DronePortReserveInfoUpdateResponseDto();
    responseDto.setDronePortReservationId("responseReservationId");

    doReturn(responseDto).when(service).update(any(), any());
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
            .andExpect(jsonPath("$.dronePortReservationId").value("responseReservationId"))
            .andReturn();

    {
      ArgumentCaptor<DronePortReserveInfoUpdateRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoUpdateRequestDto.class);
      verify(service, times(1)).update(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortReserveInfoUpdateRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoUpdateRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void put_入力チェックエラー発生() throws Exception {
    DronePortReserveInfoUpdateRequestDto requestDto = new DronePortReserveInfoUpdateRequestDto();
    requestDto.setDronePortReservationId("dummyReservationId");
    requestDto.setDronePortId("dummyDronePortId");

    DronePortReserveInfoUpdateResponseDto responseDto = new DronePortReserveInfoUpdateResponseDto();
    responseDto.setDronePortReservationId("responseDronePortReserveId");

    doReturn(responseDto).when(service).update(any(), any());
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

    verify(service, times(0)).update(any(), any());
    verify(validator, times(1)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void put_サービスエラー発生() throws Exception {
    DronePortReserveInfoUpdateRequestDto requestDto = new DronePortReserveInfoUpdateRequestDto();
    requestDto.setDronePortReservationId("dummyReservationId");
    requestDto.setDronePortId("dummyDronePortId");

    DronePortReserveInfoUpdateResponseDto responseDto = new DronePortReserveInfoUpdateResponseDto();
    responseDto.setDronePortReservationId("responseReserveId");

    doThrow(new ServiceErrorException("dummyMessage")).when(service).update(any(), any());
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

    verify(service, times(1)).update(any(), any());
    verify(validator, times(1)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 離着陸場予約情報更新APIが呼び出されない<br>
   * 条件: リクエストボディを未設定<br>
   * 結果: BadRequestが返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void put_リクエストボディなし() throws Exception {

    DronePortReserveInfoUpdateResponseDto responseDto = new DronePortReserveInfoUpdateResponseDto();
    responseDto.setDronePortReservationId("responseReservationId");

    doReturn(responseDto).when(service).update(any(), any());
    doNothing().when(validator).validateForUpdate(any());

    MvcResult response =
        mockMvc
            .perform(
                put(basePath).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).update(any(), any());
    verify(validator, times(0)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 離着陸場予約情報登録処理が正しく行われる<br>
   * 条件: 正常な DronePortInfoReserveRegisterRequestDto を渡す<br>
   * 結果: 処理結果(離着陸場予約ID)が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void put_ユーザー情報_一括予約ID() throws Exception {
    String dronePortReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e11";
    DronePortReserveInfoUpdateRequestDto requestDto = createDronePortReserveUpdateDto();

    DronePortReserveInfoUpdateResponseDto responseDto = new DronePortReserveInfoUpdateResponseDto();
    responseDto.setDronePortReservationId(dronePortReservationId);

    doReturn(responseDto).when(service).update(any(), any());
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
            .andExpect(jsonPath("$.dronePortReservationId").value(dronePortReservationId))
            .andReturn();

    verify(validator, times(1)).validateForUpdate(any());
    {
      ArgumentCaptor<DronePortReserveInfoUpdateRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoUpdateRequestDto.class);
      verify(service, times(1)).update(dtoCaptor.capture(), any());
      assertEquals(
          requestDto.getDronePortReservationId(), dtoCaptor.getValue().getDronePortReservationId());
      assertEquals(requestDto.getDronePortId(), dtoCaptor.getValue().getDronePortId());
      assertEquals(requestDto.getAircraftId(), dtoCaptor.getValue().getAircraftId());
      assertEquals(
          requestDto.getRouteReservationId(), dtoCaptor.getValue().getRouteReservationId());
      assertEquals(requestDto.getUsageType(), dtoCaptor.getValue().getUsageType());
      assertEquals(
          requestDto.getReservationTimeFrom(), dtoCaptor.getValue().getReservationTimeFrom());
      assertEquals(requestDto.getReservationTimeTo(), dtoCaptor.getValue().getReservationTimeTo());
    }
    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).update(any(), userInfoCaptor.capture());
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
   * 試験名: 離着陸場予約情報削除が正しく行われる<br>
   * 条件: 任意離着陸場IDをURLに設定。入力チェックが正常。削除処理が正常。<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void delete_正常() throws Exception {
    String droneReservationPortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(droneReservationPortId);
    doNothing().when(service).delete(eq(droneReservationPortId), any(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + droneReservationPortId))
            .andExpect(status().isOk())
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(droneReservationPortId);
    {
      ArgumentCaptor<DronePortInfoDeleteRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoDeleteRequestDto.class);
      verify(dronePortInfoValidator, times(0)).validateForDelete(dtoCaptor.capture());
    }
    {
      ArgumentCaptor<UserInfoDto> dtoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).delete(eq(droneReservationPortId), eq(false), dtoCaptor.capture());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void delete_入力チェックエラー発生() throws Exception {
    String droneReservationPortId = UUID.randomUUID().toString();

    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForGetDetail(droneReservationPortId);
    doNothing().when(service).delete(eq(droneReservationPortId), any(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + droneReservationPortId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(droneReservationPortId);
    {
      ArgumentCaptor<DronePortInfoDeleteRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoDeleteRequestDto.class);
      verify(dronePortInfoValidator, times(0)).validateForDelete(dtoCaptor.capture());
    }
    verify(service, times(0)).delete(anyString(), any(), any(UserInfoDto.class));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void delete_サービスエラー発生() throws Exception {
    String droneReservationPortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(droneReservationPortId);
    doThrow(new ServiceErrorException("ServiceErrorMessage"))
        .when(service)
        .delete(eq(droneReservationPortId), any(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + droneReservationPortId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorMessage"))
            .andReturn();

    {
      ArgumentCaptor<DronePortInfoDeleteRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoDeleteRequestDto.class);
      verify(dronePortInfoValidator, times(0)).validateForDelete(dtoCaptor.capture());
    }
    {
      ArgumentCaptor<UserInfoDto> dtoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).delete(eq(droneReservationPortId), any(), dtoCaptor.capture());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 離着陸場予約情報削除APIが呼び出されない<br>
   * 条件: 離着陸場予約IDを未設定<br>
   * 結果: MethodNotAllowedが返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void delete_離着陸場予約ID未設定() throws Exception {

    doNothing().when(validator).validateForGetDetail(anyString());
    doNothing().when(service).delete(anyString(), any(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/"))
            .andExpect(status().isInternalServerError())
            .andReturn();

    verify(validator, times(0)).validateForGetDetail(anyString());
    verify(dronePortInfoValidator, times(0))
        .validateForDelete(any(DronePortInfoDeleteRequestDto.class));
    verify(service, times(0)).delete(anyString(), any(), any(UserInfoDto.class));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 離着陸場予約情報削除が正しく行われる<br>
   * 条件: 任意離着陸場IDをURLに設定。入力チェックが正常。削除処理が正常。<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void delete_ユーザー情報_離着陸場予約ID使用フラグfalse() throws Exception {
    String droneReservationPortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(droneReservationPortId);
    doNothing().when(service).delete(eq(droneReservationPortId), any(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(
                delete(
                    basePath + "/" + droneReservationPortId + "?dronePortReservationIdFlag=false"))
            .andExpect(status().isOk())
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(droneReservationPortId);
    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).delete(any(), eq(false), userInfoCaptor.capture());
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
   * 試験名: 離着陸場予約情報削除が正しく行われる<br>
   * 条件: 任意離着陸場IDをURLに設定。入力チェックが正常。削除処理が正常。<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void delete_ユーザー情報_離着陸場予約ID使用フラグtrue() throws Exception {
    String droneReservationPortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(droneReservationPortId);
    doNothing().when(service).delete(eq(droneReservationPortId), any(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(
                delete(
                    basePath + "/" + droneReservationPortId + "?dronePortReservationIdFlag=true"))
            .andExpect(status().isOk())
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(droneReservationPortId);
    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).delete(any(), eq(true), userInfoCaptor.capture());
      UserInfoDto capturedUserInfo = userInfoCaptor.getValue();
      assertEquals("ope01", capturedUserInfo.getUserOperatorId());
      assertEquals("10", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("航路運営者_責任者", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "0a0711a5-ff74-4164-9309-8888b433cf41", capturedUserInfo.getAffiliatedOperatorId());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /** データテンプレート ■登録リクエスト 離着陸場予約登録_テンプレート */
  private DronePortReserveInfoRegisterListRequestDto createDronePortReserveRegisterDto() {
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();

    DronePortReserveInfoRegisterListRequestDto.Element element1 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element1.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
    element1.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e31");
    element1.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    element1.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    element1.setUsageType(1);
    element1.setReservationTimeFrom("2026-01-01T10:00:00+09:00");
    element1.setReservationTimeTo("2026-01-01T12:00:00+09:00");

    DronePortReserveInfoRegisterListRequestDto.Element element2 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element2.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
    element2.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
    element2.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    element2.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    element2.setUsageType(1);
    element2.setReservationTimeFrom("2026-01-01T12:00:00+09:00");
    element2.setReservationTimeTo("2026-01-01T14:00:00+09:00");

    DronePortReserveInfoRegisterListRequestDto.Element element3 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element3.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
    element3.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e33");
    element3.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    element3.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    element3.setUsageType(1);
    element3.setReservationTimeFrom("2026-01-01T14:00:00+09:00");
    element3.setReservationTimeTo("2026-01-01T16:00:00+09:00");

    dto.setData(Arrays.asList(element1, element2, element3));
    return dto;
  }

  /** データテンプレート ■更新リクエスト 離着陸場予約更新_テンプレート */
  private DronePortReserveInfoUpdateRequestDto createDronePortReserveUpdateDto() {
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e11");
    dto.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e31");
    dto.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
    dto.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
    dto.setUsageType(1);
    dto.setReservationTimeFrom("2026-01-01T10:00:00+09:00");
    dto.setReservationTimeTo("2026-01-01T12:00:00+09:00");

    return dto;
  }
}
