package com.hitachi.droneroute.arm.service;

/** ウイルススキャンサービスインタフェースクラス */
public interface VirusScanService {
  /**
   * ウイルススキャン実行
   *
   * @param binaryData スキャン対象のファイル(バイナリデータ) Throws ValidationErrorException(ウイルスチェックエラー)
   */
  void scanVirus(byte[] binaryData);
}
