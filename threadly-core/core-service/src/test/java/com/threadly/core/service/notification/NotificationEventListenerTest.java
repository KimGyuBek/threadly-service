package com.threadly.core.service.notification;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * NotificationEventListener 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

  @InjectMocks
  private NotificationEventListener notificationEventListener;

  @Mock
  private NotificationService notificationService;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 알림 발행 이벤트 처리*/
    @Order(1)
    @DisplayName("1. 알림 발행 이벤트를 수신하면 서비스가 호출되는지 검증")
    @Test
    void onNotificationPublished_shouldCallService() throws Exception {
      //given
      NotificationPublishCommand command = new NotificationPublishCommand(
          "receiver-1",
          "actor-1",
          NotificationType.POST_LIKE,
          new PostLikeMeta("post-1")
      );

      //when
      notificationEventListener.onNotificationPublished(command);

      //then
      verify(notificationService).publish(command);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 알림 발행 실패 시 예외 처리*/
    @Order(1)
    @DisplayName("1. 서비스 호출에서 예외가 발생해도 추가 동작이 없는지 검증")
    @Test
    void onNotificationPublished_shouldHandleExceptionGracefully() throws Exception {
      //given
      NotificationPublishCommand command = new NotificationPublishCommand(
          "receiver-1",
          "actor-1",
          NotificationType.POST_LIKE,
          new PostLikeMeta("post-1")
      );

      doThrow(new RuntimeException("publish failed"))
          .when(notificationService).publish(command);

      //when
      notificationEventListener.onNotificationPublished(command);

      //then
      verify(notificationService).publish(command);
      verifyNoMoreInteractions(notificationService);
    }
  }
}
