package com.hitachi.droneroute.config.dto;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** ユーザ情報Dto */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class UserInfoDto implements UserDetails {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /** ユーザーID */
  private String userOperatorId;

  /** ロール情報のリスト */
  private List<RoleInfoDto> roles;

  /** オペレータID(所属事業者) */
  private String affiliatedOperatorId;

  /** API-Keyでの認可判定用フラグ */
  private boolean dummyUserFlag;

  /**
   * Spring Securityで使用する権限情報を返す。
   *
   * <p>本システムではRoleAuthorizationFilterで独自に権限を設定しているため、
   * このメソッドは使用されない。UserDetailsインターフェースの実装として形式的に定義。
   *
   * @return 空のリスト
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  /**
   * Spring Securityで使用するユーザー識別子を返す。
   *
   * @return ユーザーID（userOperatorId）
   */
  @Override
  public String getUsername() {
    return userOperatorId;
  }

  /**
   * パスワードを返す（本システムでは使用しない）。
   *
   * @return 常にnull（JWT認証ではパスワード不要）
   */
  @Override
  public String getPassword() {
    return null;
  }

  /**
   * アカウントが期限切れでないかを返す。
   *
   * @return 常にtrue（本システムではアカウント有効期限管理なし）
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * アカウントがロックされていないかを返す。
   *
   * @return 常にtrue（本システムではアカウントロック機能なし）
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * 認証情報（パスワード等）が期限切れでないかを返す。
   *
   * @return 常にtrue（本システムではパスワード有効期限管理なし）
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * アカウントが有効かを返す。
   *
   * @return 常にtrue（本システムではアカウント有効/無効管理なし）
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
