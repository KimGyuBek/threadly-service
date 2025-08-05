package com.threadly.auth.verification;

import com.threadly.user.profile.command.dto.RegisterMyProfileApiResponse;

/**
 * 임시
 */
public interface ReissueTokenUseCase {

  RegisterMyProfileApiResponse reissueToken(String userId);


}
