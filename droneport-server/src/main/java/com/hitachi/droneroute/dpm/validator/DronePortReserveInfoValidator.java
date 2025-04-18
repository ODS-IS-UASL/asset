package com.hitachi.droneroute.dpm.validator;

import java.text.MessageFormat;
import java.util.Objects;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hitachi.droneroute.cmn.settings.CodeMaster;
import com.hitachi.droneroute.cmn.validator.Validator;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;

import lombok.RequiredArgsConstructor;

/**
 * ドローンポート予約情報関連APIのパラメータチェッククラス
 * @author Hiroshi Toyoda
 *
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class DronePortReserveInfoValidator extends Validator {

	private final CodeMaster codeMaster;
	
	/**
	 * ドローンポート予約情報登録APIのパラメータチェック
	 * @param dto ドローンポート予約情報登録更新要求
	 */
	public void validateForRegister(DronePortReserveInfoRegisterListRequestDto dto) {
		// オペレータID
		notNull("オペレータID", dto.getOperatorId());
		
		notNull("登録データ", dto.getData());
		if (Objects.nonNull(dto.getData())) {
			int index = 0;
			for (DronePortReserveInfoRegisterListRequestDto.Element elem : dto.getData()) {
				// 必須チェック
				notNull("ドローンポートID[" + index + "]", elem.getDronePortId());
				notNull("利用形態[" + index + "]", elem.getUsageType());
				notNull("予約開始日時[" + index + "]", elem.getReservationTimeFrom());
				notNull("予約終了日時[" + index + "]", elem.getReservationTimeTo());
				
				// 設定値チェック
				checkUUID("使用機体ID[" + index + "]", elem.getAircraftId());
				checkUUID("航路予約ID[" + index + "]", elem.getRouteReservationId());
				checkDateTime("予約開始日時[" + index + "]", elem.getReservationTimeFrom());
				checkDateTime("予約終了日時[" + index + "]", elem.getReservationTimeTo());
				compareDateTime(
						"予約開始日時[" + index + "]", "予約終了日時[" + index + "]", 
						elem.getReservationTimeFrom(), elem.getReservationTimeTo());
				// 利用形態
				// コードマスタから取得した値域で判定する
				checkRange("利用形態[" + index + "]", 
						elem.getUsageType(), 
						codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_RESERVE_USAGE_TYPE));
				++index;
			}
		}
		validate();
	}
	
	/**
	 * ドローンポート予約情報更新APIのパラメータチェック
	 * @param dto ドローンポート予約情報登録更新要求
	 */
	public void validateForUpdate(DronePortReserveInfoUpdateRequestDto dto) {
		// オペレータID
		notNull("オペレータID", dto.getOperatorId());
		// 必須チェック
		notNull("ドローンポート予約ID", dto.getDronePortReservationId());
		checkUUID("ドローンポート予約ID", dto.getDronePortReservationId());
		
		// ドローンポートIDは登録時必須項目なので、空文字設定不可(未設定に更新できない)
		if (Objects.nonNull(dto.getDronePortId()) && dto.getDronePortId().isBlank()) {
			details.add(MessageFormat.format(errorMessage_notNull, "ドローンポートID"));
		}
		
		// 設定値チェック
		checkUUID("使用機体ID", dto.getAircraftId());
		checkUUID("航路予約ID", dto.getRouteReservationId());
		
		// 予約開始日時は登録時必須項目なので、空文字設定不可(未設定に更新できない)
		if (Objects.nonNull(dto.getReservationTimeFrom()) && dto.getReservationTimeFrom().isBlank()) {
			details.add(MessageFormat.format(errorMessage_notNull, "予約開始日時"));
		}
		// 予約終了日時は登録時必須項目なので、空文字設定不可(未設定に更新できない)
		if (Objects.nonNull(dto.getReservationTimeTo()) && dto.getReservationTimeTo().isBlank()) {
			details.add(MessageFormat.format(errorMessage_notNull, "予約終了日時"));
		}
		checkDateTime("予約開始日時", dto.getReservationTimeFrom());
		checkDateTime("予約終了日時", dto.getReservationTimeTo());
		compareDateTime(
				"予約開始日時", "予約終了日時", 
				dto.getReservationTimeFrom(), dto.getReservationTimeTo());
		
		// 利用形態
		// コードマスタから取得した値域で判定する
		checkRange("利用形態", dto.getUsageType(), 
				codeMaster.getIntegerArray(DronePortConstants.CODE_MASTER_RESERVE_USAGE_TYPE));
		
		validate();
	}
	
	/**
	 * ドローンポート予約情報一覧取得APIのパラメータチェック
	 * @param dto
	 */
	public void validateForGetList(DronePortReserveInfoListRequestDto dto) {
		// ドローンポートID
		// チェックなし
		
		// ドローンポート名
		// 最大24文字
		checkLength("ドローンポート名", dto.getDronePortName(), 24);
		
		// 使用機体ID
		// UUID
		checkUUID("使用機体ID", dto.getAircraftId());
		
		// 航路予約ID
		// UUID
		checkUUID("航路予約ID", dto.getRouteReservationId());
		
		// 日時条件(開始)
		checkDateTime("日時条件(開始)", dto.getTimeFrom());
		
		// 日時条件(終了)
		checkDateTime("日時条件(終了)", dto.getTimeTo());
		
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
	 * ドローンポート予約情報詳細取得APIのパラメータチェック
	 * @param dronePortReserveId ドローンポート予約情報ID
	 */
	public void validateForGetDetail(String reserveId) {
		notNull("ドローンポート予約ID", reserveId);
		checkUUID("ドローンポート予約ID", reserveId);
		
		validate();
	}
}
