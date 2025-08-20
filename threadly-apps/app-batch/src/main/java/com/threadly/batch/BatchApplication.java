package com.threadly.batch;

import com.threadly.adapter.persistence.PersistenceModule;
import com.threadly.batch.properties.RetentionProperties;
import com.threadly.commons.CommonModule;
import io.prometheus.client.exporter.PushGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
@EnableConfigurationProperties(RetentionProperties.class)
public class BatchApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(BatchApplication.class);
    app.setWebApplicationType(WebApplicationType.NONE);
    ConfigurableApplicationContext context = app.run(args);
    System.exit(SpringApplication.exit(context));
  }

  @Bean(name = "taskExecutor")
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(20);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(0);
    executor.setThreadNamePrefix("part-");
    executor.setAllowCoreThreadTimeOut(true);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(120);
    executor.initialize();
    return executor;
  }

//  @Bean
//  public PushGateway pushGateway(
//      @Value("${prometheus.pushgateway.url}") String url
//  ) {
//    return new PushGateway(url);
//  }
}

