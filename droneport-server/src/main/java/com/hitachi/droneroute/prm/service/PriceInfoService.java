package com.hitachi.droneroute.prm.service;

import com.hitachi.droneroute.prm.dto.PriceInfoRequestDto;
import java.util.List;

/** 料金情報サービスインタフェースクラス */
public interface PriceInfoService {

  /**
   * 料金情報処理実行 パラメータの処理種別に応じて登録・更新・削除を実施する
   *
   * @param priceInfoList 料金情報リスト
   */
  void process(List<PriceInfoRequestDto> priceInfoList);
}
