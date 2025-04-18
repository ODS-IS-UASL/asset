package com.hitachi.droneroute.dpm.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *  VISテレメトリ情報エンティティクラス
 * @author Hiroshi Toyoda
 *
 */
@Setter
@Getter
@Table(name = "vis_telemetry_info")
@Entity
public class VisTelemetryInfoEntity {
	/**
	 * ドローンポートID
	 */
	@Id
	@Column(name = "droneport_id")
	private String droneportId;
	
	/**
	 * IPアドレス
	 */
	@Column(name = "droneport_ip_address")
	private String droneportIpAddress;
	
	/**
	 * ドローンポート名
	 */
	@Column(name = "droneport_name")
	private String droneportName;
	
	/**
	 * ドローンポートステータス
	 */
	@Column(name = "droneport_status")
	private String droneportStatus;
	
	/**
	 * VISステータス
	 */
	@Column(name = "vis_status")
	private String visStatus;

	/**
	 * 緯度
	 */
	@Column(name = "droneport_lat")
	private Double droneportLat;

	/**
	 * 経度
	 */
	@Column(name = "droneport_lon")
	private Double droneportLon;

	/**
	 * 着地面対地高度
	 */
	@Column(name = "droneport_alt")
	private Double droneportAlt;

	/**
	 * 風向
	 */
	@Column(name = "wind_direction")
	private Double windDirection;

	/**
	 * 風速
	 */
	@Column(name = "wind_speed")
	private Double windSpeed;

	/**
	 * 最大風速時風向
	 */
	@Column(name = "maxinst_wind_direction")
	private Double maxinstWindDirection;
	
	/**
	 * 最大風速
	 */
	@Column(name = "maxinst_wind_speed")
	private Double maxinstWindSpeed;

	/**
	 * 雨量
	 */
	@Column(name = "rainfall")
	private Double rainfall;

	/**
	 * 気温
	 */
	@Column(name = "temp")
	private Double temp;
	
	/**
	 * 湿度
	 */
	@Column(name = "humidity")
	private Double humidity;
	
	/**
	 * 気圧
	 */
	@Column(name = "pressure")
	private Double pressure;
	
	/**
	 * 照度
	 */
	@Column(name = "illuminance")
	private Double illuminance;
	
	/**
	 * 紫外線
	 */
	@Column(name = "ultraviolet")
	private Double ultraviolet;

	/**
	 * 観測時間
	 */
	@Column(name = "observation_time")
	private Timestamp observationTime;

	/**
	 * 侵入検知有無
	 */
	@Column(name = "invasion_flag")
	private Boolean invasionFlag;
	
	/**
	 * 検知物カテゴリ
	 */
	@Column(name = "invasion_category")
	private String invasionCategory;
	
	/**
	 * 閾値（風速）
	 */
	@Column(name = "threshold_wind_speed")
	private Double thresholdWindSpeed;
	
	/**
	 * 拠点ID
	 */
	@Column(name = "base_id")
	private String baseId;
	
	/**
	 * 拠点住所
	 */
	@Column(name = "base_address")
	private String baseAddress;
	
	/**
	 * 拠点名称
	 */
	@Column(name = "base_name")
	private String baseName;
	
	/**
	 * 拠点ステータス
	 */
	@Column(name = "base_status")
	private String baseStatus;

	/**
	 * ドローンポート状態
	 */
	@Column(name = "usage")
	private Integer usage;

	/**
	 * エラーコード
	 */
	@Column(name = "error_code")
	private String errorCode;

	/**
	 * エラー内容
	 */
	@Column(name = "error_reason")
	private String errorReason;

}
