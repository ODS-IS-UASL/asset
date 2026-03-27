package com.hitachi.droneroute.arm.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.arm.dto.AircraftInfoModelInfoListElementReq;
import com.hitachi.droneroute.cmn.util.StringUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

/** AircraftInfoSpecificationTestの単体テスト */
class AircraftInfoSpecificationTest {

  /**
   * メソッド名: aircraftNameContains<br>
   * 試験名: 機体名の検索条件未設定<br>
   * 条件: 機体名がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testAircraftNameContains_Null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    String aircraftName = null;

    Specification<Object> result = spec.aircraftNameContains(aircraftName);
    assertNull(result);
  }

  /**
   * メソッド名: aircraftNameContains<br>
   * 試験名: 機体名による検索条件が正しく生成される<br>
   * 条件: 機体名が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testAircraftNameContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    String aircraftName = "TestName";
    when(builder.like(eq(root.get("aircraftName")), contains(aircraftName))).thenReturn(predicate);

    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.aircraftNameContains(aircraftName);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get("aircraftName")), contains(aircraftName));
  }

  /**
   * メソッド名: manufacturerContains<br>
   * 試験名: 製造メーカーの検索条件未設定<br>
   * 条件: 製造メーカーがnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testManifacturerContains_Null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    String manufacturer = null;

    Specification<Object> result = spec.manufacturerContains(manufacturer);
    assertNull(result);
  }

  /**
   * メソッド名: manufacturerContains<br>
   * 試験名: 製造メーカーによる検索条件が正しく生成される<br>
   * 条件: 製造メーカーが指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testManifacturerContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    String manufacturer = "TestName";
    when(builder.like(eq(root.get("manifacturer")), contains(manufacturer))).thenReturn(predicate);

    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.manufacturerContains(manufacturer);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get("manifacturer")), contains(manufacturer));
  }

  /**
   * メソッド名: manufacturingNumberContains<br>
   * 試験名: 製造番号の検索条件未設定<br>
   * 条件: 製造番号がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testManifacturingNumberContains_Null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    String manufacturering_number = null;

    Specification<Object> result = spec.manufacturingNumberContains(manufacturering_number);
    assertNull(result);
  }

  /**
   * メソッド名: manufacturingNumberContains<br>
   * 試験名: 製造番号による検索条件が正しく生成される<br>
   * 条件: 製造番号が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testManifacturingNumberContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    String manufacturer_number = "TestNumber";
    when(builder.like(eq(root.get("manufacturingNumber")), contains(manufacturer_number)))
        .thenReturn(predicate);

    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.manufacturingNumberContains(manufacturer_number);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get("manufacturingNumber")), contains(manufacturer_number));
  }

  /**
   * メソッド名: aircraftTypeContains<br>
   * 試験名: 機体の種類の検索条件未設定<br>
   * 条件: 機体の種類がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testAircraftTypeContains_Null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Integer[] aircraftType = null;

    Specification<Object> result = spec.aircraftTypeContains(aircraftType);
    assertNull(result);
  }

  /**
   * メソッド名: aircraftTypeContains<br>
   * 試験名: 機体の種類の検索条件未設定（空配列）<br>
   * 条件: 機体の種類が空配列<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testAircraftTypeContains_Blank() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Integer[] aircraftType = {};

    Specification<Object> result = spec.aircraftTypeContains(aircraftType);
    assertNull(result);
  }

  /**
   * メソッド名: aircraftTypeContains<br>
   * 試験名: 機体の種類による検索条件が正しく生成される<br>
   * 条件: 機体の種類が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testAircraftTypeContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Integer[] aircraftType = {1, 2, 3, 4, 5, 6};
    String columnName = "aircraftType";
    when(builder.equal(root.get(columnName), aircraftType[0])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), aircraftType[1])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), aircraftType[2])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), aircraftType[3])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), aircraftType[4])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), aircraftType[5])).thenReturn(predicate);
    when(builder.or(any(Predicate[].class))).thenReturn(predicate);

    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.aircraftTypeContains(aircraftType);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), aircraftType[0]);
    verify(builder).equal(root.get(columnName), aircraftType[1]);
    verify(builder).equal(root.get(columnName), aircraftType[2]);
    verify(builder).equal(root.get(columnName), aircraftType[3]);
    verify(builder).equal(root.get(columnName), aircraftType[4]);
    verify(builder).equal(root.get(columnName), aircraftType[5]);
    verify(builder)
        .or(new Predicate[] {predicate, predicate, predicate, predicate, predicate, predicate});
  }

  /**
   * メソッド名: certiticationEqual<br>
   * 試験名: 機体認証の検索条件未設定<br>
   * 条件: 機体認証がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testCertiticationContains_Null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Boolean certifications = null;

    Specification<Object> result = spec.certiticationEqual(certifications);
    assertNull(result);
  }

  /**
   * メソッド名: certiticationEqual<br>
   * 試験名: 機体認証による検索条件が正しく生成される<br>
   * 条件: 機体認証が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testCertiticationContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Boolean certifications = true;
    when(builder.equal(root.get("certification"), certifications)).thenReturn(predicate);

    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.certiticationEqual(certifications);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("certification"), certifications);
  }

  /**
   * メソッド名: dipsRegistrationCodeContains<br>
   * 試験名: DIPS登録記号の検索条件未設定<br>
   * 条件: DIPS登録記号がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testDipsRegistrationCodeContains_Null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    String dipsRegistrationCode = null;

    Specification<Object> result = spec.dipsRegistrationCodeContains(dipsRegistrationCode);
    assertNull(result);
  }

  /**
   * メソッド名: dipsRegistrationCodeContains<br>
   * 試験名: DIPS登録記号による検索条件が正しく生成される<br>
   * 条件: DIPS登録記号が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testDipsRegistrationCodeContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    String dipsRegistrationCode = "123456";
    when(builder.like(eq(root.get("dipsRegistrationCode")), contains(dipsRegistrationCode)))
        .thenReturn(predicate);

    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.dipsRegistrationCodeContains(dipsRegistrationCode);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get("dipsRegistrationCode")), contains(dipsRegistrationCode));
  }

  /**
   * メソッド名: ownerTypeContains<br>
   * 試験名: 機体所有種別の検索条件未設定<br>
   * 条件: 機体所有種別がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testOwnerTypeContains_Null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Integer[] ownerType = null;

    Specification<Object> result = spec.ownerTypeContains(ownerType);
    assertNull(result);
  }

  /**
   * メソッド名: ownerTypeContains<br>
   * 試験名: 機体所有種別の検索条件未設定（空配列）<br>
   * 条件: 機体所有種別が空配列<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testOwnerTypeContains_Blank() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Integer[] ownerType = {};

    Specification<Object> result = spec.ownerTypeContains(ownerType);
    assertNull(result);
  }

  /**
   * メソッド名: ownerTypeContains<br>
   * 試験名: 機体所有種別による検索条件が正しく生成される<br>
   * 条件: 機体所有種別が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testOwnerTypeContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Integer[] ownerType = {1, 2};
    String columnName = "ownerType";
    when(builder.equal(root.get(columnName), ownerType[0])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), ownerType[1])).thenReturn(predicate);
    when(builder.or(any(Predicate[].class))).thenReturn(predicate);

    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.ownerTypeContains(ownerType);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), ownerType[0]);
    verify(builder).equal(root.get(columnName), ownerType[1]);
    verify(builder).or(new Predicate[] {predicate, predicate});
  }

  /**
   * メソッド名: ownerIdEquals<br>
   * 試験名: 所有者IDの検索条件未設定<br>
   * 条件: 所有者IDがnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testOwnerIdEquals_Null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    UUID ownerId = null;

    Specification<Object> result = spec.ownerIdEquals(ownerId);
    assertNull(result);
  }

  /**
   * メソッド名: ownerIdEquals<br>
   * 試験名: 所有者IDによる検索条件が正しく生成される<br>
   * 条件: 所有者IDが指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testOwnerIdEquals_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    UUID ownerId = UUID.randomUUID();
    when(builder.equal(root.get("ownerId"), ownerId)).thenReturn(predicate);

    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.ownerIdEquals(ownerId);
    // 結果確認
    assertNotNull(specification);
    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: deleteFlagEqual<br>
   * 試験名: 削除フラグによる検索条件が正しく生成される<br>
   * 条件: 削除フラグがfalse<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testDeleteFlagEqual() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    boolean flag = false;

    Specification<Object> result = spec.deleteFlagEqual(flag);
    assertNotNull(result);
  }

  /**
   * メソッド名: startLatGreaterThanEqual<br>
   * 試験名: 最小緯度による検索条件が正しく生成される<br>
   * 条件: 最小緯度が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testStartLatGreaterThanEqual_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    // モック条件設定
    Double param = 35.0d;
    String columnName = "lat";
    when(builder.greaterThanOrEqualTo(root.get(columnName), param)).thenReturn(predicate);

    // 処理実行
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.startLatGreaterThanEqual(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).greaterThanOrEqualTo(root.get(columnName), param);
  }

  /**
   * メソッド名: startLatGreaterThanEqual<br>
   * 試験名: 最小緯度の検索条件未設定<br>
   * 条件: 最小緯度がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testStartLatGreaterThanEqual_NULL() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Double minlat = null;
    Specification<Object> result = spec.startLatGreaterThanEqual(minlat);
    assertNull(result);
  }

  /**
   * メソッド名: startLonGreaterThanEqual<br>
   * 試験名: 最小経度による検索条件が正しく生成される<br>
   * 条件: 最小経度が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testStartLonGreaterThanEqual_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    // モック条件設定
    Double param = 135.0d;
    String columnName = "lon";
    when(builder.greaterThanOrEqualTo(root.get(columnName), param)).thenReturn(predicate);

    // 処理実行
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.startLonGreaterThanEqual(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).greaterThanOrEqualTo(root.get(columnName), param);
  }

  /**
   * メソッド名: startLonGreaterThanEqual<br>
   * 試験名: 最小経度の検索条件未設定<br>
   * 条件: 最小経度がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testStartLonGreaterThanEqual_NULL() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Double minlon = null;
    Specification<Object> result = spec.startLonGreaterThanEqual(minlon);
    assertNull(result);
  }

  /**
   * メソッド名: endLatLessThanEqual<br>
   * 試験名: 最大緯度による検索条件が正しく生成される<br>
   * 条件: 最大緯度が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testEndLatLessThanEqual_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    // モック条件設定
    Double param = 40.0d;
    String columnName = "lat";
    when(builder.lessThanOrEqualTo(root.get(columnName), param)).thenReturn(predicate);

    // 処理実行
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.endLatLessThanEqual(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).lessThanOrEqualTo(root.get(columnName), param);
  }

  /**
   * メソッド名: endLatLessThanEqual<br>
   * 試験名: 最大緯度の検索条件未設定<br>
   * 条件: 最大緯度がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testEndLatLessThanEqual_NULL() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Double maxlat = null;
    Specification<Object> result = spec.endLatLessThanEqual(maxlat);
    assertNull(result);
  }

  /**
   * メソッド名: endLonLessThanEqual<br>
   * 試験名: 最大経度による検索条件が正しく生成される<br>
   * 条件: 最大経度が指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testEndLonLessThanEqual_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    // モック条件設定
    Double param = 140.0d;
    String columnName = "lon";
    when(builder.lessThanOrEqualTo(root.get(columnName), param)).thenReturn(predicate);

    // 処理実行
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Specification<Object> specification = spec.endLonLessThanEqual(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).lessThanOrEqualTo(root.get(columnName), param);
  }

  /**
   * メソッド名: modelInfosMatch<br>
   * 試験名: モデル情報リストによる検索条件が正しく生成される<br>
   * 条件: モデル情報リストが指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void modelInfosMatch_正常() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.modelInfosMatch(null));
    List<AircraftInfoModelInfoListElementReq> reqList = new ArrayList<>();
    AircraftInfoModelInfoListElementReq ele1 = new AircraftInfoModelInfoListElementReq();
    ele1.setManufacturer("製造メーカー1");
    ele1.setModelNumber("MD12345V1");
    reqList.add(ele1);
    AircraftInfoModelInfoListElementReq ele2 = new AircraftInfoModelInfoListElementReq();
    ele2.setManufacturer("製造メーカー1");
    ele2.setModelNumber("MD12345V2");
    reqList.add(ele2);
    AircraftInfoModelInfoListElementReq ele3 = new AircraftInfoModelInfoListElementReq();
    ele3.setManufacturer("製造メーカー2");
    ele3.setModelNumber("MD12345V1");
    reqList.add(ele3);

    assertNotNull(spec.modelInfosMatch(reqList));
  }

  /**
   * メソッド名: endLonLessThanEqual<br>
   * 試験名: 最大経度の検索条件未設定<br>
   * 条件: 最大経度がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testEndLonLessThanEqual_NULL() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    Double maxlat = null;
    Specification<Object> result = spec.endLonLessThanEqual(maxlat);

    assertNull(result);
  }

  /**
   * メソッド名: aircraftNameContains<br>
   * 試験名: 機体名の検索条件未設定（nullまたは空文字）<br>
   * 条件: 機体名がnullまたは空文字<br>
   * 結果: nullが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void AircraftNameContains_nullOrEmpty() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.aircraftNameContains(null));
    assertNull(spec.aircraftNameContains(""));
  }

  /**
   * メソッド名: manufacturerContains<br>
   * 試験名: 製造メーカーの検索条件未設定（nullまたは空文字）<br>
   * 条件: 製造メーカーがnullまたは空文字<br>
   * 結果: nullが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void ManifacturerContains_nullOrEmpty() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.manufacturerContains(null));
    assertNull(spec.manufacturerContains(""));
  }

  /**
   * メソッド名: manufacturingNumberContains<br>
   * 試験名: 製造番号の検索条件未設定（nullまたは空文字）<br>
   * 条件: 製造番号がnullまたは空文字<br>
   * 結果: nullが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void ManifacturingNumberContains_nullOrEmpty() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.manufacturerContains(null));
    assertNull(spec.manufacturerContains(""));
  }

  /**
   * メソッド名: aircraftTypeContains<br>
   * 試験名: 機体の種類の検索条件未設定（nullまたは空配列）<br>
   * 条件: 機体の種類がnullまたは空配列<br>
   * 結果: nullが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void AircraftTypeContains_nullOrEmpty() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.aircraftTypeContains(null));
    assertNull(
        spec.aircraftTypeContains(StringUtils.stringToIntegerArray(""))); // IT-0002, CT-010? 検索方法修正
  }

  /**
   * メソッド名: certiticationEqual<br>
   * 試験名: 機体認証の検索条件未設定<br>
   * 条件: 機体認証がnull<br>
   * 結果: nullが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void CertiticationContains_null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.certiticationEqual(null));
  }

  /**
   * メソッド名: dipsRegistrationCodeContains<br>
   * 試験名: DIPS登録記号の検索条件未設定（nullまたは空文字）<br>
   * 条件: DIPS登録記号がnullまたは空文字<br>
   * 結果: nullが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void DipsRegistrationCodeContains_nullOrEmpty() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.dipsRegistrationCodeContains(null));
    assertNull(spec.dipsRegistrationCodeContains(""));
  }

  /**
   * メソッド名: ownerTypeContains<br>
   * 試験名: 機体所有種別の検索条件未設定（nullまたは空配列）<br>
   * 条件: 機体所有種別がnullまたは空配列<br>
   * 結果: nullが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void OwnerTypeContains_nullOrEmpty() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.ownerTypeContains(null));
    assertNull(
        spec.ownerTypeContains(StringUtils.stringToIntegerArray(""))); // IT-0002, CT-010? 検索方法修正
  }

  /**
   * メソッド名: ownerIdEquals<br>
   * 試験名: 所有者IDの検索条件未設定<br>
   * 条件: 所有者IDがnull<br>
   * 結果: nullが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void OwnerIdContains_null() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.ownerIdEquals(null));
  }

  /**
   * メソッド名: modelInfosMatch<br>
   * 試験名: モデル情報リストの検索条件未設定（nullまたは空）<br>
   * 条件: モデル情報リストがnullまたは空<br>
   * 結果: nullが返される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void modelInfosMatch_nullOrEmpty() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    assertNull(spec.modelInfosMatch(null));
    List<AircraftInfoModelInfoListElementReq> reqList =
        new ArrayList<AircraftInfoModelInfoListElementReq>();
    assertNull(spec.modelInfosMatch(reqList));
    reqList.add(null);
    spec.modelInfosMatch(reqList);
  }

  /**
   * メソッド名: modelInfosMatch<br>
   * 試験名: モデル情報リストの検索条件未設定（null要素のみ）<br>
   * 条件: モデル情報リストがnull要素のみ<br>
   * 結果: Specificationが生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void modelInfosMatch_nullElement() {
    // バリデータではじかれるため実際にはありえない
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    List<AircraftInfoModelInfoListElementReq> reqList =
        new ArrayList<AircraftInfoModelInfoListElementReq>();
    reqList.add(null);
    assertNotNull(spec.modelInfosMatch(reqList));
  }

  /**
   * メソッド名: deleteFlagEqual<br>
   * 試験名: 削除フラグがtrueの場合の検索条件が正しく生成される<br>
   * 条件: 削除フラグがtrue<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: エッジケース<br>
   */
  @Test
  void deleteFlagEqual_true() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("deleteFlag"), true)).thenReturn(predicate);

    Specification<Object> specification = spec.deleteFlagEqual(true);
    Predicate result = specification.toPredicate(root, query, builder);

    assertNotNull(result);
    verify(builder).equal(root.get("deleteFlag"), true);
  }

  /**
   * メソッド名: deleteFlagEqual<br>
   * 試験名: 削除フラグがfalseの場合の検索条件が正しく生成される<br>
   * 条件: 削除フラグがfalse<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: エッジケース<br>
   */
  @Test
  void deleteFlagEqual_false() {
    AircraftInfoSpecification<Object> spec = new AircraftInfoSpecification<>();
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("deleteFlag"), false)).thenReturn(predicate);

    Specification<Object> specification = spec.deleteFlagEqual(false);
    Predicate result = specification.toPredicate(root, query, builder);

    assertNotNull(result);
    verify(builder).equal(root.get("deleteFlag"), false);
  }
}
