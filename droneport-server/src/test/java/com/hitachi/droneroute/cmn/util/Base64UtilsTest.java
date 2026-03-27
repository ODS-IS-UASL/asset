package com.hitachi.droneroute.cmn.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Base64Utilsクラスの単体テスト */
public class Base64UtilsTest {

  /**
   * メソッド名: checkSubtype<br>
   * 試験名: checkSubtypeメソッドの正常動作<br>
   * 条件: 有効なデータURIを渡す<br>
   * 結果: サブタイプが一致する<br>
   * テストパターン：正常系
   */
  @Test
  public void testCheckSubtype_Normal() {
    String[] subtypes = {"jpeg", "png"};
    Base64Utils utils = new Base64Utils(subtypes);
    String dataUri = "data:image/jpeg;base64,abcd";
    assertTrue(utils.checkSubtype(dataUri));
  }

  /**
   * メソッド名: checkSubtype<br>
   * 試験名: checkSubtypeメソッドの異常動作<br>
   * 条件: 無効なデータURIを渡す<br>
   * 結果: サブタイプが一致しない<br>
   * テストパターン：異常系
   */
  @Test
  public void testCheckSubtype_Invalid() {
    String[] subtypes = {"jpeg", "png"};
    Base64Utils utils = new Base64Utils(subtypes);
    String dataUri = "data:image/gif;base64,abcd";
    assertFalse(utils.checkSubtype(dataUri));
  }

  /**
   * メソッド名: checkMimeType<br>
   * 試験名: checkMimeTypeメソッドの正常動作<br>
   * 条件: 有効なデータURIを渡す<br>
   * 結果: MIMEが一致する<br>
   * テストパターン：正常系
   */
  @Test
  public void testCheckMimeType_Normal() {
    String[] mimes = {"image/jpeg", "image/png"};
    Base64Utils utils = new Base64Utils(mimes);
    String dataUri = "data:image/jpeg;base64,abcd";
    assertTrue(utils.checkMimeType(dataUri));
  }

  /**
   * メソッド名: checkMimeType<br>
   * 試験名: checkMimeTypeメソッドの異常動作<br>
   * 条件: 無効なデータURIを渡す<br>
   * 結果: サブタイプが一致しない<br>
   * テストパターン：異常系
   */
  @Test
  public void testCheckMimeType_Invalid() {
    String[] mimes = {"image/jpeg", "image/png"};
    Base64Utils utils = new Base64Utils(mimes);
    String dataUri = "data:image/gif;base64,abcd";
    assertFalse(utils.checkMimeType(dataUri));
  }

