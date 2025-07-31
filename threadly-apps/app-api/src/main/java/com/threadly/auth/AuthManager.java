package com.threadly.auth;

import com.threadly.auth.token.response.LoginTokenApiResponse;
import com.threadly.auth.token.response.TokenReissueApiResponse;
import com.threadly.auth.verification.LoginUserUseCase;
import com.threadly.auth.verification.PasswordVerificationUseCase;
import com.threadly.auth.verification.ReissueTokenUseCase;
import com.threadly.auth.verification.response.PasswordVerificationToken;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.token.TokenException;
import com.threadly.global.exception.UserAuthenticationException;
import com.threadly.properties.TtlProperties;
import com.threadly.security.JwtTokenProvider;
import com.threadly.token.DeleteTokenPort;
import com.threadly.token.FetchTokenPort;
import com.threadly.token.InsertBlackListToken;
import com.threadly.token.InsertTokenPort;
import com.threadly.token.TokenPurpose;
import com.threadly.token.UpsertRefreshToken;
import com.threadly.token.UpsertTokenPort;
import com.threadly.user.UserStatusType;
import com.threadly.user.get.GetUserUseCase;
import com.threadly.user.get.UserResponse;
import com.threadly.user.profile.fetch.FetchUserProfilePort;
import com.threadly.user.profile.register.MyProfileRegisterApiResponse;
import com.threadly.utils.JwtTokenUtils;
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

  private final GetUserUseCase getUserUseCase;

  private final FetchTokenPort fetchTokenPort;
  private final InsertTokenPort insertTokenPort;
  private final UpsertTokenPort upsertTokenPort;
  private final DeleteTokenPort deleteTokenPort;

  private final LoginAttemptLimiter loginAttemptLimiter;

  private final JwtTokenProvider jwtTokenProvider;
  private final TtlProperties ttlProperties;

  private final FetchUserProfilePort fetchUserProfilePort;

  public Authentication getAuthentication(String accessToken) {
    /*accessToken으로 사용자 조회*/
    String userId = jwtTokenProvider.getUserId(accessToken);

    /*userId로 사용자 조회*/
    UserResponse user = getUserUseCase.findUserByUserId(userId);

    /*권한 설정*/
    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())
    );

    JwtAuthenticationUser authenticationUser = new JwtAuthenticationUser(
        user.getUserId(),
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
  public LoginTokenApiResponse login(String email, String password) {

    /*사용자 조회*/
    UserResponse user = getUserUseCase.findUserByEmail(email);

    /*탈퇴한 사용자인경우*/
    if (user.getUserStatusType().equals(UserStatusType.DELETED)) {
      throw new UserAuthenticationException(ErrorCode.USER_ALREADY_DELETED);
    }

    /*비활성화된 사용자인경우*/
    if (user.getUserStatusType().equals(UserStatusType.INACTIVE)) {
      throw new UserAuthenticationException(ErrorCode.USER_INACTIVE);
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

    boolean profileComplete = fetchUserProfilePort.existsUserProfileByUserId(userId);

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
      LoginTokenApiResponse tokenResponse = new LoginTokenApiResponse(
          jwtTokenProvider.createAccessToken(userId, user.getUserType().name(), profileComplete),
          jwtTokenProvider.createRefreshToken(userId)
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
  public TokenReissueApiResponse reissueLoginToken(String refreshToken) {

//    /*refreshToken이 null일 경우*/
//    if (refreshToken == null || refreshToken.equals("null")) {
//      throw new TokenException(ErrorCode.TOKEN_MISSING);
//    }

    /*jwt 분리*/
//    refreshToken = refreshToken.substring(7);

    refreshToken = JwtTokenUtils.extractAccessToken(refreshToken);

    /*refreshToken 검증*/
    jwtTokenProvider.validateToken(refreshToken);

    /*userId 추출*/
    String userId = jwtTokenProvider.getUserId(refreshToken);

    /*db에서 user 조회*/
    UserResponse user = getUserUseCase.findUserByUserId(userId);
    boolean profileComplete = fetchUserProfilePort.existsUserProfileByUserId(userId);


    /*refreshToken이 저장되어 있는지 검증*/
    if (!fetchTokenPort.existsRefreshTokenByUserId(userId)) {
      throw new TokenException(ErrorCode.TOKEN_MISSING);
    }

    /*login Token 재생성*/
    LoginTokenApiResponse loginTokenApiResponse = new LoginTokenApiResponse(
        jwtTokenProvider.createAccessToken(userId, user.getUserType().name(), profileComplete),
        jwtTokenProvider.createRefreshToken(userId)
    );

    /*기존 토큰 덮어쓰기*/
    upsertTokenPort.upsertRefreshToken(
        UpsertRefreshToken.builder()
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

  @Override
  public MyProfileRegisterApiResponse reissueToken(String userId) {
    /*사용자 조회*/
    UserResponse user = getUserUseCase.findUserByUserId(userId);

    boolean profileComplete = fetchUserProfilePort.existsUserProfileByUserId(userId);

    return new MyProfileRegisterApiResponse(
        jwtTokenProvider.createAccessToken(userId, user.getUserType().name(), profileComplete),
        jwtTokenProvider.createRefreshToken(userId)
    );
  }
}
