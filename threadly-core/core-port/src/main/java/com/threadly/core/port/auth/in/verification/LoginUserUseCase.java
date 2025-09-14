package com.threadly.core.port.auth.in.verification;

import com.threadly.core.port.auth.in.token.response.LoginTokenApiResponse;

/**
 * 사용자 로그인 usecase
 */
public interface LoginUserUseCase {

  LoginTokenApiResponse login(String email, String password);


}
