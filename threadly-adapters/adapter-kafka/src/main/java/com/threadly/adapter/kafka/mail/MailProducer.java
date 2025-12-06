package com.threadly.adapter.kafka.mail;

import com.threadly.adapter.kafka.config.KafkaProducerLogger;
import com.threadly.adapter.kafka.exception.KafkaPublishException;
import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.mail.model.MailModel;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

/**
 * Kafka 메일 Producer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  private static final String TOPIC = "mail";

  private final KafkaProducerLogger kafkaProducerLogger;

  @Retry(name = "kafka-mail", fallbackMethod = "publishFallback")
  public void publish(String eventId, MailType mailType, String to, MailModel model) {

    MailEvent event = new MailEvent(
        eventId,
        mailType,
        to,
        model
    );

    String kafkaKey = event.to();

    try {
      SendResult<String, Object> result = kafkaTemplate.send(TOPIC, kafkaKey, event).get();
      kafkaProducerLogger.logPublishSuccess(TOPIC, eventId, event.to(), result);

    } catch (Exception e) {
      kafkaProducerLogger.logRetryableFailure(TOPIC, eventId, to, e);
      throw new KafkaPublishException("Kafka 발행 실패", e);
    }
  }

  public void publishFallback(String eventId, MailType mailType, String to, MailModel model,
      Exception ex) {
    kafkaProducerLogger.logPublishFailure(TOPIC, eventId, to, ex);
  }
}
