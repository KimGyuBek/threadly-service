package com.threadly.core.port.mail;

import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.mail.model.MailModel;

/**
 * 메일 이벤트 발행 port
 */
public interface MailEventPublisherPort {

  void publish(String eventId, MailType mailType, String to, MailModel model);

}
