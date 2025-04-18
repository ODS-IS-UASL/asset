package com.hitachi.droneroute.cmn.util;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * base64形式の読取、変換を行うクラス
 * @author Hiroshi Toyoda
 *
 */
public class Base64Utils {
	
	private final String DATAURI_BASE64_PATTERN = "^data:image/(?<subtype>(%s));base64,";
	private final String DATAURI_BASE64 = "data:image/%s;base64,%s";
	
	private String dataUriBase64Pattern = null;
	
	/**
	 * コンストラクタ
	 * @param subtype
	 */
	public Base64Utils(String[] subtype) {
		dataUriBase64Pattern = createDataUriBase64Pattern(subtype);
	}
	
	/**
	 * dataURIの正規表現を作成する
	 * @param subtype サポートするMIMEサブタイプ(配列)
	 * @return MIMEサブタイプを埋め込んだdataURL正規表現
	 */
	private String createDataUriBase64Pattern(String[] subtype) {
		String wkSubtype = "";
		for (String typeName : subtype) {
			if (wkSubtype.length() > 0) {
				wkSubtype += "|";
			}
			wkSubtype += typeName;
		}
		return String.format(DATAURI_BASE64_PATTERN, wkSubtype);
	}
	
	/**
	 * base64を含んだdataURIのMIMEサブタイプがサポートするものかチェックする
	 * @param dataUriWithBase64 base64を含んだdataURI
	 * @return true:サポートするMIMEサブタイプが設定されている, false:未サポートのMIMEサブタイプが設定されている、またはdataURIの書式が不正
	 */
	public boolean checkSubtype(String dataUriWithBase64) {
		Matcher matcher = Pattern
				.compile(dataUriBase64Pattern)
				.matcher(dataUriWithBase64);
		return matcher.find();
	}
	
	/**
	 * base64を含んだdataURIからMIMEサブタイプを取得する
	 * @param dataUriWithBase64 base64を含んだdataURI
	 * @return MIMEサブタイプ
	 */
	public String getSubtype(String dataUriWithBase64) {
		String typeName = null;
		Matcher matcher = Pattern
				.compile(dataUriBase64Pattern)
				.matcher(dataUriWithBase64);
		if (matcher.find()) {
			typeName = matcher.group("subtype");
		}
		return typeName;
	}
	
	/**
	 * base64を含んだdataURIからバイナリデータを抽出する
	 * @param dataUriWithBase64 base64を含んだdataURI
	 * @return バイナリデータ
	 */
	public byte[] getBinaryData(String dataUriWithBase64) {
		byte[] binaryData = null;
		Matcher matcher = Pattern
				.compile(dataUriBase64Pattern)
				.matcher(dataUriWithBase64);
		if (matcher.find()) {
			String base64String = dataUriWithBase64.substring(matcher.end());
			binaryData = Base64.getDecoder().decode(base64String);
		}
		return binaryData;
	}
	
	/**
	 * base64を含んだdataURIを生成する
	 * @param typeName MIMEサブタイプ
	 * @param binaryData バイナリデータ
	 * @return base64を含んだdataURI
	 */
	public String createDataUriWithBase64(String typeName, byte[] binaryData) {
		return String.format(DATAURI_BASE64, typeName, Base64.getEncoder().encodeToString(binaryData));
	}

}
