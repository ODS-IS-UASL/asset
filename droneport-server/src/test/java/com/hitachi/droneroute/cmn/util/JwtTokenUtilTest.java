package com.hitachi.droneroute.cmn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** JwtTokenUtilの単体テスト */
class JwtTokenUtilTest {

  private static final String TEST_AUTHORIZATION_HEADER =
      "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTc2OTA0NzIwMCwiYXV0aF90aW1lIjoxNzYwNjg3OTY1LCJqdGkiOiJvbnJ0YWM6YzI5NDE5MmYtMGUwNC02MjNhLTQ2OTktYjk4ZGNiMGM2Y2MyIiwiaXNzIjoiaHR0cHM6Ly9kdHMtb2RzLWF1dGgtaWQuZGV2LmR0cy1vZHMuY29tL3JlYWxtcy90ZXN0b2RzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjRlMGRmMjI2LTAyMmItNDVhZC05N2Q1LWY2MGMxNWIzZmFmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6IkF1dGhDb2RlQ2xpZW50MDAxIiwic2lkIjoiNTBiODFlMGQtN2FjYy00Zjk1LWFlNDktNjJmMDY1YTczNmYyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy10ZXN0b2RzIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm9wZXJhdG9yX2lkIjoiMTIzZTQ1NjctZTg5Yi0xMmQzLWE0NTYtNDI2NjE0MTc0MDAwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiNHFueXk1bTRqZCIsImVtYWlsIjoibG9naW5fdXNlckBleGFtcGxlLmNvbSJ9.LWgpEcNzxIz4yQl0wejPZrycnt6jAEqiZrVVjeZxPPpgb8Q0aZMHMoeq7hBmcYROgVbhUNekzgTSG7zDeYJJe7IeAUBjNB53lT6u_6Wef9J1sAgokCIZDhHXoJUpyYSuoWxvM46QOkkAHh-wmZrEvr2VoeETrMkl5h-lVI5XscrL07gDJ6JvCY-ZvE_R-Bso54AXSMg-dvY2pReXGSBg3zlA8SU8Jqq84bajG4KJZZD6xdsVKPO-pFdq4HpJ77-H_RAaPH6XLXCGpG34sDil0cXxYuBEKVM_-T7wlKCVgY1a9qN7jgPeG7vGtunwCeN_J4fthkmYC8bZoN9EWAaiSQ";

  // operator_idが空文字のJWTトークン（ペイロード: {"operator_id":"", ...}）
  private static final String TEST_AUTHORIZATION_HEADER_EMPTY_OPERATOR_ID =
      "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTc2OTA0NzIwMCwiYXV0aF90aW1lIjoxNzYwNjg3OTY1LCJqdGkiOiJvbnJ0YWM6YzI5NDE5MmYtMGUwNC02MjNhLTQ2OTktYjk4ZGNiMGM2Y2MyIiwiaXNzIjoiaHR0cHM6Ly9kdHMtb2RzLWF1dGgtaWQuZGV2LmR0cy1vZHMuY29tL3JlYWxtcy90ZXN0b2RzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjRlMGRmMjI2LTAyMmItNDVhZC05N2Q1LWY2MGMxNWIzZmFmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6IkF1dGhDb2RlQ2xpZW50MDAxIiwic2lkIjoiNTBiODFlMGQtN2FjYy00Zjk1LWFlNDktNjJmMDY1YTczNmYyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy10ZXN0b2RzIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm9wZXJhdG9yX2lkIjoiIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiNHFueXk1bTRqZCIsImVtYWlsIjoibG9naW5fdXNlckBleGFtcGxlLmNvbSJ9.FiwNadanhzBJhghR1wivcBCaFmGljlpnUBdzpNuONnBMocOpuDiOoTw-xuN748O3L3Am6aJnZIpvKdHRLqYbpD15WlvMlFx9V8_mrgppIe33mUFmLN4gMG90_3kBoYgPV0lr4ZgXRKfy8Na6inqmMCJLBf0rmSJk1SDlrdngSBjwpmhMES8mY6-LL2cUVKpnTCrqdmHs6Wlu-TXJfijw90nHNqXRmJqebt0zd1ZKREELr0nEr4whfvYnDTo-2Ii4720vtcRRHrvAflBGGi_SmPmzaPjumMabR5mstxp-M_YrLhbCPozoHqM_xLQ-yljwqanicMkc_ff94FqvyyF8zw";

