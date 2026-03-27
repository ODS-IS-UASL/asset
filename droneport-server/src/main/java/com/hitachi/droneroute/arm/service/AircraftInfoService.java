package com.hitachi.droneroute.arm.service;

import com.hitachi.droneroute.arm.dto.AircraftInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListResponseDto;
import com.hitachi.droneroute.arm.entity.FileInfoEntity;
import com.hitachi.droneroute.arm.entity.PayloadInfoEntity;
import com.hitachi.droneroute.config.dto.UserInfoDto;

/** 機体情報サービスインタフェースクラス */
public interface AircraftInfoService {
  /**
   * 機体情報登録
   *
   * @param request 登録するデータ
   * @param user ユーザ情報
   * @return AircraftInfoResponseDto
   */
  AircraftInfoResponseDto postData(AircraftInfoRequestDto request, UserInfoDto user);

  /**
   * 機体情報更新
   *
   * @param request 更新するデータ
   * @param user ユーザ情報
   * @return AircraftInfoResponseDto
   */
  AircraftInfoResponseDto putData(AircraftInfoRequestDto request, UserInfoDto user);

  /**
   * 機体情報削除
   *
   * @param aircraftId 削除する機体ID
   * @param user ユーザ情報
   */
  void deleteData(String aircraftId, UserInfoDto user);

  /**
   * 機体情報一覧取得
   *
   * @param request 取得する機体情報
   * @param user ユーザ情報
   * @return AircraftInfoSearchListResponseDto
   */
  AircraftInfoSearchListResponseDto getList(
      AircraftInfoSearchListRequestDto request, UserInfoDto user);

  /**
   * 機体情報詳細取得
   *
   * @param aircraftId 取得する機体ID
   * @param isRequiredPayloadInfo ペイロード情報が必要かどうか
   * @param isRequiredPriceInfo 価格情報が必要かどうか
   * @param user ユーザ情報
   * @return AircraftInfoDetailResponseDto
   */
  AircraftInfoDetailResponseDto getDetail(
      String aircraftId,
      Boolean isRequiredPayloadInfo,
      Boolean isRequiredPriceInfo,
      UserInfoDto user);

  /**
   * 補足資料情報ファイルダウンロード
   *
   * @param fileId 補足資料ID
   * @param user ユーザ情報
   * @return FileInfoEntity
   */
  FileInfoEntity downloadFile(String fileId, UserInfoDto user);

  /**
   * ペイロード添付ファイルダウンロード
   *
   * @param payloadId ペイロードID
   * @param user ユーザ情報
   * @return PayloadInfoEntity
   */
  PayloadInfoEntity downloadPayloadFile(String payloadId, UserInfoDto user);

  /**
   * 機体情報画像情報変換
   *
   * @param request 変換するデータ
   */
  void decodeBinary(AircraftInfoRequestDto request);

  /**
   * ファイルデータ変換
   *
   * @param request 変換するデータ
   */
  void decodeFileData(AircraftInfoRequestDto request);
}
