package com.hitachi.droneroute.dpm.validator;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.Base64Utils;
import com.hitachi.droneroute.cmn.validator.Validator;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;

import lombok.RequiredArgsConstructor;

/**
 * ドローンポート情報関連APIのパラメータチェッククラス
 * @author Hiroshi Toyoda
 *
 */
@Component
@RequiredArgsConstructor
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DronePortInfoValidator extends Validator {
	
	private final CodeMaster codeMaster;
	private final SystemSettings systemSettings;
	
	/**
	 * ドローンポート情報登録APIのパラメータチェック
	 * @param dto ドローンポート情報登録更新要求
	 */
	public void validateForRegist(DronePortInfoRegisterRequestDto dto) {
		validateForRegistRequired(dto);
		validateForRegistUpdate(dto);
		
		notNull("動作状況", dto.getActiveStatus());
		if (Objects.nonNull(dto.getActiveStatus())) {
			if (dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_UNAVAILABLE) { 
				// 動作状況が3:使用不可の場合は、使用不可開始日時必須
				notNull("使用不可開始日時", dto.getInactiveTimeFrom());
			} else if (dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_MAINTENANCE) {
				// 動作状況が4:メンテナンス中の場合は、使用不可開始日時,使用不可終了日時必須
				notNull("使用不可開始日時", dto.getInactiveTimeFrom());
				notNull("使用不可終了日時", dto.getInactiveTimeTo());	
			} else {
				// 使用不可開始日時、または使用不可終了日時が入力あり、動作状況入力1または2はチェックエラーとする
				if ((StringUtils.hasText(dto.getInactiveTimeFrom()) 
						|| StringUtils.hasText(dto.getInactiveTimeTo()))) {
					details.add(errorMessage_checkInactiveTime);
				}
			}
		} else {
			// 使用不可開始日時、または使用不可終了日時が入力あり、動作状況入力なしはチェックエラーとする
			if ((StringUtils.hasText(dto.getInactiveTimeFrom()) 
					|| StringUtils.hasText(dto.getInactiveTimeTo()))) {
				details.add(errorMessage_checkInactiveTime);
			}
		}
		
		validate();
	}
	
	/**
	 * ドローンポート情報登録APIのパラメータチェック(必須)
	 * @param dto ドローンポート情報登録更新要求
	 */
	private void validateForRegistRequired(DronePortInfoRegisterRequestDto dto) {
		// オペレータID
		notNull("オペレータID", dto.getOperatorId());
		
		// ドローンポート名
		notNull("ドローンポート名", dto.getDronePortName());
		
		// ドローンポートメーカーID
		notNull("ドローンポートメーカーID", dto.getDronePortManufacturerId());
		checkLength("ドローンポートメーカーID", dto.getDronePortManufacturerId(), 100);
		
		// ポート形状
		notNull("ポート形状", dto.getPortType());
		
		// 緯度
		notNull("緯度", dto.getLat());
		
		// 経度
		notNull("経度", dto.getLon());
		
		//// 着陸面対地高度
		//notNull("着陸面対地高度", dto.getAlt());
		
	}
	
	/**
	 * ドローンポート情報登録/更新APIのパラメータチェック(必須)
	 * @param dto ドローンポート情報登録更新要求
	 */
	private void validateForRegistUpdate(DronePortInfoRegisterRequestDto dto) {
		// ドローンポート名
		// 最大24文字
		checkLength("ドローンポート名", dto.getDronePortName(), 24);
		
		// 設置場所住所
		// 最大50文字
		checkLength("設置場所住所", dto.getAddress(), 50);
		
		// 製造メーカー
		// 最大24文字
		checkLength("製造メーカー", dto.getManufacturer(), 24);
		
		// 製造番号
		// 最大20文字
		checkLength("製造番号", dto.getSerialNumber(), 20);
		
		// ポート形状
		// コードマスタから取得した値域で判定する
		{
			Integer[] values = codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_PORT_TYPE);
			checkRange("ポート形状", dto.getPortType(), values);
		}
		
		// VISドローンポート事業者ID
		// 最大100文字
		checkLength("VISドローンポート事業者ID", dto.getVisDronePortCompanyId(), 100);
		
		// 格納中機体ID
		if (!(Objects.nonNull(dto.getPortType()) 
				&& dto.getPortType().equals(DronePortConstants.PORT_TYPE_DRONEPORT))) {
			// ポート形状:1(ドローンポート)以外
			if (StringUtils.hasText(dto.getStoredAircraftId())) {
				details.add(MessageFormat.format(errorMessage_storedAircraftId, dto.getPortType()));
			}
		}
		checkUUID("格納中機体ID", dto.getStoredAircraftId());
		
		// 緯度
		checkRange("緯度", dto.getLat(), -90d, 90d);
		
		// 経度
		checkRange("経度", dto.getLon(), -180d, 180d);
		
		// 着陸面対地高度
		checkRange("着陸面対地高度", dto.getAlt(), 0, 100);
		
		// 対応機体
		// 最大24文字
		checkLength("対応機体", dto.getSupportDroneType(), 24);
		
		// 動作状況
		// コードマスタから取得した値域で判定する
		{
			Integer[] values = codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_ACTIVE_STATUS);
			checkRange("動作状況", dto.getActiveStatus(), values);
		}
		
		// 使用不可開始日時
		checkDateTime("使用不可開始日時", dto.getInactiveTimeFrom());
		// 使用不可終了日時
		checkDateTime("使用不可終了日時", dto.getInactiveTimeTo());
		// 開始日時、終了日時の前後チェック
		compareDateTime("使用不可開始日時", "使用不可終了日時",dto.getInactiveTimeFrom(), dto.getInactiveTimeTo());
		
		// 画像
		if (StringUtils.hasText(dto.getImageData())) {
			Base64Utils util = new Base64Utils(
					systemSettings.getStringValueArray(
							DronePortConstants.SETTINGS_IMAGE_DATA, 
							DronePortConstants.SETTINGS_SUPPORT_FORMAT));
			// データURIの画像フォーマットをチェックする
			if (!util.checkSubtype(dto.getImageData())) {
				details.add(errorMessage_checkBase64);
			}
		}
		// 画像サイズ
		// 事前にbase64からバイナリ変換を行っていること。
		int binarySize = systemSettings.getIntegerValue(
				DronePortConstants.SETTINGS_IMAGE_DATA, DronePortConstants.SETTINGS_BINARY_SIZE);
		if (Objects.nonNull(dto.getImageBinary()) && dto.getImageBinary().length > binarySize) {
			details.add(MessageFormat.format(errorMessage_checkLength, "画像", binarySize));
		}
	}
	
	/**
	 * ドローンポート情報更新APIのパラメータチェック
	 * @param dto ドローンポート情報登録更新要求
	 */
	public void validateForUpdate(DronePortInfoRegisterRequestDto dto) {
		// オペレータID
		notNull("オペレータID", dto.getOperatorId());
		// ドローンポートID
		notNull("ドローンポートID", dto.getDronePortId());
		
		// ドローンポートメーカーID
		if (StringUtils.hasText(dto.getDronePortManufacturerId())) {
			// 更新時は入力不可
			details.add(errorMessage_dronePortManufacturer);
		}
		
		validateForRegistUpdate(dto);
		
		// ドローンポート名は登録時必須項目なので、空文字設定不可(未設定に更新できない)
		if (Objects.nonNull(dto.getDronePortName()) && dto.getDronePortName().isBlank()) {
			details.add(MessageFormat.format(errorMessage_notNull, "ドローンポート名"));
		}
		
		// 動作状況と使用不可日時をチェック
		if (Objects.nonNull(dto.getActiveStatus())) {
			if (dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_UNAVAILABLE) { 
				// 動作状況が3:使用不可の場合は、使用不可開始日時必須
				notNull("使用不可開始日時", dto.getInactiveTimeFrom());
			} else if (dto.getActiveStatus() == DronePortConstants.ACTIVE_STATUS_MAINTENANCE) {
				// 動作状況が4:メンテナンス中の場合は、使用不可開始日時,使用不可終了日時必須
				notNull("使用不可開始日時", dto.getInactiveTimeFrom());
				notNull("使用不可終了日時", dto.getInactiveTimeTo());	
			} else {
				// 使用不可開始日時、または使用不可終了日時が入力あり、動作状況入力1または2はチェックエラーとする
				if ((StringUtils.hasText(dto.getInactiveTimeFrom()) 
						|| StringUtils.hasText(dto.getInactiveTimeTo()))) {
					details.add(errorMessage_checkInactiveTime);
				}
			}
		} else {
			// 使用不可開始日時、または使用不可終了日時が入力あり、動作状況入力なしはチェックエラーとする
			if ((StringUtils.hasText(dto.getInactiveTimeFrom()) 
					|| StringUtils.hasText(dto.getInactiveTimeTo()))) {
				details.add(errorMessage_checkInactiveTime);
			}
		}
		
		validate();
	}
	
	/**
	 * ドローンポート情報一覧取得APIのパラメータチェック
	 * @param dto
	 */
	public void validateForGetList(DronePortInfoListRequestDto dto) {
		// ドローンポート名
		// 最大24文字
		checkLength("ドローンポート名", dto.getDronePortName(), 24);
		
		// 設置場所住所
		// 最大50文字
		checkLength("設置場所住所", dto.getAddress(), 50);
		
		// 製造メーカー
		// 最大24文字
		checkLength("製造メーカー", dto.getManufacturer(), 24);
		
		// 製造番号
		// 最大20文字
		checkLength("製造番号", dto.getSerialNumber(), 20);
		
		// ポート形状
		// カンマ区切りで数字(桁数は問わない)
		{
			Integer[] range = codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_PORT_TYPE);
			checkRange("ポート形状", dto.getPortType(), range);
		}
		
		// 検索位置範囲は、全て設定ありの場合に範囲チェック
		if (Objects.nonNull(dto.getMinLat()) 
				|| Objects.nonNull(dto.getMinLon()) 
				|| Objects.nonNull(dto.getMaxLat()) 
				|| Objects.nonNull(dto.getMaxLon())) {
			// 最小緯度(南側)
			// -90.00000000～90.00000000
			{
				String name = "最小緯度(南側)";
				Double value = dto.getMinLat();
				notNull(name, value);
				checkRange(name, value, -90d, 90d);
			}
			// 最小経度(西側)
			// -180.0000000～180.0000000
			{
				String name = "最小経度(西側)";
				Double value = dto.getMinLon();
				notNull(name, value);
				checkRange(name, value, -180d, 180d);
			}

			// 最大緯度(北側)
			// -90.00000000～90.00000000
			{
				String name = "最大緯度(北側)";
				Double value = dto.getMaxLat();
				notNull(name, value);
				checkRange(name, value, -90d, 90d);
			}
			
			// 最大経度(東側)
			// -180.0000000～180.0000000
			{
				String name = "最大経度(東側)";
				Double value = dto.getMaxLon();
				notNull(name, value);
				checkRange(name, value, -180d, 180d);
			}
		}
		
		// 対応機体
		// 最大24文字
		checkLength("対応機体", dto.getSupportDroneType(), 24);
		
		// 動作状況
		// カンマ区切りで数字(桁数は問わない)
		{
			Integer[] range = codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_ACTIVE_STATUS);
			checkRange("動作状況", dto.getActiveStatus(), range);
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
		
		validate();
	}
	
	/**
	 * ドローンポート情報詳細取得APIのパラメータチェック
	 * @param dronePortId ドローンポートID
	 */
	public void validateForGetDetail(String dronePortId) {
		// ドローンポートID
		// UUID
		notNull("ドローンポートID", dronePortId);
		
		validate();
	}
	
	/**
	 * ドローンポート情報削除APIのパラメータチェック
	 * @param dto ドローンポート情報削除要求DTO
	 */
	public void validateForDelete(DronePortInfoDeleteRequestDto dto) {
		notNull("オペレータID", dto.getOperatorId());
		
		validate();
	}

}
