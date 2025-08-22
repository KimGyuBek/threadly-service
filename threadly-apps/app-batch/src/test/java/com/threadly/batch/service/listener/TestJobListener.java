package com.threadly.batch.service.listener;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test Job Listener
 */
@Profile("test")
@Component("jobListener")
public class TestJobListener implements JobExecutionListener {

}
