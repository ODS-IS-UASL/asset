package com.hitachi.droneroute.arm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.arm.dto.AircraftInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoModelInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoModelSearchRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoSearchListElement;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListElement;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListResponseDto;
import com.hitachi.droneroute.arm.entity.FileInfoEntity;
import com.hitachi.droneroute.arm.entity.PayloadInfoEntity;
import com.hitachi.droneroute.arm.service.AircraftInfoService;
import com.hitachi.droneroute.arm.service.VirusScanService;
import com.hitachi.droneroute.arm.validator.AircraftInfoValidator;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.RoleInfoDto;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListDetailElement;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/** AircraftInfoControllerクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@AutoConfigureMockMvc(addFilters = false)
public class AircraftInfoControllerTest {
  @Value("${droneroute.basepath}/aircraft/info")
  String basePath;

  MockMvc mockMvc;

  @Autowired WebApplicationContext webApplicationContext;

  @MockBean private AircraftInfoService service;

  @MockBean private AircraftInfoValidator validator;

  @MockBean private VirusScanService virusScanService;

  @SpyBean private SystemSettings systemSettings;

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    // テスト用UserInfoDtoをSecurityContextに設定
    UserInfoDto testUserInfo = new UserInfoDto();
    RoleInfoDto roleDto = new RoleInfoDto();
    List<RoleInfoDto> roleList = new ArrayList<>();
    roleDto.setRoleId("11");
    roleDto.setRoleName("テストロール");
    roleList.add(roleDto);
    testUserInfo.setUserOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    testUserInfo.setRoles(roleList);
    testUserInfo.setAffiliatedOperatorId("4013e2ad-6cde-432e-985e-50ab5f06bd94");

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_11"));

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  /**
   * メソッド名: post<br>
   * 試験名: 機体情報登録処理が正しく行われることを確認する<br>
   * 条件: 正常な AircraftInfoRequestDto を渡す<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void post_正常() throws Exception {
    AircraftInfoRequestDto requestDto = new AircraftInfoRequestDto();
    requestDto.setAircraftId("dummyAircraftId");
    requestDto.setAircraftName("dummyAircraftName");
    requestDto.setImageData("dummyImageData");
    requestDto.setAircraftType(1);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("responseAircraftId");

    doNothing().when(service).decodeBinary(any());
    doReturn(responseDto).when(service).postData(any(), any());
    doNothing().when(validator).validateForRegist(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("responseAircraftId"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
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
    AircraftInfoRequestDto requestDto = new AircraftInfoRequestDto();
    requestDto.setAircraftId("dummyAircraftId");
    requestDto.setAircraftName("dummyAircraftName");
    requestDto.setImageData("dummyImageData");

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("responseAircraftId");

    doNothing().when(service).decodeBinary(any());
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

    verify(service, times(1)).decodeBinary(any());
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
    AircraftInfoRequestDto requestDto = new AircraftInfoRequestDto();
    requestDto.setAircraftId("dummyAircraftId");
    requestDto.setAircraftName("dummyAircraftName");
    requestDto.setImageData("dummyImageData");

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("responseAircraftId");

    doNothing().when(service).decodeBinary(any());
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

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).postData(any(), any());
    verify(validator, times(1)).validateForRegist(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: バイナリ変換エでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void post_バイナリ変換エラー発生() throws Exception {
    AircraftInfoRequestDto requestDto = new AircraftInfoRequestDto();
    requestDto.setAircraftId("dummyAircraftId");
    requestDto.setAircraftName("dummyAircraftName");
    requestDto.setImageData("dummyImageData");

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("responseAircraftId");

    doThrow(new ServiceErrorException("dummyMessage")).when(service).decodeBinary(any());
    doReturn(responseDto).when(service).postData(any(), any());
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

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(0)).postData(any(), any());
    verify(validator, times(0)).validateForRegist(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 機体情報更新APIが呼び出されないことを確認する<br>
   * 条件: リクエストボディを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void post_リクエストボディなし() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                post(basePath).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForRegist(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 補足資料が1つの場合の正常登録処理を確認する<br>
   * 条件: 補足資料リストに1件のみ設定した正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_正常補足資料1つ() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(1)).scanVirus(dtoCaptor.capture());
      assertEquals(
          new String(requestDto.getFileInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(dtoCaptor.getValue(), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 補足資料が3つの場合の正常登録処理を確認する<br>
   * 条件: 補足資料リストに3件設定した正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_正常補足資料3つ() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku3();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(3)).scanVirus(dtoCaptor.capture());
      List<byte[]> allValues = dtoCaptor.getAllValues();
      assertEquals(
          new String(requestDto.getFileInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(0), StandardCharsets.UTF_8));
      assertEquals(
          new String(requestDto.getFileInfos().get(1).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(1), StandardCharsets.UTF_8));
      assertEquals(
          new String(requestDto.getFileInfos().get(2).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(2), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 補足資料が空配列の場合の正常登録処理を確認する<br>
   * 条件: 補足資料リストを空配列で設定した正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_正常補足資料空配列() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_empList();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 補足資料項目がない場合の正常登録処理を確認する<br>
   * 条件: 補足資料項目をnullで設定した正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_正常補足資料項目なし() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosokuNull();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 定義外項目がある場合の正常登録処理を確認する<br>
   * 条件: 定義外の項目を含む正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_定義外項目あり() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\",  \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageData\": \"data:image/png;base64,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\",  \"publicFlag\": \"true\",  \"fileInfos\": [    {      \"processingType\": 1,      \"fileId\": null,      \"fileLogicalName\": \"補足資料論理名補足資料論理名\",      \"filePhysicalName\": \"hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\",      \"teigigaiCol\": \"aaa\"    }  ]}";
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(0))
          .scanVirus(dtoCaptor.capture()); // モック実行のため、バイナリデータが設定されないので0
      // assertEquals(new
      // String(requestDto.getFileInfos().get(0).getFileBinary(),StandardCharsets.UTF_8), new
      // String(dtoCaptor.getValue(),StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 文字項目のダブルクオートなしの場合の動作を確認する<br>
   * 条件: 文字項目にダブルクオートがないリクエストを送信<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_文字項目のダブルクオートなし() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\",  \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageData\": \"data:image/png;base64,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\",  \"operatorId\": \"ope01\",  \"fileInfos\": [    {      \"processingType\": 1,      \"fileId\": null,      \"fileLogicalName\": 補足資料論理名補足資料論理名,      \"filePhysicalName\": \"hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\"    }  ]}";
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForRegist(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 補足情報ファイルのリスト内の要素がNULLの場合の正常登録処理を確認する<br>
   * 条件: 補足情報ファイルのリスト内の要素をnullに設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_補足情報ファイルのリスト内の要素がNULL() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();
    requestDto.getFileInfos().set(0, null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 補足情報ファイルのバイナリデータがNULLの場合の正常登録処理を確認する<br>
   * 条件: 補足情報ファイルのバイナリデータをnullに設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_補足情報ファイルのバイナリデータがNULL() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();
    requestDto.getFileInfos().get(0).setFileBinary(null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: jsonのフォーマット崩れの場合のエラー処理を確認する<br>
   * 条件: jsonのフォーマットが崩れたリクエストを送信<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_jsonのフォーマット崩れ() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\",  \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageData\": \"data:image/png;base64,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\",  \"operatorId\": \"ope01\",  \"fileInfos\": [    {      \"processingType\": 1,      \"fileId\": null,      \"fileLogicalName\": \"補足資料論理名補足資料論理名\",      \"filePhysicalName\": \"hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\"    }  }";
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForRegist(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ファイルバイナリデコード処理が異常終了した際の挙動を確認する<br>
   * 条件: ファイルバイナリデコード処理でServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_ファイルバイナリデコード処理が異常終了した際の挙動() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();
    requestDto.getFileInfos().get(0).setFileBinary(null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doThrow(new IllegalArgumentException("上記以外の例外が発生")).when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(0)).validateForRegist(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ウイルスチェックで異常終了(チェックエラー)した際の挙動を確認する<br>
   * 条件: ウイルスチェックでVirusScanErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_ウイルスチェックで異常終了_チェックエラー() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(virusScanService)
        .scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForRegist(any());
    verify(virusScanService, times(1)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ウイルスチェックで予期せぬエラーが発生した際の挙動を確認する<br>
   * 条件: ウイルスチェックでRuntimeExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_ウイルスチェックで異常終了_予期せぬエラー() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doThrow(new IllegalArgumentException("上記以外の例外が発生")).when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForRegist(any());
    verify(virusScanService, times(1)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 2つ目のファイルのウイルスチェックで異常終了した際の挙動を確認する<br>
   * 条件: 2つ目のファイルのウイルスチェックでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_ウイルスチェックで異常終了_チェックエラー_2つ目のファイル() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku3();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing()
        .doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(virusScanService)
        .scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForRegist(any());
    verify(virusScanService, times(2)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報い1つの場合の正常登録処理を確認する<br>
   * 条件: ペイロード情報リストで1件のみ設定した正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_正常ペイロード情報1つ() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(1)).scanVirus(dtoCaptor.capture());
      assertEquals(
          new String(requestDto.getPayloadInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(dtoCaptor.getValue(), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報い3つの場合の正常登録処理を確認する<br>
   * 条件: ペイロード情報リストで3件設定した正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_正常ペイロード情報3つ() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload3();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(3)).scanVirus(dtoCaptor.capture());
      List<byte[]> allValues = dtoCaptor.getAllValues();
      assertEquals(
          new String(requestDto.getPayloadInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(0), StandardCharsets.UTF_8));
      assertEquals(
          new String(requestDto.getPayloadInfos().get(1).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(1), StandardCharsets.UTF_8));
      assertEquals(
          new String(requestDto.getPayloadInfos().get(2).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(2), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報が空配列の場合の正常登録処理を確認する<br>
   * 条件: ペイロード情報リストを空配列で設定した正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_正常ペイロード情報空配列() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payloadEmpList();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報項目がない場合の正常登録処理を確認する<br>
   * 条件: ペイロード情報項目をnullで設定した正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_正常ペイロード情報項目なし() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payloadNull();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報に定義外項目がある場合の正常登録処理を確認する<br>
   * 条件: ペイロード情報に定義外の項目を含む正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_ペイロード情報定義外項目あり() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\",  \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageData\": \"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPUAAABuCAYAAAD/EFXlAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAcdSURBVHhe7d0hVOw6FwXgkUgkEolEIpFIJHIkEolDIpFIJBKJRCKRSCQSiexd+76b+84c0rntNE2Tnf2tlbX+978n5nSyJ2mSllUnIlRW/v8Qkbop1CJkFGoRMgq1CBmFWoSMQi1CRqEWIaNQi5BRqEXIKNQiZBRqETIKtQgZhVqEjEItQkahFiGjUIuQUailWc/Pz93p6Wn38PDg/1XVFGpp1sHBQbdarbq9vT3/r6qmUEuzEOjQmHBVIzKCQi1CRqEWIaNQi5BpPtRY/j88PNy4ELU0fG62bQuZzvYRJoOrqTXQobFtW8h0tn8wGVzN7e3tj6DU1C4vL31J0jjbP5hwVSMygkItQkahFiGjUIuQUahlJzVsBeLz3d/f+49Oz14DJlzVFKj0QNt2c3PjPz41WzsTrmoKVONWYCuHdWzNTLiqkdG+v7+7s7OzH8Fu4bCOQi20EOz1ev0j2OxYa+WqRiZj7egxrLVyVSOTsXb0GNZauaqRyVg7egxrrVzVyGSsHT2GtVauamQS7FOzdvQY1lqTVVPKySl8houLi98npN7f3/3HlB4+0LiG7Gy9TJJVU0KgpzbUcH19/XuLpyU+0Ni3buEa2JqZJKumxpNTfe3o6Kh7fX31JVJqNdBg62bCVU3XdW9vb93d3V13fn7e7e/v/wjs2MZ8ZLLlQIOtnQlXNRMhvLEfAsYjk60HGhTqRnx+fv44C832fjPMZFoPNCjUQuHp6UmB/kOhrgDrnyZNBesNuJUIHRnXqtVAg0JdAdY/TZrCx8fHxrYj/vfX15f/z5qiUFeA9UuaCqPx8fHx32uDxUAdzOHtLzTVoOOyfklTYXsvXBfMYl5eXvx/0iTW/kJTjV3Rxagk/7m6utrovC2+YNAKx5n9CUgmFNVglA730+q4//NbVwh463yYQwvPDGCRFduaNaMI9ePj498vB+FueUU38CM0puAy/DgzZnt4DqDG48IUobaLQBidWoYfNHsPHTqofug2+f36bQ0LizX1q+pDbb8cLAK13HkxbbQ/cGg4XNL61lUfe50wImNk9tfPtlrei159qHGAIlz0lu8ZcbDE3y+yHW9NzV4rCz+OuLfGPbZ/FqCGYFcdanTkcLExSte+wLErrOj6zlfTdHEpfaG2MPPzzwKUHuz+aipg7x1bHZUwotgOhx833JLIvw0JNdQW7O3VFAwnouxFxqjdGh9orPy3eB12NTTUUFOw/11NoeyWTYvbNT7QWODB+W4ZbkyoIRZszBBLu+0bVk1hcBHt00atjU6xQGuFe7yxoYZYsNEXMciUEu7h1RTEjtKtHQlVoNPZJdQQOwsQwl3CiyvHVVMIu3XT0qKQAp3WrqEO0Pdi+9pLv7hyt2oWhClOuHjYxmmFP96oQE83NdRBX7iXmpJPq2YB9gQZDp6ww2OS+OUfE+gxf1ih1Xedg70OKfS9uDJc51wPGqWpJiP7FkzmE2T4hceJJt85Tk5OtgYahgbatqWnjEuw9acSe3GlbTm2wdJVk4ldoMDTWYxwGsz/4uOfMQUfwk/Vx7SlpoxLsHWnhhmWPcJs29DvcVfpq5mR38pifCWPfx83GkbsuYIWmzKWtkUzF1vznPw22Nzv0Ju3msTYt7Jwb2s7GqbEOV491DdlZA93rlCDf93WnH/5Zf5qEvGjNNNWFmYcuFe2X/oS7+PumzKyhjtnqMH233Bd55CnmgRYR2ncX/kve4lAW9vCPff9YE65Q+3XOuZ6CClPNROhk7GN0rHRubTQ9O2/5ljBzSF3qHMpvho8pGBfKogg1AwjcGx0Rl2lLvzFwo2Z05KziRQU6gWg09jRDOGu+b4udpCktNG5j1/BRat9b1uhXsB6vd7o/LV2oG0HSUodnWP6HmSo9USaQp2Zf2d1riN2KeGRUExT/T7wmIMkJYrtbeMHqrbnuRXqjPzTSHOtEs4hBLnvqOacB0lyiu1tI+g1LWIq1JnYF/OjLb29MxRG3r4go+U6SJKbn1Gh4baphtsKhToTu9JdQ6BjW1OhoRbMMhjDbGGtw35voeG6YNZV6neoUM8s9rhgqZ0B947orBiR/NZUK0H28ORY7MCKbXMejdyFQj0zH+iSLrQNcexzotWyNTU3jNqxHzt7nUpRYl9LoZhqch2hGyI2a9jWatuaygEjN3Ys/L78kt+rZz8XE65qEondH9qG0Qb3+/ghqnXvXBTqpijEbVCoG2LvB4WXQt2QcH9f0v2fpKdQi5BRqEXIKNQiZOzaCdNhIYVammUfh8W5hFJPMI6lUEuz8KSZfYSUZWFUoZam2cd8SzrCOoVCLc1jWzDjqEJkAoVahIxCLULGbm0xvNNcoZbm2b/+glb7c/EKtTTPv9O89lVwhVrkT7DDYRS8uaVmCrUIGYVahIxCLUJGoRYho1CLkFGoRcgo1CJkFGoRMgq1CJlfMf/5w1FFkZsAAAAASUVORK5CYII=\",  \"publicFlag\": \"true\",  \"payloadInfos\": [    {      \"processingType\": 1,      \"payloadId\": null,      \"payloadName\": \"テストペイロード\",      \"payloadDetailText\": \"テストのペイロード情報を記載\",      \"imageData\": \"data:image/png;base64,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\",      \"filePhysicalName\": \"payload_hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\",      \"teigigaiCol\": \"aaa\"    }  ]}";
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(0)).scanVirus(dtoCaptor.capture());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報の文字項目のダブルクオートなしの場合の動作を確認する<br>
   * 条件: ペイロード情報の文字項目にダブルクオートがないリクエストを送信<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_ペイロード情報文字項目のダブルクオートなし() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\", \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageDate\": \"data:image/png;base64,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\",  \"operatorId\": \"ope01\",  \"payloadInfos\": [    {      \"processingType\": 1,      \"payloadId\": null,      \"payloadName\": テストペイロード,      \"payloadDetailText\": \"テストのペイロード情報を記載\",      \"imageData\": \"data:image/png;base64,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\",      \"filePhysicalName\": \"payload_hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\"}  ]}";
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForRegist(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報のリスト内の要素がNULLの場合の正常登録処理を確認する<br>
   * 条件: ペイロード情報のリスト内の要素をnullに設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_ペイロード情報のリスト内の要素がNULL() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();
    requestDto.getPayloadInfos().set(0, null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報のバイナリデータがNULLの場合の正常登録処理を確認する<br>
   * 条件: ペイロード情報のバイナリデータをnullに設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_ペイロード情報のバイナリデータがNULL() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();
    requestDto.getPayloadInfos().get(0).setFileBinary(null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報jsonのフォーマット崩れの場合のエラー処理を確認する<br>
   * 条件: ペイロード情報jsonのフォーマットが崩れたリクエストを送信<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_ペイロード情報jsonのフォーマット崩れ() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\", \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageDate\": \"data:image/png;base64,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\",  \"operatorId\": \"ope01\",  \"payloadInfos\": [    {      \"processingType\": 1,      \"payloadId\": null,      \"payloadName\": テストペイロード,      \"payloadDetailText\": \"テストのペイロード情報を記載\",      \"imageData\\\": \"data:image/png;base64,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\",      \"filePhysicalName\": \"payload_hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\"} }";
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForRegist(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報のウイルスチェックで異常終了(チェックエラー)した際の挙動を確認する<br>
   * 条件: ペイロード情報のウイルスチェックでVirusScanErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_ペイロード情報ウイルスチェックで異常終了_チェックエラー() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(virusScanService)
        .scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForRegist(any());
    verify(virusScanService, times(1)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報のウイルスチェックで予期せぬエラーが発生した際の挙動を確認する<br>
   * 条件: ペイロード情報のウイルスチェックでRuntimeExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_ペイロード情報ウイルスチェックで異常終了_予期せぬエラー() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doThrow(new IllegalArgumentException("上記以外の例外が発生")).when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForRegist(any());
    verify(virusScanService, times(1)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ペイロード情報の2つ目のファイルのウイルスチェックで異常終了した際の挙動を確認する<br>
   * 条件: ペイロード情報の2つ目のファイルのウイルスチェックでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_post_ペイロード情報ウイルスチェックで異常終了_チェックエラー_2つ目のファイル() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload3();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing()
        .doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(virusScanService)
        .scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForRegist(any());
    verify(virusScanService, times(2)).scanVirus(any());
    verify(service, times(0)).postData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: ユーザ情報のマッピングが正しく行われることを確認する<br>
   * 条件: 正常なリクエストを送信し、ユーザ情報が正しくマッピングされる<br>
   * 結果: Serviceに伝わるユーザ情報が期待値と一致する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_post_ユーザ情報確認() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForRegist(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).postData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(1)).scanVirus(dtoCaptor.capture());
      assertEquals(
          new String(requestDto.getFileInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(dtoCaptor.getValue(), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), userCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 機体情報更新処理が正しく行われることを確認する<br>
   * 条件: 正常な AircraftInfoRequestDto を渡す<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void put_正常() throws Exception {
    AircraftInfoRequestDto requestDto = new AircraftInfoRequestDto();
    requestDto.setAircraftId("dummyAircraftId");
    requestDto.setAircraftName("dummyAircraftName");
    requestDto.setImageData("dummyImageData");
    requestDto.setAircraftType(1);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("responseAircraftId");

    doNothing().when(service).decodeBinary(any());
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
            .andExpect(jsonPath("$.aircraftId").value("responseAircraftId"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
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
   * テストパターン: 異常系<br>
   */
  @Test
  public void put_入力チェックエラー発生() throws Exception {
    AircraftInfoRequestDto requestDto = new AircraftInfoRequestDto();
    requestDto.setAircraftId("dummyAircraftId");
    requestDto.setAircraftName("dummyAircraftName");
    requestDto.setImageData("dummyImageData");

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("responseAircraftId");

    doNothing().when(service).decodeBinary(any());
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

    verify(service, times(1)).decodeBinary(any());
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
    AircraftInfoRequestDto requestDto = new AircraftInfoRequestDto();
    requestDto.setAircraftId("dummyAircraftId");
    requestDto.setAircraftName("dummyAircraftName");
    requestDto.setImageData("dummyImageData");

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("responseAircraftId");

    doNothing().when(service).decodeBinary(any());
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

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).putData(any(), any());
    verify(validator, times(1)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: バイナリ変換エでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void put_バイナリ変換エラー発生() throws Exception {
    AircraftInfoRequestDto requestDto = new AircraftInfoRequestDto();
    requestDto.setAircraftId("dummyAircraftId");
    requestDto.setAircraftName("dummyAircraftName");
    requestDto.setImageData("dummyImageData");

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("responseAircraftId");

    doThrow(new ServiceErrorException("dummyMessage")).when(service).decodeBinary(any());
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
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(0)).putData(any(), any());
    verify(validator, times(0)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 機体情報更新APIが呼び出されないことを確認する<br>
   * 条件: リクエストボディを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void put_リクエストボディなし() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doReturn(responseDto).when(service).putData(any(), any());
    doNothing().when(virusScanService).scanVirus(any());
    doNothing().when(validator).validateForUpdate(any());

    MvcResult response =
        mockMvc
            .perform(
                put(basePath).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForUpdate(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足資料を1つ含む正常な更新リクエストの処理を確認する<br>
   * 条件: 補足資料を1つ含む正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_正常補足資料1つ() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(1)).scanVirus(dtoCaptor.capture());
      assertEquals(
          new String(requestDto.getFileInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(dtoCaptor.getValue(), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足資料を3つ含む正常な更新リクエストの処理を確認する<br>
   * 条件: 補足資料を3つ含む正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_正常補足資料3つ() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku3();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(3)).scanVirus(dtoCaptor.capture());
      List<byte[]> allValues = dtoCaptor.getAllValues();
      assertEquals(
          new String(requestDto.getFileInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(0), StandardCharsets.UTF_8));
      assertEquals(
          new String(requestDto.getFileInfos().get(1).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(1), StandardCharsets.UTF_8));
      assertEquals(
          new String(requestDto.getFileInfos().get(2).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(2), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足資料が空配列の場合の正常な更新処理を確認する<br>
   * 条件: 補足資料を空配列に設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_正常補足資料空配列() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_empList();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足資料項目がない場合の正常な更新処理を確認する<br>
   * 条件: 補足資料項目を含まないリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_正常補足資料項目なし() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosokuNull();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 定義外項目が含まれている場合の更新処理を確認する<br>
   * 条件: 定義外の項目を含むリクエストを送信<br>
   * 結果: 定義外項目が無視され、処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_定義外項目あり() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\",  \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageData\": \"data:image/png;base64,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\",  \"publicFlag\": \"true\",  \"fileInfos\": [    {      \"processingType\": 1,      \"fileId\": null,      \"fileLogicalName\": \"補足資料論理名補足資料論理名\",      \"filePhysicalName\": \"hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\",      \"teigigaiCol\": \"aaa\"    }  ]}";
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(0))
          .scanVirus(dtoCaptor.capture()); // モック実行のため、バイナリデータが設定されないので0
      // assertEquals(new
      // String(requestDto.getFileInfos().get(0).getFileBinary(),StandardCharsets.UTF_8), new
      // String(dtoCaptor.getValue(),StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 文字項目のダブルクオートがない場合のエラー処理を確認する<br>
   * 条件: 文字項目のダブルクオートを省略したリクエストを送信<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_文字項目のダブルクオートなし() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\",  \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageData\": \"data:image/png;base64,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\",  \"operatorId\": \"ope01\",  \"fileInfos\": [    {      \"processingType\": 1,      \"fileId\": null,      \"fileLogicalName\": 補足資料論理名補足資料論理名,      \"filePhysicalName\": \"hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\"    }  ]}";
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForUpdate(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足情報ファイルのリスト内にNULL要素がある場合の正常な更新処理を確認する<br>
   * 条件: 補足情報ファイルのリスト内の要素をnullに設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_補足情報ファイルのリスト内の要素がNULL() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();
    requestDto.getFileInfos().set(0, null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足情報ファイルのバイナリデータがNULLの場合の正常な更新処理を確認する<br>
   * 条件: 補足情報ファイルのバイナリデータをnullに設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_補足情報ファイルのバイナリデータがNULL() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();
    requestDto.getFileInfos().get(0).setFileBinary(null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足情報jsonのフォーマット崩れの場合のエラー処理を確認する<br>
   * 条件: 補足情報jsonのフォーマットが崩れたリクエストを送信<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_jsonのフォーマット崩れ() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\",  \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageData\": \"data:image/png;base64,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\",  \"operatorId\": \"ope01\",  \"fileInfos\": [    {      \"processingType\": 1,      \"fileId\": null,      \"fileLogicalName\": \"補足資料論理名補足資料論理名\",      \"filePhysicalName\": \"hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\"    }  }";
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForUpdate(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ファイルバイナリデコード処理が異常終了した際の挙動を確認する<br>
   * 条件: ファイルバイナリデコード処理でIllegalArgumentExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_ファイルバイナリデコード処理が異常終了した際の挙動() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();
    requestDto.getFileInfos().get(0).setFileBinary(null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doThrow(new IllegalArgumentException("上記以外の例外が発生")).when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(0)).validateForUpdate(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足情報ファイルのウイルスチェックで異常終了(チェックエラー)した際の挙動を確認する<br>
   * 条件: 補足情報ファイルのウイルスチェックでVirusScanErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_ウイルスチェックで異常終了_チェックエラー() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(virusScanService)
        .scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForUpdate(any());
    verify(virusScanService, times(1)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足情報ファイルのウイルスチェックで予期せぬエラーが発生した際の挙動を確認する<br>
   * 条件: 補足情報ファイルのウイルスチェックでRuntimeExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_ウイルスチェックで異常終了_予期せぬエラー() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doThrow(new IllegalArgumentException("上記以外の例外が発生")).when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForUpdate(any());
    verify(virusScanService, times(1)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 補足情報ファイルの2つ目のファイルのウイルスチェックで異常終了した際の挙動を確認する<br>
   * 条件: 補足情報ファイルの2つ目のファイルのウイルスチェックでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_ウイルスチェックで異常終了_チェックエラー_2つ目のファイル() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku3();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing()
        .doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(virusScanService)
        .scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForUpdate(any());
    verify(virusScanService, times(2)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報を1つ含む正常な更新リクエストの処理を確認する<br>
   * 条件: ペイロード情報を1つ含む正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_正常ペイロード情報1つ() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(1)).scanVirus(dtoCaptor.capture());
      assertEquals(
          new String(requestDto.getPayloadInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(dtoCaptor.getValue(), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報を3つ含む正常な更新リクエストの処理を確認する<br>
   * 条件: ペイロード情報を3つ含む正常なリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_正常ペイロード情報3つ() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload3();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(3)).scanVirus(dtoCaptor.capture());
      List<byte[]> allValues = dtoCaptor.getAllValues();
      assertEquals(
          new String(requestDto.getPayloadInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(0), StandardCharsets.UTF_8));
      assertEquals(
          new String(requestDto.getPayloadInfos().get(1).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(1), StandardCharsets.UTF_8));
      assertEquals(
          new String(requestDto.getPayloadInfos().get(2).getFileBinary(), StandardCharsets.UTF_8),
          new String(allValues.get(2), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報が空配列の場合の正常な更新処理を確認する<br>
   * 条件: ペイロード情報を空配列に設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_正常ペイロード情報空配列() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payloadEmpList();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報項目がない場合の正常な更新処理を確認する<br>
   * 条件: ペイロード情報項目を含まないリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_正常ペイロード情報項目なし() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payloadNull();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報に定義外項目が含まれている場合の更新処理を確認する<br>
   * 条件: ペイロード情報に定義外の項目を含むリクエストを送信<br>
   * 結果: 定義外項目が無視され、処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_ペイロード情報定義外項目あり() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\",  \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageData\": \"data:image/png;base64,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\",  \"publicFlag\": \"true\",  \"payloadInfos\": [    {      \"processingType\": 1,      \"payloadId\": null,      \"payloadName\": \"テストペイロード\",      \"payloadDetailText\": \"テストのペイロード情報を記載\",      \"imageData\": \"data:image/png;base64,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\",      \"filePhysicalName\": \"payload_hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\",      \"teigigaiCol\": \"aaa\"    }  ]}";
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(0)).scanVirus(dtoCaptor.capture());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報の文字項目のダブルクオートがない場合のエラー処理を確認する<br>
   * 条件: ペイロード情報の文字項目のダブルクオートを省略したリクエストを送信<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_ペイロード情報文字項目のダブルクオートなし() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\", \"manufacturingNumber\": \"N12345678\",  \"aircraftType\": 1,  \"maxTakeoffWeight\": 99,  \"bodyWeight\": 88,  \"maxFlightSpeed\": 77,  \"maxFlightTime\": 66,  \"lat\": 55,  \"lon\": 44,  \"certification\": true,  \"dipsRegistrationCode\": \"DIPS_1234\",  \"ownerType\": 1,  \"ownerId\": \"054bb198-ab4c-4bb1-a27c-78af8e495f7a\",  \"imageDate\": \"data:image/png;base64,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\",  \"operatorId\": \"ope01\",  \"payloadInfos\": [    {      \"processingType\": 1,      \"payloadId\": null,      \"payloadName\": テストペイロード,      \"payloadDetailText\": \"テストのペイロード情報を記載\",      \"imageData\": \"data:image/png;base64,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\",      \"filePhysicalName\": \"payload_hosoku.txt\",      \"fileData\": \"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\"}  ]}";
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForUpdate(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報のリスト内にNULL要素がある場合の正常な更新処理を確認する<br>
   * 条件: ペイロード情報のリスト内の要素をnullに設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_ペイロード情報のリスト内の要素がNULL() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();
    requestDto.getPayloadInfos().set(0, null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報のバイナリデータがNULLの場合の正常な更新処理を確認する<br>
   * 条件: ペイロード情報のバイナリデータをnullに設定したリクエストを送信<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_ペイロード情報のバイナリデータがNULL() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();
    requestDto.getFileInfos().get(0).setFileBinary(null);

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      verify(virusScanService, times(0)).scanVirus(any());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), any());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報jsonのフォーマット崩れの場合のエラー処理を確認する<br>
   * 条件: ペイロード情報jsonのフォーマットが崩れたリクエストを送信<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_ペイロード情報jsonのフォーマット崩れ() throws Exception {
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson =
        "{  \"aircraftId\": \"0a0711a5-ff74-4164-9309-8888b433cf22\",  \"aircraftName\": \"機体名機体名\",  \"manufacturer\": \"製造メーカー製造メーカー\", \"manufacturingNumber\": \"N12345678\\\",  \\\"aircraftType\\\": 1,  \\\"maxTakeoffWeight\\\": 99,  \\\"bodyWeight\\\": 88,  \\\"maxFlightSpeed\\\": 77,  \\\"maxFlightTime\\\": 66,  \\\"lat\\\": 55,  \\\"lon\\\": 44,  \\\"certification\\\": true,  \\\"dipsRegistrationCode\\\": \\\"DIPS_1234\\\",  \\\"ownerType\\\": 1,  \\\"ownerId\\\": \\\"054bb198-ab4c-4bb1-a27c-78af8e495f7a\\\",  \\\"imageDate\\\": \\\"data:image/png;base64,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\\\",  \\\"operatorId\\\": \\\"ope01\\\",  \\\"payloadInfos\\\": [    {      \\\"processingType\\\": 1,      \\\"payloadId\\\": null,      \\\"payloadName\\\": テストペイロード,      \\\"payloadDetailText\\\": \\\"テストのペイロード情報を記載\\\",      \\\"imageData\\\\\": \\\"data:image/png;base64,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\\\",      \\\"filePhysicalName\\\": \\\"payload_hosoku.txt\\\",      \\\"fileData\\\": \\\"data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65\\\"} }";
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).decodeFileData(any());
    verify(validator, times(0)).validateForUpdate(any());
    verify(virusScanService, times(0)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報のウイルスチェックで異常終了(チェックエラー)した際の挙動を確認する<br>
   * 条件: ペイロード情報のウイルスチェックでVirusScanErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_ペイロード情報ウイルスチェックで異常終了_チェックエラー() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(virusScanService)
        .scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForUpdate(any());
    verify(virusScanService, times(1)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報のウイルスチェックで予期せぬエラーが発生した際の挙動を確認する<br>
   * 条件: ペイロード情報のウイルスチェックでRuntimeExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_ペイロード情報ウイルスチェックで異常終了_予期せぬエラー() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doThrow(new IllegalArgumentException("上記以外の例外が発生")).when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForUpdate(any());
    verify(virusScanService, times(1)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ペイロード情報の2つ目のファイルのウイルスチェックで異常終了した際の挙動を確認する<br>
   * 条件: ペイロード情報の2つ目のファイルのウイルスチェックでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_put_ペイロード情報ウイルスチェックで異常終了_チェックエラー_2つ目のファイル() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_payload3();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing()
        .doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(virusScanService)
        .scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).decodeFileData(any());
    verify(validator, times(1)).validateForUpdate(any());
    verify(virusScanService, times(2)).scanVirus(any());
    verify(service, times(0)).putData(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: ユーザ情報のマッピングが正しく行われることを確認する<br>
   * 条件: 正常なリクエストを送信し、ユーザ情報が正しくマッピングされる<br>
   * 結果: Serviceに伝わるユーザ情報が期待値と一致する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_put_ユーザ情報確認() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosoku1();

    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    doNothing().when(service).decodeBinary(any());
    doNothing().when(service).decodeFileData(any());
    doNothing().when(validator).validateForUpdate(any());
    doNothing().when(virusScanService).scanVirus(any());
    doReturn(responseDto).when(service).putData(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                put(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf22"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeFileData(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForUpdate(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<byte[]> dtoCaptor = ArgumentCaptor.forClass(byte[].class);
      verify(virusScanService, times(1)).scanVirus(dtoCaptor.capture());
      assertEquals(
          new String(requestDto.getFileInfos().get(0).getFileBinary(), StandardCharsets.UTF_8),
          new String(dtoCaptor.getValue(), StandardCharsets.UTF_8));
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).putData(dtoCaptor.capture(), userCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報一覧取得が正しく行われることを確認する<br>
   * 条件: 正常な DronePortInfoListRequestDto を渡す<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getList_正常系() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    AircraftInfoSearchListResponseDto responseDto = new AircraftInfoSearchListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    AircraftInfoSearchListRequestDto requestDto = createAircraftInfoSearchListRequestDto();
    String queryParam =
        "aircraftName="
            + requestDto.getAircraftName()
            + "&manufacturer="
            + requestDto.getManufacturer()
            + "&modelNumber="
            + requestDto.getModelNumber()
            + "&modelName="
            + requestDto.getModelName()
            + "&manufacturingNumber="
            + requestDto.getManufacturingNumber()
            + "&aircraftType="
            + requestDto.getAircraftType()
            + "&certification="
            + requestDto.getCertification()
            + "&dipsRegistrationCode="
            + requestDto.getDipsRegistrationCode()
            + "&ownerType="
            + requestDto.getOwnerType()
            + "&ownerId="
            + requestDto.getOwnerId()
            + "&minLat="
            + requestDto.getMinLat()
            + "&minLon="
            + requestDto.getMinLon()
            + "&maxLat="
            + requestDto.getMaxLat()
            + "&maxLon="
            + requestDto.getMaxLon()
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
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture(), userCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
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

    AircraftInfoSearchListResponseDto responseDto = new AircraftInfoSearchListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    String queryParam = "aircraftType=" + "1";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list?" + queryParam))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(0)).getList(any(), any());

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
  public void getList_サービスエラー発生() throws Exception {
    doNothing().when(validator).validateForGetList(any());
    doThrow(new ServiceErrorException("dummyMessage")).when(service).getList(any(), any());

    String queryParam = "aircraftType=" + "1";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list?" + queryParam))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(1)).getList(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報一覧取得が正しく行われることを確認する<br>
   * 条件: クエリパラメータなし<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void getList_クエリパラメータ未設定() throws Exception {
    doNothing().when(validator).validateForGetList(any());
    AircraftInfoSearchListResponseDto responseDto = new AircraftInfoSearchListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list"))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(1)).getList(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 機体情報一覧取得が正しく行われることを確認する<br>
   * 条件: DronePortInfoListRequestDto を渡さず実行<br>
   * 結果: 機体情報公開データ抽出が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常系1() throws Exception {
    // テスト用UserInfoDtoをSecurityContextに設定
    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setDummyUserFlag(true);

    List<GrantedAuthority> authorities = new ArrayList<>();

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);

    doNothing().when(validator).validateForGetList(any());

    AircraftInfoSearchListResponseDto responseDto = new AircraftInfoSearchListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    // 結果確認用DTO作成
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    // 結果確認のためテンプレートから固定項目上書き
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setPage(null);
    requestDto.setPerPage(null);
    requestDto.setIsRequiredPayloadInfo("false");
    requestDto.setPublicFlag("true");

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture(), userCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertNull(capturedUserInfo.getUserOperatorId());
      assertNull(capturedUserInfo.getRoles());
      assertNull(capturedUserInfo.getAffiliatedOperatorId());
      assertEquals(true, capturedUserInfo.isDummyUserFlag());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 機体情報一覧取得が正しく行われることを確認する<br>
   * 条件: 正常な DronePortInfoListRequestDto を渡す<br>
   * 結果: 機体情報公開データ抽出が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常系2() throws Exception {
    // テスト用UserInfoDtoをSecurityContextに設定
    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setDummyUserFlag(true);

    List<GrantedAuthority> authorities = new ArrayList<>();

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);

    doNothing().when(validator).validateForGetList(any());

    AircraftInfoSearchListResponseDto responseDto = new AircraftInfoSearchListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    AircraftInfoSearchListRequestDto requestDto = createAircraftInfoSearchListRequestDto();
    String queryParam =
        "aircraftName="
            + requestDto.getAircraftName()
            + "&manufacturer="
            + requestDto.getManufacturer()
            + "&modelNumber="
            + requestDto.getModelNumber()
            + "&modelName="
            + requestDto.getModelName()
            + "&manufacturingNumber="
            + requestDto.getManufacturingNumber()
            + "&aircraftType="
            + requestDto.getAircraftType()
            + "&certification="
            + requestDto.getCertification()
            + "&dipsRegistrationCode="
            + requestDto.getDipsRegistrationCode()
            + "&ownerType="
            + requestDto.getOwnerType()
            + "&ownerId="
            + requestDto.getOwnerId()
            + "&minLat="
            + requestDto.getMinLat()
            + "&minLon="
            + requestDto.getMinLon()
            + "&maxLat="
            + requestDto.getMaxLat()
            + "&maxLon="
            + requestDto.getMaxLon();

    // 結果確認のためテンプレートから固定項目上書き
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setPage(null);
    requestDto.setPerPage(null);
    requestDto.setIsRequiredPayloadInfo("false");
    requestDto.setPublicFlag("true");

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture(), userCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertNull(capturedUserInfo.getUserOperatorId());
      assertNull(capturedUserInfo.getRoles());
      assertNull(capturedUserInfo.getAffiliatedOperatorId());
      assertEquals(true, capturedUserInfo.isDummyUserFlag());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getPublicDataExtract<br>
   * 試験名: 機体情報一覧取得が正しく行われることを確認する<br>
   * 条件: 定義外項目を含む DronePortInfoListRequestDto を渡す<br>
   * 結果: 機体情報公開データ抽出が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常系_定義外項目あり() throws Exception {
    // テスト用UserInfoDtoをSecurityContextに設定
    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setDummyUserFlag(true);

    List<GrantedAuthority> authorities = new ArrayList<>();

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);

    doNothing().when(validator).validateForGetList(any());

    AircraftInfoSearchListResponseDto responseDto = new AircraftInfoSearchListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    AircraftInfoSearchListRequestDto requestDto = createAircraftInfoSearchListRequestDto();
    String queryParam =
        "aircraftName="
            + requestDto.getAircraftName()
            + "&manufacturer="
            + requestDto.getManufacturer()
            + "&modelNumber="
            + requestDto.getModelNumber()
            + "&modelName="
            + requestDto.getModelName()
            + "&manufacturingNumber="
            + requestDto.getManufacturingNumber()
            + "&aircraftType="
            + requestDto.getAircraftType()
            + "&certification="
            + requestDto.getCertification()
            + "&dipsRegistrationCode="
            + requestDto.getDipsRegistrationCode()
            + "&ownerType="
            + requestDto.getOwnerType()
            + "&ownerId="
            + requestDto.getOwnerId()
            + "&minLat="
            + requestDto.getMinLat()
            + "&minLon="
            + requestDto.getMinLon()
            + "&maxLat="
            + requestDto.getMaxLat()
            + "&maxLon="
            + requestDto.getMaxLon()
            + "&teigigai="
            + "teigigai";

    // 結果確認のためテンプレートから固定項目上書き
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setPage(null);
    requestDto.setPerPage(null);
    requestDto.setIsRequiredPayloadInfo("false");
    requestDto.setPublicFlag("true");

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture(), userCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertNull(capturedUserInfo.getUserOperatorId());
      assertNull(capturedUserInfo.getRoles());
      assertNull(capturedUserInfo.getAffiliatedOperatorId());
      assertEquals(true, capturedUserInfo.isDummyUserFlag());
    }

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
  public void getPublicDataExtract_異常系_チェックエラー() throws Exception {
    // テスト用UserInfoDtoをSecurityContextに設定
    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setDummyUserFlag(true);

    List<GrantedAuthority> authorities = new ArrayList<>();

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);

    doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(validator)
        .validateForGetList(any());

    AircraftInfoSearchListResponseDto responseDto = new AircraftInfoSearchListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    // 結果確認用DTO作成
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(0)).getList(any(), any());

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
  public void getPublicDataExtract_異常系_サービスエラー() throws Exception {
    // テスト用UserInfoDtoをSecurityContextに設定
    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setDummyUserFlag(true);

    List<GrantedAuthority> authorities = new ArrayList<>();

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);

    doNothing().when(validator).validateForGetList(any());

    doThrow(new ServiceErrorException("ServiceErrorExceptionが発生"))
        .when(service)
        .getList(any(), any());

    // 結果確認用DTO作成
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorExceptionが発生"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(1)).getList(any(), any());

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
  public void getPublicDataExtract_異常系_予期せぬエラー() throws Exception {
    // テスト用UserInfoDtoをSecurityContextに設定
    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setDummyUserFlag(true);

    List<GrantedAuthority> authorities = new ArrayList<>();

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);

    doNothing().when(validator).validateForGetList(any());
    doThrow(new IllegalArgumentException("予期せぬ例外が発生")).when(service).getList(any(), any());

    // 結果確認用DTO作成
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("予期せぬ例外が発生"))
            .andReturn();

    verify(validator, times(1)).validateForGetList(any());
    verify(service, times(1)).getList(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: modelSearch<br>
   * 試験名: モデル情報一覧取得処理が正しく行われることを確認する<br>
   * 条件: 正常な AircraftInfoModelSearchRequestDto を渡す<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void modelSearch_正常() throws Exception {
    AircraftInfoModelSearchRequestDto requestDto = createAircraftInfoModelSearchRequestDto();

    AircraftInfoSearchListResponseDto responseDto =
        createAircraftInfoSearchListResponseModelSearchDto();

    doNothing().when(validator).validateForModelSearch(any());
    doReturn(responseDto).when(service).getList(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath + "/modelSearch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(
                jsonPath("$.data[0].aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf11"))
            .andExpect(jsonPath("$.data[0].payloadInfos").isArray())
            .andExpect(
                jsonPath("$.data[0].payloadInfos[0].payloadId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf21"))
            .andExpect(jsonPath("$.data[0].priceInfos").isArray())
            .andExpect(
                jsonPath("$.data[0].priceInfos[0].priceId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf31"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoModelSearchRequestDto> requestCaptor =
          ArgumentCaptor.forClass(AircraftInfoModelSearchRequestDto.class);
      verify(validator, times(1)).validateForModelSearch(requestCaptor.capture());
      assertEquals(requestDto.toString(), requestCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture(), any());
      assertEquals(
          requestDto.getIsRequiredPayloadInfo(), dtoCaptor.getValue().getIsRequiredPayloadInfo());
      assertEquals(
          requestDto.getIsRequiredPriceInfo(), dtoCaptor.getValue().getIsRequiredPriceInfo());
      assertEquals(3, dtoCaptor.getValue().getModelInfos().size());
      assertEquals(
          requestDto.getModelInfos().get(0).getManufacturer(),
          dtoCaptor.getValue().getModelInfos().get(0).getManufacturer());
      assertEquals(
          requestDto.getModelInfos().get(0).getModelNumber(),
          dtoCaptor.getValue().getModelInfos().get(0).getModelNumber());
      assertEquals(
          requestDto.getModelInfos().get(1).getManufacturer(),
          dtoCaptor.getValue().getModelInfos().get(1).getManufacturer());
      assertEquals(
          requestDto.getModelInfos().get(1).getModelNumber(),
          dtoCaptor.getValue().getModelInfos().get(1).getModelNumber());
      assertEquals(
          requestDto.getModelInfos().get(2).getManufacturer(),
          dtoCaptor.getValue().getModelInfos().get(2).getManufacturer());
      assertEquals(
          requestDto.getModelInfos().get(2).getModelNumber(),
          dtoCaptor.getValue().getModelInfos().get(2).getModelNumber());
    }

    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).getList(any(), userInfoCaptor.capture());
      UserInfoDto capturedUserInfo = userInfoCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
    }
    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: modelSearch<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void modelSearch_入力チェックエラー発生() throws Exception {
    AircraftInfoModelSearchRequestDto requestDto = createAircraftInfoModelSearchRequestDto();

    AircraftInfoSearchListResponseDto responseDto =
        createAircraftInfoSearchListResponseModelSearchDto();

    doReturn(responseDto).when(service).getList(any(), any());
    doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(validator)
        .validateForModelSearch(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath + "/modelSearch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(validator, times(1)).validateForModelSearch(any());
    verify(service, times(0)).getList(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: modelSearch<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void modelSearch_サービスエラー発生() throws Exception {
    AircraftInfoModelSearchRequestDto requestDto = createAircraftInfoModelSearchRequestDto();

    doNothing().when(validator).validateForModelSearch(any());
    doThrow(new ServiceErrorException("ServiceErrorExceptionが発生"))
        .when(service)
        .getList(any(), any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath + "/modelSearch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorExceptionが発生"))
            .andReturn();

    verify(validator, times(1)).validateForModelSearch(any());
    verify(service, times(1)).getList(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: modelSearch<br>
   * 試験名: リクエストボディなしの場合<br>
   * 条件: リクエストボディを未設定<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void modelSearch_リクエストボディなし() throws Exception {
    MvcResult response =
        mockMvc
            .perform(
                post(basePath + "/modelSearch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8"))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(validator, times(0)).validateForModelSearch(any());
    verify(service, times(0)).getList(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: modelSearch<br>
   * テストパターン: 定義外項目あり<br>
   * 条件: リクエストボディに定義外の項目が含まれる<br>
   * 結果: 定義外項目は無視され、正常に処理される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void modelSearch_定義外項目あり() throws Exception {
    AircraftInfoModelSearchRequestDto requestDto = createAircraftInfoModelSearchRequestDto();

    AircraftInfoSearchListResponseDto responseDto =
        createAircraftInfoSearchListResponseModelSearchDto();

    doNothing().when(validator).validateForModelSearch(any());
    doReturn(responseDto).when(service).getList(any(), any());

    // 定義外項目 "teigigaiCol" を含むJSON
    String requestJson =
        "{\"modelInfos\":[{\"manufacturer\":\"製造メーカー1\",\"modelNumber\":\"MD12345V1\",\"teigigaiCol\":\"aaa\"},{\"manufacturer\":\"製造メーカー1\",\"modelNumber\":\"MD12345V2\"},{\"manufacturer\":\"製造メーカー2\",\"modelNumber\":\"MD12345V1\"}],\"isRequiredPayloadInfo\":\"true\",\"isRequiredPriceInfo\":\"true\",\"teigigaiCol\":\"bbb\"}";

    MvcResult response =
        mockMvc
            .perform(
                post(basePath + "/modelSearch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(
                jsonPath("$.data[0].aircraftId").value("0a0711a5-ff74-4164-9309-8888b433cf11"))
            .andExpect(jsonPath("$.data[0].payloadInfos").isArray())
            .andExpect(
                jsonPath("$.data[0].payloadInfos[0].payloadId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf21"))
            .andExpect(jsonPath("$.data[0].priceInfos").isArray())
            .andExpect(
                jsonPath("$.data[0].priceInfos[0].priceId")
                    .value("0a0711a5-ff74-4164-9309-8888b433cf31"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoModelSearchRequestDto> requestCaptor =
          ArgumentCaptor.forClass(AircraftInfoModelSearchRequestDto.class);
      verify(validator, times(1)).validateForModelSearch(requestCaptor.capture());
      assertEquals(requestDto.toString(), requestCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoSearchListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoSearchListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture(), any());
      assertEquals(
          requestDto.getIsRequiredPayloadInfo(), dtoCaptor.getValue().getIsRequiredPayloadInfo());
      assertEquals(
          requestDto.getIsRequiredPriceInfo(), dtoCaptor.getValue().getIsRequiredPriceInfo());
      assertEquals(3, dtoCaptor.getValue().getModelInfos().size());
      assertEquals(
          requestDto.getModelInfos().get(0).getManufacturer(),
          dtoCaptor.getValue().getModelInfos().get(0).getManufacturer());
      assertEquals(
          requestDto.getModelInfos().get(0).getModelNumber(),
          dtoCaptor.getValue().getModelInfos().get(0).getModelNumber());
      assertEquals(
          requestDto.getModelInfos().get(1).getManufacturer(),
          dtoCaptor.getValue().getModelInfos().get(1).getManufacturer());
      assertEquals(
          requestDto.getModelInfos().get(1).getModelNumber(),
          dtoCaptor.getValue().getModelInfos().get(1).getModelNumber());
      assertEquals(
          requestDto.getModelInfos().get(2).getManufacturer(),
          dtoCaptor.getValue().getModelInfos().get(2).getManufacturer());
      assertEquals(
          requestDto.getModelInfos().get(2).getModelNumber(),
          dtoCaptor.getValue().getModelInfos().get(2).getModelNumber());
    }

    {
      ArgumentCaptor<UserInfoDto> userInfoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).getList(any(), userInfoCaptor.capture());
      UserInfoDto capturedUserInfo = userInfoCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: modelSearch<br>
   * テストパターン: リクエストの形式エラー(文字項目ダブルクォートなし)<br>
   * 条件: JSONの文字列項目にダブルクォートがない<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void modelSearch_文字項目のダブルクォートなし() throws Exception {
    doNothing().when(validator).validateForModelSearch(any());
    doReturn(new AircraftInfoSearchListResponseDto()).when(service).getList(any(), any());

    // manufacturer の値にダブルクォートがない不正なJSON
    String requestJson =
        "{\"modelInfos\":[{\"manufacturer\":製造メーカー1,\"modelNumber\":\"MD12345V1\"}],\"isRequiredPayloadInfo\":\"true\",\"isRequiredPriceInfo\":\"true\"}";

    MvcResult response =
        mockMvc
            .perform(
                post(basePath + "/modelSearch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(validator, times(0)).validateForModelSearch(any());
    verify(service, times(0)).getList(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: modelSearch<br>
   * テストパターン: リクエストの形式エラー(JSONのフォーマット崩れ)<br>
   * 条件: JSONの閉じ括弧が不足している<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void modelSearch_jsonのフォーマット崩れ() throws Exception {
    doNothing().when(validator).validateForModelSearch(any());
    doReturn(new AircraftInfoSearchListResponseDto()).when(service).getList(any(), any());

    // 配列の閉じ括弧が不足している不正なJSON
    String requestJson =
        "{\"modelInfos\":[{\"manufacturer\":\"製造メーカー1\",\"modelNumber\":\"MD12345V1\"},{\"manufacturer\":\"製造メーカー2\",\"modelNumber\":\"MD12345V2\"},\"isRequiredPayloadInfo\":\"true\",\"isRequiredPriceInfo\":\"true\"}";

    MvcResult response =
        mockMvc
            .perform(
                post(basePath + "/modelSearch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(validator, times(0)).validateForModelSearch(any());
    verify(service, times(0)).getList(any(), any());

    assertNotNull(response.getResponse().getContentAsString(StandardCharsets.UTF_8));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報詳細取得が正しく行われることを確認する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getDetail_正常() throws Exception {
    String aircraftId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftId);

    AircraftInfoDetailResponseDto responseDto = new AircraftInfoDetailResponseDto();
    responseDto.setAircraftId(aircraftId);
    responseDto.setAircraftName("dummyName");
    when(service.getDetail(any(), any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.aircraftId").value(aircraftId),
                jsonPath("$.aircraftName").value("dummyName"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    verify(service, times(1)).getDetail(any(), any(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報詳細取得が正しく行われることを確認する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getDetail_正常_ユーザ情報確認() throws Exception {
    String aircraftId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftId);

    AircraftInfoDetailResponseDto responseDto = new AircraftInfoDetailResponseDto();
    responseDto.setAircraftId(aircraftId);
    responseDto.setAircraftName("dummyName");
    when(service.getDetail(any(), any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.aircraftId").value(aircraftId),
                jsonPath("$.aircraftName").value("dummyName"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
    verify(service, times(1)).getDetail(any(), any(), any(), userCaptor.capture());
    UserInfoDto capturedUserInfo = userCaptor.getValue();
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
    assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
    assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
    assertEquals(
        "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());

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
    String aircraftId = "dummyuuid";

    // 入力エラーを意図的に発生させる
    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForDetail(aircraftId);

    AircraftInfoDetailResponseDto responseDto = new AircraftInfoDetailResponseDto();
    when(service.getDetail(any(), any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    verify(service, times(0)).getDetail(anyString(), anyBoolean(), anyBoolean(), any());

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
    String aircraftId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftId);
    doThrow(new ServiceErrorException("ServiceErrorMessage"))
        .when(service)
        .getDetail(any(), any(), any(), any());

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorMessage"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    verify(service, times(1)).getDetail(anyString(), anyBoolean(), anyBoolean(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報詳細取得APIが呼び出されないことを確認する<br>
   * 条件: 機体IDを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void getDetail_機体ID未設定() throws Exception {
    String aircraftId = "";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId))
            .andExpect(status().isInternalServerError())
            .andReturn();

    verify(validator, times(0)).validateForDetail(anyString());
    verify(service, times(0)).getDetail(anyString(), anyBoolean(), anyBoolean(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報詳細取得が正しく行われることを確認する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getDetail_正常ペイロード要否true() throws Exception {
    String aircraftId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftId);

    AircraftInfoDetailResponseDto responseDto = new AircraftInfoDetailResponseDto();
    responseDto.setAircraftId(aircraftId);
    responseDto.setAircraftName("dummyName");
    when(service.getDetail(any(), any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId + "?isRequiredPayloadInfo=true"))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.aircraftId").value(aircraftId),
                jsonPath("$.aircraftName").value("dummyName"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    verify(service, times(1)).getDetail(any(), any(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報詳細取得が正しく行われることを確認する<br>
   * 条件: 正常な機体IDを渡す、料金情報要否がtrue<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getDetail_正常_料金情報要否true() throws Exception {
    String aircraftId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftId);

    AircraftInfoDetailResponseDto responseDto = new AircraftInfoDetailResponseDto();
    responseDto.setAircraftId(aircraftId);
    responseDto.setAircraftName("dummyName");
    when(service.getDetail(any(), any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId + "?isRequiredPriceInfo=true"))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.aircraftId").value(aircraftId),
                jsonPath("$.aircraftName").value("dummyName"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    verify(service, times(1)).getDetail(any(), any(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報詳細取得が正しく行われることを確認する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getDetail_正常ペイロード要否false() throws Exception {
    String aircraftId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftId);

    AircraftInfoDetailResponseDto responseDto = new AircraftInfoDetailResponseDto();
    responseDto.setAircraftId(aircraftId);
    responseDto.setAircraftName("dummyName");
    when(service.getDetail(any(), any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId + "?isRequiredPayloadInfo=false"))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.aircraftId").value(aircraftId),
                jsonPath("$.aircraftName").value("dummyName"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    verify(service, times(1)).getDetail(any(), any(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報詳細取得が正しく行われることを確認する<br>
   * 条件: 正常な機体IDを渡す、料金情報要否false<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void getDetail_正常_料金情報要否false() throws Exception {
    String aircraftId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftId);

    AircraftInfoDetailResponseDto responseDto = new AircraftInfoDetailResponseDto();
    responseDto.setAircraftId(aircraftId);
    responseDto.setAircraftName("dummyName");
    when(service.getDetail(any(), any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId + "?isRequiredPriceInfo=false"))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.aircraftId").value(aircraftId),
                jsonPath("$.aircraftName").value("dummyName"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    verify(service, times(1)).getDetail(any(), any(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 機体情報削除が正しく行われることを確認する<br>
   * 条件: 任意機体IDをURLに設定。入力チェックが正常。削除処理が正常。<br>
   * 結果: HTTPコード:200が返却されること。<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void delete_正常() throws Exception {
    String aircraftId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftId);
    doNothing().when(service).deleteData(eq(aircraftId), any());

    MvcResult response =
        mockMvc.perform(delete(basePath + "/" + aircraftId)).andExpect(status().isOk()).andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    {
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).deleteData(eq(aircraftId), userCaptor.capture());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
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
    String aircraftId = UUID.randomUUID().toString();

    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForDetail(aircraftId);
    doNothing().when(service).deleteData(eq(aircraftId), any());

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + aircraftId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    verify(service, times(0)).deleteData(anyString(), any(UserInfoDto.class));

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
    String aircraftId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForDetail(aircraftId);
    doThrow(new ServiceErrorException("ServiceErrorMessage"))
        .when(service)
        .deleteData(eq(aircraftId), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + aircraftId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorMessage"))
            .andReturn();

    verify(validator, times(1)).validateForDetail(aircraftId);
    verify(service, times(1)).deleteData(eq(aircraftId), any(UserInfoDto.class));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報削除APIが呼び出されないことを確認する<br>
   * 条件: 機体IDを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 境界値<br>
   */
  @Test
  public void delete_機体ID未設定() throws Exception {
    doNothing().when(validator).validateForDetail(anyString());
    doNothing().when(validator).validateForDelete(any());
    doNothing().when(service).deleteData(anyString(), any());

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/"))
            .andExpect(status().isInternalServerError())
            .andReturn();

    verify(validator, times(0)).validateForDetail(anyString());
    verify(validator, times(0)).validateForDelete(any());
    verify(service, times(0)).deleteData(anyString(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: クエリパラメータなしでファイルダウンロードが正常に実行されることを確認する<br>
   * 条件: クエリパラメータを含まない正常なリクエストを送信<br>
   * 結果: ファイルデータが正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadFile_クエリパラメータなしの際に正常終了すること() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeAri_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andExpect(
                header().string("Content-Disposition", "attachment; filename*=UTF-8''hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<String> argCaptor2 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1))
          .validateForDownloadFile(argCaptor1.capture(), argCaptor2.capture());
      assertEquals(aircraftId, argCaptor1.getValue());
      assertEquals(fileId, argCaptor2.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadFile(argCaptor1.capture(), any());
      assertEquals(fileId, argCaptor1.getValue());
    }

    assertEquals(
        new String(fileEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: ファイルデータがnullの場合のダウンロード処理を確認する<br>
   * 条件: ファイルデータがnullのエンティティを返すサービスをモック<br>
   * 結果: 空のコンテンツが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadFile_ファイルデータnull() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeAri_FileNameNotContJpanese();
    fileEnt.setFileData(null);

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andExpect(
                header().string("Content-Disposition", "attachment; filename*=UTF-8''hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "0"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<String> argCaptor2 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1))
          .validateForDownloadFile(argCaptor1.capture(), argCaptor2.capture());
      assertEquals(aircraftId, argCaptor1.getValue());
      assertEquals(fileId, argCaptor2.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadFile(argCaptor1.capture(), any());
      assertEquals(fileId, argCaptor1.getValue());
    }

    assertEquals(
        new String(new byte[0]), new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: MIME設定ありで日本語を含まないファイル名のダウンロード処理を確認する<br>
   * 条件: MIME設定があり、日本語を含まないファイル名のファイルをダウンロードするリクエストを送信<br>
   * 結果: 正しいContent-TypeとContent-Dispositionヘッダーでファイルが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadFile_正常リクエスト_MIME設定あり_ファイル名に日本語なし() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeAri_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isOk())
            .andExpect(
                header().string("Content-Disposition", "attachment; filename*=UTF-8''hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<String> argCaptor2 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1))
          .validateForDownloadFile(argCaptor1.capture(), argCaptor2.capture());
      assertEquals(aircraftId, argCaptor1.getValue());
      assertEquals(fileId, argCaptor2.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadFile(argCaptor1.capture(), any());
      assertEquals(fileId, argCaptor1.getValue());
    }

    assertEquals(
        new String(fileEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 定義外カラムが含まれる場合のダウンロード処理を確認する<br>
   * 条件: 定義外のカラムを含むリクエストを送信<br>
   * 結果: 定義外カラムが無視され、ファイルが正常にダウンロードされる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadFile_定義外カラムあり() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeAri_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01")
                    .param("teigigaiCol", "999"))
            .andExpect(status().isOk())
            .andExpect(
                header().string("Content-Disposition", "attachment; filename*=UTF-8''hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<String> argCaptor2 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1))
          .validateForDownloadFile(argCaptor1.capture(), argCaptor2.capture());
      assertEquals(aircraftId, argCaptor1.getValue());
      assertEquals(fileId, argCaptor2.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadFile(argCaptor1.capture(), any());
      assertEquals(fileId, argCaptor1.getValue());
    }

    assertEquals(
        new String(fileEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: MIME設定ありで日本語を含むファイル名のダウンロード処理を確認する<br>
   * 条件: MIME設定があり、日本語を含むファイル名のファイルをダウンロードするリクエストを送信<br>
   * 結果: 正しくエンコードされたContent-Dispositionヘッダーでファイルが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadFile_正常リクエスト_MIME設定あり_ファイル名に日本語あり() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeAri_FileNameContJpanese();

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition",
                        "attachment; filename*=UTF-8''"
                            + URLEncoder.encode("補足.txt", StandardCharsets.UTF_8)))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<String> argCaptor2 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1))
          .validateForDownloadFile(argCaptor1.capture(), argCaptor2.capture());
      assertEquals(aircraftId, argCaptor1.getValue());
      assertEquals(fileId, argCaptor2.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadFile(argCaptor1.capture(), any());
      assertEquals(fileId, argCaptor1.getValue());
    }

    assertEquals(
        new String(fileEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: MIME設定なしで日本語を含まないファイル名のダウンロード処理を確認する<br>
   * 条件: MIME設定がなく、日本語を含まないファイル名のファイルをダウンロードするリクエストを送信<br>
   * 結果: デフォルトのContent-Typeでファイルが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadFile_正常リクエスト_MIME設定なし_ファイル名に日本語なし() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeNasi_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isOk())
            .andExpect(
                header().string("Content-Disposition", "attachment; filename*=UTF-8''hosoku.txt"))
            .andExpect(header().string("Content-Type", "application/octet-stream"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<String> argCaptor2 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1))
          .validateForDownloadFile(argCaptor1.capture(), argCaptor2.capture());
      assertEquals(aircraftId, argCaptor1.getValue());
      assertEquals(fileId, argCaptor2.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadFile(argCaptor1.capture(), any());
      assertEquals(fileId, argCaptor1.getValue());
    }

    assertEquals(
        new String(fileEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: MIME設定なしで日本語を含むファイル名のダウンロード処理を確認する<br>
   * 条件: MIME設定がなく、日本語を含むファイル名のファイルをダウンロードするリクエストを送信<br>
   * 結果: 正しくエンコードされたContent-Dispositionヘッダーでファイルが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadFile_正常リクエスト_MIME設定なし_ファイル名に日本語あり() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeNasi_FileNameContJpanese();

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition",
                        "attachment; filename*=UTF-8''"
                            + URLEncoder.encode("補足.txt", StandardCharsets.UTF_8)))
            .andExpect(header().string("Content-Type", "application/octet-stream"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<String> argCaptor2 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1))
          .validateForDownloadFile(argCaptor1.capture(), argCaptor2.capture());
      assertEquals(aircraftId, argCaptor1.getValue());
      assertEquals(fileId, argCaptor2.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadFile(argCaptor1.capture(), any());
      assertEquals(fileId, argCaptor1.getValue());
    }

    assertEquals(
        new String(fileEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 補足情報ファイルダウンロードリクエストチェック処理で異常終了(チェックエラー)した際の挙動を確認する<br>
   * 条件: リクエストチェック処理でValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_downloadFile_補足情報ファイルダウンロードリクエストチェック処理で異常終了_チェックエラー() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeAri_FileNameNotContJpanese();

    doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(validator)
        .validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(validator, times(1)).validateForDownloadFile(any(), any());
    verify(service, times(0)).downloadFile(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 補足情報ファイルダウンロードリクエストチェック処理で予期せぬエラーが発生した際の挙動を確認する<br>
   * 条件: リクエストチェック処理でIllegalArgumentExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_downloadFile_補足情報ファイルダウンロードリクエストチェック処理で異常終了_予期せぬエラー() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeAri_FileNameNotContJpanese();

    doThrow(new IllegalArgumentException("上記以外の例外が発生"))
        .when(validator)
        .validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(validator, times(1)).validateForDownloadFile(any(), any());
    verify(service, times(0)).downloadFile(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: ファイルダウンロードサービス実行処理でデータが見つからなかった際の挙動を確認する<br>
   * 条件: サービス処理でNotFoundExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_downloadFile_ファイルダウンロードサービス実行処理で異常終了_データなし() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doThrow(new NotFoundException("NotFoundExceptionが発生")).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("NotFoundExceptionが発生"))
            .andReturn();

    verify(validator, times(1)).validateForDownloadFile(any(), any());
    verify(service, times(1)).downloadFile(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: ファイルダウンロードサービス実行処理で予期せぬエラーが発生した際の挙動を確認する<br>
   * 条件: サービス処理でIllegalArgumentExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_downloadFile_ファイルダウンロードサービス実行処理で異常終了_予期せぬエラー() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doThrow(new IllegalArgumentException("上記以外の例外が発生")).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/detail/" + aircraftId + "/" + fileId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(validator, times(1)).validateForDownloadFile(any(), any());
    verify(service, times(1)).downloadFile(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: ユーザ情報のマッピングが正しく行われることを確認する<br>
   * 条件: 正常なリクエストを送信し、ユーザ情報が正しくマッピングされる<br>
   * 結果: ValidatorとServiceに伝わるユーザ情報が期待値と一致する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadFile_正常リクエスト_ユーザ情報確認() throws Exception {
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    FileInfoEntity fileEnt = createFileInfoEntity_MimeAri_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadFile(any(), any());
    doReturn(fileEnt).when(service).downloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + aircraftId + "/" + fileId))
            .andExpect(status().isOk())
            .andExpect(
                header().string("Content-Disposition", "attachment; filename*=UTF-8''hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<String> argCaptor2 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1))
          .validateForDownloadFile(argCaptor1.capture(), argCaptor2.capture());
      assertEquals(aircraftId, argCaptor1.getValue());
      assertEquals(fileId, argCaptor2.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).downloadFile(argCaptor.capture(), userCaptor.capture());
      assertEquals(fileId, argCaptor.getValue());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
    }

    assertEquals(
        new String(fileEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: クエリパラメータなしでペイロードファイルダウンロードが正常に実行されることを確認する<br>
   * 条件: クエリパラメータを含まない正常なリクエストを送信<br>
   * 結果: ペイロードファイルデータが正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_クエリパラメータなしの際に正常終了すること() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeAri_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition", "attachment; filename*=UTF-8''payload_hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1)).validateForDownloadPayloadFile(argCaptor1.capture());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadPayloadFile(argCaptor1.capture(), any());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    assertEquals(
        new String(paylodEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * テストパターン: ファイルデータnull<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_ファイルデータnull() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeAri_FileNameNotContJpanese();
    paylodEnt.setFileData(null);

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition", "attachment; filename*=UTF-8''payload_hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "0"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1)).validateForDownloadPayloadFile(argCaptor1.capture());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadPayloadFile(argCaptor1.capture(), any());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    assertEquals(
        new String(new byte[0]), new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: MIME設定ありで日本語を含まないファイル名のペイロードダウンロード処理を確認する<br>
   * 条件: MIME設定があり、日本語を含まないファイル名のペイロードをダウンロードするリクエストを送信<br>
   * 結果: 正しいContent-TypeとContent-Dispositionヘッダーでファイルが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_正常リクエスト_MIME設定あり_ファイル名に日本語なし() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeAri_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition", "attachment; filename*=UTF-8''payload_hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1)).validateForDownloadPayloadFile(argCaptor1.capture());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadPayloadFile(argCaptor1.capture(), any());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    assertEquals(
        new String(paylodEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: 定義外カラムが含まれる場合のペイロードダウンロード処理を確認する<br>
   * 条件: 定義外のカラムを含むリクエストを送信<br>
   * 結果: 定義外カラムが無視され、ペイロードが正常にダウンロードされる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_定義外カラムあり() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeAri_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01")
                    .param("teigigaiCol", "999"))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition", "attachment; filename*=UTF-8''payload_hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1)).validateForDownloadPayloadFile(argCaptor1.capture());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadPayloadFile(argCaptor1.capture(), any());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    assertEquals(
        new String(paylodEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: MIME設定ありで日本語を含むファイル名のペイロードダウンロード処理を確認する<br>
   * 条件: MIME設定があり、日本語を含むファイル名のペイロードをダウンロードするリクエストを送信<br>
   * 結果: 正しくエンコードされたContent-Dispositionヘッダーでファイルが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_正常リクエスト_MIME設定あり_ファイル名に日本語あり() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b02";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeAri_FileNameContJpanese();

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition",
                        "attachment; filename*=UTF-8''"
                            + URLEncoder.encode("ペイロード.txt", StandardCharsets.UTF_8)))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1)).validateForDownloadPayloadFile(argCaptor1.capture());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadPayloadFile(argCaptor1.capture(), any());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    assertEquals(
        new String(paylodEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: MIME設定なしで日本語を含まないファイル名のペイロードダウンロード処理を確認する<br>
   * 条件: MIME設定がなく、日本語を含まないファイル名のペイロードをダウンロードするリクエストを送信<br>
   * 結果: デフォルトのContent-Typeでファイルが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_正常リクエスト_MIME設定なし_ファイル名に日本語なし() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b03";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeNasi_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition", "attachment; filename*=UTF-8''payload_hosoku.txt"))
            .andExpect(header().string("Content-Type", "application/octet-stream"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1)).validateForDownloadPayloadFile(argCaptor1.capture());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadPayloadFile(argCaptor1.capture(), any());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    assertEquals(
        new String(paylodEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: MIME設定なしで日本語を含むファイル名のペイロードダウンロード処理を確認する<br>
   * 条件: MIME設定がなく、日本語を含むファイル名のペイロードをダウンロードするリクエストを送信<br>
   * 結果: 正しくエンコードされたContent-Dispositionヘッダーでファイルが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_正常リクエスト_MIME設定なし_ファイル名に日本語あり() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b04";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeNasi_FileNameContJpanese();

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition",
                        "attachment; filename*=UTF-8''"
                            + URLEncoder.encode("ペイロード.txt", StandardCharsets.UTF_8)))
            .andExpect(header().string("Content-Type", "application/octet-stream"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1)).validateForDownloadPayloadFile(argCaptor1.capture());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(service, times(1)).downloadPayloadFile(argCaptor1.capture(), any());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    assertEquals(
        new String(paylodEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: ペイロードファイルダウンロードリクエストチェック処理で異常終了(チェックエラー)した際の挙動を確認する<br>
   * 条件: リクエストチェック処理でValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_補足情報ファイルダウンロードリクエストチェック処理で異常終了_チェックエラー() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeAri_FileNameNotContJpanese();

    doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(validator)
        .validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("ValidationErrorExceptionが発生"))
            .andReturn();

    verify(validator, times(1)).validateForDownloadPayloadFile(any());
    verify(service, times(0)).downloadPayloadFile(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: ペイロードファイルダウンロードリクエストチェック処理で予期せぬエラーが発生した際の挙動を確認する<br>
   * 条件: リクエストチェック処理でIllegalArgumentExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_補足情報ファイルダウンロードリクエストチェック処理で異常終了_予期せぬエラー() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeAri_FileNameNotContJpanese();

    doThrow(new IllegalArgumentException("上記以外の例外が発生"))
        .when(validator)
        .validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(validator, times(1)).validateForDownloadPayloadFile(any());
    verify(service, times(0)).downloadPayloadFile(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: ペイロードダウンロードサービス実行処理でデータが見つからなかった際の挙動を確認する<br>
   * 条件: サービス処理でNotFoundExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_ファイルダウンロードサービス実行処理で異常終了_データなし() throws Exception {
    String payloadId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doThrow(new NotFoundException("NotFoundExceptionが発生"))
        .when(service)
        .downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("NotFoundExceptionが発生"))
            .andReturn();

    verify(validator, times(1)).validateForDownloadPayloadFile(any());
    verify(service, times(1)).downloadPayloadFile(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: ペイロードダウンロードサービス実行処理で予期せぬエラーが発生した際の挙動を確認する<br>
   * 条件: サービス処理でIllegalArgumentExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン: 異常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_ファイルダウンロードサービス実行処理で異常終了_予期せぬエラー() throws Exception {
    String payloadId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doThrow(new IllegalArgumentException("上記以外の例外が発生"))
        .when(service)
        .downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(
                get(basePath + "/payload/" + payloadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .param("operatorId", "ope01"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("上記以外の例外が発生"))
            .andReturn();

    verify(validator, times(1)).validateForDownloadPayloadFile(any());
    verify(service, times(1)).downloadPayloadFile(any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: ユーザ情報のマッピングが正しく行われることを確認する<br>
   * 条件: 正常なリクエストを送信し、ユーザ情報が正しくマッピングされる<br>
   * 結果: ValidatorとServiceに伝わるユーザ情報が期待値と一致する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void AirCon_downloadPayloadFile_正常リクエスト_ユーザ情報確認() throws Exception {
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";

    PayloadInfoEntity paylodEnt = createPayloadInfoEntity_MimeAri_FileNameNotContJpanese();

    doNothing().when(validator).validateForDownloadPayloadFile(any());
    doReturn(paylodEnt).when(service).downloadPayloadFile(any(), any());

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/payload/" + payloadId))
            .andExpect(status().isOk())
            .andExpect(
                header()
                    .string(
                        "Content-Disposition", "attachment; filename*=UTF-8''payload_hosoku.txt"))
            .andExpect(header().string("Content-Type", "text/plain"))
            .andExpect(header().string("Content-Length", "27"))
            .andReturn();

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      verify(validator, times(1)).validateForDownloadPayloadFile(argCaptor1.capture());
      assertEquals(payloadId, argCaptor1.getValue());
    }

    {
      ArgumentCaptor<String> argCaptor1 = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).downloadPayloadFile(argCaptor1.capture(), userCaptor.capture());
      assertEquals(payloadId, argCaptor1.getValue());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
    }

    assertEquals(
        new String(paylodEnt.getFileData()),
        new String(response.getResponse().getContentAsByteArray()));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * 機体情報一覧用リクエストDTO作成
   *
   * @return
   */
  private AircraftInfoSearchListRequestDto createAircraftInfoSearchListRequestDto() {
    AircraftInfoSearchListRequestDto ret = new AircraftInfoSearchListRequestDto();
    ret.setAircraftName("ダミー機体名");
    ret.setManufacturer("製造メーカー");
    ret.setModelNumber("型式番号");
    ret.setModelName("機種名");
    ret.setManufacturingNumber("製造番号");
    ret.setAircraftType("1");
    ret.setMinLat(Double.valueOf(40));
    ret.setMinLon(Double.valueOf(50));
    ret.setMaxLat(Double.valueOf(50));
    ret.setMaxLon(Double.valueOf(60));
    ret.setCertification(String.valueOf(true));
    ret.setManufacturer("Test Manufacturer");
    ret.setDipsRegistrationCode("DIPS123");
    ret.setManufacturingNumber("123456");
    ret.setOwnerId("Owner123");
    ret.setOwnerType("1");
    ret.setPerPage("1");
    ret.setPage("1");
    ret.setSortOrders("1");
    ret.setSortColumns("aircraftId");

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料1つ) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_hosoku1() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);

    List<AircraftInfoFileInfoListElementReq> fileInfos =
        new ArrayList<AircraftInfoFileInfoListElementReq>();
    AircraftInfoFileInfoListElementReq file1 = new AircraftInfoFileInfoListElementReq();
    file1.setProcessingType(1);
    file1.setFileId(null);
    file1.setFileLogicalName("補足資料論理名補足資料論理名");
    file1.setFilePhysicalName("hosoku.txt");
    file1.setFileData("data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65");
    byte[] file1Byetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    file1.setFileBinary(file1Byetes);
    fileInfos.add(file1);
    ret.setFileInfos(fileInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料3つ) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_hosoku3() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);

    List<AircraftInfoFileInfoListElementReq> fileInfos =
        new ArrayList<AircraftInfoFileInfoListElementReq>();

    AircraftInfoFileInfoListElementReq file1 = new AircraftInfoFileInfoListElementReq();
    file1.setProcessingType(1);
    file1.setFileId(null);
    file1.setFileLogicalName("1補足資料論理名補足資料論理名");
    file1.setFilePhysicalName("1hosoku.txt");
    file1.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file1Byetes = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    file1.setFileBinary(file1Byetes);
    fileInfos.add(file1);

    AircraftInfoFileInfoListElementReq file2 = new AircraftInfoFileInfoListElementReq();
    file2.setProcessingType(1);
    file2.setFileId(null);
    file2.setFileLogicalName("2補足資料論理名補足資料論理名");
    file2.setFilePhysicalName("2hosoku.txt");
    file2.setFileData("data:text/plain;base64,MuijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file2Byetes = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    file2.setFileBinary(file2Byetes);
    fileInfos.add(file2);

    AircraftInfoFileInfoListElementReq file3 = new AircraftInfoFileInfoListElementReq();
    file3.setProcessingType(1);
    file3.setFileId(null);
    file3.setFileLogicalName("3補足資料論理名補足資料論理名");
    file3.setFilePhysicalName("3hosoku.txt");
    file3.setFileData("data:text/plain;base64,M+ijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file3Byetes = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    file3.setFileBinary(file3Byetes);
    fileInfos.add(file3);

    ret.setFileInfos(fileInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料空配列) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_empList() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);
    List<AircraftInfoFileInfoListElementReq> fileInfos =
        new ArrayList<AircraftInfoFileInfoListElementReq>();
    ret.setFileInfos(fileInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料項目なし) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_hosokuNull() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);
    ret.setFileInfos(null);

    return ret;
  }

  /** データテンプレート ■補足資料情報Entity(FileInfoEntity) 補足資料情報Entity1(MIME設定あり、ファイル名に日本語なし) */
  private FileInfoEntity createFileInfoEntity_MimeAri_FileNameNotContJpanese() {
    FileInfoEntity ret = new FileInfoEntity();
    ret.setFileId(UUID.fromString("7ed6002d-a68f-4e2d-a530-3cd281b5093e"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setFileNumber(1);
    ret.setFileLogicalName("補足資料論理名補足資料論理名");
    ret.setFilePhysicalName("hosoku.txt");
    byte[] fileByetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat("text/plain");
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);

    return ret;
  }

  /** データテンプレート ■補足資料情報Entity(FileInfoEntity) 補足資料情報Entity2(MIME設定あり、ファイル名に日本語あり) */
  private FileInfoEntity createFileInfoEntity_MimeAri_FileNameContJpanese() {
    FileInfoEntity ret = new FileInfoEntity();
    ret.setFileId(UUID.fromString("7ed6002d-a68f-4e2d-a530-3cd281b5093e"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setFileNumber(1);
    ret.setFileLogicalName("補足資料論理名補足資料論理名");
    ret.setFilePhysicalName("補足.txt");
    byte[] fileByetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat("text/plain");
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);

    return ret;
  }

  /** データテンプレート ■補足資料情報Entity(FileInfoEntity) 補足資料情報Entity3(MIME設定なし、ファイル名に日本語なし) */
  private FileInfoEntity createFileInfoEntity_MimeNasi_FileNameNotContJpanese() {
    FileInfoEntity ret = new FileInfoEntity();
    ret.setFileId(UUID.fromString("7ed6002d-a68f-4e2d-a530-3cd281b5093e"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setFileNumber(1);
    ret.setFileLogicalName("補足資料論理名補足資料論理名");
    ret.setFilePhysicalName("hosoku.txt");
    byte[] fileByetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat(null);
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);

    return ret;
  }

  /** データテンプレート ■補足資料情報Entity(FileInfoEntity) 補足資料情報Entity4(MIME設定なし、ファイル名に日本語あり) */
  private FileInfoEntity createFileInfoEntity_MimeNasi_FileNameContJpanese() {
    FileInfoEntity ret = new FileInfoEntity();
    ret.setFileId(UUID.fromString("7ed6002d-a68f-4e2d-a530-3cd281b5093e"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setFileNumber(1);
    ret.setFileLogicalName("補足資料論理名補足資料論理名");
    ret.setFilePhysicalName("補足.txt");
    byte[] fileByetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat(null);
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);

    return ret;
  }

  /** データテンプレート ■ペイロード情報Entity(PayloadInfoEntity) ペイロード情報Entity1(MIME設定あり、ファイル名に日本語なし) */
  private PayloadInfoEntity createPayloadInfoEntity_MimeAri_FileNameNotContJpanese() {
    PayloadInfoEntity ret = new PayloadInfoEntity();
    ret.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setPayloadNumber(1);
    ret.setPayloadName("テストペイロード1");
    ret.setPayloadDetailText("テストのペイロード情報を記載1");
    ret.setFilePhysicalName("payload_hosoku.txt");
    byte[] fileByetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat("text/plain");
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);

    return ret;
  }

  /** データテンプレート ■ペイロード情報Entity(PayloadInfoEntity) ペイロード情報Entity2(MIME設定あり、ファイル名に日本語あり) */
  private PayloadInfoEntity createPayloadInfoEntity_MimeAri_FileNameContJpanese() {
    PayloadInfoEntity ret = new PayloadInfoEntity();
    ret.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b02"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setPayloadNumber(1);
    ret.setPayloadName("テストペイロード2");
    ret.setPayloadDetailText("テストのペイロード情報を記載2");
    ret.setFilePhysicalName("ペイロード.txt");
    byte[] fileByetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat("text/plain");
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);

    return ret;
  }

  /** データテンプレート ■ペイロード情報Entity(PayloadInfoEntity) ペイロード情報Entity3(MIME設定なし、ファイル名に日本語なし) */
  private PayloadInfoEntity createPayloadInfoEntity_MimeNasi_FileNameNotContJpanese() {
    PayloadInfoEntity ret = new PayloadInfoEntity();
    ret.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b03"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setPayloadNumber(1);
    ret.setPayloadName("テストペイロード3");
    ret.setPayloadDetailText("テストのペイロード情報を記載3");
    ret.setFilePhysicalName("payload_hosoku.txt");
    byte[] fileByetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat(null);
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);

    return ret;
  }

  /** データテンプレート ■ペイロード情報Entity(PayloadInfoEntity) ペイロード情報Entity4(MIME設定なし、ファイル名に日本語あり) */
  private PayloadInfoEntity createPayloadInfoEntity_MimeNasi_FileNameContJpanese() {
    PayloadInfoEntity ret = new PayloadInfoEntity();
    ret.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b04"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setPayloadNumber(1);
    ret.setPayloadName("テストペイロード4");
    ret.setPayloadDetailText("テストのペイロード情報を記載4");
    ret.setFilePhysicalName("ペイロード.txt");
    byte[] fileByetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat(null);
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(ペイロード情報1つ) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_payload1() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos =
        new ArrayList<AircraftInfoPayloadInfoListElementReq>();
    AircraftInfoPayloadInfoListElementReq payload1 = new AircraftInfoPayloadInfoListElementReq();
    payload1.setProcessingType(1);
    payload1.setPayloadId(null);
    payload1.setPayloadName("テストペイロード");
    payload1.setPayloadDetailText("テストのペイロード情報を記載");
    payload1.setImageData(
        "data:image/png;base64,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");
    byte[] payload1Bytes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    payload1.setImageBinary(payload1Bytes);
    payload1.setFilePhysicalName("payload_hosoku.txt");
    payload1.setFileData("data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65");
    byte[] file1Byetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    payload1.setFileBinary(file1Byetes);
    payloadInfos.add(payload1);
    ret.setPayloadInfos(payloadInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(ペイロード情報3つ) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_payload3() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos =
        new ArrayList<AircraftInfoPayloadInfoListElementReq>();
    AircraftInfoPayloadInfoListElementReq payload1 = new AircraftInfoPayloadInfoListElementReq();
    payload1.setProcessingType(1);
    payload1.setPayloadId(null);
    payload1.setPayloadName("テストペイロード1");
    payload1.setPayloadDetailText("テストのペイロード情報を記載1");
    payload1.setImageData(
        "data:image/png;base64,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");
    byte[] payload1Bytes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    payload1.setImageBinary(payload1Bytes);
    payload1.setFilePhysicalName("payload_hosoku1.txt");
    payload1.setFileData("data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65");
    byte[] file1Byetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    payload1.setFileBinary(file1Byetes);
    payloadInfos.add(payload1);

    AircraftInfoPayloadInfoListElementReq payload2 = new AircraftInfoPayloadInfoListElementReq();
    payload2.setProcessingType(1);
    payload2.setPayloadId(null);
    payload2.setPayloadName("テストペイロード2");
    payload2.setPayloadDetailText("テストのペイロード情報を記載2");
    payload2.setImageData(
        "data:image/png;base64,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");
    byte[] payload2Bytes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    payload2.setImageBinary(payload2Bytes);
    payload2.setFilePhysicalName("payload_hosoku2.txt");
    payload2.setFileData("data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65");
    byte[] file2Byetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    payload2.setFileBinary(file2Byetes);
    payloadInfos.add(payload2);

    AircraftInfoPayloadInfoListElementReq payload3 = new AircraftInfoPayloadInfoListElementReq();
    payload3.setProcessingType(1);
    payload3.setPayloadId(null);
    payload3.setPayloadName("テストペイロード3");
    payload3.setPayloadDetailText("テストのペイロード情報を記載3");
    payload3.setImageData(
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPUAAABuCAYAAAD/EFXlAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAcdSURBVHhe7d0hVOw6FwXgkUgkEolEIpFIJHIkEolDIpFIJBKJRCKRSCQSiexd+76b+84c0rntNE2Tnf2tlbX+978n5nSyJ2mSllUnIlRW/v8Qkbop1CJkFGoRMgq1CBmFWoSMQi1CRqEWIaNQi5BRqEXIKNQiZBRqETIKtQgZhVqEjEItQkahFiGjUIuQUailWc/Pz93p6Wn38PDg/1XVFGpp1sHBQbdarbq9vT3/r6qmUEuzEOjQmHBVIzKCQi1CRqEWIaNQi5BpPtRY/j88PNy4ELU0fG62bQuZzvYRJoOrqTXQobFtW8h0tn8wGVzN7e3tj6DU1C4vL31J0jjbP5hwVSMygkItQkahFiGjUIuQUahlJzVsBeLz3d/f+49Oz14DJlzVFKj0QNt2c3PjPz41WzsTrmoKVONWYCuHdWzNTLiqkdG+v7+7s7OzH8Fu4bCOQi20EOz1ev0j2OxYa+WqRiZj7egxrLVyVSOTsXb0GNZauaqRyVg7egxrrVzVyGSsHT2GtVauamQS7FOzdvQY1lqTVVPKySl8houLi98npN7f3/3HlB4+0LiG7Gy9TJJVU0KgpzbUcH19/XuLpyU+0Ni3buEa2JqZJKumxpNTfe3o6Kh7fX31JVJqNdBg62bCVU3XdW9vb93d3V13fn7e7e/v/wjs2MZ8ZLLlQIOtnQlXNRMhvLEfAsYjk60HGhTqRnx+fv44C832fjPMZFoPNCjUQuHp6UmB/kOhrgDrnyZNBesNuJUIHRnXqtVAg0JdAdY/TZrCx8fHxrYj/vfX15f/z5qiUFeA9UuaCqPx8fHx32uDxUAdzOHtLzTVoOOyfklTYXsvXBfMYl5eXvx/0iTW/kJTjV3Rxagk/7m6utrovC2+YNAKx5n9CUgmFNVglA730+q4//NbVwh463yYQwvPDGCRFduaNaMI9ePj498vB+FueUU38CM0puAy/DgzZnt4DqDG48IUobaLQBidWoYfNHsPHTqofug2+f36bQ0LizX1q+pDbb8cLAK13HkxbbQ/cGg4XNL61lUfe50wImNk9tfPtlrei159qHGAIlz0lu8ZcbDE3y+yHW9NzV4rCz+OuLfGPbZ/FqCGYFcdanTkcLExSte+wLErrOj6zlfTdHEpfaG2MPPzzwKUHuz+aipg7x1bHZUwotgOhx833JLIvw0JNdQW7O3VFAwnouxFxqjdGh9orPy3eB12NTTUUFOw/11NoeyWTYvbNT7QWODB+W4ZbkyoIRZszBBLu+0bVk1hcBHt00atjU6xQGuFe7yxoYZYsNEXMciUEu7h1RTEjtKtHQlVoNPZJdQQOwsQwl3CiyvHVVMIu3XT0qKQAp3WrqEO0Pdi+9pLv7hyt2oWhClOuHjYxmmFP96oQE83NdRBX7iXmpJPq2YB9gQZDp6ww2OS+OUfE+gxf1ih1Xedg70OKfS9uDJc51wPGqWpJiP7FkzmE2T4hceJJt85Tk5OtgYahgbatqWnjEuw9acSe3GlbTm2wdJVk4ldoMDTWYxwGsz/4uOfMQUfwk/Vx7SlpoxLsHWnhhmWPcJs29DvcVfpq5mR38pifCWPfx83GkbsuYIWmzKWtkUzF1vznPw22Nzv0Ju3msTYt7Jwb2s7GqbEOV491DdlZA93rlCDf93WnH/5Zf5qEvGjNNNWFmYcuFe2X/oS7+PumzKyhjtnqMH233Bd55CnmgRYR2ncX/kve4lAW9vCPff9YE65Q+3XOuZ6CClPNROhk7GN0rHRubTQ9O2/5ljBzSF3qHMpvho8pGBfKogg1AwjcGx0Rl2lLvzFwo2Z05KziRQU6gWg09jRDOGu+b4udpCktNG5j1/BRat9b1uhXsB6vd7o/LV2oG0HSUodnWP6HmSo9USaQp2Zf2d1riN2KeGRUExT/T7wmIMkJYrtbeMHqrbnuRXqjPzTSHOtEs4hBLnvqOacB0lyiu1tI+g1LWIq1JnYF/OjLb29MxRG3r4go+U6SJKbn1Gh4baphtsKhToTu9JdQ6BjW1OhoRbMMhjDbGGtw35voeG6YNZV6neoUM8s9rhgqZ0B947orBiR/NZUK0H28ORY7MCKbXMejdyFQj0zH+iSLrQNcexzotWyNTU3jNqxHzt7nUpRYl9LoZhqch2hGyI2a9jWatuaygEjN3Ys/L78kt+rZz8XE65qEondH9qG0Qb3+/ghqnXvXBTqpijEbVCoG2LvB4WXQt2QcH9f0v2fpKdQi5BRqEXIKNQiZOzaCdNhIYVammUfh8W5hFJPMI6lUEuz8KSZfYSUZWFUoZam2cd8SzrCOoVCLc1jWzDjqEJkAoVahIxCLULGbm0xvNNcoZbm2b/+glb7c/EKtTTPv9O89lVwhVrkT7DDYRS8uaVmCrUIGYVahIxCLUJGoRYho1CLkFGoRcgo1CJkFGoRMgq1CJlfMf/5w1FFkZsAAAAASUVORK5CYII=");
    byte[] payload3Bytes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    payload3.setImageBinary(payload3Bytes);
    payload3.setFilePhysicalName("payload_hosoku3.txt");
    payload3.setFileData("data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65");
    byte[] file3Byetes = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    payload3.setFileBinary(file3Byetes);
    payloadInfos.add(payload3);

    ret.setPayloadInfos(payloadInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(ペイロード情報空配列) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_payloadEmpList() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);
    List<AircraftInfoPayloadInfoListElementReq> payloadInfos =
        new ArrayList<AircraftInfoPayloadInfoListElementReq>();
    ret.setPayloadInfos(payloadInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(ペイロード情報項目なし) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_payloadNull() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setManufacturingNumber("N12345678");
    ret.setAircraftType(1);
    ret.setMaxTakeoffWeight(Double.valueOf(99));
    ret.setBodyWeight(Double.valueOf(88));
    ret.setMaxFlightSpeed(Double.valueOf(77));
    ret.setMaxFlightTime(Double.valueOf(66));
    ret.setLat(Double.valueOf(55));
    ret.setLon(Double.valueOf(44));
    ret.setCertification(true);
    ret.setDipsRegistrationCode("DIPS_1234");
    ret.setOwnerType(1);
    ret.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    ret.setImageData(
        "data:image/png;base64,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");
    ret.setPublicFlag(true);
    byte[] imageByetes = {
      -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110, 8,
      6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4,
      103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61,
      0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19, -35, 33, 84,
      -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36, 18, -119, 67, 34,
      -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5, -66, -101, -5, -50, 28,
      -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65, 39, -26, 116, -78, 39, 105,
      -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41, -44, 34, 100, 20, 106, 17, 50, 10,
      -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
      69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66,
      70, -95, 22, 33, -93, 80, -117, -112, 81, -88, -91, 89, -49, -49, -49, -35, -23, -23, 105, -9,
      -16, -16, -32, -1, 85, -43, 20, 106, 105, -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61,
      -1, -81, -86, -90, 80, 75, -77, 16, -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70,
      -95, 22, 33, -93, 80, -117, -112, 105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52,
      124, 110, -74, 109, 11, -103, -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91,
      -56, 116, -74, 127, 48, 25, 92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73,
      -46, 56, -37, 63, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117,
      -112, 81, -88, 101, 39, 53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92,
      -43, 20, -88, -12, 64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29,
      86, 96, 43, -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77,
      31, -63, 110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27,
      -86, 70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
      -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86, -82,
      106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124, -122,
      -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72, -122, -20,
      108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123, -117, -89, 37, 62,
      -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109, 83, 125, -19, -24, -24,
      -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62, 85, 77, -41, 117, 111, 111,
      111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1, 8, -20, -40, -58, 124, 100,
      -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31, 2, -58, 35, -109, -83, 7, 26,
      20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51, -52, 100, 90, 15, 52, 40, -44, 66,
      -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97, 38, 77, 5, -21, 13, -72, -107, 8, 29,
      25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77, -102, -62, -57, -57, -57, -58, -74, 35,
      -2, -9, -41, -41, -105, -1, -49, -102, -94, 80, 87, -128, -11, 75, -102, 10, -93, -15, -15,
      -15, -15, -33, 107, -125, -59, 64, 29, -52, -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83,
      97, 123, 47, 92, 23, -52, 98, 94, 94, 94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47,
      -59, -88, 36, -1, -71, -70, -70, -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72,
      38, 20, -43, 96, -108, 14, -9, -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11,
      -49, 12, 96, -111, 21, -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121,
      69, 55, -16, 35, 52, -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95,
      -74, -117, 64, 24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109,
      13, 11, -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63,
      112, 104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90,
      -34, -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
      29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96, 87,
      29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87, -45, 116,
      113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96, -17, 29, 91, 29,
      -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53, -44, 22, -20, -19, -43,
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4, -73, 120, 29, 118, 53, 52, -44,
      80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37, 53, 62, -48, 88, -32, -63, -7, 110,
      25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19, 27, 86, 77, 97, 112, 17, -19, -45, 70,
      -83, -115, 78, -79, 64, 107, -123, 123, -68, -79, -95, -122, 88, -80, -47, 23, 49, -56, -108,
      18, -18, -31, -43, 20, -60, -114, -46, -83, 29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16,
      -62, 93, -62, -117, 43, -57, 85, 83, 8, -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95,
      14, -48, -9, 98, -5, -38, 75, -65, -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58,
      105, -123, 63, -34, -88, 64, 79, 55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1,
      -10, 4, 25, 14, -98, -80, -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119,
      -99, -125, -67, 14, 41, -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76,
      -26, 19, 100, -8, -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74,
      -91, -89, -116, 75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119,
      93, -96, -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57,
      -76, -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
      -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126, 22,
      -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73, -102,
      -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100, 15, 119,
      -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45, 86, 22, 102,
      28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103, -88, -63, -10, -33,
      112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123, -119, 64, 91, -37, -62,
      61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79, 53, 19, -95, -109, -79, -115,
      -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63, -51, 33, 119, -88, 115, 41, -66,
      26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108, 116, 70, 93, -91, 46, -4, -59, -62,
      -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96, -45, -40, -47, 12, -31, -82, -7, -66,
      46, 118, -112, -92, -76, -47, -71, -113, 95, -63, 69, -85, 125, 111, 91, -95, 94, -64, 122,
      -67, -34, -24, -4, -75, 118, -96, 109, 7, 73, 74, 29, -99, 99, -6, 30, 100, -88, -11, 68,
      -102, 66, -99, -103, 127, 103, 117, -82, 35, 118, 41, -31, -111, 80, 76, 83, -3, 62, -16,
      -104, -125, 36, 37, -118, -19, 109, -29, 7, -86, -74, -25, -71, 21, -22, -116, -4, -45, 72,
      115, -83, 18, -50, 33, 4, -71, -17, -88, -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53,
      45, 98, 42, -44, -103, -40, 23, -13, -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27,
      58, 72, -110, -101, -97, 81, -95, -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93,
      67, -96, 99, 91, 83, -95, -95, 22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31,
      -70, 96, -42, 85, -22, 119, -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84,
      24, -111, -4, -42, 84, 43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36,
      -123, 66, 61, 51, 31, -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116,
      -38, -79, 31, 59, 123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107,
      -40, -42, 106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23,
      19, -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
      -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95, -46,
      -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9, -45, 97,
      33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75, -77, -16,
      -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58, -62, 58, -123,
      66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66, 45, 66, -58, 110,
      109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115, -15, 10, -75, 52, -49,
      -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20, -68, -71, -91, 102, 10, -75,
      8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 69,
      -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, -103, 95, 49, -1, -7, -61, 81, 69, -111,
      -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126
    };
    ret.setImageBinary(imageByetes);
    ret.setPayloadInfos(null);

    return ret;
  }

  /**
   * メソッド名: post<br>
   * 試験名: 機体情報登録処理が正しく行われることを確認する<br>
   * 条件: 正常な AircraftInfoRequestDto を渡す<br>
   * 結果: 処理結果(機体ID)が正しく返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void post_正常1() throws Exception {
    AircraftInfoRequestDto requestDto = createAircraftInfoRequestDto_hosokuNull();
    AircraftInfoResponseDto responseDto = new AircraftInfoResponseDto();
    responseDto.setAircraftId("responseAircraftId");

    doNothing().when(service).decodeBinary(any());
    doReturn(responseDto).when(service).postData(any(), any());
    doNothing().when(validator).validateForRegist(any());

    String requestJson = objectMapper.writeValueAsString(requestDto);
    MvcResult response =
        mockMvc
            .perform(
                post(basePath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.aircraftId").value("responseAircraftId"))
            .andReturn();

    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).postData(dtoCaptor.capture(), userCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
    }
    {
      ArgumentCaptor<AircraftInfoRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoRequestDto.class);
      verify(validator, times(1)).validateForRegist(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /** データテンプレート ■モデル検索リクエスト モデル検索リクエストボディ */
  private static AircraftInfoModelSearchRequestDto createAircraftInfoModelSearchRequestDto() {
    AircraftInfoModelSearchRequestDto dto = new AircraftInfoModelSearchRequestDto();

    List<AircraftInfoModelInfoListElementReq> list = new ArrayList<>();
    AircraftInfoModelInfoListElementReq ele1 = new AircraftInfoModelInfoListElementReq();
    ele1.setManufacturer("製造メーカー1");
    ele1.setModelNumber("MD12345V1");
    list.add(ele1);
    AircraftInfoModelInfoListElementReq ele2 = new AircraftInfoModelInfoListElementReq();
    ele2.setManufacturer("製造メーカー1");
    ele2.setModelNumber("MD12345V2");
    list.add(ele2);
    AircraftInfoModelInfoListElementReq ele3 = new AircraftInfoModelInfoListElementReq();
    ele3.setManufacturer("製造メーカー2");
    ele3.setModelNumber("MD12345V1");
    list.add(ele3);

    dto.setModelInfos(list);
    dto.setIsRequiredPayloadInfo("true");
    dto.setIsRequiredPriceInfo("true");
    return dto;
  }

  /** データテンプレート ■モデル検索レスポンス モデル検索レスポンスボディ */
  private static AircraftInfoSearchListResponseDto
      createAircraftInfoSearchListResponseModelSearchDto() {
    AircraftInfoSearchListResponseDto dto = new AircraftInfoSearchListResponseDto();

    List<AircraftInfoSearchListElement> dataList = new ArrayList<>();

    // モデル情報リスト1の要素
    AircraftInfoSearchListElement element = new AircraftInfoSearchListElement();
    element.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf11");
    element.setAircraftName("機体名");
    element.setManufacturer("製造メーカー");
    element.setModelNumber("MD12345V1");
    element.setModelName("機種名");
    element.setManufacturingNumber("N12345678");
    element.setAircraftType(1);
    element.setMaxTakeoffWeight(99.0);
    element.setBodyWeight(88.0);
    element.setMaxFlightSpeed(77.0);
    element.setMaxFlightTime(66.0);
    element.setLat(55.0);
    element.setLon(44.0);
    element.setCertification(true);
    element.setDipsRegistrationCode("DIPS_1234");
    element.setOwnerType(1);
    element.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    element.setPublicFlag(true);
    element.setOperatorId("ope01");

    // ペイロード情報リスト
    List<AircraftInfoPayloadInfoSearchListElement> payloadInfos = new ArrayList<>();
    AircraftInfoPayloadInfoSearchListElement payloadElement =
        new AircraftInfoPayloadInfoSearchListElement();
    payloadElement.setPayloadId("0a0711a5-ff74-4164-9309-8888b433cf21");
    payloadElement.setPayloadName("ペイロード");
    payloadElement.setPayloadDetailText("テストのペイロード情報を記載");
    payloadElement.setFilePhysicalName("ファイル物理名");
    payloadElement.setOperatorId("ope01");
    payloadInfos.add(payloadElement);
    element.setPayloadInfos(payloadInfos);

    // 料金情報
    List<PriceInfoSearchListDetailElement> priceInfos = new ArrayList<>();
    PriceInfoSearchListDetailElement priceElement = new PriceInfoSearchListDetailElement();
    priceElement.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf31");
    priceElement.setPriceType(2);
    priceElement.setPricePerUnit(1);
    priceElement.setPrice(1000);
    priceElement.setEffectiveStartTime("2026-01-01T10:00:00");
    priceElement.setEffectiveEndTime("2027-01-01T10:00:00");
    priceElement.setPriority(1);
    priceElement.setOperatorId("ope01");
    priceInfos.add(priceElement);
    element.setPriceInfos(priceInfos);

    dataList.add(element);
    dto.setData(dataList);

    return dto;
  }
}
