package com.threadly.repository.token;

import com.threadly.ErrorCode;
import com.threadly.exception.token.TokenException;
import com.threadly.token.DeleteTokenPort;
import com.threadly.token.FetchTokenPort;
import com.threadly.token.InsertRefreshToken;
import com.threadly.token.InsertTokenPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * token redis 저장 repository
 */

@Repository
@RequiredArgsConstructor
@Slf4j
public class TokenRepository implements InsertTokenPort, FetchTokenPort, DeleteTokenPort {

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void save(InsertRefreshToken insertRefreshToken) {

    String key = generateKey(insertRefreshToken.getRefreshToken());
    log.debug("key: {}", key);

    redisTemplate.opsForValue()
        .set(key, insertRefreshToken.getUserId(), insertRefreshToken.getDuration());
    log.debug("refreshToken 저장 완료");


  }

  @Override
  public String findUserIdByRefreshToken(String refreshToken) {

    /*redis에서 userId 조회*/
    String userId = redisTemplate.opsForValue().get(generateKey(refreshToken));

    return userId;
  }

  @Override
  public void deleteRefreshToken(String refreshToken) {
    String key= generateKey(refreshToken);
    redisTemplate.delete(key);
  }

  /**
   * key 생성
   *
   * @param refreshToken
   * @return
   */
  private static String generateKey(String refreshToken) {
    return "token:refresh:" + refreshToken;
  }
}
