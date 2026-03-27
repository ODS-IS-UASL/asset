package com.hitachi.droneroute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/** アプリケーションのメイン(基底)クラス */
public class DroneportServerApplication {

  /* メインメソッド
   *
   * @param args コマンドライン引数
   */
  public static void main(String[] args) {
    SpringApplication.run(DroneportServerApplication.class, args);
  }
}
