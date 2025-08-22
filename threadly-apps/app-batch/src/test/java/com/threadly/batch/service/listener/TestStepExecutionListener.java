package com.threadly.batch.service.listener;

import org.springframework.batch.core.StepExecutionListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * StepExecutionListener
 */
@Profile("test")
@Component("stepListener")
public class TestStepExecutionListener implements StepExecutionListener {


}
