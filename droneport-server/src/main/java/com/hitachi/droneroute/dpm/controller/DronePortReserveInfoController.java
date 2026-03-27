package com.hitachi.droneroute.dpm.controller;

import com.hitachi.droneroute.cmn.resolver.QueryStringArgs;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateResponseDto;
import com.hitachi.droneroute.dpm.service.DronePortReserveInfoService;
import com.hitachi.droneroute.dpm.validator.DronePortReserveInfoValidator;
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

/** 離着陸場予約情報APIのコントローラ */
@RequiredArgsConstructor
@RestController
@RequestMapping("${droneroute.basepath}/droneport/reserve")
public class DronePortReserveInfoController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  // 離着陸場予約情報関連APIのパラメータチェッククラス
  private final DronePortReserveInfoValidator validator;

  // 離着陸場予約情報関連APIのサービスクラス
  private final DronePortReserveInfoService service;

  // システム設定
  private final SystemSettings systemSettings;

  /**
   * 離着陸場予約情報登録
   *
   * @param dto 離着陸場予約情報登録更新要求
   * @param userDto 認可ユーザー情報
   * @return 離着陸場予約情報登録更新応答
   */
  @PostMapping()
  public ResponseEntity<DronePortReserveInfoRegisterListResponseDto> post(
      @RequestBody DronePortReserveInfoRegisterListRequestDto dto,
      @AuthenticationPrincipal UserInfoDto userDto) {
    logger.info("離着陸場予約情報登録:  ===== START =====");
    logger.debug(dto.toString());

    // 離着陸場予約情報登録更新入力チェック
    validator.validateForRegister(dto);

    // 離着陸場予約情報登録サービス呼び出し
    DronePortReserveInfoRegisterListResponseDto responseDto = service.register(dto, userDto);

    logger.debug(responseDto.toString());
    logger.info("離着陸場予約情報登録:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 離着陸場予約情報更新
   *
   * @param dto 離着陸場予約情報登録更新要求
   * @param userDto 認可ユーザー情報
   * @return 離着陸場予約情報登録更新応答
   */
  @PutMapping()
  public ResponseEntity<DronePortReserveInfoUpdateResponseDto> put(
      @RequestBody DronePortReserveInfoUpdateRequestDto dto,
      @AuthenticationPrincipal UserInfoDto userDto) {
    logger.info("離着陸場予約情報更新:  ===== START =====");
    logger.debug(dto.toString());

    // 離着陸場予約情報登録更新入力チェック
    validator.validateForUpdate(dto);

    // 離着陸場予約情報更新サービス呼び出し
    DronePortReserveInfoUpdateResponseDto responseDto = service.update(dto, userDto);

    logger.debug(responseDto.toString());
    logger.info("離着陸場予約情報更新:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 離着陸場予約情報削除
   *
   * @param reserveId 予約ID
   * @param dronePortReservationIdFlag 離着陸場予約ID使用フラグ
   * @param userDto 認可ユーザー情報
   */
  @DeleteMapping("/{reserveId}")
  public void delete(
      @PathVariable("reserveId") String reserveId,
      @RequestParam(value = "dronePortReservationIdFlag", required = false, defaultValue = "false")
          Boolean dronePortReservationIdFlag,
      @AuthenticationPrincipal UserInfoDto userDto) {
    logger.info("離着陸場予約情報削除:  ===== START =====");
    logger.debug(reserveId);

    // 離着陸場予約情報削除入力チェック
    validator.validateForGetDetail(reserveId);

    // 離着陸場予約情報削除サービス呼び出し
    service.delete(reserveId, dronePortReservationIdFlag, userDto);

    logger.info("離着陸場予約情報削除:  ===== END =====");
    // 成功時はHTTPステータス:200を返却。レスポンスボディなし。
  }

  /**
   * 離着陸場予約情報一覧取得API
   *
   * @param dto 離着陸場予約情報一覧取得要求
   * @return 離着陸場予約情報一覧取得応答
   */
  @GetMapping("/list")
  public ResponseEntity<DronePortReserveInfoListResponseDto> getList(
      @QueryStringArgs DronePortReserveInfoListRequestDto dto) {
    logger.info("離着陸場予約情報一覧取得:  ===== START =====");
    logger.debug(dto.toString());

    // 離着陸場予約情報一覧取得入力チェック
    validator.validateForGetList(dto);

    // クエリパラメータにソート順が設定されていない場合は、システム設定のデフォルト値を設定する
    if (!StringUtils.hasText(dto.getSortOrders())) {
      dto.setSortOrders(
          systemSettings.getString(
              DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
              DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    }
    // クエリパラメータにソート対象列名が設定されていない場合は、システム設定のデフォルト値を設定する
    if (!StringUtils.hasText(dto.getSortColumns())) {
      dto.setSortColumns(
          systemSettings.getString(
              DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
              DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    }

    // 離着陸場予約情報一覧取得サービス呼び出し
    DronePortReserveInfoListResponseDto responseDto = service.getList(dto);

    logger.debug(responseDto.toString());
    logger.info("離着陸場予約情報一覧取得:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 離着陸場予約情報公開データ抽出API
   *
   * @param dto 離着陸場予約情報一覧取得要求
   * @return 離着陸場予約情報一覧取得応答
   */
  @GetMapping("/publicDataExtract")
  public ResponseEntity<DronePortReserveInfoListResponseDto> getPublicDataExtract(
      @QueryStringArgs DronePortReserveInfoListRequestDto dto) {
    logger.info("離着陸場予約情報公開データ抽出:  ===== START =====");
    logger.debug(dto.toString());

    // ソート順,ソート対象列にシステム設定のデフォルト値を設定する
    dto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    dto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));

    // API仕様上公開していない項目の上書き
    dto.setPage(null);
    dto.setPerPage(null);

    // 離着陸場予約情報一覧取得入力チェック
    validator.validateForGetList(dto);

    // 離着陸場予約情報一覧取得サービス呼び出し
    DronePortReserveInfoListResponseDto responseDto = service.getList(dto);

    logger.debug(responseDto.toString());
    logger.info("離着陸場予約情報公開データ抽出:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 離着陸場予約情報詳細取得
   *
   * @param reserveId 予約ID
   * @return 離着陸場予約情報詳細取得応答
   */
  @GetMapping("/detail/{reserveId}")
  public ResponseEntity<DronePortReserveInfoDetailResponseDto> getDetail(
      @PathVariable("reserveId") String reserveId) {
    logger.info("離着陸場予約情報詳細取得:  ===== START =====");
    logger.debug(reserveId);

    // 離着陸場予約情報詳細取得入力チェック
    validator.validateForGetDetail(reserveId);

    // 離着陸場予約情報詳細取得サービス呼び出し
    DronePortReserveInfoDetailResponseDto responseDto = service.getDetail(reserveId);

    logger.debug(responseDto.toString());
    logger.info("離着陸場予約情報詳細取得:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
