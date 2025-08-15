package com.threadly.batch.service.listener;

import com.threadly.batch.service.monitoring.CustomPrometheusPushGatewayManager;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component(value = "stepListener")
@Slf4j
@RequiredArgsConstructor
public class StepExecutionListener implements org.springframework.batch.core.StepExecutionListener,
    ChunkListener {

  private final CustomPrometheusPushGatewayManager pushGatewayManager;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    log.info("beforeStep", stepExecution.getStepName());

    // Chunk Size 정보를 ExecutionContext에 저장
    try {
      Integer chunkSize = extractChunkSizeFromStep(stepExecution);
      if (chunkSize != null) {
        stepExecution.getExecutionContext().putInt("batch.chunkSize", chunkSize);
      }
    } catch (Exception e) {
      log.debug("Failed to extract chunk size for step: {}", stepExecution.getStepName());
    }

    pushGatewayManager.pushMetrics(
        Map.of("job_name", stepExecution.getJobExecution().getJobInstance().getJobName())
    );

  }

  @Override
  public void afterChunk(ChunkContext context) {
    pushGatewayManager.pushMetrics(
        Map.of("job_name",
            context.getStepContext().getStepExecution().getJobExecution().getJobInstance()
                .getJobName())
    );
  }

  private Integer extractChunkSizeFromStep(StepExecution stepExecution) {
    try {
      String stepName = stepExecution.getStepName();
      String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
      
      // Job별 chunk size 매핑 (실제 설정값과 동일하게)
      if (jobName.contains("userHardDeleteDeletedJob")) {
        return 500; // UserHardDeleteDeletedJobConfig에서 설정한 값
      } else if (jobName.contains("postHardDeleteDeletedJob")) {
        return 1000; // PostHardDeleteDeletedJobConfig 실제값
      } else if (jobName.contains("imageHardDelete")) {
        return 10000; // Image 관련 Job들은 10000 chunk size 사용  
      }
      
      return 1000; // 기본값
    } catch (Exception e) {
      return null;
    }
  }

}
