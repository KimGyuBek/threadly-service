package com.threadly.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class BatchRunnerConfig {

  private final JobRegistry jobRegistry;

  @Bean
  public org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner jobRunner(
      org.springframework.batch.core.launch.JobLauncher jobLauncher,
      org.springframework.batch.core.explore.JobExplorer jobExplorer,
      org.springframework.batch.core.repository.JobRepository jobRepository,
      org.springframework.core.env.Environment env
  ) {
    System.out.println("Registered jobs: " + jobRegistry.getJobNames());
    var r = new org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner(
        jobLauncher, jobExplorer, jobRepository
    );
    String names = env.getProperty("spring.batch.job.name", "NONE");
    r.setJobName(names);

    return r;
  }
}

