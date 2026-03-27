// DB接続を行う想定のテストのため、一括テストの対象外とするためにコメントアウト

// package com.hitachi.droneroute.prm.service.impl;
//
// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.doReturn;
// import static org.mockito.Mockito.doThrow;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.reset;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Tag;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInfo;
// import org.mockito.ArgumentCaptor;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.BeanUtils;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.boot.test.mock.mockito.SpyBean;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.hitachi.droneroute.cmn.exception.NotFoundException;
// import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
// import com.hitachi.droneroute.cmn.exception.ValidationErrorException;
// import com.hitachi.droneroute.cmn.settings.SystemSettings;
// import com.hitachi.droneroute.cmn.util.StringUtils;
// import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
// import com.hitachi.droneroute.prm.entity.PriceHistoryInfoEntity;
// import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
// import com.hitachi.droneroute.prm.repository.PriceHistoryInfoRepository;
// import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
// import com.hitachi.droneroute.prm.validator.PriceInfoValidator;
//
// import io.hypersistence.utils.hibernate.type.range.Range;
//
//
/// ** PriceInfoServiceImplの単体テスト */
// @SpringBootTest
// @Transactional
// @ActiveProfiles("test-db")
// public class PriceInfoServiceImplTest {
//
//   @SpyBean
//   private PriceInfoRepository priceInfoRepository;
//
//   @MockBean
//   private PriceHistoryInfoRepository priceInfoHistoryRepository;
//
//   @MockBean
//   private PriceInfoValidator validator;
//
//   @SpyBean
//   private PriceInfoServiceImpl service;
//
//   @SpyBean
//   private SystemSettings systemSettings;
//
//   @BeforeEach
//   public void setUp(TestInfo testInfo) {
//   MockitoAnnotations.openMocks(this);
//
//   // 事前登録
//   // 料金情報の登録データがリソースIDと一致、優先度は不一致のデータ(期間重複なし)
//   PriceInfoEntity entity = createPriceInfoEntity();
//   entity.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T09:00:00Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T09:59:59Z"))));
//   entity.setPriority(2);
//   priceInfoRepository.save(entity);
//
//   entity = createPriceInfoEntity();
//   entity.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T11:00:01Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T12:00:00Z"))));
//   entity.setPriority(2);
//   priceInfoRepository.save(entity);
//
//   // 料金情報の登録データがリソースIDと一致、優先度は不一致のデータ(期間重複あり)
//   entity = createPriceInfoEntity();
//   entity.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T10:00:00Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T11:00:00Z"))));
//   entity.setPriority(2);
//   priceInfoRepository.save(entity);
//
//   // 料金情報の登録データがリソースID・優先度と一致するデータ(期間重複なし)
//   entity = createPriceInfoEntity();
//   entity.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T09:00:00Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T09:59:59Z"))));
//   entity.setPriority(1);
//   priceInfoRepository.save(entity);
//
//   entity = createPriceInfoEntity();
//   entity.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T11:00:01Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T12:00:00Z"))));
//   entity.setPriority(1);
//   priceInfoRepository.save(entity);
//
//   // SkipSetupタグが付いているテストは更新データ投入をスキップ
//   if (testInfo.getTags().contains("SkipUpdateSetup")) {
//   reset(priceInfoRepository);
//   return;
//   }
//   // 更新対象データ
//   entity = createPriceInfoEntity();
//   entity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//   priceInfoRepository.save(entity);
//   reset(priceInfoRepository);
//   }
//
//   /**
//    * メソッド名: process<br>
//    * 試験名: 料金情報が0件の場合の処理<br>
//    * 条件: 料金情報が0件<br>
//    * 結果: 正常終了し処理がスキップされる<br>
//    * テストパターン: 正常系<br>
//    */
//   @Test
//   public void process_正常() {
//   // Arrange
//   List<PriceInfoRequestDto> req = new ArrayList<>();
//
//   // Act
//   assertDoesNotThrow(() -> service.process(req));
//
//   // Assert
//   verify(service, never()).registerData(any());
//   verify(service, never()).updateData(any());
//   verify(service, never()).deleteData(any());
//
//   }
//
//   /**
//    * メソッド名: process<br>
//    * 試験名: 1件の料金情報登録処理<br>
//    * 条件: 料金情報が1件、処理種別1:登録<br>
//    * 結果: registerDataが1回呼ばれる<br>
//    * テストパターン: 正常系<br>
//    */
//   @Test
//   public void process_正常1件登録() {
//   // Arrange
//   List<PriceInfoRequestDto> req = new ArrayList<>();
//
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   req.add(reqDto);
//
//   doNothing().when(service).registerData(any(PriceInfoRequestDto.class));
//
//   // Act
//   assertDoesNotThrow(() -> service.process(req));
//
//   // Assert
//   ArgumentCaptor<PriceInfoRequestDto> cap = ArgumentCaptor.forClass(PriceInfoRequestDto.class);
//   verify(service, times(1)).registerData(cap.capture());
//
//   PriceInfoRequestDto retDto = createPriceInfoRequestDto();
//
//   PriceInfoRequestDto capturedDto = cap.getValue();
//   assertEquals(retDto.getProcessingType(), capturedDto.getProcessingType());
//   assertEquals(retDto.getPriceId(), capturedDto.getPriceId());
//   assertEquals(retDto.getResourceId(), capturedDto.getResourceId());
//   assertEquals(retDto.getResourceType(), capturedDto.getResourceType());
//   assertEquals(retDto.getPriceType(), capturedDto.getPriceType());
//   assertEquals(retDto.getPricePerUnit(), capturedDto.getPricePerUnit());
//   assertEquals(retDto.getPrice(), capturedDto.getPrice());
//   assertEquals(retDto.getEffectiveStartTime(), capturedDto.getEffectiveStartTime());
//   assertEquals(retDto.getEffectiveEndTime(), capturedDto.getEffectiveEndTime());
//   assertEquals(retDto.getOperatorId(), capturedDto.getOperatorId());
//   assertEquals(retDto.getPriority(), capturedDto.getPriority());
//   assertEquals(1, capturedDto.getRowNumber());
//
//   verify(service, never()).updateData(any());
//   verify(service, never()).deleteData(any());
//
//   }
//
//   /**
//    * メソッド名: process<br>
//    * 試験名: 1件の料金情報更新処理<br>
//    * 条件: 料金情報が1件、処理種別2:更新<br>
//    * 結果: updateDataが1回呼ばれる<br>
//    * テストパターン: 正常系<br>
//    */
//   @Test
//   public void process_正常1件更新() {
//   // Arrange
//   List<PriceInfoRequestDto> req = new ArrayList<>();
//
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   reqDto.setProcessingType(2);
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//   req.add(reqDto);
//
//   doNothing().when(service).updateData(any(PriceInfoRequestDto.class));
//
//   // Act
//   assertDoesNotThrow(() -> service.process(req));
//
//   // Assert
//   ArgumentCaptor<PriceInfoRequestDto> cap = ArgumentCaptor.forClass(PriceInfoRequestDto.class);
//   verify(service, times(1)).updateData(cap.capture());
//
//   PriceInfoRequestDto retDto = createPriceInfoRequestDto();
//   retDto.setProcessingType(2);
//   retDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//
//   PriceInfoRequestDto capturedDto = cap.getValue();
//   assertEquals(retDto.getProcessingType(), capturedDto.getProcessingType());
//   assertEquals(retDto.getPriceId(), capturedDto.getPriceId());
//   assertEquals(retDto.getResourceId(), capturedDto.getResourceId());
//   assertEquals(retDto.getResourceType(), capturedDto.getResourceType());
//   assertEquals(retDto.getPriceType(), capturedDto.getPriceType());
//   assertEquals(retDto.getPricePerUnit(), capturedDto.getPricePerUnit());
//   assertEquals(retDto.getPrice(), capturedDto.getPrice());
//   assertEquals(retDto.getEffectiveStartTime(), capturedDto.getEffectiveStartTime());
//   assertEquals(retDto.getEffectiveEndTime(), capturedDto.getEffectiveEndTime());
//   assertEquals(retDto.getOperatorId(), capturedDto.getOperatorId());
//   assertEquals(retDto.getPriority(), capturedDto.getPriority());
//   assertEquals(1, capturedDto.getRowNumber());
//
//   verify(service, never()).registerData(any());
//   verify(service, never()).deleteData(any());
//   }
//
//   /**
//    * メソッド名: process<br>
//    * 試験名: 1件の料金情報削除処理<br>
//    * 条件: 料金情報が1件、処理種別3:削除<br>
//    * 結果: deleteDataが1回呼ばれる<br>
//    * テストパターン: 正常系<br>
//    */
//   @Test
//   public void process_正常1件削除() {
//   // Arrange
//   List<PriceInfoRequestDto> req = new ArrayList<>();
//
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   reqDto.setProcessingType(3);
//   reqDto.setPriceId("6b5ec052-a76f-87cb-ef4a-a31c62a13276");
//   req.add(reqDto);
//
//   doNothing().when(service).deleteData(any(PriceInfoRequestDto.class));
//
//   // Act
//   assertDoesNotThrow(() -> service.process(req));
//
//   // Assert
//   ArgumentCaptor<PriceInfoRequestDto> cap = ArgumentCaptor.forClass(PriceInfoRequestDto.class);
//   verify(service, times(1)).deleteData(cap.capture());
//
//   PriceInfoRequestDto retDto = createPriceInfoRequestDto();
//   retDto.setProcessingType(3);
//   retDto.setPriceId("6b5ec052-a76f-87cb-ef4a-a31c62a13276");
//
//   PriceInfoRequestDto capturedDto = cap.getValue();
//   assertEquals(retDto.getProcessingType(), capturedDto.getProcessingType());
//   assertEquals(retDto.getPriceId(), capturedDto.getPriceId());
//   assertEquals(retDto.getResourceId(), capturedDto.getResourceId());
//   assertEquals(retDto.getResourceType(), capturedDto.getResourceType());
//   assertEquals(retDto.getPriceType(), capturedDto.getPriceType());
//   assertEquals(retDto.getPricePerUnit(), capturedDto.getPricePerUnit());
//   assertEquals(retDto.getPrice(), capturedDto.getPrice());
//   assertEquals(retDto.getEffectiveStartTime(), capturedDto.getEffectiveStartTime());
//   assertEquals(retDto.getEffectiveEndTime(), capturedDto.getEffectiveEndTime());
//   assertEquals(retDto.getOperatorId(), capturedDto.getOperatorId());
//   assertEquals(retDto.getPriority(), capturedDto.getPriority());
//   assertEquals(1, capturedDto.getRowNumber());
//
//   verify(service, never()).registerData(any());
//   verify(service, never()).updateData(any());
//   }
//
//   /**
//    * メソッド名: process<br>
//    * 試験名: 3件の料金情報を処理種別混在で処理<br>
//    * 条件: 料金情報が3件、処理種別混在<br>
//    * 結果: 各処理メソッドが適切に呼ばれる<br>
//    * テストパターン: 正常系<br>
//    */
//   @Test
//   public void process_正常3件_処理種別混在() {
//   // Arrange
//   List<PriceInfoRequestDto> req = new ArrayList<>();
//
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   req.add(reqDto);
//
//   reqDto = createPriceInfoRequestDto();
//   reqDto.setProcessingType(2);
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//   req.add(reqDto);
//
//   reqDto = createPriceInfoRequestDto();
//   reqDto.setProcessingType(3);
//   reqDto.setPriceId("6b5ec052-a76f-87cb-ef4a-a31c62a13276");
//   req.add(reqDto);
//
//   doNothing().when(service).registerData(any(PriceInfoRequestDto.class));
//   doNothing().when(service).updateData(any(PriceInfoRequestDto.class));
//   doNothing().when(service).deleteData(any(PriceInfoRequestDto.class));
//
//   // Act
//   assertDoesNotThrow(() -> service.process(req));
//
//   // Assert
//   // 登録
//   ArgumentCaptor<PriceInfoRequestDto> cap = ArgumentCaptor.forClass(PriceInfoRequestDto.class);
//   verify(service, times(1)).registerData(cap.capture());
//
//   PriceInfoRequestDto retDto = createPriceInfoRequestDto();
//
//   PriceInfoRequestDto capturedDto = cap.getValue();
//   assertEquals(retDto.getProcessingType(), capturedDto.getProcessingType());
//   assertEquals(retDto.getPriceId(), capturedDto.getPriceId());
//   assertEquals(retDto.getResourceId(), capturedDto.getResourceId());
//   assertEquals(retDto.getResourceType(), capturedDto.getResourceType());
//   assertEquals(retDto.getPriceType(), capturedDto.getPriceType());
//   assertEquals(retDto.getPricePerUnit(), capturedDto.getPricePerUnit());
//   assertEquals(retDto.getPrice(), capturedDto.getPrice());
//   assertEquals(retDto.getEffectiveStartTime(), capturedDto.getEffectiveStartTime());
//   assertEquals(retDto.getEffectiveEndTime(), capturedDto.getEffectiveEndTime());
//   assertEquals(retDto.getOperatorId(), capturedDto.getOperatorId());
//   assertEquals(retDto.getPriority(), capturedDto.getPriority());
//   assertEquals(1, capturedDto.getRowNumber());
//
//   // 更新
//   cap = ArgumentCaptor.forClass(PriceInfoRequestDto.class);
//   verify(service, times(1)).updateData(cap.capture());
//
//   retDto = createPriceInfoRequestDto();
//   retDto.setProcessingType(2);
//   retDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//
//   capturedDto = cap.getValue();
//   assertEquals(retDto.getProcessingType(), capturedDto.getProcessingType());
//   assertEquals(retDto.getPriceId(), capturedDto.getPriceId());
//   assertEquals(retDto.getResourceId(), capturedDto.getResourceId());
//   assertEquals(retDto.getResourceType(), capturedDto.getResourceType());
//   assertEquals(retDto.getPriceType(), capturedDto.getPriceType());
//   assertEquals(retDto.getPricePerUnit(), capturedDto.getPricePerUnit());
//   assertEquals(retDto.getPrice(), capturedDto.getPrice());
//   assertEquals(retDto.getEffectiveStartTime(), capturedDto.getEffectiveStartTime());
//   assertEquals(retDto.getEffectiveEndTime(), capturedDto.getEffectiveEndTime());
//   assertEquals(retDto.getOperatorId(), capturedDto.getOperatorId());
//   assertEquals(retDto.getPriority(), capturedDto.getPriority());
//   assertEquals(2, capturedDto.getRowNumber());
//
//   // 削除
//   cap = ArgumentCaptor.forClass(PriceInfoRequestDto.class);
//   verify(service, times(1)).deleteData(cap.capture());
//
//   retDto = createPriceInfoRequestDto();
//   retDto.setProcessingType(3);
//   retDto.setPriceId("6b5ec052-a76f-87cb-ef4a-a31c62a13276");
//
//   capturedDto = cap.getValue();
//   assertEquals(retDto.getProcessingType(), capturedDto.getProcessingType());
//   assertEquals(retDto.getPriceId(), capturedDto.getPriceId());
//   assertEquals(retDto.getResourceId(), capturedDto.getResourceId());
//   assertEquals(retDto.getResourceType(), capturedDto.getResourceType());
//   assertEquals(retDto.getPriceType(), capturedDto.getPriceType());
//   assertEquals(retDto.getPricePerUnit(), capturedDto.getPricePerUnit());
//   assertEquals(retDto.getPrice(), capturedDto.getPrice());
//   assertEquals(retDto.getEffectiveStartTime(), capturedDto.getEffectiveStartTime());
//   assertEquals(retDto.getEffectiveEndTime(), capturedDto.getEffectiveEndTime());
//   assertEquals(retDto.getOperatorId(), capturedDto.getOperatorId());
//   assertEquals(retDto.getPriority(), capturedDto.getPriority());
//   assertEquals(3, capturedDto.getRowNumber());
//   }
//
//   /**
//    * メソッド名: process<br>
//    * 試験名: ループ中のエラー処理<br>
//    * 条件: ループ中にエラー発生<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void process_異常系_ループ中にエラー() {
//   // Arrange
//   List<PriceInfoRequestDto> req = new ArrayList<>();
//
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   req.add(reqDto);
//
//   reqDto = createPriceInfoRequestDto();
//   reqDto.setProcessingType(1);
//   reqDto.setPriceId("6b5ec052-a76f-87cb-ef4a-a31c62a13276");
//   req.add(reqDto);
//
//   reqDto = createPriceInfoRequestDto();
//   reqDto.setProcessingType(2);
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//   req.add(reqDto);
//
//   doThrow(new ServiceErrorException("1番目でエラー")).when(service).registerData(any());
//  // doNothing().when(service).registerData(any(PriceInfoRequestDto.class));
//   doNothing().when(service).updateData(any(PriceInfoRequestDto.class));
//   doNothing().when(service).deleteData(any(PriceInfoRequestDto.class));
//
//   // Act
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.process(req));
//   assertEquals("1番目でエラー", ex.getMessage());
//
//   // Assert
//   verify(service, times(1)).registerData(any());
//   verify(service, never()).updateData(any());
//   verify(service, never()).deleteData(any());
//   }
//
//   /**
//    * メソッド名: process<br>
//    * 試験名: バリデーションエラー処理<br>
//    * 条件: バリデーションチェックエラー<br>
//    * 結果: ValidationErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   @Tag("SkipUpdateSetup")
//   public void process_異常_バリデーションチェックエラー() {
//
//   // Arrange
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   List<PriceInfoRequestDto> reqDtoList = new ArrayList<>();
//   reqDtoList.add(reqDto);
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doThrow(new
//   ValidationErrorException("ValidationErrorMessage")).when(validator).validateAll(reqDtoList);
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   Exception ex = assertThrows(ValidationErrorException .class,() -> service.process(reqDtoList));
//   assertEquals("ValidationErrorMessage", ex.getMessage());
//
//   // Assert
//   verify(validator, times(1)).validateAll(any());
//   verify(priceInfoRepository, times(0)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//   }
//
//   /**
//    * メソッド名: registerData<br>
//    * 試験名: 料金情報の正常登録<br>
//    * 条件: 適用期間チェック正常<br>
//    * 結果: 料金情報と履歴情報が保存される<br>
//    * テストパターン: 正常系<br>
//    */
//   @Test
//   @Tag("SkipUpdateSetup")
//   public void registerData_正常() {
//   // Arrange
//   // setupで適用期間ケースのデータ登録済み
//
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setOperatorId("ope01");
//   retEntity.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T10:00:00Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T11:00:00Z"))));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   assertDoesNotThrow(() -> service.registerData(reqDto));
//
//   // Assert
//   ArgumentCaptor<PriceInfoEntity> cap = ArgumentCaptor.forClass(PriceInfoEntity.class);
//   verify(priceInfoRepository, times(1)).save(cap.capture());
//
//   Range<LocalDateTime> effectiveTime = Range.closedOpen(
//   java.time.OffsetDateTime.parse(reqDto.getEffectiveStartTime()).toLocalDateTime(),
//   java.time.OffsetDateTime.parse(reqDto.getEffectiveEndTime()).toLocalDateTime());
//   //料金情報
//   PriceInfoEntity capturedEntity = cap.getValue();
//   assertNotNull(capturedEntity.getPriceId());
//   assertEquals(retEntity.getResourceId(), capturedEntity.getResourceId());
//   assertEquals(retEntity.getResourceType(), capturedEntity.getResourceType());
//   assertEquals(retEntity.getPriceType(), capturedEntity.getPriceType());
//   assertEquals(retEntity.getPricePerUnit(), capturedEntity.getPricePerUnit());
//   assertEquals(retEntity.getPrice(), capturedEntity.getPrice());
//   assertEquals(effectiveTime, capturedEntity.getEffectiveTime());
//   assertEquals(retEntity.getPriority(), capturedEntity.getPriority());
//   assertEquals(retEntity.getOperatorId(), capturedEntity.getOperatorId());
//   assertNotNull(capturedEntity.getCreateTime());
//   assertNotNull(capturedEntity.getUpdateTime());
//   assertEquals(false, capturedEntity.getDeleteFlag());
//
//   //履歴
//   ArgumentCaptor<PriceHistoryInfoEntity> capHistory =
//   ArgumentCaptor.forClass(PriceHistoryInfoEntity.class);
//   verify(priceInfoHistoryRepository, times(1)).save(capHistory.capture());
//   PriceHistoryInfoEntity capturedHistoryEntity = capHistory.getValue();
//   assertNotNull(capturedHistoryEntity.getPriceHistoryId());
//   assertEquals(capturedEntity.getPriceId(), capturedHistoryEntity.getPriceId());
//   assertEquals(capturedEntity.getResourceId(), capturedHistoryEntity.getResourceId());
//   assertEquals(capturedEntity.getResourceType(), capturedHistoryEntity.getResourceType());
//   assertEquals(capturedEntity.getPriceType(), capturedHistoryEntity.getPriceType());
//   assertEquals(capturedEntity.getPricePerUnit(), capturedHistoryEntity.getPricePerUnit());
//   assertEquals(capturedEntity.getPrice(), capturedHistoryEntity.getPrice());
//   assertEquals(capturedEntity.getEffectiveTime(), capturedHistoryEntity.getEffectiveTime());
//   assertEquals(capturedEntity.getPriority(), capturedHistoryEntity.getPriority());
//   assertEquals(capturedEntity.getOperatorId(), capturedHistoryEntity.getOperatorId());
//   assertNotNull(capturedEntity.getCreateTime());
//   assertNotNull(capturedEntity.getUpdateTime());
//   assertEquals(capturedEntity.getDeleteFlag(), capturedHistoryEntity.getDeleteFlag());
//   }
//
//   /**
//    * メソッド名: registerData<br>
//    * 試験名: 適用期間重複エラー処理<br>
//    * 条件: 適用期間重複あり<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   @Tag("SkipUpdateSetup")
//   public void registerData_異常_適用期間重複チェック() {
//   // Arrange
//
//   // 料金情報の登録データがリソースID・優先度と一致するデータ(期間重複あり)
//   PriceInfoEntity entity = createPriceInfoEntity();
//   entity = createPriceInfoEntity();
//   entity.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T10:00:00Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T11:00:00Z"))));
//   entity.setPriority(1);
//   priceInfoRepository.save(entity);
//
//   reset(priceInfoRepository);
//
//   // リクエストデータ
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T10:00:00Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T11:00:00Z"))));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.registerData(reqDto));
//   assertEquals("1番目の料金情報の期間に重複があります。料金タイプ:4、料金単位：1、料金：1,000", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(0)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//
//   }
//
//   /**
//    * メソッド名: registerData<br>
//    * 試験名: 料金情報登録結果不正のエラー処理<br>
//    * 条件: 料金情報の登録結果で料金IDがnull<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   @Tag("SkipUpdateSetup")
//   public void registerData_異常_料金情報の登録結果不正() {
//
//   // Arrange
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(null);
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.registerData(reqDto));
//   assertEquals("料金IDの生成に失敗しました。", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//
//   }
//
//   /**
//    * メソッド名: registerData<br>
//    * 試験名: 料金履歴情報登録結果不正のエラー処理<br>
//    * 条件: 料金履歴情報の登録結果で料金履歴IDがnull<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   @Tag("SkipUpdateSetup")
//   public void registerData_異常_料金履歴情報の登録結果不正() {
//
//   // Arrange
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(null);
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.registerData(reqDto));
//   assertEquals("料金履歴IDの生成に失敗しました。", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(1)).save(any());
//
//   }
//
//
//   /**
//    * メソッド名: registerData<br>
//    * 試験名: 料金情報登録時の想定外エラー処理<br>
//    * 条件: 料金情報登録時に想定外エラー発生<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   @Tag("SkipUpdateSetup")
//   public void registerData_異常_登録時想定外エラー() {
//
//   // Arrange
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//
//   doThrow(new ServiceErrorException("想定外のエラー")).when(priceInfoRepository).save(any());
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.registerData(reqDto));
//   assertEquals("想定外のエラー", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//
//   }
//
//   /**
//    * メソッド名: registerData<br>
//    * 試験名: 料金履歴情報登録時の想定外エラー処理<br>
//    * 条件: 料金履歴情報登録時に想定外エラー発生<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   @Tag("SkipUpdateSetup")
//   public void registerData_異常_履歴登録時想定外エラー() {
//
//   // Arrange
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//   doThrow(new ServiceErrorException("想定外のエラー")).when(priceInfoHistoryRepository).save(any());
//
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.registerData(reqDto));
//   assertEquals("想定外のエラー", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(1)).save(any());
//
//   }
//
//   /**
//    * メソッド名: updateData<br>
//    * 試験名: 料金情報の正常更新<br>
//    * 条件: 存在チェック正常、期間チェック正常<br>
//    * 結果: 料金情報と履歴情報が更新される<br>
//    * テストパターン: 正常系<br>
//    */
//   @Test
//   public void updateData_正常1件更新() {
//   // Arrange
//   // setupで更新対象のデータ、適用期間ケースが登録済み
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//   retEntity.setOperatorId("ope01");
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   assertDoesNotThrow(() -> service.updateData(reqDto));
//
//   // Assert
//   ArgumentCaptor<PriceInfoEntity> cap = ArgumentCaptor.forClass(PriceInfoEntity.class);
//   verify(priceInfoRepository, times(1)).save(cap.capture());
//
//   Range<LocalDateTime> effectiveTime = Range.closedOpen(
//   java.time.OffsetDateTime.parse(reqDto.getEffectiveStartTime()).toLocalDateTime(),
//   java.time.OffsetDateTime.parse(reqDto.getEffectiveEndTime()).toLocalDateTime());
//   //料金情報
//   PriceInfoEntity capturedEntity = cap.getValue();
//   assertNotNull(capturedEntity.getPriceId());
//   assertEquals(retEntity.getResourceId(), capturedEntity.getResourceId());
//   assertEquals(retEntity.getResourceType(), capturedEntity.getResourceType());
//   assertEquals(retEntity.getPriceType(), capturedEntity.getPriceType());
//   assertEquals(retEntity.getPricePerUnit(), capturedEntity.getPricePerUnit());
//   assertEquals(retEntity.getPrice(), capturedEntity.getPrice());
//   assertEquals(effectiveTime, capturedEntity.getEffectiveTime());
//   assertEquals(retEntity.getPriority(), capturedEntity.getPriority());
//   assertEquals(retEntity.getOperatorId(), capturedEntity.getOperatorId());
//   assertNotNull(capturedEntity.getUpdateTime());
//   assertEquals(false, capturedEntity.getDeleteFlag());
//
//   //履歴
//   ArgumentCaptor<PriceHistoryInfoEntity> capHistory =
//   ArgumentCaptor.forClass(PriceHistoryInfoEntity.class);
//   verify(priceInfoHistoryRepository, times(1)).save(capHistory.capture());
//   PriceHistoryInfoEntity capturedHistoryEntity = capHistory.getValue();
//   assertNotNull(capturedHistoryEntity.getPriceHistoryId());
//   assertEquals(capturedEntity.getPriceId(), capturedHistoryEntity.getPriceId());
//   assertEquals(capturedEntity.getResourceId(), capturedHistoryEntity.getResourceId());
//   assertEquals(capturedEntity.getResourceType(), capturedHistoryEntity.getResourceType());
//   assertEquals(capturedEntity.getPriceType(), capturedHistoryEntity.getPriceType());
//   assertEquals(capturedEntity.getPricePerUnit(), capturedHistoryEntity.getPricePerUnit());
//   assertEquals(capturedEntity.getPrice(), capturedHistoryEntity.getPrice());
//   assertEquals(capturedEntity.getEffectiveTime(), capturedHistoryEntity.getEffectiveTime());
//   assertEquals(capturedEntity.getPriority(), capturedHistoryEntity.getPriority());
//   assertEquals(capturedEntity.getOperatorId(), capturedHistoryEntity.getOperatorId());
//   assertNotNull(capturedEntity.getUpdateTime());
//   assertEquals(capturedEntity.getDeleteFlag(), capturedHistoryEntity.getDeleteFlag());
//   }
//
//   /**
//    * メソッド名: updateData<br>
//    * 試験名: 更新項目なしの場合の更新処理<br>
//    * 条件: 更新項目なし<br>
//    * 結果: 料金情報と履歴情報が保存される<br>
//    * テストパターン: 正常系<br>
//    */
//   @Test
//   public void updateData_正常1件更新_更新項目なし() {
//   // Arrange
//   // setupで更新対象のデータ、適用期間ケースが登録済み
//   PriceInfoRequestDto reqDto = new PriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//   reqDto.setOperatorId("ope01");
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setOperatorId("ope01");
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   assertDoesNotThrow(() -> service.updateData(reqDto));
//
//   // Assert
//   ArgumentCaptor<PriceInfoEntity> cap = ArgumentCaptor.forClass(PriceInfoEntity.class);
//   verify(priceInfoRepository, times(1)).save(cap.capture());
//
//   //料金情報
//   PriceInfoEntity capturedEntity = cap.getValue();
//   assertNotNull(capturedEntity.getPriceId());
//   assertEquals(retEntity.getResourceId(), capturedEntity.getResourceId());
//   assertEquals(retEntity.getResourceType(), capturedEntity.getResourceType());
//   assertEquals(retEntity.getPriceType(), capturedEntity.getPriceType());
//   assertEquals(retEntity.getPricePerUnit(), capturedEntity.getPricePerUnit());
//   assertEquals(retEntity.getPrice(), capturedEntity.getPrice());
//   assertEquals(retEntity.getEffectiveTime(), capturedEntity.getEffectiveTime());
//   assertEquals(retEntity.getPriority(), capturedEntity.getPriority());
//   assertEquals(retEntity.getOperatorId(), capturedEntity.getOperatorId());
//   assertNotNull(capturedEntity.getUpdateTime());
//   assertEquals(false, capturedEntity.getDeleteFlag());
//
//   //履歴
//   ArgumentCaptor<PriceHistoryInfoEntity> capHistory =
//   ArgumentCaptor.forClass(PriceHistoryInfoEntity.class);
//   verify(priceInfoHistoryRepository, times(1)).save(capHistory.capture());
//   PriceHistoryInfoEntity capturedHistoryEntity = capHistory.getValue();
//   assertNotNull(capturedHistoryEntity.getPriceHistoryId());
//   assertEquals(capturedEntity.getPriceId(), capturedHistoryEntity.getPriceId());
//   assertEquals(capturedEntity.getResourceId(), capturedHistoryEntity.getResourceId());
//   assertEquals(capturedEntity.getResourceType(), capturedHistoryEntity.getResourceType());
//   assertEquals(capturedEntity.getPriceType(), capturedHistoryEntity.getPriceType());
//   assertEquals(capturedEntity.getPricePerUnit(), capturedHistoryEntity.getPricePerUnit());
//   assertEquals(capturedEntity.getPrice(), capturedHistoryEntity.getPrice());
//   assertEquals(capturedEntity.getEffectiveTime(), capturedHistoryEntity.getEffectiveTime());
//   assertEquals(capturedEntity.getPriority(), capturedHistoryEntity.getPriority());
//   assertEquals(capturedEntity.getOperatorId(), capturedHistoryEntity.getOperatorId());
//   assertNotNull(capturedEntity.getUpdateTime());
//   assertEquals(capturedEntity.getDeleteFlag(), capturedHistoryEntity.getDeleteFlag());
//   }
//
//   /**
//    * メソッド名: updateData<br>
//    * 試験名: 存在チェックエラー処理<br>
//    * 条件: 料金ID一致で削除フラグtrue<br>
//    * 結果: NotFoundExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void updateData_異常_存在チェック() {
//   // Arrange
//   // 事前登録データ
//   // 料金ID一致、削除フラグtrue
//   PriceInfoEntity entity = createPriceInfoEntity();
//   entity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//   entity.setDeleteFlag(true);
//   priceInfoRepository.save(entity);
//
//   // 料金ID不一致、削除フラグfalse
//   entity = createPriceInfoEntity();
//   entity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf21"));
//   entity.setDeleteFlag(false);
//   priceInfoRepository.save(entity);
//
//   // 料金ID不一致、削除フラグtrue
//   entity = createPriceInfoEntity();
//   entity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf23"));
//   entity.setDeleteFlag(true);
//   priceInfoRepository.save(entity);
//   reset(priceInfoRepository);
//
//   //引数・戻り値設定
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   Exception ex = assertThrows(NotFoundException .class,() -> service.updateData(reqDto));
//   assertEquals("1番目の料金IDが見つかりません。料金ID:0a0711a5-ff74-4164-9309-8888b433cf22",
//   ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(0)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//
//   }
//
//   /**
//    * メソッド名: updateData<br>
//    * 試験名: 適用期間重複エラー処理<br>
//    * 条件: 適用期間重複あり<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void updateData_異常_適用期間重複チェック() {
//   // Arrange
//
//   // 料金情報の登録データがリソースID・優先度と一致するデータ(期間重複あり)
//   PriceInfoEntity entity = createPriceInfoEntity();
//   entity = createPriceInfoEntity();
//   entity.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T10:00:00Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T11:00:00Z"))));
//   entity.setPriority(1);
//   priceInfoRepository.save(entity);
//
//   reset(priceInfoRepository);
//
//   //引数・戻り値設定
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.updateData(reqDto));
//   assertEquals("1番目の料金情報の期間に重複があります。料金タイプ:4、料金単位：1、料金：1,000", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(0)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//
//   }
//
//   /**
//    * メソッド名: updateData<br>
//    * 試験名: 料金情報更新結果不正のエラー処理<br>
//    * 条件: 料金情報の更新結果で料金IDがnull<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void updateData_異常_料金情報の登録結果不正() {
//   // Arrange
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(null);
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.updateData(reqDto));
//   assertEquals("料金情報の更新に失敗しました。", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//   }
//
//   /**
//    * メソッド名: updateData<br>
//    * 試験名: 料金履歴情報更新結果不正のエラー処理<br>
//    * 条件: 料金履歴情報の更新結果で料金履歴IDがnull<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void updateData_異常_料金履歴情報の登録結果不正() {
//   // Arrange
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(null);
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.updateData(reqDto));
//   assertEquals("料金履歴IDの生成に失敗しました。", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(1)).save(any());
//   }
//
//   /**
//    * メソッド名: updateData<br>
//    * 試験名: 料金情報更新時の想定外エラー処理<br>
//    * 条件: 料金情報更新時に想定外エラー発生<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void updateData_異常_料金情報更新時想定外エラー() {
//   // Arrange
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doThrow(new ServiceErrorException("想定外のエラー")).when(priceInfoRepository).save(any());
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.updateData(reqDto));
//   assertEquals("想定外のエラー", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//   }
//
//   /**
//    * メソッド名: updateData<br>
//    * 試験名: 料金履歴情報更新時の想定外エラー処理<br>
//    * 条件: 料金履歴情報更新時に想定外エラー発生<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void updateData_異常_料金履歴情報更新時想定外エラー() {
//   // Arrange
//   PriceInfoRequestDto reqDto = createPriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//   doThrow(new ServiceErrorException("想定外のエラー")).when(priceInfoHistoryRepository).save(any());
//
//   // Act
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.updateData(reqDto));
//   assertEquals("想定外のエラー", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(1)).save(any());
//   }
//
//   /**
//    * メソッド名: deleteData<br>
//    * 試験名: 料金情報の正常削除<br>
//    * 条件: 存在チェック正常<br>
//    * 結果: 削除フラグがtrueに更新され履歴情報が保存される<br>
//    * テストパターン: 正常系<br>
//    */
//   @Test
//   public void deleteData_正常1件削除() {
//   // Arrange
//   // setupで更新対象のデータ、適用期間ケースが登録済み
//   PriceInfoRequestDto reqDto = new PriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//   reqDto.setOperatorId("ope01");
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setOperatorId("ope01");
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   assertDoesNotThrow(() -> service.deleteData(reqDto));
//
//   // Assert
//   ArgumentCaptor<PriceInfoEntity> cap = ArgumentCaptor.forClass(PriceInfoEntity.class);
//   verify(priceInfoRepository, times(1)).save(cap.capture());
//
//   Range<LocalDateTime> effectiveTime = retEntity.getEffectiveTime();
//   //料金情報
//   PriceInfoEntity capturedEntity = cap.getValue();
//   assertNotNull(capturedEntity.getPriceId());
//   assertEquals(retEntity.getResourceId(), capturedEntity.getResourceId());
//   assertEquals(retEntity.getResourceType(), capturedEntity.getResourceType());
//   assertEquals(retEntity.getPriceType(), capturedEntity.getPriceType());
//   assertEquals(retEntity.getPricePerUnit(), capturedEntity.getPricePerUnit());
//   assertEquals(retEntity.getPrice(), capturedEntity.getPrice());
//   assertEquals(effectiveTime, capturedEntity.getEffectiveTime());
//   assertEquals(retEntity.getPriority(), capturedEntity.getPriority());
//   assertEquals(retEntity.getOperatorId(), capturedEntity.getOperatorId());
//   assertNotNull(capturedEntity.getUpdateTime());
//   assertEquals(true, capturedEntity.getDeleteFlag());
//
//   //履歴
//   ArgumentCaptor<PriceHistoryInfoEntity> capHistory =
//   ArgumentCaptor.forClass(PriceHistoryInfoEntity.class);
//   verify(priceInfoHistoryRepository, times(1)).save(capHistory.capture());
//   PriceHistoryInfoEntity capturedHistoryEntity = capHistory.getValue();
//   assertNotNull(capturedHistoryEntity.getPriceHistoryId());
//   assertEquals(capturedEntity.getPriceId(), capturedHistoryEntity.getPriceId());
//   assertEquals(capturedEntity.getResourceId(), capturedHistoryEntity.getResourceId());
//   assertEquals(capturedEntity.getResourceType(), capturedHistoryEntity.getResourceType());
//   assertEquals(capturedEntity.getPriceType(), capturedHistoryEntity.getPriceType());
//   assertEquals(capturedEntity.getPricePerUnit(), capturedHistoryEntity.getPricePerUnit());
//   assertEquals(capturedEntity.getPrice(), capturedHistoryEntity.getPrice());
//   assertEquals(capturedEntity.getEffectiveTime(), capturedHistoryEntity.getEffectiveTime());
//   assertEquals(capturedEntity.getPriority(), capturedHistoryEntity.getPriority());
//   assertEquals(capturedEntity.getOperatorId(), capturedHistoryEntity.getOperatorId());
//   assertNotNull(capturedEntity.getUpdateTime());
//   assertEquals(capturedEntity.getDeleteFlag(), capturedHistoryEntity.getDeleteFlag());
//   }
//
//   /**
//    * メソッド名: deleteData<br>
//    * 試験名: 存在チェックエラー処理<br>
//    * 条件: 料金ID一致で削除フラグtrue<br>
//    * 結果: NotFoundExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void deleteData_異常_存在チェック() {
//   // Arrange
//   // 事前登録データ
//   // 料金ID一致、削除フラグtrue
//   PriceInfoEntity entity = createPriceInfoEntity();
//   entity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//   entity.setDeleteFlag(true);
//   priceInfoRepository.save(entity);
//
//   // 料金ID不一致、削除フラグfalse
//   entity = createPriceInfoEntity();
//   entity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf21"));
//   entity.setDeleteFlag(false);
//   priceInfoRepository.save(entity);
//
//   // 料金ID不一致、削除フラグtrue
//   entity = createPriceInfoEntity();
//   entity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf23"));
//   entity.setDeleteFlag(true);
//   priceInfoRepository.save(entity);
//   reset(priceInfoRepository);
//
//   //引数・戻り値設定
//   PriceInfoRequestDto reqDto = new PriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//   reqDto.setOperatorId("ope01");
//   reqDto.setRowNumber(1);
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   Exception ex = assertThrows(NotFoundException .class,() -> service.deleteData(reqDto));
//   assertEquals("1番目の料金IDが見つかりません。料金ID:0a0711a5-ff74-4164-9309-8888b433cf22",
//   ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(0)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//
//   }
//
//   /**
//    * メソッド名: deleteData<br>
//    * 試験名: 料金情報削除時の想定外エラー処理<br>
//    * 条件: 料金情報削除時に想定外エラー発生<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void deleteData_異常_料金情報削除時想定外エラー() {
//   // Arrange
//   //引数・戻り値設定
//   PriceInfoRequestDto reqDto = new PriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//   reqDto.setOperatorId("ope01");
//   reqDto.setRowNumber(1);
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doThrow(new ServiceErrorException("想定外のエラー")).when(priceInfoRepository).save(any());
//
// when(priceInfoHistoryRepository.save(any(PriceHistoryInfoEntity.class))).thenReturn(retHistoryEntity);
//
//   // Act
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.deleteData(reqDto));
//   assertEquals("想定外のエラー", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(0)).save(any());
//
//   }
//
//   /**
//    * メソッド名: deleteData<br>
//    * 試験名: 料金履歴情報削除時の想定外エラー処理<br>
//    * 条件: 料金履歴情報削除時に想定外エラー発生<br>
//    * 結果: ServiceErrorExceptionがスローされる<br>
//    * テストパターン: 異常系<br>
//    */
//   @Test
//   public void deleteData_異常_料金履歴情報削除時想定外エラー() {
//   // Arrange
//   //引数・戻り値設定
//   PriceInfoRequestDto reqDto = new PriceInfoRequestDto();
//   reqDto.setPriceId("0a0711a5-ff74-4164-9309-8888b433cf22");
//   reqDto.setOperatorId("ope01");
//   reqDto.setRowNumber(1);
//
//   PriceInfoEntity retEntity = createPriceInfoEntity();
//   retEntity.setPriceId(UUID.fromString("0a0711a5-ff74-4164-9309-8888b433cf22"));
//
//   PriceHistoryInfoEntity retHistoryEntity = new PriceHistoryInfoEntity();
//   BeanUtils.copyProperties(retEntity, retHistoryEntity);
//   retHistoryEntity.setPriceHistoryId(UUID.randomUUID());
//
//   doReturn(retEntity).when(priceInfoRepository).save(any(PriceInfoEntity.class));
//   doThrow(new ServiceErrorException("想定外のエラー")).when(priceInfoHistoryRepository).save(any());
//
//   // Act
//   Exception ex = assertThrows(ServiceErrorException .class,() -> service.deleteData(reqDto));
//   assertEquals("想定外のエラー", ex.getMessage());
//
//   // Assert
//   verify(priceInfoRepository, times(1)).save(any());
//   verify(priceInfoHistoryRepository, times(1)).save(any());
//
//   }
//
//   /**
//   * 料金情報共通処理リクエストDTO作成
//   * @return
//   */
//   private PriceInfoRequestDto createPriceInfoRequestDto() {
//   PriceInfoRequestDto ret = new PriceInfoRequestDto();
//   ret.setProcessingType(1);
//   ret.setPriceId("");
//   ret.setResourceId("リソースID");
//   ret.setResourceType(1);
//   ret.setPrimaryRouteOperatorId("主管航路事業者ID");
//   ret.setPriceType(4);
//   ret.setPricePerUnit(1);
//   ret.setPrice(1000);
//   ret.setEffectiveStartTime("2025-11-13T10:00:00Z");
//   ret.setEffectiveEndTime("2025-11-13T11:00:00Z");
//   ret.setOperatorId("ope01");
//   ret.setPriority(1);
//   ret.setRowNumber(1);
//
//   return ret;
//   }
//
//   /**
//   * 料金情報共通処理登録Entity作成
//   * @return
//   */
//   private PriceInfoEntity createPriceInfoEntity() {
//   PriceInfoEntity ret = new PriceInfoEntity();
//   ret.setPriceId(UUID.randomUUID());
//   ret.setResourceId("リソースID");
//   ret.setResourceType(1);
//   ret.setPrimaryRouteOperatorId("主管航路事業者ID1");
//   ret.setPriceType(4);
//   ret.setPricePerUnit(1);
//   ret.setPrice(1000);
//   ret.setEffectiveTime(Range.localDateTimeRange(
//   String.format("[%s,%s)",
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T10:00:00Z"),
//   StringUtils.parseDatetimeStringToLocalDateTime("2025-11-13T11:00:00Z"))));
//   ret.setOperatorId("preOpe01");
//   ret.setPriority(1);
//   ret.setDeleteFlag(false);
//
//   return ret;
//   }
//
// }
