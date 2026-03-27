// DB接続を行う想定のテストのため、一括テストの対象外とするためにコメントアウト

// package com.hitachi.droneroute.dpm.service.impl;
//
// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
//
// import java.sql.Timestamp;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.SpyBean;
// import org.springframework.test.context.ActiveProfiles;
//
// import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
// import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
// import com.hitachi.droneroute.cmn.exception.NotFoundException;
// import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
// import com.hitachi.droneroute.cmn.settings.SystemSettings;
// import com.hitachi.droneroute.config.dto.RoleInfoDto;
// import com.hitachi.droneroute.config.dto.UserInfoDto;
// import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
// import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
// import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListResponseDto;
// import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
// import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListResponseDto;
// import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;
// import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateResponseDto;
// import com.hitachi.droneroute.dpm.entity.DronePortInfoEntity;
// import com.hitachi.droneroute.dpm.entity.DronePortReserveInfoEntity;
// import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;
// import com.hitachi.droneroute.dpm.repository.DronePortInfoRepository;
// import com.hitachi.droneroute.dpm.repository.DronePortReserveInfoRepository;
// import com.hitachi.droneroute.dpm.repository.DronePortStatusRepository;
//
// import io.hypersistence.utils.hibernate.type.range.Range;
//
// // DB接続のためコメントアウト
// /** DronePortReserveInfoServiceImplクラスの単体テスト */
// @SpringBootTest
// @ActiveProfiles("test")
// public class DronePortReserveInfoServiceImplWithRepositoryTest {
//
//  @Autowired private DronePortReserveInfoServiceImpl dronePortReserveInfoServiceImpl;
//
//  @Autowired private AircraftInfoRepository aircraftInfoRepository;
//
//  @Autowired private DronePortInfoRepository dronePortInfoRepository;
//
//  @Autowired private DronePortStatusRepository dronePortStatusRepository;
//
//  @Autowired private DronePortReserveInfoRepository dronePortReserveInfoRepository;
//
//  @SpyBean private SystemSettings systemSettings;
//
//  @BeforeEach
//  public void setUp() {
//    MockitoAnnotations.openMocks(this);
//
//    dronePortInfoRepository.deleteAll();
//    dronePortStatusRepository.deleteAll();
//    dronePortReserveInfoRepository.deleteAll();
//    aircraftInfoRepository.deleteAll();
//  }
//
//  /**
//   * メソッド名: register<br>
//   * 試験名: 離着陸場予約情報登録の正常系テスト<br>
//   * 条件: 正常なリクエストDTOを渡す。離着陸場種別:VIS管理<br>
//   * 結果: 3つの離着陸場予約IDが生成される<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void testregister_一括予約ID_予約事業者ID() {
//	  createDBRegister();
//    DronePortReserveInfoRegisterListRequestDto dto = createDronePortReserveRegisterDto();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    DronePortReserveInfoRegisterListResponseDto responseDto =
//        dronePortReserveInfoServiceImpl.register(dto, userInfo);
//
//    assertNotNull(responseDto);
//    assertEquals(3, responseDto.getDronePortReservationIds().size());
//    assertDoesNotThrow(() -> UUID.fromString(responseDto.getDronePortReservationIds().get(0)));
//    assertDoesNotThrow(() -> UUID.fromString(responseDto.getDronePortReservationIds().get(1)));
//    assertDoesNotThrow(() -> UUID.fromString(responseDto.getDronePortReservationIds().get(2)));
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営責任者で予約事業者IDが一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testupdate_航路運営責任者予約事業者ID一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営担当者で予約事業者IDが一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testupdate_航路運営担当者予約事業者ID一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営責任者で予約事業者IDが不一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testupdate_航路運営責任者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営担当者で予約事業者IDが不一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testupdate_航路運営担当者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営責任者でDB予約事業者IDがnull<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testupdate_航路運営責任者DB予約事業者IDnull() {
//	  createDBUpdateReserveProviderIdNull();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営担当者でDB予約事業者IDがnull<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testupdate_航路運営担当者DB予約事業者IDnull() {
//    createDBUpdateReserveProviderIdNull();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 運航事業責任者で予約事業者IDが一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testupdate_運航事業責任者予約事業者ID一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 運航事業担当者で予約事業者IDが一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testupdate_運航事業担当者予約事業者ID一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 運航事業者で予約事業者IDが一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testupdate_運航事業者予約事業者ID一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の異常系テスト<br>
//   * 条件: 運航事業責任者で予約事業者IDが不一致<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testupdate_運航事業責任者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(
//          ServiceErrorException.class,
//          () -> dronePortReserveInfoServiceImpl.update(request,userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の異常系テスト<br>
//   * 条件: 運航事業担当者で予約事業者IDが不一致<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testupdate_運航事業担当者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(
//            ServiceErrorException.class,
//            () -> dronePortReserveInfoServiceImpl.update(request,userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の異常系テスト<br>
//   * 条件: 運航事業者で予約事業者IDが不一致<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testupdate_運航事業者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(
//            ServiceErrorException.class,
//            () -> dronePortReserveInfoServiceImpl.update(request,userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の異常系テスト<br>
//   * 条件: 運航事業責任者でDB予約事業者IDがnull<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testupdate_運航事業責任者DB予約事業者IDnull() {
//    createDBUpdateReserveProviderIdNull();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    ServiceErrorException exception =
//        assertThrows(
//            ServiceErrorException.class,
//            () -> dronePortReserveInfoServiceImpl.update(request,userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の異常系テスト<br>
//   * 条件: 運航事業担当者でDB予約事業者IDがnull<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testupdate_運航事業担当者DB予約事業者IDnull() {
//    createDBUpdateReserveProviderIdNull();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    ServiceErrorException exception =
//        assertThrows(
//            ServiceErrorException.class,
//            () -> dronePortReserveInfoServiceImpl.update(request,userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の異常系テスト<br>
//   * 条件: 運航事業者でDB予約事業者IDがnull<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testupdate_運航事業者DB予約事業者IDnull() {
//    createDBUpdateReserveProviderIdNull();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    ServiceErrorException exception =
//        assertThrows(
//            ServiceErrorException.class,
//            () -> dronePortReserveInfoServiceImpl.update(request,userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営者で予約事業者IDが一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営者予約事業者ID一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("1");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営者で予約事業者IDが不一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("1");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 関係者で予約事業者IDが一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testPutData_関係者予約事業者ID一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("3");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営者、航路運営責任者、航路運営担当者で予約事業者IDが不一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営者航路運営責任者航路運営担当者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    List<RoleInfoDto> roles = new ArrayList<>();
//    RoleInfoDto roleInfoDto1 = new RoleInfoDto();
//    roleInfoDto1.setRoleId("1");
//    roles.add(roleInfoDto1);
//    RoleInfoDto roleInfoDto2 = new RoleInfoDto();
//    roleInfoDto2.setRoleId("10");
//    roles.add(roleInfoDto2);
//    RoleInfoDto roleInfoDto3 = new RoleInfoDto();
//    roleInfoDto3.setRoleId("11");
//    roles.add(roleInfoDto3);
//
//    userInfo.setRoles(roles);
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 航路運営者、運航事業者、運航事業担当者、関係者で予約事業者IDが不一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営者運航事業者運航事業担当者関係者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    List<RoleInfoDto> roles = new ArrayList<>();
//    RoleInfoDto roleInfoDto1 = new RoleInfoDto();
//    roleInfoDto1.setRoleId("1");
//    roles.add(roleInfoDto1);
//    RoleInfoDto roleInfoDto2 = new RoleInfoDto();
//    roleInfoDto2.setRoleId("2");
//    roles.add(roleInfoDto2);
//    RoleInfoDto roleInfoDto3 = new RoleInfoDto();
//    roleInfoDto3.setRoleId("21");
//    roles.add(roleInfoDto3);
//    RoleInfoDto roleInfoDto4 = new RoleInfoDto();
//    roleInfoDto4.setRoleId("3");
//    roles.add(roleInfoDto4);
//
//    userInfo.setRoles(roles);
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の正常系テスト<br>
//   * 条件: 運航事業者、運航事業責任者、運航事業担当者、関係者で予約事業者IDが一致<br>
//   * 結果: 更新が成功する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testPutData_運航事業者運航事業責任者運航事業担当者関係者予約事業者ID一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    List<RoleInfoDto> roles = new ArrayList<>();
//    RoleInfoDto roleInfoDto1 = new RoleInfoDto();
//    roleInfoDto1.setRoleId("2");
//    roles.add(roleInfoDto1);
//    RoleInfoDto roleInfoDto2 = new RoleInfoDto();
//    roleInfoDto2.setRoleId("20");
//    roles.add(roleInfoDto2);
//    RoleInfoDto roleInfoDto3 = new RoleInfoDto();
//    roleInfoDto3.setRoleId("21");
//    roles.add(roleInfoDto3);
//    RoleInfoDto roleInfoDto4 = new RoleInfoDto();
//    roleInfoDto4.setRoleId("3");
//    roles.add(roleInfoDto4);
//
//    userInfo.setRoles(roles);
//
//    DronePortReserveInfoUpdateResponseDto response =
// dronePortReserveInfoServiceImpl.update(request,userInfo);
//
//    assertNotNull(response);
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12", response.getDronePortReservationId());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の異常系テスト<br>
//   * 条件: 関係者で予約事業者IDが不一致<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testPutData_関係者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("3");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.update(request,userInfo));
//
//        assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 離着陸場予約情報更新の異常系テスト<br>
//   * 条件: 運航事業者、運航事業責任者、運航事業担当者、関係者で予約事業者IDが不一致<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testPutData_運航事業者運航事業責任者運航事業担当者関係者予約事業者ID不一致() {
//    createDBUpdate();
//    DronePortReserveInfoUpdateRequestDto request = createDronePortReserveUpdateDto();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    List<RoleInfoDto> roles = new ArrayList<>();
//    RoleInfoDto roleInfoDto1 = new RoleInfoDto();
//    roleInfoDto1.setRoleId("2");
//    roles.add(roleInfoDto1);
//    RoleInfoDto roleInfoDto2 = new RoleInfoDto();
//    roleInfoDto2.setRoleId("20");
//    roles.add(roleInfoDto2);
//    RoleInfoDto roleInfoDto3 = new RoleInfoDto();
//    roleInfoDto3.setRoleId("21");
//    roles.add(roleInfoDto3);
//    RoleInfoDto roleInfoDto4 = new RoleInfoDto();
//    roleInfoDto4.setRoleId("3");
//    roles.add(roleInfoDto4);
//
//    userInfo.setRoles(roles);
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.update(request,userInfo));
//
//        assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営責任者で予約事業者IDが一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営責任者予約事業ID一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営担当者で予約事業者IDが一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営担当者予約事業ID一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営責任者で予約事業者IDが不一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営責任者予約事業ID不一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営担当者で予約事業者IDが不一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営担当者予約事業ID不一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営責任者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営責任者_DB予約事業IDnull_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationIdReserveProviderIdNull();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営担当者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営担当者_DB予約事業IDnull_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationIdReserveProviderIdNull();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 運航事業責任者で予約事業者IDが一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_運航事業責任者予約事業ID一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 運航事業担当者で予約事業者IDが一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_運航事業担当者予約事業ID一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 運航事業者で予約事業者IDが一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_運航事業者予約事業ID一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業責任者で予約事業者IDが不一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業責任者予約事業ID不一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業担当者で予約事業者IDが不一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業担当者予約事業ID不一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業者で予約事業者IDが不一致、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業者予約事業ID不一致_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業責任者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業責任者_DB予約事業IDnull_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationIdReserveProviderIdNull();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業担当者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業担当者_DB予約事業IDnull_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationIdReserveProviderIdNull();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業者_DB予約事業IDnull_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteDronePortReservationIdReserveProviderIdNull();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: DB存在なし、離着陸場予約ID使用フラグがtrue<br>
//   * 結果: NotFoundExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_DB存在なし_離着陸場予約ID使用フラグtrue() {
//    createDBDeleteGroupReservationId();
//
//    String aircraftReserveId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    NotFoundException exception =
//            assertThrows(
//                NotFoundException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(aircraftReserveId.toString(), true,
// userInfo));
//
//    assertEquals("離着陸場予約情報が見つかりません。離着陸場予約ID:3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12",
// exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営責任者で予約事業者IDが一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営責任者予約事業ID一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営担当者で予約事業者IDが一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営担当者予約事業ID一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営責任者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営責任者予約事業ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営担当者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営担当者予約事業ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営責任者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営責任者_DB予約事業IDnull_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultipleReserveProviderIdNull();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営担当者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営担当者_DB予約事業IDnull_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultipleReserveProviderIdNull();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 運航事業責任者で予約事業者IDが一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_運航事業責任者予約事業ID一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 運航事業担当者で予約事業者IDが一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_運航事業担当者予約事業ID一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 運航事業者で予約事業者IDが一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_運航事業者予約事業ID一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業責任者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業責任者予約事業ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業担当者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業担当者予約事業ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業者予約事業ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業責任者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業責任者_DB予約事業IDnull_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultipleReserveProviderIdNull();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業担当者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業担当者_DB予約事業IDnull_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultipleReserveProviderIdNull();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業者でDB予約事業者IDがnull、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業者_DB予約事業IDnull_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultipleReserveProviderIdNull();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: DB存在なし、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: NotFoundExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_DB存在なし_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultipleNotFound();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    NotFoundException exception =
//            assertThrows(
//                NotFoundException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//
//    assertEquals("離着陸場予約情報が見つかりません。一括予約ID:3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22",
// exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営者で予約事業者IDが一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営者予約事業ID一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("1");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営者予約事業ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("1");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 関係者で予約事業者IDが一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_関係者予約事業ID一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("3");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営者、航路運営責任者、航路運営担当者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営者航路運営責任者航路運営担当者予約事業者ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    List<RoleInfoDto> roles = new ArrayList<>();
//    RoleInfoDto roleInfoDto1 = new RoleInfoDto();
//    roleInfoDto1.setRoleId("1");
//    roles.add(roleInfoDto1);
//    RoleInfoDto roleInfoDto2 = new RoleInfoDto();
//    roleInfoDto2.setRoleId("10");
//    roles.add(roleInfoDto2);
//    RoleInfoDto roleInfoDto3 = new RoleInfoDto();
//    roleInfoDto3.setRoleId("11");
//    roles.add(roleInfoDto3);
//
//    userInfo.setRoles(roles);
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 航路運営者、運航事業者、運航事業担当者、関係者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_航路運営者運航事業者運航事業担当者関係者予約事業者ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    List<RoleInfoDto> roles = new ArrayList<>();
//    RoleInfoDto roleInfoDto1 = new RoleInfoDto();
//    roleInfoDto1.setRoleId("1");
//    roles.add(roleInfoDto1);
//    RoleInfoDto roleInfoDto2 = new RoleInfoDto();
//    roleInfoDto2.setRoleId("2");
//    roles.add(roleInfoDto2);
//    RoleInfoDto roleInfoDto3 = new RoleInfoDto();
//    roleInfoDto3.setRoleId("21");
//    roles.add(roleInfoDto3);
//    RoleInfoDto roleInfoDto4 = new RoleInfoDto();
//    roleInfoDto4.setRoleId("3");
//    roles.add(roleInfoDto4);
//
//    userInfo.setRoles(roles);
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の正常系テスト<br>
//   * 条件: 運航事業者、運航事業責任者、運航事業担当者、関係者で予約事業者IDが一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testdelete_運航事業者運航事業責任者運航事業担当者関係者予約事業者ID一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    List<RoleInfoDto> roles = new ArrayList<>();
//    RoleInfoDto roleInfoDto1 = new RoleInfoDto();
//    roleInfoDto1.setRoleId("2");
//    roles.add(roleInfoDto1);
//    RoleInfoDto roleInfoDto2 = new RoleInfoDto();
//    roleInfoDto2.setRoleId("20");
//    roles.add(roleInfoDto2);
//    RoleInfoDto roleInfoDto3 = new RoleInfoDto();
//    roleInfoDto3.setRoleId("21");
//    roles.add(roleInfoDto3);
//    RoleInfoDto roleInfoDto4 = new RoleInfoDto();
//    roleInfoDto4.setRoleId("3");
//    roles.add(roleInfoDto4);
//
//    userInfo.setRoles(roles);
//
//    assertDoesNotThrow(
//        () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 関係者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_関係者予約事業者ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("3");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: delete<br>
//   * 試験名: 離着陸場予約削除の異常系テスト<br>
//   * 条件: 運航事業者、運航事業責任者、運航事業担当者、関係者で予約事業者IDが不一致、離着陸場予約ID使用フラグがfalse<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  public void testdelete_運航事業者運航事業責任者運航事業担当者関係者予約事業者ID不一致_離着陸場予約ID使用フラグfalse() {
//    createDBDeleteGroupReservationIdMultiple();
//
//    String groupReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    List<RoleInfoDto> roles = new ArrayList<>();
//    RoleInfoDto roleInfoDto1 = new RoleInfoDto();
//    roleInfoDto1.setRoleId("2");
//    roles.add(roleInfoDto1);
//    RoleInfoDto roleInfoDto2 = new RoleInfoDto();
//    roleInfoDto2.setRoleId("20");
//    roles.add(roleInfoDto2);
//    RoleInfoDto roleInfoDto3 = new RoleInfoDto();
//    roleInfoDto3.setRoleId("21");
//    roles.add(roleInfoDto3);
//    RoleInfoDto roleInfoDto4 = new RoleInfoDto();
//    roleInfoDto4.setRoleId("3");
//    roles.add(roleInfoDto4);
//
//    userInfo.setRoles(roles);
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//            assertThrows(
//                ServiceErrorException.class,
//                () -> dronePortReserveInfoServiceImpl.delete(groupReservationId.toString(), false,
// userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: 一括予約IDがnull<br>
//   * 結果: 全データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_一括予約IDnull() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setGroupReservationId(null);
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    var data14 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data14);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24", data14.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data14.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data14.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data14.getRouteReservationId());
//        assertEquals(1, data14.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data14.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data14.getReservationTimeTo());
//        assertEquals("離着陸場名4", data14.getDronePortName());
//        assertEquals("機体名4", data14.getAircraftName());
//        assertEquals(true, data14.getReservationActiveFlag());
//        assertEquals(null, data14.getInactiveTimeFrom());
//        assertEquals(null, data14.getInactiveTimeTo());
//        assertEquals(null, data14.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data14.getReserveProviderId());
//        assertEquals("ope01", data14.getOperatorId());
//
//        var data15 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e15".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data15);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e25", data15.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e35", data15.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e45", data15.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e55", data15.getRouteReservationId());
//        assertEquals(1, data15.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data15.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data15.getReservationTimeTo());
//        assertEquals("離着陸場名5", data15.getDronePortName());
//        assertEquals("機体名5", data15.getAircraftName());
//        assertEquals(true, data15.getReservationActiveFlag());
//        assertEquals(null, data15.getInactiveTimeFrom());
//        assertEquals(null, data15.getInactiveTimeTo());
//        assertEquals(null, data15.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e65", data15.getReserveProviderId());
//        assertEquals("ope01", data15.getOperatorId());
//
//        var data16 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e16".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data16);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e26", data16.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e36", data16.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e46", data16.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e56", data16.getRouteReservationId());
//        assertEquals(1, data16.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data16.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data16.getReservationTimeTo());
//        assertEquals("離着陸場名6", data16.getDronePortName());
//        assertEquals("機体名6", data16.getAircraftName());
//        assertEquals(true, data16.getReservationActiveFlag());
//        assertEquals(null, data16.getInactiveTimeFrom());
//        assertEquals(null, data16.getInactiveTimeTo());
//        assertEquals(null, data16.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e66", data16.getReserveProviderId());
//        assertEquals("ope01", data16.getOperatorId());
//
//        var data17 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e17".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data17);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e27", data17.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e37", data17.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e47", data17.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e57", data17.getRouteReservationId());
//        assertEquals(1, data17.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data17.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data17.getReservationTimeTo());
//        assertEquals("離着陸場名7", data17.getDronePortName());
//        assertEquals("機体名7", data17.getAircraftName());
//        assertEquals(true, data17.getReservationActiveFlag());
//        assertEquals(null, data17.getInactiveTimeFrom());
//        assertEquals(null, data17.getInactiveTimeTo());
//        assertEquals(null, data17.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e67", data17.getReserveProviderId());
//        assertEquals("ope01", data17.getOperatorId());
//
//        var data18 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e18".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data18);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e28", data18.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data18.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data18.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data18.getRouteReservationId());
//        assertEquals(1, data18.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data18.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data18.getReservationTimeTo());
//        assertEquals("離着陸場名4", data18.getDronePortName());
//        assertEquals("機体名4", data18.getAircraftName());
//        assertEquals(true, data18.getReservationActiveFlag());
//        assertEquals(null, data18.getInactiveTimeFrom());
//        assertEquals(null, data18.getInactiveTimeTo());
//        assertEquals(null, data18.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data18.getReserveProviderId());
//        assertEquals("ope01", data18.getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: 一括予約IDが空文字<br>
//   * 結果: 全データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_一括予約ID空文字() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setGroupReservationId("");
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    var data14 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data14);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24", data14.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data14.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data14.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data14.getRouteReservationId());
//        assertEquals(1, data14.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data14.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data14.getReservationTimeTo());
//        assertEquals("離着陸場名4", data14.getDronePortName());
//        assertEquals("機体名4", data14.getAircraftName());
//        assertEquals(true, data14.getReservationActiveFlag());
//        assertEquals(null, data14.getInactiveTimeFrom());
//        assertEquals(null, data14.getInactiveTimeTo());
//        assertEquals(null, data14.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data14.getReserveProviderId());
//        assertEquals("ope01", data14.getOperatorId());
//
//        var data15 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e15".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data15);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e25", data15.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e35", data15.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e45", data15.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e55", data15.getRouteReservationId());
//        assertEquals(1, data15.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data15.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data15.getReservationTimeTo());
//        assertEquals("離着陸場名5", data15.getDronePortName());
//        assertEquals("機体名5", data15.getAircraftName());
//        assertEquals(true, data15.getReservationActiveFlag());
//        assertEquals(null, data15.getInactiveTimeFrom());
//        assertEquals(null, data15.getInactiveTimeTo());
//        assertEquals(null, data15.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e65", data15.getReserveProviderId());
//        assertEquals("ope01", data15.getOperatorId());
//
//        var data16 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e16".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data16);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e26", data16.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e36", data16.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e46", data16.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e56", data16.getRouteReservationId());
//        assertEquals(1, data16.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data16.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data16.getReservationTimeTo());
//        assertEquals("離着陸場名6", data16.getDronePortName());
//        assertEquals("機体名6", data16.getAircraftName());
//        assertEquals(true, data16.getReservationActiveFlag());
//        assertEquals(null, data16.getInactiveTimeFrom());
//        assertEquals(null, data16.getInactiveTimeTo());
//        assertEquals(null, data16.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e66", data16.getReserveProviderId());
//        assertEquals("ope01", data16.getOperatorId());
//
//        var data17 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e17".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data17);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e27", data17.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e37", data17.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e47", data17.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e57", data17.getRouteReservationId());
//        assertEquals(1, data17.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data17.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data17.getReservationTimeTo());
//        assertEquals("離着陸場名7", data17.getDronePortName());
//        assertEquals("機体名7", data17.getAircraftName());
//        assertEquals(true, data17.getReservationActiveFlag());
//        assertEquals(null, data17.getInactiveTimeFrom());
//        assertEquals(null, data17.getInactiveTimeTo());
//        assertEquals(null, data17.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e67", data17.getReserveProviderId());
//        assertEquals("ope01", data17.getOperatorId());
//
//        var data18 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e18".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data18);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e28", data18.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data18.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data18.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data18.getRouteReservationId());
//        assertEquals(1, data18.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data18.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data18.getReservationTimeTo());
//        assertEquals("離着陸場名4", data18.getDronePortName());
//        assertEquals("機体名4", data18.getAircraftName());
//        assertEquals(true, data18.getReservationActiveFlag());
//        assertEquals(null, data18.getInactiveTimeFrom());
//        assertEquals(null, data18.getInactiveTimeTo());
//        assertEquals(null, data18.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data18.getReserveProviderId());
//        assertEquals("ope01", data18.getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: 一括予約IDがDB該当あり<br>
//   * 結果: 該当データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_一括予約ID_DB該当あり() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24");
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(1, response.getData().size());
//
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14",
// response.getData().get(0).getDronePortReservationId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24",
// response.getData().get(0).getGroupReservationId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34",
// response.getData().get(0).getDronePortId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44",
// response.getData().get(0).getAircraftId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54",
// response.getData().get(0).getRouteReservationId());
//    assertEquals(1, response.getData().get(0).getUsageType());
//    assertEquals("2025-01-01T01:00:00Z", response.getData().get(0).getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", response.getData().get(0).getReservationTimeTo());
//    assertEquals("離着陸場名4", response.getData().get(0).getDronePortName());
//    assertEquals("機体名4", response.getData().get(0).getAircraftName());
//    assertEquals(true, response.getData().get(0).getReservationActiveFlag());
//    assertEquals(null, response.getData().get(0).getInactiveTimeFrom());
//    assertEquals(null, response.getData().get(0).getInactiveTimeTo());
//    assertEquals(null, response.getData().get(0).getVisDronePortCompanyId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64",
// response.getData().get(0).getReserveProviderId());
//    assertEquals("ope01", response.getData().get(0).getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: 一括予約IDがDB該当なし<br>
//   * 結果: 0件が取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_一括予約ID_DB該当なし() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e99");
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(0, response.getData().size());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: 予約事業者IDがnull<br>
//   * 結果: 全データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_予約事業者IDnull() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setReserveProviderId(null);
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    var data14 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data14);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24", data14.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data14.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data14.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data14.getRouteReservationId());
//        assertEquals(1, data14.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data14.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data14.getReservationTimeTo());
//        assertEquals("離着陸場名4", data14.getDronePortName());
//        assertEquals("機体名4", data14.getAircraftName());
//        assertEquals(true, data14.getReservationActiveFlag());
//        assertEquals(null, data14.getInactiveTimeFrom());
//        assertEquals(null, data14.getInactiveTimeTo());
//        assertEquals(null, data14.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data14.getReserveProviderId());
//        assertEquals("ope01", data14.getOperatorId());
//
//        var data15 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e15".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data15);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e25", data15.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e35", data15.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e45", data15.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e55", data15.getRouteReservationId());
//        assertEquals(1, data15.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data15.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data15.getReservationTimeTo());
//        assertEquals("離着陸場名5", data15.getDronePortName());
//        assertEquals("機体名5", data15.getAircraftName());
//        assertEquals(true, data15.getReservationActiveFlag());
//        assertEquals(null, data15.getInactiveTimeFrom());
//        assertEquals(null, data15.getInactiveTimeTo());
//        assertEquals(null, data15.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e65", data15.getReserveProviderId());
//        assertEquals("ope01", data15.getOperatorId());
//
//        var data16 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e16".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data16);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e26", data16.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e36", data16.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e46", data16.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e56", data16.getRouteReservationId());
//        assertEquals(1, data16.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data16.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data16.getReservationTimeTo());
//        assertEquals("離着陸場名6", data16.getDronePortName());
//        assertEquals("機体名6", data16.getAircraftName());
//        assertEquals(true, data16.getReservationActiveFlag());
//        assertEquals(null, data16.getInactiveTimeFrom());
//        assertEquals(null, data16.getInactiveTimeTo());
//        assertEquals(null, data16.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e66", data16.getReserveProviderId());
//        assertEquals("ope01", data16.getOperatorId());
//
//        var data17 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e17".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data17);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e27", data17.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e37", data17.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e47", data17.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e57", data17.getRouteReservationId());
//        assertEquals(1, data17.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data17.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data17.getReservationTimeTo());
//        assertEquals("離着陸場名7", data17.getDronePortName());
//        assertEquals("機体名7", data17.getAircraftName());
//        assertEquals(true, data17.getReservationActiveFlag());
//        assertEquals(null, data17.getInactiveTimeFrom());
//        assertEquals(null, data17.getInactiveTimeTo());
//        assertEquals(null, data17.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e67", data17.getReserveProviderId());
//        assertEquals("ope01", data17.getOperatorId());
//
//        var data18 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e18".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data18);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e28", data18.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data18.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data18.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data18.getRouteReservationId());
//        assertEquals(1, data18.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data18.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data18.getReservationTimeTo());
//        assertEquals("離着陸場名4", data18.getDronePortName());
//        assertEquals("機体名4", data18.getAircraftName());
//        assertEquals(true, data18.getReservationActiveFlag());
//        assertEquals(null, data18.getInactiveTimeFrom());
//        assertEquals(null, data18.getInactiveTimeTo());
//        assertEquals(null, data18.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data18.getReserveProviderId());
//        assertEquals("ope01", data18.getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: 予約事業者IDが空文字<br>
//   * 結果: 全データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_予約事業者ID空文字() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setReserveProviderId("");
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    var data14 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data14);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24", data14.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data14.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data14.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data14.getRouteReservationId());
//        assertEquals(1, data14.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data14.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data14.getReservationTimeTo());
//        assertEquals("離着陸場名4", data14.getDronePortName());
//        assertEquals("機体名4", data14.getAircraftName());
//        assertEquals(true, data14.getReservationActiveFlag());
//        assertEquals(null, data14.getInactiveTimeFrom());
//        assertEquals(null, data14.getInactiveTimeTo());
//        assertEquals(null, data14.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data14.getReserveProviderId());
//        assertEquals("ope01", data14.getOperatorId());
//
//        var data15 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e15".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data15);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e25", data15.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e35", data15.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e45", data15.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e55", data15.getRouteReservationId());
//        assertEquals(1, data15.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data15.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data15.getReservationTimeTo());
//        assertEquals("離着陸場名5", data15.getDronePortName());
//        assertEquals("機体名5", data15.getAircraftName());
//        assertEquals(true, data15.getReservationActiveFlag());
//        assertEquals(null, data15.getInactiveTimeFrom());
//        assertEquals(null, data15.getInactiveTimeTo());
//        assertEquals(null, data15.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e65", data15.getReserveProviderId());
//        assertEquals("ope01", data15.getOperatorId());
//
//        var data16 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e16".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data16);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e26", data16.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e36", data16.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e46", data16.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e56", data16.getRouteReservationId());
//        assertEquals(1, data16.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data16.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data16.getReservationTimeTo());
//        assertEquals("離着陸場名6", data16.getDronePortName());
//        assertEquals("機体名6", data16.getAircraftName());
//        assertEquals(true, data16.getReservationActiveFlag());
//        assertEquals(null, data16.getInactiveTimeFrom());
//        assertEquals(null, data16.getInactiveTimeTo());
//        assertEquals(null, data16.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e66", data16.getReserveProviderId());
//        assertEquals("ope01", data16.getOperatorId());
//
//        var data17 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e17".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data17);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e27", data17.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e37", data17.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e47", data17.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e57", data17.getRouteReservationId());
//        assertEquals(1, data17.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data17.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data17.getReservationTimeTo());
//        assertEquals("離着陸場名7", data17.getDronePortName());
//        assertEquals("機体名7", data17.getAircraftName());
//        assertEquals(true, data17.getReservationActiveFlag());
//        assertEquals(null, data17.getInactiveTimeFrom());
//        assertEquals(null, data17.getInactiveTimeTo());
//        assertEquals(null, data17.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e67", data17.getReserveProviderId());
//        assertEquals("ope01", data17.getOperatorId());
//
//        var data18 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e18".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data18);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e28", data18.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data18.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data18.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data18.getRouteReservationId());
//        assertEquals(1, data18.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data18.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data18.getReservationTimeTo());
//        assertEquals("離着陸場名4", data18.getDronePortName());
//        assertEquals("機体名4", data18.getAircraftName());
//        assertEquals(true, data18.getReservationActiveFlag());
//        assertEquals(null, data18.getInactiveTimeFrom());
//        assertEquals(null, data18.getInactiveTimeTo());
//        assertEquals(null, data18.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data18.getReserveProviderId());
//        assertEquals("ope01", data18.getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: 予約事業者IDがDB該当あり<br>
//   * 結果: 該当データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_予約事業者ID_DB該当あり() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setReserveProviderId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64");
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(2, response.getData().size());
//
//    var data14 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data14);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24", data14.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data14.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data14.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data14.getRouteReservationId());
//        assertEquals(1, data14.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data14.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data14.getReservationTimeTo());
//        assertEquals("離着陸場名4", data14.getDronePortName());
//        assertEquals("機体名4", data14.getAircraftName());
//        assertEquals(true, data14.getReservationActiveFlag());
//        assertEquals(null, data14.getInactiveTimeFrom());
//        assertEquals(null, data14.getInactiveTimeTo());
//        assertEquals(null, data14.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data14.getReserveProviderId());
//        assertEquals("ope01", data14.getOperatorId());
//
//        var data18 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e18".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data18);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e28", data18.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data18.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data18.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data18.getRouteReservationId());
//        assertEquals(1, data18.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data18.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data18.getReservationTimeTo());
//        assertEquals("離着陸場名4", data18.getDronePortName());
//        assertEquals("機体名4", data18.getAircraftName());
//        assertEquals(true, data18.getReservationActiveFlag());
//        assertEquals(null, data18.getInactiveTimeFrom());
//        assertEquals(null, data18.getInactiveTimeTo());
//        assertEquals(null, data18.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data18.getReserveProviderId());
//        assertEquals("ope01", data18.getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: 予約事業者IDがDB該当なし<br>
//   * 結果: 0件が取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_予約事業者ID_DB該当なし() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setReserveProviderId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e99");
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(0, response.getData().size());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: 一括予約ID、予約事業者IDがDB該当あり<br>
//   * 結果: 該当データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_一括予約ID予約事業者ID_DB該当あり() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24");
//    request.setReserveProviderId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64");
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(1, response.getData().size());
//
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14",
// response.getData().get(0).getDronePortReservationId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24",
// response.getData().get(0).getGroupReservationId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34",
// response.getData().get(0).getDronePortId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44",
// response.getData().get(0).getAircraftId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54",
// response.getData().get(0).getRouteReservationId());
//    assertEquals(1, response.getData().get(0).getUsageType());
//    assertEquals("2025-01-01T01:00:00Z", response.getData().get(0).getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", response.getData().get(0).getReservationTimeTo());
//    assertEquals("離着陸場名4", response.getData().get(0).getDronePortName());
//    assertEquals("機体名4", response.getData().get(0).getAircraftName());
//    assertEquals(true, response.getData().get(0).getReservationActiveFlag());
//    assertEquals(null, response.getData().get(0).getInactiveTimeFrom());
//    assertEquals(null, response.getData().get(0).getInactiveTimeTo());
//    assertEquals(null, response.getData().get(0).getVisDronePortCompanyId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64",
// response.getData().get(0).getReserveProviderId());
//    assertEquals("ope01", response.getData().get(0).getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: ページ、ソートを除いた全項目がnull<br>
//   * 結果: 全データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_全項目null() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setGroupReservationId(null);
//    request.setDronePortId(null);
//    request.setDronePortName(null);
//    request.setAircraftId(null);
//    request.setRouteReservationId(null);
//    request.setTimeFrom(null);
//    request.setTimeTo(null);
//    request.setReserveProviderId(null);
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    var data14 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data14);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24", data14.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data14.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data14.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data14.getRouteReservationId());
//        assertEquals(1, data14.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data14.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data14.getReservationTimeTo());
//        assertEquals("離着陸場名4", data14.getDronePortName());
//        assertEquals("機体名4", data14.getAircraftName());
//        assertEquals(true, data14.getReservationActiveFlag());
//        assertEquals(null, data14.getInactiveTimeFrom());
//        assertEquals(null, data14.getInactiveTimeTo());
//        assertEquals(null, data14.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data14.getReserveProviderId());
//        assertEquals("ope01", data14.getOperatorId());
//
//        var data15 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e15".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data15);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e25", data15.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e35", data15.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e45", data15.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e55", data15.getRouteReservationId());
//        assertEquals(1, data15.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data15.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data15.getReservationTimeTo());
//        assertEquals("離着陸場名5", data15.getDronePortName());
//        assertEquals("機体名5", data15.getAircraftName());
//        assertEquals(true, data15.getReservationActiveFlag());
//        assertEquals(null, data15.getInactiveTimeFrom());
//        assertEquals(null, data15.getInactiveTimeTo());
//        assertEquals(null, data15.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e65", data15.getReserveProviderId());
//        assertEquals("ope01", data15.getOperatorId());
//
//        var data16 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e16".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data16);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e26", data16.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e36", data16.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e46", data16.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e56", data16.getRouteReservationId());
//        assertEquals(1, data16.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data16.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data16.getReservationTimeTo());
//        assertEquals("離着陸場名6", data16.getDronePortName());
//        assertEquals("機体名6", data16.getAircraftName());
//        assertEquals(true, data16.getReservationActiveFlag());
//        assertEquals(null, data16.getInactiveTimeFrom());
//        assertEquals(null, data16.getInactiveTimeTo());
//        assertEquals(null, data16.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e66", data16.getReserveProviderId());
//        assertEquals("ope01", data16.getOperatorId());
//
//        var data17 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e17".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data17);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e27", data17.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e37", data17.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e47", data17.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e57", data17.getRouteReservationId());
//        assertEquals(1, data17.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data17.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data17.getReservationTimeTo());
//        assertEquals("離着陸場名7", data17.getDronePortName());
//        assertEquals("機体名7", data17.getAircraftName());
//        assertEquals(true, data17.getReservationActiveFlag());
//        assertEquals(null, data17.getInactiveTimeFrom());
//        assertEquals(null, data17.getInactiveTimeTo());
//        assertEquals(null, data17.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e67", data17.getReserveProviderId());
//        assertEquals("ope01", data17.getOperatorId());
//
//        var data18 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e18".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data18);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e28", data18.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data18.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data18.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data18.getRouteReservationId());
//        assertEquals(1, data18.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data18.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data18.getReservationTimeTo());
//        assertEquals("離着陸場名4", data18.getDronePortName());
//        assertEquals("機体名4", data18.getAircraftName());
//        assertEquals(true, data18.getReservationActiveFlag());
//        assertEquals(null, data18.getInactiveTimeFrom());
//        assertEquals(null, data18.getInactiveTimeTo());
//        assertEquals(null, data18.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data18.getReserveProviderId());
//        assertEquals("ope01", data18.getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: ページ、ソートを除いた全項目が空文字<br>
//   * 結果: 全データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_全項目空文字() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setGroupReservationId("");
//    request.setDronePortId("");
//    request.setDronePortName("");
//    request.setAircraftId("");
//    request.setRouteReservationId("");
//    request.setTimeFrom("");
//    request.setTimeTo("");
//    request.setReserveProviderId("");
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    var data14 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data14);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24", data14.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data14.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data14.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data14.getRouteReservationId());
//        assertEquals(1, data14.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data14.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data14.getReservationTimeTo());
//        assertEquals("離着陸場名4", data14.getDronePortName());
//        assertEquals("機体名4", data14.getAircraftName());
//        assertEquals(true, data14.getReservationActiveFlag());
//        assertEquals(null, data14.getInactiveTimeFrom());
//        assertEquals(null, data14.getInactiveTimeTo());
//        assertEquals(null, data14.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data14.getReserveProviderId());
//        assertEquals("ope01", data14.getOperatorId());
//
//        var data15 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e15".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data15);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e25", data15.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e35", data15.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e45", data15.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e55", data15.getRouteReservationId());
//        assertEquals(1, data15.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data15.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data15.getReservationTimeTo());
//        assertEquals("離着陸場名5", data15.getDronePortName());
//        assertEquals("機体名5", data15.getAircraftName());
//        assertEquals(true, data15.getReservationActiveFlag());
//        assertEquals(null, data15.getInactiveTimeFrom());
//        assertEquals(null, data15.getInactiveTimeTo());
//        assertEquals(null, data15.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e65", data15.getReserveProviderId());
//        assertEquals("ope01", data15.getOperatorId());
//
//        var data16 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e16".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data16);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e26", data16.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e36", data16.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e46", data16.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e56", data16.getRouteReservationId());
//        assertEquals(1, data16.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data16.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data16.getReservationTimeTo());
//        assertEquals("離着陸場名6", data16.getDronePortName());
//        assertEquals("機体名6", data16.getAircraftName());
//        assertEquals(true, data16.getReservationActiveFlag());
//        assertEquals(null, data16.getInactiveTimeFrom());
//        assertEquals(null, data16.getInactiveTimeTo());
//        assertEquals(null, data16.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e66", data16.getReserveProviderId());
//        assertEquals("ope01", data16.getOperatorId());
//
//        var data17 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e17".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data17);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e27", data17.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e37", data17.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e47", data17.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e57", data17.getRouteReservationId());
//        assertEquals(1, data17.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data17.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data17.getReservationTimeTo());
//        assertEquals("離着陸場名7", data17.getDronePortName());
//        assertEquals("機体名7", data17.getAircraftName());
//        assertEquals(true, data17.getReservationActiveFlag());
//        assertEquals(null, data17.getInactiveTimeFrom());
//        assertEquals(null, data17.getInactiveTimeTo());
//        assertEquals(null, data17.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e67", data17.getReserveProviderId());
//        assertEquals("ope01", data17.getOperatorId());
//
//        var data18 = response.getData().stream()
//            .filter(d ->
// "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e18".equals(d.getDronePortReservationId()))
//            .findFirst().orElse(null);
//        assertNotNull(data18);
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e28", data18.getGroupReservationId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", data18.getDronePortId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", data18.getAircraftId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", data18.getRouteReservationId());
//        assertEquals(1, data18.getUsageType());
//        assertEquals("2025-01-01T01:00:00Z", data18.getReservationTimeFrom());
//        assertEquals("2025-01-01T03:00:00Z", data18.getReservationTimeTo());
//        assertEquals("離着陸場名4", data18.getDronePortName());
//        assertEquals("機体名4", data18.getAircraftName());
//        assertEquals(true, data18.getReservationActiveFlag());
//        assertEquals(null, data18.getInactiveTimeFrom());
//        assertEquals(null, data18.getInactiveTimeTo());
//        assertEquals(null, data18.getVisDronePortCompanyId());
//        assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", data18.getReserveProviderId());
//        assertEquals("ope01", data18.getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 離着陸場予約情報リスト取得の正常系テスト<br>
//   * 条件: ページ、ソートを除いた全項目を指定<br>
//   * 結果: 該当データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetList_全項目指定() {
//    createDBList();
//
//    DronePortReserveInfoListRequestDto request = new DronePortReserveInfoListRequestDto();
//    request.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24");
//    request.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//    request.setDronePortName("離着陸場名4");
//    request.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44");
//    request.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54");
//    request.setTimeFrom("2025-01-01T01:00:00Z");
//    request.setTimeTo("2025-01-01T03:00:00Z");
//    request.setReserveProviderId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64");
//
//    DronePortReserveInfoListResponseDto response =
// dronePortReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(1, response.getData().size());
//
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14",
// response.getData().get(0).getDronePortReservationId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24",
// response.getData().get(0).getGroupReservationId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34",
// response.getData().get(0).getDronePortId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44",
// response.getData().get(0).getAircraftId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54",
// response.getData().get(0).getRouteReservationId());
//    assertEquals(1, response.getData().get(0).getUsageType());
//    assertEquals("2025-01-01T01:00:00Z", response.getData().get(0).getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", response.getData().get(0).getReservationTimeTo());
//    assertEquals("離着陸場名4", response.getData().get(0).getDronePortName());
//    assertEquals("機体名4", response.getData().get(0).getAircraftName());
//    assertEquals(true, response.getData().get(0).getReservationActiveFlag());
//    assertEquals(null, response.getData().get(0).getInactiveTimeFrom());
//    assertEquals(null, response.getData().get(0).getInactiveTimeTo());
//    assertEquals(null, response.getData().get(0).getVisDronePortCompanyId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64",
// response.getData().get(0).getReserveProviderId());
//    assertEquals("ope01", response.getData().get(0).getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getDetail<br>
//   * 試験名: 離着陸場予約情報詳細取得の正常系テスト<br>
//   * 条件: 離着陸場予約IDを指定<br>
//   * 結果: 該当データが取得できる<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  public void testGetdetail() {
//    createDBDetail();
//
//    String dronePortReservationId = "3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14";
//
//    DronePortReserveInfoDetailResponseDto response =
// dronePortReserveInfoServiceImpl.getDetail(dronePortReservationId);
//
//    assertNotNull(response);
//
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14", response.getDronePortReservationId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24", response.getGroupReservationId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34", response.getDronePortId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44", response.getAircraftId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54", response.getRouteReservationId());
//    assertEquals(1, response.getUsageType());
//    assertEquals("2025-01-01T01:00:00Z", response.getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", response.getReservationTimeTo());
//    assertEquals("離着陸場名4", response.getDronePortName());
//    assertEquals("機体名4", response.getAircraftName());
//    assertEquals(true, response.getReservationActiveFlag());
//    assertEquals(null, response.getVisDronePortCompanyId());
//    assertEquals("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64", response.getReserveProviderId());
//    assertEquals("ope01", response.getOperatorId());
//  }
//
//  /** データテンプレート ■登録リクエスト 離着陸場予約登録_テンプレート */
//  private DronePortReserveInfoRegisterListRequestDto createDronePortReserveRegisterDto() {
//    DronePortReserveInfoRegisterListRequestDto dto = new
// DronePortReserveInfoRegisterListRequestDto();
//
//    DronePortReserveInfoRegisterListRequestDto.Element element1 = new
// DronePortReserveInfoRegisterListRequestDto.Element();
//    element1.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
//    element1.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e31");
//    element1.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
//    element1.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
//    element1.setUsageType(1);
//    element1.setReservationTimeFrom("2026-01-01T10:00:00+09:00");
//    element1.setReservationTimeTo("2026-01-01T12:00:00+09:00");
//
//    DronePortReserveInfoRegisterListRequestDto.Element element2 = new
// DronePortReserveInfoRegisterListRequestDto.Element();
//    element2.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
//    element2.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//    element2.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
//    element2.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
//    element2.setUsageType(1);
//    element2.setReservationTimeFrom("2026-01-01T12:00:00+09:00");
//    element2.setReservationTimeTo("2026-01-01T14:00:00+09:00");
//
//    DronePortReserveInfoRegisterListRequestDto.Element element3 = new
// DronePortReserveInfoRegisterListRequestDto.Element();
//    element3.setGroupReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e21");
//    element3.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e33");
//    element3.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41");
//    element3.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e51");
//    element3.setUsageType(1);
//    element3.setReservationTimeFrom("2026-01-01T14:00:00+09:00");
//    element3.setReservationTimeTo("2026-01-01T16:00:00+09:00");
//
//    dto.setData(Arrays.asList(element1, element2, element3));
//    return dto;
//  }
//
//  /** データテンプレート ■更新リクエスト 離着陸場予約更新_テンプレート */
//  private DronePortReserveInfoUpdateRequestDto createDronePortReserveUpdateDto() {
//    DronePortReserveInfoUpdateRequestDto dto = new DronePortReserveInfoUpdateRequestDto();
//    dto.setDronePortReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12");
//    dto.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//    dto.setAircraftId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42");
//    dto.setRouteReservationId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52");
//    dto.setUsageType(1);
//    dto.setReservationTimeFrom("2026-01-01T10:00:00+09:00");
//    dto.setReservationTimeTo("2026-01-01T12:00:00+09:00");
//
//    return dto;
//  }
//
//  /** データテンプレート ■登録更新リクエスト ユーザー情報_テンプレート */
//  private static UserInfoDto createUserInfoDto() {
//    List<RoleInfoDto> roles = new ArrayList<>();
//    RoleInfoDto roleInfoDto = new RoleInfoDto();
//    roleInfoDto.setRoleId("10");
//    roleInfoDto.setRoleName("航路運営者_責任者");
//    roles.add(roleInfoDto);
//
//    UserInfoDto ret = new UserInfoDto();
//    ret.setUserOperatorId("ope99");
//    ret.setRoles(roles);
//    ret.setAffiliatedOperatorId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61");
//    return ret;
//  }
//
//  /** データテンプレート ■登録用 事前準備データ */
//  private void createDBRegister() {
//      // 機体情報作成
//      AircraftInfoEntity aircraftEnt = new AircraftInfoEntity();
//      aircraftEnt.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e41"));
//      aircraftEnt.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      aircraftEnt.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      aircraftEnt.setDeleteFlag(false);
//      aircraftInfoRepository.save(aircraftEnt);
//
//      // 離着陸場情報1作成
//      DronePortInfoEntity dronePortEnt1 = new DronePortInfoEntity();
//      dronePortEnt1.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e31");
//      dronePortEnt1.setPortType(1);
//      dronePortEnt1.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt1.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt1.setDeleteFlag(false);
//      dronePortInfoRepository.save(dronePortEnt1);
//
//      // 離着陸場情報2作成
//      DronePortInfoEntity dronePortEnt2 = new DronePortInfoEntity();
//      dronePortEnt2.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      dronePortEnt2.setPortType(1);
//      dronePortEnt2.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt2.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt2.setDeleteFlag(false);
//      dronePortInfoRepository.save(dronePortEnt2);
//
//      // 離着陸場情報3作成
//      DronePortInfoEntity dronePortEnt3 = new DronePortInfoEntity();
//      dronePortEnt3.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e33");
//      dronePortEnt3.setPortType(1);
//      dronePortEnt3.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt3.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt3.setDeleteFlag(false);
//      dronePortInfoRepository.save(dronePortEnt3);
//
//      // 離着陸場状態情報1作成
//      DronePortStatusEntity statusEnt1 = new DronePortStatusEntity();
//      statusEnt1.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e31");
//      statusEnt1.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt1.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt1.setDeleteFlag(false);
//      dronePortStatusRepository.save(statusEnt1);
//
//      // 離着陸場状態情報2作成
//      DronePortStatusEntity statusEnt2 = new DronePortStatusEntity();
//      statusEnt2.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      statusEnt2.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt2.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt2.setDeleteFlag(false);
//      dronePortStatusRepository.save(statusEnt2);
//
//      // 離着陸場状態情報3作成
//      DronePortStatusEntity statusEnt3 = new DronePortStatusEntity();
//      statusEnt3.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e33");
//      statusEnt3.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt3.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt3.setDeleteFlag(false);
//      dronePortStatusRepository.save(statusEnt3);
//  }
//
//  /** データテンプレート ■更新用 事前準備データ */
//  private void createDBUpdate() {
//      // 機体情報作成
//      AircraftInfoEntity aircraftEnt = new AircraftInfoEntity();
//      aircraftEnt.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      aircraftEnt.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      aircraftEnt.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      aircraftEnt.setDeleteFlag(false);
//      aircraftInfoRepository.save(aircraftEnt);
//
//      // 離着陸場情報作成
//      DronePortInfoEntity dronePortEnt = new DronePortInfoEntity();
//      dronePortEnt.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      dronePortEnt.setPortType(1);
//      dronePortEnt.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt.setDeleteFlag(false);
//      dronePortInfoRepository.save(dronePortEnt);
//
//      // 離着陸場状態情報作成
//      DronePortStatusEntity statusEnt = new DronePortStatusEntity();
//      statusEnt.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      statusEnt.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt.setDeleteFlag(false);
//      dronePortStatusRepository.save(statusEnt);
//
//      // 離着陸場予約情報作成
//      DronePortReserveInfoEntity reserveEnt = new DronePortReserveInfoEntity();
//
// reserveEnt.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12"));
//      reserveEnt.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      reserveEnt.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      reserveEnt.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      reserveEnt.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      reserveEnt.setUsageType(1);
//
// reserveEnt.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      reserveEnt.setReservationActiveFlag(true);
//      reserveEnt.setOperatorId("ope01");
//      reserveEnt.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61"));
//      reserveEnt.setUpdateUserId("user01");
//      reserveEnt.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      reserveEnt.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      reserveEnt.setDeleteFlag(false);
//      reserveEnt.setDronePortInfoEntity(dronePortEnt);
//      reserveEnt.setAircraftInfoEntity(aircraftEnt);
//      dronePortReserveInfoRepository.save(reserveEnt);
//  }
//
//  /** データテンプレート ■更新用 事前準備データ(DB予約事業者IDnull) */
//  private void createDBUpdateReserveProviderIdNull() {
//      // 機体情報作成
//      AircraftInfoEntity aircraftEnt = new AircraftInfoEntity();
//      aircraftEnt.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      aircraftEnt.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      aircraftEnt.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      aircraftEnt.setDeleteFlag(false);
//      aircraftInfoRepository.save(aircraftEnt);
//
//      // 離着陸場情報作成
//      DronePortInfoEntity dronePortEnt = new DronePortInfoEntity();
//      dronePortEnt.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      dronePortEnt.setPortType(1);
//      dronePortEnt.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      dronePortEnt.setDeleteFlag(false);
//      dronePortInfoRepository.save(dronePortEnt);
//
//      // 離着陸場状態情報作成
//      DronePortStatusEntity statusEnt = new DronePortStatusEntity();
//      statusEnt.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      statusEnt.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      statusEnt.setDeleteFlag(false);
//      dronePortStatusRepository.save(statusEnt);
//
//      // 離着陸場予約情報作成（予約事業者IDがnull）
//      DronePortReserveInfoEntity reserveEnt = new DronePortReserveInfoEntity();
//
// reserveEnt.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12"));
//      reserveEnt.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      reserveEnt.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      reserveEnt.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      reserveEnt.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      reserveEnt.setUsageType(1);
//
// reserveEnt.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      reserveEnt.setReservationActiveFlag(true);
//      reserveEnt.setOperatorId("ope01");
//      reserveEnt.setReserveProviderId(null);
//      reserveEnt.setUpdateUserId("user01");
//      reserveEnt.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      reserveEnt.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      reserveEnt.setDeleteFlag(false);
//      reserveEnt.setDronePortInfoEntity(dronePortEnt);
//      reserveEnt.setAircraftInfoEntity(aircraftEnt);
//      dronePortReserveInfoRepository.save(reserveEnt);
//  }
//
//  /** データテンプレート ■削除用 事前準備データ(離着陸場予約ID指定) */
//  private void createDBDeleteDronePortReservationId() {
//      DronePortReserveInfoEntity ent = new DronePortReserveInfoEntity();
//      ent.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12"));
//      ent.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      ent.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      ent.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent.setUsageType(1);
//
// ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent.setReservationActiveFlag(true);
//      ent.setOperatorId("ope01");
//      ent.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61"));
//      ent.setUpdateUserId("user01");
//      ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■削除用 事前準備データ(離着陸場予約ID指定、予約事業者IDnull) */
//  private void createDBDeleteDronePortReservationIdReserveProviderIdNull() {
//      DronePortReserveInfoEntity ent = new DronePortReserveInfoEntity();
//      ent.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12"));
//      ent.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      ent.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      ent.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent.setUsageType(1);
//
// ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent.setReservationActiveFlag(true);
//      ent.setOperatorId("ope01");
//      ent.setReserveProviderId(null);  // ★★★ nullを設定 ★★★
//      ent.setUpdateUserId("user01");
//      ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■削除用 事前準備データ(指定した離着陸場予約IDがDBに存在なし) */
//  private void createDBDeleteGroupReservationId() {
//      DronePortReserveInfoEntity ent = new DronePortReserveInfoEntity();
//      ent.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e99"));
//      ent.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      ent.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      ent.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent.setUsageType(1);
//
// ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent.setReservationActiveFlag(true);
//      ent.setOperatorId("ope01");
//      ent.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61"));
//      ent.setUpdateUserId("user01");
//      ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■削除用 事前準備データ(一括予約ID削除用) */
//  private void createDBDeleteGroupReservationIdMultiple() {
//      // 1件目
//      DronePortReserveInfoEntity ent1 = new DronePortReserveInfoEntity();
//      ent1.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12"));
//      ent1.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      ent1.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      ent1.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent1.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent1.setUsageType(1);
//
// ent1.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent1.setReservationActiveFlag(true);
//      ent1.setOperatorId("ope01");
//      ent1.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61"));
//      ent1.setUpdateUserId("user01");
//      ent1.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent1.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent1.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent1);
//
//      // 2件目
//      DronePortReserveInfoEntity ent2 = new DronePortReserveInfoEntity();
//      ent2.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e13"));
//      ent2.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      ent2.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e33");
//      ent2.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent2.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent2.setUsageType(1);
//
// ent2.setReservationTime(Range.localDateTimeRange("[2025-01-01T03:00:00,2025-01-01T05:00:00)"));
//      ent2.setReservationActiveFlag(true);
//      ent2.setOperatorId("ope01");
//      ent2.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61"));
//      ent2.setUpdateUserId("user01");
//      ent2.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent2.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent2.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent2);
//
//      // 3件目
//      DronePortReserveInfoEntity ent3 = new DronePortReserveInfoEntity();
//      ent3.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14"));
//      ent3.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      ent3.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//      ent3.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent3.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent3.setUsageType(1);
//
// ent3.setReservationTime(Range.localDateTimeRange("[2025-01-01T05:00:00,2025-01-01T07:00:00)"));
//      ent3.setReservationActiveFlag(true);
//      ent3.setOperatorId("ope01");
//      ent3.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61"));
//      ent3.setUpdateUserId("user01");
//      ent3.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent3.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent3.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent3);
//  }
//
//  /** データテンプレート ■削除用 事前準備データ(一括予約ID削除用) */
//  private void createDBDeleteGroupReservationIdMultipleReserveProviderIdNull() {
//      // 1件目
//      DronePortReserveInfoEntity ent1 = new DronePortReserveInfoEntity();
//      ent1.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12"));
//      ent1.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      ent1.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      ent1.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent1.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent1.setUsageType(1);
//
// ent1.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent1.setReservationActiveFlag(true);
//      ent1.setOperatorId("ope01");
//      ent1.setReserveProviderId(null);  // ★★★ nullを設定 ★★★
//      ent1.setUpdateUserId("user01");
//      ent1.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent1.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent1.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent1);
//
//      // 2件目
//      DronePortReserveInfoEntity ent2 = new DronePortReserveInfoEntity();
//      ent2.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e13"));
//      ent2.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      ent2.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e33");
//      ent2.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent2.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent2.setUsageType(1);
//
// ent2.setReservationTime(Range.localDateTimeRange("[2025-01-01T03:00:00,2025-01-01T05:00:00)"));
//      ent2.setReservationActiveFlag(true);
//      ent2.setOperatorId("ope01");
//      ent2.setReserveProviderId(null);  // ★★★ nullを設定 ★★★
//      ent2.setUpdateUserId("user01");
//      ent2.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent2.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent2.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent2);
//
//      // 3件目
//      DronePortReserveInfoEntity ent3 = new DronePortReserveInfoEntity();
//      ent3.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14"));
//      ent3.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e22"));
//      ent3.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//      ent3.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent3.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent3.setUsageType(1);
//
// ent3.setReservationTime(Range.localDateTimeRange("[2025-01-01T05:00:00,2025-01-01T07:00:00)"));
//      ent3.setReservationActiveFlag(true);
//      ent3.setOperatorId("ope01");
//      ent3.setReserveProviderId(null);  // ★★★ nullを設定 ★★★
//      ent3.setUpdateUserId("user01");
//      ent3.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent3.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent3.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent3);
//  }
//
//  /** データテンプレート ■削除用 事前準備データ(一括予約ID削除用・DB該当なし) */
//  private void createDBDeleteGroupReservationIdMultipleNotFound() {
//      // 1件目
//      DronePortReserveInfoEntity ent1 = new DronePortReserveInfoEntity();
//      ent1.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e12"));
//      ent1.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e99"));
//      ent1.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e32");
//      ent1.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent1.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent1.setUsageType(1);
//
// ent1.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent1.setReservationActiveFlag(true);
//      ent1.setOperatorId("ope01");
//      ent1.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61"));
//      ent1.setUpdateUserId("user01");
//      ent1.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent1.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent1.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent1);
//
//      // 2件目
//      DronePortReserveInfoEntity ent2 = new DronePortReserveInfoEntity();
//      ent2.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e13"));
//      ent2.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e99"));
//      ent2.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e33");
//      ent2.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent2.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent2.setUsageType(1);
//
// ent2.setReservationTime(Range.localDateTimeRange("[2025-01-01T03:00:00,2025-01-01T05:00:00)"));
//      ent2.setReservationActiveFlag(true);
//      ent2.setOperatorId("ope01");
//      ent2.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61"));
//      ent2.setUpdateUserId("user01");
//      ent2.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent2.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent2.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent2);
//
//      // 3件目
//      DronePortReserveInfoEntity ent3 = new DronePortReserveInfoEntity();
//      ent3.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14"));
//      ent3.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e99"));
//      ent3.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//      ent3.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e42"));
//      ent3.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e52"));
//      ent3.setUsageType(1);
//
// ent3.setReservationTime(Range.localDateTimeRange("[2025-01-01T05:00:00,2025-01-01T07:00:00)"));
//      ent3.setReservationActiveFlag(true);
//      ent3.setOperatorId("ope01");
//      ent3.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e61"));
//      ent3.setUpdateUserId("user01");
//      ent3.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent3.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent3.setDeleteFlag(false);
//      dronePortReserveInfoRepository.save(ent3);
//  }
//
//  /** データテンプレート ■一覧取得用 事前準備データ */
//  private void createDBList() {
//      // 機体情報作成
//      AircraftInfoEntity ent11 = new AircraftInfoEntity();
//      ent11.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44"));
//      ent11.setAircraftName("機体名4");
//      ent11.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent11.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent11.setDeleteFlag(false);
//      aircraftInfoRepository.save(ent11);
//
//      AircraftInfoEntity ent12 = new AircraftInfoEntity();
//      ent12.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e45"));
//      ent12.setAircraftName("機体名5");
//      ent12.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent12.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent12.setDeleteFlag(false);
//      aircraftInfoRepository.save(ent12);
//
//      AircraftInfoEntity ent13 = new AircraftInfoEntity();
//      ent13.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e46"));
//      ent13.setAircraftName("機体名6");
//      ent13.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent13.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent13.setDeleteFlag(false);
//      aircraftInfoRepository.save(ent13);
//
//      AircraftInfoEntity ent14 = new AircraftInfoEntity();
//      ent14.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e47"));
//      ent14.setAircraftName("機体名7");
//      ent14.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent14.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent14.setDeleteFlag(false);
//      aircraftInfoRepository.save(ent14);
//
//      // 離着陸場情報作成
//      DronePortInfoEntity ent21 = new DronePortInfoEntity();
//      ent21.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//      ent21.setDronePortName("離着陸場名4");
//      ent21.setPortType(1);
//      ent21.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent21.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent21.setDeleteFlag(false);
//      dronePortInfoRepository.save(ent21);
//
//      DronePortInfoEntity ent22 = new DronePortInfoEntity();
//      ent22.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e35");
//      ent22.setDronePortName("離着陸場名5");
//      ent22.setPortType(1);
//      ent22.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent22.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent22.setDeleteFlag(false);
//      dronePortInfoRepository.save(ent22);
//
//      DronePortInfoEntity ent23 = new DronePortInfoEntity();
//      ent23.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e36");
//      ent23.setDronePortName("離着陸場名6");
//      ent23.setPortType(1);
//      ent23.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent23.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent23.setDeleteFlag(false);
//      dronePortInfoRepository.save(ent23);
//
//      DronePortInfoEntity ent24 = new DronePortInfoEntity();
//      ent24.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e37");
//      ent24.setDronePortName("離着陸場名7");
//      ent24.setPortType(1);
//      ent24.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent24.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent24.setDeleteFlag(false);
//      dronePortInfoRepository.save(ent24);
//
//      // 離着陸場状態情報作成
//      DronePortStatusEntity ent31 = new DronePortStatusEntity();
//      ent31.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//      ent31.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent31.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent31.setDeleteFlag(false);
//      dronePortStatusRepository.save(ent31);
//
//      DronePortStatusEntity ent32 = new DronePortStatusEntity();
//      ent32.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e35");
//      ent32.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent32.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent32.setDeleteFlag(false);
//      dronePortStatusRepository.save(ent32);
//
//      DronePortStatusEntity ent33 = new DronePortStatusEntity();
//      ent33.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e36");
//      ent33.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent33.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent33.setDeleteFlag(false);
//      dronePortStatusRepository.save(ent33);
//
//      DronePortStatusEntity ent34 = new DronePortStatusEntity();
//      ent34.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e37");
//      ent34.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent34.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent34.setDeleteFlag(false);
//      dronePortStatusRepository.save(ent34);
//
//      // 離着陸場予約情報作成（エンティティの関連を設定）
//      DronePortReserveInfoEntity ent41 = new DronePortReserveInfoEntity();
//      ent41.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14"));
//      ent41.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24"));
//      ent41.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//      ent41.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44"));
//      ent41.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54"));
//      ent41.setUsageType(1);
//
// ent41.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent41.setReservationActiveFlag(true);
//      ent41.setOperatorId("ope01");
//      ent41.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64"));
//      ent41.setUpdateUserId("user01");
//      ent41.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent41.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent41.setDeleteFlag(false);
//      ent41.setDronePortInfoEntity(ent21);
//      ent41.setAircraftInfoEntity(ent11);
//      dronePortReserveInfoRepository.save(ent41);
//
//      DronePortReserveInfoEntity ent42 = new DronePortReserveInfoEntity();
//      ent42.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e15"));
//      ent42.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e25"));
//      ent42.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e35");
//      ent42.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e45"));
//      ent42.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e55"));
//      ent42.setUsageType(1);
//
// ent42.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent42.setReservationActiveFlag(true);
//      ent42.setOperatorId("ope01");
//      ent42.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e65"));
//      ent42.setUpdateUserId("user01");
//      ent42.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent42.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent42.setDeleteFlag(false);
//      ent42.setDronePortInfoEntity(ent22);
//      ent42.setAircraftInfoEntity(ent12);
//      dronePortReserveInfoRepository.save(ent42);
//
//      DronePortReserveInfoEntity ent43 = new DronePortReserveInfoEntity();
//      ent43.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e16"));
//      ent43.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e26"));
//      ent43.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e36");
//      ent43.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e46"));
//      ent43.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e56"));
//      ent43.setUsageType(1);
//
// ent43.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent43.setReservationActiveFlag(true);
//      ent43.setOperatorId("ope01");
//      ent43.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e66"));
//      ent43.setUpdateUserId("user01");
//      ent43.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent43.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent43.setDeleteFlag(false);
//      ent43.setDronePortInfoEntity(ent23);
//      ent43.setAircraftInfoEntity(ent13);
//      dronePortReserveInfoRepository.save(ent43);
//
//      DronePortReserveInfoEntity ent44 = new DronePortReserveInfoEntity();
//      ent44.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e17"));
//      ent44.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e27"));
//      ent44.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e37");
//      ent44.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e47"));
//      ent44.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e57"));
//      ent44.setUsageType(1);
//
// ent44.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent44.setReservationActiveFlag(true);
//      ent44.setOperatorId("ope01");
//      ent44.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e67"));
//      ent44.setUpdateUserId("user01");
//      ent44.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent44.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent44.setDeleteFlag(false);
//      ent44.setDronePortInfoEntity(ent24);
//      ent44.setAircraftInfoEntity(ent14);
//      dronePortReserveInfoRepository.save(ent44);
//
//      DronePortReserveInfoEntity ent45 = new DronePortReserveInfoEntity();
//      ent45.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e18"));
//      ent45.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e28"));
//      ent45.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//      ent45.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44"));
//      ent45.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54"));
//      ent45.setUsageType(1);
//
// ent45.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent45.setReservationActiveFlag(true);
//      ent45.setOperatorId("ope01");
//      ent45.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64"));
//      ent45.setUpdateUserId("user01");
//      ent45.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent45.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent45.setDeleteFlag(false);
//      ent45.setDronePortInfoEntity(ent21);
//      ent45.setAircraftInfoEntity(ent11);
//      dronePortReserveInfoRepository.save(ent45);
//  }
//
//  /** データテンプレート ■詳細取得用 事前準備データ */
//  private void createDBDetail() {
//      // 機体情報作成
//      AircraftInfoEntity ent11 = new AircraftInfoEntity();
//      ent11.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44"));
//      ent11.setAircraftName("機体名4");
//      ent11.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent11.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent11.setDeleteFlag(false);
//      aircraftInfoRepository.save(ent11);
//
//      // 離着陸場情報作成
//      DronePortInfoEntity ent21 = new DronePortInfoEntity();
//      ent21.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//      ent21.setDronePortName("離着陸場名4");
//      ent21.setPortType(1);
//      ent21.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent21.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent21.setDeleteFlag(false);
//      dronePortInfoRepository.save(ent21);
//
//      // 離着陸場予約情報作成（エンティティの関連を設定）
//      DronePortReserveInfoEntity ent41 = new DronePortReserveInfoEntity();
//      ent41.setDronePortReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e14"));
//      ent41.setGroupReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e24"));
//      ent41.setDronePortId("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e34");
//      ent41.setAircraftId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e44"));
//      ent41.setRouteReservationId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e54"));
//      ent41.setUsageType(1);
//
// ent41.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//      ent41.setReservationActiveFlag(true);
//      ent41.setOperatorId("ope01");
//      ent41.setReserveProviderId(UUID.fromString("3c9f2e7a-1b4d-4e8f-92c1-7a3d5f0b4e64"));
//      ent41.setUpdateUserId("user01");
//      ent41.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent41.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//      ent41.setDeleteFlag(false);
//      ent41.setDronePortInfoEntity(ent21);
//      ent41.setAircraftInfoEntity(ent11);
//      dronePortReserveInfoRepository.save(ent41);
//  }
// }
