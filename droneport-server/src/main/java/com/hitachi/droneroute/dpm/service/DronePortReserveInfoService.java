package com.hitachi.droneroute.dpm.service;

import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateResponseDto;

/**
 *  ドローンポート予約情報サービスインタフェース
 * @author Hiroshi Toyoda
 *
 */
public interface DronePortReserveInfoService {
	
	/**
	 * ドローンポート予約情報登録
	 * @param dto ドローンポート予約情報登録更新要求
	 * @return ドローンポート予約情報登録更新応答
	 */
	public DronePortReserveInfoRegisterListResponseDto register(DronePortReserveInfoRegisterListRequestDto dto);

	/**
	 * ドローンポート予約情報更新
	 * @param dto ドローンポート予約情報登録更新要求
	 * @return ドローンポート予約情報登録更新応答
	 */
	public DronePortReserveInfoUpdateResponseDto update(DronePortReserveInfoUpdateRequestDto dto);
	
	/**
	 * ドローンポート予約情報削除
	 * @param dronePortReserveInfoId ドローンポート予約ID 
	 * @param dto ドローンポート予約情報削除要求
	 */
	public void delete(String dronePortReservationId, DronePortInfoDeleteRequestDto dto);
	
	/**
	 * ドローンポート予約情報一覧取得
	 * @param dto ドローンポート予約情報一覧取得要求
	 * @return ドローンポート予約情報一覧取得応答
	 */
	public DronePortReserveInfoListResponseDto getList(DronePortReserveInfoListRequestDto dto);
	
	/**
	 * ドローンポート予約情報詳細取得
	 * @param dronePortReservationId ドローンポート予約ID
	 * @return
	 */
	public DronePortReserveInfoDetailResponseDto getDetail(String dronePortReservationId);
}
