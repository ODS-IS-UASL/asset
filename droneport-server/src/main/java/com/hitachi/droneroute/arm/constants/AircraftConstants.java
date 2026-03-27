package com.hitachi.droneroute.arm.constants;

/** 機体(予約)情報に関する定数クラス */
public class AircraftConstants {

  /** 機体の種類：飛行機 */
  public static final int AIRCRAFT_TYPE_AIRPLANE = 1;

  /** 機体の種類：回転翼航空機（ヘリコプター） */
  public static final int AIRCRAFT_TYPE_HELICOPTER = 2;

  /** 機体の種類：回転翼航空機（マルチコプター） */
  public static final int AIRCRAFT_TYPE_MULTICOPTER = 3;

  /** 機体の種類：回転翼航空機（その他） */
  public static final int AIRCRAFT_TYPE_OTHER = 4;

  /** 機体の種類：滑空機 */
  public static final int AIRCRAFT_TYPE_GLIDER = 5;

  /** 機体の種類：飛行船 */
  public static final int AIRCRAFT_TYPE_AIRSHIP = 6;

  /** 機体所有種別：事業者所有機体 */
  public static final int AIRCRAFT_OWNER_TYPE_BUSINESS = 1;

  /** 機体所有種別：レンタル機体 */
  public static final int AIRCRAFT_OWNER_TYPE_RENTAL = 2;

  /** 処理種別：登録 */
  public static final int AIRCRAFT_PROCESSING_TYPE_REGISTER = 1;

  /** 処理種別：更新 */
  public static final int AIRCRAFT_PROCESSING_TYPE_UPDATE = 2;

  /** 処理種別：削除 */
  public static final int AIRCRAFT_PROCESSING_TYPE_DELETE = 3;

  /** コード定義名:機体の種別 */
  public static final String CODE_MASTER_AIRCRAFT_TYPE = "aircraft-info-aircraft_type";

  /** コード定義名:機体所有種別 */
  public static final String CODE_MASTER_OWNER_TYPE = "aircraft-info-owner-type";

  /** コード定義名:処理種別 */
  public static final String CODE_MASTER_PROCESSING_TYPE = "processing-type";

  /** システム設定名:画像データカテゴリ */
  public static final String SETTINGS_IMAGE_DATA = "imageData";

  /** システム設定名:画像データ_サポートフォーマット */
  public static final String SETTINGS_SUPPORT_FORMAT = "supportFormat";

  /** システム設定名:画像データ_最大バイナリサイズ */
  public static final String SETTINGS_BINARY_SIZE = "maxBinarySize";

  /** システム設定名:ファイルデータカテゴリ */
  public static final String SETTINGS_FILE_DATA = "fileData";

  /** システム設定名:ファイルデータ_サポートファイルMIMEタイプ */
  public static final String SETTINGS_SUPPORT_FILE_MIME = "supportFileMime";

  /** システム設定名:ファイルデータ_ファイルバイナリ最大サイズ */
  public static final String SETTINGS_MAX_FILE_BINARY_SIZE = "maxFileBinarySize";

  /** システム設定名:ペイロード情報カテゴリ */
  public static final String SETTINGS_PAYLOAD_INFOS = "payloadInfos";

  /** システム設定名:ペイロード情報_ペイロード情報最大数 */
  public static final String SETTINGS_MAX_PAYLOAD_COUNT = "maxPayloadCount";

  /** システム設定名:補足資料情報カテゴリ */
  public static final String SETTINGS_FILE_INFOS = "fileInfos";

  /** システム設定名:補足資料情報_補足資料最大数 */
  public static final String SETTINGS_MAX_FILE_COUNT = "maxFileCount";

  /** システム設定名:航路運営者 */
  public static final String SETTING_OPERATOR_INFO = "operatorInfo";

  /** システム設定名:航路運営者_事業者ID */
  public static final String SETTING_SYSTEM_OPERATOR_ID = "systemOperatorId";
}
