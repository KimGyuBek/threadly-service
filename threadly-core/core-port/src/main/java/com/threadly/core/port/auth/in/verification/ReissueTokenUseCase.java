package com.threadly.core.port.auth.in.verification;

import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileApiResponse;

/**
 * 임시
 */
public interface ReissueTokenUseCase {

  RegisterMyProfileApiResponse reissueToken(String userId);


}
