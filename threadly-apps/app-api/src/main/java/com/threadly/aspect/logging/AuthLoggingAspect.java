package com.threadly.aspect.logging;

import static com.threadly.utils.LogFormatUtils.debugLog;
import static com.threadly.utils.LogFormatUtils.logFailure;
import static com.threadly.utils.LogFormatUtils.logSuccess;

import com.threadly.exception.ErrorCode;
import com.threadly.global.exception.UserAuthenticationException;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.auth.token.response.TokenReissueResponse;
import com.threadly.auth.verification.response.PasswordVerificationToken;
import com.threadly.exception.user.UserException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Auth Service logging Aspect
 */
@Component
@Aspect
@Slf4j
public class AuthLoggingAspect {


  /*
   * login()
   * */
  @Pointcut("execution(* com.threadly.auth.AuthManager.login(..))")
  public void login() {
  }

  /*성공*/
  @AfterReturning(pointcut = "login()", returning = "result")
  public void logLoginSuccess(JoinPoint joinPoint, LoginTokenResponse result) {
    debugLog(joinPoint, "로그인 성공");
    logSuccess(joinPoint);
  }

  /*실패*/
  @AfterThrowing(pointcut = "login()", throwing = "exception")
  public void logLoginFailed(JoinPoint joinPoint, Exception exception) {
    debugLog(joinPoint, "로그인 실패");

    if (exception instanceof UserAuthenticationException) {
      log.error(exception.getMessage(), exception);
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(
          ((UserAuthenticationException) exception).getErrorCode());

    } else if (exception instanceof UserException) {
      log.error(exception.getMessage(), exception);
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(ErrorCode.USER_NOT_FOUND);

    } else if (exception instanceof UsernameNotFoundException
        || exception instanceof BadCredentialsException) {
      log.error(exception.getMessage(), exception);
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(ErrorCode.USER_AUTHENTICATION_FAILED);

    } else if (exception instanceof DisabledException) {
      log.error(exception.getMessage(), exception);
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(ErrorCode.ACCOUNT_DISABLED);

    } else if (exception instanceof LockedException) {
      log.error(exception.getMessage(), exception);
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(ErrorCode.ACCOUNT_LOCKED);
    } else {
      log.error(exception.getMessage(), exception);
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(ErrorCode.AUTHENTICATION_ERROR);
    }
  }

  /*
   * */
  @Pointcut("execution(* com.threadly.auth.AuthManager.logout(..))")
  public void logout() {
  }

  /*성공*/
  @AfterReturning(pointcut = "logout()")
  public void logLogOutSuccess(JoinPoint joinPoint) {
    debugLog(joinPoint, "로그아웃 성공");
    logSuccess(joinPoint);
  }

  /*실패*/
  @AfterThrowing(pointcut = "logout()", throwing = "exception")
  public void logLogOutFailed(JoinPoint joinPoint, Exception exception) {
    debugLog(joinPoint, "로그아웃 실패");
    logFailure(joinPoint, exception);
  }


  /*
   * getPasswordVerificationToken()
   */
  @Pointcut("execution(* com.threadly.auth.AuthManager.getPasswordVerificationToken(..))")
  public void getPasswordVerification() {
  }

  /*성공*/
  @AfterReturning(pointcut = "getPasswordVerification()", returning = "result")
  public void logGetPasswordVerificationTokenSuccess(JoinPoint joinPoint,
      PasswordVerificationToken result) {
    debugLog(joinPoint, "이중인증 토큰 생성 성공");
    logSuccess(joinPoint);
  }

  /*실패*/
  @AfterThrowing(pointcut = "getPasswordVerification()", throwing = "exception")
  public void logGetPasswordVerificationTokenFailed(JoinPoint joinPoint, Exception exception) {
    debugLog(joinPoint, "이중인증 실패");

    /*UserNameNotFound*/
    /*BadCredential*/
    if (exception instanceof UserException || exception instanceof UsernameNotFoundException
        || exception instanceof BadCredentialsException e) {
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(ErrorCode.USER_AUTHENTICATION_FAILED);

    } else if (exception instanceof DisabledException) {
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(ErrorCode.INVALID_USER_STATUS);

      /*Locked*/
    } else if (exception instanceof LockedException) {
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(ErrorCode.ACCOUNT_LOCKED);

      /*나머지*/
    } else {
      logFailure(joinPoint, exception);
      throw new UserAuthenticationException(ErrorCode.AUTHENTICATION_ERROR);
    }

  }

  /*
   * reissueLoginToken()
   */
  @Pointcut("execution(* com.threadly.auth.AuthManager.reissueLoginToken(..))")
  public void reissueLoginToken() {
  }

  /*성공*/
  @AfterReturning(pointcut = "reissueLoginToken()", returning = "result")
  public void logReissueLoginTokenSuccess(JoinPoint joinPoint,
      TokenReissueResponse result) {
    debugLog(joinPoint, "로그인 토큰 재발급 성공");
    logSuccess(joinPoint);
  }

  /*실패*/
  @AfterThrowing(pointcut = "reissueLoginToken()", throwing = "exception")
  public void logReissueLoginTokenFailed(JoinPoint joinPoint, Exception exception) {
    debugLog(joinPoint, "로그인 토큰 재발급 실패");
    logFailure(joinPoint, exception);
  }
}
