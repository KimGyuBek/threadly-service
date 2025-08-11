package com.threadly.batch.job.postImage;

import com.threadly.batch.utils.RetentionThresholdProvider.ThresholdTargetType;
import com.threadly.core.domain.image.ImageStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * PostImage의 Status가 DELETED 상태인 데이터 하드 딜리트
 */
@Configuration
@RequiredArgsConstructor
public class PostImageHardDeleteDeletedStatusJobConfig {

  private final PostImageDeleteJobFactory postImageDeleteJobFactory;

  @Bean
  public Job postImageHardDeleteDeletedJob(
      JobRepository jobRepository,
      JobExecutionListener listener,
      PlatformTransactionManager transactionManager,
      EntityManagerFactory entityManagerFactory,
      EntityManager entityManager
  ) {
    return postImageDeleteJobFactory.createPostImageJob(
        jobRepository,
        "postImageHardDeleteDeletedJob",
        "postImageHardDeleteDeletedStep",
        ImageStatus.DELETED,
        ThresholdTargetType.IMAGE_DELETED,
        listener,
        transactionManager,
        entityManagerFactory,
        entityManager
    );
  }
}
