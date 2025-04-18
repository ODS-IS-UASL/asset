package com.hitachi.droneroute.cmn.exception;

/**
 * サービスクラスでエラー発生時の例外クラス
 * @author Hiroshi Toyoda
 *
 */
public class ServiceErrorException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public ServiceErrorException(String message) {
		super(message);
	}

	public ServiceErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
