package com.threadly.adapter.redis.token;

import com.threadly.core.port.token.out.command.InsertBlackListTokenCommand;
import com.threadly.core.port.token.out.command.InsertRefreshTokenCommand;
import com.threadly.core.port.token.out.command.UpsertRefreshTokenCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TokenRepository {

  private final RedisTemplate<String, String> redisTemplate;

  /**
   * @param insertRefreshTokenCommand
   * @deprecated {@link #upsertRefreshToken(UpsertRefreshTokenCommand)} 사용
   */
  @Deprecated
  public void save(InsertRefreshTokenCommand insertRefreshTokenCommand) {

    String key = generateRefreshKey(insertRefreshTokenCommand.getUserId());
    log.debug("key: {}", key);

    /*refreshToken 저장*/
    redisTemplate.opsForValue()
        .set(key, insertRefreshTokenCommand.getRefreshToken(), insertRefreshTokenCommand.getDuration());

    log.debug("refreshToken 저장 완료");
  }


  public void upsertRefreshToken(UpsertRefreshTokenCommand upsertRefreshTokenCommand) {
    /*token:refresh:{userId}*/
    String key = generateRefreshKey(upsertRefreshTokenCommand.getUserId());

    redisTemplate.opsForValue()
        .set(key, upsertRefreshTokenCommand.getRefreshToken(), upsertRefreshTokenCommand.getDuration());
    log.debug("신규 토큰 저장 완료");

  }


  public boolean existsRefreshTokenByUserId(String userId) {
    String key = generateRefreshKey(userId);
    return
        redisTemplate.hasKey(key);
  }

  public String findRefreshTokenByUserId(String userId) {
    return
        redisTemplate.opsForValue().get(generateRefreshKey(userId));
  }

  public void saveBlackListToken(InsertBlackListTokenCommand insertBlackListTokenCommand) {
    String key = generateBlackListKey(insertBlackListTokenCommand.getAccessToken());

    redisTemplate.opsForValue()
        .set(key, insertBlackListTokenCommand.getUserId(), insertBlackListTokenCommand.getDuration());
    log.debug("BlackList 토큰 저장 완료");
  }

  public void deleteRefreshToken(String userId) {
    String key = generateRefreshKey(userId);

    redisTemplate.delete(key);
    log.debug("RefreshToken 삭제 완료");
  }

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
