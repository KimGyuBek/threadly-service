package com.threadly.core.port.auth.in.verification.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자 이중 인증을 위한 코드
 */
@Schema(description = "비밀번호 인증 토큰 응답")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordVerificationToken {

  private String verifyToken;

}
