package com.hitachi.droneroute.dpm.constants;

public class DronePortConstants {
	
	/**
	 * ドローンポート種別 : 1:自システム管理
	 */
	public static final int DRONE_PORT_TYPE_INTERNAL = 1;
	/**
	 * ドローンポート種別 : 2:VIS管理
	 */
	public static final int DRONE_PORT_TYPE_VIS = 2;
	/**
	 * ドローンポート種別 : 3:他事業者管理
	 */
	public static final int DRONE_PORT_TYPE_OTHER_COMPANY = 3;
	
	/**
	 * ポート形状 : 1:ドローンポート
	 */
	public static final int PORT_TYPE_DRONEPORT = 1;
	
	/**
	 * 動作状況 : 1:準備中
	 */
	public static final int ACTIVE_STATUS_PREPARING = 1;
	
	/**
	 * 動作状況 : 2:使用可
	 */
	public static final int ACTIVE_STATUS_AVAILABLE = 2;
	
	/**
	 * 動作状況 : 3:使用不可
	 */
	public static final int ACTIVE_STATUS_UNAVAILABLE = 3;
	
	/**
	 * 動作状況 : 4:メンテナンス中
	 */
	public static final int ACTIVE_STATUS_MAINTENANCE = 4;
	
	/**
	 * コード定義:ポート形状
	 */
	public static final String CODE_MASTER_PORT_TYPE = "droneport-info-port-type";
	
	/**
	 *　コード定義:使用用途
	 */
	public static final String CODE_MASTER_USAGE_TYPE = "droneport-info-usage-type";

	/**
	 * コード定義:ドローンポート種別
	 */
	public static final String CODE_MASTER_DRONE_PORT_TYPE = "droneport-info-drone-port-type";
	
	/**
	 * コード定義:予約利用形態
	 */
	public static final String CODE_MASTER_RESERVE_USAGE_TYPE = "droneport-reserve-usage-type";
	
	/**
	 * コード定義:予約利用形態
	 */
	public static final String CODE_MASTER_ACTIVE_STATUS = "droneport-info-active-status";
	
	/**
	 * システム設定
	 */
	/**
	 * 画像データ
	 */
	public static final String SETTINGS_IMAGE_DATA = "imageData";
	public static final String SETTINGS_SUPPORT_FORMAT = "supportFormat";
	public static final String SETTINGS_BINARY_SIZE = "maxBinarySize";
	
	/**
	 * VIS接続情報
	 */
	public static final String SETTINGS_VIS_CONNECT = "visConnectInfo";
	public static final String SETTINGS_VIS_BROKER_URL = "brokerUrl";
	public static final String SETTINGS_VIS_USERNAME = "userName";
	public static final String SETTINGS_VIS_PASSWORD = "password";
	public static final String SETTINGS_VIS_QOS = "qos";
	public static final String SETTINGS_VIS_CLIENT_ID = "clientId";
	public static final String SETTINGS_VIS_MQTT_BASE_URI = "mqttBaseUri";
	public static final String SETTINGS_VIS_TOPIC_TELEMETRY = "topicSubscribeTelemetryInfo";
	public static final String SETTINGS_VIS_TOPIC_QUERY_RESERVATION_REQ = "topicSubscribeQueryReservationRequest";
	public static final String SETTINGS_VIS_TOPIC_QUERY_RESERVATION_RES = "topicPublishQueryReservationResponse";
	public static final String SETTINGS_VIS_ENABLE_TELEMETRY = "enableSubscribeTelemetryInfo";
	public static final String SETTINGS_VIS_ENABLE_QUERY_RESERVATION_REQ = "enableSubscribeQueryReservationRequest";
	
	/**
	 * ドローンポートID
	 */
	public static final String SETTINGS_DRONEPORT_ID = "dronePortId";
	public static final String SETTINGS_DRONEPORT_ID_OPR = "dronerouteOperatorId";
	public static final String SETTINGS_DRONEPORT_ID_FORMAT = "format";
	
	/**
	 * ドローンポート予約
	 */
	public static final String SETTINGS_DRONEPORT_RESERVATION = "dronePortReservation";
	/**
	 * 予約可能ポート形状
	 */
	public static final String SETTINGS_DRONEPORT_RESERVATION_PORT_TPYE = "reservablePortType";
	
	/**
	 * 一覧取得デフォルト設定
	 */
	public static final String SETTINGS_GETLIST_DEFAULT = "getListDefault";
	public static final String SETTINGS_GETLIST_DEFAULT_PERPAGE = "perPage";
	public static final String SETTINGS_GETLIST_DEFAULT_SORT_ORDERS = "sortOrders";
	public static final String SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS = "sortColumns";
	
	/**
	 * ドローンポート情報一覧取得デフォルト設定
	 */
	public static final String SETTINGS_DRONEPORT_INFOLIST_DEFAULT = "dronePortInfoListDefault";
	/**
	 * ドローンポート予約情報一覧取得デフォルト設定
	 */
	public static final String SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT = "dronePortReserveInfoListDefault";
	/**
	 * 機体情報一覧取得デフォルト設定
	 */
	public static final String SETTINGS_AIRCRAFT_INFOLIST_DEFAULT = "aircraftInfoListDefault";
	/**
	 * 機体予約情報一覧取得デフォルト設定
	 */
	public static final String SETTINGS_AIRCRAFT_RESERVEINFOLIST_DEFAULT = "aircraftReserveInfoListDefault";
	
}
