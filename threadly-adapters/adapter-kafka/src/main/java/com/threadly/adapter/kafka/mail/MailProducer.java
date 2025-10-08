package com.threadly.adapter.kafka.mail;

import com.threadly.adapter.kafka.config.KafkaErrorHandler;
import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.mail.model.MailModel;
import com.threadly.core.port.mail.out.MailEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka 메일 Producer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailProducer  {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  private final KafkaErrorHandler kafkaErrorHandler;

  public void publish(String eventId, MailType mailType, String to, MailModel model) {

    MailEvent event = new MailEvent(
        eventId,
        mailType,
        to,
        model
    );

    String kafkaKey = event.to();

    kafkaTemplate.send(
        "mail",
        kafkaKey,
        event
    ).whenComplete((result, ex) -> {
      if (ex == null) {
        kafkaErrorHandler.successCallback(event.eventId(), kafkaKey).onSuccess(result);
      } else {
        kafkaErrorHandler.failureCallback(event.eventId(), kafkaKey).onFailure(ex);
      }
    });
  }
}
