package com.threadly.token;

public interface UpdateTokenPort {

  /**
   * upsert Token
   * @param token
   * @return
   */
  Token upsertToken(Token token);

}
