package com.hitachi.droneroute.config.filter;

import com.hitachi.droneroute.cmn.constants.CommonConstants;
import com.hitachi.droneroute.cmn.exception.AuthorizationException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.cmn.util.JwtTokenUtil;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.config.properties.ResourceRolesProperties;
import com.hitachi.droneroute.config.properties.ResourceRolesProperties.ResourceRoleRule;
import com.hitachi.droneroute.config.service.UserInfoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * ユーザー情報を取得し、認可を行うフィルター
 *
 * <p>このフィルターは以下の処理を行います： 1. JWTからユーザーID抽出 2. ユーザー情報取得API呼び出し 3. SecurityContextに設定 4.
 * 自事業者チェック（条件付き）
 *
 * <p>注意：ロールチェックは上位システムで実施済みのため、このFilterでは実施しない
 */
@Component
@RequiredArgsConstructor
public class RoleAuthorizationFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(RoleAuthorizationFilter.class);

  private final ResourceRolesProperties securityProperties;
  private final SystemSettings systemSettings;
  private final UserInfoService userInfoService;
  private final RequestMatcher batchApiMatcher;

  // 除外対象のパスをここに集約（必要に応じて追加）
  private final RequestMatcher skipMatcher =
      new OrRequestMatcher(
          new AntPathRequestMatcher("/awshealth/**"),
          new AntPathRequestMatcher("/actuator/health"),
          new AntPathRequestMatcher("/actuator/info"));

  @Value("${droneroute.basepath}")
  private String basePath;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    // true を返すと「このフィルタは実行しない」
    // バッチAPIはBatchApiKeyAuthFilterで認証するため、このフィルタはスキップ
    return skipMatcher.matches(request) || batchApiMatcher.matches(request);
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    // リクエストログ出力
    String message =
        String.format(
            "[%s] %4s, %s", request.getRemoteAddr(), request.getMethod(), request.getRequestURI());
    LOGGER.info(message);

    try {
      // 1. JWTからユーザーID抽出
      String authorizationHeader = request.getHeader(CommonConstants.AUTHORIZATION_HEADER_NAME);
      String userId =
          JwtTokenUtil.extractClaimFromJwt(
              authorizationHeader, CommonConstants.JWT_CLAIM_OPERATOR_ID);

      if (userId == null) {
        LOGGER.warn("AuthorizationヘッダーのアクセストークンからユーザーIDを取得できませんでした");
        throw new AuthorizationException("認可エラー。アクセストークンが不正です。");
      }

      // 2. ユーザー情報取得
      UserInfoDto userInfo = userInfoService.getUserInfo(authorizationHeader, userId);

      // 3. SecurityContextに設定
      setSecurityContext(userInfo);

      // 4. 認可チェック（自事業者チェック）
      authorizeRequest(request, userInfo);

      // アクセス許可
      filterChain.doFilter(request, response);

    } catch (AuthorizationException e) {
      // AuthorizationException（403）は再throwしてSecurityExceptionHandler.handle()で処理
      throw e;
    } catch (Exception e) {
      // ServiceErrorException含む全例外をInternalAuthenticationServiceExceptionでラップして500で返却
      // SecurityExceptionHandler.commence()でcauseを判定してログレベル分け
      throw new InternalAuthenticationServiceException(e.getMessage(), e);
    }
  }

  /**
   * SecurityContextにユーザー情報を設定.
   *
   * @param userInfo ユーザー情報
   */
  private void setSecurityContext(UserInfoDto userInfo) {
    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(userInfo, null, List.of());
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  /**
   * 認可チェック（自事業者チェック）.
   *
   * <p>ルールが存在しない場合はチェックをスキップ。 ルールが存在し、requireSystemOperator=trueの場合のみ自事業者チェックを実施。
   *
   * @param request HTTPリクエスト
   * @param userInfo ユーザー情報
   * @throws AuthorizationException 自事業者チェック失敗時
   */
  private void authorizeRequest(HttpServletRequest request, UserInfoDto userInfo) {
    String requestPath = request.getRequestURI();
    String requestMethod = request.getMethod();

    // ルール検索
    ResourceRoleRule matchedRule = findMatchingRule(requestPath, requestMethod);
    if (matchedRule == null) {
      // 存在しない場合は自システム事業者チェック処理をスキップ
      return;
    }

    // 自システム事業者チェック
    if (Boolean.TRUE.equals(matchedRule.getRequireSystemOperator())) {
      String systemOperatorId =
          systemSettings.getString(
              CommonConstants.SETTINGS_KEY_OPERATOR_INFO,
              CommonConstants.SETTINGS_KEY_SYSTEM_OPERATOR_ID);
      String affiliatedOperatorId = userInfo.getAffiliatedOperatorId();

      if (!StringUtils.hasText(affiliatedOperatorId)
          || !StringUtils.hasText(systemOperatorId)
          || !affiliatedOperatorId.trim().equals(systemOperatorId.trim())) {
        throw new AuthorizationException("自システムの事業者のみアクセス可能です");
      }
    }
  }

  /**
   * リクエストパスとメソッドに一致するルールを検索.
   *
   * @param requestPath リクエストパス
   * @param requestMethod HTTPメソッド
   * @return 一致したルール、見つからない場合はnull
   */
  private ResourceRoleRule findMatchingRule(String requestPath, String requestMethod) {
    List<ResourceRoleRule> rules = securityProperties.getCheckTargets();
    for (ResourceRoleRule rule : rules) {
      boolean pathMatches = pathMatches(requestPath, rule.getPath());
      boolean methodMatches = rule.getMethod().equalsIgnoreCase(requestMethod);
      if (pathMatches && methodMatches) {
        return rule;
      }
    }
    return null;
  }

  /**
   * パスがルールにマッチするかチェック.
   *
   * @param requestPath リクエストパス
   * @param rulePath ルールのパス
   * @return マッチする場合true
   */
  private boolean pathMatches(String requestPath, String rulePath) {
    // requestPathからbasepathを除去
    String pathWithoutBase = requestPath.substring(basePath.length());
    // AntPathMatcherによるパス判定
    AntPathMatcher matcher = new AntPathMatcher();
    return matcher.match(rulePath, pathWithoutBase);
  }
}
