package com.hitachi.droneroute.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** ユーザ属性取得API応答のDTO. */
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAttributeApiResponseDto {
  /** 検索結果のリスト */
  private List<AttributeDto> attributeList;

  @Setter
  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class AttributeDto {
    /** 検索結果ユーザのオペレーターID */
    private String user_id;

    /** ログインID */
    private String user_login_id;

    /** オペレーター名 */
    private String operator_name;

    /** 付加情報 */
    private Attribute attribute;

    /** DIPSアカウントID */
    private String dipsAccountId;

    /** DIPSアカウント名 */
    private String dipsAccountName;

    /** 電話番号 */
    private String phone;

    /** 更新日時(yyyyMMdd-HHmmSS) */
    private String updateDatetime;
  }

  @Setter
  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Attribute {
    /** 所属事業者のオペレーターID */
    private String operatorId;

    /** ロール情報 */
    private List<Role> roles;
  }

  @Setter
  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Role {
    /** ロール */
    private String roleId;

    /** ロールの名称 */
    private String roleName;
  }
}
