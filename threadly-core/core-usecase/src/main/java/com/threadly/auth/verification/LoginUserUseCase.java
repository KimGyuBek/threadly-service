package com.threadly.auth.verification;

import com.threadly.auth.token.response.LoginTokenApiResponse;

/**
 * 사용자 로그인 usecase
 */
public interface LoginUserUseCase {

  LoginTokenApiResponse login(String email, String password);


}