  // Bearerで始まらないAuthorizationヘッダー（正常なトークンの先頭をBasicに変更）
  private static final String TEST_AUTHORIZATION_HEADER_NOT_BEARER =
      "Basic eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTc2OTA0NzIwMCwiYXV0aF90aW1lIjoxNzYwNjg3OTY1LCJqdGkiOiJvbnJ0YWM6YzI5NDE5MmYtMGUwNC02MjNhLTQ2OTktYjk4ZGNiMGM2Y2MyIiwiaXNzIjoiaHR0cHM6Ly9kdHMtb2RzLWF1dGgtaWQuZGV2LmR0cy1vZHMuY29tL3JlYWxtcy90ZXN0b2RzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjRlMGRmMjI2LTAyMmItNDVhZC05N2Q1LWY2MGMxNWIzZmFmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6IkF1dGhDb2RlQ2xpZW50MDAxIiwic2lkIjoiNTBiODFlMGQtN2FjYy00Zjk1LWFlNDktNjJmMDY1YTczNmYyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy10ZXN0b2RzIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm9wZXJhdG9yX2lkIjoiMTIzZTQ1NjctZTg5Yi0xMmQzLWE0NTYtNDI2NjE0MTc0MDAwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiNHFueXk1bTRqZCIsImVtYWlsIjoibG9naW5fdXNlckBleGFtcGxlLmNvbSJ9.LWgpEcNzxIz4yQl0wejPZrycnt6jAEqiZrVVjeZxPPpgb8Q0aZMHMoeq7hBmcYROgVbhUNekzgTSG7zDeYJJe7IeAUBjNB53lT6u_6Wef9J1sAgokCIZDhHXoJUpyYSuoWxvM46QOkkAHh-wmZrEvr2VoeETrMkl5h-lVI5XscrL07gDJ6JvCY-ZvE_R-Bso54AXSMg-dvY2pReXGSBg3zlA8SU8Jqq84bajG4KJZZD6xdsVKPO-pFdq4HpJ77-H_RAaPH6XLXCGpG34sDil0cXxYuBEKVM_-T7wlKCVgY1a9qN7jgPeG7vGtunwCeN_J4fthkmYC8bZoN9EWAaiSQ";

  // ピリオドが1つのJWTトークン（署名部分を削除）
  private static final String TEST_AUTHORIZATION_HEADER_ONE_PERIOD =
      "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTc2OTA0NzIwMCwiYXV0aF90aW1lIjoxNzYwNjg3OTY1LCJqdGkiOiJvbnJ0YWM6YzI5NDE5MmYtMGUwNC02MjNhLTQ2OTktYjk4ZGNiMGM2Y2MyIiwiaXNzIjoiaHR0cHM6Ly9kdHMtb2RzLWF1dGgtaWQuZGV2LmR0cy1vZHMuY29tL3JlYWxtcy90ZXN0b2RzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjRlMGRmMjI2LTAyMmItNDVhZC05N2Q1LWY2MGMxNWIzZmFmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6IkF1dGhDb2RlQ2xpZW50MDAxIiwic2lkIjoiNTBiODFlMGQtN2FjYy00Zjk1LWFlNDktNjJmMDY1YTczNmYyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy10ZXN0b2RzIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm9wZXJhdG9yX2lkIjoiMTIzZTQ1NjctZTg5Yi0xMmQzLWE0NTYtNDI2NjE0MTc0MDAwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiNHFueXk1bTRqZCIsImVtYWlsIjoibG9naW5fdXNlckBleGFtcGxlLmNvbSJ9";

