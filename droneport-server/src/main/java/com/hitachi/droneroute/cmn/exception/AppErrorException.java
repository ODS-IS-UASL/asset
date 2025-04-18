package com.hitachi.droneroute.cmn.exception;

/**
 * アプリケーション内でエラー発生時の例外クラス
 * 
 * @author Hiroshi Toyoda
 *
 */
public class AppErrorException extends RuntimeException {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public AppErrorException(String message) {
		super(message);
	}
}
