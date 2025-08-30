package com.threadly.controller;

import com.threadly.core.service.post.like.post.PostLikeCommandService;
import com.threadly.core.usecase.post.like.post.LikePostCommand;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class MetricsTestController {

    private final MeterRegistry meterRegistry;
    private final PostLikeCommandService postLikeCommandService;

    @GetMapping("/metrics")
    public String getTransactionMetrics() {
        StringBuilder sb = new StringBuilder();
        
        meterRegistry.getMeters().stream()
            .filter(meter -> meter.getId().getName().equals("transaction.outcome"))
            .forEach(meter -> {
                sb.append("Metric: ").append(meter.getId()).append(" = ");
                sb.append(meter.measure()).append("\n");
            });
        
        if (sb.length() == 0) {
            return "No transaction.outcome metrics found. Try triggering some transactions first.";
        }
        
        return sb.toString();
    }

    @GetMapping("/trigger-transaction")
    public String triggerTransaction() {
        try {
            // 실제 트랜잭션이 있는 서비스 메서드 호출
            // 이는 실패할 것이지만, 트랜잭션 메트릭은 수집됨
            postLikeCommandService.likePost(new LikePostCommand("test-post", "test-user"));
            return "Transaction completed successfully";
        } catch (Exception e) {
            return "Transaction failed (expected): " + e.getMessage() + " - Check /api/test/metrics for transaction outcome metrics";
        }
    }

    @GetMapping("/trigger-success")
    public String triggerSuccessfulTransaction() {
        // 성공하는 트랜잭션을 시뮬레이션
        meterRegistry.counter("transaction.outcome", 
            "outcome", "committed",
            "usecase", "TestService.testMethod",
            "exception", "none"
        ).increment();
        
        return "Successful transaction metric recorded - rollback rate should show 0%";
    }

    @GetMapping("/trigger-rollback")
    public String triggerRollbackTransaction() {
        // 실패하는 트랜잭션을 시뮬레이션
        meterRegistry.counter("transaction.outcome", 
            "outcome", "rolled_back",
            "usecase", "PostLikeCommandService.likePost",
            "exception", "SerializationException"
        ).increment();
        
        meterRegistry.counter("transaction.outcome", 
            "outcome", "rolled_back",
            "usecase", "NotificationService.publish",
            "exception", "SerializationException"  
        ).increment();
        
        return "Rollback transaction metrics recorded - should see rollback data in Grafana";
    }

    @GetMapping("/generate-sample-data")
    public String generateSampleData() {
        // PostLike 서비스 시뮬레이션 (성공 50건, 실패 8건)
        for (int i = 0; i < 50; i++) {
            meterRegistry.counter("transaction.outcome",
                "outcome", "committed",
                "usecase", "PostLikeCommandService.likePost",
                "exception", "none"
            ).increment();
            
            // 실행 시간도 함께 기록
            meterRegistry.timer("transaction.duration",
                "usecase", "PostLikeCommandService.likePost",
                "outcome", "committed"
            ).record(50 + (int)(Math.random() * 100), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        
        for (int i = 0; i < 8; i++) {
            meterRegistry.counter("transaction.outcome",
                "outcome", "rolled_back", 
                "usecase", "PostLikeCommandService.likePost",
                "exception", "SerializationException"
            ).increment();
            
            meterRegistry.timer("transaction.duration",
                "usecase", "PostLikeCommandService.likePost",
                "outcome", "rolled_back"
            ).record(200 + (int)(Math.random() * 300), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        
        // NotificationService 시뮬레이션 (성공 45건, 실패 3건)
        for (int i = 0; i < 45; i++) {
            meterRegistry.counter("transaction.outcome",
                "outcome", "committed",
                "usecase", "NotificationService.publish", 
                "exception", "none"
            ).increment();
            
            meterRegistry.timer("transaction.duration",
                "usecase", "NotificationService.publish",
                "outcome", "committed"
            ).record(30 + (int)(Math.random() * 70), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        
        for (int i = 0; i < 3; i++) {
            meterRegistry.counter("transaction.outcome",
                "outcome", "rolled_back",
                "usecase", "NotificationService.publish",
                "exception", "KafkaException"
            ).increment();
            
            meterRegistry.timer("transaction.duration",
                "usecase", "NotificationService.publish",
                "outcome", "rolled_back"
            ).record(500 + (int)(Math.random() * 1000), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        
        // FollowService 시뮬레이션 (성공 100건, 실패 1건)
        for (int i = 0; i < 100; i++) {
            meterRegistry.counter("transaction.outcome",
                "outcome", "committed",
                "usecase", "FollowCommandService.follow",
                "exception", "none"
            ).increment();
            
            meterRegistry.timer("transaction.duration",
                "usecase", "FollowCommandService.follow",
                "outcome", "committed"
            ).record(20 + (int)(Math.random() * 40), java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        
        meterRegistry.counter("transaction.outcome",
            "outcome", "rolled_back",
            "usecase", "FollowCommandService.follow",
            "exception", "DatabaseException"
        ).increment();
        
        return "Realistic sample data generated:\n" +
               "- PostLikeCommandService: 50 success, 8 rollback (13.8% rollback rate)\n" +
               "- NotificationService: 45 success, 3 rollback (6.3% rollback rate)\n" +
               "- FollowCommandService: 100 success, 1 rollback (1.0% rollback rate)\n" +
               "Total: 195 success, 12 rollback (5.8% overall rollback rate)";
    }

    @GetMapping("/test-actual-service")
    public String testActualService() {
        try {
            // 실제 서비스 메서드 호출 시도 (실패할 것이지만 Aspect는 동작함)
            postLikeCommandService.likePost(new LikePostCommand("test-post-id", "test-user-id"));
            return "Actual service call succeeded (unexpected)";
        } catch (Exception e) {
            return "Actual service call failed as expected: " + e.getMessage() + 
                   "\nCheck logs and metrics - Aspect should have recorded this transaction!";
        }
    }

    @GetMapping("/all-metrics")
    public String getAllMetrics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Total meters: ").append(meterRegistry.getMeters().size()).append("\n\n");
        
        meterRegistry.getMeters().stream()
            .filter(meter -> meter.getId().getName().contains("transaction"))
            .forEach(meter -> {
                sb.append("Metric: ").append(meter.getId().getName())
                  .append(" Tags: ").append(meter.getId().getTags())
                  .append(" Value: ").append(meter.measure()).append("\n");
            });
            
        return sb.toString();
    }
}