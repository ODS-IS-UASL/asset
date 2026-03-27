package com.hitachi.droneroute.prm.controller;

import com.hitachi.droneroute.cmn.resolver.QueryStringArgs;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListRequestDto;
import com.hitachi.droneroute.prm.dto.PriceInfoSearchListResponseDto;
import com.hitachi.droneroute.prm.service.PriceInfoSearchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** リソース料金管理系APIのコントローラ */
@RequiredArgsConstructor
@RestController
@RequestMapping("${droneroute.basepath}/price/info")
public class PriceInfoController {

  // 料金情報APIサービスクラス
  private final PriceInfoSearchListService service;

  /**
   * 料金情報一覧
   *
   * @param dto 料金情報一覧取得要求DTO
   * @return 正常終了レスポンス
   */
  @GetMapping("/resourcePriceList")
  public ResponseEntity<PriceInfoSearchListResponseDto> getList(
      @QueryStringArgs PriceInfoSearchListRequestDto dto) {

    // サービス呼び出し
    PriceInfoSearchListResponseDto responseDto = service.getPriceInfoList(dto);

    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
