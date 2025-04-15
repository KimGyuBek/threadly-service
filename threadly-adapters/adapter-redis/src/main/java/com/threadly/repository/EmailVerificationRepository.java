package com.threadly.repository;

import com.threadly.verification.EmailVerificationPort;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * email 인증 repository
 */
@Repository
@RequiredArgsConstructor
public class EmailVerificationRepository implements EmailVerificationPort {

  private final RedisTemplate<String, Object> redisTemplate;

  /**
   * code 저장
   *
   * @param userId
   * @param code
   */
  public void saveCode(String userId, String code, Duration expiration) {
    String key = generateKey(code);

    System.out.println("인증 코드 : " + code);

    /**
     * key : email:verify:{code}
     * value : userId
     */
    redisTemplate.opsForValue().set(key, userId, expiration);
  }

  /**
   * code로 userId 조회
   *
   * @param code
   * @return
   */
  public String getUserId(String code) {
    String key = generateKey(code);

    String userId = (String) redisTemplate.opsForValue().get(key);

    return userId;
  }


  /**
   * code 삭제
   *
   * @param code
   */
  public void deleteCode(String code) {
    String key = generateKey(code);

    redisTemplate.delete(key);
  }

  /**
   * key 생성
   *
   * @param code
   * @return
   */
  private static String generateKey(String code) {
    return "email:verify:" + code;
  }


}
