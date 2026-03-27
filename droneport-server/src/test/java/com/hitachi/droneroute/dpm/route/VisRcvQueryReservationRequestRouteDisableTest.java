package com.hitachi.droneroute.dpm.route;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.hitachi.droneroute.cmn.settings.SystemSettings;
import org.apache.camel.CamelContext;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/** VisRcvInfoRouteクラスの単体テスト */
@SpringBootTest
@CamelSpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VisRcvQueryReservationRequestRouteDisableTest {

  @SpyBean private SystemSettings systemSettings;

  @Autowired private CamelContext camelContext;

  /**
   * メソッド名: configure_enableQueryReservationFalse<br>
   * 試験名: enableQueryReservationがfalseの場合のルート設定を確認する<br>
   * 条件: enableQueryReservationがfalseの場合<br>
   * 結果: ルートが設定されないこと<br>
   * テストパターン：正常系
   */
  @Test
  public void configure_enableQueryReservationFalse() throws Exception {
    // ルート設定されていないことを確認する
    assertNull(camelContext.getRoute(VisRcvQueryReservationRequestRoute.class.getSimpleName()));
  }
}
