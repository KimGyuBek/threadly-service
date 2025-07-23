package com.threadly.user;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.user.get.GetUserUseCase;
import com.threadly.user.get.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueryService implements GetUserUseCase {

  private final FetchUserPort fetchUserPort;

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
            .userType(user.getUserType())
            .isActive(user.isActive())
            .isEmailVerified(user.isEmailVerified())
            .type(user.getUserType())
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
            .userType(user.getUserType())
            .isActive(user.isActive())
            .build();
  }

  @Override
  public boolean isUserProfileExists(String userId) {
    return fetchUserPort.existsUserProfileByUserId(userId);
  }
}