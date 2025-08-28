package com.threadly.core.domain.notification.metadata;

import com.threadly.core.domain.notification.NotificationType;

/**
 * Notification metadata
 */
public sealed interface NotificationMetaData
    permits PostLikeMeta, PostCommentMeta, CommentLikeMeta, FollowRequestMeta, FollowMeta,
    FollowAcceptMeta {

  NotificationType notificationType();

}

