package com.threadly.batch.job;

import com.threadly.adapter.persistence.post.entity.PostImageEntity;
import com.threadly.batch.properties.ImagePurgeProperties;
import com.threadly.core.domain.image.ImageStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class DeleteJobConfig {

  private final ImagePurgeProperties imagePurgeProperties;

  @Bean
  public Job hardDeletePostImageJob(
      JobRepository jobRepository,
      Step hardDeletePostImageStep,
      JobExecutionListener listener
  ) {
    return new JobBuilder("hardDeletePostImageJob", jobRepository)
        .start(hardDeletePostImageStep)
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .build();
  }

  @Bean
  public Step hardDeletePostImageStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<PostImageEntity> deleteImageReader,
      ItemProcessor<PostImageEntity, String> postImageToIdProcessor,
      ItemWriter<String> deletePostImageWriter
  ) {
    return new StepBuilder("hardDeletePostImageStep", jobRepository)
        .<PostImageEntity, String>chunk(10, transactionManager)
        .reader(deleteImageReader)
        .processor(postImageToIdProcessor)
        .writer(deletePostImageWriter)
        .build();
  }

  @StepScope
  @Bean
  public JpaPagingItemReader<PostImageEntity> deleteImageReader(
      EntityManagerFactory entityManagerFactory
  ) {
    return new JpaPagingItemReaderBuilder<PostImageEntity>()
        .name("deletedPostImagesReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("""
            select pi
            from PostImageEntity pi
            where pi.status = :status
            and pi.modifiedAt < :threshold
            order by pi.modifiedAt asc
            """)
        .parameterValues(Map.of("status", ImageStatus.DELETED, "threshold", getThreshold()))
        .pageSize(10)
        .build();
  }

  @StepScope
  @Bean
  public ItemProcessor<PostImageEntity, String> postImageToIdProcessor() {
    return PostImageEntity::getPostImageId;
  }

  @StepScope
  @Bean
  public ItemWriter<String> deletePostImageWriter(EntityManager em) {
    return chunk -> {
      var ids = chunk.getItems();
      if (ids == null || ids.isEmpty()) {
        return;
      }
      em.createQuery("""
              delete from PostImageEntity pi
              where pi.postImageId in :ids
              and pi.status = :status
              and pi.modifiedAt < :threshold
              """)
          .setParameter("ids", ids)
          .setParameter("status", ImageStatus.DELETED)
          .setParameter("threshold", getThreshold())
          .executeUpdate();
      em.clear();
    };
  }

  /**
   * 삭제 기준 시간 추출
   *
   * @return
   */
  private LocalDateTime getThreshold() {
    return LocalDateTime.now().minus(imagePurgeProperties.retention());
  }
}
