package com.threadly.batch.job.image.temporary;

import com.threadly.batch.job.image.PostImageDeleteJobFactory;
import com.threadly.batch.utils.RetentionThresholdProvider.ThresholdTargetType;
import com.threadly.core.domain.image.ImageStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class TemporaryPostImageFlowConfig {

  private final PostImageDeleteJobFactory postImageDeleteJobFactory;

  @Bean
  public Flow postImageHardDeleteTemporaryFlow(
      Step postImageHardDeleteTemporaryStep) {
    return new FlowBuilder<SimpleFlow>("postImageHardDeleteTemporaryFlow")
        .start(postImageHardDeleteTemporaryStep)
        .build();
  }

  @Bean
  public Step postImageHardDeleteTemporaryStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      EntityManagerFactory entityMangerFactory,
      EntityManager entityManager
  ) {
    return postImageDeleteJobFactory.createPostImageDeleteStep(
        jobRepository,
        "postImageHardDeleteTemporaryStep",
        ImageStatus.TEMPORARY,
        ThresholdTargetType.IMAGE_TEMPORARY,
        transactionManager,
        entityMangerFactory,
        entityManager);
  }

}
