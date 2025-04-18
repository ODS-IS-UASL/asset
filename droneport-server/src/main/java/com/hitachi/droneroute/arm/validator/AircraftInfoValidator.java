package com.hitachi.droneroute.arm.validator;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hitachi.droneroute.arm.constants.AircraftConstants;
import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.Base64Utils;
import com.hitachi.droneroute.cmn.validator.Validator;

import lombok.RequiredArgsConstructor;

/**
 * 機体情報関連APIパラメータチェッククラス
 * @author ikkan.suzuki
 *
 */
@Component
@RequiredArgsConstructor
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AircraftInfoValidator extends Validator {
	
	private final CodeMaster codeMaster;
	
	private final SystemSettings systemSettings;

	/**
	 * 機体情報登録APIパラメータチェック
	 * @param dto
	 */
	public void validateForRegist(AircraftInfoRequestDto dto) {
		// 登録時必須チェック
		validateForRegistRequied(dto);
		
		// 入力チェック
		validateForRegistUpdate(dto);
		
		validate();
		
	}
	
	/**
	 * 機体情報更新APIパラメータチェック
	 * @param dto
	 */
	public void validateForUpdate(AircraftInfoRequestDto dto) {
		// 更新時必須チェック
		validateForUpdateRequired(dto);
		
		// 入力チェック
		validateForRegistUpdate(dto);
		
		validate();
	}

	/**
	 * 機体情報一覧APIパラメータチェック
	 * @param dto
	 */
	public void validateForGetList(AircraftInfoSearchListRequestDto dto) {
		// 入力チェック
		validateForSearchList(dto);
		
		validate();
	}
	
	/**
	 * 機体情報詳細APIパラメータチェック
	 * @param aircraftId
	 */
	public void validateForDetail(String aircraftId) {
		// 入力チェック
		notNull("機体ID", aircraftId);
		checkUUID("機体ID", aircraftId);
		
		validate();
	}
	
	/**
	 * 登録時必須チェック
	 * @param dto
	 */
	private void validateForRegistRequied(AircraftInfoRequestDto dto) {
		// オペレータID
		notNull("オペレータID", dto.getOperatorId());
		
		// 機体名
		notNull("機体名", dto.getAircraftName());
		
		// 機体所有種別
		notNull("機体所有種別", dto.getOwnerType());
		
		
	}
	
	/**
	 * 登録更新時入力チェック
	 * @param dto
	 */
	private void validateForRegistUpdate(AircraftInfoRequestDto dto) {
		// 機体ID
		checkUUID("機体ID", dto.getAircraftId());
		
		// 機体名 24文字
		checkLength("機体名", dto.getAircraftName(), 24);
		
		// 製造メーカー 24文字
		checkLength("製造メーカー", dto.getManufacturer(), 24);
		
		// 製造番号 20文字
		checkLength("製造番号", dto.getManufacturingNumber(), 20);
		
		// 機体の種別 1~6
		{
			Integer[] values = codeMaster.getIntegerArray(AircraftConstants.CODE_MASTER_AIRCRAFT_TYPE);
			checkRange("機体の種別", dto.getAircraftType(), values);
		}
		
		// 最大離陸重量 0.0~1000.000
		checkRange("最大離陸重量", dto.getMaxTakeoffWeight(), 0.0, 1000.000);
		
		// 重量 0.0~1000.000
		checkRange("重量", dto.getBodyWeight(), 0.0, 1000.0);
		
		// 最大速度 0.0~1000.000
		checkRange("最大速度", dto.getMaxFlightSpeed(), 0.0, 1000.0);
		
		// 最大飛行時間 0.0~1000.000
		checkRange("最大速度", dto.getMaxFlightTime(), 0.0, 1000.000);
		
		// 位置情報（緯度）　-90.00000～90.00000
		checkRange("位置情報（緯度）", dto.getLat(), -90.00000, 90.00000);
		
		// 位置情報（経度）　-180.00000~180.00000
		checkRange("位置情報（経度）", dto.getLon(), -180.00000, 180.00000);
		
		// DIPS登録記号 12文字
		checkLength("DIPS登録記号", dto.getDipsRegistrationCode(), 12);
		
		// 機体所有種別 1~2
		{
			Integer[] values = codeMaster.getIntegerArray(AircraftConstants.CODE_MASTER_OWNER_TYPE);
			checkRange("機体所有種別", dto.getOwnerType(), values);
		}
		
		// 所有者ID
		checkUUID("所有者ID", dto.getOwnerId());

		// 画像
		if (StringUtils.hasText(dto.getImageData())) {
			Base64Utils util = new Base64Utils(
					systemSettings.getStringValueArray(
							AircraftConstants.SETTINGS_IMAGE_DATA, 
							AircraftConstants.SETTINGS_SUPPORT_FORMAT));
			if (!util.checkSubtype(dto.getImageData())) {
				details.add(errorMessage_checkBase64);
			}
		}
		
		// 画像サイズ
		int binarySize = systemSettings.getIntegerValue(
				AircraftConstants.SETTINGS_IMAGE_DATA, AircraftConstants.SETTINGS_BINARY_SIZE);
		if (Objects.nonNull(dto.getImageBinary()) && dto.getImageBinary().length > binarySize) {
			details.add(MessageFormat.format(errorMessage_checkLength, "画像", binarySize));
		}
	}
	
	/**
	 * 更新時必須チェック
	 * @param dto
	 */
	private void validateForUpdateRequired(AircraftInfoRequestDto dto) {
		// オペレータID
		notNull("オペレータID", dto.getOperatorId());
		
		// 機体ID
		notNull("機体ID", dto.getAircraftId());
	}
	
	/**
	 * 機体情報一覧入力チェック
	 * @param dto
	 */
	private void validateForSearchList(AircraftInfoSearchListRequestDto dto) {
		// 機体名 24文字
		checkLength("機体名", dto.getAircraftName(), 24);
		
		// 製造メーカー 24文字
		checkLength("製造メーカー", dto.getManufacturer(), 24);
		
		// 製造番号 20文字
		checkLength("製造番号", dto.getManufacturingNumber(), 20);

		// 機体の種別
		// IT-0002 チェック方法修正
		{
			String str = dto.getAircraftType();
			if (StringUtils.hasText(str)) {
				for (String s : str.split(",")) {
					checkValue("機体の種別", s, Pattern.compile("[\\d]+"));
					int chkVal = Integer.valueOf(s);
					checkRange("機体の種別", chkVal, AircraftConstants.AIRCRAFT_TYPE_AIRPLANE,
							AircraftConstants.AIRCRAFT_TYPE_AIRSHIP);
				}
			}
		}

		// 機体認証の有無　true/false
		// IT-0002 チェック方法修正
		{
			String str = dto.getCertification();
			if (StringUtils.hasText(str)) {
				if(!"true".equals(str) && !"false".equals(str)) {
					checkValue("機体認証の有無", str, Pattern.compile("[\\d]+"));
				}
			}
		}
		
		// DIPS登録記号 12文字
		checkLength("DIPS登録記号", dto.getDipsRegistrationCode(), 12);
		
		// 機体所有種別
		// IT-0002 チェック方法修正
		{
			String str = dto.getOwnerType();
			if (StringUtils.hasText(str)) {
				for (String s : str.split(",")) {
					checkValue("機体所有種別", s, Pattern.compile("[\\d]+"));
					int chkVal = Integer.valueOf(s);
					checkRange("機体所有種別", chkVal, AircraftConstants.AIRCRAFT_OWNER_TYPE_BUSINESS,
							AircraftConstants.AIRCRAFT_OWNER_TYPE_RENTAL);
				}
			}
		}

		// 所有者ID
		// IT-0002 チェック方法修正
		checkUUID("所有者ID", dto.getOwnerId());

		// 矩形範囲(1つでもあれば4つとも必須)
		if (Objects.nonNull(dto.getMinLat()) 
		 || Objects.nonNull(dto.getMinLon()) 
		 || Objects.nonNull(dto.getMaxLat()) 
		 || Objects.nonNull(dto.getMaxLon())) {
			// 最小緯度（南側）
			// -90.000 ~ 90.000
			notNull("最小緯度（南側）", dto.getMinLat());
			checkRange("最小緯度（南側）", dto.getMinLat(), -90d,90d);
			
			// 最小経度（西側）
			// -180.000 ~ 180.000
			notNull("最小経度（西側）", dto.getMinLon());
			checkRange("最小経度（西側）", dto.getMinLon(), -180d,180d);
			
			// 最大緯度（北側）
			// -90.000 ~ 90.000
			notNull("最大緯度（北側）", dto.getMaxLat());
			checkRange("最大緯度（北側）", dto.getMaxLat(), -90d,90d);
			
			// 最大経度（東側）
			// -180.000 ~ 180.000
			notNull("最大経度（東側）", dto.getMaxLon());
			checkRange("最大経度（東側）", dto.getMaxLon(), -180d,180d);
		}
		
		// MVP1指摘対応　ソート順機能追加
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
		
	}
	
	/**
	 * 機体情報削除APIのパラメータチェック
	 * @param dto 機体情報削除要求DTO
	 */
	public void validateForDelete(AircraftInfoDeleteRequestDto dto) {
		notNull("オペレータID", dto.getOperatorId());
		
		validate();
	}

}
