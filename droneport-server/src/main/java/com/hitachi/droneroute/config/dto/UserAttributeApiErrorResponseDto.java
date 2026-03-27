package com.hitachi.droneroute.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** ユーザ属性取得APIエラー応答のDTO. */
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAttributeApiErrorResponseDto {
  /** エラーコード */
  private String code;

  /** エラーメッセージ */
  private String errorMessage;
}
