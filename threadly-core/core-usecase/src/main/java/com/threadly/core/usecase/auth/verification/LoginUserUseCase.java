package com.threadly.core.usecase.auth.verification;

import com.threadly.core.usecase.auth.token.response.LoginTokenApiResponse;

/**
 * 사용자 로그인 usecase
 */
public interface LoginUserUseCase {

  LoginTokenApiResponse login(String email, String password);


}
