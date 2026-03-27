package com.hitachi.droneroute.config.service;

import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.config.dto.UserInfoDto;

/** ユーザー情報取得サービス */
public interface UserInfoService {
  /**
   * ユーザー情報取得APIを呼び出し、ユーザー属性情報を取得する。
   *
   * @param systemAccessToken システムアクセストークン
   * @param userId ユーザーID
   * @return ユーザー情報DTO
   * @throws ServiceErrorException 必須項目不足時
   */
  UserInfoDto getUserInfo(String systemAccessToken, String userId);
}
