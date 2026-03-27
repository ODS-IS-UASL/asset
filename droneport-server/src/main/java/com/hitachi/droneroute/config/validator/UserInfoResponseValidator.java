package com.hitachi.droneroute.config.validator;

import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.validator.Validator;
import com.hitachi.droneroute.config.dto.UserAttributeApiResponseDto;
import com.hitachi.droneroute.config.dto.UserAttributeApiResponseDto.AttributeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** ユーザー属性取得APIレスポンスバリデータ */
@Component
public class UserInfoResponseValidator extends Validator {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoResponseValidator.class);

  /**
   * ユーザ属性取得APIの正常レスポンスを検証する。
   *
   * @param responseDto ユーザ情報APIのレスポンスDTO
   * @throws ServiceErrorException レスポンスが不正な場合
   */
  public void validateResponse(UserAttributeApiResponseDto responseDto) {
    details.clear(); // 再利用のためクリア

    if (responseDto == null
        || responseDto.getAttributeList() == null
        || responseDto.getAttributeList().isEmpty()) {
      LOGGER.warn("ユーザ属性取得結果が空です");
      throw new ServiceErrorException("ユーザ情報の取得に失敗しました");
    }

    AttributeDto attr = responseDto.getAttributeList().get(0);

    notNull("ユーザ属性取得結果のユーザのオペレーターID", attr.getUser_id());
    checkUUID("ユーザ属性取得結果のユーザのオペレーターID", attr.getUser_id());
    notNull("ユーザ属性取得結果の付加情報", attr.getAttribute());
    if (attr.getAttribute() != null) {
      checkUUID("ユーザ属性取得結果の所属事業者のオペレーターID", attr.getAttribute().getOperatorId());
      notNull("ユーザ属性取得結果のロール情報", attr.getAttribute().getRoles());
      if (attr.getAttribute().getRoles() != null) {
        if (attr.getAttribute().getRoles().isEmpty()) {
          details.add("ユーザ属性取得結果のロール情報が空です");
        } else {
          for (int i = 0; i < attr.getAttribute().getRoles().size(); i++) {
            notNull(
                "ユーザ属性取得結果のロール[" + i + "]のロールID",
                attr.getAttribute().getRoles().get(i).getRoleId());
          }
        }
      }
    }

    validate();
  }

  /** 検証結果を確認し、エラーがあればServiceErrorExceptionをスローする。 */
  @Override
  protected void validate() {
    if (!details.isEmpty()) {
      // エラー詳細をログ出力
      details.forEach(LOGGER::warn);
      throw new ServiceErrorException("ユーザ情報の取得に失敗しました");
    }
  }
}
