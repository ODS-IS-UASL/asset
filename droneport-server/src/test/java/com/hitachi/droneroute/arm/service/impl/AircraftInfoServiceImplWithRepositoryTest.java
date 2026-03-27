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
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoModelInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoSearchListElement;
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
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListResponseDto;
import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
import com.hitachi.droneroute.prm.service.PriceInfoSearchListService;
import com.hitachi.droneroute.prm.service.impl.PriceInfoServiceImpl;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

/** AircraftInfoServiceImplクラスの単体テスト(リポジトリをモック化せずのテスト用) */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({AircraftInfoServiceImpl.class, PriceInfoServiceImpl.class})
class AircraftInfoServiceImplWithRepositoryTest {

  @SpyBean private AircraftInfoRepository aircraftInfoRepository;

  @SpyBean private FileInfoRepository fileInfoRepository;

  @SpyBean private PayloadInfoRepository payloadInfoRepository;

  @Autowired private AircraftInfoServiceImpl aircraftInfoServiceImpl;

  @SpyBean private SystemSettings systemSettings;

  @MockBean private PriceInfoServiceImpl priceInfoServiceImpl;

  @MockBean private PriceInfoSearchListService priceInfoSearchListService;

  @SpyBean private PriceInfoRepository priceInfoRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料なしの機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報は空配列<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象なし_補足資料空配列() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_empList();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getModelNumber(), dto.getModelNumber());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料なしの機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報はnull<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象なし_補足資料項目null() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosokuNull();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getModelNumber(), dto.getModelNumber());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料なしの機体情報登録の正常系テスト<br>
   * 条件: 補足情報ファイルのリスト内の要素がNull<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象なし_補足情報ファイルのリスト内の要素がNULL() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().set(0, null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報の処理種別コード値外（4）<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象なし_処理種別コード値外() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(4);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報の処理種別nullの機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報の処理種別null<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象なし_処理種別null() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報1件<br>
   * 結果: 機体情報と補足資料情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象あり_1件登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    assertNotNull(capFileEnt.getFileId());
    assertNotEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), 1);
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    byte[] byteData = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt.getFileData(), byteData);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), null);
    assertNotNull(capFileEnt.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報1件、補足資料IDが設定されている<br>
   * 結果: 機体情報と補足資料情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象あり_1件登録_補足資料IDが設定されている() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    assertNotNull(capFileEnt.getFileId());
    assertNotEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), 1);
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    byte[] byteData = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt.getFileData(), byteData);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), null);
    assertNotNull(capFileEnt.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報3件<br>
   * 結果: 機体情報と補足資料情報3件が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象あり_3件登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku3();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(3)).save(entFileCaptor.capture());
    List<FileInfoEntity> capFileEntList = entFileCaptor.getAllValues();
    FileInfoEntity capFileEnt1 = capFileEntList.get(0);
    FileInfoEntity capFileEnt2 = capFileEntList.get(1);
    FileInfoEntity capFileEnt3 = capFileEntList.get(2);
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    AircraftInfoFileInfoListElementReq reqFile2 = dto.getFileInfos().get(1);
    AircraftInfoFileInfoListElementReq reqFile3 = dto.getFileInfos().get(2);

    assertNotNull(capFileEnt1.getFileId());
    assertNotEquals(capFileEnt1.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt1.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt1.getFileNumber(), 1);
    assertEquals(capFileEnt1.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt1.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    byte[] byteData1 = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt1.getFileData(), byteData1);
    assertEquals(capFileEnt1.getFileFormat(), "text/plain");
    assertEquals(capFileEnt1.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt1.getUpdateUserId(), null);
    assertNotNull(capFileEnt1.getCreateTime());
    assertNotNull(capFileEnt1.getUpdateTime());
    assertEquals(capFileEnt1.getDeleteFlag(), false);

    assertNotNull(capFileEnt2.getFileId());
    assertNotEquals(capFileEnt2.getFileId().toString(), reqFile2.getFileId());
    assertEquals(capFileEnt2.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt2.getFileNumber(), 2);
    assertEquals(capFileEnt2.getFileLogicalName(), reqFile2.getFileLogicalName());
    assertEquals(capFileEnt2.getFilePhysicalName(), reqFile2.getFilePhysicalName());
    byte[] byteData2 = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt2.getFileData(), byteData2);
    assertEquals(capFileEnt2.getFileFormat(), "text/plain");
    assertEquals(capFileEnt2.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt2.getUpdateUserId(), null);
    assertNotNull(capFileEnt2.getCreateTime());
    assertNotNull(capFileEnt2.getUpdateTime());
    assertEquals(capFileEnt2.getDeleteFlag(), false);

    assertNotNull(capFileEnt3.getFileId());
    assertNotEquals(capFileEnt3.getFileId().toString(), reqFile3.getFileId());
    assertEquals(capFileEnt3.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt3.getFileNumber(), 3);
    assertEquals(capFileEnt3.getFileLogicalName(), reqFile3.getFileLogicalName());
    assertEquals(capFileEnt3.getFilePhysicalName(), reqFile3.getFilePhysicalName());
    byte[] byteData3 = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt3.getFileData(), byteData3);
    assertEquals(capFileEnt3.getFileFormat(), "text/plain");
    assertEquals(capFileEnt3.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt3.getUpdateUserId(), null);
    assertNotNull(capFileEnt3.getCreateTime());
    assertNotNull(capFileEnt3.getUpdateTime());
    assertEquals(capFileEnt3.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の異常系テスト<br>
   * 条件: 更新対象の補足資料情報が存在しない<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_補足資料情報の更新対象のデータなし() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(2);
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の異常系テスト<br>
   * 条件: 削除対象の補足資料情報が存在しない<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_補足資料情報の削除対象のデータなし() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(3);
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料なしの機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報は空配列、型式番号なし<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象なし_補足資料空配列_型式番号なし() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_empList();
    dto.setModelNumber(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getModelNumber(), null);
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料なしの機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報はnull、型式番号空文字<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象なし_補足資料項目null_型式番号空文字() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosokuNull();
    dto.setModelNumber("");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getModelNumber(), "");
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料なしの機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報はnull、型式番号値あり<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象なし_補足資料項目null_型式番号値あり() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosokuNull();
    dto.setModelNumber("U87654321");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getModelNumber(), "U87654321");
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の正常系テスト<br>
   * 条件: 補足資料情報100件<br>
   * 結果: 機体情報と補足資料情報100件が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料情報の処理対象あり_補足資料100件() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.setAircraftId(null);
    List<AircraftInfoFileInfoListElementReq> fileInfos = dto.getFileInfos();
    for (int i = 1; i < 100; i++) {
      AircraftInfoFileInfoListElementReq fileInfo = new AircraftInfoFileInfoListElementReq();
      fileInfo.setFileId(UUID.randomUUID().toString());
      fileInfo.setProcessingType(1);
      fileInfo.setFileLogicalName("補足資料論理名補足資料論理名");
      fileInfo.setFilePhysicalName("1hosoku.txt");
      fileInfo.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
      byte[] file1Byetes = {
        49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27,
        -96, -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
      };
      fileInfo.setFileBinary(file1Byetes);
      fileInfos.add(fileInfo);
    }
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getModelNumber(), dto.getModelNumber());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(100)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料情報を含む機体情報登録の異常系テスト<br>
   * 条件: 補足資料情報101件<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_異常終了すること_補足資料情報の処理対象なし_補足資料101件登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    List<AircraftInfoFileInfoListElementReq> fileInfos = dto.getFileInfos();
    for (int i = 1; i <= 100; i++) {
      AircraftInfoFileInfoListElementReq fileInfo = new AircraftInfoFileInfoListElementReq();
      fileInfo.setFileId(UUID.randomUUID().toString());
      fileInfo.setProcessingType(1);
      fileInfo.setFileLogicalName("補足資料論理名補足資料論理名");
      fileInfo.setFilePhysicalName("1hosoku.txt");
      fileInfo.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
      byte[] file1Byetes = {
        49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27,
        -96, -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
      };
      fileInfo.setFileBinary(file1Byetes);
      fileInfos.add(fileInfo);
    }
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));

    assertEquals("補足資料情報の数が上限数(100)を超えています。", exception.getMessage());

    // 結果確認
    verify(aircraftInfoRepository, times(0)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロードなしの機体情報登録の正常系テスト<br>
   * 条件: ペイロード情報は空配列<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_ペイロード情報の処理対象なし_ペイロード情空配列() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadEmpList();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロードなしの機体情報登録の正常系テスト<br>
   * 条件: ペイロード情報はnull<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_ペイロード情報の処理対象なし_ペイロード情報項目null() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadNull();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロードなしの機体情報登録の正常系テスト<br>
   * 条件: ペイロード情報のリスト内の要素がNull<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_ペイロード情報の処理対象なし_ペイロード情報のリスト内の要素がNULL() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().set(0, null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の正常系テスト<br>
   * 条件: ペイロード情報の処理種別コード値外（4）<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_ペイロード情報の処理対象なし_処理種別コード値外() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(4);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報の処理種別nullの機体情報登録の正常系テスト<br>
   * 条件: ペイロード情報の処理種別null<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_ペイロード情報の処理対象なし_処理種別null() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertNotNull(capAirEnt.getAircraftId().toString());
      assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), null);
      assertNotNull(capAirEnt.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の正常系テスト<br>
   * 条件: ペイロード情報1件<br>
   * 結果: 機体情報とペイロード情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_ペイロード情報の処理対象あり_1件登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capFileEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertNotNull(capFileEnt.getPayloadId());
    assertNotEquals(capFileEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getPayloadNumber(), 1);
    assertEquals(capFileEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capFileEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage = {
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
    assertArrayEquals(capFileEnt.getImageData(), byteImage);
    assertEquals(capFileEnt.getImageFormat(), "png");
    assertEquals(capFileEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    byte[] byteFile = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt.getFileData(), byteFile);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), null);
    assertNotNull(capFileEnt.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の正常系テスト<br>
   * 条件: ペイロード情報1件、ペイロード情報IDが設定されている<br>
   * 結果: 機体情報とペイロード情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_ペイロード情報の処理対象あり_1件登録_ペイロード情報IDが設定されている() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capFileEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertNotNull(capFileEnt.getPayloadId());
    assertNotEquals(capFileEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getPayloadNumber(), 1);
    assertEquals(capFileEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capFileEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage = {
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
    assertArrayEquals(capFileEnt.getImageData(), byteImage);
    assertEquals(capFileEnt.getImageFormat(), "png");
    assertEquals(capFileEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    byte[] byteFile = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt.getFileData(), byteFile);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), null);
    assertNotNull(capFileEnt.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の正常系テスト<br>
   * 条件: ペイロード情報3件<br>
   * 結果: 機体情報とペイロード情報3件が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_ペイロード情報の処理対象あり_3件登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload3();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(3)).save(entPayloadCaptor.capture());
    List<PayloadInfoEntity> capPayloadEntList = entPayloadCaptor.getAllValues();
    PayloadInfoEntity capFileEnt1 = capPayloadEntList.get(0);
    PayloadInfoEntity capFileEnt2 = capPayloadEntList.get(1);
    PayloadInfoEntity capFileEnt3 = capPayloadEntList.get(2);
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    AircraftInfoPayloadInfoListElementReq reqPayload2 = dto.getPayloadInfos().get(1);
    AircraftInfoPayloadInfoListElementReq reqPayload3 = dto.getPayloadInfos().get(2);

    assertNotNull(capFileEnt1.getPayloadId());
    assertNotEquals(capFileEnt1.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capFileEnt1.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt1.getPayloadNumber(), 1);
    assertEquals(capFileEnt1.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capFileEnt1.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage1 = {
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
    assertArrayEquals(capFileEnt1.getImageData(), byteImage1);
    assertEquals(capFileEnt1.getImageFormat(), "png");
    assertEquals(capFileEnt1.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    byte[] byteFile1 = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt1.getFileData(), byteFile1);
    assertEquals(capFileEnt1.getFileFormat(), "text/plain");
    assertEquals(capFileEnt1.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt1.getUpdateUserId(), null);
    assertNotNull(capFileEnt1.getCreateTime());
    assertNotNull(capFileEnt1.getUpdateTime());
    assertEquals(capFileEnt1.getDeleteFlag(), false);

    assertNotNull(capFileEnt2.getPayloadId());
    assertNotEquals(capFileEnt2.getPayloadId().toString(), reqPayload2.getPayloadId());
    assertEquals(capFileEnt2.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt2.getPayloadNumber(), 2);
    assertEquals(capFileEnt2.getPayloadName(), reqPayload2.getPayloadName());
    assertEquals(capFileEnt2.getPayloadDetailText(), reqPayload2.getPayloadDetailText());
    byte[] byteImage2 = {
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
    assertArrayEquals(capFileEnt2.getImageData(), byteImage2);
    assertEquals(capFileEnt2.getImageFormat(), "png");
    assertEquals(capFileEnt2.getFilePhysicalName(), reqPayload2.getFilePhysicalName());
    byte[] byteFile2 = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt2.getFileData(), byteFile2);
    assertEquals(capFileEnt2.getFileFormat(), "text/plain");
    assertEquals(capFileEnt2.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt2.getUpdateUserId(), null);
    assertNotNull(capFileEnt2.getCreateTime());
    assertNotNull(capFileEnt2.getUpdateTime());
    assertEquals(capFileEnt2.getDeleteFlag(), false);

    assertNotNull(capFileEnt3.getPayloadId());
    assertNotEquals(capFileEnt3.getPayloadId().toString(), reqPayload3.getPayloadId());
    assertEquals(capFileEnt3.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt3.getPayloadNumber(), 3);
    assertEquals(capFileEnt3.getPayloadName(), reqPayload3.getPayloadName());
    assertEquals(capFileEnt3.getPayloadDetailText(), reqPayload3.getPayloadDetailText());
    byte[] byteImage3 = {
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
    assertArrayEquals(capFileEnt3.getImageData(), byteImage3);
    assertEquals(capFileEnt3.getImageFormat(), "png");
    assertEquals(capFileEnt3.getFilePhysicalName(), reqPayload3.getFilePhysicalName());
    byte[] byteFile3 = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt3.getFileData(), byteFile3);
    assertEquals(capFileEnt3.getFileFormat(), "text/plain");
    assertEquals(capFileEnt3.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt3.getUpdateUserId(), null);
    assertNotNull(capFileEnt3.getCreateTime());
    assertNotNull(capFileEnt3.getUpdateTime());
    assertEquals(capFileEnt3.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の異常系テスト<br>
   * 条件: 更新対象のペイロード情報が存在しない<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_ペイロード情報の更新対象のデータなし() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(2);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");

    // テスト実施
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の異常系テスト<br>
   * 条件: 削除対象のペイロード情報が存在しない<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_ペイロード情報の削除対象のデータなし() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(3);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");

    // テスト実施
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  // postData_payload #11～14はAircraftInfoServiceImplTestの方で実施

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の正常系テスト<br>
   * 条件: ペイロード情報20件<br>
   * 結果: 機体情報とペイロード情報20件が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_20件登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_reg_payload20();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(20)).save(entPayloadCaptor.capture());

    List<PayloadInfoEntity> savedList = entPayloadCaptor.getAllValues();
    assertEquals(20, savedList.size());

    for (int i = 0; i < savedList.size(); i++) {
      PayloadInfoEntity cap = savedList.get(i);
      AircraftInfoPayloadInfoListElementReq expected = dto.getPayloadInfos().get(i);

      assertNotNull(cap.getPayloadId());
      assertEquals(i + 1, cap.getPayloadNumber());
      assertEquals(expected.getPayloadName(), cap.getPayloadName());

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機種名を含む機体情報登録の正常系テスト<br>
   * 条件: 機種名あり<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_機種名登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getModelNumber(), dto.getModelNumber());
    assertEquals(capAirEnt.getModelName(), dto.getModelName());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), null);
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機種名なしの機体情報登録の正常系テスト<br>
   * 条件: 機種名null<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_機種名null() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
    dto.setModelName(null);

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getModelNumber(), dto.getModelNumber());
    assertEquals(capAirEnt.getModelName(), dto.getModelName());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), null);
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 機種名なしの機体情報登録の正常系テスト<br>
   * 条件: 機種名空文字<br>
   * 結果: 機体情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_機種名空文字() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
    dto.setModelName("");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getModelNumber(), dto.getModelNumber());
    assertEquals(capAirEnt.getModelName(), dto.getModelName());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), null);
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料・ペイロード情報を含む機体情報登録の正常系テスト<br>
   * 条件: 補足資料1件、ペイロード情報1件<br>
   * 結果: 機体情報・補足資料・ペイロード情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料1件_ペイロード情報1件登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1_payload1_price1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getPublicFlag(), dto.getPublicFlag());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    assertNotNull(capFileEnt.getFileId());
    assertNotEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), 1);
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    byte[] byteData = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt.getFileData(), byteData);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), null);
    assertNotNull(capFileEnt.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertNotNull(capPayloadEnt.getPayloadId());
    assertNotEquals(capPayloadEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), 1);
    assertEquals(capPayloadEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage = {
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
    assertArrayEquals(capPayloadEnt.getImageData(), byteImage);
    assertEquals(capPayloadEnt.getImageFormat(), "png");
    assertEquals(capPayloadEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    assertArrayEquals(capPayloadEnt.getFileData(), byteData);
    assertEquals(capPayloadEnt.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), null);
    assertNotNull(capPayloadEnt.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: 補足資料・ペイロード情報を含む機体情報登録の正常系テスト<br>
   * 条件: 補足資料1件、ペイロード情報1件、ファイルデータ空<br>
   * 結果: 機体情報・補足資料・ペイロード情報が登録される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void postData_正常終了すること_補足資料1件_ペイロード情報1件_ファイルデータ空() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    byte[] byteData = new byte[0];
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1_payload1_price1();
    dto.getFileInfos().get(0).setFileBinary(byteData);
    dto.getPayloadInfos().get(0).setFileBinary(byteData);
    dto.getPayloadInfos().get(0).setImageBinary(byteData);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.postData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertNotNull(capAirEnt.getAircraftId().toString());
    assertNotEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getPublicFlag(), dto.getPublicFlag());
    assertEquals(capAirEnt.getUpdateUserId(), null);
    assertNotNull(capAirEnt.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    assertNotNull(capFileEnt.getFileId());
    assertNotEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), 1);
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    assertArrayEquals(capFileEnt.getFileData(), byteData);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), null);
    assertNotNull(capFileEnt.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertNotNull(capPayloadEnt.getPayloadId());
    assertNotEquals(capPayloadEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), 1);
    assertEquals(capPayloadEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    assertArrayEquals(capPayloadEnt.getImageData(), null);
    assertEquals(capPayloadEnt.getImageFormat(), "png");
    assertEquals(capPayloadEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    assertArrayEquals(capPayloadEnt.getFileData(), byteData);
    assertEquals(capPayloadEnt.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), null);
    assertNotNull(capPayloadEnt.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: postData<br>
   * 試験名: ペイロード情報を含む機体情報登録の異常系テスト<br>
   * 条件: ペイロード情報21件<br>
   * 結果: ValidationErrorExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void postData_異常終了すること_21件登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_reg_payload20();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // 追加する要素を作成
    AircraftInfoPayloadInfoListElementReq extra = new AircraftInfoPayloadInfoListElementReq();
    extra.setProcessingType(1);
    extra.setPayloadName("ペイロード名21");
    dto.getPayloadInfos().add(extra);

    // テスト実施
    Exception ex =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.postData(dto, userDto));
    assertEquals("ペイロード情報の数が上限数(20)を超えています。", ex.getMessage());

    // 結果確認
    verify(aircraftInfoRepository, times(0)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料1件とペイロード1件と料金情報1件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料1件、ペイロード情報1件、料金情報1件<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料1件_ペイロード情報1件_料金情報1件更新() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_n1();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity();
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);
    PriceInfoEntity priceEnt1 = createPriceInfoEntity();
    priceInfoRepository.save(priceEnt1);
    clearInvocations(priceInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1_payload1_price1();
    dto.getFileInfos().get(0).setProcessingType(2);
    dto.getFileInfos().get(0).setFileId(fileEnt1.getFileId().toString());
    dto.getPayloadInfos().get(0).setProcessingType(2);
    dto.getPayloadInfos().get(0).setPayloadId(payloadEnt1.getPayloadId().toString());
    dto.getPriceInfos().get(0).setProcessingType(2);
    dto.getPriceInfos().get(0).setPriceId(priceEnt1.getPriceId().toString());

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getPublicFlag(), existingEntity1.getPublicFlag());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    byte[] byteFile = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);

    assertEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), fileEnt1.getFileNumber());
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    assertArrayEquals(capFileEnt.getFileData(), byteFile);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), fileEnt1.getUpdateUserId());
    assertEquals(capFileEnt.getCreateTime(), fileEnt1.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertNotEquals(capFileEnt.getUpdateTime(), fileEnt1.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertEquals(capPayloadEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), payloadEnt1.getPayloadNumber());
    assertEquals(capPayloadEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage = {
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
    assertArrayEquals(capPayloadEnt.getImageData(), byteImage);
    assertEquals(capPayloadEnt.getImageFormat(), "png");
    assertEquals(capPayloadEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    assertArrayEquals(capPayloadEnt.getFileData(), byteFile);
    assertEquals(capPayloadEnt.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), payloadEnt1.getUpdateUserId());
    assertEquals(capPayloadEnt.getCreateTime(), payloadEnt1.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertNotEquals(capPayloadEnt.getUpdateTime(), payloadEnt1.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), false);

    ArgumentCaptor<List> capPriceInfoList = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(capPriceInfoList.capture());

    List<PriceInfoRequestDto> listCap = capPriceInfoList.getValue();
    PriceInfoRequestDto capPriceEnt = listCap.get(0);
    assertEquals(capPriceEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPriceEnt.getProcessingType(), dto.getPriceInfos().get(0).getProcessingType());

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料1件とペイロード1件と料金情報1件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料1件、ペイロード情報1件、料金情報1件、ファイルデータ空<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料1件_ペイロード情報1件_料金情報1件更新_ファイルデータ空() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_n1();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity();
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);
    PriceInfoEntity priceEnt1 = createPriceInfoEntity();
    priceInfoRepository.save(priceEnt1);
    clearInvocations(priceInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    byte[] byteData = new byte[0];
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1_payload1_price1();
    dto.getFileInfos().get(0).setProcessingType(2);
    dto.getFileInfos().get(0).setFileId(fileEnt1.getFileId().toString());
    dto.getPayloadInfos().get(0).setProcessingType(2);
    dto.getPayloadInfos().get(0).setPayloadId(payloadEnt1.getPayloadId().toString());
    dto.getPayloadInfos().get(0).setFileBinary(byteData);
    dto.getPayloadInfos().get(0).setImageBinary(byteData);
    dto.getPriceInfos().get(0).setProcessingType(2);
    dto.getPriceInfos().get(0).setPriceId(priceEnt1.getPriceId().toString());

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getPublicFlag(), existingEntity1.getPublicFlag());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);

    assertEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), fileEnt1.getFileNumber());
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertArrayEquals(capFileEnt.getFileData(), reqFile1.getFileBinary());
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), fileEnt1.getUpdateUserId());
    assertEquals(capFileEnt.getCreateTime(), fileEnt1.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertNotEquals(capFileEnt.getUpdateTime(), fileEnt1.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertEquals(capPayloadEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), payloadEnt1.getPayloadNumber());
    assertEquals(capPayloadEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    assertArrayEquals(capPayloadEnt.getImageData(), reqPayload1.getImageBinary());
    assertEquals(capPayloadEnt.getImageFormat(), "png");
    assertEquals(capPayloadEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    assertArrayEquals(capPayloadEnt.getFileData(), payloadEnt1.getFileData());
    assertEquals(capPayloadEnt.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), payloadEnt1.getUpdateUserId());
    assertEquals(capPayloadEnt.getCreateTime(), payloadEnt1.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertNotEquals(capPayloadEnt.getUpdateTime(), payloadEnt1.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), false);

    ArgumentCaptor<List> capPriceInfoList = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(capPriceInfoList.capture());

    List<PriceInfoRequestDto> listCap = capPriceInfoList.getValue();
    PriceInfoRequestDto capPriceEnt = listCap.get(0);
    assertEquals(capPriceEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPriceEnt.getProcessingType(), dto.getPriceInfos().get(0).getProcessingType());

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料2件とペイロード2件と料金情報1件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料2件、ペイロード情報2件、料金情報1件、処理種別nullあり<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料2件_ペイロード情報2件_料金情報1件更新_処理種別nullあり() {
    // リポジトリにデータ準備
    UUID id = UUID.randomUUID();
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_n1();
    fileInfoRepository.save(fileEnt1);
    FileInfoEntity fileEnt2 = createFileInfoEntity_n1();
    fileEnt2.setFileId(id);
    fileInfoRepository.save(fileEnt2);
    clearInvocations(fileInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity();
    payloadInfoRepository.save(payloadEnt1);
    PayloadInfoEntity payloadEnt2 = createPayloadInfoEntity();
    payloadEnt2.setPayloadId(id);
    clearInvocations(payloadInfoRepository);
    PriceInfoEntity priceEnt1 = createPriceInfoEntity();
    priceInfoRepository.save(priceEnt1);
    clearInvocations(priceInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    byte[] byteData = new byte[0];
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1_payload1_price1();
    dto.getFileInfos().get(0).setProcessingType(null);
    dto.getFileInfos().get(0).setFileId(fileEnt1.getFileId().toString());
    dto.getFileInfos().get(0).setFileBinary(byteData);
    dto.getPayloadInfos().get(0).setProcessingType(null);
    dto.getPayloadInfos().get(0).setFileBinary(byteData);
    dto.getPayloadInfos().get(0).setImageBinary(byteData);
    dto.getPayloadInfos().get(0).setPayloadId(payloadEnt1.getPayloadId().toString());
    dto.getPriceInfos().get(0).setProcessingType(2);
    dto.getPriceInfos().get(0).setPriceId(priceEnt1.getPriceId().toString());

    AircraftInfoPayloadInfoListElementReq payload1 = new AircraftInfoPayloadInfoListElementReq();
    payload1.setProcessingType(1);
    payload1.setPayloadId(id.toString());
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
    dto.getPayloadInfos().add(payload1);

    AircraftInfoFileInfoListElementReq file1 = new AircraftInfoFileInfoListElementReq();
    file1.setProcessingType(1);
    file1.setFileId(id.toString());
    file1.setFileLogicalName("1補足資料論理名補足資料論理名");
    file1.setFilePhysicalName("1hosoku.txt");
    file1.setFileData("data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65");
    file1.setFileBinary(file1Byetes);

    dto.getFileInfos().add(file1);

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getPublicFlag(), existingEntity1.getPublicFlag());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    ArgumentCaptor<List> capPriceInfoList = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(capPriceInfoList.capture());

    List<PriceInfoRequestDto> listCap = capPriceInfoList.getValue();
    PriceInfoRequestDto capPriceEnt = listCap.get(0);
    assertEquals(capPriceEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPriceEnt.getProcessingType(), dto.getPriceInfos().get(0).getProcessingType());

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料1件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料1件、補足資料系null<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料1件更新_補足資料系null() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_n1();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1_payload1_price1();
    dto.getFileInfos().get(0).setProcessingType(2);
    dto.getFileInfos().get(0).setFileId(fileEnt1.getFileId().toString());
    dto.getFileInfos().get(0).setFileLogicalName(null);
    dto.getFileInfos().get(0).setFilePhysicalName(null);
    dto.getFileInfos().get(0).setFileData(null);
    dto.getFileInfos().get(0).setFileBinary(null);

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getPublicFlag(), existingEntity1.getPublicFlag());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);

    assertEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), fileEnt1.getFileNumber());
    assertEquals(capFileEnt.getFileLogicalName(), fileEnt1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), fileEnt1.getFilePhysicalName());
    assertArrayEquals(capFileEnt.getFileData(), fileEnt1.getFileData());
    assertEquals(capFileEnt.getFileFormat(), fileEnt1.getFileFormat());
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), fileEnt1.getUpdateUserId());
    assertEquals(capFileEnt.getCreateTime(), fileEnt1.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertNotEquals(capFileEnt.getUpdateTime(), fileEnt1.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料1件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料1件、補足資料系空文字<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料1件更新_補足資料系空文字l() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_n1();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1_payload1_price1();
    dto.getFileInfos().get(0).setProcessingType(2);
    dto.getFileInfos().get(0).setFileId(fileEnt1.getFileId().toString());
    dto.getFileInfos().get(0).setFileLogicalName("");
    dto.getFileInfos().get(0).setFilePhysicalName("");
    dto.getFileInfos().get(0).setFileData("");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getPublicFlag(), existingEntity1.getPublicFlag());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);

    assertEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), fileEnt1.getFileNumber());
    assertEquals(capFileEnt.getFileLogicalName(), fileEnt1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), fileEnt1.getFilePhysicalName());
    assertArrayEquals(capFileEnt.getFileData(), fileEnt1.getFileData());
    assertEquals(capFileEnt.getFileFormat(), fileEnt1.getFileFormat());
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), fileEnt1.getUpdateUserId());
    assertEquals(capFileEnt.getCreateTime(), fileEnt1.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertNotEquals(capFileEnt.getUpdateTime(), fileEnt1.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料1件とペイロード1件を1件削除を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料1件、ペイロード情報1件削除。<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料1件_ペイロード情報1件削除() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_n1();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity();
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);
    PriceInfoEntity priceEnt1 = createPriceInfoEntity();
    priceInfoRepository.save(priceEnt1);
    clearInvocations(priceInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1_payload1_price1();
    dto.getFileInfos().get(0).setProcessingType(3);
    dto.getFileInfos().get(0).setFileId(fileEnt1.getFileId().toString());
    dto.getPayloadInfos().get(0).setProcessingType(3);
    dto.getPayloadInfos().get(0).setPayloadId(payloadEnt1.getPayloadId().toString());
    dto.getPriceInfos().get(0).setProcessingType(3);
    dto.getPriceInfos().get(0).setPriceId(priceEnt1.getPriceId().toString());

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capAirEnt.getPublicFlag(), existingEntity1.getPublicFlag());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);

    assertEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), fileEnt1.getFileNumber());
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), fileEnt1.getUpdateUserId());
    assertEquals(capFileEnt.getCreateTime(), fileEnt1.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertNotEquals(capFileEnt.getUpdateTime(), fileEnt1.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), true);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    assertEquals(capPayloadEnt.getPayloadId().toString(), payloadEnt1.getPayloadId().toString());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), payloadEnt1.getPayloadNumber());
    assertEquals(capPayloadEnt.getPayloadName(), payloadEnt1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), payloadEnt1.getPayloadDetailText());

    assertEquals(capPayloadEnt.getImageFormat(), "png");
    assertEquals(capPayloadEnt.getFilePhysicalName(), payloadEnt1.getFilePhysicalName());

    assertEquals(capPayloadEnt.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), payloadEnt1.getUpdateUserId());
    assertEquals(capPayloadEnt.getCreateTime(), payloadEnt1.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertNotEquals(capPayloadEnt.getUpdateTime(), payloadEnt1.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), true);

    ArgumentCaptor<List> capPriceInfoList = ArgumentCaptor.forClass(List.class);
    verify(priceInfoServiceImpl, times(1)).process(capPriceInfoList.capture());

    List<PriceInfoRequestDto> listCap = capPriceInfoList.getValue();
    PriceInfoRequestDto capPriceEnt = listCap.get(0);
    assertEquals(capPriceEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPriceEnt.getProcessingType(), dto.getPriceInfos().get(0).getProcessingType());

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料なしの機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報は空配列<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象なし_補足資料空配列() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_empList();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料なしの機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報はnull<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象なし_補足資料項目null() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosokuNull();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料なしの機体情報更新の正常系テスト<br>
   * 条件: 補足情報ファイルのリスト内の要素がNull<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象なし_補足情報ファイルのリスト内の要素がNULL() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().set(0, null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報の処理種別コード値外（4）<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象なし_処理種別コード値外() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(4);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報の処理種別nullの機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報の処理種別null<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象なし_処理種別null() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報1件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報1件、既存補足資料情報なし<br>
   * 結果: 機体情報と補足資料情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象あり_1件登録_既存補足資料なし() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(1);
    dto.getFileInfos().get(0).setFileId(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    assertNotNull(capFileEnt.getFileId());
    assertNotEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), 1);
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    byte[] byteData = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt.getFileData(), byteData);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), null);
    assertNotNull(capFileEnt.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報1件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報1件、既存補足資料情報あり<br>
   * 結果: 機体情報と補足資料情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象あり_1件登録_既存補足資料あり() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileEnt1.setFileId(UUID.randomUUID());
    fileEnt1.setFileNumber(2);
    FileInfoEntity fileEnt2 = createFileInfoEntity_templete();
    fileEnt2.setFileId(UUID.randomUUID());
    fileEnt2.setFileNumber(3);
    fileEnt2.setDeleteFlag(true);
    fileInfoRepository.save(fileEnt1);
    fileInfoRepository.save(fileEnt2);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(1);
    dto.getFileInfos().get(0).setFileId(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    assertNotNull(capFileEnt.getFileId());
    assertNotEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), 3);
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    byte[] byteData = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt.getFileData(), byteData);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), null);
    assertNotNull(capFileEnt.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報1件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報1件、リクエストにファイルIDが設定されている<br>
   * 結果: 機体情報と補足資料情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象あり_1件登録_リクエストにファイルIDあり() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(1);
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    assertNotNull(capFileEnt.getFileId());
    assertNotEquals(capFileEnt.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), 1);
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    byte[] byteData = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt.getFileData(), byteData);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), null);
    assertNotNull(capFileEnt.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報1件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報1件を更新<br>
   * 結果: 機体情報と補足資料情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象あり_1件更新() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = new FileInfoEntity();
    fileEnt1.setFileId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    fileEnt1.setAircraftId(existingEntity1.getAircraftId());
    fileEnt1.setFileNumber(1);
    fileEnt1.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    fileEnt1.setUpdateUserId("user01");
    fileEnt1.setCreateTime(new Timestamp(System.currentTimeMillis()));
    fileEnt1.setDeleteFlag(false);
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(2);
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    assertEquals(capFileEnt.getFileId(), fileEnt1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), fileEnt1.getFileNumber());
    assertEquals(capFileEnt.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    byte[] byteData = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt.getFileData(), byteData);
    assertEquals(capFileEnt.getFileFormat(), "text/plain");
    assertEquals(capFileEnt.getOperatorId(), fileEnt1.getOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), fileEnt1.getUpdateUserId());
    assertEquals(capFileEnt.getCreateTime(), fileEnt1.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertNotEquals(capFileEnt.getUpdateTime(), fileEnt1.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報1件削除を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報1件を削除<br>
   * 結果: 機体情報と補足資料情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象あり_1件削除() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileEnt1.setFileId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(3);
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(1)).save(entFileCaptor.capture());

    FileInfoEntity capFileEnt = entFileCaptor.getValue();
    assertEquals(capFileEnt.getFileId(), fileEnt1.getFileId());
    assertEquals(capFileEnt.getAircraftId(), fileEnt1.getAircraftId());
    assertEquals(capFileEnt.getFileNumber(), fileEnt1.getFileNumber());
    assertEquals(capFileEnt.getFileLogicalName(), fileEnt1.getFileLogicalName());
    assertEquals(capFileEnt.getFilePhysicalName(), fileEnt1.getFilePhysicalName());
    assertArrayEquals(capFileEnt.getFileData(), fileEnt1.getFileData());
    assertEquals(capFileEnt.getFileFormat(), fileEnt1.getFileFormat());
    assertEquals(capFileEnt.getOperatorId(), fileEnt1.getOperatorId());
    assertEquals(capFileEnt.getUpdateUserId(), fileEnt1.getUpdateUserId());
    assertEquals(capFileEnt.getCreateTime(), fileEnt1.getCreateTime());
    assertNotNull(capFileEnt.getUpdateTime());
    assertNotEquals(capFileEnt.getUpdateTime(), fileEnt1.getUpdateTime());
    assertEquals(capFileEnt.getDeleteFlag(), true);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報3件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報3件。<br>
   * 結果: 機体情報と補足資料情報3件が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_補足資料情報の処理対象あり_3件登録() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku3();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(補足資料)
    ArgumentCaptor<FileInfoEntity> entFileCaptor = ArgumentCaptor.forClass(FileInfoEntity.class);
    verify(fileInfoRepository, times(3)).save(entFileCaptor.capture());

    List<FileInfoEntity> capFileEntList = entFileCaptor.getAllValues();

    FileInfoEntity capFileEnt1 = capFileEntList.get(0);
    AircraftInfoFileInfoListElementReq reqFile1 = dto.getFileInfos().get(0);
    assertNotNull(capFileEnt1.getFileId());
    assertNotEquals(capFileEnt1.getFileId().toString(), reqFile1.getFileId());
    assertEquals(capFileEnt1.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt1.getFileNumber(), 1);
    assertEquals(capFileEnt1.getFileLogicalName(), reqFile1.getFileLogicalName());
    assertEquals(capFileEnt1.getFilePhysicalName(), reqFile1.getFilePhysicalName());
    byte[] byteData1 = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt1.getFileData(), byteData1);
    assertEquals(capFileEnt1.getFileFormat(), "text/plain");
    assertEquals(capFileEnt1.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt1.getUpdateUserId(), null);
    assertNotNull(capFileEnt1.getCreateTime());
    assertNotNull(capFileEnt1.getUpdateTime());
    assertEquals(capFileEnt1.getDeleteFlag(), false);

    FileInfoEntity capFileEnt2 = capFileEntList.get(1);
    AircraftInfoFileInfoListElementReq reqFile2 = dto.getFileInfos().get(1);
    assertNotNull(capFileEnt2.getFileId());
    assertNotEquals(capFileEnt2.getFileId().toString(), reqFile2.getFileId());
    assertEquals(capFileEnt2.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt2.getFileNumber(), 2);
    assertEquals(capFileEnt2.getFileLogicalName(), reqFile2.getFileLogicalName());
    assertEquals(capFileEnt2.getFilePhysicalName(), reqFile2.getFilePhysicalName());
    byte[] byteData2 = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt2.getFileData(), byteData2);
    assertEquals(capFileEnt2.getFileFormat(), "text/plain");
    assertEquals(capFileEnt2.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt2.getUpdateUserId(), null);
    assertNotNull(capFileEnt2.getCreateTime());
    assertNotNull(capFileEnt2.getUpdateTime());
    assertEquals(capFileEnt2.getDeleteFlag(), false);

    FileInfoEntity capFileEnt3 = capFileEntList.get(2);
    AircraftInfoFileInfoListElementReq reqFile3 = dto.getFileInfos().get(2);
    assertNotNull(capFileEnt3.getFileId());
    assertNotEquals(capFileEnt3.getFileId().toString(), reqFile3.getFileId());
    assertEquals(capFileEnt3.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capFileEnt3.getFileNumber(), 3);
    assertEquals(capFileEnt3.getFileLogicalName(), reqFile3.getFileLogicalName());
    assertEquals(capFileEnt3.getFilePhysicalName(), reqFile3.getFilePhysicalName());
    byte[] byteData3 = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capFileEnt3.getFileData(), byteData3);
    assertEquals(capFileEnt3.getFileFormat(), "text/plain");
    assertEquals(capFileEnt3.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capFileEnt3.getUpdateUserId(), null);
    assertNotNull(capFileEnt3.getCreateTime());
    assertNotNull(capFileEnt3.getUpdateTime());
    assertEquals(capFileEnt3.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料100件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報なし、補足資料100件<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_登録済み補足資料情報なし_補足資料100件登録() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    List<AircraftInfoFileInfoListElementReq> fileInfos = dto.getFileInfos();
    // 　補足資料ファイル100件作成
    for (int i = 1; i < 100; i++) {
      AircraftInfoFileInfoListElementReq fileInfo = new AircraftInfoFileInfoListElementReq();
      fileInfo.setFileId(UUID.randomUUID().toString());
      fileInfo.setProcessingType(1);
      fileInfo.setFileLogicalName("補足資料論理名補足資料論理名");
      fileInfo.setFilePhysicalName("1hosoku.txt");
      fileInfo.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
      byte[] file1Byetes = {
        49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27,
        -96, -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
      };
      fileInfo.setFileBinary(file1Byetes);
      fileInfos.add(fileInfo);
    }
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(100)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料99件を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報1件、補足資料99件<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_登録済み補足資料情報1件_補足資料99件登録() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    FileInfoEntity fileEntity = createFileInfoEntity_n1();
    fileInfoRepository.save(fileEntity);
    clearInvocations(aircraftInfoRepository);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    List<AircraftInfoFileInfoListElementReq> fileInfos = dto.getFileInfos();
    // 補足資料ファイル99件作成
    for (int i = 1; i < 99; i++) {
      AircraftInfoFileInfoListElementReq fileInfo = new AircraftInfoFileInfoListElementReq();
      fileInfo.setFileId(UUID.randomUUID().toString());
      fileInfo.setProcessingType(1);
      fileInfo.setFileLogicalName("補足資料論理名補足資料論理名");
      fileInfo.setFilePhysicalName("1hosoku.txt");
      fileInfo.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
      byte[] file1Byetes = {
        49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27,
        -96, -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
      };
      fileInfo.setFileBinary(file1Byetes);
      fileInfos.add(fileInfo);
    }
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(99)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料100件を含む機体情報更新の正常系テスト<br>
   * 条件: 論理削除済み補足資料情報1件、補足資料100件<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_論理削除済み補足資料情報1件_補足資料100件登録() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    FileInfoEntity fileEntity = createFileInfoEntity_n1();
    fileEntity.setDeleteFlag(true);
    fileInfoRepository.save(fileEntity);
    clearInvocations(aircraftInfoRepository);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    List<AircraftInfoFileInfoListElementReq> fileInfos = dto.getFileInfos();
    // 補足資料ファイル100件作成
    for (int i = 1; i < 100; i++) {
      AircraftInfoFileInfoListElementReq fileInfo = new AircraftInfoFileInfoListElementReq();
      fileInfo.setFileId(UUID.randomUUID().toString());
      fileInfo.setProcessingType(1);
      fileInfo.setFileLogicalName("補足資料論理名補足資料論理名");
      fileInfo.setFilePhysicalName("1hosoku.txt");
      fileInfo.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
      byte[] file1Byetes = {
        49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27,
        -96, -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
      };
      fileInfo.setFileBinary(file1Byetes);
      fileInfos.add(fileInfo);
    }
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(100)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料1件登録1件削除を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報100件、補足資料1件登録、1件削除<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_登録済み補足資料情報100件_補足資料1件登録1件削除() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    FileInfoEntity fileEntity = createFileInfoEntity_templete();
    fileEntity.setFileId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    fileInfoRepository.save(fileEntity);
    // 補足資料ファイル100件作成
    for (int i = 1; i < 100; i++) {
      FileInfoEntity fileEntityTemp = fileEntity;
      fileEntityTemp.setFileId(UUID.randomUUID());
      fileInfoRepository.save(fileEntity);
    }
    clearInvocations(aircraftInfoRepository);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku3();
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    dto.getFileInfos().get(0).setProcessingType(3);
    dto.getFileInfos().remove(2);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }

    // 結果確認
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(2)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料101件削除を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報101件、補足資料101件削除<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_登録済み補足資料情報101件_補足資料101件削除() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    FileInfoEntity fileEntity = createFileInfoEntity_templete();
    fileInfoRepository.save(fileEntity);
    List<String> fileIdList = new ArrayList<>();
    fileIdList.add(fileEntity.getFileId().toString());
    // 補足資料ファイル101件作成
    for (int i = 1; i < 101; i++) {
      FileInfoEntity fileEntityTemp = fileEntity;
      fileEntityTemp.setFileId(UUID.randomUUID());
      fileIdList.add(fileEntityTemp.getFileId().toString());
      fileInfoRepository.save(fileEntity);
    }
    clearInvocations(aircraftInfoRepository);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_empList();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    List<AircraftInfoFileInfoListElementReq> fileInfos = dto.getFileInfos();
    // 　補足資料ファイル101件作成
    for (int i = 0; i < 101; i++) {
      AircraftInfoFileInfoListElementReq fileInfo = new AircraftInfoFileInfoListElementReq();
      fileInfo.setFileId(fileIdList.get(i));
      fileInfo.setProcessingType(3);
      fileInfos.add(fileInfo);
    }

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }

    // 結果確認
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(101)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料101件更新を含む機体情報更新の正常系テスト<br>
   * 条件: 補足資料情報101件、補足資料101件更新<br>
   * 結果: 機体情報が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_登録済み補足資料情報101件_補足資料101件更新() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    FileInfoEntity fileEntity = createFileInfoEntity_templete();
    fileInfoRepository.save(fileEntity);
    List<String> fileIdList = new ArrayList<>();
    fileIdList.add(fileEntity.getFileId().toString());
    // 補足資料ファイル101件作成
    for (int i = 1; i < 101; i++) {
      FileInfoEntity fileEntityTemp = fileEntity;
      fileEntityTemp.setFileId(UUID.randomUUID());
      fileIdList.add(fileEntityTemp.getFileId().toString());
      fileInfoRepository.save(fileEntity);
    }
    clearInvocations(aircraftInfoRepository);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_empList();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    List<AircraftInfoFileInfoListElementReq> fileInfos = dto.getFileInfos();
    // 　補足資料ファイル101件作成
    for (int i = 0; i < 101; i++) {
      AircraftInfoFileInfoListElementReq fileInfo = new AircraftInfoFileInfoListElementReq();
      fileInfo.setFileId(fileIdList.get(i));
      fileInfo.setProcessingType(2);
      fileInfo.setFileLogicalName("更新後ファイル名" + i);
      fileInfo.setFilePhysicalName("updateFile.txt");
      fileInfo.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
      byte[] file1Byetes = {
        49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27,
        -96, -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
      };
      fileInfo.setFileBinary(file1Byetes);
      fileInfos.add(fileInfo);
    }

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }

    // 結果確認
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(101)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料101件登録を含む機体情報更新の異常系テスト<br>
   * 条件: 補足資料情報なし、補足資料101件<br>
   * 結果: 異常終了し、「補足資料情報の数が上限数(100)を超えています。」例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_異常終了すること_登録済み補足資料情報なし_補足資料101件登録() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    List<AircraftInfoFileInfoListElementReq> fileInfos = dto.getFileInfos();
    // 補足資料ファイル101件作成
    for (int i = 1; i <= 100; i++) {
      AircraftInfoFileInfoListElementReq fileInfo = new AircraftInfoFileInfoListElementReq();
      fileInfo.setFileId(UUID.randomUUID().toString());
      fileInfo.setProcessingType(1);
      fileInfo.setFileLogicalName("補足資料論理名補足資料論理名");
      fileInfo.setFilePhysicalName("1hosoku.txt");
      fileInfo.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
      byte[] file1Byetes = {
        49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27,
        -96, -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
      };
      fileInfo.setFileBinary(file1Byetes);
      fileInfos.add(fileInfo);
    }

    // テスト実施
    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));

    assertEquals("補足資料情報の数が上限数(100)を超えています。", exception.getMessage());

    // 結果確認
    verify(aircraftInfoRepository, times(0)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料100件登録を含む機体情報更新の異常系テスト<br>
   * 条件: 補足資料情報1件、補足資料100件<br>
   * 結果: 異常終了し、例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_異常終了すること_登録済み補足資料情報1件_補足資料100件登録() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    FileInfoEntity fileEntity = createFileInfoEntity_n1();
    fileInfoRepository.save(fileEntity);
    clearInvocations(aircraftInfoRepository);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    List<AircraftInfoFileInfoListElementReq> fileInfos = dto.getFileInfos();
    // 補足資料ファイル100件作成
    for (int i = 1; i < 100; i++) {
      AircraftInfoFileInfoListElementReq fileInfo = new AircraftInfoFileInfoListElementReq();
      fileInfo.setFileId(UUID.randomUUID().toString());
      fileInfo.setProcessingType(1);
      fileInfo.setFileLogicalName("補足資料論理名補足資料論理名");
      fileInfo.setFilePhysicalName("1hosoku.txt");
      fileInfo.setFileData("data:text/plain;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
      byte[] file1Byetes = {
        49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27,
        -96, -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
      };
      fileInfo.setFileBinary(file1Byetes);
      fileInfos.add(fileInfo);
    }

    // テスト実施
    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));

    assertEquals("補足資料情報の数が上限数(100)を超えています。", exception.getMessage());

    // 結果確認
    verify(aircraftInfoRepository, times(0)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料2件登録1件削除を含む機体情報更新の異常系テスト<br>
   * 条件: 補足資料情報100件、補足資料2件登録、1件削除<br>
   * 結果: 異常終了し、例外が発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_異常終了すること_登録済み補足資料情報100件_補足資料2件登録1件削除() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    FileInfoEntity fileEntity = createFileInfoEntity_templete();
    fileEntity.setFileId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    fileInfoRepository.save(fileEntity);
    // 補足資料ファイル100件作成
    for (int i = 1; i < 100; i++) {
      FileInfoEntity fileEntityTemp = fileEntity;
      fileEntityTemp.setFileId(UUID.randomUUID());
      fileInfoRepository.save(fileEntity);
    }
    clearInvocations(aircraftInfoRepository);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku3();
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    dto.getFileInfos().get(0).setProcessingType(3);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    ValidationErrorException exception =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));

    assertEquals("補足資料情報の数が上限数(100)を超えています。", exception.getMessage());

    // 結果確認
    verify(aircraftInfoRepository, times(0)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報の更新対象データなしの機体情報更新の異常系テスト<br>
   * 条件: 更新対象の補足資料情報が存在しない。<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_補足資料情報の更新対象のデータなし_1件更新() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(2);
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報の削除対象データなしの機体情報更新の異常系テスト<br>
   * 条件: 削除対象の補足資料情報が存在しない。<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_補足資料情報の削除対象のデータなし() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(3);
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // 結果確認
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報の更新対象論理削除済みの機体情報更新の異常系テスト<br>
   * 条件: 更新対象の補足資料情報が論理削除済み。<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_補足資料情報の更新対象のデータ論理削除済() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = new FileInfoEntity();
    fileEnt1.setFileId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    fileEnt1.setAircraftId(existingEntity1.getAircraftId());
    fileEnt1.setFileNumber(1);
    fileEnt1.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    fileEnt1.setUpdateUserId("user01");
    fileEnt1.setCreateTime(new Timestamp(System.currentTimeMillis()));
    fileEnt1.setDeleteFlag(true);
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(2);
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 補足資料情報の削除対象論理削除済みの機体情報更新の異常系テスト<br>
   * 条件: 削除対象の補足資料情報が論理削除済み。<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_補足資料情報の削除対象のデータ論理削除済() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileEnt1.setFileId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    fileEnt1.setDeleteFlag(true);
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setProcessingType(3);
    dto.getFileInfos().get(0).setFileId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // 結果確認
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 型式番号部分一致時の機体情報返却<br>
   * 条件: リポジトリに型式番号"2345"の機体情報を保存し、リクエストの型式番号を"2345"に設定、オペレータ情報を設定<br>
   * 結果: 返却データ件数が1件、返却データが保存した機体情報と一致<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getList_型式番号部分一致() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    // requestの設定
    AircraftInfoSearchListRequestDto request = new AircraftInfoSearchListRequestDto();
    request.setModelNumber("2345");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoSearchListResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.getList(request, userDto));
    AircraftInfoSearchListElement resultData = result.getData().get(0);

    // 結果比較
    assertEquals(1, result.getData().size());

    assertEquals(existingEntity.getAircraftId().toString(), resultData.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), resultData.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), resultData.getManufacturer());
    assertNotNull(resultData.getModelNumber());
    assertEquals(existingEntity.getModelNumber(), resultData.getModelNumber());
    assertEquals(existingEntity.getManufacturingNumber(), resultData.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), resultData.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), resultData.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), resultData.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), resultData.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), resultData.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), resultData.getLat());
    assertEquals(existingEntity.getLon(), resultData.getLon());
    assertEquals(existingEntity.getCertification(), resultData.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), resultData.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), resultData.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), resultData.getOwnerId().toString());
    assertEquals(existingEntity.getOperatorId(), resultData.getOperatorId());
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 型式番号不一致時の機体情報返却なし<br>
   * 条件: リポジトリに型式番号"2345"の機体情報を保存し、リクエストの型式番号を"未登録型式番号"に設定、オペレータ情報を設定<br>
   * 結果: 返却データ件数が0件<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getList_型式番号不一致() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    // requestの設定
    AircraftInfoSearchListRequestDto request = new AircraftInfoSearchListRequestDto();
    request.setAircraftName("未登録機体名");
    request.setModelNumber("2345");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoSearchListResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.getList(request, userDto));

    // 結果比較
    assertEquals(0, result.getData().size());
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機体名不一致かつ型式番号部分一致時の機体情報返却なし<br>
   * 条件: リポジトリに型式番号"9"の機体情報を保存し、リクエストの機体名を"未登録機体名"、型式番号を"9"に設定、オペレータ情報を設定<br>
   * 結果: 返却データ件数が0件<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getList_機体名不一致_型式番号部分一致() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    // requestの設定
    AircraftInfoSearchListRequestDto request = new AircraftInfoSearchListRequestDto();
    request.setModelNumber("9");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoSearchListResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.getList(request, userDto));

    // 結果比較
    assertEquals(0, result.getData().size());
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロードなしの機体情報更新の正常系テスト<br>
   * 条件: ペイロード情報は空配列。<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象なし_ペイロード情報空配列() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadEmpList();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロードなしの機体情報更新の正常系テスト<br>
   * 条件: ペイロード情報はnull。<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象なし_ペイロード情報項目null() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadNull();
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロードなしの機体情報更新の正常系テスト<br>
   * 条件: ペイロード情報のリスト内の要素がNull。<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象なし_補足情報ファイルのリスト内の要素がNULL() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().set(0, null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報を含む機体情報更新の正常系テスト<br>
   * 条件: ペイロード情報の処理種別コード値外（4）<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象なし_処理種別コード値外() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(4);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報の処理種別nullの機体情報更新の正常系テスト<br>
   * 条件: ペイロード情報の処理種別null。<br>
   * 結果: 機体情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象なし_処理種別null() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), "png");
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報1件を含む機体情報更新の正常系テスト<br>
   * 条件: ペイロード情報1件。<br>
   * 既存ペイロード情報なし。<br>
   * 結果: 機体情報とペイロード情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象あり_1件登録_既存ペイロード情報なし() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(1);
    dto.getPayloadInfos().get(0).setPayloadId(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertNotNull(capPayloadEnt.getPayloadId());
    assertNotEquals(capPayloadEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), 1);
    assertEquals(capPayloadEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage = {
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
    assertArrayEquals(capPayloadEnt.getImageData(), byteImage);
    assertEquals(capPayloadEnt.getImageFormat(), "png");
    assertEquals(capPayloadEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    byte[] byteFile = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capPayloadEnt.getFileData(), byteFile);
    assertEquals(capPayloadEnt.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), null);
    assertNotNull(capPayloadEnt.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報1件を登録することを確認する（既存補足資料あり）<br>
   * 条件: ペイロード情報1件。<br>
   * 既存補足資料情報あり。<br>
   * 結果: 機体情報とペイロード情報が更新されること。<br>
   * テストパターン： 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象あり_1件登録_既存ペイロード情報あり() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity();
    payloadEnt1.setPayloadId(UUID.randomUUID());
    payloadEnt1.setPayloadNumber(2);
    PayloadInfoEntity payloadEnt2 = createPayloadInfoEntity();
    payloadEnt2.setPayloadId(UUID.randomUUID());
    payloadEnt2.setPayloadNumber(3);
    payloadEnt2.setDeleteFlag(true);
    payloadInfoRepository.save(payloadEnt1);
    payloadInfoRepository.save(payloadEnt2);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(1);
    dto.getPayloadInfos().get(0).setPayloadId(null);
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertNotNull(capPayloadEnt.getPayloadId());
    assertNotEquals(capPayloadEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), 3);
    assertEquals(capPayloadEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage = {
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
    assertArrayEquals(capPayloadEnt.getImageData(), byteImage);
    assertEquals(capPayloadEnt.getImageFormat(), "png");
    assertEquals(capPayloadEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    byte[] byteFile = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capPayloadEnt.getFileData(), byteFile);
    assertEquals(capPayloadEnt.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), null);
    assertNotNull(capPayloadEnt.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報1件を登録することを確認する（リクエストにファイルIDあり）<br>
   * 条件: ペイロード情報1件。<br>
   * リクエストにファイルIDが設定されている。<br>
   * 結果: 機体情報とペイロード情報が更新されること。<br>
   * テストパターン： 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象あり_1件登録_リクエストにペイロードIDあり() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(1);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    UserInfoDto userDto = createUserInfoDto_OwnOperator();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertNotNull(capPayloadEnt.getPayloadId());
    assertNotEquals(capPayloadEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), 1);
    assertEquals(capPayloadEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage = {
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
    assertArrayEquals(capPayloadEnt.getImageData(), byteImage);
    assertEquals(capPayloadEnt.getImageFormat(), "png");
    assertEquals(capPayloadEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    byte[] byteFile = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capPayloadEnt.getFileData(), byteFile);
    assertEquals(capPayloadEnt.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), null);
    assertNotNull(capPayloadEnt.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報1件を更新することを確認する<br>
   * 条件: ペイロード情報1件を更新。<br>
   * 結果: 機体情報とペイロード情報が更新されること。<br>
   * テストパターン： 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象あり_1件更新() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = new PayloadInfoEntity();
    payloadEnt1.setPayloadId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    payloadEnt1.setAircraftId(existingEntity1.getAircraftId());
    payloadEnt1.setPayloadNumber(1);
    payloadEnt1.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    payloadEnt1.setUpdateUserId("user01");
    payloadEnt1.setCreateTime(new Timestamp(System.currentTimeMillis()));
    payloadEnt1.setDeleteFlag(false);
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(2);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertEquals(capPayloadEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), payloadEnt1.getPayloadNumber());
    assertEquals(capPayloadEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage = {
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
    assertArrayEquals(capPayloadEnt.getImageData(), byteImage);
    assertEquals(capPayloadEnt.getImageFormat(), "png");
    assertEquals(capPayloadEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    byte[] byteFile = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capPayloadEnt.getFileData(), byteFile);
    assertEquals(capPayloadEnt.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt.getOperatorId(), payloadEnt1.getOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), payloadEnt1.getUpdateUserId());
    assertEquals(capPayloadEnt.getCreateTime(), payloadEnt1.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertNotEquals(capPayloadEnt.getUpdateTime(), payloadEnt1.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報1件削除を含む機体情報更新の正常系テスト<br>
   * 条件: ペイロード情報1件を削除。<br>
   * 結果: 機体情報とペイロード情報が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象あり_1件削除() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity();
    payloadEnt1.setPayloadId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(3);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    assertEquals(capPayloadEnt.getPayloadId(), payloadEnt1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), payloadEnt1.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), payloadEnt1.getPayloadNumber());
    assertEquals(capPayloadEnt.getPayloadName(), payloadEnt1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), payloadEnt1.getPayloadDetailText());
    assertEquals(capPayloadEnt.getImageFormat(), payloadEnt1.getImageFormat());
    assertArrayEquals(capPayloadEnt.getImageData(), payloadEnt1.getImageData());
    assertEquals(capPayloadEnt.getFilePhysicalName(), payloadEnt1.getFilePhysicalName());
    assertEquals(capPayloadEnt.getFileFormat(), payloadEnt1.getFileFormat());
    assertArrayEquals(capPayloadEnt.getFileData(), payloadEnt1.getFileData());
    assertEquals(capPayloadEnt.getOperatorId(), payloadEnt1.getOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), payloadEnt1.getUpdateUserId());
    assertEquals(capPayloadEnt.getCreateTime(), payloadEnt1.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertNotEquals(capPayloadEnt.getUpdateTime(), payloadEnt1.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), true);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報3件を含む機体情報更新の正常系テスト<br>
   * 条件: ペイロード情報3件。<br>
   * 結果: 機体情報とペイロード情報3件が更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象あり_3件登録() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload3();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(3)).save(entPayloadCaptor.capture());

    List<PayloadInfoEntity> capPayloadEntList = entPayloadCaptor.getAllValues();

    PayloadInfoEntity capPayloadEnt1 = capPayloadEntList.get(0);
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertNotNull(capPayloadEnt1.getPayloadId());
    assertNotEquals(capPayloadEnt1.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt1.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt1.getPayloadNumber(), 1);
    assertEquals(capPayloadEnt1.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt1.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    byte[] byteImage1 = {
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
    assertArrayEquals(capPayloadEnt1.getImageData(), byteImage1);
    assertEquals(capPayloadEnt1.getImageFormat(), "png");
    assertEquals(capPayloadEnt1.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    byte[] byteFile1 = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capPayloadEnt1.getFileData(), byteFile1);
    assertEquals(capPayloadEnt1.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt1.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt1.getUpdateUserId(), null);
    assertNotNull(capPayloadEnt1.getCreateTime());
    assertNotNull(capPayloadEnt1.getUpdateTime());
    assertEquals(capPayloadEnt1.getDeleteFlag(), false);

    PayloadInfoEntity capPayloadEnt2 = capPayloadEntList.get(1);
    AircraftInfoPayloadInfoListElementReq reqPayload2 = dto.getPayloadInfos().get(1);
    assertNotNull(capPayloadEnt2.getPayloadId());
    assertNotEquals(capPayloadEnt2.getPayloadId().toString(), reqPayload2.getPayloadId());
    assertEquals(capPayloadEnt2.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt2.getPayloadNumber(), 2);
    assertEquals(capPayloadEnt2.getPayloadName(), reqPayload2.getPayloadName());
    assertEquals(capPayloadEnt2.getPayloadDetailText(), reqPayload2.getPayloadDetailText());
    byte[] byteImage2 = {
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
    assertArrayEquals(capPayloadEnt2.getImageData(), byteImage2);
    assertEquals(capPayloadEnt2.getImageFormat(), "png");
    assertEquals(capPayloadEnt2.getFilePhysicalName(), reqPayload2.getFilePhysicalName());
    byte[] byteFile2 = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capPayloadEnt2.getFileData(), byteFile2);
    assertEquals(capPayloadEnt2.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt2.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt2.getUpdateUserId(), null);
    assertNotNull(capPayloadEnt2.getCreateTime());
    assertNotNull(capPayloadEnt2.getUpdateTime());
    assertEquals(capPayloadEnt2.getDeleteFlag(), false);

    PayloadInfoEntity capPayloadEnt3 = capPayloadEntList.get(2);
    AircraftInfoPayloadInfoListElementReq reqPayload3 = dto.getPayloadInfos().get(2);
    assertNotNull(capPayloadEnt3.getPayloadId());
    assertNotEquals(capPayloadEnt3.getPayloadId().toString(), reqPayload3.getPayloadId());
    assertEquals(capPayloadEnt3.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt3.getPayloadNumber(), 3);
    assertEquals(capPayloadEnt3.getPayloadName(), reqPayload3.getPayloadName());
    assertEquals(capPayloadEnt3.getPayloadDetailText(), reqPayload3.getPayloadDetailText());
    byte[] byteImage3 = {
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
    assertArrayEquals(capPayloadEnt3.getImageData(), byteImage3);
    assertEquals(capPayloadEnt3.getImageFormat(), "png");
    assertEquals(capPayloadEnt3.getFilePhysicalName(), reqPayload3.getFilePhysicalName());
    byte[] byteFile3 = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(capPayloadEnt3.getFileData(), byteFile3);
    assertEquals(capPayloadEnt3.getFileFormat(), "text/plain");
    assertEquals(capPayloadEnt3.getOperatorId(), userDto.getUserOperatorId());
    assertEquals(capPayloadEnt3.getUpdateUserId(), null);
    assertNotNull(capPayloadEnt3.getCreateTime());
    assertNotNull(capPayloadEnt3.getUpdateTime());
    assertEquals(capPayloadEnt3.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報の更新対象データなしの機体情報更新の異常系テスト<br>
   * 条件: 更新対象のペイロード情報が存在しない。<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_ペイロード情報の更新対象のデータなし_1件更新() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(2);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");

    // テスト実施
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報の削除対象データなしの機体情報更新の異常系テスト<br>
   * 条件: 削除対象のペイロード情報が存在しない。<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_ペイロード情報の削除対象のデータなし() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(3);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");

    // 結果確認
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報の更新対象論理削除済みの機体情報更新の異常系テスト<br>
   * 条件: 更新対象のペイロード情報が論理削除済み。<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_ペイロード情報の更新対象のデータ論理削除済() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = new PayloadInfoEntity();
    payloadEnt1.setPayloadId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    payloadEnt1.setAircraftId(existingEntity1.getAircraftId());
    payloadEnt1.setPayloadNumber(1);
    payloadEnt1.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    payloadEnt1.setUpdateUserId("user01");
    payloadEnt1.setCreateTime(new Timestamp(System.currentTimeMillis()));
    payloadEnt1.setDeleteFlag(true);
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(2);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");

    // テスト実施
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報の削除対象論理削除済みの機体情報更新の異常系テスト<br>
   * 条件: 削除対象のペイロード情報が論理削除済み。<br>
   * 結果: NotFoundExceptionが発生する<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void putData_ペイロード情報の削除対象のデータ論理削除済() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity();
    payloadEnt1.setPayloadId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    payloadEnt1.setDeleteFlag(true);
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(3);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");

    // 結果確認
    Exception ex =
        assertThrows(NotFoundException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:a37ea275-26f6-4178-bf4f-93a35901bac8", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報1件を更新することを確認する<br>
   * 条件: ペイロード情報1件を更新。<br>
   * 結果: 機体情報とペイロード情報が更新されること。<br>
   * テストパターン： 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報の処理対象あり_必須項目以外null_1件更新() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = new PayloadInfoEntity();
    payloadEnt1.setPayloadId(UUID.fromString("a37ea275-26f6-4178-bf4f-93a35901bac8"));
    payloadEnt1.setAircraftId(existingEntity1.getAircraftId());
    payloadEnt1.setPayloadNumber(1);
    payloadEnt1.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    payloadEnt1.setUpdateUserId("user01");
    payloadEnt1.setCreateTime(new Timestamp(System.currentTimeMillis()));
    payloadEnt1.setDeleteFlag(false);
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setProcessingType(2);
    dto.getPayloadInfos().get(0).setPayloadId("a37ea275-26f6-4178-bf4f-93a35901bac8");
    dto.getPayloadInfos().get(0).setPayloadName(null);
    dto.getPayloadInfos().get(0).setPayloadDetailText(null);
    dto.getPayloadInfos().get(0).setImageData(null);
    dto.getPayloadInfos().get(0).setImageBinary(null);
    dto.getPayloadInfos().get(0).setFilePhysicalName(null);
    dto.getPayloadInfos().get(0).setFileData(null);
    dto.getPayloadInfos().get(0).setFileBinary(null);

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    ArgumentCaptor<AircraftInfoEntity> entCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
    AircraftInfoEntity capAirEnt = entCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
    assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
    assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
    assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
    assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
    assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
    assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
    assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
    assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
    assertEquals(capAirEnt.getLat(), dto.getLat());
    assertEquals(capAirEnt.getLon(), dto.getLon());
    assertEquals(capAirEnt.getCertification(), dto.getCertification());
    assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
    assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
    assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
    assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
    assertEquals(capAirEnt.getImageFormat(), "png");
    assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
    assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
    assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
    assertNotNull(capAirEnt.getUpdateTime());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
    assertEquals(capAirEnt.getDeleteFlag(), false);

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(1)).save(entPayloadCaptor.capture());

    PayloadInfoEntity capPayloadEnt = entPayloadCaptor.getValue();
    AircraftInfoPayloadInfoListElementReq reqPayload1 = dto.getPayloadInfos().get(0);
    assertEquals(capPayloadEnt.getPayloadId().toString(), reqPayload1.getPayloadId());
    assertEquals(capPayloadEnt.getAircraftId(), capAirEnt.getAircraftId());
    assertEquals(capPayloadEnt.getPayloadNumber(), payloadEnt1.getPayloadNumber());
    assertEquals(capPayloadEnt.getPayloadName(), reqPayload1.getPayloadName());
    assertEquals(capPayloadEnt.getPayloadDetailText(), reqPayload1.getPayloadDetailText());
    assertArrayEquals(capPayloadEnt.getImageData(), null);
    assertEquals(capPayloadEnt.getImageFormat(), null);
    assertEquals(capPayloadEnt.getFilePhysicalName(), reqPayload1.getFilePhysicalName());
    assertArrayEquals(capPayloadEnt.getFileData(), null);
    assertEquals(capPayloadEnt.getFileFormat(), null);
    assertEquals(capPayloadEnt.getOperatorId(), payloadEnt1.getOperatorId());
    assertEquals(capPayloadEnt.getUpdateUserId(), payloadEnt1.getUpdateUserId());
    assertEquals(capPayloadEnt.getCreateTime(), payloadEnt1.getCreateTime());
    assertNotNull(capPayloadEnt.getUpdateTime());
    assertNotEquals(capPayloadEnt.getUpdateTime(), payloadEnt1.getUpdateTime());
    assertEquals(capPayloadEnt.getDeleteFlag(), false);

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報20件登録（登録済みなし）<br>
   * 条件: 登録済みペイロード情報なし、リクエストでペイロード情報20件登録<br>
   * 結果: ペイロード情報20件が正常に登録される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報20件登録_登録済みなし() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_reg_payload20();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(20)).save(entPayloadCaptor.capture());

    List<PayloadInfoEntity> savedList = entPayloadCaptor.getAllValues();
    assertEquals(20, savedList.size());

    for (int i = 0; i < savedList.size(); i++) {
      PayloadInfoEntity cap = savedList.get(i);
      AircraftInfoPayloadInfoListElementReq expected = dto.getPayloadInfos().get(i);

      assertNotNull(cap.getPayloadId());
      assertEquals(i + 1, cap.getPayloadNumber());
      assertEquals(expected.getPayloadName(), cap.getPayloadName());

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報登録件数が空配列で正常終了することを確認する（登録済み20件）<br>
   * 条件: ペイロード情報は空配列。<br>
   * 登録済み20件。<br>
   * 結果: 機体情報が更新されること。<br>
   * テストパターン： 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報登録件数空配列_登録済み20件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    UUID aircraftId = existingEntity1.getAircraftId();
    for (int i = 1; i <= 20; i++) {
      PayloadInfoEntity p = new PayloadInfoEntity();
      p.setPayloadId(UUID.randomUUID());
      p.setAircraftId(aircraftId);
      p.setPayloadNumber(i);
      p.setPayloadName("ペイロード名" + i);
      p.setCreateTime(new Timestamp(System.currentTimeMillis()));
      p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
      p.setOperatorId(existingEntity1.getOperatorId());
      p.setDeleteFlag(false);
      payloadInfoRepository.save(p);
    }
    clearInvocations(aircraftInfoRepository);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadEmpList();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());

    // 結果確認(ペイロード情報)
    verify(payloadInfoRepository, times(0)).save(any());

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報登録件数がnullで正常終了することを確認する（登録済み20件）<br>
   * 条件: ペイロード情報はnull。<br>
   * 登録済み20件。<br>
   * 結果: 機体情報が更新されること。<br>
   * テストパターン： 正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報登録件数null_登録済み20件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    UUID aircraftId = existingEntity1.getAircraftId();
    for (int i = 1; i <= 20; i++) {
      PayloadInfoEntity p = new PayloadInfoEntity();
      p.setPayloadId(UUID.randomUUID());
      p.setAircraftId(aircraftId);
      p.setPayloadNumber(i);
      p.setPayloadName("ペイロード名" + i);
      p.setCreateTime(new Timestamp(System.currentTimeMillis()));
      p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
      p.setOperatorId(existingEntity1.getOperatorId());
      p.setDeleteFlag(false);
      payloadInfoRepository.save(p);
    }
    clearInvocations(aircraftInfoRepository);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadNull();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());

    // 結果確認(ペイロード情報)
    verify(payloadInfoRepository, times(0)).save(any());

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報19件登録（登録済み1件）<br>
   * 条件: 登録済みペイロード情報1件、リクエストでペイロード情報19件登録<br>
   * 結果: ペイロード情報20件が正常に登録される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報19件登録_登録済1件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    UUID aircraftId = existingEntity1.getAircraftId();
    PayloadInfoEntity p = new PayloadInfoEntity();
    p.setPayloadId(UUID.randomUUID());
    p.setAircraftId(aircraftId);
    p.setPayloadNumber(1);
    p.setPayloadName("ペイロード名1");
    p.setCreateTime(new Timestamp(System.currentTimeMillis()));
    p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    p.setOperatorId(existingEntity1.getOperatorId());
    p.setDeleteFlag(false);
    payloadInfoRepository.save(p);

    clearInvocations(aircraftInfoRepository);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_reg_payload20();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    // 1件削除
    dto.getPayloadInfos().remove(dto.getPayloadInfos().size() - 1);

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(19)).save(entPayloadCaptor.capture());

    List<PayloadInfoEntity> savedList = entPayloadCaptor.getAllValues();
    assertEquals(19, savedList.size());

    for (int i = 0; i < savedList.size(); i++) {
      PayloadInfoEntity cap = savedList.get(i);
      AircraftInfoPayloadInfoListElementReq expected = dto.getPayloadInfos().get(i);

      assertNotNull(cap.getPayloadId());
      assertEquals(i + 2, cap.getPayloadNumber());
      assertEquals(expected.getPayloadName(), cap.getPayloadName());

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報20件登録（登録済み1件論理削除済）<br>
   * 条件: 登録済みペイロード情報1件（論理削除済）、リクエストでペイロード情報20件登録<br>
   * 結果: ペイロード情報20件が正常に登録される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報20件登録_登録済1件論理削除済() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    UUID aircraftId = existingEntity1.getAircraftId();
    PayloadInfoEntity p = new PayloadInfoEntity();
    p.setPayloadId(UUID.randomUUID());
    p.setAircraftId(aircraftId);
    p.setPayloadNumber(1);
    p.setPayloadName("ペイロード名1");
    p.setCreateTime(new Timestamp(System.currentTimeMillis()));
    p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    p.setOperatorId(existingEntity1.getOperatorId());
    p.setDeleteFlag(true);
    payloadInfoRepository.save(p);

    clearInvocations(aircraftInfoRepository);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_reg_payload20();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(20)).save(entPayloadCaptor.capture());

    List<PayloadInfoEntity> savedList = entPayloadCaptor.getAllValues();
    assertEquals(20, savedList.size());

    for (int i = 0; i < savedList.size(); i++) {
      PayloadInfoEntity cap = savedList.get(i);
      AircraftInfoPayloadInfoListElementReq expected = dto.getPayloadInfos().get(i);

      assertNotNull(cap.getPayloadId());
      assertEquals(i + 1, cap.getPayloadNumber());
      assertEquals(expected.getPayloadName(), cap.getPayloadName());

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報登録1削除1（登録済み20件）<br>
   * 条件: 登録済みペイロード情報20件、リクエストで1件登録・1件削除<br>
   * 結果: ペイロード情報21件が正常に登録・削除される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報登録1削除1_登録済み20件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    UUID aircraftId = existingEntity1.getAircraftId();
    UUID payloadId = (UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf20"));
    for (int i = 1; i <= 19; i++) {
      PayloadInfoEntity p = new PayloadInfoEntity();
      p.setPayloadId(UUID.randomUUID());
      p.setAircraftId(aircraftId);
      p.setPayloadNumber(i);
      p.setPayloadName("ペイロード名" + i);
      p.setCreateTime(new Timestamp(System.currentTimeMillis()));
      p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
      p.setOperatorId(existingEntity1.getOperatorId());
      p.setDeleteFlag(false);
      payloadInfoRepository.save(p);
    }
    PayloadInfoEntity p = new PayloadInfoEntity();
    p.setPayloadId(payloadId);
    p.setAircraftId(aircraftId);
    p.setPayloadNumber(20);
    p.setPayloadName("ペイロード名20");
    p.setCreateTime(new Timestamp(System.currentTimeMillis()));
    p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    p.setOperatorId(existingEntity1.getOperatorId());
    p.setDeleteFlag(false);
    payloadInfoRepository.save(p);
    clearInvocations(aircraftInfoRepository);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadNull();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos =
        new ArrayList<AircraftInfoPayloadInfoListElementReq>();
    AircraftInfoPayloadInfoListElementReq payload1 = new AircraftInfoPayloadInfoListElementReq();
    payload1.setProcessingType(1);
    payload1.setPayloadName("テストペイロード");
    payloadInfos.add(payload1);
    AircraftInfoPayloadInfoListElementReq payload2 = new AircraftInfoPayloadInfoListElementReq();
    payload2.setProcessingType(3);
    payload2.setPayloadId(payloadId.toString());
    payloadInfos.add(payload2);
    dto.setPayloadInfos(payloadInfos);

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(2)).save(entPayloadCaptor.capture());

    List<PayloadInfoEntity> savedList = entPayloadCaptor.getAllValues();
    assertEquals(2, savedList.size());

    // 結果確認(戻り値)
    assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報21件更新（登録済み21件）<br>
   * 条件: 登録済みペイロード情報21件、リクエストで21件更新<br>
   * 結果: ペイロード情報21件が正常に更新される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報21件更新_登録済21件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    UUID aircraftId = existingEntity1.getAircraftId();
    List<PayloadInfoEntity> payloadinfos = new ArrayList<>();
    for (int i = 1; i <= 21; i++) {
      PayloadInfoEntity p = new PayloadInfoEntity();
      p.setPayloadId(UUID.randomUUID());
      p.setAircraftId(aircraftId);
      p.setPayloadNumber(i);
      p.setPayloadName("ペイロード名" + i);
      p.setCreateTime(new Timestamp(System.currentTimeMillis()));
      p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
      p.setOperatorId(existingEntity1.getOperatorId());
      p.setDeleteFlag(false);
      payloadinfos.add(p);
      payloadInfoRepository.save(p);
    }
    clearInvocations(aircraftInfoRepository);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setAircraftName("機体名");
    dto.setLat(Double.valueOf(11));
    dto.setLon(Double.valueOf(22));
    dto.setCertification(Boolean.TRUE);

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos = new ArrayList<>();
    for (int i = 0; i <= 20; i++) {
      AircraftInfoPayloadInfoListElementReq p = new AircraftInfoPayloadInfoListElementReq();
      p.setProcessingType(2);
      p.setPayloadId(payloadinfos.get(i).getPayloadId().toString());
      p.setPayloadName("ペイロード名" + i + 1);
      payloadInfos.add(p);
    }
    dto.setPayloadInfos(payloadInfos);

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(21)).save(entPayloadCaptor.capture());

    List<PayloadInfoEntity> savedList = entPayloadCaptor.getAllValues();
    assertEquals(21, savedList.size());

    for (int i = 0; i < savedList.size(); i++) {
      PayloadInfoEntity cap = savedList.get(i);
      AircraftInfoPayloadInfoListElementReq expected = dto.getPayloadInfos().get(i);

      assertNotNull(cap.getPayloadId());
      assertEquals(i + 1, cap.getPayloadNumber());
      assertEquals(expected.getPayloadName(), cap.getPayloadName());
      assertNotEquals(cap.getUpdateTime(), payloadinfos.get(i).getUpdateTime());

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報21件削除（登録済み21件）<br>
   * 条件: 登録済みペイロード情報21件、リクエストで21件削除<br>
   * 結果: ペイロード情報21件が正常に削除される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void putData_正常終了すること_ペイロード情報21件削除_登録済21件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    UUID aircraftId = existingEntity1.getAircraftId();
    List<PayloadInfoEntity> payloadinfos = new ArrayList<>();
    for (int i = 1; i <= 21; i++) {
      PayloadInfoEntity p = new PayloadInfoEntity();
      p.setPayloadId(UUID.randomUUID());
      p.setAircraftId(aircraftId);
      p.setPayloadNumber(i);
      p.setPayloadName("ペイロード名" + i);
      p.setCreateTime(new Timestamp(System.currentTimeMillis()));
      p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
      p.setOperatorId(existingEntity1.getOperatorId());
      p.setDeleteFlag(false);
      payloadinfos.add(p);
      payloadInfoRepository.save(p);
    }
    clearInvocations(aircraftInfoRepository);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = new AircraftInfoRequestDto();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    dto.setAircraftName("機体名");
    dto.setLat(Double.valueOf(11));
    dto.setLon(Double.valueOf(22));
    dto.setCertification(Boolean.TRUE);

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos = new ArrayList<>();
    for (int i = 0; i <= 20; i++) {
      AircraftInfoPayloadInfoListElementReq p = new AircraftInfoPayloadInfoListElementReq();
      p.setProcessingType(3);
      p.setPayloadId(payloadinfos.get(i).getPayloadId().toString());
      payloadInfos.add(p);
    }
    dto.setPayloadInfos(payloadInfos);

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認(機体)
    ArgumentCaptor<AircraftInfoEntity> entAirCaptor =
        ArgumentCaptor.forClass(AircraftInfoEntity.class);
    verify(aircraftInfoRepository, times(1)).save(entAirCaptor.capture());
    AircraftInfoEntity capAirEnt = entAirCaptor.getValue();

    assertEquals(capAirEnt.getAircraftId().toString(), dto.getAircraftId());
    assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());

    // 結果確認(ペイロード情報)
    ArgumentCaptor<PayloadInfoEntity> entPayloadCaptor =
        ArgumentCaptor.forClass(PayloadInfoEntity.class);
    verify(payloadInfoRepository, times(21)).save(entPayloadCaptor.capture());

    List<PayloadInfoEntity> savedList = entPayloadCaptor.getAllValues();
    assertEquals(21, savedList.size());

    for (int i = 0; i < savedList.size(); i++) {
      PayloadInfoEntity cap = savedList.get(i);

      assertNotNull(cap.getPayloadId());
      assertEquals(i + 1, cap.getPayloadNumber());
      assertNotEquals(cap.getUpdateTime(), payloadinfos.get(i).getUpdateTime());
      assertEquals(cap.getDeleteFlag(), true);

      // 結果確認(戻り値)
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());
    }
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: ペイロード情報なしの機体の詳細取得（ペイロード情報リストnull）<br>
   * 条件: ペイロード情報が存在しない機体IDで詳細取得を実施<br>
   * 結果: 返却される詳細情報のペイロード情報リストがnull<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_ペイロード情報なしの機体の詳細取得_ペイロード情報リストnull() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    doReturn(null)
        .when(payloadInfoRepository)
        .findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(any());

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, true, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,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";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(0, result.getFileInfos().size());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(payloadInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報21件登録時の異常終了<br>
   * 条件: リクエストでペイロード情報21件登録（上限超過）<br>
   * 結果: 例外が発生し登録されない<br>
   * テストパターン：異常系<br>
   */
  @Test
  void putData_異常終了すること_21件登録() {
    // リポジトリにデータ準備不要

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_reg_payload20();

    // 追加する要素を作成
    AircraftInfoPayloadInfoListElementReq extra = new AircraftInfoPayloadInfoListElementReq();
    extra.setProcessingType(1);
    extra.setPayloadName("ペイロード名21");
    dto.getPayloadInfos().add(extra);

    // テスト実施
    Exception ex =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("ペイロード情報の数が上限数(20)を超えています。", ex.getMessage());

    // 結果確認
    verify(aircraftInfoRepository, times(0)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報20件登録＋登録済1件時の異常終了<br>
   * 条件: 登録済みペイロード情報1件、リクエストで20件登録（上限超過）<br>
   * 結果: 例外が発生し登録されない<br>
   * テストパターン：異常系<br>
   */
  @Test
  void putData_異常終了すること_登録20件登録済1件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    UUID aircraftId = existingEntity1.getAircraftId();
    PayloadInfoEntity p = new PayloadInfoEntity();
    p.setPayloadId(UUID.randomUUID());
    p.setAircraftId(aircraftId);
    p.setPayloadNumber(1);
    p.setPayloadName("ペイロード名1");
    p.setCreateTime(new Timestamp(System.currentTimeMillis()));
    p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    p.setOperatorId(existingEntity1.getOperatorId());
    p.setDeleteFlag(false);
    payloadInfoRepository.save(p);
    clearInvocations(aircraftInfoRepository);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_reg_payload20();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    // テスト実施
    Exception ex =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("ペイロード情報の数が上限数(20)を超えています。", ex.getMessage());

    // 結果確認
    verify(aircraftInfoRepository, times(0)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: ペイロード情報2件登録＋1件削除＋登録済20件時の異常終了<br>
   * 条件: 登録済みペイロード情報20件、リクエストで2件登録・1件削除（上限超過）<br>
   * 結果: 例外が発生し登録されない<br>
   * テストパターン：異常系<br>
   */
  @Test
  void putData_異常終了すること_登録2削除1登録済20件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity1);
    UUID aircraftId = existingEntity1.getAircraftId();
    List<PayloadInfoEntity> payloadinfos = new ArrayList<>();
    for (int i = 1; i <= 20; i++) {
      PayloadInfoEntity p = new PayloadInfoEntity();
      p.setPayloadId(UUID.randomUUID());
      p.setAircraftId(aircraftId);
      p.setPayloadNumber(i);
      p.setPayloadName("ペイロード名" + i);
      p.setCreateTime(new Timestamp(System.currentTimeMillis()));
      p.setUpdateTime(new Timestamp(System.currentTimeMillis()));
      p.setOperatorId(existingEntity1.getOperatorId());
      p.setDeleteFlag(false);
      payloadinfos.add(p);
      payloadInfoRepository.save(p);
    }
    clearInvocations(aircraftInfoRepository);
    clearInvocations(payloadInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadNull();
    dto.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos =
        new ArrayList<AircraftInfoPayloadInfoListElementReq>();
    AircraftInfoPayloadInfoListElementReq payload1 = new AircraftInfoPayloadInfoListElementReq();
    payload1.setProcessingType(1);
    payload1.setPayloadName("テストペイロード");
    payloadInfos.add(payload1);
    AircraftInfoPayloadInfoListElementReq payload2 = new AircraftInfoPayloadInfoListElementReq();
    payload2.setProcessingType(1);
    payload2.setPayloadName("テストペイロード");
    payloadInfos.add(payload2);
    AircraftInfoPayloadInfoListElementReq payload3 = new AircraftInfoPayloadInfoListElementReq();
    payload3.setProcessingType(3);
    payload3.setPayloadId(payloadinfos.get(0).getPayloadId().toString());
    payload3.setPayloadName("テストペイロー1");
    payloadInfos.add(payload2);
    dto.setPayloadInfos(payloadInfos);

    // テスト実施
    Exception ex =
        assertThrows(
            ValidationErrorException.class, () -> aircraftInfoServiceImpl.putData(dto, userDto));
    assertEquals("ペイロード情報の数が上限数(20)を超えています。", ex.getMessage());

    // 結果確認
    verify(aircraftInfoRepository, times(0)).save(any());
    verify(payloadInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機種名ありの機体情報更新の正常系テスト<br>
   * 条件: 機種名を新しい値で更新<br>
   * 結果: 機種名が正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_機種名更新() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfoEntity();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto();

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getModelNumber(), dto.getModelNumber());
      assertEquals(capAirEnt.getModelName(), dto.getModelName());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), null);
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機種名nullの機体情報更新の正常系テスト<br>
   * 条件: 機種名をnullで更新<br>
   * 結果: 機種名がnullで正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_機種名null() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfoEntity();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
    dto.setModelName(null);

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getModelNumber(), dto.getModelNumber());
      assertEquals(capAirEnt.getModelName(), existingEntity1.getModelName());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), null);
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: putData<br>
   * 試験名: 機種名空文字の機体情報更新の正常系テスト<br>
   * 条件: 機種名を空文字で更新<br>
   * 結果: 機種名が空文字で正常に更新される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void putData_正常終了すること_機種名空文字() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity1 = createAircraftInfoEntity();
    aircraftInfoRepository.save(existingEntity1);
    clearInvocations(aircraftInfoRepository);

    // 引数、戻り値、モックの挙動等準備
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto();
    dto.setModelName("");

    // テスト実施
    AircraftInfoResponseDto result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.putData(dto, userDto));

    // 結果確認
    {
      ArgumentCaptor<AircraftInfoEntity> entCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(entCaptor.capture());
      AircraftInfoEntity capAirEnt = entCaptor.getValue();
      assertEquals(capAirEnt.getAircraftId().toString(), result.getAircraftId());

      assertEquals(capAirEnt.getAircraftId(), existingEntity1.getAircraftId());
      assertEquals(capAirEnt.getAircraftName(), dto.getAircraftName());
      assertEquals(capAirEnt.getManufacturer(), dto.getManufacturer());
      assertEquals(capAirEnt.getModelNumber(), dto.getModelNumber());
      assertEquals(capAirEnt.getModelName(), dto.getModelName());
      assertEquals(capAirEnt.getManufacturingNumber(), dto.getManufacturingNumber());
      assertEquals(capAirEnt.getAircraftType(), dto.getAircraftType());
      assertEquals(capAirEnt.getMaxTakeoffWeight(), dto.getMaxTakeoffWeight());
      assertEquals(capAirEnt.getBodyWeight(), dto.getBodyWeight());
      assertEquals(capAirEnt.getMaxFlightSpeed(), dto.getMaxFlightSpeed());
      assertEquals(capAirEnt.getMaxFlightTime(), dto.getMaxFlightTime());
      assertEquals(capAirEnt.getLat(), dto.getLat());
      assertEquals(capAirEnt.getLon(), dto.getLon());
      assertEquals(capAirEnt.getCertification(), dto.getCertification());
      assertEquals(capAirEnt.getDipsRegistrationCode(), dto.getDipsRegistrationCode());
      assertEquals(capAirEnt.getOwnerType(), dto.getOwnerType());
      assertEquals(capAirEnt.getOwnerId().toString(), dto.getOwnerId().toString());
      assertArrayEquals(capAirEnt.getImageBinary(), dto.getImageBinary());
      assertEquals(capAirEnt.getImageFormat(), null);
      assertEquals(capAirEnt.getOperatorId(), existingEntity1.getOperatorId());
      assertEquals(capAirEnt.getUpdateUserId(), existingEntity1.getUpdateUserId());
      assertEquals(capAirEnt.getCreateTime(), existingEntity1.getCreateTime());
      assertNotNull(capAirEnt.getUpdateTime());
      assertNotEquals(capAirEnt.getUpdateTime(), existingEntity1.getUpdateTime());
      assertEquals(capAirEnt.getDeleteFlag(), false);
    }
    verify(aircraftInfoRepository, times(1)).save(any());
  }

  /**
   * メソッド名: getDetail<br>
   * テストパターン：補足資料情報なしの機体の詳細取得_補足資料データなし #1<br>
   */
  @Test
  void getDetail_補足資料情報なしの機体の詳細取得_補足資料データなし() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,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";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(0, result.getFileInfos().size());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 補足資料情報なしの機体の詳細取得（補足資料データ論理削除）<br>
   * 条件: 論理削除された補足資料データのみ存在する機体IDで詳細取得を実施<br>
   * 結果: 返却される補足資料リストが空またはnull<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_補足資料情報なしの機体の詳細取得_補足資料データ論理削除() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_save_nml();
    fileEnt1.setDeleteFlag(true);
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPUAAABuCAYAAAD/EFXlAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAcdSURBVHhe7d0hVOw6FwXgkUgkEolEIpFIJHIkEolDIpFIJBKJRCKRSCQSiexd+76b+84c0rntNE2Tnf2tlbX+978n5nSyJ2mSllUnIlRW/v8Qkbop1CJkFGoRMgq1CBmFWoSMQi1CRqEWIaNQi5BRqEXIKNQiZBRqETIKtQgZhVqEjEItQkahFiGjUIuQUailWc/Pz93p6Wn38PDg/1XVFGpp1sHBQbdarbq9vT3/r6qmUEuzEOjQmHBVIzKCQi1CRqEWIaNQi5BpPtRY/j88PNy4ELU0fG62bQuZzvYRJoOrqTXQobFtW8h0tn8wGVzN7e3tj6DU1C4vL31J0jjbP5hwVSMygkItQkahFiGjUIuQUahlJzVsBeLz3d/f+49Oz14DJlzVFKj0QNt2c3PjPz41WzsTrmoKVONWYCuHdWzNTLiqkdG+v7+7s7OzH8Fu4bCOQi20EOz1ev0j2OxYa+WqRiZj7egxrLVyVSOTsXb0GNZauaqRyVg7egxrrVzVyGSsHT2GtVauamQS7FOzdvQY1lqTVVPKySl8houLi98npN7f3/3HlB4+0LiG7Gy9TJJVU0KgpzbUcH19/XuLpyU+0Ni3buEa2JqZJKumxpNTfe3o6Kh7fX31JVJqNdBg62bCVU3XdW9vb93d3V13fn7e7e/v/wjs2MZ8ZLLlQIOtnQlXNRMhvLEfAsYjk60HGhTqRnx+fv44C832fjPMZFoPNCjUQuHp6UmB/kOhrgDrnyZNBesNuJUIHRnXqtVAg0JdAdY/TZrCx8fHxrYj/vfX15f/z5qiUFeA9UuaCqPx8fHx32uDxUAdzOHtLzTVoOOyfklTYXsvXBfMYl5eXvx/0iTW/kJTjV3Rxagk/7m6utrovC2+YNAKx5n9CUgmFNVglA730+q4//NbVwh463yYQwvPDGCRFduaNaMI9ePj498vB+FueUU38CM0puAy/DgzZnt4DqDG48IUobaLQBidWoYfNHsPHTqofug2+f36bQ0LizX1q+pDbb8cLAK13HkxbbQ/cGg4XNL61lUfe50wImNk9tfPtlrei159qHGAIlz0lu8ZcbDE3y+yHW9NzV4rCz+OuLfGPbZ/FqCGYFcdanTkcLExSte+wLErrOj6zlfTdHEpfaG2MPPzzwKUHuz+aipg7x1bHZUwotgOhx833JLIvw0JNdQW7O3VFAwnouxFxqjdGh9orPy3eB12NTTUUFOw/11NoeyWTYvbNT7QWODB+W4ZbkyoIRZszBBLu+0bVk1hcBHt00atjU6xQGuFe7yxoYZYsNEXMciUEu7h1RTEjtKtHQlVoNPZJdQQOwsQwl3CiyvHVVMIu3XT0qKQAp3WrqEO0Pdi+9pLv7hyt2oWhClOuHjYxmmFP96oQE83NdRBX7iXmpJPq2YB9gQZDp6ww2OS+OUfE+gxf1ih1Xedg70OKfS9uDJc51wPGqWpJiP7FkzmE2T4hceJJt85Tk5OtgYahgbatqWnjEuw9acSe3GlbTm2wdJVk4ldoMDTWYxwGsz/4uOfMQUfwk/Vx7SlpoxLsHWnhhmWPcJs29DvcVfpq5mR38pifCWPfx83GkbsuYIWmzKWtkUzF1vznPw22Nzv0Ju3msTYt7Jwb2s7GqbEOV491DdlZA93rlCDf93WnH/5Zf5qEvGjNNNWFmYcuFe2X/oS7+PumzKyhjtnqMH233Bd55CnmgRYR2ncX/kve4lAW9vCPff9YE65Q+3XOuZ6CClPNROhk7GN0rHRubTQ9O2/5ljBzSF3qHMpvho8pGBfKogg1AwjcGx0Rl2lLvzFwo2Z05KziRQU6gWg09jRDOGu+b4udpCktNG5j1/BRat9b1uhXsB6vd7o/LV2oG0HSUodnWP6HmSo9USaQp2Zf2d1riN2KeGRUExT/T7wmIMkJYrtbeMHqrbnuRXqjPzTSHOtEs4hBLnvqOacB0lyiu1tI+g1LWIq1JnYF/OjLb29MxRG3r4go+U6SJKbn1Gh4baphtsKhToTu9JdQ6BjW1OhoRbMMhjDbGGtw35voeG6YNZV6neoUM8s9rhgqZ0B947orBiR/NZUK0H28ORY7MCKbXMejdyFQj0zH+iSLrQNcexzotWyNTU3jNqxHzt7nUpRYl9LoZhqch2hGyI2a9jWatuaygEjN3Ys/L78kt+rZz8XE65qEondH9qG0Qb3+/ghqnXvXBTqpijEbVCoG2LvB4WXQt2QcH9f0v2fpKdQi5BRqEXIKNQiZOzaCdNhIYVammUfh8W5hFJPMI6lUEuz8KSZfYSUZWFUoZam2cd8SzrCOoVCLc1jWzDjqEJkAoVahIxCLULGbm0xvNNcoZbm2b/+glb7c/EKtTTPv9O89lVwhVrkT7DDYRS8uaVmCrUIGYVahIxCLUJGoRYho1CLkFGoRcgo1CJkFGoRMgq1CJlfMf/5w1FFkZsAAAAASUVORK5CYII=";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(0, result.getFileInfos().size());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: リクエストの機体IDに紐づく補足資料情報のデータあり（1件）<br>
   * 条件: 機体IDに紐づく補足資料情報が1件存在<br>
   * 結果: 返却される補足資料リストに1件含まれる<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づく補足資料情報のデータあり_1件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_save_nml();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,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";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(1, result.getFileInfos().size());
    assertEquals(fileEnt1.getFileId().toString(), result.getFileInfos().get(0).getFileId());
    assertEquals(fileEnt1.getFileLogicalName(), result.getFileInfos().get(0).getFileLogicalName());
    assertEquals(
        fileEnt1.getFilePhysicalName(), result.getFileInfos().get(0).getFilePhysicalName());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: リクエストの機体IDに紐づく補足資料情報のデータあり（3件）<br>
   * 条件: 機体IDに紐づく補足資料情報が3件存在<br>
   * 結果: 返却される補足資料リストに3件含まれる<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づく補足資料情報のデータあり_3件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_n1();
    fileInfoRepository.save(fileEnt1);
    FileInfoEntity fileEnt2 = createFileInfoEntity_n2();
    fileInfoRepository.save(fileEnt2);
    FileInfoEntity fileEnt3 = createFileInfoEntity_n3();
    fileInfoRepository.save(fileEnt3);
    clearInvocations(fileInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPUAAABuCAYAAAD/EFXlAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAcdSURBVHhe7d0hVOw6FwXgkUgkEolEIpFIJHIkEolDIpFIJBKJRCKRSCQSiexd+76b+84c0rntNE2Tnf2tlbX+978n5nSyJ2mSllUnIlRW/v8Qkbop1CJkFGoRMgq1CBmFWoSMQi1CRqEWIaNQi5BRqEXIKNQiZBRqETIKtQgZhVqEjEItQkahFiGjUIuQUailWc/Pz93p6Wn38PDg/1XVFGpp1sHBQbdarbq9vT3/r6qmUEuzEOjQmHBVIzKCQi1CRqEWIaNQi5BpPtRY/j88PNy4ELU0fG62bQuZzvYRJoOrqTXQobFtW8h0tn8wGVzN7e3tj6DU1C4vL31J0jjbP5hwVSMygkItQkahFiGjUIuQUahlJzVsBeLz3d/f+49Oz14DJlzVFKj0QNt2c3PjPz41WzsTrmoKVONWYCuHdWzNTLiqkdG+v7+7s7OzH8Fu4bCOQi20EOz1ev0j2OxYa+WqRiZj7egxrLVyVSOTsXb0GNZauaqRyVg7egxrrVzVyGSsHT2GtVauamQS7FOzdvQY1lqTVVPKySl8houLi98npN7f3/3HlB4+0LiG7Gy9TJJVU0KgpzbUcH19/XuLpyU+0Ni3buEa2JqZJKumxpNTfe3o6Kh7fX31JVJqNdBg62bCVU3XdW9vb93d3V13fn7e7e/v/wjs2MZ8ZLLlQIOtnQlXNRMhvLEfAsYjk60HGhTqRnx+fv44C832fjPMZFoPNCjUQuHp6UmB/kOhrgDrnyZNBesNuJUIHRnXqtVAg0JdAdY/TZrCx8fHxrYj/vfX15f/z5qiUFeA9UuaCqPx8fHx32uDxUAdzOHtLzTVoOOyfklTYXsvXBfMYl5eXvx/0iTW/kJTjV3Rxagk/7m6utrovC2+YNAKx5n9CUgmFNVglA730+q4//NbVwh463yYQwvPDGCRFduaNaMI9ePj498vB+FueUU38CM0puAy/DgzZnt4DqDG48IUobaLQBidWoYfNHsPHTqofug2+f36bQ0LizX1q+pDbb8cLAK13HkxbbQ/cGg4XNL61lUfe50wImNk9tfPtlrei159qHGAIlz0lu8ZcbDE3y+yHW9NzV4rCz+OuLfGPbZ/FqCGYFcdanTkcLExSte+wLErrOj6zlfTdHEpfaG2MPPzzwKUHuz+aipg7x1bHZUwotgOhx833JLIvw0JNdQW7O3VFAwnouxFxqjdGh9orPy3eB12NTTUUFOw/11NoeyWTYvbNT7QWODB+W4ZbkyoIRZszBBLu+0bVk1hcBHt00atjU6xQGuFe7yxoYZYsNEXMciUEu7h1RTEjtKtHQlVoNPZJdQQOwsQwl3CiyvHVVMIu3XT0qKQAp3WrqEO0Pdi+9pLv7hyt2oWhClOuHjYxmmFP96oQE83NdRBX7iXmpJPq2YB9gQZDp6ww2OS+OUfE+gxf1ih1Xedg70OKfS9uDJc51wPGqWpJiP7FkzmE2T4hceJJt85Tk5OtgYahgbatqWnjEuw9acSe3GlbTm2wdJVk4ldoMDTWYxwGsz/4uOfMQUfwk/Vx7SlpoxLsHWnhhmWPcJs29DvcVfpq5mR38pifCWPfx83GkbsuYIWmzKWtkUzF1vznPw22Nzv0Ju3msTYt7Jwb2s7GqbEOV491DdlZA93rlCDf93WnH/5Zf5qEvGjNNNWFmYcuFe2X/oS7+PumzKyhjtnqMH233Bd55CnmgRYR2ncX/kve4lAW9vCPff9YE65Q+3XOuZ6CClPNROhk7GN0rHRubTQ9O2/5ljBzSF3qHMpvho8pGBfKogg1AwjcGx0Rl2lLvzFwo2Z05KziRQU6gWg09jRDOGu+b4udpCktNG5j1/BRat9b1uhXsB6vd7o/LV2oG0HSUodnWP6HmSo9USaQp2Zf2d1riN2KeGRUExT/T7wmIMkJYrtbeMHqrbnuRXqjPzTSHOtEs4hBLnvqOacB0lyiu1tI+g1LWIq1JnYF/OjLb29MxRG3r4go+U6SJKbn1Gh4baphtsKhToTu9JdQ6BjW1OhoRbMMhjDbGGtw35voeG6YNZV6neoUM8s9rhgqZ0B947orBiR/NZUK0H28ORY7MCKbXMejdyFQj0zH+iSLrQNcexzotWyNTU3jNqxHzt7nUpRYl9LoZhqch2hGyI2a9jWatuaygEjN3Ys/L78kt+rZz8XE65qEondH9qG0Qb3+/ghqnXvXBTqpijEbVCoG2LvB4WXQt2QcH9f0v2fpKdQi5BRqEXIKNQiZOzaCdNhIYVammUfh8W5hFJPMI6lUEuz8KSZfYSUZWFUoZam2cd8SzrCOoVCLc1jWzDjqEJkAoVahIxCLULGbm0xvNNcoZbm2b/+glb7c/EKtTTPv9O89lVwhVrkT7DDYRS8uaVmCrUIGYVahIxCLUJGoRYho1CLkFGoRcgo1CJkFGoRMgq1CJlfMf/5w1FFkZsAAAAASUVORK5CYII=";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(3, result.getFileInfos().size());
    assertEquals(fileEnt1.getFileId().toString(), result.getFileInfos().get(0).getFileId());
    assertEquals(fileEnt1.getFileLogicalName(), result.getFileInfos().get(0).getFileLogicalName());
    assertEquals(
        fileEnt1.getFilePhysicalName(), result.getFileInfos().get(0).getFilePhysicalName());
    assertEquals(fileEnt2.getFileId().toString(), result.getFileInfos().get(1).getFileId());
    assertEquals(fileEnt2.getFileLogicalName(), result.getFileInfos().get(1).getFileLogicalName());
    assertEquals(
        fileEnt2.getFilePhysicalName(), result.getFileInfos().get(1).getFilePhysicalName());
    assertEquals(fileEnt3.getFileId().toString(), result.getFileInfos().get(2).getFileId());
    assertEquals(fileEnt3.getFileLogicalName(), result.getFileInfos().get(2).getFileLogicalName());
    assertEquals(
        fileEnt3.getFilePhysicalName(), result.getFileInfos().get(2).getFilePhysicalName());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: リクエストの機体IDに紐づく補足資料情報のデータあり（1件・型式番号存在確認）<br>
   * 条件: 機体IDに紐づく補足資料情報が1件存在し型式番号も存在<br>
   * 結果: 返却される補足資料リストに1件含まれ型式番号も確認できる<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づく補足資料情報のデータあり_1件_型式番号存在確認() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_save_nml();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    // 結果比較
    assertNotNull(result.getModelNumber());
    assertEquals(existingEntity.getModelNumber(), result.getModelNumber());
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPUAAABuCAYAAAD/EFXlAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAcdSURBVHhe7d0hVOw6FwXgkUgkEolEIpFIJHIkEolDIpFIJBKJRCKRSCQSiexd+76b+84c0rntNE2Tnf2tlbX+978n5nSyJ2mSllUnIlRW/v8Qkbop1CJkFGoRMgq1CBmFWoSMQi1CRqEWIaNQi5BRqEXIKNQiZBRqETIKtQgZhVqEjEItQkahFiGjUIuQUailWc/Pz93p6Wn38PDg/1XVFGpp1sHBQbdarbq9vT3/r6qmUEuzEOjQmHBVIzKCQi1CRqEWIaNQi5BpPtRY/j88PNy4ELU0fG62bQuZzvYRJoOrqTXQobFtW8h0tn8wGVzN7e3tj6DU1C4vL31J0jjbP5hwVSMygkItQkahFiGjUIuQUahlJzVsBeLz3d/f+49Oz14DJlzVFKj0QNt2c3PjPz41WzsTrmoKVONWYCuHdWzNTLiqkdG+v7+7s7OzH8Fu4bCOQi20EOz1ev0j2OxYa+WqRiZj7egxrLVyVSOTsXb0GNZauaqRyVg7egxrrVzVyGSsHT2GtVauamQS7FOzdvQY1lqTVVPKySl8houLi98npN7f3/3HlB4+0LiG7Gy9TJJVU0KgpzbUcH19/XuLpyU+0Ni3buEa2JqZJKumxpNTfe3o6Kh7fX31JVJqNdBg62bCVU3XdW9vb93d3V13fn7e7e/v/wjs2MZ8ZLLlQIOtnQlXNRMhvLEfAsYjk60HGhTqRnx+fv44C832fjPMZFoPNCjUQuHp6UmB/kOhrgDrnyZNBesNuJUIHRnXqtVAg0JdAdY/TZrCx8fHxrYj/vfX15f/z5qiUFeA9UuaCqPx8fHx32uDxUAdzOHtLzTVoOOyfklTYXsvXBfMYl5eXvx/0iTW/kJTjV3Rxagk/7m6utrovC2+YNAKx5n9CUgmFNVglA730+q4//NbVwh463yYQwvPDGCRFduaNaMI9ePj498vB+FueUU38CM0puAy/DgzZnt4DqDG48IUobaLQBidWoYfNHsPHTqofug2+f36bQ0LizX1q+pDbb8cLAK13HkxbbQ/cGg4XNL61lUfe50wImNk9tfPtlrei159qHGAIlz0lu8ZcbDE3y+yHW9NzV4rCz+OuLfGPbZ/FqCGYFcdanTkcLExSte+wLErrOj6zlfTdHEpfaG2MPPzzwKUHuz+aipg7x1bHZUwotgOhx833JLIvw0JNdQW7O3VFAwnouxFxqjdGh9orPy3eB12NTTUUFOw/11NoeyWTYvbNT7QWODB+W4ZbkyoIRZszBBLu+0bVk1hcBHt00atjU6xQGuFe7yxoYZYsNEXMciUEu7h1RTEjtKtHQlVoNPZJdQQOwsQwl3CiyvHVVMIu3XT0qKQAp3WrqEO0Pdi+9pLv7hyt2oWhClOuHjYxmmFP96oQE83NdRBX7iXmpJPq2YB9gQZDp6ww2OS+OUfE+gxf1ih1Xedg70OKfS9uDJc51wPGqWpJiP7FkzmE2T4hceJJt85Tk5OtgYahgbatqWnjEuw9acSe3GlbTm2wdJVk4ldoMDTWYxwGsz/4uOfMQUfwk/Vx7SlpoxLsHWnhhmWPcJs29DvcVfpq5mR38pifCWPfx83GkbsuYIWmzKWtkUzF1vznPw22Nzv0Ju3msTYt7Jwb2s7GqbEOV491DdlZA93rlCDf93WnH/5Zf5qEvGjNNNWFmYcuFe2X/oS7+PumzKyhjtnqMH233Bd55CnmgRYR2ncX/kve4lAW9vCPff9YE65Q+3XOuZ6CClPNROhk7GN0rHRubTQ9O2/5ljBzSF3qHMpvho8pGBfKogg1AwjcGx0Rl2lLvzFwo2Z05KziRQU6gWg09jRDOGu+b4udpCktNG5j1/BRat9b1uhXsB6vd7o/LV2oG0HSUodnWP6HmSo9USaQp2Zf2d1riN2KeGRUExT/T7wmIMkJYrtbeMHqrbnuRXqjPzTSHOtEs4hBLnvqOacB0lyiu1tI+g1LWIq1JnYF/OjLb29MxRG3r4go+U6SJKbn1Gh4baphtsKhToTu9JdQ6BjW1OhoRbMMhjDbGGtw35voeG6YNZV6neoUM8s9rhgqZ0B947orBiR/NZUK0H28ORY7MCKbXMejdyFQj0zH+iSLrQNcexzotWyNTU3jNqxHzt7nUpRYl9LoZhqch2hGyI2a9jWatuaygEjN3Ys/L78kt+rZz8XE65qEondH9qG0Qb3+/ghqnXvXBTqpijEbVCoG2LvB4WXQt2QcH9f0v2fpKdQi5BRqEXIKNQiZOzaCdNhIYVammUfh8W5hFJPMI6lUEuz8KSZfYSUZWFUoZam2cd8SzrCOoVCLc1jWzDjqEJkAoVahIxCLULGbm0xvNNcoZbm2b/+glb7c/EKtTTPv9O89lVwhVrkT7DDYRS8uaVmCrUIGYVahIxCLUJGoRYho1CLkFGoRcgo1CJkFGoRMgq1CJlfMf/5w1FFkZsAAAAASUVORK5CYII=";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(1, result.getFileInfos().size());
    assertEquals(fileEnt1.getFileId().toString(), result.getFileInfos().get(0).getFileId());
    assertEquals(fileEnt1.getFileLogicalName(), result.getFileInfos().get(0).getFileLogicalName());
    assertEquals(
        fileEnt1.getFilePhysicalName(), result.getFileInfos().get(0).getFilePhysicalName());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 想定外エラー（機体情報取得時）<br>
   * 条件: 機体情報取得時に例外発生<br>
   * 結果: 例外が返却される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_想定外エラー_機体情報取得時() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_save_nml();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenThrow(new DuplicateKeyException("上記以外の例外が発生"));
    Exception ex =
        assertThrows(
            DuplicateKeyException.class,
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(0))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 想定外エラー（補足資料情報取得時）<br>
   * 条件: 補足資料情報取得時に例外発生<br>
   * 結果: 例外が返却される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_想定外エラー_補足資料情報情報取得時() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_save_nml();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    when(fileInfoRepository.findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any()))
        .thenThrow(new DuplicateKeyException("上記以外の例外が発生"));
    Exception ex =
        assertThrows(
            DuplicateKeyException.class,
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: ペイロード情報なしの機体の詳細取得（ペイロード情報論理削除）<br>
   * 条件: 論理削除されたペイロード情報のみ存在する機体IDで詳細取得を実施<br>
   * 結果: 返却されるペイロード情報リストが空またはnull<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_ペイロード情報なしの機体の詳細取得_ペイロード情報論理削除() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadEnt1.setDeleteFlag(true);
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, true, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,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";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(0, result.getFileInfos().size());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(payloadInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: リクエストの機体IDに紐づくペイロード情報のデータあり（1件）<br>
   * 条件: 機体IDに紐づくペイロード情報が1件存在<br>
   * 結果: 返却されるペイロード情報リストに1件含まれる<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づくペイロード情報のデータあり_1件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, true, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,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";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(1, result.getPayloadInfos().size());
    assertEquals(
        payloadEnt1.getPayloadId().toString(), result.getPayloadInfos().get(0).getPayloadId());
    assertEquals(payloadEnt1.getPayloadName(), result.getPayloadInfos().get(0).getPayloadName());
    assertEquals(
        payloadEnt1.getPayloadDetailText(), result.getPayloadInfos().get(0).getPayloadDetailText());
    assertEquals(
        payloadEnt1.getFilePhysicalName(), result.getPayloadInfos().get(0).getFilePhysicalName());
    assertEquals(imgBase64Str, result.getPayloadInfos().get(0).getImageData());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
    verify(payloadInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: リクエストの機体IDに紐づくペイロード情報のデータあり（3件）<br>
   * 条件: 機体IDに紐づくペイロード情報が3件存在<br>
   * 結果: 返却されるペイロード情報リストに3件含まれる<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づくペイロード情報のデータあり_3件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadInfoRepository.save(payloadEnt1);
    PayloadInfoEntity payloadEnt2 = createPayloadInfoEntity_2();
    payloadInfoRepository.save(payloadEnt2);
    PayloadInfoEntity payloadEnt3 = createPayloadInfoEntity_3();
    payloadInfoRepository.save(payloadEnt3);
    clearInvocations(payloadInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, true, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,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";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(3, result.getPayloadInfos().size());
    assertEquals(
        payloadEnt1.getPayloadId().toString(), result.getPayloadInfos().get(0).getPayloadId());
    assertEquals(payloadEnt1.getPayloadName(), result.getPayloadInfos().get(0).getPayloadName());
    assertEquals(
        payloadEnt1.getPayloadDetailText(), result.getPayloadInfos().get(0).getPayloadDetailText());
    assertEquals(
        payloadEnt1.getFilePhysicalName(), result.getPayloadInfos().get(0).getFilePhysicalName());
    assertEquals(imgBase64Str, result.getPayloadInfos().get(0).getImageData());
    assertEquals(
        payloadEnt2.getPayloadId().toString(), result.getPayloadInfos().get(1).getPayloadId());
    assertEquals(payloadEnt2.getPayloadName(), result.getPayloadInfos().get(1).getPayloadName());
    assertEquals(
        payloadEnt2.getPayloadDetailText(), result.getPayloadInfos().get(1).getPayloadDetailText());
    assertEquals(
        payloadEnt2.getFilePhysicalName(), result.getPayloadInfos().get(1).getFilePhysicalName());
    assertEquals(imgBase64Str, result.getPayloadInfos().get(1).getImageData());
    assertEquals(
        payloadEnt3.getPayloadId().toString(), result.getPayloadInfos().get(2).getPayloadId());
    assertEquals(payloadEnt3.getPayloadName(), result.getPayloadInfos().get(2).getPayloadName());
    assertEquals(
        payloadEnt3.getPayloadDetailText(), result.getPayloadInfos().get(2).getPayloadDetailText());
    assertEquals(
        payloadEnt3.getFilePhysicalName(), result.getPayloadInfos().get(2).getFilePhysicalName());
    assertEquals(imgBase64Str, result.getPayloadInfos().get(2).getImageData());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
    verify(payloadInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 想定外エラー（ペイロード情報取得時）<br>
   * 条件: ペイロード情報取得時に例外発生<br>
   * 結果: 例外が返却される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_想定外エラー_ペイロード情報情報取得時() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    when(payloadInfoRepository.findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(any()))
        .thenThrow(new DuplicateKeyException("上記以外の例外が発生"));
    Exception ex =
        assertThrows(
            DuplicateKeyException.class,
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, true, false, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 取得失敗（ペイロードIDがnull）<br>
   * 条件: ペイロードIDがnullのデータで詳細取得を実施<br>
   * 結果: 例外が返却される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void getDetail_取得失敗_ペイロードIDnull() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    List<PayloadInfoEntity> listPayload = new ArrayList<>();
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadEnt1.setPayloadId(null);
    listPayload.add(payloadEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    when(payloadInfoRepository.findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(any()))
        .thenReturn(listPayload);
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    Exception ex =
        assertThrows(
            ServiceErrorException.class,
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, true, false, userDto));
    assertEquals("ペイロード情報の取得に失敗しました。", ex.getMessage());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体IDに紐づくペイロード情報（画像null）1件の詳細取得<br>
   * 条件: ペイロード情報の画像データがnull<br>
   * 結果: 詳細情報の画像データがnullで格納されている<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づくペイロード情報のデータあり_画像null_1件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadEnt1.setImageData(null);
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, true, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,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";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(1, result.getPayloadInfos().size());
    assertEquals(
        payloadEnt1.getPayloadId().toString(), result.getPayloadInfos().get(0).getPayloadId());
    assertEquals(payloadEnt1.getPayloadName(), result.getPayloadInfos().get(0).getPayloadName());
    assertEquals(
        payloadEnt1.getPayloadDetailText(), result.getPayloadInfos().get(0).getPayloadDetailText());
    assertEquals(
        payloadEnt1.getFilePhysicalName(), result.getPayloadInfos().get(0).getFilePhysicalName());
    assertEquals(null, result.getPayloadInfos().get(0).getImageData());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
    verify(payloadInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体IDに紐づくペイロード情報（画像空）1件の詳細取得<br>
   * 条件: ペイロード情報の画像データが空配列<br>
   * 結果: 詳細情報の画像データが空配列で格納されている<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づくペイロード情報のデータあり_画像空_1件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadEnt1.setImageData(new byte[] {});
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, true, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    String imgBase64Str =
        "data:image/png;base64,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";
    assertEquals(imgBase64Str, result.getImageData());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    assertEquals(1, result.getPayloadInfos().size());
    assertEquals(
        payloadEnt1.getPayloadId().toString(), result.getPayloadInfos().get(0).getPayloadId());
    assertEquals(payloadEnt1.getPayloadName(), result.getPayloadInfos().get(0).getPayloadName());
    assertEquals(
        payloadEnt1.getPayloadDetailText(), result.getPayloadInfos().get(0).getPayloadDetailText());
    assertEquals(
        payloadEnt1.getFilePhysicalName(), result.getPayloadInfos().get(0).getFilePhysicalName());
    assertEquals(null, result.getPayloadInfos().get(0).getImageData());

    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
    verify(fileInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByFileNumberAsc(any());
    verify(payloadInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseOrderByPayloadNumberAsc(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体IDに紐づく機体情報（機種名値あり）の詳細取得<br>
   * 条件: 機体情報の機種名が値あり<br>
   * 結果: 詳細情報の機種名が値ありで格納されている<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づく機体情報のデータあり_機種名値あり() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setModelName("機種名1");
    aircraftInfoRepository.save(existingEntity);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf21";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getModelNumber(), result.getModelNumber());
    assertEquals(existingEntity.getModelName(), result.getModelName());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体IDに紐づく機体情報（機種名null）の詳細取得<br>
   * 条件: 機体情報の機種名がnull<br>
   * 結果: 詳細情報の機種名がnullで格納されている<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づく機体情報のデータあり_機種名null() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setModelName(null);
    aircraftInfoRepository.save(existingEntity);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf21";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getModelNumber(), result.getModelNumber());
    assertEquals(existingEntity.getModelName(), result.getModelName());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 機体IDに紐づく機体情報（機種名空文字）の詳細取得<br>
   * 条件: 機体情報の機種名が空文字<br>
   * 結果: 詳細情報の機種名が空文字で格納されている<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_リクエストの機体IDに紐づく機体情報のデータあり_機種名空文字() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfoEntity();
    existingEntity.setModelName("");
    aircraftInfoRepository.save(existingEntity);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf21";
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));
    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getModelNumber(), result.getModelNumber());
    assertEquals(existingEntity.getModelName(), result.getModelName());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 自事業者による対象機体の詳細取得（公開可）<br>
   * 条件: 公開フラグがtrue<br>
   * 結果: 詳細情報の公開フラグがtrueで格納されている<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_自事業者_対象機体公開可() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));

    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getModelNumber(), result.getModelNumber());
    assertEquals(existingEntity.getModelName(), result.getModelName());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getDetail_自事業者_対象機体公開可<br>
   * 試験名: 自事業者による対象機体の詳細取得（公開可）<br>
   * 条件: 公開フラグがtrue<br>
   * 結果: 詳細情報の公開フラグがtrueで格納されている<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_自事業者_対象機体公開不可() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    existingEntity.setPublicFlag(false);
    aircraftInfoRepository.save(existingEntity);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));

    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getModelNumber(), result.getModelNumber());
    assertEquals(existingEntity.getModelName(), result.getModelName());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 他事業者による対象機体の詳細取得（公開可）<br>
   * 条件: 公開フラグがtrue<br>
   * 結果: 詳細情報の公開フラグがtrueで格納されている<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_他事業者_対象機体公開可() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    AircraftInfoDetailResponseDto result =
        assertDoesNotThrow(
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, false, false, userDto));

    // 結果比較
    assertEquals(existingEntity.getAircraftId().toString(), result.getAircraftId().toString());
    assertEquals(existingEntity.getAircraftName(), result.getAircraftName());
    assertEquals(existingEntity.getManufacturer(), result.getManufacturer());
    assertEquals(existingEntity.getModelNumber(), result.getModelNumber());
    assertEquals(existingEntity.getModelName(), result.getModelName());
    assertEquals(existingEntity.getManufacturingNumber(), result.getManufacturingNumber());
    assertEquals(existingEntity.getAircraftType(), result.getAircraftType());
    assertEquals(existingEntity.getMaxTakeoffWeight(), result.getMaxTakeoffWeight());
    assertEquals(existingEntity.getBodyWeight(), result.getBodyWeight());
    assertEquals(existingEntity.getMaxFlightSpeed(), result.getMaxFlightSpeed());
    assertEquals(existingEntity.getMaxFlightTime(), result.getMaxFlightTime());
    assertEquals(existingEntity.getLat(), result.getLat());
    assertEquals(existingEntity.getLon(), result.getLon());
    assertEquals(existingEntity.getCertification(), result.getCertification());
    assertEquals(existingEntity.getDipsRegistrationCode(), result.getDipsRegistrationCode());
    assertEquals(existingEntity.getOwnerType(), result.getOwnerType());
    assertEquals(existingEntity.getOwnerId().toString(), result.getOwnerId().toString());
    assertEquals(existingEntity.getOperatorId(), result.getOperatorId());
    verify(aircraftInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseAndPublicFlagTrue(any());
  }

  /**
   * メソッド名: getDetail<br>
   * 試験名: 自事業者による対象機体の詳細取得（公開不可）<br>
   * 条件: 公開フラグがfalse<br>
   * 結果: 詳細情報の公開フラグがfalseで格納されている<br>
   * テストパターン：正常系<br>
   */
  @Test
  void getDetail_他事業者_対象機体公開不可() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    existingEntity.setPublicFlag(false);
    aircraftInfoRepository.save(existingEntity);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    Exception ex =
        assertThrows(
            NotFoundException.class,
            () -> aircraftInfoServiceImpl.getDetail(aircraftId, true, false, userDto));
    assertEquals("機体IDが見つかりません。機体ID:0a0711a5-ff74-4164-9309-8888b433cf22", ex.getMessage());
    verify(aircraftInfoRepository, times(1))
        .findByAircraftIdAndDeleteFlagFalseAndPublicFlagTrue(any());
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 正常終了<br>
   * 条件: 正常な削除リクエスト<br>
   * 結果: 正常に削除されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void deleteData_正常終了() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoDeleteRequestDto dto = new AircraftInfoDeleteRequestDto();
    dto.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.deleteData(aircraftId, userDto));
    {
      ArgumentCaptor<AircraftInfoEntity> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(dtoCaptor.capture());
      AircraftInfoEntity savedEnt = dtoCaptor.getValue();
      // 更新項目
      assertEquals(true, savedEnt.getDeleteFlag());
      assertNotNull(savedEnt.getUpdateTime());
      assertNotEquals(existingEntity.getUpdateTime(), savedEnt.getUpdateTime());
      assertEquals(userDto.getUserOperatorId(), savedEnt.getOperatorId());
      // 更新しない項目
      assertEquals(existingEntity.getAircraftId().toString(), savedEnt.getAircraftId().toString());
      assertEquals(existingEntity.getAircraftName(), savedEnt.getAircraftName());
      assertEquals(existingEntity.getManufacturer(), savedEnt.getManufacturer());
      assertEquals(existingEntity.getManufacturingNumber(), savedEnt.getManufacturingNumber());
      assertEquals(existingEntity.getAircraftType(), savedEnt.getAircraftType());
      assertEquals(existingEntity.getMaxTakeoffWeight(), savedEnt.getMaxTakeoffWeight());
      assertEquals(existingEntity.getBodyWeight(), savedEnt.getBodyWeight());
      assertEquals(existingEntity.getMaxFlightSpeed(), savedEnt.getMaxFlightSpeed());
      assertEquals(existingEntity.getMaxFlightTime(), savedEnt.getMaxFlightTime());
      assertEquals(existingEntity.getLat(), savedEnt.getLat());
      assertEquals(existingEntity.getLon(), savedEnt.getLon());
      assertEquals(existingEntity.getCertification(), savedEnt.getCertification());
      assertEquals(existingEntity.getDipsRegistrationCode(), savedEnt.getDipsRegistrationCode());
      assertEquals(existingEntity.getOwnerType(), savedEnt.getOwnerType());
      assertEquals(existingEntity.getOwnerId().toString(), savedEnt.getOwnerId().toString());
      assertArrayEquals(existingEntity.getImageBinary(), savedEnt.getImageBinary());
      assertEquals(existingEntity.getImageFormat(), savedEnt.getImageFormat());
      assertEquals(existingEntity.getUpdateUserId(), savedEnt.getUpdateUserId());
      assertEquals(existingEntity.getCreateTime(), savedEnt.getCreateTime());
    }
    {
      ArgumentCaptor<UUID> dtoCaptor = ArgumentCaptor.forClass(UUID.class);
      ArgumentCaptor<String> opeIdCaptor = ArgumentCaptor.forClass(String.class);
      verify(fileInfoRepository, times(1))
          .deleteByAircraftId(dtoCaptor.capture(), opeIdCaptor.capture());
      assertEquals(aircraftId, dtoCaptor.getValue().toString());
      assertEquals(userDto.getUserOperatorId(), opeIdCaptor.getValue());
    }
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 想定外エラー（機体情報検索時）<br>
   * 条件: 機体情報検索時に例外発生<br>
   * 結果: 例外がスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void deleteData_想定外エラー_機体情報検索時() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoDeleteRequestDto dto = new AircraftInfoDeleteRequestDto();
    dto.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    when(aircraftInfoRepository.findByAircraftIdAndDeleteFlagFalse(any()))
        .thenThrow(new DuplicateKeyException("上記以外の例外が発生"));
    Exception ex =
        assertThrows(
            DuplicateKeyException.class,
            () -> aircraftInfoServiceImpl.deleteData(aircraftId, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(0)).save(any());
    verify(fileInfoRepository, times(0)).deleteByAircraftId(any(), any());
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 想定外エラー（補足資料情報削除時）<br>
   * 条件: 補足資料情報削除時に例外発生<br>
   * 結果: 例外がスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void deleteData_想定外エラー_補足資料情報情報削除時() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoDeleteRequestDto dto = new AircraftInfoDeleteRequestDto();
    dto.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    when(fileInfoRepository.deleteByAircraftId(any(), any()))
        .thenThrow(new DuplicateKeyException("上記以外の例外が発生"));
    Exception ex =
        assertThrows(
            DuplicateKeyException.class,
            () -> aircraftInfoServiceImpl.deleteData(aircraftId, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(1)).deleteByAircraftId(any(), any());
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 正常終了（ペイロード情報）<br>
   * 条件: 正常なペイロード情報削除<br>
   * 結果: 正常に削除されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void deleteData_正常終了ペイロード情報() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.deleteData(aircraftId, userDto));
    {
      ArgumentCaptor<AircraftInfoEntity> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(dtoCaptor.capture());
      AircraftInfoEntity savedEnt = dtoCaptor.getValue();
      // 更新項目
      assertEquals(true, savedEnt.getDeleteFlag());
      assertNotNull(savedEnt.getUpdateTime());
      assertNotEquals(existingEntity.getUpdateTime(), savedEnt.getUpdateTime());
      assertEquals(userDto.getUserOperatorId(), savedEnt.getOperatorId());
      // 更新しない項目
      assertEquals(existingEntity.getAircraftId().toString(), savedEnt.getAircraftId().toString());
      assertEquals(existingEntity.getAircraftName(), savedEnt.getAircraftName());
      assertEquals(existingEntity.getManufacturer(), savedEnt.getManufacturer());
      assertEquals(existingEntity.getManufacturingNumber(), savedEnt.getManufacturingNumber());
      assertEquals(existingEntity.getAircraftType(), savedEnt.getAircraftType());
      assertEquals(existingEntity.getMaxTakeoffWeight(), savedEnt.getMaxTakeoffWeight());
      assertEquals(existingEntity.getBodyWeight(), savedEnt.getBodyWeight());
      assertEquals(existingEntity.getMaxFlightSpeed(), savedEnt.getMaxFlightSpeed());
      assertEquals(existingEntity.getMaxFlightTime(), savedEnt.getMaxFlightTime());
      assertEquals(existingEntity.getLat(), savedEnt.getLat());
      assertEquals(existingEntity.getLon(), savedEnt.getLon());
      assertEquals(existingEntity.getCertification(), savedEnt.getCertification());
      assertEquals(existingEntity.getDipsRegistrationCode(), savedEnt.getDipsRegistrationCode());
      assertEquals(existingEntity.getOwnerType(), savedEnt.getOwnerType());
      assertEquals(existingEntity.getOwnerId().toString(), savedEnt.getOwnerId().toString());
      assertArrayEquals(existingEntity.getImageBinary(), savedEnt.getImageBinary());
      assertEquals(existingEntity.getImageFormat(), savedEnt.getImageFormat());
      assertEquals(existingEntity.getUpdateUserId(), savedEnt.getUpdateUserId());
      assertEquals(existingEntity.getCreateTime(), savedEnt.getCreateTime());
    }
    {
      ArgumentCaptor<UUID> dtoCaptor = ArgumentCaptor.forClass(UUID.class);
      ArgumentCaptor<String> opeIdCaptor = ArgumentCaptor.forClass(String.class);
      verify(fileInfoRepository, times(1))
          .deleteByAircraftId(dtoCaptor.capture(), opeIdCaptor.capture());
      assertEquals(aircraftId, dtoCaptor.getValue().toString());
      assertEquals(userDto.getUserOperatorId(), opeIdCaptor.getValue());
    }
    {
      ArgumentCaptor<UUID> dtoCaptor = ArgumentCaptor.forClass(UUID.class);
      ArgumentCaptor<String> opeIdCaptor = ArgumentCaptor.forClass(String.class);
      verify(payloadInfoRepository, times(1))
          .deleteByAircraftId(dtoCaptor.capture(), opeIdCaptor.capture());
      assertEquals(aircraftId, dtoCaptor.getValue().toString());
      assertEquals(userDto.getUserOperatorId(), opeIdCaptor.getValue());
    }
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 正常終了すること（補足資料1件、ペイロード情報1件、料金情報1件）<br>
   * 条件: 補足資料1件、ペイロード情報1件、料金情報1件が存在<br>
   * 結果: 正常に削除されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void deleteData_正常終了すること_補足資料1件_ペイロード情報1件_料金情報1件() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    UUID airId = existingEntity.getAircraftId();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);
    FileInfoEntity fileEnt1 = createFileInfoEntity_n1();
    fileEnt1.setAircraftId(airId);
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity();
    payloadEnt1.setAircraftId(airId);
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);
    PriceInfoEntity priceEnt1 = createPriceInfoEntity();
    priceEnt1.setResourceId(airId.toString());
    priceInfoRepository.save(priceEnt1);
    clearInvocations(priceInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.deleteData(airId.toString(), userDto));
    {
      ArgumentCaptor<AircraftInfoEntity> dtoCaptor =
          ArgumentCaptor.forClass(AircraftInfoEntity.class);
      verify(aircraftInfoRepository, times(1)).save(dtoCaptor.capture());
      AircraftInfoEntity savedEnt = dtoCaptor.getValue();
      // 更新項目
      assertEquals(true, savedEnt.getDeleteFlag());
      assertNotNull(savedEnt.getUpdateTime());
      assertNotEquals(existingEntity.getUpdateTime(), savedEnt.getUpdateTime());
      assertEquals(userDto.getUserOperatorId(), savedEnt.getOperatorId());
      // 更新しない項目
      assertEquals(existingEntity.getAircraftId().toString(), savedEnt.getAircraftId().toString());
      assertEquals(existingEntity.getAircraftName(), savedEnt.getAircraftName());
      assertEquals(existingEntity.getManufacturer(), savedEnt.getManufacturer());
      assertEquals(existingEntity.getManufacturingNumber(), savedEnt.getManufacturingNumber());
      assertEquals(existingEntity.getAircraftType(), savedEnt.getAircraftType());
      assertEquals(existingEntity.getMaxTakeoffWeight(), savedEnt.getMaxTakeoffWeight());
      assertEquals(existingEntity.getBodyWeight(), savedEnt.getBodyWeight());
      assertEquals(existingEntity.getMaxFlightSpeed(), savedEnt.getMaxFlightSpeed());
      assertEquals(existingEntity.getMaxFlightTime(), savedEnt.getMaxFlightTime());
      assertEquals(existingEntity.getLat(), savedEnt.getLat());
      assertEquals(existingEntity.getLon(), savedEnt.getLon());
      assertEquals(existingEntity.getCertification(), savedEnt.getCertification());
      assertEquals(existingEntity.getDipsRegistrationCode(), savedEnt.getDipsRegistrationCode());
      assertEquals(existingEntity.getOwnerType(), savedEnt.getOwnerType());
      assertEquals(existingEntity.getOwnerId().toString(), savedEnt.getOwnerId().toString());
      assertArrayEquals(existingEntity.getImageBinary(), savedEnt.getImageBinary());
      assertEquals(existingEntity.getImageFormat(), savedEnt.getImageFormat());
      assertEquals(existingEntity.getUpdateUserId(), savedEnt.getUpdateUserId());
      assertEquals(existingEntity.getCreateTime(), savedEnt.getCreateTime());
    }
    {
      ArgumentCaptor<UUID> dtoCaptor = ArgumentCaptor.forClass(UUID.class);
      ArgumentCaptor<String> opeIdCaptor = ArgumentCaptor.forClass(String.class);
      verify(fileInfoRepository, times(1))
          .deleteByAircraftId(dtoCaptor.capture(), opeIdCaptor.capture());
      assertEquals(airId.toString(), dtoCaptor.getValue().toString());
      assertEquals(userDto.getUserOperatorId(), opeIdCaptor.getValue());
    }
    {
      ArgumentCaptor<UUID> dtoCaptor = ArgumentCaptor.forClass(UUID.class);
      ArgumentCaptor<String> opeIdCaptor = ArgumentCaptor.forClass(String.class);
      verify(payloadInfoRepository, times(1))
          .deleteByAircraftId(dtoCaptor.capture(), opeIdCaptor.capture());
      assertEquals(airId.toString(), dtoCaptor.getValue().toString());
      assertEquals(userDto.getUserOperatorId(), opeIdCaptor.getValue());
    }
    {
      ArgumentCaptor<PriceInfoEntity> dtoCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
      verify(priceInfoRepository, times(1)).save(dtoCaptor.capture());
      PriceInfoEntity capEnt = dtoCaptor.getValue();
      assertEquals(airId.toString(), capEnt.getResourceId());
      assertNotNull(capEnt.getUpdateTime());
      assertNotEquals(priceEnt1.getUpdateTime(), capEnt.getUpdateTime());
      assertEquals(userDto.getUserOperatorId(), capEnt.getOperatorId());
      assertEquals(true, capEnt.getDeleteFlag());
    }
  }

  /**
   * メソッド名: deleteData<br>
   * 試験名: 想定外エラー（ペイロード情報削除時）<br>
   * 条件: ペイロード情報削除時に例外発生<br>
   * 結果: 例外がスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void deleteData_想定外エラー_ペイロード情報削除時() {
    // リポジトリにデータ準備
    AircraftInfoEntity existingEntity = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(existingEntity);
    clearInvocations(aircraftInfoRepository);

    // テスト実施
    String aircraftId = "0a0711a5-ff74-4164-9309-8888b433cf22";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    when(payloadInfoRepository.deleteByAircraftId(any(), any()))
        .thenThrow(new DuplicateKeyException("上記以外の例外が発生"));
    Exception ex =
        assertThrows(
            DuplicateKeyException.class,
            () -> aircraftInfoServiceImpl.deleteData(aircraftId, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());
    verify(aircraftInfoRepository, times(1)).save(any());
    verify(fileInfoRepository, times(1)).deleteByAircraftId(any(), any());
    verify(payloadInfoRepository, times(1)).deleteByAircraftId(any(), any());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: パラメータで指定された補足資料情報あり（非論理削除）<br>
   * 条件: パラメータで指定された補足資料情報が存在し、非論理削除状態<br>
   * 結果: 正常にダウンロードできること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void downloadFile_パラメータで指定された補足資料情報あり_非論理削除() {
    // リポジトリにデータ準備
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // テスト実施
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    FileInfoEntity result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.downloadFile(fileId, userDto));
    // 結果比較
    assertEquals(fileEnt1.getFileId(), result.getFileId());
    assertEquals(fileEnt1.getAircraftId(), result.getAircraftId());
    assertEquals(fileEnt1.getFileNumber(), result.getFileNumber());
    assertEquals(fileEnt1.getFileLogicalName(), result.getFileLogicalName());
    assertEquals(fileEnt1.getFilePhysicalName(), result.getFilePhysicalName());
    assertArrayEquals(fileEnt1.getFileData(), result.getFileData());
    assertEquals(fileEnt1.getFileFormat(), result.getFileFormat());
    assertEquals(fileEnt1.getOperatorId(), result.getOperatorId());
    assertEquals(fileEnt1.getUpdateUserId(), result.getUpdateUserId());
    assertEquals(fileEnt1.getCreateTime(), result.getCreateTime());
    assertEquals(fileEnt1.getUpdateTime(), result.getUpdateTime());
    assertEquals(fileEnt1.getDeleteFlag(), result.getDeleteFlag());

    verify(fileInfoRepository, times(1)).findByFileIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: ペイロード要否がnullの場合の機体情報一覧取得<br>
   * 条件: isRequiredPayloadInfoがnull<br>
   * 結果: 機体情報が取得でき、ペイロード情報取得処理が呼ばれないこと<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_ペイロード要否null() {
    // 準備：機体/ペイロードエンティティをリポジトリに設定
    payloadTestEntities();
    AircraftInfoEntity airEntity1 = createAircraftInfoEntity_3();
    AircraftInfoEntity airEntity2 = createAircraftInfoEntity_4();
    AircraftInfoEntity airEntity3 = createAircraftInfoEntity_5();

    // テスト実施（ペイロード要否null）
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPayloadInfo(null);
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    assertFalse(result.getData().isEmpty());
    AircraftInfoSearchListElement el1 = result.getData().get(0);
    assertEquals(airEntity1.getAircraftId().toString(), el1.getAircraftId());
    assertEquals(airEntity1.getAircraftName(), el1.getAircraftName());
    AircraftInfoSearchListElement el2 = result.getData().get(1);
    assertEquals(airEntity2.getAircraftId().toString(), el2.getAircraftId());
    assertEquals(airEntity2.getAircraftName(), el2.getAircraftName());
    AircraftInfoSearchListElement el3 = result.getData().get(2);
    assertEquals(airEntity3.getAircraftId().toString(), el3.getAircraftId());
    assertEquals(airEntity3.getAircraftName(), el3.getAircraftName());

    // isRequiredPayloadInfo が null の場合はペイロード取得処理が呼ばれないこと
    verify(payloadInfoRepository, times(0))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: ペイロード要否が空文字の場合の機体情報一覧取得<br>
   * 条件: isRequiredPayloadInfoが空文字<br>
   * 結果: 機体情報が取得でき、ペイロード情報取得処理が呼ばれないこと<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_ペイロード要否空文字() {
    // 準備：機体/ペイロードエンティティをリポジトリに設定
    payloadTestEntities();
    AircraftInfoEntity airEntity1 = createAircraftInfoEntity_3();
    AircraftInfoEntity airEntity2 = createAircraftInfoEntity_4();
    AircraftInfoEntity airEntity3 = createAircraftInfoEntity_5();

    // テスト実施（ペイロード要否null）
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPayloadInfo("");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    assertFalse(result.getData().isEmpty());
    AircraftInfoSearchListElement el1 = result.getData().get(0);
    assertEquals(airEntity1.getAircraftId().toString(), el1.getAircraftId());
    assertEquals(airEntity1.getAircraftName(), el1.getAircraftName());
    AircraftInfoSearchListElement el2 = result.getData().get(1);
    assertEquals(airEntity2.getAircraftId().toString(), el2.getAircraftId());
    assertEquals(airEntity2.getAircraftName(), el2.getAircraftName());
    AircraftInfoSearchListElement el3 = result.getData().get(2);
    assertEquals(airEntity3.getAircraftId().toString(), el3.getAircraftId());
    assertEquals(airEntity3.getAircraftName(), el3.getAircraftName());

    // 呼び出し回数確認
    verify(payloadInfoRepository, times(0))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: ペイロード要否がfalseの場合の機体情報一覧取得<br>
   * 条件: isRequiredPayloadInfoが"false"<br>
   * 結果: 機体情報が取得でき、ペイロード情報取得処理が呼ばれないこと<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_ペイロード要否false() {
    // 準備：機体/ペイロードエンティティをリポジトリに設定
    payloadTestEntities();
    AircraftInfoEntity airEntity1 = createAircraftInfoEntity_3();
    AircraftInfoEntity airEntity2 = createAircraftInfoEntity_4();
    AircraftInfoEntity airEntity3 = createAircraftInfoEntity_5();

    // テスト実施（ペイロード要否null）
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPayloadInfo("false");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    assertFalse(result.getData().isEmpty());
    AircraftInfoSearchListElement el1 = result.getData().get(0);
    assertEquals(airEntity1.getAircraftId().toString(), el1.getAircraftId());
    assertEquals(airEntity1.getAircraftName(), el1.getAircraftName());
    assertEquals(airEntity1.getManufacturer(), el1.getManufacturer());
    assertEquals(airEntity1.getManufacturingNumber(), el1.getManufacturingNumber());
    assertEquals(airEntity1.getAircraftType(), el1.getAircraftType());
    assertEquals(airEntity1.getMaxTakeoffWeight(), el1.getMaxTakeoffWeight());
    assertEquals(airEntity1.getBodyWeight(), el1.getBodyWeight());
    assertEquals(airEntity1.getMaxFlightSpeed(), el1.getMaxFlightSpeed());
    assertEquals(airEntity1.getMaxFlightTime(), el1.getMaxFlightTime());
    assertEquals(airEntity1.getLat(), el1.getLat());
    assertEquals(airEntity1.getLon(), el1.getLon());
    assertEquals(airEntity1.getCertification(), el1.getCertification());
    assertEquals(airEntity1.getDipsRegistrationCode(), el1.getDipsRegistrationCode());
    assertEquals(airEntity1.getOwnerType(), el1.getOwnerType());
    assertEquals(airEntity1.getOwnerId().toString(), el1.getOwnerId());
    assertEquals(airEntity1.getOperatorId(), el1.getOperatorId());
    AircraftInfoSearchListElement el2 = result.getData().get(1);
    assertEquals(airEntity2.getAircraftId().toString(), el2.getAircraftId());
    assertEquals(airEntity2.getAircraftName(), el2.getAircraftName());
    assertEquals(airEntity2.getManufacturer(), el2.getManufacturer());
    assertEquals(airEntity2.getManufacturingNumber(), el2.getManufacturingNumber());
    assertEquals(airEntity2.getAircraftType(), el2.getAircraftType());
    assertEquals(airEntity2.getMaxTakeoffWeight(), el2.getMaxTakeoffWeight());
    assertEquals(airEntity2.getBodyWeight(), el2.getBodyWeight());
    assertEquals(airEntity2.getMaxFlightSpeed(), el2.getMaxFlightSpeed());
    assertEquals(airEntity2.getMaxFlightTime(), el2.getMaxFlightTime());
    assertEquals(airEntity2.getLat(), el2.getLat());
    assertEquals(airEntity2.getLon(), el2.getLon());
    assertEquals(airEntity2.getCertification(), el2.getCertification());
    assertEquals(airEntity2.getDipsRegistrationCode(), el2.getDipsRegistrationCode());
    assertEquals(airEntity2.getOwnerType(), el2.getOwnerType());
    assertEquals(airEntity2.getOwnerId().toString(), el2.getOwnerId());
    assertEquals(airEntity2.getOperatorId(), el2.getOperatorId());
    AircraftInfoSearchListElement el3 = result.getData().get(2);
    assertEquals(airEntity3.getAircraftId().toString(), el3.getAircraftId());
    assertEquals(airEntity3.getAircraftName(), el3.getAircraftName());
    assertEquals(airEntity3.getManufacturer(), el3.getManufacturer());
    assertEquals(airEntity3.getManufacturingNumber(), el3.getManufacturingNumber());
    assertEquals(airEntity3.getAircraftType(), el3.getAircraftType());
    assertEquals(airEntity3.getMaxTakeoffWeight(), el3.getMaxTakeoffWeight());
    assertEquals(airEntity3.getBodyWeight(), el3.getBodyWeight());
    assertEquals(airEntity3.getMaxFlightSpeed(), el3.getMaxFlightSpeed());
    assertEquals(airEntity3.getMaxFlightTime(), el3.getMaxFlightTime());
    assertEquals(airEntity3.getLat(), el3.getLat());
    assertEquals(airEntity3.getLon(), el3.getLon());
    assertEquals(airEntity3.getCertification(), el3.getCertification());
    assertEquals(airEntity3.getDipsRegistrationCode(), el3.getDipsRegistrationCode());
    assertEquals(airEntity3.getOwnerType(), el3.getOwnerType());
    assertEquals(airEntity3.getOwnerId().toString(), el3.getOwnerId());
    assertEquals(airEntity3.getOperatorId(), el3.getOperatorId());

    // 呼び出し回数確認
    verify(payloadInfoRepository, times(0))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: ペイロード要否がtrueで機体1件、ペイロード情報1件の場合の機体情報一覧取得<br>
   * 条件: isRequiredPayloadInfoが"true"、機体情報1件、ペイロード情報1件が存在<br>
   * 結果: 機体情報とペイロード情報が正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_ペイロード要否true機体1件ペイロード情報1件() {
    // 準備：機体/ペイロードエンティティをリポジトリに設定
    payloadTestEntities();
    AircraftInfoEntity airEntity = createAircraftInfoEntity_3();
    PayloadInfoEntity payloadEntity = createPayloadInfoEntity_5();

    // テスト実施（ペイロード要否null）
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("機体名1");
    dto.setIsRequiredPayloadInfo("true");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    assertFalse(result.getData().isEmpty());
    AircraftInfoSearchListElement el = result.getData().get(0);
    assertEquals(airEntity.getAircraftId().toString(), el.getAircraftId());
    assertEquals(airEntity.getAircraftName(), el.getAircraftName());
    assertEquals(airEntity.getManufacturer(), el.getManufacturer());
    assertEquals(airEntity.getManufacturingNumber(), el.getManufacturingNumber());
    assertEquals(airEntity.getAircraftType(), el.getAircraftType());
    assertEquals(airEntity.getMaxTakeoffWeight(), el.getMaxTakeoffWeight());
    assertEquals(airEntity.getBodyWeight(), el.getBodyWeight());
    assertEquals(airEntity.getMaxFlightSpeed(), el.getMaxFlightSpeed());
    assertEquals(airEntity.getMaxFlightTime(), el.getMaxFlightTime());
    assertEquals(airEntity.getLat(), el.getLat());
    assertEquals(airEntity.getLon(), el.getLon());
    assertEquals(airEntity.getCertification(), el.getCertification());
    assertEquals(airEntity.getDipsRegistrationCode(), el.getDipsRegistrationCode());
    assertEquals(airEntity.getOwnerType(), el.getOwnerType());
    assertEquals(airEntity.getOwnerId().toString(), el.getOwnerId());
    assertEquals(airEntity.getOperatorId(), el.getOperatorId());
    AircraftInfoPayloadInfoSearchListElement payloadEl = el.getPayloadInfos().get(0);
    assertEquals(payloadEntity.getPayloadId().toString(), payloadEl.getPayloadId());
    assertEquals(payloadEntity.getPayloadName(), payloadEl.getPayloadName());
    assertEquals(payloadEntity.getPayloadDetailText(), payloadEl.getPayloadDetailText());
    assertEquals(payloadEntity.getFilePhysicalName(), payloadEl.getFilePhysicalName());

    // 呼び出し回数確認
    verify(payloadInfoRepository, times(1))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: ペイロード要否がtrueで機体1件、ペイロード情報3件の場合の機体情報一覧取得<br>
   * 条件: isRequiredPayloadInfoが"true"、機体情報1件、ペイロード情報3件が存在<br>
   * 結果: 機体情報と複数のペイロード情報が正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_ペイロード要否true機体1件ペイロード情報3件() {
    // 準備：機体/ペイロードエンティティをリポジトリに設定
    payloadTestEntities();
    AircraftInfoEntity airEntity = createAircraftInfoEntity_4();
    PayloadInfoEntity payloadEntity1 = createPayloadInfoEntity_6();
    PayloadInfoEntity payloadEntity2 = createPayloadInfoEntity_7();
    PayloadInfoEntity payloadEntity3 = createPayloadInfoEntity_8();

    // テスト実施（ペイロード要否null）
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("機体名2");
    dto.setIsRequiredPayloadInfo("true");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    assertFalse(result.getData().isEmpty());
    AircraftInfoSearchListElement el = result.getData().get(0);
    assertEquals(airEntity.getAircraftId().toString(), el.getAircraftId());
    assertEquals(airEntity.getAircraftName(), el.getAircraftName());
    assertEquals(airEntity.getManufacturer(), el.getManufacturer());
    assertEquals(airEntity.getManufacturingNumber(), el.getManufacturingNumber());
    assertEquals(airEntity.getAircraftType(), el.getAircraftType());
    assertEquals(airEntity.getMaxTakeoffWeight(), el.getMaxTakeoffWeight());
    assertEquals(airEntity.getBodyWeight(), el.getBodyWeight());
    assertEquals(airEntity.getMaxFlightSpeed(), el.getMaxFlightSpeed());
    assertEquals(airEntity.getMaxFlightTime(), el.getMaxFlightTime());
    assertEquals(airEntity.getLat(), el.getLat());
    assertEquals(airEntity.getLon(), el.getLon());
    assertEquals(airEntity.getCertification(), el.getCertification());
    assertEquals(airEntity.getDipsRegistrationCode(), el.getDipsRegistrationCode());
    assertEquals(airEntity.getOwnerType(), el.getOwnerType());
    assertEquals(airEntity.getOwnerId().toString(), el.getOwnerId());
    assertEquals(airEntity.getOperatorId(), el.getOperatorId());
    AircraftInfoPayloadInfoSearchListElement payloadEl1 = el.getPayloadInfos().get(0);
    assertEquals(payloadEntity1.getPayloadId().toString(), payloadEl1.getPayloadId());
    assertEquals(payloadEntity1.getPayloadName(), payloadEl1.getPayloadName());
    assertEquals(payloadEntity1.getPayloadDetailText(), payloadEl1.getPayloadDetailText());
    assertEquals(payloadEntity1.getFilePhysicalName(), payloadEl1.getFilePhysicalName());
    AircraftInfoPayloadInfoSearchListElement payloadEl2 =
        result.getData().get(0).getPayloadInfos().get(1);
    assertEquals(payloadEntity2.getPayloadId().toString(), payloadEl2.getPayloadId());
    assertEquals(payloadEntity2.getPayloadName(), payloadEl2.getPayloadName());
    assertEquals(payloadEntity2.getPayloadDetailText(), payloadEl2.getPayloadDetailText());
    assertEquals(payloadEntity2.getFilePhysicalName(), payloadEl2.getFilePhysicalName());
    AircraftInfoPayloadInfoSearchListElement payloadEl3 =
        result.getData().get(0).getPayloadInfos().get(2);
    assertEquals(payloadEntity3.getPayloadId().toString(), payloadEl3.getPayloadId());
    assertEquals(payloadEntity3.getPayloadName(), payloadEl3.getPayloadName());
    assertEquals(payloadEntity3.getPayloadDetailText(), payloadEl3.getPayloadDetailText());
    assertEquals(payloadEntity3.getFilePhysicalName(), payloadEl3.getFilePhysicalName());

    // 呼び出し回数確認
    verify(payloadInfoRepository, times(1))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: ペイロード要否がtrueで機体3件、ペイロード情報4件の場合の機体情報一覧取得<br>
   * 条件: isRequiredPayloadInfoが"true"、機体情報3件、ペイロード情報4件が存在<br>
   * 結果: 複数の機体情報とペイロード情報が正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_ペイロード要否true機体3件ペイロード情報4件() {
    // 準備：機体/ペイロードエンティティをリポジトリに設定
    payloadTestEntities();
    AircraftInfoEntity airEntity1 = createAircraftInfoEntity_3();
    AircraftInfoEntity airEntity2 = createAircraftInfoEntity_4();
    AircraftInfoEntity airEntity3 = createAircraftInfoEntity_5();
    PayloadInfoEntity payloadEntity1 = createPayloadInfoEntity_5();
    PayloadInfoEntity payloadEntity2 = createPayloadInfoEntity_6();
    PayloadInfoEntity payloadEntity3 = createPayloadInfoEntity_7();
    PayloadInfoEntity payloadEntity4 = createPayloadInfoEntity_8();

    // テスト実施（ペイロード要否null）
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPayloadInfo("true");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    assertFalse(result.getData().isEmpty());

    AircraftInfoSearchListElement el1 = result.getData().get(0);
    assertEquals(airEntity1.getAircraftId().toString(), el1.getAircraftId());
    assertEquals(airEntity1.getAircraftName(), el1.getAircraftName());
    assertEquals(airEntity1.getManufacturer(), el1.getManufacturer());
    assertEquals(airEntity1.getManufacturingNumber(), el1.getManufacturingNumber());
    assertEquals(airEntity1.getAircraftType(), el1.getAircraftType());
    assertEquals(airEntity1.getMaxTakeoffWeight(), el1.getMaxTakeoffWeight());
    assertEquals(airEntity1.getBodyWeight(), el1.getBodyWeight());
    assertEquals(airEntity1.getMaxFlightSpeed(), el1.getMaxFlightSpeed());
    assertEquals(airEntity1.getMaxFlightTime(), el1.getMaxFlightTime());
    assertEquals(airEntity1.getLat(), el1.getLat());
    assertEquals(airEntity1.getLon(), el1.getLon());
    assertEquals(airEntity1.getCertification(), el1.getCertification());
    assertEquals(airEntity1.getDipsRegistrationCode(), el1.getDipsRegistrationCode());
    assertEquals(airEntity1.getOwnerType(), el1.getOwnerType());
    assertEquals(airEntity1.getOwnerId().toString(), el1.getOwnerId());
    assertEquals(airEntity1.getOperatorId(), el1.getOperatorId());
    AircraftInfoPayloadInfoSearchListElement payloadEl1 = el1.getPayloadInfos().get(0);
    assertEquals(payloadEntity1.getPayloadId().toString(), payloadEl1.getPayloadId());
    assertEquals(payloadEntity1.getPayloadName(), payloadEl1.getPayloadName());
    assertEquals(payloadEntity1.getPayloadDetailText(), payloadEl1.getPayloadDetailText());
    assertEquals(payloadEntity1.getFilePhysicalName(), payloadEl1.getFilePhysicalName());

    AircraftInfoSearchListElement el2 = result.getData().get(1);
    assertEquals(airEntity2.getAircraftId().toString(), el2.getAircraftId());
    assertEquals(airEntity2.getAircraftName(), el2.getAircraftName());
    assertEquals(airEntity2.getManufacturer(), el2.getManufacturer());
    assertEquals(airEntity2.getManufacturingNumber(), el2.getManufacturingNumber());
    assertEquals(airEntity2.getAircraftType(), el2.getAircraftType());
    assertEquals(airEntity2.getMaxTakeoffWeight(), el2.getMaxTakeoffWeight());
    assertEquals(airEntity2.getBodyWeight(), el2.getBodyWeight());
    assertEquals(airEntity2.getMaxFlightSpeed(), el2.getMaxFlightSpeed());
    assertEquals(airEntity2.getMaxFlightTime(), el2.getMaxFlightTime());
    assertEquals(airEntity2.getLat(), el2.getLat());
    assertEquals(airEntity2.getLon(), el2.getLon());
    assertEquals(airEntity2.getCertification(), el2.getCertification());
    assertEquals(airEntity2.getDipsRegistrationCode(), el2.getDipsRegistrationCode());
    assertEquals(airEntity2.getOwnerType(), el2.getOwnerType());
    assertEquals(airEntity2.getOwnerId().toString(), el2.getOwnerId());
    assertEquals(airEntity2.getOperatorId(), el2.getOperatorId());
    AircraftInfoPayloadInfoSearchListElement payloadEl2 = el2.getPayloadInfos().get(0);
    assertEquals(payloadEntity2.getPayloadId().toString(), payloadEl2.getPayloadId());
    assertEquals(payloadEntity2.getPayloadName(), payloadEl2.getPayloadName());
    assertEquals(payloadEntity2.getPayloadDetailText(), payloadEl2.getPayloadDetailText());
    assertEquals(payloadEntity2.getFilePhysicalName(), payloadEl2.getFilePhysicalName());
    AircraftInfoPayloadInfoSearchListElement payloadEl3 = el2.getPayloadInfos().get(1);
    assertEquals(payloadEntity3.getPayloadId().toString(), payloadEl3.getPayloadId());
    assertEquals(payloadEntity3.getPayloadName(), payloadEl3.getPayloadName());
    assertEquals(payloadEntity3.getPayloadDetailText(), payloadEl3.getPayloadDetailText());
    assertEquals(payloadEntity3.getFilePhysicalName(), payloadEl3.getFilePhysicalName());
    AircraftInfoPayloadInfoSearchListElement payloadEl4 = el2.getPayloadInfos().get(2);
    assertEquals(payloadEntity4.getPayloadId().toString(), payloadEl4.getPayloadId());
    assertEquals(payloadEntity4.getPayloadName(), payloadEl4.getPayloadName());
    assertEquals(payloadEntity4.getPayloadDetailText(), payloadEl4.getPayloadDetailText());
    assertEquals(payloadEntity4.getFilePhysicalName(), payloadEl4.getFilePhysicalName());

    AircraftInfoSearchListElement el3 = result.getData().get(2);
    assertEquals(airEntity3.getAircraftId().toString(), el3.getAircraftId());
    assertEquals(airEntity3.getAircraftName(), el3.getAircraftName());
    assertEquals(airEntity3.getManufacturer(), el3.getManufacturer());
    assertEquals(airEntity3.getManufacturingNumber(), el3.getManufacturingNumber());
    assertEquals(airEntity3.getAircraftType(), el3.getAircraftType());
    assertEquals(airEntity3.getMaxTakeoffWeight(), el3.getMaxTakeoffWeight());
    assertEquals(airEntity3.getBodyWeight(), el3.getBodyWeight());
    assertEquals(airEntity3.getMaxFlightSpeed(), el3.getMaxFlightSpeed());
    assertEquals(airEntity3.getMaxFlightTime(), el3.getMaxFlightTime());
    assertEquals(airEntity3.getLat(), el3.getLat());
    assertEquals(airEntity3.getLon(), el3.getLon());
    assertEquals(airEntity3.getCertification(), el3.getCertification());
    assertEquals(airEntity3.getDipsRegistrationCode(), el3.getDipsRegistrationCode());
    assertEquals(airEntity3.getOwnerType(), el3.getOwnerType());
    assertEquals(airEntity3.getOwnerId().toString(), el3.getOwnerId());
    assertEquals(airEntity3.getOperatorId(), el3.getOperatorId());
    assertTrue(el3.getPayloadInfos().isEmpty());

    // 呼び出し回数確認
    verify(payloadInfoRepository, times(1))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: ペイロード要否がtrueで機体1件、ペイロード情報0件の場合の機体情報一覧取得<br>
   * 条件: isRequiredPayloadInfoが"true"、機体情報1件、ペイロード情報0件<br>
   * 結果: 機体情報が取得でき、ペイロード情報が空であること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_ペイロード要否true機体1件ペイロード情報0件() {
    // 準備：機体/ペイロードエンティティをリポジトリに設定
    payloadTestEntities();
    AircraftInfoEntity airEntity = createAircraftInfoEntity_5();

    // テスト実施（ペイロード要否null）
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setAircraftName("機体名3");
    dto.setIsRequiredPayloadInfo("true");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    assertFalse(result.getData().isEmpty());
    AircraftInfoSearchListElement el = result.getData().get(0);
    assertEquals(airEntity.getAircraftId().toString(), el.getAircraftId());
    assertEquals(airEntity.getAircraftName(), el.getAircraftName());
    assertEquals(airEntity.getManufacturer(), el.getManufacturer());
    assertEquals(airEntity.getManufacturingNumber(), el.getManufacturingNumber());
    assertEquals(airEntity.getAircraftType(), el.getAircraftType());
    assertEquals(airEntity.getMaxTakeoffWeight(), el.getMaxTakeoffWeight());
    assertEquals(airEntity.getBodyWeight(), el.getBodyWeight());
    assertEquals(airEntity.getMaxFlightSpeed(), el.getMaxFlightSpeed());
    assertEquals(airEntity.getMaxFlightTime(), el.getMaxFlightTime());
    assertEquals(airEntity.getLat(), el.getLat());
    assertEquals(airEntity.getLon(), el.getLon());
    assertEquals(airEntity.getCertification(), el.getCertification());
    assertEquals(airEntity.getDipsRegistrationCode(), el.getDipsRegistrationCode());
    assertEquals(airEntity.getOwnerType(), el.getOwnerType());
    assertEquals(airEntity.getOwnerId().toString(), el.getOwnerId());
    assertEquals(airEntity.getOperatorId(), el.getOperatorId());
    assertTrue(el.getPayloadInfos().isEmpty());

    // 呼び出し回数確認
    verify(payloadInfoRepository, times(1))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 製造メーカー検索条件がnullの場合の機体情報一覧取得<br>
   * 条件: manufacturer=null<br>
   * 結果: 全ての機体情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_製造メーカーnull() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setManufacturer(null);
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(5, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(2).getAircraftId());
    assertEquals("製造メーカー2", el1.get(2).getManufacturer());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf14", el1.get(3).getAircraftId());
    assertNull(el1.get(3).getManufacturer());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf15", el1.get(4).getAircraftId());
    assertEquals("製造メーカー2", el1.get(4).getManufacturer());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 製造メーカー検索条件が空文字の場合の機体情報一覧取得<br>
   * 条件: manufacturer=""<br>
   * 結果: 全ての機体情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_製造メーカー空文字() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setManufacturer("");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(5, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(2).getAircraftId());
    assertEquals("製造メーカー2", el1.get(2).getManufacturer());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf14", el1.get(3).getAircraftId());
    assertNull(el1.get(3).getManufacturer());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf15", el1.get(4).getAircraftId());
    assertEquals("製造メーカー2", el1.get(4).getManufacturer());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 製造メーカー検索条件が一致する場合の機体情報一覧取得<br>
   * 条件: manufacturer="製造メーカー1"<br>
   * 結果: 該当する機体情報2件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_製造メーカー一致() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setManufacturer("製造メーカー1");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 型式番号検索条件がnullの場合の機体情報一覧取得<br>
   * 条件: modelNumber=null<br>
   * 結果: 全ての機体情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_型式番号null() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setModelNumber(null);
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(5, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(2).getAircraftId());
    assertEquals("MD12345V1", el1.get(2).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf14", el1.get(3).getAircraftId());
    assertNull(el1.get(3).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf15", el1.get(4).getAircraftId());
    assertEquals("MD12345V2", el1.get(4).getModelNumber());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 型式番号検索条件が空文字の場合の機体情報一覧取得<br>
   * 条件: modelNumber=""<br>
   * 結果: 全ての機体情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_型式番号空文字() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setModelNumber("");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(5, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(2).getAircraftId());
    assertEquals("MD12345V1", el1.get(2).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf14", el1.get(3).getAircraftId());
    assertNull(el1.get(3).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf15", el1.get(4).getAircraftId());
    assertEquals("MD12345V2", el1.get(4).getModelNumber());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 型式番号検索条件が一致する場合の機体情報一覧取得<br>
   * 条件: modelNumber="MD12345V1"<br>
   * 結果: 該当する機体情報2件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_型式番号一致() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setModelNumber("MD12345V1");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(1).getAircraftId());
    assertEquals("MD12345V1", el1.get(1).getModelNumber());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 製造メーカーと型式番号の複合検索条件が一致する場合の機体情報一覧取得<br>
   * 条件: manufacturer="製造メーカー1", modelNumber="MD12345V1"<br>
   * 結果: 両方の条件に該当する機体情報1件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_製造メーカー型式番号一致() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setManufacturer("製造メーカー1");
    dto.setModelNumber("MD12345V1");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(1, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 製造メーカーと型式番号にコンマ区切りの値が指定された場合の機体情報一覧取得<br>
   * 条件: manufacturer="製造メーカー1,製造メーカー1,製造メーカー2", modelNumber="MD12345V1,MD12345V2,MD12345V1"<br>
   * 結果: コンマ区切りは完全一致とみなされ、該当する機体情報0件が返却されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_製造メーカー型式番号_コンマ区切り不一致() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setManufacturer("製造メーカー1,製造メーカー1,製造メーカー2");
    dto.setModelNumber("MD12345V1,MD12345V2,MD12345V1");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(0, el1.size());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機種名検索条件がnullの場合の機体情報一覧取得<br>
   * 条件: modelName=null<br>
   * 結果: 全ての機体情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_機種名null() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_6();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_7();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_8();
    aircraftInfoRepository.save(entity3);
    AircraftInfoEntity entity4 = createAircraftInfoEntity_9();
    aircraftInfoRepository.save(entity4);
    AircraftInfoEntity entity5 = createAircraftInfoEntity_10();
    aircraftInfoRepository.save(entity5);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setModelName(null);
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(5, el1.size());
    assertEquals(entity1.getAircraftId().toString(), el1.get(0).getAircraftId());
    assertEquals(entity1.getModelName().toString(), el1.get(0).getModelName());
    assertEquals(entity2.getAircraftId().toString(), el1.get(1).getAircraftId());
    assertEquals(entity2.getModelName(), el1.get(1).getModelName());
    assertEquals(entity3.getAircraftId().toString(), el1.get(2).getAircraftId());
    assertEquals(entity3.getModelName(), el1.get(2).getModelName());
    assertEquals(entity4.getAircraftId().toString(), el1.get(3).getAircraftId());
    assertEquals(entity4.getModelName(), el1.get(3).getModelName());
    assertEquals(entity5.getAircraftId().toString(), el1.get(4).getAircraftId());
    assertNull(el1.get(4).getModelName());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機種名検索条件が空文字の場合の機体情報一覧取得<br>
   * 条件: modelName=""<br>
   * 結果: 全ての機体情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_機種名空文字() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_6();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_7();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_8();
    aircraftInfoRepository.save(entity3);
    AircraftInfoEntity entity4 = createAircraftInfoEntity_9();
    aircraftInfoRepository.save(entity4);
    AircraftInfoEntity entity5 = createAircraftInfoEntity_10();
    aircraftInfoRepository.save(entity5);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setModelName("");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(5, el1.size());
    assertEquals(entity1.getAircraftId().toString(), el1.get(0).getAircraftId());
    assertEquals(entity1.getModelName().toString(), el1.get(0).getModelName());
    assertEquals(entity2.getAircraftId().toString(), el1.get(1).getAircraftId());
    assertEquals(entity2.getModelName(), el1.get(1).getModelName());
    assertEquals(entity3.getAircraftId().toString(), el1.get(2).getAircraftId());
    assertEquals(entity3.getModelName(), el1.get(2).getModelName());
    assertEquals(entity4.getAircraftId().toString(), el1.get(3).getAircraftId());
    assertEquals(entity4.getModelName(), el1.get(3).getModelName());
    assertEquals(entity5.getAircraftId().toString(), el1.get(4).getAircraftId());
    assertNull(el1.get(4).getModelName());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機種名検索条件が完全一致する場合の機体情報一覧取得<br>
   * 条件: modelName="テスト機種名1"<br>
   * 結果: 該当する機体情報1件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_機種名完全一致() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_6();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_7();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_8();
    aircraftInfoRepository.save(entity3);
    AircraftInfoEntity entity4 = createAircraftInfoEntity_9();
    aircraftInfoRepository.save(entity4);
    AircraftInfoEntity entity5 = createAircraftInfoEntity_10();
    aircraftInfoRepository.save(entity5);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setModelName("テスト機種名1");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(1, el1.size());
    assertEquals(entity1.getAircraftId().toString(), el1.get(0).getAircraftId());
    assertEquals(entity1.getModelName().toString(), el1.get(0).getModelName());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機種名検索条件が部分一致する場合の機体情報一覧取得<br>
   * 条件: modelName="機種名"<br>
   * 結果: 部分一致する機体情報4件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_機種名部分一致() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_6();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_7();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_8();
    aircraftInfoRepository.save(entity3);
    AircraftInfoEntity entity4 = createAircraftInfoEntity_9();
    aircraftInfoRepository.save(entity4);
    AircraftInfoEntity entity5 = createAircraftInfoEntity_10();
    aircraftInfoRepository.save(entity5);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setModelName("機種名");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(4, el1.size());
    assertEquals(entity1.getAircraftId().toString(), el1.get(0).getAircraftId());
    assertEquals(entity1.getModelName().toString(), el1.get(0).getModelName());
    assertEquals(entity2.getAircraftId().toString(), el1.get(1).getAircraftId());
    assertEquals(entity2.getModelName(), el1.get(1).getModelName());
    assertEquals(entity3.getAircraftId().toString(), el1.get(2).getAircraftId());
    assertEquals(entity3.getModelName(), el1.get(2).getModelName());
    assertEquals(entity4.getAircraftId().toString(), el1.get(3).getAircraftId());
    assertEquals(entity4.getModelName(), el1.get(3).getModelName());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 機種名検索条件に一致する機体が存在しない場合の機体情報一覧取得<br>
   * 条件: modelName="存在しない機種名"<br>
   * 結果: 該当する機体情報0件が返却されること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_機種名_取得件数0件() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_6();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_7();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_8();
    aircraftInfoRepository.save(entity3);
    AircraftInfoEntity entity4 = createAircraftInfoEntity_9();
    aircraftInfoRepository.save(entity4);
    AircraftInfoEntity entity5 = createAircraftInfoEntity_10();
    aircraftInfoRepository.save(entity5);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setModelName("テスト機種名0");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(0, el1.size());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 公開可否フラグがnullで自事業者ユーザの場合の機体情報一覧取得<br>
   * 条件: publicFlag=null、自事業者ユーザ<br>
   * 結果: 自事業者の機体情報3件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_公開可否フラグ_null_自事業者_取得件数3件() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_3();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_4();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_5();
    aircraftInfoRepository.save(entity3);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(3, el1.size());
    assertEquals(entity1.getAircraftId().toString(), el1.get(0).getAircraftId());
    assertEquals(entity2.getAircraftId().toString(), el1.get(1).getAircraftId());
    assertEquals(entity3.getAircraftId().toString(), el1.get(2).getAircraftId());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 公開可否フラグがtrueで自事業者ユーザの場合の機体情報一覧取得<br>
   * 条件: publicFlag="true"、自事業者ユーザ<br>
   * 結果: 公開可の機体情報2件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_公開可否フラグ_true_自事業者_取得件数2件() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_3();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_4();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_5();
    aircraftInfoRepository.save(entity3);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setPublicFlag("true");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals(entity1.getAircraftId().toString(), el1.get(0).getAircraftId());
    assertEquals(entity2.getAircraftId().toString(), el1.get(1).getAircraftId());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 公開可否フラグがfalseで自事業者ユーザの場合の機体情報一覧取得<br>
   * 条件: publicFlag="false"、自事業者ユーザ<br>
   * 結果: 非公開の機体情報1件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_公開可否フラグ_false_自事業者_取得件数1件() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_3();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_4();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_5();
    aircraftInfoRepository.save(entity3);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setPublicFlag("false");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(1, el1.size());
    assertEquals(entity3.getAircraftId().toString(), el1.get(0).getAircraftId());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 公開可否フラグがnullで他事業者ユーザの場合の機体情報一覧取得<br>
   * 条件: publicFlag=null、他事業者ユーザ<br>
   * 結果: 公開可の機体情報2件のみ取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_公開可否フラグ_null_他事業者_取得件数2件() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_3();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_4();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_5();
    aircraftInfoRepository.save(entity3);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals(entity1.getAircraftId().toString(), el1.get(0).getAircraftId());
    assertEquals(entity2.getAircraftId().toString(), el1.get(1).getAircraftId());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 公開可否フラグがtrueで他事業者ユーザの場合の機体情報一覧取得<br>
   * 条件: publicFlag="true"、他事業者ユーザ<br>
   * 結果: 公開可の機体情報2件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_公開可否フラグ_true_他事業者_取得件数2件() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_3();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_4();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_5();
    aircraftInfoRepository.save(entity3);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setPublicFlag("true");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals(entity1.getAircraftId().toString(), el1.get(0).getAircraftId());
    assertEquals(entity2.getAircraftId().toString(), el1.get(1).getAircraftId());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 公開可否フラグがfalseで他事業者ユーザの場合の機体情報一覧取得<br>
   * 条件: publicFlag="false"、他事業者ユーザ<br>
   * 結果: 公開可の機体情報2件のみ取得できること（非公開は取得できない）<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_公開可否フラグ_false_他事業者_取得件数2件() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_3();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_4();
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_5();
    aircraftInfoRepository.save(entity3);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setPublicFlag("false");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals(entity1.getAircraftId().toString(), el1.get(0).getAircraftId());
    assertEquals(entity2.getAircraftId().toString(), el1.get(1).getAircraftId());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 公開可否フラグがtrue、機体名部分一致で自事業者ユーザの場合の機体情報一覧取得<br>
   * 条件: publicFlag="true"、aircraftName="検索機体名"、自事業者ユーザ<br>
   * 結果: 条件に一致する機体情報1件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_検索条件_公開可否フラグ_true_機体名_自事業者_取得件数2件() {
    // 準備：機体をリポジトリに設定
    AircraftInfoEntity entity1 = createAircraftInfoEntity_3();
    aircraftInfoRepository.save(entity1);
    AircraftInfoEntity entity2 = createAircraftInfoEntity_4();
    entity2.setAircraftName("検索機体名1");
    aircraftInfoRepository.save(entity2);
    AircraftInfoEntity entity3 = createAircraftInfoEntity_5();
    entity3.setAircraftName("検索機体名2");
    aircraftInfoRepository.save(entity3);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setPublicFlag("true");
    dto.setAircraftName("検索機体名");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(1, el1.size());
    assertEquals(entity2.getAircraftId().toString(), el1.get(0).getAircraftId());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: API-Key認証ユーザの場合の機体情報一覧取得<br>
   * 条件: dummyUserFlag=trueのAPI-Keyユーザ<br>
   * 結果: 公開可の機体情報2件が取得でき、ペイロード情報がnullであること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_APIKeyユーザ() {
    // 準備：機体エンティティをリポジトリに設定
    AircraftInfoEntity airEntity1 = createAircraftInfoEntity_3();
    aircraftInfoRepository.save(airEntity1);
    AircraftInfoEntity airEntity2 = createAircraftInfoEntity_4();
    aircraftInfoRepository.save(airEntity2);

    // テスト実施（ペイロード要否null）
    UserInfoDto userDto = new UserInfoDto();
    userDto.setDummyUserFlag(true);
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPayloadInfo(null);
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    assertNotNull(result);
    assertEquals(2, result.getData().size());
    assertFalse(result.getData().isEmpty());
    AircraftInfoSearchListElement el1 = result.getData().get(0);
    assertEquals(airEntity1.getAircraftId().toString(), el1.getAircraftId());
    assertEquals(airEntity1.getAircraftName(), el1.getAircraftName());
    assertNull(el1.getPayloadInfos());
    AircraftInfoSearchListElement el2 = result.getData().get(1);
    assertEquals(airEntity2.getAircraftId().toString(), el2.getAircraftId());
    assertEquals(airEntity2.getAircraftName(), el2.getAircraftName());
    assertNull(el2.getPayloadInfos());

    // isRequiredPayloadInfo が null の場合はペイロード取得処理が呼ばれないこと
    verify(payloadInfoRepository, times(0))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: 自事業者ユーザの場合の機体情報一覧取得<br>
   * 条件: 自事業者ユーザ<br>
   * 結果: 自事業者の機体情報2件が取得でき、ペイロード情報が空配列であること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_自事業者ユーザ() {
    // 準備：機体エンティティをリポジトリに設定
    AircraftInfoEntity airEntity1 = createAircraftInfoEntity_3();
    aircraftInfoRepository.save(airEntity1);
    AircraftInfoEntity airEntity2 = createAircraftInfoEntity_4();
    aircraftInfoRepository.save(airEntity2);

    // テスト実施（ペイロード要否null）
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setIsRequiredPayloadInfo(null);
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    assertNotNull(result);
    assertEquals(2, result.getData().size());
    AircraftInfoSearchListElement el1 = result.getData().get(0);
    assertEquals(airEntity1.getAircraftId().toString(), el1.getAircraftId());
    assertEquals(airEntity1.getAircraftName(), el1.getAircraftName());
    assertTrue(el1.getPayloadInfos().isEmpty());
    AircraftInfoSearchListElement el2 = result.getData().get(1);
    assertEquals(airEntity2.getAircraftId().toString(), el2.getAircraftId());
    assertEquals(airEntity2.getAircraftName(), el2.getAircraftName());
    assertTrue(el2.getPayloadInfos().isEmpty());

    // isRequiredPayloadInfo が null の場合はペイロード取得処理が呼ばれないこと
    verify(payloadInfoRepository, times(0))
        .findAllByAircraftIdInAndDeleteFlagFalse(any(List.class));
  }

  /**
   * メソッド名: getList<br>
   * 試験名: モデル検索（製造メーカーと型式番号3組）でペイロード・料金情報あり、自事業者の場合の機体情報一覧取得<br>
   * 条件: modelInfosリストに3組指定、isRequiredPayloadInfo="true"、isRequiredPriceInfo="true"、自事業者ユーザ<br>
   * 結果: 該当する機体情報3件がペイロード・料金情報と共に取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_3組指定_ペイロード情報料金情報要否true_自事業者() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();
    createModelSerchPayloadInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(3, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(2).getAircraftId());
    assertEquals("製造メーカー2", el1.get(2).getManufacturer());
    assertEquals("MD12345V1", el1.get(2).getModelNumber());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(1))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(1)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: モデル検索（製造メーカーと型式番号3組）でペイロード・料金情報あり、他事業者の場合の機体情報一覧取得<br>
   * 条件: modelInfosリストに3組指定、isRequiredPayloadInfo="true"、isRequiredPriceInfo="true"、他事業者ユーザ<br>
   * 結果: 公開可の機体情報2件がペイロード・料金情報と共に取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_3組指定_ペイロード情報料金情報要否true_他事業者() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();
    createModelSerchPayloadInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(1))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(1)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: モデル検索（製造メーカーと型式番号3組）でペイロード・料金情報なし、自事業者の場合の機体情報一覧取得<br>
   * 条件: modelInfosリストに3組指定、isRequiredPayloadInfo="false"、isRequiredPriceInfo="false"、自事業者ユーザ<br>
   * 結果: 該当する機体情報3件がペイロード・料金情報なしで取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_3組指定_ペイロード情報料金情報要否false_自事業者() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();
    createModelSerchPayloadInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    dto.setIsRequiredPayloadInfo("false");
    dto.setIsRequiredPriceInfo("false");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(3, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(2).getAircraftId());
    assertEquals("製造メーカー2", el1.get(2).getManufacturer());
    assertEquals("MD12345V1", el1.get(2).getModelNumber());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(0))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(0)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: モデル検索（製造メーカーと型式番号3組）でペイロード・料金情報なし、他事業者の場合の機体情報一覧取得<br>
   * 条件: modelInfosリストに3組指定、isRequiredPayloadInfo="false"、isRequiredPriceInfo="false"、他事業者ユーザ<br>
   * 結果: 公開可の機体情報2件がペイロード・料金情報なしで取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_3組指定_ペイロード情報料金情報要false_他事業者() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();
    createModelSerchPayloadInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    dto.setIsRequiredPayloadInfo("false");
    dto.setIsRequiredPriceInfo("false");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(0))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(0)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: モデル検索（製造メーカーと型式番号3組）でペイロード・料金情報要否null、自事業者の場合の機体情報一覧取得<br>
   * 条件: modelInfosリストに3組指定、isRequiredPayloadInfo=null、isRequiredPriceInfo=null、自事業者ユーザ<br>
   * 結果: 該当する機体情報3件が取得でき、ペイロード・料金情報取得処理が呼ばれないこと<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_3組指定_ペイロード情報料金情報要否null_自事業者() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();
    createModelSerchPayloadInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    dto.setIsRequiredPayloadInfo(null);
    dto.setIsRequiredPriceInfo(null);
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(3, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(2).getAircraftId());
    assertEquals("製造メーカー2", el1.get(2).getManufacturer());
    assertEquals("MD12345V1", el1.get(2).getModelNumber());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(0))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(0)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: モデル検索（製造メーカーと型式番号3組）でペイロード・料金情報要否null、他事業者の場合の機体情報一覧取得<br>
   * 条件: modelInfosリストに3組指定、isRequiredPayloadInfo=null、isRequiredPriceInfo=null、他事業者ユーザ<br>
   * 結果: 公開可の機体情報2件が取得でき、ペイロード・料金情報取得処理が呼ばれないこと<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_3組指定_ペイロード情報料金情報要null_他事業者() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();
    createModelSerchPayloadInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    dto.setIsRequiredPayloadInfo(null);
    dto.setIsRequiredPriceInfo(null);
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(0))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(0)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList<br>
   * 試験名: モデル検索（製造メーカーと型式番号3組）でペイロード・料金情報要否空文字、自事業者の場合の機体情報一覧取得<br>
   * 条件: modelInfosリストに3組指定、isRequiredPayloadInfo=""、isRequiredPriceInfo=""、自事業者ユーザ<br>
   * 結果: 該当する機体情報3件が取得でき、ペイロード・料金情報取得処理が呼ばれないこと<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_3組指定_ペイロード情報料金情報要否空文字_自事業者() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();
    createModelSerchPayloadInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    dto.setIsRequiredPayloadInfo("");
    dto.setIsRequiredPriceInfo("");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(3, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(2).getAircraftId());
    assertEquals("製造メーカー2", el1.get(2).getManufacturer());
    assertEquals("MD12345V1", el1.get(2).getModelNumber());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(0))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(0)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList_正常_モデル検索_3組指定_ペイロード情報料金情報要空文字_他事業者<br>
   * 試験名: モデル検索（製造メーカーと型式番号3組）でペイロード・料金情報要否空文字、他事業者の場合の機体情報一覧取得<br>
   * 条件: modelInfosリストに3組指定、isRequiredPayloadInfo=""、isRequiredPriceInfo=""、他事業者ユーザ<br>
   * 結果: 公開可の機体情報2件が取得でき、ペイロード・料金情報取得処理が呼ばれないこと<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_3組指定_ペイロード情報料金情報要空文字_他事業者() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();
    createModelSerchPayloadInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    dto.setIsRequiredPayloadInfo("");
    dto.setIsRequiredPriceInfo("");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", el1.get(1).getAircraftId());
    assertEquals("製造メーカー1", el1.get(1).getManufacturer());
    assertEquals("MD12345V2", el1.get(1).getModelNumber());
    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(0))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(0)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList_正常_モデル検索_3組指定1組null_ペイロード情報料金情報要否false_自事業者<br>
   * 試験名: モデル検索リストに3組指定して1組がnullの場合の機体情報一覧取得<br>
   * 条件:
   * modelInfosリストに3組指定、1組をnullに設定、isRequiredPayloadInfo="false"、isRequiredPriceInfo="false"、自事業者ユーザ
   * <br>
   * 結果: nullを除く2組に一致する機体情報2件が取得できること<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_3組指定1組null_ペイロード情報料金情報要否false_自事業者() {
    // 準備：機体をリポジトリに設定
    createModelSerchAircraftInfoEntity();
    createModelSerchPayloadInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    dto.getModelInfos().set(1, null);
    dto.setIsRequiredPayloadInfo("false");
    dto.setIsRequiredPriceInfo("false");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(2, el1.size());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf11", el1.get(0).getAircraftId());
    assertEquals("製造メーカー1", el1.get(0).getManufacturer());
    assertEquals("MD12345V1", el1.get(0).getModelNumber());
    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf13", el1.get(1).getAircraftId());
    assertEquals("製造メーカー2", el1.get(1).getManufacturer());
    assertEquals("MD12345V1", el1.get(1).getModelNumber());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(0))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(0)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList_正常_モデル検索_nullの要素のみ_ペイロード情報料金情報要否false_自事業者<br>
   * 試験名: モデル検索リストがnullの要素のみの場合の機体情報一覧取得<br>
   * 条件: modelInfosリストにnull要素のみ、isRequiredPayloadInfo="false"、isRequiredPriceInfo="false"、自事業者ユーザ
   * <br>
   * 結果: 該当する機体情報0件が返却されること（JaCoCo網羅用テスト）<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_nullの要素のみ_ペイロード情報料金情報要否false_自事業者() {
    // JaCoCo網羅用テスト（実際にはありえないケース)
    // 準備：機体をリポジトリに設定
    createModelSerchPayloadInfoEntity();
    createModelSerchAircraftInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    List<AircraftInfoModelInfoListElementReq> modelInfos = new ArrayList<>();
    modelInfos.add(null);
    dto.setModelInfos(modelInfos);
    dto.setIsRequiredPayloadInfo("false");
    dto.setIsRequiredPriceInfo("false");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(0, el1.size());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(0))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(0)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: getList_正常_モデル検索_空のリスト_ペイロード情報料金情報要否false_自事業者<br>
   * 試験名: モデル検索リストが空の場合の機体情報一覧取得<br>
   * 条件: modelInfosリストが空配列、isRequiredPayloadInfo="false"、isRequiredPriceInfo="false"、自事業者ユーザ<br>
   * 結果: 全ての機体情報5件が取得できること（JaCoCo網羅用テスト）<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_正常_モデル検索_空のリスト_ペイロード情報料金情報要否false_自事業者() {
    // JaCoCo網羅用テスト（実際にはありえないケース)
    // 準備：機体をリポジトリに設定
    createModelSerchPayloadInfoEntity();
    createModelSerchAircraftInfoEntity();

    // 料金情報検索サービスのモック設定
    PriceInfoSearchListResponseDto mockPriceResponse = createModelSerchPriceInfoResponseDto();
    when(priceInfoSearchListService.getPriceInfoList(any(PriceInfoSearchListRequestDto.class)))
        .thenReturn(mockPriceResponse);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    AircraftInfoSearchListRequestDto dto = createAircraftInfoModelSearchRequestDto();
    List<AircraftInfoModelInfoListElementReq> modelInfos = new ArrayList<>();
    dto.setModelInfos(modelInfos);
    dto.setIsRequiredPayloadInfo("false");
    dto.setIsRequiredPriceInfo("false");
    AircraftInfoSearchListResponseDto result = aircraftInfoServiceImpl.getList(dto, userDto);

    // 結果検証（準備した値と同じであること）
    assertNotNull(result);
    List<AircraftInfoSearchListElement> el1 = result.getData();
    assertEquals(5, el1.size());

    // 呼び出し回数確認
    verify(aircraftInfoRepository, times(1)).findAll(any(Specification.class));
    verify(priceInfoSearchListService, times(0))
        .getPriceInfoList(any(PriceInfoSearchListRequestDto.class));
    verify(payloadInfoRepository, times(0)).findAllByAircraftIdInAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 自事業者ユーザが公開可の機体の補足資料をダウンロード<br>
   * 条件: 自事業者ユーザ、公開可の機体に紐づく補足資料<br>
   * 結果: 補足資料情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void downloadFile_自事業者_紐づく機体公開可() {
    // リポジトリにデータ準備
    AircraftInfoEntity airEnt = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(airEnt);
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileInfoRepository.save(fileEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    FileInfoEntity result = aircraftInfoServiceImpl.downloadFile(fileId, userDto);

    assertEquals(result.getFileId(), fileEnt1.getFileId());
    assertEquals(result.getFilePhysicalName(), fileEnt1.getFilePhysicalName());
    verify(fileInfoRepository, times(1)).findByFileIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(0)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 自事業者ユーザが非公開の機体の補足資料をダウンロード<br>
   * 条件: 自事業者ユーザ、非公開の機体に紐づく補足資料<br>
   * 結果: 補足資料情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void downloadFile_自事業者_紐づく機体公開不可() {
    // リポジトリにデータ準備
    AircraftInfoEntity airEnt = createAircraftInfo_save_nml();
    airEnt.setPublicFlag(false);
    aircraftInfoRepository.save(airEnt);
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileInfoRepository.save(fileEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    FileInfoEntity result = aircraftInfoServiceImpl.downloadFile(fileId, userDto);

    assertEquals(result.getFileId(), fileEnt1.getFileId());
    assertEquals(result.getFilePhysicalName(), fileEnt1.getFilePhysicalName());
    verify(fileInfoRepository, times(1)).findByFileIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(0)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 他事業者ユーザが公開可の機体の補足資料をダウンロード<br>
   * 条件: 他事業者ユーザ、公開可の機体に紐づく補足資料<br>
   * 結果: 補足資料情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void downloadFile_他事業者_紐づく機体公開可() {
    // リポジトリにデータ準備
    AircraftInfoEntity airEnt = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(airEnt);
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileInfoRepository.save(fileEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    FileInfoEntity result = aircraftInfoServiceImpl.downloadFile(fileId, userDto);

    assertEquals(result.getFileId(), fileEnt1.getFileId());
    assertEquals(result.getFilePhysicalName(), fileEnt1.getFilePhysicalName());
    verify(fileInfoRepository, times(1)).findByFileIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 他事業者ユーザが非公開の機体の補足資料をダウンロード<br>
   * 条件: 他事業者ユーザ、非公開の機体に紐づく補足資料<br>
   * 結果: NotFoundExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadFile_他事業者_紐づく機体公開不可() {
    // リポジトリにデータ準備
    AircraftInfoEntity airEnt = createAircraftInfo_save_nml();
    airEnt.setPublicFlag(false);
    aircraftInfoRepository.save(airEnt);
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileInfoRepository.save(fileEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    Exception ex =
        assertThrows(
            NotFoundException.class, () -> aircraftInfoServiceImpl.downloadFile(fileId, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:7ed6002d-a68f-4e2d-a530-3cd281b5093e", ex.getMessage());
    verify(fileInfoRepository, times(1)).findByFileIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 論理削除済みの補足資料をダウンロードしようとする場合<br>
   * 条件: 補足資料が論理削除済み<br>
   * 結果: NotFoundExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadFile_パラメータで指定された補足資料情報あり_論理削除済() {
    // リポジトリにデータ準備
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileEnt1.setDeleteFlag(true);
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    Exception ex =
        assertThrows(
            NotFoundException.class, () -> aircraftInfoServiceImpl.downloadFile(fileId, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:7ed6002d-a68f-4e2d-a530-3cd281b5093e", ex.getMessage());

    verify(fileInfoRepository, times(1)).findByFileIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 存在しない補足資料IDでダウンロードしようとする場合<br>
   * 条件: 補足資料情報が存在しない<br>
   * 結果: NotFoundExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadFile_パラメータで指定された補足資料情報なし() {
    // リポジトリにデータ準備しない

    // テスト実施
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    Exception ex =
        assertThrows(
            NotFoundException.class, () -> aircraftInfoServiceImpl.downloadFile(fileId, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:7ed6002d-a68f-4e2d-a530-3cd281b5093e", ex.getMessage());

    verify(fileInfoRepository, times(1)).findByFileIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 他事業者ユーザが機体に紐づかない補足資料をダウンロードしようとする場合<br>
   * 条件: 他事業者ユーザ、補足資料に紐づく機体が存在しない<br>
   * 結果: NotFoundExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadFile_パラメータで指定された補足資料情報が紐づく機体なし_他事業者() {
    // リポジトリにデータ準備
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileInfoRepository.save(fileEnt1);

    // テスト実施
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    Exception ex =
        assertThrows(
            NotFoundException.class, () -> aircraftInfoServiceImpl.downloadFile(fileId, userDto));
    assertEquals("補足資料IDが見つかりません。補足資料ID:7ed6002d-a68f-4e2d-a530-3cd281b5093e", ex.getMessage());

    verify(fileInfoRepository, times(1)).findByFileIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: 補足資料情報取得時に想定外エラーが発生する場合<br>
   * 条件: 補足資料情報取得時にDuplicateKeyExceptionがスローされる<br>
   * 結果: DuplicateKeyExceptionが伝播されること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadFile_想定外エラー_補足資料情報取得時() {
    // リポジトリにデータ準備
    FileInfoEntity fileEnt1 = createFileInfoEntity_templete();
    fileInfoRepository.save(fileEnt1);
    clearInvocations(fileInfoRepository);

    // テスト実施
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    when(fileInfoRepository.findByFileIdAndDeleteFlagFalse(any()))
        .thenThrow(new DuplicateKeyException("上記以外の例外が発生"));
    Exception ex =
        assertThrows(
            DuplicateKeyException.class,
            () -> aircraftInfoServiceImpl.downloadFile(fileId, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());

    verify(fileInfoRepository, times(1)).findByFileIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: 非論理削除のペイロード情報をダウンロード<br>
   * 条件: ペイロード情報が存在し、論理削除されていない<br>
   * 結果: ペイロード情報が正しく取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void downloadPayloadFile_パラメータで指定されペイロード情報あり_非論理削除() {
    // リポジトリにデータ準備
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_10();
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    PayloadInfoEntity result =
        assertDoesNotThrow(() -> aircraftInfoServiceImpl.downloadPayloadFile(fileId, userDto));
    // 結果比較
    assertEquals(payloadEnt1.getPayloadId(), result.getPayloadId());
    assertEquals(payloadEnt1.getAircraftId(), result.getAircraftId());
    assertEquals(payloadEnt1.getPayloadNumber(), result.getPayloadNumber());
    assertEquals(payloadEnt1.getPayloadName(), result.getPayloadName());
    assertEquals(payloadEnt1.getPayloadDetailText(), result.getPayloadDetailText());
    assertArrayEquals(payloadEnt1.getFileData(), result.getFileData());
    assertEquals(payloadEnt1.getFileFormat(), result.getFileFormat());
    assertEquals(payloadEnt1.getFilePhysicalName(), result.getFilePhysicalName());
    assertArrayEquals(payloadEnt1.getFileData(), result.getFileData());
    assertEquals(payloadEnt1.getFileFormat(), result.getFileFormat());
    assertEquals(payloadEnt1.getOperatorId(), result.getOperatorId());
    assertEquals(payloadEnt1.getUpdateUserId(), result.getUpdateUserId());
    assertEquals(payloadEnt1.getCreateTime(), result.getCreateTime());
    assertEquals(payloadEnt1.getUpdateTime(), result.getUpdateTime());
    assertEquals(payloadEnt1.getDeleteFlag(), result.getDeleteFlag());

    verify(payloadInfoRepository, times(1)).findByPayloadIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: 論理削除済みのペイロード情報をダウンロードしようとする場合<br>
   * 条件: ペイロード情報が論理削除済み<br>
   * 結果: NotFoundExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadPayloadFile_パラメータで指定された補足資料情報あり_論理削除済() {
    // リポジトリにデータ準備
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_10();
    payloadEnt1.setDeleteFlag(true);
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    Exception ex =
        assertThrows(
            NotFoundException.class,
            () -> aircraftInfoServiceImpl.downloadPayloadFile(fileId, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:7ed6002d-a68f-4e2d-a530-3cd281b5093e", ex.getMessage());

    verify(payloadInfoRepository, times(1)).findByPayloadIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: 存在しないペイロードIDでダウンロードしようとする場合<br>
   * 条件: ペイロード情報が存在しない<br>
   * 結果: NotFoundExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadPayloadFile_パラメータで指定された補足資料情報なし() {
    // リポジトリにデータ準備しない

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    Exception ex =
        assertThrows(
            NotFoundException.class,
            () -> aircraftInfoServiceImpl.downloadPayloadFile(fileId, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:7ed6002d-a68f-4e2d-a530-3cd281b5093e", ex.getMessage());

    verify(payloadInfoRepository, times(1)).findByPayloadIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: 自事業者ユーザが公開可の機体のペイロード情報をダウンロード<br>
   * 条件: 自事業者ユーザ、公開可の機体に紐づくペイロード情報<br>
   * 結果: ペイロード情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void downloadPayloadFile_自事業者_紐づく機体公開可() {
    // リポジトリにデータ準備
    AircraftInfoEntity airEnt = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(airEnt);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadInfoRepository.save(payloadEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";
    PayloadInfoEntity result = aircraftInfoServiceImpl.downloadPayloadFile(payloadId, userDto);

    assertEquals(payloadEnt1.getPayloadId(), result.getPayloadId());
    assertEquals(payloadEnt1.getAircraftId(), result.getAircraftId());
    assertEquals(payloadEnt1.getPayloadNumber(), result.getPayloadNumber());
    assertEquals(payloadEnt1.getPayloadName(), result.getPayloadName());
    assertEquals(payloadEnt1.getPayloadDetailText(), result.getPayloadDetailText());
    assertArrayEquals(payloadEnt1.getFileData(), result.getFileData());
    assertEquals(payloadEnt1.getFileFormat(), result.getFileFormat());
    assertEquals(payloadEnt1.getFilePhysicalName(), result.getFilePhysicalName());
    assertArrayEquals(payloadEnt1.getFileData(), result.getFileData());
    assertEquals(payloadEnt1.getFileFormat(), result.getFileFormat());
    assertEquals(payloadEnt1.getOperatorId(), result.getOperatorId());
    assertEquals(payloadEnt1.getUpdateUserId(), result.getUpdateUserId());
    assertEquals(payloadEnt1.getCreateTime(), result.getCreateTime());
    assertEquals(payloadEnt1.getUpdateTime(), result.getUpdateTime());
    assertEquals(payloadEnt1.getDeleteFlag(), result.getDeleteFlag());
    verify(payloadInfoRepository, times(1)).findByPayloadIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(0)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: 自事業者ユーザが非公開の機体のペイロード情報をダウンロード<br>
   * 条件: 自事業者ユーザ、非公開の機体に紐づくペイロード情報<br>
   * 結果: ペイロード情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void downloadPayloadFile_自事業者_紐づく機体公開不可() {
    // リポジトリにデータ準備
    AircraftInfoEntity airEnt = createAircraftInfo_save_nml();
    airEnt.setPublicFlag(false);
    aircraftInfoRepository.save(airEnt);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadInfoRepository.save(payloadEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";
    PayloadInfoEntity result = aircraftInfoServiceImpl.downloadPayloadFile(payloadId, userDto);

    assertEquals(payloadEnt1.getPayloadId(), result.getPayloadId());
    assertEquals(payloadEnt1.getAircraftId(), result.getAircraftId());
    assertEquals(payloadEnt1.getPayloadNumber(), result.getPayloadNumber());
    assertEquals(payloadEnt1.getPayloadName(), result.getPayloadName());
    assertEquals(payloadEnt1.getPayloadDetailText(), result.getPayloadDetailText());
    assertArrayEquals(payloadEnt1.getFileData(), result.getFileData());
    assertEquals(payloadEnt1.getFileFormat(), result.getFileFormat());
    assertEquals(payloadEnt1.getFilePhysicalName(), result.getFilePhysicalName());
    assertArrayEquals(payloadEnt1.getFileData(), result.getFileData());
    assertEquals(payloadEnt1.getFileFormat(), result.getFileFormat());
    assertEquals(payloadEnt1.getOperatorId(), result.getOperatorId());
    assertEquals(payloadEnt1.getUpdateUserId(), result.getUpdateUserId());
    assertEquals(payloadEnt1.getCreateTime(), result.getCreateTime());
    assertEquals(payloadEnt1.getUpdateTime(), result.getUpdateTime());
    assertEquals(payloadEnt1.getDeleteFlag(), result.getDeleteFlag());
    verify(payloadInfoRepository, times(1)).findByPayloadIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(0)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: 他事業者ユーザが公開可の機体のペイロード情報をダウンロード<br>
   * 条件: 他事業者ユーザ、公開可の機体に紐づくペイロード情報<br>
   * 結果: ペイロード情報が取得できること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void downloadPayloadFile_他事業者_紐づく機体公開可() {
    // リポジトリにデータ準備
    AircraftInfoEntity airEnt = createAircraftInfo_save_nml();
    aircraftInfoRepository.save(airEnt);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadInfoRepository.save(payloadEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";
    PayloadInfoEntity result = aircraftInfoServiceImpl.downloadPayloadFile(payloadId, userDto);

    assertEquals(payloadEnt1.getPayloadId(), result.getPayloadId());
    assertEquals(payloadEnt1.getAircraftId(), result.getAircraftId());
    assertEquals(payloadEnt1.getPayloadNumber(), result.getPayloadNumber());
    assertEquals(payloadEnt1.getPayloadName(), result.getPayloadName());
    assertEquals(payloadEnt1.getPayloadDetailText(), result.getPayloadDetailText());
    assertArrayEquals(payloadEnt1.getFileData(), result.getFileData());
    assertEquals(payloadEnt1.getFileFormat(), result.getFileFormat());
    assertEquals(payloadEnt1.getFilePhysicalName(), result.getFilePhysicalName());
    assertArrayEquals(payloadEnt1.getFileData(), result.getFileData());
    assertEquals(payloadEnt1.getFileFormat(), result.getFileFormat());
    assertEquals(payloadEnt1.getOperatorId(), result.getOperatorId());
    assertEquals(payloadEnt1.getUpdateUserId(), result.getUpdateUserId());
    assertEquals(payloadEnt1.getCreateTime(), result.getCreateTime());
    assertEquals(payloadEnt1.getUpdateTime(), result.getUpdateTime());
    assertEquals(payloadEnt1.getDeleteFlag(), result.getDeleteFlag());
    verify(payloadInfoRepository, times(1)).findByPayloadIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: 他事業者ユーザが非公開の機体のペイロード情報をダウンロード<br>
   * 条件: 他事業者ユーザ、非公開の機体に紐づくペイロード情報<br>
   * 結果: NotFoundExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadPayloadFile_他事業者_紐づく機体公開不可() {
    // リポジトリにデータ準備
    AircraftInfoEntity airEnt = createAircraftInfo_save_nml();
    airEnt.setPublicFlag(false);
    aircraftInfoRepository.save(airEnt);
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadInfoRepository.save(payloadEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    String payloadId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";
    Exception ex =
        assertThrows(
            NotFoundException.class,
            () -> aircraftInfoServiceImpl.downloadPayloadFile(payloadId, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01", ex.getMessage());
    verify(payloadInfoRepository, times(1)).findByPayloadIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: 他事業者ユーザが機体に紐づかないペイロード情報をダウンロードしようとする場合<br>
   * 条件: 他事業者ユーザ、ペイロード情報に紐づく機体が存在しない<br>
   * 結果: NotFoundExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadPayloadFile_パラメータで指定されたペイロード情報が紐づく機体なし_他事業者() {
    // リポジトリにデータ準備
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_1();
    payloadInfoRepository.save(payloadEnt1);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OtherOperator();
    String fileId = "1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01";
    Exception ex =
        assertThrows(
            NotFoundException.class,
            () -> aircraftInfoServiceImpl.downloadPayloadFile(fileId, userDto));
    assertEquals("ペイロードIDが見つかりません。ペイロードID:1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01", ex.getMessage());

    verify(payloadInfoRepository, times(1)).findByPayloadIdAndDeleteFlagFalse(any());
    verify(aircraftInfoRepository, times(1)).findByAircraftIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: downloadPayloadFile<br>
   * 試験名: ペイロード情報取得時に想定外エラーが発生する場合<br>
   * 条件: ペイロード情報取得時にDuplicateKeyExceptionがスローされる<br>
   * 結果: DuplicateKeyExceptionが伝播されること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadPayloadFile_想定外エラー_補足資料情報取得時() {
    // リポジトリにデータ準備
    PayloadInfoEntity payloadEnt1 = createPayloadInfoEntity_10();
    payloadInfoRepository.save(payloadEnt1);
    clearInvocations(payloadInfoRepository);

    // テスト実施
    UserInfoDto userDto = createUserInfoDto_OwnOperator();
    String fileId = "7ed6002d-a68f-4e2d-a530-3cd281b5093e";
    when(payloadInfoRepository.findByPayloadIdAndDeleteFlagFalse(any()))
        .thenThrow(new DuplicateKeyException("上記以外の例外が発生"));
    Exception ex =
        assertThrows(
            DuplicateKeyException.class,
            () -> aircraftInfoServiceImpl.downloadPayloadFile(fileId, userDto));
    assertEquals("上記以外の例外が発生", ex.getMessage());

    verify(payloadInfoRepository, times(1)).findByPayloadIdAndDeleteFlagFalse(any());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報がnullの場合のデコード処理<br>
   * 条件: fileInfos=null<br>
   * 結果: 処理が成功し、fileInfosがnullのままであること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_補足資料情報なし_null() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosokuNull();
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(null, dto.getFileInfos());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報が空リストの場合のデコード処理<br>
   * 条件: fileInfos=空リスト<br>
   * 結果: 処理が成功し、サイズが0のままであること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_補足資料情報なし_空のリスト() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_empList();
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(0, dto.getFileInfos().size());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報1件で要素がnullの場合のデコード処理<br>
   * 条件: fileInfosリストの要素がnull<br>
   * 結果: 処理が成功し、要素がnullのままであること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_補足資料情報あり_1件_要素null() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().set(0, null);
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getFileInfos().size());
    assertEquals(null, dto.getFileInfos().get(0));
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報1件でファイルデータが空文字の場合のデコード処理<br>
   * 条件: fileData=""<br>
   * 結果: 処理が成功し、ファイルデータの長さが0であること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_補足資料情報あり_1件_ファイルデータが空() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setFileBinary(null);
    dto.getFileInfos().get(0).setFileData("");
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getFileInfos().size());
    assertEquals(0, dto.getFileInfos().get(0).getFileData().length());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報1件でファイルデータがnullの場合のデコード処理<br>
   * 条件: fileData=null<br>
   * 結果: 処理が成功し、fileBinaryがnullのままであること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_補足資料情報あり_1件_ファイルデータがnull() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setFileBinary(null);
    dto.getFileInfos().get(0).setFileData(null);
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getFileInfos().size());
    assertEquals(null, dto.getFileInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報1件でファイルデータ長が0の場合のデコード処理<br>
   * 条件: fileData="data:text/plain;base64,"<br>
   * 結果: 処理が成功し、fileBinaryが空バイト配列であること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_補足資料情報あり_1件_ファイルデータ長が0() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setFileBinary(null);
    dto.getFileInfos().get(0).setFileData("data:text/plain;base64,");
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getFileInfos().size());
    byte[] byteData = {};
    assertArrayEquals(byteData, dto.getFileInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報1件でサポート外のMIMEタイプの場合のデコード処理<br>
   * 条件: fileData="data:application/zip;base64,..."<br>
   * 結果: 処理が成功し、fileBinaryがnullのままであること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeFileData_補足資料情報あり_1件_サポート外のMIME() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setFileBinary(null);
    dto.getFileInfos()
        .get(0)
        .setFileData("data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getFileInfos().size());
    assertEquals(null, dto.getFileInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報1件でサポート対象のMIMEタイプ、1バイトの場合のデコード処理<br>
   * 条件: fileData="data:text/plain;base64,MQ=="<br>
   * 結果: 処理が成功し、fileBinaryが正しくデコードされること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeFileData_補足資料情報あり_1件_サポート対象のMIME_1バイト() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setFileBinary(null);
    dto.getFileInfos().get(0).setFileData("data:text/plain;base64,MQ==");
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getFileInfos().size());
    byte[] byteData = {49};
    assertArrayEquals(byteData, dto.getFileInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報1件でサポート対象のMIMEタイプ、2バイト以上の場合のデコード処理<br>
   * 条件: fileDataに2バイト以上のデータが設定されている<br>
   * 結果: 処理が成功し、fileBinaryが正しくデコードされること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeFileData_補足資料情報あり_1件_サポート対象のMIME_2バイト以上() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setFileBinary(null);
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getFileInfos().size());
    byte[] byteData = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(byteData, dto.getFileInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: 補足資料情報3件でサポート対象のMIMEタイプの場合のデコード処理<br>
   * 条件: fileInfosリストに3件の補足資料情報が設定されている<br>
   * 結果: 処理が成功し、全てのfileBinaryが正しくデコードされること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeFileData_補足資料情報あり_3件_サポート対象のMIME() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku3();
    dto.getFileInfos().get(0).setFileBinary(null);
    dto.getFileInfos().get(1).setFileBinary(null);
    dto.getFileInfos().get(2).setFileBinary(null);
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(3, dto.getFileInfos().size());
    byte[] byteData1 = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    byte[] byteData2 = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    byte[] byteData3 = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(byteData1, dto.getFileInfos().get(0).getFileBinary());
    assertArrayEquals(byteData2, dto.getFileInfos().get(1).getFileBinary());
    assertArrayEquals(byteData3, dto.getFileInfos().get(2).getFileBinary());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: base64ではない文字列で補足資料のデコード処理を実施する場合<br>
   * 条件: fileData="data:text/plain;base64,デコード不可"<br>
   * 結果: IllegalArgumentExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadFile_想定外エラー_デコード処理をbase64ではない文字列で実施() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_hosoku1();
    dto.getFileInfos().get(0).setFileBinary(null);
    dto.getFileInfos().get(0).setFileData("data:text/plain;base64,デコード不可");

    Exception ex =
        assertThrows(
            IllegalArgumentException.class, () -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals("Illegal base64 character 3f", ex.getMessage());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報がnullの場合のデコード処理<br>
   * 条件: payloadInfos=null<br>
   * 結果: 処理が成功し、payloadInfosがnullのままであること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_ペイロード情報なし_null() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadNull();
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(null, dto.getPayloadInfos());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報が空リストの場合のデコード処理<br>
   * 条件: payloadInfos=空リスト<br>
   * 結果: 処理が成功し、サイズが0のままであること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_ペイロード情報なし_空のリスト() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payloadEmpList();
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(0, dto.getPayloadInfos().size());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報1件で要素がnullの場合のデコード処理<br>
   * 条件: payloadInfosリストの要素がnull<br>
   * 結果: 処理が成功し、要素がnullのままであること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_ペイロード情報あり_1件_要素null() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().set(0, null);
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getPayloadInfos().size());
    assertEquals(null, dto.getPayloadInfos().get(0));
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報1件でファイルデータがnullの場合のデコード処理<br>
   * 条件: fileData=null<br>
   * 結果: 処理が成功し、fileBinaryがnullのままであること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_ペイロード情報あり_1件_ファイルデータがnull() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setFileBinary(null);
    dto.getPayloadInfos().get(0).setFileData(null);
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getPayloadInfos().size());
    assertEquals(null, dto.getPayloadInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報1件でファイルデータが空文字の場合のデコード処理<br>
   * 条件: fileData=""<br>
   * 結果: 処理が成功し、ファイルデータの長さが0であること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_ペイロード情報あり_1件_ファイルデータが空() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setFileBinary(null);
    dto.getPayloadInfos().get(0).setFileData("");
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getPayloadInfos().size());
    assertEquals(0, dto.getPayloadInfos().get(0).getFileData().length());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報1件でファイルデータ長が0の場合のデコード処理<br>
   * 条件: fileData="data:text/plain;base64,"<br>
   * 結果: 処理が成功し、fileBinaryが空バイト配列であること<br>
   * テストパターン：境界値<br>
   */
  @Test
  void decodeFileData_ペイロード情報あり_1件_ファイルデータ長が0() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setFileBinary(null);
    dto.getPayloadInfos().get(0).setFileData("data:text/plain;base64,");
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getPayloadInfos().size());
    byte[] byteData = {};
    assertArrayEquals(byteData, dto.getPayloadInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報1件でサポート外のMIMEタイプの場合のデコード処理<br>
   * 条件: fileData="data:application/zip;base64,..."<br>
   * 結果: 処理が成功し、fileBinaryがnullのままであること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeFileData_ペイロード情報あり_1件_サポート外のMIME() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setFileBinary(null);
    dto.getPayloadInfos()
        .get(0)
        .setFileData("data:application/zip;base64,MeijnOi2s+izh+aWmeaDheWgseOBruWGheWuuQ==");
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getPayloadInfos().size());
    assertEquals(null, dto.getPayloadInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報1件でサポート対象のMIMEタイプ、1バイトの場合のデコード処理<br>
   * 条件: fileData="data:text/plain;base64,MQ=="<br>
   * 結果: 処理が成功し、fileBinaryが正しくデコードされること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeFileData_ペイロード情報あり_1件_サポート対象のMIME_1バイト() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setFileBinary(null);
    dto.getPayloadInfos().get(0).setFileData("data:text/plain;base64,MQ==");
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getPayloadInfos().size());
    byte[] byteData = {49};
    assertArrayEquals(byteData, dto.getPayloadInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報1件でサポート対象のMIMEタイプ、2バイト以上の場合のデコード処理<br>
   * 条件: fileDataに2バイト以上のデータが設定されている<br>
   * 結果: 処理が成功し、fileBinaryが正しくデコードされること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeFileData_ペイロード情報あり_1件_サポート対象のMIME_2バイト以上() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setFileBinary(null);
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(1, dto.getPayloadInfos().size());
    byte[] byteData = {
      -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(byteData, dto.getPayloadInfos().get(0).getFileBinary());
  }

  /**
   * メソッド名: decodeFileData<br>
   * 試験名: ペイロード情報3件でサポート対象のMIMEタイプの場合のデコード処理<br>
   * 条件: payloadInfosリストに3件のペイロード情報が設定されている<br>
   * 結果: 処理が成功し、全てのfileBinaryが正しくデコードされること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void decodeFileData_ペイロード情報あり_3件_サポート対象のMIME() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload3();
    dto.getPayloadInfos().get(0).setFileBinary(null);
    dto.getPayloadInfos().get(1).setFileBinary(null);
    dto.getPayloadInfos().get(2).setFileBinary(null);
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals(3, dto.getPayloadInfos().size());
    byte[] byteData1 = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    byte[] byteData2 = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    byte[] byteData3 = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    assertArrayEquals(byteData1, dto.getPayloadInfos().get(0).getFileBinary());
    assertArrayEquals(byteData2, dto.getPayloadInfos().get(1).getFileBinary());
    assertArrayEquals(byteData3, dto.getPayloadInfos().get(2).getFileBinary());
  }

  /**
   * メソッド名: downloadFile<br>
   * 試験名: base64ではない文字列でペイロード情報のデコード処理を実施する場合<br>
   * 条件: fileData="data:text/plain;base64,デコード不可"<br>
   * 結果: IllegalArgumentExceptionがスローされること<br>
   * テストパターン：異常系<br>
   */
  @Test
  void downloadFile_ペイロード情報想定外エラー_デコード処理をbase64ではない文字列で実施() {
    AircraftInfoRequestDto dto = createAircraftInfoRequestDto_payload1();
    dto.getPayloadInfos().get(0).setFileBinary(null);
    dto.getPayloadInfos().get(0).setFileData("data:text/plain;base64,デコード不可");

    Exception ex =
        assertThrows(
            IllegalArgumentException.class, () -> aircraftInfoServiceImpl.decodeFileData(dto));
    assertEquals("Illegal base64 character 3f", ex.getMessage());
  }

  /** データテンプレート ■機体情報Entity(AircraftInfoEntity) 機体情報Entity1(save結果_正常) */
  private AircraftInfoEntity createAircraftInfo_save_nml() {
    AircraftInfoEntity ret = new AircraftInfoEntity();
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setModelNumber("M12345678");
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
    ret.setImageFormat("png");
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** データテンプレート ■補足資料情報Entity(FileInfoEntity) 補足資料情報Entity5(save結果_正常) */
  private FileInfoEntity createFileInfoEntity_save_nml() {
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
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** データテンプレート ■補足資料情報Entity(FileInfoEntity) 補足資料情報Entity7(結果確認用テンプレート) */
  private FileInfoEntity createFileInfoEntity_templete() {
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
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** 複数件確認用の補足資料情報のEntity1を作成 */
  private FileInfoEntity createFileInfoEntity_n1() {
    FileInfoEntity ret = new FileInfoEntity();
    ret.setFileId(UUID.fromString("d3d036e1-a0c6-4939-beb8-98528e26ef6e"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setFileNumber(1);
    ret.setFileLogicalName("1補足資料論理名補足資料論理名");
    ret.setFilePhysicalName("1hosoku.txt");
    byte[] fileByetes = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat("text/plain");
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** 複数件確認用の補足資料情報のEntity2を作成 */
  private FileInfoEntity createFileInfoEntity_n2() {
    FileInfoEntity ret = new FileInfoEntity();
    ret.setFileId(UUID.fromString("1ddd2d26-a695-4123-b7fd-8372656ae56e"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setFileNumber(2);
    ret.setFileLogicalName("2補足資料論理名補足資料論理名");
    ret.setFilePhysicalName("2hosoku.txt");
    byte[] fileByetes = {
      50, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat("text/plain");
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** 複数件確認用の補足資料情報のEntity3を作成 */
  private FileInfoEntity createFileInfoEntity_n3() {
    FileInfoEntity ret = new FileInfoEntity();
    ret.setFileId(UUID.fromString("c738a694-4af2-4656-a058-6d19462b41e4"));
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ret.setFileNumber(3);
    ret.setFileLogicalName("3補足資料論理名補足資料論理名");
    ret.setFilePhysicalName("3hosoku.txt");
    byte[] fileByetes = {
      51, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    ret.setFileData(fileByetes);
    ret.setFileFormat("text/plain");
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料1つ) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_hosoku1() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setModelNumber("M12345678");
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
    ret.setModelNumber("M12345678");
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
    ret.setModelNumber("M12345678");
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
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPUAAABuCAYAAAD/EFXlAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAcdSURBVHhe7d0hVOw6FwXgkUgkEolEIpFIJHIkEolDIpFIJBKJRCKRSCQSiexd+76b+84c0rntNE2Tnf2tlbX+978n5nSyJ2mSllUnIlRW/v8Qkbop1CJkFGoRMgq1CBmFWoSMQi1CRqEWIaNQi5BRqEXIKNQiZBRqETIKtQgZhVqEjEItQkahFiGjUIuQUailWc/Pz93p6Wn38PDg/1XVFGpp1sHBQbdarbq9vT3/r6qmUEuzEOjQmHBVIzKCQi1CRqEWIaNQi5BpPtRY/j88PNy4ELU0fG62bQuZzvYRJoOrqTXQobFtW8h0tn8wGVzN7e3tj6DU1C4vL31J0jjbP5hwVSMygkItQkahFiGjUIuQUahlJzVsBeLz3d/f+49Oz14DJlzVFKj0QNt2c3PjPz41WzsTrmoKVONWYCuHdWzNTLiqkdG+v7+7s7OzH8Fu4bCOQi20EOz1ev0j2OxYa+WqRiZj7egxrLVyVSOTsXb0GNZauaqRyVg7egxrrVzVyGSsHT2GtVauamQS7FOzdvQY1lqTVVPKySl8houLi98npN7f3/3HlB4+0LiG7Gy9TJJVU0KgpzbUcH19/XuLpyU+0Ni3buEa2JqZJKumxpNTfe3o6Kh7fX31JVJqNdBg62bCVU3XdW9vb93d3V13fn7e7e/v/wjs2MZ8ZLLlQIOtnQlXNRMhvLEfAsYjk60HGhTqRnx+fv44C832fjPMZFoPNCjUQuHp6UmB/kOhrgDrnyZNBesNuJUIHRnXqtVAg0JdAdY/TZrCx8fHxrYj/vfX15f/z5qiUFeA9UuaCqPx8fHx32uDxUAdzOHtLzTVoOOyfklTYXsvXBfMYl5eXvx/0iTW/kJTjV3Rxagk/7m6utrovC2+YNAKx5n9CUgmFNVglA730+q4//NbVwh463yYQwvPDGCRFduaNaMI9ePj498vB+FueUU38CM0puAy/DgzZnt4DqDG48IUobaLQBidWoYfNHsPHTqofug2+f36bQ0LizX1q+pDbb8cLAK13HkxbbQ/cGg4XNL61lUfe50wImNk9tfPtlrei159qHGAIlz0lu8ZcbDE3y+yHW9NzV4rCz+OuLfGPbZ/FqCGYFcdanTkcLExSte+wLErrOj6zlfTdHEpfaG2MPPzzwKUHuz+aipg7x1bHZUwotgOhx833JLIvw0JNdQW7O3VFAwnouxFxqjdGh9orPy3eB12NTTUUFOw/11NoeyWTYvbNT7QWODB+W4ZbkyoIRZszBBLu+0bVk1hcBHt00atjU6xQGuFe7yxoYZYsNEXMciUEu7h1RTEjtKtHQlVoNPZJdQQOwsQwl3CiyvHVVMIu3XT0qKQAp3WrqEO0Pdi+9pLv7hyt2oWhClOuHjYxmmFP96oQE83NdRBX7iXmpJPq2YB9gQZDp6ww2OS+OUfE+gxf1ih1Xedg70OKfS9uDJc51wPGqWpJiP7FkzmE2T4hceJJt85Tk5OtgYahgbatqWnjEuw9acSe3GlbTm2wdJVk4ldoMDTWYxwGsz/4uOfMQUfwk/Vx7SlpoxLsHWnhhmWPcJs29DvcVfpq5mR38pifCWPfx83GkbsuYIWmzKWtkUzF1vznPw22Nzv0Ju3msTYt7Jwb2s7GqbEOV491DdlZA93rlCDf93WnH/5Zf5qEvGjNNNWFmYcuFe2X/oS7+PumzKyhjtnqMH233Bd55CnmgRYR2ncX/kve4lAW9vCPff9YE65Q+3XOuZ6CClPNROhk7GN0rHRubTQ9O2/5ljBzSF3qHMpvho8pGBfKogg1AwjcGx0Rl2lLvzFwo2Z05KziRQU6gWg09jRDOGu+b4udpCktNG5j1/BRat9b1uhXsB6vd7o/LV2oG0HSUodnWP6HmSo9USaQp2Zf2d1riN2KeGRUExT/T7wmIMkJYrtbeMHqrbnuRXqjPzTSHOtEs4hBLnvqOacB0lyiu1tI+g1LWIq1JnYF/OjLb29MxRG3r4go+U6SJKbn1Gh4baphtsKhToTu9JdQ6BjW1OhoRbMMhjDbGGtw35voeG6YNZV6neoUM8s9rhgqZ0B947orBiR/NZUK0H28ORY7MCKbXMejdyFQj0zH+iSLrQNcexzotWyNTU3jNqxHzt7nUpRYl9LoZhqch2hGyI2a9jWatuaygEjN3Ys/L78kt+rZz8XE65qEondH9qG0Qb3+/ghqnXvXBTqpijEbVCoG2LvB4WXQt2QcH9f0v2fpKdQi5BRqEXIKNQiZOzaCdNhIYVammUfh8W5hFJPMI6lUEuz8KSZfYSUZWFUoZam2cd8SzrCOoVCLc1jWzDjqEJkAoVahIxCLULGbm0xvNNcoZbm2b/+glb7c/EKtTTPv9O89lVwhVrkT7DDYRS8uaVmCrUIGYVahIxCLUJGoRYho1CLkFGoRcgo1CJkFGoRMgq1CJlfMf/5w1FFkZsAAAAASUVORK5CYII=");
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
    ret.setModelNumber("M12345678");
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

  private AircraftInfoEntity createAircraftInfoEntity_3() {
    AircraftInfoEntity ent = new AircraftInfoEntity();
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf23"));
    ent.setAircraftName("機体名1");
    ent.setManufacturer("製造メーカー1");
    ent.setManufacturingNumber("N12345678");
    ent.setAircraftType(1);
    ent.setMaxTakeoffWeight(99.0);
    ent.setBodyWeight(88.0);
    ent.setMaxFlightSpeed(77.0);
    ent.setMaxFlightTime(66.0);
    ent.setLat(55.0);
    ent.setLon(44.0);
    ent.setCertification(Boolean.TRUE);
    ent.setDipsRegistrationCode("DIPS_1234");
    ent.setOwnerType(1);
    ent.setOwnerId(UUID.fromString("054bb198-ab4c-4bb1-a27c-78af8e495f7a"));
    ent.setPublicFlag(true);

    // 画像データ（共通ユーティリティメソッド createImageBytes() があればそれを使う）
    ent.setImageBinary(createImageBytes());
    ent.setImageFormat("png");

    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now);
    ent.setUpdateTime(now);
    ent.setDeleteFlag(Boolean.FALSE);

    return ent;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(ペイロード情報20) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_reg_payload20() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftName("機体名");
    ret.setLat(Double.valueOf(11));
    ret.setLon(Double.valueOf(22));
    ret.setCertification(Boolean.TRUE);
    ret.setPublicFlag(true);

    List<AircraftInfoPayloadInfoListElementReq> payloadInfos = new ArrayList<>();
    for (int i = 1; i <= 20; i++) {
      AircraftInfoPayloadInfoListElementReq p = new AircraftInfoPayloadInfoListElementReq();
      p.setProcessingType(1);
      p.setPayloadName("ペイロード名" + i);
      payloadInfos.add(p);
    }
    ret.setPayloadInfos(payloadInfos);

    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ(補足資料1つ) */
  private AircraftInfoRequestDto createAircraftInfoRequestDto_hosoku1_payload1_price1() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setModelNumber("M12345678");
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
    file1.setFileData("data:text/plain;base64,6KOc6Laz6LOH5paZ5oOF5aCx44Gu5YaF5a65");
    byte[] file1Byetes = {
      49, -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
      -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
    };
    file1.setFileBinary(file1Byetes);
    fileInfos.add(file1);
    ret.setFileInfos(fileInfos);

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
    payload1.setFileBinary(file1Byetes);
    payloadInfos.add(payload1);
    ret.setPayloadInfos(payloadInfos);

    List<PriceInfoRequestDto> priceInfos = new ArrayList<>();
    PriceInfoRequestDto price1 = new PriceInfoRequestDto();
    price1.setProcessingType(1);
    price1.setPriceId("");
    price1.setResourceId("リソースID");
    price1.setResourceType(1);
    price1.setPrimaryRouteOperatorId("主管航路事業者ID");
    price1.setPriceType(4);
    price1.setPricePerUnit(1);
    price1.setPrice(1000);
    price1.setEffectiveStartTime("2025-11-13T10:00:00Z");
    price1.setEffectiveEndTime("2025-11-13T11:00:00Z");
    price1.setOperatorId("ope01");
    price1.setPriority(1);
    price1.setRowNumber(1);
    priceInfos.add(price1);
    ret.setPriceInfos(priceInfos);

    return ret;
  }

  /** テスト用: 機体情報 Entity (項番4) を作成して返す */
  private AircraftInfoEntity createAircraftInfoEntity_4() {
    AircraftInfoEntity ent = new AircraftInfoEntity();
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf24"));
    ent.setAircraftName("機体名2");
    ent.setManufacturer("製造メーカー2");
    ent.setManufacturingNumber("N12345678");
    ent.setAircraftType(1);
    ent.setMaxTakeoffWeight(99.0);
    ent.setBodyWeight(88.0);
    ent.setMaxFlightSpeed(77.0);
    ent.setMaxFlightTime(66.0);
    ent.setLat(55.0);
    ent.setLon(44.0);
    ent.setCertification(Boolean.TRUE);
    ent.setDipsRegistrationCode("DIPS_1234");
    ent.setOwnerType(1);
    ent.setOwnerId(UUID.fromString("054bb198-ab4c-4bb1-a27c-78af8e495f7a"));
    ent.setPublicFlag(true);

    ent.setImageBinary(createImageBytes());
    ent.setImageFormat("png");

    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now);
    ent.setUpdateTime(now);
    ent.setDeleteFlag(Boolean.FALSE);

    return ent;
  }

  /** テスト用: 機体情報 Entity (項番5) を作成して返す */
  private AircraftInfoEntity createAircraftInfoEntity_5() {
    AircraftInfoEntity ent = new AircraftInfoEntity();
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf25"));
    ent.setAircraftName("機体名3");
    ent.setManufacturer("製造メーカー3");
    ent.setManufacturingNumber("N12345678");
    ent.setAircraftType(1);
    ent.setMaxTakeoffWeight(99.0);
    ent.setBodyWeight(88.0);
    ent.setMaxFlightSpeed(77.0);
    ent.setMaxFlightTime(66.0);
    ent.setLat(55.0);
    ent.setLon(44.0);
    ent.setCertification(Boolean.TRUE);
    ent.setDipsRegistrationCode("DIPS_1234");
    ent.setOwnerType(1);
    ent.setOwnerId(UUID.fromString("054bb198-ab4c-4bb1-a27c-78af8e495f7a"));
    ent.setPublicFlag(false);

    ent.setImageBinary(createImageBytes());
    ent.setImageFormat("png");

    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now);
    ent.setUpdateTime(now);
    ent.setDeleteFlag(Boolean.FALSE);

    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ent.setPayloadNumber(1);
    ent.setPayloadName("ペイロード");
    ent.setPayloadDetailText("テストのペイロード情報を記載");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("text/plain");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now5 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now5);
    ent.setUpdateTime(now5);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity_1() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b01"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ent.setPayloadNumber(1);
    ent.setPayloadName("ペイロード1");
    ent.setPayloadDetailText("テストのペイロード情報を記載1");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku1.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("text/plain");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now5 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now5);
    ent.setUpdateTime(now5);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity_2() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b02"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ent.setPayloadNumber(1);
    ent.setPayloadName("ペイロード2");
    ent.setPayloadDetailText("テストのペイロード情報を記載2");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku2.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("text/plain");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now5 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now5);
    ent.setUpdateTime(now5);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity_3() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b03"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ent.setPayloadNumber(1);
    ent.setPayloadName("ペイロード3");
    ent.setPayloadDetailText("テストのペイロード情報を記載3");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku3.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("text/plain");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now5 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now5);
    ent.setUpdateTime(now5);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity_5() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b05"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf23"));
    ent.setPayloadNumber(1);
    ent.setPayloadName("ペイロード1");
    ent.setPayloadDetailText("ペイロード詳細テキスト");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("text/plain");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now5 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now5);
    ent.setUpdateTime(now5);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity_6() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b06"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf24"));
    ent.setPayloadNumber(1);
    ent.setPayloadName("ペイロード2");
    ent.setPayloadDetailText("ペイロード詳細テキスト");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("text/plain");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now6 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now6);
    ent.setUpdateTime(now6);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity_7() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b07"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf24"));
    ent.setPayloadNumber(2);
    ent.setPayloadName("ペイロード3");
    ent.setPayloadDetailText("ペイロード詳細テキスト");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("text/plain");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now7 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now7);
    ent.setUpdateTime(now7);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity_8() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b08"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf24"));
    ent.setPayloadNumber(3);
    ent.setPayloadName("ペイロード4");
    ent.setPayloadDetailText("ペイロード詳細テキスト");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("text/plain");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now8 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now8);
    ent.setUpdateTime(now8);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity_9() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("1a3f9b2c-4d5e-4f7a-8b9c-0d1e2f3a4b09"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf99"));
    ent.setPayloadNumber(1);
    ent.setPayloadName("ペイロード5");
    ent.setPayloadDetailText("ペイロード詳細テキスト");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("text/plain");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now9 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now9);
    ent.setUpdateTime(now9);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  private PayloadInfoEntity createPayloadInfoEntity_10() {
    PayloadInfoEntity ent = new PayloadInfoEntity();
    ent.setPayloadId(UUID.fromString("7ed6002d-a68f-4e2d-a530-3cd281b5093e"));
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
    ent.setPayloadNumber(1);
    ent.setPayloadName("ペイロード");
    ent.setPayloadDetailText("ペイロード詳細テキスト");
    ent.setImageData(createImageBytes());
    ent.setImageFormat("png");
    ent.setFilePhysicalName("payload_hosoku.txt");
    ent.setFileData(
        new byte[] {
          -24, -93, -100, -24, -74, -77, -24, -77, -121, -26, -106, -103, -26, -125, -123, -27, -96,
          -79, -29, -127, -82, -27, -122, -123, -27, -82, -71
        });
    ent.setFileFormat("png");
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ent.setUpdateUserId("user01");
    java.sql.Timestamp now10 = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now10);
    ent.setUpdateTime(now10);
    ent.setDeleteFlag(Boolean.FALSE);
    return ent;
  }

  /**
   * 料金情報共通処理登録Entity作成
   *
   * @return
   */
  private PriceInfoEntity createPriceInfoEntity() {
    PriceInfoEntity ret = new PriceInfoEntity();
    ret.setPriceId(UUID.randomUUID());
    ret.setResourceId("リソースID");
    ret.setResourceType(1);
    ret.setPrimaryRouteOperatorId("主管航路事業者ID1");
    ret.setPriceType(4);
    ret.setPricePerUnit(1);
    ret.setPrice(1000);
    //   ret.setEffectiveTime(Range.localDateTimeRange(
    //   String.format("[%s,%s)",
    //   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T10:00:00Z"),
    //   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T11:00:00Z"))));
    ret.setOperatorId("ope01");
    ret.setPriority(1);
    ret.setDeleteFlag(false);

    return ret;
  }

  // 共通の画像バイト配列を返すユーティリティ（上で複数回使うため）
  private byte[] createImageBytes() {
    return new byte[] {
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
  }

  /**
   * テスト用: 機体情報・ペイロード情報を repository に保存するユーティリティ
   *
   * @param aircraftInfoRepository テストで @Autowired する AircraftInfoRepository
   * @param payloadInfoRepository テストで @Autowired する PayloadInfoRepository
   */
  private void payloadTestEntities() {

    // AircraftInfo を登録
    aircraftInfoRepository.save(createAircraftInfoEntity_3());
    aircraftInfoRepository.save(createAircraftInfoEntity_4());
    aircraftInfoRepository.save(createAircraftInfoEntity_5());

    // PayloadInfo を登録
    payloadInfoRepository.save(createPayloadInfoEntity_5());
    payloadInfoRepository.save(createPayloadInfoEntity_6());
    payloadInfoRepository.save(createPayloadInfoEntity_7());
    payloadInfoRepository.save(createPayloadInfoEntity_8());
    payloadInfoRepository.save(createPayloadInfoEntity_9());
  }

  /** データテンプレート ■機体情報Entity(AircraftInfoEntity) 機体情報Entity6(save結果_正常) */
  private AircraftInfoEntity createAircraftInfoEntity_6() {
    AircraftInfoEntity ret = new AircraftInfoEntity();
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf27"));
    ret.setAircraftName("機体名1");
    ret.setManufacturer("製造メーカー1");
    ret.setModelNumber("型式番号1");
    ret.setModelName("テスト機種名1");
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
    ret.setImageBinary(createImageBytes());
    ret.setImageFormat("png");
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);

    return ret;
  }

  /** データテンプレート ■機体情報Entity(AircraftInfoEntity) 機体情報Entity7(save結果_正常) */
  private AircraftInfoEntity createAircraftInfoEntity_7() {
    AircraftInfoEntity ret = new AircraftInfoEntity();
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf28"));
    ret.setAircraftName("機体名2");
    ret.setManufacturer("製造メーカー2");
    ret.setModelNumber("型式番号2");
    ret.setModelName("テスト機種名2");
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
    ret.setImageBinary(createImageBytes());
    ret.setImageFormat("png");
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** データテンプレート ■機体情報Entity(AircraftInfoEntity) 機体情報Entity8(save結果_正常) */
  private AircraftInfoEntity createAircraftInfoEntity_8() {
    AircraftInfoEntity ret = new AircraftInfoEntity();
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf29"));
    ret.setAircraftName("機体名3");
    ret.setManufacturer("製造メーカー3");
    ret.setModelNumber("型式番号3");
    ret.setModelName("テスト機種名3");
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
    ret.setImageBinary(createImageBytes());
    ret.setImageFormat("png");
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** データテンプレート ■機体情報Entity(AircraftInfoEntity) 機体情報Entity9(save結果_正常) */
  private AircraftInfoEntity createAircraftInfoEntity_9() {
    AircraftInfoEntity ret = new AircraftInfoEntity();
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf30"));
    ret.setAircraftName("機体名4");
    ret.setManufacturer("製造メーカー4");
    ret.setModelNumber("型式番号4");
    ret.setModelName("テスト機種名4");
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
    ret.setImageBinary(createImageBytes());
    ret.setImageFormat("png");
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** データテンプレート ■機体情報Entity(AircraftInfoEntity) 機体情報Entity9(save結果_正常) */
  private AircraftInfoEntity createAircraftInfoEntity_10() {
    AircraftInfoEntity ret = new AircraftInfoEntity();
    ret.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf31"));
    ret.setAircraftName("機体名5");
    ret.setManufacturer(null);
    ret.setModelNumber(null);
    ret.setModelName(null);
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
    ret.setImageBinary(createImageBytes());
    ret.setImageFormat("png");
    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    ret.setUpdateUserId("user01");
    ret.setCreateTime(new Timestamp(System.currentTimeMillis()));
    ret.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    ret.setDeleteFlag(false);
    return ret;
  }

  /** データテンプレート ■登録更新リクエスト 登録更新_正常リクエストボディ */
  private static AircraftInfoRequestDto createAircraftInfoRequestDto() {
    AircraftInfoRequestDto ret = new AircraftInfoRequestDto();
    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf21");
    ret.setAircraftName("機体名機体名");
    ret.setManufacturer("製造メーカー製造メーカー");
    ret.setModelNumber("MD12345V1");
    ret.setModelName("更新後機種名");
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

    return ret;
  }

  /** テスト用: 機体情報 Entity を作成して返す */
  private AircraftInfoEntity createAircraftInfoEntity() {
    AircraftInfoEntity ent = new AircraftInfoEntity();
    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf21"));
    ent.setAircraftName("機体名機体名");
    ent.setManufacturer("製造メーカー製造メーカー");
    ent.setModelNumber("MD12345V1");
    ent.setModelName("更新前機種名");
    ent.setManufacturingNumber("N12345678");
    ent.setAircraftType(1);
    ent.setMaxTakeoffWeight(99.0);
    ent.setBodyWeight(88.0);
    ent.setMaxFlightSpeed(77.0);
    ent.setMaxFlightTime(66.0);
    ent.setLat(55.0);
    ent.setLon(44.0);
    ent.setCertification(Boolean.TRUE);
    ent.setDipsRegistrationCode("DIPS_1234");
    ent.setOwnerType(1);
    ent.setOwnerId(UUID.fromString("054bb198-ab4c-4bb1-a27c-78af8e495f7a"));
    ent.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
    ent.setCreateTime(now);
    ent.setUpdateTime(now);
    ent.setDeleteFlag(Boolean.FALSE);

    return ent;
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

  /** テスト用: 機体情報Entity モデル検索用事前準備データ1 */
  private void createModelSerchAircraftInfoEntity() {
    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

    AircraftInfoEntity ent1 = new AircraftInfoEntity();
    ent1.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf11"));
    ent1.setManufacturer("製造メーカー1");
    ent1.setModelNumber("MD12345V1");
    ent1.setPublicFlag(Boolean.TRUE);
    ent1.setOperatorId("ope01");
    ent1.setUpdateUserId("user01");
    ent1.setCreateTime(now);
    ent1.setUpdateTime(now);
    ent1.setDeleteFlag(Boolean.FALSE);
    aircraftInfoRepository.save(ent1);

    AircraftInfoEntity ent2 = new AircraftInfoEntity();
    ent2.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf12"));
    ent2.setManufacturer("製造メーカー1");
    ent2.setModelNumber("MD12345V2");
    ent2.setPublicFlag(Boolean.TRUE);
    ent2.setOperatorId("ope01");
    ent2.setUpdateUserId("user01");
    ent2.setCreateTime(now);
    ent2.setUpdateTime(now);
    ent2.setDeleteFlag(Boolean.FALSE);
    aircraftInfoRepository.save(ent2);

    AircraftInfoEntity ent3 = new AircraftInfoEntity();
    ent3.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf13"));
    ent3.setManufacturer("製造メーカー2");
    ent3.setModelNumber("MD12345V1");
    ent3.setPublicFlag(Boolean.FALSE);
    ent3.setOperatorId("ope01");
    ent3.setUpdateUserId("user01");
    ent3.setCreateTime(now);
    ent3.setUpdateTime(now);
    ent3.setDeleteFlag(Boolean.FALSE);
    aircraftInfoRepository.save(ent3);

    AircraftInfoEntity ent4 = new AircraftInfoEntity();
    ent4.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf14"));
    ent4.setManufacturer(null);
    ent4.setModelNumber(null);
    ent4.setPublicFlag(Boolean.TRUE);
    ent4.setOperatorId("ope01");
    ent4.setUpdateUserId("user01");
    ent4.setCreateTime(now);
    ent4.setUpdateTime(now);
    ent4.setDeleteFlag(Boolean.FALSE);
    aircraftInfoRepository.save(ent4);

    AircraftInfoEntity ent5 = new AircraftInfoEntity();
    ent5.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf15"));
    ent5.setManufacturer("製造メーカー2");
    ent5.setModelNumber("MD12345V2");
    ent5.setPublicFlag(Boolean.TRUE);
    ent5.setOperatorId("ope01");
    ent5.setUpdateUserId("user01");
    ent5.setCreateTime(now);
    ent5.setUpdateTime(now);
    ent5.setDeleteFlag(Boolean.FALSE);
    aircraftInfoRepository.save(ent5);
  }

  /** テスト用: ペイロード情報Entity モデル検索用事前準備データ1 */
  private List<PayloadInfoEntity> createModelSerchPayloadInfoEntity() {
    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
    List<PayloadInfoEntity> list = new ArrayList<>();

    PayloadInfoEntity ent1 = new PayloadInfoEntity();
    ent1.setPayloadId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf31"));
    ent1.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf11"));
    ent1.setPayloadNumber(1);
    ent1.setPayloadName("ペイロード");
    ent1.setPayloadDetailText("テストのペイロード情報を記載");
    ent1.setOperatorId("ope01");
    ent1.setUpdateUserId("user01");
    ent1.setCreateTime(now);
    ent1.setUpdateTime(now);
    ent1.setDeleteFlag(Boolean.FALSE);
    list.add(ent1);
    payloadInfoRepository.save(ent1);

    PayloadInfoEntity ent2 = new PayloadInfoEntity();
    ent2.setPayloadId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf32"));
    ent2.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf12"));
    ent2.setPayloadNumber(1);
    ent2.setPayloadName("ペイロード");
    ent2.setPayloadDetailText("テストのペイロード情報を記載");
    ent2.setOperatorId("ope01");
    ent2.setUpdateUserId("user01");
    ent2.setCreateTime(now);
    ent2.setUpdateTime(now);
    ent2.setDeleteFlag(Boolean.FALSE);
    list.add(ent2);
    payloadInfoRepository.save(ent2);

    PayloadInfoEntity ent3 = new PayloadInfoEntity();
    ent3.setPayloadId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf33"));
    ent3.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf13"));
    ent3.setPayloadNumber(1);
    ent3.setPayloadName("ペイロード");
    ent3.setPayloadDetailText("テストのペイロード情報を記載");
    ent3.setOperatorId("ope01");
    ent3.setUpdateUserId("user01");
    ent3.setCreateTime(now);
    ent3.setUpdateTime(now);
    ent3.setDeleteFlag(Boolean.FALSE);
    list.add(ent3);
    payloadInfoRepository.save(ent3);

    PayloadInfoEntity ent4 = new PayloadInfoEntity();
    ent4.setPayloadId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf34"));
    ent4.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf14"));
    ent4.setPayloadNumber(1);
    ent4.setPayloadName("ペイロード");
    ent4.setPayloadDetailText("テストのペイロード情報を記載");
    ent4.setOperatorId("ope01");
    ent4.setUpdateUserId("user01");
    ent4.setCreateTime(now);
    ent4.setUpdateTime(now);
    ent4.setDeleteFlag(Boolean.FALSE);

    payloadInfoRepository.save(ent4);

    PayloadInfoEntity ent5 = new PayloadInfoEntity();
    ent5.setPayloadId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf35"));
    ent5.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf15"));
    ent5.setPayloadNumber(1);
    ent5.setPayloadName("ペイロード");
    ent5.setPayloadDetailText("テストのペイロード情報を記載");
    ent5.setOperatorId("ope01");
    ent5.setUpdateUserId("user01");
    ent5.setCreateTime(now);
    ent5.setUpdateTime(now);
    ent5.setDeleteFlag(Boolean.FALSE);
    list.add(ent5);
    payloadInfoRepository.save(ent5);

    return list;
  }

  /**
   * テスト用: 料金情報レスポンスDTO モデル検索用事前準備データ作成
   *
   * @return PriceInfoSearchListResponseDto
   */
  private PriceInfoSearchListResponseDto createModelSerchPriceInfoResponseDto() {
    PriceInfoSearchListResponseDto responseDto = new PriceInfoSearchListResponseDto();
    List<PriceInfoSearchListElement> resources = new ArrayList<>();

    // リソース1
    PriceInfoSearchListElement resource1 = new PriceInfoSearchListElement();
    resource1.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf11");
    resource1.setResourceType(2);

    List<PriceInfoSearchListDetailElement> priceInfos1 = new ArrayList<>();

    // 料金情報1
    PriceInfoSearchListDetailElement price1 = new PriceInfoSearchListDetailElement();
    price1.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf21");
    price1.setPriceType(2);
    price1.setPricePerUnit(1);
    price1.setPrice(1000);
    price1.setEffectiveStartTime("2026-01-01T10:00:00Z");
    price1.setEffectiveEndTime("2027-01-01T10:00:00Z");
    price1.setPriority(1);
    price1.setOperatorId("ope01");
    priceInfos1.add(price1);

    resource1.setPriceInfos(priceInfos1);
    resources.add(resource1);

    // リソース2
    PriceInfoSearchListElement resource2 = new PriceInfoSearchListElement();
    resource2.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf12");
    resource2.setResourceType(2);

    List<PriceInfoSearchListDetailElement> priceInfos2 = new ArrayList<>();

    // 料金情報2
    PriceInfoSearchListDetailElement price2 = new PriceInfoSearchListDetailElement();
    price2.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
    price2.setPriceType(2);
    price2.setPricePerUnit(1);
    price2.setPrice(1000);
    price2.setEffectiveStartTime("2026-01-01T10:00:00Z");
    price2.setEffectiveEndTime("2027-01-01T10:00:00Z");
    price2.setPriority(1);
    price2.setOperatorId("ope01");
    priceInfos2.add(price2);

    resource2.setPriceInfos(priceInfos2);
    resources.add(resource2);

    // リソース3
    PriceInfoSearchListElement resource3 = new PriceInfoSearchListElement();
    resource3.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf13");
    resource3.setResourceType(2);

    List<PriceInfoSearchListDetailElement> priceInfos3 = new ArrayList<>();

    // 料金情報3
    PriceInfoSearchListDetailElement price3 = new PriceInfoSearchListDetailElement();
    price3.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf23");
    price3.setPriceType(2);
    price3.setPricePerUnit(1);
    price3.setPrice(1000);
    price3.setEffectiveStartTime("2026-01-01T10:00:00Z");
    price3.setEffectiveEndTime("2027-01-01T10:00:00Z");
    price3.setPriority(1);
    price3.setOperatorId("ope01");
    priceInfos2.add(price3);

    resource3.setPriceInfos(priceInfos3);
    resources.add(resource3);

    // リソース4
    PriceInfoSearchListElement resource4 = new PriceInfoSearchListElement();
    resource4.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf14");
    resource4.setResourceType(2);

    List<PriceInfoSearchListDetailElement> priceInfos4 = new ArrayList<>();

    // 料金情報4
    PriceInfoSearchListDetailElement price4 = new PriceInfoSearchListDetailElement();
    price4.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf24");
    price4.setPriceType(2);
    price4.setPricePerUnit(1);
    price4.setPrice(1000);
    price4.setEffectiveStartTime("2026-01-01T10:00:00Z");
    price4.setEffectiveEndTime("2027-01-01T10:00:00Z");
    price4.setPriority(1);
    price4.setOperatorId("ope01");
    priceInfos2.add(price4);

    resource4.setPriceInfos(priceInfos4);
    resources.add(resource4);

    // リソース5
    PriceInfoSearchListElement resource5 = new PriceInfoSearchListElement();
    resource5.setResourceId("0a0711a5-ff74-4164-9309-8888b433cf15");
    resource5.setResourceType(2);

    List<PriceInfoSearchListDetailElement> priceInfos5 = new ArrayList<>();

    // 料金情報5
    PriceInfoSearchListDetailElement price5 = new PriceInfoSearchListDetailElement();
    price5.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf25");
    price5.setPriceType(2);
    price5.setPricePerUnit(1);
    price5.setPrice(1000);
    price5.setEffectiveStartTime("2026-01-01T10:00:00Z");
    price5.setEffectiveEndTime("2027-01-01T10:00:00Z");
    price5.setPriority(1);
    price5.setOperatorId("ope01");
    priceInfos2.add(price5);

    resource5.setPriceInfos(priceInfos5);
    resources.add(resource5);

    responseDto.setResources(resources);
    return responseDto;
  }

  /** データテンプレート ■一覧要求リクエスト 一覧要求リクエストボディ(モデル検索用) */
  private static AircraftInfoSearchListRequestDto createAircraftInfoModelSearchRequestDto() {
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();

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
}
