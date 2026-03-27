package com.hitachi.droneroute.config.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * リクエストパラメータとボディに含まれる不正な文字（ヌルバイト等）を検証するフィルター
 *
 * <p>AppScanで検出されたヌルバイト（%00）脆弱性に対応するため、<br>
 * クエリパラメータとリクエストボディを検証し、不正な制御文字を含むリクエストを事前にブロックする。<br>
 * ※ multipart/form-data（ファイルアップロード）およびバイナリコンテンツは検証対象外
 */
public class RequestValidationFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestValidationFilter.class);

  /** 検証対象のテキスト系Content-Type */
  private static final Set<String> TEXTUAL_TYPES =
      Set.of(
          "application/json",
          "application/xml",
          "text/plain",
          "text/xml",
          "application/x-www-form-urlencoded",
          "text/csv");

  /** リクエストボディ読み取り上限（10MB） */
  private static final int MAX_BODY_SIZE = 10 * 1024 * 1024;

  private final ObjectMapper objectMapper;

  public RequestValidationFilter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    // 1. クエリパラメータの検証（全HTTPメソッドで実施）
    Map<String, String[]> parameterMap = request.getParameterMap();
    for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
      for (String value : entry.getValue()) {
        if (value != null && StringUtils.containsInvalidCharacters(value)) {
          LOGGER.warn("クエリパラメータに不正な文字が含まれています: パラメータ名={}", entry.getKey());
          sendErrorResponse(response, "クエリパラメータに不正な文字が含まれています");
          return;
        }
      }
    }

    // 2. リクエストボディの検証（GET/HEADはボディを持たないのでスキップ）
    String method = request.getMethod();
    if (HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method)) {
      filterChain.doFilter(request, response);
      return;
    }
    String contentType = request.getContentType();
    if (contentType == null || contentType.isBlank()) {
      // Content-Type不明の場合はそのまま通す
      filterChain.doFilter(request, response);
      return;
    }

    // MimeType解析
    MimeType mimeType;
    try {
      mimeType = MimeTypeUtils.parseMimeType(contentType);
    } catch (Exception e) {
      // MimeType解析失敗時はそのまま通す
      filterChain.doFilter(request, response);
      return;
    }

    // multipartはスキップ（添付ファイルのバイナリデータは検証対象外）
    if ("multipart".equalsIgnoreCase(mimeType.getType())) {
      filterChain.doFilter(request, response);
      return;
    }

    // バイナリコンテンツはスキップ
    if (isLikelyBinary(mimeType)) {
      filterChain.doFilter(request, response);
      return;
    }

    // テキスト系のみ検証
    if (!isTextual(mimeType)) {
      filterChain.doFilter(request, response);
      return;
    }

    // Content-Lengthチェック（DoS対策）
    int contentLength = request.getContentLength();
    if (contentLength > MAX_BODY_SIZE) {
      LOGGER.warn("リクエストボディが上限を超えています: {} bytes", contentLength);
      sendErrorResponse(response, "リクエストボディが大きすぎます");
      return;
    }

    // カスタムWrapperでボディを先読み+キャッシュ（後続でも読める）
    CachedBodyHttpServletRequest wrapper;
    try {
      wrapper = new CachedBodyHttpServletRequest(request);
    } catch (IOException e) {
      LOGGER.warn("リクエストボディの読み取りに失敗: {}", e.getMessage());
      sendErrorResponse(response, "リクエストボディの読み取りに失敗しました");
      return;
    }

    // キャッシュから取得（InputStreamを消費しない）
    byte[] body = wrapper.getCachedBody();

    // 形式別の文字レベル検証
    boolean isJsonFormat = isJson(mimeType);
    if (isJsonFormat) {
      // JSON形式: JSONエスケープシーケンス（\u0000等）が実際の制御文字に変換されるため、
      // Jackson解析後のノード値を再帰的に検証
      try {
        JsonNode rootNode = objectMapper.readTree(body);
        String invalidPath = validateJsonNode(rootNode, "$");
        if (invalidPath != null) {
          LOGGER.warn("リクエストボディに不正な文字が含まれています: フィールド={}", invalidPath);
          sendErrorResponse(response, "リクエストボディに不正な文字が含まれています");
          return;
        }
      } catch (IOException e) {
        // JSON解析エラーは後続の処理に任せる（ここでは検証のみ）
      }
    } else {
      // JSON以外: UTF-8デコード後の文字列を直接検証（C0+C1制御文字を検出）
      // UTF-8継続バイト（0x80-0xBF）は正当な文字にデコードされるため誤検出なし
      String bodyString = new String(body, java.nio.charset.StandardCharsets.UTF_8);
      if (StringUtils.containsInvalidCharacters(bodyString)) {
        LOGGER.warn("リクエストボディに不正な文字が含まれています");
        sendErrorResponse(response, "リクエストボディに不正な文字が含まれています");
        return;
      }
    }

    // 検証完了、後続のフィルタへ
    filterChain.doFilter(wrapper, response);
  }

  /**
   * JSONノードを再帰的に検証（\u0000などエスケープ後に変換されるケース）
   *
   * @param node 検証対象のJSONノード
   * @param path 現在のJSONパス（例: $.dronePortName, $.items[0].name）
   * @return 不正な文字が含まれる場合はそのフィールドパス、有効な場合はnull
   */
  private String validateJsonNode(JsonNode node, String path) {
    if (node == null) {
      return null;
    }
    if (node.isTextual() && StringUtils.containsInvalidCharacters(node.asText())) {
      return path;
    }
    if (node.isObject()) {
      var fields = node.fields();
      while (fields.hasNext()) {
        var entry = fields.next();
        String fieldPath = path + "." + entry.getKey();
        String result = validateJsonNode(entry.getValue(), fieldPath);
        if (result != null) {
          return result;
        }
      }
    }
    if (node.isArray()) {
      for (int i = 0; i < node.size(); i++) {
        String arrayPath = path + "[" + i + "]";
        String result = validateJsonNode(node.get(i), arrayPath);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }

  /** JSONコンテンツか判定 */
  private boolean isJson(MimeType mimeType) {
    String raw = mimeType.getType() + "/" + mimeType.getSubtype();
    if ("application/json".equals(raw)) {
      return true;
    }
    // +json のベンダー型（例: application/vnd.api+json）もJSON扱い
    return mimeType.getSubtype().endsWith("+json");
  }

  /** バイナリコンテンツか判定 */
  private boolean isLikelyBinary(MimeType mimeType) {
    String type = mimeType.getType();
    String subtype = mimeType.getSubtype();

    if ("application".equalsIgnoreCase(type)) {
      return subtype.equalsIgnoreCase("octet-stream")
          || subtype.contains("zip")
          || subtype.contains("pdf")
          || subtype.contains("protobuf")
          || subtype.contains("msgpack");
    }

    return "image".equalsIgnoreCase(type)
        || "audio".equalsIgnoreCase(type)
        || "video".equalsIgnoreCase(type);
  }

  /** テキストコンテンツか判定 */
  private boolean isTextual(MimeType mimeType) {
    String raw = mimeType.getType() + "/" + mimeType.getSubtype();
    if (TEXTUAL_TYPES.contains(raw)) {
      return true;
    }

    // text/* は広くテキスト扱い
    if ("text".equalsIgnoreCase(mimeType.getType())) {
      return true;
    }

    // +json / +xml のベンダー型（例: application/vnd.api+json）もテキスト扱い
    String subtype = mimeType.getSubtype();
    return subtype.endsWith("+json") || subtype.endsWith("+xml");
  }

  /** エラーレスポンスを送信 */
  private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    String jsonResponse = String.format("{\"message\":\"%s\"}", message);
    response.getWriter().write(jsonResponse);
    response.getWriter().flush();
  }
}
