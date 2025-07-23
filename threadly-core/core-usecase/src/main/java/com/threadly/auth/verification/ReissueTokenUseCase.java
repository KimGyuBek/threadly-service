package com.threadly.auth.verification;

import com.threadly.user.profile.register.UserProfileRegistrationApiResponse;

/**
 * 임시
 */
public interface ReissueTokenUseCase {

  UserProfileRegistrationApiResponse reissueToken(String userId);


}
