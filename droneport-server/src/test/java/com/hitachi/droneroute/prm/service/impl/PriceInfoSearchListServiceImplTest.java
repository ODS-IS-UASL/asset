// DB接続を行う想定のテストのため、一括テストの対象外とするためにコメントアウト

// package com.hitachi.droneroute.prm.service.impl;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.nullable;
// import static org.mockito.Mockito.doThrow;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import java.math.BigInteger;
// import java.util.UUID;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Tag;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInfo;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.SpyBean;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.jpa.domain.Specification;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;
// import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
// import com.hitachi.droneroute.cmn.settings.SystemSettings;
// import com.hitachi.droneroute.prm.constants.PriceInfoConstants;
// import com.hitachi.droneroute.prm.dto.PriceInfoSearchListDetailElement;
// import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
// import com.hitachi.droneroute.prm.dto.PriceInfoSearchListResponseDto;
// import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
// import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
// import io.hypersistence.utils.hibernate.type.range.Range;
//
/// ** PriceInfoServiceImplクラスの単体テスト */
// @SpringBootTest
// @Transactional
// @ActiveProfiles("test")
// public class PriceInfoSearchListServiceImplTest {
//
//    @SpyBean
//    private PriceInfoRepository repository;
//
//    @Autowired
//    private PriceInfoSearchListServiceImpl service;
//
//    @SpyBean
//    private SystemSettings systemSettings;
//
//    // 特定のテストで使用するUUID
//    private String uuidForPriceIdSearch; // 料金ID検索用
//    private String uuidForDeleteFlag; // 削除フラグ検索用
//
//    @BeforeEach
//    void setUp(TestInfo testInfo) {
//      // 料金情報検索データ登録
//
//      // SkipSetupタグが付いているテストはスキップ
//      if (testInfo.getTags().contains("SkipSetup")) {
//        return;
//      }
//
//      // 料金ID検索対象
//      PriceInfoEntity entity = createPriceInfoEntity("1");
//      UUID uuid = UUID.randomUUID();
//      uuidForPriceIdSearch = uuid.toString();
//      entity.setPriceId(uuid);
//      repository.save(entity);
//
//      // リソースID検索対象
//      entity = createPriceInfoEntity("1");
//      entity.setResourceId("リソースID2");
//      repository.save(entity);
//
//      // リソース種別検索対象
//      entity = createPriceInfoEntity("1");
//      entity.setResourceId("f3113bed-357e-2386-2b15-effaf01a592e");
//      entity.setResourceType(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT);
//      repository.save(entity);
//
//      // 料金タイプ検索対象
//      entity = createPriceInfoEntity("1");
//      entity.setPriceType(5);
//      repository.save(entity);
//
//      // 料金単位検索対象
//      entity = createPriceInfoEntity("1");
//      entity.setPricePerUnit(3);
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setPricePerUnit(4);
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setPricePerUnit(5);
//      repository.save(entity);
//
//      // 料金検索対象
//      entity = createPriceInfoEntity("1");
//      entity.setPrice(2000);
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setPrice(3000);
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setPrice(4000);
//      repository.save(entity);
//
//      // 適用日時範囲検索対象
//      entity = createPriceInfoEntity("1");
//      entity.setEffectiveTime(
//
//   Range.closedOpen(java.time.OffsetDateTime.parse("2025-11-15T11:00:00Z").toLocalDateTime(),
//              java.time.OffsetDateTime.parse("2025-11-15T12:00:00Z").toLocalDateTime()));
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setEffectiveTime(
//
//   Range.closedOpen(java.time.OffsetDateTime.parse("2025-11-15T12:00:00Z").toLocalDateTime(),
//              java.time.OffsetDateTime.parse("2025-11-15T13:00:00Z").toLocalDateTime()));
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setEffectiveTime(
//
//   Range.closedOpen(java.time.OffsetDateTime.parse("2025-11-15T15:00:00Z").toLocalDateTime(),
//              java.time.OffsetDateTime.parse("2025-11-15T16:00:00Z").toLocalDateTime()));
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setEffectiveTime(
//
//   Range.closedOpen(java.time.OffsetDateTime.parse("2025-11-13T09:00:00Z").toLocalDateTime(),
//              java.time.OffsetDateTime.parse("2025-11-13T09:59:59Z").toLocalDateTime()));
//      repository.save(entity);
//
//      // 削除フラグ検索対象
//      entity = createPriceInfoEntity("1");
//      uuid = UUID.randomUUID();
//      uuidForDeleteFlag = uuid.toString();
//      entity.setPriceId(uuid);
//      entity.setDeleteFlag(true);
//      repository.save(entity);
//
//      // ソート条件
//      entity = createPriceInfoEntity("1");
//      entity.setResourceId("リソースID11");
//      entity.setPriority(1);
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setResourceId("リソースID11");
//      entity.setPriority(2);
//      repository.save(entity);
//
//      // リソースID複数件検索対象
//      entity = createPriceInfoEntity("1");
//      entity.setResourceId("リソースID20");
//      entity.setPrice(1000);
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setResourceId("リソースID20");
//      entity.setPrice(2000);
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setResourceId("リソースID21");
//      entity.setPrice(3000);
//      repository.save(entity);
//
//      // 主管航路事業者ID検索対象
//      entity = createPriceInfoEntity("1");
//      entity.setPriceId(UUID.fromString("74ed6fd9-5e63-41ab-b50c-c3cfbb89e101"));
//      entity.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd201");
//      entity.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
//      entity.setPrimaryRouteOperatorId("ROUTEOPRID");
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setPriceId(UUID.fromString("74ed6fd9-5e63-41ab-b50c-c3cfbb89e102"));
//      entity.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd201");
//      entity.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
//      entity.setPrimaryRouteOperatorId("TEST");
//      entity.setPriority(2);
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setPriceId(UUID.fromString("74ed6fd9-5e63-41ab-b50c-c3cfbb89e103"));
//      entity.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd202");
//      entity.setResourceType(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT);
//      entity.setPrimaryRouteOperatorId("ROUTEOPRID");
//      repository.save(entity);
//
//      entity = createPriceInfoEntity("1");
//      entity.setPriceId(UUID.fromString("74ed6fd9-5e63-41ab-b50c-c3cfbb89e104"));
//      entity.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd202");
//      entity.setResourceType(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT);
//      entity.setPrimaryRouteOperatorId("TEST");
//      entity.setPriority(2);
//      repository.save(entity);
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 料金情報IDで検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_1件取得() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setPriceId(uuidForPriceIdSearch);
//      req.setResourceId("リソースID1");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      assertEquals(1, res.getResources().size());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().size());
//
//      PriceInfoSearchListDetailElement expectedPrice =
//   createPriceInfoSearchListDetailElement("1");
//
//      assertEquals(uuidForPriceIdSearch,
//   res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals(expectedPrice.getPriceType(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriceType());
//      assertEquals(expectedPrice.getPricePerUnit(),
//          res.getResources().get(0).getPriceInfos().get(0).getPricePerUnit());
//      assertEquals(expectedPrice.getPrice(),
//          res.getResources().get(0).getPriceInfos().get(0).getPrice());
//      assertEquals(expectedPrice.getEffectiveStartTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals(expectedPrice.getEffectiveEndTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveEndTime());
//      assertEquals(expectedPrice.getPriority(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriority());
//
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 条件全指定で検索<br>
//     * 結果: 正常終了<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_0件取得() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req = createPriceInfoSearchListRequestDto();
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      assertEquals(0, res.getResources().size());
//
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 全項目nullで検索<br>
//     * 結果: 正常終了<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    @Tag("SkipSetup")
//    public void getPriceInfoList_正常_1件取得_項目null() {
//
//      repository.save(createPriceInfoEntityWithRandomUuid("1"));
//
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setPriceId(null);
//      req.setResourceId("リソースID1");
//      req.setResourceType(null);
//      req.setPriceType(null);
//      req.setPricePerUnitFrom(null);
//      req.setPricePerUnitTo(null);
//      req.setPriceFrom(null);
//      req.setPriceTo(null);
//      req.setEffectiveStartTime(null);
//      req.setEffectiveEndTime(null);
//      req.setSortOrders(null);
//      req.setSortColumns(null);
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      assertEquals(1, res.getResources().size());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().size());
//
//      PriceInfoSearchListDetailElement expectedPrice =
//   createPriceInfoSearchListDetailElement("1");
//
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals(expectedPrice.getPriceType(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriceType());
//      assertEquals(expectedPrice.getPricePerUnit(),
//          res.getResources().get(0).getPriceInfos().get(0).getPricePerUnit());
//      assertEquals(expectedPrice.getPrice(),
//          res.getResources().get(0).getPriceInfos().get(0).getPrice());
//      assertEquals(expectedPrice.getEffectiveStartTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals(expectedPrice.getEffectiveEndTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveEndTime());
//      assertEquals(expectedPrice.getPriority(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriority());
//
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 全項目空で検索<br>
//     * 結果: 正常終了<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    @Tag("SkipSetup")
//    public void getPriceInfoList_正常_1件取得_項目空() {
//
//      PriceInfoEntity entity = createPriceInfoEntity("1");
//      entity.setResourceId("リソースID1");
//      repository.save(entity);
//
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setPriceId("");
//      req.setResourceId("リソースID1");
//      req.setPricePerUnitTo(null);
//      req.setPriceFrom(null);
//      req.setPriceTo(null);
//      req.setEffectiveStartTime("");
//      req.setEffectiveEndTime("");
//      req.setSortOrders("");
//      req.setSortColumns("");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      assertEquals(1, res.getResources().size());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().size());
//
//      PriceInfoSearchListDetailElement expectedPrice =
//   createPriceInfoSearchListDetailElement("1");
//
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals(expectedPrice.getPriceType(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriceType());
//      assertEquals(expectedPrice.getPricePerUnit(),
//          res.getResources().get(0).getPriceInfos().get(0).getPricePerUnit());
//      assertEquals(expectedPrice.getPrice(),
//          res.getResources().get(0).getPriceInfos().get(0).getPrice());
//      assertEquals(expectedPrice.getEffectiveStartTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals(expectedPrice.getEffectiveEndTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveEndTime());
//      assertEquals(expectedPrice.getPriority(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriority());
//
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソースIDで検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_リソースID() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID2");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      assertEquals(1, res.getResources().size());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().size());
//
//      PriceInfoSearchListDetailElement expectedPrice =
//   createPriceInfoSearchListDetailElement("1");
//
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals("リソースID2", res.getResources().get(0).getResourceId());
//      assertEquals(expectedPrice.getPriceType(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriceType());
//      assertEquals(expectedPrice.getPricePerUnit(),
//          res.getResources().get(0).getPriceInfos().get(0).getPricePerUnit());
//      assertEquals(expectedPrice.getPrice(),
//          res.getResources().get(0).getPriceInfos().get(0).getPrice());
//      assertEquals(expectedPrice.getEffectiveStartTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals(expectedPrice.getEffectiveEndTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveEndTime());
//      assertEquals(expectedPrice.getPriority(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriority());
//
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソース種別で検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_リソース種別() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("f3113bed-357e-2386-2b15-effaf01a592e");
//      req.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT));
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      assertEquals(1, res.getResources().size());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().size());
//
//      PriceInfoSearchListDetailElement expectedPrice =
//   createPriceInfoSearchListDetailElement("1");
//
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals(30, res.getResources().get(0).getResourceType());
//      assertEquals(expectedPrice.getPriceType(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriceType());
//      assertEquals(expectedPrice.getPricePerUnit(),
//          res.getResources().get(0).getPriceInfos().get(0).getPricePerUnit());
//      assertEquals(expectedPrice.getPrice(),
//          res.getResources().get(0).getPriceInfos().get(0).getPrice());
//      assertEquals(expectedPrice.getEffectiveStartTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals(expectedPrice.getEffectiveEndTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveEndTime());
//      assertEquals(expectedPrice.getPriority(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriority());
//
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 料金タイプで検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_料金タイプ() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID1");
//      req.setPriceType(BigInteger.valueOf(5));
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      assertEquals(1, res.getResources().size());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().size());
//
//      PriceInfoSearchListDetailElement expectedPrice =
//   createPriceInfoSearchListDetailElement("1");
//
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals(5, res.getResources().get(0).getPriceInfos().get(0).getPriceType());
//      assertEquals(expectedPrice.getPricePerUnit(),
//          res.getResources().get(0).getPriceInfos().get(0).getPricePerUnit());
//      assertEquals(expectedPrice.getPrice(),
//          res.getResources().get(0).getPriceInfos().get(0).getPrice());
//      assertEquals(expectedPrice.getEffectiveStartTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals(expectedPrice.getEffectiveEndTime(),
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveEndTime());
//      assertEquals(expectedPrice.getPriority(),
//          res.getResources().get(0).getPriceInfos().get(0).getPriority());
//
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 料金単位(以上)、料金単位(以下)で検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_料金単位() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID1");
//      req.setPricePerUnitFrom(BigInteger.valueOf(3));
//      req.setPricePerUnitTo(BigInteger.valueOf(4));
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 2つのリソース、それぞれ1つの料金情報を持つ
//      assertEquals(1, res.getResources().size());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//
//      // 料金単位3の検証
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals(3, res.getResources().get(0).getPriceInfos().get(0).getPricePerUnit());
//      assertEquals(1000, res.getResources().get(0).getPriceInfos().get(0).getPrice());
//
//      // 料金単位4の検証
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(1).getPriceId());
//      assertEquals(4, res.getResources().get(0).getPriceInfos().get(1).getPricePerUnit());
//      assertEquals(1000, res.getResources().get(0).getPriceInfos().get(1).getPrice());
//
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 料金(以上)、料金(以下)で検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_料金() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID1");
//      req.setPriceFrom(BigInteger.valueOf(2000));
//      req.setPriceTo(BigInteger.valueOf(3000));
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 2つのリソース、それぞれ1つの料金情報を持つ
//      assertEquals(1, res.getResources().size());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//
//      // 料金2000円の検証
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals(2000, res.getResources().get(0).getPriceInfos().get(0).getPrice());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().get(0).getPricePerUnit());
//
//      // 料金3000円の検証
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(1).getPriceId());
//      assertEquals(3000, res.getResources().get(0).getPriceInfos().get(1).getPrice());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().get(1).getPricePerUnit());
//
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 適用開始日時で検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_適用開始日時() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID1");
//      req.setEffectiveStartTime("2025-11-15T15:00:00Z");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      assertEquals(1, res.getResources().size());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().size());
//
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals("2025-11-15T15:00:00Z",
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals("2025-11-15T16:00:00Z",
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveEndTime());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 適用終了日時で検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_適用終了日時() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID1");
//      req.setEffectiveEndTime("2025-11-13T09:59:59Z");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      assertEquals(1, res.getResources().size());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().size());
//
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals("2025-11-13T09:00:00Z",
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals("2025-11-13T09:59:59Z",
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveEndTime());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 適用日時範囲で検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_適用日時範囲() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID1");
//      req.setEffectiveStartTime("2025-11-15T11:00:00Z");
//      req.setEffectiveEndTime("2025-11-15T13:00:00Z");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 1つのリソース、2つの料金情報を持つ
//      assertEquals(1, res.getResources().size());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//
//      // 適用日時範囲12:00-13:00の検証
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals("2025-11-15T12:00:00Z",
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals("2025-11-15T13:00:00Z",
//          res.getResources().get(0).getPriceInfos().get(0).getEffectiveEndTime());
//
//      // 適用日時範囲11:00-12:00の検証
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(1).getPriceId());
//      assertEquals("2025-11-15T11:00:00Z",
//          res.getResources().get(0).getPriceInfos().get(1).getEffectiveStartTime());
//      assertEquals("2025-11-15T12:00:00Z",
//          res.getResources().get(0).getPriceInfos().get(1).getEffectiveEndTime());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: 削除フラグtrueのデータで検索<br>
//     * 結果: 削除フラグtrueのデータが取得されない<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_削除フラグ() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setPriceId(uuidForDeleteFlag);
//      req.setResourceId("リソースID1");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 削除フラグtrueのデータは取得されない
//      assertEquals(0, res.getResources().size());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: ソート条件指定で検索<br>
//     * 結果: ソートされる<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_ソート条件指定() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID11");
//      req.setSortColumns("resourceId,priority");
//      req.setSortOrders("0,0");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 同じリソースIDなので1つのリソースに2つの料金情報がグルーピングされる
//      assertEquals(1, res.getResources().size());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//      assertEquals("リソースID11", res.getResources().get(0).getResourceId());
//
//      // priority順にソートされていることを確認
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().get(0).getPriority());
//
//      assertNotNull(res.getResources().get(0).getPriceInfos().get(1).getPriceId());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().get(1).getPriority());
//
//    }
//
//    /**
//     * メソッド名: getList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: ソート条件指定なしで検索<br>
//     * 結果: データが取得される<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getList_正常_検索条件_ソート条件なし() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID11");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 同じリソースIDなので1つのリソースに2つの料金情報がグルーピングされる
//      assertEquals(1, res.getResources().size());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//      assertEquals("リソースID11", res.getResources().get(0).getResourceId());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の異常系テスト<br>
//     * 条件: リポジトリで想定外のエラーが発生<br>
//     * 結果: ServiceErrorExceptionが発生<br>
//     * テストパターン: 異常系<br>
//     */
//    @Test
//    public void getPriceInfoList_異常() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID11");
//
//      doThrow(new ServiceErrorException("想定外のエラー"))
//      .when(repository).findAll(any(Specification.class), nullable(Sort.class));
//      Exception ex = assertThrows(ServiceErrorException.class, () ->
//   service.getPriceInfoList(req));
//      assertEquals("想定外のエラー", ex.getMessage());
//      verify(repository, times(1)).findAll(any(Specification.class), nullable(Sort.class));
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソースID複数件で検索<br>
//     * 結果: DTOのマッピング内容を確認<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_検索条件_リソースID複数件() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//      req.setResourceId("リソースID20,リソースID21");
//      req.setSortColumns("price");
//      req.setSortOrders("0");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 2つのリソースが取得されること
//      assertEquals(2, res.getResources().size());
//
//      // リソースID20の検証（料金情報2件）
//      assertEquals("リソースID20", res.getResources().get(0).getResourceId());
//      assertEquals(20, res.getResources().get(0).getResourceType());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//
//      // リソースID20の料金情報1件目（1000円）
//      assertEquals(1000, res.getResources().get(0).getPriceInfos().get(0).getPrice());
//      assertEquals(4, res.getResources().get(0).getPriceInfos().get(0).getPriceType());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().get(0).getPricePerUnit());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().get(0).getPriority());
//      assertEquals("ope01", res.getResources().get(0).getPriceInfos().get(0).getOperatorId());
//
//      // リソースID20の料金情報2件目（2000円）
//      assertEquals(2000, res.getResources().get(0).getPriceInfos().get(1).getPrice());
//      assertEquals(4, res.getResources().get(0).getPriceInfos().get(1).getPriceType());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().get(1).getPricePerUnit());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().get(1).getPriority());
//      assertEquals("ope01", res.getResources().get(0).getPriceInfos().get(1).getOperatorId());
//
//      // リソースID21の検証（料金情報1件）
//      assertEquals("リソースID21", res.getResources().get(1).getResourceId());
//      assertEquals(20, res.getResources().get(1).getResourceType());
//      assertEquals(1, res.getResources().get(1).getPriceInfos().size());
//
//      // リソースID21の料金情報（3000円）
//      assertEquals(3000, res.getResources().get(1).getPriceInfos().get(0).getPrice());
//      assertEquals(4, res.getResources().get(1).getPriceInfos().get(0).getPriceType());
//      assertEquals(1, res.getResources().get(1).getPriceInfos().get(0).getPricePerUnit());
//      assertEquals(1, res.getResources().get(1).getPriceInfos().get(0).getPriority());
//      assertEquals("ope01", res.getResources().get(1).getPriceInfos().get(0).getOperatorId());
//      assertEquals("2025-11-13T10:00:00Z",
//          res.getResources().get(1).getPriceInfos().get(0).getEffectiveStartTime());
//      assertEquals("2025-11-13T11:00:00Z",
//          res.getResources().get(1).getPriceInfos().get(0).getEffectiveEndTime());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソースID複数件、リソース種別null、主管航路事業者IDnullで検索<br>
//     * 結果: 複数リソースの料金情報が正常に取得される<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_主管航路事業者追加_項目null() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//
//   req.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd201,b38560dc-f35b-4817-b3ca-92989a5cd202");
//      req.setResourceType(null);
//      req.setPrimaryRouteOperatorId(null);
//      req.setSortColumns("resourceId,priority");
//      req.setSortOrders("0,0");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 2つのリソースが取得されること
//      assertEquals(2, res.getResources().size());
//
//      // リソースID: b38560dc-f35b-4817-b3ca-92989a5cd201 の検証（料金情報2件）
//      assertEquals("b38560dc-f35b-4817-b3ca-92989a5cd201",
//   res.getResources().get(0).getResourceId());
//      assertEquals(20, res.getResources().get(0).getResourceType());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=ROUTEOPRID）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e101",
//          res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals("ROUTEOPRID",
//          res.getResources().get(0).getPriceInfos().get(0).getPrimaryRouteOperatorId());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=TEST）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e102",
//          res.getResources().get(0).getPriceInfos().get(1).getPriceId());
//      assertEquals("TEST",
//          res.getResources().get(0).getPriceInfos().get(1).getPrimaryRouteOperatorId());
//
//      // リソースID: b38560dc-f35b-4817-b3ca-92989a5cd202 の検証（料金情報2件）
//      assertEquals("b38560dc-f35b-4817-b3ca-92989a5cd202",
//   res.getResources().get(1).getResourceId());
//      assertEquals(30, res.getResources().get(1).getResourceType());
//      assertEquals(2, res.getResources().get(1).getPriceInfos().size());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=ROUTEOPRID）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e103",
//          res.getResources().get(1).getPriceInfos().get(0).getPriceId());
//      assertEquals("ROUTEOPRID",
//          res.getResources().get(1).getPriceInfos().get(0).getPrimaryRouteOperatorId());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=TEST）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e104",
//          res.getResources().get(1).getPriceInfos().get(1).getPriceId());
//      assertEquals("TEST",
//          res.getResources().get(1).getPriceInfos().get(1).getPrimaryRouteOperatorId());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソースID複数件、リソース種別null、主管航路事業者ID空で検索<br>
//     * 結果: 複数リソースの料金情報が正常に取得される<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_主管航路事業者追加_項目空() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//
//   req.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd201,b38560dc-f35b-4817-b3ca-92989a5cd202");
//      req.setResourceType(null);
//      req.setPrimaryRouteOperatorId("");
//      req.setSortColumns("resourceId,priority");
//      req.setSortOrders("0,0");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 2つのリソースが取得されること
//      assertEquals(2, res.getResources().size());
//
//      // リソースID: b38560dc-f35b-4817-b3ca-92989a5cd201 の検証（料金情報2件）
//      assertEquals("b38560dc-f35b-4817-b3ca-92989a5cd201",
//   res.getResources().get(0).getResourceId());
//      assertEquals(20, res.getResources().get(0).getResourceType());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=ROUTEOPRID）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e101",
//          res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals("ROUTEOPRID",
//          res.getResources().get(0).getPriceInfos().get(0).getPrimaryRouteOperatorId());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=TEST）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e102",
//          res.getResources().get(0).getPriceInfos().get(1).getPriceId());
//      assertEquals("TEST",
//          res.getResources().get(0).getPriceInfos().get(1).getPrimaryRouteOperatorId());
//
//      // リソースID: b38560dc-f35b-4817-b3ca-92989a5cd202 の検証（料金情報2件）
//      assertEquals("b38560dc-f35b-4817-b3ca-92989a5cd202",
//   res.getResources().get(1).getResourceId());
//      assertEquals(30, res.getResources().get(1).getResourceType());
//      assertEquals(2, res.getResources().get(1).getPriceInfos().size());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=ROUTEOPRID）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e103",
//          res.getResources().get(1).getPriceInfos().get(0).getPriceId());
//      assertEquals("ROUTEOPRID",
//          res.getResources().get(1).getPriceInfos().get(0).getPrimaryRouteOperatorId());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=TEST）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e104",
//          res.getResources().get(1).getPriceInfos().get(1).getPriceId());
//      assertEquals("TEST",
//          res.getResources().get(1).getPriceInfos().get(1).getPrimaryRouteOperatorId());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソースID複数件、リソース種別20で検索<br>
//     * 結果: リソース種別20の料金情報が正常に取得される<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_主管航路事業者追加_リソース種別20() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//
//   req.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd201,b38560dc-f35b-4817-b3ca-92989a5cd202");
//      req.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
//      req.setSortColumns("resourceId,priority");
//      req.setSortOrders("0,0");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // リソース種別20のリソースのみ取得されること
//      assertEquals(1, res.getResources().size());
//
//      // リソースID: b38560dc-f35b-4817-b3ca-92989a5cd201 の検証（料金情報2件）
//      assertEquals("b38560dc-f35b-4817-b3ca-92989a5cd201",
//   res.getResources().get(0).getResourceId());
//      assertEquals(20, res.getResources().get(0).getResourceType());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=ROUTEOPRID）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e101",
//          res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals("ROUTEOPRID",
//          res.getResources().get(0).getPriceInfos().get(0).getPrimaryRouteOperatorId());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=TEST）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e102",
//          res.getResources().get(0).getPriceInfos().get(1).getPriceId());
//      assertEquals("TEST",
//          res.getResources().get(0).getPriceInfos().get(1).getPrimaryRouteOperatorId());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソースID複数件、リソース種別30で検索<br>
//     * 結果: リソース種別30の料金情報が正常に取得される<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_主管航路事業者追加_リソース種別30() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//
//   req.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd201,b38560dc-f35b-4817-b3ca-92989a5cd202");
//      req.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_AIRCRAFT));
//      req.setSortColumns("resourceId,priority");
//      req.setSortOrders("0,0");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // リソース種別30のリソースのみ取得されること
//      assertEquals(1, res.getResources().size());
//
//      // リソースID: b38560dc-f35b-4817-b3ca-92989a5cd202 の検証（料金情報2件）
//      assertEquals("b38560dc-f35b-4817-b3ca-92989a5cd202",
//   res.getResources().get(0).getResourceId());
//      assertEquals(30, res.getResources().get(0).getResourceType());
//      assertEquals(2, res.getResources().get(0).getPriceInfos().size());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=ROUTEOPRID）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e103",
//          res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals("ROUTEOPRID",
//          res.getResources().get(0).getPriceInfos().get(0).getPrimaryRouteOperatorId());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=TEST）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e104",
//          res.getResources().get(0).getPriceInfos().get(1).getPriceId());
//      assertEquals("TEST",
//          res.getResources().get(0).getPriceInfos().get(1).getPrimaryRouteOperatorId());
//    }
//
//    /**
//     * メソッド名: getList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソースID複数件、リソース種別40で検索<br>
//     * 結果: リソース種別40のデータが存在しないため0件取得される<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getList_正常_主管航路事業者追加_リソース種別40() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//
//
// req.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd201,b38560dc-f35b-4817-b3ca-92989a5cd202,b38560dc-f35b-4817-b3ca-92989a5cd203");
//      req.setResourceType(BigInteger.valueOf(40));
//      req.setSortColumns("resourceId,priority");
//      req.setSortOrders("0,0");
//
//      // Act
//      // バリデーションは実施しないため、getListで呼び出し
//      PriceInfoSearchListResponseDto res = service.getList(req);
//
//      // Assert
//      // リソース種別40のデータが存在しないため0件取得される
//      assertEquals(0, res.getResources().size());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソースID複数件、リソース種別null、主管航路事業者ID指定で検索<br>
//     * 結果: 指定された主管航路事業者IDの料金情報が正常に取得される<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_主管航路事業者追加_主管航路事業者ID存在() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//
//   req.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd201,b38560dc-f35b-4817-b3ca-92989a5cd202");
//      req.setResourceType(null);
//      req.setPrimaryRouteOperatorId("ROUTEOPRID");
//      req.setSortColumns("resourceId,priority");
//      req.setSortOrders("0,0");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 2つのリソースが取得されること（各リソース1件ずつ）
//      assertEquals(2, res.getResources().size());
//
//      // リソースID: b38560dc-f35b-4817-b3ca-92989a5cd201 の検証（料金情報1件）
//      assertEquals("b38560dc-f35b-4817-b3ca-92989a5cd201",
//   res.getResources().get(0).getResourceId());
//      assertEquals(20, res.getResources().get(0).getResourceType());
//      assertEquals(1, res.getResources().get(0).getPriceInfos().size());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=ROUTEOPRID）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e101",
//          res.getResources().get(0).getPriceInfos().get(0).getPriceId());
//      assertEquals("ROUTEOPRID",
//          res.getResources().get(0).getPriceInfos().get(0).getPrimaryRouteOperatorId());
//
//      // リソースID: b38560dc-f35b-4817-b3ca-92989a5cd202 の検証（料金情報1件）
//      assertEquals("b38560dc-f35b-4817-b3ca-92989a5cd202",
//   res.getResources().get(1).getResourceId());
//      assertEquals(30, res.getResources().get(1).getResourceType());
//      assertEquals(1, res.getResources().get(1).getPriceInfos().size());
//
//      // 料金ID、主管航路事業者IDの検証（primary_route_operator_id=ROUTEOPRID）
//      assertEquals("74ed6fd9-5e63-41ab-b50c-c3cfbb89e103",
//          res.getResources().get(1).getPriceInfos().get(0).getPriceId());
//      assertEquals("ROUTEOPRID",
//          res.getResources().get(1).getPriceInfos().get(0).getPrimaryRouteOperatorId());
//    }
//
//    /**
//     * メソッド名: getPriceInfoList<br>
//     * 試験名: リソース料金情報検索の正常系テスト<br>
//     * 条件: リソースID複数件、リソース種別null、存在しない主管航路事業者ID指定で検索<br>
//     * 結果: データが存在しないため0件取得される<br>
//     * テストパターン: 正常系<br>
//     */
//    @Test
//    public void getPriceInfoList_正常_主管航路事業者追加_主管航路事業者ID存在しない() {
//      // Arrange
//      PriceInfoSearchListRequestDto req = new PriceInfoSearchListRequestDto();
//
//   req.setResourceId("b38560dc-f35b-4817-b3ca-92989a5cd201,b38560dc-f35b-4817-b3ca-92989a5cd202");
//      req.setResourceType(null);
//      req.setPrimaryRouteOperatorId("ROUTEOPRID2");
//      req.setSortColumns("resourceId,priority");
//      req.setSortOrders("0,0");
//
//      // Act
//      PriceInfoSearchListResponseDto res = service.getPriceInfoList(req);
//
//      // Assert
//      // 指定された主管航路事業者IDのデータが存在しないため0件取得される
//      assertEquals(0, res.getResources().size());
//    }
//
//    /**
//     * 料金情報一覧用リクエストDTO作成
//     *
//     * @return
//     */
//    private PriceInfoSearchListRequestDto createPriceInfoSearchListRequestDto() {
//      PriceInfoSearchListRequestDto ret = new PriceInfoSearchListRequestDto();
//      ret.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//      ret.setResourceId("リソースID");
//      ret.setResourceType(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT));
//      ret.setPriceType(BigInteger.valueOf(4));
//      ret.setPricePerUnitFrom(BigInteger.valueOf(1));
//      ret.setPricePerUnitTo(BigInteger.valueOf(2));
//      ret.setPriceFrom(BigInteger.valueOf(1000));
//      ret.setPriceTo(BigInteger.valueOf(2000));
//      ret.setEffectiveStartTime("2025-11-13T10:00:00Z");
//      ret.setEffectiveEndTime("2025-11-13T11:00:00Z");
//      ret.setSortOrders("");
//      ret.setSortColumns("");
//
//      return ret;
//    }
//
//    /**
//     * 料金情報詳細要素作成
//     *
//     * @param num 識別番号
//     * @return 料金情報詳細要素
//     */
//    private PriceInfoSearchListDetailElement createPriceInfoSearchListDetailElement(String num) {
//      PriceInfoSearchListDetailElement ele = new PriceInfoSearchListDetailElement();
//      ele.setPriceType(4);
//      ele.setPricePerUnit(1);
//      ele.setPrice(1000);
//      ele.setEffectiveStartTime("2025-11-13T10:00:00Z");
//      ele.setEffectiveEndTime("2025-11-13T11:00:00Z");
//      ele.setPriority(1);
//      ele.setOperatorId("ope01");
//
//      return ele;
//    }
//
//    /**
//     * 料金情報エンティティ作成
//     *
//     * @return
//     */
//    private PriceInfoEntity createPriceInfoEntity(String num) {
//
//      String startStr = "2025-11-13T10:00:00Z";
//      String endStr = "2025-11-13T11:00:00Z";
//
//      PriceInfoEntity entity = new PriceInfoEntity();
//      entity.setPriceId(UUID.randomUUID());
//      entity.setResourceId("リソースID" + num);
//      entity.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
//      entity.setPrimaryRouteOperatorId("主管航路事業者ID1");
//      entity.setPriceType(4);
//      entity.setPricePerUnit(1);
//      entity.setPrice(1000);
//      entity.setEffectiveTime(
//          Range.closedOpen(java.time.OffsetDateTime.parse(startStr).toLocalDateTime(),
//              java.time.OffsetDateTime.parse(endStr).toLocalDateTime()));
//      entity.setPriority(1);
//      entity.setOperatorId("ope01");
//      entity.setDeleteFlag(false);
//      return entity;
//    }
//
//    /**
//     * ランダムUUIDを持つ料金情報エンティティ作成
//     *
//     * @param num 識別番号
//     * @return 料金情報エンティティ
//     */
//    private PriceInfoEntity createPriceInfoEntityWithRandomUuid(String num) {
//      PriceInfoEntity entity = createPriceInfoEntity(num);
//      entity.setPriceId(UUID.randomUUID());
//      return entity;
//    }
//
// }
