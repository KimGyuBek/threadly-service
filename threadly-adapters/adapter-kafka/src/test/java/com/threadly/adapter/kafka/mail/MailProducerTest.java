package com.threadly.adapter.kafka.mail;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.adapter.kafka.config.KafkaProducerLogger;
import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.mail.model.WelcomeModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Mail Producer Retry 정책 테스트
 */
@DisplayName("MailProducer 재시도 정책 테스트")
@SpringBootTest
class MailProducerTest {

  @Autowired
  private MailProducer mailProducer;

  @MockBean
  private KafkaTemplate<String, Object> kafkaTemplate;

  @MockBean
  private KafkaProducerLogger kafkaProducerLogger;

  @DisplayName("Kafka 발행 실패 시 3회 재시도 검증")
  @Test
  public void verify_retry_3times() throws Exception {
    //given
    when(kafkaTemplate.send(anyString(), anyString(), any()))
        .thenThrow(new KafkaException("Kafka 연결 실패"));

    //when
    mailProducer.publish("eventId", MailType.WELCOME, "to", new WelcomeModel("", ""));

    //then
    verify(kafkaTemplate, times(3)).send(anyString(), anyString(), any());
  }
}