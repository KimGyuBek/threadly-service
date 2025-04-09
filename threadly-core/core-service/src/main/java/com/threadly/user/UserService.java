package com.threadly.user;

import com.threadly.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.user.command.UserRegistrationCommand;
import com.threadly.user.response.UserPortResponse;
import com.threadly.user.response.UserRegistrationResponse;
import com.threadly.user.response.UserResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase, FetchUserUseCase, UpdateUserUseCase {

  private final InsertUserPort insertUserPort;
  private final FetchUserPort fetchUserPort;


  @Transactional
  @Override
  public UserRegistrationResponse register(UserRegistrationCommand command) {

    /*email로 사용자 조회*/
    Optional<UserPortResponse> byEmail = fetchUserPort.findByEmail(command.getEmail());

    /*이미 존재하는 사용자면*/
    if (byEmail.isPresent()) {
      throw new UserException(ErrorCode.USER_ALREADY_EXISTS);
    }

    /*사용자 생성*/
    User user = User.newUser(
        command.getUserName(),
        command.getPassword(),
        command.getEmail(),
        command.getPhone()
    );

    UserPortResponse userPortResponse = insertUserPort.create(user);

    return UserRegistrationResponse.builder()
        .userId(userPortResponse.getUserId())
        .userName(userPortResponse.getUserName())
        .userType(userPortResponse.getUserType())
        .email(userPortResponse.getEmail())
        .isActive(userPortResponse.isActive())
        .isEmailVerified(userPortResponse.isEmailVerified())
        .build();
  }

  @Override
  public UserResponse findUserByEmail(String email) {
    UserPortResponse userPortResponse = fetchUserPort.findByEmail(email).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
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
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
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

  @Override
  public boolean validateEmail(String code) {
    return false;
  }
}