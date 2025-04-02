package com.threadly.user;

import com.threadly.user.command.UserRegisterationCommand;
import com.threadly.user.response.UserRegisterationResponse;

public interface RegisterUserUseCase {

  UserRegisterationResponse register(UserRegisterationCommand request);


}
