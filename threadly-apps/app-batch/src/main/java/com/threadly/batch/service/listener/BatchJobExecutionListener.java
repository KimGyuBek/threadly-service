package com.threadly.batch.service.listener;

import com.threadly.commons.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchJobExecutionListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.info("start job :{}", jobExecution.getJobInstance().getJobName());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info("jobName: {}", jobExecution.getJobInstance().getJobName());
    log.info("result: {}", jobExecution.getExitStatus().getExitCode());
    log.info("jobExecutionTime: {}",
        TimeUtils.getExecutionTimeFormatted(jobExecution.getStartTime(),
            jobExecution.getEndTime()));
  }

}
