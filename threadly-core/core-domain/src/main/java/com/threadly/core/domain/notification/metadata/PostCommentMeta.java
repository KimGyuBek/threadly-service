package com.threadly.core.domain.notification.metadata;

import com.threadly.core.domain.notification.NotificationType;

/**
 * 게시글 댓글 알림 메타 데이터
 * @param postId
 * @param commentId
 * @param commentExcerpt
 */
public record PostCommentMeta(
    String postId, String commentId, String commentExcerpt
) implements NotificationMetaData{

  @Override
  public NotificationType notificationType() {
    return NotificationType.COMMENT_ADDED;
  }
}
