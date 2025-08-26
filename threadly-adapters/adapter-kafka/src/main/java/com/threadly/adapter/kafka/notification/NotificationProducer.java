package com.threadly.adapter.kafka.notification;

import com.threadly.adapter.kafka.notification.dto.NotificationEvent;
import com.threadly.core.domain.notification.Notification;
import com.threadly.core.port.notification.NotificationEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer implements NotificationEventPublisherPort {

  private final KafkaTemplate<String, Object> kafkaTemplate;

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

    kafkaTemplate.send(
        "notification",
        event
    ).whenComplete((result, ex) -> {
      if (ex == null) {
        log.info("Notification event published successfully: {}", event.getEventId());
      } else {
        log.error("Notification event publishing failed", ex);
      }
    });
  }
}
