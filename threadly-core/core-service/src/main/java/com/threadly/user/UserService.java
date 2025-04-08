package com.threadly.user;

import com.threadly.exception.authentication.UserAuthErrorType;
import com.threadly.exception.authentication.UserAuthenticationException;
import com.threadly.user.command.UserRegistrationCommand;
import com.threadly.user.response.UserPortResponse;
import com.threadly.user.response.UserRegistrationResponse;
import com.threadly.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase, FetchUserUseCase {

  private final InsertUserPort insertUserPort;
  private final FetchUserPort fetchUserPort;


  @Override
  public UserRegistrationResponse register(UserRegistrationCommand command) {
    UserPortResponse userPortResponse = insertUserPort.create(
        new CreateUser(
            command.getEmail(),
            command.getUserName(),
            command.getPassword(),
            command.getPhone()
        )
    ).get();

    return UserRegistrationResponse.builder()
        .userId(userPortResponse.getUserId())
        .userName(userPortResponse.getUserName())
        .email(userPortResponse.getEmail())
        .userType(userPortResponse.getUserType())
        .build();
  }

  @Override
  public UserResponse findUserByEmail(String email) {
    UserPortResponse userPortResponse = fetchUserPort.findByEmail(email).orElseThrow(
        () -> new UserAuthenticationException(UserAuthErrorType.NOT_FOUND)
    );

    return
        UserResponse.builder()
            .userId(userPortResponse.getUserId())
            .userName(userPortResponse.getUserName())
            .password(userPortResponse.getPassword())
            .email(userPortResponse.getEmail())
            .phone(userPortResponse.getPhone())
            .userType(userPortResponse.getUserType())
            .isActive(userPortResponse.isActive())
            .build();

  }

  @Override
  public UserResponse findUserByUserId(String userId) {
    UserPortResponse response = fetchUserPort.findByUserId(userId).orElseThrow(
        () -> new RuntimeException("userId 조회 실패 : " + userId)
    );

    return
        UserResponse.builder()
            .userId(response.getUserId())
            .userName(response.getUserName())
            .password(response.getPassword())
            .email(response.getEmail())
            .phone(response.getPhone())
            .userType(response.getUserType())
            .isActive(response.isActive())
            .build();
  }

}