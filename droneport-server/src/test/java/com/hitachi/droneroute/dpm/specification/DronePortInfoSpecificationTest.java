package com.hitachi.droneroute.dpm.specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

/** DronePortInfoSpecificationクラスの単体テスト */
public class DronePortInfoSpecificationTest {

  /**
   * メソッド名: dronePortTypeContains<br>
   * 試験名: 離着陸場IDで検索が正しく行われる<br>
   * 条件: 離着陸場IDを渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testDronePortIdEqual_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    String dronePortId = "test-droneport-id";
    String columnName = "dronePortId";
    when(builder.equal(root.get(columnName), dronePortId)).thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.dronePortIdEqual(dronePortId);
    Predicate result = specification.toPredicate(root, query, builder);
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), dronePortId);
  }

  /**
   * メソッド名: dronePortIdEqual<br>
   * 試験名: 離着陸場IDの検索条件未設定<br>
   * 条件: 離着陸場IDがnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testDronePortIdEqual_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    String dronePortId = null;
    Specification<Object> result = spec.dronePortIdEqual(dronePortId);
    assertNull(result);
  }

  /**
   * メソッド名: dronePortTypeContains<br>
   * 試験名: 離着陸場種別で検索が正しく行われる<br>
   * 条件: 離着陸場種別の配列を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testDronePortTypeContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Integer[] params = {1, 2, 3};
    String columnName = "dronePortType";
    when(builder.equal(root.get(columnName), params[0])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), params[1])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), params[2])).thenReturn(predicate);
    when(builder.or(any(Predicate[].class))).thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
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
   * メソッド名: dronePortTypeContains<br>
   * 試験名: 離着陸場種別の検索条件未設定<br>
   * 条件: 離着陸場種別がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testDronePortTypeContains_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Integer[] dronePortTypes = null;
    Specification<Object> result = spec.dronePortTypeContains(dronePortTypes);
    assertNull(result);
  }

  /**
   * メソッド名: dronePortNameContains<br>
   * 試験名: 離着陸場名で検索条件未設定<br>
   * 条件: 離着陸場名がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testDronePortNameContains_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    String dronePortName = null;
    Specification<Object> result = spec.dronePortNameContains(dronePortName);
    assertNull(result);
  }

  /**
   * メソッド名: dronePortNameContains<br>
   * 試験名: 離着陸場名で検索が正しく行われる<br>
   * 条件: 離着陸場名を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testDronePortNameContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    String dronePortName = "TestPort";
    when(builder.like(eq(root.get("dronePortName")), contains(dronePortName)))
        .thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.dronePortNameContains(dronePortName);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get("dronePortName")), contains(dronePortName));
  }

  /**
   * メソッド名: addressContains<br>
   * 試験名: 設置場所住所で検索が正しく行われる<br>
   * 条件: 設置場所住所を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testAddressContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    // モック条件設定
    String param = "TestAddress";
    String columnName = "address";
    when(builder.like(eq(root.get(columnName)), contains(param))).thenReturn(predicate);

    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.addressContains(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get(columnName)), contains(param));
  }

  /**
   * メソッド名: addressContains<br>
   * 試験名: 設置場所住所の検索条件未設定<br>
   * 条件: 設置場所住所がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testAddressContains_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    String address = null;
    Specification<Object> result = spec.addressContains(address);
    assertNull(result);
  }

  /**
   * メソッド名: manufacturerContains<br>
   * 試験名: 製造メーカーで検索が正しく行われる<br>
   * 条件: 製造メーカーを渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testManufacturerContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    // モック条件設定
    String param = "TestManufacturer";
    String columnName = "manufacturer";
    when(builder.like(eq(root.get(columnName)), contains(param))).thenReturn(predicate);

    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.manufacturerContains(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get(columnName)), contains(param));
  }

  /**
   * メソッド名: manufacturerContains<br>
   * 試験名: 製造メーカーの検索条件未設定<br>
   * 条件: 製造メーカーがnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testManufacturerContains_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    String manufacturer = null;
    Specification<Object> result = spec.manufacturerContains(manufacturer);
    assertNull(result);
  }

  /**
   * メソッド名: serialNumberContains<br>
   * 試験名: 製造番号で検索が正しく行われる<br>
   * 条件: 製造番号を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSerialNumberContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    // モック条件設定
    String param = "TestSerialNumber";
    String columnName = "serialNumber";
    when(builder.like(eq(root.get(columnName)), contains(param))).thenReturn(predicate);

    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.serialNumberContains(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get(columnName)), contains(param));
  }

  /**
   * メソッド名: serialNumberContains<br>
   * 試験名: 製造番号の検索条件未設定<br>
   * 条件: 製造番号がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSerialNumberContains_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    String serialNumber = null;
    Specification<Object> result = spec.serialNumberContains(serialNumber);
    assertNull(result);
  }

  /**
   * メソッド名: portTypeContains<br>
   * 試験名: ポート形状で検索が正しく行われる<br>
   * 条件: ポート形状の配列を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testPortTypeContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Integer[] params = {1, 2, 3};
    String columnName = "portType";
    when(builder.equal(root.get(columnName), params[0])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), params[1])).thenReturn(predicate);
    when(builder.equal(root.get(columnName), params[2])).thenReturn(predicate);
    when(builder.or(any(Predicate[].class))).thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.portTypeContains(params);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get(columnName), params[0]);
    verify(builder).equal(root.get(columnName), params[1]);
    verify(builder).equal(root.get(columnName), params[2]);
    verify(builder).or(new Predicate[] {predicate, predicate, predicate});
  }

  /**
   * メソッド名: portTypeContains<br>
   * 試験名: ポート形状の検索条件未設定<br>
   * 条件: ポート形状がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testPortTypeContains_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Integer[] portTypes = null;
    Specification<Object> result = spec.portTypeContains(portTypes);
    assertNull(result);
  }

  /**
   * メソッド名: startLatGreaterThanEqual<br>
   * 試験名: 最小緯度で検索が正しく行われる<br>
   * 条件: 最小緯度を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
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
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.startLatGreaterThanEqual(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).greaterThanOrEqualTo(root.get(columnName), param);
  }

  /**
   * メソッド名: startLatGreaterThanEqual<br>
   * 試験名: 最小緯度の検索条件未設定<br>
   * 条件: 最小緯度がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testStartLatGreaterThanEqual_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Double minlat = null;
    Specification<Object> result = spec.startLatGreaterThanEqual(minlat);
    assertNull(result);
  }

  /**
   * メソッド名: endLatLessThanEqual<br>
   * 試験名: 最大緯度で検索が正しく行われる<br>
   * 条件: 最大緯度を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
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
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.endLatLessThanEqual(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).lessThanOrEqualTo(root.get(columnName), param);
  }

  /**
   * メソッド名: endLatLessThanEqual<br>
   * 試験名: 最大緯度の検索条件未設定<br>
   * 条件: 最大緯度がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testEndLatLessThanEqual_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Double maxlat = 40.0;
    Specification<Object> result = spec.endLatLessThanEqual(maxlat);
    assertNotNull(result);
  }

  /**
   * メソッド名: startLonGreaterThanEqual<br>
   * 試験名: 最小経度で検索が正しく行われる<br>
   * 条件: 最小経度を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
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
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.startLonGreaterThanEqual(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).greaterThanOrEqualTo(root.get(columnName), param);
  }

  /**
   * メソッド名: startLonGreaterThanEqual<br>
   * 試験名: 最小経度の検索条件未設定<br>
   * 条件: 最小経度がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testStartLonGreaterThanEqual_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Double minlon = null;
    Specification<Object> result = spec.startLonGreaterThanEqual(minlon);
    assertNull(result);
  }

  /**
   * メソッド名: endLonLessThanEqual<br>
   * 試験名: 最大経度で検索が正しく行われる<br>
   * 条件: 最大経度を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
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
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.endLonLessThanEqual(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).lessThanOrEqualTo(root.get(columnName), param);
  }

  /**
   * メソッド名: endLonLessThanEqual<br>
   * 試験名: 最大経度の検索条件未設定<br>
   * 条件: 最大経度がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testEndLonLessThanEqual_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Double maxlon = null;
    Specification<Object> result = spec.endLonLessThanEqual(maxlon);
    assertNull(result);
  }

  /**
   * メソッド名: supportDroneTypeContains<br>
   * 試験名: 対応機体で検索が正しく行われる<br>
   * 条件: 対応機体を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSupportDroneTypeContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);

    // モック条件設定
    String param = "TestDroneType";
    String columnName = "supportDroneType";
    when(builder.like(eq(root.get(columnName)), contains(param))).thenReturn(predicate);

    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.supportDroneTypeContains(param);
    Predicate result = specification.toPredicate(root, query, builder);

    // 結果確認
    assertNotNull(result);
    verify(builder).like(eq(root.get(columnName)), contains(param));
  }

  /**
   * メソッド名: supportDroneTypeContains<br>
   * 試験名: 対応機体の検索条件未設定<br>
   * 条件: 対応機体がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testSupportDroneTypeContains_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    String supportDroneType = null;
    Specification<Object> result = spec.supportDroneTypeContains(supportDroneType);
    assertNull(result);
  }

  /**
   * メソッド名: activeStatusContains<br>
   * 試験名: 動作状況で検索が正しく行われる<br>
   * 条件: 動作状況を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testActiveStatusContains_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Integer[] params = {1, 2, 3};
    String columnName = "activeStatus";
    when(root.get("dronePortStatusEntity")).thenReturn(root).thenReturn(root).thenReturn(root);
    when(builder.equal(root.get("dronePortStatusEntity").get(columnName), params[0]))
        .thenReturn(predicate);
    when(builder.equal(root.get("dronePortStatusEntity").get(columnName), params[1]))
        .thenReturn(predicate);
    when(builder.equal(root.get("dronePortStatusEntity").get(columnName), params[2]))
        .thenReturn(predicate);
    when(builder.or(any(Predicate[].class))).thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.activeStatusContains(params);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("dronePortStatusEntity").get(columnName), params[0]);
    verify(builder).equal(root.get("dronePortStatusEntity").get(columnName), params[1]);
    verify(builder).equal(root.get("dronePortStatusEntity").get(columnName), params[2]);
    verify(builder).or(new Predicate[] {predicate, predicate, predicate});
  }

  /**
   * メソッド名: activeStatusContains<br>
   * 試験名: 動作状況の検索条件未設定<br>
   * 条件: 動作状況がnullの場合に、Specificationが生成されない<br>
   * 結果: Specificationがnullとなる<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testActiveStatusContains_NULL() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Integer[] activeStatus = null;
    Specification<Object> result = spec.activeStatusContains(activeStatus);
    assertNull(result);
  }

  /**
   * メソッド名: deleteFlagEqual<br>
   * 試験名: 削除フラグがfalseで検索が正しが行われる<br>
   * 条件: 削除フラグを渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void testDeleteFlagEqual() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    boolean flag = false;
    Specification<Object> result = spec.deleteFlagEqual(flag);
    assertNotNull(result);
  }

  /**
   * メソッド名: dronePortTypeContains<br>
   * 試験名: 離着陸場種別で検索するSpecificationが正しく生成される<br>
   * 条件: dronePortTypesがnullまたは空配列の場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void dronePortTypeContains_nullOrEmpty() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.dronePortTypeContains(null));
    assertNull(spec.dronePortTypeContains(new Integer[] {}));
  }

  /**
   * メソッド名: dronePortNameContains<br>
   * 試験名: 離着陸場名で検索するSpecificationが正しく生成される<br>
   * 条件: dronePortNameがnullまたは空文字の場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void dronePortNameContains_nullOrEmpty() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.dronePortNameContains(null));
    assertNull(spec.dronePortNameContains(""));
  }

  /**
   * メソッド名: addressContains<br>
   * 試験名: 設置場所住所で検索するSpecificationが正しく生成される<br>
   * 条件: addressがnullまたは空文字の場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void addressContains_nullOrEmpty() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.addressContains(null));
    assertNull(spec.addressContains(""));
  }

  /**
   * メソッド名: manufacturerContains<br>
   * 試験名: 製造メーカーで検索するSpecificationが正しく生成される<br>
   * 条件: manufacturerがnullまたは空文字の場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void manufacturerContains_nullOrEmpty() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.manufacturerContains(null));
    assertNull(spec.manufacturerContains(""));
  }

  /**
   * メソッド名: serialNumberContains<br>
   * 試験名: 製造番号で検索するSpecificationが正しく生成される<br>
   * 条件: serialNumberがnullまたは空文字の場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void serialNumberContains_nullOrEmpty() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.serialNumberContains(null));
    assertNull(spec.serialNumberContains(""));
  }

  /**
   * メソッド名: portTypeContains<br>
   * 試験名: ポート形状で検索するSpecificationが正しく生成される<br>
   * 条件: portTypesがnullまたは空配列の場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void portTypeContains_nullOrEmpty() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.portTypeContains(null));
    assertNull(spec.portTypeContains(new Integer[] {}));
  }

  /**
   * メソッド名: startLatGreaterThanEqual<br>
   * 試験名: 最小緯度で検索するSpecificationが正しく生成される<br>
   * 条件: minlatがnullの場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void startLatGreaterThanEqual_null() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.startLatGreaterThanEqual(null));
  }

  /**
   * メソッド名: endLatLessThanEqual<br>
   * 試験名: 最大緯度で検索するSpecificationが正しく生成される<br>
   * 条件: maxlatがnullの場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void endLatLessThanEqual_null() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.endLatLessThanEqual(null));
  }

  /**
   * メソッド名: startLonGreaterThanEqual<br>
   * 試験名: 最小経度で検索するSpecificationが正しく生成される<br>
   * 条件: minlonがnullの場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void startLonGreaterThanEqual_null() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.startLonGreaterThanEqual(null));
  }

  /**
   * メソッド名: endLonLessThanEqual<br>
   * 試験名: 最大経度で検索するSpecificationが正しく生成される<br>
   * 条件: maxlonがnullの場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void endLonLessThanEqual_null() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.endLonLessThanEqual(null));
  }

  /**
   * メソッド名: supportDroneTypeContains<br>
   * 試験名: 対応機体で検索するSpecificationが正しく生成される<br>
   * 条件: supportDroneTypeがnullまたは空文字の場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void supportDroneTypeContains_nullOrEmpty() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.supportDroneTypeContains(null));
    assertNull(spec.supportDroneTypeContains(""));
  }

  /**
   * メソッド名: activeStatusContains<br>
   * 試験名: 動作状況で検索するSpecificationが正しく生成される<br>
   * 条件: activeStatusがnullまたは空文字の場合、nullを返す<br>
   * 結果: nullが返される<br>
   * テストパターン: 境界値テスト<br>
   */
  @Test
  void activeStatusContains_nullOrEmpty() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    assertNull(spec.activeStatusContains(null));
    assertNull(spec.activeStatusContains(new Integer[] {}));
  }

  /**
   * メソッド名: deleteFlagEqual<br>
   * 試験名: 削除フラグがfalseで検索するSpecificationが正しく生成される<br>
   * 条件: flagがtrueの場合、正しいPredicateが生成される<br>
   * 結果: 生成されたPredicateが期待通りである<br>
   * テストパターン: エッジケース<br>
   */
  @Test
  void deleteFlagEqual_true() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
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
   * 試験名: 削除フラグがfalseで検索するSpecificationが正しく生成される<br>
   * 条件: flagがfalseの場合、正しいPredicateが生成される<br>
   * 結果: 生成されたPredicateが期待通りである<br>
   * テストパターン: エッジケース<br>
   */
  @Test
  void deleteFlagEqual_false() {
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
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

  /**
   * メソッド名: activeStatusInner1<br>
   * 試験名: 指定日時における動作状況(使用可)の検索条件1の生成<br>
   * 条件: 動作状況を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void activeStatusInner1_正常() {
    // モック設定
    @SuppressWarnings("unchecked")
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    Integer[] params = {1, 2, 3};
    String columnName = "activeStatus";
    when(root.get("dronePortStatusEntity"))
        .thenReturn(root)
        .thenReturn(root)
        .thenReturn(root)
        .thenReturn(root)
        .thenReturn(root);
    when(builder.equal(root.get("dronePortStatusEntity").get(columnName), params[0]))
        .thenReturn(predicate);
    when(builder.equal(root.get("dronePortStatusEntity").get(columnName), params[1]))
        .thenReturn(predicate);
    when(builder.equal(root.get("dronePortStatusEntity").get(columnName), params[2]))
        .thenReturn(predicate);
    when(builder.or(any(Predicate[].class))).thenReturn(predicate);
    when(builder.isNull(root.get("dummyColumn"))).thenReturn(predicate).thenReturn(predicate);
    when(builder.and(any(Predicate.class), any(Predicate.class), any(Predicate.class)))
        .thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.activeStatusInner1(Arrays.asList(params));
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("dronePortStatusEntity").get(columnName), params[0]);
    verify(builder).equal(root.get("dronePortStatusEntity").get(columnName), params[1]);
    verify(builder).equal(root.get("dronePortStatusEntity").get(columnName), params[2]);
    verify(builder).or(new Predicate[] {predicate, predicate, predicate});
    verify(builder, times(2)).isNull(root.get("dummyColumn"));
    verify(builder).and(predicate, predicate, predicate);
  }

  /**
   * メソッド名: activeStatusInner2<br>
   * 試験名: 指定日時における動作状況(使用可)の検索条件2の生成<br>
   * 条件: 動作状況、指定日時を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void activeStatusInner2_正常() {
    Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
    // モック設定
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    Expression<Timestamp> expression = mock(Expression.class);

    // モック条件設定
    Integer[] params = {1, 2, 4};
    when(root.get("dronePortStatusEntity"))
        .thenReturn(root)
        .thenReturn(root)
        .thenReturn(root)
        .thenReturn(root)
        .thenReturn(root);
    when(builder.equal(root.get("dummyColumn"), params[0])).thenReturn(predicate);
    when(builder.equal(root.get("dummyColumn"), params[1])).thenReturn(predicate);
    when(builder.equal(root.get("dummyColumn"), params[2])).thenReturn(predicate);
    when(builder.or(any(Predicate[].class))).thenReturn(predicate);
    when(builder.equal(root.get("dummyColumn"), DronePortConstants.ACTIVE_STATUS_UNAVAILABLE))
        .thenReturn(predicate);
    when(builder.function("tsrange_lower", Timestamp.class, root.get("dummyColumn")))
        .thenReturn(expression);
    when(builder.greaterThan(expression, currentTime)).thenReturn(predicate);
    when(builder.and(any(Predicate.class), any(Predicate.class), any(Predicate.class)))
        .thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification =
        spec.activeStatusInner2(Arrays.asList(params), currentTime);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("dummyColumn"), params[0]);
    verify(builder).equal(root.get("dummyColumn"), params[1]);
    verify(builder).equal(root.get("dummyColumn"), params[2]);
    verify(builder).or(new Predicate[] {predicate, predicate, predicate});
    verify(builder).equal(root.get("dummyColumn"), DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    verify(builder).greaterThan(expression, currentTime);
    verify(builder).and(predicate, predicate, predicate);
  }

  /**
   * メソッド名: activeStatusInner3<br>
   * 試験名: 指定日時における動作状況(使用可)の検索条件2の生成<br>
   * 条件: 動作状況、指定日時を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void activeStatusInner3_正常() {
    Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
    // モック設定
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    Expression<Timestamp> expression = mock(Expression.class);

    // モック条件設定
    Integer[] params = {1, 2, 3};
    when(root.get("dronePortStatusEntity"))
        .thenReturn(root)
        .thenReturn(root)
        .thenReturn(root)
        .thenReturn(root)
        .thenReturn(root);
    when(builder.equal(root.get("dummyColumn"), params[0])).thenReturn(predicate);
    when(builder.equal(root.get("dummyColumn"), params[1])).thenReturn(predicate);
    when(builder.equal(root.get("dummyColumn"), params[2])).thenReturn(predicate);
    when(builder.or(any(Predicate[].class))).thenReturn(predicate);
    when(builder.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
    when(builder.equal(root.get("dummyColumn"), DronePortConstants.ACTIVE_STATUS_MAINTENANCE))
        .thenReturn(predicate);
    when(builder.function("tsrange_lower", Timestamp.class, root.get("dummyColumn")))
        .thenReturn(expression);
    when(builder.function("tsrange_upper", Timestamp.class, root.get("dummyColumn")))
        .thenReturn(expression);
    when(builder.greaterThan(expression, currentTime)).thenReturn(predicate);
    when(builder.lessThan(expression, currentTime)).thenReturn(predicate);
    when(builder.and(any(Predicate.class), any(Predicate.class), any(Predicate.class)))
        .thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification =
        spec.activeStatusInner3(Arrays.asList(params), currentTime);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("dummyColumn"), params[0]);
    verify(builder).equal(root.get("dummyColumn"), params[1]);
    verify(builder).equal(root.get("dummyColumn"), params[2]);
    verify(builder).or(new Predicate[] {predicate, predicate, predicate});
    verify(builder).equal(root.get("dummyColumn"), DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    verify(builder).greaterThan(expression, currentTime);
    verify(builder).lessThan(expression, currentTime);
    verify(builder).and(predicate, predicate, predicate);
  }

  /**
   * メソッド名: unavailableStatus<br>
   * 試験名: 指定日時における動作状況(使用不可):"使用不可"の検索条件の生成<br>
   * 条件: 指定日時を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void unavailableStatus_正常() {
    Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
    // モック設定
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    Expression<Timestamp> expression = mock(Expression.class);

    // モック条件設定
    when(root.get("dronePortStatusEntity")).thenReturn(root).thenReturn(root).thenReturn(root);
    when(builder.equal(root.get("dummyColumn"), DronePortConstants.ACTIVE_STATUS_UNAVAILABLE))
        .thenReturn(predicate);
    when(builder.function("tsrange_lower", Timestamp.class, root.get("dummyColumn")))
        .thenReturn(expression);
    when(builder.lessThanOrEqualTo(expression, currentTime)).thenReturn(predicate);
    when(builder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.unavailableStatus(currentTime);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("dummyColumn"), DronePortConstants.ACTIVE_STATUS_UNAVAILABLE);
    verify(builder).lessThanOrEqualTo(expression, currentTime);
    verify(builder).and(predicate, predicate);
  }

  /**
   * メソッド名: maintenanceStatus<br>
   * 試験名: 指定日時における動作状況(使用不可):"メンテナンス中"の検索条件の生成<br>
   * 条件: 指定日時を渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  public void maintenanceStatus_正常() {
    Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
    // モック設定
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    Expression<Timestamp> expression = mock(Expression.class);

    // モック条件設定
    when(root.get("dronePortStatusEntity")).thenReturn(root).thenReturn(root).thenReturn(root);
    when(builder.equal(root.get("dummyColumn"), DronePortConstants.ACTIVE_STATUS_MAINTENANCE))
        .thenReturn(predicate);
    when(builder.function("tsrange_lower", Timestamp.class, root.get("dummyColumn")))
        .thenReturn(expression);
    when(builder.function("tsrange_upper", Timestamp.class, root.get("dummyColumn")))
        .thenReturn(expression);
    when(builder.lessThanOrEqualTo(expression, currentTime)).thenReturn(predicate);
    when(builder.greaterThan(expression, currentTime)).thenReturn(predicate);
    when(builder.and(any(Predicate.class), any(Predicate.class), any(Predicate.class)))
        .thenReturn(predicate);
    // 処理実行
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.maintenanceStatus(currentTime);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
    verify(builder).equal(root.get("dummyColumn"), DronePortConstants.ACTIVE_STATUS_MAINTENANCE);
    verify(builder).lessThanOrEqualTo(expression, currentTime);
    verify(builder).greaterThan(expression, currentTime);
    verify(builder).and(predicate, predicate, predicate);
  }

  /**
   * メソッド名: publicFlagEqual<br>
   * 試験名: 公開可否フラグの検索条件の生成<br>
   * 条件: 公開可否フラグを渡し、Specificationが正しく生成されることを確認する<br>
   * 結果: Specificationがnullでない<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testPublicFlagEqual() {
    Boolean flag = true;
    // モック設定
    Root<Object> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder builder = mock(CriteriaBuilder.class);
    Predicate predicate = mock(Predicate.class);
    // モック条件設定
    when(builder.equal(root.get("publicFlag"), flag)).thenReturn(predicate);
    DronePortInfoSpecification<Object> spec = new DronePortInfoSpecification<>();
    Specification<Object> specification = spec.publicFlagEqual(flag);
    Predicate result = specification.toPredicate(root, query, builder);
    // 結果確認
    assertNotNull(result);
  }
}
