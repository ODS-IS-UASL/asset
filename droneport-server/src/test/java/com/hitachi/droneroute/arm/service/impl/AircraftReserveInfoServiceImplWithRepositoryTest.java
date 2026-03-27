// DB接続を行う想定のテストのため、一括テストの対象外とするためにコメントアウト

// package com.hitachi.droneroute.arm.service.impl;
//
// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
//
// import java.sql.Timestamp;
// import java.util.ArrayList;
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
// import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListRequestDto;
// import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListResponseDto;
// import com.hitachi.droneroute.arm.dto.AircraftReserveInfoRequestDto;
// import com.hitachi.droneroute.arm.dto.AircraftReserveInfoResponseDto;
// import com.hitachi.droneroute.arm.entity.AircraftInfoEntity;
// import com.hitachi.droneroute.arm.entity.AircraftReserveInfoEntity;
// import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
// import com.hitachi.droneroute.arm.repository.AircraftReserveInfoRepository;
// import com.hitachi.droneroute.cmn.exception.NotFoundException;
// import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
// import com.hitachi.droneroute.cmn.settings.SystemSettings;
// import com.hitachi.droneroute.config.dto.RoleInfoDto;
// import com.hitachi.droneroute.config.dto.UserInfoDto;
//
// import io.hypersistence.utils.hibernate.type.range.Range;
//
/// ** AircraftReserveInfoServiceImplクラスの単体テスト */
// @SpringBootTest
// @ActiveProfiles("test")
// public class AircraftReserveInfoServiceImplWithRepositoryTest {
//
//  // DB接続のためコメントアウト
//  @Autowired
//  private AircraftReserveInfoServiceImpl aircraftReserveInfoServiceImpl;
//
//  @Autowired
//  private AircraftInfoRepository aircraftInfoRepository;
//
//  @Autowired
//  private AircraftReserveInfoRepository aircraftReserveInfoRepository;
//
//  @SpyBean
//  private SystemSettings systemSettings;
//
//  @BeforeEach
//  public void setUp() {
//    MockitoAnnotations.openMocks(this);
//
//    aircraftReserveInfoRepository.deleteAll();
//    aircraftInfoRepository.deleteAll();
//  }
//
//  /**
//   * メソッド名: postData<br>
//   * 試験名: 機体予約情報を正常に登録できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 登録されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPostData_機体予約ID予約事業者ID() {
//    createAircraftInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto1();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.postData(request, userInfo);
//
//    assertNotNull(response);
//    assertDoesNotThrow(() -> UUID.fromString(response.getAircraftReservationId()));
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営責任者予約事業者ID一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営担当者予約事業者ID一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営責任者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営担当者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営責任者DB予約事業者IDnull() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity2();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営担当者DB予約事業者IDnull() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity2();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_運航事業責任者予約事業者ID一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_運航事業担当者予約事業者ID一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_運航事業者予約事業者ID一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 更新に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 更新データがNullの場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testPutData_運航事業責任者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception = assertThrows(ServiceErrorException.class,
//        () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 更新に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 更新データがNullの場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testPutData_運航事業担当者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception = assertThrows(ServiceErrorException.class,
//        () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 更新に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 更新データがNullの場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testPutData_運航事業者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception = assertThrows(ServiceErrorException.class,
//        () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 更新に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 更新データがNullの場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testPutData_運航事業責任者DB予約事業者IDnull() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity2();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    ServiceErrorException exception = assertThrows(ServiceErrorException.class,
//        () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 更新に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 更新データがNullの場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testPutData_運航事業担当者DB予約事業者IDnull() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity2();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    ServiceErrorException exception = assertThrows(ServiceErrorException.class,
//        () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 更新に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 更新データがNullの場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testPutData_運航事業者DB予約事業者IDnull() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity2();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    ServiceErrorException exception = assertThrows(ServiceErrorException.class,
//        () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_一括予約IDnull() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    request.setGroupReservationId(null);
//    UserInfoDto userInfo = createUserInfoDto();
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_一括予約ID空文字() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    request.setGroupReservationId("");
//    UserInfoDto userInfo = createUserInfoDto();
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営者予約事業者ID一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("1");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("1");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_関係者予約事業者ID一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("3");
//
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営者航路運営責任者航路運営担当者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
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
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_航路運営者運航事業者運航事業担当者関係者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
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
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_運航事業者運航事業責任者運航事業担当者関係者予約事業者ID一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
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
//    AircraftReserveInfoResponseDto response =
//        aircraftReserveInfoServiceImpl.putData(request, userInfo);
//
//    assertNotNull(response);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf12", response.getAircraftReservationId());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_関係者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("3");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception = assertThrows(ServiceErrorException.class,
//        () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: putData<br>
//   * 試験名: 機体予約情報を正常に更新できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoRequestDto<br>
//   * 結果: 更新されたAircraftReserveInfoResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testPutData_運航事業者運航事業責任者運航事業担当者関係者予約事業者ID不一致() {
//    createAircraftInfoEntity2();
//    createAircraftReserveInfoEntity1();
//
//    AircraftReserveInfoRequestDto request = createAircraftReserveInfoRequestDto2();
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
//    ServiceErrorException exception = assertThrows(ServiceErrorException.class,
//        () -> aircraftReserveInfoServiceImpl.putData(request, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営責任者予約事業ID一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        true, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営担当者予約事業ID一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        true, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営責任者予約事業ID不一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        true, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営担当者予約事業ID不一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        true, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営責任者_DB予約事業IDnull_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        true, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営担当者_DB予約事業IDnull_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        true, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業責任者予約事業ID一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        true, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業担当者予約事業ID一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        true, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業者予約事業ID一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        true, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業責任者予約事業ID不一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), true, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業担当者予約事業ID不一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), true, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業者予約事業ID不一致_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), true, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業責任者_DB予約事業IDnull_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), true, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業担当者_DB予約事業IDnull_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), true, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業者_DB予約事業IDnull_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), true, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_DB存在なし_機体予約ID使用フラグtrue() {
//    createAircraftReserveInfoEntity3();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf12";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    NotFoundException exception =
//        assertThrows(NotFoundException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), true, userInfo));
//
//    assertEquals("機体予約情報が見つかりません。機体予約ID:0a0711a5-ff74-4164-9309-8888b433cf12",
//        exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営責任者予約事業ID一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営担当者予約事業ID一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営責任者予約事業ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営担当者予約事業ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営責任者_DB予約事業IDnull_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営担当者_DB予約事業IDnull_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("11");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業責任者予約事業ID一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業担当者予約事業ID一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業者予約事業ID一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業責任者予約事業者ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), false, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業担当者予約事業者ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), false, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業者予約事業者ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), false, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業責任者_DB予約事業IDnull_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("20");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), false, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業担当者_DB予約事業IDnull_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("21");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), false, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業者_DB予約事業IDnull_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity2();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("2");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), false, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_DB存在なし_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity4();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    NotFoundException exception =
//        assertThrows(NotFoundException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), false, userInfo));
//
//    assertEquals("機体予約情報が見つかりません。一括予約ID:0a0711a5-ff74-4164-9309-8888b433cf22",
//        exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営者予約事業ID一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("1");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営者予約事業ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("1");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_関係者予約事業ID一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//    userInfo.getRoles().get(0).setRoleId("3");
//
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営者航路運営責任者航路運営担当者予約事業者ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
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
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_航路運営者運航事業者運航事業担当者関係者予約事業者ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
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
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 機体予約情報を正常に削除できることを確認する<br>
//   * 条件: 正常なaircraftReservationId<br>
//   * 結果: 例外が発生しない<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業者運航事業責任者運航事業担当者関係者予約事業者ID一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
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
//    assertDoesNotThrow(() ->
// aircraftReserveInfoServiceImpl.deleteData(aircraftReserveId.toString(),
//        false, userInfo));
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_関係者予約事業者ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userInfo = createUserInfoDto();
//
//    userInfo.getRoles().get(0).setRoleId("3");
//    userInfo.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    ServiceErrorException exception =
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), false, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: deleteData<br>
//   * 試験名: 削除に失敗した場合に例外が発生することを確認する<br>
//   * 条件: 予約事業者IDが不一致の場合<br>
//   * 結果: ServiceErrorExceptionが発生する<br>
//   * テストパターン：異常系<br>
//   */
//  @Test
//  public void testDeleteData_運航事業者運航事業責任者運航事業担当者関係者予約事業者ID不一致_機体予約ID使用フラグfalse() {
//    createAircraftReserveInfoEntity1();
//
//    String aircraftReserveId = "0a0711a5-ff74-4164-9309-8888b433cf22";
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
//        assertThrows(ServiceErrorException.class, () -> aircraftReserveInfoServiceImpl
//            .deleteData(aircraftReserveId.toString(), false, userInfo));
//
//    assertEquals("認可エラー。予約事業者IDが一致しません。", exception.getMessage());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoListRequestDto<br>
//   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testGetList_一括予約IDnull() {
//    createAircraftInfoEntity3();
//    createAircraftInfoEntity4();
//    createAircraftInfoEntity5();
//    createAircraftInfoEntity6();
//
//    createAircraftReserveInfoEntity5();
//    createAircraftReserveInfoEntity6();
//    createAircraftReserveInfoEntity7();
//    createAircraftReserveInfoEntity8();
//    createAircraftReserveInfoEntity9();
//
//    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
//    request.setGroupReservationId(null);
//
//    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    // IDでフィルタリングして全項目を検証
//    var data14 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf14".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data14);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf24", data14.getGroupReservationId());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf34", data14.getAircraftId());
//    assertEquals("2025-01-01T01:00:00Z", data14.getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", data14.getReservationTimeTo());
//    assertEquals("機体名4", data14.getAircraftName());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf44", data14.getReserveProviderId());
//    assertEquals("ope01", data14.getOperatorId());
//
//    var data15 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf15".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data15);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf25", data15.getGroupReservationId());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf35", data15.getAircraftId());
//    assertEquals("2025-01-01T01:00:00Z", data15.getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", data15.getReservationTimeTo());
//    assertEquals("機体名5", data15.getAircraftName());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf45", data15.getReserveProviderId());
//    assertEquals("ope01", data15.getOperatorId());
//
//    var data16 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf16".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data16);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf26", data16.getGroupReservationId());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf36", data16.getAircraftId());
//    assertEquals("2025-01-01T01:00:00Z", data16.getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", data16.getReservationTimeTo());
//    assertEquals("機体名6", data16.getAircraftName());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf46", data16.getReserveProviderId());
//    assertEquals("ope01", data16.getOperatorId());
//
//    var data17 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf17".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data17);
//    assertEquals(null, data17.getGroupReservationId());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf37", data17.getAircraftId());
//    assertEquals("2025-01-01T01:00:00Z", data17.getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", data17.getReservationTimeTo());
//    assertEquals("機体名7", data17.getAircraftName());
//    assertEquals(null, data17.getReserveProviderId());
//    assertEquals("ope01", data17.getOperatorId());
//
//    var data18 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf18".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data18);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf28", data18.getGroupReservationId());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf34", data18.getAircraftId());
//    assertEquals("2025-01-01T01:00:00Z", data18.getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", data18.getReservationTimeTo());
//    assertEquals("機体名4", data18.getAircraftName());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf44", data18.getReserveProviderId());
//    assertEquals("ope01", data18.getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 一括予約IDが空文字の場合の検索処理<br>
//   * 条件: groupReservationId=""のAircraftReserveInfoListRequestDto<br>
//   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testGetList_一括予約ID空文字() {
//    aircraftReserveInfoRepository.deleteAll();
//    aircraftInfoRepository.deleteAll();
//    createAircraftInfoEntity3();
//    createAircraftInfoEntity4();
//    createAircraftInfoEntity5();
//    createAircraftInfoEntity6();
//
//    createAircraftReserveInfoEntity5();
//    createAircraftReserveInfoEntity6();
//    createAircraftReserveInfoEntity7();
//    createAircraftReserveInfoEntity8();
//    createAircraftReserveInfoEntity9();
//
//    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
//    request.setGroupReservationId("");
//
//    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    // IDでフィルタリングして全項目を検証
//    var data14 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf14".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data14);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf44", data14.getReserveProviderId());
//
//    var data15 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf15".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data15);
//
//    var data16 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf16".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data16);
//
//    var data17 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf17".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data17);
//
//    var data18 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf18".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data18);
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 予約事業者IDがnullの場合の検索処理<br>
//   * 条件: reserveProviderId=nullのAircraftReserveInfoListRequestDto<br>
//   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testGetList_予約事業者IDnull() {
//    createAircraftInfoEntity3();
//    createAircraftInfoEntity4();
//    createAircraftInfoEntity5();
//    createAircraftInfoEntity6();
//
//    createAircraftReserveInfoEntity5();
//    createAircraftReserveInfoEntity6();
//    createAircraftReserveInfoEntity7();
//    createAircraftReserveInfoEntity8();
//    createAircraftReserveInfoEntity9();
//
//    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
//    request.setReserveProviderId(null);
//
//    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    // IDでフィルタリングして全項目を検証
//    var data14 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf14".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data14);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf44", data14.getReserveProviderId());
//
//    var data15 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf15".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data15);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf45", data15.getReserveProviderId());
//
//    var data16 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf16".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data16);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf46", data16.getReserveProviderId());
//
//    var data17 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf17".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data17);
//    assertEquals(null, data17.getReserveProviderId());
//
//    var data18 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf18".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data18);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf44", data18.getReserveProviderId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 予約事業者IDが空文字の場合の検索処理<br>
//   * 条件: reserveProviderId=""のAircraftReserveInfoListRequestDto<br>
//   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testGetList_予約事業者ID空文字() {
//    createAircraftInfoEntity3();
//    createAircraftInfoEntity4();
//    createAircraftInfoEntity5();
//    createAircraftInfoEntity6();
//
//    createAircraftReserveInfoEntity5();
//    createAircraftReserveInfoEntity6();
//    createAircraftReserveInfoEntity7();
//    createAircraftReserveInfoEntity8();
//    createAircraftReserveInfoEntity9();
//
//    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
//    request.setReserveProviderId("");
//
//    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(5, response.getData().size());
//
//    // IDでフィルタリングして全項目を検証
//    var data14 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf14".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data14);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf44", data14.getReserveProviderId());
//
//    var data15 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf15".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data15);
//
//    var data16 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf16".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data16);
//
//    var data17 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf17".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data17);
//
//    var data18 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf18".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data18);
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 予約事業者IDでDB検索して該当データがある場合の検索処理<br>
//   * 条件: reserveProviderId指定のAircraftReserveInfoListRequestDto<br>
//   * 結果: 該当データが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testGetList_予約事業者ID_DB該当あり() {
//    createAircraftInfoEntity3();
//    createAircraftInfoEntity4();
//    createAircraftInfoEntity5();
//    createAircraftInfoEntity6();
//
//    createAircraftReserveInfoEntity5();
//    createAircraftReserveInfoEntity6();
//    createAircraftReserveInfoEntity7();
//    createAircraftReserveInfoEntity8();
//    createAircraftReserveInfoEntity9();
//
//    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
//    request.setReserveProviderId("0a0711a5-ff74-4164-9309-8888b433cf44");
//
//    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(2, response.getData().size());
//
//    // IDでフィルタリングして全項目を検証
//    var data14 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf14".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data14);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf24", data14.getGroupReservationId());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf34", data14.getAircraftId());
//    assertEquals("2025-01-01T01:00:00Z", data14.getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", data14.getReservationTimeTo());
//    assertEquals("機体名4", data14.getAircraftName());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf44", data14.getReserveProviderId());
//    assertEquals("ope01", data14.getOperatorId());
//
//    var data18 = response.getData().stream()
//        .filter(d -> "0a0711a5-ff74-4164-9309-8888b433cf18".equals(d.getAircraftReservationId()))
//        .findFirst().orElse(null);
//    assertNotNull(data18);
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf28", data18.getGroupReservationId());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf34", data18.getAircraftId());
//    assertEquals("2025-01-01T01:00:00Z", data18.getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", data18.getReservationTimeTo());
//    assertEquals("機体名4", data18.getAircraftName());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf44", data18.getReserveProviderId());
//    assertEquals("ope01", data18.getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 予約事業者IDでDB検索して該当データがない場合の検索処理<br>
//   * 条件: DB該当なしのreserveProviderId指定のAircraftReserveInfoListRequestDto<br>
//   * 結果: 空のリストが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testGetList_予約事業者ID_DB該当なし() {
//    createAircraftInfoEntity3();
//    createAircraftInfoEntity4();
//    createAircraftInfoEntity5();
//    createAircraftInfoEntity6();
//
//    createAircraftReserveInfoEntity5();
//    createAircraftReserveInfoEntity6();
//    createAircraftReserveInfoEntity7();
//    createAircraftReserveInfoEntity8();
//    createAircraftReserveInfoEntity9();
//
//    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
//    request.setReserveProviderId("0a0711a5-ff74-4164-9309-8888b433cf99");
//
//    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);
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
//   * 試験名: 機体予約情報リストを正常に取得できることを確認する<br>
//   * 条件: 正常なAircraftReserveInfoListRequestDto<br>
//   * 結果: 取得されたAircraftReserveInfoListResponseDtoが返される<br>
//   * テストパターン：正常系<br>
//   */
//  @Test
//  public void testGetList_一括予約ID予約事業者ID_DB該当あり() {
//    createAircraftInfoEntity3();
//    createAircraftInfoEntity4();
//    createAircraftInfoEntity5();
//    createAircraftInfoEntity6();
//
//    createAircraftReserveInfoEntity5();
//    createAircraftReserveInfoEntity6();
//    createAircraftReserveInfoEntity7();
//    createAircraftReserveInfoEntity8();
//    createAircraftReserveInfoEntity9();
//
//    AircraftReserveInfoListRequestDto request = new AircraftReserveInfoListRequestDto();
//    request.setGroupReservationId("0a0711a5-ff74-4164-9309-8888b433cf24");
//    request.setReserveProviderId("0a0711a5-ff74-4164-9309-8888b433cf44");
//
//    AircraftReserveInfoListResponseDto response = aircraftReserveInfoServiceImpl.getList(request);
//
//    assertNotNull(response);
//    assertEquals(1, response.getData().size());
//
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf14",
//        response.getData().get(0).getAircraftReservationId());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf24",
//        response.getData().get(0).getGroupReservationId());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf34",
// response.getData().get(0).getAircraftId());
//    assertEquals("2025-01-01T01:00:00Z", response.getData().get(0).getReservationTimeFrom());
//    assertEquals("2025-01-01T03:00:00Z", response.getData().get(0).getReservationTimeTo());
//    assertEquals("機体名4", response.getData().get(0).getAircraftName());
//    assertEquals("0a0711a5-ff74-4164-9309-8888b433cf44",
//        response.getData().get(0).getReserveProviderId());
//    assertEquals("ope01", response.getData().get(0).getOperatorId());
//
//    assertNull(response.getPerPage());
//    assertNull(response.getCurrentPage());
//    assertNull(response.getLastPage());
//    assertNull(response.getTotal());
//  }
//
//  /** データテンプレート ■登録更新リクエスト 機体予約登録更新_テンプレート */
//  private static AircraftReserveInfoRequestDto createAircraftReserveInfoRequestDto1() {
//    AircraftReserveInfoRequestDto ret = new AircraftReserveInfoRequestDto();
//    ret.setAircraftReservationId("0a0711a5-ff74-4164-9309-8888b433cf11");
//    ret.setGroupReservationId("0a0711a5-ff74-4164-9309-8888b433cf21");
//    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf31");
//    ret.setReservationTimeFrom("2026-01-01T10:00:00+09:00");
//    ret.setReservationTimeTo("2026-01-01T12:00:00+09:00");
//    return ret;
//  }
//
//  /** データテンプレート ■登録更新リクエスト 機体予約登録更新_テンプレート */
//  private static AircraftReserveInfoRequestDto createAircraftReserveInfoRequestDto2() {
//    AircraftReserveInfoRequestDto ret = new AircraftReserveInfoRequestDto();
//    ret.setAircraftReservationId("0a0711a5-ff74-4164-9309-8888b433cf12");
//    ret.setGroupReservationId("0a0711a5-ff74-4164-9309-8888b433cf99");
//    ret.setAircraftId("0a0711a5-ff74-4164-9309-8888b433cf32");
//    ret.setReservationTimeFrom("2026-01-01T10:00:00+09:00");
//    ret.setReservationTimeTo("2026-01-01T12:00:00+09:00");
//    return ret;
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
//    ret.setAffiliatedOperatorId("0a0711a5-ff74-4164-9309-8888b433cf41");
//    return ret;
//  }
//
//  /** データテンプレート ■機体情報Entity 事前準備データ1 */
//  private void createAircraftInfoEntity1() {
//    AircraftInfoEntity ent = new AircraftInfoEntity();
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf31"));
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//    aircraftInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体情報Entity 事前準備データ2 */
//  private void createAircraftInfoEntity2() {
//    AircraftInfoEntity ent = new AircraftInfoEntity();
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf32"));
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//    aircraftInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体情報Entity 事前準備データ3 */
//  private void createAircraftInfoEntity3() {
//    AircraftInfoEntity ent = new AircraftInfoEntity();
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf34"));
//    ent.setAircraftName("機体名4");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//    aircraftInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体情報Entity 事前準備データ4 */
//  private void createAircraftInfoEntity4() {
//    AircraftInfoEntity ent = new AircraftInfoEntity();
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf35"));
//    ent.setAircraftName("機体名5");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//    aircraftInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体情報Entity 事前準備データ5 */
//  private void createAircraftInfoEntity5() {
//    AircraftInfoEntity ent = new AircraftInfoEntity();
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf36"));
//    ent.setAircraftName("機体名6");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//    aircraftInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体情報Entity 事前準備データ6 */
//  private void createAircraftInfoEntity6() {
//    AircraftInfoEntity ent = new AircraftInfoEntity();
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf37"));
//    ent.setAircraftName("機体名7");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//    aircraftInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体予約情報Entity 事前準備データ1 */
//  private void createAircraftReserveInfoEntity1() {
//    AircraftReserveInfoEntity ent = new AircraftReserveInfoEntity();
//    ent.setAircraftReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf12"));
//    ent.setGroupReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf32"));
//    ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//    ent.setReserveProviderId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf41"));
//    ent.setOperatorId("ope02");
//    ent.setUpdateUserId("user01");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//
//    aircraftReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体予約情報Entity 事前準備データ2 */
//  private void createAircraftReserveInfoEntity2() {
//    AircraftReserveInfoEntity ent = new AircraftReserveInfoEntity();
//    ent.setAircraftReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf12"));
//    ent.setGroupReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf32"));
//    ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//    ent.setReserveProviderId(null);
//    ent.setOperatorId("ope02");
//    ent.setUpdateUserId("user01");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//
//    aircraftReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体予約情報Entity 事前準備データ3 */
//  private void createAircraftReserveInfoEntity3() {
//    AircraftReserveInfoEntity ent = new AircraftReserveInfoEntity();
//    ent.setAircraftReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf99"));
//    ent.setGroupReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf32"));
//    ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//    ent.setReserveProviderId(null);
//    ent.setOperatorId("ope02");
//    ent.setUpdateUserId("user01");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//
//    aircraftReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体予約情報Entity 事前準備データ4 */
//  private void createAircraftReserveInfoEntity4() {
//    AircraftReserveInfoEntity ent = new AircraftReserveInfoEntity();
//    ent.setAircraftReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf12"));
//    ent.setGroupReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf99"));
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf32"));
//    ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//    ent.setReserveProviderId(null);
//    ent.setOperatorId("ope02");
//    ent.setUpdateUserId("user01");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//
//    aircraftReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体予約情報Entity 事前準備データ5 */
//  private void createAircraftReserveInfoEntity5() {
//    AircraftReserveInfoEntity ent = new AircraftReserveInfoEntity();
//    ent.setAircraftReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf14"));
//    ent.setGroupReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf24"));
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf34"));
//    ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//    ent.setReserveProviderId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf44"));
//    ent.setOperatorId("ope01");
//    ent.setUpdateUserId("user01");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//
//    aircraftReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体予約情報Entity 事前準備データ6 */
//  private void createAircraftReserveInfoEntity6() {
//    AircraftReserveInfoEntity ent = new AircraftReserveInfoEntity();
//    ent.setAircraftReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf15"));
//    ent.setGroupReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf25"));
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf35"));
//    ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//    ent.setReserveProviderId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf45"));
//    ent.setOperatorId("ope01");
//    ent.setUpdateUserId("user01");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//
//    aircraftReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体予約情報Entity 事前準備データ7 */
//  private void createAircraftReserveInfoEntity7() {
//    AircraftReserveInfoEntity ent = new AircraftReserveInfoEntity();
//    ent.setAircraftReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf16"));
//    ent.setGroupReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf26"));
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf36"));
//    ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//    ent.setReserveProviderId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf46"));
//    ent.setOperatorId("ope01");
//    ent.setUpdateUserId("user01");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//
//    aircraftReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体予約情報Entity 事前準備データ8 */
//  private void createAircraftReserveInfoEntity8() {
//    AircraftReserveInfoEntity ent = new AircraftReserveInfoEntity();
//    ent.setAircraftReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf17"));
//    ent.setGroupReservationId(null);
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf37"));
//    ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//    ent.setReserveProviderId(null);
//    ent.setOperatorId("ope01");
//    ent.setUpdateUserId("user01");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//
//    aircraftReserveInfoRepository.save(ent);
//  }
//
//  /** データテンプレート ■機体予約情報Entity 事前準備データ9 */
//  private void createAircraftReserveInfoEntity9() {
//    AircraftReserveInfoEntity ent = new AircraftReserveInfoEntity();
//    ent.setAircraftReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf18"));
//    ent.setGroupReservationId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf28"));
//    ent.setAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf34"));
//    ent.setReservationTime(Range.localDateTimeRange("[2025-01-01T01:00:00,2025-01-01T03:00:00)"));
//    ent.setReserveProviderId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf44"));
//    ent.setOperatorId("ope01");
//    ent.setUpdateUserId("user01");
//    ent.setCreateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setUpdateTime(Timestamp.valueOf("2026-01-01 10:00:00"));
//    ent.setDeleteFlag(false);
//
//    aircraftReserveInfoRepository.save(ent);
//  }
// }
