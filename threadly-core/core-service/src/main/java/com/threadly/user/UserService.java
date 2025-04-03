package com.threadly.user;

import com.threadly.user.command.UserRegisterationCommand;
import com.threadly.user.response.UserRegisterationResponse;
import com.threadly.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase, FetchUserUseCase {

  private final InsertUserPort insertUserPort;
  private final FetchUserPort fetchUserPort;


  @Override
  public UserRegisterationResponse register(UserRegisterationCommand command) {
    UserPortResponse userPortResponse = insertUserPort.create(
        new CreateUser(
            command.getEmail(),
            command.getUserName(),
            command.getPassword(),
            command.getPhone()
        )
    ).get();

    return UserRegisterationResponse.builder()
        .userId(userPortResponse.getUserId())
        .userName(userPortResponse.getUserName())
        .email(userPortResponse.getEmail())
        .userType(userPortResponse.getUserType())
        .build();
  }

  @Override
  public UserResponse findUserByEmail(String email) {
    UserPortResponse userPortResponse = fetchUserPort.findByEmail(email).orElseThrow(
        () -> new RuntimeException("사용자를 찾을 수 없음 : " + email)
    );

    return
        UserResponse.builder()
            .userName(userPortResponse.getUserName())
            .password(userPortResponse.getPassword())
            .email(userPortResponse.getEmail())
            .phone(userPortResponse.getPhone())
            .userType(userPortResponse.getUserType())
            .isActive(userPortResponse.isActive())
            .build();

  }
}
