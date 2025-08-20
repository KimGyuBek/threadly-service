package com.threadly.batch.job.post;

import com.threadly.batch.service.partitioner.HashPartitioner;
import com.threadly.batch.utils.RetentionThresholdProvider;
import com.threadly.batch.utils.RetentionThresholdProvider.ThresholdTargetType;
import com.threadly.core.domain.post.PostStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * DELETED 상태의 Post 하드 딜리트 job.
 */
@Configuration
@Profile("!data-insert")
@RequiredArgsConstructor
public class PostHardDeleteDeletedJobConfig {


  @Bean
  public Job postHardDeleteDeletedJob(
      JobExecutionListener listener,
      JobRepository jobRepository,
      Step postShardMasterStep
  ) {
    return new JobBuilder("postHardDeleteDeletedJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(postShardMasterStep)
        .listener(listener)
        .build();
  }

  @Bean
  @JobScope
  public Step postShardMasterStep(JobRepository jobRepository, HashPartitioner hashPartitioner,
      Step postShardWorkStep,
      @Qualifier("taskExecutor") TaskExecutor taskExecutor,
      @Value("#{jobParameters['gridSize']}") int gridSize) {
    var handler = new TaskExecutorPartitionHandler();
    handler.setGridSize(gridSize);
    handler.setTaskExecutor(taskExecutor);
    handler.setStep(postShardWorkStep);

    return new StepBuilder("postShardMasterStep", jobRepository)
        .partitioner("postShardMasterStep", hashPartitioner)
        .partitionHandler(handler)
        .allowStartIfComplete(true)
        .build();
  }


  @Bean
  public Step postShardWorkStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemStreamReader<String> postIdReaderByShard,
      ItemWriter<String> postDeleteWriter
  ) {
    return new StepBuilder("postShardWorkStep", jobRepository)
        .<String, String>chunk(1000, transactionManager)
        .reader(postIdReaderByShard)
        .writer(postDeleteWriter)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  @StepScope
  public JdbcPagingItemReader<String> postIdReaderByShard(
      DataSource dataSource, RetentionThresholdProvider retentionThresholdProvider,
      @Value("#{stepExecutionContext['shard']}") Integer shard,
      @Value("#{stepExecutionContext['gridSize']}") Integer N
  ) {
    var qp = new PostgresPagingQueryProvider();
    qp.setSelectClause("post_id");
    qp.setFromClause("from posts");
    qp.setWhereClause("""
        where status = :status
        and modified_at < :threshold
        and mod(abs(hashtext(post_id)), :N) = :shard
        """);
    qp.setSortKeys(Map.of("post_id", Order.ASCENDING));

    return new JdbcPagingItemReaderBuilder<String>()
        .name("postIdReaderById")
        .dataSource(dataSource)
        .queryProvider(qp)
        .pageSize(1000)
        .parameterValues(Map.of(
            "status", PostStatus.DELETED.name(),
            "threshold", retentionThresholdProvider.thresholdFor(ThresholdTargetType.POST_DELETED),
            "N", N,
            "shard", shard
        ))
        .rowMapper(((rs, rowNum) -> rs.getString(1)))
        .build();
  }

  @Bean
  @StepScope
  public ItemWriter<String> postDeleteWriter(
      JdbcTemplate jdbcTemplate, RetentionThresholdProvider retentionThresholdProvider
  ) {
    return ids -> {
      if (ids == null || ids.isEmpty()) {
        return;
      }
      String query = """
          delete from posts
          where post_id = any(?)
          and status = ?
          and modified_at < ?
          """;
      jdbcTemplate.execute((Connection con) -> {
        try (PreparedStatement ps = con.prepareStatement(query)) {
          java.sql.Array idArray = con.createArrayOf("text", ids.getItems().toArray(new String[0]));
          try {
            ps.setArray(1, idArray);
            ps.setString(2, PostStatus.DELETED.name());
            ps.setTimestamp(
                3,
                java.sql.Timestamp.valueOf(
                    retentionThresholdProvider.thresholdFor(ThresholdTargetType.POST_DELETED)
                )
            );
            ps.executeUpdate();
          } finally {
            idArray.free();
          }
        }
        return null;
      });
    };
  }
}
