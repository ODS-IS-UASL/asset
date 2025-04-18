package com.hitachi.droneroute.cmn.exception;

/**
 * 入力パラメータエラー発生時の例外クラス
 * 
 * @author Hiroshi Toyoda
 *
 */
public class ValidationErrorException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public ValidationErrorException(String message) {
		super(message);
	}
}
