package com.threadly.core.service.notification;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.notification.Notification;
import com.threadly.core.domain.notification.Notification.ActorProfile;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.core.port.notification.NotificationEventPublisherPort;
import com.threadly.core.port.user.profile.fetch.FetchUserProfilePort;
import com.threadly.core.port.user.profile.fetch.UserProfileProjection;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final FetchUserProfilePort fetchUserProfilePort;
  private final NotificationEventPublisherPort notificationEventPublisherPort;

  public void sendPostLikeNotification(String targetUserId, String actorUserId, String postId) {
    /*사용자 조회*/
    UserProfileProjection actorUserProfile = fetchUserProfilePort.findUserProfileByUserId(
        actorUserId).orElseThrow(() -> new UserException(
        ErrorCode.USER_NOT_FOUND));

    /*NotificationEvent 생성*/
    Notification notification = Notification.newNotification(
        targetUserId,
        NotificationType.POST_LIKE,
        LocalDateTime.now(),
        new ActorProfile(
            actorUserProfile.getUserId(),
            actorUserProfile.getNickname(),
            actorUserProfile.getProfileImageUrl()
        ),
        new PostLikeMeta(
            postId,
            actorUserId
        )
    );

    /*이벤트 발행*/
    notificationEventPublisherPort.publish(notification);
  }

}
