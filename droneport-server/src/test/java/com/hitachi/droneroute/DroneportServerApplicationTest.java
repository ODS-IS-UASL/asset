package com.hitachi.droneroute;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.hitachi.droneroute.cmn.settings.SystemSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

/** DroneportServerApplicationクラスの単体テスト */
@SpringBootTest(classes = DroneportServerApplication.class)
@ActiveProfiles("test")
public class DroneportServerApplicationTest {

  @SpyBean private SystemSettings systemSettings;

  @BeforeEach
  void beforeEach() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * メソッド名: main<br>
   * 試験名: SpringApplicationが正常で起動することを確認する<br>
   * 条件: コマンド引数を未設定でmaiを呼び出す<br>
   * 結果: SpringApplication.runメソッドが呼び出されること<br>
   * テストパターン：正常系<br>
   */
  @Test
  public void 正常起動() {
    // ●準備
    ConfigurableApplicationContext context = null;
    String[] args = {};

    try (MockedStatic<SpringApplication> springApplicationMock =
        mockStatic(SpringApplication.class)) {
      springApplicationMock
          .when(() -> SpringApplication.run(DroneportServerApplication.class, args))
          .thenReturn(context);

      // ●実行
      DroneportServerApplication.main(args);

      // ●検証
      springApplicationMock.verify(
          () -> SpringApplication.run(DroneportServerApplication.class, args), times(1));
    }
  }
}
