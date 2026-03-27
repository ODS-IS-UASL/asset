package com.hitachi.droneroute.prm.validator;

import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.validator.Validator;
import com.hitachi.droneroute.prm.constants.PriceInfoConstants;
import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
import java.math.BigInteger;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** 料金情報関連APIパラメータチェッククラス */
@Component
@RequiredArgsConstructor
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PriceInfoValidator extends Validator {

  private final CodeMaster codeMaster;

  /**
   * 料金情報一覧APIパラメータチェック
   *
   * @param dto 料金情報検索リクエストDTO
   */
  public void validateForGetList(PriceInfoSearchListRequestDto dto) {

    // リソースID
    notNull("リソースID", dto.getResourceId());

    // 入力チェック
    validateForSearchList(dto);

    validate();
  }

  /**
   * 料金情報一覧入力チェック
   *
   * @param dto 料金情報検索リクエストDTO
   */
  private void validateForSearchList(PriceInfoSearchListRequestDto dto) {

    // 料金ID
    checkUUID("料金ID", dto.getPriceId());

    // リソースID
    // リソース種別が離着陸場以外の場合はUUIDのチェックを実施
    BigInteger resourceType = dto.getResourceType();
    if (resourceType != null
        && !resourceType.equals(BigInteger.valueOf(PriceInfoConstants.RESOURCE_TYPE_PORT))) {
      // カンマ区切りの複数IDに対応
      String[] resourceIds = dto.getResourceId().split(",");
      for (String resourceId : resourceIds) {
        checkUUID("リソースID", resourceId.trim());
      }
    }

    // リソース種別
    {
      Integer[] values = codeMaster.getIntegerArray(PriceInfoConstants.CODE_MASTER_RESOURCE_TYPE);
      checkRange("リソース種別", dto.getResourceType(), values);
    }

    // 料金タイプ
    {
      Integer[] values = codeMaster.getIntegerArray(PriceInfoConstants.CODE_MASTER_PRICE_TYPE);
      checkRange("料金タイプ", dto.getPriceType(), values);
    }

    // 料金単位
    checkRange(
        "料金単位(以上)",
        dto.getPricePerUnitFrom(),
        BigInteger.valueOf(1),
        BigInteger.valueOf(Integer.MAX_VALUE));
    checkRange(
        "料金単位(以下)",
        dto.getPricePerUnitTo(),
        BigInteger.valueOf(1),
        BigInteger.valueOf(Integer.MAX_VALUE));

    // 料金
    checkRange(
        "料金(以上)", dto.getPriceFrom(), BigInteger.valueOf(0), BigInteger.valueOf(Integer.MAX_VALUE));
    checkRange(
        "料金(以下)", dto.getPriceTo(), BigInteger.valueOf(0), BigInteger.valueOf(Integer.MAX_VALUE));

    // 日時条件
    checkDateTime("日時条件(開始)", dto.getEffectiveStartTime());
    checkDateTime("日時条件(終了)", dto.getEffectiveEndTime());

    // ソート順
    // カンマ区切りで0、または1のみ
    checkRange("ソート順", dto.getSortOrders(), new Integer[] {0, 1});

    // ソート順の設定数と、ソート対象列の設定数
    {
      int sortOrdersNum = 0;
      if (StringUtils.hasText(dto.getSortOrders())) {
        sortOrdersNum = dto.getSortOrders().split(",").length;
      }
      int sortColumnsNum = 0;
      if (StringUtils.hasText(dto.getSortColumns())) {
        sortColumnsNum = dto.getSortColumns().split(",").length;
      }
      if (sortOrdersNum != sortColumnsNum) {
        details.add(errorMessage_sort);
      }
    }
  }

  /**
   * 料金情報パラメータチェック(全件)
   *
   * @param priceInfoList 料金情報リクエストDTOのリスト
   */
  public void validateAll(List<PriceInfoRequestDto> priceInfoList) {

    PriceInfoRequestDto priceInfo = new PriceInfoRequestDto();

    for (int i = 0; i < priceInfoList.size(); i++) {
      priceInfo = priceInfoList.get(i);

      // 処理種別チェック
      notNull(String.valueOf(priceInfo.getRowNumber()) + "番目の処理種別", priceInfo.getProcessingType());
      // 処理種別のコード値チェック
      {
        Integer[] values =
            codeMaster.getIntegerArray(PriceInfoConstants.CODE_MASTER_PROCESSING_TYPE);
        checkRange(
            String.valueOf(priceInfo.getRowNumber()) + "番目の処理種別",
            priceInfo.getProcessingType(),
            values);
      }

      if (priceInfo.getProcessingType() != null
          && priceInfo.getProcessingType() == PriceInfoConstants.PROCESS_TYPE_REGIST) {
        validateForRegist(priceInfo);
      } else if (priceInfo.getProcessingType() != null
          && priceInfo.getProcessingType() == PriceInfoConstants.PROCESS_TYPE_UPDATE) {
        validateForUpdate(priceInfo);
      } else if (priceInfo.getProcessingType() != null
          && priceInfo.getProcessingType() == PriceInfoConstants.PROCESS_TYPE_DELETE) {
        validateForDelete(priceInfo);
      } else {
        // バリデーションチェックをスキップする
      }
    }
    validate();
  }

  /**
   * 料金情報登録パラメータチェック
   *
   * @param dto 料金情報リクエストDTO
   */
  public void validateForRegist(PriceInfoRequestDto dto) {
    // 登録時必須チェック
    validateForRegistRequied(dto);

    // 入力チェック
    validateForRegistUpdate(dto);
  }

  /**
   * 料金情報更新パラメータチェック
   *
   * @param dto 料金情報リクエストDTO
   */
  public void validateForUpdate(PriceInfoRequestDto dto) {
    // 更新時必須チェック
    validateForUpdateRequired(dto);

    // 入力チェック
    validateForRegistUpdate(dto);
  }

  /**
   * 料金情報削除パラメータチェック
   *
   * @param dto 料金情報リクエストDTO
   */
  public void validateForDelete(PriceInfoRequestDto dto) {

    // 料金ID
    notNull(String.valueOf(dto.getRowNumber()) + "番目の料金ID", dto.getPriceId());
    checkUUID(String.valueOf(dto.getRowNumber()) + "番目の料金ID", dto.getPriceId());

    // オペレータID
    notNull(String.valueOf(dto.getRowNumber()) + "番目のオペレータID", dto.getOperatorId());
  }

  /**
   * 登録時必須チェック
   *
   * @param dto 料金情報リクエストDTO
   */
  private void validateForRegistRequied(PriceInfoRequestDto dto) {

    // リソースID
    notNull(String.valueOf(dto.getRowNumber()) + "番目のリソースID", dto.getResourceId());

    // リソース種別
    notNull(String.valueOf(dto.getRowNumber()) + "番目のリソース種別", dto.getResourceType());

    // 料金タイプ
    notNull(String.valueOf(dto.getRowNumber()) + "番目の料金タイプ", dto.getPriceType());

    // 料金単位
    notNull(String.valueOf(dto.getRowNumber()) + "番目の料金単位", dto.getPricePerUnit());

    // 料金
    notNull(String.valueOf(dto.getRowNumber()) + "番目の料金", dto.getPrice());

    // 適用開始時間
    notNull(String.valueOf(dto.getRowNumber()) + "番目の適用開始時間", dto.getEffectiveStartTime());

    // 適用終了時間
    notNull(String.valueOf(dto.getRowNumber()) + "番目の適用終了時間", dto.getEffectiveEndTime());

    // 優先度
    notNull(String.valueOf(dto.getRowNumber()) + "番目の優先度", dto.getPriority());

    // オペレータID
    notNull(String.valueOf(dto.getRowNumber()) + "番目のオペレータID", dto.getOperatorId());
  }

  /**
   * 登録更新時入力チェック
   *
   * @param dto 料金情報リクエストDTO
   */
  private void validateForRegistUpdate(PriceInfoRequestDto dto) {
    // 料金ID
    checkUUID(String.valueOf(dto.getRowNumber()) + "番目の料金ID", dto.getPriceId());

    // リソースID
    // リソース種別が離着陸場以外の場合はUUIDのチェックを実施
    if (dto.getResourceType() != null
        && dto.getResourceType() != PriceInfoConstants.RESOURCE_TYPE_PORT) {
      checkUUID(String.valueOf(dto.getRowNumber()) + "番目のリソースID", dto.getResourceId());
    }

    // リソース種別
    {
      Integer[] values = codeMaster.getIntegerArray(PriceInfoConstants.CODE_MASTER_RESOURCE_TYPE);
      checkRange(String.valueOf(dto.getRowNumber()) + "番目のリソース種別", dto.getResourceType(), values);
    }

    // 料金タイプ
    {
      Integer[] values = codeMaster.getIntegerArray(PriceInfoConstants.CODE_MASTER_PRICE_TYPE);
      checkRange(dto.getRowNumber() + "番目の料金タイプ", dto.getPriceType(), values);
    }

    // 料金単位
    checkRange(
        String.valueOf(dto.getRowNumber()) + "番目の料金単位",
        dto.getPricePerUnit(),
        1,
        Integer.MAX_VALUE);

    // 料金
    checkRange(String.valueOf(dto.getRowNumber()) + "番目の料金", dto.getPrice(), 0, Integer.MAX_VALUE);

    // 日時条件
    checkDateTime(String.valueOf(dto.getRowNumber()) + "番目の適用開始時間", dto.getEffectiveStartTime());
    checkDateTime(String.valueOf(dto.getRowNumber()) + "番目の適用終了時間", dto.getEffectiveEndTime());
    // 開始日時、終了日時の前後チェック
    compareDateTime(
        String.valueOf(dto.getRowNumber()) + "番目の適用開始日時",
        "適用終了日時",
        dto.getEffectiveStartTime(),
        dto.getEffectiveEndTime());
    // 適用開始時間終了の同一チェック
    checkNotEqualDateTime(
        String.valueOf(dto.getRowNumber()) + "番目の適用開始時間",
        "適用終了時間",
        dto.getEffectiveStartTime(),
        dto.getEffectiveEndTime());
    // 優先度
    checkRange(String.valueOf(dto.getRowNumber()) + "番目の優先度", dto.getPriority(), 1, 100);
  }

  /**
   * 更新時必須チェック
   *
   * @param dto 料金情報リクエストDTO
   */
  private void validateForUpdateRequired(PriceInfoRequestDto dto) {
    // 料金ID
    notNull(String.valueOf(dto.getRowNumber()) + "番目の料金ID", dto.getPriceId());

    // リソースID
    notNull(String.valueOf(dto.getRowNumber()) + "番目のリソースID", dto.getResourceId());

    // オペレータID
    notNull(String.valueOf(dto.getRowNumber()) + "番目のオペレータID", dto.getOperatorId());
  }
}
