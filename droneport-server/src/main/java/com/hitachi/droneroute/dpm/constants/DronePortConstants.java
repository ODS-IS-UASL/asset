package com.hitachi.droneroute.dpm.constants;

/** 離着陸場(予約)情報に関する定数クラス */
public class DronePortConstants {

  /** 離着陸場種別 : 1:自システム管理 */
  public static final int DRONE_PORT_TYPE_INTERNAL = 1;

  /** 離着陸場種別 : 2:VIS管理 */
  public static final int DRONE_PORT_TYPE_VIS = 2;

  /** 離着陸場種別 : 3:他事業者管理 */
  public static final int DRONE_PORT_TYPE_OTHER_COMPANY = 3;

  /** ポート形状 : 1:ドローンポート */
  public static final int PORT_TYPE_DRONEPORT = 1;

  /** 動作状況 : 1:準備中 */
  public static final int ACTIVE_STATUS_PREPARING = 1;

  /** 動作状況 : 2:使用可 */
  public static final int ACTIVE_STATUS_AVAILABLE = 2;

  /** 動作状況 : 3:使用不可 */
  public static final int ACTIVE_STATUS_UNAVAILABLE = 3;

  /** 動作状況 : 4:メンテナンス中 */
  public static final int ACTIVE_STATUS_MAINTENANCE = 4;

  /** コード定義名:ポート形状 */
  public static final String CODE_MASTER_PORT_TYPE = "droneport-info-port-type";

  /** コード定義名:使用用途 */
  public static final String CODE_MASTER_USAGE_TYPE = "droneport-info-usage-type";

  /** コード定義名:離着陸場種別 */
  public static final String CODE_MASTER_DRONE_PORT_TYPE = "droneport-info-drone-port-type";

  /** コード定義名:予約利用形態 */
  public static final String CODE_MASTER_RESERVE_USAGE_TYPE = "droneport-reserve-usage-type";

  /** コード定義名:動作状況 */
  public static final String CODE_MASTER_ACTIVE_STATUS = "droneport-info-active-status";

  /** システム設定名:画像データカテゴリ */
  public static final String SETTINGS_IMAGE_DATA = "imageData";

  /** システム設定名:画像データ_サポートフォーマット */
  public static final String SETTINGS_SUPPORT_FORMAT = "supportFormat";

  /** システム設定名:画像データ_最大バイナリサイズ */
  public static final String SETTINGS_BINARY_SIZE = "maxBinarySize";

  /** システム設定名:VIS接続情報カテゴリ */
  public static final String SETTINGS_VIS_CONNECT = "visConnectInfo";

  /** システム設定名:VIS接続情報_MTQQブローカーの接続先URL */
  public static final String SETTINGS_VIS_BROKER_URL = "brokerUrl";

  /** システム設定名:VIS接続情報_MTQQブローカーの接続ユーザー名 */
  public static final String SETTINGS_VIS_USERNAME = "userName";

  /** システム設定名:VIS接続情報_MTQQブローカーの接続パスワード */
  public static final String SETTINGS_VIS_PASSWORD = "password";

  /** システム設定名:VIS接続情報_MTQQブローカーの接続品質 */
  public static final String SETTINGS_VIS_QOS = "qos";

  /** システム設定名:VIS接続情報_MTQQブローカーの接続クライアントID */
  public static final String SETTINGS_VIS_CLIENT_ID = "clientId";

  /** システム設定名:VIS接続情報_MQTTブローカーの接続先URI */
  public static final String SETTINGS_VIS_MQTT_BASE_URI = "mqttBaseUri";

  /** システム設定名:VIS接続情報_MQTTブローカーのトピック_テレメトリ情報 */
  public static final String SETTINGS_VIS_TOPIC_TELEMETRY = "topicSubscribeTelemetryInfo";

  /** システム設定名:VIS接続情報_MQTTブローカーのトピック_予約情報問い合わせリクエスト */
  public static final String SETTINGS_VIS_TOPIC_QUERY_RESERVATION_REQ =
      "topicSubscribeQueryReservationRequest";

  /** システム設定名:VIS接続情報_MQTTブローカーのトピック_予約情報問い合わせレスポンス */
  public static final String SETTINGS_VIS_TOPIC_QUERY_RESERVATION_RES =
      "topicPublishQueryReservationResponse";

  /** システム設定名:VIS接続情報_MQTTブローカーのテレメトリ情報の有効化フラグ */
  public static final String SETTINGS_VIS_ENABLE_TELEMETRY = "enableSubscribeTelemetryInfo";

  /** システム設定名:VIS接続情報_MQTTブローカーの予約情報問い合わせリクエストの有効化フラグ */
  public static final String SETTINGS_VIS_ENABLE_QUERY_RESERVATION_REQ =
      "enableSubscribeQueryReservationRequest";

  /** システム設定名:離着陸場IDカテゴリ */
  public static final String SETTINGS_DRONEPORT_ID = "dronePortId";

  /** システム設定名:離着陸場ID_オペレーターID */
  public static final String SETTINGS_DRONEPORT_ID_OPR = "dronerouteOperatorId";

  /** システム設定名:離着陸場ID_フォーマット */
  public static final String SETTINGS_DRONEPORT_ID_FORMAT = "format";

  /** システム設定名:離着陸場予約カテゴリ */
  public static final String SETTINGS_DRONEPORT_RESERVATION = "dronePortReservation";

  /** システム設定名:離着陸場予約_予約可能ポート形状 */
  public static final String SETTINGS_DRONEPORT_RESERVATION_PORT_TPYE = "reservablePortType";

  /** システム設定名:一覧取得デフォルト設定カテゴリ */
  public static final String SETTINGS_GETLIST_DEFAULT = "getListDefault";

  /** システム設定名:一覧取得デフォルト設定_1ページあたりの件数 */
  public static final String SETTINGS_GETLIST_DEFAULT_PERPAGE = "perPage";

  /** システム設定名:一覧取得デフォルト設定_ソート順 */
  public static final String SETTINGS_GETLIST_DEFAULT_SORT_ORDERS = "sortOrders";

  /** システム設定名:一覧取得デフォルト設定_ソート列 */
  public static final String SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS = "sortColumns";

  /** システム設定名:離着陸場情報一覧取得デフォルト設定カテゴリ */
  public static final String SETTINGS_DRONEPORT_INFOLIST_DEFAULT = "dronePortInfoListDefault";

  /** システム設定名:離着陸場予約情報一覧取得デフォルト設定カテゴリ */
  public static final String SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT =
      "dronePortReserveInfoListDefault";

  /** システム設定名:機体情報一覧取得デフォルト設定カテゴリ */
  public static final String SETTINGS_AIRCRAFT_INFOLIST_DEFAULT = "aircraftInfoListDefault";

  /** システム設定名:機体予約情報一覧取得デフォルト設定カテゴリ */
  public static final String SETTINGS_AIRCRAFT_RESERVEINFOLIST_DEFAULT =
      "aircraftReserveInfoListDefault";

  /** システム設定名:オペレーター情報カテゴリ */
  public static final String SETTING_OPERATOR_INFO = "operatorInfo";

  /** システム設定名:オペレーター情報_自事業者ID */
  public static final String SETTING_SYSTEM_OPERATOR_ID = "systemOperatorId";
}
