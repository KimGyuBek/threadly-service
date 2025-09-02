package com.threadly.adapter.kafka.notification;

import com.threadly.adapter.kafka.config.KafkaErrorHandler;
import com.threadly.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.core.domain.notification.Notification;
import com.threadly.core.port.notification.NotificationEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka 알림 producer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer implements NotificationEventPublisherPort {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final KafkaErrorHandler kafkaErrorHandler;

  @Override
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

    kafkaTemplate.send(
        "notification",
        kafkaKey,
        event
    ).whenComplete((result, ex) -> {
      if (ex == null) {
        kafkaErrorHandler.successCallback(event.getEventId(), kafkaKey).onSuccess(result);
      } else {
        kafkaErrorHandler.failureCallback(event.getEventId(), kafkaKey).onFailure(ex);
      }
    });
  }
}
