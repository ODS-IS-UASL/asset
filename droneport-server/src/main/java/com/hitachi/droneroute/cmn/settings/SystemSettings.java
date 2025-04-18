package com.hitachi.droneroute.cmn.settings;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * システム設定
 * @author Hiroshi Toyoda
 *
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "systemsettings")
public class SystemSettings {
	private Map<String, Map<String, String>> settingclass;
	
	/**
	 * システム設定を文字列で取得する
	 * @param className 分類名
	 * @param keyName キー名
	 * @return システム設定値
	 */
	public String getString(String className, String keyName) {
		return settingclass.get(className).get(keyName);
	}
	
	/**
	 * システム設定をカンマ区切りで分解して文字列配列で取得する
	 * @param className 分類名
	 * @param keyName キー名
	 * @return カンマ区切りで配列に分解したシステム設定値
	 */
	public String[] getStringValueArray(String className, String keyName) {
		return settingclass.get(className).get(keyName).split(",");
	}
	
	/**
	 * システム設定値を整数変換して取得する
	 * @param className 分類名
	 * @param keyName キー名
	 * @return 整数変換したシステム設定値
	 */
	public Integer getIntegerValue(String className, String keyName) {
		return Integer.parseInt(settingclass.get(className).get(keyName));
		
	}
	
	/**
	 * システム設定をカンマ区切りで分解して整数変換した配列で取得する
	 * @param className 分類名
	 * @param keyName キー名
	 * @return 整数変換したシステム設定値
	 */
	public Integer[] getIntegerValueArray(String className, String keyName) {
		return Stream.of(getStringValueArray(className, keyName))
				.map(e -> Integer.parseInt(e))
				.collect(Collectors.toList())
				.toArray(Integer[]::new);
		
	}

	/**
	 * システム設定値を真偽変換して取得する
	 * @param className 分類名
	 * @param keyName キー名
	 * @return 真偽変換したシステム設定値
	 */
	public boolean getBoolean(String className, String keyName) {
		return Boolean.parseBoolean(settingclass.get(className).get(keyName));
	}
}
