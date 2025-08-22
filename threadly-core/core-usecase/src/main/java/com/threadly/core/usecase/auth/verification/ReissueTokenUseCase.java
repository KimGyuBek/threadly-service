package com.threadly.core.usecase.auth.verification;

import com.threadly.core.usecase.user.profile.command.dto.RegisterMyProfileApiResponse;

/**
 * 임시
 */
public interface ReissueTokenUseCase {

  RegisterMyProfileApiResponse reissueToken(String userId);


}
