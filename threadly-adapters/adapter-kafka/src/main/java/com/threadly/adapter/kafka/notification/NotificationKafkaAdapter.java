package com.threadly.adapter.kafka.notification;

import com.threadly.core.domain.notification.Notification;
import com.threadly.core.port.notification.out.NotificationEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * NotificationKafkaAdapter
 */
@Repository
@RequiredArgsConstructor
public class NotificationKafkaAdapter implements NotificationEventPublisherPort {

  private final NotificationProducer notificationProducer;

  @Override
  public void publish(Notification notification) {
    notificationProducer.publish(notification);
  }
}
