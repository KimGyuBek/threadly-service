package com.threadly.repository.token;

import com.threadly.token.FetchTokenPort;
import com.threadly.token.InsertRefreshToken;
import com.threadly.token.InsertTokenPort;
import com.threadly.token.UpsertRefreshToken;
import com.threadly.token.UpsertToken;
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
public class TokenRepository implements InsertTokenPort, FetchTokenPort, UpsertToken {

  private final RedisTemplate<String, String> redisTemplate;

  /**
   * @param insertRefreshToken
   * @deprecated {@link #upsertRefreshToken(UpsertRefreshToken)} 사용
   */
  @Deprecated
  @Override
  public void save(InsertRefreshToken insertRefreshToken) {

    String key = generateKey(insertRefreshToken.getUserId());
    log.debug("key: {}", key);

    /*refreshToken 저장*/
    redisTemplate.opsForValue()
        .set(key, insertRefreshToken.getRefreshToken(), insertRefreshToken.getDuration());

    log.debug("refreshToken 저장 완료");
  }


  @Override
  public void upsertRefreshToken(UpsertRefreshToken upsertRefreshToken) {
    /*token:refresh:{userId}*/
    String key = generateKey(upsertRefreshToken.getUserId());

    redisTemplate.opsForValue()
        .set(key, upsertRefreshToken.getRefreshToken(), upsertRefreshToken.getDuration());
    log.debug("신규 토큰 저장 완료");

  }


  @Override
  public boolean existsRefreshTokenByUserId(String userId) {
    String key = generateKey(userId);
    return
        redisTemplate.hasKey(key);
  }

  @Override
  public String findRefreshTokenByUserId(String userId) {
    return
        redisTemplate.opsForValue().get(generateKey(userId));
  }

  /**
   * key 생성
   *
   * @param userId
   * @return
   */
  /*
   * key : token:refresh:{userId}
   * value : {refreshToken}
   * */
  private static String generateKey(String userId) {
    return "token:refresh:" + userId;
  }

}
