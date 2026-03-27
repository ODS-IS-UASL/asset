package com.hitachi.droneroute.dpm.service;

import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateResponseDto;

/** 離着陸場予約情報サービスインタフェース */
public interface DronePortReserveInfoService {

  /**
   * 離着陸場予約情報登録
   *
   * @param dto 離着陸場予約情報登録更新要求
   * @param userInfo 認可ユーザー情報
   * @return 離着陸場予約情報登録更新応答
   */
  DronePortReserveInfoRegisterListResponseDto register(
      DronePortReserveInfoRegisterListRequestDto dto, UserInfoDto userInfo);

  /**
   * 離着陸場予約情報更新
   *
   * @param dto 離着陸場予約情報登録更新要求
   * @param userInfo 認可ユーザー情報
   * @return 離着陸場予約情報登録更新応答
   */
  DronePortReserveInfoUpdateResponseDto update(
      DronePortReserveInfoUpdateRequestDto dto, UserInfoDto userInfo);

  /**
   * 離着陸場予約情報削除
   *
   * @param ReservationId 一括予約IDまたは離着陸場予約ID
   * @param dronePortReservationIdFlag 離着陸場予約ID使用フラグ
   * @param userInfo 認可ユーザー情報
   */
  void delete(String ReservationId, Boolean dronePortReservationIdFlag, UserInfoDto userInfo);

  /**
   * 離着陸場予約情報一覧取得
   *
   * @param dto 離着陸場予約情報一覧取得要求
   * @return 離着陸場予約情報一覧取得応答
   */
  DronePortReserveInfoListResponseDto getList(DronePortReserveInfoListRequestDto dto);

  /**
   * 離着陸場予約情報詳細取得
   *
   * @param dronePortReservationId 離着陸場予約ID
   * @return 離着陸場予約情報詳細取得応答
   */
  DronePortReserveInfoDetailResponseDto getDetail(String dronePortReservationId);
}
