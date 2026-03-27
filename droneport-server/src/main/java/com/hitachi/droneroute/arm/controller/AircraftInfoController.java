package com.hitachi.droneroute.arm.controller;

import com.hitachi.droneroute.arm.dto.AircraftInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoFileInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoModelSearchRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoPayloadInfoListElementReq;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListResponseDto;
import com.hitachi.droneroute.arm.entity.FileInfoEntity;
import com.hitachi.droneroute.arm.entity.PayloadInfoEntity;
import com.hitachi.droneroute.arm.service.AircraftInfoService;
import com.hitachi.droneroute.arm.service.VirusScanService;
import com.hitachi.droneroute.arm.validator.AircraftInfoValidator;
import com.hitachi.droneroute.cmn.resolver.QueryStringArgs;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.config.dto.UserInfoDto;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

/** 機体リソース管理機体情報APIのコントローラ */
@RequiredArgsConstructor
@RestController
@RequestMapping("${droneroute.basepath}/aircraft/info")
public class AircraftInfoController {

  // ロガー
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  // 機体情報APIパラメータチェック
  private final AircraftInfoValidator validator;

  // 機体情報APIサービスクラス
  private final AircraftInfoService service;

  // ウイルススキャンサービスクラス
  private final VirusScanService virusScanService;

  // システム設定
  private final SystemSettings systemSettings;

