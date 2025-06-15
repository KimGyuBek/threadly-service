package com.threadly.auth;

import static com.threadly.utils.LogFormatUtils.logFailure;
import static com.threadly.utils.LogFormatUtils.logSuccess;

import com.threadly.exception.ErrorCode;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.exception.token.TokenException;
import com.threadly.properties.TtlProperties;
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
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final FetchUserUseCase fetchUserUseCase;

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

    AuthenticationUser authenticationUser = new AuthenticationUser(
        user.getUserId(),
        user.getEmail(),
        user.getPhone(),
        user.getPassword()
    );

    return new UsernamePasswordAuthenticationToken(
        authenticationUser,
        "",
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

      logSuccess("토큰 검증됨");

      return true;

      /*토큰 만료*/
    } catch (ExpiredJwtException e) {
      logFailure("토큰 만료됨");
      throw new TokenException(ErrorCode.TOKEN_EXPIRED);

      /*기타 예외*/
    } catch (Exception e) {
      logFailure("토큰 검증 안 됨");
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
            .setId(UUID.randomUUID().toString().substring(0, 8))
            .setIssuedAt(now)
            .setExpiration(
                Date.from(Instant.from(instant.plus(duration)))
            )
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * accessToken에서 남은 ttl 추출
   *
   * @return
   */
  public Duration getAccessTokenTtl(String accessToken) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(accessToken)
        .getBody();

    /*만료 시간*/
    Date expiration = claims.getExpiration();

    /*현재 시간*/
    Date now = new Date();

    long ttlMillis = expiration.getTime() - now.getTime();

    return
        Duration.ofSeconds(ttlMillis);
  }

}
