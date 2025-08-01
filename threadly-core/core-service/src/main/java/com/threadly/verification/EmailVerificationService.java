package com.threadly.verification;

import com.threadly.auth.verification.EmailVerificationUseCase;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.mail.EmailVerificationException;
import com.threadly.exception.user.UserException;
import com.threadly.mail.SendMailPort;
import com.threadly.properties.TtlProperties;
import com.threadly.user.FetchUserPort;
import com.threadly.user.UpdateUserPort;
import com.threadly.user.User;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Email 인증 관련 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService implements EmailVerificationUseCase {

  private final com.threadly.verification.EmailVerificationPort emailVerificationPort;
  private final SendMailPort sendMailPort;
  private final UpdateUserPort updateUserPort;
  private final FetchUserPort fetchUserPort;

  private final TtlProperties ttlProperties;


  @Transactional
  @Override
  public void verificationEmail(String code) {
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
    sendMailPort.sendVerificationCompleteMail(userId, user.getUserName());
  }

  @Override
  public void sendVerificationEmail(String userId) {

    /*인증 코드 생성*/
    String code = UUID.randomUUID().toString().substring(0, 6);

    log.debug("인증 코드 : {}", code);

    /*인증 코드 저장*/
    emailVerificationPort.saveCode(userId, code, ttlProperties.getAccessToken());

    /*메일 전송*/
    sendMailPort.sendVerificationMail(userId, code);
  }
}
