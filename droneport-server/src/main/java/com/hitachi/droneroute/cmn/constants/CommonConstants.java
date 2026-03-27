package com.hitachi.droneroute.cmn.constants;

/** 共通定数クラス */
public class CommonConstants {

  /** Authorizationヘッダー名 */
  public static final String AUTHORIZATION_HEADER_NAME = "Authorization";

  /** APIキー(asset)のヘッダー名 */
  public static final String ASSET_API_KEY_HEADER_NAME = "ASSET-API-Key";

  /** JWTクレーム名:オペレーターID */
  public static final String JWT_CLAIM_OPERATOR_ID = "operator_id";

  /** ハッシュアルゴリズム:SHA-256 */
  public static final String HASH_ALGORITHM_SHA256 = "SHA-256";

  /** システム設定名:ロールIDカテゴリ */
  public static final String ROLE_ID = "roleId";

  /** システム設定名:ロールID_航路運営者 */
  public static final String ROUTE_OPERATOR = "routeOperator";

  /** システム設定名:ロールID_航路運営者_責任者 */
  public static final String ROUTE_OPERATOR_MANAGER = "routeOperatorManager";

  /** システム設定名:ロールID_航路運営者_担当者 */
  public static final String ROUTE_OPERATOR_ASSIGNEE = "routeOperatorAssignee";

  /** システム設定名:ロールID_運航事業者 */
  public static final String ROUTE_PROVIDER = "routeProvider";

  /** システム設定名:ロールID_運航事業者_責任者 */
  public static final String ROUTE_PROVIDER_MANAGER = "routeProviderManager";

  /** システム設定名:ロールID_運航事業者_担当者 */
  public static final String ROUTE_PROVIDER_ASSIGNEE = "routeProviderAssignee";

  /** システム設定名:ロールID_関係者 */
  public static final String ROUTE_OBSERVER = "routeObserver";

  /** システム設定名:オペレーター情報カテゴリ */
  public static final String SETTINGS_KEY_OPERATOR_INFO = "operatorInfo";

  /** システム設定名:オペレーター情報_システムオペレーターID */
  public static final String SETTINGS_KEY_SYSTEM_OPERATOR_ID = "systemOperatorId";

  /** システム設定名:バッチ設定カテゴリ */
  public static final String BATCH_SETTINGS = "batch";

  /** システム設定名:バッチ設定_APIキー(asset) */
  public static final String SETTINGS_ASSET_API_KEY = "assetApiKey";

  /** システム設定名:バッチ設定_APIリスト */
  public static final String ASSET_BATCH_API_API_LIST = "assetBatchApiList";
}
