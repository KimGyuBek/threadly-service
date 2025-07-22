package com.threadly.auth.verification;

import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.user.response.UserProfileSetupApiResponse;

/**
 * 임시
 */
public interface ReissueTokenUseCase {

  UserProfileSetupApiResponse reissueToken(String userId);


}
