package com.threadly.adapter.kafka.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.threadly.core.domain.notification.Notification;
import com.threadly.core.domain.notification.Notification.ActorProfile;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.NotificationMetaData;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationEvent {

  /*eventId*/
  private String eventId;

  /*알림 받는 사용자*/
  private String receiverUserId;

  /*Event 종류*/
  private NotificationType notificationType;

  /*행위자 프로필*/
  private ActorProfile actorProfile;

  /*이벤트 발생 시각*/
  private LocalDateTime occurredAt;

  /*메타 데이터*/
  private NotificationMetaData metadata;

  @JsonCreator
  public NotificationEvent(
      @JsonProperty("eventId") String eventId,
      @JsonProperty("receiverUserId") String receiverUserId,
      @JsonProperty("notificationType") NotificationType notificationType,
      @JsonProperty("occurredAt") LocalDateTime occurredAt,
      @JsonProperty("actorProfile") ActorProfile actorProfile,
      @JsonProperty("metadata") NotificationMetaData metadata
  ) {
    this.eventId = eventId;
    this.receiverUserId = receiverUserId;
    this.notificationType = notificationType;
    this.occurredAt = occurredAt;
    this.actorProfile = actorProfile;
    this.metadata = metadata;
  }

  /**
   * domain -> event
   * @param notification
   * @return
   */
  public static NotificationEvent fromDomain(Notification notification) {
    return new NotificationEvent(
        notification.getEventId(),
        notification.getReceiverId(),
        notification.getNotificationType(),
        notification.getOccurredAt(),
        notification.getActorProfile(),
        notification.getMetadata()
    );
  }
}