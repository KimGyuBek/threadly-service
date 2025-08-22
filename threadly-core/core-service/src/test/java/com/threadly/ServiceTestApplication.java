package com.threadly;


import com.threadly.core.service.ServiceModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackageClasses = ServiceModule.class
)
public class ServiceTestApplication {


}
