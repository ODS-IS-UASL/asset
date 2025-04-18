package com.hitachi.droneroute.dpm.controller;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hitachi.droneroute.cmn.resolver.QueryStringArgs;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoRegisterListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortReserveInfoUpdateResponseDto;
import com.hitachi.droneroute.dpm.service.DronePortReserveInfoService;
import com.hitachi.droneroute.dpm.validator.DronePortInfoValidator;
import com.hitachi.droneroute.dpm.validator.DronePortReserveInfoValidator;

import lombok.RequiredArgsConstructor;

/**
 * ドローンポート予約情報APIのコントローラ
 * 
 * @author Hiroshi Toyoda
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("${droneroute.basepath}/droneport/reserve")
public class DronePortReserveInfoController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// ドローンポート予約情報関連APIのパラメータチェッククラス
	private final DronePortReserveInfoValidator validator;
	
	// ドローンポート情報関連APIのパラメータチェッククラス
	private final DronePortInfoValidator dronePortInfoValidator;
	
	// ドローンポート予約情報関連APIのサービスクラス
	private final DronePortReserveInfoService service;
	
	// システム設定
	private final SystemSettings systemSettings;

	/**
     * ドローンポート予約情報登録
     * @param dto ドローンポート予約情報登録更新要求
     * @return ドローンポート予約情報登録更新応答
     */
	@PostMapping()
    public ResponseEntity<DronePortReserveInfoRegisterListResponseDto> post(
    		@RequestBody DronePortReserveInfoRegisterListRequestDto dto) {
		logger.info("ドローンポート予約情報登録:  ===== START =====");
		logger.debug(dto.toString());
		
        // ドローンポート予約情報登録更新入力チェック
    	validator.validateForRegister(dto);
    	
        // ドローンポート予約情報登録サービス呼び出し
    	DronePortReserveInfoRegisterListResponseDto responseDto = service.register(dto);
    	
		logger.debug(responseDto.toString());
		logger.info("ドローンポート予約情報登録:  ===== END =====");
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    /** 
     * ドローンポート予約情報更新
     * @param dto ドローンポート予約情報登録更新要求
     * @return ドローンポート予約情報登録更新応答
     */
    @PutMapping()
    public ResponseEntity<DronePortReserveInfoUpdateResponseDto> put(@RequestBody DronePortReserveInfoUpdateRequestDto dto) {
		logger.info("ドローンポート予約情報更新:  ===== START =====");
		logger.debug(dto.toString());
		
        // ドローンポート予約情報登録更新入力チェック
    	validator.validateForUpdate(dto);
    	
        // ドローンポート予約情報更新サービス呼び出し
    	DronePortReserveInfoUpdateResponseDto responseDto = service.update(dto);
    	
    	logger.debug(responseDto.toString());
		logger.info("ドローンポート予約情報更新:  ===== END =====");
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * ドローンポート予約情報削除
     * @param reserveId 予約ID
     * @param ドローンポート予約情報削除要求
     */
    @DeleteMapping("/{reserveId}")
    public void delete(
    		@PathVariable("reserveId") String reserveId,
    		@Nullable @RequestBody DronePortInfoDeleteRequestDto dto) {
    	if (Objects.isNull(dto)) {
    		// リクエストボディが存在しない場合は、空のDTOを設定する。
    		dto = new DronePortInfoDeleteRequestDto();
    	}
		logger.info("ドローンポート予約情報削除:  ===== START =====");
		logger.debug(reserveId);
		logger.debug(dto.toString());
		
        // ドローンポート予約情報削除入力チェック
		dronePortInfoValidator.validateForDelete(dto);
    	validator.validateForGetDetail(reserveId);
    	
        // ドローンポート予約情報削除サービス呼び出し
    	service.delete(reserveId, dto);
    	
		logger.info("ドローンポート予約情報削除:  ===== END =====");
        // 成功時はHTTPステータス:200を返却。レスポンスボディなし。
    }

    /**
     * ドローンポート予約情報一覧取得API
     * @param dto ドローンポート予約情報一覧取得要求
     * @return ドローンポート予約情報一覧取得応答
     */
    @GetMapping("/list")
    public ResponseEntity<DronePortReserveInfoListResponseDto> getList(@QueryStringArgs DronePortReserveInfoListRequestDto dto) {
		logger.info("ドローンポート予約情報一覧取得:  ===== START =====");
		logger.debug(dto.toString());
		
        // ドローンポート予約情報一覧取得入力チェック
    	validator.validateForGetList(dto);
    	
		// クエリパラメータにソート順が設定されていない場合は、システム設定のデフォルト値を設定する
		if (!StringUtils.hasText(dto.getSortOrders())) {
			dto.setSortOrders(systemSettings.getString(
					DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT, 
					DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
		}
		// クエリパラメータにソート対象列名が設定されていない場合は、システム設定のデフォルト値を設定する
		if (!StringUtils.hasText(dto.getSortColumns())) {
			dto.setSortColumns(systemSettings.getString(
					DronePortConstants.SETTINGS_DRONEPORT_RESERVEINFOLIST_DEFAULT, 
					DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
		}
		
        // ドローンポート予約情報一覧取得サービス呼び出し
    	DronePortReserveInfoListResponseDto responseDto = service.getList(dto);
    	
    	logger.debug(responseDto.toString());
    	logger.info("ドローンポート予約情報一覧取得:  ===== END =====");
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    // ドローンポート予約情報詳細取得
    /** 
     * ドローンポート予約情報詳細取得
     * @param reserveId 予約ID
     * @return ドローンポート予約情報詳細取得応答
     */
    @GetMapping("/detail/{reserveId}")
    public ResponseEntity<DronePortReserveInfoDetailResponseDto> getDetail(@PathVariable("reserveId") String reserveId) {
		logger.info("ドローンポート予約情報詳細取得:  ===== START =====");
		logger.debug(reserveId);
		
        // ドローンポート情報詳細取得入力チェック
    	validator.validateForGetDetail(reserveId);
    	
        // ドローンポート情報詳細取得サービス呼び出し
    	DronePortReserveInfoDetailResponseDto responseDto = service.getDetail(reserveId);
    	
    	logger.debug(responseDto.toString());
    	logger.info("ドローンポート予約情報詳細取得:  ===== END =====");
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
