package com.threadly.batch.job.image;

import com.threadly.adapter.persistence.user.entity.UserProfileImageEntity;
import com.threadly.batch.utils.RetentionThresholdProvider;
import com.threadly.batch.utils.RetentionThresholdProvider.ThresholdTargetType;
import com.threadly.core.domain.image.ImageStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@RequiredArgsConstructor
public class ProfileImageDeleteJobFactory {

  private final RetentionThresholdProvider retentionThresholdProvider;

  private final StepListener stepListener;

  /**
   * PostImageDelete Step 생성
   *
   * @param jobRepository
   * @param stepName
   * @param targetStatus
   * @param transactionManager
   * @param entityManagerFactory
   * @param entityManager
   * @return
   */
  public Step createProfileImageDeleteStep(
      JobRepository jobRepository,
      String stepName,
      ImageStatus targetStatus,
      ThresholdTargetType thresholdTargetType,
      PlatformTransactionManager transactionManager,
      EntityManagerFactory entityManagerFactory,
      EntityManager entityManager
  ) {
    ItemReader<UserProfileImageEntity> reader = createProfileImagReader(targetStatus,
        thresholdTargetType,
        entityManagerFactory);
    ItemProcessor<UserProfileImageEntity, String> processor = createProfileImageProcessor();
    ItemWriter<String> writer = createImageWriter(targetStatus, thresholdTargetType, entityManager);

    return new StepBuilder(stepName, jobRepository)
        .allowStartIfComplete(true)
        .<UserProfileImageEntity, String>chunk(10000, transactionManager)
        .listener(stepListener)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @StepScope
  public JpaCursorItemReader<UserProfileImageEntity> createProfileImagReader(
      ImageStatus targetStatus,
      ThresholdTargetType thresholdTargetType,
      EntityManagerFactory entityManagerFactory
  ) {
    return new JpaCursorItemReaderBuilder<UserProfileImageEntity>()
        .name(targetStatus.name().toLowerCase() + "UserProfileImageReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("""
            select e
            from UserProfileImageEntity e
            where e.status = :status
            and e.modifiedAt < :threshold
            order by e.modifiedAt asc
            """)
        .parameterValues(
            Map.of("status", targetStatus, "threshold",
                retentionThresholdProvider.thresholdFor(thresholdTargetType)))
        .build();
  }

  @StepScope
  public ItemProcessor<UserProfileImageEntity, String> createProfileImageProcessor() {
    return UserProfileImageEntity::getUserProfileImageId;
  }

  @StepScope
  public ItemWriter<String> createImageWriter(ImageStatus targetStatus,
      ThresholdTargetType thresholdTargetType,
      EntityManager entityManager) {
    return chunk -> {
      var ids = chunk.getItems();
      if (ids == null || ids.isEmpty()) {
        return;
      }
      entityManager.createQuery("""
              delete from UserProfileImageEntity e
              where e.userProfileImageId in :ids
              and e.status = :status
              and e.modifiedAt < :threshold
              """)
          .setParameter("ids", ids)
          .setParameter("status", targetStatus)
          .setParameter("threshold", retentionThresholdProvider.thresholdFor(thresholdTargetType))
          .executeUpdate();
      entityManager.clear();
    };
  }
}