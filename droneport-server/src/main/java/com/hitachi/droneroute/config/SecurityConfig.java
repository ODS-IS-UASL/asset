package com.hitachi.droneroute.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.constants.CommonConstants;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.filter.BatchApiKeyAuthFilter;
import com.hitachi.droneroute.config.filter.RequestValidationFilter;
import com.hitachi.droneroute.config.filter.RoleAuthorizationFilter;
import com.hitachi.droneroute.config.handler.SecurityExceptionHandler;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;

/** Securityコンフィグレーションクラス. */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

  @Bean
  public ForwardedHeaderFilter forwardedHeaderFilter() {
    return new ForwardedHeaderFilter();
  }

  @Bean
  public RequestValidationFilter requestValidationFilter(ObjectMapper objectMapper) {
    return new RequestValidationFilter(objectMapper);
  }

  /**
   * バッチAPI判定用RequestMatcher.
   *
   * <p>バッチAPI用のパスパターンに一致するかを判定するRequestMatcherを生成します。
   * BatchApiKeyAuthFilterとRoleAuthorizationFilterで共通使用されます。
   *
   * @param systemSettings システム設定
   * @return バッチAPI判定用RequestMatcher
   */
  @Bean
  public RequestMatcher batchApiMatcher(SystemSettings systemSettings) {
    String[] batchApiList =
        systemSettings.getStringValueArray(
            CommonConstants.BATCH_SETTINGS, CommonConstants.ASSET_BATCH_API_API_LIST);
    List<RequestMatcher> apiKeyMatcherList =
        Arrays.stream(batchApiList)
            .map(p -> (RequestMatcher) new AntPathRequestMatcher(p))
            .toList();
    return new OrRequestMatcher(apiKeyMatcherList);
  }

  /**
   * RequestValidationFilterのServlet Filter自動登録を無効化
   *
   * <p>OncePerRequestFilterを継承したフィルターは、Spring Bootにより自動的に Servlet
   * Filterとして登録されます。しかし、このフィルターはSpring Security FilterChain内で明示的に登録するため、2重登録を防ぐために自動登録を無効化します。
   *
   * @param filter RequestValidationFilter
   * @return FilterRegistrationBean（自動登録無効化設定済み）
   */
  @Bean
  public FilterRegistrationBean<RequestValidationFilter> disableRequestValidationFilterRegistration(
      RequestValidationFilter filter) {
    FilterRegistrationBean<RequestValidationFilter> registration =
        new FilterRegistrationBean<>(filter);
    registration.setEnabled(false); // Servlet Filter自動登録を無効化
    return registration;
  }

  /**
   * ヘルスチェック・監視系パス専用チェーン（フィルターなし・permitAll）
   *
   * <p>死活監視やクラウド/監視ツールからのアクセスで認証・認可・カスタムフィルターを通さず 常に200応答を返すための推奨設定。必要なパスは運用要件に応じて追加・削除してください。
   */
  @Bean
  @Order(1) // ★ ヘルスチェック用を最優先に適用
  public SecurityFilterChain healthCheckChain(HttpSecurity http) throws Exception {
    http
        // ヘルス系のみこのチェーンに適用
        .securityMatcher("/awshealth/**", "/actuator/health", "/actuator/info")
        .authorizeHttpRequests(a -> a.anyRequest().permitAll())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .requestCache(rc -> rc.disable())
        .securityContext(sc -> sc.disable());
    return http.build();
  }

  /**
   * 全般API用チェーン（認可・カスタムフィルター適用）
   *
   * @param http HttpSecurityのインスタンス
   * @param requestValidationFilter RequestValidationFilterのインスタンス
   * @param batchApiKeyAuthFilter BatchApiKeyAuthFilterのインスタンス
   * @param roleAuthorizationFilter RoleAuthorizationFilterのインスタンス
   * @param securityExceptionHandler SecurityExceptionHandlerのインスタンス
   * @return SecurityFilterChainのインスタンス
   * @throws Exception セキュリティ設定中に例外が発生した場合にスローされる例外
   */
  @Bean
  @Order(2)
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      RequestValidationFilter requestValidationFilter,
      BatchApiKeyAuthFilter batchApiKeyAuthFilter,
      RoleAuthorizationFilter roleAuthorizationFilter,
      SecurityExceptionHandler securityExceptionHandler)
      throws Exception {
    http
        // カスタムフィルターを追加（AuthorizationFilterの前に配置）
        // BatchApiKeyAuthFilter → RoleAuthorizationFilter → AuthorizationFilter の順
        .addFilterBefore(requestValidationFilter, AuthorizationFilter.class)
        .addFilterBefore(batchApiKeyAuthFilter, AuthorizationFilter.class)
        .addFilterBefore(roleAuthorizationFilter, AuthorizationFilter.class)
        // Security例外ハンドラーを設定
        .exceptionHandling(
            eh ->
                eh.accessDeniedHandler(securityExceptionHandler)
                    .authenticationEntryPoint(securityExceptionHandler))
        // HTTP Strict Transport Security
        .headers(
            headers ->
                headers.httpStrictTransportSecurity(
                    hsts -> hsts.maxAgeInSeconds(31536000).includeSubDomains(true).preload(true)))
        // Content Security Policy
        .headers(
            headers ->
                headers.contentSecurityPolicy(
                    csp ->
                        csp.policyDirectives(
                            "default-src 'self'; "
                                + "object-src 'none'; "
                                + "script-src 'self'; "
                                + "report-uri /csp-report-endpoint/")))
        // X-Content-Type-Options
        .headers(
            headers ->
                headers.xssProtection(
                    xssp -> xssp.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED)))
        // Referrer Policy
        .headers(
            headers ->
                headers.referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.SAME_ORIGIN)))
        // HSTS Missing From HTTPS Server対策
        .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()))
        // セキュリティ対策追加によるForbidden対策
        .csrf(
            csrf ->
                csrf.ignoringRequestMatchers(
                    "/asset/api/aircraft/info",
                    "/asset/api/aircraft/info/**",
                    "/asset/api/aircraft/reserve",
                    "/asset/api/aircraft/reserve/**",
                    "/asset/api/droneport/info",
                    "/asset/api/droneport/info/**",
                    "/asset/api/droneport/reserve",
                    "/asset/api/droneport/reserve/**",
                    "/asset/api/expansionDevice/info",
                    "/asset/api/expansionDevice/info/**",
                    "/asset/api/expansionDevice/reserve",
                    "/asset/api/expansionDevice/reserve/**",
                    "/asset/api/price/info",
                    "/asset/api/price/info/**"))
        .authorizeHttpRequests(ahr -> ahr.anyRequest().permitAll());

    return http.build();
  }
}
