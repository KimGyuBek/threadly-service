package com.threadly.batch.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusPushGatewayManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Spring Batch 메트릭 설정 클래스
 * 
 * 이 클래스는 Spring Batch 메트릭이 올바르게 Prometheus로 전송되도록 보장합니다.
 */
@Configuration
public class BatchMetricsConfiguration {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment environment) {
        return registry -> registry.config().commonTags(
            List.of(
                Tag.of("application", "threadly-batch"),
                Tag.of("environment", environment.getActiveProfiles().length > 0 
                    ? environment.getActiveProfiles()[0] : "default")
            )
        );
    }

    @Bean
    public PushGateway pushGateway(@Value("${prometheus.pushgateway.url:http://localhost:9091}") String pushgatewayUrl) {
        return new PushGateway(pushgatewayUrl);
    }

    @Bean
    public PrometheusPushGatewayManager prometheusPushGatewayManager(
            PushGateway pushGateway,
            CollectorRegistry collectorRegistry,
            @Value("${prometheus.job.name:pushgateway}") String jobName) {
        
        return new PrometheusPushGatewayManager(
            pushGateway, 
            collectorRegistry, 
            Duration.ofSeconds(2),
            jobName, 
            Map.of(),
            PrometheusPushGatewayManager.ShutdownOperation.POST
        );
    }
}