package com.threadly.user;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.user.register.RegisterUserCommand;
import com.threadly.user.register.RegisterUserUseCase;
import com.threadly.user.register.UserRegistrationApiResponse;
import com.threadly.user.response.UserPortResponse;
import com.threadly.user.update.UpdateUserUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandService implements RegisterUserUseCase, UpdateUserUseCase {

  private final SaveUserPort saveUserPort;
  private final FetchUserPort fetchUserPort;


  @Transactional
  @Override
  public UserRegistrationApiResponse register(RegisterUserCommand command) {

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

    UserPortResponse userPortResponse = saveUserPort.save(user);

    log.info("회원 가입 성공");

    return UserRegistrationApiResponse.builder()
        .userId(userPortResponse.getUserId())
        .userName(userPortResponse.getUserName())
        .userType(userPortResponse.getUserType())
        .email(userPortResponse.getEmail())
        .isActive(userPortResponse.isActive())
        .isEmailVerified(userPortResponse.isEmailVerified())
        .build();
  }
}