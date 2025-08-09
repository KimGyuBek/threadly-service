package com.threadly.user.account.command;

import com.threadly.user.account.command.dto.RegisterUserApiResponse;
import com.threadly.user.account.command.dto.RegisterUserCommand;

/**
 * 사용자 등록 관련 usecase
 */
public interface RegisterUserUseCase {

  /**
   * 사용자 회원 가입
   * @param request
   * @return
   */
  RegisterUserApiResponse register(RegisterUserCommand request);


}
