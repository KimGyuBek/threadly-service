package com.threadly.adapter.kafka.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.adapter.kafka.config.KafkaProducerLogger;
import com.threadly.core.domain.notification.Notification;
import com.threadly.core.domain.notification.Notification.ActorProfile;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.FollowMeta;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 알림 Kafka 발행 실패 시 재시도 기능 테스트
 */
@SpringBootTest
class NotificationProducerTest {

  @Autowired
  private NotificationProducer notificationProducer;

  @MockBean
  private KafkaTemplate<String, Object> kafkaTemplate;

  @MockBean
  private KafkaProducerLogger kafkaProducerLogger;



  @DisplayName("Kafka 발행 실패 시 3회 재시도 검증")
  @Test
  public void test_01() throws Exception {
    //given
    Notification notification = createNotification();

    when(kafkaTemplate.send(anyString(), anyString(), any()))
        .thenThrow(new KafkaException("Kafka 연결 실패"));

    //when
    notificationProducer.publish(notification);

    //then
    verify(kafkaTemplate, times(3)).send(anyString(), anyString(), any());

  }

  private static Notification createNotification() {
    return Notification.newNotification(
        "receiverId",
        NotificationType.FOLLOW_ACCEPT,
        LocalDateTime.now(),
        new ActorProfile("userId", "nickname", "url"),
        new FollowMeta()
    );
  }


}