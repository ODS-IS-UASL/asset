package com.hitachi.droneroute.dpm.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.hitachi.droneroute.cmn.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *  ドローンポート情報エンティティクラス
 * @author Hiroshi Toyoda
 *
 */
@Setter
@Getter
@Table(name = "droneport_info")
@Entity
@NamedEntityGraph(
		name = "droneport_info_join",
		includeAllAttributes = true
		)
public class DronePortInfoEntity extends CommonEntity {

	/**
	 * ドローンポートID
	 */
	@Id
	@Column(name = "droneport_id")
	private String dronePortId;
	
	/**
	 * ドローンポート名
	 */
	@Column(name = "droneport_name")
	private String dronePortName;
	
	/**
	 * 設置場所住所
	 */
	@Column(name = "address")
	private String address;
	
	/**
	 * 製造メーカー
	 */
	@Column(name = "manufacturer")
	private String manufacturer;
	
	/**
	 * 製造番号
	 */
	@Column(name = "serial_number")
	private String serialNumber;
	
	/**
	 * ポート形状
	 */
	@Column(name = "port_type")
	private Integer portType;
	
	/**
	 * VISドローンポート事業者ID
	 */
	@Column(name = "vis_droneport_company_id")
	private String visDronePortCompanyId;

	/**
	 * 緯度(設置位置)
	 */
	@Column(name = "lat")
	private Double lat;
	
	/**
	 * 経度(設置位置)
	 */
	@Column(name = "lon")
	private Double lon;
	
	/**
	 * 着陸面対地高度
	 */
	@Column(name = "alt")
	private Double alt;
	
	/**
	 * 対応機体
	 */
	@Column(name = "support_drone_type")
	private String supportDroneType;
	
	/**
	 * 画像フォーマット
	 */
	@Column(name = "image_format")
	private String imageFormat;
	
	/**
	 * 画像
	 */
	@Column(name = "image_data", length = 2097152)
	private byte[] imageBinary;
	
//	/**
//	 * オペレータID
//	 */
//	@Column(name ="operator_id")
//	private String operatorId;
//	
//	/**
//	 * 更新者ID
//	 */
//	@Column(name = "update_user_id")
//	private String updateUserId;
//	
//	/**
//	 * 登録日時
//	 */
//	@Column(name = "create_time")
//	private Timestamp createTime;
//	
//	/**
//	 * 更新日時
//	 */
//	@Column(name = "update_time")
//	private Timestamp updateTime;
//	
//	/**
//	 * 削除フラグ
//	 */
//	@Column(name = "delete_flag")
//	private Boolean deleteFlag;
//
    /**
     * 対応するドローンポート情報
     */
	@OneToOne
	@JoinColumn(name = "droneport_id", referencedColumnName = "droneport_id", insertable=false, updatable=false)
	@Fetch(FetchMode.JOIN)
    private DronePortStatusEntity dronePortStatusEntity;
}
