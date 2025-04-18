package com.hitachi.droneroute.arm.service;

import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListResponseDto;

/**
 *  機体情報サービスインタフェースクラス
 * @author Ikkan Suzuki
 *
 */
public interface AircraftInfoService {
    /**
     * 機体情報登録
     * 
     * @param request 登録するデータ
     * @return AircraftInfoResponseDto
     */
    AircraftInfoResponseDto postData(AircraftInfoRequestDto request);
    
    /**
     * 機体情報更新
     * 
     * @param request 更新するデータ
     * @return AircraftInfoResponseDto
     */
    AircraftInfoResponseDto putData(AircraftInfoRequestDto request);
    
    /**
     * 機体情報削除
     * 
     * @param aircraftId 削除する機体ID
     * @param dto 機体情報削除要求
     */
    void deleteData(String aircraftId, AircraftInfoDeleteRequestDto dto);
    
    /**
     * 機体情報一覧取得
     * 
     * @param request 取得する機体情報
     * @return AircraftInfoSearchListResponseDto
     */
    
    AircraftInfoSearchListResponseDto getList(AircraftInfoSearchListRequestDto request);
    
    /**
     * 機体情報詳細取得
     * 
     * @param aircraftId 取得する機体ID
     * @return AircraftInfoDetailResponseDto
     */
    AircraftInfoDetailResponseDto getDetail(String aircraftId);
    
    /**
     * 機体情報画像情報変換
     * @param request
     */
    void decodeBinary(AircraftInfoRequestDto request);
}
