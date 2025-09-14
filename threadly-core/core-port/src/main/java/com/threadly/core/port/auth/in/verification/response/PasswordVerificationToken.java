package com.threadly.core.port.auth.in.verification.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 이중 인증을 위한 코드
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordVerificationToken {

  private String verifyToken;

}
