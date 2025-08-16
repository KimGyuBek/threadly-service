package com.threadly.batch.job.user;

import com.threadly.batch.service.partitioner.HashPartitioner;
import com.threadly.batch.utils.RetentionThresholdProvider;
import com.threadly.batch.utils.RetentionThresholdProvider.ThresholdTargetType;
import com.threadly.core.domain.user.UserStatusType;
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
      Step userShardMasterStep
  ) {
    return new JobBuilder("userHardDeleteDeletedJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(userShardMasterStep)
        .listener(listener)
        .build();
  }

  @Bean
  @JobScope
  public Step userShardMasterStep(JobRepository jobRepository, HashPartitioner hashPartitioner,
      Step userShardWorkStep,
      @Qualifier("taskExecutor") TaskExecutor taskExecutor,
      @Value("#{jobParameters['gridSize']}") int gridSize
  ) {
    var handler = new TaskExecutorPartitionHandler();
    handler.setGridSize(gridSize);
    handler.setTaskExecutor(taskExecutor);
    handler.setStep(userShardWorkStep);

    return new StepBuilder("userShardMasterStep", jobRepository)
        .partitioner("userShardMasterStep", hashPartitioner)
        .partitionHandler(handler)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  public Step userShardWorkStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemStreamReader<String> userIdReaderByShard,
      ItemWriter<String> userDeleteWriter
  ) {
    return new StepBuilder("userShardWorkStep", jobRepository)
        .<String, String>chunk(1000, transactionManager)
        .reader(userIdReaderByShard)
        .writer(userDeleteWriter)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  @StepScope
  public JdbcPagingItemReader<String> userIdReaderByShard(
      DataSource dataSource, RetentionThresholdProvider retentionThresholdProvider,
      @Value("#{stepExecutionContext['shard']}") Integer shard,
      @Value("#{stepExecutionContext['gridSize']}") Integer N
  ) {
    var qp = new PostgresPagingQueryProvider();
    qp.setSelectClause("user_id");
    qp.setFromClause("from users");
    qp.setWhereClause("""
        where status = :status
        and modified_at < :threshold
        and mod(abs(hashtext(user_id)), :N) = :shard
        """);
    qp.setSortKeys(Map.of("user_id", Order.ASCENDING));

    return new JdbcPagingItemReaderBuilder<String>()
        .name("userIdReaderById")
        .dataSource(dataSource)
        .queryProvider(qp)
        .pageSize(1000)
        .parameterValues(Map.of(
            "status", UserStatusType.DELETED.name(),
            "threshold", retentionThresholdProvider.thresholdFor(ThresholdTargetType.USER_DELETED),
            "N", N,
            "shard", shard
        ))
        .rowMapper(((rs, rowNum) -> rs.getString(1)))
        .build();
  }

  @Bean
  @StepScope
  public ItemWriter<String> userDeleteWriter(
      JdbcTemplate jdbcTemplate, RetentionThresholdProvider retentionThresholdProvider
  ) {
    return ids -> {
      if (ids == null || ids.isEmpty()) {
        return;
      }

      String query = """
          delete from users
          where user_id = any(?)
          and status = ?
          and modified_at < ?
          """;
      jdbcTemplate.execute((Connection con) -> {
        try (PreparedStatement ps = con.prepareStatement(query)) {
          // user_id 컬럼 타입에 맞춰 elementType 지정: text | uuid
          // 예) UUID면 "uuid", TEXT면 "text"
          java.sql.Array idArray = con.createArrayOf("text", ids.getItems().toArray(new String[0]));
          try {
            ps.setArray(1, idArray);
            ps.setString(2, UserStatusType.DELETED.name());
            ps.setTimestamp(
                3,
                java.sql.Timestamp.valueOf(
                    retentionThresholdProvider.thresholdFor(ThresholdTargetType.USER_DELETED)
                )
            );
            ps.executeUpdate();
          } finally {
            // 드라이버에 따라 누수 방지
            idArray.free();
          }
        }
        return null;
      });
    };
  }

}
