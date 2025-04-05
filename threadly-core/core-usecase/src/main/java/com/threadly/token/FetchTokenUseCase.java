package com.threadly.token;

import com.threadly.token.response.TokenResponse;

public interface FetchTokenUseCase {

  TokenResponse upsertToken(String userId);

}
