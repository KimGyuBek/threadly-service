package com.threadly.core.usecase.auth.verification;

import com.threadly.core.usecase.auth.verification.response.PasswordVerificationToken;

/**
 * 사용자 이중 인증 usecase
 */
public interface PasswordVerificationUseCase {

  /**
   * 이중 인증을 위한 토큰 생성
   * @param userId
   * @param password
   * @return
   */
  PasswordVerificationToken getPasswordVerificationToken(String userId, String password);



}
