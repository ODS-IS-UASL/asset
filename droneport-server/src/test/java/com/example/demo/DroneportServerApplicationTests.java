package com.example.demo;

import com.hitachi.droneroute.DroneportServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DroneportServerApplication.class)
@ActiveProfiles("test")
class DroneportServerApplicationTests {

  @Test
  void contextLoads() {}
}
