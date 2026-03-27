package com.hitachi.droneroute.arm.service.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.arm.dto.AircraftInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListElement;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListResponseDto;
import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
import com.hitachi.droneroute.arm.entity.FileInfoEntity;
import com.hitachi.droneroute.arm.entity.PayloadInfoEntity;
import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
import com.hitachi.droneroute.arm.repository.FileInfoRepository;
import com.hitachi.droneroute.arm.repository.PayloadInfoRepository;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListDetailElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListElement;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListResponseDto;
import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
import com.hitachi.droneroute.prm.repository.PriceHistoryInfoRepository;
import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
import com.hitachi.droneroute.prm.service.PriceInfoSearchListService;
import com.hitachi.droneroute.prm.service.impl.PriceInfoServiceImpl;
import com.hitachi.droneroute.prm.validator.PriceInfoValidator;
import io.hypersistence.utils.hibernate.type.range.Range;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

/** AircraftInfoServiceImplクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
class AircraftInfoServiceImplTest {

  @MockBean private AircraftInfoRepository aircraftInfoRepository;

  @MockBean private FileInfoRepository fileInfoRepository;

  @MockBean private PayloadInfoRepository payloadInfoRepository;

  @MockBean private PriceInfoRepository priceInfoRepository;

  @MockBean private PriceHistoryInfoRepository priceInfoHistoryRepository;

  @MockBean private PriceInfoSearchListService priceInfoSearchService;

  @Autowired private AircraftInfoServiceImpl aircraftInfoServiceImpl;

  @SpyBean private PriceInfoServiceImpl priceInfoServiceImpl;

  @MockBean private PriceInfoValidator priceInfoValidator;

  @SpyBean private SystemSettings systemSettings;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * 機体情報リクエストDTO作成
   *
   * @return
   */
  private AircraftInfoRequestDto createAircraftInfoRequestDto() {
    AircraftInfoRequestDto requestDto = new AircraftInfoRequestDto();
    requestDto.setAircraftName("ダミー機体名");
    requestDto.setManufacturer("manufacuturer");
    requestDto.setManufacturingNumber("123456");
    requestDto.setAircraftType(1);
    requestDto.setMaxTakeoffWeight(50.0);
    requestDto.setBodyWeight(10.0);
    requestDto.setMaxFlightSpeed(10.0);
    requestDto.setMaxFlightTime(3.0);
    requestDto.setLat(38.0);
    requestDto.setLon(130.0);
    requestDto.setCertification(true);
    requestDto.setDipsRegistrationCode("dummy-code");
    requestDto.setOwnerType(1);
    requestDto.setImageData(
        "data:image/png;base64," + Base64.getEncoder().encodeToString("testbinary".getBytes()));
    requestDto.setImageBinary("testbinary".getBytes());

    return requestDto;
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体情報登録APIの正常系テスト<br>
   * 条件: 正常なリクエストデータを渡す<br>
   * 結果: 登録されたデータのIDが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_Normal() {
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    String operatorId = "5cd3e8fe-c1e5-4d86-9756-0cb16c744afc";
    AircraftInfoRequestDto request = createAircraftInfoRequestDto();
    request.setOwnerId(ownerId.toString());
    UserInfoDto userDro = createUserInfoDto_OwnOperator();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);

    AircraftInfoResponseDto response = aircraftInfoServiceImpl.postData(request, userDro);

    assertNotNull(response);
    assertEquals(aircraftId.toString(), response.getAircraftId());

    ArgumentCaptor<AircraftInfoEntity> entityCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository).save(entityCaptor.capture());
    AircraftInfoEntity argEntity = entityCaptor.getValue();
    assertEquals(request.getAircraftName(), argEntity.getAircraftName());
    assertEquals(request.getManufacturer(), argEntity.getManufacturer());
    assertEquals(request.getManufacturingNumber(), argEntity.getManufacturingNumber());
    assertEquals(request.getAircraftType(), argEntity.getAircraftType());
    assertEquals(request.getMaxTakeoffWeight(), argEntity.getMaxTakeoffWeight());
    assertEquals(request.getBodyWeight(), argEntity.getBodyWeight());
    assertEquals(request.getMaxFlightSpeed(), argEntity.getMaxFlightSpeed());
    assertEquals(request.getMaxFlightTime(), argEntity.getMaxFlightTime());
    assertEquals(request.getLat(), argEntity.getLat());
    assertEquals(request.getLon(), argEntity.getLon());
    assertEquals(request.getCertification(), argEntity.getCertification());
    assertEquals(request.getDipsRegistrationCode(), argEntity.getDipsRegistrationCode());
    assertEquals(request.getOwnerType(), argEntity.getOwnerType());
    assertEquals(request.getOwnerId(), argEntity.getOwnerId().toString());
    assertEquals(
        request
            .getImageData()
            .substring(
                request.getImageData().indexOf("/") + 1, request.getImageData().indexOf(";")),
        argEntity.getImageFormat());
    assertEquals(request.getImageBinary(), argEntity.getImageBinary());
    assertEquals(operatorId, argEntity.getOperatorId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体情報登録APIの正常系テスト<br>
   * 条件: aircraftIdとownerIdが空文字のリクエストデータを渡す<br>
   * 結果: 登録されたデータのIDが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_Normal2() {
    UUID aircraftId = UUID.randomUUID();
    String operatorId = "5cd3e8fe-c1e5-4d86-9756-0cb16c744afc";
    AircraftInfoRequestDto request = createAircraftInfoRequestDto();
    request.setAircraftId("");
    request.setOwnerId("");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(null);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);

    AircraftInfoResponseDto response = aircraftInfoServiceImpl.postData(request, userDto);

    assertNotNull(response);
    assertEquals(aircraftId.toString(), response.getAircraftId());

    ArgumentCaptor<AircraftInfoEntity> entityCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository).save(entityCaptor.capture());
    AircraftInfoEntity argEntity = entityCaptor.getValue();
    assertEquals(request.getAircraftName(), argEntity.getAircraftName());
    assertEquals(request.getManufacturer(), argEntity.getManufacturer());
    assertEquals(request.getManufacturingNumber(), argEntity.getManufacturingNumber());
    assertEquals(request.getAircraftType(), argEntity.getAircraftType());
    assertEquals(request.getMaxTakeoffWeight(), argEntity.getMaxTakeoffWeight());
    assertEquals(request.getBodyWeight(), argEntity.getBodyWeight());
    assertEquals(request.getMaxFlightSpeed(), argEntity.getMaxFlightSpeed());
    assertEquals(request.getMaxFlightTime(), argEntity.getMaxFlightTime());
    assertEquals(request.getLat(), argEntity.getLat());
    assertEquals(request.getLon(), argEntity.getLon());
    assertEquals(request.getCertification(), argEntity.getCertification());
    assertEquals(request.getDipsRegistrationCode(), argEntity.getDipsRegistrationCode());
    assertEquals(request.getOwnerType(), argEntity.getOwnerType());
    assertNull(argEntity.getOwnerId());
    assertEquals(
        request
            .getImageData()
            .substring(
                request.getImageData().indexOf("/") + 1, request.getImageData().indexOf(";")),
        argEntity.getImageFormat());
    assertEquals(request.getImageBinary(), argEntity.getImageBinary());
    assertEquals(operatorId, argEntity.getOperatorId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体情報登録APIの異常系テスト<br>
   * 条件: 登録後にnullのAircraftIｄを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_ServiceError_AircraftId() {
    UUID ownerId = UUID.randomUUID();
    AircraftInfoRequestDto request = createAircraftInfoRequestDto();
    request.setOwnerId(ownerId.toString());
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(null);
    savedEntity.setOwnerId(ownerId);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.postData(request, userDto));

    assertEquals("機体情報の生成に失敗しました。", exception.getMessage());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体情報登録の異常系テスト<br>
   * 条件: repositoryのsaveメソッドが不正なエンティティを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_機体情報の登録応答不正() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    AircraftInfoEntity ent = createAircraftInfo_save_err();
    when(aircraftInfoRepository.save(any())).thenReturn(ent);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    Exception ex =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("機体情報の生成に失敗しました。", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の異常系テスト<br>
   * 条件: fileInfoRepositoryのsaveメソッドが不正なエンティティを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_補足資料情報の登録応答不正() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoEntity airEnt = new AircraftInfoEntity();
    airEnt.setAircraftId(UUID.randomUUID());
    when(aircraftInfoRepository.save(any())).thenReturn(airEnt);
    FileInfoEntity fileEnt = createFileInfoEntity_save_errl();
    when(fileInfoRepository.save(any())).thenReturn(fileEnt);

    // テスト実施
    Exception ex =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("補足資料情報の更新に失敗しました。", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体情報登録APIの異常系テスト<br>
   * 条件: aircraftInfoRepositoryのsaveメソッドがDuplicateKeyExceptionをスローする<br>
   * 結果: DuplicateKeyExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_想定外エラー_機体情報登録時() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    when(aircraftInfoRepository.save(any())).thenThrow(new DuplicateKeyException("上記以外の例外が発生"));

    // テスト実施
    Exception ex =
        assertThrows(
            DuplicateKeyException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の異常系テスト<br>
   * 条件: fileInfoRepositoryのsaveメソッドがDuplicateKeyExceptionをスローする<br>
   * 結果: DuplicateKeyExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_想定外エラー_補足資料情報登録時() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoEntity airEnt = new AircraftInfoEntity();
    airEnt.setAircraftId(UUID.randomUUID());
    when(aircraftInfoRepository.save(any())).thenReturn(airEnt);
    when(fileInfoRepository.save(any())).thenThrow(new DuplicateKeyException("上記以外の例外が発生"));

    // テスト実施
    Exception ex =
        assertThrows(
            DuplicateKeyException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の異常系テスト<br>
   * 条件: aircraftInfoRepositoryのsaveメソッドが不正なエンティティを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_機体情報の登録応答不正_ペイロード情報() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    AircraftInfoEntity ent = createAircraftInfo_save_err();
    when(aircraftInfoRepository.save(any())).thenReturn(ent);

    // テスト実施
    Exception ex =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("機体情報の生成に失敗しました。", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の異常系テスト<br>
   * 条件: payloadInfoRepositoryのsaveメソッドが不正なエンティティを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_ペイロード情報の登録応答不正() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    AircraftInfoEntity airEnt = new AircraftInfoEntity();
    airEnt.setAircraftId(UUID.randomUUID());
    when(aircraftInfoRepository.save(any())).thenReturn(airEnt);
    PayloadInfoEntity payloadEnt = createPayloadInfoEntity_save_errl();
    when(payloadInfoRepository.save(any())).thenReturn(payloadEnt);

    // テスト実施
    Exception ex =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("ペイロード情報の更新に失敗しました。", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機体情報登録APIの異常系テスト<br>
   * 条件: aircraftInfoRepositoryのsaveメソッドがDuplicateKeyExceptionをスローする<br>
   * 結果: DuplicateKeyExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_想定外エラー_機体情報登録時_ペイロード情報() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    when(aircraftInfoRepository.save(any())).thenThrow(new DuplicateKeyException("上記以外の例外が発生"));

    // テスト実施
    Exception ex =
        assertThrows(
            DuplicateKeyException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の異常系テスト<br>
   * 条件: payloadInfoRepositoryのsaveメソッドがDuplicateKeyExceptionをスローする<br>
   * 結果: DuplicateKeyExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_想定外エラー_ペイロード情報登録時() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    AircraftInfoEntity airEnt = new AircraftInfoEntity();
    airEnt.setAircraftId(UUID.randomUUID());
    when(aircraftInfoRepository.save(any())).thenReturn(airEnt);
    when(payloadInfoRepository.save(any())).thenThrow(new DuplicateKeyException("上記以外の例外が発生"));

    // テスト実施
    Exception ex =
        assertThrows(
            DuplicateKeyException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 料金情報を含む機体情報登録の正常系テスト<br>
   * 条件: 料金情報1件、正常なリクエストDTOを渡す<br>
   * 結果: DTOのマッピング内容を確認<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void postData_onePriceInfo_Normal() {
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    doNothing().when(priceInfoServiceImpl).process(any());

    aircraftInfoServiceImpl.postData(dto, userDto);

    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
    List<PriceInfoRequestDto> argEntity = Captor.getValue();

    // DTOのマッピング内容を確認
    assertEquals(1, argEntity.get(0).getProcessingType());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getPriceId());
    assertEquals(aircraftId.toString(), argEntity.get(0).getResourceId());
    assertEquals(30, argEntity.get(0).getResourceType());
    assertEquals(4, argEntity.get(0).getPriceType());
    assertEquals(1, argEntity.get(0).getPricePerUnit());
    assertEquals(1000, argEntity.get(0).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveEndTime());
    assertEquals(1, argEntity.get(0).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(0).getOperatorId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 料金情報を含む機体情報登録の正常系テスト<br>
   * 条件: 料金情報は3件<br>
   * 結果: DTOのマッピング内容を確認<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void postData_threePriceInfo_Normal() {
    AircraftInfoRequestDto dto = createThreePriceInfoAircraftInfoRequestDto();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    doNothing().when(priceInfoServiceImpl).process(any());

    aircraftInfoServiceImpl.postData(dto, userDto);

    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
    List<PriceInfoRequestDto> argEntity = Captor.getValue();

    // DTOのマッピング内容を確認
    assertEquals(1, argEntity.get(0).getProcessingType());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getPriceId());
    assertEquals(aircraftId.toString(), argEntity.get(0).getResourceId());
    assertEquals(30, argEntity.get(0).getResourceType());
    assertEquals(4, argEntity.get(0).getPriceType());
    assertEquals(1, argEntity.get(0).getPricePerUnit());
    assertEquals(1000, argEntity.get(0).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveEndTime());
    assertEquals(1, argEntity.get(0).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(0).getOperatorId());

    assertEquals(1, argEntity.get(1).getProcessingType());
    assertEquals("6b5ec052-a76f-87cb-ef4a-a31c62a13276", argEntity.get(1).getPriceId());
    assertEquals(aircraftId.toString(), argEntity.get(1).getResourceId());
    assertEquals(30, argEntity.get(1).getResourceType());
    assertEquals(4, argEntity.get(1).getPriceType());
    assertEquals(1, argEntity.get(1).getPricePerUnit());
    assertEquals(2000, argEntity.get(1).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(1).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(1).getEffectiveEndTime());
    assertEquals(2, argEntity.get(1).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(1).getOperatorId());

    assertEquals(1, argEntity.get(2).getProcessingType());
    assertEquals("f3113bed-357e-2386-2b15-effaf01a592e", argEntity.get(2).getPriceId());
    assertEquals(aircraftId.toString(), argEntity.get(2).getResourceId());
    assertEquals(30, argEntity.get(2).getResourceType());
    assertEquals(4, argEntity.get(2).getPriceType());
    assertEquals(1, argEntity.get(2).getPricePerUnit());
    assertEquals(3000, argEntity.get(2).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(2).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(2).getEffectiveEndTime());
    assertEquals(3, argEntity.get(2).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(2).getOperatorId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 料金情報を含む機体情報登録の正常系テスト<br>
   * 条件: 料金情報は空配列<br>
   * 結果: 正常に処理が終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_zeroPriceInfo_Normal() {
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    dto.setPriceInfos(Collections.emptyList());
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    doNothing().when(priceInfoServiceImpl).process(any());

    AircraftInfoResponseDto response = aircraftInfoServiceImpl.postData(dto, userDto);

    assertNotNull(response);
    assertEquals(savedEntity.getAircraftId().toString(), response.getAircraftId());
    verify(priceInfoServiceImpl, never()).process(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 料金情報を含む機体情報登録の正常系テスト<br>
   * 条件: 料金情報はnull<br>
   * 結果: 正常に処理が終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_nullPriceInfo_Normal() {
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    dto.setPriceInfos(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    doNothing().when(priceInfoServiceImpl).process(any());

    AircraftInfoResponseDto response = aircraftInfoServiceImpl.postData(dto, userDto);

    assertNotNull(response);
    assertEquals(savedEntity.getAircraftId().toString(), response.getAircraftId());
    verify(priceInfoServiceImpl, never()).process(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 料金情報を含む機体情報登録の異常系テスト<br>
   * 条件: 料金情報1件、サービスで例外を発生させる<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void postData_onePriceInfo_NotFoundException() {
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);

    // モックを設定(料金情報：エラー)
    doThrow(new NotFoundException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // 実行確認
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 料金情報を含む機体情報登録の異常系テスト<br>
   * 条件: 料金情報1件、サービスで例外を発生させる<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void postData_onePriceInfo_ServiceErrorException() {
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);

    // モックを設定(料金情報：エラー)
    doThrow(new ServiceErrorException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // 実行確認
    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 料金情報を含む機体情報登録の異常系テスト<br>
   * 条件: 料金情報1件、サービスで例外を発生させる<br>
   * 結果: ValidationErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void postData_onePriceInfo_ValidationErrorException() {
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);

    // モックを設定(料金情報：エラー)
    doThrow(new ValidationErrorException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // 実行確認
    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 料金情報を含む機体情報登録の異常系テスト<br>
   * 条件: 料金情報1件、サービスで例外を発生させる<br>
   * 結果: NullPointerExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void postData_onePriceInfo_NullPointerException() {
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);

    // モックを設定(料金情報：エラー)
    doThrow(new NullPointerException("上記以外の例外が発生")).when(priceInfoServiceImpl).process(any());

    // 実行確認
    NullPointerException exception =
        assertThrows(
            NullPointerException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertTrue(exception.getMessage().contains("上記以外の例外が発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 通常の機体情報更新の正常系テスト<br>
   * 条件: 正常なリクエストデータを渡す<br>
   * 結果: 更新されたデータのIDが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_Normal() {
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    byte[] imageData = "testbinary".getBytes();
    String operatorId = "5cd3e8fe-c1e5-4d86-9756-0cb16c744afc";
    AircraftInfoRequestDto request = new AircraftInfoRequestDto();
    request.setAircraftId(aircraftId.toString());
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.fromString(request.getAircraftId()));
    existingEntity.setOwnerId(ownerId);
    existingEntity.setImageBinary(imageData);
    existingEntity.setOperatorId(operatorId);

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(existingEntity);

    AircraftInfoResponseDto response = aircraftInfoServiceImpl.putData(request, userDto);

    assertNotNull(response);
    assertEquals(existingEntity.getAircraftId().toString(), response.getAircraftId());

    ArgumentCaptor<AircraftInfoEntity> entityCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository).save(entityCaptor.capture());
    AircraftInfoEntity argEntity = entityCaptor.getValue();
    assertEquals(request.getAircraftId(), argEntity.getAircraftId().toString());
    assertEquals("ダミー機体名", argEntity.getAircraftName());
    assertEquals("ダミー製造メーカー", argEntity.getManufacturer());
    assertEquals("123456", argEntity.getManufacturingNumber());
    assertEquals(1, argEntity.getAircraftType());
    assertEquals(50.0, argEntity.getMaxTakeoffWeight());
    assertEquals(10.0, argEntity.getBodyWeight());
    assertEquals(10.0, argEntity.getMaxFlightSpeed());
    assertEquals(3.0, argEntity.getMaxFlightTime());
    assertEquals(38.0, argEntity.getLat());
    assertEquals(130.0, argEntity.getLon());
    assertEquals(true, argEntity.getCertification());
    assertEquals("12345", argEntity.getDipsRegistrationCode());
    assertEquals(1, argEntity.getOwnerType());
    assertEquals(ownerId.toString(), argEntity.getOwnerId().toString());
    assertEquals("png", argEntity.getImageFormat());
    assertEquals(imageData, argEntity.getImageBinary());
    assertEquals(operatorId, argEntity.getOperatorId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 通常の機体情報更新の異常系テスト<br>
   * 条件: 存在しないIDを渡す<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_NotFound() {
    AircraftInfoRequestDto request = new AircraftInfoRequestDto();
    request.setAircraftId(UUID.randomUUID().toString());
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(request, userDto));
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 通常の機体情報更新の異常系テスト<br>
   * 条件: DBからaircraft_idがnullのデータを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_ServiceError_AircraftId() {
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    byte[] imageData = "testbinary".getBytes();
    String operatorId = "dummyOperator";
    AircraftInfoRequestDto request = new AircraftInfoRequestDto();
    request.setAircraftId(aircraftId.toString());
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    existingEntity.setImageBinary(imageData);
    existingEntity.setOperatorId(operatorId);

    AircraftInfoEntity existingEntity2 = createAircraftInfoEntity();
    existingEntity2.setAircraftId(null);
    existingEntity2.setOwnerId(ownerId);
    existingEntity2.setImageBinary(imageData);

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(existingEntity2);

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.putData(request, userDto));

    assertEquals("機体情報の更新に失敗しました。", exception.getMessage());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機体情報更新APIの異常系テスト<br>
   * 条件: aircraftInfoRepositoryのsaveメソッドが不正なエンティティを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_機体情報の登録応答不正() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setOperatorId("ope01");
    existingEntity.setDeleteFlag(false);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenReturn(Optional.of(existingEntity));

    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    AircraftInfoEntity ent = createAircraftInfo_save_err();
    when(aircraftInfoRepository.save(any())).thenReturn(ent);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    Exception ex =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("機体情報の更新に失敗しました。", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報を含む機体情報更新の異常系テスト<br>
   * 条件: fileInfoRepositoryのsaveメソッドが不正なエンティティを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_補足資料情報の登録応答不正() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setOperatorId("ope01");
    existingEntity.setDeleteFlag(false);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenReturn(Optional.of(existingEntity));
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    AircraftInfoEntity airEnt = new AircraftInfoEntity();
    airEnt.setAircraftId(UUID.randomUUID());
    when(aircraftInfoRepository.save(any())).thenReturn(airEnt);
    FileInfoEntity fileEnt = createFileInfoEntity_save_errl();
    when(fileInfoRepository.save(any())).thenReturn(fileEnt);

    // テスト実施
    Exception ex =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("補足資料情報の更新に失敗しました。", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機体情報更新APIの異常系テスト<br>
   * 条件: aircraftInfoRepositoryのsaveメソッドがDuplicateKeyExceptionをスローする<br>
   * 結果: DuplicateKeyExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_想定外エラー_機体情報登録時() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setOperatorId("ope01");
    existingEntity.setDeleteFlag(false);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenReturn(Optional.of(existingEntity));
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    when(aircraftInfoRepository.save(any())).thenThrow(new DuplicateKeyException("上記以外の例外が発生"));
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    Exception ex =
        assertThrows(
            DuplicateKeyException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報を含む機体情報更新の異常系テスト<br>
   * 条件: fileInfoRepositoryのsaveメソッドがDuplicateKeyExceptionをスローする<br>
   * 結果: DuplicateKeyExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_想定外エラー_補足資料情報登録時() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setOperatorId("ope01");
    existingEntity.setDeleteFlag(false);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenReturn(Optional.of(existingEntity));
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoEntity airEnt = new AircraftInfoEntity();
    airEnt.setAircraftId(UUID.randomUUID());
    when(aircraftInfoRepository.save(any())).thenReturn(airEnt);
    when(fileInfoRepository.save(any())).thenThrow(new DuplicateKeyException("上記以外の例外が発生"));

    // テスト実施
    Exception ex =
        assertThrows(
            DuplicateKeyException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機体情報更新APIの異常系テスト<br>
   * 条件: aircraftInfoRepositoryのsaveメソッドが不正なエンティティを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_機体情報の登録応答不正_ペイロード情報() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setOperatorId("ope01");
    existingEntity.setDeleteFlag(false);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenReturn(Optional.of(existingEntity));

    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    AircraftInfoEntity ent = createAircraftInfo_save_err();
    when(aircraftInfoRepository.save(any())).thenReturn(ent);

    // テスト実施
    Exception ex =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("機体情報の更新に失敗しました。", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報を含む機体情報更新の異常系テスト<br>
   * 条件: payloadInfoRepositoryのsaveメソッドが不正なエンティティを返す<br>
   * 結果: ServiceErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_ペイロード情報の登録応答不正() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setOperatorId(userDto.getUserOperatorId());
    existingEntity.setDeleteFlag(false);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenReturn(Optional.of(existingEntity));
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    AircraftInfoEntity airEnt = new AircraftInfoEntity();
    airEnt.setAircraftId(UUID.randomUUID());
    when(aircraftInfoRepository.save(any())).thenReturn(airEnt);
    PayloadInfoEntity payloadEnt = createPayloadInfoEntity_save_errl();
    when(payloadInfoRepository.save(any())).thenReturn(payloadEnt);

    // テスト実施
    Exception ex =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("ペイロード情報の更新に失敗しました。", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機体情報更新APIの異常系テスト<br>
   * 条件: aircraftInfoRepositoryのsaveメソッドがDuplicateKeyExceptionをスローする<br>
   * 結果: DuplicateKeyExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_想定外エラー_機体情報登録時_ペイロード情報() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setOperatorId("ope01");
    existingEntity.setDeleteFlag(false);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenReturn(Optional.of(existingEntity));
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    when(aircraftInfoRepository.save(any())).thenThrow(new DuplicateKeyException("上記以外の例外が発生"));

    // テスト実施
    Exception ex =
        assertThrows(
            DuplicateKeyException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報を含む機体情報更新の異常系テスト<br>
   * 条件: payloadInfoRepositoryのsaveメソッドがDuplicateKeyExceptionをスローする<br>
   * 結果: DuplicateKeyExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_想定外エラー_ペイロード情報登録時() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setOperatorId("ope01");
    existingEntity.setDeleteFlag(false);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenReturn(Optional.of(existingEntity));
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoEntity airEnt = new AircraftInfoEntity();
    airEnt.setAircraftId(UUID.randomUUID());
    when(aircraftInfoRepository.save(any())).thenReturn(airEnt);
    when(payloadInfoRepository.save(any())).thenThrow(new DuplicateKeyException("上記以外の例外が発生"));

    // テスト実施
    Exception ex =
        assertThrows(
            DuplicateKeyException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 料金情報を含む機体情報更新の正常系テスト<br>
   * 条件: 料金情報1件、正常なリクエストDTOを渡す<br>
   * 結果: DTOのマッピング内容を確認<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void putData_onePriceInfo_Nomal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    PriceInfoRequestDto info = dto.getPriceInfos().get(0);
    info.setProcessingType(2);

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.fromString(dto.getAircraftId()));
    existingEntity.setOperatorId("ope01");

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    doNothing().when(priceInfoServiceImpl).process(any());

    aircraftInfoServiceImpl.putData(dto, userDto);

    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
    List<PriceInfoRequestDto> argEntity = Captor.getValue();

    // DTOのマッピング内容を確認
    assertEquals(2, argEntity.get(0).getProcessingType());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getPriceId());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getResourceId());
    assertEquals(30, argEntity.get(0).getResourceType());
    assertEquals(4, argEntity.get(0).getPriceType());
    assertEquals(1, argEntity.get(0).getPricePerUnit());
    assertEquals(1000, argEntity.get(0).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(0).getEffectiveEndTime());
    assertEquals(1, argEntity.get(0).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(0).getOperatorId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 料金情報を含む機体情報更新の正常系テスト<br>
   * 条件: 料金情報3件、正常なリクエストDTOを渡す<br>
   * 結果: DTOのマッピング内容を確認<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void putData_threePriceInfo_Nomal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createThreePriceInfoAircraftInfoRequestDto();
    PriceInfoRequestDto info1 = dto.getPriceInfos().get(0);
    PriceInfoRequestDto info2 = dto.getPriceInfos().get(1);
    PriceInfoRequestDto info3 = dto.getPriceInfos().get(2);
    info1.setProcessingType(2);
    info2.setProcessingType(2);
    info3.setProcessingType(2);

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.fromString(dto.getAircraftId()));
    existingEntity.setOperatorId("ope01");

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    doNothing().when(priceInfoServiceImpl).process(any());

    aircraftInfoServiceImpl.putData(dto, userDto);

    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
    List<PriceInfoRequestDto> argEntity = Captor.getValue();

    // DTOのマッピング内容を確認
    assertEquals(2, argEntity.get(0).getProcessingType());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getPriceId());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf22", argEntity.get(0).getResourceId());
    assertEquals(30, argEntity.get(0).getResourceType());
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
    assertEquals(30, argEntity.get(1).getResourceType());
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
    assertEquals(30, argEntity.get(2).getResourceType());
    assertEquals(4, argEntity.get(2).getPriceType());
    assertEquals(1, argEntity.get(2).getPricePerUnit());
    assertEquals(3000, argEntity.get(2).getPrice());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(2).getEffectiveStartTime());
    assertEquals("2025-11-13T10:00:00Z", argEntity.get(2).getEffectiveEndTime());
    assertEquals(3, argEntity.get(2).getPriority());
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.get(2).getOperatorId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 料金情報を含む機体情報更新の正常系テスト<br>
   * 条件: 料金情報は空配列、正常なリクエストDTOを渡す<br>
   * 結果: 正常に処理が終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_zeroPriceInfo_Nomal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    dto.setPriceInfos(Collections.emptyList());

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    String operatorId = "ope01";
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);
    savedEntity.setOperatorId(operatorId);
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.fromString(dto.getAircraftId()));
    existingEntity.setOperatorId(operatorId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    doNothing().when(priceInfoServiceImpl).process(any());

    AircraftInfoResponseDto response = aircraftInfoServiceImpl.putData(dto, userDto);

    // DTOのマッピング内容を確認
    assertNotNull(response);
    assertEquals(savedEntity.getAircraftId().toString(), response.getAircraftId());
    verify(priceInfoServiceImpl, never()).process(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 料金情報を含む機体情報更新の正常系テスト<br>
   * 条件: 料金情報はnull、正常なリクエストDTOを渡す<br>
   * 結果: 正常に処理が終了する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_nullPriceInfo_Nomal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    dto.setPriceInfos(null);

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    String operatorId = "ope01";
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);
    savedEntity.setOperatorId(operatorId);
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.fromString(dto.getAircraftId()));
    existingEntity.setOperatorId(operatorId);

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    doNothing().when(priceInfoServiceImpl).process(any());

    AircraftInfoResponseDto response = aircraftInfoServiceImpl.putData(dto, userDto);

    // DTOのマッピング内容を確認
    assertNotNull(response);
    assertEquals(savedEntity.getAircraftId().toString(), response.getAircraftId());
    verify(priceInfoServiceImpl, never()).process(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 料金情報を含む機体情報更新の異常系テスト<br>
   * 条件: 料金情報1件、サービスで例外を発生させる<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void putData_onePriceInfo_NotFoundException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    PriceInfoRequestDto info = dto.getPriceInfos().get(0);
    info.setProcessingType(2);

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.fromString(dto.getAircraftId()));
    existingEntity.setOperatorId("ope01");

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    // モックを設定(料金情報：エラー)
    doThrow(new NotFoundException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // 実行確認
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 料金情報を含む機体情報更新の異常系テスト<br>
   * 条件: 料金情報1件、サービスで例外を発生させる<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void putData_onePriceInfo_ServiceErrorException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    PriceInfoRequestDto info = dto.getPriceInfos().get(0);
    info.setProcessingType(2);

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.fromString(dto.getAircraftId()));
    existingEntity.setOperatorId("ope01");

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    // モックを設定(料金情報：エラー)
    doThrow(new ServiceErrorException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // 実行確認
    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 料金情報を含む機体情報更新の異常系テスト<br>
   * 条件: 料金情報1件、サービスで例外を発生させる<br>
   * 結果: ValidationErrorExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void putData_onePriceInfo_ValidationErrorException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    PriceInfoRequestDto info = dto.getPriceInfos().get(0);
    info.setProcessingType(2);

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.fromString(dto.getAircraftId()));
    existingEntity.setOperatorId("ope01");

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    // モックを設定(料金情報：エラー)
    doThrow(new ValidationErrorException("ServiceErrorExceptionが発生"))
        .when(priceInfoServiceImpl)
        .process(any());

    // 実行確認
    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 料金情報を含む機体情報更新の異常系テスト<br>
   * 条件: 料金情報1件、サービスで例外を発生させる<br>
   * 結果: NullPointerExceptionがスローされる<br>
   * テストパターン: 異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void putData_onePriceInfo_NullPointerException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createOnePriceInfoAircraftInfoRequestDto();
    PriceInfoRequestDto info = dto.getPriceInfos().get(0);
    info.setProcessingType(2);

    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity savedEntity = new AircraftInfoEntity();
    savedEntity.setAircraftId(aircraftId);
    savedEntity.setOwnerId(ownerId);
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.fromString(dto.getAircraftId()));
    existingEntity.setOperatorId("ope01");

    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(savedEntity);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    // モックを設定(料金情報：エラー)
    doThrow(new NullPointerException("上記以外の例外")).when(priceInfoServiceImpl).process(any());

    // 実行確認
    NullPointerException exception =
        assertThrows(
            NullPointerException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertTrue(exception.getMessage().contains("上記以外の例外"));
    ArgumentCaptor<List<PriceInfoRequestDto>> Captor = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(Captor.capture());
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 正常にデータを削除できることを確認する<br>
   * 条件: 正常なIDを渡す<br>
   * 結果: データが削除されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void deleteData_Normal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = new AircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOperatorId(userDto.getUserOperatorId());

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    assertDoesNotThrow(() -> aircraftInfoServiceImpl.deleteData(aircraftId.toString(), userDto));
    verify(aircraftInfoRepository, times(1)).save(existingEntity);
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 機体情報削除APIの異常系テスト<br>
   * 条件: 存在しないIDを渡す<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void deleteData_NotFound() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> aircraftInfoServiceImpl.deleteData(aircraftId.toString(), userDto));
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名:機体情報を削除する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 結果: 1件の料金情報エンティティの削除フラグがtrueに設定される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_Normal_onePriceInfo() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // モックを設定(機体情報)
    UUID aircraftId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = new AircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOperatorId(userDto.getUserOperatorId());
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(null);

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
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    ent.setCreateTime(ts);
    ent.setUpdateTime(ts);
    ent.setDeleteFlag(false);
    List<PriceInfoEntity> list = new ArrayList<>();
    list.add(ent);
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(list);
    when(priceInfoRepository.save(any(PriceInfoEntity.class))).thenReturn(null);
    doNothing().when(priceInfoServiceImpl).registerPriceHistoryInfo(any());

    aircraftInfoServiceImpl.deleteData(aircraftId.toString(), userDto);

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
    assertEquals("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc", argEntity.getOperatorId());
    assertEquals("user01", argEntity.getUpdateUserId());
    assertEquals(ts, argEntity.getCreateTime());
    assertNotEquals(ts, argEntity.getUpdateTime());
    assertTrue(argEntity.getDeleteFlag());
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 機体情報を削除する<br>
   * 条件: 正常な機体トIDを渡す<br>
   * 結果:3件の料金情報エンティティの削除フラグがtrueに設定される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_Normal_threePriceInfo() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // モックを設定(機体情報)
    UUID aircraftId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = new AircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOperatorId(userDto.getUserOperatorId());
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(aircraftInfoRepository.save(any(AircraftInfoEntity.class))).thenReturn(null);

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
    PriceInfoEntity ent = new PriceInfoEntity();
    ent.setPriceId(UUID.fromString(priceId1));
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

    aircraftInfoServiceImpl.deleteData(aircraftId.toString(), userDto);

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
   * メソッド名: deleteData<br>
   * 試験名: 機体情報を削除する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 結果: 機体情報削除処理が正常終了すること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_Normal_zeroPriceInfo() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = new AircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOperatorId(userDto.getUserOperatorId());

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(priceInfoRepository.save(any(PriceInfoEntity.class))).thenReturn(null);

    // モックを設定(料金情報)
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());
    doNothing().when(priceInfoServiceImpl).registerPriceHistoryInfo(any());

    aircraftInfoServiceImpl.deleteData(aircraftId.toString(), userDto);

    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(0)).save(entityCaptor.capture());
    verify(priceInfoServiceImpl, times(0)).registerPriceHistoryInfo(entityCaptor.capture());
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 機体情報を削除する<br>
   * 条件: サービスで例外を発生させる<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_PriceInfo_ServiceErrorException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = new AircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOperatorId(userDto.getUserOperatorId());

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(priceInfoRepository.save(any(PriceInfoEntity.class))).thenReturn(null);

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
            () -> aircraftInfoServiceImpl.deleteData(aircraftId.toString(), userDto));
    assertTrue(exception.getMessage().contains("ServiceErrorExceptionが発生"));
    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(1)).save(entityCaptor.capture());
    verify(priceInfoServiceImpl, times(1)).registerPriceHistoryInfo(entityCaptor.capture());
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 機体情報を削除する<br>
   * 条件: サービスで例外を発生させる<br>
   * 結果: NullPointerExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void delete_PriceInfo_NullPointerException() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    UUID aircraftId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = new AircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOperatorId(userDto.getUserOperatorId());

    // モックを設定(機体情報)
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));
    when(priceInfoRepository.save(any(PriceInfoEntity.class))).thenReturn(null);

    // モックを設定(料金情報)
    PriceInfoEntity ent = new PriceInfoEntity();
    ent.setPriceId(UUID.randomUUID());
    List<PriceInfoEntity> list = new ArrayList<>();
    list.add(ent);
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(list);
    when(priceInfoRepository.save(any(PriceInfoEntity.class))).thenReturn(null);

    // モックを設定(料金情報：エラー)
    doThrow(new NullPointerException("上記以外の例外"))
        .when(priceInfoServiceImpl)
        .registerPriceHistoryInfo(any());

    // 実行確認
    NullPointerException exception =
        assertThrows(
            NullPointerException.class,
            () -> aircraftInfoServiceImpl.deleteData(aircraftId.toString(), userDto));
    assertTrue(exception.getMessage().contains("上記以外の例外"));
    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(1)).save(entityCaptor.capture());
    verify(priceInfoServiceImpl, times(1)).registerPriceHistoryInfo(entityCaptor.capture());
  }

  /**
   * 機体情報エンティティ作成
   *
   * @return
   */
  private AircraftInfoEntity createAircraftInfoEntity() {
    AircraftInfoEntity existingEntity = new AircraftInfoEntity();
    existingEntity.setAircraftName("ダミー機体名");
    existingEntity.setManufacturer("ダミー製造メーカー");
    existingEntity.setManufacturingNumber("123456");
    existingEntity.setAircraftType(1);
    existingEntity.setMaxTakeoffWeight(50.0);
    existingEntity.setBodyWeight(10.0);
    existingEntity.setMaxFlightSpeed(10.0);
    existingEntity.setMaxFlightTime(3.0);
    existingEntity.setLat(38.0);
    existingEntity.setLon(130.0);
    existingEntity.setCertification(true);
    existingEntity.setDipsRegistrationCode("12345");
    existingEntity.setOwnerType(1);
    existingEntity.setImageFormat("png");
    existingEntity.setImageBinary("data:image/png;base64,dGVzdGRhdGE=".getBytes());

    return existingEntity;
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名:機体情報の詳細を取得する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 結果: 機体情報の詳細が返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    existingEntity.setOperatorId(userDto.getUserOperatorId());
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    AircraftInfoDetailResponseDto response =
        aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, false, userDto);

    assertNotNull(response);
    assertEquals(aircraftId.toString(), response.getAircraftId());
    assertEquals(existingEntity.getAircraftName(), response.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), response.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), response.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), response.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), response.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), response.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), response.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), response.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), response.getLat());
    assertEquals(existingEntity.getLon(), response.getLon());
    assertEquals(existingEntity.getCertification(), response.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), response.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), response.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), response.getOwnerId());
    assertEquals(
        "data:image/"
            + existingEntity.getImageFormat()
            + ";base64,"
            + Base64.getEncoder().encodeToString(existingEntity.getImageBinary()),
        response.getImageData());
    assertEquals(userDto.getUserOperatorId(), response.getOperatorId());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名:機体情報の詳細を取得する<br>
   * 条件: 正常な機体IDを渡す。機体情報の画像データ未設定。<br>
   * 結果: 機体情報の詳細が返される。画像データがnullであること。<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal_ImageEmpty() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    existingEntity.setImageFormat(null);
    existingEntity.setImageBinary(new byte[] {});
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    AircraftInfoDetailResponseDto response =
        aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, false, userDto);

    assertNotNull(response);
    assertEquals(aircraftId.toString(), response.getAircraftId());
    assertEquals(existingEntity.getAircraftName(), response.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), response.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), response.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), response.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), response.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), response.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), response.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), response.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), response.getLat());
    assertEquals(existingEntity.getLon(), response.getLon());
    assertEquals(existingEntity.getCertification(), response.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), response.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), response.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), response.getOwnerId());
    assertNull(response.getImageData());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報の詳細を取得する<br>
   * 条件: 存在しない機体IDを渡す<br>
   * 結果: NotFoundExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_NotFound() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, false, userDto));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報の詳細を取得する<br>
   * 条件: 存在しない機体IDを渡す<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_NotFound_AircraftId() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setOwnerId(ownerId);
    existingEntity.setImageFormat(null);
    existingEntity.setImageBinary(new byte[] {});
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    assertThrows(
        ServiceErrorException.class,
        () -> aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, false, userDto));
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名:機体情報の詳細を取得する<br>
   * 条件: 正常な機体IDを渡す。機体情報のOwnerId未設定。<br>
   * 結果: 機体情報の詳細が返される。<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_NotFound_OwnerId() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    AircraftInfoDetailResponseDto response =
        aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, false, userDto);

    assertNotNull(response);
    assertEquals(aircraftId.toString(), response.getAircraftId());
    assertEquals(existingEntity.getAircraftName(), response.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), response.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), response.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), response.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), response.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), response.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), response.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), response.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), response.getLat());
    assertEquals(existingEntity.getLon(), response.getLon());
    assertEquals(existingEntity.getCertification(), response.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), response.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), response.getOwnerType());
    //    	assertEquals(existingEntity.getOwnerId().toString(), response.getOwnerId());
    assertNull(response.getOwnerId());
    assertEquals(
        "data:image/"
            + existingEntity.getImageFormat()
            + ";base64,"
            + Base64.getEncoder().encodeToString(existingEntity.getImageBinary()),
        response.getImageData());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名:機体情報の詳細を取得する<br>
   * 条件: 正常な機体IDを渡す。機体情報のOwnerId未設定。<br>
   * 結果: 機体情報の詳細が返される。<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_NotFound_Imagedata() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    existingEntity.setImageFormat(null);
    existingEntity.setImageBinary(null);
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(existingEntity));

    AircraftInfoDetailResponseDto response =
        aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, false, userDto);

    assertNotNull(response);
    assertEquals(aircraftId.toString(), response.getAircraftId());
    assertEquals(existingEntity.getAircraftName(), response.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), response.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), response.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), response.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), response.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), response.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), response.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), response.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), response.getLat());
    assertEquals(existingEntity.getLon(), response.getLon());
    assertEquals(existingEntity.getCertification(), response.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), response.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), response.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), response.getOwnerId());
    assertNull(response.getImageData());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報の詳細を取得する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 料金情報要否がtrue<br>
   * 料金情報1件<br>
   * 結果: 機体情報の詳細が返される<br>
   * 料金情報が含まれていること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal_料金情報1件() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    existingEntity.setOperatorId(userDto.getUserOperatorId());
    when(aircraftInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(existingEntity));

    // 機体情報エンティティ作成
    AircraftInfoEntity entity = createAircraftInfoEntity();
    entity.setImageBinary(null);
    entity.setAircraftId(aircraftId);
    entity.setOperatorId(userDto.getUserOperatorId());

    // 料金情報レスポンスDTO作成
    PriceInfoSearchListDetailElement priceDetailElement = new PriceInfoSearchListDetailElement();
    priceDetailElement.setPriceId(UUID.randomUUID().toString());
    priceDetailElement.setPriceType(4);
    priceDetailElement.setPricePerUnit(1);
    priceDetailElement.setPrice(1000);
    priceDetailElement.setPriority(1);
    priceDetailElement.setOperatorId(userDto.getUserOperatorId());
    priceDetailElement.setEffectiveStartTime("2025-11-13T10:00:00Z");
    priceDetailElement.setEffectiveEndTime("2025-11-13T10:00:00Z");

    PriceInfoSearchListElement priceElement = new PriceInfoSearchListElement();
    priceElement.setResourceId(aircraftId.toString());
    priceElement.setResourceType(2);
    priceElement.setPriceInfos(List.of(priceDetailElement));

    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(List.of(priceElement));

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    AircraftInfoDetailResponseDto response =
        aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, true, userDto);

    assertNotNull(response);
    assertEquals(aircraftId.toString(), response.getAircraftId());

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
   * 試験名: 機体情報の詳細を取得する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 料金情報要否がtrue<br>
   * 料金情報0件<br>
   * 結果: 機体情報の詳細が返される<br>
   * 料金情報が空であること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal_料金情報0件() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    // 機体情報エンティティ作成
    AircraftInfoEntity entity = createAircraftInfoEntity();
    entity.setImageBinary(null);
    entity.setAircraftId(aircraftId);
    entity.setOperatorId(userDto.getUserOperatorId());

    // 料金情報レスポンスDTO作成（空のリスト）
    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(new ArrayList<>());

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    AircraftInfoDetailResponseDto response =
        aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, true, userDto);

    assertNotNull(response);
    assertEquals(aircraftId.toString(), response.getAircraftId());

    // 料金情報の検証（空であること）
    assertNotNull(response.getPriceInfos());
    assertEquals(0, response.getPriceInfos().size());
    assertTrue(response.getPriceInfos().isEmpty());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報の詳細を取得する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 料金情報要否がtrue<br>
   * 料金情報3件<br>
   * 結果: 機体情報の詳細が返される<br>
   * 料金情報が3件含まれていること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_Normal_料金情報複数件() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    // 機体情報エンティティ作成
    AircraftInfoEntity entity = createAircraftInfoEntity();
    entity.setImageBinary(null);
    entity.setAircraftId(aircraftId);
    entity.setOperatorId(userDto.getUserOperatorId());

    // 料金情報レスポンスDTO作成（3件）
    PriceInfoSearchListDetailElement priceDetailElement1 = new PriceInfoSearchListDetailElement();
    priceDetailElement1.setPriceId(UUID.randomUUID().toString());
    priceDetailElement1.setPriceType(4);
    priceDetailElement1.setPricePerUnit(1);
    priceDetailElement1.setPrice(1000);
    priceDetailElement1.setPriority(1);
    priceDetailElement1.setOperatorId(userDto.getUserOperatorId());
    priceDetailElement1.setEffectiveStartTime("2025-11-13T10:00:00Z");
    priceDetailElement1.setEffectiveEndTime("2025-11-13T12:00:00Z");

    PriceInfoSearchListDetailElement priceDetailElement2 = new PriceInfoSearchListDetailElement();
    priceDetailElement2.setPriceId(UUID.randomUUID().toString());
    priceDetailElement2.setPriceType(4);
    priceDetailElement2.setPricePerUnit(1);
    priceDetailElement2.setPrice(2000);
    priceDetailElement2.setPriority(2);
    priceDetailElement2.setOperatorId(userDto.getUserOperatorId());
    priceDetailElement2.setEffectiveStartTime("2025-11-13T12:00:00Z");
    priceDetailElement2.setEffectiveEndTime("2025-11-13T14:00:00Z");

    PriceInfoSearchListDetailElement priceDetailElement3 = new PriceInfoSearchListDetailElement();
    priceDetailElement3.setPriceId(UUID.randomUUID().toString());
    priceDetailElement3.setPriceType(4);
    priceDetailElement3.setPricePerUnit(1);
    priceDetailElement3.setPrice(3000);
    priceDetailElement3.setPriority(3);
    priceDetailElement3.setOperatorId(userDto.getUserOperatorId());
    priceDetailElement3.setEffectiveStartTime("2025-11-13T14:00:00Z");
    priceDetailElement3.setEffectiveEndTime("2025-11-13T16:00:00Z");

    PriceInfoSearchListElement priceElement = new PriceInfoSearchListElement();
    priceElement.setResourceId(aircraftId.toString());
    priceElement.setResourceType(2);
    priceElement.setPriceInfos(
        List.of(priceDetailElement1, priceDetailElement2, priceDetailElement3));

    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(List.of(priceElement));

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    AircraftInfoDetailResponseDto response =
        aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, true, userDto);

    assertNotNull(response);
    assertEquals(aircraftId.toString(), response.getAircraftId());

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
   * 試験名: 機体情報の詳細を取得する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 料金情報要否がtrue<br>
   * 料金情報サービスでValidationErrorExceptionが発生<br>
   * 結果: ValidationErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_Error_料金情報でバリデーションエラー() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    // 機体情報エンティティ作成
    AircraftInfoEntity entity = createAircraftInfoEntity();
    entity.setImageBinary(null);
    entity.setAircraftId(aircraftId);
    entity.setOperatorId(userDto.getUserOperatorId());

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any()))
        .thenThrow(new ValidationErrorException("ValidationErrorException"));

    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class,
            () -> {
              aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, true, userDto);
            });

    assertEquals("ValidationErrorException", exception.getMessage());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any(UUID.class));
    verify(priceInfoSearchService, times(1)).getPriceInfoList(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体情報の詳細を取得する<br>
   * 条件: 正常な機体IDを渡す<br>
   * 料金情報要否がtrue<br>
   * 料金情報サービスでServiceErrorExceptionが発生<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_Error_料金情報でサービスエラー() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    // 機体情報エンティティ作成
    AircraftInfoEntity entity = createAircraftInfoEntity();
    entity.setImageBinary(null);
    entity.setAircraftId(aircraftId);
    entity.setOperatorId(userDto.getUserOperatorId());

    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any(UUID.class)))
        .thenReturn(Optional.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any()))
        .thenThrow(new ServiceErrorException("ServiceErrorException"));

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> {
              aircraftInfoServiceImpl.getDetail(aircraftId.toString(), false, true, userDto);
            });

    assertEquals("ServiceErrorException", exception.getMessage());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any(UUID.class));
    verify(priceInfoSearchService, times(1)).getPriceInfoList(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 結果: 機体情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    existingEntity.setOperatorId(userDto.getUserOperatorId());
    when(aircraftInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(existingEntity));

    requestDto.setOwnerId(ownerId.toString());
    requestDto.setCertification(String.valueOf(true));

    AircraftInfoSearchListResponseDto response =
        aircraftInfoServiceImpl.getList(requestDto, userDto);

    assertNotNull(response);
    assertFalse(response.getData().isEmpty());
    for (AircraftInfoSearchListElement dto : response.getData()) {
      assertEquals(aircraftId.toString(), dto.getAircraftId());
      assertEquals(existingEntity.getAircraftName(), dto.getAircraftName());
      assertEquals(existingEntity.getManufacturer(), dto.getManufacturer());
      assertEquals(existingEntity.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(existingEntity.getAircraftType(), dto.getAircraftType());
      assertEquals(existingEntity.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(existingEntity.getBodyWeight(), dto.getBodyWeight());
      assertEquals(existingEntity.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(existingEntity.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(existingEntity.getLat(), dto.getLat());
      assertEquals(existingEntity.getLon(), dto.getLon());
      assertEquals(existingEntity.getCertification(), dto.getCertification());
      assertEquals(existingEntity.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(existingEntity.getOwnerType(), dto.getOwnerType());
      assertEquals(existingEntity.getOwnerId().toString(), dto.getOwnerId());
    }
    assertNull(response.getPerPage());
    assertNull(response.getCurrentPage());
    assertNull(response.getLastPage());
    assertNull(response.getTotal());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 結果: 機体情報のリストが返される<br>
   * テストパターン：正常系<br>
   *
   * @param <U>
   */
  @SuppressWarnings({"unchecked"})
  @Test
  void getList_Normal2() {
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    List<AircraftInfoEntity> listEntity = List.of(existingEntity);
    Page<AircraftInfoEntity> pageList = new PageImpl<>(listEntity.subList(0, 1));
    when(aircraftInfoRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pageList);
    String perPage = "1";
    String page = "1";
    Integer lastPage = 1;
    Integer total = 1;
    String sortOrder = "1";
    String sortColumns = "aircraftId";

    requestDto.setOwnerId(ownerId.toString());
    requestDto.setCertification(String.valueOf(true));
    requestDto.setPerPage(perPage);
    requestDto.setPage(page);
    requestDto.setSortOrders(sortOrder);
    requestDto.setSortColumns(sortColumns);

    AircraftInfoSearchListResponseDto response =
        aircraftInfoServiceImpl.getList(requestDto, userDto);

    assertNotNull(response);
    assertFalse(response.getData().isEmpty());
    for (AircraftInfoSearchListElement dto : response.getData()) {
      assertEquals(aircraftId.toString(), dto.getAircraftId());
      assertEquals(existingEntity.getAircraftName(), dto.getAircraftName());
      assertEquals(existingEntity.getManufacturer(), dto.getManufacturer());
      assertEquals(existingEntity.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(existingEntity.getAircraftType(), dto.getAircraftType());
      assertEquals(existingEntity.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(existingEntity.getBodyWeight(), dto.getBodyWeight());
      assertEquals(existingEntity.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(existingEntity.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(existingEntity.getLat(), dto.getLat());
      assertEquals(existingEntity.getLon(), dto.getLon());
      assertEquals(existingEntity.getCertification(), dto.getCertification());
      assertEquals(existingEntity.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(existingEntity.getOwnerType(), dto.getOwnerType());
      assertEquals(existingEntity.getOwnerId().toString(), dto.getOwnerId());
    }
    assertEquals(perPage, response.getPerPage().toString());
    assertEquals(page, response.getCurrentPage().toString());
    assertEquals(lastPage, response.getLastPage());
    assertEquals(total, response.getTotal());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 結果: 機体情報のリストが返される<br>
   * テストパターン：正常系<br>
   *
   * @param <U>
   */
  @SuppressWarnings({"unchecked"})
  @Test
  void getList_Normal3() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    List<AircraftInfoEntity> listEntity = List.of(existingEntity);
    when(aircraftInfoRepository.findAll(any(Specification.class), any(Sort.class)))
        .thenReturn(listEntity);
    String sortOrder = "1";
    String sortColumns = "aircraftId";

    requestDto.setOwnerId(ownerId.toString());
    requestDto.setCertification(String.valueOf(true));
    requestDto.setSortOrders(sortOrder);
    requestDto.setSortColumns(sortColumns);

    AircraftInfoSearchListResponseDto response =
        aircraftInfoServiceImpl.getList(requestDto, userDto);

    assertNotNull(response);
    assertFalse(response.getData().isEmpty());
    for (AircraftInfoSearchListElement dto : response.getData()) {
      assertEquals(aircraftId.toString(), dto.getAircraftId());
      assertEquals(existingEntity.getAircraftName(), dto.getAircraftName());
      assertEquals(existingEntity.getManufacturer(), dto.getManufacturer());
      assertEquals(existingEntity.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(existingEntity.getAircraftType(), dto.getAircraftType());
      assertEquals(existingEntity.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(existingEntity.getBodyWeight(), dto.getBodyWeight());
      assertEquals(existingEntity.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(existingEntity.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(existingEntity.getLat(), dto.getLat());
      assertEquals(existingEntity.getLon(), dto.getLon());
      assertEquals(existingEntity.getCertification(), dto.getCertification());
      assertEquals(existingEntity.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(existingEntity.getOwnerType(), dto.getOwnerType());
      assertEquals(existingEntity.getOwnerId().toString(), dto.getOwnerId());
    }
    assertNull(response.getPerPage());
    assertNull(response.getCurrentPage());
    assertNull(response.getLastPage());
    assertNull(response.getTotal());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 結果: 機体情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_NullParam() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(null);
    existingEntity.setImageBinary(null);
    existingEntity.setImageFormat(null);
    when(aircraftInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(existingEntity));

    requestDto.setOwnerId(null);
    requestDto.setCertification(null);

    AircraftInfoSearchListResponseDto response =
        aircraftInfoServiceImpl.getList(requestDto, userDto);

    assertNotNull(response);
    assertFalse(response.getData().isEmpty());
    for (AircraftInfoSearchListElement dto : response.getData()) {
      assertEquals(aircraftId.toString(), dto.getAircraftId());
      assertEquals(existingEntity.getAircraftName(), dto.getAircraftName());
      assertEquals(existingEntity.getManufacturer(), dto.getManufacturer());
      assertEquals(existingEntity.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(existingEntity.getAircraftType(), dto.getAircraftType());
      assertEquals(existingEntity.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(existingEntity.getBodyWeight(), dto.getBodyWeight());
      assertEquals(existingEntity.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(existingEntity.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(existingEntity.getLat(), dto.getLat());
      assertEquals(existingEntity.getLon(), dto.getLon());
      assertEquals(existingEntity.getCertification(), dto.getCertification());
      assertEquals(existingEntity.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(existingEntity.getOwnerType(), dto.getOwnerType());
      assertNull(existingEntity.getOwnerId());
    }
    assertNull(response.getPerPage());
    assertNull(response.getCurrentPage());
    assertNull(response.getLastPage());
    assertNull(response.getTotal());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 結果: 機体情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_EmptyParam() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    when(aircraftInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(existingEntity));

    requestDto.setOwnerId("");
    requestDto.setCertification("");

    AircraftInfoSearchListResponseDto response =
        aircraftInfoServiceImpl.getList(requestDto, userDto);

    assertNotNull(response);
    assertFalse(response.getData().isEmpty());
    for (AircraftInfoSearchListElement dto : response.getData()) {
      assertEquals(aircraftId.toString(), dto.getAircraftId());
      assertEquals(existingEntity.getAircraftName(), dto.getAircraftName());
      assertEquals(existingEntity.getManufacturer(), dto.getManufacturer());
      assertEquals(existingEntity.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(existingEntity.getAircraftType(), dto.getAircraftType());
      assertEquals(existingEntity.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(existingEntity.getBodyWeight(), dto.getBodyWeight());
      assertEquals(existingEntity.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(existingEntity.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(existingEntity.getLat(), dto.getLat());
      assertEquals(existingEntity.getLon(), dto.getLon());
      assertEquals(existingEntity.getCertification(), dto.getCertification());
      assertEquals(existingEntity.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(existingEntity.getOwnerType(), dto.getOwnerType());
      assertEquals(existingEntity.getOwnerId().toString(), dto.getOwnerId());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 結果: 機体情報のリストが返される<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_BlankParam() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(ownerId);
    when(aircraftInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(existingEntity));

    requestDto.setOwnerId(" ");
    requestDto.setCertification(" ");

    AircraftInfoSearchListResponseDto response =
        aircraftInfoServiceImpl.getList(requestDto, userDto);

    assertNotNull(response);
    assertFalse(response.getData().isEmpty());
    for (AircraftInfoSearchListElement dto : response.getData()) {
      assertEquals(aircraftId.toString(), dto.getAircraftId());
      assertEquals(existingEntity.getAircraftName(), dto.getAircraftName());
      assertEquals(existingEntity.getManufacturer(), dto.getManufacturer());
      assertEquals(existingEntity.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(existingEntity.getAircraftType(), dto.getAircraftType());
      assertEquals(existingEntity.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(existingEntity.getBodyWeight(), dto.getBodyWeight());
      assertEquals(existingEntity.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(existingEntity.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(existingEntity.getLat(), dto.getLat());
      assertEquals(existingEntity.getLon(), dto.getLon());
      assertEquals(existingEntity.getCertification(), dto.getCertification());
      assertEquals(existingEntity.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(existingEntity.getOwnerType(), dto.getOwnerType());
      assertEquals(existingEntity.getOwnerId().toString(), dto.getOwnerId());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得に失敗する<br>
   * 条件: aircraft_idがnullで返ってくる<br>
   * 結果: ServiceErrorExceptionが返される<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_NotFound_AircraftId() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID ownerId = UUID.randomUUID();
    AircraftInfoSearchListRequestDto requestDto = new AircraftInfoSearchListRequestDto();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(null);
    existingEntity.setOwnerId(ownerId);
    when(aircraftInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(existingEntity));

    requestDto.setOwnerId(ownerId.toString());
    requestDto.setCertification(String.valueOf(true));

    assertThrows(
        ServiceErrorException.class, () -> aircraftInfoServiceImpl.getList(requestDto, userDto));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: ペイロード情報取得時に想定外エラーが発生した場合の挙動を確認する<br>
   * 条件: payloadInfoRepositoryがDuplicateKeyExceptionをスローする<br>
   * 結果: DuplicateKeyExceptionが発生すること<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_想定外エラー_ペイロード要否true() {
    // モック化
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(UUID.randomUUID());
    existingEntity.setOwnerId(UUID.randomUUID());
    when(aircraftInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(existingEntity));
    when(payloadInfoRepository.findAllByAircraftIdInAndDeleteFlagFalse(any()))
        .thenThrow(new DuplicateKeyException("上記以外の例外が発生"));

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPayloadInfo("true");

    // テスト実施
    Exception ex =
        assertThrows(
            DuplicateKeyException.class, () -> aircraftInfoServiceImpl.getList(dto, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(payloadInfoRepository, times(1))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: ペイロードIDがnullの場合の挙動を確認する<br>
   * 条件: ペイロード要否がtrueでペイロードIDがnullのエンティティを返す<br>
   * 結果: 機体情報リストが返されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_取得失敗_ペイロードIDnull() {
    // モック化
    UUID aircraftId = UUID.randomUUID();
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setAircraftId(aircraftId);
    existingEntity.setOwnerId(UUID.randomUUID());
    List<PayloadInfoEntity> payloadEntitylist = new ArrayList<>();
    PayloadInfoEntity payloadEntity = new PayloadInfoEntity();
    payloadEntity.setPayloadId(null);
    payloadEntity.setAircraftId(aircraftId);
    payloadEntitylist.add(payloadEntity);

    when(aircraftInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(existingEntity));
    when(payloadInfoRepository.findAllByAircraftIdInAndDeleteFlagFalse(any()))
        .thenReturn(payloadEntitylist);

    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPayloadInfo("true");

    // テスト実施
    Exception ex =
        assertThrows(
            ServiceErrorException.class, () -> aircraftInfoServiceImpl.getList(dto, userDto));
    assertEquals("ペイロード情報の取得に失敗しました。", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(payloadInfoRepository, times(1))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 料金情報要否がtrue<br>
   * 機体情報0件<br>
   * 結果: 機体情報のリストが空で返される<br>
   * 料金情報サービスが呼ばれないこと<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal6_機体情報0件() {
    // リクエストDTO作成
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPriceInfo("true");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // 機体情報0件
    when(aircraftInfoRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

    AircraftInfoSearchListResponseDto response = aircraftInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertNotNull(response.getData());
    assertEquals(0, response.getData().size());
    assertTrue(response.getData().isEmpty());

    // 料金情報サービスが呼ばれていないことを確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchService, never()).getPriceInfoList(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 料金情報要否がtrue<br>
   * 機体情報1件<br>
   * 料金情報1件<br>
   * 結果: 機体情報のリストが返される<br>
   * 料金情報が含まれていること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal6_料金情報1件() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    // リクエストDTO作成
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPriceInfo("true");

    // 機体情報エンティティ作成
    AircraftInfoEntity entity = new AircraftInfoEntity();
    entity.setAircraftId(aircraftId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(userDto.getUserOperatorId());

    // 料金情報レスポンスDTO作成
    PriceInfoSearchListDetailElement priceDetailElement = new PriceInfoSearchListDetailElement();
    priceDetailElement.setPriceId(UUID.randomUUID().toString());
    priceDetailElement.setPriceType(4);
    priceDetailElement.setPricePerUnit(1);
    priceDetailElement.setPrice(1000);
    priceDetailElement.setPriority(1);
    priceDetailElement.setOperatorId(userDto.getUserOperatorId());
    priceDetailElement.setEffectiveStartTime("2025-11-13T10:00:00Z");
    priceDetailElement.setEffectiveEndTime("2025-11-13T10:00:00Z");

    PriceInfoSearchListElement priceElement = new PriceInfoSearchListElement();
    priceElement.setResourceId(aircraftId.toString());
    priceElement.setResourceType(2);
    priceElement.setPriceInfos(List.of(priceDetailElement));

    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(List.of(priceElement));

    when(aircraftInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    AircraftInfoSearchListResponseDto response = aircraftInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      AircraftInfoSearchListElement element = response.getData().get(0);
      assertEquals(entity.getAircraftId().toString(), element.getAircraftId());

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
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 料金情報要否がtrue<br>
   * 機体情報1件<br>
   * 料金情報0件<br>
   * 結果: 機体情報のリストが返される<br>
   * 料金情報が空であること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal6_料金情報0件() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    // リクエストDTO作成
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPriceInfo("true");

    // 機体情報エンティティ作成
    AircraftInfoEntity entity = new AircraftInfoEntity();
    entity.setAircraftId(aircraftId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(userDto.getUserOperatorId());

    // 料金情報レスポンスDTO作成（空のリスト）
    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(new ArrayList<>());

    when(aircraftInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    AircraftInfoSearchListResponseDto response = aircraftInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(1, response.getData().size());
    {
      AircraftInfoSearchListElement element = response.getData().get(0);
      assertEquals(entity.getAircraftId().toString(), element.getAircraftId());

      // 料金情報の検証（空であること）
      assertNotNull(element.getPriceInfos());
      assertEquals(0, element.getPriceInfos().size());
      assertTrue(element.getPriceInfos().isEmpty());
    }
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 料金情報要否がtrue<br>
   * 機体情報3件<br>
   * 料金情報3件（0件、1件、3件）<br>
   * 結果: 機体情報のリストが返される<br>
   * 料金情報が含まれていること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Normal6_料金情報複数件() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId1 = UUID.randomUUID();
    UUID aircraftId2 = UUID.randomUUID();
    UUID aircraftId3 = UUID.randomUUID();

    // リクエストDTO作成
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPriceInfo("true");

    // 機体情報エンティティ作成（3件）
    AircraftInfoEntity entity1 = new AircraftInfoEntity();
    entity1.setAircraftId(aircraftId1);
    entity1.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity1.setOperatorId(userDto.getUserOperatorId());

    AircraftInfoEntity entity2 = new AircraftInfoEntity();
    entity2.setAircraftId(aircraftId2);
    entity2.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T03:00:00Z")));
    entity2.setOperatorId(userDto.getUserOperatorId());

    AircraftInfoEntity entity3 = new AircraftInfoEntity();
    entity3.setAircraftId(aircraftId3);
    entity3.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T05:00:00Z")));
    entity3.setOperatorId(userDto.getUserOperatorId());

    // 料金情報レスポンスDTO作成
    // 機体1: 料金情報0件
    // レスポンスにリソースがなし

    // 機体2: 料金情報1件
    PriceInfoSearchListDetailElement priceDetailElement2_1 = new PriceInfoSearchListDetailElement();
    priceDetailElement2_1.setPriceId(UUID.randomUUID().toString());
    priceDetailElement2_1.setPriceType(4);
    priceDetailElement2_1.setPricePerUnit(1);
    priceDetailElement2_1.setPrice(1000);
    priceDetailElement2_1.setPriority(1);
    priceDetailElement2_1.setOperatorId(userDto.getUserOperatorId());
    priceDetailElement2_1.setEffectiveStartTime("2025-11-13T10:00:00Z");
    priceDetailElement2_1.setEffectiveEndTime("2025-11-13T12:00:00Z");

    PriceInfoSearchListElement priceElement2 = new PriceInfoSearchListElement();
    priceElement2.setResourceId(aircraftId2.toString());
    priceElement2.setResourceType(2);
    priceElement2.setPriceInfos(List.of(priceDetailElement2_1));

    // 機体3: 料金情報3件
    PriceInfoSearchListDetailElement priceDetailElement3_1 = new PriceInfoSearchListDetailElement();
    priceDetailElement3_1.setPriceId(UUID.randomUUID().toString());
    priceDetailElement3_1.setPriceType(4);
    priceDetailElement3_1.setPricePerUnit(1);
    priceDetailElement3_1.setPrice(2000);
    priceDetailElement3_1.setPriority(1);
    priceDetailElement3_1.setOperatorId(userDto.getUserOperatorId());
    priceDetailElement3_1.setEffectiveStartTime("2025-11-13T12:00:00Z");
    priceDetailElement3_1.setEffectiveEndTime("2025-11-13T14:00:00Z");

    PriceInfoSearchListDetailElement priceDetailElement3_2 = new PriceInfoSearchListDetailElement();
    priceDetailElement3_2.setPriceId(UUID.randomUUID().toString());
    priceDetailElement3_2.setPriceType(4);
    priceDetailElement3_2.setPricePerUnit(1);
    priceDetailElement3_2.setPrice(3000);
    priceDetailElement3_2.setPriority(2);
    priceDetailElement3_2.setOperatorId(userDto.getUserOperatorId());
    priceDetailElement3_2.setEffectiveStartTime("2025-11-13T14:00:00Z");
    priceDetailElement3_2.setEffectiveEndTime("2025-11-13T16:00:00Z");

    PriceInfoSearchListDetailElement priceDetailElement3_3 = new PriceInfoSearchListDetailElement();
    priceDetailElement3_3.setPriceId(UUID.randomUUID().toString());
    priceDetailElement3_3.setPriceType(4);
    priceDetailElement3_3.setPricePerUnit(1);
    priceDetailElement3_3.setPrice(4000);
    priceDetailElement3_3.setPriority(3);
    priceDetailElement3_3.setOperatorId(userDto.getUserOperatorId());
    priceDetailElement3_3.setEffectiveStartTime("2025-11-13T16:00:00Z");
    priceDetailElement3_3.setEffectiveEndTime("2025-11-13T18:00:00Z");

    PriceInfoSearchListElement priceElement3 = new PriceInfoSearchListElement();
    priceElement3.setResourceId(aircraftId3.toString());
    priceElement3.setResourceType(2);
    priceElement3.setPriceInfos(
        List.of(priceDetailElement3_1, priceDetailElement3_2, priceDetailElement3_3));

    PriceInfoSearchListResponseDto priceInfoResponse = new PriceInfoSearchListResponseDto();
    priceInfoResponse.setResources(List.of(priceElement2, priceElement3));

    when(aircraftInfoRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(entity1, entity2, entity3));
    when(priceInfoSearchService.getPriceInfoList(any())).thenReturn(priceInfoResponse);

    AircraftInfoSearchListResponseDto response = aircraftInfoServiceImpl.getList(dto, userDto);

    assertNotNull(response);
    assertEquals(3, response.getData().size());

    // 機体1の検証（料金情報0件）
    {
      AircraftInfoSearchListElement element = response.getData().get(0);
      assertEquals(entity1.getAircraftId().toString(), element.getAircraftId());

      // 料金情報の検証（0件）
      assertNotNull(element.getPriceInfos());
      assertTrue(element.getPriceInfos().isEmpty());
    }

    // 機体2の検証（料金情報1件）
    {
      AircraftInfoSearchListElement element = response.getData().get(1);
      assertEquals(entity2.getAircraftId().toString(), element.getAircraftId());

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

    // 機体3の検証（料金情報3件）
    {
      AircraftInfoSearchListElement element = response.getData().get(2);
      assertEquals(entity3.getAircraftId().toString(), element.getAircraftId());

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
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 料金情報要否がtrue<br>
   * 料金情報サービスでValidationErrorExceptionが発生<br>
   * 結果: ValidationErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Error_料金情報でバリデーションエラー() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    // リクエストDTO作成
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPriceInfo("true");

    // 機体情報エンティティ作成
    AircraftInfoEntity entity = new AircraftInfoEntity();
    entity.setAircraftId(aircraftId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(userDto.getUserOperatorId());

    when(aircraftInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any()))
        .thenThrow(new ValidationErrorException("ValidationErrorException"));

    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class,
            () -> {
              aircraftInfoServiceImpl.getList(dto, userDto);
            });

    assertEquals("ValidationErrorException", exception.getMessage());
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchService, times(1)).getPriceInfoList(any());
  }

  private LocalDateTime toUtcLocalDateTime(String str) {
    return ZonedDateTime.parse(str).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体情報のリストを取得する<br>
   * 条件: 正常なリクエストDTOを渡す<br>
   * 料金情報要否がtrue<br>
   * 料金情報サービスでServiceErrorExceptionが発生<br>
   * 結果: ServiceErrorExceptionがスローされる<br>
   * テストパターン：異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Error_料金情報でサービスエラー() {
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    UUID aircraftId = UUID.randomUUID();

    // リクエストDTO作成
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPriceInfo("true");

    // 機体情報エンティティ作成
    AircraftInfoEntity entity = new AircraftInfoEntity();
    entity.setAircraftId(aircraftId);
    entity.setUpdateTime(Timestamp.valueOf(toUtcLocalDateTime("2024-12-04T01:00:00Z")));
    entity.setOperatorId(userDto.getUserOperatorId());

    when(aircraftInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
    when(priceInfoSearchService.getPriceInfoList(any()))
        .thenThrow(new ServiceErrorException("ServiceErrorException"));

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> {
              aircraftInfoServiceImpl.getList(dto, userDto);
            });

    assertEquals("ServiceErrorException", exception.getMessage());
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchService, times(1)).getPriceInfoList(any());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: Base64デコードの正常系テスト<br>
   * 条件: 正常なBase64エンコード文字列を渡す<br>
   * 結果: デコードされたバイナリデータが設定される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void decodeBinary_Normal() {
    AircraftInfoRequestDto request = new AircraftInfoRequestDto();
    String base64Data =
        "data:image/png;base64," + Base64.getEncoder().encodeToString("testData".getBytes());
    request.setImageData(base64Data);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertArrayEquals("testData".getBytes(), request.getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: Base64デコードの正常系テスト<br>
   * 条件: データURL未設定なBase64エンコード文字列を渡す<br>
   * 結果: デコードされたバイナリデータが設定される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void decodeBinary_Normal_noDataUrl() {
    AircraftInfoRequestDto request = new AircraftInfoRequestDto();
    String base64Data = Base64.getEncoder().encodeToString("testData".getBytes());
    request.setImageData(base64Data);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertNull(request.getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: 空のBase64エンコード文字列を渡した場合に空のバイナリデータが設定されることを確認する<br>
   * 条件: 空のBase64エンコード文字列を渡す<br>
   * 結果: 空のバイナリデータが設定されること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void decodeBinary_Empty() {
    AircraftInfoRequestDto request = new AircraftInfoRequestDto();
    request.setImageData("");

    aircraftInfoServiceImpl.decodeBinary(request);

    assertNotNull(request.getImageBinary());
    assertArrayEquals(new byte[] {}, request.getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: nullのBase64エンコード文字列を渡した場合にバイナリデータが設定されないことを確認する<br>
   * 条件: nullのBase64エンコード文字列を渡す<br>
   * 結果: バイナリデータが設定されないこと<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void decodeBinary_Null() {
    AircraftInfoRequestDto request = new AircraftInfoRequestDto();
    request.setImageData(null);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertNull(request.getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: ペイロード情報のBase64デコードが正常に行われることを確認する<br>
   * 条件: 正常なBase64エンコード文字列を渡す<br>
   * 結果: デコードされたバイナリデータが設定されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeBinary_payload_Normal() {
    AircraftInfoRequestDto request = createAircraftInfoRequestDto_payload1();
    request.setImageData(null);
    String base64Data =
        "data:image/png;base64," + Base64.getEncoder().encodeToString("testData".getBytes());
    request.getPayloadInfos().get(0).setImageData(base64Data);
    request.getPayloadInfos().get(0).setImageBinary(null);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertArrayEquals("testData".getBytes(), request.getPayloadInfos().get(0).getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: ペイロード情報のBase64デコードの正常系テスト<br>
   * 条件: データURL未設定なBase64エンコード文字列を渡す<br>
   * 結果: デコードされたバイナリデータが設定されない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void decodeBinary_payload_Normal_noDataUrl() {
    AircraftInfoRequestDto request = createAircraftInfoRequestDto_payload1();
    request.setImageData(null);
    String base64Data = Base64.getEncoder().encodeToString("testData".getBytes());
    request.getPayloadInfos().get(0).setImageData(base64Data);
    request.getPayloadInfos().get(0).setImageBinary(null);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertNull(request.getPayloadInfos().get(0).getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: 空のBase64エンコード文字列を渡した場合に空のバイナリデータが設定されることを確認する<br>
   * 条件: 空のBase64エンコード文字列を渡す<br>
   * 結果: 空のバイナリデータが設定されること<br>
   * テストパターン：エッジケース<br>
   */
  @Test
  void decodeBinary_payload_Empty() {
    AircraftInfoRequestDto request = createAircraftInfoRequestDto_payloadEmpList();
    request.setImageData(null);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertNotNull(request.getPayloadInfos());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: ペイロード情報のnullの場合のテスト<br>
   * 条件: nullのBase64エンコード文字列を渡す<br>
   * 結果: バイナリデータが設定されない<br>
   * テストパターン: エッジケース<br>
   */
  @Test
  void decodeBinary_payload_Null() {
    AircraftInfoRequestDto request = createAircraftInfoRequestDto_payloadNull();
    request.setImageData(null);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertNull(request.getPayloadInfos());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: ペイロード情報のBase64デコードの正常系テスト<br>
   * 条件: 正常なBase64エンコード文字列を渡す<br>
   * 結果: デコードされたバイナリデータが設定される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void decodeBinary_payload_Normal_3件() {
    AircraftInfoRequestDto request = createAircraftInfoRequestDto_payload3();
    request.setImageData(null);
    String base64Data =
        "data:image/png;base64," + Base64.getEncoder().encodeToString("testData".getBytes());
    request.getPayloadInfos().get(0).setImageData(base64Data);
    request.getPayloadInfos().get(0).setImageBinary(null);
    request.getPayloadInfos().get(1).setImageData(base64Data);
    request.getPayloadInfos().get(1).setImageBinary(null);
    request.getPayloadInfos().get(2).setImageData(base64Data);
    request.getPayloadInfos().get(2).setImageBinary(null);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertArrayEquals("testData".getBytes(), request.getPayloadInfos().get(0).getImageBinary());
    assertArrayEquals("testData".getBytes(), request.getPayloadInfos().get(1).getImageBinary());
    assertArrayEquals("testData".getBytes(), request.getPayloadInfos().get(2).getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: ペイロード情報の空文字列の場合のテスト<br>
   * 条件: 空のBase64エンコード文字列を渡す<br>
   * 結果: 空のバイナリデータが設定される<br>
   * テストパターン: エッジケース<br>
   */
  @Test
  void decodeBinary_payload_imageDataEmpty() {
    AircraftInfoRequestDto request = createAircraftInfoRequestDto_payload1();
    request.setImageData(null);
    request.getPayloadInfos().get(0).setImageData("");
    request.getPayloadInfos().get(0).setImageBinary(null);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertNotNull(request.getPayloadInfos().get(0).getImageBinary());
    assertArrayEquals(new byte[] {}, request.getPayloadInfos().get(0).getImageBinary());
  }

  /**
   * メソッド名: decodeBinary<br>
   * 試験名: ペイロード情報のnullの場合のテスト<br>
   * 条件: nullのBase64エンコード文字列を渡す<br>
   * 結果: バイナリデータが設定されない<br>
   * テストパターン: エッジケース<br>
   */
  @Test
  void decodeBinary_payload_imageDataNull() {
    AircraftInfoRequestDto request = createAircraftInfoRequestDto_payload1();
    request.setImageData(null);
    request.getPayloadInfos().get(0).setImageData(null);
    request.getPayloadInfos().get(0).setImageBinary(null);

    aircraftInfoServiceImpl.decodeBinary(request);

    assertNull(request.getPayloadInfos().get(0).getImageBinary());
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

  /** データテンプレート ■補足資料情報Entity(FileInfoEntity) 補足資料情報Entity6(save結果_異常) */
  private FileInfoEntity createFileInfoEntity_save_errl() {
    FileInfoEntity ret = new FileInfoEntity();
    ret.setFileId(null);
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

  /** データテンプレート ■ペイロード情報Entity(FileInfoEntity) ペイロード情報Entity11(save結果_異常) */
  private PayloadInfoEntity createPayloadInfoEntity_save_errl() {
    PayloadInfoEntity ret = new PayloadInfoEntity();
    ret.setPayloadId(null);
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setPayloadNumber(1);
    ret.setPayloadName("ペイロード1");
    ret.setPayloadDetailText("ペイロード詳細テキスト");
    ret.setImageData(
        new byte[] {
          -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -11, 0, 0, 0, 110,
          8, 6, 0, 0, 0, -1, 16, 85, -27, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0,
          0, 4, 103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0,
          14, -61, 0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 7, 29, 73, 68, 65, 84, 120, 94, -19,
          -35, 33, 84, -20, 58, 23, 5, -32, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 114, 36,
          18, -119, 67, 34, -111, 72, 36, 18, -119, 68, 34, -111, 72, 36, 18, -119, -20, 93, -5,
          -66, -101, -5, -50, 28, -46, -71, -19, 52, 77, -109, -99, -3, -83, -107, -75, -2, -9, -65,
          39, -26, 116, -78, 39, 105, -110, -106, 85, 39, 34, 84, 86, -2, -1, 16, -111, -70, 41,
          -44, 34, 100, 20, 106, 17, 50, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95,
          22, 33, -93, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75,
          8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88,
          -91, 89, -49, -49, -49, -35, -23, -23, 105, -9, -16, -16, -32, -1, 85, -43, 20, 106, 105,
          -42, -63, -63, 65, -73, 90, -83, -70, -67, -67, 61, -1, -81, -86, -90, 80, 75, -77, 16,
          -24, -48, -104, 112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112,
          105, 62, -44, 88, -2, 63, 60, 60, -36, -72, 16, -75, 52, 124, 110, -74, 109, 11, -103,
          -50, -10, 17, 38, -125, -85, -87, 53, -48, -95, -79, 109, 91, -56, 116, -74, 127, 48, 25,
          92, -51, -19, -19, -19, -113, -96, -44, -44, 46, 47, 47, 125, 73, -46, 56, -37, 63, -104,
          112, 85, 35, 50, -126, 66, 45, 66, 70, -95, 22, 33, -93, 80, -117, -112, 81, -88, 101, 39,
          53, 108, 5, -30, -13, -35, -33, -33, -5, -113, 78, -49, 94, 3, 38, 92, -43, 20, -88, -12,
          64, -37, 118, 115, 115, -29, 63, 62, 53, 91, 59, 19, -82, 106, 10, 84, -29, 86, 96, 43,
          -121, 117, 108, -51, 76, -72, -86, -111, -47, -66, -65, -65, -69, -77, -77, -77, 31, -63,
          110, -31, -80, -114, 66, 45, -76, 16, -20, -11, 122, -3, 35, -40, -20, 88, 107, -27, -86,
          70, 38, 99, -19, -24, 49, -84, -75, 114, 85, 35, -109, -79, 118, -12, 24, -42, 90, -71,
          -86, -111, -55, 88, 59, 122, 12, 107, -83, 92, -43, -56, 100, -84, 29, 61, -122, -75, 86,
          -82, 106, 100, 18, -20, 83, -77, 118, -12, 24, -42, 90, -109, 85, 83, -54, -55, 41, 124,
          -122, -117, -117, -117, -33, 39, -92, -34, -33, -33, -3, -57, -108, 30, 62, -48, -72,
          -122, -20, 108, -67, 76, -110, 85, 83, 66, -96, -89, 54, -44, 112, 125, 125, -3, 123,
          -117, -89, 37, 62, -48, -40, -73, 110, -31, 26, -40, -102, -103, 36, -85, -90, -58, -109,
          83, 125, -19, -24, -24, -88, 123, 125, 125, -11, 37, 82, 106, 53, -48, 96, -21, 102, -62,
          85, 77, -41, 117, 111, 111, 111, -35, -35, -35, 93, 119, 126, 126, -34, -19, -17, -17, -1,
          8, -20, -40, -58, 124, 100, -78, -27, 64, -125, -83, -99, 9, 87, 53, 19, 33, -68, -79, 31,
          2, -58, 35, -109, -83, 7, 26, 20, -22, 70, 124, 126, 126, -2, 56, 11, -51, -10, 126, 51,
          -52, 100, 90, 15, 52, 40, -44, 66, -31, -23, -23, 73, -127, -2, 67, -95, -82, 0, -21, -97,
          38, 77, 5, -21, 13, -72, -107, 8, 29, 25, -41, -86, -43, 64, -125, 66, 93, 1, -42, 63, 77,
          -102, -62, -57, -57, -57, -58, -74, 35, -2, -9, -41, -41, -105, -1, -49, -102, -94, 80,
          87, -128, -11, 75, -102, 10, -93, -15, -15, -15, -15, -33, 107, -125, -59, 64, 29, -52,
          -31, -19, 47, 52, -43, -96, -29, -78, 126, 73, 83, 97, 123, 47, 92, 23, -52, 98, 94, 94,
          94, -4, 127, -46, 36, -42, -2, 66, 83, -115, 93, -47, -59, -88, 36, -1, -71, -70, -70,
          -38, -24, -68, 45, -66, 96, -48, 10, -57, -103, -3, 9, 72, 38, 20, -43, 96, -108, 14, -9,
          -45, -22, -72, -1, -13, 91, 87, 8, 120, -21, 124, -104, 67, 11, -49, 12, 96, -111, 21,
          -37, -102, 53, -93, 8, -11, -29, -29, -29, -33, 47, 7, -31, 110, 121, 69, 55, -16, 35, 52,
          -90, -32, 50, -4, 56, 51, 102, 123, 120, 14, -96, -58, -29, -62, 20, -95, -74, -117, 64,
          24, -99, 90, -122, 31, 52, 123, 15, 29, 58, -88, 126, -24, 54, -7, -3, -6, 109, 13, 11,
          -117, 53, -11, -85, -22, 67, 109, -65, 28, 44, 2, -75, -36, 121, 49, 109, -76, 63, 112,
          104, 56, 92, -46, -6, -42, 85, 31, 123, -99, 48, 34, 99, 100, -10, -41, -49, -74, 90, -34,
          -117, 94, 125, -88, 113, -128, 34, 92, -12, -106, -17, 25, 113, -80, -60, -33, 47, -78,
          29, 111, 77, -51, 94, 43, 11, 63, -114, -72, -73, -58, 61, -74, 127, 22, -96, -122, 96,
          87, 29, 106, 116, -28, 112, -79, 49, 74, -41, -66, -64, -79, 43, -84, -24, -6, -50, 87,
          -45, 116, 113, 41, 125, -95, -74, 48, -13, -13, -49, 2, -108, 30, -20, -2, 106, 42, 96,
          -17, 29, 91, 29, -107, 48, -94, -40, 14, -121, 31, 55, -36, -110, -56, -65, 13, 9, 53,
          -44, 22, -20, -19, -43, 20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84, -4,
          -73, 120, 29, 118, 53, 52, -44, 80, 83, -80, -1, 93, 77, -95, -20, -106, 77, -117, -37,
          53, 62, -48, 88, -32, -63, -7, 110, 25, 110, 76, -88, 33, 22, 108, -52, 16, 75, -69, -19,
          27, 86, 77, 97, 112, 17, -19, -45, 70, -83, -115, 78, -79, 64, 107, -123, 123, -68, -79,
          -95, -122, 88, -80, -47, 23, 49, -56, -108, 18, -18, -31, -43, 20, -60, -114, -46, -83,
          29, 9, 85, -96, -45, -39, 37, -44, 16, 59, 11, 16, -62, 93, -62, -117, 43, -57, 85, 83, 8,
          -69, 117, -45, -46, -94, -112, 2, -99, -42, -82, -95, 14, -48, -9, 98, -5, -38, 75, -65,
          -72, 114, -73, 106, 22, -124, 41, 78, -72, 120, -40, -58, 105, -123, 63, -34, -88, 64, 79,
          55, 53, -44, 65, 95, -72, -105, -102, -110, 79, -85, 102, 1, -10, 4, 25, 14, -98, -80,
          -61, 99, -110, -8, -27, 31, 19, -24, 49, 127, 88, -95, -43, 119, -99, -125, -67, 14, 41,
          -12, -67, -72, 50, 92, -25, 92, 15, 26, -91, -87, 38, 35, -5, 22, 76, -26, 19, 100, -8,
          -123, -57, -119, 38, -33, 57, 78, 78, 78, -74, 6, 26, -122, 6, -38, -74, -91, -89, -116,
          75, -80, -11, -89, 18, 123, 113, -91, 109, 57, -74, -63, -46, 85, -109, -119, 93, -96,
          -64, -45, 89, -116, 112, 26, -52, -1, -30, -29, -97, 49, 5, 31, -62, 79, -43, -57, -76,
          -91, -90, -116, 75, -80, 117, -89, -122, 25, -106, 61, -62, 108, -37, -48, -17, 113, 87,
          -23, -85, -103, -111, -33, -54, 98, 124, 37, -113, 127, 31, 55, 26, 70, -20, -71, -126,
          22, -101, 50, -106, -74, 69, 51, 23, 91, -13, -100, -4, 54, -40, -36, -17, -48, -101, -73,
          -102, -60, -40, -73, -78, 112, 111, 107, 59, 26, -90, -60, 57, 94, 61, -44, 55, 101, 100,
          15, 119, -82, 80, -125, 127, -35, -42, -100, 127, -7, 101, -2, 106, 18, -15, -93, 52, -45,
          86, 22, 102, 28, -72, 87, -74, 95, -6, 18, -17, -29, -18, -101, 50, -78, -122, 59, 103,
          -88, -63, -10, -33, 112, 93, -25, -112, -89, -102, 4, 88, 71, 105, -36, 95, -7, 47, 123,
          -119, 64, 91, -37, -62, 61, -9, -3, 96, 78, -71, 67, -19, -41, 58, -26, 122, 8, 41, 79,
          53, 19, -95, -109, -79, -115, -46, -79, -47, -71, -76, -48, -12, -19, -65, -26, 88, -63,
          -51, 33, 119, -88, 115, 41, -66, 26, 60, -92, 96, 95, 42, -120, 32, -44, 12, 35, 112, 108,
          116, 70, 93, -91, 46, -4, -59, -62, -115, -103, -45, -110, -77, -119, 20, 20, -22, 5, -96,
          -45, -40, -47, 12, -31, -82, -7, -66, 46, 118, -112, -92, -76, -47, -71, -113, 95, -63,
          69, -85, 125, 111, 91, -95, 94, -64, 122, -67, -34, -24, -4, -75, 118, -96, 109, 7, 73,
          74, 29, -99, 99, -6, 30, 100, -88, -11, 68, -102, 66, -99, -103, 127, 103, 117, -82, 35,
          118, 41, -31, -111, 80, 76, 83, -3, 62, -16, -104, -125, 36, 37, -118, -19, 109, -29, 7,
          -86, -74, -25, -71, 21, -22, -116, -4, -45, 72, 115, -83, 18, -50, 33, 4, -71, -17, -88,
          -26, -100, 7, 73, 114, -118, -19, 109, 35, -24, 53, 45, 98, 42, -44, -103, -40, 23, -13,
          -93, 45, -67, -67, 51, 20, 70, -34, -66, 32, -93, -27, 58, 72, -110, -101, -97, 81, -95,
          -31, -74, -87, -122, -37, 10, -123, 58, 19, -69, -46, 93, 67, -96, 99, 91, 83, -95, -95,
          22, -52, 50, 24, -61, 108, 97, -83, -61, 126, 111, -95, -31, -70, 96, -42, 85, -22, 119,
          -88, 80, -49, 44, -10, -72, 96, -87, -99, 1, -9, -114, -24, -84, 24, -111, -4, -42, 84,
          43, 65, -10, -16, -28, 88, -20, -64, -118, 109, 115, 30, -115, -36, -123, 66, 61, 51, 31,
          -24, -110, 46, -76, 13, 113, -20, 115, -94, -43, -78, 53, 53, 55, -116, -38, -79, 31, 59,
          123, -99, 74, 81, 98, 95, 75, -95, -104, 106, 114, 29, -95, 27, 34, 54, 107, -40, -42,
          106, -37, -102, -54, 1, 35, 55, 118, 44, -4, -66, -4, -110, -33, -85, 103, 63, 23, 19,
          -82, 106, 18, -119, -35, 31, -38, -122, -47, 6, -9, -5, -8, 33, -86, 117, -17, 92, 20,
          -22, -90, 40, -60, 109, 80, -88, 27, 98, -17, 7, -123, -105, 66, -35, -112, 112, 127, 95,
          -46, -3, -97, -92, -89, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, -20, -38, 9,
          -45, 97, 33, -123, 90, -102, 101, 31, -121, -59, -71, -124, 82, 79, 48, -114, -91, 80, 75,
          -77, -16, -92, -103, 125, -124, -108, 101, 97, 84, -95, -106, -90, -39, -57, 124, 75, 58,
          -62, 58, -123, 66, 45, -51, 99, 91, 48, -29, -88, 66, 100, 2, -123, 90, -124, -116, 66,
          45, 66, -58, 110, 109, 49, -68, -45, 92, -95, -106, -26, -39, -65, -2, -126, 86, -5, 115,
          -15, 10, -75, 52, -49, -65, -45, -68, -10, 85, 112, -123, 90, -28, 79, -80, -61, 97, 20,
          -68, -71, -91, 102, 10, -75, 8, 25, -123, 90, -124, -116, 66, 45, 66, 70, -95, 22, 33,
          -93, 80, -117, -112, 81, -88, 69, -56, 40, -44, 34, 100, 20, 106, 17, 50, 10, -75, 8,
          -103, 95, 49, -1, -7, -61, 81, 69, -111, -101, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96,
          -126
        });
    ret.setImageFormat("png");
    ret.setFilePhysicalName("payload_hosoku.txt");
    ret.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ret.setFileFormat("text/plain");
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** データテンプレート ■機体情報Entity(AircraftInfoEntity) 機体情報Entity2(save結果_異常) */
  private AircraftInfoEntity createAircraftInfo_save_err() {
    AircraftInfoEntity ret = new AircraftInfoEntity();
    ret.setAircraftId(null);
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
    ret.setOwnerId(UUID.fromString("054bb198-ab4c-4bb1-a27c-78af8e495f7a"));
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
    ret.setImageFormat("png");
    ret.setOperatorId("ope01");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  // 料金情報用リクエストDTO作成
  private AircraftInfoRequestDto createOnePriceInfoAircraftInfoRequestDto() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setAircraftName("機体名機体名");
    dto.setManufacturer("製造メーカー製造メーカー");
    dto.setManufacturingNumber("N12345678");
    dto.setAircraftType(1);
    dto.setMaxTakeoffWeight((double) 99);
    dto.setBodyWeight((double) 88);
    dto.setMaxFlightSpeed((double) 77);
    dto.setMaxFlightTime((double) 66);
    dto.setLat((double) 55);
    dto.setLon((double) 44);
    dto.setCertification(true);
    dto.setDipsRegistrationCode("DIPS_1234");
    dto.setOwnerType(1);
    dto.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    dto.setImageData(
        "data:image/png;base64,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");
    dto.setPublicFlag(true);

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

  private AircraftInfoRequestDto createThreePriceInfoAircraftInfoRequestDto() {
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setAircraftName("機体名機体名");
    dto.setManufacturer("製造メーカー製造メーカー");
    dto.setManufacturingNumber("N12345678");
    dto.setAircraftType(1);
    dto.setMaxTakeoffWeight((double) 99);
    dto.setBodyWeight((double) 88);
    dto.setMaxFlightSpeed((double) 77);
    dto.setMaxFlightTime((double) 66);
    dto.setLat((double) 55);
    dto.setLon((double) 44);
    dto.setCertification(true);
    dto.setDipsRegistrationCode("DIPS_1234");
    dto.setOwnerType(1);
    dto.setOwnerId("054bb198-ab4c-4bb1-a27c-78af8e495f7a");
    dto.setImageData(
        "data:image/png;base64,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");

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
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84
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
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84
    };
    payload1.setImageBinary(payload1Bytes);
    payload1.setFilePhysicalName("payload_hosoku1.txt");
    payload1.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file1Byetes = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
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
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84
    };
    payload2.setImageBinary(payload2Bytes);
    payload2.setFilePhysicalName("payload_hosoku2.txt");
    payload2.setFileData("data:text/plain;base64,MuijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file2Byetes = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
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
        "data:image/png;base64,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");
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
      20, 12, 39, -94, -20, 69, -58, -88, -35, 26, 31, 104, -84
    };
    payload3.setImageBinary(payload3Bytes);
    payload3.setFilePhysicalName("payload_hosoku3.txt");
    payload3.setFileData("data:text/plain;base64,M+ijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    byte[] file3Byetes = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
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

  /** ユーザ情報DTO(自事業者) */
  private UserInfoDto createUserInfoDto_OwnOperator() {
    UserInfoDto ret = new UserInfoDto();
    ret.setUserOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setAffiliatedOperatorId("12345678-1234-1234-1234-123456789abc");
    return ret;
  }
}
