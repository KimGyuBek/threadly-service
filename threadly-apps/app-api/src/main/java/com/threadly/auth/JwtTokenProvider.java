package com.threadly.auth;

import com.threadly.ErrorCode;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.exception.token.TokenException;
import com.threadly.properties.TtlProperties;
import com.threadly.token.InsertTokenPort;
import com.threadly.user.FetchUserUseCase;
import com.threadly.user.response.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

  private final FetchUserUseCase fetchUserUseCase;
  private final InsertTokenPort insertTokenPort;

  private final TtlProperties ttlProperties;

  @Value("${jwt.secret}")
  private String secretKey;


  /*TODO 위치 옮기기*/
  public Authentication getAuthentication(String accessToken) {

    /*accessToken으로 사용자 조회*/
    String userId = getUserId(accessToken);

    /*userId로 사용자 조회*/
    UserResponse user = fetchUserUseCase.findUserByUserId(userId);

    /*권한 설정*/
    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority(user.getUserType())
    );

    /*UserDetails 생성*/
    UserDetails principal = new User(
        user.getUserId(),
        StringUtils.isEmpty(
            user.getPassword()) ? "password" : user.getPassword(),
        authorities
    );

    return new UsernamePasswordAuthenticationToken(
        principal,
        user.getUserId(),
        authorities
    );

  }


  /**
   * jwt 토큰 생성
   *
   * @param userId
   * @param duration
   * @return
   */
  public String generateToken(String userId, Duration duration) {
    String token = getToken(userId, duration);
    return token;
  }

  /**
   * login 토큰 생성
   *
   * @param userId
   * @return
   */
  public LoginTokenResponse generateLoginToken(String userId) {
    String accessToken = getToken(userId, ttlProperties.getAccessToken());
    String refreshToken = getToken(userId, ttlProperties.getRefreshToken());

    return new LoginTokenResponse(accessToken, refreshToken);
  }


  /**
   * validate Token
   *
   * @param token
   * @return
   */
  public boolean validateToken(String token) {

    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token);

      log.info("토큰 검증됨");

      return true;

      /*토큰 만료*/
    } catch (ExpiredJwtException e) {
      log.info("토큰 만료됨");

      throw new TokenException(ErrorCode.TOKEN_EXPIRED);

      /*기타 예외*/
    } catch (Exception e) {
      log.info("토큰 검증 안 됨");

      throw new TokenException(ErrorCode.TOKEN_INVALID);
    }

  }

  /**
   * accessToken에서 userId 추출
   *
   * @param token
   * @return
   */
  public String getUserId(String token) {
    Claims body = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return
        body.get("userId", String.class);
  }

  /**
   * SiginingKey 생성
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Token 생성
   *
   * @return
   */
  private String getToken(String userId, Duration duration) {
    Date now = new Date();
    Instant instant = now.toInstant();

    return
        Jwts.builder()
            .claim("userId", userId)
            .claim("userType", "USER")
            .setIssuedAt(now)
            .setExpiration(
                Date.from(Instant.from(instant.plus(duration)))
            )
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }

}
