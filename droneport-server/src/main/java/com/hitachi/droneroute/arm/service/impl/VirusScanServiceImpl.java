package com.hitachi.droneroute.arm.service.impl;

import com.hitachi.droneroute.arm.service.VirusScanService;
import org.springframework.stereotype.Service;

/** ウイルススキャンサービス実装クラス */
@Service
public class VirusScanServiceImpl implements VirusScanService {
  /**
   * ウイルススキャン実行 ※OSS利用者で利用環境に応じたウイルススキャン実装を行うこと ウイルスを検知した際にはValidationErrorExceptionをスローすること
   *
   * @param binaryData スキャン対象のファイル(バイナリデータ) Throws ValidationErrorException(ウイルスチェックエラー)
   */
  @Override
  public void scanVirus(byte[] binaryData) {
    // OSS利用者で要ウイルススキャン実装

    // 例）ウイルス検知なしの場合
    return;

    // 例）ウイルス検知ありの場合
    // throw new ValidationErrorException("ウイルスが検出されました。");
  }
}
