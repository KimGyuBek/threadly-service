package com.threadly.batch.job.image;

import com.threadly.adapter.persistence.post.entity.PostImageEntity;
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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@RequiredArgsConstructor
public class PostImageDeleteJobFactory {

  private final RetentionThresholdProvider retentionThresholdProvider;

  /**
   * PostImage Delete job 생성
   *
   * @param jobRepository
   * @param jobName
   * @param stepName
   * @param targetStatus
   * @param listener
   * @param transactionManager
   * @param entityManagerFactory
   * @param entityManager
   * @return
   */
  public Job createPostImageJob(
      JobRepository jobRepository,
      String jobName,
      String stepName,
      ImageStatus targetStatus,
      ThresholdTargetType thresholdTargetType,
      JobExecutionListener listener,
      PlatformTransactionManager transactionManager,
      EntityManagerFactory entityManagerFactory,
      EntityManager entityManager
  ) {
    Step step = createPostImageDeleteStep(
        jobRepository,
        stepName,
        targetStatus,
        thresholdTargetType,
        transactionManager,
        entityManagerFactory,
        entityManager
    );

    return new JobBuilder(jobName, jobRepository)
        .start(step)
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .build();
  }

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
  public Step createPostImageDeleteStep(
      JobRepository jobRepository,
      String stepName,
      ImageStatus targetStatus,
      ThresholdTargetType thresholdTargetType,
      PlatformTransactionManager transactionManager,
      EntityManagerFactory entityManagerFactory,
      EntityManager entityManager
  ) {
    ItemReader<PostImageEntity> reader = creatPostImageDeleteItemReader(targetStatus,
        thresholdTargetType,
        entityManagerFactory);
    ItemProcessor<PostImageEntity, String> processor = createImageProcessor();
    ItemWriter<String> writer = createImageWriter(targetStatus, thresholdTargetType, entityManager);

    return new StepBuilder(stepName, jobRepository)
        .<PostImageEntity, String>chunk(10000, transactionManager)
        .allowStartIfComplete(true)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @StepScope
  public JpaPagingItemReader<PostImageEntity> creatPostImageDeleteItemReader(
      ImageStatus targetStatus,
      ThresholdTargetType thresholdTargetType,
      EntityManagerFactory entityManagerFactory
  ) {
    return new JpaPagingItemReaderBuilder<PostImageEntity>()
        .name(targetStatus.name().toLowerCase() + "PostImagesReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("""
            select pi
            from PostImageEntity pi
            where pi.status = :status
            and pi.modifiedAt < :threshold
            order by pi.modifiedAt asc
            """)
        .parameterValues(
            Map.of("status", targetStatus, "threshold",
                retentionThresholdProvider.thresholdFor(thresholdTargetType)))
        .pageSize(10000)
        .build();
  }

  @StepScope
  public ItemProcessor<PostImageEntity, String> createImageProcessor() {
    return PostImageEntity::getPostImageId;
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
              delete from PostImageEntity pi
              where pi.postImageId in :ids
              and pi.status = :status
              and pi.modifiedAt < :threshold
              """)
          .setParameter("ids", ids)
          .setParameter("status", targetStatus)
          .setParameter("threshold", retentionThresholdProvider.thresholdFor(thresholdTargetType))
          .executeUpdate();
      entityManager.clear();
    };
  }
}