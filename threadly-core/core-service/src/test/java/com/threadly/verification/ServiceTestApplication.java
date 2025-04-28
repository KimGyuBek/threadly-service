package com.threadly.verification;

import com.threadly.ServiceModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackageClasses = ServiceModule.class
)
public class ServiceTestApplication {

}