  // ピリオドがないJWTトークン（全てのピリオドを削除）
  private static final String TEST_AUTHORIZATION_HEADER_NO_PERIOD =
      "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9eyJleHAiOjk5OTk5OTk5OTksImlhdCI6MTc2OTA0NzIwMCwiYXV0aF90aW1lIjoxNzYwNjg3OTY1LCJqdGkiOiJvbnJ0YWM6YzI5NDE5MmYtMGUwNC02MjNhLTQ2OTktYjk4ZGNiMGM2Y2MyIiwiaXNzIjoiaHR0cHM6Ly9kdHMtb2RzLWF1dGgtaWQuZGV2LmR0cy1vZHMuY29tL3JlYWxtcy90ZXN0b2RzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjRlMGRmMjI2LTAyMmItNDVhZC05N2Q1LWY2MGMxNWIzZmFmMyIsInR5cCI6IkJlYXJlciIsImF6cCI6IkF1dGhDb2RlQ2xpZW50MDAxIiwic2lkIjoiNTBiODFlMGQtN2FjYy00Zjk1LWFlNDktNjJmMDY1YTczNmYyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIvKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy10ZXN0b2RzIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm9wZXJhdG9yX2lkIjoiMTIzZTQ1NjctZTg5Yi0xMmQzLWE0NTYtNDI2NjE0MTc0MDAwIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiNHFueXk1bTRqZCIsImVtYWlsIjoibG9naW5fdXNlckBleGFtcGxlLmNvbSJ9LWdwRWNOenh6SXo0eVFsMHdlalBaeXJjbnQ2akFFcWlacllWamVaeFBQcGdiOFEwYVpNSE1vZXE3aEJtY1lST2dWYmhVTmVremdUU0c3ekRlWUpKZTdJZUFVQmpOQjUzbFQ2dV82V2VmOUoxc0Fnb2tDSVpEaEhYb0pVcHlZU3VvV3h2TTQ2UU9ra0FIaC13bVpyRXZyMlZvZUVUck1rbDVoLWxWSTVYc2NyTDA3Z0RKNkp2Q1ktWnZFX1ItQnNvNTRBWFNNZy1kdlkycFJlWEdTQmczemxBOFNVOEpxcTg0YmFqRzRLSlpaRDZ4ZHNWS1BPLXBGZHE0SHBKNzctSF9SQWFQSDZYTFhDR3BHMzRzRGlsMGNYeFl1QkVLVk1fLVQ3d2xLQ1ZnWTFhOXFON2pnUGVHN3ZHdHVud0NlTl9KNGZ0aGttWUM4YlpvTjlFV0FhaVNR";

  // 不正なBase64形式のJWTトークン（Base64で使用できない文字「@#$」を含む）
  private static final String TEST_AUTHORIZATION_HEADER_INVALID_BASE64 =
      "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjk5OTk5@#$OTk5LCJpYXQiOjE3NjkwNDcyMDAsImF1dGhfdGltZSI6MTc2MDY4Nzk2NSwianRpIjoib25ydGFjOmMyOTQxOTJmLTBlMDQtNjIzYS00Njk5LWI5OGRjYjBjNmNjMiIsImlzcyI6Imh0dHBzOi8vZHRzLW9kcy1hdXRoLWlkLmRldi5kdHMtb2RzLmNvbS9yZWFsbXMvdGVzdG9kcyIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI0ZTBkZjIyNi0wMjJiLTQ1YWQtOTdkNS1mNjBjMTViM2ZhZjMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJBdXRoQ29kZUNsaWVudDAwMSIsInNpZCI6IjUwYjgxZTBkLTdhY2MtNGY5NS1hZTQ5LTYyZjA2NWE3MzZmMiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtdGVzdG9kcyIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJvcGVyYXRvcl9pZCI6IjEyM2U0NTY3LWU4OWItMTJkMy1hNDU2LTQyNjYxNDE3NDAwMCIsInByZWZlcnJlZF91c2VybmFtZSI6IjRxbnl5NW00amQiLCJlbWFpbCI6ImxvZ2luX3VzZXJAZXhhbXBsZS5jb20ifQ.LWdwRWNOenh6SXo0eVFsMHdlalBaeXJjbnQ2akFFcWlacllWamVaeFBQcGdiOFEwYVpNSE1vZXE3aEJtY1lST2dWYmhVTmVremdUU0c3ekRlWUpKZTdJZUFVQmpOQjUzbFQ2dV82V2VmOUoxc0Fnb2tDSVpEaEhYb0pVcHlZU3VvV3h2TTQ2UU9ra0FIaC13bVpyRXZyMlZvZUVUck1rbDVoLWxWSTVYc2NyTDA3Z0RKNkp2Q1ktWnZFX1ItQnNvNTRBWFNNZy1kdlkycFJlWEdTQmczemxBOFNVOEpxcTg0YmFqRzRLSlpaRDZ4ZHNWS1BPLXBGZHE0SHBKNzctSF9SQWFQSDZYTFhDR3BHMzRzRGlsMGNYeFl1QkVLVk1fLVQ3d2xLQ1ZnWTFhOXFON2pnUGVHN3ZHdHVud0NlTl9KNGZ0aGttWUM4YlpvTjlFV0FhaVNR";

