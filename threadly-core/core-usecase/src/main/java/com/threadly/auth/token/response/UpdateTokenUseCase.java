package com.threadly.auth.token.response;

public interface UpdateTokenUseCase {

  TokenResponse upsertToken(String userId, String accessToken, String refreshToken);

}
