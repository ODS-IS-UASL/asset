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
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoDetailResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoListResponseDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoRequestDto;
import com.hitachi.droneroute.arm.dto.AircraftReserveInfoResponseDto;
import com.hitachi.droneroute.arm.service.AircraftReserveInfoService;
import com.hitachi.droneroute.arm.validator.AircraftInfoValidator;
import com.hitachi.droneroute.arm.validator.AircraftReserveInfoValidator;
import com.hitachi.droneroute.cmn.resolver.QueryStringArgs;
import com.hitachi.droneroute.cmn.settings.SystemSettings;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;

import lombok.RequiredArgsConstructor;

/**
 * 機体リソース管理機体予約APIのコントローラ
 * 
 * @author Ikkan Suzuki 
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("${droneroute.basepath}/aircraft/reserve")
public class AircraftReserveInfoController {
    
	// ロガー
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// 機体予約情報APIパラメータチェック
	private final AircraftReserveInfoValidator validator;
	// 機体情報APIパラメータチェック
	private final AircraftInfoValidator aircraftInfoValidator;
	
	// 機体予約情報APIサービスクラス
	private final AircraftReserveInfoService service;
	
	// システム設定
	private final SystemSettings systemSettings;

	/**
	 * 機体予約登録
	 * @param dto
	 * @return
	 */
	@PostMapping()
    public ResponseEntity<AircraftReserveInfoResponseDto> post(@RequestBody AircraftReserveInfoRequestDto dto) {
		logger.info("機体予約情報登録:  ===== START =====");
		logger.debug(dto.toString());

    	// 入力チェック
		validator.validateForRegist(dto);
		
		// サービス呼び出し
		AircraftReserveInfoResponseDto responseDto = service.postData(dto);
		logger.info("機体予約情報登録:  ===== END =====");
		
        // 処理結果編集
    	return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
	/**
	 * 機体予約更新
	 * @param dto
	 * @return
	 */
	@PutMapping()
    public ResponseEntity<AircraftReserveInfoResponseDto> put(@RequestBody AircraftReserveInfoRequestDto dto) {
		logger.info("機体予約情報更新:  ===== START =====");
		logger.debug(dto.toString());

    	// 入力チェック
		validator.validateForUpdate(dto);
		
		// サービス呼び出し
		AircraftReserveInfoResponseDto responseDto = service.putData(dto);
		logger.info("機体予約情報更新:  ===== END =====");

        // 処理結果編集
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

	/**
	 * 機体予約一覧
	 * @param dto
	 * @return
	 */
	@GetMapping("/list")
    public ResponseEntity<AircraftReserveInfoListResponseDto> getList(@QueryStringArgs AircraftReserveInfoListRequestDto dto) {
		logger.info("機体予約一覧:  ===== START =====");
		logger.debug(dto.toString());

        // 入力チェック
		validator.validateForGetList(dto);
		
    	// MVP1指摘対応　ソート順機能追加
		// クエリパラメータにソート順が設定されていない場合は、システム設定のデフォルト値を設定する
		if (!StringUtils.hasText(dto.getSortOrders())) {
			dto.setSortOrders(systemSettings.getString(
					DronePortConstants.SETTINGS_AIRCRAFT_RESERVEINFOLIST_DEFAULT, 
					DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_ORDERS));
		}
		
    	// MVP1指摘対応　ソート順機能追加
		// クエリパラメータにソート対象列名が設定されていない場合は、システム設定のデフォルト値を設定する
		if (!StringUtils.hasText(dto.getSortColumns())) {
			dto.setSortColumns(systemSettings.getString(
					DronePortConstants.SETTINGS_AIRCRAFT_RESERVEINFOLIST_DEFAULT, 
					DronePortConstants.SETTINGS_GETLIST_DEFAULT_SORT_COLUMNS));
		}
		
        // サービス呼び出し
		AircraftReserveInfoListResponseDto responseDto = service.getList(dto);
		logger.info("機体予約一覧:  ===== END =====");

        // 処理結果編集
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

	/**
	 * 機体予約詳細
	 * @param aircraftReserveId
	 * @return
	 */
	@GetMapping("/detail/{aircraftReserveId}")
    public ResponseEntity<AircraftReserveInfoDetailResponseDto> getDetail(@PathVariable("aircraftReserveId") String aircraftReserveId) {
		logger.info("機体予約情報詳細:  ===== START =====");
		logger.debug(aircraftReserveId);
		
    	// 入力チェック
		validator.validateForDetail(aircraftReserveId);
		
        // サービス呼び出し
		AircraftReserveInfoDetailResponseDto responseDto = service.getDetail(aircraftReserveId);
	
		logger.info("機体予約情報詳細:  ===== END =====");
        // 処理結果編集
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    
    /**
     * 機体予約削除
     * @param aircraftReserveId
     * @param dto 機体予約情報削除要求
     */
    @DeleteMapping("/{aircraftReserveId}")
    public void delete(
    		@PathVariable("aircraftReserveId") String aircraftReserveId,
    		@Nullable @RequestBody AircraftInfoDeleteRequestDto dto) {
    	if (Objects.isNull(dto)) {
    		// リクエストボディが存在しない場合は、空のDTOを設定する。
    		dto = new AircraftInfoDeleteRequestDto();
    	}
		logger.info("機体予約情報削除:  ===== START =====");
		logger.debug(aircraftReserveId);
		
    	// 入力チェック
		aircraftInfoValidator.validateForDelete(dto);
		validator.validateForDetail(aircraftReserveId);
		
        // サービス呼び出し
		service.deleteData(aircraftReserveId, dto);
		
		logger.info("機体予約情報削除:  ===== END =====");
    }
    
}
