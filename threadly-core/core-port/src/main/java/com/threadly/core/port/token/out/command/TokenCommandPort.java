package com.threadly.core.port.token.out.command;

import com.threadly.core.port.token.out.command.dto.InsertBlackListTokenCommand;
import com.threadly.core.port.token.out.command.dto.InsertRefreshTokenCommand;
import com.threadly.core.port.token.out.command.dto.UpsertRefreshTokenCommand;

/**
 * token command port
 */
public interface TokenCommandPort {

  /**
   * refreshToken 삭제
   *
   * @param userId
   */
  void deleteRefreshToken(String userId);

  /**
   * refreshToken 저장
   *
   * @param insertRefreshTokenCommand
   * @return
   */
  void save(InsertRefreshTokenCommand insertRefreshTokenCommand);

  /**
   * accessToken을 블랙리스트 토큰으로 저장
   *
   * @param insertBlackListTokenCommand
   */
  void saveBlackListToken(InsertBlackListTokenCommand insertBlackListTokenCommand);

  /**
   * refreshToken upsert
   * @param upsertRefreshTokenCommand
   */
  void upsertRefreshToken(UpsertRefreshTokenCommand upsertRefreshTokenCommand);
}
