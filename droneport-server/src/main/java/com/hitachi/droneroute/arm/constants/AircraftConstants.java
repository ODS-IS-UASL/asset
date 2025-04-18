package com.hitachi.droneroute.arm.constants;

public class AircraftConstants {

	/**
	 * 機体の種類：飛行機
	 */
	public static final int AIRCRAFT_TYPE_AIRPLANE = 1;
	
	/**
	 * 機体の種類：回転翼航空機（ヘリコプター）
	 */
	public static final int AIRCRAFT_TYPE_HELICOPTER = 2;
	
	/**
	 * 機体の種類：回転翼航空機（マルチコプター）
	 */
	public static final int AIRCRAFT_TYPE_MULTICOPTER = 3;
	
	/**
	 * 機体の種類：回転翼航空機（その他）
	 */
	public static final int AIRCRAFT_TYPE_OTHER = 4;
	
	/**
	 * 機体の種類：滑空機
	 */
	public static final int AIRCRAFT_TYPE_GLIDER = 5;
	
	/**
	 * 機体の種類：飛行船
	 */
	public static final int AIRCRAFT_TYPE_AIRSHIP = 6;
	
	/**
	 * 機体所有種別：事業者所有機体
	 */
	public static final int AIRCRAFT_OWNER_TYPE_BUSINESS = 1;
	
	/**
	 * 機体所有種別：レンタル機体
	 */
	public static final int AIRCRAFT_OWNER_TYPE_RENTAL = 2;
	
	/**
	 * コード定義:機体の種別
	 */
	public static final String CODE_MASTER_AIRCRAFT_TYPE = "aircraft-info-aircraft_type";
	
	/**
	 * コード定義:機体所有種別
	 */
	public static final String CODE_MASTER_OWNER_TYPE = "aircraft-info-owner-type";
	
	/**
	 * システム設定
	 */
	public static final String SETTINGS_IMAGE_DATA = "imageData";
	public static final String SETTINGS_SUPPORT_FORMAT = "supportFormat";
	public static final String SETTINGS_BINARY_SIZE = "maxBinarySize";

}
