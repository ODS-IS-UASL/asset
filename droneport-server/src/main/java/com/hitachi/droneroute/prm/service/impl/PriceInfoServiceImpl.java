package com.hitachi.droneroute.prm.service.impl;

import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.service.DroneRouteCommonService;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.StringUtils;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.prm.constants.PriceInfoConstants;
import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import com.hitachi.droneroute.prm.entity.PriceHistoryInfoEntity;
import com.hitachi.droneroute.prm.entity.PriceInfoEntity;
import com.hitachi.droneroute.prm.repository.PriceHistoryInfoRepository;
import com.hitachi.droneroute.prm.repository.PriceInfoRepository;
import com.hitachi.droneroute.prm.specification.PriceInfoSpecification;
import com.hitachi.droneroute.prm.validator.PriceInfoValidator;
import io.hypersistence.utils.hibernate.type.range.Range;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * 料金情報の共通機能クラス
 *
 * <p>料金情報の登録、更新、削除機能を提供する。 各リソースの処理から共通で呼び出す処理（APIとしては作成しない）
 */
@Service
@RequiredArgsConstructor
public class PriceInfoServiceImpl implements DroneRouteCommonService {

  // 料金情報リポジトリ
  private final PriceInfoRepository priceInfoRepository;

  // 料金履歴情報リポジトリ
  private final PriceHistoryInfoRepository priceInfoHistoryRepository;

  // 料金情報パラメータチェック
  private final PriceInfoValidator validator;

  // システム設定
  private final SystemSettings systemSettings;

  // ロガー
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 料金情報処理実行 パラメータの処理種別に応じて登録・更新・削除を実施する
   *
   * @param priceInfoList 料金情報リクエストDTOのリスト
   */
  public void process(List<PriceInfoRequestDto> priceInfoList) {

    logger.info("料金情報登録・更新・削除:  ===== START =====");
    logger.debug(priceInfoList.toString());

    // 主管航路事業者ID取得
    String primaryRouteOperatorId =
        (systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_ID,
            DronePortConstants.SETTINGS_DRONEPORT_ID_OPR));

    PriceInfoRequestDto priceInfo = new PriceInfoRequestDto();
    for (int i = 0; i < priceInfoList.size(); i++) {
      // 主管航路事業者IDを設定
      priceInfoList.get(i).setPrimaryRouteOperatorId(primaryRouteOperatorId);
      // エラーメッセージ用にレコード番号を設定
      priceInfoList.get(i).setRowNumber(i + 1);
    }

    // バリデーションチェック実施(全レコード)
    validator.validateAll(priceInfoList);

    // 処理種別に応じたサービス処理を実施
    for (int i = 0; i < priceInfoList.size(); i++) {
      priceInfo = priceInfoList.get(i);
      if (priceInfo.getProcessingType() != null
          && priceInfo.getProcessingType() == PriceInfoConstants.PROCESS_TYPE_REGIST) {
        registerData(priceInfo);
      } else if (priceInfo.getProcessingType() != null
          && priceInfo.getProcessingType() == PriceInfoConstants.PROCESS_TYPE_UPDATE) {
        updateData(priceInfo);
      } else if (priceInfo.getProcessingType() != null
          && priceInfo.getProcessingType() == PriceInfoConstants.PROCESS_TYPE_DELETE) {
        deleteData(priceInfo);
      } else {
        // スキップする
      }
    }

