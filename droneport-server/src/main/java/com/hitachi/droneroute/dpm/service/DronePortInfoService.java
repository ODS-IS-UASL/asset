package com.hitachi.droneroute.dpm.service;

import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.dto.DronePortEnvironmentInfoResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterResponseDto;

/** 離着陸場情報サービスインタフェースクラス */
public interface DronePortInfoService {

  /**
   * 離着陸場情報登録
   *
   * @param dto 離着陸場情報登録更新要求
   * @param user 認可ユーザー情報
   * @return 離着陸場情報登録更新応答
   */
  DronePortInfoRegisterResponseDto register(DronePortInfoRegisterRequestDto dto, UserInfoDto user);

  /**
   * 離着陸場情報更新
   *
   * @param dto 離着陸場情報登録更新要求
   * @param user 認可ユーザー情報
   * @return 離着陸場情報登録更新応答
   */
  DronePortInfoRegisterResponseDto update(DronePortInfoRegisterRequestDto dto, UserInfoDto user);

  /**
   * 離着陸場情報削除
   *
   * @param dronePortId 離着陸場ID
   * @param user 認可ユーザー情報
   */
  void delete(String dronePortId, UserInfoDto user);

  /**
   * 離着陸場情報一覧取得
   *
   * @param dto 離着陸場情報一覧取得要求
   * @param user 認可ユーザー情報
   * @return 離着陸場情報一覧取得応答
   */
  DronePortInfoListResponseDto getList(DronePortInfoListRequestDto dto, UserInfoDto user);

  /**
   * 離着陸場情報詳細取得
   *
   * @param dronePortId 離着陸場ID
   * @param isRequiredPriceInfo 料金情報の取得要否
   * @param user 認可ユーザー情報
   * @return 離着陸場情報詳細取得応答
   */
  DronePortInfoDetailResponseDto getDetail(
      String dronePortId, Boolean isRequiredPriceInfo, UserInfoDto user);

  /**
   * 離着陸場周辺情報取得
   *
   * @param dronePortId 離着陸場ID
   * @param user 認可ユーザー情報
   * @return 離着陸場周辺情報取得応答
   */
  DronePortEnvironmentInfoResponseDto getEnvironment(String dronePortId, UserInfoDto user);

  void decodeBinary(DronePortInfoRegisterRequestDto dto);
}
