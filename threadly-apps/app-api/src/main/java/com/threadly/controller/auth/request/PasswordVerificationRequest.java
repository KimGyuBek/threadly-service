package com.threadly.controller.auth.request;

import com.threadly.annotation.PasswordEncryption;
import lombok.Getter;

/**
 * 사용자 정보 변경을 위한 이중인증 요청 바디
 */
@Getter
public class PasswordVerificationRequest {

  private String password;



}