    logger.info("料金情報登録・更新・削除:  ===== END =====");
  }

  /**
   * 登録処理
   *
   * @param item 料金情報リクエストDTO
   */
  public void registerData(PriceInfoRequestDto item) {

    // 同一リソース・優先度に対して期間重複チェック
    checkEffectiveTimeRegest(item);

    // 料金情報登録更新Entity
    PriceInfoEntity newEntity = new PriceInfoEntity();

    // 登録データ作成
    setEntity(newEntity, item);
    newEntity.setPriceId(UUID.randomUUID());
    newEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
    newEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    newEntity.setOperatorId(item.getOperatorId());
    newEntity.setDeleteFlag(false);

    // DB登録
    PriceInfoEntity priceInfoEntity = priceInfoRepository.save(newEntity);

    // 編集
    if (Objects.nonNull(priceInfoEntity.getPriceId())) {
      // 処理なし
    } else {
      throw new ServiceErrorException("料金IDの生成に失敗しました。");
    }

    // 料金履歴登録
    registerPriceHistoryInfo(newEntity);
  }

  /**
   * 更新処理
   *
   * @param item 料金情報リクエストDTO
   */
  public void updateData(PriceInfoRequestDto item) {

    // 既存レコード検索
    Optional<PriceInfoEntity> optEntity =
        priceInfoRepository.findByPriceIdAndDeleteFlagFalse(UUID.fromString(item.getPriceId()));
    if (optEntity.isEmpty()) {
      throw new NotFoundException(
          MessageFormat.format(
              "{0}番目の料金IDが見つかりません。料金ID:{1}",
              String.valueOf(item.getRowNumber()), item.getPriceId()));
    }

    PriceInfoEntity entity = optEntity.get();

    // 同一リソース・優先度に対して期間重複チェック
    checkEffectiveTimeUpdate(item, entity);

    // 料金情報更新Entity
    PriceInfoEntity updateEntity = optEntity.get();

    // 更新データ作成
    setEntity(updateEntity, item);
    updateEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    updateEntity.setOperatorId(item.getOperatorId());

    // DB登録
    PriceInfoEntity priceInfoEntity = priceInfoRepository.save(updateEntity);

    // 編集
    if (Objects.nonNull(priceInfoEntity.getPriceId())) {
      // 処理なし
    } else {
      throw new ServiceErrorException("料金情報の更新に失敗しました。");
    }

    // 料金履歴登録
    registerPriceHistoryInfo(updateEntity);
  }

  /**
   * 削除処理
   *
   * @param item 料金情報リクエストDTO
   */
  public void deleteData(PriceInfoRequestDto item) {

    // 既存レコード検索
    Optional<PriceInfoEntity> optEntity =
        priceInfoRepository.findByPriceIdAndDeleteFlagFalse(UUID.fromString(item.getPriceId()));
    if (optEntity.isEmpty()) {
      throw new NotFoundException(
          MessageFormat.format(
              "{0}番目の料金IDが見つかりません。料金ID:{1}",
              String.valueOf(item.getRowNumber()), item.getPriceId()));
    }

    // 料金情報更新Entity
    PriceInfoEntity entity = optEntity.get();

    entity.setDeleteFlag(true);
    entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    entity.setOperatorId(item.getOperatorId());

    // DB更新
    priceInfoRepository.save(entity);

    // 料金履歴登録
    registerPriceHistoryInfo(entity);
  }

  /**
   * エンティティにリクエスト情報を設定
   *
   * @param priceInfoEntity 料金情報エンティティ
   * @param request 料金情報リクエストDTO
   */
  private void setEntity(PriceInfoEntity priceInfoEntity, PriceInfoRequestDto request) {
    if (request.getPriceId() != null && !request.getPriceId().isBlank()) {
      priceInfoEntity.setPriceId(UUID.fromString(request.getPriceId()));
    }

    if (request.getResourceId() != null) {
      priceInfoEntity.setResourceId(request.getResourceId());
    }

    if (request.getResourceType() != null) {
      priceInfoEntity.setResourceType(request.getResourceType());
    }

    if (request.getPrimaryRouteOperatorId() != null) {
      priceInfoEntity.setPrimaryRouteOperatorId(request.getPrimaryRouteOperatorId());
    }

    if (request.getPriceType() != null) {
      priceInfoEntity.setPriceType(request.getPriceType());
    }

    if (request.getPricePerUnit() != null) {
      priceInfoEntity.setPricePerUnit(request.getPricePerUnit());
    }

    if (request.getPrice() != null) {
      priceInfoEntity.setPrice(request.getPrice());
    }

    if (org.springframework.util.StringUtils.hasText(request.getEffectiveStartTime())
        && org.springframework.util.StringUtils.hasText(request.getEffectiveEndTime())) {
      priceInfoEntity.setEffectiveTime(
          Range.localDateTimeRange(
              String.format(
                  "[%s,%s)",
                  StringUtils.parseDatetimeStringToLocalDateTime(request.getEffectiveStartTime())
                      .toString(),
                  StringUtils.parseDatetimeStringToLocalDateTime(request.getEffectiveEndTime())
                      .toString())));
    }

    if (request.getPriority() != null) {
      priceInfoEntity.setPriority(request.getPriority());
    }
  }

  /**
   * 料金履歴情報登録処理
   *
   * @param entity 料金情報エンティティ
   */
  public void registerPriceHistoryInfo(PriceInfoEntity entity) {
    // 料金履歴情報登録Entity
    PriceHistoryInfoEntity newEntity = new PriceHistoryInfoEntity();

    // 登録データ作成
    BeanUtils.copyProperties(entity, newEntity);
    newEntity.setPriceHistoryId(UUID.randomUUID());

    // DB登録
    PriceHistoryInfoEntity priceHistoryInfoEntity = priceInfoHistoryRepository.save(newEntity);

    // 編集
    if (Objects.nonNull(priceHistoryInfoEntity.getPriceHistoryId())) {
      // 処理なし
    } else {
      throw new ServiceErrorException("料金履歴IDの生成に失敗しました。");
    }
  }

  /**
   * 適用期間 重複確認 同一リソース・優先度に対して期間の重複チェックを実施する
   *
   * @param item 料金情報リクエストDTO
   */
  private void checkEffectiveTimeRegest(PriceInfoRequestDto item) {
    // 料金情報の検索条件作成クラスを生成
    PriceInfoSpecification<PriceInfoEntity> spec = new PriceInfoSpecification<>();

    // 料金情報を検索
    Specification<PriceInfoEntity> specification =
        Specification.where(spec.resourceIdIn(List.of(item.getResourceId())))
            .and(spec.priorityEquals(Integer.valueOf(item.getPriority())))
            .and(
                spec.tsrangeOverlap(
                    StringUtils.parseDatetimeString(item.getEffectiveStartTime()),
                    StringUtils.parseDatetimeString(item.getEffectiveEndTime())));

    List<PriceInfoEntity> entityList = priceInfoRepository.findAll(specification);
    if (!entityList.isEmpty()) {
      throw new ServiceErrorException(
          MessageFormat.format(
              "{0}番目の料金情報の期間に重複があります。料金タイプ:{1}、料金単位：{2}、料金：{3}",
              String.valueOf(item.getRowNumber()),
              item.getPriceType(),
              item.getPricePerUnit(),
              item.getPrice()));
    }
  }

  /**
   * 適用期間 重複確認(更新時) 同一リソース・優先度に対して期間の重複チェックを実施する
   *
   * @param item 料金情報リクエストDTO
   * @param entity 料金情報エンティティ
   */
  private void checkEffectiveTimeUpdate(PriceInfoRequestDto item, PriceInfoEntity entity) {

    // 項目に値がない場合は元の設定を使用する
    // リソースID
    String resouseId = item.getResourceId();
    if (!org.springframework.util.StringUtils.hasText(item.getResourceId())) {
      resouseId = entity.getResourceId();
    }

    // 優先度
    Integer priority = item.getPriority();
    if (item.getPriority() == null) {
      priority = entity.getPriority();
    }

    // 適用開始日時
    String effectiveStartTime = item.getEffectiveStartTime();
    if (!org.springframework.util.StringUtils.hasText(item.getEffectiveStartTime())) {
      effectiveStartTime = entity.getEffectiveTime().lower().toString() + "Z";
    }

    // 適用終了日時
    String effectiveEndTime = item.getEffectiveEndTime();
    if (!org.springframework.util.StringUtils.hasText(item.getEffectiveEndTime())) {
      effectiveEndTime = entity.getEffectiveTime().upper().toString() + "Z";
    }

    // 料金情報の検索条件作成クラスを生成
    PriceInfoSpecification<PriceInfoEntity> spec = new PriceInfoSpecification<>();

    // 料金情報を検索
    Specification<PriceInfoEntity> specification =
        Specification.where(spec.resourceIdIn(List.of(resouseId)))
            .and(spec.priorityEquals(Integer.valueOf(priority)))
            .and(
                spec.tsrangeOverlap(
                    StringUtils.parseDatetimeString(effectiveStartTime),
                    StringUtils.parseDatetimeString(effectiveEndTime)));

    // 自分自身を除外する
    specification = specification.and(spec.priceIdNotEquals(UUID.fromString(item.getPriceId())));

    List<PriceInfoEntity> entityList = priceInfoRepository.findAll(specification);
    if (!entityList.isEmpty()) {
      throw new ServiceErrorException(
          MessageFormat.format(
              "{0}番目の料金情報の期間に重複があります。料金タイプ:{1}、料金単位：{2}、料金：{3}",
              String.valueOf(item.getRowNumber()),
              item.getPriceType(),
              item.getPricePerUnit(),
              item.getPrice()));
    }
  }
}
