package com.threadly.core.domain.notification.metadata;

public record CommentLikeMeta(
    String commentId, String likerId, String commentExcerpt
) implements NotificationMetaData {

}
