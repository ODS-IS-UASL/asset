package com.hitachi.droneroute.cmn.settings;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** CodeMasterクラスの単体テスト */
public class CodeMasterTest {

  private CodeMaster codeMaster;

  @BeforeEach
  public void setUp() {
    codeMaster = new CodeMaster();
    Map<String, Map<Integer, String>> codeclass = new HashMap<>();
    Map<Integer, String> innerMap = new HashMap<>();
    innerMap.put(1, "One");
    innerMap.put(2, "Two");
    codeclass.put("testClass", innerMap);
    codeMaster.setCodeclass(codeclass);
  }

  /**
   * メソッド名: getIntegerArray<br>
   * 試験名: 正常なクラス名を入力した場合、正しい整数配列が返されること<br>
   * 条件: クラス名 "testClass" を入力<br>
   * 結果: [1, 2] が返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetIntegerArray_Normal() {
    Integer[] expected = {1, 2};
    Integer[] result = codeMaster.getIntegerArray("testClass");
    assertArrayEquals(expected, result);
  }

  /**
   * メソッド名: getIntegerArray<br>
   * 試験名: 存在しないクラス名を入力した場合、NullPointerExceptionがスローされること<br>
   * 条件: クラス名 "nonExistentClass" を入力<br>
   * 結果: NullPointerException がスローされる<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetIntegerArray_NonExistentClass() {
    assertThrows(
        NullPointerException.class,
        () -> {
          codeMaster.getIntegerArray("nonExistentClass");
        });
  }

  /**
   * メソッド名: getIntegerArray<br>
   * 試験名: クラス名がnullの場合、NullPointerExceptionがスローされること<br>
   * 条件: クラス名 null を入力<br>
   * 結果: NullPointerException がスローされる<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetIntegerArray_NullClassName() {
    assertThrows(
        NullPointerException.class,
        () -> {
          codeMaster.getIntegerArray(null);
        });
  }

  /**
   * メソッド名: getIntegerArray<br>
   * 試験名: codeclassがnullの場合、NullPointerExceptionがスローされること<br>
   * 条件: codeclass を null に設定<br>
   * 結果: NullPointerException がスローされる<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetIntegerArray_NullCodeclass() {
    codeMaster.setCodeclass(null);
    assertThrows(
        NullPointerException.class,
        () -> {
          codeMaster.getIntegerArray("testClass");
        });
  }

  /**
   * メソッド名: getIntegerArray<br>
   * 試験名: codeclassが空の場合、NullPointerExceptionがスローされること<br>
   * 条件: codeclass を空のマップに設定<br>
   * 結果: NullPointerException がスローされる<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetIntegerArray_EmptyCodeclass() {
    codeMaster.setCodeclass(new HashMap<>());
    assertThrows(
        NullPointerException.class,
        () -> {
          codeMaster.getIntegerArray("testClass");
        });
  }
}