  /**
   * 機体情報登録
   *
   * @param dto リクエストボディ
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @PostMapping()
  public ResponseEntity<AircraftInfoResponseDto> post(
      @RequestBody AircraftInfoRequestDto dto, @AuthenticationPrincipal UserInfoDto user) {
    logger.info("機体情報登録:  ===== START =====");
    logger.debug(dto.toString());
    logger.debug(user.toString());

    // 画像データ変換
    service.decodeBinary(dto);

    // ファイルデータ変換
    service.decodeFileData(dto);

    // 入力チェック
    validator.validateForRegist(dto);

    // ウイルスチェック（補足資料情報）
    List<AircraftInfoFileInfoListElementReq> fileInfoList = dto.getFileInfos();
    if (fileInfoList != null) {
      for (AircraftInfoFileInfoListElementReq fileInfoReq : fileInfoList) {
        if (fileInfoReq != null && fileInfoReq.getFileBinary() != null) {
          virusScanService.scanVirus(fileInfoReq.getFileBinary());
        }
      }
    }

    // ウイルスチェック（ペイロード情報）
    List<AircraftInfoPayloadInfoListElementReq> payloadInfoList = dto.getPayloadInfos();
    if (payloadInfoList != null) {
      for (AircraftInfoPayloadInfoListElementReq PayloadInfoReq : payloadInfoList) {
        if (PayloadInfoReq != null && PayloadInfoReq.getFileBinary() != null) {
          virusScanService.scanVirus(PayloadInfoReq.getFileBinary());
        }
      }
    }

    // サービス呼び出し
    AircraftInfoResponseDto responseDto = service.postData(dto, user);

    logger.info("機体情報登録:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 機体情報更新
   *
   * @param dto リクエストボディ
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @PutMapping()
  public ResponseEntity<AircraftInfoResponseDto> put(
      @RequestBody AircraftInfoRequestDto dto, @AuthenticationPrincipal UserInfoDto user) {
    logger.info("機体情報更新:  ===== START =====");
    logger.debug(dto.toString());
    logger.debug(user.toString());

    // 画像データ変換
    service.decodeBinary(dto);

    // ファイルデータ変換
    service.decodeFileData(dto);

    // 入力チェック
    validator.validateForUpdate(dto);

    // ウイルスチェック（補足資料情報）
    List<AircraftInfoFileInfoListElementReq> fileInfoList = dto.getFileInfos();
    if (fileInfoList != null) {
      for (AircraftInfoFileInfoListElementReq fileInfoReq : fileInfoList) {
        if (fileInfoReq != null && fileInfoReq.getFileBinary() != null) {
          virusScanService.scanVirus(fileInfoReq.getFileBinary());
        }
      }
    }

    // ウイルスチェック（ペイロード情報）
    List<AircraftInfoPayloadInfoListElementReq> payloadInfoList = dto.getPayloadInfos();
    if (payloadInfoList != null) {
      for (AircraftInfoPayloadInfoListElementReq PayloadInfoReq : payloadInfoList) {
        if (PayloadInfoReq != null && PayloadInfoReq.getFileBinary() != null) {
          virusScanService.scanVirus(PayloadInfoReq.getFileBinary());
        }
      }
    }

    // サービス呼び出し
    AircraftInfoResponseDto responseDto = service.putData(dto, user);

    logger.info("機体情報更新:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 機体情報一覧
   *
   * @param dto クエリパラメータ
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @GetMapping("/list")
  public ResponseEntity<AircraftInfoSearchListResponseDto> getList(
      @QueryStringArgs AircraftInfoSearchListRequestDto dto,
      @AuthenticationPrincipal UserInfoDto user) {
    logger.info("機体情報一覧:  ===== START =====");
    logger.debug(dto.toString());
    logger.debug(user.toString());

    // 入力チェック
    validator.validateForGetList(dto);

    // クエリパラメータにソート順が設定されていない場合は、システム設定のデフォルト値を設定する
    if (!StringUtils.hasText(dto.getSortOrders())) {
      dto.setSortOrders(
          systemSettings.getString(
              DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
              DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    }

    // クエリパラメータにソート対象列名が設定されていない場合は、システム設定のデフォルト値を設定する
    if (!StringUtils.hasText(dto.getSortColumns())) {
      dto.setSortColumns(
          systemSettings.getString(
              DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
              DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
    }

    // サービス呼び出し
    AircraftInfoSearchListResponseDto responseDto = service.getList(dto, user);

    logger.info("機体情報一覧:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 機体情報公開データ抽出
   *
   * @param dto クエリパラメータ
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @GetMapping("/publicDataExtract")
  public ResponseEntity<AircraftInfoSearchListResponseDto> getPublicDataExtract(
      @QueryStringArgs AircraftInfoSearchListRequestDto dto,
      @AuthenticationPrincipal UserInfoDto user) {
    logger.info("機体情報公開データ抽出:  ===== START =====");
    logger.debug(dto.toString());

    // ソート順、ソート対象列名にシステム設定のデフォルト値を設定する
    dto.setSortOrders(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
    dto.setSortColumns(
        systemSettings.getString(
            DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT,
            DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));

    // API仕様上公開していない項目の上書き
    dto.setPage(null);
    dto.setPerPage(null);
    dto.setIsRequiredPayloadInfo("false");
    dto.setPublicFlag("true");

    // 入力チェック
    validator.validateForGetList(dto);

    // サービス呼び出し
    AircraftInfoSearchListResponseDto responseDto = service.getList(dto, user);

    logger.info("機体情報公開データ抽出:  ===== END =====");

    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 機体情報モデル検索
   *
   * @param request リクエストボディ
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @PostMapping("/modelSearch")
  public ResponseEntity<AircraftInfoSearchListResponseDto> modelSearch(
      @RequestBody AircraftInfoModelSearchRequestDto request,
      @AuthenticationPrincipal UserInfoDto user) {
    logger.info("機体情報モデル検索:  ===== START =====");
    logger.debug(request.toString());
    logger.debug(user.toString());

    // 入力チェック
    validator.validateForModelSearch(request);

    // 一覧要求DTOへ詰め替え
    AircraftInfoSearchListRequestDto dto = new AircraftInfoSearchListRequestDto();
    dto.setModelInfos(request.getModelInfos());
    dto.setIsRequiredPayloadInfo(request.getIsRequiredPayloadInfo());
    dto.setIsRequiredPriceInfo(request.getIsRequiredPriceInfo());

    // サービス呼び出し
    AircraftInfoSearchListResponseDto responseDto = service.getList(dto, user);

    logger.info("機体情報モデル検索:  ===== END =====");
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 機体情報詳細
   *
   * @param aircraftId 機体ID(パスパラメータ)
   * @param isRequiredPayloadInfo ペイロード情報要否(クエリパラメータ)
   * @param isRequiredPriceInfo 料金情報要否(クエリパラメータ)
   * @param user 認可ユーザー情報
   * @return 正常終了レスポンス
   */
  @GetMapping("/detail/{aircraftId}")
  public ResponseEntity<AircraftInfoDetailResponseDto> getDetail(
      @PathVariable("aircraftId") String aircraftId,
      @RequestParam(value = "isRequiredPayloadInfo", required = false, defaultValue = "false")
          Boolean isRequiredPayloadInfo,
      @RequestParam(value = "isRequiredPriceInfo", required = false, defaultValue = "false")
          Boolean isRequiredPriceInfo,
      @AuthenticationPrincipal UserInfoDto user) {
    logger.info("機体情報詳細:  ===== START =====");
    logger.debug(aircraftId);
    logger.debug(user.toString());

    // 入力チェック
    validator.validateForDetail(aircraftId);

    // サービス呼び出し
    AircraftInfoDetailResponseDto responseDto =
        service.getDetail(aircraftId, isRequiredPayloadInfo, isRequiredPriceInfo, user);

    logger.info("機体情報詳細:  ===== END =====");
    // 処理結果編集
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 機体情報削除
   *
   * @param aircraftId 機体ID(パスパラメータ)
   * @param user 認可ユーザー情報
   */
  @DeleteMapping("/{aircraftId}")
  public void delete(
      @PathVariable("aircraftId") String aircraftId, @AuthenticationPrincipal UserInfoDto user) {
    logger.info("機体情報削除:  ===== START =====");
    logger.debug(aircraftId);
    logger.debug(user.toString());

    // 入力チェック
    validator.validateForDetail(aircraftId);

    // サービス呼び出し
    service.deleteData(aircraftId, user);

    logger.info("機体情報削除:  ===== END =====");
  }

  /**
   * 補足資料ファイルダウンロード
   *
   * @param aircraftId 機体ID(パスパラメータ)
   * @param fileId 補足資料ID(パスパラメータ)
   * @param user ユーザ情報DTO
   * @return 正常終了レスポンス
   */
  @GetMapping("/detail/{aircraftId}/{fileId}")
  public ResponseEntity<byte[]> downloadFile(
      @PathVariable("aircraftId") String aircraftId,
      @PathVariable("fileId") String fileId,
      @AuthenticationPrincipal UserInfoDto user) {

    logger.info("補足資料ファイルダウンロード:  ===== START =====");
    logger.debug(aircraftId);
    logger.debug(fileId);
    logger.debug(user.toString());

    // 入力チェック
    validator.validateForDownloadFile(aircraftId, fileId);

    // サービス呼び出し
    FileInfoEntity fileEntity = service.downloadFile(fileId, user);

    logger.info("補足資料ファイルダウンロード:  ===== END =====");

    // レスポンス編集
    // MIMEタイプ設定
    MediaType mediaType;
    if (fileEntity.getFileFormat() != null) {
      mediaType = MediaType.parseMediaType(fileEntity.getFileFormat());
    } else {
      mediaType = MediaType.APPLICATION_OCTET_STREAM;
    }
    // ダウンロードファイル(Content-Disposition)設定（日本語ファイル名＆RFC5987対応）
    String encodedFilename =
        URLEncoder.encode(fileEntity.getFilePhysicalName(), StandardCharsets.UTF_8)
            .replaceAll("\\+", "%20");
    String contentDispositionStr = "attachment; filename*=UTF-8''" + encodedFilename;

    // ヘッダに編集内容を設定
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(mediaType);
    headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDispositionStr);
    if (fileEntity.getFileData() != null) {
      headers.setContentLength(fileEntity.getFileData().length);
    } else {
      headers.setContentLength(0);
    }

    // 編集結果を返却
    return new ResponseEntity<>(fileEntity.getFileData(), headers, HttpStatus.OK);
  }

