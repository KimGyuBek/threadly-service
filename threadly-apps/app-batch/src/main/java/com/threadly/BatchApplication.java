package com.threadly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.threadly.batch")
public class BatchApplication {

  public static void main(String[] args) {
    SpringApplication.run(BatchApplication.class, args);
  }

}
