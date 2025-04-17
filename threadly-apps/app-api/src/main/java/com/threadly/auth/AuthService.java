package com.threadly.auth;

import com.threadly.ErrorCode;
import com.threadly.auth.token.response.TokenResponse;
import com.threadly.auth.verification.PasswordVerificationUseCase;
import com.threadly.auth.verification.response.PasswordVerificationToken;
import com.threadly.controller.auth.request.UserLoginRequest;
import com.threadly.exception.authentication.UserAuthenticationException;
import com.threadly.exception.user.UserException;
import com.threadly.user.FetchUserUseCase;
import com.threadly.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements PasswordVerificationUseCase {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  private final FetchUserUseCase fetchUserUseCase;

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public PasswordVerificationToken getPasswordVerificationToken(String userId, String password) {

    try {
      /*인증용 토큰 생성*/
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          userId, password);

      /*인증 시도*/
      Authentication authenticate = authenticationManagerBuilder.getObject()
          .authenticate(authenticationToken);

      /*토큰 생성*/
      String tokenResponse = jwtTokenProvider.generateToken(userId);

      /*SecurityContextHolder에 인증 정보 저장*/
      SecurityContextHolder.getContext().setAuthentication(authenticate);
      log.info("이중인증 성공");

      return new PasswordVerificationToken(tokenResponse);

      /*UserNameNotFound*/
      /*BadCredential*/
    } catch (UserException | UsernameNotFoundException | BadCredentialsException e) {
      throw new UserAuthenticationException(ErrorCode.USER_AUTHENTICATION_FAILED);

      /*Disabled*/
    } catch (DisabledException e) {
      throw new UserAuthenticationException(ErrorCode.INVALID_USER_STATUS);

      /*Locked*/
    } catch (LockedException e) {
      throw new UserAuthenticationException(ErrorCode.ACCOUNT_LOCKED);

      /*나머지*/
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new UserAuthenticationException(ErrorCode.AUTHENTICATION_ERROR);
    }
  }

  /**
   * login
   *
   * @param request
   * @return
   */
  public TokenResponse login(UserLoginRequest request) {

    String email = request.getEmail();
    String password = request.getPassword();

    try {
      /*사용자 조회*/
      UserResponse findUser = fetchUserUseCase.findUserByEmail(email);

      /*email 인증이 되어있는지 검증*/
      if (!findUser.isEmailVerified()) {
        throw new UserAuthenticationException(ErrorCode.EMAIL_NOT_VERIFIED);
      }

      String userId = findUser.getUserId();

      /*TODO user 상태 검증*/

      /*인증용 토큰 생성*/
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          userId, password);

      /*인증 시도*/
      Authentication authenticate = authenticationManagerBuilder.getObject()
          .authenticate(authenticationToken);

      /*토큰 생성*/
      TokenResponse tokenResponse = jwtTokenProvider.upsertToken(userId);

      /*SecurityContextHolder에 인증 정보 저장*/
      SecurityContextHolder.getContext().setAuthentication(authenticate);
      log.info("로그인 성공");

      return tokenResponse;

      /*email 인증 실패*/
    } catch (UserAuthenticationException e) {
      log.info("이메일 인증 실패");
      log.error(e.getMessage());
      throw e;

      /*email로 사용자를 찾을 수 없는 경우*/
      /*UserNameNotFound*/
      /*BadCredential*/
    } catch (UserException | UsernameNotFoundException | BadCredentialsException e) {
      log.info(e.getMessage());
      throw new UserAuthenticationException(ErrorCode.USER_AUTHENTICATION_FAILED);

      /*Disabled*/
    } catch (DisabledException e) {
      throw new UserAuthenticationException(ErrorCode.INVALID_USER_STATUS);

      /*Locked*/
    } catch (LockedException e) {
      throw new UserAuthenticationException(ErrorCode.ACCOUNT_LOCKED);

      /*나머지*/
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new UserAuthenticationException(ErrorCode.AUTHENTICATION_ERROR);
    }

  }

}
