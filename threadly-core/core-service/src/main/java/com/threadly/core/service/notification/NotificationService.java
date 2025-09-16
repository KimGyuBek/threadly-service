package com.threadly.core.service.notification;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.notification.Notification;
import com.threadly.core.domain.notification.Notification.ActorProfile;
import com.threadly.core.domain.notification.metadata.NotificationMetaData;
import com.threadly.core.port.notification.out.NotificationEventPublisherPort;
import com.threadly.core.port.user.out.profile.UserProfileQueryPort;
import com.threadly.core.port.user.out.profile.projection.UserProfileProjection;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  private final NotificationEventPublisherPort notificationEventPublisherPort;

  private final UserProfileQueryPort userProfileQueryPort;

  /**
   * 알림 발행
   *
   * @param command
   */
  public void publish(NotificationPublishCommand command) {
    /*이벤트 발행*/
    notificationEventPublisherPort.publish(generateNotification(
        command.receiverId(),
        command.actorId(),
        command.notificationMetaData()
    ));
  }

  /**
   * 행위자 조회 후 Notification 도메인 생성
   *
   * @param targetUserId
   * @param actorUserId
   * @param metadata
   * @param <T>
   * @return
   */
  private <T extends NotificationMetaData> Notification generateNotification(String targetUserId,
      String actorUserId, T metadata) {
    /*사용자 조회*/
    UserProfileProjection actorUserProfile = getActorProfile(
        actorUserId);

    /*NotificationEvent 생성*/
    return Notification.newNotification(
        targetUserId,
        metadata.notificationType(),
        LocalDateTime.now(),
        new ActorProfile(
            actorUserProfile.getUserId(),
            actorUserProfile.getNickname(),
            actorUserProfile.getProfileImageUrl()
        ),
        metadata);
  }

  /**
   * 행위자 프로필 조회
   *
   * @param actorUserId
   * @return
   */
  private UserProfileProjection getActorProfile(String actorUserId) {
    UserProfileProjection actorUserProfile = userProfileQueryPort.findUserProfileByUserId(
        actorUserId).orElseThrow(() -> new UserException(
        ErrorCode.USER_NOT_FOUND));
    return actorUserProfile;
  }

}
