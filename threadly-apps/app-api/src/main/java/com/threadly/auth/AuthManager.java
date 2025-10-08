package com.threadly.auth;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.token.TokenException;
import com.threadly.commons.properties.TtlProperties;
import com.threadly.commons.security.JwtTokenProvider;
import com.threadly.commons.utils.JwtTokenUtils;
import com.threadly.core.domain.token.TokenPurposeType;
import com.threadly.core.domain.user.UserStatus;
import com.threadly.core.port.auth.in.token.response.LoginTokenApiResponse;
import com.threadly.core.port.auth.in.token.response.TokenReissueApiResponse;
import com.threadly.core.port.auth.in.verification.LoginUserUseCase;
import com.threadly.core.port.auth.in.verification.PasswordVerificationUseCase;
import com.threadly.core.port.auth.in.verification.ReissueTokenUseCase;
import com.threadly.core.port.auth.in.verification.response.PasswordVerificationToken;
import com.threadly.core.port.token.out.TokenCommandPort;
import com.threadly.core.port.token.out.command.InsertBlackListTokenCommand;
import com.threadly.core.port.token.out.command.UpsertRefreshTokenCommand;
import com.threadly.core.port.token.out.TokenQueryPort;
import com.threadly.core.port.user.in.profile.command.dto.RegisterMyProfileApiResponse;
import com.threadly.core.port.user.in.query.UserQueryUseCase;
import com.threadly.core.port.user.in.shared.UserResponse;
import com.threadly.global.exception.UserAuthenticationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthManager implements LoginUserUseCase, PasswordVerificationUseCase,
    ReissueTokenUseCase {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  private final UserQueryUseCase userQueryUseCase;

  private final TokenQueryPort tokenQueryPort;
  private final TokenCommandPort tokenCommandPort;

  private final LoginAttemptLimiter loginAttemptLimiter;

  private final JwtTokenProvider jwtTokenProvider;
  private final TtlProperties ttlProperties;

  public Authentication getAuthentication(String accessToken) {
    /*accessToken으로 사용자 조회*/
    String userId = jwtTokenProvider.getUserId(accessToken);

    /*userId로 사용자 조회*/
    UserResponse user = userQueryUseCase.findUserByUserId(userId);

    /*권한 설정*/
    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_" + user.getUserRoleType().name())
    );

    JwtAuthenticationUser authenticationUser = new JwtAuthenticationUser(
        user.getUserId(),
        user.getUserStatus(),
        authorities
    );

    return new UsernamePasswordAuthenticationToken(
        authenticationUser,
        null,
        authenticationUser.getAuthorities()
    );
  }

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
        TokenPurposeType.PASSWORD_REVERIFY.name(),
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
  public LoginTokenApiResponse login(String email, String password) {

    /*사용자 조회*/
    UserResponse user = userQueryUseCase.findUserByEmail(email);

    /*탈퇴한 사용자인경우*/
    if (user.getUserStatus().equals(UserStatus.DELETED)) {
      throw new UserAuthenticationException(ErrorCode.USER_ALREADY_DELETED);
    }

    String userId = user.getUserId();

    /*로그인 횟수 제한이 걸려있는지 검증*/
    if (!loginAttemptLimiter.checkLoginAttempt(userId)) {
      throw new UserAuthenticationException(ErrorCode.LOGIN_ATTEMPT_EXCEEDED);
    }

    /*email 인증이 되어있는지 검증*/
    if (!user.isEmailVerified()) {
      throw new UserAuthenticationException(ErrorCode.EMAIL_NOT_VERIFIED);
    }

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

      /*인증*/
      SecurityContextHolder.getContext().setAuthentication(authenticate);

      /*로그인 토큰 응답 생성*/
      LoginTokenApiResponse tokenResponse = new LoginTokenApiResponse(
          jwtTokenProvider.createAccessToken(userId, user.getUserRoleType().name(),
              user.getUserStatus().name()),
          jwtTokenProvider.createRefreshToken(userId)
      );

      /*토큰 저장*/
      tokenCommandPort.upsertRefreshToken(UpsertRefreshTokenCommand.builder()
          .userId(userId)
          .refreshToken(tokenResponse.refreshToken())
          .duration(ttlProperties.getRefreshToken())
          .build());


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
  public TokenReissueApiResponse reissueLoginToken(String refreshToken) {
    /*토큰 추출*/
    refreshToken = JwtTokenUtils.extractAccessToken(refreshToken);

    /*refreshToken 검증*/
    jwtTokenProvider.validateToken(refreshToken);

    /*userId 추출*/
    String userId = jwtTokenProvider.getUserId(refreshToken);

    /*db에서 user 조회*/
    UserResponse user = userQueryUseCase.findUserByUserId(userId);

    /*refreshToken이 저장되어 있는지 검증*/
    if (!tokenQueryPort.existsRefreshTokenByUserId(userId)) {
      throw new TokenException(ErrorCode.TOKEN_MISSING);
    }

    /*login Token 재생성*/
    LoginTokenApiResponse loginTokenApiResponse = new LoginTokenApiResponse(
        jwtTokenProvider.createAccessToken(userId, user.getUserRoleType().name(),
            user.getUserStatus().name()),
        jwtTokenProvider.createRefreshToken(userId)
    );

    /*기존 토큰 덮어쓰기*/
    tokenCommandPort.upsertRefreshToken(
        UpsertRefreshTokenCommand.builder()
            .userId(userId)
            .refreshToken(loginTokenApiResponse.refreshToken())
            .duration(ttlProperties.getRefreshToken())
            .build()
    );

    return TokenReissueApiResponse.builder()
        .accessToken(loginTokenApiResponse.accessToken())
        .refreshToken(loginTokenApiResponse.refreshToken())
        .build();
  }

  /**
   * 로그아웃
   *
   * @param bearerToken
   */
  public void logout(String bearerToken) {


    /*accessToken 추출*/
    String accessToken = JwtTokenUtils.extractAccessToken(bearerToken);

    /*accessToken 검증*/
    jwtTokenProvider.validateToken(accessToken);

    /*token에서 userId 추출*/
    String userId = jwtTokenProvider.getUserId(accessToken);

    /*redis에 저장*/
    tokenCommandPort.saveBlackListToken(
        InsertBlackListTokenCommand.builder()
            .userId(userId)
            .accessToken(accessToken)
            .duration(jwtTokenProvider.getAccessTokenTtl(accessToken))
            .build()
    );

    /*refreshToken 삭제*/
    tokenCommandPort.deleteRefreshToken(userId);
  }

  /**
   * blacklist 검증
   *
   * @param token
   * @return
   */
  public boolean isBlacklisted(String token) {
    return
        tokenQueryPort.existsBlackListTokenByAccessToken(token);
  }

  @Override
  public RegisterMyProfileApiResponse reissueToken(String userId) {
    /*사용자 조회*/
    UserResponse user = userQueryUseCase.findUserByUserId(userId);

    return new RegisterMyProfileApiResponse(
        jwtTokenProvider.createAccessToken(userId, user.getUserRoleType().name(),
            user.getUserStatus().name()),
        jwtTokenProvider.createRefreshToken(userId)
    );
  }
}