  private static final String TEST_CLAIM_NAME_OPERATOR_ID = "operator_id";
  private static final String TEST_CLAIM_NAME_NOT_EXISTS = "test";
  private static final String TEST_EXPECTED_OPERATOR_ID = "123e4567-e89b-12d3-a456-426614174000";

  /**
   * メソッド名: JwtTokenUtil<br>
   * 試験名: コンストラクタの検証<br>
   * 条件: コンストラクタを呼び出す<br>
   * 結果: インスタンスが生成される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testConstractor_コンストラクタ() {
    new JwtTokenUtil();
  }

  /**
   * メソッド名: extractClaimFromJwt<br>
   * 試験名: operator_idの取得<br>
   * 条件: 有効なJWTトークンとoperator_idクレーム名を渡す<br>
   * 結果: operator_idが正しく取得される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testExtractClaimFromJwt_正常系_operator_idを取得() {
    // Arrange
    // (定数を使用)

    // Act
    String result =
        JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, TEST_CLAIM_NAME_OPERATOR_ID);

    // Assert
    assertNotNull(result, "operator_idが取得できること");
    assertEquals(TEST_EXPECTED_OPERATOR_ID, result, "operator_idの値が正しいこと");
  }

  /**
   * メソッド名: extractClaimFromJwt<br>
   * 試験名: operator_idが空文字の場合の動作<br>
   * 条件: operator_idが空文字のJWTトークンを渡す<br>
   * 結果: nullが返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testExtractClaimFromJwt_正常系_operator_idが空文字でnullを返却() {
    // Arrange
    // (定数を使用)

    // Act
    String result =
        JwtTokenUtil.extractClaimFromJwt(
            TEST_AUTHORIZATION_HEADER_EMPTY_OPERATOR_ID, TEST_CLAIM_NAME_OPERATOR_ID);

    // Assert
    assertNull(result, "operator_idが空文字の場合はnullを返却すること");
  }

  /**
   * メソッド名: extractClaimFromJwt<br>
   * 試験名: Authorizationヘッダーがnullの場合の動作<br>
   * 条件: nullのAuthorizationヘッダーを渡す<br>
   * 結果: nullが返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testExtractClaimFromJwt_正常系_Authorizationヘッダーがnullでnullを返却() {
    // Arrange
    String authorizationHeader = null;

    // Act
    String result =
        JwtTokenUtil.extractClaimFromJwt(authorizationHeader, TEST_CLAIM_NAME_OPERATOR_ID);

    // Assert
    assertNull(result, "Authorizationヘッダーがnullの場合はnullを返却すること");
  }

  /**
   * メソッド名: extractClaimFromJwt<br>
   * 試験名: Authorizationヘッダーが空文字の場合の動作<br>
   * 条件: 空文字のAuthorizationヘッダーを渡す<br>
   * 結果: nullが返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testExtractClaimFromJwt_正常系_Authorizationヘッダーが空文字でnullを返却() {
    // Arrange
    String authorizationHeader = "";

    // Act
    String result =
        JwtTokenUtil.extractClaimFromJwt(authorizationHeader, TEST_CLAIM_NAME_OPERATOR_ID);

    // Assert
    assertNull(result, "Authorizationヘッダーが空文字の場合はnullを返却すること");
  }

  /**
   * メソッド名: extractClaimFromJwt<br>
   * 試験名: AuthorizationヘッダーがBearerで始まらない場合の動作<br>
   * 条件: Basicで始まるAuthorizationヘッダーを渡す<br>
   * 結果: nullが返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testExtractClaimFromJwt_正常系_AuthorizationヘッダーがBearerで始まらずnullを返却() {
    // Arrange
    // (定数を使用)

    // Act
    String result =
        JwtTokenUtil.extractClaimFromJwt(
            TEST_AUTHORIZATION_HEADER_NOT_BEARER, TEST_CLAIM_NAME_OPERATOR_ID);

    // Assert
    assertNull(result, "Authorizationヘッダーが「Bearer 」で始まらない場合はnullを返却すること");
  }

  /**
   * メソッド名: extractClaimFromJwt<br>
   * 試験名: JWTトークンのピリオドが不足している場合の動作<br>
   * 条件: ピリオドが1つのJWTトークンを渡す<br>
   * 結果: nullが返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testExtractClaimFromJwt_正常系_ピリオドが1つでnullを返却() {
    // Arrange
    // (定数を使用)

    // Act
    String result =
        JwtTokenUtil.extractClaimFromJwt(
            TEST_AUTHORIZATION_HEADER_ONE_PERIOD, TEST_CLAIM_NAME_OPERATOR_ID);

    // Assert
    assertNull(result, "JWTトークンのピリオドが1つの場合はnullを返却すること");
  }

  /**
   * メソッド名: extractClaimFromJwt<br>
   * 試験名: JWTトークンにピリオドがない場合の動作<br>
   * 条件: ピリオドがないJWTトークンを渡す<br>
   * 結果: nullが返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testExtractClaimFromJwt_正常系_ピリオドがないでnullを返却() {
    // Arrange
    // (定数を使用)

    // Act
    String result =
        JwtTokenUtil.extractClaimFromJwt(
            TEST_AUTHORIZATION_HEADER_NO_PERIOD, TEST_CLAIM_NAME_OPERATOR_ID);

    // Assert
    assertNull(result, "JWTトークンにピリオドがない場合はnullを返却すること");
  }

  /**
   * メソッド名: extractClaimFromJwt<br>
   * 試験名: 不正なBase64形式のトークンの動作<br>
   * 条件: 不正なBase64形式のJWTトークンを渡す<br>
   * 結果: 例外をキャッチしてnullが返却される<br>
   * テストパターン：異常系<br>
   */
  @Test
  void testExtractClaimFromJwt_正常系_不正なBase64形式でnullを返却() {
    // Arrange
    // (定数を使用)

    // Act
    String result =
        JwtTokenUtil.extractClaimFromJwt(
            TEST_AUTHORIZATION_HEADER_INVALID_BASE64, TEST_CLAIM_NAME_OPERATOR_ID);

    // Assert
    assertNull(result, "不正なBase64形式の場合は例外をキャッチしてnullを返却すること");
  }

  /**
   * メソッド名: extractClaimFromJwt<br>
   * 試験名: 存在しないクレーム名の動作<br>
   * 条件: 存在しないクレーム名を指定する<br>
   * 結果: nullが返却される<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testExtractClaimFromJwt_正常系_存在しないクレーム名でnullを返却() {
    // Arrange
    // (定数を使用)

    // Act
    String result =
        JwtTokenUtil.extractClaimFromJwt(TEST_AUTHORIZATION_HEADER, TEST_CLAIM_NAME_NOT_EXISTS);

    // Assert
    assertNull(result, "存在しないクレーム名の場合はnullを返却すること");
  }
}
