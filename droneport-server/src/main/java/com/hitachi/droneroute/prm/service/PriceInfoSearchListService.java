package com.hitachi.droneroute.prm.service;

import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListResponseDto;

/** 料金情報検索サービスインタフェースクラス */
public interface PriceInfoSearchListService {

  /**
   * 料金情報一覧取得
   *
   * @param dto 取得する料金情報
   * @return PriceInfoSearchListResponseDto
   */
  PriceInfoSearchListResponseDto getPriceInfoList(PriceInfoSearchListRequestDto dto);
}
