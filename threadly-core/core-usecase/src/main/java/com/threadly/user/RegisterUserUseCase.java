package com.threadly.user;

import com.threadly.user.command.UserRegisterationCommand;
import com.threadly.user.response.UserRegistrationResponse;

public interface RegisterUserUseCase {

  UserRegistrationResponse register(UserRegisterationCommand request);


}
