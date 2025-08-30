package com.threadly.core.service.notification.dto;

import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.NotificationMetaData;

/**
 * 알림 발행 command 객체
 */
public record NotificationPublishCommand(
    String receiverId,
    String actorId,
    NotificationType notificationType,
    NotificationMetaData notificationMetaData
) {


}
