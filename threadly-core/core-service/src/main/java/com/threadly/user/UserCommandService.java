package com.threadly.user;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.properties.TtlProperties;
import com.threadly.security.JwtTokenProvider;
import com.threadly.token.DeleteTokenPort;
import com.threadly.token.InsertBlackListToken;
import com.threadly.token.InsertTokenPort;
import com.threadly.user.register.RegisterUserCommand;
import com.threadly.user.register.RegisterUserUseCase;
import com.threadly.user.register.UserRegistrationApiResponse;
import com.threadly.user.response.UserPortResponse;
import com.threadly.user.update.UpdateUserUseCase;
import com.threadly.user.withdraw.WithdrawUserUseCase;
import com.threadly.utils.JwtTokenUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandService implements RegisterUserUseCase, UpdateUserUseCase,
    WithdrawUserUseCase {

  private final SaveUserPort saveUserPort;
  private final FetchUserPort fetchUserPort;
  private final UpdateUserPort updateUserPort;

  private final InsertTokenPort insertTokenPort;
  private final DeleteTokenPort deleteTokenPort;

  private final JwtTokenProvider jwtTokenProvider;

  private final TtlProperties ttlProperties;

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
        .userStatusType(userPortResponse.getUserStatusType())
        .isEmailVerified(userPortResponse.isEmailVerified())
        .build();
  }

  @Transactional
  @Override
  public void withdrawUser(String userId, String bearerToken) {
    /*토큰 추출*/
    String accessToken = JwtTokenUtils.extractAccessToken(bearerToken);

    /*user 조회*/
    User user = fetchUserPort.findByUserId(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /*accessToken 추출*/
    accessToken = accessToken.substring(7);

    /* userStatusType 변경*/
    user.markAsDeleted();

    updateUserPort.updateUserStatus(userId, user.getUserStatusType());

    /*블랙리스트 토큰 등록*/
    insertTokenPort.saveBlackListToken(InsertBlackListToken.builder()
        .accessToken(accessToken)
        .userId(userId)
        .duration(ttlProperties.getBlacklistToken())
        .build());

    /*refreshToken 삭제*/
    deleteTokenPort.deleteRefreshToken(userId);
    log.info("회원 탈퇴 성공");
  }
}