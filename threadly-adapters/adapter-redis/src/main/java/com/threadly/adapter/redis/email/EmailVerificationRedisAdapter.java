package com.threadly.adapter.redis.email;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.mail.EmailVerificationException;
import com.threadly.core.port.verification.EmailVerificationPort;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * email 인증 repository
 */
@Repository
@RequiredArgsConstructor
public class EmailVerificationRedisAdapter implements EmailVerificationPort {

  private final EmailVerificationRepository emailVerificationRepository;

  @Override
  public void saveCode(String userId, String code, Duration expiration) {
    emailVerificationRepository.saveCode(userId, code, expiration);
  }

  @Override
  public String getUserId(String code) {
    return emailVerificationRepository.getUserId(code);
  }

  @Override
  public void deleteCode(String code) {
    emailVerificationRepository.deleteCode(code);
  }
}
