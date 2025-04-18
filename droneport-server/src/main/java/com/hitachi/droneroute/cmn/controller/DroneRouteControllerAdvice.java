package com.hitachi.droneroute.cmn.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.hitachi.droneroute.cmn.dto.ErrorResponse;
import com.hitachi.droneroute.cmn.exception.AppErrorException;
import com.hitachi.droneroute.cmn.exception.NotFoundException;
import com.hitachi.droneroute.cmn.exception.ServiceErrorException;
import com.hitachi.droneroute.cmn.exception.ValidationErrorException;

/**
 * コントローラーの共通処理を行うするAdviceクラス
 * @author Hiroshi Toyoda
 *
 */
@ControllerAdvice
public class DroneRouteControllerAdvice {
	
	// https://qiita.com/niwasawa/items/f5a6a285d7bd99e8273a
	// https://spring.pleiades.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ExceptionHandler.html
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * AppErrorException発生時のレスポンスを設定する
	 * @param e AppErrorExceptionクラス
	 * @return レスポンス
	 */
	@ExceptionHandler({AppErrorException.class})
	public ResponseEntity<ErrorResponse> handleAppErrorException(Exception e) {
		logger.warn(e.toString(), e);
		return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * NotFoundException発生時のレスポンスを設定する
	 * @param e NotFoundExceptionクラス
	 * @return レスポンス
	 */
	@ExceptionHandler({NotFoundException.class})
	public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e) {
		logger.warn(e.toString(), e);
		return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
	}

	/**
	 * ValidationErrorException発生時のレスポンスを設定する
	 * @param e ValidationErrorExceptionクラス
	 * @return レスポンス
	 */
	@ExceptionHandler({ValidationErrorException.class})
	public ResponseEntity<ErrorResponse> handleValidationErrorException(Exception e) {
		logger.warn(e.toString(), e);
		return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
	}

	/**
	 * ServiceErrorException発生時のレスポンスを設定する
	 * @param e ServiceErrorExceptionクラス
	 * @return レスポンス
	 */
	@ExceptionHandler({ServiceErrorException.class})
	public ResponseEntity<ErrorResponse> handleServiceException(Exception e) {
		logger.warn(e.toString(), e);
		return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * HttpMessageNotReadableException発生時のレスポンスを設定する
	 * @param e HttpMessageNotReadableExceptionクラス
	 * @return レスポンス
	 */
	@ExceptionHandler({HttpMessageNotReadableException.class})
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(Exception e) {
		logger.warn(e.toString(), e);
		return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * その他例外発生時のレスポンスを設定する
	 * @param e Exceptionクラス
	 * @return レスポンス
	 */
	@ExceptionHandler({Exception.class})
	public ResponseEntity<ErrorResponse> handleOtherException(Exception e) {
		logger.error(e.toString(), e);
		return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
