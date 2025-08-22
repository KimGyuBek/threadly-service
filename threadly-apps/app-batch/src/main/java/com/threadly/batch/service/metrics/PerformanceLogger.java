package com.threadly.batch.service.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.commons.utils.TimeUtils;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

/**
 * dev 환경 성능 측정용 로거
 */
@Component
//@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class PerformanceLogger {

  private final PerformanceMetricsCollector metricsCollector;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String PERFORMANCE_LOG_PATH = "logs/batch/performance-metrics.log";

  public void logPerformanceMetrics(JobExecution jobExecution, String phase) {
    try {
      Map<String, Object> performanceData = new HashMap<>();

      // 기본 Job 정보

      if (jobExecution.getStartTime() != null) {
        performanceData.put("startTime",
            jobExecution.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00");
      }

      performanceData.put("timestamp",
          LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00");
      performanceData.put("jobName", jobExecution.getJobInstance().getJobName());
      performanceData.put("executionId", jobExecution.getId());
      performanceData.put("phase", phase); // "START" or "COMPLETE"
      performanceData.put("status", jobExecution.getExitStatus().getExitCode());


      if (jobExecution.getEndTime() != null) {
        performanceData.put("endTime",
            jobExecution.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00");
      }

      // 상세 성능 메트릭 수집
      if ("COMPLETE".equals(phase)) {
        Map<String, Object> detailedMetrics = metricsCollector.collectAllMetrics(jobExecution);
        performanceData.putAll(detailedMetrics);
      } else {
        // START 단계에서는 기본 시스템 메트릭만 수집
        performanceData.put("memory",
            ((PerformanceMetricsCollector) metricsCollector).collectMemoryMetrics());
        performanceData.put("system",
            ((PerformanceMetricsCollector) metricsCollector).collectSystemMetrics());
      }

      // 파일에 저장
      writePerformanceLog(performanceData);

      log.debug("Performance metrics logged for job: {} ({})",
          jobExecution.getJobInstance().getJobName(), phase);

    } catch (Exception e) {
      log.error("Failed to log performance metrics", e);
    }
  }

  private void writePerformanceLog(Map<String, Object> performanceData) {
    try {
      // logs 디렉토리 생성
      java.io.File logFile = new java.io.File(PERFORMANCE_LOG_PATH);
      logFile.getParentFile().mkdirs();

      // JSON을 파일에 한 줄로 저장
      try (FileWriter writer = new FileWriter(logFile, true)) {
        writer.write(objectMapper.writeValueAsString(performanceData) + System.lineSeparator());
      }
    } catch (IOException e) {
      log.error("Failed to write performance log to file", e);
    }
  }

  // 메트릭 수집을 위한 개별 메서드들 (필요 시 직접 호출 가능)
  public Map<String, Object> getMemoryMetrics() {
    return (Map<String, Object>) metricsCollector.collectMemoryMetrics();
  }

  public Map<String, Object> getSystemMetrics() {
    return (Map<String, Object>) metricsCollector.collectSystemMetrics();
  }

  public Map<String, Object> getDatabaseMetrics() {
    return metricsCollector.collectAllMetrics(null);
  }
}