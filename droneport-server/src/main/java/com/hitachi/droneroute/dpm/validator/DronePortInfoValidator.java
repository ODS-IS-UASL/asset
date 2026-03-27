package com.hitachi.droneroute.dpm.validator;

import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.Base64Utils;
import com.hitachi.droneroute.cmn.validator.Validator;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** 離着陸場情報関連APIのパラメータチェッククラス */
@Component
@RequiredArgsConstructor
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DronePortInfoValidator extends Validator {

  private final CodeMaster codeMaster;
  private final SystemSettings systemSettings;

  /**
   * 離着陸場情報登録APIのパラメータチェック
   *
   * @param dto 離着陸場情報登録更新要求
   */
  public void validateForRegist(DronePortInfoRegisterRequestDto dto) {
    validateForRegistRequired(dto);
    validateForRegistUpdate(dto);

    notNull("動作状況", dto.getActiveStatus());
    if (Objects.nonNull(dto.getActiveStatus())) {
      if (dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_UNAVAILABLE) {
        // 動作状況が3:使用不可の場合は、使用不可開始日時必須
        notNull("使用不可開始日時", dto.getInactiveTimeFrom());
      } else if (dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_MAINTENANCE) {
        // 動作状況が4:メンテナンス中の場合は、使用不可開始日時,使用不可終了日時必須
        notNull("使用不可開始日時", dto.getInactiveTimeFrom());
        notNull("使用不可終了日時", dto.getInactiveTimeTo());
      } else {
        // 使用不可開始日時、または使用不可終了日時が入力あり、動作状況入力1または2はチェックエラーとする
        if ((StringUtils.hasText(dto.getInactiveTimeFrom())
            || StringUtils.hasText(dto.getInactiveTimeTo()))) {
          details.add(errorMessage_checkInactiveTime);
        }
      }
    } else {
      // 使用不可開始日時、または使用不可終了日時が入力あり、動作状況入力なしはチェックエラーとする
      if ((StringUtils.hasText(dto.getInactiveTimeFrom())
          || StringUtils.hasText(dto.getInactiveTimeTo()))) {
        details.add(errorMessage_checkInactiveTime);
      }
    }

    validate();
  }

  /**
   * 離着陸場情報登録APIのパラメータチェック(必須)
   *
   * @param dto 離着陸場情報登録更新要求
   */
  private void validateForRegistRequired(DronePortInfoRegisterRequestDto dto) {

    // 離着陸場名
    notNull("離着陸場名", dto.getDronePortName());

    // 離着陸場メーカーID
    notNull("離着陸場メーカーID", dto.getDronePortManufacturerId());
    checkLength("離着陸場メーカーID", dto.getDronePortManufacturerId(), 100);

    // ポート形状
    notNull("ポート形状", dto.getPortType());

    // 緯度
    notNull("緯度", dto.getLat());

    // 経度
    notNull("経度", dto.getLon());

    // 公開可否フラグ
    notNull("公開可否フラグ", dto.getPublicFlag());
  }

  /**
   * 離着陸場情報登録/更新APIのパラメータチェック(必須)
   *
   * @param dto 離着陸場情報登録更新要求
   */
  private void validateForRegistUpdate(DronePortInfoRegisterRequestDto dto) {
    // 離着陸場名
    // 最大24文字
    checkLength("離着陸場名", dto.getDronePortName(), 24);

    // 設置場所住所
    // 最大50文字
    checkLength("設置場所住所", dto.getAddress(), 50);

    // 製造メーカー
    // 最大24文字
    checkLength("製造メーカー", dto.getManufacturer(), 24);

    // 製造番号
    // 最大20文字
    checkLength("製造番号", dto.getSerialNumber(), 20);

    // ポート形状
    // コードマスタから取得した値域で判定する
    {
      Integer[] values = codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_PORT_TYPE);
      checkRange("ポート形状", dto.getPortType(), values);
    }

    // VIS離着陸場事業者ID
    // 最大100文字
    checkLength("VIS離着陸場事業者ID", dto.getVisDronePortCompanyId(), 100);

    // 格納中機体ID
    if (!(Objects.nonNull(dto.getPortType())
        && dto.getPortType().equals(DronePortConstants.PORT_TYPE_DRONEPORT))) {
      // ポート形状:1(離着陸場)以外
      if (StringUtils.hasText(dto.getStoredAircraftId())) {
        details.add(MessageFormat.format(errorMessage_storedAircraftId, dto.getPortType()));
      }
    }
    checkUUID("格納中機体ID", dto.getStoredAircraftId());

    // 緯度
    checkRange("緯度", dto.getLat(), -90d, 90d);

    // 経度
    checkRange("経度", dto.getLon(), -180d, 180d);

    // 着陸面対地高度
    checkRange("着陸面対地高度", dto.getAlt(), 0, 100);

    // 対応機体
    // 最大24文字
    checkLength("対応機体", dto.getSupportDroneType(), 24);

    // 動作状況
    // コードマスタから取得した値域で判定する
    {
      Integer[] values = codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_ACTIVE_STATUS);
      checkRange("動作状況", dto.getActiveStatus(), values);
    }

    // 使用不可開始日時
    checkDateTime("使用不可開始日時", dto.getInactiveTimeFrom());
    // 使用不可終了日時
    checkDateTime("使用不可終了日時", dto.getInactiveTimeTo());
    // 開始日時、終了日時の前後チェック
    compareDateTime("使用不可開始日時", "使用不可終了日時", dto.getInactiveTimeFrom(), dto.getInactiveTimeTo());

    // 画像
    if (StringUtils.hasText(dto.getImageData())) {
      Base64Utils util =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  DronePortConstants.SETTINGS_IMAGE_DATA,
                  DronePortConstants.SETTINGS_SUPPORT_FORMAT));
      // データURIの画像フォーマットをチェックする
      if (!util.checkSubtype(dto.getImageData())) {
        details.add(errorMessage_checkBase64);
      }
    }
    // 画像サイズ
    // 事前にbase64からバイナリ変換を行っていること。
    int binarySize =
        systemSettings.getIntegerValue(
            DronePortConstants.SETTINGS_IMAGE_DATA, DronePortConstants.SETTINGS_BINARY_SIZE);
    if (Objects.nonNull(dto.getImageBinary()) && dto.getImageBinary().length > binarySize) {
      details.add(MessageFormat.format(errorMessage_checkLength, "画像", binarySize));
    }
  }

  /**
   * 離着陸場情報更新APIのパラメータチェック
   *
   * @param dto 離着陸場情報登録更新要求
   */
  public void validateForUpdate(DronePortInfoRegisterRequestDto dto) {
    // 離着陸場ID
    notNull("離着陸場ID", dto.getDronePortId());

    // 離着陸場メーカーID
    if (StringUtils.hasText(dto.getDronePortManufacturerId())) {
      // 更新時は入力不可
      details.add(errorMessage_dronePortManufacturer);
    }

    validateForRegistUpdate(dto);

    // 離着陸場名は登録時必須項目なので、空文字設定不可(未設定に更新できない)
    if (Objects.nonNull(dto.getDronePortName()) && dto.getDronePortName().isBlank()) {
      details.add(MessageFormat.format(errorMessage_notNull, "離着陸場名"));
    }

    // 動作状況と使用不可日時をチェック
    if (Objects.nonNull(dto.getActiveStatus())) {
      if (dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_UNAVAILABLE) {
        // 動作状況が3:使用不可の場合は、使用不可開始日時必須
        notNull("使用不可開始日時", dto.getInactiveTimeFrom());
      } else if (dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_MAINTENANCE) {
        // 動作状況が4:メンテナンス中の場合は、使用不可開始日時,使用不可終了日時必須
        notNull("使用不可開始日時", dto.getInactiveTimeFrom());
        notNull("使用不可終了日時", dto.getInactiveTimeTo());
      } else {
        // 使用不可開始日時、または使用不可終了日時が入力あり、動作状況入力1または2はチェックエラーとする
        if ((StringUtils.hasText(dto.getInactiveTimeFrom())
            || StringUtils.hasText(dto.getInactiveTimeTo()))) {
          details.add(errorMessage_checkInactiveTime);
        }
      }
    } else {
      // 使用不可開始日時、または使用不可終了日時が入力あり、動作状況入力なしはチェックエラーとする
      if ((StringUtils.hasText(dto.getInactiveTimeFrom())
          || StringUtils.hasText(dto.getInactiveTimeTo()))) {
        details.add(errorMessage_checkInactiveTime);
      }
    }

    validate();
  }

  /**
   * 離着陸場情報一覧取得APIのパラメータチェック
   *
   * @param dto 離着陸場情報一覧取得要求
   */
  public void validateForGetList(DronePortInfoListRequestDto dto) {
    // 離着陸場名
    // 最大24文字
    checkLength("離着陸場名", dto.getDronePortName(), 24);

    // 設置場所住所
    // 最大50文字
    checkLength("設置場所住所", dto.getAddress(), 50);

    // 製造メーカー
    // 最大24文字
    checkLength("製造メーカー", dto.getManufacturer(), 24);

    // 製造番号
    // 最大20文字
    checkLength("製造番号", dto.getSerialNumber(), 20);

    // ポート形状
    {
      Integer[] range = codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_PORT_TYPE);
      String str = dto.getPortType();
      if (StringUtils.hasText(str)) {
        for (String s : str.split(",")) {
          checkValue("ポート形状", s, Pattern.compile("[\\d]+"));
          checkRangeInteger(
              "ポート形状",
              s,
              Collections.min(Arrays.asList(range)),
              Collections.max(Arrays.asList(range)));
        }
      }
    }

    // 検索位置範囲は、全て設定ありの場合に範囲チェック
    if (Objects.nonNull(dto.getMinLat())
        || Objects.nonNull(dto.getMinLon())
        || Objects.nonNull(dto.getMaxLat())
        || Objects.nonNull(dto.getMaxLon())) {
      // 最小緯度(南側)
      // -90.00000000～90.00000000
      {
        String name = "最小緯度(南側)";
        Double value = dto.getMinLat();
        notNull(name, value);
        checkRange(name, value, -90d, 90d);
      }
      // 最小経度(西側)
      // -180.0000000～180.0000000
      {
        String name = "最小経度(西側)";
        Double value = dto.getMinLon();
        notNull(name, value);
        checkRange(name, value, -180d, 180d);
      }

      // 最大緯度(北側)
      // -90.00000000～90.00000000
      {
        String name = "最大緯度(北側)";
        Double value = dto.getMaxLat();
        notNull(name, value);
        checkRange(name, value, -90d, 90d);
      }

      // 最大経度(東側)
      // -180.0000000～180.0000000
      {
        String name = "最大経度(東側)";
        Double value = dto.getMaxLon();
        notNull(name, value);
        checkRange(name, value, -180d, 180d);
      }
    }

    // 公開可否フラグ　true/false
    {
      String str = dto.getPublicFlag();
      if (StringUtils.hasText(str)) {
        if (!"true".equals(str) && !"false".equals(str)) {
          details.add(MessageFormat.format("公開可否フラグが不正です。\n入力値:{0}", str));
        }
      }
    }

    // 料金情報要否フラグ true/false
    {
      String str = dto.getIsRequiredPriceInfo();
      if (StringUtils.hasText(str)) {
        if (!"true".equals(str) && !"false".equals(str)) {
          checkValue("料金情報要否", str, Pattern.compile("[\\d]+"));
        }
      }
    }

    // 対応機体
    // 最大24文字
    checkLength("対応機体", dto.getSupportDroneType(), 24);

    // 動作状況
    {
      Integer[] range = codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_ACTIVE_STATUS);
      String str = dto.getActiveStatus();
      if (StringUtils.hasText(str)) {
        for (String s : str.split(",")) {
          checkValue("動作状況", s, Pattern.compile("[\\d]+"));
          checkRangeInteger(
              "動作状況",
              s,
              Collections.min(Arrays.asList(range)),
              Collections.max(Arrays.asList(range)));
        }
      }
    }

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

    // ページ制御項目のチェック
    checkRangeInteger("1ページ当たりの件数", dto.getPerPage(), 1, 100);
    checkRangeInteger("現在ページ番号", dto.getPage(), 1, Integer.MAX_VALUE);
    if (!StringUtils.hasText(dto.getPage()) && StringUtils.hasText(dto.getPerPage())) {
      notNull("現在ページ番号", dto.getPage());
    }
    if (StringUtils.hasText(dto.getPage()) && !StringUtils.hasText(dto.getPerPage())) {
      notNull("1ページ当たりの件数", dto.getPerPage());
    }

    validate();
  }

  /**
   * 離着陸場情報詳細取得APIのパラメータチェック
   *
   * @param dronePortId 離着陸場ID
   */
  public void validateForGetDetail(String dronePortId) {
    // 離着陸場ID
    // UUID
    notNull("離着陸場ID", dronePortId);

    validate();
  }

  /**
   * 離着陸場情報削除APIのパラメータチェック
   *
   * @param dto 離着陸場情報削除要求DTO
   */
  public void validateForDelete(DronePortInfoDeleteRequestDto dto) {
    notNull("オペレータID", dto.getOperatorId());

    validate();
  }
}
