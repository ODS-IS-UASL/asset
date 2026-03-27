package com.hitachi.droneroute.arm.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** AircraftInfoServiceImplクラスの単体テスト */
@SpringBootTest
@ActiveProfiles("test")
class VirusScanServiceImplTest {
  @Autowired private VirusScanServiceImpl aircraftInfoServiceImpl;

  /**
   * メソッド名: scanVirus<br>
   * 試験名: 正常終了することを確認する<br>
   * 条件: 第1引数:null<br>
   * 結果: 例外が発生しないこと<br>
   * テストパターン：正常系<br>
   */
  @Test
  void virSer_scanVirus_第1引数null() {
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.scanVirus(null));
  }

  /**
   * メソッド名: scanVirus<br>
   * 試験名: 正常終了することを確認する<br>
   * 条件: 第1引数:バイト配列データ<br>
   * 結果: 例外が発生しないこと<br>
   * テストパターン：正常系<br>
   */
  @Test
  void virSer_scanVirus_第1引数バイト配列() {
    byte[] byteData = new byte[2];
    new Random().nextBytes(byteData);
    assertDoesNotThrow(() -> aircraftInfoServiceImpl.scanVirus(byteData));
  }
}
