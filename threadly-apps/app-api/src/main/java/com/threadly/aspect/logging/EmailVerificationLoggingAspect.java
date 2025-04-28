package com.threadly.aspect.logging;

import static com.threadly.util.LogFormatUtils.debugLog;
import static com.threadly.util.LogFormatUtils.logFailure;
import static com.threadly.util.LogFormatUtils.logSuccess;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * EmailVerificationService logging Aspect
 */
@Component
@Aspect
@Slf4j
public class EmailVerificationLoggingAspect {


  /*
   * verificationEmail()
   * */
  @Pointcut("execution(* com.threadly.verification.EmailVerificationService.verificationEmail(..))")
  public void verificationEmail() {
  }

  /*성공*/
  @AfterReturning(pointcut = "verificationEmail()")
  public void logVerificationEmailSuccess(JoinPoint joinPoint) {
    debugLog(joinPoint, "인증 메일 검증 성공");
    logSuccess(joinPoint);
  }

  /*실패*/
  @AfterThrowing(pointcut = "verificationEmail()", throwing = "exception")
  public void logVerificationEmailFailed(JoinPoint joinPoint, Exception exception) {
    debugLog(joinPoint, "인증 메일 검증 실패");
    logFailure(joinPoint, exception);

  }

  /*
  * sendVerificationEmail()
   */
  @Pointcut("execution(* com.threadly.verification.EmailVerificationService.sendVerificationEmail(..))")
  public void sendVerificationEmail() {
  }

  /*성공*/
  @AfterReturning(pointcut = "sendVerificationEmail()")
  public void logSendVerificationEmail(JoinPoint joinPoint) {
    debugLog(joinPoint, "인증 메일 전송 성공");
    logSuccess(joinPoint);
  }

  /*실패*/
  @AfterThrowing(pointcut = "sendVerificationEmail()", throwing = "exception")
  public void logSendVerificationEmailFailed(JoinPoint joinPoint, Exception exception) {
    debugLog(joinPoint, "인증 메일 전송 실패");
    logFailure(joinPoint, exception);

  }

}
