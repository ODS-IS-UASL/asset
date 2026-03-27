package com.hitachi.droneroute.config.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.RoleInfoDto;
import com.hitachi.droneroute.config.dto.UserAttributeApiErrorResponseDto;
import com.hitachi.droneroute.config.dto.UserAttributeApiResponseDto;
import com.hitachi.droneroute.config.dto.UserAttributeApiResponseDto.AttributeDto;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.config.service.UserInfoService;
import com.hitachi.droneroute.config.validator.UserInfoResponseValidator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** ユーザー情報取得サービス実装クラス */
@Service
public class UserInfoServiceImpl implements UserInfoService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoServiceImpl.class);
  private static final String SETTING_CLASS = "user-attribute-api";
  private final SystemSettings systemSettings;
  private final WebClient webClient;
  private final UserInfoResponseValidator validator;

  /**
   * コンストラクタで必要な依存関係を注入する
   *
   * @param systemSettings システム設定を提供するSystemSettingsのインスタンス
   * @param webClientBuilder WebClient.Builderのインスタンス、WebClientの構築に使用
   * @param objectMapper ObjectMapperのインスタンス、JSON処理に使用（現在は未使用）
   * @param validator UserInfoResponseValidatorのインスタンス、APIレスポンスの妥当性検査に使用
   */
  public UserInfoServiceImpl(
      SystemSettings systemSettings,
      WebClient.Builder webClientBuilder,
      ObjectMapper objectMapper,
      UserInfoResponseValidator validator) {
    this.systemSettings = systemSettings;
    this.webClient = webClientBuilder.build();
    this.validator = validator;
  }

  @Override
  /**
   * ユーザ属性APIを呼び出してユーザ情報を取得するメソッド
   *
   * @param systemAccessToken システムアクセストークン
   * @param userId ユーザID
   * @return ユーザ情報を含むUserInfoDtoのインスタンス
   * @throws ServiceErrorException ユーザ情報の取得に失敗した場合にスローされる例外
   */
  public UserInfoDto getUserInfo(String systemAccessToken, String userId) {
    // リクエストボディ作成
    Map<String, Object> requestBody = Map.of("userIdList", List.of(userId));
    // ユーザ属性取得PI呼び出し（POST）
    UserAttributeApiResponseDto responseDto =
        webClient
            .method(HttpMethod.valueOf(systemSettings.getString(SETTING_CLASS, "method")))
            .uri(systemSettings.getString(SETTING_CLASS, "url"))
            .header("Authorization", systemAccessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .onStatus(status -> status.value() != 200, this::handleErrorResponse)
            .bodyToMono(UserAttributeApiResponseDto.class)
            .block();

    // レスポンスの妥当性チェック
    validator.validateResponse(responseDto);

    // ユーザ情報をレスポンスから抽出
    AttributeDto attr = responseDto.getAttributeList().get(0);
    String userOperatorId = attr.getUser_id();
    UserAttributeApiResponseDto.Attribute attribute = attr.getAttribute();
    String affiliatedOperatorId = attribute.getOperatorId();
    if (!org.springframework.util.StringUtils.hasText(affiliatedOperatorId)) {
      // 事業者ユーザでアクセスされた場合(=所属事業者IDが空の場合)には、ユーザのオペレーターIDを所属事業者IDと見なして使う
      affiliatedOperatorId = userOperatorId;
    }

    // ロール情報をリストで取得し、RoleInfoDtoのリストに変換
    List<RoleInfoDto> roles =
        attribute.getRoles().stream()
            .map(role -> new RoleInfoDto(role.getRoleId(), role.getRoleName()))
            .collect(Collectors.toList());

    // ユーザ情報を設定
    UserInfoDto userInfo = new UserInfoDto();
    userInfo.setUserOperatorId(userOperatorId);
    userInfo.setRoles(roles);
    userInfo.setAffiliatedOperatorId(affiliatedOperatorId);
    userInfo.setDummyUserFlag(false);
    return userInfo;
  }

  /**
   * APIエラーレスポンス(JSON)をDTOに変換し、内容を例外メッセージに含めてthrowする。
   *
   * @param clientResponse WebClientのClientResponse
   * @return 例外をthrowするMono
   */
  private Mono<? extends Throwable> handleErrorResponse(ClientResponse clientResponse) {
    return clientResponse
        .bodyToMono(UserAttributeApiErrorResponseDto.class)
        .map(
            errorDto -> {
              // DTOに定義されていないフィールドは無視され、未設定フィールドはnullまたは空で返される
              LOGGER.warn(
                  "ユーザー属性API呼び出し失敗 [code={}, message={}]",
                  errorDto.getCode(),
                  errorDto.getErrorMessage());

              return new ServiceErrorException("ユーザ情報の取得に失敗しました");
            });
  }
}
