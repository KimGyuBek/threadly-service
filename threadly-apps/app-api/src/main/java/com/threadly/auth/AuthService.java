package com.threadly.auth;

import com.threadly.controller.auth.request.UserLoginRequest;
import com.threadly.exception.authentication.UserAuthErrorType;
import com.threadly.exception.authentication.UserAuthenticationException;
import com.threadly.token.response.TokenResponse;
import com.threadly.user.FetchUserUseCase;
import com.threadly.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
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
public class AuthService {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  private final FetchUserUseCase fetchUserUseCase;

  private final JwtTokenProvider jwtTokenProvider;

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

      String userId = findUser.getUserId();

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

      return tokenResponse;

      /*email로 사용자를 찾을 수 없는 경우*/
      /*TODO UserException으로 변경*/
    } catch (UserAuthenticationException e) {
      throw new UserAuthenticationException(UserAuthErrorType.NOT_FOUND);

      /*UserNameNotFound*/
    } catch (UsernameNotFoundException e) {
      throw new UserAuthenticationException(UserAuthErrorType.NOT_FOUND);

      /*BadCredential*/
    } catch (BadCredentialsException e) {
      throw new UserAuthenticationException(UserAuthErrorType.INVALID_PASSWORD);

      /*Disabled*/
    } catch (DisabledException e) {
      throw new UserAuthenticationException(UserAuthErrorType.ACCOUNT_DISABLED);

      /*Locked*/
    } catch (LockedException e) {
      throw new UserAuthenticationException(UserAuthErrorType.ACCOUNT_LOCKED);

      /*나머지*/
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new UserAuthenticationException(UserAuthErrorType.AUTHENTICATION_ERROR);
    }

  }
}
