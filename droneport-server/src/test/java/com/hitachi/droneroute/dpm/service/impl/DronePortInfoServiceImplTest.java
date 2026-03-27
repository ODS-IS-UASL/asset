package com.hitachi.droneroute.dpm.service.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortEnvironmentInfoResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseElement;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterResponseDto;
import com.hitachi.droneroute.dpm.entity.DronePortInfoEntity;
import com.hitachi.droneroute.dpm.entity.DronePortReserveInfoEntity;
import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;
import com.hitachi.droneroute.dpm.entity.VisTelemetryInfoEntity;
import com.hitachi.droneroute.dpm.repository.DronePortInfoRepository;
import com.hitachi.droneroute.dpm.repository.DronePortReserveInfoRepository;
import com.hitachi.droneroute.dpm.repository.DronePortStatusRepository;
import com.hitachi.droneroute.dpm.repository.VisTelemetryInfoRepository;
import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListDetailElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListResponseDto;
import com.hitachi.droneroute.prm.entity.PriceHistoryInfoEntity;
import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
import com.hitachi.droneroute.prm.repository.PriceHistoryInfoRepository;
import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
import com.hitachi.droneroute.prm.service.PriceInfoSearchListService;
import com.hitachi.droneroute.prm.service.impl.PriceInfoServiceImpl;
import com.hitachi.droneroute.prm.validator.PriceInfoValidator;
import io.hypersistence.utils.hibernate.type.range.Range;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

/** DronePortInfoServiceImplクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class DronePortInfoServiceImplTest {

  @MockBean private DronePortInfoRepository dronePortInfoRepository;

  @MockBean private VisTelemetryInfoRepository visTelemetryInfoRepository;

  @MockBean private DronePortStatusRepository dronePortStatusRepository;

  @MockBean private DronePortReserveInfoRepository dronePortReserveInfoRepository;

  @MockBean private AircraftInfoRepository aircraftInfoRepository;

  @MockBean private PriceInfoRepository priceInfoRepository;

  @MockBean private PriceHistoryInfoRepository priceInfoHistoryRepository;

  @MockBean private PriceInfoSearchListService priceInfoSearchService;

  @Autowired private DronePortInfoServiceImpl dronePortInfoServiceImpl;

  @SpyBean private PriceInfoServiceImpl priceInfoServiceImpl;

  @MockBean private PriceInfoValidator priceInfoValidator;

  @SpyBean private SystemSettings systemSettings;

  private static MockedStatic<LocalDateTime> localDateTimeMock;

  @BeforeAll
  static void setUpOnce() {
    localDateTimeMock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /** ユーザ情報DTO(自事業者) */
  private UserInfoDto createUserInfoDto_OwnOperator() {
    UserInfoDto ret = new UserInfoDto();
    ret.setUserOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setAffiliatedOperatorId("12345678-1234-1234-1234-123456789abc");
    return ret;
  }

  /** ユーザ情報DTO(他事業者) */
  private UserInfoDto createUserInfoDto_OtherOperator() {
    UserInfoDto ret = new UserInfoDto();
    ret.setUserOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setAffiliatedOperatorId("9913e2ad-6cde-432e-985e-50ab5f06b999");
    return ret;
  }

  private DronePortInfoRegisterRequestDto createDronePortInfoRegisterRequestDto() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortName("dummyDronePortName");
    dto.setAddress("dummyAddress");
    dto.setManufacturer("製造メーカー");
    dto.setSerialNumber("シリアル番号");
    dto.setPortType(2);
    dto.setDronePortManufacturerId("PORTMFRID");
    dto.setVisDronePortCompanyId("dummyVisDronePortCompanyId");
    dto.setLat(12.3d);
    dto.setLon(23.4d);
    dto.setAlt(34.1d);
    dto.setSupportDroneType("対応機体");
    dto.setActiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    dto.setInactiveTimeFrom("2024-12-04T10:20:30Z");
    dto.setInactiveTimeTo("2024-12-04T11:20:30Z");
    dto.setImageData(
        "data:image/png;base64," + Base64.getEncoder().encodeToString("testbinary".getBytes()));
    dto.setImageBinary("testbinary".getBytes());
    return dto;
  }

  private DronePortInfoRegisterRequestDto createDronePortInfoRegisterRequestDto2() {
    DronePortInfoRegisterRequestDto ret = new DronePortInfoRegisterRequestDto();

    ret.setDronePortName("離着陸場名");
    ret.setAddress("設置場所住所");
    ret.setManufacturer("製造メーカー");
    ret.setSerialNumber("製造番号");
    ret.setDronePortManufacturerId("離着陸場メーカーID");
    ret.setPortType(1);
    ret.setVisDronePortCompanyId("VIS離着陸場事業者ID");
    ret.setLat(Double.valueOf(11));
    ret.setLon(Double.valueOf(22));
    ret.setAlt(Double.valueOf(33));
    ret.setSupportDroneType("対応機体");
    ret.setActiveStatus(1);
    ret.setImageData(
        "data:image/png;base64," + Base64.getEncoder().encodeToString("testbinary".getBytes()));
    ret.setImageBinary("testbinary".getBytes());
    ret.setPublicFlag(true);

    return ret;
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場情報を登録する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　VIS離着陸場ID設定あり<br>
   * 　　　　　動作状況4:メンテナンス中,使用不可開始/終了日時設定あり<br>
   * 結果: 登録されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void register_Normal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());
    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.register(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals("ROUTEOPRID-PORTMFRID-99", argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(DronePortConstants.ACTIVE_STATUS_PREPARING, argEntity.getActiveStatus());
      assertEquals(dto.getActiveStatus(), argEntity.getInactiveStatus());
      assertEquals(
          toUtcLocalDateTime(dto.getInactiveTimeFrom()).toString(),
          argEntity.getInactiveTime().lower().toString());
      assertEquals(
          toUtcLocalDateTime(dto.getInactiveTimeTo()).toString(),
          argEntity.getInactiveTime().upper().toString());
      assertEquals(aircraftId, argEntity.getStoredAircraftId());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
  }

  private LocalDateTime toUtcLocalDateTime(String str) {
    return ZonedDateTime.parse(str).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場情報を登録する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　VIS離着陸場ID設定なし<br>
   * 　　　　　動作状況4:メンテナンス中,使用不可開始日時のみ設定あり<br>
   * 結果: 登録されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void register_Normal2() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setDronePortName(null);
    dto.setActiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE); // 動作状況、3:使用不可
    dto.setInactiveTimeTo(null); // 使用不可終了日時はnull
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());
    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.register(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals("ROUTEOPRID-PORTMFRID-99", argEntity.getDronePortId());
      assertNull(argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(DronePortConstants.ACTIVE_STATUS_PREPARING, argEntity.getActiveStatus());
      assertEquals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE, argEntity.getInactiveStatus());
      assertEquals(
          toUtcLocalDateTime(dto.getInactiveTimeFrom()).toString(),
          argEntity.getInactiveTime().lower().toString());
      assertNull(argEntity.getInactiveTime().upper());
      assertFalse(argEntity.getInactiveTime().hasUpperBound());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場情報を登録する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　VIS離着陸場ID設定なし<br>
   * 　　　　　動作状況1:準備中,使用不可開始/終了日時設定なし<br>
   * 結果: 登録されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void register_Normal3() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setActiveStatus(1); // 動作状況、1:準備中
    dto.setInactiveTimeFrom(null); // 使用不可開始日時はnull
    dto.setInactiveTimeTo(null); // 使用不可終了日時はnull
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());
    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.register(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals("ROUTEOPRID-PORTMFRID-99", argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getActiveStatus(), argEntity.getActiveStatus());
      assertNull(argEntity.getInactiveStatus());
      assertNull(argEntity.getInactiveTime());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場情報を登録する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　VIS離着陸場ID設定なし<br>
   * 　　　　　動作状況2:使用可,使用不可開始/終了日時設定なし<br>
   * 結果: 登録されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void register_Normal4() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setActiveStatus(2); // 動作状況、2:使用可
    dto.setInactiveTimeFrom(null); // 使用不可開始日時はnull
    dto.setInactiveTimeTo(null); // 使用不可終了日時はnull
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());
    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.register(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals("ROUTEOPRID-PORTMFRID-99", argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getActiveStatus(), argEntity.getActiveStatus());
      assertNull(argEntity.getInactiveStatus());
      assertNull(argEntity.getInactiveTime());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場情報を登録する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　VIS離着陸場ID設定なし<br>
   * 　　　　　動作状況9(不正値),使用不可開始/終了日時設定なし<br>
   * 　　　　　カバレッジ100%のため<br>
   * 結果: 登録されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void register_Normal5() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setActiveStatus(9); // 動作状況、あり得ない値
    dto.setInactiveTimeFrom(null); // 使用不可開始日時はnull
    dto.setInactiveTimeTo(null); // 使用不可終了日時はnull
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());
    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.register(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals("ROUTEOPRID-PORTMFRID-99", argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertNull(argEntity.getActiveStatus());
      assertNull(argEntity.getInactiveStatus());
      assertNull(argEntity.getInactiveTime());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場ID重複時の例外<br>
   * 条件: 新たに採番した離着陸場IDが重複している<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void register_DupulicateId() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString()))
        .thenReturn(Optional.of(new DronePortInfoEntity()));

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class, () -> dronePortInfoServiceImpl.register(dto, userDto));

    assertEquals("離着陸場IDが重複しています。離着陸場ID:ROUTEOPRID-PORTMFRID-99", exception.getMessage());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 格納中機体IDが存在しない場合の例外<br>
   * 条件: 格納中機体IDに対応する機体情報が存在しない<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void register_StoredAircraftId_invalid() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());
    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(
            NotFoundException.class, () -> dronePortInfoServiceImpl.register(dto, userDto));

    assertEquals("機体情報が見つかりません。格納中機体ID:" + aircraftId.toString(), exception.getMessage());
  }

  // 料金情報用リクエストDTO作成
  private DronePortInfoRegisterRequestDto createOnePriceInfoDronePortInfoRegisterRequestDto() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setDronePortName("離着陸場名");
    dto.setAddress("設置場所住所");
    dto.setManufacturer("製造メーカー");
    dto.setSerialNumber("製造番号");
    dto.setDronePortManufacturerId("離着陸場メーカーID");
    dto.setPortType(1);
    dto.setVisDronePortCompanyId("VIS離着陸場事業者ID");
    dto.setStoredAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setLat((double) 11);
    dto.setLon((double) 22);
    dto.setAlt((double) 33);
    dto.setSupportDroneType("対応機体");
    // dto.setUsageType(1);
    dto.setActiveStatus(1);
    dto.setInactiveTimeFrom("2025-11-13T10:00:00Z");
    dto.setInactiveTimeTo("2025-11-13T10:00:00Z");
    dto.setImageData(null);

    PriceInfoRequestDto ret = new PriceInfoRequestDto();
    List<PriceInfoRequestDto> list = new ArrayList<>();
    ret.setProcessingType(1);
    ret.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setPriceType(4);
    ret.setPricePerUnit(1);
    ret.setPrice(1000);
    ret.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret.setEffectiveEndTime("2025-11-13T10:00:00Z");
    ret.setPriority(1);
    list.add(ret);
    dto.setPriceInfos(list);
    return dto;
  }

  private DronePortInfoRegisterRequestDto createThreePriceInfoDronePortInfoRegisterRequestDto() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setDronePortName("離着陸場名");
    dto.setAddress("設置場所住所");
    dto.setManufacturer("製造メーカー");
    dto.setSerialNumber("製造番号");
    dto.setDronePortManufacturerId("離着陸場メーカーID");
    dto.setPortType(1);
    dto.setVisDronePortCompanyId("VIS離着陸場事業者ID");
    dto.setStoredAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setLat((double) 11);
    dto.setLon((double) 22);
    dto.setAlt((double) 33);
    dto.setSupportDroneType("対応機体");
    // dto.setUsageType(1);
    dto.setActiveStatus(1);
    dto.setInactiveTimeFrom("2025-11-13T10:00:00Z");
    dto.setInactiveTimeTo("2025-11-13T10:00:00Z");
    dto.setImageData(null);

    PriceInfoRequestDto ret1 = new PriceInfoRequestDto();
    List<PriceInfoRequestDto> list = new ArrayList<>();
    ret1.setProcessingType(1);
    ret1.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret1.setPriceType(4);
    ret1.setPricePerUnit(1);
    ret1.setPrice(1000);
    ret1.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret1.setEffectiveEndTime("2025-11-13T10:00:00Z");
    ret1.setPriority(1);
    list.add(ret1);

    PriceInfoRequestDto ret2 = new PriceInfoRequestDto();
    ret2.setProcessingType(1);
    ret2.setPriceId("6b5ec052-a76f-87cb-ef4a-a31c62a13276");
    ret2.setPriceType(4);
    ret2.setPricePerUnit(1);
    ret2.setPrice(2000);
    ret2.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret2.setEffectiveEndTime("2025-11-13T10:00:00Z");
    ret2.setPriority(2);
    list.add(ret2);

    PriceInfoRequestDto ret3 = new PriceInfoRequestDto();
    ret3.setProcessingType(1);
    ret3.setPriceId("f3113bed-357e-2386-2b15-effaf01a592e");
    ret3.setPriceType(4);
    ret3.setPricePerUnit(1);
    ret3.setPrice(3000);
    ret3.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret3.setEffectiveEndTime("2025-11-13T10:00:00Z");
    ret3.setPriority(3);
    list.add(ret3);

    dto.setPriceInfos(list);

    return dto;
  }

  /**
   * メソッド名: register<br>
   * 試験名: 料金情報を含む離着陸場情報登録<br>
   * 条件: 料金情報1件<br>
   * 結果: DTOのマッピング内容を確認<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_onePriceInfo_Nomal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();

    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(UUID.randomUUID().toString());

    PriceInfoEntity entity2 = new PriceInfoEntity();
    entity2.setPriceId(UUID.randomUUID());

    PriceHistoryInfoEntity entity3 = new PriceHistoryInfoEntity();
    entity3.setPriceHistoryId(UUID.randomUUID());

    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity1);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());

    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // モックを設定(料金情報)
    doNothing().when(priceInfoServiceImpl).process(any());

    dronePortInfoServiceImpl.register(dto, userDto);

    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
    List<PriceInfoRequestDto> argEntity = Captor.getValue();

    // DTOのマッピング内容を確認
    assertEquals(1, argEntity.get(0).getProcessingType());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getPriceId());
    assertEquals(entity1.getDronePortId(), argEntity.get(0).getResourceId());
    assertEquals(20, argEntity.get(0).getResourceType());
    assertEquals(4, argEntity.get(0).getPriceType());
    assertEquals(1, argEntity.get(0).getPricePerUnit());
    assertEquals(1000, argEntity.get(0).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveEndTime());
    assertEquals(1, argEntity.get(0).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(0).getOperatorId());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 料金情報を含む離着陸場情報登録<br>
   * 条件: 料金情報3件<br>
   * 結果: DTOのマッピング内容を確認<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_threePriceInfo_Nomal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createThreePriceInfoDronePortInfoRegisterRequestDto();

    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(UUID.randomUUID().toString());

    PriceInfoEntity entity2 = new PriceInfoEntity();
    entity2.setPriceId(UUID.randomUUID());

    PriceHistoryInfoEntity entity3 = new PriceHistoryInfoEntity();
    entity3.setPriceHistoryId(UUID.randomUUID());

    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity1);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());

    doNothing().when(priceInfoServiceImpl).process(any());

    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    dronePortInfoServiceImpl.register(dto, userDto);

    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
    List<PriceInfoRequestDto> argEntity = Captor.getValue();

    // DTOのマッピング内容を確認
    assertEquals(1, argEntity.get(0).getProcessingType());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getPriceId());
    assertEquals(entity1.getDronePortId(), argEntity.get(0).getResourceId());
    assertEquals(20, argEntity.get(0).getResourceType());
    assertEquals(4, argEntity.get(0).getPriceType());
    assertEquals(1, argEntity.get(0).getPricePerUnit());
    assertEquals(1000, argEntity.get(0).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveEndTime());
    assertEquals(1, argEntity.get(0).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(0).getOperatorId());

    assertEquals(1, argEntity.get(1).getProcessingType());
    assertEquals("6b5ec052-a76f-87cb-ef4a-a31c62a13276", argEntity.get(1).getPriceId());
    assertEquals(entity1.getDronePortId(), argEntity.get(1).getResourceId());
    assertEquals(20, argEntity.get(1).getResourceType());
    assertEquals(4, argEntity.get(1).getPriceType());
    assertEquals(1, argEntity.get(1).getPricePerUnit());
    assertEquals(2000, argEntity.get(1).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(1).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(1).getEffectiveEndTime());
    assertEquals(2, argEntity.get(1).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(1).getOperatorId());

    assertEquals(1, argEntity.get(2).getProcessingType());
    assertEquals("f3113bed-357e-2386-2b15-effaf01a592e", argEntity.get(2).getPriceId());
    assertEquals(entity1.getDronePortId(), argEntity.get(2).getResourceId());
    assertEquals(20, argEntity.get(2).getResourceType());
    assertEquals(4, argEntity.get(2).getPriceType());
    assertEquals(1, argEntity.get(2).getPricePerUnit());
    assertEquals(3000, argEntity.get(2).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(2).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(2).getEffectiveEndTime());
    assertEquals(3, argEntity.get(2).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(2).getOperatorId());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 料金情報を含む離着陸場情報登録<br>
   * 条件: 料金情報は空配列<br>
   * 結果: 正常に処理が終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void register_zeroPriceInfo_Nomal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto2();
    List<PriceInfoRequestDto> priceList = new ArrayList<>();
    dto.setPriceInfos(priceList);

    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(UUID.randomUUID().toString());

    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity1);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());

    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.register(dto, userDto);

    assertNotNull(response);
    assertEquals(entity1.getDronePortId().toString(), response.getDronePortId());
    verify(priceInfoServiceImpl, never()).process(any());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 料金情報を含む離着陸場情報登録<br>
   * 条件: 料金情報はnull<br>
   * 結果: 正常に処理が終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void register_nullPriceInfo_Nomal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setPriceInfos(null);

    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(UUID.randomUUID().toString());

    PriceInfoEntity entity2 = new PriceInfoEntity();
    entity2.setPriceId(UUID.randomUUID());

    PriceHistoryInfoEntity entity3 = new PriceHistoryInfoEntity();
    entity3.setPriceHistoryId(UUID.randomUUID());

    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity1);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());

    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // モックを設定(料金情報)
    doNothing().when(priceInfoServiceImpl).process(any());

    // 実行
    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.register(dto, userDto);

    // 確認
    assertNotNull(response);
    assertEquals(entity1.getDronePortId().toString(), response.getDronePortId());
    verify(priceInfoServiceImpl, never()).process(any());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 料金情報を含む離着陸場情報登録<br>
   * 条件: 料金情報1件<br>
   * 結果: DTOのマッピング内容を確認<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_Nomal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();

    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(UUID.randomUUID().toString());

    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity1);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());

    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // モックを設定(料金情報)
    doNothing().when(priceInfoServiceImpl).process(any());

    dronePortInfoServiceImpl.register(dto, userDto);

    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
    List<PriceInfoRequestDto> argEntity = Captor.getValue();

    // DTOのマッピング内容を確認
    assertEquals(1, argEntity.get(0).getProcessingType());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getPriceId());
    assertEquals(entity1.getDronePortId(), argEntity.get(0).getResourceId());
    assertEquals(20, argEntity.get(0).getResourceType());
    assertEquals(4, argEntity.get(0).getPriceType());
    assertEquals(1, argEntity.get(0).getPricePerUnit());
    assertEquals(1000, argEntity.get(0).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveEndTime());
    assertEquals(1, argEntity.get(0).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(0).getOperatorId());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 料金情報を含む離着陸場情報登録の異常系テスト<br>
   * 条件: 料金情報1件、サービスでNotFoundExceptionを発生させる<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_onePriceInfo_NotFoundException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();

    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(UUID.randomUUID().toString());

    PriceInfoEntity entity2 = new PriceInfoEntity();
    entity2.setPriceId(UUID.randomUUID());

    PriceHistoryInfoEntity entity3 = new PriceHistoryInfoEntity();
    entity3.setPriceHistoryId(UUID.randomUUID());

    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity1);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());

    // モックを設定(料金情報：エラー)
    doThrow(new NotFoundException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // 実行確認
    NotFoundException exception =
        assertThrows(
            NotFoundException.class, () -> dronePortInfoServiceImpl.register(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 料金情報を含む離着陸場情報登録の異常系テスト<br>
   * 条件: 料金情報1件、サービスでServiceErrorExceptionを発生させる<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_onePriceInfo_ServiceErrorException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();

    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(UUID.randomUUID().toString());

    PriceInfoEntity entity2 = new PriceInfoEntity();
    entity2.setPriceId(UUID.randomUUID());

    PriceHistoryInfoEntity entity3 = new PriceHistoryInfoEntity();
    entity3.setPriceHistoryId(UUID.randomUUID());

    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity1);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());

    // モックを設定(料金情報：エラー)
    doThrow(new ServiceErrorException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // 実行確認
    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class, () -> dronePortInfoServiceImpl.register(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 料金情報を含む離着陸場情報登録の異常系テスト<br>
   * 条件: 料金情報1件、サービスでValidationErrorExceptionを発生させる<br>
   * 結果: ValidationErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_onePriceInfo_ValidationErrorException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();

    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(UUID.randomUUID().toString());

    PriceInfoEntity entity2 = new PriceInfoEntity();
    entity2.setPriceId(UUID.randomUUID());

    PriceHistoryInfoEntity entity3 = new PriceHistoryInfoEntity();
    entity3.setPriceHistoryId(UUID.randomUUID());

    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity1);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());

    // モックを設定(料金情報：エラー)
    doThrow(new ValidationErrorException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // 実行確認
    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class, () -> dronePortInfoServiceImpl.register(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 料金情報を含む離着陸場情報登録の異常系テスト<br>
   * 条件: 料金情報1件、サービスでNullPointerExceptionを発生させる<br>
   * 結果: NullPointerExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_onePriceInfo_NullPointerException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();

    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(UUID.randomUUID().toString());

    PriceInfoEntity entity2 = new PriceInfoEntity();
    entity2.setPriceId(UUID.randomUUID());

    PriceHistoryInfoEntity entity3 = new PriceHistoryInfoEntity();
    entity3.setPriceHistoryId(UUID.randomUUID());

    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity1);
    when(dronePortInfoRepository.getNextSequenceValue()).thenReturn(99L);
    when(dronePortInfoRepository.findByDronePortId(anyString())).thenReturn(Optional.empty());

    // モックを設定(料金情報：エラー)
    doThrow(new NullPointerException("上記以外の例外が発生")).when(priceInfoServiceImpl).process(any());

    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // 実行確認
    NullPointerException exception =
        assertThrows(
            NullPointerException.class, () -> dronePortInfoServiceImpl.register(dto, userDto));
    assertTrue(exception.getMessage().contains("上記以外の例外が発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報更新あり<br>
   * 　　　　　離着陸場状態更新あり(4:メンテナンス中,使用不可開始/終了日時設定あり)<br>
   * 　　　　　離着陸場状態テーブル<br>
   * 　　　　　　　更新前: 動作状況(使用可):使用可、動作状況(使用不可):null、使用不可時間範囲設定null<br>
   * 　　　　　取消対象の予約なし<br>
   * 結果: 更新されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_Normal1() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setStoredAircraftId(aircraftId.toString());
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);
    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getDronePortId(), argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(DronePortConstants.ACTIVE_STATUS_AVAILABLE, argEntity.getActiveStatus());
      assertEquals(DronePortConstants.ACTIVE_STATUS_MAINTENANCE, argEntity.getInactiveStatus());
      assertEquals(
          toUtcLocalDateTime(dto.getInactiveTimeFrom()).toString(),
          argEntity.getInactiveTime().lower().toString());
      assertEquals(
          toUtcLocalDateTime(dto.getInactiveTimeTo()).toString(),
          argEntity.getInactiveTime().upper().toString());
      assertEquals(aircraftId, argEntity.getStoredAircraftId());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場予約情報の取消実施検証
    verify(dronePortReserveInfoRepository, times(0)).save(any(DronePortReserveInfoEntity.class));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報更新あり<br>
   * 　　　　　離着陸場状態更新あり(4:メンテナンス中,使用不可開始/終了日時設定あり)<br>
   * 　　　　　離着陸場状態テーブル<br>
   * 　　　　　　　更新前: 動作状況(使用可):使用可、動作状況(使用不可):null、使用不可時間範囲設定null<br>
   * 　　　　　取消対象の予約なし<br>
   * 結果: 更新されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_Normal1_2() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setStoredAircraftId(aircraftId.toString());
    dto.setActiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    dto.setInactiveTimeTo(null);
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);
    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getDronePortId(), argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(DronePortConstants.ACTIVE_STATUS_AVAILABLE, argEntity.getActiveStatus());
      assertEquals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE, argEntity.getInactiveStatus());
      assertEquals(
          toUtcLocalDateTime(dto.getInactiveTimeFrom()).toString(),
          argEntity.getInactiveTime().lower().toString());
      assertNull(argEntity.getInactiveTime().upper());
      assertFalse(argEntity.getInactiveTime().hasUpperBound());
      assertEquals(aircraftId, argEntity.getStoredAircraftId());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場予約情報の取消実施検証
    verify(dronePortReserveInfoRepository, times(0)).save(any(DronePortReserveInfoEntity.class));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報更新あり<br>
   * 　　　　　離着陸場状態更新あり(動作状況:不正値)<br>
   * 　　　　　離着陸場状態テーブル<br>
   * 　　　　　　　更新前: 動作状況(使用可):使用可、動作状況(使用不可):null、使用不可時間範囲設定null<br>
   * 　　　　　取消対象の予約なし<br>
   * 結果: 更新されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_Normal1_3() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setStoredAircraftId(aircraftId.toString());
    dto.setActiveStatus(99);
    dto.setInactiveTimeFrom(null);
    dto.setInactiveTimeTo(null);
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);
    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getDronePortId(), argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(DronePortConstants.ACTIVE_STATUS_AVAILABLE, argEntity.getActiveStatus());
      assertNull(argEntity.getInactiveStatus());
      assertNull(argEntity.getInactiveTime());
      assertEquals(aircraftId, argEntity.getStoredAircraftId());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場予約情報の取消実施検証
    verify(dronePortReserveInfoRepository, times(0)).save(any(DronePortReserveInfoEntity.class));
  }

  private DronePortReserveInfoEntity createDronePortReserveInfoEntity(String dronePortId) {
    DronePortReserveInfoEntity entity = new DronePortReserveInfoEntity();
    entity.setDronePortReservationId(UUID.randomUUID());
    entity.setDronePortId(dronePortId);
    entity.setAircraftId(UUID.randomUUID());
    entity.setRouteReservationId(UUID.randomUUID());
    entity.setUsageType(1);
    entity.setReservationActiveFlag(true);
    entity.setReservationTime(
        Range.localDateTimeRange("[2024-12-04T01:00:00,2024-12-04T02:00:00)"));
    return entity;
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報更新あり<br>
   * 　　　　　離着陸場状態更新あり(動作状況3:使用不可,使用不可開始日時のみ設定あり)<br>
   * 　　　　　取消対象の予約1件あり。オペレータIDがリクエストと不一致。<br>
   * 結果: 更新されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_Normal2() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setActiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE); // 動作状況、3:使用不可
    dto.setInactiveTimeTo(null); // 使用不可終了日時はnull
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    DronePortReserveInfoEntity reserveEntity =
        createDronePortReserveInfoEntity(dto.getDronePortId());
    reserveEntity.setOperatorId(userDto.getUserOperatorId() + "xyz");
    reserveList.add(reserveEntity);
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(null);

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getDronePortId(), argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getDronePortId(), argEntity.getDronePortId());
      assertEquals(DronePortConstants.ACTIVE_STATUS_PREPARING, argEntity.getActiveStatus());
      assertEquals(dto.getActiveStatus(), argEntity.getInactiveStatus());
      assertEquals(
          toUtcLocalDateTime(dto.getInactiveTimeFrom()).toString(),
          argEntity.getInactiveTime().lower().toString());
      assertNull(argEntity.getInactiveTime().upper());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場予約情報の登録データを検証
    {
      ArgumentCaptor<DronePortReserveInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoEntity.class);
      verify(dronePortReserveInfoRepository).save(entityCaptor.capture());
      DronePortReserveInfoEntity argEntity = entityCaptor.getValue();
      assertEquals(
          reserveEntity.getDronePortReservationId(), argEntity.getDronePortReservationId());
      assertEquals(reserveEntity.getDronePortId(), argEntity.getDronePortId());
      assertEquals(reserveEntity.getAircraftId(), argEntity.getAircraftId());
      assertEquals(reserveEntity.getRouteReservationId(), argEntity.getRouteReservationId());
      assertEquals(reserveEntity.getUsageType(), argEntity.getUsageType());
      assertFalse(argEntity.getReservationActiveFlag());
      assertEquals(reserveEntity.getReservationTime(), argEntity.getReservationTime());
      assertEquals(userDto.getUserOperatorId() + "xyz", argEntity.getOperatorId());
    }
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報更新あり<br>
   * 　　　　　離着陸場状態更新あり(動作状況のみ設定。準備中に変更。)<br>
   * 結果: 更新されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_Normal3() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING); // 動作状況、1:準備中
    dto.setInactiveTimeFrom(null); // 使用不可開始日時はnull
    dto.setInactiveTimeTo(null); // 使用不可終了日時はnull
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    DronePortReserveInfoEntity reserveEntity =
        createDronePortReserveInfoEntity(dto.getDronePortId());
    reserveEntity.setOperatorId(userDto.getUserOperatorId());
    reserveList.add(reserveEntity);
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(null);

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getDronePortId(), argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getDronePortId(), argEntity.getDronePortId());
      assertEquals(DronePortConstants.ACTIVE_STATUS_PREPARING, argEntity.getActiveStatus());
      assertNull(argEntity.getInactiveStatus());
      assertNull(argEntity.getInactiveTime());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場予約情報の取消対象検索を検証
    verify(dronePortReserveInfoRepository, times(0)).findAll(any(Specification.class));
    verify(dronePortReserveInfoRepository, times(0)).save(any(DronePortReserveInfoEntity.class));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報更新あり<br>
   * 　　　　　離着陸場状態更新あり(動作状況のみ設定。使用可に変更。)<br>
   * 結果: 更新されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_Normal3_1() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE); // 動作状況、2:使用可
    dto.setInactiveTimeFrom(null); // 使用不可開始日時はnull
    dto.setInactiveTimeTo(null); // 使用不可終了日時はnull
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    DronePortReserveInfoEntity reserveEntity =
        createDronePortReserveInfoEntity(dto.getDronePortId());
    reserveEntity.setOperatorId(userDto.getUserOperatorId());
    reserveList.add(reserveEntity);
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(null);

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getDronePortId(), argEntity.getDronePortId());
      assertEquals(dto.getDronePortName(), argEntity.getDronePortName());
      assertEquals(dto.getAddress(), argEntity.getAddress());
      assertEquals(dto.getManufacturer(), argEntity.getManufacturer());
      assertEquals(dto.getSerialNumber(), argEntity.getSerialNumber());
      assertEquals(dto.getPortType(), argEntity.getPortType());
      assertEquals(dto.getVisDronePortCompanyId(), argEntity.getVisDronePortCompanyId());
      assertEquals(dto.getLat(), argEntity.getLat());
      assertEquals(dto.getLon(), argEntity.getLon());
      assertEquals(Double.valueOf(dto.getAlt().doubleValue()), argEntity.getAlt());
      assertEquals(dto.getSupportDroneType(), argEntity.getSupportDroneType());
      assertEquals(dto.getImageBinary(), argEntity.getImageBinary());
      assertEquals(argEntity.getImageFormat(), "png");
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(dto.getDronePortId(), argEntity.getDronePortId());
      assertEquals(DronePortConstants.ACTIVE_STATUS_AVAILABLE, argEntity.getActiveStatus());
      assertNull(argEntity.getInactiveStatus());
      assertNull(argEntity.getInactiveTime());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場予約情報の取消対象検索を検証
    verify(dronePortReserveInfoRepository, times(0)).findAll(any(Specification.class));
    verify(dronePortReserveInfoRepository, times(0)).save(any(DronePortReserveInfoEntity.class));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報更新あり<br>
   * 　　　　　離着陸場状態更新あり(使用不可開始日時のみ設定)<br>
   * 結果: 更新されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_Normal4() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("drone1234");
    // この他の離着陸場情報の項目は全て更新なし(null)
    dto.setActiveStatus(null); // 動作状況、1:準備中
    dto.setInactiveTimeFrom("2024-12-01T01:00:00Z");
    // この他の離着陸場状態の項目は全て更新なし(null)
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    DronePortReserveInfoEntity reserveEntity =
        createDronePortReserveInfoEntity(dto.getDronePortId());
    reserveEntity.setOperatorId(userDto.getUserOperatorId());
    reserveList.add(reserveEntity);
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(null);

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // DB登録の呼出を検証
    verify(dronePortInfoRepository, times(1)).save(any(DronePortInfoEntity.class));
    verify(dronePortStatusRepository, times(1)).save(any(DronePortStatusEntity.class));
    // 離着陸場予約情報の取消対象検索を検証
    verify(dronePortReserveInfoRepository, times(0)).findAll(any(Specification.class));
    verify(dronePortReserveInfoRepository, times(0)).save(any(DronePortReserveInfoEntity.class));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報更新あり<br>
   * 　　　　　離着陸場状態更新あり(格納中機体IDのみ設定)<br>
   * 結果: 更新されたエンティティのIDがレスポンスDTOに含まれる<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_Normal4_1() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setDronePortName("drone1234");
    // この他の離着陸場情報の項目は全て更新なし(null)
    dto.setActiveStatus(null);
    dto.setInactiveTimeFrom(null);
    dto.setInactiveTimeTo(null);
    dto.setStoredAircraftId(UUID.randomUUID().toString());
    // この他の離着陸場状態の項目は全て更新なし(null)
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    DronePortReserveInfoEntity reserveEntity =
        createDronePortReserveInfoEntity(dto.getDronePortId());
    reserveEntity.setOperatorId(userDto.getUserOperatorId());
    reserveList.add(reserveEntity);
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(null);
    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // DB登録の呼出を検証
    verify(dronePortInfoRepository, times(1)).save(any(DronePortInfoEntity.class));
    verify(dronePortStatusRepository, times(1)).save(any(DronePortStatusEntity.class));
    // 離着陸場予約情報の取消対象検索を検証
    verify(dronePortReserveInfoRepository, times(0)).findAll(any(Specification.class));
    verify(dronePortReserveInfoRepository, times(0)).save(any(DronePortReserveInfoEntity.class));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 更新対象なし<br>
   * 結果: 更新されたエンティティのIDがレスポンスDTOに含まれ、離着陸場情報テーブル、離着陸場状態テーブル更新が呼び出されない<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_Normal5() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());

    // 離着陸場情報の登録呼出を検証
    verify(dronePortInfoRepository, times(0)).save(any(DronePortInfoEntity.class));
    // 離着陸場状態の登録呼出を検証
    verify(dronePortStatusRepository, times(0)).save(any(DronePortStatusEntity.class));
    // 離着陸場予約情報の取消対象検索を検証
    verify(dronePortReserveInfoRepository, times(0)).findAll(any(Specification.class));
    verify(dronePortReserveInfoRepository, times(0)).save(any(DronePortReserveInfoEntity.class));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 離着陸場情報テーブルに該当レコードが存在しない<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void update_NotFound1() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dto.getDronePortId()))
        .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> dronePortInfoServiceImpl.update(dto, userDto));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場情報を更新する<br>
   * 条件: 離着陸場状態テーブルに該当レコードが存在しない<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void update_NotFound2() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dto.getDronePortId()))
        .thenReturn(Optional.of(entity));
    // モックを設定(離着陸場状態)
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dto.getDronePortId()))
        .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> dronePortInfoServiceImpl.update(dto, userDto));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 動作状況を使用不可からメンテナンス中に変更する<br>
   * 条件: 離着陸場状態の動作状況をメンテナンス中に設定し、更新前の状態が使用不可である<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void update_dronePortStatus_prohibited_status_error() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setStoredAircraftId(aircraftId.toString());
    dto.setActiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);
    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class, () -> dronePortInfoServiceImpl.update(dto, userDto));

    assertEquals("動作状況を、使用不可からメンテナンス中に変更することはできません。", exception.getMessage());

    // 離着陸場情報の更新呼出検証
    verify(dronePortInfoRepository, times(1)).save(any(DronePortInfoEntity.class));
    // 離着陸場状態の更新呼出検証
    verify(dronePortStatusRepository, times(0)).save(any(DronePortStatusEntity.class));
    // 離着陸場予約情報の取消実施検証
    verify(dronePortReserveInfoRepository, times(0)).save(any(DronePortReserveInfoEntity.class));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 料金情報を含む離着陸場情報更新の正常系テスト<br>
   * 条件: 料金情報1件<br>
   * 結果: DTOのマッピング内容を確認<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_onePriceInfo_Nomal() {

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());

    PriceInfoRequestDto info = dto.getPriceInfos().get(0);
    info.setProcessingType(2);

    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    doNothing().when(priceInfoServiceImpl).process(any());

    dronePortInfoServiceImpl.update(dto, userDto);

    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
    List<PriceInfoRequestDto> argEntity = Captor.getValue();

    // DTOのマッピング内容を確認
    assertEquals(2, argEntity.get(0).getProcessingType());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getPriceId());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getResourceId());
    assertEquals(20, argEntity.get(0).getResourceType());
    assertEquals(4, argEntity.get(0).getPriceType());
    assertEquals(1, argEntity.get(0).getPricePerUnit());
    assertEquals(1000, argEntity.get(0).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveEndTime());
    assertEquals(1, argEntity.get(0).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(0).getOperatorId());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 料金情報を含む離着陸場情報更新の正常系テスト<br>
   * 条件: 料金情報3件<br>
   * 結果: DTOのマッピング内容を確認<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_threePriceInfo_Nomal() {

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    DronePortInfoRegisterRequestDto dto = createThreePriceInfoDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());

    PriceInfoRequestDto info1 = dto.getPriceInfos().get(0);
    PriceInfoRequestDto info2 = dto.getPriceInfos().get(1);
    PriceInfoRequestDto info3 = dto.getPriceInfos().get(2);
    info1.setProcessingType(2);
    info2.setProcessingType(2);
    info3.setProcessingType(2);

    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    doNothing().when(priceInfoServiceImpl).process(any());

    dronePortInfoServiceImpl.update(dto, userDto);

    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
    List<PriceInfoRequestDto> argEntity = Captor.getValue();

    // DTOのマッピング内容を確認
    assertEquals(2, argEntity.get(0).getProcessingType());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getPriceId());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getResourceId());
    assertEquals(20, argEntity.get(0).getResourceType());
    assertEquals(4, argEntity.get(0).getPriceType());
    assertEquals(1, argEntity.get(0).getPricePerUnit());
    assertEquals(1000, argEntity.get(0).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveEndTime());
    assertEquals(1, argEntity.get(0).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(0).getOperatorId());

    assertEquals(2, argEntity.get(1).getProcessingType());
    assertEquals("6b5ec052-a76f-87cb-ef4a-a31c62a13276", argEntity.get(1).getPriceId());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(1).getResourceId());
    assertEquals(20, argEntity.get(1).getResourceType());
    assertEquals(4, argEntity.get(1).getPriceType());
    assertEquals(1, argEntity.get(1).getPricePerUnit());
    assertEquals(2000, argEntity.get(1).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(1).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(1).getEffectiveEndTime());
    assertEquals(2, argEntity.get(1).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(1).getOperatorId());

    assertEquals(2, argEntity.get(2).getProcessingType());
    assertEquals("f3113bed-357e-2386-2b15-effaf01a592e", argEntity.get(2).getPriceId());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(2).getResourceId());
    assertEquals(20, argEntity.get(2).getResourceType());
    assertEquals(4, argEntity.get(2).getPriceType());
    assertEquals(1, argEntity.get(2).getPricePerUnit());
    assertEquals(3000, argEntity.get(2).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(2).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(2).getEffectiveEndTime());
    assertEquals(3, argEntity.get(2).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(2).getOperatorId());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 料金情報を含む離着陸場情報更新の正常系テスト<br>
   * 条件: 料金情報は空配列<br>
   * 結果: 正常に処理が終了する<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_zeroPriceInfo_Nomal() {

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());
    dto.setPriceInfos(Collections.emptyList());

    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    doNothing().when(priceInfoServiceImpl).process(any());

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    // DTOのマッピング内容を確認
    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());
    verify(priceInfoServiceImpl, never()).process(any());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 料金情報を含む離着陸場情報更新の正常系テスト<br>
   * 条件: 料金情報はnull<br>
   * 結果: 正常に処理が終了する<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_nullPriceInfo_Nomal() {

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());
    dto.setPriceInfos(null);

    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    doNothing().when(priceInfoServiceImpl).process(any());

    DronePortInfoRegisterResponseDto response = dronePortInfoServiceImpl.update(dto, userDto);

    // DTOのマッピング内容を確認
    assertNotNull(response);
    assertEquals(entity.getDronePortId().toString(), response.getDronePortId());
    verify(priceInfoServiceImpl, never()).process(any());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 料金情報を含む離着陸場情報更新の異常系テスト<br>
   * 条件: 料金情報1件、サービスでNotFoundExceptionを発生させる<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_onePriceInfo_NotFoundException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());

    PriceInfoRequestDto info1 = dto.getPriceInfos().get(0);
    info1.setProcessingType(2);

    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // モックを設定(料金情報：エラー)
    doThrow(new NotFoundException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // 実行確認
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> dronePortInfoServiceImpl.update(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 料金情報を含む離着陸場情報更新の異常系テスト<br>
   * 条件: 料金情報1件、サービスでServiceErrorExceptionを発生させる<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_onePriceInfo_ServiceErrorException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());

    PriceInfoRequestDto info1 = dto.getPriceInfos().get(0);
    info1.setProcessingType(2);

    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // モックを設定(料金情報：エラー)
    doThrow(new ServiceErrorException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // 実行確認
    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class, () -> dronePortInfoServiceImpl.update(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 料金情報を含む離着陸場情報更新の異常系テスト<br>
   * 条件: 料金情報1件、サービスでValidationErrorExceptionを発生させる<br>
   * 結果: ValidationErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_onePriceInfo_ValidationErrorException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());

    PriceInfoRequestDto info1 = dto.getPriceInfos().get(0);
    info1.setProcessingType(2);

    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // モックを設定(料金情報：エラー)
    doThrow(new ValidationErrorException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // 実行確認
    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class, () -> dronePortInfoServiceImpl.update(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 料金情報を含む離着陸場情報更新の異常系テスト<br>
   * 条件: 料金情報1件、サービスでNullPointerExceptionを発生させる<br>
   * 結果: NullPointerExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void update_onePriceInfo_NullPointerException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    DronePortInfoRegisterRequestDto dto = createOnePriceInfoDronePortInfoRegisterRequestDto();
    dto.setStoredAircraftId(aircraftId.toString());

    PriceInfoRequestDto info1 = dto.getPriceInfos().get(0);
    info1.setProcessingType(2);

    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dto.getDronePortId());
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(entity);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dto.getDronePortId());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(null);
    statusEntity.setInactiveTime(null);
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モック設定(離着陸場予約情報)
    List<DronePortReserveInfoEntity> reserveList = new ArrayList<>();
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(reserveList);

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(new AircraftInfoEntity()));

    // モックを設定(料金情報：エラー)
    doThrow(new NullPointerException("上記以外の例外が発生")).when(priceInfoServiceImpl).process(any());

    // 実行確認
    NullPointerException exception =
        assertThrows(
            NullPointerException.class, () -> dronePortInfoServiceImpl.update(dto, userDto));
    assertTrue(exception.getMessage().contains("上記以外の例外が発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 離着陸場情報を削除する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 結果: エンティティの削除フラグがtrueに設定される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void delete_Normal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(userDto.getUserOperatorId());
    // モックを設定(離着陸場情報)
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(null);
    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dronePortId);
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    dronePortInfoServiceImpl.delete(dronePortId.toString(), userDto);

    // 離着陸場情報の登録データを検証
    {
      ArgumentCaptor<DronePortInfoEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortInfoEntity.class);
      verify(dronePortInfoRepository).save(entityCaptor.capture());
      DronePortInfoEntity argEntity = entityCaptor.getValue();
      assertEquals(dronePortId, argEntity.getDronePortId());
      assertTrue(argEntity.getDeleteFlag());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
    // 離着陸場状態の登録データを検証
    {
      ArgumentCaptor<DronePortStatusEntity> entityCaptor =
          ArgumentCaptor.forClass(DronePortStatusEntity.class);
      verify(dronePortStatusRepository).save(entityCaptor.capture());
      DronePortStatusEntity argEntity = entityCaptor.getValue();
      assertEquals(dronePortId, argEntity.getDronePortId());
      assertTrue(argEntity.getDeleteFlag());
      assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    }
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 離着陸場情報を削除する<br>
   * 条件: 離着陸場情報テーブルに該当レコードが存在しない<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void delete_NotFound1() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    String dronePortId = UUID.randomUUID().toString();
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.empty());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> dronePortInfoServiceImpl.delete(dronePortId.toString(), userDto));

    verify(dronePortInfoRepository, times(1)).findByDronePortIdAndDeleteFlagFalse(dronePortId);
    verify(dronePortStatusRepository, times(0)).findByDronePortIdAndDeleteFlagFalse(dronePortId);
    verify(dronePortInfoRepository, times(0)).save(any(DronePortInfoEntity.class));
    verify(dronePortStatusRepository, times(0)).save(any(DronePortStatusEntity.class));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 離着陸場情報を削除する<br>
   * 条件: 離着陸場状態テーブルに該当レコードが存在しない<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void delete_NotFound2() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    String dronePortId = UUID.randomUUID().toString();
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(new DronePortInfoEntity()));
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> dronePortInfoServiceImpl.delete(dronePortId.toString(), userDto));

    verify(dronePortInfoRepository, times(1)).findByDronePortIdAndDeleteFlagFalse(dronePortId);
    verify(dronePortStatusRepository, times(1)).findByDronePortIdAndDeleteFlagFalse(dronePortId);
    verify(dronePortInfoRepository, times(0)).save(any(DronePortInfoEntity.class));
    verify(dronePortStatusRepository, times(0)).save(any(DronePortStatusEntity.class));
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 料金情報を含む離着陸場情報削除の正常系テスト<br>
   * 条件: 料金情報1件<br>
   * 結果: 1件の料金情報エンティティの削除フラグがtrueに設定される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_Normal_onePriceInfo() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // モックを設定(離着陸場情報)
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(null);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dronePortId);
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(料金情報)
    String priceId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index);
    Range<LocalDateTime> rLocaldatetime =
        Range.localDateTimeRange(String.format("[%s,%s)", timedata, timedata));
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    PriceInfoEntity ent = new PriceInfoEntity();
    ent.setPriceId(UUID.fromString(priceId));
    ent.setResourceId("リソースID");
    ent.setResourceType(1);
    ent.setPriceType(4);
    ent.setPricePerUnit(1);
    ent.setPrice(1000);
    ent.setEffectiveTime(rLocaldatetime);
    ent.setPriority(1);
    ent.setOperatorId("ope01");
    ent.setUpdateUserId("user01");
    ent.setCreateTime(ts);
    ent.setUpdateTime(ts);
    ent.setDeleteFlag(false);
    List<PriceInfoEntity> list = new ArrayList<>();
    list.add(ent);
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(list);
    when(priceInfoRepository.save(any(PriceInfoEntity.class))).thenReturn(null);
    doNothing().when(priceInfoServiceImpl).registerPriceHistoryInfo(any());

    dronePortInfoServiceImpl.delete(dronePortId.toString(), userDto);

    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(1)).save(entityCaptor.capture());
    verify(priceInfoServiceImpl, times(1)).registerPriceHistoryInfo(entityCaptor.capture());
    PriceInfoEntity argEntity = entityCaptor.getValue();
    assertEquals(UUID.fromString(priceId), argEntity.getPriceId());
    assertEquals("リソースID", argEntity.getResourceId());
    assertEquals(1, argEntity.getResourceType());
    assertEquals(4, argEntity.getPriceType());
    assertEquals(1, argEntity.getPricePerUnit());
    assertEquals(1000, argEntity.getPrice());
    assertEquals(rLocaldatetime, argEntity.getEffectiveTime());
    assertEquals(1, argEntity.getPriority());
    assertEquals(userDto.getUserOperatorId(), argEntity.getOperatorId());
    assertEquals("user01", argEntity.getUpdateUserId());
    assertEquals(ts, argEntity.getCreateTime());
    assertNotEquals(ts, argEntity.getUpdateTime());
    assertTrue(argEntity.getDeleteFlag());
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 料金情報を含む離着陸場情報削除の正常系テスト<br>
   * 条件: 料金情報3件<br>
   * 結果: 3件の料金情報エンティティの削除フラグがtrueに設定される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_Normal_threePriceInfo() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // モックを設定(離着陸場情報)
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(null);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dronePortId);
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(料金情報)
    String priceId1 = "0a0711a5-ff74-4164-9309-8888b433cf22";
    String priceId2 = "1a0711a5-ff74-4164-9309-8888b433cf22";
    String priceId3 = "2a0711a5-ff74-4164-9309-8888b433cf22";
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index);
    Range<LocalDateTime> rLocaldatetime =
        Range.localDateTimeRange(String.format("[%s,%s)", timedata, timedata));
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    PriceInfoEntity ent1 = new PriceInfoEntity();
    List<PriceInfoEntity> list = new ArrayList<>();
    ent1.setPriceId(UUID.fromString(priceId1));
    ent1.setResourceId("リソースID");
    ent1.setResourceType(1);
    ent1.setPriceType(4);
    ent1.setPricePerUnit(1);
    ent1.setPrice(1000);
    ent1.setEffectiveTime(rLocaldatetime);
    ent1.setPriority(1);
    ent1.setOperatorId("ope01");
    ent1.setUpdateUserId("user01");
    ent1.setCreateTime(ts);
    ent1.setUpdateTime(ts);
    ent1.setDeleteFlag(false);
    list.add(ent1);
    PriceInfoEntity ent2 = new PriceInfoEntity();
    ent2.setPriceId(UUID.fromString(priceId2));
    ent2.setResourceId("リソースID");
    ent2.setResourceType(1);
    ent2.setPriceType(4);
    ent2.setPricePerUnit(1);
    ent2.setPrice(1000);
    ent2.setEffectiveTime(rLocaldatetime);
    ent2.setPriority(1);
    ent2.setOperatorId("ope01");
    ent2.setUpdateUserId("user01");
    ent2.setCreateTime(ts);
    ent2.setUpdateTime(ts);
    ent2.setDeleteFlag(false);
    list.add(ent2);
    PriceInfoEntity ent3 = new PriceInfoEntity();
    ent3.setPriceId(UUID.fromString(priceId3));
    ent3.setResourceId("リソースID");
    ent3.setResourceType(1);
    ent3.setPriceType(4);
    ent3.setPricePerUnit(1);
    ent3.setPrice(1000);
    ent3.setEffectiveTime(rLocaldatetime);
    ent3.setPriority(1);
    ent3.setOperatorId("ope01");
    ent3.setUpdateUserId("user01");
    ent3.setCreateTime(ts);
    ent3.setUpdateTime(ts);
    ent3.setDeleteFlag(false);
    list.add(ent3);
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(list);
    when(priceInfoRepository.save(any(PriceInfoEntity.class))).thenReturn(null);
    doNothing().when(priceInfoServiceImpl).registerPriceHistoryInfo(any());

    dronePortInfoServiceImpl.delete(dronePortId.toString(), userDto);

    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(3)).save(entityCaptor.capture());
    verify(priceInfoServiceImpl, times(3)).registerPriceHistoryInfo(entityCaptor.capture());
    List<PriceInfoEntity> argEntity = entityCaptor.getAllValues();
    assertEquals(UUID.fromString(priceId1), argEntity.get(0).getPriceId());
    assertEquals("リソースID", argEntity.get(0).getResourceId());
    assertEquals(1, argEntity.get(0).getResourceType());
    assertEquals(4, argEntity.get(0).getPriceType());
    assertEquals(1, argEntity.get(0).getPricePerUnit());
    assertEquals(1000, argEntity.get(0).getPrice());
    assertEquals(rLocaldatetime, argEntity.get(0).getEffectiveTime());
    assertEquals(1, argEntity.get(0).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(0).getOperatorId());
    assertEquals("user01", argEntity.get(0).getUpdateUserId());
    assertEquals(ts, argEntity.get(0).getCreateTime());
    assertNotEquals(ts, argEntity.get(0).getUpdateTime());
    assertTrue(argEntity.get(0).getDeleteFlag());

    assertEquals(UUID.fromString(priceId2), argEntity.get(1).getPriceId());
    assertEquals("リソースID", argEntity.get(1).getResourceId());
    assertEquals(1, argEntity.get(1).getResourceType());
    assertEquals(4, argEntity.get(1).getPriceType());
    assertEquals(1, argEntity.get(1).getPricePerUnit());
    assertEquals(1000, argEntity.get(1).getPrice());
    assertEquals(rLocaldatetime, argEntity.get(1).getEffectiveTime());
    assertEquals(1, argEntity.get(1).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(1).getOperatorId());
    assertEquals("user01", argEntity.get(1).getUpdateUserId());
    assertEquals(ts, argEntity.get(1).getCreateTime());
    assertNotEquals(ts, argEntity.get(1).getUpdateTime());
    assertTrue(argEntity.get(1).getDeleteFlag());

    assertEquals(UUID.fromString(priceId3), argEntity.get(2).getPriceId());
    assertEquals("リソースID", argEntity.get(2).getResourceId());
    assertEquals(1, argEntity.get(2).getResourceType());
    assertEquals(4, argEntity.get(2).getPriceType());
    assertEquals(1, argEntity.get(2).getPricePerUnit());
    assertEquals(1000, argEntity.get(2).getPrice());
    assertEquals(rLocaldatetime, argEntity.get(2).getEffectiveTime());
    assertEquals(1, argEntity.get(2).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(2).getOperatorId());
    assertEquals("user01", argEntity.get(2).getUpdateUserId());
    assertEquals(ts, argEntity.get(2).getCreateTime());
    assertNotEquals(ts, argEntity.get(2).getUpdateTime());
    assertTrue(argEntity.get(2).getDeleteFlag());
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 料金情報を含む離着陸場情報削除の正常系テスト<br>
   * 条件: 料金情報は空配列<br>
   * 結果: 0件の料金情報エンティティの削除フラグがtrueに設定される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_Normal_nullPriceInfo() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // モックを設定(離着陸場情報)
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(null);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dronePortId);
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(料金情報)
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

    dronePortInfoServiceImpl.delete(dronePortId.toString(), userDto);

    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(0)).save(entityCaptor.capture());
    verify(priceInfoServiceImpl, times(0)).registerPriceHistoryInfo(entityCaptor.capture());
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 離着陸場情報を削除する<br>
   * 条件: サービスで例外を発生させる<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_placeInfo_ServiceErrorException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // モックを設定(離着陸場情報)
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(null);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dronePortId);
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(料金情報)
    PriceInfoEntity ent = new PriceInfoEntity();
    ent.setPriceId(UUID.randomUUID());
    List<PriceInfoEntity> list = new ArrayList<>();
    list.add(ent);
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(list);
    when(priceInfoRepository.save(any(PriceInfoEntity.class))).thenReturn(null);

    // モックを設定(料金情報：エラー)
    doThrow(new ServiceErrorException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .registerPriceHistoryInfo(any());

    // 実行確認
    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> dronePortInfoServiceImpl.delete(dronePortId.toString(), userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(1)).save(entityCaptor.capture());
    verify(priceInfoServiceImpl, times(1)).registerPriceHistoryInfo(entityCaptor.capture());
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 離着陸場情報を削除する<br>
   * 条件: サービスで例外を発生させる<br>
   * 結果: NullPointerExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_placeInfo_NullPointerException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // モックを設定(離着陸場情報)
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(entity));
    when(dronePortInfoRepository.save(any(DronePortInfoEntity.class))).thenReturn(null);

    // モックを設定(離着陸場状態)
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setDronePortId(dronePortId);
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    when(dronePortStatusRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(statusEntity));
    when(dronePortStatusRepository.save(any(DronePortStatusEntity.class))).thenReturn(null);

    // モックを設定(料金情報)
    PriceInfoEntity ent = new PriceInfoEntity();
    ent.setPriceId(UUID.randomUUID());
    List<PriceInfoEntity> list = new ArrayList<>();
    list.add(ent);
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(list);
    when(priceInfoRepository.save(any(PriceInfoEntity.class))).thenReturn(null);

    // モックを設定(料金情報：エラー)
    doThrow(new NullPointerException("上記以外の例外が発生"))
        .when(priceInfoServiceImpl)
        .registerPriceHistoryInfo(any());

    // 実行確認
    NullPointerException exception =
        assertThrows(
            NullPointerException.class,
            () -> dronePortInfoServiceImpl.delete(dronePortId.toString(), userDto));
    assertTrue(exception.getMessage().contains("上記以外の例外が発生"));
    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(1)).save(entityCaptor.capture());
    verify(priceInfoServiceImpl, times(1)).registerPriceHistoryInfo(entityCaptor.capture());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場状態の更新日の方が、離着陸場情報の更新日より新しい場合<br>
   * 　　　　　動作状況が1:準備中で、使用不可日時範囲が未設定<br>
   * 　　　　　ソート条件なし<br>
   * 　　　　　ページ制御なし<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal1() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setActiveStatus("1,2,3,4");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(userDto.getUserOperatorId());
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(1);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertNull(element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
      assertEquals(statusEntity.getActiveStatus(), element.getActiveStatus());
      assertEquals(statusEntity.getStoredAircraftId().toString(), element.getStoredAircraftId());
      assertEquals(userDto.getUserOperatorId(), element.getOperatorId());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　ソート条件あり<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索実行日時が使用不可日時より前<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal2() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T02:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setSortOrders("0,1");
    dto.setSortColumns("a,b");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertEquals(statusEntity.getActiveStatus(), element.getActiveStatus()); // 現在の動作状況
      assertEquals(statusEntity.getInactiveStatus(), element.getScheduledStatus()); // 使用不可予定の動作状態
      assertEquals("2024-12-04T03:00:00Z", element.getInactiveTimeFrom()); // 使用不可予定の時間
      assertEquals("2024-12-04T04:00:00Z", element.getInactiveTimeTo()); // 使用不可予定の時間
    }

    // findAllのソート条件を検証
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
    verify(dronePortInfoRepository, times(1))
        .findAll(any(Specification.class), sortCaptor.capture());
    List<Order> orderList = sortCaptor.getValue().toList();
    assertTrue(orderList.get(0).isAscending());
    assertEquals("a", orderList.get(0).getProperty());
    assertTrue(orderList.get(1).isDescending());
    assertEquals("b", orderList.get(1).getProperty());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が3:使用不可で、使用不可日時範囲が開始のみ設定あり<br>
   * 　　　　　ソート条件あり(ソート順とソート対象列名の設定個数不整合)<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索実行日時が、使用不可日時範囲より前の場合<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal3() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T02:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setSortOrders(null);
    dto.setSortColumns("a,b");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    statusEntity.setInactiveTime(Range.localDateTimeRange("[2024-12-04T03:00:00,)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertEquals(DronePortConstants.ACTIVE_STATUS_AVAILABLE, element.getActiveStatus());
      assertEquals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE, element.getScheduledStatus());
      assertEquals("2024-12-04T03:00:00Z", element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が3:使用不可で、使用不可日時範囲が開始のみ設定あり<br>
   * 　　　　　ソート条件あり(ソート順とソート対象列名の設定個数不整合)<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索実行日時が、使用不可開始日時より後の場合<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal3_1() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T03:10:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setSortOrders(null);
    dto.setSortColumns("a,b");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    statusEntity.setInactiveTime(Range.localDateTimeRange("[2024-12-04T03:00:00,)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertEquals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE, element.getActiveStatus());
      assertNull(element.getScheduledStatus());
      assertEquals("2024-12-04T03:00:00Z", element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が3:使用不可で、使用不可日時範囲が開始のみ設定あり<br>
   * 　　　　　ソート条件あり(ソート順とソート対象列名の設定個数不整合)<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索実行日時が、使用不可開始日時と一致する場合<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal3_2() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T03:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setSortOrders(null);
    dto.setSortColumns("a,b");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    statusEntity.setInactiveTime(Range.localDateTimeRange("[2024-12-04T03:00:00,)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertEquals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE, element.getActiveStatus());
      assertNull(element.getScheduledStatus());
      assertEquals("2024-12-04T03:00:00Z", element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場状態の更新日の方が、離着陸場情報の更新日より新しい場合<br>
   * 　　　　　動作状況が1:準備中で、使用不可日時範囲が未設定<br>
   * 　　　　　ソート条件なし<br>
   * 　　　　　ページ制御あり<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal4() {
    long total = 20;
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    Integer intPerPage = 10;
    Integer intPage = 1;
    dto.setPerPage(intPerPage.toString());
    dto.setPage(intPage.toString());
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    entity.setDronePortStatusEntity(statusEntity);
    Page<DronePortInfoEntity> page =
        new PageImpl<>(
            List.of(entity), PageRequest.of(intPage - 1, intPerPage, Sort.unsorted()), total);
    when(dronePortInfoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertNull(element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
      assertNull(element.getScheduledStatus());
      assertEquals(statusEntity.getStoredAircraftId().toString(), element.getStoredAircraftId());
      assertEquals(statusEntity.getActiveStatus(), element.getActiveStatus());
      assertNull(element.getScheduledStatus());
      assertNull(element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
    }
    log.info(response.toString());
    assertEquals(dto.getPerPage(), response.getPerPage().toString());
    assertEquals(dto.getPage(), response.getCurrentPage().toString());
    assertEquals((int) total / intPerPage, response.getLastPage());
    assertEquals(total, (long) response.getTotal());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　ソート条件あり<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索実行日時が、使用不可日時範囲内の場合<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal5() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T03:10:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setSortOrders("0,1");
    dto.setSortColumns("a,b");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertEquals(DronePortConstants.ACTIVE_STATUS_MAINTENANCE, element.getActiveStatus());
      assertNull(element.getScheduledStatus());
      assertEquals("2024-12-04T03:00:00Z", element.getInactiveTimeFrom());
      assertEquals("2024-12-04T04:00:00Z", element.getInactiveTimeTo());
    }

    // findAllのソート条件を検証
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
    verify(dronePortInfoRepository, times(1))
        .findAll(any(Specification.class), sortCaptor.capture());
    List<Order> orderList = sortCaptor.getValue().toList();
    assertTrue(orderList.get(0).isAscending());
    assertEquals("a", orderList.get(0).getProperty());
    assertTrue(orderList.get(1).isDescending());
    assertEquals("b", orderList.get(1).getProperty());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　ソート条件あり<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索実行日時が、使用不可日時範囲外(開始より前)の場合<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal5_1() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T02:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setSortOrders("0,1");
    dto.setSortColumns("a,b");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertEquals(DronePortConstants.ACTIVE_STATUS_PREPARING, element.getActiveStatus());
      assertEquals(DronePortConstants.ACTIVE_STATUS_MAINTENANCE, element.getScheduledStatus());
      assertEquals("2024-12-04T03:00:00Z", element.getInactiveTimeFrom());
      assertEquals("2024-12-04T04:00:00Z", element.getInactiveTimeTo());
    }

    // findAllのソート条件を検証
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
    verify(dronePortInfoRepository, times(1))
        .findAll(any(Specification.class), sortCaptor.capture());
    List<Order> orderList = sortCaptor.getValue().toList();
    assertTrue(orderList.get(0).isAscending());
    assertEquals("a", orderList.get(0).getProperty());
    assertTrue(orderList.get(1).isDescending());
    assertEquals("b", orderList.get(1).getProperty());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　ソート条件あり<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索実行日時が、使用不可日時範囲外(終了より後)の場合<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal5_2() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T04:10:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setSortOrders("0,1");
    dto.setSortColumns("a,b");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertEquals(DronePortConstants.ACTIVE_STATUS_PREPARING, element.getActiveStatus());
      assertNull(element.getScheduledStatus());
      assertNull(element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
    }

    // findAllのソート条件を検証
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
    verify(dronePortInfoRepository, times(1))
        .findAll(any(Specification.class), sortCaptor.capture());
    List<Order> orderList = sortCaptor.getValue().toList();
    assertTrue(orderList.get(0).isAscending());
    assertEquals("a", orderList.get(0).getProperty());
    assertTrue(orderList.get(1).isDescending());
    assertEquals("b", orderList.get(1).getProperty());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　ソート条件あり<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索実行日時が、使用不可開始日時と一致する場合<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal5_3() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T03:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setSortOrders("0,1");
    dto.setSortColumns("a,b");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertEquals(DronePortConstants.ACTIVE_STATUS_MAINTENANCE, element.getActiveStatus());
      assertNull(element.getScheduledStatus());
      assertEquals("2024-12-04T03:00:00Z", element.getInactiveTimeFrom());
      assertEquals("2024-12-04T04:00:00Z", element.getInactiveTimeTo());
    }

    // findAllのソート条件を検証
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
    verify(dronePortInfoRepository, times(1))
        .findAll(any(Specification.class), sortCaptor.capture());
    List<Order> orderList = sortCaptor.getValue().toList();
    assertTrue(orderList.get(0).isAscending());
    assertEquals("a", orderList.get(0).getProperty());
    assertTrue(orderList.get(1).isDescending());
    assertEquals("b", orderList.get(1).getProperty());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　ソート条件あり<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索実行日時が、使用不可終了日時と一致する場合<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal5_4() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T04:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setSortOrders("0,1");
    dto.setSortColumns("a,b");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(entity));

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertEquals(DronePortConstants.ACTIVE_STATUS_PREPARING, element.getActiveStatus());
      assertNull(element.getScheduledStatus());
      assertNull(element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
    }

    // findAllのソート条件を検証
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
    verify(dronePortInfoRepository, times(1))
        .findAll(any(Specification.class), sortCaptor.capture());
    List<Order> orderList = sortCaptor.getValue().toList();
    assertTrue(orderList.get(0).isAscending());
    assertEquals("a", orderList.get(0).getProperty());
    assertTrue(orderList.get(1).isDescending());
    assertEquals("b", orderList.get(1).getProperty());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　料金情報要否がtrue<br>
   * 　　　　　離着陸場情報0件<br>
   * 結果: 離着陸場情報のリストが空で返され、料金情報サービスが呼ばれない<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal6_ドローン情報0件() {
    // リクエストDTO作成
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setActiveStatus("1,2,3,4");
    dto.setIsRequiredPriceInfo("true");

    // 離着陸場情報0件
    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertNotNull(response.getData());
    assertEquals(0, response.getData().size());
    assertTrue(response.getData().isEmpty());

    // 料金情報サービスが呼ばれていないことを確認
    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchService, never()).getPriceInfoList(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　料金情報要否がtrue<br>
   * 　　　　　離着陸場情報1件<br>
   * 　　　　　料金情報1件<br>
   * 結果: 離着陸場情報のリストが返され、料金情報が含まれている<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal6_料金情報1件() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String operatorId = "dummyOperator";
    String dronePortId = UUID.randomUUID().toString();

    // リクエストDTO作成
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setActiveStatus("1,2,3,4");
    dto.setIsRequiredPriceInfo("true");

    // 離着陸場情報エンティティ作成
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(operatorId);

    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(1);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    statusEntity.setOperatorId(operatorId);
    entity.setDronePortStatusEntity(statusEntity);

    // 料金情報レスポンスDTO作成
    PriceInfoSearchListDetailElement priceDetailElement = new PriceInfoSearchListDetailElement();
    priceDetailElement.setPriceId(UUID.randomUUID().toString());
    priceDetailElement.setPriceType(4);
    priceDetailElement.setPricePerUnit(1);
    priceDetailElement.setPrice(1000);
    priceDetailElement.setPriority(1);
    priceDetailElement.setOperatorId(operatorId);
    priceDetailElement.setEffectiveStartTime("2025-11-13T10:00:00Z");
    priceDetailElement.setEffectiveEndTime("2025-11-13T10:00:00Z");

    PriceInfoSearchListElement priceElement = new PriceInfoSearchListElement();
    priceElement.setResourceId(dronePortId);
    priceElement.setResourceType(1);
    priceElement.setPriceInfos(List.of(priceDetailElement));

    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(List.of(priceElement));

    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertNull(element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
      assertEquals(statusEntity.getActiveStatus(), element.getActiveStatus());
      assertEquals(statusEntity.getStoredAircraftId().toString(), element.getStoredAircraftId());
      assertEquals(operatorId, element.getOperatorId());

      // 料金情報の検証
      assertNotNull(element.getPriceInfos());
      assertEquals(1, element.getPriceInfos().size());
      assertEquals(priceDetailElement.getPriceId(), element.getPriceInfos().get(0).getPriceId());
      assertEquals(
          priceDetailElement.getPriceType(), element.getPriceInfos().get(0).getPriceType());
      assertEquals(
          priceDetailElement.getPricePerUnit(), element.getPriceInfos().get(0).getPricePerUnit());
      assertEquals(priceDetailElement.getPrice(), element.getPriceInfos().get(0).getPrice());
      assertEquals(priceDetailElement.getPriority(), element.getPriceInfos().get(0).getPriority());
      assertEquals(
          priceDetailElement.getEffectiveStartTime(),
          element.getPriceInfos().get(0).getEffectiveStartTime());
      assertEquals(
          priceDetailElement.getEffectiveEndTime(),
          element.getPriceInfos().get(0).getEffectiveEndTime());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　料金情報要否がtrue<br>
   * 　　　　　離着陸場情報1件<br>
   * 　　　　　料金情報0件<br>
   * 結果: 離着陸場情報のリストが返され、料金情報が空である<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal6_料金情報0件() {
    String operatorId = "dummyOperator";
    String dronePortId = UUID.randomUUID().toString();

    // リクエストDTO作成
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setActiveStatus("1,2,3,4");
    dto.setIsRequiredPriceInfo("true");

    // 離着陸場情報エンティティ作成
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(operatorId);

    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(1);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    statusEntity.setOperatorId(operatorId);
    entity.setDronePortStatusEntity(statusEntity);

    // 料金情報レスポンスDTO作成（空のリスト）
    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(new ArrayList<>());

    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertNull(element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
      assertEquals(statusEntity.getActiveStatus(), element.getActiveStatus());
      assertEquals(statusEntity.getStoredAircraftId().toString(), element.getStoredAircraftId());
      assertEquals(operatorId, element.getOperatorId());

      // 料金情報の検証（空であること）
      assertNotNull(element.getPriceInfos());
      assertEquals(0, element.getPriceInfos().size());
      assertTrue(element.getPriceInfos().isEmpty());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　料金情報要否がtrue<br>
   * 　　　　　離着陸場情報1件<br>
   * 　　　　　料金情報3件<br>
   * 結果: 離着陸場情報のリストが返され、料金情報が3件含まれていること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal6_料金情報複数件() {
    String operatorId = "dummyOperator";
    String dronePortId1 = UUID.randomUUID().toString();
    String dronePortId2 = UUID.randomUUID().toString();
    String dronePortId3 = UUID.randomUUID().toString();

    // リクエストDTO作成
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setActiveStatus("1,2,3,4");
    dto.setIsRequiredPriceInfo("true");

    // 離着陸場情報エンティティ作成（3件）
    DronePortInfoEntity entity1 = new DronePortInfoEntity();
    entity1.setDronePortId(dronePortId1);
    entity1.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity1.setOperatorId(operatorId);

    DronePortStatusEntity statusEntity1 = new DronePortStatusEntity();
    statusEntity1.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity1.setDronePortId(entity1.getDronePortId());
    statusEntity1.setActiveStatus(1);
    statusEntity1.setStoredAircraftId(UUID.randomUUID());
    statusEntity1.setOperatorId(operatorId);
    entity1.setDronePortStatusEntity(statusEntity1);

    DronePortInfoEntity entity2 = new DronePortInfoEntity();
    entity2.setDronePortId(dronePortId2);
    entity2.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T03:00:00Z")));
    entity2.setOperatorId(operatorId);

    DronePortStatusEntity statusEntity2 = new DronePortStatusEntity();
    statusEntity2.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T04:00:00Z")));
    statusEntity2.setDronePortId(entity2.getDronePortId());
    statusEntity2.setActiveStatus(2);
    statusEntity2.setStoredAircraftId(UUID.randomUUID());
    statusEntity2.setOperatorId(operatorId);
    entity2.setDronePortStatusEntity(statusEntity2);

    DronePortInfoEntity entity3 = new DronePortInfoEntity();
    entity3.setDronePortId(dronePortId3);
    entity3.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T05:00:00Z")));
    entity3.setOperatorId(operatorId);

    DronePortStatusEntity statusEntity3 = new DronePortStatusEntity();
    statusEntity3.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T06:00:00Z")));
    statusEntity3.setDronePortId(entity3.getDronePortId());
    statusEntity3.setActiveStatus(3);
    statusEntity3.setStoredAircraftId(UUID.randomUUID());
    statusEntity3.setOperatorId(operatorId);
    entity3.setDronePortStatusEntity(statusEntity3);

    // 料金情報レスポンスDTO作成
    // 離着陸場1: 料金情報0件
    // レスポンスにリソースがなし

    // 離着陸場2: 料金情報1件
    PriceInfoSearchListDetailElement priceDetailElement2_1 = new PriceInfoSearchListDetailElement();
    priceDetailElement2_1.setPriceId(UUID.randomUUID().toString());
    priceDetailElement2_1.setPriceType(4);
    priceDetailElement2_1.setPricePerUnit(1);
    priceDetailElement2_1.setPrice(1000);
    priceDetailElement2_1.setPriority(1);
    priceDetailElement2_1.setOperatorId(operatorId);
    priceDetailElement2_1.setEffectiveStartTime("2025-11-13T10:00:00Z");
    priceDetailElement2_1.setEffectiveEndTime("2025-11-13T12:00:00Z");

    PriceInfoSearchListElement priceElement2 = new PriceInfoSearchListElement();
    priceElement2.setResourceId(dronePortId2);
    priceElement2.setResourceType(1);
    priceElement2.setPriceInfos(List.of(priceDetailElement2_1));

    // 離着陸場3: 料金情報3件
    PriceInfoSearchListDetailElement priceDetailElement3_1 = new PriceInfoSearchListDetailElement();
    priceDetailElement3_1.setPriceId(UUID.randomUUID().toString());
    priceDetailElement3_1.setPriceType(4);
    priceDetailElement3_1.setPricePerUnit(1);
    priceDetailElement3_1.setPrice(2000);
    priceDetailElement3_1.setPriority(1);
    priceDetailElement3_1.setOperatorId(operatorId);
    priceDetailElement3_1.setEffectiveStartTime("2025-11-13T12:00:00Z");
    priceDetailElement3_1.setEffectiveEndTime("2025-11-13T14:00:00Z");

    PriceInfoSearchListDetailElement priceDetailElement3_2 = new PriceInfoSearchListDetailElement();
    priceDetailElement3_2.setPriceId(UUID.randomUUID().toString());
    priceDetailElement3_2.setPriceType(4);
    priceDetailElement3_2.setPricePerUnit(1);
    priceDetailElement3_2.setPrice(3000);
    priceDetailElement3_2.setPriority(2);
    priceDetailElement3_2.setOperatorId(operatorId);
    priceDetailElement3_2.setEffectiveStartTime("2025-11-13T14:00:00Z");
    priceDetailElement3_2.setEffectiveEndTime("2025-11-13T16:00:00Z");

    PriceInfoSearchListDetailElement priceDetailElement3_3 = new PriceInfoSearchListDetailElement();
    priceDetailElement3_3.setPriceId(UUID.randomUUID().toString());
    priceDetailElement3_3.setPriceType(4);
    priceDetailElement3_3.setPricePerUnit(1);
    priceDetailElement3_3.setPrice(4000);
    priceDetailElement3_3.setPriority(3);
    priceDetailElement3_3.setOperatorId(operatorId);
    priceDetailElement3_3.setEffectiveStartTime("2025-11-13T16:00:00Z");
    priceDetailElement3_3.setEffectiveEndTime("2025-11-13T18:00:00Z");

    PriceInfoSearchListElement priceElement3 = new PriceInfoSearchListElement();
    priceElement3.setResourceId(dronePortId3);
    priceElement3.setResourceType(1);
    priceElement3.setPriceInfos(
        List.of(priceDetailElement3_1, priceDetailElement3_2, priceDetailElement3_3));

    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(List.of(priceElement2, priceElement3));

    when(dronePortInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity1, entity2, entity3));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    DronePortInfoListResponseDto response = dronePortInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(3, response.getData().size());

    // 離着陸場1の検証（料金情報0件）
    {
      DronePortInfoListResponseElement element = response.getData().get(0);
      assertEquals(entity1.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T02:00:00Z", element.getUpdateTime());
      assertNull(element.getInactiveTimeFrom());
      assertNull(element.getInactiveTimeTo());
      assertEquals(statusEntity1.getActiveStatus(), element.getActiveStatus());
      assertEquals(statusEntity1.getStoredAircraftId().toString(), element.getStoredAircraftId());
      assertEquals(operatorId, element.getOperatorId());

      // 料金情報の検証（0件）
      assertNotNull(element.getPriceInfos());
      assertTrue(element.getPriceInfos().isEmpty());
    }

    // 離着陸場2の検証（料金情報1件）
    {
      DronePortInfoListResponseElement element = response.getData().get(1);
      assertEquals(entity2.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T04:00:00Z", element.getUpdateTime());
      assertEquals(statusEntity2.getActiveStatus(), element.getActiveStatus());
      assertEquals(statusEntity2.getStoredAircraftId().toString(), element.getStoredAircraftId());
      assertEquals(operatorId, element.getOperatorId());

      // 料金情報の検証（1件）
      assertNotNull(element.getPriceInfos());
      assertEquals(1, element.getPriceInfos().size());
      assertEquals(priceDetailElement2_1.getPriceId(), element.getPriceInfos().get(0).getPriceId());
      assertEquals(
          priceDetailElement2_1.getPriceType(), element.getPriceInfos().get(0).getPriceType());
      assertEquals(
          priceDetailElement2_1.getPricePerUnit(),
          element.getPriceInfos().get(0).getPricePerUnit());
      assertEquals(priceDetailElement2_1.getPrice(), element.getPriceInfos().get(0).getPrice());
      assertEquals(
          priceDetailElement2_1.getPriority(), element.getPriceInfos().get(0).getPriority());
      assertEquals(
          priceDetailElement2_1.getEffectiveStartTime(),
          element.getPriceInfos().get(0).getEffectiveStartTime());
      assertEquals(
          priceDetailElement2_1.getEffectiveEndTime(),
          element.getPriceInfos().get(0).getEffectiveEndTime());
    }

    // 離着陸場3の検証（料金情報3件）
    {
      DronePortInfoListResponseElement element = response.getData().get(2);
      assertEquals(entity3.getDronePortId(), element.getDronePortId());
      assertEquals("2024-12-04T06:00:00Z", element.getUpdateTime());
      assertEquals(statusEntity3.getActiveStatus(), element.getActiveStatus());
      assertEquals(statusEntity3.getStoredAircraftId().toString(), element.getStoredAircraftId());
      assertEquals(operatorId, element.getOperatorId());

      // 料金情報の検証（3件）
      assertNotNull(element.getPriceInfos());
      assertEquals(3, element.getPriceInfos().size());

      // 1件目の検証
      assertEquals(priceDetailElement3_1.getPriceId(), element.getPriceInfos().get(0).getPriceId());
      assertEquals(
          priceDetailElement3_1.getPriceType(), element.getPriceInfos().get(0).getPriceType());
      assertEquals(
          priceDetailElement3_1.getPricePerUnit(),
          element.getPriceInfos().get(0).getPricePerUnit());
      assertEquals(priceDetailElement3_1.getPrice(), element.getPriceInfos().get(0).getPrice());
      assertEquals(
          priceDetailElement3_1.getPriority(), element.getPriceInfos().get(0).getPriority());
      assertEquals(
          priceDetailElement3_1.getEffectiveStartTime(),
          element.getPriceInfos().get(0).getEffectiveStartTime());
      assertEquals(
          priceDetailElement3_1.getEffectiveEndTime(),
          element.getPriceInfos().get(0).getEffectiveEndTime());

      // 2件目の検証
      assertEquals(priceDetailElement3_2.getPriceId(), element.getPriceInfos().get(1).getPriceId());
      assertEquals(
          priceDetailElement3_2.getPriceType(), element.getPriceInfos().get(1).getPriceType());
      assertEquals(
          priceDetailElement3_2.getPricePerUnit(),
          element.getPriceInfos().get(1).getPricePerUnit());
      assertEquals(priceDetailElement3_2.getPrice(), element.getPriceInfos().get(1).getPrice());
      assertEquals(
          priceDetailElement3_2.getPriority(), element.getPriceInfos().get(1).getPriority());

      // 3件目の検証
      assertEquals(priceDetailElement3_3.getPriceId(), element.getPriceInfos().get(2).getPriceId());
      assertEquals(
          priceDetailElement3_3.getPriceType(), element.getPriceInfos().get(2).getPriceType());
      assertEquals(
          priceDetailElement3_3.getPricePerUnit(),
          element.getPriceInfos().get(2).getPricePerUnit());
      assertEquals(priceDetailElement3_3.getPrice(), element.getPriceInfos().get(2).getPrice());
      assertEquals(
          priceDetailElement3_3.getPriority(), element.getPriceInfos().get(2).getPriority());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 公開可の条件で自事業者の離着陸場一覧を取得できること<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　自事業者リクエストでの一覧取得<br>
   * 　　　　　公開可否フラグ条件：true<br>
   * 　　　　　ソート条件なし<br>
   * 　　　　　ページ制御なし<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal_OwnOpe() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setPublicFlag("true");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(userDto.getUserOperatorId());
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(1);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));

    dronePortInfoServiceImpl.getList(dto, userDto);
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 公開可の条件で他事業者の離着陸場一覧を取得できること<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　他事業者リクエストでの一覧取得<br>
   * 　　　　　公開可否フラグ条件：true<br>
   * 　　　　　ソート条件なし<br>
   * 　　　　　ページ制御なし<br>
   * 結果: 離着陸場情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal_OtherOpe() {
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setPublicFlag("true");
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(UUID.randomUUID().toString());
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(userDto.getUserOperatorId());
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(1);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));

    dronePortInfoServiceImpl.getList(dto, userDto);
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　料金情報要否がtrue<br>
   * 　　　　　料金情報サービスでValidationErrorExceptionが発生<br>
   * 結果: ValidationErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Error_料金情報でバリデーションエラー() {
    String operatorId = "dummyOperator";
    String dronePortId = UUID.randomUUID().toString();

    // リクエストDTO作成
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setActiveStatus("1,2,3,4");
    dto.setIsRequiredPriceInfo("true");

    // 離着陸場情報エンティティ作成
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(operatorId);

    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(1);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    statusEntity.setOperatorId(operatorId);
    entity.setDronePortStatusEntity(statusEntity);

    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any()))
        .thenThrow(new ValidationErrorException("ValidationErrorException"));

    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class,
            () -> {
              dronePortInfoServiceImpl.getList(dto, userDto);
            });

    assertEquals("ValidationErrorException", exception.getMessage());
    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchService, times(1)).getPriceInfoList(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 離着陸場情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　料金情報要否がtrue<br>
   * 　　　　　料金情報サービスでServiceErrorExceptionが発生<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Error_料金情報でサービスエラー() {
    String operatorId = "dummyOperator";
    String dronePortId = UUID.randomUUID().toString();

    // リクエストDTO作成
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
    dto.setActiveStatus("1,2,3,4");
    dto.setIsRequiredPriceInfo("true");

    // 離着陸場情報エンティティ作成
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(operatorId);

    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(1);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    statusEntity.setOperatorId(operatorId);
    entity.setDronePortStatusEntity(statusEntity);

    when(dronePortInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any()))
        .thenThrow(new ServiceErrorException("ServiceErrorException"));

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> {
              dronePortInfoServiceImpl.getList(dto, userDto);
            });

    assertEquals("ServiceErrorException", exception.getMessage());
    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchService, times(1)).getPriceInfoList(any());
  }

  private DronePortInfoEntity createDronePortInfoEntity() {
    DronePortInfoEntity entity = new DronePortInfoEntity();
    entity.setDronePortName("dummyDronePortName");
    entity.setAddress("dummyAddress");
    entity.setManufacturer("製造メーカー");
    entity.setSerialNumber("シリアル番号");
    entity.setPortType(2);
    entity.setLat(12.3d);
    entity.setLon(23.4d);
    entity.setAlt(34d);
    entity.setSupportDroneType("対応機体");
    entity.setImageBinary("testbinary".getBytes());
    entity.setVisDronePortCompanyId("dummyDronePortCompanyId");

    return entity;
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　離着陸場状態の更新日の方が、離着陸場情報の更新日より新しい場合<br>
   * 　　　　　動作状況が1:準備中で、使用不可日時範囲が未設定<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal1() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(userDto.getUserOperatorId());
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals("2024-12-04T02:00:00Z", response.getUpdateTime());
    assertEquals(statusEntity.getStoredAircraftId().toString(), response.getStoredAircraftId());
    assertEquals(statusEntity.getActiveStatus(), response.getActiveStatus());
    assertNull(response.getScheduledStatus());
    assertNull(response.getInactiveTimeFrom());
    assertNull(response.getInactiveTimeTo());
    assertEquals(userDto.getUserOperatorId(), response.getOperatorId());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　検索実行日時が、使用不可開始日時より前の場合<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal2() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T02:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals(DronePortConstants.ACTIVE_STATUS_AVAILABLE, response.getActiveStatus());
    assertEquals(DronePortConstants.ACTIVE_STATUS_MAINTENANCE, response.getScheduledStatus());
    assertEquals("2024-12-04T03:00:00Z", response.getInactiveTimeFrom());
    assertEquals("2024-12-04T04:00:00Z", response.getInactiveTimeTo());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　検索実行日時が、使用不可日時範囲内の場合<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal2_1() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T03:10:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals(DronePortConstants.ACTIVE_STATUS_MAINTENANCE, response.getActiveStatus());
    assertNull(response.getScheduledStatus());
    assertEquals("2024-12-04T03:00:00Z", response.getInactiveTimeFrom());
    assertEquals("2024-12-04T04:00:00Z", response.getInactiveTimeTo());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　検索実行日時が、使用不可終了日時より後の場合<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal2_2() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T04:10:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals(DronePortConstants.ACTIVE_STATUS_AVAILABLE, response.getActiveStatus());
    assertNull(response.getScheduledStatus());
    assertNull(response.getInactiveTimeFrom());
    assertNull(response.getInactiveTimeTo());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　検索実行日時が、使用不可開始日時と一致する場合<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal2_3() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T03:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals(DronePortConstants.ACTIVE_STATUS_MAINTENANCE, response.getActiveStatus());
    assertNull(response.getScheduledStatus());
    assertEquals("2024-12-04T03:00:00Z", response.getInactiveTimeFrom());
    assertEquals("2024-12-04T04:00:00Z", response.getInactiveTimeTo());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が4:メンテナンス中で、使用不可日時範囲が開始,終了の両方設定あり<br>
   * 　　　　　検索実行日時が、使用不可終了日時と一致する場合<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal2_4() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T04:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    statusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T03:00:00,2024-12-04T04:00:00)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals(DronePortConstants.ACTIVE_STATUS_AVAILABLE, response.getActiveStatus());
    assertNull(response.getScheduledStatus());
    assertNull(response.getInactiveTimeFrom());
    assertNull(response.getInactiveTimeTo());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が3:使用不可で、使用不可日時範囲が開始のみ設定あり<br>
   * 　　　　　検索実行日時が、使用不可開始日時より前の場合<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal3() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T02:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    statusEntity.setInactiveTime(Range.localDateTimeRange("[2024-12-04T03:00:00,)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals(DronePortConstants.ACTIVE_STATUS_PREPARING, response.getActiveStatus());
    assertEquals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE, response.getScheduledStatus());
    assertEquals("2024-12-04T03:00:00Z", response.getInactiveTimeFrom());
    assertNull(response.getInactiveTimeTo());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が3:使用不可で、使用不可日時範囲が開始のみ設定あり<br>
   * 　　　　　検索実行日時が、使用不可開始日時より後の場合<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal3_1() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T03:10:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    statusEntity.setInactiveTime(Range.localDateTimeRange("[2024-12-04T03:00:00,)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE, response.getActiveStatus());
    assertNull(response.getScheduledStatus());
    assertEquals("2024-12-04T03:00:00Z", response.getInactiveTimeFrom());
    assertNull(response.getInactiveTimeTo());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　離着陸場情報の更新日の方が、離着陸場状態の更新日より新しい場合<br>
   * 　　　　　動作状況が3:使用不可で、使用不可日時範囲が開始のみ設定あり<br>
   * 　　　　　検索実行日時が、使用不可開始日時に一致する場合<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal3_2() {
    LocalDateTime currentTime = toUtcLocalDateTime("2024-12-04T03:00:00Z");
    localDateTimeMock.when(LocalDateTime::now).thenReturn(currentTime);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    statusEntity.setInactiveTime(Range.localDateTimeRange("[2024-12-04T03:00:00,)"));
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE, response.getActiveStatus());
    assertNull(response.getScheduledStatus());
    assertEquals("2024-12-04T03:00:00Z", response.getInactiveTimeFrom());
    assertNull(response.getInactiveTimeTo());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す。離着陸場情報の画像データ未設定。<br>
   * 結果: 離着陸場情報の詳細が返され、画像データがnullである<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal_ImageEmpty() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setImageBinary(new byte[] {});
    entity.setDronePortId(dronePortId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertNull(response.getImageData());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す。離着陸場情報の画像データ未設定。<br>
   * 結果: 離着陸場情報の詳細が返され、画像データがnullである<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal_ImageNull() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setImageBinary(null);
    entity.setDronePortId(dronePortId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertNull(response.getImageData());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 他事業者のオペレータID<br>
   * 結果: 離着陸場情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal_OtherOpe() {
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    String dronePortId = UUID.randomUUID().toString();
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setDronePortId(dronePortId);
    entity.setImageFormat("png");
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(userDto.getUserOperatorId());
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setStoredAircraftId(UUID.randomUUID());
    statusEntity.setOperatorId(userDto.getUserOperatorId());
    entity.setDronePortStatusEntity(statusEntity);
    when(dronePortInfoRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto);

    assertNotNull(response);
    assertEquals(dronePortId.toString(), response.getDronePortId());
    assertEquals(entity.getDronePortName(), response.getDronePortName());
    assertEquals(entity.getAddress(), response.getAddress());
    assertEquals(entity.getManufacturer(), response.getManufacturer());
    assertEquals(entity.getSerialNumber(), response.getSerialNumber());
    assertEquals(entity.getPortType(), response.getPortType());
    assertEquals(entity.getLat(), response.getLat());
    assertEquals(entity.getLon(), response.getLon());
    assertEquals(entity.getAlt().intValue(), response.getAlt());
    assertEquals(entity.getSupportDroneType(), response.getSupportDroneType());
    assertEquals(
        "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getImageBinary()),
        response.getImageData());
    assertEquals("2024-12-04T02:00:00Z", response.getUpdateTime());
    assertEquals(statusEntity.getStoredAircraftId().toString(), response.getStoredAircraftId());
    assertEquals(statusEntity.getActiveStatus(), response.getActiveStatus());
    assertNull(response.getScheduledStatus());
    assertNull(response.getInactiveTimeFrom());
    assertNull(response.getInactiveTimeTo());
    assertEquals(userDto.getUserOperatorId(), response.getOperatorId());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 存在しない離着陸場IDを渡す<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_NotFound() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID dronePortId = UUID.randomUUID();
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> dronePortInfoServiceImpl.getDetail(dronePortId.toString(), false, userDto));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　料金情報要否がtrue<br>
   * 　　　　　料金情報1件<br>
   * 結果: 離着陸場情報の詳細が返され、料金情報が1件含まれる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getDetail_Normal4_料金情報1件() {
    String operatorId = "dummyOperator";
    String dronePortId = UUID.randomUUID().toString();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // 離着陸場情報エンティティ作成
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setImageBinary(null);
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(operatorId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));

    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setOperatorId(operatorId);
    entity.setDronePortStatusEntity(statusEntity);

    // 料金情報レスポンスDTO作成
    PriceInfoSearchListDetailElement priceDetailElement = new PriceInfoSearchListDetailElement();
    priceDetailElement.setPriceId(UUID.randomUUID().toString());
    priceDetailElement.setPriceType(4);
    priceDetailElement.setPricePerUnit(1);
    priceDetailElement.setPrice(1000);
    priceDetailElement.setPriority(1);
    priceDetailElement.setOperatorId(operatorId);
    priceDetailElement.setEffectiveStartTime("2025-11-13T10:00:00Z");
    priceDetailElement.setEffectiveEndTime("2025-11-13T10:00:00Z");

    PriceInfoSearchListElement priceElement = new PriceInfoSearchListElement();
    priceElement.setResourceId(dronePortId);
    priceElement.setResourceType(1);
    priceElement.setPriceInfos(List.of(priceDetailElement));

    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(List.of(priceElement));

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId, true, userDto);

    assertNotNull(response);
    assertEquals(dronePortId, response.getDronePortId());

    // 料金情報の検証
    assertNotNull(response.getPriceInfos());
    assertEquals(1, response.getPriceInfos().size());
    assertEquals(priceDetailElement.getPriceId(), response.getPriceInfos().get(0).getPriceId());
    assertEquals(priceDetailElement.getPriceType(), response.getPriceInfos().get(0).getPriceType());
    assertEquals(
        priceDetailElement.getPricePerUnit(), response.getPriceInfos().get(0).getPricePerUnit());
    assertEquals(priceDetailElement.getPrice(), response.getPriceInfos().get(0).getPrice());
    assertEquals(priceDetailElement.getPriority(), response.getPriceInfos().get(0).getPriority());
    assertEquals(
        priceDetailElement.getEffectiveStartTime(),
        response.getPriceInfos().get(0).getEffectiveStartTime());
    assertEquals(
        priceDetailElement.getEffectiveEndTime(),
        response.getPriceInfos().get(0).getEffectiveEndTime());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　料金情報要否がtrue<br>
   * 　　　　　料金情報0件<br>
   * 結果: 離着陸場情報の詳細が返され、料金情報が空である<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getDetail_Normal4_料金情報0件() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String operatorId = "dummyOperator";
    String dronePortId = UUID.randomUUID().toString();

    // 離着陸場情報エンティティ作成
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setImageBinary(null);
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(operatorId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));

    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setOperatorId(operatorId);
    entity.setDronePortStatusEntity(statusEntity);

    // 料金情報レスポンスDTO作成（空のリスト）
    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(new ArrayList<>());

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId, true, userDto);

    assertNotNull(response);
    assertEquals(dronePortId, response.getDronePortId());

    // 料金情報の検証（空であること）
    assertNotNull(response.getPriceInfos());
    assertEquals(0, response.getPriceInfos().size());
    assertTrue(response.getPriceInfos().isEmpty());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 　　　　　料金情報要否がtrue<br>
   * 　　　　　料金情報3件<br>
   * 結果: 離着陸場情報の詳細が返され、料金情報が3件含まれる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getDetail_Normal4_料金情報複数件() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String operatorId = "dummyOperator";
    String dronePortId = UUID.randomUUID().toString();

    // 離着陸場情報エンティティ作成
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setImageBinary(null);
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(operatorId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));

    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setOperatorId(operatorId);
    entity.setDronePortStatusEntity(statusEntity);

    // 料金情報レスポンスDTO作成（3件）
    PriceInfoSearchListDetailElement priceDetailElement1 = new PriceInfoSearchListDetailElement();
    priceDetailElement1.setPriceId(UUID.randomUUID().toString());
    priceDetailElement1.setPriceType(4);
    priceDetailElement1.setPricePerUnit(1);
    priceDetailElement1.setPrice(1000);
    priceDetailElement1.setPriority(1);
    priceDetailElement1.setOperatorId(operatorId);
    priceDetailElement1.setEffectiveStartTime("2025-11-13T10:00:00Z");
    priceDetailElement1.setEffectiveEndTime("2025-11-13T12:00:00Z");

    PriceInfoSearchListDetailElement priceDetailElement2 = new PriceInfoSearchListDetailElement();
    priceDetailElement2.setPriceId(UUID.randomUUID().toString());
    priceDetailElement2.setPriceType(4);
    priceDetailElement2.setPricePerUnit(1);
    priceDetailElement2.setPrice(2000);
    priceDetailElement2.setPriority(2);
    priceDetailElement2.setOperatorId(operatorId);
    priceDetailElement2.setEffectiveStartTime("2025-11-13T12:00:00Z");
    priceDetailElement2.setEffectiveEndTime("2025-11-13T14:00:00Z");

    PriceInfoSearchListDetailElement priceDetailElement3 = new PriceInfoSearchListDetailElement();
    priceDetailElement3.setPriceId(UUID.randomUUID().toString());
    priceDetailElement3.setPriceType(4);
    priceDetailElement3.setPricePerUnit(1);
    priceDetailElement3.setPrice(3000);
    priceDetailElement3.setPriority(3);
    priceDetailElement3.setOperatorId(operatorId);
    priceDetailElement3.setEffectiveStartTime("2025-11-13T14:00:00Z");
    priceDetailElement3.setEffectiveEndTime("2025-11-13T16:00:00Z");

    PriceInfoSearchListElement priceElement = new PriceInfoSearchListElement();
    priceElement.setResourceId(dronePortId);
    priceElement.setResourceType(1);
    priceElement.setPriceInfos(
        List.of(priceDetailElement1, priceDetailElement2, priceDetailElement3));

    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(List.of(priceElement));

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    DronePortInfoDetailResponseDto response =
        dronePortInfoServiceImpl.getDetail(dronePortId, true, userDto);

    assertNotNull(response);
    assertEquals(dronePortId, response.getDronePortId());

    // 料金情報の検証（3件）
    assertNotNull(response.getPriceInfos());
    assertEquals(3, response.getPriceInfos().size());

    // 1件目の検証
    assertEquals(priceDetailElement1.getPriceId(), response.getPriceInfos().get(0).getPriceId());
    assertEquals(
        priceDetailElement1.getPriceType(), response.getPriceInfos().get(0).getPriceType());
    assertEquals(
        priceDetailElement1.getPricePerUnit(), response.getPriceInfos().get(0).getPricePerUnit());
    assertEquals(priceDetailElement1.getPrice(), response.getPriceInfos().get(0).getPrice());
    assertEquals(priceDetailElement1.getPriority(), response.getPriceInfos().get(0).getPriority());
    assertEquals(
        priceDetailElement1.getEffectiveStartTime(),
        response.getPriceInfos().get(0).getEffectiveStartTime());
    assertEquals(
        priceDetailElement1.getEffectiveEndTime(),
        response.getPriceInfos().get(0).getEffectiveEndTime());

    // 2件目の検証
    assertEquals(priceDetailElement2.getPriceId(), response.getPriceInfos().get(1).getPriceId());
    assertEquals(
        priceDetailElement2.getPriceType(), response.getPriceInfos().get(1).getPriceType());
    assertEquals(
        priceDetailElement2.getPricePerUnit(), response.getPriceInfos().get(1).getPricePerUnit());
    assertEquals(priceDetailElement2.getPrice(), response.getPriceInfos().get(1).getPrice());
    assertEquals(priceDetailElement2.getPriority(), response.getPriceInfos().get(1).getPriority());

    // 3件目の検証
    assertEquals(priceDetailElement3.getPriceId(), response.getPriceInfos().get(2).getPriceId());
    assertEquals(
        priceDetailElement3.getPriceType(), response.getPriceInfos().get(2).getPriceType());
    assertEquals(
        priceDetailElement3.getPricePerUnit(), response.getPriceInfos().get(2).getPricePerUnit());
    assertEquals(priceDetailElement3.getPrice(), response.getPriceInfos().get(2).getPrice());
    assertEquals(priceDetailElement3.getPriority(), response.getPriceInfos().get(2).getPriority());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 料金情報要否がtrue<br>
   * 料金情報サービスでValidationErrorExceptionが発生<br>
   * 結果: ValidationErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_Error_料金情報でバリデーションエラー() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String operatorId = "dummyOperator";
    String dronePortId = UUID.randomUUID().toString();

    // 離着陸場情報エンティティ作成
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setImageBinary(null);
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(operatorId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));

    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setOperatorId(operatorId);
    entity.setDronePortStatusEntity(statusEntity);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any()))
        .thenThrow(new ValidationErrorException("ValidationErrorException"));

    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class,
            () -> {
              dronePortInfoServiceImpl.getDetail(dronePortId, true, userDto);
            });

    assertEquals("ValidationErrorException", exception.getMessage());
    verify(dronePortInfoRepository, times(1))
        .findByDronePortIdAndDeleteFlagFalse(any(String.class));
    verify(priceInfoSearchService, times(1)).getPriceInfoList(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場情報の詳細を取得する<br>
   * 条件: 正常な離着陸場IDを渡す<br>
   * 料金情報要否がtrue<br>
   * 料金情報サービスでServiceErrorExceptionが発生<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_Error_料金情報でサービスエラー() {
    String operatorId = "dummyOperator";
    String dronePortId = UUID.randomUUID().toString();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // 離着陸場情報エンティティ作成
    DronePortInfoEntity entity = createDronePortInfoEntity();
    entity.setImageBinary(null);
    entity.setDronePortId(dronePortId);
    entity.setOperatorId(operatorId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));

    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T02:00:00Z")));
    statusEntity.setDronePortId(entity.getDronePortId());
    statusEntity.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    statusEntity.setOperatorId(operatorId);
    entity.setDronePortStatusEntity(statusEntity);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any()))
        .thenThrow(new ServiceErrorException("ServiceErrorException"));

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> {
              dronePortInfoServiceImpl.getDetail(dronePortId, true, userDto);
            });

    assertEquals("ServiceErrorException", exception.getMessage());
    verify(dronePortInfoRepository, times(1))
        .findByDronePortIdAndDeleteFlagFalse(any(String.class));
    verify(priceInfoSearchService, times(1)).getPriceInfoList(any());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: base64をバイト型にデコードする<br>
   * 条件: 正常なbase64文字列を渡す<br>
   * 結果: デコードされたバイト配列が設定される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeBinary_Normal() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setImageData(
        "data:image/png;base64," + Base64.getEncoder().encodeToString("test".getBytes()));

    dronePortInfoServiceImpl.decodeBinary(dto);

    assertArrayEquals("test".getBytes(), dto.getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: base64をバイト型にデコードする<br>
   * 条件: データURL未設定なbase64文字列を渡す<br>
   * 結果: デコードされたバイト配列が設定される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeBinary_Normal_noDataUrl() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setImageData(Base64.getEncoder().encodeToString("test".getBytes()));

    dronePortInfoServiceImpl.decodeBinary(dto);

    assertNull(dto.getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: base64をバイト型にデコードする<br>
   * 条件: 空のbase64文字列を渡す<br>
   * 結果: 空のバイト配列が設定される<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void decodeBinary_Empty() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setImageData("");

    dronePortInfoServiceImpl.decodeBinary(dto);

    assertArrayEquals(new byte[] {}, dto.getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: base64をバイト型にデコードする<br>
   * 条件: nullのbase64文字列を渡す<br>
   * 結果: バイト配列がnullのままである<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void decodeBinary_Null() {
    DronePortInfoRegisterRequestDto dto = new DronePortInfoRegisterRequestDto();
    dto.setImageData(null);

    dronePortInfoServiceImpl.decodeBinary(dto);

    assertNull(dto.getImageBinary());
  }

  private VisTelemetryInfoEntity createVisTelemetryInfoEntity() {
    VisTelemetryInfoEntity entity = new VisTelemetryInfoEntity();
    entity.setDroneportId("dummyDroneportId");
    entity.setDroneportName("dummyDroneportName");
    entity.setBaseAddress("dummyBaseAddress");
    entity.setBaseId("dummyBaseId");
    entity.setBaseName("dummyBaseName");
    entity.setBaseStatus("dummyBaseStatus");
    entity.setDroneportAlt(123.4d);
    entity.setDroneportLat(23.4d);
    entity.setDroneportLon(34.5d);
    entity.setHumidity(45.6d);
    entity.setIlluminance(56.7d);
    entity.setMaxinstWindDirection(67.8d);
    entity.setMaxinstWindSpeed(67.8d);
    entity.setPressure(78.9d);
    entity.setRainfall(89.0d);
    entity.setTemp(98.7d);
    entity.setUltraviolet(76.5d);
    entity.setWindDirection(75.4d);
    entity.setWindSpeed(65.4d);
    entity.setThresholdWindSpeed(87.6d);
    entity.setObservationTime(new Timestamp(System.currentTimeMillis()));
    entity.setInvasionCategory("dummyInvasionCategory");
    entity.setInvasionFlag(true);
    entity.setUsage(1);
    entity.setDroneportStatus("dummyDroneportStatus");
    entity.setVisStatus("dummyVisStatus");
    entity.setErrorCode("dummyErrorCode");
    entity.setErrorReason("dummyErrorReason");

    return entity;
  }

  /**
   * メソッド名: getEnvironment<br>
   * 試験名: 離着陸場周辺情報を取得する<br>
   * 条件: 正常な離着陸場IDを渡す。<br>
   * 　　　　　離着陸場が存在する、テレメトリ情報が存在する場合<br>
   * 結果: 離着陸場周辺情報が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getEnvironment_Normal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    // 取得対象の離着陸場ID
    String dronePortId = "inputdroneportid";
    // 離着陸場情報検索のモック設定
    DronePortInfoEntity dronePortInfoEntity = createDronePortInfoEntity();
    dronePortInfoEntity.setDronePortId(dronePortId);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(dronePortInfoEntity));
    // テレメトリ情報検索のモック設定
    VisTelemetryInfoEntity telemetryEntity = createVisTelemetryInfoEntity();
    when(visTelemetryInfoRepository.findByDroneportId(dronePortId))
        .thenReturn(Optional.of(telemetryEntity));
    // 期待値設定
    DronePortEnvironmentInfoResponseDto expectResponse = new DronePortEnvironmentInfoResponseDto();
    expectResponse.setDronePortId(dronePortId);
    expectResponse.setWindSpeed(telemetryEntity.getWindSpeed());
    expectResponse.setWindDirection(telemetryEntity.getWindDirection());
    expectResponse.setRainfall(telemetryEntity.getRainfall());
    expectResponse.setTemp(telemetryEntity.getTemp());
    expectResponse.setPressure(telemetryEntity.getPressure());
    expectResponse.setObstacleDetected(telemetryEntity.getInvasionFlag());
    expectResponse.setObservationTime(
        StringUtils.toUtcDateTimeString(telemetryEntity.getObservationTime().toLocalDateTime()));
    // 処理呼出
    DronePortEnvironmentInfoResponseDto response =
        dronePortInfoServiceImpl.getEnvironment(dronePortId, userDto);
    // 結果確認
    assertNotNull(response);
    assertEquals(expectResponse.toString(), response.toString());
  }

  /**
   * メソッド名: getEnvironment<br>
   * 試験名: 離着陸場周辺情報を取得する<br>
   * 条件: 正常な離着陸場IDを渡す。<br>
   * 　　　　　離着陸場情報が存在する、テレメトリ情報が存在しない場合<br>
   * 結果: 離着陸場周辺情報が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getEnvironment_Normal2() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    // 取得対象の離着陸場ID
    String dronePortId = "inputdroneportid";
    // 離着陸場情報検索のモック設定
    DronePortInfoEntity dronePortInfoEntity = createDronePortInfoEntity();
    dronePortInfoEntity.setDronePortId(dronePortId);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(dronePortInfoEntity));
    // テレメトリ情報検索のモック設定
    when(visTelemetryInfoRepository.findByDroneportId(dronePortInfoEntity.getDronePortId()))
        .thenReturn(Optional.empty());
    // 期待値設定
    DronePortEnvironmentInfoResponseDto expectResponse = new DronePortEnvironmentInfoResponseDto();
    expectResponse.setDronePortId(dronePortId);
    // 処理呼出
    DronePortEnvironmentInfoResponseDto response =
        dronePortInfoServiceImpl.getEnvironment(dronePortId, userDto);
    // 結果確認
    assertNotNull(response);
    assertEquals(expectResponse.toString(), response.toString());
  }

  /**
   * メソッド名: getEnvironment<br>
   * 試験名: 離着陸場周辺情報を取得する<br>
   * 条件: 他事業者のオペレータID<br>
   * 　　　　　離着陸場が存在する、テレメトリ情報が存在する場合<br>
   * 結果: 離着陸場周辺情報が返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getEnvironment_Normal_OtherOpe() {
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    // 取得対象の離着陸場ID
    String dronePortId = "inputdroneportid";
    // 離着陸場情報検索のモック設定
    DronePortInfoEntity dronePortInfoEntity = createDronePortInfoEntity();
    dronePortInfoEntity.setDronePortId(dronePortId);
    when(dronePortInfoRepository.findOne(any(Specification.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    // テレメトリ情報検索のモック設定
    VisTelemetryInfoEntity telemetryEntity = createVisTelemetryInfoEntity();
    when(visTelemetryInfoRepository.findByDroneportId(dronePortId))
        .thenReturn(Optional.of(telemetryEntity));
    // 期待値設定
    DronePortEnvironmentInfoResponseDto expectResponse = new DronePortEnvironmentInfoResponseDto();
    expectResponse.setDronePortId(dronePortId);
    expectResponse.setWindSpeed(telemetryEntity.getWindSpeed());
    expectResponse.setWindDirection(telemetryEntity.getWindDirection());
    expectResponse.setRainfall(telemetryEntity.getRainfall());
    expectResponse.setTemp(telemetryEntity.getTemp());
    expectResponse.setPressure(telemetryEntity.getPressure());
    expectResponse.setObstacleDetected(telemetryEntity.getInvasionFlag());
    expectResponse.setObservationTime(
        StringUtils.toUtcDateTimeString(telemetryEntity.getObservationTime().toLocalDateTime()));
    // 処理呼出
    DronePortEnvironmentInfoResponseDto response =
        dronePortInfoServiceImpl.getEnvironment(dronePortId, userDto);
    // 結果確認
    assertNotNull(response);
    assertEquals(expectResponse.toString(), response.toString());
  }

  /**
   * メソッド名: getEnvironment<br>
   * 試験名: 離着陸場周辺情報を取得する<br>
   * 条件: 存在しない離着陸場IDを渡す<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getEnvironment_DronePortInfo_NotFound() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    // 取得対象の離着陸場ID
    String dronePortId = "inputdroneportid";
    // 離着陸場情報検索のモック設定
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.empty());
    // 処理呼出
    NotFoundException e =
        assertThrows(
            NotFoundException.class,
            () -> dronePortInfoServiceImpl.getEnvironment(dronePortId, userDto));

    assertEquals("離着陸場情報が見つかりません。離着陸場ID:" + dronePortId, e.getMessage());
  }

  /**
   * メソッド名: getEnvironment<br>
   * 試験名: 離着陸場周辺情報を取得する<br>
   * 条件: テレメトリ情報が見つからない<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getEnvironment_VisTelemetryInfo_NotFound() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    // 取得対象の離着陸場ID
    String dronePortId = "inputdroneportid";
    // 離着陸場情報検索のモック設定
    DronePortInfoEntity dronePortInfoEntity = createDronePortInfoEntity();
    dronePortInfoEntity.setDronePortId(dronePortId);
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(dronePortId))
        .thenReturn(Optional.of(dronePortInfoEntity));
    // テレメトリ情報検索のモック設定
    when(visTelemetryInfoRepository.findByDroneportId(dronePortInfoEntity.getDronePortId()))
        .thenReturn(Optional.empty());
    // 期待値設定
    DronePortEnvironmentInfoResponseDto expectResponse = new DronePortEnvironmentInfoResponseDto();
    expectResponse.setDronePortId(dronePortId);

    // 処理呼出
    DronePortEnvironmentInfoResponseDto response =
        dronePortInfoServiceImpl.getEnvironment(dronePortId, userDto);
    // 結果確認
    assertNotNull(response);
    assertEquals(expectResponse.toString(), response.toString());
  }

  /**
   * メソッド名: isNullDronePortInfo<br>
   * 試験名: 離着陸場情報登録更新要求DTOの離着陸場情報更新対象判定を確認する<br>
   * 条件: DTOの一部の項目のみ設定、他はすべてnullを設定<br>
   * 結果: 更新対象あり(false)となる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void isNullDronePortInfo() throws Exception {
    Method targetMethod =
        DronePortInfoServiceImpl.class.getDeclaredMethod(
            "isNullDronePortInfo", DronePortInfoRegisterRequestDto.class);
    targetMethod.setAccessible(true);

    DronePortInfoRegisterRequestDto dto;

    dto = new DronePortInfoRegisterRequestDto();
    dto.setAddress("dummy");
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setManufacturer("dummy");
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setSerialNumber("dummy");
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setPortType(99);
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setVisDronePortCompanyId("dummy");
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setLat(12.45d);
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setLon(34.56d);
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setAlt(45.67d);
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setSupportDroneType("dummy");
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setImageData("dummy");
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setPublicFlag(true);
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));
  }

  /**
   * メソッド名: isNullDronePortStatus<br>
   * 試験名: 離着陸場情報登録更新要求DTOの離着陸場状態更新対象判定を確認する<br>
   * 条件: DTOの一部の項目のみ設定、他はすべてnullを設定<br>
   * 結果: 更新対象あり(false)となる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void isNullDronePortStatus() throws Exception {
    Method targetMethod =
        DronePortInfoServiceImpl.class.getDeclaredMethod(
            "isNullDronePortStatus", DronePortInfoRegisterRequestDto.class);
    targetMethod.setAccessible(true);

    DronePortInfoRegisterRequestDto dto;

    dto = new DronePortInfoRegisterRequestDto();
    dto.setActiveStatus(88);
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setInactiveTimeFrom("dummy");
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));

    dto = new DronePortInfoRegisterRequestDto();
    dto.setInactiveTimeTo("dummy");
    assertFalse((boolean) targetMethod.invoke(dronePortInfoServiceImpl, dto));
  }
}
