package com.hitachi.droneroute.arm.service;

import com.hitachi.droneroute.arm.dto.AircraftReserveInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoDetailResponseDto;

/**
 *  機体予約情報サービスインタフェースクラス
 * @author Ikkan Suzuki
 *
 */
public interface AircraftReserveInfoService {
    /**
     * 機体予約情報登録
     * 
     * @param request 予約するデータ
     * @return AircraftReserveInfoResponseDto
     */
    AircraftReserveInfoResponseDto postData(AircraftReserveInfoRequestDto request);
    
    /**
     * 機体予約情報更新
     * 
     * @param request 予約するデータ
     * @return AircraftReserveInfoResponseDto
     */
    AircraftReserveInfoResponseDto putData(AircraftReserveInfoRequestDto request);
    
    /**
     * 機体予約情報削除
     * 
     * @param aircraftRevservationId 機体予約ID
     * @param dto 機体予約情報削除要求
     */
    void deleteData(String aircraftRevservationId, AircraftInfoDeleteRequestDto dto);
    
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
     * @param aircraftRevservationId 機体予約ID
     * @return AircraftReserveInfoDetailResponseDto
     */
    AircraftReserveInfoDetailResponseDto getDetail(String aircraftRevservationId);
    
}
