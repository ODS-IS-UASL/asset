package com.hitachi.droneroute.prm.constants;

/** 料金情報に関する定数クラス */
public class PriceInfoConstants {

  /** リソース種別：離着陸場 */
  public static final int RESOURCE_TYPE_PORT = 20;

  /** リソース種別：機体 */
  public static final int RESOURCE_TYPE_AIRCRAFT = 30;

  /** 料金タイプ：時間(秒)課金 */
  public static final int PRICE_TYPE_SECOND = 1;

  /** 料金タイプ：時間(分)課金 */
  public static final int PRICE_TYPE_MINUTE = 2;

  /** 料金タイプ：時間(時)課金 */
  public static final int PRICE_TYPE_HOUR = 3;

  /** 料金タイプ：時間(日)課金 */
  public static final int PRICE_TYPE_DAY = 4;

  /** 料金タイプ：時間(週)課金 */
  public static final int PRICE_TYPE_WEEK = 5;

  /** 料金タイプ：時間(月)課金 */
  public static final int PRICE_TYPE_MONTH = 6;

  /** 料金タイプ：時間(年)課金 */
  public static final int PRICE_TYPE_YEAR = 7;

  /** 処理種別：登録 */
  public static final int PROCESS_TYPE_REGIST = 1;

  /** 処理種別：更新 */
  public static final int PROCESS_TYPE_UPDATE = 2;

  /** 処理種別：削除 */
  public static final int PROCESS_TYPE_DELETE = 3;

  /** コード定義名:処理種別 */
  public static final String CODE_MASTER_PROCESSING_TYPE = "processing-type";

  /** コード定義名:リソース種別 */
  public static final String CODE_MASTER_RESOURCE_TYPE = "price-info-resource-type";

  /** コード定義名:料金タイプ */
  public static final String CODE_MASTER_PRICE_TYPE = "price-info-price-type";

  /** システム設定名:料金情報一覧取得デフォルト設定カテゴリ */
  public static final String SETTINGS_PRICE_INFOLIST_DEFAULT = "priceInfoListDefault";
}
