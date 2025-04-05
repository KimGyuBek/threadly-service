package com.threadly.token.response;

public interface UpdateTokenUseCase {

  TokenResponse upsertToken(String userId, String accessToken, String refreshToken);

}
