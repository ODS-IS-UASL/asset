package com.hitachi.droneroute.arm.validator;

import com.hitachi.droneroute.arm.constants.AircraftConstants;
import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoModelInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoModelSearchRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.Base64Utils;
import com.hitachi.droneroute.cmn.validator.Validator;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** 機体情報関連APIパラメータチェッククラス */
@Component
@RequiredArgsConstructor
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AircraftInfoValidator extends Validator {

  private final CodeMaster codeMaster;

  private final SystemSettings systemSettings;

  /**
   * 機体情報登録APIパラメータチェック
   *
   * @param dto 機体情報登録APIのリクエストDTO
   */
  public void validateForRegist(AircraftInfoRequestDto dto) {
    // 登録時必須チェック
    validateForRegistRequied(dto);

    // 入力チェック
    validateForRegistUpdate(dto);

    validate();
  }

  /**
   * 機体情報更新APIパラメータチェック
   *
   * @param dto 機体情報更新APIのリクエストDTO
   */
  public void validateForUpdate(AircraftInfoRequestDto dto) {
    // 更新時必須チェック
    validateForUpdateRequired(dto);

    // 入力チェック
    validateForRegistUpdate(dto);

    validate();
  }

  /**
   * 機体情報一覧APIパラメータチェック
   *
   * @param dto 機体情報一覧APIのリクエストDTO
   */
  public void validateForGetList(AircraftInfoSearchListRequestDto dto) {
    // 入力チェック
    validateForSearchList(dto);

    validate();
  }

  /**
   * 機体情報モデル検索APIパラメータチェック
   *
   * @param dto 機体情報モデル検索APIのリクエストDTO
   */
  public void validateForModelSearch(AircraftInfoModelSearchRequestDto dto) {
    // 入力チェック
    validateForModelSearchList(dto);

    validate();
  }

  /**
   * 機体情報詳細APIパラメータチェック
   *
   * @param aircraftId 機体ID
   */
  public void validateForDetail(String aircraftId) {
    // 入力チェック
    notNull("機体ID", aircraftId);
    checkUUID("機体ID", aircraftId);

    validate();
  }

  /**
   * 登録時必須チェック
   *
   * @param dto 機体情報登録APIのリクエストDTO
   */
  private void validateForRegistRequied(AircraftInfoRequestDto dto) {

    // 機体名
    notNull("機体名", dto.getAircraftName());

    // 機体所有種別
    notNull("機体所有種別", dto.getOwnerType());

    // 位置情報（緯度）
    notNull("位置情報（緯度）", dto.getLat());

    // 位置情報（経度）
    notNull("位置情報（経度）", dto.getLon());

    // 公開可否フラグ
    notNull("公開可否フラグ", dto.getPublicFlag());
  }

  /**
   * 登録更新時入力チェック
   *
   * @param dto 機体情報登録APIのリクエストDTO
   */
  private void validateForRegistUpdate(AircraftInfoRequestDto dto) {
    // 機体ID
    checkUUID("機体ID", dto.getAircraftId());

    // 機体名 24文字
    checkLength("機体名", dto.getAircraftName(), 24);

    // 製造メーカー 200文字
    checkLength("製造メーカー", dto.getManufacturer(), 200);

    // 型式番号 200文字
    checkLength("型式番号", dto.getModelNumber(), 200);

    // 機種名 200文字
    checkLength("機種名", dto.getModelName(), 200);

    // 製造番号 20文字
    checkLength("製造番号", dto.getManufacturingNumber(), 20);

    // 機体の種別 1~6
    {
      Integer[] values = codeMaster.getIntegerArray(AircraftConstants.CODE_MASTER_AIRCRAFT_TYPE);
      checkRange("機体の種別", dto.getAircraftType(), values);
    }

    // 最大離陸重量 0.0~1000.000
    checkRange("最大離陸重量", dto.getMaxTakeoffWeight(), 0.0, 1000.000);

    // 重量 0.0~1000.000
    checkRange("重量", dto.getBodyWeight(), 0.0, 1000.0);

    // 最大速度 0.0~1000.000
    checkRange("最大速度", dto.getMaxFlightSpeed(), 0.0, 1000.0);

    // 最大飛行時間 0.0~1000.000
    checkRange("最大速度", dto.getMaxFlightTime(), 0.0, 1000.000);

    // 位置情報（緯度）　-90.00000～90.00000
    checkRange("位置情報（緯度）", dto.getLat(), -90.00000, 90.00000);

    // 位置情報（経度）　-180.00000~180.00000
    checkRange("位置情報（経度）", dto.getLon(), -180.00000, 180.00000);

    // DIPS登録記号 12文字
    checkLength("DIPS登録記号", dto.getDipsRegistrationCode(), 12);

    // 機体所有種別 1~2
    {
      Integer[] values = codeMaster.getIntegerArray(AircraftConstants.CODE_MASTER_OWNER_TYPE);
      checkRange("機体所有種別", dto.getOwnerType(), values);
    }

    // 所有者ID
    checkUUID("所有者ID", dto.getOwnerId());

    // 画像
    if (StringUtils.hasText(dto.getImageData())) {
      Base64Utils util =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  AircraftConstants.SETTINGS_IMAGE_DATA,
                  AircraftConstants.SETTINGS_SUPPORT_FORMAT));
      if (!util.checkSubtype(dto.getImageData())) {
        details.add(errorMessage_checkBase64);
      }
    }

    // 画像サイズ
    int binarySize =
        systemSettings.getIntegerValue(
            AircraftConstants.SETTINGS_IMAGE_DATA, AircraftConstants.SETTINGS_BINARY_SIZE);
    if (Objects.nonNull(dto.getImageBinary()) && dto.getImageBinary().length > binarySize) {
      details.add(MessageFormat.format(errorMessage_checkLength, "画像", binarySize));
    }

    // 補足資料情報ファイル
    if (Objects.isNull(dto.getFileInfos())) {
      // 補足資料情報ファイルリストがnullの場合は処理なし
    } else {
      // ファイルデータ用Base64ユーティリティ生成
      Base64Utils util =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  AircraftConstants.SETTINGS_FILE_DATA,
                  AircraftConstants.SETTINGS_SUPPORT_FILE_MIME));

      // ファイルバイナリサイズ上限値を設定から取得
      int maxFileBinarySize =
          systemSettings.getIntegerValue(
              AircraftConstants.SETTINGS_FILE_DATA,
              AircraftConstants.SETTINGS_MAX_FILE_BINARY_SIZE);

      // ファイル情報分ループ
      for (int i = 0; i < dto.getFileInfos().size(); i++) {
        AircraftInfoFileInfoListElementReq file = dto.getFileInfos().get(i);
        if (file == null) {
          // もし配列にNull要素があった場合はスキップ
          continue;
        } else {
          // 要素がある場合の必須チェック
          notNull(String.valueOf(i + 1) + "番目のファイルの処理種別", file.getProcessingType());

          // 処理種別に応じたチェック
          if (file.getProcessingType() == null) {
            // 処理種別がnullの場合は追加チェックなし
          } else {
            int procType = file.getProcessingType().intValue();
            // 処理種別のコード値チェック
            {
              Integer[] values =
                  codeMaster.getIntegerArray(AircraftConstants.CODE_MASTER_PROCESSING_TYPE);
              checkRange(String.valueOf(i + 1) + "番目のファイルの処理種別", procType, values);
            }

            // 処理種別が登録の場合のチェック
            if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER == procType) {
              // 必須チェック
              notNull(String.valueOf(i + 1) + "番目のファイルの補足資料名称", file.getFileLogicalName());
              notNull(String.valueOf(i + 1) + "番目のファイルのファイル物理名", file.getFilePhysicalName());
              notNull(String.valueOf(i + 1) + "番目のファイルのファイルデータ", file.getFileData());
            }
            // 処理種別が登録または更新の場合のチェック
            if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER == procType
                || AircraftConstants.AIRCRAFT_PROCESSING_TYPE_UPDATE == procType) {

              // 補足資料名称の文字列長チェック
              if (StringUtils.hasText(file.getFileLogicalName())) {
                checkLength(
                    String.valueOf(i + 1) + "番目のファイルの補足資料名称", file.getFileLogicalName(), 100);
              }
              // ファイル物理名の文字列長チェック
              if (StringUtils.hasText(file.getFilePhysicalName())) {
                checkLength(
                    String.valueOf(i + 1) + "番目のファイルのファイル物理名", file.getFilePhysicalName(), 200);
              }

              // ファイルの内容をチェック
              if (StringUtils.hasText(file.getFileData())) {
                // エラーメッセージ用のファイル名
                String fileName =
                    file.getFilePhysicalName() + "(" + String.valueOf(i + 1) + "番目のファイル)";
                if (!util.checkMimeType(file.getFileData())) {
                  // MIMEタイプチェックエラー
                  details.add(MessageFormat.format(errorMessage_checkBase64_MIME, fileName));
                } else {
                  // サイズチェック
                  byte[] fileBinary = file.getFileBinary();
                  if (Objects.nonNull(fileBinary) && fileBinary.length > maxFileBinarySize) {
                    details.add(
                        MessageFormat.format(
                            errorMessage_checkLength, fileName, maxFileBinarySize));
                  } else {
                    // チェックエラーなし
                  }
                }
              } else {
                // 処理なし(必須チェックエラーのみ)
              }
            }
            // 処理種別が更新または削除の場合のチェック
            if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_UPDATE == procType
                || AircraftConstants.AIRCRAFT_PROCESSING_TYPE_DELETE == procType) {
              // 必須チェック
              notNull(String.valueOf(i + 1) + "番目のファイルの補足資料ID", file.getFileId());

              // UUID形式チェック
              checkUUID(String.valueOf(i + 1) + "番目のファイルの補足資料ID", file.getFileId());
            }
          }
        }
      }
    }
    // ペイロード添付ファイル
    if (Objects.isNull(dto.getPayloadInfos())) {
      // ペイロード添付ファイルリストがnullの場合は処理なし
    } else {
      // 画像データ用Base64ユーティリティ生成
      Base64Utils utilImage =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  AircraftConstants.SETTINGS_IMAGE_DATA,
                  AircraftConstants.SETTINGS_SUPPORT_FORMAT));

      // ファイルバイナリサイズ上限値を設定から取得
      int maxImageBinarySize =
          systemSettings.getIntegerValue(
              AircraftConstants.SETTINGS_IMAGE_DATA, AircraftConstants.SETTINGS_BINARY_SIZE);

      // ファイルデータ用Base64ユーティリティ生成
      Base64Utils utilFile =
          new Base64Utils(
              systemSettings.getStringValueArray(
                  AircraftConstants.SETTINGS_FILE_DATA,
                  AircraftConstants.SETTINGS_SUPPORT_FILE_MIME));

      // ファイルバイナリサイズ上限値を設定から取得
      int maxFileBinarySize =
          systemSettings.getIntegerValue(
              AircraftConstants.SETTINGS_FILE_DATA,
              AircraftConstants.SETTINGS_MAX_FILE_BINARY_SIZE);

      // ファイル情報分ループ
      for (int i = 0; i < dto.getPayloadInfos().size(); i++) {
        AircraftInfoPayloadInfoListElementReq payload = dto.getPayloadInfos().get(i);
        if (payload == null) {
          // もし配列にNull要素があった場合はスキップ
          continue;
        } else {
          // 要素がある場合の必須チェック
          notNull(String.valueOf(i + 1) + "番目のペイロード情報の処理種別", payload.getProcessingType());

          // 処理種別に応じたチェック
          if (payload.getProcessingType() == null) {
            // 処理種別がnullの場合は追加チェックなし
          } else {
            int procType = payload.getProcessingType().intValue();
            // 処理種別のコード値チェック
            {
              Integer[] values =
                  codeMaster.getIntegerArray(AircraftConstants.CODE_MASTER_PROCESSING_TYPE);
              checkRange(String.valueOf(i + 1) + "番目のペイロード情報の処理種別", procType, values);
            }
            // 処理種別が登更新の場合のチェック
            if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER == procType
                || AircraftConstants.AIRCRAFT_PROCESSING_TYPE_UPDATE == procType) {
              // ペイロード名の文字列チェック
              if (StringUtils.hasText(payload.getPayloadName())) {
                checkLength(
                    String.valueOf(i + 1) + "番目のペイロード情報のペイロード名", payload.getPayloadName(), 100);
              }
              // ペイロード詳細テキストの文字列チェック
              if (StringUtils.hasText(payload.getPayloadDetailText())) {
                checkLength(
                    String.valueOf(i + 1) + "番目のペイロード情報のペイロード詳細テキスト",
                    payload.getPayloadDetailText(),
                    1000);
              }
              // ファイル物理名の文字列チェック
              if (StringUtils.hasText(payload.getFilePhysicalName())) {
                checkLength(
                    String.valueOf(i + 1) + "番目のペイロード情報のファイル物理名",
                    payload.getFilePhysicalName(),
                    200);
              }
              // 画像の内容をチェック
              if (StringUtils.hasText(payload.getImageData())) {
                if (!utilImage.checkSubtype(payload.getImageData())) {
                  // Subtypeチェックエラー
                  details.add(String.valueOf(i + 1) + "番目のペイロード情報の" + errorMessage_checkBase64);
                } else {
                  // サイズチェック
                  byte[] imgaeBinary = payload.getImageBinary();
                  if (Objects.nonNull(imgaeBinary) && imgaeBinary.length > maxImageBinarySize) {
                    details.add(
                        MessageFormat.format(
                            errorMessage_checkLength,
                            String.valueOf(i + 1) + "番目のペイロード情報の画像",
                            maxImageBinarySize));
                  } else {
                    // チェックエラーなし
                  }
                }
              } else {
                // 処理なし(必須チェックエラーのみ)
              }
              // ファイルの内容をチェック
              if (StringUtils.hasText(payload.getFileData())) {
                // 必須チェック
                notNull(
                    String.valueOf(i + 1) + "番目のペイロード情報のファイル物理名", payload.getFilePhysicalName());
                // エラーメッセージ用のファイル名
                String fileName =
                    payload.getFilePhysicalName()
                        + "("
                        + String.valueOf(i + 1)
                        + "番目のペイロード情報のファイル)";
                if (!utilFile.checkMimeType(payload.getFileData())) {
                  // MIMEタイプチェックエラー
                  details.add(MessageFormat.format(errorMessage_checkBase64_MIME, fileName));
                } else {
                  // サイズチェック
                  byte[] fileBinary = payload.getFileBinary();
                  if (Objects.nonNull(fileBinary) && fileBinary.length > maxFileBinarySize) {
                    details.add(
                        MessageFormat.format(
                            errorMessage_checkLength, fileName, maxFileBinarySize));
                  } else {
                    // チェックエラーなし
                  }
                }
              } else {
                // 処理なし(必須チェックエラーのみ)
              }
              if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_REGISTER == procType) {
                // 必須チェック
                notNull(String.valueOf(i + 1) + "番目のペイロード情報のペイロード名", payload.getPayloadName());
              } else {
                // 処理なし
              }
            }
            // 処理種別が更新または削除の場合のチェック
            if (AircraftConstants.AIRCRAFT_PROCESSING_TYPE_UPDATE == procType
                || AircraftConstants.AIRCRAFT_PROCESSING_TYPE_DELETE == procType) {
              // 必須チェック
              notNull(String.valueOf(i + 1) + "番目のペイロード情報のペイロードID", payload.getPayloadId());

              // UUID形式チェック
              checkUUID(String.valueOf(i + 1) + "番目のペイロード情報のペイロードID", payload.getPayloadId());
            }
          }
        }
      }
    }
  }

  /**
   * 更新時必須チェック
   *
   * @param dto 機体情報更新APIのリクエストDTO
   */
  private void validateForUpdateRequired(AircraftInfoRequestDto dto) {

    // 機体ID
    notNull("機体ID", dto.getAircraftId());
  }

  /**
   * 機体情報一覧入力チェック
   *
   * @param dto 機体情報一覧APIのリクエストDTO
   */
  private void validateForSearchList(AircraftInfoSearchListRequestDto dto) {
    // 機体名 24文字
    checkLength("機体名", dto.getAircraftName(), 24);

    // 製造メーカー 200文字
    checkLength("製造メーカー", dto.getManufacturer(), 200);

    // 型式番号 200文字
    checkLength("型式番号", dto.getModelNumber(), 200);

    // 機種名 200文字
    checkLength("機種名", dto.getModelName(), 200);

    // 製造番号 20文字
    checkLength("製造番号", dto.getManufacturingNumber(), 20);

    // 機体の種別
    {
      String str = dto.getAircraftType();
      if (StringUtils.hasText(str)) {
        for (String s : str.split(",")) {
          checkValue("機体の種別", s, Pattern.compile("[\\d]+"));
          checkRangeInteger(
              "機体の種別",
              s,
              AircraftConstants.AIRCRAFT_TYPE_AIRPLANE,
              AircraftConstants.AIRCRAFT_TYPE_AIRSHIP);
        }
      }
    }

    // 機体認証の有無　true/false
    {
      String str = dto.getCertification();
      if (StringUtils.hasText(str)) {
        if (!"true".equals(str) && !"false".equals(str)) {
          checkValue("機体認証の有無", str, Pattern.compile("[\\d]+"));
        }
      }
    }

    // DIPS登録記号 12文字
    checkLength("DIPS登録記号", dto.getDipsRegistrationCode(), 12);

    // 機体所有種別
    {
      String str = dto.getOwnerType();
      if (StringUtils.hasText(str)) {
        for (String s : str.split(",")) {
          checkValue("機体所有種別", s, Pattern.compile("[\\d]+"));
          checkRangeInteger(
              "機体所有種別",
              s,
              AircraftConstants.AIRCRAFT_OWNER_TYPE_BUSINESS,
              AircraftConstants.AIRCRAFT_OWNER_TYPE_RENTAL);
        }
      }
    }

    // 所有者ID
    checkUUID("所有者ID", dto.getOwnerId());

    // 矩形範囲(1つでもあれば4つとも必須)
    if (Objects.nonNull(dto.getMinLat())
        || Objects.nonNull(dto.getMinLon())
        || Objects.nonNull(dto.getMaxLat())
        || Objects.nonNull(dto.getMaxLon())) {
      // 最小緯度（南側）
      // -90.000 ~ 90.000
      notNull("最小緯度（南側）", dto.getMinLat());
      checkRange("最小緯度（南側）", dto.getMinLat(), -90d, 90d);

      // 最小経度（西側）
      // -180.000 ~ 180.000
      notNull("最小経度（西側）", dto.getMinLon());
      checkRange("最小経度（西側）", dto.getMinLon(), -180d, 180d);

      // 最大緯度（北側）
      // -90.000 ~ 90.000
      notNull("最大緯度（北側）", dto.getMaxLat());
      checkRange("最大緯度（北側）", dto.getMaxLat(), -90d, 90d);

      // 最大経度（東側）
      // -180.000 ~ 180.000
      notNull("最大経度（東側）", dto.getMaxLon());
      checkRange("最大経度（東側）", dto.getMaxLon(), -180d, 180d);
    }

    // ペイロード情報要否　true/false
    {
      String str = dto.getIsRequiredPayloadInfo();
      if (StringUtils.hasText(str)) {
        if (!"true".equals(str) && !"false".equals(str)) {
          checkValue("ペイロード情報要否", str, Pattern.compile("[\\d]+"));
        }
      }
    }

    // 料金情報要否フラグ　true/false
    {
      String str = dto.getIsRequiredPriceInfo();
      if (StringUtils.hasText(str)) {
        if (!"true".equals(str) && !"false".equals(str)) {
          checkValue("料金情報要否", str, Pattern.compile("[\\d]+"));
        }
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
  }

  /**
   * 機体情報モデル検索入力チェック
   *
   * @param dto 機体情報モデル検索APIのリクエストDTO
   */
  private void validateForModelSearchList(AircraftInfoModelSearchRequestDto dto) {
    // モデル情報リスト必須チェック
    if (dto.getModelInfos() == null || dto.getModelInfos().isEmpty()) {
      details.add("モデル情報リストに値が設定されていません。");
    } else {
      int index = 0;
      for (AircraftInfoModelInfoListElementReq elem : dto.getModelInfos()) {
        String prefix = (index + 1) + "番目のモデル情報";
        notNull(prefix, elem);
        if (elem != null) {
          // 必須チェック
          notNull(prefix + "の製造メーカー", elem.getManufacturer());
          notNull(prefix + "の型式番号", elem.getModelNumber());
          // 入力チェック
          checkLength(prefix + "の製造メーカー", elem.getManufacturer(), 200);
          checkLength(prefix + "の型式番号", elem.getModelNumber(), 200);
        }
        ++index;
      }
    }

    // ペイロード情報要否
    {
      String str = dto.getIsRequiredPayloadInfo();
      if (StringUtils.hasText(str)) {
        if (!"true".equals(str) && !"false".equals(str)) {
          checkValue("ペイロード情報要否", str, Pattern.compile("[\\d]+"));
        }
      }
    }

    // 料金情報要否
    {
      String str = dto.getIsRequiredPriceInfo();
      if (StringUtils.hasText(str)) {
        if (!"true".equals(str) && !"false".equals(str)) {
          checkValue("料金情報要否", str, Pattern.compile("[\\d]+"));
        }
      }
    }
  }

  /**
   * 機体情報削除APIのパラメータチェック
   *
   * @param dto 機体情報削除要求DTO
   */
  public void validateForDelete(AircraftInfoDeleteRequestDto dto) {
    notNull("オペレータID", dto.getOperatorId());

    validate();
  }

  /**
   * 補足資料情報ファイルダウンロードAPIのパラメータチェック
   *
   * @param aircraftId 機体ID
   * @param fileId 補足資料ID
   */
  public void validateForDownloadFile(String aircraftId, String fileId) {

    // 機体ID
    notNull("機体ID", aircraftId);
    checkUUID("機体ID", aircraftId);

    // 補足資料ID
    notNull("補足資料ID", fileId);
    checkUUID("補足資料ID", fileId);

    validate();
  }

  /**
   * ペイロード添付ファイルダウンロードAPIのパラメータチェック
   *
   * @param payloadId ペイロードID
   */
  public void validateForDownloadPayloadFile(String payloadId) {

    // ペイロードID
    notNull("ペイロードID", payloadId);
    checkUUID("ペイロードID", payloadId);

    validate();
  }
}
