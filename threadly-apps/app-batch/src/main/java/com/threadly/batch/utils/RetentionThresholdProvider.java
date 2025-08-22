package com.threadly.batch.utils;

import com.threadly.batch.properties.RetentionProperties;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 보존 기간(retention) 설정값을 기반으로 각 도메인/상태별 삭제 기준 시각(Threshold)을 계산해주는 provider
 * <p>
 * Batch Job에서 삭제 대상 데이터를 조회할 떄 기준 시각을 구하는 용도로 사용됨
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RetentionThresholdProvider {

  private final RetentionProperties retentionProperties;

  public enum ThresholdTargetType {
    IMAGE_DELETED,
    IMAGE_TEMPORARY,
    USER_DELETED,
    POST_DELETED
  }

  /**
   * 주어진 targetType에 해당하는 threshold 리턴
   *
   * @param thresholdTargetType
   * @return
   */
  public LocalDateTime thresholdFor(ThresholdTargetType thresholdTargetType) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime threshold = switch (thresholdTargetType) {
      case IMAGE_DELETED -> now.minus(retentionProperties.getImage().getDeleted());
      case IMAGE_TEMPORARY -> now.minus(retentionProperties.getImage().getTemporary());
      case USER_DELETED -> now.minus(retentionProperties.getUser().getDeleted());
      case POST_DELETED -> now.minus(retentionProperties.getPost().getDeleted());
    };

//    log.info("Threshold time: {}", threshold);
    return threshold;
  }
}
