package com.threadly.core.service.user;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.commons.properties.TtlProperties;
import com.threadly.commons.utils.JwtTokenUtils;
import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.user.User;
import com.threadly.core.port.mail.in.SendMailCommand;
import com.threadly.core.port.token.out.command.TokenCommandPort;
import com.threadly.core.port.token.out.command.dto.InsertBlackListTokenCommand;
import com.threadly.core.port.user.in.account.command.UserAccountCommandUseCase;
import com.threadly.core.port.user.in.account.command.dto.ChangePasswordCommand;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserApiResponse;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserCommand;
import com.threadly.core.port.user.in.account.command.dto.UpdateMyPrivacySettingCommand;
import com.threadly.core.port.user.out.UserQueryPort;
import com.threadly.core.port.user.out.UserCommandPort;
import com.threadly.core.port.user.out.response.UserPortResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandService implements
    UserAccountCommandUseCase {

  private final UserQueryPort userQueryPort;
  private final UserCommandPort userCommandPort;

  private final TokenCommandPort tokenCommandPort;

  private final ApplicationEventPublisher applicationEventPublisher;

  private final TtlProperties ttlProperties;

  @Transactional
  @Override
  public RegisterUserApiResponse register(RegisterUserCommand command) {

    /*email로 사용자 조회*/
    Optional<User> byEmail = userQueryPort.findByEmail(command.getEmail());

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

    UserPortResponse userPortResponse = userCommandPort.save(user);

    log.info("회원 가입 성공");

    /*인증 메일 전송 이벤트 발행*/
    applicationEventPublisher.publishEvent(
        new SendMailCommand(
            user.getUserId(),
            user.getEmail(),
            user.getUserName(),
            MailType.VERIFICATION
        )
    );

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
    User user = userQueryPort.findByUserId(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /* userStatusType 변경*/
    user.markAsDeleted();

    userCommandPort.updateUserStatus(userId, user.getUserStatusType());

    /*토큰 무효화 처리*/
    addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);

    log.info("계정 탈퇴 성공");
  }

  @Transactional
  @Override
  public void deactivateMyAccount(String userId, String bearerToken) {
    /*user 조회*/
    User user = userQueryPort.findByUserId(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /*비활성화 처리*/
    user.markAsInactive();
    userCommandPort.updateUserStatus(userId, user.getUserStatusType());

    /*토큰 무효화 처리*/
    addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);

    log.info("계정 비활성화 성공");
  }


  @Transactional
  @Override
  public void changePassword(ChangePasswordCommand command) {
    userCommandPort.changePassword(command.getUserId(), command.getNewPassword());
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

    userCommandPort.updatePrivacy(user);
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
    tokenCommandPort.saveBlackListToken(InsertBlackListTokenCommand.builder()
        .accessToken(accessToken)
        .userId(userId)
        .duration(ttlProperties.getBlacklistToken())
        .build());

    /*refreshToken 삭제*/
    tokenCommandPort.deleteRefreshToken(userId);
  }
}