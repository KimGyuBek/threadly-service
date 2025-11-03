package com.threadly.core.service.notification;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.notification.Notification;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.core.port.notification.out.NotificationEventPublisherPort;
import com.threadly.core.port.user.out.profile.UserProfileQueryPort;
import com.threadly.core.port.user.out.profile.projection.UserProfileProjection;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * NotificationService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @InjectMocks
  private NotificationService notificationService;

  @Mock
  private NotificationEventPublisherPort notificationEventPublisherPort;

  @Mock
  private UserProfileQueryPort userProfileQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 알림 발행*/
    @Order(1)
    @DisplayName("1. 행위자 정보가 존재하면 알림이 발행되는지 검증")
    @Test
    void publish_shouldPublishNotification_whenActorExists() throws Exception {
      //given
      NotificationPublishCommand command = new NotificationPublishCommand(
          "receiver-1",
          "actor-1",
          NotificationType.POST_LIKE,
          new PostLikeMeta("post-1")
      );

      UserProfileProjection actorProfile = new UserProfileProjection() {
        @Override
        public String getUserId() {
          return "actor-1";
        }

        @Override
        public String getNickname() {
          return "행위자";
        }

        @Override
        public String getStatusMessage() {
          return null;
        }

        @Override
        public String getBio() {
          return null;
        }

        @Override
        public String getPhone() {
          return null;
        }

        @Override
        public String getProfileImageUrl() {
          return "/actor.png";
        }

        @Override
        public com.threadly.core.domain.user.UserStatus getUserStatus() {
          return com.threadly.core.domain.user.UserStatus.ACTIVE;
        }

        @Override
        public boolean getIsPrivate() {
          return false;
        }
      };

      when(userProfileQueryPort.findUserProfileByUserId(command.actorId()))
          .thenReturn(Optional.of(actorProfile));

      ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

      //when
      notificationService.publish(command);

      //then
      verify(userProfileQueryPort).findUserProfileByUserId(command.actorId());
      verify(notificationEventPublisherPort).publish(notificationCaptor.capture());

      Notification notification = notificationCaptor.getValue();
      assertThat(notification.getReceiverId()).isEqualTo("receiver-1");
      assertThat(notification.getNotificationType()).isEqualTo(NotificationType.POST_LIKE);
      assertThat(notification.getActorProfile().userId()).isEqualTo("actor-1");
      assertThat(notification.getActorProfile().nickname()).isEqualTo("행위자");
      assertThat(notification.getMetadata()).isInstanceOf(PostLikeMeta.class);
      assertThat(((PostLikeMeta) notification.getMetadata()).postId()).isEqualTo("post-1");
      assertThat(notification.getOccurredAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 행위자 정보 미존재*/
    @Order(1)
    @DisplayName("1. 행위자 정보가 없으면 예외가 발생하는지 검증")
    @Test
    void publish_shouldThrow_whenActorNotFound() throws Exception {
      //given
      NotificationPublishCommand command = new NotificationPublishCommand(
          "receiver-1",
          "actor-unknown",
          NotificationType.POST_LIKE,
          new PostLikeMeta("post-1")
      );

      when(userProfileQueryPort.findUserProfileByUserId(command.actorId()))
          .thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> notificationService.publish(command))
          .isInstanceOf(UserException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.USER_NOT_FOUND);

      verify(notificationEventPublisherPort, never()).publish(org.mockito.Mockito.any());
    }
  }
}
