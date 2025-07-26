package com.threadly.repository.token;

import com.threadly.token.DeleteTokenPort;
import com.threadly.token.FetchTokenPort;
import com.threadly.token.InsertBlackListToken;
import com.threadly.token.InsertRefreshToken;
import com.threadly.token.InsertTokenPort;
import com.threadly.token.UpsertRefreshToken;
import com.threadly.token.UpsertTokenPort;
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
public class TokenPortRepository implements InsertTokenPort, FetchTokenPort, UpsertTokenPort,
    DeleteTokenPort {

  private final RedisTemplate<String, String> redisTemplate;

  /**
   * @param insertRefreshToken
   * @deprecated {@link #upsertRefreshToken(UpsertRefreshToken)} 사용
   */
  @Deprecated
  @Override
  public void save(InsertRefreshToken insertRefreshToken) {

    String key = generateRefreshKey(insertRefreshToken.getUserId());
    log.debug("key: {}", key);

    /*refreshToken 저장*/
    redisTemplate.opsForValue()
        .set(key, insertRefreshToken.getRefreshToken(), insertRefreshToken.getDuration());

    log.debug("refreshToken 저장 완료");
  }


  @Override
  public void upsertRefreshToken(UpsertRefreshToken upsertRefreshToken) {
    /*token:refresh:{userId}*/
    String key = generateRefreshKey(upsertRefreshToken.getUserId());

    redisTemplate.opsForValue()
        .set(key, upsertRefreshToken.getRefreshToken(), upsertRefreshToken.getDuration());
    log.debug("신규 토큰 저장 완료");

  }


  @Override
  public boolean existsRefreshTokenByUserId(String userId) {
    String key = generateRefreshKey(userId);
    return
        redisTemplate.hasKey(key);
  }

  @Override
  public String findRefreshTokenByUserId(String userId) {
    return
        redisTemplate.opsForValue().get(generateRefreshKey(userId));
  }

  @Override
  public void saveBlackListToken(InsertBlackListToken insertBlackListToken) {
    String key = generateBlackListKey(insertBlackListToken.getAccessToken());

    redisTemplate.opsForValue()
        .set(key, insertBlackListToken.getUserId(), insertBlackListToken.getDuration());
    log.debug("BlackList 토큰 저장 완료");
  }

  @Override
  public void deleteRefreshToken(String userId) {
    String key = generateRefreshKey(userId);

    redisTemplate.delete(key);
    log.debug("RefreshToken 삭제 완료");
  }

  @Override
  public boolean existsBlackListTokenByAccessToken(String accessToken) {
    String key = generateBlackListKey(accessToken);
    return
        redisTemplate.hasKey(key);
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
  private static String generateRefreshKey(String userId) {
    return "token:refresh:" + userId;
  }

  /*
   * key : token:blacklist:{accessToken}
   * value : {userId}
   * */
  private static String generateBlackListKey(String accessToken) {
    return "token:blacklist:" + accessToken;
  }

}
