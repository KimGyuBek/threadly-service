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
  private final UpdateUserPort updateUserPort;


  @Transactional
  @Override
  public UserRegistrationResponse register(UserRegistrationCommand command) {

    /*email로 사용자 조회*/
    Optional<User> byEmail = fetchUserPort.findByEmail(command.getEmail());

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

    /*email 인증 코드 생성 및 메일 전송*/


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
    User user = fetchUserPort.findByEmail(email).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
    );

    return
        UserResponse.builder()
            .userId(user.getUserId())
            .userName(user.getUserName())
            .password(user.getPassword())
            .email(user.getEmail())
            .phone(user.getPhone())
            .userType(user.getUserType().name())
            .isActive(user.isActive())
            .build();

  }

  @Override
  public UserResponse findUserByUserId(String userId) {

    /*userId로 user 조회*/
    User user = fetchUserPort.findByUserId(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    return
        UserResponse.builder()
            .userId(user.getUserId())
            .userName(user.getUserName())
            .password(user.getPassword())
            .email(user.getEmail())
            .phone(user.getPhone())
            .userType(user.getUserType().name())
            .isActive(user.isActive())
            .build();
  }

  @Transactional
  @Override
  public void verifyEmail(String userId) {

    /*userId로 사용자 조회*/
    User user = fetchUserPort.findByUserId(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));


    /*email 인증 여부 true*/
    user.verifyEmail();

    /*TODO 굳이 user를 다 넘겨줘야할까*/
    updateUserPort.updateEmailVerification(user);

  }
}