package com.threadly.batch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchRunnerConfig {

  @Bean
  public org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner jobRunner(
      org.springframework.batch.core.launch.JobLauncher jobLauncher,
      org.springframework.batch.core.explore.JobExplorer jobExplorer,
      org.springframework.batch.core.repository.JobRepository jobRepository
  ) {
    return new org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner(
        jobLauncher, jobExplorer, jobRepository
    );
  }
}

