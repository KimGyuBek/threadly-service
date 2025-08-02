package com.threadly.auth.verification;

import com.threadly.user.profile.register.RegisterMyProfileApiResponse;

/**
 * 임시
 */
public interface ReissueTokenUseCase {

  RegisterMyProfileApiResponse reissueToken(String userId);


}
