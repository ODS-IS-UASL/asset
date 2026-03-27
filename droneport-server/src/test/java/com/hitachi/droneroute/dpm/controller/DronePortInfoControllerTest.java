package com.hitachi.droneroute.dpm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.RoleInfoDto;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortEnvironmentInfoResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterResponseDto;
import com.hitachi.droneroute.dpm.service.DronePortInfoService;
import com.hitachi.droneroute.dpm.validator.DronePortInfoValidator;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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

/** DronePortInfoControllerクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@AutoConfigureMockMvc(addFilters = false)
public class DronePortInfoControllerTest {

  @Value("${droneroute.basepath}/droneport/info")
  String basePath;

  @Value("${droneroute.basepath}/droneport")
  String basePath2;

  MockMvc mockMvc;

  @Autowired WebApplicationContext webApplicationContext;

  @MockBean DronePortInfoValidator validator;

  @MockBean DronePortInfoService service;

  @SpyBean private SystemSettings systemSettings;

  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    // テスト用UserInfoDtoをSecurityContextに設定
    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setUserOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    RoleInfoDto roleDto = new RoleInfoDto();
    List<RoleInfoDto> roleList = new ArrayList<>();
    roleDto.setRoleId("11");
    roleDto.setRoleName("テストロール");
    roleList.add(roleDto);
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
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報詳細取得が正しく行われる<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getDetail_正常() throws Exception {
    log.info("basepath:" + basePath);
    String dronePortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(dronePortId);

    DronePortInfoDetailResponseDto responseDto = new DronePortInfoDetailResponseDto();
    responseDto.setDronePortId(dronePortId);
    responseDto.setDronePortName("dummyDronePortName");
    when(service.getDetail(any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + dronePortId))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.dronePortId").value(dronePortId),
                jsonPath("$.dronePortName").value("dummyDronePortName"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
    verify(service, times(1)).getDetail(any(), any(), userCaptor.capture());
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
   * 試験名: 離着陸場情報詳細取得が正しく行われる<br>
   * 条件: 正常な離着陸場IDを渡す、料金情報要否がtrue<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getDetail_正常_料金情報要否true() throws Exception {
    String dronePortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(dronePortId);

    DronePortInfoDetailResponseDto responseDto = new DronePortInfoDetailResponseDto();
    responseDto.setDronePortId(dronePortId);
    responseDto.setDronePortName("dummyDronePortName");
    when(service.getDetail(any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + dronePortId + "?isRequiredPriceInfo=true"))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.dronePortId").value(dronePortId),
                jsonPath("$.dronePortName").value("dummyDronePortName"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    verify(service, times(1)).getDetail(any(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報詳細取得が正しく行われる<br>
   * 条件: 正常な離着陸場IDを渡す、料金情報要否false<br>
   * 結果: 詳細情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getDetail_正常_料金情報要否false() throws Exception {
    String dronePortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(dronePortId);

    DronePortInfoDetailResponseDto responseDto = new DronePortInfoDetailResponseDto();
    responseDto.setDronePortId(dronePortId);
    responseDto.setDronePortName("dummyDronePortName");
    when(service.getDetail(any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + dronePortId + "?isRequiredPriceInfo=false"))
            .andExpect(status().isOk())
            .andExpectAll(
                jsonPath("$.dronePortId").value(dronePortId),
                jsonPath("$.dronePortName").value("dummyDronePortName"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    verify(service, times(1)).getDetail(any(), any(), any());

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
    String dronePortId = "dummyuuid";

    // 入力エラーを意図的に発生させる
    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForGetDetail(dronePortId);

    DronePortInfoDetailResponseDto responseDto = new DronePortInfoDetailResponseDto();
    when(service.getDetail(any(), any(), any())).thenReturn(responseDto);

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + dronePortId))
            .andExpect(status().isBadRequest())
            // .andExpect(jsonPath("$.errorDetail").value("[離着陸場IDがUUIDではありません。\n入力値:" +
            // dronePortId + "]"))
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    verify(service, times(0)).getDetail(anyString(), any(), any());

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
    String dronePortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(dronePortId);
    doThrow(new ServiceErrorException("ServiceErrorMessage"))
        .when(service)
        .getDetail(any(), any(), any());

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + dronePortId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    verify(service, times(1)).getDetail(anyString(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報詳細取得APIが呼び出されない<br>
   * 条件: 離着陸場IDを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void getDetail_離着陸場ID未設定() throws Exception {
    String dronePortId = "";

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/detail/" + dronePortId))
            .andExpect(status().isInternalServerError())
            .andReturn();

    verify(validator, times(0)).validateForGetDetail(anyString());
    verify(service, times(0)).getDetail(anyString(), any(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  private DronePortInfoListRequestDto createDronePortInfoListRequestDto() {
    DronePortInfoListRequestDto ret = new DronePortInfoListRequestDto();
    ret.setDronePortName("ダミー離着陸場名");
    ret.setAddress("dummy address");
    ret.setManufacturer("製造メーカー");
    ret.setSerialNumber("シリアルナンバー");
    ret.setPortType("2");
    ret.setMinLat(1.2d);
    ret.setMinLon(2.3d);
    ret.setMaxLat(3.4d);
    ret.setMaxLon(4.5d);
    ret.setSupportDroneType("対応機体");
    ret.setActiveStatus("動作状況");
    ret.setPerPage("50");
    ret.setPage("1");
    ret.setSortOrders("1,0");
    ret.setSortColumns("dronePortName,dronePortId");
    ret.setPublicFlag("true");
    ret.setIsRequiredPriceInfo("false");

    return ret;
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報一覧取得が正しく行われる<br>
   * 条件: 正常な DronePortInfoListRequestDto を渡す<br>
   * 　　　　　クエリパラメータにソート関連項目の設定なし<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getList_正常() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    DronePortInfoListRequestDto requestDto = createDronePortInfoListRequestDto();
    String queryParam =
        "dronePortType="
            + "&dronePortName="
            + requestDto.getDronePortName()
            + "&minLat="
            + BigDecimal.valueOf(requestDto.getMinLat()).toPlainString()
            + "&minLon="
            + BigDecimal.valueOf(requestDto.getMinLon()).toPlainString()
            + "&maxLat="
            + BigDecimal.valueOf(requestDto.getMaxLat()).toPlainString()
            + "&maxLon="
            + BigDecimal.valueOf(requestDto.getMaxLon()).toPlainString()
            + "&address="
            + requestDto.getAddress()
            + "&manufacturer="
            + requestDto.getManufacturer()
            + "&serialNumber="
            + requestDto.getSerialNumber()
            + "&portType="
            + requestDto.getPortType()
            + "&supportDroneType="
            + requestDto.getSupportDroneType()
            + "&activeStatus="
            + requestDto.getActiveStatus()
            + "&perPage="
            + requestDto.getPerPage()
            + "&page="
            + requestDto.getPage()
            + "&perPage="
            + requestDto.getPerPage()
            + "&page="
            + requestDto.getPage()
            + "&publicFlag="
            + requestDto.getPublicFlag()
            + "&isRequiredPriceInfo="
            + requestDto.getIsRequiredPriceInfo();

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
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
   * 試験名: 離着陸場情報一覧取得が正しく行われる<br>
   * 条件: 正常な DronePortInfoListRequestDto を渡す<br>
   * 　　　　　クエリパラメータにページ、ソート関連項目の設定あり<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getList_正常2() throws Exception {

    doNothing().when(validator).validateForGetList(any());

    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    DronePortInfoListRequestDto requestDto = createDronePortInfoListRequestDto();
    requestDto.setPage("2");
    requestDto.setPerPage("12");
    requestDto.setSortOrders("0,0,1");
    requestDto.setSortColumns("a,b,c");
    String queryParam =
        "dronePortType="
            + "&dronePortName="
            + requestDto.getDronePortName()
            + "&minLat="
            + BigDecimal.valueOf(requestDto.getMinLat()).toPlainString()
            + "&minLon="
            + BigDecimal.valueOf(requestDto.getMinLon()).toPlainString()
            + "&maxLat="
            + BigDecimal.valueOf(requestDto.getMaxLat()).toPlainString()
            + "&maxLon="
            + BigDecimal.valueOf(requestDto.getMaxLon()).toPlainString()
            + "&address="
            + requestDto.getAddress()
            + "&manufacturer="
            + requestDto.getManufacturer()
            + "&serialNumber="
            + requestDto.getSerialNumber()
            + "&portType="
            + requestDto.getPortType()
            + "&supportDroneType="
            + requestDto.getSupportDroneType()
            + "&activeStatus="
            + requestDto.getActiveStatus()
            + "&perPage="
            + requestDto.getPerPage()
            + "&page="
            + requestDto.getPage()
            + "&sortOrders="
            + requestDto.getSortOrders()
            + "&sortColumns="
            + requestDto.getSortColumns()
            + "&publicFlag="
            + requestDto.getPublicFlag()
            + "&isRequiredPriceInfo="
            + requestDto.getIsRequiredPriceInfo();

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/list?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
      verify(service, times(1)).getList(dtoCaptor.capture(), any());
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

    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    // String queryParam = "drone_port_type=" + "1";
    String queryParam = "dronePortType=" + "1";

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
   * メソッド名: getList<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getList_サービスエラー発生() throws Exception {

    doNothing().when(validator).validateForGetList(any());
    doThrow(new ServiceErrorException("dummyMessage")).when(service).getList(any(), any());

    // String queryParam = "drone_port_type=" + "1";
    String queryParam = "dronePortType=" + "1";

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
   * 試験名: 離着陸場情報一覧取得が正しく行われる<br>
   * 条件: クエリパラメータなし<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void getList_クエリパラメータ未設定() throws Exception {

    doNothing().when(validator).validateForGetList(any());
    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
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
   * 試験名: 離着陸場情報一覧取得が正しく行われる<br>
   * 条件: 正常な DronePortInfoListRequestDto を渡す<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常() throws Exception {

    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setDummyUserFlag(true);

    List<GrantedAuthority> authorities = new ArrayList<>();

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);

    doNothing().when(validator).validateForGetList(any());

    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    DronePortInfoListRequestDto requestDto = createDronePortInfoListRequestDto();
    String queryParam =
        "dronePortType="
            + "&dronePortName="
            + requestDto.getDronePortName()
            + "&minLat="
            + BigDecimal.valueOf(requestDto.getMinLat()).toPlainString()
            + "&minLon="
            + BigDecimal.valueOf(requestDto.getMinLon()).toPlainString()
            + "&maxLat="
            + BigDecimal.valueOf(requestDto.getMaxLat()).toPlainString()
            + "&maxLon="
            + BigDecimal.valueOf(requestDto.getMaxLon()).toPlainString()
            + "&address="
            + requestDto.getAddress()
            + "&manufacturer="
            + requestDto.getManufacturer()
            + "&serialNumber="
            + requestDto.getSerialNumber()
            + "&portType="
            + requestDto.getPortType()
            + "&supportDroneType="
            + requestDto.getSupportDroneType()
            + "&activeStatus="
            + requestDto.getActiveStatus()
            + "&isRequiredPriceInfo="
            + requestDto.getIsRequiredPriceInfo();

    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setPage(null);
    requestDto.setPerPage(null);
    requestDto.setPublicFlag("true");

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
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
   * 試験名: 離着陸場情報一覧取得が正しく行われる<br>
   * 条件: クエリパラメータ未設定で実行<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常_クエリ未設定() throws Exception {

    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setDummyUserFlag(true);

    List<GrantedAuthority> authorities = new ArrayList<>();

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);

    doNothing().when(validator).validateForGetList(any());

    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    DronePortInfoListRequestDto requestDto = new DronePortInfoListRequestDto();

    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setPage(null);
    requestDto.setPerPage(null);
    requestDto.setPublicFlag("true");

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract"))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
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
   * 試験名: 離着陸場情報一覧取得が正しく行われる<br>
   * 条件: 定義外項目の存在する DronePortInfoListRequestDto を渡す<br>
   * 結果: 一覧情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getPublicDataExtract_正常_定義外項目あり() throws Exception {

    UserInfoDto testUserInfo = new UserInfoDto();
    testUserInfo.setDummyUserFlag(true);

    List<GrantedAuthority> authorities = new ArrayList<>();

    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(testUserInfo, null, authorities);
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);

    doNothing().when(validator).validateForGetList(any());

    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

    DronePortInfoListRequestDto requestDto = createDronePortInfoListRequestDto();
    String queryParam =
        "dronePortType="
            + "&dronePortName="
            + requestDto.getDronePortName()
            + "&minLat="
            + BigDecimal.valueOf(requestDto.getMinLat()).toPlainString()
            + "&minLon="
            + BigDecimal.valueOf(requestDto.getMinLon()).toPlainString()
            + "&maxLat="
            + BigDecimal.valueOf(requestDto.getMaxLat()).toPlainString()
            + "&maxLon="
            + BigDecimal.valueOf(requestDto.getMaxLon()).toPlainString()
            + "&address="
            + requestDto.getAddress()
            + "&manufacturer="
            + requestDto.getManufacturer()
            + "&serialNumber="
            + requestDto.getSerialNumber()
            + "&portType="
            + requestDto.getPortType()
            + "&supportDroneType="
            + requestDto.getSupportDroneType()
            + "&activeStatus="
            + requestDto.getActiveStatus()
            + "&isRequiredPriceInfo="
            + requestDto.getIsRequiredPriceInfo()
            + "&teigigai="
            + "teigigai";

    requestDto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    requestDto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    requestDto.setPage(null);
    requestDto.setPerPage(null);
    requestDto.setPublicFlag("true");

    MvcResult response =
        mockMvc
            .perform(get(basePath + "/publicDataExtract?" + queryParam))
            .andExpect(status().isOk())
            .andExpectAll(jsonPath("$.data").isEmpty())
            .andReturn();

    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
      verify(validator, times(1)).validateForGetList(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortInfoListRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoListRequestDto.class);
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
   * テストパターン：異常系<br>
   */
  @Test
  public void getPublicDataExtract_チェックエラー() throws Exception {

    doThrow(new ValidationErrorException("ValidationErrorExceptionが発生"))
        .when(validator)
        .validateForGetList(any());
    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    when(service.getList(any(), any())).thenReturn(responseDto);

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
   * テストパターン：異常系<br>
   */
  @Test
  public void getPublicDataExtract_サービスエラー() throws Exception {

    doNothing().when(validator).validateForGetList(any());
    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    doThrow(new ServiceErrorException("ServiceErrorExceptionが発生"))
        .when(service)
        .getList(any(), any());

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
   * テストパターン：異常系<br>
   */
  @Test
  public void getPublicDataExtract_予期せぬエラー() throws Exception {

    doNothing().when(validator).validateForGetList(any());
    DronePortInfoListResponseDto responseDto = new DronePortInfoListResponseDto();
    responseDto.setData(new ArrayList<>());
    doThrow(new IllegalArgumentException("予期せぬ例外が発生")).when(service).getList(any(), any());

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
   * メソッド名: delete<br>
   * 試験名: 離着陸場情報削除が正しく行われる<br>
   * 条件: 任意離着陸場IDをURLに設定。入力チェックが正常。削除処理が正常。<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void delete_正常() throws Exception {
    String dronePortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(dronePortId);
    doNothing().when(validator).validateForDelete(any(DronePortInfoDeleteRequestDto.class));
    doNothing().when(service).delete(eq(dronePortId), any());

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + dronePortId))
            .andExpect(status().isOk())
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    {
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).delete(eq(dronePortId), userCaptor.capture());
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
   * テストパターン：異常系<br>
   */
  @Test
  public void delete_入力チェックエラー発生() throws Exception {
    String dronePortId = UUID.randomUUID().toString();

    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForGetDetail(dronePortId);
    doNothing().when(service).delete(eq(dronePortId), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + dronePortId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    verify(service, times(0)).delete(anyString(), any());

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
    String dronePortId = UUID.randomUUID().toString();

    doNothing().when(validator).validateForGetDetail(dronePortId);
    doNothing().when(validator).validateForDelete(any(DronePortInfoDeleteRequestDto.class));
    doThrow(new ServiceErrorException("ServiceErrorMessage"))
        .when(service)
        .delete(eq(dronePortId), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/" + dronePortId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    {
      ArgumentCaptor<UserInfoDto> dtoCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).delete(eq(dronePortId), dtoCaptor.capture());
      assertEquals(
          "5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", dtoCaptor.getValue().getUserOperatorId());
    }
    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 離着陸場情報削除APIが呼び出されない<br>
   * 条件: 離着陸場IDを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void delete_離着陸場ID未設定() throws Exception {

    doNothing().when(validator).validateForGetDetail(anyString());
    doNothing().when(service).delete(anyString(), any(UserInfoDto.class));

    MvcResult response =
        mockMvc
            .perform(delete(basePath + "/"))
            .andExpect(status().isInternalServerError())
            .andReturn();

    verify(validator, times(0)).validateForGetDetail(anyString());
    verify(service, times(0)).delete(anyString(), any(UserInfoDto.class));

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 離着陸場情報更新が正しく行われる<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 結果: HTTPコード:200が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void put_正常() throws Exception {
    DronePortInfoRegisterRequestDto requestDto = new DronePortInfoRegisterRequestDto();
    requestDto.setDronePortId("dummyDronePortId");
    requestDto.setDronePortName("dummyDronePortName");
    requestDto.setImageData("dummyImageData");

    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doNothing().when(service).decodeBinary(any());
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
            .andExpect(jsonPath("$.dronePortId").value("responseDronePortId"))
            .andReturn();

    {
      ArgumentCaptor<DronePortInfoRegisterRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoRegisterRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortInfoRegisterRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoRegisterRequestDto.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).update(dtoCaptor.capture(), userCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
    }
    {
      ArgumentCaptor<DronePortInfoRegisterRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoRegisterRequestDto.class);
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
    DronePortInfoRegisterRequestDto requestDto = new DronePortInfoRegisterRequestDto();
    requestDto.setDronePortId("dummyDronePortId");
    requestDto.setDronePortName("dummyDronePortName");
    requestDto.setImageData("dummyImageData");

    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doNothing().when(service).decodeBinary(any());
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

    verify(service, times(1)).decodeBinary(any());
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
    DronePortInfoRegisterRequestDto requestDto = new DronePortInfoRegisterRequestDto();
    requestDto.setDronePortId("dummyDronePortId");
    requestDto.setDronePortName("dummyDronePortName");
    requestDto.setImageData("dummyImageData");

    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doNothing().when(service).decodeBinary(any());
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

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(1)).update(any(), any());
    verify(validator, times(1)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: バイナリ変換エでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void put_バイナリ変換エラー発生() throws Exception {
    DronePortInfoRegisterRequestDto requestDto = new DronePortInfoRegisterRequestDto();
    requestDto.setDronePortId("dummyDronePortId");
    requestDto.setDronePortName("dummyDronePortName");
    requestDto.setImageData("dummyImageData");

    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doThrow(new ServiceErrorException("dummyMessage")).when(service).decodeBinary(any());
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
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(service, times(1)).decodeBinary(any());
    verify(service, times(0)).update(any(), any());
    verify(validator, times(0)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: put<br>
   * 試験名: 離着陸場情報更新APIが呼び出されない<br>
   * 条件: リクエストボディを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void put_リクエストボディなし() throws Exception {

    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doNothing().when(service).decodeBinary(any());
    doReturn(responseDto).when(service).update(any(), any());
    doNothing().when(validator).validateForUpdate(any());

    MvcResult response =
        mockMvc
            .perform(
                put(basePath).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).update(any(), any());
    verify(validator, times(0)).validateForUpdate(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 離着陸場情報登録処理が正しく行われる<br>
   * 条件: 正常な DronePortInfoRegisterRequestDto を渡す<br>
   * 結果: 処理結果(離着陸場ID)が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void post_正常() throws Exception {
    DronePortInfoRegisterRequestDto requestDto = new DronePortInfoRegisterRequestDto();
    requestDto.setDronePortId("dummyDronePortId");
    requestDto.setDronePortName("dummyDronePortName");
    requestDto.setImageData("dummyImageData");

    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doNothing().when(service).decodeBinary(any());
    doReturn(responseDto).when(service).register(any(), any());
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
            .andExpect(jsonPath("$.dronePortId").value("responseDronePortId"))
            .andReturn();

    {
      ArgumentCaptor<DronePortInfoRegisterRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoRegisterRequestDto.class);
      verify(service, times(1)).decodeBinary(dtoCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
    }
    {
      ArgumentCaptor<DronePortInfoRegisterRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoRegisterRequestDto.class);
      ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
      verify(service, times(1)).register(dtoCaptor.capture(), userCaptor.capture());
      assertEquals(requestDto.toString(), dtoCaptor.getValue().toString());
      UserInfoDto capturedUserInfo = userCaptor.getValue();
      assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
      assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
      assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
      assertEquals(
          "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());
    }
    {
      ArgumentCaptor<DronePortInfoRegisterRequestDto> dtoCaptor =
          ArgumentCaptor.forClass(DronePortInfoRegisterRequestDto.class);
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
   * テストパターン：異常系<br>
   */
  @Test
  public void post_入力チェックエラー発生() throws Exception {
    DronePortInfoRegisterRequestDto requestDto = new DronePortInfoRegisterRequestDto();
    requestDto.setDronePortId("dummyDronePortId");
    requestDto.setDronePortName("dummyDronePortName");
    requestDto.setImageData("dummyImageData");

    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doNothing().when(service).decodeBinary(any());
    doReturn(responseDto).when(service).register(any(), any());
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
    verify(service, times(0)).register(any(), any());
    verify(validator, times(1)).validateForRegist(any());

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
    DronePortInfoRegisterRequestDto requestDto = new DronePortInfoRegisterRequestDto();
    requestDto.setDronePortId("dummyDronePortId");
    requestDto.setDronePortName("dummyDronePortName");
    requestDto.setImageData("dummyImageData");

    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doNothing().when(service).decodeBinary(any());
    doThrow(new ServiceErrorException("dummyMessage")).when(service).register(any(), any());
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
    verify(service, times(1)).register(any(), any());
    verify(validator, times(1)).validateForRegist(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: バイナリ変換エでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void post_バイナリ変換エラー発生() throws Exception {
    DronePortInfoRegisterRequestDto requestDto = new DronePortInfoRegisterRequestDto();
    requestDto.setDronePortId("dummyDronePortId");
    requestDto.setDronePortName("dummyDronePortName");
    requestDto.setImageData("dummyImageData");

    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doThrow(new ServiceErrorException("dummyMessage")).when(service).decodeBinary(any());
    doReturn(responseDto).when(service).register(any(), any());
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
    verify(service, times(0)).register(any(), any());
    verify(validator, times(0)).validateForRegist(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: post<br>
   * 試験名: 離着陸場情報登録APIが呼び出されない<br>
   * 条件: リクエストボディを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void post_リクエストボディなし() throws Exception {
    DronePortInfoRegisterResponseDto responseDto = new DronePortInfoRegisterResponseDto();
    responseDto.setDronePortId("responseDronePortId");

    doNothing().when(service).decodeBinary(any());
    doReturn(responseDto).when(service).register(any(), any());
    doNothing().when(validator).validateForRegist(any());

    MvcResult response =
        mockMvc
            .perform(
                post(basePath).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
            .andExpect(status().isBadRequest())
            .andReturn();

    verify(service, times(0)).decodeBinary(any());
    verify(service, times(0)).register(any(), any());
    verify(validator, times(0)).validateForRegist(any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getEnvironment<br>
   * 試験名: 離着陸場周辺情報取得が正しく行われる<br>
   * 条件: 正常な DronePortEnvironmentInfoResponseDto を渡す<br>
   * 結果: 離着陸場周辺情報が正しく返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void getEnvironment_正常() throws Exception {
    String dronePortId = "dummyDronePortId";
    DronePortEnvironmentInfoResponseDto responseDto = new DronePortEnvironmentInfoResponseDto();
    responseDto.setDronePortId(dronePortId);

    when(service.getEnvironment(any(), any())).thenReturn(responseDto);
    doNothing().when(validator).validateForGetDetail(dronePortId);

    MvcResult response =
        mockMvc
            .perform(get(basePath2 + "/environment/" + dronePortId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.dronePortId").value(dronePortId))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    ArgumentCaptor<UserInfoDto> userCaptor = ArgumentCaptor.forClass(UserInfoDto.class);
    verify(service, times(1)).getEnvironment(any(), userCaptor.capture());
    UserInfoDto capturedUserInfo = userCaptor.getValue();
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", capturedUserInfo.getUserOperatorId());
    assertEquals("11", capturedUserInfo.getRoles().get(0).getRoleId());
    assertEquals("テストロール", capturedUserInfo.getRoles().get(0).getRoleName());
    assertEquals(
        "4013e2ad-6cde-432e-985e-50ab5f06bd94", capturedUserInfo.getAffiliatedOperatorId());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getEnvironment<br>
   * 試験名: 入力チェックエラー発生時の動作<br>
   * 条件: 入力チェックでValidationErrorExceptionを発生させる<br>
   * 結果: BadRequestが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getEnvironment_入力チェックエラー発生() throws Exception {
    String dronePortId = "dummyDronePortId";

    doThrow(new ValidationErrorException("dummyMessage"))
        .when(validator)
        .validateForGetDetail(dronePortId);

    MvcResult response =
        mockMvc
            .perform(get(basePath2 + "/environment/" + dronePortId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetail").value("dummyMessage"))
            .andReturn();

    verify(service, times(0)).getEnvironment(anyString(), any());
    verify(validator, times(1)).validateForGetDetail(dronePortId);

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getEnvironment<br>
   * 試験名: サービスでエラー発生時の動作<br>
   * 条件: サービスでServiceErrorExceptionを発生させる<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void getEnvironment_サービスエラー発生() throws Exception {
    String dronePortId = "dummyDronePortId";

    doNothing().when(validator).validateForGetDetail(dronePortId);
    doThrow(new ServiceErrorException("ServiceErrorMessage"))
        .when(service)
        .getEnvironment(any(), any());

    MvcResult response =
        mockMvc
            .perform(get(basePath2 + "/environment/" + dronePortId))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorDetail").value("ServiceErrorMessage"))
            .andReturn();

    verify(validator, times(1)).validateForGetDetail(dronePortId);
    verify(service, times(1)).getEnvironment(anyString(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: getEnvironment<br>
   * 試験名: 離着陸場周辺情報取得APIが呼び出されない<br>
   * 条件: 離着陸場IDを未設定<br>
   * 結果: InternalServerErrorが返される<br>
   * テストパターン：境界値<br>
   */
  @Test
  public void getEnvironment_離着陸場ID未設定() throws Exception {

    MvcResult response =
        mockMvc
            .perform(get(basePath2 + "/environment/"))
            .andExpect(status().isInternalServerError())
            .andReturn();

    verify(validator, times(0)).validateForGetDetail(anyString());
    verify(service, times(0)).getEnvironment(anyString(), any());

    log.info(response.getResponse().getContentAsString(StandardCharsets.UTF_8));
  }

  /**
   * メソッド名: callHealthCheck<br>
   * 試験名: ヘルスチェックの動作<br>
   * 条件: ヘルスチェックを呼び出す<br>
   * 結果: HTTPステータス:200が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void callHealthCheck() throws Exception {
    mockMvc.perform(get("/awshealth/check.html")).andExpect(status().isOk()).andReturn();
  }
}
