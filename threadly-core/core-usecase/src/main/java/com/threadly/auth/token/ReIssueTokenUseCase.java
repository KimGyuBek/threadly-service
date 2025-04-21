package com.threadly.auth.token;

import com.threadly.auth.token.response.TokenReissueResponse;

/**
 * Token 재발급 usecase
 */
public interface ReIssueTokenUseCase {

  TokenReissueResponse reIssueAccessToken(String userId);

}
