package com.hitachi.droneroute.arm.controller;

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

import com.hitachi.droneroute.arm.dto.AircraftInfoDeleteRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftInfoSearchListResponseDto;
import com.hitachi.droneroute.arm.service.AircraftInfoService;
import com.hitachi.droneroute.arm.validator.AircraftInfoValidator;
import com.hitachi.droneroute.cmn.resolver.QueryStringArgs;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;

import lombok.RequiredArgsConstructor;

/**
 * 機体リソース管理機体情報APIのコントローラ
 * 
 * @author Ikkan Suzuki 
 */
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
	
	// システム設定
	private final SystemSettings systemSettings;
	
	/**
	 * 機体情報登録
	 * @param dto
	 * @return
	 */
	@PostMapping()
    public ResponseEntity<AircraftInfoResponseDto> post(@RequestBody AircraftInfoRequestDto dto) {
		logger.info("機体情報登録:  ===== START =====");
		logger.debug(dto.toString());
		
		// 画像データ変換
		service.decodeBinary(dto);
		
		// 入力チェック
		validator.validateForRegist(dto);
		
        // サービス呼び出し
		AircraftInfoResponseDto responseDto = service.postData(dto);
		
		logger.info("機体情報登録:  ===== END =====");
        // 処理結果編集
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
 
	/**
	 * 機体情報更新
	 * @param dto
	 * @return
	 */
	@PutMapping()
	public ResponseEntity<AircraftInfoResponseDto> put(@RequestBody AircraftInfoRequestDto dto) {
		logger.info("機体情報更新:  ===== START =====");
		logger.debug(dto.toString());
		
		// 画像データ変換
		service.decodeBinary(dto);
		
        // 入力チェック
		validator.validateForUpdate(dto);
		
        // サービス呼び出し
		AircraftInfoResponseDto responseDto = service.putData(dto);
		
		logger.info("機体情報更新:  ===== END =====");
		// 処理結果編集
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
	/**
	 * 機体情報一覧
	 * @param dto
	 * @return
	 */
	@GetMapping("/list")
    public ResponseEntity<AircraftInfoSearchListResponseDto> getList(@QueryStringArgs AircraftInfoSearchListRequestDto dto) {
		logger.info("機体情報一覧:  ===== START =====");
		logger.debug(dto.toString());
		
        // 入力チェック
    	validator.validateForGetList(dto);

    	// MVP1指摘対応　ソート順機能追加
		// クエリパラメータにソート順が設定されていない場合は、システム設定のデフォルト値を設定する
		if (!StringUtils.hasText(dto.getSortOrders())) {
			dto.setSortOrders(systemSettings.getString(
					DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT, 
					DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
		}
		
    	// MVP1指摘対応　ソート順機能追加
		// クエリパラメータにソート対象列名が設定されていない場合は、システム設定のデフォルト値を設定する
		if (!StringUtils.hasText(dto.getSortColumns())) {
			dto.setSortColumns(systemSettings.getString(
					DronePortConstants.SETTINGS_AIRCRAFT_INFOLIST_DEFAULT, 
					DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
		}
		
        // サービス呼び出し
    	AircraftInfoSearchListResponseDto responseDto = service.getList(dto);
    	
		logger.info("機体情報一覧:  ===== END =====");
       // 処理結果編集
		return new ResponseEntity<>(responseDto, HttpStatus.OK);

    }
    
	/**
	 * 機体情報詳細
	 * @param aircraftId
	 * @return
	 */
	@GetMapping("/detail/{aircraftId}")
	public ResponseEntity<AircraftInfoDetailResponseDto> getDetail(@PathVariable("aircraftId") String aircraftId) {
		logger.info("機体情報詳細:  ===== START =====");
		logger.debug(aircraftId);
		
		// 入力チェック
		validator.validateForDetail(aircraftId);

        // サービス呼び出し
		AircraftInfoDetailResponseDto responseDto = service.getDetail(aircraftId);
		
		logger.info("機体情報詳細:  ===== END =====");
        // 処理結果編集
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
	/**
	 * 機体情報削除
	 * @param aircraftId
     * @param dto 機体情報削除要求
	 */
	@DeleteMapping("/{aircraftId}")
    public void delete(
    		@PathVariable("aircraftId") String aircraftId,
    		@Nullable @RequestBody AircraftInfoDeleteRequestDto dto) {
		if (Objects.isNull(dto)) {
    		// リクエストボディが存在しない場合は、空のDTOを設定する。
			dto = new AircraftInfoDeleteRequestDto();
		}
		logger.info("機体情報削除:  ===== START =====");
		logger.debug(aircraftId);
		logger.debug(dto.toString());
		
		// 入力チェック
		validator.validateForDelete(dto);
		validator.validateForDetail(aircraftId);

        // サービス呼び出し
		service.deleteData(aircraftId, dto);
		
		logger.info("機体情報削除:  ===== END =====");

    }
    
}


