package com.threadly;

import com.threadly.adapter.persistence.PersistenceModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackageClasses = PersistenceModule.class
)
public class PersistenceTestApplication {

}
