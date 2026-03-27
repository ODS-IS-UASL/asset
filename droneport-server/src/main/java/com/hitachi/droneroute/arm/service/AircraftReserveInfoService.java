package com.hitachi.droneroute.arm.service;

import com.hitachi.droneroute.arm.dto.AircraftReserveInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoResponseDto;
import com.hitachi.droneroute.config.dto.UserInfoDto;

/** 機体予約情報サービスインタフェースクラス */
public interface AircraftReserveInfoService {
  /**
   * 機体予約情報登録
   *
   * @param request 予約するデータ
   * @param userInfo ユーザ情報
   * @return AircraftReserveInfoResponseDto
   */
  AircraftReserveInfoResponseDto postData(
      AircraftReserveInfoRequestDto request, UserInfoDto userInfo);

  /**
   * 機体予約情報更新
   *
   * @param request 予約するデータ
   * @param userInfo ユーザ情報
   * @return AircraftReserveInfoResponseDto
   */
  AircraftReserveInfoResponseDto putData(
      AircraftReserveInfoRequestDto request, UserInfoDto userInfo);

  /**
   * 機体予約情報削除
   *
   * @param aircraftReservationId 機体予約ID
   * @param aircraftReservationIdFlag 機体予約IDフラグ
   * @param userInfo ユーザ情報
   */
  void deleteData(
      String aircraftReservationId, Boolean aircraftReservationIdFlag, UserInfoDto userInfo);

  /**
   * 機体予約情報一覧取得
   *
   * @param request 取得する機体予約情報
   * @return AircraftReserveInfoListResponseDto
   */
  AircraftReserveInfoListResponseDto getList(AircraftReserveInfoListRequestDto request);

  /**
   * 機体予約情報詳細取得
   *
   * @param aircraftReservationId 機体予約ID
   * @return AircraftReserveInfoDetailResponseDto
   */
  AircraftReserveInfoDetailResponseDto getDetail(String aircraftReservationId);
}
