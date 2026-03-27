package com.hitachi.droneroute.arm.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.arm.dto.AircraftReserveInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoResponseDto;
import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.arm.entity.AircraftReserveInfoEntity;
import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
import com.hitachi.droneroute.arm.repository.AircraftReserveInfoRepository;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.config.dto.RoleInfoDto;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import io.hypersistence.utils.hibernate.type.range.Range;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

/** AircraftReserveInfoServiceImplクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
public class AircraftReserveInfoServiceImplTest {

  @MockBean private AircraftReserveInfoRepository aircraftReserveInfoRepository;

  @MockBean private AircraftInfoRepository aircraftInfoRepository;

  @Autowired private AircraftReserveInfoServiceImpl aircraftReserveInfoServiceImpl;

  @SpyBean private SystemSettings systemSettings;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体予約情報を正常に登録できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoRequestDtoを入力する<br>
   * 結果: 登録されたAircraftReserveInfoResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testPostData_Nomal() {
    // String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index);

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftId(aircraftId.toString());
    request.setReservationTimeFrom(timedata + "+09:00");
    request.setReservationTimeTo(timedata + "+09:00");
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    Range<LocalDateTime> rLocaldatetime =
        Range.localDateTimeRange(String.format("[%s,%s)", timedata, timedata));
    entity.setReservationTime(rLocaldatetime);
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);

    AircraftReserveInfoResponseDto response =
        aircraftReserveInfoServiceImpl.postData(request, userInfo);

    assertNotNull(response);
    assertEquals(entity.getAircraftReservationId().toString(), response.getAircraftReservationId());

    ArgumentCaptor<AircraftReserveInfoEntity> entityCaptor =
        ArgumentCaptor.forClass(AircraftReserveInfoEntity.class);
    verify(aircraftReserveInfoRepository).save(entityCaptor.capture());
    AircraftReserveInfoEntity argEntity = entityCaptor.getValue();
    assertEquals(request.getAircraftId(), argEntity.getAircraftId().toString());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeFrom()),
        argEntity.getReservationTime().lower());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeTo()),
        argEntity.getReservationTime().upper());
    assertEquals(userInfo.getUserOperatorId(), argEntity.getOperatorId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体予約情報を正常に登録できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoRequestDtoを入力する<br>
   * 結果: 登録されたAircraftReserveInfoResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testPostData_Nomal2() {
    // String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index);

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId("");
    request.setAircraftId(aircraftId.toString());
    request.setReservationTimeFrom(timedata + "+09:00");
    request.setReservationTimeTo(timedata + "+09:00");
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    Range<LocalDateTime> rLocaldatetime =
        Range.localDateTimeRange(String.format("[%s,%s)", timedata, timedata));
    entity.setReservationTime(rLocaldatetime);
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);

    AircraftReserveInfoResponseDto response =
        aircraftReserveInfoServiceImpl.postData(request, userInfo);

    assertNotNull(response);
    assertEquals(entity.getAircraftReservationId().toString(), response.getAircraftReservationId());

    ArgumentCaptor<AircraftReserveInfoEntity> entityCaptor =
        ArgumentCaptor.forClass(AircraftReserveInfoEntity.class);
    verify(aircraftReserveInfoRepository).save(entityCaptor.capture());
    AircraftReserveInfoEntity argEntity = entityCaptor.getValue();
    assertEquals(request.getAircraftId(), argEntity.getAircraftId().toString());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeFrom()),
        argEntity.getReservationTime().lower());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeTo()),
        argEntity.getReservationTime().upper());
    assertEquals(userInfo.getUserOperatorId(), argEntity.getOperatorId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 対象データが存在しない場合に例外が発生することを確認する<br>
   * 条件: 存在しないIDを渡す<br>
   * 結果: ServiceErrorExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testPostData_NotFound1() {
    // String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index);

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setAircraftId(aircraftId.toString());
    request.setReservationTimeFrom(timedata + "+09:00");
    request.setReservationTimeTo(timedata + "+09:00");
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    Range<LocalDateTime> rLocaldatetime =
        Range.localDateTimeRange(String.format("[%s,%s)", timedata, timedata));
    entity.setReservationTime(rLocaldatetime);
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.empty());

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftReserveInfoServiceImpl.postData(request, userInfo));

    assertEquals("機体IDが存在しません:機体ID:" + aircraftId.toString(), exception.getMessage());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体予約情報が重複している場合に例外が発生することを確認する<br>
   * 条件: 重複するIDを渡す<br>
   * 結果: ServiceErrorExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPostData_NotFound2() {
    // String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index);

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setAircraftId(aircraftId.toString());
    request.setReservationTimeFrom(timedata + "+09:00");
    request.setReservationTimeTo(timedata + "+09:00");
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    Range<LocalDateTime> rLocaldatetime =
        Range.localDateTimeRange(String.format("[%s,%s)", timedata, timedata));
    entity.setReservationTime(rLocaldatetime);
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);

    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity));

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftReserveInfoServiceImpl.postData(request, userInfo));

    assertEquals("他の予約と被っているため、予約できません", exception.getMessage());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体予約情報の生成に失敗している場合に例外が発生することを確認する<br>
   * 条件: 生成時に失敗データを返す<br>
   * 結果: ServiceErrorExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testPostData_NotFound3() {
    // String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index);

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setAircraftId(aircraftId.toString());
    request.setReservationTimeFrom(timedata + "+09:00");
    request.setReservationTimeTo(timedata + "+09:00");
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(null);
    entity.setAircraftId(aircraftId);
    Range<LocalDateTime> rLocaldatetime =
        Range.localDateTimeRange(String.format("[%s,%s)", timedata, timedata));
    entity.setReservationTime(rLocaldatetime);
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftReserveInfoServiceImpl.postData(request, userInfo));

    assertEquals("機体予約IDの生成に失敗しました。", exception.getMessage());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体IDが空文字の場合に例外が発生することを確認する<br>
   * 条件: 機体IDに空文字を設定する<br>
   * 結果: ServiceErrorExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testPostData_NotFound4() {
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index);

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setAircraftId("");
    request.setReservationTimeFrom(timedata + "+09:00");
    request.setReservationTimeTo(timedata + "+09:00");

    UserInfoDto userInfo = createUserInfoDto();

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftReserveInfoServiceImpl.postData(request, userInfo));

    assertEquals("機体IDが入力されていません。", exception.getMessage());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体IDがnullの場合に例外が発生することを確認する<br>
   * 条件: 機体IDにnullを設定する<br>
   * 結果: ServiceErrorExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testPostData_NotFound5() {
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index);

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setAircraftId(null);
    request.setReservationTimeFrom(timedata + "+09:00");
    request.setReservationTimeTo(timedata + "+09:00");

    UserInfoDto userInfo = createUserInfoDto();

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftReserveInfoServiceImpl.postData(request, userInfo));

    assertEquals("機体IDが入力されていません。", exception.getMessage());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoRequestDtoを入力する<br>
   * 結果: 更新されたAircraftReserveInfoResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testPutData_Nomal() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index) + "+09:00";

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setAircraftId(aircraftId.toString());
    request.setReservationTimeFrom(timedata);
    request.setReservationTimeTo(timedata);
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
    entity.setOperatorId(operatorId);
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setOperatorId(operatorId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            aircraftreservationId))
        .thenReturn(Optional.of(entity));
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    AircraftReserveInfoResponseDto response =
        aircraftReserveInfoServiceImpl.putData(request, userInfo);

    assertNotNull(response);
    assertEquals(entity.getAircraftReservationId().toString(), response.getAircraftReservationId());

    ArgumentCaptor<AircraftReserveInfoEntity> entityCaptor =
        ArgumentCaptor.forClass(AircraftReserveInfoEntity.class);
    verify(aircraftReserveInfoRepository).save(entityCaptor.capture());
    AircraftReserveInfoEntity argEntity = entityCaptor.getValue();
    assertEquals(
        request.getAircraftReservationId(), argEntity.getAircraftReservationId().toString());
    assertEquals(request.getAircraftId(), argEntity.getAircraftId().toString());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeFrom()),
        argEntity.getReservationTime().lower());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeTo()),
        argEntity.getReservationTime().upper());
    assertEquals(userInfo.getUserOperatorId(), argEntity.getOperatorId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoRequestDtoを入力する<br>
   * 結果: 更新されたAircraftReserveInfoResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPutData_Nomal2() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index) + "+09:00";

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setAircraftId("");
    request.setReservationTimeFrom(timedata);
    request.setReservationTimeTo(timedata);
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
    entity.setOperatorId(operatorId);
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setOperatorId(operatorId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            aircraftreservationId))
        .thenReturn(Optional.of(entity));
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);
    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity));
    AircraftReserveInfoResponseDto response =
        aircraftReserveInfoServiceImpl.putData(request, userInfo);

    assertNotNull(response);
    assertEquals(entity.getAircraftReservationId().toString(), response.getAircraftReservationId());

    ArgumentCaptor<AircraftReserveInfoEntity> entityCaptor =
        ArgumentCaptor.forClass(AircraftReserveInfoEntity.class);
    verify(aircraftReserveInfoRepository).save(entityCaptor.capture());
    AircraftReserveInfoEntity argEntity = entityCaptor.getValue();
    assertEquals(
        request.getAircraftReservationId(), argEntity.getAircraftReservationId().toString());
    assertEquals(aircraftId, argEntity.getAircraftId());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeFrom()),
        argEntity.getReservationTime().lower());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeTo()),
        argEntity.getReservationTime().upper());
    assertEquals(userInfo.getUserOperatorId(), argEntity.getOperatorId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機体予約情報を更新できないことを確認する<br>
   * 条件: 正常なAircraftReserveInfoRequestDtoを入力する(ただし結果は重複する予約が2つ存在する)<br>
   * 結果: ServiceErrorExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPutData_重複1() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index) + "+09:00";

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setAircraftId(aircraftId.toString());
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
    entity.setOperatorId(operatorId);

    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setOperatorId(operatorId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            aircraftreservationId))
        .thenReturn(Optional.of(entity));
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    List<AircraftReserveInfoEntity> entityList = new ArrayList<>();
    entityList.add(entity);
    entityList.add(entity);
    when(aircraftReserveInfoRepository.findAll(any(Specification.class))).thenReturn(entityList);

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));

    assertEquals("他の予約と被っているため、予約できません", exception.getMessage());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機体予約情報を更新できないことを確認する<br>
   * 条件: 正常なAircraftReserveInfoRequestDtoを入力する(ただし結果は別の予約と重複する)<br>
   * 結果: ServiceErrorExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPutData_重複2() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index) + "+09:00";

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setAircraftId(aircraftId.toString());
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
    entity.setOperatorId(operatorId);
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setOperatorId(operatorId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            aircraftreservationId))
        .thenReturn(Optional.of(entity));
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    UUID aircraftreservationId2 = UUID.randomUUID();
    AircraftReserveInfoEntity entity3 = new AircraftReserveInfoEntity();
    entity3.setAircraftReservationId(UUID.fromString(aircraftreservationId2.toString()));
    entity3.setAircraftId(aircraftId);
    entity3.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));

    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity3));

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));

    assertEquals("他の予約と被っているため、予約できません", exception.getMessage());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoRequestDtoを入力する<br>
   * 結果: 更新されたAircraftReserveInfoResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testPutData_No_AircraftId() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index) + "+09:00";

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setReservationTimeFrom(timedata);
    request.setReservationTimeTo(timedata);
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
    entity.setOperatorId(operatorId);
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setOperatorId(operatorId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            aircraftreservationId))
        .thenReturn(Optional.of(entity));
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    AircraftReserveInfoResponseDto response =
        aircraftReserveInfoServiceImpl.putData(request, userInfo);

    assertNotNull(response);
    assertEquals(entity.getAircraftReservationId().toString(), response.getAircraftReservationId());
    ArgumentCaptor<AircraftReserveInfoEntity> entityCaptor =
        ArgumentCaptor.forClass(AircraftReserveInfoEntity.class);
    verify(aircraftReserveInfoRepository).save(entityCaptor.capture());
    AircraftReserveInfoEntity argEntity = entityCaptor.getValue();
    assertEquals(
        request.getAircraftReservationId(), argEntity.getAircraftReservationId().toString());
    assertEquals(aircraftId.toString(), argEntity.getAircraftId().toString());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeFrom()),
        argEntity.getReservationTime().lower());
    assertEquals(
        StringUtils.parseDatetimeStringToLocalDateTime(request.getReservationTimeTo()),
        argEntity.getReservationTime().upper());
    assertEquals(userInfo.getUserOperatorId(), argEntity.getOperatorId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 存在しないデータを更新しようとした場合に例外が発生することを確認する<br>
   * 条件: 存在しないIDを渡す<br>
   * 結果: NotFoundExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testPutData_NotFound() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setOperatorId(operatorId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));

    assertEquals(
        "機体予約IDが見つかりません。機体予約ID:" + aircraftreservationId.toString(), exception.getMessage());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 存在しないデータを更新しようとした場合に例外が発生することを確認する<br>
   * 条件: 存在しないIDを渡す<br>
   * 結果: ServiceErrorExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testPutData_NotFound_AircraftId() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index) + "+09:00";

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
    entity.setOperatorId(operatorId);

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.empty());
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            aircraftreservationId))
        .thenReturn(Optional.of(entity));

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));

    assertEquals("機体IDが存在しません:機体ID:" + aircraftId.toString(), exception.getMessage());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 更新に失敗した場合に例外が発生することを確認する<br>
   * 条件: 更新データにNullを返す<br>
   * 結果: ServiceErrorExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testPutData_NotFound_AircraftReserveId() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index) + "+09:00";

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
    entity.setOperatorId(operatorId);

    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setOperatorId(operatorId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            aircraftreservationId))
        .thenReturn(Optional.of(entity));

    AircraftReserveInfoEntity entity3 = new AircraftReserveInfoEntity();
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity3);

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(optEntity);

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));

    assertEquals("機体予約情報の更新に失敗しました。", exception.getMessage());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 開始時間がなくても機体予約情報を正常に更新できることを確認する<br>
   * 条件: 開始時間抜きの正常なAircraftReserveInfoRequestDtoを入力する<br>
   * 結果: 更新されたAircraftReserveInfoResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testPutData_NotTimeFrom() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index) + "+09:00";

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setReservationTimeTo(timedata);
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
    entity.setOperatorId(operatorId);
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setOperatorId(operatorId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            aircraftreservationId))
        .thenReturn(Optional.of(entity));
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    AircraftReserveInfoResponseDto response =
        aircraftReserveInfoServiceImpl.putData(request, userInfo);

    assertNotNull(response);
    assertEquals(entity.getAircraftReservationId().toString(), response.getAircraftReservationId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 終了時間がなくても機体予約情報を正常に更新できることを確認する<br>
   * 条件: 終了時間抜きの正常なAircraftReserveInfoRequestDtoを入力する<br>
   * 結果: 更新されたAircraftReserveInfoResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testPutData_NotTimeTo() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
    int index = basetime.toString().indexOf(".");
    String timedata = basetime.toString().substring(0, index) + "+09:00";

    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
    request.setAircraftReservationId(aircraftreservationId.toString());
    request.setReservationTimeFrom(timedata);
    // request.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
    entity.setOperatorId(operatorId);
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setOperatorId(operatorId);
    Optional<AircraftInfoEntity> optEntity = Optional.of(entity2);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(aircraftId))
        .thenReturn(optEntity);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            aircraftreservationId))
        .thenReturn(Optional.of(entity));
    when(aircraftReserveInfoRepository.save(any(AircraftReserveInfoEntity.class)))
        .thenReturn(entity);

    AircraftReserveInfoResponseDto response =
        aircraftReserveInfoServiceImpl.putData(request, userInfo);

    assertNotNull(response);
    assertEquals(entity.getAircraftReservationId().toString(), response.getAircraftReservationId());
  }

  /**
   * メソッド名: pudData<br>
   * 試験名: リクエストのオペレータIDと機体予約情報のオペレータIDが不一致の場合の認可エラー処理を確認する<br>
   * 条件: リクエストのオペレータIDと機体予約情報のオペレータIDが不一致の場合<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  //  @Test
  //  public void testPutData_permission_error() {
  //    String operatorId = "dummyOperator";
  //    UUID aircraftId = UUID.randomUUID();
  //    UUID aircraftreservationId = UUID.randomUUID();
  //    LocalDateTime basetime = ZonedDateTime.now().toLocalDateTime();
  //    int index = basetime.toString().indexOf(".");
  //    String timedata = basetime.toString().substring(0, index) + "+09:00";
  //
  //    AircraftReserveInfoRequestDto request = new AircraftReserveInfoRequestDto();
  //    request.setAircraftReservationId(aircraftreservationId.toString());
  //    request.setAircraftId(aircraftId.toString());
  //    request.setReservationTimeFrom(timedata);
  //    request.setReservationTimeTo(timedata);
  //    request.setOperatorId(operatorId);
  //
  //    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
  //    entity.setAircraftReservationId(UUID.fromString(request.getAircraftReservationId()));
  //    entity.setAircraftId(aircraftId);
  //    entity.setReservationTime(
  //        Range.localDateTimeRange(
  //            String.format(
  //                "[%s,%s)",
  //                StringUtils.parseDatetimeStringToLocalDateTime(timedata),
  //                StringUtils.parseDatetimeStringToLocalDateTime(timedata))));
  //    entity.setOperatorId(operatorId + "xyz");
  //    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
  //            aircraftreservationId))
  //        .thenReturn(Optional.of(entity));
  //
  //    ServiceErrorException exception =
  //        assertThrows(
  //            ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl.putData(request));
  //
  //    assertEquals("認可エラー。オペレータIDが一致しません。", exception.getMessage());
  //
  //    verify(aircraftReserveInfoRepository, times(0)).save(any(AircraftReserveInfoEntity.class));
  //  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
   * 条件: 正常なaircraftRevservationIdを入力する<br>
   * 結果: 例外が発生しないこと<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testDeleteData_Nomal() {
    String operatorId = "dummyOperator";
    // AircraftInfoDeleteRequestDto dto = new AircraftInfoDeleteRequestDto();
    // dto.setOperatorId(operatorId);

    UserInfoDto userInfo = createUserInfoDto();

    UUID aircraftReservationId = UUID.randomUUID();
    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftReservationId);
    entity.setOperatorId(operatorId);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(entity));

    assertDoesNotThrow(
        () ->
            aircraftReserveInfoServiceImpl.deleteData(
                aircraftReservationId.toString(), true, userInfo));
    verify(aircraftReserveInfoRepository, times(1)).save(entity);
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 存在しないデータを削除しようとした場合に例外が発生することを確認する<br>
   * 条件: 存在しないIDを渡す<br>
   * 結果: NotFoundExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testDeleteData_NotFound() {
    // String operatorId = "dummyOperator";
    // AircraftInfoDeleteRequestDto dto = new AircraftInfoDeleteRequestDto();
    // dto.setOperatorId(operatorId);

    UUID aircraftReservationId = UUID.randomUUID();
    UserInfoDto userInfo = createUserInfoDto();

    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () ->
            aircraftReserveInfoServiceImpl.deleteData(
                aircraftReservationId.toString(), true, userInfo));
  }

  //  @Test
  //  public void testDeleteData_permission_error() {
  //    String operatorId = "dummyOperator";
  //    AircraftInfoDeleteRequestDto dto = new AircraftInfoDeleteRequestDto();
  //    dto.setOperatorId(operatorId);
  //
  //    UUID aircraftReservationId = UUID.randomUUID();
  //    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
  //    entity.setAircraftReservationId(aircraftReservationId);
  //    entity.setOperatorId(operatorId + "xyz");
  //    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
  //            any(UUID.class)))
  //        .thenReturn(Optional.of(entity));
  //
  //    ServiceErrorException exception =
  //        assertThrows(
  //            ServiceErrorException.class,
  //            () -> aircraftReserveInfoServiceImpl.deleteData(aircraftReservationId.toString(),
  // dto));
  //
  //    assertEquals("認可エラー。オペレータIDが一致しません。", exception.getMessage());
  //    verify(aircraftReserveInfoRepository, times(0)).save(entity);
  //  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoListRequestDtoを入力する<br>
   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetList_Nomal() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    UUID groupReservationId = UUID.randomUUID();
    String time = ZonedDateTime.now().toString();
    String aircraftName = "aircraftName";
    UUID reserveProviderId = UUID.randomUUID();

    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
    request.setAircraftId(aircraftId.toString());
    request.setAircraftName(aircraftName);
    request.setTimeFrom(time);
    request.setTimeTo(time);

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setGroupReservationId(groupReservationId);
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(time),
                StringUtils.parseDatetimeStringToLocalDateTime(time))));
    entity.setReserveProviderId(reserveProviderId);
    entity.setOperatorId(operatorId);
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName(aircraftName);
    entity2.setOperatorId(operatorId);
    entity.setAircraftEntity(entity2);

    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity));

    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);

    assertNotNull(response);
    assertEquals(1, response.getData().size());

    for (AircraftReserveInfoDetailResponseDto dto : response.getData()) {
      assertEquals(dto.getAircraftId(), entity.getAircraftId().toString());
      assertEquals(dto.getAircraftReservationId(), entity.getAircraftReservationId().toString());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeFrom()),
          entity.getReservationTime().lower());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeTo()),
          entity.getReservationTime().upper());
      assertEquals(dto.getAircraftName(), entity.getAircraftEntity().getAircraftName());
      assertEquals(operatorId, dto.getOperatorId());
    }
    assertNull(response.getPerPage());
    assertNull(response.getCurrentPage());
    assertNull(response.getLastPage());
    assertNull(response.getTotal());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoListRequestDtoを入力する<br>
   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetList_Nomal2() {
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    String time = ZonedDateTime.now().toString();
    String aircraftName = "aircraftName";
    String perPage = "1";
    String page = "1";
    Integer lastPage = 1;
    Integer total = 1;
    String sortOrder = "1";
    String sortColumns = "aircraftId";

    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
    request.setAircraftId(aircraftId.toString());
    request.setAircraftName(aircraftName);
    request.setTimeFrom(time);
    request.setTimeTo(time);
    request.setPerPage(perPage);
    request.setPage(page);
    request.setSortOrders(sortOrder);
    request.setSortColumns(sortColumns);

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(time),
                StringUtils.parseDatetimeStringToLocalDateTime(time))));
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName(aircraftName);
    entity.setAircraftEntity(entity2);
    List<AircraftReserveInfoEntity> listEntity = List.of(entity);
    Page<AircraftReserveInfoEntity> pageList = new PageImpl<>(listEntity.subList(0, 1));

    when(aircraftReserveInfoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pageList);

    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);

    assertNotNull(response);
    assertEquals(1, response.getData().size());

    for (AircraftReserveInfoDetailResponseDto dto : response.getData()) {
      assertEquals(dto.getAircraftId(), entity.getAircraftId().toString());
      assertEquals(dto.getAircraftReservationId(), entity.getAircraftReservationId().toString());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeFrom()),
          entity.getReservationTime().lower());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeTo()),
          entity.getReservationTime().upper());
      assertEquals(dto.getAircraftName(), entity.getAircraftEntity().getAircraftName());
    }
    assertEquals(response.getPerPage().toString(), perPage);
    assertEquals(response.getCurrentPage().toString(), page);
    assertEquals(response.getLastPage(), lastPage);
    assertEquals(response.getTotal(), total);
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoListRequestDtoを入力する<br>
   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetList_Nomal3() {
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    String time = ZonedDateTime.now().toString();
    String aircraftName = "aircraftName";
    String sortOrder = "1";
    String sortColumns = "aircraftId";

    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
    request.setAircraftId(aircraftId.toString());
    request.setAircraftName(aircraftName);
    request.setTimeFrom(time);
    request.setTimeTo(time);
    request.setSortOrders(sortOrder);
    request.setSortColumns(sortColumns);

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(time),
                StringUtils.parseDatetimeStringToLocalDateTime(time))));
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName(aircraftName);
    entity.setAircraftEntity(entity2);

    when(aircraftReserveInfoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(List.of(entity));

    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);

    assertNotNull(response);
    assertEquals(1, response.getData().size());

    for (AircraftReserveInfoDetailResponseDto dto : response.getData()) {
      assertEquals(dto.getAircraftId(), entity.getAircraftId().toString());
      assertEquals(dto.getAircraftReservationId(), entity.getAircraftReservationId().toString());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeFrom()),
          entity.getReservationTime().lower());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeTo()),
          entity.getReservationTime().upper());
      assertEquals(dto.getAircraftName(), entity.getAircraftEntity().getAircraftName());
    }
    assertNull(response.getPerPage());
    assertNull(response.getCurrentPage());
    assertNull(response.getLastPage());
    assertNull(response.getTotal());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoListRequestDtoを入力する<br>
   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetList_NullParam() {
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    String time = ZonedDateTime.now().toString();
    String aircraftName = "aircraftName";

    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
    request.setAircraftId(null);
    request.setTimeFrom(null);
    request.setTimeTo(null);

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(time),
                StringUtils.parseDatetimeStringToLocalDateTime(time))));
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName(aircraftName);
    entity.setAircraftEntity(entity2);

    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity));

    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);

    assertNotNull(response);
    assertEquals(1, response.getData().size());

    for (AircraftReserveInfoDetailResponseDto dto : response.getData()) {
      assertEquals(dto.getAircraftId(), entity.getAircraftId().toString());
      assertEquals(dto.getAircraftReservationId(), entity.getAircraftReservationId().toString());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeFrom()),
          entity.getReservationTime().lower());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeTo()),
          entity.getReservationTime().upper());
      assertEquals(dto.getAircraftName(), entity.getAircraftEntity().getAircraftName());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoListRequestDtoを入力する<br>
   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetList_EmptyParam() {
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    String time = ZonedDateTime.now().toString();
    String aircraftName = "aircraftName";

    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
    request.setAircraftId("");
    request.setTimeFrom("");
    request.setTimeTo("");

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(time),
                StringUtils.parseDatetimeStringToLocalDateTime(time))));
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName(aircraftName);
    entity.setAircraftEntity(entity2);

    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity));

    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);

    assertNotNull(response);
    assertEquals(1, response.getData().size());

    for (AircraftReserveInfoDetailResponseDto dto : response.getData()) {
      assertEquals(dto.getAircraftId(), entity.getAircraftId().toString());
      assertEquals(dto.getAircraftReservationId(), entity.getAircraftReservationId().toString());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeFrom()),
          entity.getReservationTime().lower());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeTo()),
          entity.getReservationTime().upper());
      assertEquals(dto.getAircraftName(), entity.getAircraftEntity().getAircraftName());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoListRequestDtoを入力する<br>
   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetList_BalnkParam() {
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    String time = ZonedDateTime.now().toString();
    String aircraftName = "aircraftName";

    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
    request.setAircraftId(" ");
    request.setTimeFrom(" ");
    request.setTimeTo(" ");

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(time),
                StringUtils.parseDatetimeStringToLocalDateTime(time))));
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName(aircraftName);
    entity.setAircraftEntity(entity2);

    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity));

    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);

    assertNotNull(response);
    assertEquals(1, response.getData().size());

    for (AircraftReserveInfoDetailResponseDto dto : response.getData()) {
      assertEquals(dto.getAircraftId(), entity.getAircraftId().toString());
      assertEquals(dto.getAircraftReservationId(), entity.getAircraftReservationId().toString());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeFrom()),
          entity.getReservationTime().lower());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeTo()),
          entity.getReservationTime().upper());
      assertEquals(dto.getAircraftName(), entity.getAircraftEntity().getAircraftName());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報を取得できずにエラーになることを確認する<br>
   * 条件: 機体予約情報をNullで返す<br>
   * 結果: ServiceErrorExceptionが返されること<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetList_NotFound_AircraftReserveId() {
    UUID aircraftId = UUID.randomUUID();
    String time = ZonedDateTime.now().toString();
    String aircraftName = "aircraftName";

    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
    request.setAircraftId(aircraftId.toString());
    request.setTimeFrom(time);
    request.setTimeTo(time);

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(null);
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(time),
                StringUtils.parseDatetimeStringToLocalDateTime(time))));
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName(aircraftName);
    entity.setAircraftEntity(entity2);

    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity));

    assertThrows(
        ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl.getList(request));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoListRequestDtoを入力する<br>
   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetList_NotFound_AircraftId() {
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    String time = ZonedDateTime.now().toString();
    String aircraftName = "aircraftName";

    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
    request.setAircraftId(aircraftId.toString());
    request.setTimeFrom(time);
    request.setTimeTo(time);

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(null);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(time),
                StringUtils.parseDatetimeStringToLocalDateTime(time))));
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName(aircraftName);
    entity.setAircraftEntity(entity2);

    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity));

    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);

    assertNotNull(response);
    assertEquals(1, response.getData().size());

    for (AircraftReserveInfoDetailResponseDto dto : response.getData()) {
      assertNull(dto.getAircraftId());
      assertEquals(dto.getAircraftReservationId(), entity.getAircraftReservationId().toString());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeFrom()),
          entity.getReservationTime().lower());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeTo()),
          entity.getReservationTime().upper());
      assertEquals(dto.getAircraftName(), entity.getAircraftEntity().getAircraftName());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
   * 条件: 正常なAircraftReserveInfoListRequestDtoを入力する<br>
   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testGetList_一括予約IDnull() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftreservationId = UUID.randomUUID();
    String time = ZonedDateTime.now().toString();
    String aircraftName = "aircraftName";

    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
    request.setAircraftId(aircraftId.toString());
    request.setAircraftName(aircraftName);
    request.setTimeFrom(time);
    request.setTimeTo(time);

    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftreservationId);
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(time),
                StringUtils.parseDatetimeStringToLocalDateTime(time))));
    entity.setOperatorId(operatorId);
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName(aircraftName);
    entity2.setOperatorId(operatorId);
    entity.setAircraftEntity(entity2);

    when(aircraftReserveInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity));

    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);

    assertNotNull(response);
    assertEquals(1, response.getData().size());

    for (AircraftReserveInfoDetailResponseDto dto : response.getData()) {
      assertEquals(dto.getAircraftId(), entity.getAircraftId().toString());
      assertEquals(dto.getAircraftReservationId(), entity.getAircraftReservationId().toString());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeFrom()),
          entity.getReservationTime().lower());
      assertEquals(
          StringUtils.parseDatetimeStringToLocalDateTime(dto.getReservationTimeTo()),
          entity.getReservationTime().upper());
      assertEquals(dto.getAircraftName(), entity.getAircraftEntity().getAircraftName());
      assertEquals(operatorId, dto.getOperatorId());
    }
    assertNull(response.getPerPage());
    assertNull(response.getCurrentPage());
    assertNull(response.getLastPage());
    assertNull(response.getTotal());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体予約情報の詳細を正常に取得できることを確認する<br>
   * 条件: 正常なaircraftRevservationIdを入力する<br>
   * 結果: 取得されたAircraftReserveInfoDetailResponseDtoが返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testGetDetail_Nomal() {
    String operatorId = "dummyOperator";
    UUID aircraftId = UUID.randomUUID();
    UUID aircraftReservationId = UUID.randomUUID();
    Timestamp timeFrom = new Timestamp(System.currentTimeMillis());
    Timestamp timeTo = new Timestamp(System.currentTimeMillis());
    Range<LocalDateTime> rLocaldatetime =
        Range.localDateTimeRange(String.format("[%s,%s)", timeFrom, timeTo));
    AircraftReserveInfoEntity entity = new AircraftReserveInfoEntity();
    entity.setAircraftReservationId(aircraftReservationId);
    entity.setAircraftId(aircraftId);
    entity.setReservationTime(rLocaldatetime);
    entity.setOperatorId(operatorId);
    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId);
    entity2.setAircraftName("aircraftName");
    entity2.setOperatorId(operatorId);
    entity.setAircraftEntity(entity2);
    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.of(entity));

    AircraftReserveInfoDetailResponseDto response =
        aircraftReserveInfoServiceImpl.getDetail(aircraftReservationId.toString());

    assertNotNull(response);
    assertEquals(entity.getAircraftReservationId().toString(), response.getAircraftReservationId());
    assertEquals(entity.getAircraftId().toString(), response.getAircraftId());
    assertEquals(
        entity.getReservationTime().lower(),
        StringUtils.parseDatetimeStringToLocalDateTime(response.getReservationTimeFrom()));
    assertEquals(
        entity.getReservationTime().upper(),
        StringUtils.parseDatetimeStringToLocalDateTime(response.getReservationTimeTo()));
    assertEquals(entity.getAircraftEntity().getAircraftName(), response.getAircraftName());
    assertEquals(operatorId, response.getOperatorId());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体予約情報の詳細を取得する<br>
   * 条件: 存在しない機体IDを渡す<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  public void testGetDetail_NotFound() {
    UUID aircraftReservationId = UUID.randomUUID();

    when(aircraftReserveInfoRepository.findByAircraftReservationIdAndDeleteFlagFalse(
            any(UUID.class)))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> aircraftReserveInfoServiceImpl.getDetail(aircraftReservationId.toString()));
  }

  /** データテンプレート ■登録更新リクエスト ユーザー情報_テンプレート */
  private static UserInfoDto createUserInfoDto() {
    List<RoleInfoDto> roles = new ArrayList<>();
    RoleInfoDto roleInfoDto = new RoleInfoDto();
    roleInfoDto.setRoleId("10");
    roleInfoDto.setRoleName("航路運営者_責任者");
    roles.add(roleInfoDto);

    UserInfoDto ret = new UserInfoDto();
    ret.setUserOperatorId("ope99");
    ret.setRoles(roles);
    ret.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf41");
    return ret;
  }
}
