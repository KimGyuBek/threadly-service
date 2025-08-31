package com.threadly.core.service.notification;

import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 알림 발행 이벤트 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventHandler {

  private final NotificationService notificationService;

  @Async("eventExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleNotificationEvent(NotificationPublishCommand command) {

    try {
      notificationService.publish(command);
      log.debug("Published notification event for command {}", command);

    } catch (Exception e) {
      log.error("Failed to publish notification event for command {}", command, e);
    }
  }
}
