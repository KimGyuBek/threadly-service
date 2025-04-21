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

  @Override
  public void save(InsertRefreshToken insertRefreshToken) {

    String key = generateKey(insertRefreshToken.getUserId());
    String reverseKey = generateReverseKey(insertRefreshToken.getRefreshToken());
    log.debug("key: {}", key);
    log.debug("reverseKey: {}", reverseKey);


    /*refreshToken 저장*/
    redisTemplate.opsForValue()
        .set(key, insertRefreshToken.getRefreshToken(), insertRefreshToken.getDuration());

    /*reverse map 저장*/
    redisTemplate.opsForValue()
        .set(reverseKey, insertRefreshToken.getUserId(), insertRefreshToken.getDuration());
    log.debug("refreshToken 저장 완료");
  }

  @Override
  public String findUserIdByRefreshToken(String refreshToken) {

    /*redis에서 userId 조회*/
    String userId = redisTemplate.opsForValue().get(generateReverseKey(refreshToken));

    return userId;
  }

  @Override
  public void upsertRefreshToken(UpsertRefreshToken upsertRefreshToken) {
    /*token:refresh:{userId}*/
    String key = generateKey(upsertRefreshToken.getUserId());

    /*token:reverse:{refreshToken}*/
    String refreshToken = redisTemplate.opsForValue().get(key);

    /*reverse map이 존재한다면 삭제*/
    if (refreshToken != null) {
      String reverseKey = generateReverseKey(refreshToken);
      redisTemplate.delete(reverseKey);
      log.debug("기존 reverse map 삭제 완료");
    }

    redisTemplate.opsForValue()
        .set(key, upsertRefreshToken.getRefreshToken(), upsertRefreshToken.getDuration());
    log.debug("신규 토큰 저장 완료");

    String newReverseKey = generateReverseKey(upsertRefreshToken.getRefreshToken());
    redisTemplate.opsForValue()
        .set(newReverseKey, upsertRefreshToken.getUserId(), upsertRefreshToken.getDuration());
    log.debug("reverse map 저장 완료");
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

  /*
   * key : token:reverse:{refreshToken}
   * value : {userId}
   *  */
  private static String generateReverseKey(String refreshToken) {
    return "token:reverse:" + refreshToken;
  }
}
