package com.threadly.controller.user.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * mail 전송 테스트를 위한 요청dto
 */
@Getter
@AllArgsConstructor
public class RequestMail {

  private String from;
  private String to;
  private String subject;
  private String body;


}
