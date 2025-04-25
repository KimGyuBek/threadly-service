package com.threadly.token;

public interface InsertTokenPort {

  /**
   * refreshToken 저장
   *
   * @param insertRefreshToken
   * @return
   */
  void save(InsertRefreshToken insertRefreshToken);

  /**
   * accessToken을 블랙리스트 토큰으로 저장
   * @param insertBlackListToken
   */
  void saveBlackListToken(InsertBlackListToken insertBlackListToken);

}
