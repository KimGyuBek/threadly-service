package com.threadly.batch.service.metrics;

import com.threadly.commons.utils.TimeUtils;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

/**
 * 배치 성능 메트릭 수집기
 */
@Component
@RequiredArgsConstructor
public class PerformanceMetricsCollector {

  private final DatabaseMetricsCollector databaseMetricsCollector;

  public Map<String, Object> collectAllMetrics(JobExecution jobExecution) {
    Map<String, Object> metrics = new HashMap<>();

    metrics.put("memory", collectMemoryMetrics());
    metrics.put("system", collectSystemMetrics());
    metrics.put("database", databaseMetricsCollector.collectDatabaseMetrics());
    metrics.put("stepDetails", collectStepDetailMetrics(jobExecution));
    metrics.put("businessMetrics", collectBusinessMetrics(jobExecution));

    return metrics;
  }

  public Map<String, Object> collectMemoryMetrics() {
    MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    Map<String, Object> memory = new HashMap<>();

    // Heap 메모리
    long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
    long heapMax = memoryBean.getHeapMemoryUsage().getMax();
    long heapCommitted = memoryBean.getHeapMemoryUsage().getCommitted();

    memory.put("heapUsedMB", heapUsed / (1024 * 1024));
    memory.put("heapMaxMB", heapMax / (1024 * 1024));
    memory.put("heapCommittedMB", heapCommitted / (1024 * 1024));
    memory.put("heapFreeMB", (heapMax - heapUsed) / (1024 * 1024));
    memory.put("heapUsagePercent", Math.round((double) heapUsed / heapMax * 100 * 100) / 100.0);

    // Non-Heap 메모리
    long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
    memory.put("nonHeapUsedMB", nonHeapUsed / (1024 * 1024));

    // GC 정보
    long totalGcCount = 0;
    long totalGcTime = 0;
    for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
      totalGcCount += gcBean.getCollectionCount();
      totalGcTime += gcBean.getCollectionTime();
    }
    memory.put("gcCount", totalGcCount);
    memory.put("gcTimeMs", totalGcTime);

