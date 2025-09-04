package com.threadly.adapter.kafka.mail;

import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.mail.model.MailModel;

/**
 * Kafka Mail event 객체
 */
public record MailEvent(
    String eventId,
    MailType mailType,
    String to,
    MailModel model
) {

}
