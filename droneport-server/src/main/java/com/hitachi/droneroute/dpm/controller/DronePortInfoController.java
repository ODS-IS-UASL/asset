package com.hitachi.droneroute.dpm.controller;

import com.hitachi.droneroute.cmn.resolver.QueryStringArgs;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortEnvironmentInfoResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterResponseDto;
import com.hitachi.droneroute.dpm.service.DronePortInfoService;
import com.hitachi.droneroute.dpm.validator.DronePortInfoValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 離着陸場情報APIのコントローラ */
@RequiredArgsConstructor
@RestController
@RequestMapping("${droneroute.basepath}/droneport")
public class DronePortInfoController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  // 離着陸場情報関連APIのパラメータチェッククラス
  private final DronePortInfoValidator validator;

  // 離着陸場情報関連APIのサービスクラス
  private final DronePortInfoService service;

  // システム設定
  private final SystemSettings systemSettings;

  /**
   * 離着陸場情報登録
   *
   * @param dto 離着陸場情報登録更新要求
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @PostMapping("/info")
  public ResponseEntity<DronePortInfoRegisterResponseDto> post(
      @RequestBody DronePortInfoRegisterRequestDto dto, @AuthenticationPrincipal UserInfoDto user) {
    logger.info("離着陸場情報登録:  ===== START =====");
    logger.debug(dto.toString());

    // 画像(base64)をバイナリに変換しておく
    service.decodeBinary(dto);
    // 離着陸場情報登録入力チェック
    validator.validateForRegist(dto);

    // 離着陸場情報登録サービス呼び出し
    DronePortInfoRegisterResponseDto responseDto = service.register(dto, user);

    logger.debug(responseDto.toString());
    logger.info("離着陸場情報登録:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 離着陸場情報更新
   *
   * @param dto 離着陸場情報登録更新要求
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @PutMapping("/info")
  public ResponseEntity<DronePortInfoRegisterResponseDto> put(
      @RequestBody DronePortInfoRegisterRequestDto dto, @AuthenticationPrincipal UserInfoDto user) {
    logger.info("離着陸場情報更新:  ===== START =====");
    logger.debug(dto.toString());

    // 画像(base64)をバイナリに変換しておく
    service.decodeBinary(dto);
    // 離着陸場情報更新入力チェック
    validator.validateForUpdate(dto);

    // 離着陸場情報更新サービス呼び出し
    DronePortInfoRegisterResponseDto responseDto = service.update(dto, user);

    logger.debug(responseDto.toString());
    logger.info("離着陸場情報更新:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 離着陸場情報削除
   *
   * @param dronePortId 離着陸場ID
   * @param user 認可ユーザー情報
   */
  @DeleteMapping("/info/{dronePortId}")
  public void delete(
      @PathVariable("dronePortId") String dronePortId, @AuthenticationPrincipal UserInfoDto user) {
    logger.info("離着陸場情報削除(パスパラメータ版):  ===== START =====");
    logger.debug(dronePortId);

    // 離着陸場情報削除入力チェック
    validator.validateForGetDetail(dronePortId);

    // 離着陸場情報削除サービス呼び出し
    service.delete(dronePortId, user);

    // 成功時はHTTPステータス:200を返却。レスポンスボディなし。
    logger.info("離着陸場情報削除:  ===== END =====");
  }

  /**
   * 離着陸場情報一覧取得API
   *
   * @param dto 離着陸場情報一覧取得要求
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @GetMapping("/info/list")
  public ResponseEntity<DronePortInfoListResponseDto> getList(
      @QueryStringArgs DronePortInfoListRequestDto dto, @AuthenticationPrincipal UserInfoDto user) {
    logger.info("離着陸場情報一覧取得:  ===== START =====");
    logger.debug(dto.toString());

    // 離着陸場情報一覧取得入力チェック
    validator.validateForGetList(dto);

    // クエリパラメータにソート順が設定されていない場合は、システム設定のデフォルト値を設定する
    if (!StringUtils.hasText(dto.getSortOrders())) {
      dto.setSortOrders(
          systemSettings.getString(
              DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
              DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    }
    // クエリパラメータにソート対象列名が設定されていない場合は、システム設定のデフォルト値を設定する
    if (!StringUtils.hasText(dto.getSortColumns())) {
      dto.setSortColumns(
          systemSettings.getString(
              DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
              DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    }

    // 離着陸場情報一覧取得サービス呼び出し
    DronePortInfoListResponseDto responseDto = service.getList(dto, user);

    logger.debug(responseDto.toString());
    logger.info("離着陸場情報一覧取得:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 離着陸場情報公開データ抽出API
   *
   * @param dto 離着陸場情報一覧取得要求
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @GetMapping("/info/publicDataExtract")
  public ResponseEntity<DronePortInfoListResponseDto> getPublicDataExtract(
      @QueryStringArgs DronePortInfoListRequestDto dto, @AuthenticationPrincipal UserInfoDto user) {
    logger.info("離着陸場情報公開データ抽出:  ===== START =====");
    logger.debug(dto.toString());

    // ソート順、ソート対象列名にシステム設定のデフォルト値を設定する
    dto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    dto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));

    // API仕様上公開していない項目の上書き
    dto.setPage(null);
    dto.setPerPage(null);
    dto.setPublicFlag("true");

    // 離着陸場情報一覧取得入力チェック
    validator.validateForGetList(dto);

    // 離着陸場情報一覧取得サービス呼び出し
    DronePortInfoListResponseDto responseDto = service.getList(dto, user);

    logger.debug(responseDto.toString());
    logger.info("離着陸場情報公開データ抽出:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 離着陸場情報詳細取得
   *
   * @param dronePortId 離着陸場ID
   * @param isRequiredPriceInfo 料金情報の要否(true:料金情報あり、false:料金情報なし(デフォルト))
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @GetMapping("/info/detail/{dronePortId}")
  public ResponseEntity<DronePortInfoDetailResponseDto> getDetail(
      @PathVariable("dronePortId") String dronePortId,
      @RequestParam(value = "isRequiredPriceInfo", required = false, defaultValue = "false")
          Boolean isRequiredPriceInfo,
      @AuthenticationPrincipal UserInfoDto user) {
    logger.info("離着陸場情報詳細取得:  ===== START =====");
    logger.debug(dronePortId);

    // 離着陸場情報詳細取得入力チェック
    validator.validateForGetDetail(dronePortId);

    // 離着陸場情報詳細取得サービス呼び出し
    DronePortInfoDetailResponseDto responseDto =
        service.getDetail(dronePortId, isRequiredPriceInfo, user);

    logger.debug(responseDto.toString());
    logger.info("離着陸場情報詳細取得:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 離着陸場周辺情報取得
   *
   * @param dronePortId 離着陸場ID
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @GetMapping("/environment/{dronePortId}")
  public ResponseEntity<DronePortEnvironmentInfoResponseDto> getEnvironment(
      @PathVariable("dronePortId") String dronePortId, @AuthenticationPrincipal UserInfoDto user) {
    logger.info("離着陸場周辺情報取得:  ===== START =====");
    logger.debug(dronePortId);

    // 離着陸場情報詳細取得入力チェックを流用する
    validator.validateForGetDetail(dronePortId);

    // 離着陸場周辺情報取得サービス呼出
    DronePortEnvironmentInfoResponseDto responseDto = service.getEnvironment(dronePortId, user);

    logger.debug(responseDto.toString());
    logger.info("離着陸場周辺情報取得:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
