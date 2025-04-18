package com.hitachi.droneroute.dpm.service;

import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortEnvironmentInfoResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterResponseDto;

/**
 *  ドローンポート情報サービスインタフェースクラス
 * @author Hiroshi Toyoda
 *
 */
public interface DronePortInfoService {
	
	/**
	 * ドローンポート情報登録
	 * @param dto ドローンポート情報登録更新要求
	 * @return ドローンポート情報登録更新応答
	 */
	public DronePortInfoRegisterResponseDto register(DronePortInfoRegisterRequestDto dto);

	/**
	 * ドローンポート情報更新
	 * @param dto ドローンポート情報登録更新要求
	 * @return ドローンポート情報登録更新応答
	 */
	public DronePortInfoRegisterResponseDto update(DronePortInfoRegisterRequestDto dto);
	
	/**
	 * ドローンポート情報削除
	 * @param dronePortId ドローンポートID
	 * @param dto ドローンポート情報削除要求
	 */
	public void delete(String dronePortId, DronePortInfoDeleteRequestDto dto);
	
	/**
	 * ドローンポート情報一覧取得
	 * @param dto ドローンポート情報一覧取得要求
	 * @return ドローンポート情報一覧取得応答
	 */
	public DronePortInfoListResponseDto getList(DronePortInfoListRequestDto dto);

	/**
	 * ドローンポート情報詳細取得
	 * @param dronePortId ドローンポートID
	 * @return ドローンポート情報詳細取得応答
	 */
	public DronePortInfoDetailResponseDto getDetail(String dronePortId);
	
	/**
	 * ドローンポート周辺情報取得
	 * @param dronePortId ドローンポートID
	 * @return ドローンポート周辺情報取得応答
	 */
	public DronePortEnvironmentInfoResponseDto getEnvironment(String dronePortId);
	
	public void decodeBinary(DronePortInfoRegisterRequestDto dto);
}
