package com.threadly.adapter.kafka.notification;

import com.threadly.adapter.kafka.config.KafkaProducerLogger;
import com.threadly.adapter.kafka.exception.KafkaPublishException;
import com.threadly.core.domain.notification.Notification;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

/**
 * Kafka 알림 producer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final KafkaProducerLogger kafkaProducerLogger;

  private static final String TOPIC = "notification";

  @Retry(name = "kafka-notification", fallbackMethod = "publishFallback")
  public void publish(Notification notification) {
    NotificationEvent event = new NotificationEvent(
        notification.getEventId(),
        notification.getReceiverId(),
        notification.getNotificationType(),
        notification.getOccurredAt(),
        notification.getActorProfile(),
        notification.getMetadata()
    );

    String kafkaKey = event.getReceiverUserId();

    try {
      /*Kafka 이벤트 동기 발행 발행 */
      SendResult<String, Object> result = kafkaTemplate.send(
          TOPIC,
          kafkaKey,
          event
      ).get();

      /*로깅*/
      kafkaProducerLogger.logPublishSuccess(TOPIC, notification.getEventId(),
          notification.getReceiverId(), result);

    } catch (Exception e) {
      kafkaProducerLogger.logRetryableFailure(TOPIC, event.getEventId(), event.getReceiverUserId(),
          e);
      throw new KafkaPublishException("Kafka 발행 실패", e);
    }
  }

  /**
   * Fallback 메서드 - 모든 재시도 실패 시 호출
   *
   * @param notification
   * @param ex
   */
  public void publishFallback(Notification notification, Exception ex) {
    kafkaProducerLogger.logPublishFailure(TOPIC, notification.getEventId(),
        notification.getReceiverId(), ex);
  }
}