  /**
   * メソッド名: getSubtype<br>
   * 試験名: getSubtypeメソッドの正常動作<br>
   * 条件: 有効なデータURIを渡す<br>
   * 結果: 正しいサブタイプが返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetSubtype_Normal() {
    String[] subtypes = {"jpeg", "png"};
    Base64Utils utils = new Base64Utils(subtypes);
    String dataUri = "data:image/jpeg;base64,abcd";
    assertEquals("jpeg", utils.getSubtype(dataUri));
  }

  /**
   * メソッド名: getSubtype<br>
   * 試験名: getSubtypeメソッドの異常動作<br>
   * 条件: 無効なデータURIを渡す<br>
   * 結果: サブタイプがnullである<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetSubtype_Invalid() {
    String[] subtypes = {"jpeg", "png"};
    Base64Utils utils = new Base64Utils(subtypes);
    String dataUri = "data:image/gif;base64,abcd";
    assertNull(utils.getSubtype(dataUri));
  }

  /**
   * メソッド名: getMimeType<br>
   * 試験名: getMimeTypeメソッドの正常動作<br>
   * 条件: 有効なデータURIを渡す<br>
   * 結果: 正しいMIMEタイプが返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetMimeType_Normal() {
    String[] mimes = {"image/jpeg", "image/png"};
    Base64Utils utils = new Base64Utils(mimes);
    String dataUri = "data:image/jpeg;base64,abcd";
    assertEquals("image/jpeg", utils.getMimeType(dataUri));
  }

  /**
   * メソッド名: getMimeType<br>
   * 試験名: getMimeTypeメソッドの異常動作<br>
   * 条件: 無効なデータURIを渡す<br>
   * 結果: MIMEがnullである<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetMimeType_Invalid() {
    String[] mimes = {"image/jpeg", "image/png"};
    Base64Utils utils = new Base64Utils(mimes);
    String dataUri = "data:image/gif;base64,abcd";
    assertNull(utils.getMimeType(dataUri));
  }

  /**
   * メソッド名: getBinaryData<br>
   * 試験名: getBinaryDataメソッドの正常動作<br>
   * 条件: 有効なデータURIを渡す<br>
   * 結果: 正しいバイナリデータが返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetBinaryData_Normal() {
    String[] subtypes = {"jpeg", "png"};
    Base64Utils utils = new Base64Utils(subtypes);
    String dataUri = "data:image/jpeg;base64,SGVsbG8=";
    byte[] expectedData = "Hello".getBytes();
    assertArrayEquals(expectedData, utils.getBinaryData(dataUri));
  }

  /**
   * メソッド名: getBinaryData<br>
   * 試験名: getBinaryDataメソッドの異常動作<br>
   * 条件: 無効なデータURIを渡す<br>
   * 結果: バイナリデータがnullである<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetBinaryData_Invalid() {
    String[] subtypes = {"jpeg", "png"};
    Base64Utils utils = new Base64Utils(subtypes);
    String dataUri = "data:image/gif;base64,SGVsbG8=";
    assertNull(utils.getBinaryData(dataUri));
  }

  /**
   * メソッド名: getAllMimeBinaryData<br>
   * 試験名: getAllMimeBinaryDataメソッドの正常動作<br>
   * 条件: 有効なデータURIを渡す<br>
   * 結果: 正しいバイナリデータが返される<br>
   * テストパターン：正常系
   */
  @Test
  public void testGetAllMimeBinaryData_Normal() {
    String[] mimes = {"image/jpeg", "image/png"};
    Base64Utils utils = new Base64Utils(mimes);
    String dataUri = "data:image/jpeg;base64,SGVsbG8=";
    byte[] expectedData = "Hello".getBytes();
    assertArrayEquals(expectedData, utils.getAllMimeBinaryData(dataUri));
  }

  /**
   * メソッド名: getAllMimeBinaryData<br>
   * 試験名: getAllMimeBinaryDataメソッドの異常動作<br>
   * 条件: 無効なデータURIを渡す<br>
   * 結果: バイナリデータがnullである<br>
   * テストパターン：異常系
   */
  @Test
  public void testGetAllMimeBinaryData_Invalid() {
    String[] mimes = {"image/jpeg", "image/png"};
    Base64Utils utils = new Base64Utils(mimes);
    String dataUri = "data:image/gif;base64,SGVsbG8=";
    assertNull(utils.getAllMimeBinaryData(dataUri));
  }

  /**
   * メソッド名: createDataUriWithBase64<br>
   * 試験名: createDataUriWithBase64メソッドの正常動作<br>
   * 条件: 有効なタイプ名とバイナリデータを渡す<br>
   * 結果: 正しいデータURIが生成される<br>
   * テストパターン：正常系
   */
  @Test
  public void testCreateDataUriWithBase64_Normal() {
    String[] subtypes = {"jpeg", "png"};
    Base64Utils utils = new Base64Utils(subtypes);
    byte[] binaryData = "Hello".getBytes();
    String expectedDataUri = "data:image/jpeg;base64,SGVsbG8=";
    assertEquals(expectedDataUri, utils.createDataUriWithBase64("jpeg", binaryData));
  }

  /**
   * メソッド名: createDataUriWithBase64<br>
   * 試験名: createDataUriWithBase64メソッドの異常動作<br>
   * 条件: 無効なタイプ名を渡す<br>
   * 結果: 正しいデータURIが生成される<br>
   * テストパターン：異常系
   */
  @Test
  public void testCreateDataUriWithBase64_Invalid() {
    String[] subtypes = {"jpeg", "png"};
    Base64Utils utils = new Base64Utils(subtypes);
    byte[] binaryData = "Hello".getBytes();
    String expectedDataUri = "data:image/invalid;base64,SGVsbG8=";
    assertEquals(expectedDataUri, utils.createDataUriWithBase64("invalid", binaryData));
  }
}