    return memory;
  }

  public Map<String, Object> collectSystemMetrics() {
    OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    Map<String, Object> system = new HashMap<>();

    // CPU 사용률
    if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
      system.put("cpuUsagePercent", Math.round(sunOsBean.getProcessCpuLoad() * 100 * 100) / 100.0);
      system.put("systemCpuUsagePercent",
          Math.round(sunOsBean.getSystemCpuLoad() * 100 * 100) / 100.0);
      system.put("committedVirtualMemoryMB",
          sunOsBean.getCommittedVirtualMemorySize() / (1024 * 1024));
      system.put("freePhysicalMemoryMB", sunOsBean.getFreePhysicalMemorySize() / (1024 * 1024));
      system.put("totalPhysicalMemoryMB", sunOsBean.getTotalPhysicalMemorySize() / (1024 * 1024));
    }

    // 시스템 정보
    system.put("availableProcessors", osBean.getAvailableProcessors());
    system.put("systemLoadAverage", Math.round(osBean.getSystemLoadAverage() * 100) / 100.0);

    // JVM 정보
    system.put("activeThreads", Thread.activeCount());

    return system;
  }

  private Map<String, Object> collectStepDetailMetrics(JobExecution jobExecution) {
    Map<String, Object> stepDetails = new HashMap<>();

    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
      Map<String, Object> stepMetric = new HashMap<>();

      // 기본 Step 정보
      stepMetric.put("name", stepExecution.getStepName());
      stepMetric.put("status", stepExecution.getExitStatus().getExitCode());
      stepMetric.put("readCount", stepExecution.getReadCount());
      stepMetric.put("writeCount", stepExecution.getWriteCount());
      stepMetric.put("filterCount", stepExecution.getFilterCount());
      stepMetric.put("skipCount", stepExecution.getSkipCount());
      stepMetric.put("commitCount", stepExecution.getCommitCount());

      // Chunk Size 정보 추가 (Step Configuration에서 추출)
      try {
        // ExecutionContext에서 chunk size 정보를 찾거나 기본값 설정
        String stepName = stepExecution.getStepName();
        Integer chunkSize = extractChunkSizeFromStepExecution(stepExecution);
        if (chunkSize != null) {
          stepMetric.put("chunkSize", chunkSize);
        }
      } catch (Exception e) {
        // chunk size 정보를 얻을 수 없는 경우 로그만 남기고 계속 진행
        stepMetric.put("chunkSize", "unknown");
      }

      // Step 실행 시간
      if (stepExecution.getStartTime() != null && stepExecution.getEndTime() != null) {
        long stepDurationMs = java.time.Duration.between(
            stepExecution.getStartTime(), stepExecution.getEndTime()).toMillis();
        stepMetric.put("durationMs", stepDurationMs);

        // 아이템별 처리 시간 계산
        if (stepExecution.getReadCount() > 0) {
          double avgItemProcessingMs = (double) stepDurationMs / stepExecution.getReadCount();
          stepMetric.put("avgItemProcessingMs", Math.round(avgItemProcessingMs * 100) / 100.0);
        }

        // 처리량 계산
        if (stepDurationMs > 0) {
          double itemsPerSecond = (stepExecution.getWriteCount() * 1000.0) / stepDurationMs;
          stepMetric.put("itemsPerSecond", Math.round(itemsPerSecond * 100) / 100.0);
        }
      }

      stepDetails.put(stepExecution.getStepName(), stepMetric);
    }

    return stepDetails;
  }

  private Map<String, Object> collectBusinessMetrics(JobExecution jobExecution) {
    Map<String, Object> business = new HashMap<>();

    String jobName = jobExecution.getJobInstance().getJobName();

    // Job별 비즈니스 메트릭
    if (jobName.contains("user")) {
      business.put("entityType", "USER");
      business.put("deleteType", "HARD_DELETE");
      business.put("statusFilter", "DELETED");
    } else if (jobName.contains("post") && !jobName.contains("Image")) {
      business.put("entityType", "POST");
      business.put("deleteType", "HARD_DELETE");
      business.put("statusFilter", "DELETED");
    } else if (jobName.contains("Image")) {
      business.put("entityType", "IMAGE");
      business.put("deleteType", "HARD_DELETE");
      if (jobName.contains("Deleted")) {
        business.put("statusFilter", "DELETED");
      } else if (jobName.contains("Temporary")) {
        business.put("statusFilter", "TEMPORARY");
      }
    }

    // 전체 처리 통계 (Master Step 제외하고 실제 Work Step들만 카운트)
    long totalProcessed = jobExecution.getStepExecutions().stream()
        .filter(step -> !step.getStepName().contains("Master") && !step.getStepName().contains("master"))
        .mapToLong(StepExecution::getWriteCount)
        .sum();

    long totalRead = jobExecution.getStepExecutions().stream()
        .filter(step -> !step.getStepName().contains("Master") && !step.getStepName().contains("master"))
        .mapToLong(StepExecution::getReadCount)
        .sum();

    business.put("totalItemsProcessed", totalProcessed);
    business.put("totalItemsRead", totalRead);

    if (totalRead > 0) {
      double deletionRate = (double) totalProcessed / totalRead * 100;
      business.put("deletionRatePercent", Math.round(deletionRate * 100) / 100.0);
    }

    // 실행 시간 통계
    if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
      long executionTimeMs = java.time.Duration.between(
          jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis();
      business.put("totalExecutionTimeMs", executionTimeMs);

      // TimeUtils를 사용한 포맷된 실행 시간 추가
      business.put("jobExecutionTime",
          TimeUtils.getExecutionTimeFormatted(jobExecution.getStartTime(),
              jobExecution.getEndTime()));

      if (totalProcessed > 0 && executionTimeMs > 0) {
        double throughput = (totalProcessed * 1000.0) / executionTimeMs;
        business.put("overallThroughputItemsPerSec", Math.round(throughput * 100) / 100.0);
      }
    }

    // Configuration 정보 추가
    Map<String, Object> configuration = new HashMap<>();

    // 주요 Work Step의 chunk size 추출 (Master Step 제외)
    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
      if (!stepExecution.getStepName().contains("Master") && !stepExecution.getStepName().contains("master")) {
        Integer chunkSize = extractChunkSizeFromStepExecution(stepExecution);
        if (chunkSize != null) {
          configuration.put("chunkSize", chunkSize);
          break; // 첫 번째 work step의 chunk size 사용
        }
      }
    }

    // maxItemCount 정보 (userHardDeleteDeletedJob의 경우)
    if (jobName.contains("userHardDeleteDeletedJob")) {
      configuration.put("maxItemCount", 1000000);
    }

    if (!configuration.isEmpty()) {
      business.put("configuration", configuration);
    }

    return business;
  }

  private Integer extractChunkSizeFromStepExecution(StepExecution stepExecution) {
    try {
      // ExecutionContext에서 chunk size를 찾거나
      if (stepExecution.getExecutionContext().containsKey("batch.chunkSize")) {
        return stepExecution.getExecutionContext().getInt("batch.chunkSize");
      }

      // Commit Count 기반으로 chunk size 추정
      if (stepExecution.getCommitCount() > 0 && stepExecution.getWriteCount() > 0) {
        int estimatedChunkSize = (int) Math.ceil((double) stepExecution.getWriteCount() / stepExecution.getCommitCount());
        // 유효한 범위 내에서만 반환 (100~10000)
        if (estimatedChunkSize >= 100 && estimatedChunkSize <= 10000) {
          return estimatedChunkSize;
        }
      }

      // 기본값 반환
      return null;
    } catch (Exception e) {
      return null;
    }
  }
}