package com.threadly.batch;

import com.threadly.adapter.persistence.PersistenceModule;
import com.threadly.batch.properties.ImagePurgeProperties;
import com.threadly.commons.CommonModule;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackageClasses = {
        PersistenceModule.class,
        CommonModule.class,
    },
    scanBasePackages = "com.threadly.batch"
)
@EnableJpaRepositories(basePackageClasses = {
    PersistenceModule.class
})
@EntityScan(basePackageClasses = {
    PersistenceModule.class
})
@EnableBatchProcessing
@EnableConfigurationProperties(ImagePurgeProperties.class)
public class BatchApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(BatchApplication.class);
    app.setWebApplicationType(WebApplicationType.NONE);
    ConfigurableApplicationContext context = app.run(args);
    System.exit(SpringApplication.exit(context));
  }
}

