package com.threadly.auth.verification;

import com.threadly.user.profile.register.MyProfileRegisterApiResponse;

/**
 * 임시
 */
public interface ReissueTokenUseCase {

  MyProfileRegisterApiResponse reissueToken(String userId);


}
