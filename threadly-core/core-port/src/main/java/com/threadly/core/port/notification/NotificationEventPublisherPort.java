package com.threadly.core.port.notification;

import com.threadly.core.domain.notification.Notification;

/**
 * Notification Kafka Event 발행 port
 */
public interface NotificationEventPublisherPort {

  /**
   * Notification event 발행
   * @param notification
   */
  void publish(Notification notification);

}
