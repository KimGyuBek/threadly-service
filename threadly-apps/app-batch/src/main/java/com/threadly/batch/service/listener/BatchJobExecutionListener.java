package com.threadly.batch.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.commons.utils.TimeUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchJobExecutionListener implements JobExecutionListener {

  private final BatchJobLogger batchJobLogger;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    String jobName = jobExecution.getJobInstance().getJobName();
    String startTime = jobExecution.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00";
    
    Map<String, Object> logData = new HashMap<>();
    logData.put("jobName", jobName);
    logData.put("executionId", jobExecution.getId());
    logData.put("runId", jobExecution.getJobParameters().getLong("run.id", 0L));
    logData.put("status", "STARTED");
    logData.put("startTime", startTime);
    logData.put("host", getHostName());
    logData.put("env", getEnvironment());
    
    // Job Parameters 추가
    Map<String, Object> jobParams = new HashMap<>();
    jobExecution.getJobParameters().getParameters().forEach((key, value) -> {
      if (!"run.id".equals(key)) {
        jobParams.put(key, value.getValue());
      }
    });
    if (!jobParams.isEmpty()) {
      logData.put("jobParameters", jobParams);
    }
    
//    batchJobLogger.logJobStart(logData);
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    String jobName = jobExecution.getJobInstance().getJobName();
    String startTime = jobExecution.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00";
    String endTime = jobExecution.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00";
    String executionTime = TimeUtils.getExecutionTimeFormatted(
        jobExecution.getStartTime(), jobExecution.getEndTime());
    
    // Step별 처리 통계 집계
    long totalReadCount = 0;
    long totalWriteCount = 0;
    long totalFilterCount = 0;
    long totalSkipCount = 0;
    long totalRollbackCount = 0;
    long totalCommitCount = 0;
    
    // Steps 배열 생성
    List<Map<String, Object>> steps = new ArrayList<>();
    
    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
      totalReadCount += stepExecution.getReadCount();
      totalWriteCount += stepExecution.getWriteCount();
      totalFilterCount += stepExecution.getFilterCount();
      totalSkipCount += stepExecution.getSkipCount();
      totalRollbackCount += stepExecution.getRollbackCount();
      totalCommitCount += stepExecution.getCommitCount();
      
      // 각 Step 정보
      Map<String, Object> step = new HashMap<>();
      step.put("name", stepExecution.getStepName());
      step.put("status", stepExecution.getExitStatus().getExitCode());
      step.put("readCount", stepExecution.getReadCount());
      step.put("writeCount", stepExecution.getWriteCount());
      step.put("filterCount", stepExecution.getFilterCount());
      step.put("skipCount", stepExecution.getSkipCount());
      step.put("commitCount", stepExecution.getCommitCount());
      step.put("startTime", stepExecution.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00");
      step.put("endTime", stepExecution.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+09:00");
      
      steps.add(step);
    }
    
    // 처리량 계산 (초당 아이템 수)
    long executionTimeMs = java.time.Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis();
    double throughputItemsPerSec = executionTimeMs > 0 ? (totalWriteCount * 1000.0) / executionTimeMs : 0;
    
    Map<String, Object> logData = new HashMap<>();
    logData.put("jobName", jobName);
    logData.put("executionId", jobExecution.getId());
    logData.put("runId", jobExecution.getJobParameters().getLong("run.id", 0L));
    logData.put("status", jobExecution.getExitStatus().getExitCode());
    logData.put("startTime", startTime);
    logData.put("endTime", endTime);
    logData.put("executionTime", executionTime);
    logData.put("host", getHostName());
    logData.put("env", getEnvironment());
    
    // Job Parameters 추가
    Map<String, Object> jobParams = new HashMap<>();
    jobExecution.getJobParameters().getParameters().forEach((key, value) -> {
      if (!"run.id".equals(key)) {
        jobParams.put(key, value.getValue());
      }
    });
    if (!jobParams.isEmpty()) {
      logData.put("jobParameters", jobParams);
    }
    
    // Totals 객체
    Map<String, Object> totals = new HashMap<>();
    totals.put("readCount", totalReadCount);
    totals.put("writeCount", totalWriteCount);
    totals.put("filterCount", totalFilterCount);
    totals.put("skipCount", totalSkipCount);
    totals.put("rollbackCount", totalRollbackCount);
    totals.put("commitCount", totalCommitCount);
    totals.put("processed", totalReadCount);
    totals.put("deleted", totalWriteCount);
    totals.put("throughputItemsPerSec", Math.round(throughputItemsPerSec * 100.0) / 100.0);
    
    logData.put("totals", totals);
    logData.put("steps", steps);
    
    // 에러 정보 추가
    if (!jobExecution.getAllFailureExceptions().isEmpty()) {
      logData.put("errorMessage", jobExecution.getAllFailureExceptions().get(0).getMessage());
    }
    
    batchJobLogger.logJobComplete(logData);
  }
  
  private String getHostName() {
    try {
      return java.net.InetAddress.getLocalHost().getHostName();
    } catch (Exception e) {
      return "unknown-host";
    }
  }
  
  private String getEnvironment() {
    return System.getProperty("spring.profiles.active", "dev");
  }
  
}
