package com.threadly.core.domain.notification.metadata;

public record FollowRequestMeta(
    String followId, String followerId
) implements NotificationMetaData {

}
