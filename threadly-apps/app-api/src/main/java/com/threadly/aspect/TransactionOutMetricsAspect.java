package com.threadly.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import jakarta.annotation.PostConstruct;

@Slf4j
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE) //트랜잭션 시작된 뒤에 실행되도록 설정
@RequiredArgsConstructor
public class TransactionOutMetricsAspect {

  private final MeterRegistry meterRegistry;
  
  @PostConstruct
  public void initializeMetrics() {
    // 메트릭을 0으로 초기화하여 No Data 방지
    meterRegistry.counter("transaction.outcome", 
      "outcome", "committed",
      "usecase", "initialization",
      "exception", "none"
    ).increment(0);
    
    meterRegistry.counter("transaction.outcome", 
      "outcome", "rolled_back",
      "usecase", "initialization", 
      "exception", "none"
    ).increment(0);
  }

  @Around("execution(* com.threadly.core.service..*Service.*(..))")
  public Object measure(final ProceedingJoinPoint pjp) throws Throwable {
    final String usecase = pjp.getSignature().getDeclaringType().getSimpleName() + "." + pjp.getSignature().getName();
    final long startTime = System.currentTimeMillis();
    
    // 디버깅용 로그 추가
    log.debug("TransactionOutMetricsAspect intercepted: {}", usecase);

    final boolean txActiveBefore = TransactionSynchronizationManager.isActualTransactionActive();
    final CauseHolder causeHolder = new CauseHolder();
    
    // 트랜잭션이 활성화된 경우에만 트랜잭션 결과 추적
    if (txActiveBefore) {
      log.debug("Transaction is active for: {}", usecase);
      TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            recordTransactionOutcome(usecase, status, causeHolder.getCause(), System.currentTimeMillis() - startTime);
          }
        }
      );
    } else {
      // 트랜잭션이 없는 메서드도 추적 (non-transactional)
      log.debug("No transaction for: {}, recording as non-transactional", usecase);
    }

    try {
      Object result = pjp.proceed();
      
      // 트랜잭션이 없는 경우 성공으로 기록
      if (!txActiveBefore) {
        recordTransactionOutcome(usecase, TransactionSynchronization.STATUS_COMMITTED, null, System.currentTimeMillis() - startTime);
      }
      
      return result;
    } catch (Throwable ex) {
      causeHolder.setCause(ex);
      
      // 트랜잭션이 없는 경우 실패로 기록
      if (!txActiveBefore) {
        recordTransactionOutcome(usecase, TransactionSynchronization.STATUS_ROLLED_BACK, ex, System.currentTimeMillis() - startTime);
      }
      
      throw ex;
    }
  }

  private void recordTransactionOutcome(String usecase, int status, Throwable cause, long duration) {
    String outcome = switch (status) {
      case TransactionSynchronization.STATUS_COMMITTED -> "committed";
      case TransactionSynchronization.STATUS_ROLLED_BACK -> "rolled_back";
      case TransactionSynchronization.STATUS_UNKNOWN -> "unknown";
      default -> "other";
    };

    // 트랜잭션 결과 카운터
    meterRegistry.counter("transaction.outcome", 
      "usecase", usecase,
      "outcome", outcome,
      "exception", cause != null ? cause.getClass().getSimpleName() : "none"
    ).increment();
    
    // 트랜잭션 실행 시간 히스토그램
    meterRegistry.timer("transaction.duration",
      "usecase", usecase,
      "outcome", outcome
    ).record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);

    // 로깅 개선
    if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
      log.warn("Transaction ROLLED BACK for usecase: {}, duration: {}ms, cause: {}", 
        usecase, duration, cause != null ? cause.getMessage() : "unknown");
    } else {
      log.info("Transaction {} for usecase: {}, duration: {}ms", 
        outcome.toUpperCase(), usecase, duration);
    }
  }

  private static class CauseHolder {
    private Throwable cause;

    public Throwable getCause() {
      return cause;
    }

    public void setCause(Throwable cause) {
      this.cause = cause;
    }
  }

}
