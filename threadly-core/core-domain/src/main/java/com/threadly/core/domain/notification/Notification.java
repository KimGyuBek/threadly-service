package com.threadly.core.domain.notification;

import com.threadly.commons.utils.RandomUtils;
import com.threadly.core.domain.notification.metadata.NotificationMetaData;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 게시글 좋아요 도메인
 */
@Getter
@AllArgsConstructor
public class Notification {

  private String eventId;
  private String receiverId;
  private NotificationType notificationType;
  private LocalDateTime occurredAt;
  private ActorProfile actorProfile;
  private boolean isRead;
  private NotificationMetaData metadata;

  /**
   * 새로운 Notification 도메인 생성
   *
   * @param receiverId
   * @param notificationType
   * @param occurredAt
   * @param metadata
   * @return
   */
  public static Notification newNotification(String receiverId,
      NotificationType notificationType,
      LocalDateTime occurredAt, ActorProfile actorProfile, NotificationMetaData metadata) {
    return new Notification(RandomUtils.generateNanoId(), receiverId, notificationType, occurredAt,
        actorProfile, false,
        metadata);
  }

  /**
   * 행위자 프로필
   *
   * @param userId
   * @param nickname
   * @param profileImageUrl
   */
  public record ActorProfile(String userId, String nickname, String profileImageUrl) {


  }
}
