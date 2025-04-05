package com.threadly.token;

import java.util.Optional;

public interface TokenPort {

  Optional<Token> findByUserId(String userId);

  Token upsertToken(Token token);

}
