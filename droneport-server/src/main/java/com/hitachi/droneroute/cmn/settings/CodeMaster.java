package com.hitachi.droneroute.cmn.settings;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * コード定義
 * @author Hiroshi Toyoda
 *
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "codemaster")
public class CodeMaster {
    private Map<String, Map<Integer, String>> codeclass;

    /**
     * 指定した分類の全てのコードを整数配列で取得する
     * @param className 分類名
     * @return 整数配列
     */
    public Integer[] getIntegerArray(String className) {
    	return codeclass.get(className).keySet().toArray(Integer[]::new);
    }
    
    /**
     * 指定した分類、コードの名称(文字列)を取得する
     * @param className 分類名
     * @param code コード値
     * @return 名称
     */
    public String getString(String className, Integer code) {
    	return codeclass.get(className).get(code);
    }
}
