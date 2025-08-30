package com.threadly.core.domain.notification.metadata;

import com.threadly.core.domain.notification.NotificationType;

/**
 * 팔로우 알림 메타 데이터
 */
public record FollowRequestMeta(
) implements NotificationMetaData {

  @Override
  public NotificationType notificationType() {
    return NotificationType.FOLLOW_REQUEST;
  }
}
