package com.threadly.core.service.user;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.user.CannotInactiveException;
import com.threadly.core.domain.user.User;
import com.threadly.core.port.mail.in.SendMailCommand;
import com.threadly.core.port.user.in.account.command.UserAccountCommandUseCase;
import com.threadly.core.port.user.in.account.command.dto.ChangePasswordCommand;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserApiResponse;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserCommand;
import com.threadly.core.port.user.in.account.command.dto.UpdateMyPrivacySettingCommand;
import com.threadly.core.port.user.out.UserCommandPort;
import com.threadly.core.port.user.out.UserQueryPort;
import com.threadly.core.port.user.out.UserResult;
import com.threadly.core.service.processor.TokenProcessor;
import com.threadly.core.service.validator.user.UserValidator;
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

  private final UserValidator userValidator;

  private final UserQueryPort userQueryPort;
  private final UserCommandPort userCommandPort;

  private final TokenProcessor tokenProcessor;

  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional
  @Override
  public RegisterUserApiResponse register(RegisterUserCommand command) {
    /*email 중복 검증*/
    userValidator.validateEmailDuplicate(command.getEmail());

    /*사용자 생성*/
    User user = User.newUser(
        command.getUserName(),
        command.getPassword(),
        command.getEmail(),
        command.getPhone()
    );

    /*저장*/
    UserResult userResult = userCommandPort.save(user);

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
        .userId(userResult.getUserId())
        .userName(userResult.getUserName())
        .userRoleType(userResult.getUserRoleType())
        .email(userResult.getEmail())
        .userStatus(userResult.getUserStatus())
        .isEmailVerified(userResult.isEmailVerified())
        .build();
  }

  @Transactional
  @Override
  public void withdrawMyAccount(String userId, String bearerToken) {
    /*user 조회*/
    User user = userValidator.getUserByIdOrElseThrow(userId);

    /* userStatusType 변경*/
    user.markAsDeleted();
    userCommandPort.updateUserStatus(userId, user.getUserStatus());

    /*토큰 무효화 처리*/
    tokenProcessor.addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);

    log.info("계정 탈퇴 성공");
  }

  @Transactional
  @Override
  public void deactivateMyAccount(String userId, String bearerToken) {
    /*user 조회*/
    User user = userValidator.getUserByIdOrElseThrow(userId);

    /*user 비활성화 처리*/
    try {
      user.markAsInactive();
    } catch (CannotInactiveException e) {
      throw new UserException(ErrorCode.USER_ALREADY_DELETED);
    }

    userCommandPort.updateUserStatus(userId, user.getUserStatus());

    /*토큰 무효화 처리*/
    tokenProcessor.addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);

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
}