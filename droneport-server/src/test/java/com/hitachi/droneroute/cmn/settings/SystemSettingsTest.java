package com.hitachi.droneroute.cmn.settings;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** SystemSettingsクラスの単体テスト */
public class SystemSettingsTest {

  private SystemSettings systemSettings;

  @BeforeEach
  public void setUp() {
    systemSettings = new SystemSettings();
    Map<String, String> innerMap = new HashMap<>();
    innerMap.put("key1", "value1");
    innerMap.put("key2", "1,2,3");
    innerMap.put("key3", "123");
    innerMap.put("key4", "true");
    Map<String, Map<String, String>> outerMap = new HashMap<>();
    outerMap.put("class1", innerMap);
    systemSettings.setSettingclass(outerMap);
  }

  /**
   * メソッド名: getString<br>
   * 試験名: getStringメソッドの正常系テスト<br>
   * 条件: 有効なクラス名とキー名を渡す<br>
   * 結果: 期待される文字列が返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetString_Normal() {
    assertEquals("value1", systemSettings.getString("class1", "key1"));
  }

  /**
   * メソッド名: getString<br>
   * 試験名: getStringメソッドの異常系テスト<br>
   * 条件: 無効なクラス名を渡す<br>
   * 結果: NullPointerExceptionがスローされる<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetString_InvalidClass() {
    assertThrows(
        NullPointerException.class,
        () -> {
          systemSettings.getString("invalidClass", "key1");
        });
  }

  /**
   * メソッド名: getStringValueArray<br>
   * 試験名: getStringValueArrayメソッドの正常系テスト<br>
   * 条件: 有効なクラス名とキー名を渡す<br>
   * 結果: 期待される配列が返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetStringValueArray_Normal() {
    assertArrayEquals(
        new String[] {"1", "2", "3"}, systemSettings.getStringValueArray("class1", "key2"));
  }

  /**
   * メソッド名: getIntegerValueArray<br>
   * 試験名: getIntegerValueArrayメソッドの正常系テスト<br>
   * 条件: 有効なクラス名とキー名を渡す<br>
   * 結果: 期待される配列が返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetIntegerValueArray_Normal() {
    assertArrayEquals(
        new Integer[] {1, 2, 3}, systemSettings.getIntegerValueArray("class1", "key2"));
  }

  /**
   * メソッド名: getIntegerValue<br>
   * 試験名: getIntegerValueメソッドの正常系テスト<br>
   * 条件: 有効なクラス名とキー名を渡す<br>
   * 結果: 期待される整数が返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetIntegerValue_Normal() {
    assertEquals(123, systemSettings.getIntegerValue("class1", "key3"));
  }

  /**
   * メソッド名: getIntegerValue<br>
   * 試験名: getIntegerValueメソッドの異常系テスト<br>
   * 条件: 数字以外の文字列を渡す<br>
   * 結果: NumberFormatExceptionがスローされる<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetIntegerValue_InvalidNumber() {
    assertThrows(
        NumberFormatException.class,
        () -> {
          systemSettings.getIntegerValue("class1", "key1");
        });
  }

  /**
   * メソッド名: getBoolean<br>
   * 試験名: getBooleanメソッドの正常系テスト<br>
   * 条件: 有効なクラス名とキー名を渡す<br>
   * 結果: 期待されるブール値が返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetBoolean_Normal() {
    assertTrue(systemSettings.getBoolean("class1", "key4"));
  }

  /**
   * メソッド名: getBoolean<br>
   * 試験名: getBooleanメソッドの異常系テスト<br>
   * 条件: ブール値以外の文字列を渡す<br>
   * 結果: falseが返される<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetBoolean_InvalidBoolean() {
    assertFalse(systemSettings.getBoolean("class1", "key1"));
  }

  /**
   * メソッド名: getSettingclass<br>
   * 試験名: getSettingclassメソッドの正常系テスト<br>
   * 条件: getSettingclassを呼び出す<br>
   * 結果: null以外が返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetSettingClass() {
    assertNotNull(systemSettings.getSettingclass());
  }
}
