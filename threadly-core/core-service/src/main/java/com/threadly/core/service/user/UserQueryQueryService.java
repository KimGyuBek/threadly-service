package com.threadly.core.service.user;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.port.user.in.query.UserQueryUseCase;
import com.threadly.core.port.user.in.shared.UserResponse;
import com.threadly.core.domain.user.User;
import com.threadly.core.port.user.out.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueryQueryService implements UserQueryUseCase {

  private final UserQueryPort userQueryPort;

  @Override
  public UserResponse findUserByEmail(String email) {
    User user = userQueryPort.findByEmail(email).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
    );

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

    /*userId로 user 조회*/
    User user = userQueryPort.findByUserId(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

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