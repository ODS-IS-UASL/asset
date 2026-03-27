package com.hitachi.droneroute.config.filter;

import com.hitachi.droneroute.cmn.constants.CommonConstants;
import com.hitachi.droneroute.cmn.exception.AuthorizationException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** バッチAPI用API-Key認証フィルタークラス */
@Component
@RequiredArgsConstructor
public class BatchApiKeyAuthFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchApiKeyAuthFilter.class);

  private final SystemSettings systemSettings;
  private final RequestMatcher batchApiMatcher;

  @Override
  /**
   * バッチAPI以外のリクエストはこのフィルタをスキップするように制御
   *
   * @param request HTTPリクエスト
   * @return バッチAPI以外のリクエストの場合はtrueを返す
   */
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    // バッチAPI以外はこのフィルタをスキップ（BatchApiKeyAuthFilterはバッチAPI専用）
    return !batchApiMatcher.matches(request);
  }

  @Override
  /**
   * リクエストのASSET-API-Keyヘッダーを検査し、正しいAPIキーであればSecurityContextにバッチ用のダミーユーザー情報を設定する。
   * APIキーが不正な場合はAuthorizationExceptionをスローする。
   *
   * @param request HTTPリクエスト
   * @param response HTTPレスポンス
   * @param filterChain フィルタチェーン
   * @throws AuthorizationException ASSET-API-Keyが不正な場合にスローされる認可エラー例外
   * @throws ServletException フィルタ処理中にサーブレット例外が発生した場合にスローされる例外
   * @throws IOException フィルタ処理中に入出力例外が発生した場合にスローされる例外
   */
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

    // このメソッドはバッチAPIのみが来る前提（shouldNotFilterで制御済み）
    String requestApiKey = request.getHeader(CommonConstants.ASSET_API_KEY_HEADER_NAME);
    String validApiKeyHash =
        systemSettings.getString(
            CommonConstants.BATCH_SETTINGS, CommonConstants.SETTINGS_ASSET_API_KEY);

    if (requestApiKey != null && validApiKeyHash != null) {
      try {
        // リクエストのAPI-KeyをSHA-256でハッシュ化
        MessageDigest digest = MessageDigest.getInstance(CommonConstants.HASH_ALGORITHM_SHA256);
        byte[] requestHash = digest.digest(requestApiKey.getBytes(StandardCharsets.UTF_8));
        String requestHashHex = HexFormat.of().formatHex(requestHash);

        // ハッシュ値を比較
        if (requestHashHex.equals(validApiKeyHash)) {
          setSecurityContextForBatch();
          filterChain.doFilter(request, response);
          return;
        }
      } catch (NoSuchAlgorithmException e) {
        LOGGER.error("ASSET-API-Keyのハッシュ変換処理で例外が発生しました", e);
        throw new AuthorizationException("認可エラー。ASSET-API-Key認証処理に失敗しました。");
      }
    }

    LOGGER.warn("ASSET-API-Key認証に失敗しました");
    throw new AuthorizationException("認可エラー。ASSET-API-Keyが不正です。");
  }

  /** SecurityContextにバッチ用のダミーユーザー情報を設定する。 */
  private void setSecurityContextForBatch() {
    UserInfoDto batchUserInfo = new UserInfoDto();
    batchUserInfo.setDummyUserFlag(true);
    PreAuthenticatedAuthenticationToken authToken =
        new PreAuthenticatedAuthenticationToken(batchUserInfo, null, List.of());
    authToken.setAuthenticated(true);
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }
}
