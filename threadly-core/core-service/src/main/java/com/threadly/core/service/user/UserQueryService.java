package com.threadly.core.service.user;

import com.threadly.core.domain.user.User;
import com.threadly.core.port.user.in.query.UserQueryUseCase;
import com.threadly.core.port.user.in.shared.UserResponse;
import com.threadly.core.service.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueryService implements UserQueryUseCase {

  private final UserValidator userValidator;

  @Override
  public UserResponse findUserByEmail(String email) {
    User user = userValidator.getUserByEmailOrThrow(email);
    return
        UserResponse.builder()
            .userId(user.getUserId())
            .userName(user.getUserName())
            .password(user.getPassword())
            .email(user.getEmail())
            .phone(user.getPhone())
            .userRoleType(user.getUserRoleType())
            .userStatus(user.getUserStatus())
            .isEmailVerified(user.isEmailVerified())
            .type(user.getUserRoleType())
            .build();
  }

  @Override
  public UserResponse findUserByUserId(String userId) {
    User user = userValidator.getUserByIdOrElseThrow(userId);
    return
        UserResponse.builder()
            .userId(user.getUserId())
            .userName(user.getUserName())
            .password(user.getPassword())
            .email(user.getEmail())
            .phone(user.getPhone())
            .userRoleType(user.getUserRoleType())
            .userStatus(user.getUserStatus())
            .build();
  }
}