package com.hitachi.droneroute.prm.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

/** PriceInfoSpecificationの単体テスト */
class PriceInfoSpecificationTest {

  /**
   * メソッド名: priceIdEquals<br>
   * 試験名: 料金IDでの検索<br>
   * 条件: 料金IDを指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPriceIdEquals() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "priceId";
    // モック条件設定
    UUID priceId = UUID.randomUUID();
    when(builder.equal(root.get(columnName), priceId)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.priceIdEquals(priceId);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), priceId);
  }

  /**
   * メソッド名: priceIdEquals<br>
   * 試験名: 料金IDでの検索<br>
   * 条件: 料金IDがnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPriceIdEquals_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.priceIdEquals(null);
    assertNull(result);
  }

  /**
   * メソッド名: priceIdNotEquals<br>
   * 試験名: 料金IDでの除外検索<br>
   * 条件: 料金IDを指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPriceIdNotEquals() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "priceId";
    // モック条件設定
    UUID priceId = UUID.randomUUID();
    when(builder.notEqual(root.get(columnName), priceId)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.priceIdNotEquals(priceId);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).notEqual(root.get(columnName), priceId);
  }

  /**
   * メソッド名: priceIdNotEquals<br>
   * 試験名: 料金IDでの除外検索<br>
   * 条件: 料金IDがnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPriceIdNotEquals_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.priceIdNotEquals(null);
    assertNull(result);
  }

  /**
   * メソッド名: resourceIdIn<br>
   * 試験名: リソースIDでの検索<br>
   * 条件: リソースIDリスト（3件）を指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testResourceIdIn() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    Path<Object> path = mock(Path.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "resourceId";
    // モック条件設定
    List<String> resourceIds = new ArrayList<>();
    String id1 = "test-id1";
    resourceIds.add(id1);
    String id2 = "test-id2";
    resourceIds.add(id2);
    String id3 = "test-id3";
    resourceIds.add(id3);
    assertNotNull(resourceIds);
    assertEquals(3, resourceIds.size());
    when(root.get(columnName)).thenReturn(path);
    when(path.in(List.of(id1, id2, id3))).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.resourceIdIn(resourceIds);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
  }

  /**
   * メソッド名: resourceIdIn<br>
   * 試験名: リソースIDでの検索<br>
   * 条件: リソースIDリストがnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testResourceIdIn_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.resourceIdIn(null);
    assertNull(result);
  }

  /**
   * メソッド名: resourceIdIn<br>
   * 試験名: リソースIDでの検索<br>
   * 条件: リソースIDリストが空<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testResourceIdIn_empty() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    List<String> resourceIds = new ArrayList<>();

    Specification<Object> result = spec.resourceIdIn(resourceIds);
    assertNull(result);
  }

  /**
   * メソッド名: resourceTypeEquals<br>
   * 試験名: リソース種別での検索<br>
   * 条件: リソース種別を指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testResourceTypeEquals() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "resourceType";
    // モック条件設定
    BigInteger resourceType = new BigInteger("20");
    when(builder.equal(root.get(columnName), resourceType)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.resourceTypeEquals(resourceType);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), resourceType);
  }

  /**
   * メソッド名: resourceTypeEquals<br>
   * 試験名: リソース種別での検索<br>
   * 条件: リソース種別がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testResourceTypeEquals_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.resourceTypeEquals(null);
    assertNull(result);
  }

  /**
   * メソッド名: primaryRouteOperatorIdEquals<br>
   * 試験名: 主管航路事業者IDでの検索<br>
   * 条件: 主管航路事業者IDを指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPrimaryRouteOperatorIdEquals() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "primaryRouteOperatorId";
    // モック条件設定
    String primaryRouteOperatorId = "test-id";
    when(builder.equal(root.get(columnName), primaryRouteOperatorId)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.primaryRouteOperatorIdEquals(primaryRouteOperatorId);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), primaryRouteOperatorId);
  }

  /**
   * メソッド名: primaryRouteOperatorIdEquals<br>
   * 試験名: 主管航路事業者IDでの検索<br>
   * 条件: 主管航路事業者IDがnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPrimaryRouteOperatorIdEquals_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.primaryRouteOperatorIdEquals(null);
    assertNull(result);
  }

  /**
   * メソッド名: priceTypeEquals<br>
   * 試験名: 料金タイプでの検索<br>
   * 条件: 料金タイプを指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPriceTypeEquals() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "priceType";
    // モック条件設定
    BigInteger priceType = new BigInteger("1");
    when(builder.equal(root.get(columnName), priceType)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.priceTypeEquals(priceType);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), priceType);
  }

  /**
   * メソッド名: priceTypeEquals<br>
   * 試験名: 料金タイプでの検索<br>
   * 条件: 料金タイプがnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPriceTypeEquals_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.priceTypeEquals(null);
    assertNull(result);
  }

  /**
   * メソッド名: startPricePerUnitGreaterThanEqual<br>
   * 試験名: 単位料金の下限値での検索<br>
   * 条件: 単位料金の下限値を指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testStartPricePerUnitGreaterThanEqual() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "pricePerUnit";
    // モック条件設定
    BigInteger pricePerUnitFrom = new BigInteger("1");
    when(builder.greaterThanOrEqualTo(root.get(columnName), pricePerUnitFrom))
        .thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.startPricePerUnitGreaterThanEqual(pricePerUnitFrom);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).greaterThanOrEqualTo(root.get(columnName), pricePerUnitFrom);
  }

  /**
   * メソッド名: startPricePerUnitGreaterThanEqual<br>
   * 試験名: 単位料金の下限値での検索<br>
   * 条件: 単位料金の下限値がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testStartPricePerUnitGreaterThanEqual_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.startPricePerUnitGreaterThanEqual(null);
    assertNull(result);
  }

  /**
   * メソッド名: endPricePerUnitLessThanEqual<br>
   * 試験名: 単位料金の上限値での検索<br>
   * 条件: 単位料金の上限値を指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testEndPricePerUnitLessThanEqual() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "pricePerUnit";
    // モック条件設定
    BigInteger pricePerUnitTo = new BigInteger("1");
    when(builder.lessThanOrEqualTo(root.get(columnName), pricePerUnitTo)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.endPricePerUnitLessThanEqual(pricePerUnitTo);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).lessThanOrEqualTo(root.get(columnName), pricePerUnitTo);
  }

  /**
   * メソッド名: endPricePerUnitLessThanEqual<br>
   * 試験名: 単位料金の上限値での検索<br>
   * 条件: 単位料金の上限値がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testEndPricePerUnitLessThanEqual_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.endPricePerUnitLessThanEqual(null);
    assertNull(result);
  }

  /**
   * メソッド名: startPriceGreaterThanEqual<br>
   * 試験名: 料金の下限値での検索<br>
   * 条件: 料金の下限値を指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testStartPriceGreaterThanEqual() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "price";
    // モック条件設定
    BigInteger priceFrom = new BigInteger("1");
    when(builder.greaterThanOrEqualTo(root.get(columnName), priceFrom)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.startPriceGreaterThanEqual(priceFrom);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).greaterThanOrEqualTo(root.get(columnName), priceFrom);
  }

  /**
   * メソッド名: startPriceGreaterThanEqual<br>
   * 試験名: 料金の下限値での検索<br>
   * 条件: 料金の下限値がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testStartPriceGreaterThanEqual_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.startPriceGreaterThanEqual(null);
    assertNull(result);
  }

  /**
   * メソッド名: endPriceLessThanEqual<br>
   * 試験名: 料金の上限値での検索<br>
   * 条件: 料金の上限値を指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testEndPriceLessThanEqual() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "price";
    // モック条件設定
    BigInteger priceTo = new BigInteger("1");
    when(builder.lessThanOrEqualTo(root.get(columnName), priceTo)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.endPriceLessThanEqual(priceTo);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
  }

  /**
   * メソッド名: endPriceLessThanEqual<br>
   * 試験名: 料金の上限値での検索<br>
   * 条件: 料金の上限値がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testEndPriceLessThanEqual_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.endPriceLessThanEqual(null);
    assertNull(result);
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 適用日時範囲での検索<br>
   * 条件: 開始日時と終了日時を指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testTsrangeOverlap() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Timestamp timeFrom = new Timestamp(0);
    Timestamp timeTo = new Timestamp(1);
    String condition = "[" + timeFrom.toString() + "," + timeTo.toString() + ")";
    when(builder.isTrue(
            builder.function(
                "fn_tsrange_overlap",
                Boolean.class,
                root.get("effectiveTime"),
                builder.literal(condition))))
        .thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.tsrangeOverlap(timeFrom, timeTo);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 適用日時範囲での検索<br>
   * 条件: 開始日時がnull、終了日時を指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testTsrangeOverlap_from_null() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Timestamp timeFrom = null;
    Timestamp timeTo = new Timestamp(0);
    String condition = "[," + timeTo.toString() + ")";
    when(builder.isTrue(
            builder.function(
                "fn_tsrange_overlap",
                Boolean.class,
                root.get("effectiveTime"),
                builder.literal(condition))))
        .thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.tsrangeOverlap(timeFrom, timeTo);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 適用日時範囲での検索<br>
   * 条件: 開始日時を指定、終了日時がnullでSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testTsrangeOverlap_to_null() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Timestamp timeFrom = new Timestamp(0);
    Timestamp timeTo = null;
    String condition = "[" + timeFrom.toString() + ",)";
    when(builder.isTrue(
            builder.function(
                "fn_tsrange_overlap",
                Boolean.class,
                root.get("effectiveTime"),
                builder.literal(condition))))
        .thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.tsrangeOverlap(timeFrom, timeTo);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 適用日時範囲での検索<br>
   * 条件: 開始日時と終了日時がともにnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testTsrangeOverlap_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.tsrangeOverlap(null, null);
    assertNull(result);
  }

  /**
   * メソッド名: priorityEquals<br>
   * 試験名: 優先度での検索<br>
   * 条件: 優先度を指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPriorityEquals() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "priority";
    // モック条件設定
    Integer priority = 1;
    when(builder.equal(root.get(columnName), priority)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.priorityEquals(priority);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), priority);
  }

  /**
   * メソッド名: priorityEquals<br>
   * 試験名: 優先度での検索<br>
   * 条件: 優先度がnull<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testPriorityEquals_null() {
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();

    Specification<Object> result = spec.priorityEquals(null);
    assertNull(result);
  }

  /**
   * メソッド名: deleteFlagEqual<br>
   * 試験名: 削除フラグでの検索<br>
   * 条件: 削除フラグを指定してSpecificationを生成<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  public void testDeleteFlagEquals() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    String columnName = "priority";
    // モック条件設定
    boolean flag = false;
    when(builder.equal(root.get(columnName), flag)).thenReturn(predicate);
    PriceInfoSpecification<Object> spec = new PriceInfoSpecification<>();
    // 処理実行
    Specification<Object> specification = spec.deleteFlagEqual(flag);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), flag);
  }
}
