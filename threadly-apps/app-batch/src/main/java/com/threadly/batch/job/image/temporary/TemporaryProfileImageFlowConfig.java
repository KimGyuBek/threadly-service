package com.threadly.batch.job.image.temporary;

import com.threadly.batch.job.image.ProfileImageDeleteJobFactory;
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
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Profile("!data-insert")
@RequiredArgsConstructor
public class TemporaryProfileImageFlowConfig {

  private final ProfileImageDeleteJobFactory profileImageDeleteJobFactory;

  @Bean
  public Flow profileImageHardDeleteTemporaryFlow(
      Step profileImageHardDeleteTemporaryStep
  ) {
    return new FlowBuilder<SimpleFlow>("profileImageHardDeleteDeletedFlow")
        .start(profileImageHardDeleteTemporaryStep)
        .build();
  }

  @Bean
  public Step profileImageHardDeleteTemporaryStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      EntityManagerFactory entityMangerFactory,
      EntityManager entityManager
  ) {
    return profileImageDeleteJobFactory.createProfileImageDeleteStep(
        jobRepository,
        "profileImageHardDeleteTemporaryStep",
        ImageStatus.TEMPORARY,
        ThresholdTargetType.IMAGE_TEMPORARY,
        transactionManager,
        entityMangerFactory,
        entityManager);
  }

}
