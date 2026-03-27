package com.hitachi.droneroute.config.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

/** UserInfoDtoクラスの単体テスト */
public class UserInfoDtoTest {

  /**
   * メソッド名: getUserOperatorId<br>
   * 試験名: userOperatorIdの取得が正しく行われること<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetUserOperatorId() {
    UserInfoDto userInfo = new UserInfoDto();
    String opeId = "opeId";
    userInfo.setUserOperatorId(opeId);
    assertEquals(opeId, userInfo.getUserOperatorId());
  }

  /**
   * メソッド名: getRoles<br>
   * 試験名: rolesの取得が正しく行われること<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetRoles() {
    UserInfoDto userInfo = new UserInfoDto();
    RoleInfoDto role = new RoleInfoDto();
    role.setRoleId("10");
    role.setRoleName("roleName");
    List<RoleInfoDto> roles = new ArrayList<>();
    roles.add(role);
    userInfo.setRoles(roles);
    List<RoleInfoDto> res = userInfo.getRoles();
    assertEquals(roles.size(), res.size());
    assertEquals(roles.get(0).getRoleId(), res.get(0).getRoleId());
    assertEquals(roles.get(0).getRoleName(), res.get(0).getRoleName());
  }

  /**
   * メソッド名: getAffiliatedOperatorId<br>
   * 試験名: affiliatedOperatorIdの取得が正しく行われること<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致する<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetAffiliatedOperatorId() {
    UserInfoDto userInfo = new UserInfoDto();
    String opeId = "opeId";
    userInfo.setAffiliatedOperatorId(opeId);
    assertEquals(opeId, userInfo.getAffiliatedOperatorId());
  }

  /**
   * メソッド名: getAuthorities<br>
   * 試験名: 空のコレクションが取得されること<br>
   * 条件: getAuthoritiesを呼び出し空のコレクションが取得される<br>
   * 結果: 空のコレクションが取得される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetAuthorities() {
    UserInfoDto userInfo = new UserInfoDto();
    Collection<? extends GrantedAuthority> col = userInfo.getAuthorities();
    assertEquals(0, col.size());
  }

  /**
   * メソッド名: getUsername<br>
   * 試験名: getUsernameの取得が正しく行われること<br>
   * 条件: setUserOperatorIdで値を設定し、getUsernameで取得する。設定値と取得値を比較する<br>
   * 結果: 空のコレクションが取得される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetUserName() {
    UserInfoDto userInfo = new UserInfoDto();
    userInfo.setUserOperatorId("opeId");
    String userName = userInfo.getUsername();
    assertEquals(userInfo.getUserOperatorId(), userName);
  }

  /**
   * メソッド名: getPassword<br>
   * 試験名: getPasswordの戻り値がnullであること<br>
   * 条件: getPasswordを呼び出しnullが取得される<br>
   * 結果: nullが取得される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testGetPassword() {
    UserInfoDto userInfo = new UserInfoDto();
    String pass = userInfo.getPassword();
    assertNull(pass);
  }

  /**
   * メソッド名: isAccountNonExpired<br>
   * 試験名: isAccountNonExpiredの戻り値がtrueであること<br>
   * 条件: isAccountNonExpiredを呼び出しtrueが取得される<br>
   * 結果: trueが取得される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testIsAccountNonExpired() {
    UserInfoDto userInfo = new UserInfoDto();
    boolean res = userInfo.isAccountNonExpired();
    assertEquals(true, res);
  }

  /**
   * メソッド名: isAccountNonLocked<br>
   * 試験名: isAccountNonLockedの戻り値がtrueであること<br>
   * 条件: isAccountNonLockedを呼び出しtrueが取得される<br>
   * 結果: trueが取得される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testIsAccountNonLocked() {
    UserInfoDto userInfo = new UserInfoDto();
    boolean res = userInfo.isAccountNonLocked();
    assertEquals(true, res);
  }

  /**
   * メソッド名: isCredentialsNonExpired<br>
   * 試験名: isCredentialsNonExpiredの戻り値がtrueであること<br>
   * 条件: isCredentialsNonExpiredを呼び出しtrueが取得される<br>
   * 結果: trueが取得される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testIsCredentialsNonExpired() {
    UserInfoDto userInfo = new UserInfoDto();
    boolean res = userInfo.isCredentialsNonExpired();
    assertEquals(true, res);
  }

  /**
   * メソッド名: isEnabled<br>
   * 試験名: isEnabledの戻り値がtrueであること<br>
   * 条件: isEnabledを呼び出しtrueが取得される<br>
   * 結果: trueが取得される<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void testIsEnabled() {
    UserInfoDto userInfo = new UserInfoDto();
    boolean res = userInfo.isEnabled();
    assertEquals(true, res);
  }
}
