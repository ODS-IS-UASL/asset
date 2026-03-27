package com.hitachi.droneroute.prm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.prm.constants.PriceInfoConstants;
import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import com.hitachi.droneroute.prm.entity.PriceHistoryInfoEntity;
import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
import com.hitachi.droneroute.prm.repository.PriceHistoryInfoRepository;
import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
import com.hitachi.droneroute.prm.validator.PriceInfoValidator;
import io.hypersistence.utils.hibernate.type.range.Range;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

/** PriceInfoServiceImplの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
public class PriceInfoServiceImplMockTest {

  @MockBean private PriceInfoRepository priceInfoRepository;

  @MockBean private PriceHistoryInfoRepository priceInfoHistoryRepository;

  @MockBean private PriceInfoValidator validator;

  @Autowired private PriceInfoServiceImpl service;

  @SpyBean private SystemSettings systemSettings;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /** 料金情報登録更新リクエストDTO作成 */
  private PriceInfoRequestDto createPriceInfoRequestDto() {
    PriceInfoRequestDto ret = new PriceInfoRequestDto();
    ret.setProcessingType(1);
    ret.setPriceId(null);
    ret.setResourceId("リソースID");
    ret.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
    ret.setPrimaryRouteOperatorId("主管航路事業者ID");
    ret.setPriceType(4);
    ret.setPricePerUnit(1);
    ret.setPrice(1000);
    ret.setEffectiveStartTime("2025-11-13T10:00:00Z");
    ret.setEffectiveEndTime("2025-11-13T11:00:00Z");
    ret.setOperatorId("ope01");
    ret.setPriority(1);
    ret.setRowNumber(1);

    return ret;
  }

  private List<PriceInfoRequestDto> createPriceInfoListList() {
    List<PriceInfoRequestDto> ret = new ArrayList<>();

    // 登録DTO
    PriceInfoRequestDto dto1 = createPriceInfoRequestDto();
    // 更新DTO
    PriceInfoRequestDto dto2 = createPriceInfoRequestDto();
    dto2.setProcessingType(2);
    dto2.setPriceId(UUID.randomUUID().toString());
    // 削除DTO
    PriceInfoRequestDto dto3 = new PriceInfoRequestDto();
    dto3.setPriceId(UUID.randomUUID().toString());
    dto3.setProcessingType(3);
    ret.add(dto1);
    ret.add(dto2);
    ret.add(dto3);

    return ret;
  }

  /**
   * 料金情報共通処理登録Entity作成
   *
   * @return
   */
  private PriceInfoEntity createPriceInfoEntity() {
    PriceInfoEntity ret = new PriceInfoEntity();
    ret.setPriceId(UUID.randomUUID());
    ret.setResourceId("リソースID");
    ret.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
    ret.setPrimaryRouteOperatorId("主管航路事業者ID1");
    ret.setPriceType(4);
    ret.setPricePerUnit(1);
    ret.setPrice(1000);
    ret.setEffectiveTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime("2024-11-13T10:00:00Z"),
                StringUtils.parseDatetimeStringToLocalDateTime("2024-11-13T11:00:00Z"))));
    ret.setOperatorId("preOpe01");
    ret.setPriority(1);
    ret.setDeleteFlag(false);

    return ret;
  }

  private PriceHistoryInfoEntity createPriceInfoHistoryEntity() {
    PriceHistoryInfoEntity ret = new PriceHistoryInfoEntity();
    ret.setPriceHistoryId(UUID.randomUUID());
    ret.setPriceId(UUID.randomUUID());
    ret.setResourceId("リソースID");
    ret.setResourceType(PriceInfoConstants.RESOURCE_TYPE_PORT);
    ret.setPrimaryRouteOperatorId("主管航路事業者ID1");
    ret.setPriceType(4);
    ret.setPricePerUnit(1);
    ret.setPrice(1000);
    ret.setEffectiveTime(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime("2024-11-13T10:00:00Z"),
                StringUtils.parseDatetimeStringToLocalDateTime("2024-11-13T11:00:00Z"))));
    ret.setOperatorId("preOpe01");
    ret.setPriority(1);
    ret.setDeleteFlag(false);
    return ret;
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の正常系テスト<br>
   * 条件: 正常な登録更新削除対象の料金情報を各１件渡す<br>
   * 結果: 正常終了<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void process_Nomal1() {
    List<PriceInfoRequestDto> priceInfoList = createPriceInfoListList();
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findByPriceIdAndDeleteFlagFalse(any()))
        .thenReturn(java.util.Optional.of(ent1));
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);
    service.process(priceInfoList);

    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(3)).save(entityCaptor.capture());
    List<PriceInfoEntity> capList = entityCaptor.getAllValues();
    PriceInfoEntity capEnt1 = capList.get(0);
    PriceInfoRequestDto dto1 = priceInfoList.get(0);
    assertNotNull(capEnt1.getPriceId());
    assertEquals(dto1.getResourceId(), capEnt1.getResourceId());
    assertEquals(dto1.getResourceType(), capEnt1.getResourceType());
    assertEquals(dto1.getPrimaryRouteOperatorId(), capEnt1.getPrimaryRouteOperatorId());
    assertEquals(dto1.getPriceType(), capEnt1.getPriceType());
    assertEquals(dto1.getPricePerUnit(), capEnt1.getPricePerUnit());
    assertEquals(dto1.getPrice(), capEnt1.getPrice());
    assertEquals(dto1.getPriority(), capEnt1.getPriority());
    assertEquals(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(dto1.getEffectiveStartTime()),
                StringUtils.parseDatetimeStringToLocalDateTime(dto1.getEffectiveEndTime()))),
        capEnt1.getEffectiveTime());

    PriceInfoEntity capEnt2 = capList.get(1);
    PriceInfoRequestDto dto2 = priceInfoList.get(1);
    assertEquals(dto2.getPriceId().toString(), capEnt2.getPriceId().toString());
    assertEquals(dto2.getResourceId(), capEnt2.getResourceId().toString());
    assertEquals(dto2.getResourceType(), capEnt2.getResourceType());
    assertEquals(dto2.getPrimaryRouteOperatorId(), capEnt2.getPrimaryRouteOperatorId());
    assertEquals(dto2.getPriceType(), capEnt2.getPriceType());
    assertEquals(dto2.getPricePerUnit(), capEnt2.getPricePerUnit());
    assertEquals(dto2.getPrice(), capEnt2.getPrice());
    assertEquals(dto2.getPriority(), capEnt2.getPriority());
    assertEquals(
        Range.localDateTimeRange(
            String.format(
                "[%s,%s)",
                StringUtils.parseDatetimeStringToLocalDateTime(dto2.getEffectiveStartTime()),
                StringUtils.parseDatetimeStringToLocalDateTime(dto2.getEffectiveEndTime()))),
        capEnt2.getEffectiveTime());

    PriceInfoEntity capEnt3 = capList.get(2);
    assertEquals(true, capEnt3.getDeleteFlag());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の正常系テスト<br>
   * 条件: 処理種別がnullの料金情報を渡す<br>
   * 結果: 登録更新削除処理を行わずに正常終了<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void process_processingType_null() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    dto.setProcessingType(null);

    service.process(List.of(dto));
    verify(priceInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の正常系テスト<br>
   * 条件: 処理種別が定義外(0)の料金情報を渡す<br>
   * 結果: 登録更新削除処理を行わずに正常終了<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void process_processingType_teigigai() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    dto.setProcessingType(0);

    service.process(List.of(dto));
    verify(priceInfoRepository, times(0)).save(any());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の正常系テスト<br>
   * 条件: 処理種別が登録で料金IDが空の状態で渡す<br>
   * 結果: 正常終了<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void process_update_料金ID空() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    dto.setPriceId("");
    dto.setProcessingType(1);
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findByPriceIdAndDeleteFlagFalse(any()))
        .thenReturn(java.util.Optional.of(ent1));
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);

    service.process(List.of(dto));
    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(1)).save(entityCaptor.capture());
    PriceInfoEntity capEnt = entityCaptor.getValue();
    assertNotNull(capEnt.getPriceId());
    assertEquals(dto.getResourceId(), capEnt.getResourceId());
    assertEquals(dto.getResourceType(), capEnt.getResourceType());
    assertEquals(dto.getPrimaryRouteOperatorId(), capEnt.getPrimaryRouteOperatorId());
    assertEquals(dto.getPriceType(), capEnt.getPriceType());
    assertEquals(dto.getPricePerUnit(), capEnt.getPricePerUnit());
    assertEquals(dto.getPrice(), capEnt.getPrice());
    assertEquals(dto.getPriority(), capEnt.getPriority());
    assertNotNull(capEnt.getEffectiveTime());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の正常系テスト<br>
   * 条件: 処理種別が更新で料金ID以外の項目がnullの状態で渡す<br>
   * 結果: 正常終了<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void process_update_null() {
    PriceInfoRequestDto dto = new PriceInfoRequestDto();
    dto.setPriceId(UUID.randomUUID().toString());
    dto.setProcessingType(2);
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findByPriceIdAndDeleteFlagFalse(any()))
        .thenReturn(java.util.Optional.of(ent1));
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);
    doReturn(null).when(systemSettings).getString(any(), any());

    service.process(List.of(dto));
    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(1)).save(entityCaptor.capture());
    PriceInfoEntity capEnt = entityCaptor.getValue();
    assertEquals(dto.getPriceId().toString(), capEnt.getPriceId().toString());
    assertEquals(ent1.getResourceId(), capEnt.getResourceId());
    assertEquals(ent1.getResourceType(), capEnt.getResourceType());
    assertEquals(ent1.getPrimaryRouteOperatorId(), capEnt.getPrimaryRouteOperatorId());
    assertEquals(ent1.getPriceType(), capEnt.getPriceType());
    assertEquals(ent1.getPricePerUnit(), capEnt.getPricePerUnit());
    assertEquals(ent1.getPrice(), capEnt.getPrice());
    assertEquals(ent1.getPriority(), capEnt.getPriority());
    assertNotNull(capEnt.getEffectiveTime());
    assertEquals(ent1.getEffectiveTime(), capEnt.getEffectiveTime());
    assertEquals(ent1.getEffectiveTime(), capEnt.getEffectiveTime());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の正常系テスト<br>
   * 条件: 処理種別が更新で適用終了日時がnullの状態で渡す<br>
   * 結果: 正常終了<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void process_effectiveEndTime_null() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    dto.setPriceId(UUID.randomUUID().toString());
    dto.setProcessingType(2);
    dto.setEffectiveEndTime(null);
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findByPriceIdAndDeleteFlagFalse(any()))
        .thenReturn(java.util.Optional.of(ent1));
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);

    service.process(List.of(dto));
    ArgumentCaptor<PriceInfoEntity> entityCaptor = ArgumentCaptor.forClass(PriceInfoEntity.class);
    verify(priceInfoRepository, times(1)).save(entityCaptor.capture());
    PriceInfoEntity capEnt = entityCaptor.getValue();
    assertNotNull(capEnt.getPriceId());
    assertEquals(dto.getResourceId(), capEnt.getResourceId());
    assertEquals(dto.getResourceType(), capEnt.getResourceType());
    assertEquals(dto.getPrimaryRouteOperatorId(), capEnt.getPrimaryRouteOperatorId());
    assertEquals(dto.getPriceType(), capEnt.getPriceType());
    assertEquals(dto.getPricePerUnit(), capEnt.getPricePerUnit());
    assertEquals(dto.getPrice(), capEnt.getPrice());
    assertEquals(dto.getPriority(), capEnt.getPriority());
    assertNotNull(capEnt.getEffectiveTime());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の異常系テスト<br>
   * 条件: DB登録時に料金IDの生成に失敗<br>
   * 結果: ServiceErrorExceptionが発生<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void process_err_登録失敗() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    dto.setPriceId(UUID.randomUUID().toString());
    dto.setProcessingType(1);
    PriceInfoEntity ent1 = createPriceInfoEntity();
    ent1.setPriceId(null);
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);

    ServiceErrorException exception =
        assertThrows(ServiceErrorException.class, () -> service.process(List.of(dto)));
    assertEquals("料金IDの生成に失敗しました。", exception.getMessage());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の異常系テスト<br>
   * 条件: 料金履歴情報保存時に料金履歴IDの生成に失敗<br>
   * 結果: ServiceErrorExceptionが発生<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void process_err_料金履歴情報保存失敗() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    dto.setPriceId(UUID.randomUUID().toString());
    dto.setProcessingType(1);
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    histEnt.setPriceHistoryId(null);
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);

    ServiceErrorException exception =
        assertThrows(ServiceErrorException.class, () -> service.process(List.of(dto)));
    assertEquals("料金履歴IDの生成に失敗しました。", exception.getMessage());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の異常系テスト<br>
   * 条件: DB更新時に料金IDが一致しない<br>
   * 結果: ServiceErrorExceptionが発生<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void process_err_更新失敗() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    dto.setPriceId(UUID.randomUUID().toString());
    dto.setProcessingType(2);
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(new PriceInfoEntity());
    when(priceInfoRepository.findByPriceIdAndDeleteFlagFalse(any()))
        .thenReturn(java.util.Optional.of(ent1));
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);

    ServiceErrorException exception =
        assertThrows(ServiceErrorException.class, () -> service.process(List.of(dto)));
    assertEquals("料金情報の更新に失敗しました。", exception.getMessage());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の異常系テスト<br>
   * 条件: 更新対象の料金IDが存在しない<br>
   * 結果: NotFoundExceptionが発生<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void process_err_更新対象なし() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    dto.setPriceId(UUID.randomUUID().toString());
    dto.setProcessingType(2);
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findByPriceIdAndDeleteFlagFalse(any()))
        .thenReturn(java.util.Optional.empty());
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of());
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.process(List.of(dto)));
    assertEquals(
        MessageFormat.format(
            "{0}番目の料金IDが見つかりません。料金ID:{1}", String.valueOf(dto.getRowNumber()), dto.getPriceId()),
        exception.getMessage());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の異常系テスト<br>
   * 条件: 削除対象の料金IDが存在しない<br>
   * 結果: NotFoundExceptionが発生<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void process_err_削除対象なし() {
    PriceInfoRequestDto dto = new PriceInfoRequestDto();
    dto.setPriceId(UUID.randomUUID().toString());
    dto.setProcessingType(3);
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findByPriceIdAndDeleteFlagFalse(any()))
        .thenReturn(java.util.Optional.empty());
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.process(List.of(dto)));
    assertEquals(
        MessageFormat.format(
            "{0}番目の料金IDが見つかりません。料金ID:{1}", String.valueOf(dto.getRowNumber()), dto.getPriceId()),
        exception.getMessage());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の異常系テスト<br>
   * 条件: 登録時に期間重複データが存在<br>
   * 結果: ServiceErrorExceptionが発生<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void process_err_登録時機関重複() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findByPriceIdAndDeleteFlagFalse(any()))
        .thenReturn(java.util.Optional.of(ent1));
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(ent1));
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);

    ServiceErrorException exception =
        assertThrows(ServiceErrorException.class, () -> service.process(List.of(dto)));
    assertEquals(
        MessageFormat.format(
            "{0}番目の料金情報の期間に重複があります。料金タイプ:{1}、料金単位：{2}、料金：{3}",
            String.valueOf(dto.getRowNumber()),
            dto.getPriceType(),
            dto.getPricePerUnit(),
            dto.getPrice()),
        exception.getMessage());
  }

  /**
   * メソッド名: process<br>
   * 試験名: リソース料金情報処理の異常系テスト<br>
   * 条件: 更新時に期間重複データが存在<br>
   * 結果: ServiceErrorExceptionが発生<br>
   * テストパターン: 異常系<br>
   */
  @Test
  void process_err_更新時期間重複() {
    PriceInfoRequestDto dto = createPriceInfoRequestDto();
    dto.setPriceId(UUID.randomUUID().toString());
    dto.setProcessingType(2);
    PriceInfoEntity ent1 = createPriceInfoEntity();
    PriceHistoryInfoEntity histEnt = createPriceInfoHistoryEntity();
    when(priceInfoRepository.save(any())).thenReturn(ent1);
    when(priceInfoRepository.findByPriceIdAndDeleteFlagFalse(any()))
        .thenReturn(java.util.Optional.of(ent1));
    when(priceInfoRepository.findAll(any(Specification.class))).thenReturn(List.of(ent1));
    when(priceInfoHistoryRepository.save(any())).thenReturn(histEnt);

    ServiceErrorException exception =
        assertThrows(ServiceErrorException.class, () -> service.process(List.of(dto)));
    assertEquals(
        MessageFormat.format(
            "{0}番目の料金情報の期間に重複があります。料金タイプ:{1}、料金単位：{2}、料金：{3}",
            String.valueOf(dto.getRowNumber()),
            dto.getPriceType(),
            dto.getPricePerUnit(),
            dto.getPrice()),
        exception.getMessage());
  }
}
