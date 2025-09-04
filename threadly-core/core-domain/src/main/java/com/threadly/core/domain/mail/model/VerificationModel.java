package com.threadly.core.domain.mail.model;

public record VerificationModel(
    String userName,
    String verificationUrl
)
    implements MailModel {


  @Override
  public String userName() {
    return userName;
  }
}
