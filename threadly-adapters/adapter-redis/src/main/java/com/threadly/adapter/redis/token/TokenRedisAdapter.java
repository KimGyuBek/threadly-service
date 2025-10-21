package com.threadly.adapter.redis.token;

import com.threadly.core.port.token.out.TokenCommandPort;
import com.threadly.core.port.token.out.TokenQueryPort;
import com.threadly.core.port.token.out.command.InsertBlackListTokenCommand;
import com.threadly.core.port.token.out.command.InsertRefreshTokenCommand;
import com.threadly.core.port.token.out.command.UpsertRefreshTokenCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * token redis 저장 repository
 */

@Repository
@RequiredArgsConstructor
public class TokenRedisAdapter implements TokenQueryPort,
    TokenCommandPort {

  private final TokenRepository tokenRepository;

  @Override
  public void deleteRefreshToken(String userId) {
    tokenRepository.deleteRefreshToken(userId);
  }

  @Override
  public void save(InsertRefreshTokenCommand insertRefreshTokenCommand) {
    tokenRepository.save(insertRefreshTokenCommand);
  }

  @Override
  public void saveBlackListToken(InsertBlackListTokenCommand insertBlackListTokenCommand) {
    tokenRepository.saveBlackListToken(insertBlackListTokenCommand);

  }

  @Override
  public void upsertRefreshToken(UpsertRefreshTokenCommand upsertRefreshTokenCommand) {
    tokenRepository.upsertRefreshToken(upsertRefreshTokenCommand);
  }

  @Override
  public boolean existsRefreshTokenByUserId(String userId) {
    return tokenRepository.existsRefreshTokenByUserId(userId);
  }

  @Override
  public String findRefreshTokenByUserId(String userId) {
    return
        tokenRepository.findRefreshTokenByUserId(userId);
  }

  @Override
  public boolean existsBlackListTokenByAccessToken(String accessToken) {
    return tokenRepository.existsBlackListTokenByAccessToken(accessToken);
  }
}
