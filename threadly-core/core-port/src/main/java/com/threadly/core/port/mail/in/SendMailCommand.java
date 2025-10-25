package com.threadly.core.port.mail.in;

import com.threadly.core.domain.mail.MailType;

/**
 * 이메일 전송 command 객체
 */
public record SendMailCommand(
    String userId,
    String email,
    String userName,
    MailType mailType
) {

}
