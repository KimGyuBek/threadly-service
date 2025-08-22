package com.threadly.core.service.user;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.commons.properties.TtlProperties;
import com.threadly.core.port.token.DeleteTokenPort;
import com.threadly.core.port.token.InsertBlackListToken;
import com.threadly.core.port.token.InsertTokenPort;
import com.threadly.core.usecase.user.account.command.dto.ChangePasswordCommand;
import com.threadly.core.usecase.user.account.command.ChangePasswordUseCase;
import com.threadly.core.usecase.user.account.command.DeactivateMyAccountUseCase;
import com.threadly.core.usecase.user.account.command.WithdrawMyAccountUseCase;
import com.threadly.core.usecase.user.profile.command.dto.UpdateMyPrivacySettingCommand;
import com.threadly.core.usecase.user.profile.command.UpdateMyPrivacySettingUseCase;
import com.threadly.core.usecase.user.account.command.dto.RegisterUserApiResponse;
import com.threadly.core.usecase.user.account.command.dto.RegisterUserCommand;
import com.threadly.core.usecase.user.account.command.RegisterUserUseCase;
import com.threadly.core.port.user.FetchUserPort;
import com.threadly.core.port.user.SaveUserPort;
import com.threadly.core.port.user.UpdateUserPort;
import com.threadly.core.port.user.response.UserPortResponse;
import com.threadly.core.usecase.user.account.command.UpdateUserUseCase;
import com.threadly.commons.utils.JwtTokenUtils;
import com.threadly.core.domain.user.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandService implements RegisterUserUseCase, UpdateUserUseCase,
    WithdrawMyAccountUseCase, DeactivateMyAccountUseCase, ChangePasswordUseCase,
    UpdateMyPrivacySettingUseCase {

  private final SaveUserPort saveUserPort;
  private final FetchUserPort fetchUserPort;
  private final UpdateUserPort updateUserPort;

  private final InsertTokenPort insertTokenPort;
  private final DeleteTokenPort deleteTokenPort;

  private final TtlProperties ttlProperties;

  @Transactional
  @Override
  public RegisterUserApiResponse register(RegisterUserCommand command) {

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

    return RegisterUserApiResponse.builder()
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
  public void withdrawMyAccount(String userId, String bearerToken) {
    /*user 조회*/
    User user = fetchUserPort.findByUserId(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /* userStatusType 변경*/
    user.markAsDeleted();

    updateUserPort.updateUserStatus(userId, user.getUserStatusType());

    /*토큰 무효화 처리*/
    addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);

    log.info("계정 탈퇴 성공");
  }

  @Transactional
  @Override
  public void deactivateMyAccount(String userId, String bearerToken) {
    /*user 조회*/
    User user = fetchUserPort.findByUserId(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /*비활성화 처리*/
    user.markAsInactive();
    updateUserPort.updateUserStatus(userId, user.getUserStatusType());

    /*토큰 무효화 처리*/
    addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);

    log.info("계정 비활성화 성공");
  }


  @Transactional
  @Override
  public void changePassword(ChangePasswordCommand command) {
    updateUserPort.changePassword(command.getUserId(), command.getNewPassword());
  }

  @Transactional
  @Override
  public void updatePrivacy(UpdateMyPrivacySettingCommand command) {

    /*계정 공개 여부 설정*/
    User user = User.emptyWithUserId(command.getUserId());
    if (command.isPrivate()) {
      user.markAsPrivate();
    } else {
      user.markAsPublic();
    }

    updateUserPort.updatePrivacy(user);
  }

  /**
   * 주어진 bearerToken으로 accessToken 블랙리스트 등록 및 refreshToken 삭제
   *
   * @param userId
   * @param bearerToken
   */
  private void addBlackListTokenAndDeleteRefreshToken(String userId, String bearerToken) {
    /*토큰 추출*/
    String accessToken = JwtTokenUtils.extractAccessToken(bearerToken);
    /*블랙리스트 토큰 등록*/
    insertTokenPort.saveBlackListToken(InsertBlackListToken.builder()
        .accessToken(accessToken)
        .userId(userId)
        .duration(ttlProperties.getBlacklistToken())
        .build());

    /*refreshToken 삭제*/
    deleteTokenPort.deleteRefreshToken(userId);
  }
}