package com.hitachi.droneroute.arm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** AircraftInfoDeleteRequestDtoTestの単体テスト */
public class AircraftInfoDeleteRequestDtoTest {

  /**
   * メソッド名: getOperatorId, setOperatorId<br>
   * 試験名: オペレータIDの設定と取得が正しく行われることを確認する<br>
   * 条件: setterで値を設定し、getterで取得する。設定値と取得値を比較する<br>
   * 結果: 設定値が取得値と一致すること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testGetSetDronePortId() {
    AircraftInfoDeleteRequestDto dto = new AircraftInfoDeleteRequestDto();
    String expected = "operator123";
    dto.setOperatorId(expected);
    assertEquals(expected, dto.getOperatorId());
  }

  /**
   * メソッド名: toString<br>
   * 試験名: toStringメソッドが正しく動作することを確認する<br>
   * 条件: 全ての項目に値を設定し、toStringメソッドを呼び出す<br>
   * 結果: 設定値が含まれる文字列が返されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  void testToString() {
    AircraftInfoDeleteRequestDto dto = new AircraftInfoDeleteRequestDto();
    dto.setOperatorId("operator123");

    String expected = "AircraftInfoDeleteRequestDto(operatorId=operator123)";
    assertEquals(expected, dto.toString());
  }
}
