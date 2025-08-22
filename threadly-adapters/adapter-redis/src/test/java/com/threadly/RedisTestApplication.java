package com.threadly;


import com.threadly.adapter.redis.RedisModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackageClasses = RedisModule.class
)
public class RedisTestApplication {


}
