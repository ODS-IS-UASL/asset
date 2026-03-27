package com.hitachi.droneroute.cmn.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

/** JWTトークンからクレームを抽出するユーティリティクラス。 */
public class JwtTokenUtil {

  /**
   * Authorizationヘッダー（Bearer ...）からJWTの指定されたクレームを抽出する。
   *
   * @param authorizationHeader Authorizationヘッダー値（Bearer ...）
   * @param claimName 取得したいクレーム名
   * @return クレームの値、取得できない場合はnull
   */
  public static String extractClaimFromJwt(String authorizationHeader, String claimName) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      return null;
    }
    String token = authorizationHeader.substring(7);
    String[] parts = token.split("\\.");
    if (parts.length != 3) {
      return null;
    }
    try {
      String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
      ObjectMapper mapper = new ObjectMapper();
      JsonNode node = mapper.readTree(payload);
      if (!node.has(claimName)) {
        return null;
      }
      String value = node.get(claimName).asText();
      return (!value.isEmpty()) ? value : null;
    } catch (Exception e) {
      return null;
    }
  }
}
