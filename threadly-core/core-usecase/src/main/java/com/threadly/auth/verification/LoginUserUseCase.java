package com.threadly.auth.verification;

import com.threadly.auth.token.response.LoginTokenResponse;

/**
 * 사용자 로그인 usecase
 */
public interface LoginUserUseCase {

  LoginTokenResponse login(String email, String password);


}
