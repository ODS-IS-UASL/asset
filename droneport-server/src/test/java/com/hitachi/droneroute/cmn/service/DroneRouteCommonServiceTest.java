package com.hitachi.droneroute.cmn.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hitachi.droneroute.cmn.entity.CommonEntity;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

/** DroneRouteCommonServiceクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
public class DroneRouteCommonServiceTest {

  private static final DroneRouteCommonService TEST_CLASS = new DroneRouteCommonService() {};

  /**
   * メソッド名: createPageRequest<br>
   * 試験名: 正常な引数<br>
   * 条件: strPerPage,strPage,sortが正常値の場合<br>
   * 結果: 正しいPageableが返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void createPageRequestTest() {
    String strPerPage = "1";
    String strPage = "1";
    String sortOrders = "1";
    String sortColumns = "column";
    Sort sort = TEST_CLASS.createSort(sortOrders, sortColumns, null);

    Pageable result =
        assertDoesNotThrow(() -> TEST_CLASS.createPageRequest(strPerPage, strPage, sort));
    assertNotNull(result);
  }

  /**
   * メソッド名: createPageRequest<br>
   * 試験名: strPageがnull<br>
   * 条件: strPageがnullの場合<br>
   * 結果: Pageableがnullで返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void createPageRequestTest_page_null() {
    String strPerPage = "1";
    String strPage = null;
    String sortOrders = "1";
    String sortColumns = "column";
    Sort sort = TEST_CLASS.createSort(sortOrders, sortColumns, null);

    Pageable result =
        assertDoesNotThrow(() -> TEST_CLASS.createPageRequest(strPerPage, strPage, sort));
    assertNull(result);
  }

  /**
   * メソッド名: createPageRequest<br>
   * 試験名: strPageが0<br>
   * 条件: strPageが0の場合<br>
   * 結果: Pageableがnullで返される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void createPageRequestTest_page_zero() {
    String strPerPage = "1";
    String strPage = "0";
    String sortOrders = "1";
    String sortColumns = "column";
    Sort sort = TEST_CLASS.createSort(sortOrders, sortColumns, null);

    Pageable result =
        assertDoesNotThrow(() -> TEST_CLASS.createPageRequest(strPerPage, strPage, sort));
    assertNull(result);
  }

  /**
   * メソッド名: checkUpdatePermissionByOperatorId<br>
   * 試験名: 正常パターン<br>
   * 条件: オペレータIDが一致<br>
   * 結果: 正常終了する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void checkUpdatePermissionByOperatorIdTest() {
    String opeId = UUID.randomUUID().toString();
    CommonEntity cmnEnt = new CommonEntity();

    cmnEnt.setOperatorId(opeId);

    assertDoesNotThrow(() -> TEST_CLASS.checkUpdatePermissionByOperatorId(cmnEnt, opeId));
  }

  /**
   * メソッド名: checkUpdatePermissionByOperatorId<br>
   * 試験名: エンティティのオペレータIDがnull<br>
   * 条件: エンティティのオペレータIDがnullの場合<br>
   * 結果: ServiceErrorExceptionが発生し適切なエラーメッセージ返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void checkUpdatePermissionByOperatorIdTest_null() {
    String opeId = UUID.randomUUID().toString();
    CommonEntity cmnEnt = new CommonEntity();
    cmnEnt.setOperatorId(null);

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> TEST_CLASS.checkUpdatePermissionByOperatorId(cmnEnt, opeId));
    assertTrue(exception.getMessage().contains("処理対象のデータにオペレータIDに値が設定されていません。"));
  }

  /**
   * メソッド名: checkUpdatePermissionByOperatorId<br>
   * 試験名: オペレータIDが不一致<br>
   * 条件: オペレータIDが不一致の場合<br>
   * 結果: ServiceErrorExceptionが発生し適切なエラーメッセージ返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void checkUpdatePermissionByOperatorIdTest_mismatch() {
    String opeId = UUID.randomUUID().toString();
    CommonEntity cmnEnt = new CommonEntity();
    cmnEnt.setOperatorId(UUID.randomUUID().toString());

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () -> TEST_CLASS.checkUpdatePermissionByOperatorId(cmnEnt, opeId));
    assertTrue(exception.getMessage().contains("認可エラー。オペレータIDが一致しません。"));
  }

  /**
   * メソッド名: checkUpdatePermissionByAffiliatedOperatorId<br>
   * 試験名: 正常パターン<br>
   * 条件: 予約事業者IDが一致の場合<br>
   * 結果: 正常終了する<br>
   * テストパターン：正常系<br>
   */
  @Test
  void checkUpdatePermissionByAffiliatedOperatorId() {
    UUID reserveProviderId = UUID.randomUUID();
    String affiliatedOperatorId = reserveProviderId.toString();

    assertDoesNotThrow(
        () ->
            TEST_CLASS.checkUpdatePermissionByAffiliatedOperatorId(
                reserveProviderId, affiliatedOperatorId));
  }

  /**
   * メソッド名: checkUpdatePermissionByAffiliatedOperatorId<br>
   * 試験名: 予約事業者IDが不一致<br>
   * 条件: 予約事業者IDが不一致の場合<br>
   * 結果: ServiceErrorExceptionが発生し適切なエラーメッセージ返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void checkUpdatePermissionByAffiliatedOperatorId_mismatch() {
    UUID reserveProviderId = UUID.randomUUID();
    String affiliatedOperatorId = UUID.randomUUID().toString();

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () ->
                TEST_CLASS.checkUpdatePermissionByAffiliatedOperatorId(
                    reserveProviderId, affiliatedOperatorId));
    assertTrue(exception.getMessage().contains("認可エラー。予約事業者IDが一致しません。"));
  }

  /**
   * メソッド名: checkUpdatePermissionByAffiliatedOperatorId<br>
   * 試験名: 対象のエンティティの予約事業者IDがnull<br>
   * 条件: 対象のエンティティの予約事業者IDがnullの場合<br>
   * 結果: ServiceErrorExceptionが発生し適切なエラーメッセージ返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void checkUpdatePermissionByAffiliatedOperatorId_affiliatedOperatorId_null() {
    UUID reserveProviderId = UUID.randomUUID();
    String affiliatedOperatorId = null;

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () ->
                TEST_CLASS.checkUpdatePermissionByAffiliatedOperatorId(
                    reserveProviderId, affiliatedOperatorId));
    assertTrue(exception.getMessage().contains("認可エラー。予約事業者IDが一致しません。"));
  }

  /**
   * メソッド名: checkUpdatePermissionByAffiliatedOperatorId<br>
   * 試験名: ユーザの事業者IDがnull<br>
   * 条件: ユーザの事業者IDがnullの場合<br>
   * 結果: ServiceErrorExceptionが発生し適切なエラーメッセージ返される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void checkUpdatePermissionByAffiliatedOperatorId_reserveProviderId_null() {
    UUID reserveProviderId = null;
    String affiliatedOperatorId = UUID.randomUUID().toString();

    ServiceErrorException exception =
        assertThrows(
            ServiceErrorException.class,
            () ->
                TEST_CLASS.checkUpdatePermissionByAffiliatedOperatorId(
                    reserveProviderId, affiliatedOperatorId));
    assertTrue(exception.getMessage().contains("認可エラー。予約事業者IDが一致しません。"));
  }
}
