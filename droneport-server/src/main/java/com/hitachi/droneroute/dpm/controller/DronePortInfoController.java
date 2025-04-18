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
import com.hitachi.droneroute.dpm.dto.DronePortEnvironmentInfoResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDeleteRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoDetailResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoListResponseDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterRequestDto;
import com.hitachi.droneroute.dpm.dto.DronePortInfoRegisterResponseDto;
import com.hitachi.droneroute.dpm.service.DronePortInfoService;
import com.hitachi.droneroute.dpm.validator.DronePortInfoValidator;

import lombok.RequiredArgsConstructor;

/**
 * ドローンポート情報APIのコントローラ
 * 
 * @author Hiroshi Toyoda
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("${droneroute.basepath}/droneport")
public class DronePortInfoController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// ドローンポート情報関連APIのパラメータチェッククラス
	private final DronePortInfoValidator validator;
	
	// ドローンポート情報関連APIのサービスクラス
	private final DronePortInfoService service;
    
	// システム設定
	private final SystemSettings systemSettings;
	
    /**
     * ドローンポート情報登録
     * @param dto ドローンポート情報登録更新要求
     * @return ドローンポート情報登録更新応答
     */
	@PostMapping("/info")
    public ResponseEntity<DronePortInfoRegisterResponseDto> post(@RequestBody DronePortInfoRegisterRequestDto dto) {
		logger.info("ドローンポート情報登録:  ===== START =====");
		logger.debug(dto.toString());
		
		// 画像(base64)をバイナリに変換しておく
		service.decodeBinary(dto);
        // ドローンポート情報登録入力チェック
    	validator.validateForRegist(dto);
    	
        // ドローンポート情報登録サービス呼び出し
    	DronePortInfoRegisterResponseDto responseDto = service.register(dto);	
    	
		logger.debug(responseDto.toString());
		logger.info("ドローンポート情報登録:  ===== END =====");
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    /** 
     * ドローンポート情報更新
     * @param dto ドローンポート情報登録更新要求
     * @return ドローンポート情報登録更新応答
     */
    @PutMapping("/info")
    public ResponseEntity<DronePortInfoRegisterResponseDto> put(@RequestBody DronePortInfoRegisterRequestDto dto) {
		logger.info("ドローンポート情報更新:  ===== START =====");
		logger.debug(dto.toString());
		
		// 画像(base64)をバイナリに変換しておく
		service.decodeBinary(dto);
        // ドローンポート情報更新入力チェック
    	validator.validateForUpdate(dto);
    	
        // ドローンポート情報更新サービス呼び出し
    	DronePortInfoRegisterResponseDto responseDto = service.update(dto);
    	
		logger.debug(responseDto.toString());
		logger.info("ドローンポート情報更新:  ===== END =====");
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    /**
     * ドローンポート情報削除
     * @param dronePortId ドローンポートID
     * @param dto ドローンポート情報削除要求
     */
    @DeleteMapping("/info/{dronePortId}")
    public void delete(
    		@PathVariable("dronePortId") String dronePortId, 
    		@Nullable @RequestBody DronePortInfoDeleteRequestDto dto) {
    	if (Objects.isNull(dto)) {
    		// リクエストボディが存在しない場合は、空のDTOを設定する。
    		dto = new DronePortInfoDeleteRequestDto();
    	}
		logger.info("ドローンポート情報削除(パスパラメータ版):  ===== START =====");
		logger.debug(dronePortId);
		logger.debug(dto.toString());
		
        // ドローンポート情報削除入力チェック
		validator.validateForDelete(dto);
    	validator.validateForGetDetail(dronePortId);
    	
        // ドローンポート情報削除サービス呼び出し
    	service.delete(dronePortId, dto);
    	
        // 成功時はHTTPステータス:200を返却。レスポンスボディなし。
		logger.info("ドローンポート情報削除:  ===== END =====");
    }
    
    /**
     * ドローンポート情報一覧取得API
     * @param dto ドローンポート情報一覧取得要求
     * @return ドローンポート情報一覧取得応答
     */
    @GetMapping("/info/list")
    public ResponseEntity<DronePortInfoListResponseDto> getList(@QueryStringArgs DronePortInfoListRequestDto dto) {
		logger.info("ドローンポート情報一覧取得:  ===== START =====");
		logger.debug(dto.toString());
		
        // ドローンポート情報一覧取得入力チェック
    	validator.validateForGetList(dto);
    	
		// クエリパラメータにソート順が設定されていない場合は、システム設定のデフォルト値を設定する
		if (!StringUtils.hasText(dto.getSortOrders())) {
			dto.setSortOrders(systemSettings.getString(
					DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT, 
					DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
		}
		// クエリパラメータにソート対象列名が設定されていない場合は、システム設定のデフォルト値を設定する
		if (!StringUtils.hasText(dto.getSortColumns())) {
			dto.setSortColumns(systemSettings.getString(
					DronePortConstants.SETTINGS_DRONEPORT_INFOLIST_DEFAULT, 
					DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
		}
		
        // ドローンポート情報一覧取得サービス呼び出し
    	DronePortInfoListResponseDto responseDto = service.getList(dto);
    	
    	logger.debug(responseDto.toString());
		logger.info("ドローンポート情報一覧取得:  ===== END =====");
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    /** 
     * ドローンポート情報詳細取得
     * @param dronePortId ドローンポートID
     * @return ドローンポート情報詳細取得応答
     */
    @GetMapping("/info/detail/{dronePortId}")
    public ResponseEntity<DronePortInfoDetailResponseDto> getDetail(@PathVariable("dronePortId") String dronePortId) {
		logger.info("ドローンポート情報詳細取得:  ===== START =====");
		logger.debug(dronePortId);
		
        // ドローンポート情報詳細取得入力チェック
    	validator.validateForGetDetail(dronePortId);
    	
        // ドローンポート情報詳細取得サービス呼び出し
    	DronePortInfoDetailResponseDto responseDto = service.getDetail(dronePortId);
    	
    	logger.debug(responseDto.toString());
		logger.info("ドローンポート情報詳細取得:  ===== END =====");
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    /**
     * ドローンポート周辺情報取得
     * @param dronePortId ドローンポートID
     * @return ドローンポート周辺情報取得
     */
    @GetMapping("/environment/{dronePortId}")
    public ResponseEntity<DronePortEnvironmentInfoResponseDto> getEnvironment(@PathVariable("dronePortId") String dronePortId) {
		logger.info("ドローンポート周辺情報取得:  ===== START =====");
		logger.debug(dronePortId);
		
        // ドローンポート情報詳細取得入力チェックを流用する
    	validator.validateForGetDetail(dronePortId);
		
		// ドローンポート周辺情報取得サービス呼出
    	DronePortEnvironmentInfoResponseDto responseDto = service.getEnvironment(dronePortId);
    	
    	logger.debug(responseDto.toString());
		logger.info("ドローンポート周辺情報取得:  ===== END =====");
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
}
