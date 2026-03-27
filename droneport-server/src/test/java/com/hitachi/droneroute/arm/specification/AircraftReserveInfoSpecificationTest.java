package com.hitachi.droneroute.arm.specification;

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

/** AircraftReserveInfoSpecificationTestの単体テスト */
public class AircraftReserveInfoSpecificationTest {

  /**
   * メソッド名: aircraftIdEqual<br>
   * 試験名: 機体IDの検索条件未設定<br>
   * 条件: 機体IDがnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testAircraftIdEqual_Null() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    UUID aircraftId = null;
    Specification<Object> result = spec.aircraftIdEqual(aircraftId);
    assertNull(result);
  }

  /**
   * メソッド名: aircraftIdEqual<br>
   * 試験名: 機体IDによる検索条件が正しく生成される<br>
   * 条件: 機体IDが指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testAircraftIdEqual_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    UUID aircraftId = UUID.randomUUID();
    when(builder.equal(root.get("aircraftId"), aircraftId)).thenReturn(predicate);

    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Specification<Object> specification = spec.aircraftIdEqual(aircraftId);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("aircraftId"), aircraftId);
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 予約開始の検索条件未設定<br>
   * 条件: 予約開始がnull<br>
   * 結果: Specificationが生成される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testTsrangeOverlap_TimeFrom_Null() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Timestamp now = new Timestamp(System.currentTimeMillis());

    Specification<Object> result = spec.tsrangeOverlap(null, now);
    assertNotNull(result);

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

    assertEquals(predicate, result.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 予約終了の検索条件未設定<br>
   * 条件: 予約終了がnull<br>
   * 結果: Specificationが生成される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testTsrangeOverlap_TimeTo_Null() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Timestamp now = new Timestamp(System.currentTimeMillis());

    Specification<Object> result = spec.tsrangeOverlap(now, null);
    assertNotNull(result);

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

    assertEquals(predicate, result.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 予約開始、終了の検索条件未設定<br>
   * 条件: 予約開始と終了が両方null<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testTsrangeOverlap_Both_Null() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();

    Specification<Object> result = spec.tsrangeOverlap(null, null);

    assertNull(result);
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 予約開始と終了が同じ値の場合の動作確認<br>
   * 条件: 予約開始と終了が同じ値<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 境界値<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testTsrangeOverlap_境界値() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Timestamp now = new Timestamp(System.currentTimeMillis());

    Specification<Object> result = spec.tsrangeOverlap(now, now);
    assertNotNull(result);

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

    assertEquals(predicate, result.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 予約開始と終了に時間差がある場合の動作確認<br>
   * 条件: 予約開始と終了に時間差がある<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testTsrangeOverlap_正常() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();

    Timestamp timeFrom = new Timestamp(System.currentTimeMillis());
    Timestamp timeTo = new Timestamp(timeFrom.getTime() + 1000);

    Specification<Object> result = spec.tsrangeOverlap(timeFrom, timeTo);
    assertNotNull(result);

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

    assertEquals(predicate, result.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: tsrangeInclude<br>
   * 試験名: 予約開始の検索条件未設定<br>
   * 条件: 予約開始がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testTsrangeInclude_TimeFrom_Null() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Timestamp now = new Timestamp(System.currentTimeMillis());

    Specification<Object> result = spec.tsrangeInclude(null, now);
    assertNull(result);
  }

  /**
   * メソッド名: tsrangeInclude<br>
   * 試験名: 予約終了の検索条件未設定<br>
   * 条件: 予約終了がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testTsrangeInclude_TimeTo_Null() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Timestamp now = new Timestamp(System.currentTimeMillis());

    Specification<Object> result = spec.tsrangeInclude(now, null);
    assertNull(result);
  }

  /**
   * メソッド名: tsrangeInclude<br>
   * 試験名: 予約開始、終了の検索条件未設定<br>
   * 条件: 予約開始と終了が両方null<br>
   * 結枚: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testTsrangeInclude_Both_Null() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();

    Specification<Object> result = spec.tsrangeInclude(null, null);

    assertNull(result);
  }

  /**
   * メソッド名: tsrangeInclude<br>
   * 試験名: 予約開始と終了が同じ値の場合の動作確認<br>
   * 条件: 予約開始と終了が同じ値<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 境界値<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testTsrangeInclude_境界値() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Timestamp now = new Timestamp(System.currentTimeMillis());

    Specification<Object> result = spec.tsrangeInclude(now, now);
    assertNotNull(result);

    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    Expression<String> expression = mock(Expression.class);
    Path path = mock(Path.class);

    when(builder.isTrue(any())).thenReturn(predicate);
    when(builder.literal(anyString())).thenReturn(expression);
    when(root.get(eq("reservation_time"))).thenReturn(path);
    when(builder.function(eq("fn_tsrange_include"), eq(Boolean.class), eq(path), eq(expression)))
        .thenReturn(predicate);

    assertEquals(predicate, result.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: tsrangeInclude2<br>
   * 試験名: 予約日時範囲による検索条件が正しく生成される<br>
   * 条件: timeFromとtimeToがnull、片方null、正常値<br>
   * 結果: nullまたはSpecificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void tsrangeInclude2Test() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();

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
   * メソッド名: tsrangeInclude<br>
   * 試験名: 予約開始と終了に時間差がある場合の動作確認<br>
   * 条件: 予約開始と終了に時間差がある<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  public void testTsrangeInclude_正常() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();

    Timestamp timeFrom = new Timestamp(System.currentTimeMillis());
    Timestamp timeTo = new Timestamp(timeFrom.getTime() + 1000);

    Specification<Object> result = spec.tsrangeInclude(timeFrom, timeTo);
    assertNotNull(result);

    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    Expression<String> expression = mock(Expression.class);
    Path path = mock(Path.class);

    when(builder.isTrue(any())).thenReturn(predicate);
    when(builder.literal(anyString())).thenReturn(expression);
    when(root.get(eq("reservation_time"))).thenReturn(path);
    when(builder.function(eq("fn_tsrange_include"), eq(Boolean.class), eq(path), eq(expression)))
        .thenReturn(predicate);

    assertEquals(predicate, result.toPredicate(root, query, builder));
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
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    boolean flag = false;

    Specification<Object> result = spec.deleteFlagEqual(flag);
    assertNotNull(result);
  }

  /**
   * メソッド名: deleteFlagEqual<br>
   * 試験名: 削除フラグがtrueの場合の検索条件が正しく生成される<br>
   * 条件: 削除フラグがtrue<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: エッジケース<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void deleteFlagEqual_true() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Specification<Object> specification = spec.deleteFlagEqual(true);
    assertNotNull(specification);

    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("deleteFlag"), true)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: deleteFlagEqual<br>
   * 試験名: 削除フラグがfalseの場合の検索条件が正しく生成される<br>
   * 条件: 削除フラグがfalse<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: エッジケース<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void deleteFlagEqual_false() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();

    Specification<Object> specification = spec.deleteFlagEqual(false);
    assertNotNull(specification);

    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("deleteFlag"), false)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: aircraftNameContains<br>
   * 試験名: 機体名の検索条件未設定<br>
   * 条件: 機体名がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testAircraftNameContains_Null() {
    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
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
    when(root.get("aircraftEntity")).thenReturn(root).thenReturn(root).thenReturn(root);
    when(builder.like(eq(root.get("aircraftName")), contains(aircraftName))).thenReturn(predicate);

    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Specification<Object> specification = spec.aircraftNameContains(aircraftName);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get("aircraftName")), contains(aircraftName));
  }

  /**
   * メソッド名: groupReservationIdEqual<br>
   * 試験名: 一括予約IDによる検索条件が正しく生成される<br>
   * 条件: 一括予約IDが指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testGroupReservationIdEqual() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    UUID groupReservationId = UUID.randomUUID();
    when(builder.equal(root.get("groupReservationId"), groupReservationId)).thenReturn(predicate);

    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Specification<Object> specification = spec.groupReservationIdEqual(groupReservationId);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("groupReservationId"), groupReservationId);
  }

  /**
   * メソッド名: reserveProviderIdEqual<br>
   * 試験名: 予約事業者IDによる検索条件が正しく生成される<br>
   * 条件: 予約事業者IDが指定される<br>
   * 結果: Specificationが正しく生成される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testReserveProviderIdEqual() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    UUID reserveProviderId = UUID.randomUUID();
    when(builder.equal(root.get("groupReservationId"), reserveProviderId)).thenReturn(predicate);

    AircraftReserveInfoSpecification<Object> spec = new AircraftReserveInfoSpecification<>();
    Specification<Object> specification = spec.reserveProviderIdEqual(reserveProviderId);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("reserveProviderId"), reserveProviderId);
  }
}
