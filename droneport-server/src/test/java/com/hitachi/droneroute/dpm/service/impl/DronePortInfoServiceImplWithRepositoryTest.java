// DB接続を行う想定のテストのため、一括テストの対象外とするためにコメントアウト

// package com.hitachi.droneroute.dpm.service.impl;
//
// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.clearInvocations;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import java.sql.Timestamp;
// import java.time.LocalDateTime;
// import java.util.Base64;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.mockito.MockedStatic;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.boot.test.mock.mockito.SpyBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.data.jpa.domain.Specification;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
// import com.hitachi.droneroute.arm.repository.AircraftInfoRepository;
// import com.hitachi.droneroute.cmn.exception.NotFoundException;
// import com.hitachi.droneroute.cmn.settings.SystemSettings;
// import com.hitachi.droneroute.config.dto.UserInfoDto;
// import com.hitachi.droneroute.dpm.constants.DronePortConstants;
// import com.hitachi.droneroute.dpm.dto.DronePortEnvironmentInfoResponseDto;
// import com.hitachi.droneroute.dpm.dto.DronePortInfoDetailResponseDto;
// import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
// import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseDto;
// import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseElement;
// import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
// import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterResponseDto;
// import com.hitachi.droneroute.dpm.entity.DronePortInfoEntity;
// import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;
// import com.hitachi.droneroute.dpm.repository.DronePortInfoRepository;
// import com.hitachi.droneroute.dpm.repository.DronePortReserveInfoRepository;
// import com.hitachi.droneroute.dpm.repository.DronePortStatusRepository;
// import com.hitachi.droneroute.dpm.repository.VisTelemetryInfoRepository;
// import com.hitachi.droneroute.prm.repository.PriceHistoryInfoRepository;
// import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
// import com.hitachi.droneroute.prm.service.PriceInfoSearchListService;
// import com.hitachi.droneroute.prm.service.impl.PriceInfoServiceImpl;
// import com.hitachi.droneroute.prm.validator.PriceInfoValidator;
// import io.hypersistence.utils.hibernate.type.range.Range;
// import net.bytebuddy.utility.dispatcher.JavaDispatcher.Container;
//
// @DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @ActiveProfiles("test-db")
// @Import(DronePortInfoServiceImpl.class)
// public class DronePortInfoServiceImplWithRepositoryTest {
//
//  @SpyBean private DronePortInfoRepository dronePortInfoRepository;
//
//  @SpyBean private VisTelemetryInfoRepository visTelemetryInfoRepository;
//
//  @SpyBean private DronePortStatusRepository dronePortStatusRepository;
//
//  @SpyBean private DronePortReserveInfoRepository dronePortReserveInfoRepository;
//
//  @SpyBean private AircraftInfoRepository aircraftInfoRepository;
//
//  @SpyBean private PriceInfoRepository priceInfoRepository;
//
//  @SpyBean private PriceHistoryInfoRepository priceInfoHistoryRepository;
//
//  @MockBean private PriceInfoSearchListService priceInfoSearchService;
//
//  @Autowired private DronePortInfoServiceImpl dronePortInfoServiceImpl;
//
//  @SpyBean private PriceInfoServiceImpl priceInfoServiceImpl;
//
//  @MockBean private PriceInfoValidator priceInfoValidator;
//
//  @SpyBean private SystemSettings systemSettings;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.openMocks(this);
//  }
//
//  /** ユーザ情報DTO(自事業者) */
//  private UserInfoDto createUserInfoDto_OwnOperator() {
//    UserInfoDto ret = new UserInfoDto();
//    ret.setUserOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
//    ret.setAffiliatedOperatorId("4013e2ad-6cde-432e-985e-50ab5f06bd94");
//    return ret;
//  }
//
//  /** ユーザ情報DTO(他事業者) */
//  private UserInfoDto createUserInfoDto_OtherOperator() {
//    UserInfoDto ret = new UserInfoDto();
//    ret.setUserOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
//    ret.setAffiliatedOperatorId("9913e2ad-6cde-432e-985e-50ab5f06b999");
//    return ret;
//  }
//
//  /** 離着陸場登録更新dtoテンプレート */
//  private DronePortInfoRegisterRequestDto createDronePortInfoRegisterRequestDto() {
//    DronePortInfoRegisterRequestDto ret = new DronePortInfoRegisterRequestDto();
//
//    ret.setDronePortName("離着陸場名");
//    ret.setAddress("設置場所住所");
//    ret.setManufacturer("製造メーカー");
//    ret.setSerialNumber("製造番号");
//    ret.setDronePortManufacturerId("離着陸場メーカーID");
//    ret.setPortType(1);
//    ret.setVisDronePortCompanyId("VIS離着陸場事業者ID");
////    ret.setStoredAircraftId("0a0711a5-ff74-4164-9309-8888b433cf22");
//    ret.setLat(Double.valueOf(11));
//    ret.setLon(Double.valueOf(22));
//    ret.setAlt(Double.valueOf(33));
//    ret.setSupportDroneType("対応機体");
//    ret.setActiveStatus(2);
//    ret.setImageData(
//        "data:image/png;base64," + Base64.getEncoder().encodeToString("testbinary".getBytes()));
//    ret.setImageBinary("testbinary".getBytes());
//    ret.setPublicFlag(true);
//
//    return ret;
//  }
//
//  /** 離着陸場entityテンプレート */
//  private DronePortInfoEntity createDronePortInfoEntity() {
//    DronePortInfoEntity ret = new DronePortInfoEntity();
//
//    ret.setDronePortId("0a0711a5-ff74-4164-9309-8888b433cf22");
//    ret.setDronePortName("離着陸場名");
//    ret.setAddress("設置場所住所");
//    ret.setManufacturer("製造メーカー");
//    ret.setSerialNumber("製造番号");
//    ret.setPortType(1);
//    ret.setVisDronePortCompanyId("VIS離着陸場事業者ID");
//    ret.setLat(Double.valueOf(11));
//    ret.setLon(Double.valueOf(22));
//    ret.setAlt(Double.valueOf(33));
//    ret.setSupportDroneType("対応機体");
//    ret.setImageBinary(null);
//    ret.setImageFormat(null);
//    ret.setPublicFlag(true);
//    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
//    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
//    ret.setCreateTime(now);
//    ret.setUpdateTime(now);
//    ret.setDeleteFlag(Boolean.FALSE);
//
//    return ret;
//  }
//
//  private DronePortStatusEntity createDronePortStatusEntity() {
//    DronePortStatusEntity ret = new DronePortStatusEntity();
//
//    ret.setDronePortId("0a0711a5-ff74-4164-9309-8888b433cf22");
//    ret.setStoredAircraftId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//    ret.setActiveStatus(DronePortConstants.ACTIVE_STATUS_AVAILABLE);
//    ret.setInactiveStatus(DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
//    ret.setInactiveTime(Range.localDateTimeRange("[2024-12-04T03:00:00,)"));
//    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
//    ret.setOperatorId("5cd3e8fe-c1e5-4d86-9756-0cb16c744afc");
//    ret.setCreateTime(now);
//    ret.setUpdateTime(now);
//    ret.setDeleteFlag(Boolean.FALSE);
//
//    return ret;
//  }
//
//  /**
//   * メソッド名: register<br>
//   * 試験名: 正常なリクエストでの離着陸場登録<br>
//   * 条件: 正常なリクエストデータを渡す<br>
//   * 結果: 登録されたデータのIDが返される<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void register_nomal() {
//    // リポジトリにデータ準備不要
//
//    // 引数、戻り値、モックの挙動等準備
//    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//
//    // テスト実施
//    DronePortInfoRegisterResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.register(dto, userDto));
//
//    // 結果照会
//    assertNotNull(result);
//    {
//      ArgumentCaptor<DronePortInfoEntity> capPortEnt =
//          ArgumentCaptor.forClass(DronePortInfoEntity.class);
//      verify(dronePortInfoRepository, times(1)).save(capPortEnt.capture());
//      DronePortInfoEntity entCap = capPortEnt.getValue();
//      assertEquals(entCap.getAddress(), dto.getAddress());
//      assertEquals(entCap.getDronePortName(), dto.getDronePortName());
//      assertEquals(entCap.getManufacturer(), dto.getManufacturer());
//      assertEquals(entCap.getSerialNumber(), dto.getSerialNumber());
//      assertEquals(entCap.getPortType(), dto.getPortType());
//      assertEquals(entCap.getLat(), dto.getLat());
//      assertEquals(entCap.getLon(), dto.getLon());
//      assertEquals(entCap.getAlt(), dto.getAlt());
//      assertEquals(entCap.getPublicFlag(), dto.getPublicFlag());
//      assertNotNull(entCap.getCreateTime());
//      assertNotNull(entCap.getUpdateTime());
//      assertEquals(entCap.getOperatorId(), userDto.getUserOperatorId());
//    }
//  }
//
//  /**
//   * メソッド名: update<br>
//   * 試験名: 正常なリクエストでの離着陸場更新<br>
//   * 条件: 正常なリクエストデータを渡す<br>
//   * 結果: 更新されたデータのIDが返される<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void update_nomal(){
//    // リポジトリにデータ準備
//    String portId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    DronePortInfoEntity portEnt = createDronePortInfoEntity();
//    portEnt.setDronePortId(portId);
//    dronePortInfoRepository.save(portEnt);
//    clearInvocations(dronePortInfoRepository);
//    DronePortStatusEntity statusEnt = createDronePortStatusEntity();
//    dronePortStatusRepository.save(statusEnt);
//    clearInvocations(dronePortStatusRepository);
//
//    // 引数、戻り値、モックの挙動等準備
//    DronePortInfoRegisterRequestDto dto = createDronePortInfoRegisterRequestDto();
//    dto.setDronePortId(portId);
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//
//    // テスト実施
//    DronePortInfoRegisterResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.update(dto, userDto));
//
//    // 結果照会
//    assertEquals(result.getDronePortId(), dto.getDronePortId());
//    {
//      ArgumentCaptor<DronePortInfoEntity> capPortEnt =
//          ArgumentCaptor.forClass(DronePortInfoEntity.class);
//      verify(dronePortInfoRepository, times(1)).save(capPortEnt.capture());
//      DronePortInfoEntity entCap = capPortEnt.getValue();
//      assertEquals(entCap.getAddress(), dto.getAddress());
//      assertEquals(entCap.getDronePortName(), dto.getDronePortName());
//      assertEquals(entCap.getManufacturer(), dto.getManufacturer());
//      assertEquals(entCap.getSerialNumber(), dto.getSerialNumber());
//      assertEquals(entCap.getPortType(), dto.getPortType());
//      assertEquals(entCap.getLat(), dto.getLat());
//      assertEquals(entCap.getLon(), dto.getLon());
//      assertEquals(entCap.getAlt(), dto.getAlt());
//      assertEquals(entCap.getPublicFlag(), dto.getPublicFlag());
//      assertNotNull(entCap.getCreateTime());
//      assertNotNull(entCap.getUpdateTime());
//      assertEquals(entCap.getOperatorId(), userDto.getUserOperatorId());
//    }
//    {
//      ArgumentCaptor<DronePortStatusEntity> capStatusEnt =
//          ArgumentCaptor.forClass(DronePortStatusEntity.class);
//      verify(dronePortStatusRepository, times(1)).save(capStatusEnt.capture());
//      DronePortStatusEntity entCap = capStatusEnt.getValue();
//      assertEquals(entCap.getDronePortId().toString(), portId);
//      assertEquals(entCap.getActiveStatus(), dto.getActiveStatus());
//      assertEquals(entCap.getCreateTime(), statusEnt.getCreateTime());
//      assertNotEquals(entCap.getUpdateTime(), statusEnt.getUpdateTime());
//      assertNotNull(entCap.getUpdateTime());
//      assertEquals(entCap.getOperatorId(), userDto.getUserOperatorId());
//    }
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 自事業者による公開可否フラグ未設定での一覧取得<br>
//   * 条件: 自事業者のユーザとして公開可否フラグ未設定のリクエストを渡す<br>
//   * 結果: 登録されている離着陸場をすべて取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getList_正常終了_自事業者_公開可否フラグnull() {
//    // リポジトリにデータ準備
//    String portId1 = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    String portId2 = "1a0711a5-ff74-4164-9309-8888b433cf22";
//    String portId3 = "2a0711a5-ff74-4164-9309-8888b433cf22";
//
//    // 引数、戻り値、モックの挙動等準備
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
//    dto.setPublicFlag(null);
//
//    // テスト実施
//    DronePortInfoListResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getList(dto, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
//    assertNotNull(result);
//    List<DronePortInfoListResponseElement> resList = result.getData();
//    assertEquals(3, resList.size());
//    assertEquals(portId1, resList.get(0).getDronePortId());
//    assertEquals(portId2, resList.get(1).getDronePortId());
//    assertEquals(portId3, resList.get(2).getDronePortId());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 自事業者による公開可否フラグtrueでの一覧取得<br>
//   * 条件: 自事業者のユーザとして公開可否フラグtrueのリクエストを渡す<br>
//   * 結果: 公開可否フラグtrueで登録されている離着陸場を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getList_正常終了_自事業者_公開可否フラグtrue() {
//    // リポジトリにデータ準備
//    String portId1 = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    String portId2 = "1a0711a5-ff74-4164-9309-8888b433cf22";
//
//    // 引数、戻り値、モックの挙動等準備
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
//    dto.setPublicFlag("true");
//
//    // テスト実施
//    DronePortInfoListResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getList(dto, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
//    assertNotNull(result);
//    List<DronePortInfoListResponseElement> resList = result.getData();
//    assertEquals(2, resList.size());
//    assertEquals(portId1, resList.get(0).getDronePortId());
//    assertEquals(portId2, resList.get(1).getDronePortId());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 自事業者による公開可否フラグfalseでの一覧取得<br>
//   * 条件: 自事業者のユーザとして公開可否フラグfalseのリクエストを渡す<br>
//   * 結果: 公開可否フラグfalseで登録されている離着陸場を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getList_正常終了_自事業者_公開可否フラグfalse() {
//    // リポジトリにデータ準備
//    String portId3 = "2a0711a5-ff74-4164-9309-8888b433cf22";
//
//    // 引数、戻り値、モックの挙動等準備
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
//    dto.setPublicFlag("false");
//
//    // テスト実施
//    DronePortInfoListResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getList(dto, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
//    assertNotNull(result);
//    List<DronePortInfoListResponseElement> resList = result.getData();
//    assertEquals(1, resList.size());
//    assertEquals(portId3, resList.get(0).getDronePortId());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 他事業者による公開可否フラグ未設定での一覧取得<br>
//   * 条件: 他事業者のユーザとして公開可否フラグ未設定のリクエストを渡す<br>
//   * 結果: 公開可否フラグtrueで登録されている離着陸場を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getList_正常終了_他事業者_公開可否フラグnull() {
//    // リポジトリにデータ準備
//    String portId1 = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    String portId2 = "1a0711a5-ff74-4164-9309-8888b433cf22";
//
//    // 引数、戻り値、モックの挙動等準備
//    UserInfoDto userDto = createUserInfoDto_OtherOperator();
//    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
//    dto.setPublicFlag(null);
//
//    // テスト実施
//    DronePortInfoListResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getList(dto, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
//    assertNotNull(result);
//    List<DronePortInfoListResponseElement> resList = result.getData();
//    assertEquals(2, resList.size());
//    assertEquals(portId1, resList.get(0).getDronePortId());
//    assertEquals(portId2, resList.get(1).getDronePortId());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 他事業者による公開可否フラグtrueでの一覧取得<br>
//   * 条件: 他事業者のユーザとして公開可否フラグtrueのリクエストを渡す<br>
//   * 結果: 公開可否フラグtrueで登録されている離着陸場を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getList_正常終了_他事業者_公開可否フラグtrue() {
//    // リポジトリにデータ準備
//    String portId1 = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    String portId2 = "1a0711a5-ff74-4164-9309-8888b433cf22";
//
//    // 引数、戻り値、モックの挙動等準備
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
//    dto.setPublicFlag("true");
//
//    // テスト実施
//    DronePortInfoListResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getList(dto, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
//    assertNotNull(result);
//    List<DronePortInfoListResponseElement> resList = result.getData();
//    assertEquals(2, resList.size());
//    assertEquals(portId1, resList.get(0).getDronePortId());
//    assertEquals(portId2, resList.get(1).getDronePortId());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 他事業者による公開可否フラグfalseでの一覧取得<br>
//   * 条件: 他事業者のユーザとして公開可否フラグfalseのリクエストを渡す<br>
//   * 結果: 公開可否フラグtrueで登録されている離着陸場を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getList_正常終了_他事業者_公開可否フラグfalse() {
//    // リポジトリにデータ準備
//    String portId1 = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    String portId2 = "1a0711a5-ff74-4164-9309-8888b433cf22";
//
//    // 引数、戻り値、モックの挙動等準備
//    UserInfoDto userDto = createUserInfoDto_OtherOperator();
//    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
//    dto.setPublicFlag("false");
//
//    // テスト実施
//    DronePortInfoListResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getList(dto, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
//    assertNotNull(result);
//    List<DronePortInfoListResponseElement> resList = result.getData();
//    assertEquals(2, resList.size());
//    assertEquals(portId1, resList.get(0).getDronePortId());
//    assertEquals(portId2, resList.get(1).getDronePortId());
//  }
//
//  /**
//   * メソッド名: getList<br>
//   * 試験名: 自事業者による公開可否フラグtrueと離着陸場名での一覧取得<br>
//   * 条件: 自事業者のユーザとして公開可否フラグtrueと離着陸場名を指定したリクエストを渡す<br>
//   * 結果: 条件に一致する離着陸場を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getList_正常終了_自事業者_公開可否フラグ_離着陸場名() {
//    // リポジトリにデータ準備
//    String portId1 = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    String portId2 = "1a0711a5-ff74-4164-9309-8888b433cf22";
//
//    // 引数、戻り値、モックの挙動等準備
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//    DronePortInfoListRequestDto dto = new DronePortInfoListRequestDto();
//    dto.setDronePortName("検索離着陸場名");
//    dto.setPublicFlag("true");
//
//    // テスト実施
//    DronePortInfoListResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getList(dto, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(1)).findAll(any(Specification.class));
//    assertNotNull(result);
//    List<DronePortInfoListResponseElement> resList = result.getData();
//    assertEquals(1, resList.size());
//    assertEquals(portId2, resList.get(0).getDronePortId());
//  }
//
//  /**
//   * メソッド名: getDetail<br>
//   * 試験名: 自事業者による公開可否フラグtrueの詳細取得<br>
//   * 条件: 自事業者のユーザとして公開可否フラグtrueの離着陸場IDを指定<br>
//   * 結果: 離着陸場の詳細情報を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getDetail_正常終了_自事業者_公開可否フラグtrue() {
//    // リポジトリにデータ準備
//    String portId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//
//    // テスト実施
//    DronePortInfoDetailResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getDetail(portId, false, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(1)).findByDronePortIdAndDeleteFlagFalse(any());
//    verify(dronePortInfoRepository, times(0)).findOne(any(Specification.class));
//    assertEquals(result.getDronePortId(), portId);
//    assertEquals(result.getPublicFlag(), true);
//  }
//
//  /**
//   * メソッド名: getDetail<br>
//   * 試験名: 自事業者による公開可否フラグfalseの詳細取得<br>
//   * 条件: 自事業者のユーザとして公開可否フラグfalseの離着陸場IDを指定<br>
//   * 結果: 離着陸場の詳細情報を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getDetail_正常終了_自事業者_公開可否フラグfalse() {
//    // リポジトリにデータ準備
//    String portId = "2a0711a5-ff74-4164-9309-8888b433cf22";
//
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//
//    // テスト実施
//    DronePortInfoDetailResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getDetail(portId, false, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(1)).findByDronePortIdAndDeleteFlagFalse(any());
//    verify(dronePortInfoRepository, times(0)).findOne(any(Specification.class));
//    assertEquals(result.getDronePortId(), portId);
//    assertEquals(result.getPublicFlag(), false);
//  }
//
//  /**
//   * メソッド名: getDetail<br>
//   * 試験名: 他事業者による公開可否フラグtrueの詳細取得<br>
//   * 条件: 他事業者のユーザとして公開可否フラグtrueの離着陸場IDを指定<br>
//   * 結果: 離着陸場の詳細情報を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getDetail_正常終了_他事業者_公開可否フラグtrue() {
//    // リポジトリにデータ準備
//    String portId = "0a0711a5-ff74-4164-9309-8888b433cf22";
////    DronePortInfoEntity portEnt1 = createDronePortInfoEntity();
////    portEnt1.setDronePortId(portId);
////    dronePortInfoRepository.save(portEnt1);
////    DronePortStatusEntity statusEnt1 = createDronePortStatusEntity();
////    statusEnt1.setDronePortId(portId);
////    dronePortStatusRepository.save(statusEnt1);
//
//    UserInfoDto userDto = createUserInfoDto_OtherOperator();
//
//    // テスト実施
//    DronePortInfoDetailResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getDetail(portId, false, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(0)).findByDronePortIdAndDeleteFlagFalse(any());
//    verify(dronePortInfoRepository, times(1)).findOne(any(Specification.class));
//    assertEquals(result.getDronePortId(), portId);
//    assertEquals(result.getPublicFlag(), true);
//  }
//
//  /**
//   * メソッド名: getDetail<br>
//   * 試験名: 他事業者による公開可否フラグfalseの詳細取得でNotFoundExceptionが発生<br>
//   * 条件: 他事業者のユーザとして公開可否フラグfalseの離着陸場IDを指定<br>
//   * 結果: NotFoundExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  void getDetail_異常終了_他事業者_公開可否フラグfalse() {
//    // リポジトリにデータ準備
//    String portId = "2a0711a5-ff74-4164-9309-8888b433cf22";
//
//    UserInfoDto userDto = createUserInfoDto_OtherOperator();
//
//    // テスト実施
//    NotFoundException ex =
//        assertThrows(NotFoundException.class, () -> dronePortInfoServiceImpl.getDetail(portId,
// false, userDto));
//
//    // 結果照会
//    verify(dronePortInfoRepository, times(0)).findByDronePortIdAndDeleteFlagFalse(any());
//    verify(dronePortInfoRepository, times(1)).findOne(any(Specification.class));
//    assertEquals("離着陸場情報が見つかりません。離着陸場ID:" + portId, ex.getMessage());
//  }
//
//  /**
//   * メソッド名: getEnvironment<br>
//   * 試験名: 自事業者による公開可否フラグtrueの環境情報取得<br>
//   * 条件: 自事業者のユーザとして公開可否フラグtrueの離着陸場IDを指定<br>
//   * 結果: 離着陸場の環境情報を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getEnvironment_正常終了_自事業者_公開可否フラグtrue() {
//    // リポジトリにデータ準備
//    String portId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//
//    // テスト実施
//    DronePortEnvironmentInfoResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getEnvironment(portId, userDto));
//
//    // 結果照会
//    assertNotNull(result);
//    assertEquals(portId, result.getDronePortId());
//    verify(dronePortInfoRepository, times(1)).findByDronePortIdAndDeleteFlagFalse(any());
//    verify(dronePortInfoRepository, times(0)).findOne(any(Specification.class));
//    verify(visTelemetryInfoRepository, times(1)).findByDroneportId(any());
//  }
//
//  /**
//   * メソッド名: getEnvironment<br>
//   * 試験名: 自事業者による公開可否フラグfalseの環境情報取得<br>
//   * 条件: 自事業者のユーザとして公開可否フラグfalseの離着陸場IDを指定<br>
//   * 結果: 離着陸場の環境情報を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getEnvironment_正常終了_自事業者_公開可否フラグfalse() {
//    // リポジトリにデータ準備
//    String portId = "2a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userDto = createUserInfoDto_OwnOperator();
//
//    // テスト実施
//    DronePortEnvironmentInfoResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getEnvironment(portId, userDto));
//
//    // 結果照会
//    assertNotNull(result);
//    assertEquals(portId, result.getDronePortId());
//    verify(dronePortInfoRepository, times(1)).findByDronePortIdAndDeleteFlagFalse(any());
//    verify(dronePortInfoRepository, times(0)).findOne(any(Specification.class));
//    verify(visTelemetryInfoRepository, times(1)).findByDroneportId(any());
//  }
//
//  /**
//   * メソッド名: getEnvironment<br>
//   * 試験名: 他事業者による公開可否フラグtrueの環境情報取得<br>
//   * 条件: 他事業者のユーザとして公開可否フラグtrueの離着陸場IDを指定<br>
//   * 結果: 離着陸場の環境情報を取得する<br>
//   * テストパターン: 正常系<br>
//   */
//  @Test
//  void getEnvironment_正常終了_他事業者_公開可否フラグtrue() {
//    // リポジトリにデータ準備
//    String portId = "0a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userDto = createUserInfoDto_OtherOperator();
//
//    // テスト実施
//    DronePortEnvironmentInfoResponseDto result =
//        assertDoesNotThrow(() -> dronePortInfoServiceImpl.getEnvironment(portId, userDto));
//    assertNotNull(result);
//    assertEquals(portId, result.getDronePortId());
//    verify(dronePortInfoRepository, times(0)).findByDronePortIdAndDeleteFlagFalse(any());
//    verify(dronePortInfoRepository, times(1)).findOne(any(Specification.class));
//    verify(visTelemetryInfoRepository, times(1)).findByDroneportId(any());
//  }
//
//  /**
//   * メソッド名: getEnvironment<br>
//   * 試験名: 他事業者による公開可否フラグfalseの環境情報取得でNotFoundExceptionが発生<br>
//   * 条件: 他事業者のユーザとして公開可否フラグfalseの離着陸場IDを指定<br>
//   * 結果: NotFoundExceptionが発生する<br>
//   * テストパターン: 異常系<br>
//   */
//  @Test
//  void getEnvironment_異常終了_他事業者_公開可否フラグfalse() {
//    // リポジトリにデータ準備
//    String portId = "2a0711a5-ff74-4164-9309-8888b433cf22";
//    UserInfoDto userDto = createUserInfoDto_OtherOperator();
//
//    // テスト実施
//    NotFoundException ex =
//        assertThrows(NotFoundException.class, () ->
// dronePortInfoServiceImpl.getEnvironment(portId, userDto));
//
//    // 結果照会
//    assertEquals("離着陸場情報が見つかりません。離着陸場ID:" + portId, ex.getMessage());
//    verify(dronePortInfoRepository, times(0)).findByDronePortIdAndDeleteFlagFalse(any());
//    verify(dronePortInfoRepository, times(1)).findOne(any(Specification.class));
//    verify(visTelemetryInfoRepository, times(0)).findByDroneportId(any());
//  }
// }
