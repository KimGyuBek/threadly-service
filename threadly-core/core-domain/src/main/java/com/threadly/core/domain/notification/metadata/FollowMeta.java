package com.threadly.core.domain.notification.metadata;

import com.threadly.core.domain.notification.NotificationType;

/**
 * 팔로우 메타 데이터
 *
 */
public record FollowMeta(
) implements NotificationMetaData {

  @Override
  public NotificationType notificationType() {
    return NotificationType.FOLLOW_REQUEST;
  }
}
