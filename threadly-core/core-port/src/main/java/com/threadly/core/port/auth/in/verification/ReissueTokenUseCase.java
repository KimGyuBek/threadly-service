package com.threadly.core.port.auth.in.verification;

import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileApiResponse;

/**
 * 토큰 재발급 usecase
 */
public interface ReissueTokenUseCase {

  RegisterMyProfileApiResponse reissueToken(String userId);


}
