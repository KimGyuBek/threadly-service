package com.threadly.auth;

import com.threadly.ErrorCode;
import com.threadly.auth.exception.UserAuthenticationException;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.auth.token.response.TokenReissueResponse;
import com.threadly.auth.verification.LoginUserUseCase;
import com.threadly.auth.verification.PasswordVerificationUseCase;
import com.threadly.auth.verification.response.PasswordVerificationToken;
import com.threadly.exception.token.TokenException;
import com.threadly.exception.user.UserException;
import com.threadly.properties.TtlProperties;
import com.threadly.repository.token.TokenRepository;
import com.threadly.token.FetchTokenPort;
import com.threadly.token.InsertTokenPort;
import com.threadly.token.UpsertRefreshToken;
import com.threadly.token.UpsertToken;
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

/*TODO 이름 변경*/
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements LoginUserUseCase, PasswordVerificationUseCase {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  private final FetchUserUseCase fetchUserUseCase;

  private final FetchTokenPort fetchTokenPort;
  private final InsertTokenPort insertTokenPort;
  private final UpsertToken upsertTokenPort;

  private final JwtTokenProvider jwtTokenProvider;
  private final TtlProperties ttlProperties;
  private final TokenRepository tokenRepository;

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
      String tokenResponse = jwtTokenProvider.generateToken(userId,
          ttlProperties.getPasswordVerification());

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
   * @return
   */
  @Override
  public LoginTokenResponse login(String email, String password) {

    try {
      /*사용자 조회*/
      UserResponse findUser = fetchUserUseCase.findUserByEmail(email);


      String userId = findUser.getUserId();

      /*TODO user 상태 검증*/

      /*인증용 토큰 생성*/
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          userId, password);

      /*인증 시도*/
      Authentication authenticate = authenticationManagerBuilder.getObject()
          .authenticate(authenticationToken);

      /*email 인증이 되어있는지 검증*/
      if (!findUser.isEmailVerified()) {
        throw new UserAuthenticationException(ErrorCode.EMAIL_NOT_VERIFIED);
      }

      /*토큰 생성*/
      LoginTokenResponse tokenResponse = jwtTokenProvider.generateLoginToken(userId);

      /*토큰 저장*/
      upsertTokenPort.upsertRefreshToken(UpsertRefreshToken.builder()
          .userId(userId)
          .refreshToken(tokenResponse.getRefreshToken())
          .duration(ttlProperties.getRefreshToken())
          .build());


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

  /**
   * login token 재발급
   *
   * @return
   */
  public TokenReissueResponse reissueLoginToken(String refreshToken) {
    try {
      /*refreshToken이 null일 경우*/
      if (refreshToken == null) {
        throw new TokenException(ErrorCode.TOKEN_MISSING);
      }

      /*refrehToken으로 userId 조회*/
      String userId = jwtTokenProvider.getUserId(refreshToken);

      /*refreshToken이 저장되어 있는지 검증*/
      if(!fetchTokenPort.existsRefreshTokenByUserId(userId)) {
        throw new TokenException(ErrorCode.TOKEN_MISSING);
      }

      /*refreshToken 검증*/
      jwtTokenProvider.validateToken(refreshToken);

      /*login Token 재생성*/
      LoginTokenResponse loginTokenResponse = jwtTokenProvider.generateLoginToken(userId);

      log.info("로그인 토큰 재발급됨");


      /*기존 토큰 덮어쓰기*/
      upsertTokenPort.upsertRefreshToken(
          UpsertRefreshToken.builder()
              .userId(userId)
              .refreshToken(loginTokenResponse.getRefreshToken())
              .duration(ttlProperties.getRefreshToken())
              .build()
      );

      log.debug("기존 토큰 대치 성공");

      return TokenReissueResponse.builder()
          .accessToken(loginTokenResponse.getAccessToken())
          .refreshToken(loginTokenResponse.getRefreshToken())
          .build();

    } catch (Exception e) {
      log.error(e.getMessage());
      throw e;
    }


  }

}
