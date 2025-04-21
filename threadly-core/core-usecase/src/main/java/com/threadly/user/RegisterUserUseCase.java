package com.threadly.user;

import com.threadly.user.command.UserRegistrationCommand;
import com.threadly.user.response.UserRegistrationResponse;

public interface RegisterUserUseCase {

  UserRegistrationResponse register(UserRegistrationCommand request);


}
