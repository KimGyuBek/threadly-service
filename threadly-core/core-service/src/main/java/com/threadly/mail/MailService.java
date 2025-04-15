package com.threadly.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService implements MailUseCase{

  private final SendMailPort sendMailPort;

  @Override
  public void sendVerificationEmail(String userId, String email) {


  }
}
