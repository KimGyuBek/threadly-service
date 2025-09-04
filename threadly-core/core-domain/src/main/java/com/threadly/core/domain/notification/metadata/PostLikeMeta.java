package com.threadly.core.domain.notification.metadata;

import com.threadly.core.domain.notification.NotificationType;

/**
 * 게시글 좋아요 메타 데이터
 *
 * @param postId
 */
public record PostLikeMeta(
    String postId
) implements NotificationMetaData {

  @Override
  public NotificationType notificationType() {
    return NotificationType.POST_LIKE;
  }
}
