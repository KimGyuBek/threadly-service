package com.threadly.batch.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component(value = "stepListener")
@Slf4j
public class StepExecutionListener implements org.springframework.batch.core.StepExecutionListener,
    ChunkListener {

  @Override
  public void beforeStep(StepExecution stepExecution) {
    log.info("beforeStep", stepExecution.getStepName());
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    log.info("afterStep", stepExecution.getJobExecution().getJobInstance().getJobName());
    return ExitStatus.COMPLETED;
  }

}
