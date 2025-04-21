package com.threadly.auth.token.response;

public interface UpdateTokenUseCase {

  LoginTokenResponse upsertToken(String userId, String accessToken, String refreshToken);

}
