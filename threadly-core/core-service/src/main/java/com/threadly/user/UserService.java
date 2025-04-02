package com.threadly.user;

import com.threadly.user.command.UserRegisterationCommand;
import com.threadly.user.response.UserRegisterationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase {

  private final InsertUserPort insertUserPort;

  @Override
  public UserRegisterationResponse register(UserRegisterationCommand command) {
    UserPortResponse userPortResponse = insertUserPort.create(
        new CreateUser(
            command.getEmail(),
            command.getUserName(),
            command.getPassword(),
            command.getPhone()
        )
    );

    return UserRegisterationResponse.builder()
        .userId(userPortResponse.getUserId())
        .userName(userPortResponse.getUserName())
        .email(userPortResponse.getEmail())
        .userType(userPortResponse.getUserType())
        .build();
  }
}
