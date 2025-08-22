package com.threadly.batch.service.metrics;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 데이터베이스 성능 메트릭 수집기
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseMetricsCollector {
    
    private final DataSource dataSource;
    
    public Map<String, Object> collectDatabaseMetrics() {
        Map<String, Object> dbMetrics = new HashMap<>();
        
        try {
            if (dataSource instanceof HikariDataSource hikariDataSource) {
                HikariPoolMXBean poolBean = hikariDataSource.getHikariPoolMXBean();
                
                // Connection Pool 정보
                dbMetrics.put("activeConnections", poolBean.getActiveConnections());
                dbMetrics.put("idleConnections", poolBean.getIdleConnections());
                dbMetrics.put("totalConnections", poolBean.getTotalConnections());
                dbMetrics.put("threadsAwaitingConnection", poolBean.getThreadsAwaitingConnection());
                
                // Pool 설정 정보
                dbMetrics.put("maxPoolSize", hikariDataSource.getMaximumPoolSize());
                dbMetrics.put("minPoolSize", hikariDataSource.getMinimumIdle());
                
                // Connection 사용률
                int maxPoolSize = hikariDataSource.getMaximumPoolSize();
                int activeConnections = poolBean.getActiveConnections();
                if (maxPoolSize > 0) {
                    double connectionUsagePercent = (double) activeConnections / maxPoolSize * 100;
                    dbMetrics.put("connectionUsagePercent", Math.round(connectionUsagePercent * 100) / 100.0);
                }
                
                // Connection 타임아웃 설정
                dbMetrics.put("connectionTimeoutMs", hikariDataSource.getConnectionTimeout());
                dbMetrics.put("idleTimeoutMs", hikariDataSource.getIdleTimeout());
                dbMetrics.put("maxLifetimeMs", hikariDataSource.getMaxLifetime());
                
                log.debug("Database metrics collected successfully");
            } else {
                dbMetrics.put("error", "DataSource is not HikariDataSource");
                log.warn("Cannot collect detailed database metrics - DataSource is not HikariDataSource");
            }
        } catch (Exception e) {
            dbMetrics.put("error", "Failed to collect database metrics: " + e.getMessage());
            log.error("Failed to collect database metrics", e);
        }
        
        return dbMetrics;
    }
}