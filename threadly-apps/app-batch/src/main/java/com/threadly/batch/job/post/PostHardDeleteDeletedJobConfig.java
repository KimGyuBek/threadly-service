package com.threadly.batch.job.post;

import com.threadly.adapter.persistence.post.entity.PostEntity;
import com.threadly.batch.utils.RetentionThresholdProvider;
import com.threadly.batch.utils.RetentionThresholdProvider.ThresholdTargetType;
import com.threadly.core.domain.post.PostStatus;
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
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * DELETED 상태의 Post 하드 딜리트 job.
 */
@Configuration
@RequiredArgsConstructor
public class PostHardDeleteDeletedJobConfig {

  private final RetentionThresholdProvider retentionThresholdProvider;

  @Bean
  public Job postHardDeleteDeletedJob(
      JobExecutionListener listener,
      JobRepository jobRepository,
      Step postHardDeleteStep
  ) {
    return new JobBuilder("postHardDeleteDeletedJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(postHardDeleteStep)
        .listener(listener)
        .build();
  }

  @Bean
  public Step postHardDeleteStep(JobRepository jobRepository, StepListener stepListener,
      PlatformTransactionManager platformTransactionManager,
      JpaPagingItemReader<PostEntity> postItemReader,
      ItemProcessor<PostEntity, String> postItemProcessor,
      ItemWriter<String> postItemWriter
  ) {
    return new StepBuilder("postHardDeleteStep", jobRepository)
        .<PostEntity, String>chunk(1000, platformTransactionManager)
        .listener(stepListener)
        .allowStartIfComplete(true)
        .reader(postItemReader)
        .processor(postItemProcessor)
        .writer(postItemWriter)
        .build();
  }

  @Bean
  public JpaPagingItemReader<PostEntity> postItemReader(
      EntityManagerFactory entityManagerFactory
  ) {
    return new JpaPagingItemReaderBuilder<PostEntity>()
        .name("postItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("""
            select e
            from PostEntity e
            where e.status = :status
            and e.modifiedAt < :threshold
            order by e.modifiedAt asc
            """)
        .parameterValues(
            Map.of("status", PostStatus.DELETED,
                "threshold",
                retentionThresholdProvider.thresholdFor(ThresholdTargetType.POST_DELETED))
        )
        .pageSize(1000)
        .build();
  }

  @Bean
  public ItemProcessor<PostEntity, String> postItemProcessor() {
    return PostEntity::getPostId;
  }

  @Bean
  public ItemWriter<String> postItemWriter(EntityManager em) {
    return chunk -> {
      var ids = chunk.getItems();
      if (ids == null || ids.isEmpty()) {
        return;
      }
      em.createQuery("""
              delete from PostEntity e
              where  e.postId in :ids 
              and e.status = :status
              and e.modifiedAt < :threshold
              """)
          .setParameter("ids", ids)
          .setParameter("status", PostStatus.DELETED)
          .setParameter("threshold",
              retentionThresholdProvider.thresholdFor(ThresholdTargetType.POST_DELETED))
          .executeUpdate();
      em.clear();
    };

  }


}
