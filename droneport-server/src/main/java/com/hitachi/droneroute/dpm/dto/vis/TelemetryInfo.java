package com.hitachi.droneroute.dpm.dto.vis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * VISから取得したドローンポートテレメトリ情報
 * @author dpls01
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class TelemetryInfo {
	/**
	 * ドローンポートID
	 */
	private String droneportId;
	
	/**
	 * IPアドレス
	 */
	private String droneportIpAddress;
	
	/**
	 * ドローンポート名
	 */
	private String droneportName;
	
	/**
	 * ドローンポートステータス
	 */
	private String droneportStatus;
	
	/**
	 * VISステータス
	 */
	private String visStatus;

	/**
	 * 緯度
	 */
	private Double droneportLat;

	/**
	 * 経度
	 */
	private Double droneportLon;

	/**
	 * 着地面対地高度
	 */
	private Double droneportAlt;

	/**
	 * 風向
	 */
	private Double windDirection;

	/**
	 * 風速
	 */
	private Double windSpeed;

	/**
	 * 最大風速時風向
	 */
	private Double maxinstWindDirection;
	
	/**
	 * 最大風速
	 */
	private Double maxinstWindSpeed;

	/**
	 * 雨量
	 */
	private Double rainfall;

	/**
	 * 気温
	 */
	private Double temp;
	
	/**
	 * 湿度
	 */
	private Double humidity;
	
	/**
	 * 気圧
	 */
	private Double pressure;
	
	/**
	 * 照度
	 */
	private Double illuminance;
	
	/**
	 * 紫外線
	 */
	private Double ultraviolet;

	/**
	 * 観測時間
	 */
	private String observationTime;

	/**
	 * 侵入検知有無
	 */
	private boolean invasionFlag;
	
	/**
	 * 検知物カテゴリ
	 */
	private String invasionCategory;
	
	/**
	 * 閾値（風速）
	 */
	private Double thresholdWindSpeed;
	
	/**
	 * 拠点ID
	 */
	private String baseId;
	
	/**
	 * 拠点住所
	 */
	private String baseAddress;
	
	/**
	 * 拠点名称
	 */
	private String baseName;
	
	/**
	 * 拠点ステータス
	 */
	private String baseStatus;

	/**
	 * ドローンポート使用可否状態
	 */
	private Integer usage;

	/**
	 * エラーコード
	 */
	private String errorCode;

	/**
	 * エラー内容
	 */
	private String errorReason;

}
