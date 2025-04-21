package com.threadly.token;

import com.threadly.auth.token.ReIssueTokenUseCase;
import com.threadly.auth.token.response.TokenReissueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * TokenService
 */
@Service
@RequiredArgsConstructor
public class TokenService implements ReIssueTokenUseCase {

  @Override
  public TokenReissueResponse reIssueAccessToken(String userId) {
    return null;
  }
}
