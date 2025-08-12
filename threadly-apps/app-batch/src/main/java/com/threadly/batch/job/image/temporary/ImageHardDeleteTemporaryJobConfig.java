package com.threadly.batch.job.image.temporary;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DELETED 상태의 PostImage와 UserProfileImage를 하드 딜리트 하는 job.
 */
@Configuration
public class ImageHardDeleteTemporaryJobConfig {

  @Bean
  public Job imageHardDeleteTemporaryJob(
      JobExecutionListener listener,
      JobRepository jobRepository,
      Flow profileImageHardDeleteTemporaryFlow,
      Flow postImageHardDeleteTemporaryFlow) {
    return new JobBuilder("imageHardDeleteTemporaryJob", jobRepository)
        .listener(listener)
        .start(profileImageHardDeleteTemporaryFlow)
        .next(postImageHardDeleteTemporaryFlow)
        .end()
        .build();
  }

}