  /**
   * ペイロード添付ファイルダウンロード
   *
   * @param payloadId ペイロードID
   * @param user ユーザ情報DTO
   * @return 正常終了レスポンス
   */
  @GetMapping("/payload/{payloadId}")
  public ResponseEntity<byte[]> downloadPayloadFile(
      @PathVariable("payloadId") String payloadId, @AuthenticationPrincipal UserInfoDto user) {

    logger.info("ペイロード添付ファイルダウンロード:  ===== START =====");
    logger.debug(payloadId);
    logger.debug(user.toString());

    // 入力チェック
    validator.validateForDownloadPayloadFile(payloadId);

    // サービス呼び出し
    PayloadInfoEntity payloadEntity = service.downloadPayloadFile(payloadId, user);

    logger.info("ペイロード添付ファイルダウンロード:  ===== END =====");

    // レスポンス編集
    // MIMEタイプ設定
    MediaType mediaType;
    if (payloadEntity.getFileFormat() != null) {
      mediaType = MediaType.parseMediaType(payloadEntity.getFileFormat());
    } else {
      mediaType = MediaType.APPLICATION_OCTET_STREAM;
    }
    // ダウンロードファイル(Content-Disposition)設定（日本語ファイル名＆RFC5987対応）
    String encodedFilename =
        URLEncoder.encode(payloadEntity.getFilePhysicalName(), StandardCharsets.UTF_8)
            .replaceAll("\\+", "%20");
    String contentDispositionStr = "attachment; filename*=UTF-8''" + encodedFilename;

    // ヘッダに編集内容を設定
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(mediaType);
    headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDispositionStr);
    if (payloadEntity.getFileData() != null) {
      headers.setContentLength(payloadEntity.getFileData().length);
    } else {
      headers.setContentLength(0);
    }

    // 編集結果を返却
    return new ResponseEntity<>(payloadEntity.getFileData(), headers, HttpStatus.OK);
  }
}
