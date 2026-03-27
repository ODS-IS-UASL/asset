package com.hitachi.droneroute.dpm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
import com.hitachi.droneroute.arm.repository.AircraftReserveInfoRepository;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.RoleInfoDto;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListElement;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateResponseDto;
import com.hitachi.droneroute.dpm.entity.DronePortInfoEntity;
import com.hitachi.droneroute.dpm.entity.DronePortReserveInfoEntity;
import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;
import com.hitachi.droneroute.dpm.repository.DronePortInfoRepository;
import com.hitachi.droneroute.dpm.repository.DronePortReserveInfoRepository;
import com.hitachi.droneroute.dpm.repository.DronePortStatusRepository;
import io.hypersistence.utils.hibernate.type.range.Range;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

/** DronePortReserveInfoServiceImplクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class DronePortReserveInfoServiceImplTest {

  @MockBean private DronePortInfoRepository dronePortInfoRepository;

  @MockBean private DronePortReserveInfoRepository dronePortReserveInfoRepository;

  @MockBean private AircraftInfoRepository aircraftInfoRepository;

  @MockBean private AircraftReserveInfoRepository aircraftReserveInfoRepository;

  @MockBean private DronePortStatusRepository dronePortStatusRepository;

  @Autowired private DronePortReserveInfoServiceImpl dronePortReserveInfoServiceImpl;

  @SpyBean private SystemSettings systemSettings;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * メソッド名: register<br>
   * 試験名: 正常系の登録処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。離着陸場種別:VIS管理。<br>
   * 結果: 登録処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_success_vis() {
    // ●準備
    String operatorId = "dummyOperator";
    UUID dronePortId = UUID.randomUUID();
    UUID aircraftId = UUID.randomUUID();
    UUID routeId = UUID.randomUUID();
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId(dronePortId.toString());
    element.setAircraftId(aircraftId.toString());
    element.setRouteReservationId(routeId.toString());
    element.setUsageType(1);
    element.setReservationTimeFrom("2023-01-01T00:00:00Z");
    element.setReservationTimeTo("2023-01-03T00:00:00+09:00");
    dto.setData(Arrays.asList(element));
    // dto.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(1);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.getDronePortStatusEntity().setOperatorId(operatorId);

    UUID reservationId = UUID.randomUUID();
    DronePortReserveInfoEntity responseEntity = new DronePortReserveInfoEntity();
    responseEntity.setDronePortReservationId(reservationId);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(responseEntity);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行
    DronePortReserveInfoRegisterListResponseDto responseDto =
        dronePortReserveInfoServiceImpl.register(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(reservationId.toString(), responseDto.getDronePortReservationIds().get(0));
    {
      ArgumentCaptor<DronePortReserveInfoEntity> reserveEntityCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoEntity.class);
      verify(dronePortReserveInfoRepository, times(1)).save(reserveEntityCaptor.capture());
      DronePortReserveInfoEntity e = reserveEntityCaptor.getValue();
      assertEquals(dronePortId.toString(), e.getDronePortId());
      assertEquals(aircraftId, e.getAircraftId());
      assertEquals(routeId, e.getRouteReservationId());
      assertEquals(dto.getData().get(0).getUsageType(), e.getUsageType());
      assertEquals(
          toUtcLocalDateTime(element.getReservationTimeFrom()).toString(),
          e.getReservationTime().lower().toString());
      assertEquals(
          toUtcLocalDateTime(element.getReservationTimeTo()).toString(),
          e.getReservationTime().upper().toString());
      assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
      log.info(e.getReservationTime().lower().toString());
      log.info(e.getReservationTime().upper().toString());
    }
  }

  private LocalDateTime toUtcLocalDateTime(String str) {
    return ZonedDateTime.parse(str).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
  }

  /**
   * メソッド名: register<br>
   * 試験名: 正常系の登録処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　1件目:離着陸場種別:自システム管理。<br>
   * 　　　　　2件目:離着陸場種別:VIS管理 結果: 登録処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_success() {
    // ●準備
    String operatorId = "dummyOperator";
    String regDronePortId1 = UUID.randomUUID().toString();
    String regDronePortId2 = UUID.randomUUID().toString();
    UUID regAircraftId1 = UUID.randomUUID();
    UUID regAircraftId2 = UUID.randomUUID();
    UUID regRouteReservationId1 = UUID.randomUUID();
    UUID regRouteReservationId2 = UUID.randomUUID();
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element1 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element1.setDronePortId(regDronePortId1);
    element1.setAircraftId(regAircraftId1.toString());
    element1.setRouteReservationId(regRouteReservationId1.toString());
    element1.setUsageType(97);
    element1.setReservationTimeFrom("2023-01-01T00:00:00Z");
    element1.setReservationTimeTo("2023-01-03T01:00:00+09:00");
    DronePortReserveInfoRegisterListRequestDto.Element element2 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element2.setDronePortId(regDronePortId2);
    element2.setAircraftId(regAircraftId2.toString());
    element2.setRouteReservationId(regRouteReservationId2.toString());
    element1.setUsageType(96);
    element2.setReservationTimeFrom("2023-02-01T00:00:00Z");
    element2.setReservationTimeTo("2023-02-03T01:00:00+09:00");
    dto.setData(Arrays.asList(element1, element2));
    // dto.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity1 = new DronePortInfoEntity();
    dronePortInfoEntity1.setPortType(1);
    dronePortInfoEntity1.setOperatorId(operatorId);
    DronePortStatusEntity dronePortStatusEntity1 = new DronePortStatusEntity();
    dronePortStatusEntity1.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    dronePortStatusEntity1.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    dronePortStatusEntity1.setOperatorId(operatorId);
    dronePortInfoEntity1.setDronePortStatusEntity(dronePortStatusEntity1);
    DronePortInfoEntity dronePortInfoEntity2 = new DronePortInfoEntity();
    dronePortInfoEntity2.setPortType(2);
    dronePortInfoEntity2.setOperatorId(operatorId);
    DronePortStatusEntity dronePortStatusEntity2 = new DronePortStatusEntity();
    dronePortStatusEntity2.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    dronePortStatusEntity2.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    dronePortStatusEntity2.setOperatorId(operatorId);
    dronePortInfoEntity2.setDronePortStatusEntity(dronePortStatusEntity2);

    UUID reservationId1 = UUID.randomUUID();
    DronePortReserveInfoEntity responseEntity1 = new DronePortReserveInfoEntity();
    responseEntity1.setDronePortReservationId(reservationId1);
    UUID reservationId2 = UUID.randomUUID();
    DronePortReserveInfoEntity responseEntity2 = new DronePortReserveInfoEntity();
    responseEntity2.setDronePortReservationId(reservationId2);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(element1.getDronePortId()))
        .thenReturn(Optional.of(dronePortInfoEntity1));
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(element2.getDronePortId()))
        .thenReturn(Optional.of(dronePortInfoEntity2));
    when(dronePortReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of())
        .thenReturn(List.of());
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(responseEntity1)
        .thenReturn(responseEntity2);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(regAircraftId1))
        .thenReturn(Optional.of(aircraftEntity));
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(regAircraftId2))
        .thenReturn(Optional.of(aircraftEntity));

    when(dronePortStatusRepository.findAll(any(Specification.class)))
        .thenReturn(List.of())
        .thenReturn(List.of());

    // ●実行
    DronePortReserveInfoRegisterListResponseDto responseDto =
        dronePortReserveInfoServiceImpl.register(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(reservationId1.toString(), responseDto.getDronePortReservationIds().get(0));
    assertEquals(reservationId2.toString(), responseDto.getDronePortReservationIds().get(1));
    {
      ArgumentCaptor<DronePortReserveInfoEntity> reserveEntityCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoEntity.class);
      verify(dronePortReserveInfoRepository, times(2)).save(reserveEntityCaptor.capture());
      {
        DronePortReserveInfoEntity e = reserveEntityCaptor.getAllValues().get(0);
        assertEquals(regDronePortId1, e.getDronePortId());
        assertEquals(regAircraftId1, e.getAircraftId());
        assertEquals(regRouteReservationId1, e.getRouteReservationId());
        assertEquals(element1.getUsageType(), e.getUsageType());
        assertEquals(
            toUtcLocalDateTime(element1.getReservationTimeFrom()).toString(),
            e.getReservationTime().lower().toString());
        assertEquals(
            toUtcLocalDateTime(element1.getReservationTimeTo()).toString(),
            e.getReservationTime().upper().toString());
        log.info(e.getReservationTime().lower().toString());
        log.info(e.getReservationTime().upper().toString());
        assertTrue(e.getReservationActiveFlag());
        assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
      }
      {
        DronePortReserveInfoEntity e = reserveEntityCaptor.getAllValues().get(1);
        assertEquals(regDronePortId2, e.getDronePortId());
        assertEquals(regAircraftId2, e.getAircraftId());
        assertEquals(regRouteReservationId2, e.getRouteReservationId());
        assertEquals(element2.getUsageType(), e.getUsageType());
        assertEquals(
            toUtcLocalDateTime(element2.getReservationTimeFrom()).toString(),
            e.getReservationTime().lower().toString());
        assertEquals(
            toUtcLocalDateTime(element2.getReservationTimeTo()).toString(),
            e.getReservationTime().upper().toString());
        log.info(e.getReservationTime().lower().toString());
        log.info(e.getReservationTime().upper().toString());
        assertTrue(e.getReservationActiveFlag());
        assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
      }
    }
  }

  /**
   * メソッド名: register<br>
   * 試験名: 正常系の登録処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　1件目:離着陸場種別:自システム管理、機体ID:null、航路予約ID:null<br>
   * 　　　　　2件目:離着陸場種別:VIS管理 結果: 登録処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_success2() {
    // ●準備
    String operatorId = "dummyOperator";
    String regDronePortId1 = UUID.randomUUID().toString();
    String regDronePortId2 = UUID.randomUUID().toString();
    UUID regAircraftId2 = UUID.randomUUID();
    UUID regRouteReservationId2 = UUID.randomUUID();
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element1 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element1.setDronePortId(regDronePortId1);
    element1.setUsageType(97);
    element1.setReservationTimeFrom("2023-01-01T00:00:00Z");
    element1.setReservationTimeTo("2023-01-03T01:00:00+09:00");
    DronePortReserveInfoRegisterListRequestDto.Element element2 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element2.setDronePortId(regDronePortId2);
    element2.setAircraftId(regAircraftId2.toString());
    element2.setRouteReservationId(regRouteReservationId2.toString());
    element1.setUsageType(96);
    element2.setReservationTimeFrom("2023-02-01T00:00:00Z");
    element2.setReservationTimeTo("2023-02-03T01:00:00+09:00");
    dto.setData(Arrays.asList(element1, element2));
    // dto.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity1 = new DronePortInfoEntity();
    dronePortInfoEntity1.setPortType(1);
    dronePortInfoEntity1.setOperatorId(operatorId);
    DronePortInfoEntity dronePortInfoEntity2 = new DronePortInfoEntity();
    dronePortInfoEntity2.setPortType(2);
    dronePortInfoEntity2.setOperatorId(operatorId);

    UUID reservationId1 = UUID.randomUUID();
    DronePortReserveInfoEntity responseEntity1 = new DronePortReserveInfoEntity();
    responseEntity1.setDronePortReservationId(reservationId1);
    DronePortStatusEntity dronePortStatusEntity1 = new DronePortStatusEntity();
    dronePortStatusEntity1.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    dronePortStatusEntity1.setOperatorId(operatorId);
    dronePortInfoEntity1.setDronePortStatusEntity(dronePortStatusEntity1);
    UUID reservationId2 = UUID.randomUUID();
    DronePortReserveInfoEntity responseEntity2 = new DronePortReserveInfoEntity();
    responseEntity2.setDronePortReservationId(reservationId2);
    DronePortStatusEntity dronePortStatusEntity2 = new DronePortStatusEntity();
    dronePortStatusEntity2.setActiveStatus(null);
    dronePortStatusEntity2.setOperatorId(operatorId);
    dronePortInfoEntity2.setDronePortStatusEntity(dronePortStatusEntity2);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(element1.getDronePortId()))
        .thenReturn(Optional.of(dronePortInfoEntity1));
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(element2.getDronePortId()))
        .thenReturn(Optional.of(dronePortInfoEntity2));
    when(dronePortReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of())
        .thenReturn(List.of());
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(responseEntity1)
        .thenReturn(responseEntity2);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(regAircraftId2))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行
    DronePortReserveInfoRegisterListResponseDto responseDto =
        dronePortReserveInfoServiceImpl.register(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(reservationId1.toString(), responseDto.getDronePortReservationIds().get(0));
    assertEquals(reservationId2.toString(), responseDto.getDronePortReservationIds().get(1));
    {
      ArgumentCaptor<DronePortReserveInfoEntity> reserveEntityCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoEntity.class);
      verify(dronePortReserveInfoRepository, times(2)).save(reserveEntityCaptor.capture());
      {
        DronePortReserveInfoEntity e = reserveEntityCaptor.getAllValues().get(0);
        assertEquals(regDronePortId1, e.getDronePortId());
        assertNull(e.getAircraftId());
        assertNull(e.getRouteReservationId());
        assertEquals(element1.getUsageType(), e.getUsageType());
        assertEquals(
            toUtcLocalDateTime(element1.getReservationTimeFrom()).toString(),
            e.getReservationTime().lower().toString());
        assertEquals(
            toUtcLocalDateTime(element1.getReservationTimeTo()).toString(),
            e.getReservationTime().upper().toString());
        log.info(e.getReservationTime().lower().toString());
        log.info(e.getReservationTime().upper().toString());
        assertTrue(e.getReservationActiveFlag());
        assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
      }
      {
        DronePortReserveInfoEntity e = reserveEntityCaptor.getAllValues().get(1);
        assertEquals(regDronePortId2, e.getDronePortId());
        assertEquals(regAircraftId2, e.getAircraftId());
        assertEquals(regRouteReservationId2, e.getRouteReservationId());
        assertEquals(element2.getUsageType(), e.getUsageType());
        assertEquals(
            toUtcLocalDateTime(element2.getReservationTimeFrom()).toString(),
            e.getReservationTime().lower().toString());
        assertEquals(
            toUtcLocalDateTime(element2.getReservationTimeTo()).toString(),
            e.getReservationTime().upper().toString());
        log.info(e.getReservationTime().lower().toString());
        log.info(e.getReservationTime().upper().toString());
        assertTrue(e.getReservationActiveFlag());
        assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
      }
    }
  }

  /**
   * メソッド名: register<br>
   * 試験名: 正常系の登録処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　1件目:離着陸場種別:自システム管理。<br>
   * 　　　　　2件目:離着陸場種別:VIS管理 結果: 登録処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_success3() {
    // ●準備
    String operatorId = "dummyOperator";
    String regDronePortId1 = UUID.randomUUID().toString();
    String regDronePortId2 = UUID.randomUUID().toString();
    UUID regAircraftId1 = UUID.randomUUID();
    UUID regAircraftId2 = UUID.randomUUID();
    UUID regRouteReservationId1 = UUID.randomUUID();
    UUID regRouteReservationId2 = UUID.randomUUID();
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element1 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element1.setDronePortId(regDronePortId1);
    element1.setAircraftId(regAircraftId1.toString());
    element1.setRouteReservationId(regRouteReservationId1.toString());
    element1.setUsageType(97);
    element1.setReservationTimeFrom(null);
    element1.setReservationTimeTo(null);
    DronePortReserveInfoRegisterListRequestDto.Element element2 =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element2.setDronePortId(regDronePortId2);
    element2.setAircraftId(regAircraftId2.toString());
    element2.setRouteReservationId(regRouteReservationId2.toString());
    element1.setUsageType(96);
    element2.setReservationTimeFrom("2023-02-01T00:00:00Z");
    element2.setReservationTimeTo("2023-02-03T01:00:00+09:00");
    dto.setData(Arrays.asList(element1, element2));
    // dto.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity1 = new DronePortInfoEntity();
    dronePortInfoEntity1.setPortType(1);
    dronePortInfoEntity1.setOperatorId(operatorId);
    DronePortStatusEntity dronePortStatusEntity1 = new DronePortStatusEntity();
    dronePortStatusEntity1.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    dronePortStatusEntity1.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    dronePortStatusEntity1.setOperatorId(operatorId);
    dronePortInfoEntity1.setDronePortStatusEntity(dronePortStatusEntity1);
    DronePortInfoEntity dronePortInfoEntity2 = new DronePortInfoEntity();
    dronePortInfoEntity2.setPortType(2);
    dronePortInfoEntity2.setOperatorId(operatorId);
    DronePortStatusEntity dronePortStatusEntity2 = new DronePortStatusEntity();
    dronePortStatusEntity2.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    dronePortStatusEntity2.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    dronePortStatusEntity2.setOperatorId(operatorId);
    dronePortInfoEntity2.setDronePortStatusEntity(dronePortStatusEntity2);

    UUID reservationId1 = UUID.randomUUID();
    DronePortReserveInfoEntity responseEntity1 = new DronePortReserveInfoEntity();
    responseEntity1.setDronePortReservationId(reservationId1);
    UUID reservationId2 = UUID.randomUUID();
    DronePortReserveInfoEntity responseEntity2 = new DronePortReserveInfoEntity();
    responseEntity2.setDronePortReservationId(reservationId2);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(element1.getDronePortId()))
        .thenReturn(Optional.of(dronePortInfoEntity1));
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(element2.getDronePortId()))
        .thenReturn(Optional.of(dronePortInfoEntity2));
    when(dronePortReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of())
        .thenReturn(List.of());
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(responseEntity1)
        .thenReturn(responseEntity2);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(regAircraftId1))
        .thenReturn(Optional.of(aircraftEntity));
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(regAircraftId2))
        .thenReturn(Optional.of(aircraftEntity));

    when(dronePortStatusRepository.findAll(any(Specification.class)))
        .thenReturn(List.of())
        .thenReturn(List.of());

    // ●実行
    DronePortReserveInfoRegisterListResponseDto responseDto =
        dronePortReserveInfoServiceImpl.register(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(reservationId1.toString(), responseDto.getDronePortReservationIds().get(0));
    assertEquals(reservationId2.toString(), responseDto.getDronePortReservationIds().get(1));
    {
      ArgumentCaptor<DronePortReserveInfoEntity> reserveEntityCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoEntity.class);
      verify(dronePortReserveInfoRepository, times(2)).save(reserveEntityCaptor.capture());
      {
        DronePortReserveInfoEntity e = reserveEntityCaptor.getAllValues().get(0);
        assertEquals(regDronePortId1, e.getDronePortId());
        assertEquals(regAircraftId1, e.getAircraftId());
        assertEquals(regRouteReservationId1, e.getRouteReservationId());
        assertEquals(element1.getUsageType(), e.getUsageType());
        assertEquals(null, e.getReservationTime());
        assertTrue(e.getReservationActiveFlag());
        assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
      }
      {
        DronePortReserveInfoEntity e = reserveEntityCaptor.getAllValues().get(1);
        assertEquals(regDronePortId2, e.getDronePortId());
        assertEquals(regAircraftId2, e.getAircraftId());
        assertEquals(regRouteReservationId2, e.getRouteReservationId());
        assertEquals(element2.getUsageType(), e.getUsageType());
        assertEquals(
            toUtcLocalDateTime(element2.getReservationTimeFrom()).toString(),
            e.getReservationTime().lower().toString());
        assertEquals(
            toUtcLocalDateTime(element2.getReservationTimeTo()).toString(),
            e.getReservationTime().upper().toString());
        log.info(e.getReservationTime().lower().toString());
        log.info(e.getReservationTime().upper().toString());
        assertTrue(e.getReservationActiveFlag());
        assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
      }
    }
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場が存在しない場合のエラー処理を確認する<br>
   * 条件: 存在しない離着陸場IDを渡す<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void register_dronePortNotFound() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId(UUID.randomUUID().toString());
    element.setAircraftId(UUID.randomUUID().toString());
    dto.setData(Arrays.asList(element));

    UserInfoDto userInfo = createUserInfoDto();

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.empty());

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行 & 検証
    ServiceErrorException actualException =
        assertThrows(
            ServiceErrorException.class,
            () -> dronePortReserveInfoServiceImpl.register(dto, userInfo));

    assertEquals(
        "離着陸場が存在しません:離着陸場ID:" + dto.getData().get(0).getDronePortId(),
        actualException.getMessage());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 使用機体が存在しない場合のエラー処理を確認する<br>
   * 条件: 存在しない使用機体IDを渡す<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void register_AircraftNotFound() {
    // ●準備
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId(UUID.randomUUID().toString());
    element.setAircraftId(UUID.randomUUID().toString());
    dto.setData(Arrays.asList(element));
    // dto.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.empty());

    // ●実行 & 検証
    ServiceErrorException actualException =
        assertThrows(
            ServiceErrorException.class,
            () -> dronePortReserveInfoServiceImpl.register(dto, userInfo));

    assertEquals(
        "機体情報が存在しません:機体ID:" + dto.getData().get(0).getAircraftId(), actualException.getMessage());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 予約が重複する場合のエラー処理を確認する<br>
   * 条件: 重複する予約時間を渡す<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_reservationOverlap() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId(UUID.randomUUID().toString());
    element.setAircraftId(UUID.randomUUID().toString());
    element.setReservationTimeFrom("2023-01-01T00:00:00Z");
    element.setReservationTimeTo("2023-01-01T01:00:00Z");
    dto.setData(Arrays.asList(element));

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(1);
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());
    ;

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(new DronePortReserveInfoEntity()));

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行 & 検証
    ServiceErrorException actualException =
        assertThrows(
            ServiceErrorException.class,
            () -> dronePortReserveInfoServiceImpl.register(dto, userInfo));

    assertEquals("他の予約と被っているため、予約できません", actualException.getMessage());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場がメンテナンス中の場合のエラー処理を確認する<br>
   * 条件: 離着陸場状態が、動作状況:メンテナンス中(4)、使用不可日時範囲:時刻設定あり<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_dronePortInactive1() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId(UUID.randomUUID().toString());
    element.setAircraftId(UUID.randomUUID().toString());
    element.setReservationTimeFrom("2023-01-01T00:00:00Z");
    element.setReservationTimeTo("2023-01-01T01:00:00Z");
    dto.setData(Arrays.asList(element));

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(1);
    dronePortInfoEntity.setOperatorId(operatorId);
    DronePortStatusEntity dronePortStatusEntity1 = new DronePortStatusEntity();
    dronePortStatusEntity1.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
    dronePortStatusEntity1.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    dronePortStatusEntity1.setInactiveTime(
        Range.localDateTimeRange("[2023-01-01T00:00:00,2023-01-01T01:00:00)"));
    dronePortStatusEntity1.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(dronePortStatusEntity1);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(new DronePortReserveInfoEntity()));

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    when(dronePortStatusRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(dronePortStatusEntity1));

    // ●実行 & 検証
    ServiceErrorException actualException =
        assertThrows(
            ServiceErrorException.class,
            () -> dronePortReserveInfoServiceImpl.register(dto, userInfo));

    assertEquals(
        "離着陸場が使用できないため、予約できません。メンテナンス中(2023-01-01T00:00:00Z～2023-01-01T01:00:00Z)",
        actualException.getMessage());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場がメンテナンス中の場合のエラー処理を確認する<br>
   * 条件: 離着陸場状態が、動作状況:メンテナンス中(4)、使用不可日時範囲:時刻設定なし<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_dronePortInactive2() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId(UUID.randomUUID().toString());
    element.setAircraftId(UUID.randomUUID().toString());
    element.setReservationTimeFrom("2023-01-01T00:00:00Z");
    element.setReservationTimeTo("2023-01-01T01:00:00Z");
    dto.setData(Arrays.asList(element));

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(1);
    dronePortInfoEntity.setOperatorId(operatorId);
    DronePortStatusEntity dronePortStatusEntity1 = new DronePortStatusEntity();
    dronePortStatusEntity1.setActiveStatus(DronePortConstants.ACTIVE_STATUS_PREPARING);
    dronePortStatusEntity1.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    dronePortStatusEntity1.setInactiveTime(Range.localDateTimeRange("[,)"));
    dronePortStatusEntity1.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(dronePortStatusEntity1);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(new DronePortReserveInfoEntity()));

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    when(dronePortStatusRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(dronePortStatusEntity1));

    // ●実行 & 検証
    ServiceErrorException actualException =
        assertThrows(
            ServiceErrorException.class,
            () -> dronePortReserveInfoServiceImpl.register(dto, userInfo));

    assertEquals("離着陸場が使用できないため、予約できません。メンテナンス中(～)", actualException.getMessage());
  }

  /**
   * メソッド名: register<br>
   * 試験名: 離着陸場状態が存在しない場合のエラー処理を確認する<br>
   * 条件: 離着陸場情報の離着陸場状態が未設定<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_dronePortInactive3() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId(UUID.randomUUID().toString());
    element.setAircraftId(UUID.randomUUID().toString());
    element.setReservationTimeFrom("2023-01-01T00:00:00Z");
    element.setReservationTimeTo("2023-01-01T01:00:00Z");
    dto.setData(Arrays.asList(element));

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setOperatorId(operatorId);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(new DronePortReserveInfoEntity()));

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行 & 検証
    ServiceErrorException actualException =
        assertThrows(
            ServiceErrorException.class,
            () -> dronePortReserveInfoServiceImpl.register(dto, userInfo));

    assertEquals("離着陸場状態が存在しません:離着陸場ID:" + element.getDronePortId(), actualException.getMessage());
  }

  /**
   * メソッド名: register<br>
   * 試験名: ポート形状が予約不可の場合のエラー処理を確認する<br>
   * 条件: 予約対象の離着陸場情報のポート形状が0:緊急着陸地点の場合<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void register_portType_invalid() {
    // ●準備
    String operatorId = "dummyOperator";
    UUID dronePortId = UUID.randomUUID();
    UUID aircraftId = UUID.randomUUID();
    UUID routeId = UUID.randomUUID();
    DronePortReserveInfoRegisterListRequestDto dto =
        new DronePortReserveInfoRegisterListRequestDto();
    DronePortReserveInfoRegisterListRequestDto.Element element =
        new DronePortReserveInfoRegisterListRequestDto.Element();
    element.setDronePortId(dronePortId.toString());
    element.setAircraftId(aircraftId.toString());
    element.setRouteReservationId(routeId.toString());
    element.setUsageType(1);
    element.setReservationTimeFrom("2023-01-01T00:00:00Z");
    element.setReservationTimeTo("2023-01-03T00:00:00+09:00");
    dto.setData(Arrays.asList(element));

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(0); // 緊急着陸地点は予約不可
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());
    dronePortInfoEntity.setOperatorId(operatorId);

    UUID reservationId = UUID.randomUUID();
    DronePortReserveInfoEntity responseEntity = new DronePortReserveInfoEntity();
    responseEntity.setDronePortReservationId(reservationId);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行 & 検証
    ServiceErrorException actualException =
        assertThrows(
            ServiceErrorException.class,
            () -> dronePortReserveInfoServiceImpl.register(dto, userInfo));

    assertEquals(
        "離着陸場のポート形状が予約可能ではありません:ポート形状:" + dronePortInfoEntity.getPortType(),
        actualException.getMessage());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 正常系の更新処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　離着陸場ID:設定なし<br>
   * 　　　　　使用機体ID:設定なし<br>
   * 　　　　　航路予約ID:設定あり<br>
   * 　　　　　予約時間:設定あり<br>
   * 結果: 更新処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void update_success_vis() {
    // ●準備
    String operatorId = "dummyOperator";
    UUID updateReservationId = UUID.randomUUID();
    UUID routeReservationId = UUID.randomUUID();
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortReservationId(updateReservationId.toString());
    dto.setRouteReservationId(routeReservationId.toString());
    dto.setUsageType(99);
    dto.setReservationTimeFrom("2023-01-01T00:00:00Z");
    dto.setReservationTimeTo("2023-01-01T01:00:00Z");

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(1);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());
    dronePortInfoEntity.setOperatorId(operatorId);

    UUID existingReservationId = UUID.randomUUID();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortReservationId(existingReservationId);
    existingEntity.setDronePortId(UUID.randomUUID().toString());
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setUsageType(98);
    existingEntity.setReservationActiveFlag(true);
    existingEntity.setOperatorId(operatorId);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(
            existingEntity.getDronePortId()))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            updateReservationId))
        .thenReturn(Optional.of(existingEntity));
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(existingEntity);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行
    DronePortReserveInfoUpdateResponseDto responseDto =
        dronePortReserveInfoServiceImpl.update(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(existingReservationId.toString(), responseDto.getDronePortReservationId());
    {
      ArgumentCaptor<DronePortReserveInfoEntity> reserveEntityCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoEntity.class);
      verify(dronePortReserveInfoRepository, times(1)).save(reserveEntityCaptor.capture());
      DronePortReserveInfoEntity e = reserveEntityCaptor.getValue();
      assertEquals(existingEntity.getDronePortId(), e.getDronePortId());
      assertEquals(existingEntity.getAircraftId(), e.getAircraftId());
      assertEquals(routeReservationId, e.getRouteReservationId());
      assertEquals(dto.getUsageType(), e.getUsageType());
      assertEquals(
          toUtcLocalDateTime(dto.getReservationTimeFrom()).toString(),
          e.getReservationTime().lower().toString());
      assertEquals(
          toUtcLocalDateTime(dto.getReservationTimeTo()).toString(),
          e.getReservationTime().upper().toString());
      log.info(e.getReservationTime().lower().toString());
      log.info(e.getReservationTime().upper().toString());
      assertTrue(e.getReservationActiveFlag());
      assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
    }
  }

  /**
   * メソッド名: update<br>
   * 試験名: 正常系の更新処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　離着陸場ID:設定あり<br>
   * 　　　　　使用機体ID:設定あり<br>
   * 　　　　　航路予約ID:設定あり<br>
   * 　　　　　予約時間:設定あり<br>
   * 結果: 更新処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void update_success_internal() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setRouteReservationId(UUID.randomUUID().toString());
    dto.setDronePortReservationId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom("2023-01-01T00:00:00Z");
    dto.setReservationTimeTo("2023-01-01T01:00:00Z");

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(1);
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    UUID existingReservationId = UUID.randomUUID();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortReservationId(existingReservationId);
    existingEntity.setReservationActiveFlag(true);
    existingEntity.setOperatorId(operatorId);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(existingEntity);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行
    DronePortReserveInfoUpdateResponseDto responseDto =
        dronePortReserveInfoServiceImpl.update(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(existingReservationId.toString(), responseDto.getDronePortReservationId());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 正常系の更新処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　離着陸場ID:設定あり<br>
   * 　　　　　使用機体ID:空文字を設定<br>
   * 　　　　　航路予約ID:空文字を設定<br>
   * 　　　　　予約時間:設定あり<br>
   * 結果: 更新処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void update_success_internal2() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setAircraftId("");
    dto.setRouteReservationId("");
    dto.setDronePortReservationId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom("2023-01-01T00:00:00Z");
    dto.setReservationTimeTo("2023-01-01T01:00:00Z");

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(2);
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    UUID existingReservationId = UUID.randomUUID();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortReservationId(existingReservationId);
    existingEntity.setOperatorId(operatorId);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(existingEntity);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行
    DronePortReserveInfoUpdateResponseDto responseDto =
        dronePortReserveInfoServiceImpl.update(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(existingReservationId.toString(), responseDto.getDronePortReservationId());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 正常系の更新処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　離着陸場ID:設定あり<br>
   * 　　　　　使用機体ID:nullを設定<br>
   * 　　　　　航路予約ID:設定あり<br>
   * 　　　　　予約時間:設定あり<br>
   * 結果: 更新処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void update_success_internal3() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setAircraftId(null);
    dto.setRouteReservationId(UUID.randomUUID().toString());
    dto.setDronePortReservationId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom("2023-01-01T00:00:00Z");
    dto.setReservationTimeTo("2023-01-01T01:00:00Z");

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(2);
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    UUID existingReservationId = UUID.randomUUID();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortReservationId(existingReservationId);
    existingEntity.setOperatorId(operatorId);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(existingEntity);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行
    DronePortReserveInfoUpdateResponseDto responseDto =
        dronePortReserveInfoServiceImpl.update(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(existingReservationId.toString(), responseDto.getDronePortReservationId());
  }

  /**
   * メソッド名: update<br>
   * 試験名: 正常系の更新処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　離着陸場ID:設定あり<br>
   * 　　　　　使用機体ID:設定あり<br>
   * 　　　　　航路予約ID:設定あり<br>
   * 　　　　　予約時間:未設定<br>
   * 結果: 更新処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void update_success_internal_noReservationTime1() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setDronePortReservationId(UUID.randomUUID().toString());

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(1);
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    UUID existingReservationId = UUID.randomUUID();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortReservationId(existingReservationId);
    existingEntity.setOperatorId(operatorId);
    existingEntity.setReservationTime(
        Range.localDateTimeRange("[2023-01-01T00:00:00,2023-01-01T01:00:00)"));

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(existingEntity);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行
    DronePortReserveInfoUpdateResponseDto responseDto =
        dronePortReserveInfoServiceImpl.update(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(existingReservationId.toString(), responseDto.getDronePortReservationId());
    {
      ArgumentCaptor<DronePortReserveInfoEntity> reserveEntityCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoEntity.class);
      verify(dronePortReserveInfoRepository, times(1)).save(reserveEntityCaptor.capture());
      DronePortReserveInfoEntity e = reserveEntityCaptor.getValue();
      assertEquals(dto.getDronePortId(), e.getDronePortId());
      assertEquals(dto.getAircraftId(), e.getAircraftId().toString());
      assertEquals(
          toUtcLocalDateTime("2023-01-01T00:00:00Z").toString(),
          e.getReservationTime().lower().toString());
      assertEquals(
          toUtcLocalDateTime("2023-01-01T01:00:00Z").toString(),
          e.getReservationTime().upper().toString());
      assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
    }
  }

  /**
   * メソッド名: update<br>
   * 試験名: 正常系の更新処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　離着陸場ID:設定あり<br>
   * 　　　　　使用機体ID:設定あり<br>
   * 　　　　　航路予約ID:設定あり<br>
   * 　　　　　予約時間:開始日時のみ設定。終了日時は未設定。<br>
   * 結果: 更新処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void update_success_internal_noReservationTime2() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setDronePortReservationId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom("2023-01-01T00:00:00Z");

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(2);
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    UUID existingReservationId = UUID.randomUUID();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortReservationId(existingReservationId);
    existingEntity.setOperatorId(operatorId);
    existingEntity.setReservationTime(
        Range.localDateTimeRange("[2023-01-02T00:00:00,2023-01-02T01:00:00)"));

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(existingEntity);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行
    DronePortReserveInfoUpdateResponseDto responseDto =
        dronePortReserveInfoServiceImpl.update(dto, userInfo);

    // ●検証
    assertNotNull(responseDto);
    assertEquals(existingReservationId.toString(), responseDto.getDronePortReservationId());
    {
      ArgumentCaptor<DronePortReserveInfoEntity> reserveEntityCaptor =
          ArgumentCaptor.forClass(DronePortReserveInfoEntity.class);
      verify(dronePortReserveInfoRepository, times(1)).save(reserveEntityCaptor.capture());
      DronePortReserveInfoEntity e = reserveEntityCaptor.getValue();
      assertEquals(dto.getDronePortId(), e.getDronePortId());
      assertEquals(dto.getAircraftId(), e.getAircraftId().toString());
      assertEquals(
          toUtcLocalDateTime("2023-01-01T00:00:00Z").toString(),
          e.getReservationTime().lower().toString());
      assertEquals(
          toUtcLocalDateTime("2023-01-02T01:00:00Z").toString(),
          e.getReservationTime().upper().toString());
      assertEquals(userInfo.getUserOperatorId(), e.getOperatorId());
    }
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場予約情報が見つからない場合のエラー処理を確認する<br>
   * 条件: 存在しない離着陸場予約IDを渡す<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void update_reservationNotFound() {
    // ●準備
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortReservationId(UUID.randomUUID().toString());

    UserInfoDto userInfo = createUserInfoDto();

    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.empty());

    // ●実行 & 検証
    assertThrows(
        NotFoundException.class, () -> dronePortReserveInfoServiceImpl.update(dto, userInfo));
  }

  /**
   * メソッド名: update<br>
   * 試験名: 離着陸場予約情報が取消済みの場合のエラー処理を確認する<br>
   * 条件: 既存の離着陸場予約情報の予約有効フラグがfalseとなっている<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void update_ReservationActiveFlag_false() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortId(UUID.randomUUID().toString());
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setRouteReservationId(UUID.randomUUID().toString());
    dto.setDronePortReservationId(UUID.randomUUID().toString());
    dto.setReservationTimeFrom("2023-01-01T00:00:00Z");
    dto.setReservationTimeTo("2023-01-01T01:00:00Z");

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());
    dronePortInfoEntity.getDronePortStatusEntity().setOperatorId(operatorId);

    UUID existingReservationId = UUID.randomUUID();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortReservationId(existingReservationId);
    existingEntity.setReservationActiveFlag(false);
    existingEntity.setOperatorId(operatorId);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(dronePortReserveInfoRepository.save(any(DronePortReserveInfoEntity.class)))
        .thenReturn(existingEntity);

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行 & 検証
    assertThrows(
        ServiceErrorException.class, () -> dronePortReserveInfoServiceImpl.update(dto, userInfo));
  }

  /**
   * メソッド名: update<br>
   * 試験名: ポート形状が予約不可の場合のエラー処理を確認する<br>
   * 条件: 予約対象の離着陸場情報のポート形状が0:緊急着陸地点の場合<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void update_portType_invalid() {
    // ●準備
    String operatorId = "dummyOperator";
    UUID updateReservationId = UUID.randomUUID();
    UUID routeReservationId = UUID.randomUUID();
    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
    dto.setDronePortReservationId(updateReservationId.toString());
    dto.setRouteReservationId(routeReservationId.toString());
    dto.setUsageType(99);
    dto.setReservationTimeFrom("2023-01-01T00:00:00Z");
    dto.setReservationTimeTo("2023-01-01T01:00:00Z");

    UserInfoDto userInfo = createUserInfoDto();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setPortType(0);
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    UUID existingReservationId = UUID.randomUUID();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortReservationId(existingReservationId);
    existingEntity.setDronePortId(UUID.randomUUID().toString());
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setUsageType(98);
    existingEntity.setReservationActiveFlag(true);
    existingEntity.setOperatorId(operatorId);

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(
            existingEntity.getDronePortId()))
        .thenReturn(Optional.of(dronePortInfoEntity));
    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            updateReservationId))
        .thenReturn(Optional.of(existingEntity));

    AircraftInfoEntity aircraftEntity = new AircraftInfoEntity();
    aircraftEntity.setOperatorId(operatorId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(aircraftEntity));

    // ●実行 & 検証
    ServiceErrorException actualException =
        assertThrows(
            ServiceErrorException.class,
            () -> dronePortReserveInfoServiceImpl.update(dto, userInfo));

    assertEquals(
        "離着陸場のポート形状が予約可能ではありません:ポート形状:" + dronePortInfoEntity.getPortType(),
        actualException.getMessage());
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 正常系の削除処理が成功することを確認する<br>
   * 条件: 正常な離着陸場予約IDを渡す。離着陸場種別:VIS管理。<br>
   * 結果: 削除処理が成功する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void delete_success_vis() {
    // ●準備
    String dronePortReservationId = UUID.randomUUID().toString();
    String operatorId = "dummyOperator";

    UserInfoDto userInfo = createUserInfoDto();

    List<DronePortReserveInfoEntity> existingEntities = new ArrayList<>();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortId(UUID.randomUUID().toString());
    existingEntity.setOperatorId(operatorId);
    existingEntities.add(existingEntity);

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setOperatorId(operatorId);
    DronePortStatusEntity statusEntity = new DronePortStatusEntity();
    statusEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(statusEntity);

    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));

    // ●実行
    dronePortReserveInfoServiceImpl.delete(dronePortReservationId, true, userInfo);

    // ●検証
    verify(dronePortReserveInfoRepository, times(1)).saveAll(existingEntities);
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 正常系の削除処理が成功することを確認する<br>
   * 条件: 正常な離着陸場予約IDを渡す。離着陸場種別:自システム管理。<br>
   * 結果: 削除処理が成功する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void delete_success_internal() {
    // ●準備
    String dronePortReservationId = UUID.randomUUID().toString();
    String operatorId = "dummyOperator";

    UserInfoDto userInfo = createUserInfoDto();

    List<DronePortReserveInfoEntity> existingEntities = new ArrayList<>();
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setDronePortId(UUID.randomUUID().toString());
    existingEntity.setOperatorId(operatorId);

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setOperatorId(operatorId);
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());
    dronePortInfoEntity.getDronePortStatusEntity().setOperatorId(operatorId);
    existingEntities.add(existingEntity);

    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortInfoEntity));

    // ●実行
    dronePortReserveInfoServiceImpl.delete(dronePortReservationId, true, userInfo);

    // ●検証
    verify(dronePortReserveInfoRepository, times(1)).saveAll(existingEntities);
  }

  /**
   * メソッド名: delete<br>
   * 試験名: 離着陸場予約情報が見つからない場合のエラー処理を確認する<br>
   * 条件: 存在しない離着陸場予約IDを渡す<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void delete_reservationNotFound() {
    // ●準備
    String dronePortReservationId = UUID.randomUUID().toString();

    UserInfoDto userInfo = createUserInfoDto();

    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.empty());

    // ●実行 & 検証
    assertThrows(
        NotFoundException.class,
        () -> dronePortReserveInfoServiceImpl.delete(dronePortReservationId, true, userInfo));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 正常系のリスト取得処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　ソート条件なし<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索結果の予約情報が、使用機体ID、航路予約IDが設定あり。<br>
   * 　　　　　予約対象の離着陸場情報が、動作状況が1:準備中で、使用不可日時範囲未設定。<br>
   * 結果: リスト取得処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_success() {
    // ●準備
    String operatorId = "dummyOperator";
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setRouteReservationId(UUID.randomUUID().toString());
    dto.setTimeFrom("2023-01-01T00:00:00Z");
    dto.setTimeTo("2023-01-01T01:00:00Z");

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortName("dummyDronePortName");
    DronePortStatusEntity dronePortStatusEntity = new DronePortStatusEntity();
    dronePortStatusEntity.setActiveStatus(1); // 準備中
    AircraftInfoEntity aircraftInfoEntity = new AircraftInfoEntity();
    aircraftInfoEntity.setAircraftName("dummyAircraftName");
    DronePortReserveInfoEntity reserveEntity = new DronePortReserveInfoEntity();
    reserveEntity.setGroupReservationId(UUID.randomUUID());
    reserveEntity.setDronePortId(UUID.randomUUID().toString());
    reserveEntity.setDronePortReservationId(UUID.randomUUID());
    reserveEntity.setAircraftId(UUID.randomUUID());
    reserveEntity.setRouteReservationId(UUID.randomUUID());
    reserveEntity.setReservationTime(
        Range.localDateTimeRange("[2023-01-01T00:00:00,2023-01-01T01:00:00)"));
    reserveEntity.setDronePortInfoEntity(dronePortInfoEntity);
    reserveEntity.setAircraftInfoEntity(aircraftInfoEntity);
    reserveEntity.setDronePortStatusEntity(dronePortStatusEntity);
    reserveEntity.setReservationActiveFlag(false);
    reserveEntity.setReserveProviderId(UUID.randomUUID());
    reserveEntity.setOperatorId(operatorId);
    when(dronePortReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(reserveEntity));

    DronePortInfoEntity dronePortEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortEntity));

    // ●実行
    DronePortReserveInfoListResponseDto responseDto = dronePortReserveInfoServiceImpl.getList(dto);

    // 期待値設定
    DronePortReserveInfoListElement expectElem = new DronePortReserveInfoListElement();
    expectElem.setGroupReservationId(reserveEntity.getGroupReservationId().toString());
    expectElem.setDronePortId(reserveEntity.getDronePortId());
    expectElem.setDronePortReservationId(reserveEntity.getDronePortReservationId().toString());
    expectElem.setAircraftId(reserveEntity.getAircraftId().toString());
    expectElem.setRouteReservationId(reserveEntity.getRouteReservationId().toString());
    expectElem.setReservationTimeFrom("2023-01-01T00:00:00Z");
    expectElem.setReservationTimeTo("2023-01-01T01:00:00Z");
    expectElem.setAircraftName(reserveEntity.getAircraftInfoEntity().getAircraftName());
    expectElem.setDronePortName(reserveEntity.getDronePortInfoEntity().getDronePortName());
    expectElem.setReservationActiveFlag(false);
    expectElem.setReserveProviderId(reserveEntity.getReserveProviderId().toString());
    expectElem.setOperatorId(operatorId);

    // ●検証
    assertNotNull(responseDto);
    assertNotNull(responseDto.getData());
    assertFalse(responseDto.getData().isEmpty());
    assertEquals(expectElem.toString(), responseDto.getData().get(0).toString());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 正常系のリスト取得処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　ソート条件あり<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索結果の予約情報が、使用機体ID、航路予約IDが未設定。<br>
   * 　　　　　予約対象の離着陸場情報が、動作状況が3:使用不可で、使用不可日時範囲の開始日時を設定あり、終了日時を未設定。<br>
   * 結果: リスト取得処理が成功し、期待されるレスポンスDTOが返される。機体情報なし、航路予約IDなし。<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_success_2() {
    // ●準備
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    dto.setTimeFrom("2023-01-01T00:00:00Z");
    dto.setTimeTo("2023-01-01T01:00:00Z");
    dto.setSortOrders("0,1");
    dto.setSortColumns("a,b");

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortName("dummyDronePortName");
    DronePortStatusEntity dronePortStatusEntity = new DronePortStatusEntity();
    dronePortStatusEntity.setActiveStatus(3); // 使用不可
    dronePortStatusEntity.setInactiveTime(Range.localDateTimeRange("[2024-12-04T01:00:00,)"));
    DronePortReserveInfoEntity reserveEntity = new DronePortReserveInfoEntity();
    reserveEntity.setGroupReservationId(UUID.randomUUID());
    reserveEntity.setDronePortId(UUID.randomUUID().toString());
    reserveEntity.setDronePortReservationId(UUID.randomUUID());
    reserveEntity.setReservationTime(
        Range.localDateTimeRange("[2023-01-01T00:00:00,2023-01-01T01:00:00)"));
    reserveEntity.setDronePortInfoEntity(dronePortInfoEntity);
    reserveEntity.setDronePortStatusEntity(dronePortStatusEntity);
    reserveEntity.setReservationActiveFlag(true);
    reserveEntity.setReserveProviderId(UUID.randomUUID());
    when(dronePortReserveInfoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(reserveEntity));

    DronePortInfoEntity dronePortEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortEntity));

    // ●実行
    DronePortReserveInfoListResponseDto responseDto = dronePortReserveInfoServiceImpl.getList(dto);

    // 期待値設定
    DronePortReserveInfoListElement expectElem = new DronePortReserveInfoListElement();
    expectElem.setGroupReservationId(reserveEntity.getGroupReservationId().toString());
    expectElem.setDronePortId(reserveEntity.getDronePortId());
    expectElem.setDronePortReservationId(reserveEntity.getDronePortReservationId().toString());
    expectElem.setReservationTimeFrom("2023-01-01T00:00:00Z");
    expectElem.setReservationTimeTo("2023-01-01T01:00:00Z");
    expectElem.setDronePortName(reserveEntity.getDronePortInfoEntity().getDronePortName());
    expectElem.setReservationActiveFlag(true);
    expectElem.setInactiveTimeFrom("2024-12-04T01:00:00Z");
    expectElem.setInactiveTimeTo(null);
    expectElem.setReserveProviderId(reserveEntity.getReserveProviderId().toString());

    // ●検証
    assertNotNull(responseDto);
    assertNotNull(responseDto.getData());
    assertFalse(responseDto.getData().isEmpty());
    assertEquals(expectElem.toString(), responseDto.getData().get(0).toString());

    // findAllのソート条件を検証
    ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
    verify(dronePortReserveInfoRepository, times(1))
        .findAll(any(Specification.class), sortCaptor.capture());
    List<Order> orderList = sortCaptor.getValue().toList();
    assertTrue(orderList.get(0).isAscending());
    assertEquals("a", orderList.get(0).getProperty());
    assertTrue(orderList.get(1).isDescending());
    assertEquals("b", orderList.get(1).getProperty());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 正常系のリスト取得処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　ソート条件あり(ソート順とソート対象列名の設定個数不整合)<br>
   * 　　　　　ページ制御なし<br>
   * 　　　　　検索結果の予約情報が、使用機体ID、航路予約IDが未設定。<br>
   * 　　　　　予約対象の離着陸場情報が、動作状況が4:メンテナンス中で、使用不可日時範囲の開始日時を設定あり、終了日時を設定あり。<br>
   * 結果: リスト取得処理が成功し、期待されるレスポンスDTOが返される。機体情報なし、航路予約IDなし。<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_success_3() {
    // ●準備
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    dto.setTimeFrom("2023-01-01T00:00:00Z");
    dto.setTimeTo("2023-01-01T01:00:00Z");
    dto.setSortOrders(null);
    dto.setSortColumns("a,b");

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortName("dummyDronePortName");
    DronePortStatusEntity dronePortStatusEntity = new DronePortStatusEntity();
    dronePortStatusEntity.setActiveStatus(4); // 使用不可
    dronePortStatusEntity.setInactiveTime(
        Range.localDateTimeRange("[2024-12-04T01:00:00,2024-12-04T02:00:00)"));
    DronePortReserveInfoEntity reserveEntity = new DronePortReserveInfoEntity();
    reserveEntity.setGroupReservationId(UUID.randomUUID());
    reserveEntity.setDronePortId(UUID.randomUUID().toString());
    reserveEntity.setDronePortReservationId(UUID.randomUUID());
    reserveEntity.setReservationTime(
        Range.localDateTimeRange("[2023-01-01T00:00:00,2023-01-01T01:00:00)"));
    reserveEntity.setDronePortInfoEntity(dronePortInfoEntity);
    reserveEntity.setDronePortStatusEntity(dronePortStatusEntity);
    reserveEntity.setReservationActiveFlag(true);
    reserveEntity.setReserveProviderId(UUID.randomUUID());
    when(dronePortReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(reserveEntity));

    DronePortInfoEntity dronePortEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortEntity));

    // ●実行
    DronePortReserveInfoListResponseDto responseDto = dronePortReserveInfoServiceImpl.getList(dto);

    // 期待値設定
    DronePortReserveInfoListElement expectElem = new DronePortReserveInfoListElement();
    expectElem.setGroupReservationId(reserveEntity.getGroupReservationId().toString());
    expectElem.setDronePortId(reserveEntity.getDronePortId());
    expectElem.setDronePortReservationId(reserveEntity.getDronePortReservationId().toString());
    expectElem.setReservationTimeFrom("2023-01-01T00:00:00Z");
    expectElem.setReservationTimeTo("2023-01-01T01:00:00Z");
    expectElem.setDronePortName(reserveEntity.getDronePortInfoEntity().getDronePortName());
    expectElem.setReservationActiveFlag(true);
    expectElem.setInactiveTimeFrom("2024-12-04T01:00:00Z");
    expectElem.setInactiveTimeTo("2024-12-04T02:00:00Z");
    expectElem.setReserveProviderId(reserveEntity.getReserveProviderId().toString());

    // ●検証
    assertNotNull(responseDto);
    assertNotNull(responseDto.getData());
    assertFalse(responseDto.getData().isEmpty());
    assertEquals(expectElem.toString(), responseDto.getData().get(0).toString());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 正常系のリスト取得処理が成功することを確認する<br>
   * 条件: 正常なリクエストDTOを渡す。<br>
   * 　　　　　ソート条件なし<br>
   * 　　　　　ページ制御あり<br>
   * 　　　　　検索結果の予約情報が、使用機体ID、航路予約IDが設定あり。<br>
   * 　　　　　予約対象の離着陸場情報が、動作状況が1:準備中で、使用不可日時範囲未設定。<br>
   * 結果: リスト取得処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_success_4() {
    long total = 20;
    // ●準備
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    dto.setAircraftId(UUID.randomUUID().toString());
    dto.setRouteReservationId(UUID.randomUUID().toString());
    dto.setTimeFrom("2023-01-01T00:00:00Z");
    dto.setTimeTo("2023-01-01T01:00:00Z");
    Integer intPerPage = 10;
    Integer intPage = 1;
    dto.setPerPage(intPerPage.toString());
    dto.setPage(intPage.toString());

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortName("dummyDronePortName");
    DronePortStatusEntity dronePortStatusEntity = new DronePortStatusEntity();
    dronePortStatusEntity.setActiveStatus(1); // 準備中
    AircraftInfoEntity aircraftInfoEntity = new AircraftInfoEntity();
    aircraftInfoEntity.setAircraftName("dummyAircraftName");
    DronePortReserveInfoEntity reserveEntity = new DronePortReserveInfoEntity();
    reserveEntity.setGroupReservationId(UUID.randomUUID());
    reserveEntity.setDronePortId(UUID.randomUUID().toString());
    reserveEntity.setDronePortReservationId(UUID.randomUUID());
    reserveEntity.setAircraftId(UUID.randomUUID());
    reserveEntity.setRouteReservationId(UUID.randomUUID());
    reserveEntity.setReservationTime(
        Range.localDateTimeRange("[2023-01-01T00:00:00,2023-01-01T01:00:00)"));
    reserveEntity.setDronePortInfoEntity(dronePortInfoEntity);
    reserveEntity.setAircraftInfoEntity(aircraftInfoEntity);
    reserveEntity.setDronePortStatusEntity(dronePortStatusEntity);
    reserveEntity.setReservationActiveFlag(false);
    reserveEntity.setReserveProviderId(UUID.randomUUID());

    Page<DronePortReserveInfoEntity> page =
        new PageImpl<>(
            List.of(reserveEntity),
            PageRequest.of(intPage - 1, intPerPage, Sort.unsorted()),
            total);
    when(dronePortReserveInfoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);

    DronePortInfoEntity dronePortEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortStatusEntity(new DronePortStatusEntity());

    when(dronePortInfoRepository.findByDronePortIdAndDeleteFlagFalse(any(String.class)))
        .thenReturn(Optional.of(dronePortEntity));

    // ●実行
    DronePortReserveInfoListResponseDto responseDto = dronePortReserveInfoServiceImpl.getList(dto);

    // 期待値設定
    DronePortReserveInfoListElement expectElem = new DronePortReserveInfoListElement();
    expectElem.setGroupReservationId(reserveEntity.getGroupReservationId().toString());
    expectElem.setDronePortId(reserveEntity.getDronePortId());
    expectElem.setDronePortReservationId(reserveEntity.getDronePortReservationId().toString());
    expectElem.setAircraftId(reserveEntity.getAircraftId().toString());
    expectElem.setRouteReservationId(reserveEntity.getRouteReservationId().toString());
    expectElem.setReservationTimeFrom("2023-01-01T00:00:00Z");
    expectElem.setReservationTimeTo("2023-01-01T01:00:00Z");
    expectElem.setAircraftName(reserveEntity.getAircraftInfoEntity().getAircraftName());
    expectElem.setDronePortName(reserveEntity.getDronePortInfoEntity().getDronePortName());
    expectElem.setReservationActiveFlag(false);
    expectElem.setReserveProviderId(reserveEntity.getReserveProviderId().toString());

    // ●検証
    assertNotNull(responseDto);
    assertNotNull(responseDto.getData());
    assertFalse(responseDto.getData().isEmpty());
    assertEquals(expectElem.toString(), responseDto.getData().get(0).toString());

    assertEquals(dto.getPerPage(), responseDto.getPerPage().toString());
    assertEquals(dto.getPage(), responseDto.getCurrentPage().toString());
    assertEquals((int) total / intPerPage, responseDto.getLastPage());
    assertEquals(total, (long) responseDto.getTotal());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 正常系のリスト取得処理が成功することを確認する。結果0件。<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 　　　　　ソート条件なし<br>
   * 　　　　　ページ制御なし<br>
   * 結果: リスト取得処理が成功し、期待されるレスポンスDTOが返される。結果0件。<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_success_emptyResult() {
    // ●準備
    DronePortReserveInfoListRequestDto dto = new DronePortReserveInfoListRequestDto();
    dto.setTimeFrom("2023-01-01T00:00:00Z");
    dto.setTimeTo("2023-01-01T01:00:00Z");

    when(dronePortReserveInfoRepository.findAll(any(Specification.class), isNull(Sort.class)))
        .thenReturn(List.of());

    // ●実行
    DronePortReserveInfoListResponseDto responseDto = dronePortReserveInfoServiceImpl.getList(dto);

    // ●検証
    assertNotNull(responseDto);
    assertNotNull(responseDto.getData());
    assertTrue(responseDto.getData().isEmpty());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 正常系の詳細取得処理が成功することを確認する<br>
   * 条件: 正常な離着陸場予約IDを渡す<br>
   * 結果: 詳細取得処理が成功し、期待されるレスポンスDTOが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_success() {
    // ●準備
    String operatorId = "dummyOperator";
    String dronePortReservationId = UUID.randomUUID().toString();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortName("dummyDronePortName");
    AircraftInfoEntity aircraftInfoEntity = new AircraftInfoEntity();
    aircraftInfoEntity.setAircraftName("dummyAircraftName");
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setGroupReservationId(UUID.randomUUID());
    existingEntity.setReservationTime(
        Range.localDateTimeRange("[2023-01-01T00:00:00,2023-01-01T01:00:00)"));
    existingEntity.setDronePortReservationId(UUID.randomUUID());
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setDronePortId(UUID.randomUUID().toString());
    existingEntity.setRouteReservationId(UUID.randomUUID());
    existingEntity.setDronePortInfoEntity(dronePortInfoEntity);
    existingEntity.setAircraftInfoEntity(aircraftInfoEntity);
    existingEntity.setReservationActiveFlag(true);
    existingEntity.setOperatorId(operatorId);
    existingEntity.setReserveProviderId(UUID.randomUUID());

    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    // ●実行
    DronePortReserveInfoDetailResponseDto responseDto =
        dronePortReserveInfoServiceImpl.getDetail(dronePortReservationId);

    // 期待値設定
    DronePortReserveInfoDetailResponseDto expectDto = new DronePortReserveInfoDetailResponseDto();
    expectDto.setGroupReservationId(existingEntity.getGroupReservationId().toString());
    expectDto.setDronePortId(existingEntity.getDronePortId());
    expectDto.setDronePortReservationId(existingEntity.getDronePortReservationId().toString());
    expectDto.setAircraftId(existingEntity.getAircraftId().toString());
    expectDto.setRouteReservationId(existingEntity.getRouteReservationId().toString());
    expectDto.setReservationTimeFrom("2023-01-01T00:00:00Z");
    expectDto.setReservationTimeTo("2023-01-01T01:00:00Z");
    expectDto.setAircraftName(existingEntity.getAircraftInfoEntity().getAircraftName());
    expectDto.setDronePortName(existingEntity.getDronePortInfoEntity().getDronePortName());
    expectDto.setReservationActiveFlag(true);
    expectDto.setOperatorId(operatorId);
    expectDto.setReserveProviderId(existingEntity.getReserveProviderId().toString());

    // ●検証
    assertNotNull(responseDto);
    assertEquals(expectDto.toString(), responseDto.toString());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 正常系の詳細取得処理が成功することを確認する<br>
   * 条件: 正常な離着陸場予約IDを渡す<br>
   * 結果: 詳細取得処理が成功し、期待されるレスポンスDTOが返される。機体情報なし、航路予約IDなし。<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_success2() {
    // ●準備
    String dronePortReservationId = UUID.randomUUID().toString();

    DronePortInfoEntity dronePortInfoEntity = new DronePortInfoEntity();
    dronePortInfoEntity.setDronePortName("dummyDronePortName");
    DronePortReserveInfoEntity existingEntity = new DronePortReserveInfoEntity();
    existingEntity.setGroupReservationId(UUID.randomUUID());
    existingEntity.setReservationTime(
        Range.localDateTimeRange("[2023-01-01T00:00:00,2023-01-01T01:00:00)"));
    existingEntity.setDronePortReservationId(UUID.randomUUID());
    existingEntity.setDronePortId(UUID.randomUUID().toString());
    existingEntity.setDronePortInfoEntity(dronePortInfoEntity);
    existingEntity.setReservationActiveFlag(false);
    existingEntity.setReserveProviderId(UUID.randomUUID());

    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    // ●実行
    DronePortReserveInfoDetailResponseDto responseDto =
        dronePortReserveInfoServiceImpl.getDetail(dronePortReservationId);

    // 期待値設定
    DronePortReserveInfoDetailResponseDto expectDto = new DronePortReserveInfoDetailResponseDto();
    expectDto.setGroupReservationId(existingEntity.getGroupReservationId().toString());
    expectDto.setDronePortId(existingEntity.getDronePortId());
    expectDto.setDronePortReservationId(existingEntity.getDronePortReservationId().toString());
    expectDto.setReservationTimeFrom("2023-01-01T00:00:00Z");
    expectDto.setReservationTimeTo("2023-01-01T01:00:00Z");
    expectDto.setDronePortName(existingEntity.getDronePortInfoEntity().getDronePortName());
    expectDto.setReservationActiveFlag(false);
    expectDto.setReserveProviderId(existingEntity.getReserveProviderId().toString());

    // ●検証
    assertNotNull(responseDto);
    assertEquals(expectDto.toString(), responseDto.toString());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 離着陸場予約情報が見つからない場合のエラー処理を確認する<br>
   * 条件: 存在しない離着陸場予約IDを渡す<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_reservationNotFound() {
    // ●準備
    String dronePortReservationId = UUID.randomUUID().toString();

    when(dronePortReserveInfoRepository.findByDronePortReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.empty());

    // ●実行 & 検証
    assertThrows(
        NotFoundException.class,
        () -> dronePortReserveInfoServiceImpl.getDetail(dronePortReservationId));
  }

  /**
   * メソッド名: setEntity<br>
   * 試験名: 離着陸場予約情報登録更新要求DTOを離着陸場情報エンティティに設定する処理を確認する。<br>
   * 条件: 離着陸場IDを含むDTOの全ての項目がnullの場合。<br>
   * 結果: 例外が発生しないこと。<br>
   * テストパターン：正常系<br>
   */
  @Test
  void setEntity_1() throws Exception {
    Method targetMethod =
        DronePortReserveInfoServiceImpl.class.getDeclaredMethod(
            "setEntity",
            String.class,
            String.class,
            String.class,
            String.class,
            Integer.class,
            String.class,
            String.class,
            DronePortReserveInfoEntity.class);
    targetMethod.setAccessible(true);

    DronePortReserveInfoEntity entity = new DronePortReserveInfoEntity();

    targetMethod.invoke(
        dronePortReserveInfoServiceImpl, null, null, null, null, null, null, null, entity);
  }

  /**
   * メソッド名: setEntity<br>
   * 試験名: 離着陸場予約情報登録更新要求DTOを離着陸場情報エンティティに設定する処理を確認する。<br>
   * 条件: 予約開始日時がnull以外、予約終了日時がnullの場合。<br>
   * 結果: 例外が発生しないこと。<br>
   * テストパターン：正常系<br>
   */
  @Test
  void setEntity_2() throws Exception {
    Method targetMethod =
        DronePortReserveInfoServiceImpl.class.getDeclaredMethod(
            "setEntity",
            String.class,
            String.class,
            String.class,
            String.class,
            Integer.class,
            String.class,
            String.class,
            DronePortReserveInfoEntity.class);
    targetMethod.setAccessible(true);

    DronePortReserveInfoEntity entity = new DronePortReserveInfoEntity();

    targetMethod.invoke(
        dronePortReserveInfoServiceImpl, null, null, null, null, null, "dummyTime", null, entity);
  }

  /** データテンプレート ■登録更新リクエスト ユーザー情報_正常リクエストヘッダ */
  private static UserInfoDto createUserInfoDto() {
    List<RoleInfoDto> roles = new ArrayList<>();
    RoleInfoDto roleInfoDto = new RoleInfoDto();
    roleInfoDto.setRoleId("10");
    roleInfoDto.setRoleName("航路運営者_責任者");
    roles.add(roleInfoDto);

    UserInfoDto ret = new UserInfoDto();
    ret.setUserOperatorId(UUID.randomUUID().toString());
    ret.setRoles(roles);
    ret.setAffiliatedOperatorId(UUID.randomUUID().toString());
    return ret;
  }
}
