package com.hitachi.droneroute.dpm.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

/** DronePortReserveInfoSpecificationクラスの単体テスト */
public class DronePortReserveInfoSpecificationTest {

  /**
   * メソッド名: dronePortTypeContains<br>
   * 試験名: 離着陸場種別で検索する<br>
   * 条件: 離着陸場種別がnull、空配列、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系、境界値<br>
   */
  @Test
  public void testDronePortTypeContains_正常() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // Null case
    assertNull(spec.dronePortTypeContains(null));

    // Empty array case
    assertNull(spec.dronePortTypeContains(new Integer[] {}));

    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Integer[] params = {1, 2, 3};
    String columnName = "dronePortType";
    when(root.get("dronePortInfoEntity")).thenReturn(root).thenReturn(root).thenReturn(root);
    when(builder.equal(root.get(columnName), params[0])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), params[1])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), params[2])).thenReturn(predicate);
    when(builder.or(any(Predicate[].class))).thenReturn(predicate);
    // 処理実行
    Specification<Object> specification = spec.dronePortTypeContains(params);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), params[0]);
    verify(builder).equal(root.get(columnName), params[1]);
    verify(builder).equal(root.get(columnName), params[2]);
    verify(builder).or(new Predicate[] {predicate, predicate, predicate});
  }

  /**
   * メソッド名: groupReservationIdEqual<br>
   * 試験名: 一括予約IDで検索する<br>
   * 条件: 一括予約IDがnull、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void groupReservationIdIdEqualTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // Null case
    assertNull(spec.groupReservationIdEqual(null));

    // Normal case
    UUID id = UUID.randomUUID();
    Specification<Object> specification = spec.groupReservationIdEqual(id);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("groupReservationId"), id)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: dronePortIdEqual<br>
   * 試験名: 離着陸場IDで検索する<br>
   * 条件: 離着陸場IDがnull、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void dronePortIdEqualTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // Null case
    assertNull(spec.dronePortIdEqual(null));

    // Normal case
    String id = UUID.randomUUID().toString();
    Specification<Object> specification = spec.dronePortIdEqual(id);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("dronePortId"), id)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: dronePortNameContains<br>
   * 試験名: 離着陸場名で検索する<br>
   * 条件: 離着陸場名がnull、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系<br>
   */
  @Test
  void dronePortNameContainsTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();
    // Null case
    assertNull(spec.dronePortNameContains(null));

    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    // モック条件設定
    String param = "TestDronePortName";
    String columnName = "dronePortName";
    when(root.get("dronePortInfoEntity")).thenReturn(root).thenReturn(root).thenReturn(root);
    when(builder.like(eq(root.get(columnName)), contains(param))).thenReturn(predicate);

    // 処理実行
    Specification<Object> specification = spec.dronePortNameContains(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get(columnName)), contains(param));
  }

  /**
   * メソッド名: dronePortReserveIdNotEqual<br>
   * 試験名: 離着陸場予約IDが含まれない<br>
   * 条件: 離着陸場予約IDがnull、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void dronePortReserveIdNotEqualTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // Null case
    assertNull(spec.dronePortReserveIdNotEqual(null));

    // Normal case
    UUID id = UUID.randomUUID();
    Specification<Object> specification = spec.dronePortReserveIdNotEqual(id);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.notEqual(root.get("dronePortReservationId"), id)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: aircraftIdEqual<br>
   * 試験名: 使用機体IDで検索する<br>
   * 条件: 使用機体IDがnull、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void aircraftIdEqualTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // Null case
    assertNull(spec.aircraftIdEqual(null));

    // Normal case
    UUID id = UUID.randomUUID();
    Specification<Object> specification = spec.aircraftIdEqual(id);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("aircraftId"), id)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: routeReservationIdEqual<br>
   * 試験名: 紐付け航路IDで検索する<br>
   * 条件: 航路IDがnull、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void routeIdEqualTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // Null case
    assertNull(spec.routeReservationIdEqual(null));

    // Normal case
    UUID id = UUID.randomUUID();
    Specification<Object> specification = spec.routeReservationIdEqual(id);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("routeId"), id)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 予約日時範囲で検索する<br>
   * 条件: timeFromとtimeToがnull、片方null、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系、境界値<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void tsrangeOverlapTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // Both null case
    assertNull(spec.tsrangeOverlap(null, null));

    Specification<Object> specification;
    // timeFrom null case
    Timestamp timeTo = new Timestamp(System.currentTimeMillis());
    {
      specification = spec.tsrangeOverlap(null, timeTo);
      assertNotNull(specification);

      // Mocking CriteriaBuilder and Root
      Root<Object> root = mock(Root.class);
      CriteriaQuery<?> query = mock(CriteriaQuery.class);
      CriteriaBuilder builder = mock(CriteriaBuilder.class);
      Predicate predicate = mock(Predicate.class);
      Expression<String> expression = mock(Expression.class);
      Path path = mock(Path.class);

      when(builder.isTrue(any())).thenReturn(predicate);
      when(builder.literal(anyString())).thenReturn(expression);
      when(root.get(eq("reservation_time"))).thenReturn(path);
      when(builder.function(eq("fn_tsrange_overlap"), eq(Boolean.class), eq(path), eq(expression)))
          .thenReturn(predicate);

      assertEquals(predicate, specification.toPredicate(root, query, builder));
    }
    // timeTo null case
    Timestamp timeFrom = new Timestamp(System.currentTimeMillis());
    {
      specification = spec.tsrangeOverlap(timeFrom, null);
      assertNotNull(specification);

      // Mocking CriteriaBuilder and Root
      Root<Object> root = mock(Root.class);
      CriteriaQuery<?> query = mock(CriteriaQuery.class);
      CriteriaBuilder builder = mock(CriteriaBuilder.class);
      Predicate predicate = mock(Predicate.class);
      Expression<String> expression = mock(Expression.class);
      Path path = mock(Path.class);

      when(builder.isTrue(any())).thenReturn(predicate);
      when(builder.literal(anyString())).thenReturn(expression);
      when(root.get(eq("reservation_time"))).thenReturn(path);
      when(builder.function(eq("fn_tsrange_overlap"), eq(Boolean.class), eq(path), eq(expression)))
          .thenReturn(predicate);

      assertEquals(predicate, specification.toPredicate(root, query, builder));
    }

    // Both non-null case
    {
      specification = spec.tsrangeOverlap(timeFrom, timeTo);
      assertNotNull(specification);

      // Mocking CriteriaBuilder and Root
      Root<Object> root = mock(Root.class);
      CriteriaQuery<?> query = mock(CriteriaQuery.class);
      CriteriaBuilder builder = mock(CriteriaBuilder.class);
      Predicate predicate = mock(Predicate.class);
      Expression<String> expression = mock(Expression.class);
      Path path = mock(Path.class);

      when(builder.isTrue(any())).thenReturn(predicate);
      when(builder.literal(anyString())).thenReturn(expression);
      when(root.get(eq("reservation_time"))).thenReturn(path);
      when(builder.function(eq("fn_tsrange_overlap"), eq(Boolean.class), eq(path), eq(expression)))
          .thenReturn(predicate);

      assertEquals(predicate, specification.toPredicate(root, query, builder));
    }
  }

  /**
   * メソッド名: tsrangeInclude2<br>
   * 試験名: 予約日時範囲で検索する<br>
   * 条件: timeFromとtimeToがnull、片方null、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系、境界値<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void tsrangeInclude2Test() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // Both null case
    assertNull(spec.tsrangeInclude2(null, null));

    Specification<Object> specification;
    // timeFrom null case
    Timestamp timeTo = new Timestamp(System.currentTimeMillis());
    {
      specification = spec.tsrangeInclude2(null, timeTo);
      assertNotNull(specification);

      // Mocking CriteriaBuilder and Root
      Root<Object> root = mock(Root.class);
      CriteriaQuery<?> query = mock(CriteriaQuery.class);
      CriteriaBuilder builder = mock(CriteriaBuilder.class);
      Predicate predicate = mock(Predicate.class);
      Expression<String> expression = mock(Expression.class);
      Path path = mock(Path.class);

      when(builder.isTrue(any())).thenReturn(predicate);
      when(builder.literal(anyString())).thenReturn(expression);
      when(root.get(eq("reservation_time"))).thenReturn(path);
      when(builder.function(eq("fn_tsrange_include2"), eq(Boolean.class), eq(path), eq(expression)))
          .thenReturn(predicate);

      assertEquals(predicate, specification.toPredicate(root, query, builder));
    }
    // timeTo null case
    Timestamp timeFrom = new Timestamp(System.currentTimeMillis());
    {
      specification = spec.tsrangeInclude2(timeFrom, null);
      assertNotNull(specification);

      // Mocking CriteriaBuilder and Root
      Root<Object> root = mock(Root.class);
      CriteriaQuery<?> query = mock(CriteriaQuery.class);
      CriteriaBuilder builder = mock(CriteriaBuilder.class);
      Predicate predicate = mock(Predicate.class);
      Expression<String> expression = mock(Expression.class);
      Path path = mock(Path.class);

      when(builder.isTrue(any())).thenReturn(predicate);
      when(builder.literal(anyString())).thenReturn(expression);
      when(root.get(eq("reservation_time"))).thenReturn(path);
      when(builder.function(eq("fn_tsrange_include2"), eq(Boolean.class), eq(path), eq(expression)))
          .thenReturn(predicate);

      assertEquals(predicate, specification.toPredicate(root, query, builder));
    }

    // Both non-null case
    {
      specification = spec.tsrangeInclude2(timeFrom, timeTo);
      assertNotNull(specification);

      // Mocking CriteriaBuilder and Root
      Root<Object> root = mock(Root.class);
      CriteriaQuery<?> query = mock(CriteriaQuery.class);
      CriteriaBuilder builder = mock(CriteriaBuilder.class);
      Predicate predicate = mock(Predicate.class);
      Expression<String> expression = mock(Expression.class);
      Path path = mock(Path.class);

      when(builder.isTrue(any())).thenReturn(predicate);
      when(builder.literal(anyString())).thenReturn(expression);
      when(root.get(eq("reservation_time"))).thenReturn(path);
      when(builder.function(eq("fn_tsrange_include2"), eq(Boolean.class), eq(path), eq(expression)))
          .thenReturn(predicate);

      assertEquals(predicate, specification.toPredicate(root, query, builder));
    }
  }

  /**
   * メソッド名: reserveProviderIdEqual<br>
   * 試験名: 予約事業者IDで検索する<br>
   * 条件: 予約事業者IDがnull、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void reserveProviderIdEqualTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // Null case
    assertNull(spec.reserveProviderIdEqual(null));

    // Normal case
    UUID id = UUID.randomUUID();
    Specification<Object> specification = spec.reserveProviderIdEqual(id);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("reserveProviderId"), id)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: reservationActiveFlag<br>
   * 試験名: 予約有効フラグが一致する条件で検索する<br>
   * 条件: 予約有効フラグがtrue、falseの場合<br>
   * 結果: 正しいPredicateが返される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void reservationActiveFlagEqualTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // True case
    Specification<Object> specification = spec.reservationActiveFlag(true);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("reservationActiveFlag"), true)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));

    // False case
    specification = spec.reservationActiveFlag(false);
    assertNotNull(specification);

    when(builder.equal(root.get("delereservationActiveFlagteFlag"), false)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: deleteFlagEqual<br>
   * 試験名: 削除フラグが一致する条件で検索する<br>
   * 条件: 削除フラグがtrue、falseの場合<br>
   * 結果: 正しいPredicateが返される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void deleteFlagEqualTest() {
    DronePortReserveInfoSpecification<Object> spec = new DronePortReserveInfoSpecification<>();

    // True case
    Specification<Object> specification = spec.deleteFlagEqual(true);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("deleteFlag"), true)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));

    // False case
    specification = spec.deleteFlagEqual(false);
    assertNotNull(specification);

    when(builder.equal(root.get("deleteFlag"), false)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }
}
