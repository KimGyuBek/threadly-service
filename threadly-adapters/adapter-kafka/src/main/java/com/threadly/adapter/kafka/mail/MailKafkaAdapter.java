package com.threadly.adapter.kafka.mail;

import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.mail.model.MailModel;
import com.threadly.core.port.mail.out.MailEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * MailKafkaAdaper
 */
@Repository
@RequiredArgsConstructor
public class MailKafkaAdapter implements MailEventPublisherPort {

  private final MailProducer mailProducer;

  @Override
  public void publish(String eventId, MailType mailType, String to, MailModel model) {
    mailProducer.publish(eventId, mailType, to, model);
  }
}
