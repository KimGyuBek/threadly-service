package com.threadly.batch.job.image.deleted;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DELETED 상태의 PostImage와 UserProfileImage를 하드 딜리트 하는 job.
 */
@Configuration
public class ImageHardDeleteDeletedJobConfig {

  @Bean
  public Job imageHardDeleteDeletedJob(
      JobExecutionListener listener,
      JobRepository jobRepository,
      Flow profileImageHardDeleteDeletedFlow,
      Flow postImageHardDeleteDeletedFlow) {
    return new JobBuilder("imageHardDeleteDeletedJob", jobRepository)
        .listener(listener)
        .incrementer(new RunIdIncrementer())
        .start(profileImageHardDeleteDeletedFlow)
        .next(postImageHardDeleteDeletedFlow)
        .end()
        .build();
  }

}