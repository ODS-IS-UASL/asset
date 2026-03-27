package com.hitachi.droneroute.constants;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.hitachi.droneroute.arm.constants.AircraftConstants;
import com.hitachi.droneroute.cmn.constants.CommonConstants;
import com.hitachi.droneroute.dpm.constants.DronePortConstants;
import com.hitachi.droneroute.prm.constants.PriceInfoConstants;
import org.junit.jupiter.api.Test;

/** ConstantsTestの単体テスト */
public class ConstantsTest {

  /**
   * メソッド名: (コンストラクタ)<br>
   * 試験名: 定数クラスのインスタンス化検証<br>
   * 条件: CommonConstants、AircraftConstants、DronePortConstants、PriceInfoConstantsの各定数クラスをインスタンス化<br>
   * 結果: 全てのインスタンスがnullでない<br>
   * テストパターン: 正常系<br>
   */
  @Test
  void constantsTest() {
    CommonConstants cmn = new CommonConstants();
    AircraftConstants arm = new AircraftConstants();
    DronePortConstants dpm = new DronePortConstants();
    PriceInfoConstants prm = new PriceInfoConstants();
    assertNotNull(cmn);
    assertNotNull(arm);
    assertNotNull(dpm);
    assertNotNull(prm);
  }
}
