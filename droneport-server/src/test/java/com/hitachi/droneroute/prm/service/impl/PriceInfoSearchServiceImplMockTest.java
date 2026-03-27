package com.hitachi.droneroute.prm.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.prm.constants.PriceInfoConstants;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
import io.hypersistence.utils.hibernate.type.range.Range;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

/** PriceInfoServiceImplクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
public class PriceInfoSearchServiceImplMockTest {

  @MockBean private PriceInfoRepository repository;

  @Autowired private PriceInfoSearchListServiceImpl service;

  @SpyBean private SystemSettings systemSettings;

  /**
   * メソッド名: getPriceInfoList<br>
   * 試験名: リソース料金情報検索の正常系テスト<br>
   * 条件: 正常な料金情報一覧取得リクエストで検索<br>
   * 結果: 正常終了<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getPriceInfoList_Nomal1() {
    PriceInfoSearchListRequestDto request = createPriceInfoSearchListRequestDto();
    List<PriceInfoEntity> entList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      PriceInfoEntity priceEnt = createPriceInfoEntityWithRandomUuid(String.valueOf(i));
      entList.add(priceEnt);
    }

    when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(entList);
    service.getPriceInfoList(request);
  }

  /**
   * メソッド名: getPriceInfoList<br>
   * 試験名: リソース料金情報検索の正常系テスト<br>
   * 条件: 検索条件を必須項目以外nullで検索<br>
   * 結果: 正常終了<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getPriceInfoList_Nomal_項目null() {
    PriceInfoSearchListRequestDto request = new PriceInfoSearchListRequestDto();
    request.setResourceId(UUID.randomUUID().toString() + ",," + UUID.randomUUID());
    List<PriceInfoEntity> entList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        PriceInfoEntity priceEnt = createPriceInfoEntityWithRandomUuid(String.valueOf(i));
        entList.add(priceEnt);
      }
    }
    when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(entList);
    service.getPriceInfoList(request);
  }

  /**
   * メソッド名: getPriceInfoList<br>
   * 試験名: リソース料金情報検索の正常系テスト<br>
   * 条件: 検索条件を必須項目以外空で検索<br>
   * 結果: 正常終了<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void getPriceInfoList_Nomal_項目empty() {
    PriceInfoSearchListRequestDto request = new PriceInfoSearchListRequestDto();
    request.setResourceId(UUID.randomUUID().toString());
    request.setPriceId("");
    request.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
    request.setPriceType(BigInteger.valueOf(4));
    request.setPricePerUnitFrom(BigInteger.valueOf(1));
    request.setPricePerUnitTo(BigInteger.valueOf(2));
    request.setPriceFrom(BigInteger.valueOf(1000));
    request.setPriceTo(BigInteger.valueOf(2000));
    request.setEffectiveStartTime("");
    request.setEffectiveEndTime("");
    request.setSortOrders("");
    request.setSortColumns("");
    List<PriceInfoEntity> entList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      PriceInfoEntity priceEnt = createPriceInfoEntityWithRandomUuid(String.valueOf(i));
      entList.add(priceEnt);
    }
    when(repository.findAll()).thenReturn(entList);
    service.getPriceInfoList(request);
  }

  /**
   * メソッド名: getList<br>
   * 試験名: リソース料金情報検索の正常系テスト<br>
   * 条件: 検索条件をresourceId,sortColumns,sortOrdersがnullで検索<br>
   * 結果: 正常終了<br>
   * テストパターン: 正常系<br>
   */
  @SuppressWarnings("unchecked")
  @Test
  void getList_Nomal1() {
    PriceInfoSearchListRequestDto request = createPriceInfoSearchListRequestDto();
    request.setResourceId(null);
    request.setSortColumns(null);
    request.setSortOrders(null);
    List<PriceInfoEntity> entList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      PriceInfoEntity priceEnt = createPriceInfoEntityWithRandomUuid(String.valueOf(i));
      entList.add(priceEnt);
    }

    when(repository.findAll(any(Specification.class))).thenReturn(entList);
    service.getList(request);
  }

  /**
   * 料金情報一覧用リクエストDTO作成
   *
   * @return
   */
  private PriceInfoSearchListRequestDto createPriceInfoSearchListRequestDto() {
    PriceInfoSearchListRequestDto ret = new PriceInfoSearchListRequestDto();
    ret.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
    ret.setResourceId("リソースID");
    ret.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
    ret.setPriceType(BigInteger.valueOf(4));
    ret.setPricePerUnitFrom(BigInteger.valueOf(1));
    ret.setPricePerUnitTo(BigInteger.valueOf(2));
    ret.setPriceFrom(BigInteger.valueOf(1000));
    ret.setPriceTo(BigInteger.valueOf(2000));
    ret.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret.setEffectiveEndTime("2025-11-13T11:00:00Z");
    ret.setSortOrders("1,0,0,0");
    ret.setSortColumns("effectiveTime,resourceType,resourceId,priority");

    return ret;
  }

  /**
   * 料金情報エンティティ作成
   *
   * @return
   */
  private PriceInfoEntity createPriceInfoEntity(String num) {

    String startStr = "2025-11-13T10:00:00Z";
    String endStr = "2025-11-13T11:00:00Z";

    PriceInfoEntity entity = new PriceInfoEntity();
    entity.setPriceId(UUID.randomUUID());
    entity.setResourceId("リソースID" + num);
    entity.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
    entity.setPrimaryRouteOperatorId("主管航路事業者ID1");
    entity.setPriceType(4);
    entity.setPricePerUnit(1);
    entity.setPrice(1000);
    entity.setEffectiveTime(
        Range.closedOpen(
            java.time.OffsetDateTime.parse(startStr).toLocalDateTime(),
            java.time.OffsetDateTime.parse(endStr).toLocalDateTime()));
    entity.setPriority(1);
    entity.setOperatorId("ope01");
    entity.setDeleteFlag(false);
    return entity;
  }

  /**
   * ランダムUUIDを持つ料金情報エンティティ作成
   *
   * @param num 識別番号
   * @return 料金情報エンティティ
   */
  private PriceInfoEntity createPriceInfoEntityWithRandomUuid(String num) {
    PriceInfoEntity entity = createPriceInfoEntity(num);
    entity.setPriceId(UUID.randomUUID());
    return entity;
  }
}
