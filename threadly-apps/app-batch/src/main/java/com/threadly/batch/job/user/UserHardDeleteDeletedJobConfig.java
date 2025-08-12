package com.threadly.batch.job.user;

import com.threadly.adapter.persistence.user.entity.UserEntity;
import com.threadly.batch.listener.DeleteLoggingItemWriteListener;
import com.threadly.batch.utils.RetentionThresholdProvider;
import com.threadly.batch.utils.RetentionThresholdProvider.ThresholdTargetType;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.user.UserStatusType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * DELETED 상태의 User 하드 딜리트  job.
 */
@Configuration
@Profile("!data-insert")
@RequiredArgsConstructor
public class UserHardDeleteDeletedJobConfig {

  private final RetentionThresholdProvider retentionThresholdProvider;

  @Bean
  public Job userHardDeleteDeletedJob(
      JobExecutionListener listener,
      JobRepository jobRepository,
      Step userHardDeleteStep
  ) {
    return new JobBuilder("userHardDeleteDeletedJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(userHardDeleteStep)
        .listener(listener)
        .build();
  }

  @Bean
  public Step userHardDeleteStep(JobRepository jobRepository, StepListener stepListener,
      DeleteLoggingItemWriteListener deleteLoggingItemWriteListener,
      PlatformTransactionManager platformTransactionManager,
      JpaCursorItemReader<UserEntity> userItemReader,
      ItemProcessor<UserEntity, String> userItemProcessor,
      ItemWriter<String> userItemWriter
  ) {
    return new StepBuilder("userHardDeleteStep", jobRepository)
        .<UserEntity, String>chunk(1000, platformTransactionManager)
        .listener(stepListener)
        .listener(new DeleteLoggingItemWriteListener(UserEntity.class.getName(),
            ImageStatus.DELETED.name()))
        .allowStartIfComplete(true)
        .reader(userItemReader)
        .processor(userItemProcessor)
        .writer(userItemWriter)
        .build();
  }

  @Bean
  public JpaCursorItemReader<UserEntity> userItemReader(
      EntityManagerFactory entityManagerFactory
  ) {
    return new JpaCursorItemReaderBuilder<UserEntity>()
        .name("userItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("""
            select e
            from UserEntity e
            where e.userStatusType = :status
            and e.modifiedAt < :threshold
            order by e.modifiedAt asc, e.userId asc
            """)
        .parameterValues(
            Map.of("status", UserStatusType.DELETED,
                "threshold",
                retentionThresholdProvider.thresholdFor(ThresholdTargetType.USER_DELETED))
        )
        .build();
  }

  @Bean
  public ItemProcessor<UserEntity, String> userItemProcessor() {
    return UserEntity::getUserId;
  }

  @Bean
  public ItemWriter<String> userItemWriter(EntityManager em) {
    return chunk -> {
      var ids = chunk.getItems();
      if (ids == null || ids.isEmpty()) {
        return;
      }
      em.createQuery("""
              delete from UserEntity e
              where  e.userId in :ids 
              and e.userStatusType = :status
              and e.modifiedAt < :threshold
              """)
          .setParameter("ids", ids)
          .setParameter("status", UserStatusType.DELETED)
          .setParameter("threshold",
              retentionThresholdProvider.thresholdFor(ThresholdTargetType.USER_DELETED))
          .executeUpdate();
      em.clear();
    };
  }


}
