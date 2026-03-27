package com.hitachi.droneroute.dpm.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.dpm.entity.DronePortStatusEntity;
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

/** DronePortStatusSpecificationクラスの単体テスト */
public class DronePortStatusSpecificationTest {

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
    DronePortStatusSpecification spec = new DronePortStatusSpecification();

    // Null case
    assertNull(spec.dronePortIdEqual(null));

    // Normal case
    String id = UUID.randomUUID().toString();
    Specification<DronePortStatusEntity> specification = spec.dronePortIdEqual(id);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<DronePortStatusEntity> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    when(builder.equal(root.get("dronePortId"), id)).thenReturn(predicate);

    assertEquals(predicate, specification.toPredicate(root, query, builder));
  }

  /**
   * メソッド名: tsrangeOverlap<br>
   * 試験名: 使用不可日時範囲で検索する<br>
   * 条件: timeFromとtimeToがnull、片方null、正常値の場合<br>
   * 結果: nullまたは正しいPredicateが返される<br>
   * テストパターン: 正常系、異常系、境界値<br>
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void tsrangeOverlapTest() {
    DronePortStatusSpecification spec = new DronePortStatusSpecification();

    // Both null case
    assertNull(spec.tsrangeOverlap(null, null));

    Specification<DronePortStatusEntity> specification;
    // timeFrom null case
    Timestamp timeTo = new Timestamp(System.currentTimeMillis());
    {
      specification = spec.tsrangeOverlap(null, timeTo);
      assertNotNull(specification);

      // Mocking CriteriaBuilder and Root
      Root<DronePortStatusEntity> root = mock(Root.class);
      CriteriaQuery<?> query = mock(CriteriaQuery.class);
      CriteriaBuilder builder = mock(CriteriaBuilder.class);
      Predicate predicate = mock(Predicate.class);
      Expression<String> expression = mock(Expression.class);
      Path path = mock(Path.class);

      when(builder.isTrue(any())).thenReturn(predicate);
      when(builder.literal(anyString())).thenReturn(expression);
      when(root.get(eq("inactiveTime"))).thenReturn(path);
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
      Root<DronePortStatusEntity> root = mock(Root.class);
      CriteriaQuery<?> query = mock(CriteriaQuery.class);
      CriteriaBuilder builder = mock(CriteriaBuilder.class);
      Predicate predicate = mock(Predicate.class);
      Expression<String> expression = mock(Expression.class);
      Path path = mock(Path.class);

      when(builder.isTrue(any())).thenReturn(predicate);
      when(builder.literal(anyString())).thenReturn(expression);
      when(root.get(eq("inactiveTime"))).thenReturn(path);
      when(builder.function(eq("fn_tsrange_overlap"), eq(Boolean.class), eq(path), eq(expression)))
          .thenReturn(predicate);

      assertEquals(predicate, specification.toPredicate(root, query, builder));
    }

    // Both non-null case
    {
      specification = spec.tsrangeOverlap(timeFrom, timeTo);
      assertNotNull(specification);

      // Mocking CriteriaBuilder and Root
      Root<DronePortStatusEntity> root = mock(Root.class);
      CriteriaQuery<?> query = mock(CriteriaQuery.class);
      CriteriaBuilder builder = mock(CriteriaBuilder.class);
      Predicate predicate = mock(Predicate.class);
      Expression<String> expression = mock(Expression.class);
      Path path = mock(Path.class);

      when(builder.isTrue(any())).thenReturn(predicate);
      when(builder.literal(anyString())).thenReturn(expression);
      when(root.get(eq("inactiveTime"))).thenReturn(path);
      when(builder.function(eq("fn_tsrange_overlap"), eq(Boolean.class), eq(path), eq(expression)))
          .thenReturn(predicate);

      assertEquals(predicate, specification.toPredicate(root, query, builder));
    }
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
    DronePortStatusSpecification spec = new DronePortStatusSpecification();

    // True case
    Specification<DronePortStatusEntity> specification = spec.deleteFlagEqual(true);
    assertNotNull(specification);

    // Mocking CriteriaBuilder and Root
    Root<DronePortStatusEntity> root = mock(Root.class);
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
