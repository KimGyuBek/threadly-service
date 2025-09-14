package com.threadly.core.service.verification;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.mail.EmailVerificationException;
import com.threadly.commons.exception.user.UserException;
import com.threadly.core.domain.mail.MailType;
import com.threadly.core.domain.user.User;
import com.threadly.core.port.user.out.FetchUserPort;
import com.threadly.core.port.user.out.UpdateUserPort;
import com.threadly.core.port.verification.EmailVerificationPort;
import com.threadly.core.port.auth.in.verification.EmailVerificationUseCase;
import com.threadly.core.port.mail.in.SendMailCommand;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Email 인증 관련 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService implements EmailVerificationUseCase {

  private final EmailVerificationPort emailVerificationPort;
  private final UpdateUserPort updateUserPort;
  private final FetchUserPort fetchUserPort;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public void verifyEmail(String code) {
    /*code로 userId 조회*/
    String userId = emailVerificationPort.getUserId(code);

    /*인증되지 않은 사용자인지 찾아서 검증 */
    Optional<User> findByUserId = fetchUserPort.findByUserId(userId);

    User user = findByUserId.orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
    );
    /*이미 인증이 되어 있다면*/
    if (user.isEmailVerified()) {
      throw new EmailVerificationException(ErrorCode.EMAIL_ALREADY_VERIFIED);
    }

    /*인증 처리*/
    user.verifyEmail();

    /*db 업데이트*/
    updateUserPort.updateEmailVerification(user.getUserId(), user.isEmailVerified());

    /*redis에서 코드 삭제*/
    emailVerificationPort.deleteCode(code);

    /*가입 환영 메일 전송*/
    eventPublisher.publishEvent(
        new SendMailCommand(
            user.getUserId(),
            user.getEmail(),
            user.getUserName(),
            MailType.WELCOME
        )
    );
  }
}
