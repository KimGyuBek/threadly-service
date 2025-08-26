package com.threadly.core.domain.notification.metadata;

public record PostCommentMeta(
    String postId, String commentId, String commenterId, String commentExcerpt
) implements NotificationMetaData{

}
