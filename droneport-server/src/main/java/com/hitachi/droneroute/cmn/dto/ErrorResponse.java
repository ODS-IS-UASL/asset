package com.hitachi.droneroute.cmn.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** エラー発生時のレスポンスボディ */
@Getter
@AllArgsConstructor
public class ErrorResponse {

  /** エラー詳細 */
  private String errorDetail;
}
