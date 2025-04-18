package com.hitachi.droneroute.cmn.exception;

/**
 * 検索結果が該当なしの場合の例外クラス
 * @author Hiroshi Toyoda
 *
 */
public class NotFoundException extends RuntimeException {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
		super(message);
	}
}
