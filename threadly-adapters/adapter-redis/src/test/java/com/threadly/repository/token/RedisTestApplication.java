package com.threadly.repository.token;


import com.threadly.RedisModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackageClasses = RedisModule.class
)
public class RedisTestApplication {


}
