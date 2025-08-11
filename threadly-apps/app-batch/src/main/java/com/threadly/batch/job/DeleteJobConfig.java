package com.threadly.batch.job;

import com.threadly.adapter.persistence.post.entity.PostImageEntity;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class DeleteJobConfig {

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
  public JpaPagingItemReader deleteImageReader(
      EntityManagerFactory entityManagerFactory
  ) {
    return new JpaPagingItemReaderBuilder()
        .name("deletedPostImagesReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("""
            select pi from PostImageEntity pi where pi.status = :status
            """)
        .parameterValues(Map.of("status", ImageStatus.DELETED))
        .pageSize(10)
        .build();
  }

  @StepScope
  @Bean
  public ItemProcessor<PostImageEntity, String> postImageToIdProcessor() {
    return entity -> {
      System.out.println("Processing entity: " + entity.getPostImageId());
      return entity.getPostImageId();
    };
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
              """)
          .setParameter("ids", ids)
          .executeUpdate();

      em.clear();
    };
  }
}
