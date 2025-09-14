package com.threadly.adapter.redis.repository.token;

import com.threadly.core.port.token.out.command.TokenCommandPort;
import com.threadly.core.port.token.out.command.dto.InsertBlackListTokenCommand;
import com.threadly.core.port.token.out.command.dto.InsertRefreshTokenCommand;
import com.threadly.core.port.token.out.command.dto.UpsertRefreshTokenCommand;
import com.threadly.core.port.token.out.query.TokenQueryPort;
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
public class TokenCommandPortRepository implements TokenQueryPort,
    TokenCommandPort {

  private final RedisTemplate<String, String> redisTemplate;

  /**
   * @param insertRefreshTokenCommand
   * @deprecated {@link #upsertRefreshToken(UpsertRefreshTokenCommand)} 사용
   */
  @Deprecated
  @Override
  public void save(InsertRefreshTokenCommand insertRefreshTokenCommand) {

    String key = generateRefreshKey(insertRefreshTokenCommand.getUserId());
    log.debug("key: {}", key);

    /*refreshToken 저장*/
    redisTemplate.opsForValue()
        .set(key, insertRefreshTokenCommand.getRefreshToken(), insertRefreshTokenCommand.getDuration());

    log.debug("refreshToken 저장 완료");
  }


  @Override
  public void upsertRefreshToken(UpsertRefreshTokenCommand upsertRefreshTokenCommand) {
    /*token:refresh:{userId}*/
    String key = generateRefreshKey(upsertRefreshTokenCommand.getUserId());

    redisTemplate.opsForValue()
        .set(key, upsertRefreshTokenCommand.getRefreshToken(), upsertRefreshTokenCommand.getDuration());
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
  public void saveBlackListToken(InsertBlackListTokenCommand insertBlackListTokenCommand) {
    String key = generateBlackListKey(insertBlackListTokenCommand.getAccessToken());

    redisTemplate.opsForValue()
        .set(key, insertBlackListTokenCommand.getUserId(), insertBlackListTokenCommand.getDuration());
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
