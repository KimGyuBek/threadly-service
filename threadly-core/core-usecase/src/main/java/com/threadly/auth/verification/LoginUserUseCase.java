package com.threadly.auth.verification;

import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.user.response.UserLoginResponse;

/**
 * 사용자 로그인 usecase
 */
public interface LoginUserUseCase {

  LoginTokenResponse login(String email, String password);


}
