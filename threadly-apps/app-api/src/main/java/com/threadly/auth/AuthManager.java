package com.threadly.auth;

import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.auth.token.response.TokenReissueResponse;
import com.threadly.auth.verification.LoginUserUseCase;
import com.threadly.auth.verification.PasswordVerificationUseCase;
import com.threadly.auth.verification.ReissueTokenUseCase;
import com.threadly.auth.verification.response.PasswordVerificationToken;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.token.TokenException;
import com.threadly.global.exception.UserAuthenticationException;
import com.threadly.properties.TtlProperties;
import com.threadly.token.DeleteTokenPort;
import com.threadly.token.FetchTokenPort;
import com.threadly.token.InsertBlackListToken;
import com.threadly.token.InsertTokenPort;
import com.threadly.token.TokenPurpose;
import com.threadly.token.UpsertRefreshToken;
import com.threadly.token.UpsertToken;
import com.threadly.user.FetchUserUseCase;
import com.threadly.user.response.UserProfileSetupApiResponse;
import com.threadly.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*TODO 이름 변경*/
@Service
@RequiredArgsConstructor
public class AuthManager implements LoginUserUseCase, PasswordVerificationUseCase,
    ReissueTokenUseCase {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  private final FetchUserUseCase fetchUserUseCase;

  private final FetchTokenPort fetchTokenPort;
  private final InsertTokenPort insertTokenPort;
  private final UpsertToken upsertTokenPort;
  private final DeleteTokenPort deleteTokenPort;

  private final LoginAttemptLimiter loginAttemptLimiter;

  private final JwtTokenProvider jwtTokenProvider;
  private final TtlProperties ttlProperties;

  @Override
  public PasswordVerificationToken getPasswordVerificationToken(String userId, String password) {

    /*인증용 토큰 생성*/
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        userId, password);

    /*인증 시도*/
    Authentication authenticate = authenticationManagerBuilder.getObject()
        .authenticate(authenticationToken);

    /*토큰 생성*/
    String tokenResponse = jwtTokenProvider.createTokenWithPurpose(userId,
        TokenPurpose.PASSWORD_REVERIFY.name(),
        ttlProperties.getPasswordVerification());

    /*SecurityContextHolder에 인증 정보 저장*/
    SecurityContextHolder.getContext().setAuthentication(authenticate);

    return new PasswordVerificationToken(tokenResponse);
  }

  /**
   * login
   *
   * @return
   */
  @Override
  public LoginTokenResponse login(String email, String password) {

    /*사용자 조회*/
    UserResponse findUser = fetchUserUseCase.findUserByEmail(email);

    String userId = findUser.getUserId();

    /*로그인 횟수 제한이 걸려있는지 검증*/
    if (!loginAttemptLimiter.checkLoginAttempt(userId)) {
      throw new UserAuthenticationException(ErrorCode.LOGIN_ATTEMPT_EXCEEDED);
    }

    /*email 인증이 되어있는지 검증*/
    if (!findUser.isEmailVerified()) {
      throw new UserAuthenticationException(ErrorCode.EMAIL_NOT_VERIFIED);
    }

    boolean profileComplete = fetchUserUseCase.isUserProfileExists(userId);

    /*TODO user 상태 검증*/

    /*인증용 토큰 생성*/
    /*
     * userId는 email로 조회된 사용자 식별자임
     * spring security의 username에 userId를 넣는 구조임
     */
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        userId, password);

    /*인증 시도*/
    try {
      Authentication authenticate = authenticationManagerBuilder.getObject()
          .authenticate(authenticationToken);

      /*로그인 토큰 응답 생성*/
      LoginTokenResponse tokenResponse = new LoginTokenResponse(
          jwtTokenProvider.createToken(findUser.getUserId(),
              findUser.getUserType().name(), profileComplete, ttlProperties.getAccessToken()),
          jwtTokenProvider.createToken(findUser.getUserId(),
              findUser.getUserType().name(), profileComplete, ttlProperties.getRefreshToken())
      );

      /*토큰 저장*/
      upsertTokenPort.upsertRefreshToken(UpsertRefreshToken.builder()
          .userId(userId)
          .refreshToken(tokenResponse.refreshToken())
          .duration(ttlProperties.getRefreshToken())
          .build());

      /*인증*/
      SecurityContextHolder.getContext().setAuthentication(authenticate);

      /*login attempt 삭제*/
      loginAttemptLimiter.removeLoginAttempt(userId);

      return tokenResponse;

      /*비밀번호가 일치하지 않는 경우*/
    } catch (BadCredentialsException e) {
      loginAttemptLimiter.upsertLoginAttempt(userId);
      throw e;
    }
  }

  /**
   * login token 재발급
   *
   * @return
   */
  public TokenReissueResponse reissueLoginToken(String refreshToken) {

    /*refreshToken이 null일 경우*/
    if (refreshToken == null || refreshToken.equals("null")) {
      throw new TokenException(ErrorCode.TOKEN_MISSING);
    }

    refreshToken = refreshToken.substring(7);

    /*refrehToken으로 userId 조회*/
    String userId = jwtTokenProvider.getUserId(refreshToken);
    String userType = jwtTokenProvider.getUserType(refreshToken);

    /*TODO 임시, refreshToken은 필요 없음 -> 분리*/
    boolean profileComplete = jwtTokenProvider.isProfileComplete(refreshToken);

    /*refreshToken이 저장되어 있는지 검증*/
    if (!fetchTokenPort.existsRefreshTokenByUserId(userId)) {
      throw new TokenException(ErrorCode.TOKEN_MISSING);
    }

    /*refreshToken 검증*/
    jwtTokenProvider.validateToken(refreshToken);

    /*login Token 재생성*/
    LoginTokenResponse loginTokenResponse = new LoginTokenResponse(
        jwtTokenProvider.createToken(userId, userType, profileComplete,
            ttlProperties.getAccessToken()),
        jwtTokenProvider.createToken(userId, userType, profileComplete,
            ttlProperties.getRefreshToken()
        ));

    /*기존 토큰 덮어쓰기*/
    upsertTokenPort.upsertRefreshToken(
        UpsertRefreshToken.builder()
            .userId(userId)
            .refreshToken(loginTokenResponse.refreshToken())
            .duration(ttlProperties.getRefreshToken())
            .build()
    );

    return TokenReissueResponse.builder()
        .accessToken(loginTokenResponse.accessToken())
        .refreshToken(loginTokenResponse.refreshToken())
        .build();
  }

  /**
   * 로그아웃
   *
   * @param token
   */
  public void logout(String token) {

    /*header에 토큰이 존재하지 않을경우*/
    if (!token.startsWith("Bearer")) {
      throw new TokenException(ErrorCode.TOKEN_MISSING);
    }

    /*accessToken 추출*/
    String accessToken = token.substring(7);

    /*accessToken 검증*/
    jwtTokenProvider.validateToken(accessToken);

    /*tokne으로 부터 userId 추출*/
    String userId = jwtTokenProvider.getUserId(accessToken);

    /*redis에 저장*/
    insertTokenPort.saveBlackListToken(
        InsertBlackListToken.builder()
            .userId(userId)
            .accessToken(accessToken)
            .duration(jwtTokenProvider.getAccessTokenTtl(accessToken))
            .build()
    );

    /*refreshToken 삭제*/
    deleteTokenPort.deleteRefreshToken(userId);
  }

  /**
   * blacklist 검증
   *
   * @param token
   * @return
   */
  public boolean isBlacklisted(String token) {
    return
        fetchTokenPort.existsBlackListTokenByAccessToken(token);
  }

  /**
   * TODO 임시
   */
  @Override
  public UserProfileSetupApiResponse reissueToken(String userId) {
    /*사용자 조회*/
    UserResponse findUser = fetchUserUseCase.findUserByUserId(userId);

    boolean profileComplete = fetchUserUseCase.isUserProfileExists(userId);

    return new UserProfileSetupApiResponse(
        jwtTokenProvider.createToken(userId,
            findUser.getUserType().name(), profileComplete, ttlProperties.getAccessToken()),
        jwtTokenProvider.createToken(findUser.getUserId(),
            findUser.getUserType().name(), profileComplete, ttlProperties.getRefreshToken())
    );
  }
}
