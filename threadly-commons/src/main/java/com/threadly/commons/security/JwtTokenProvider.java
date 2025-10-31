package com.threadly.commons.security;

import static com.threadly.commons.utils.LogFormatUtils.logFailure;
import static com.threadly.commons.utils.LogFormatUtils.logSuccess;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.token.TokenException;
import com.threadly.commons.properties.TtlProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "jwt.enabled", havingValue = "true", matchIfMissing = false)
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final TtlProperties ttlProperties;

  @Value("${jwt.secret}")
  private String secretKey;

  /**
   * header에서 jwt 추출
   *
   * @param request
   * @return
   */
  public String resolveToken(HttpServletRequest request) {
    /*authorization header 가져오기*/
    String bearerToken = request.getHeader("Authorization");

    /*bearer Token이 존재할 경우*/
    if (bearerToken != null && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }

    /*존재하지 않을 경우*/
    throw new TokenException(ErrorCode.TOKEN_MISSING);
  }


  /**
   * accessToken 생성
   *
   * @param userId
   * @return
   */
  public String createAccessToken(String userId, String userType, String userStatusType) {
    return generateAccessToken(userId, userType, userStatusType);
  }

  /**
   * refreshToken 생성
   *
   * @param userId
   * @return
   */
  public String createRefreshToken(String userId) {
    return generateRefreshToken(userId);
  }

  /**
   * 목적이 있는 토큰 생성
   *
   * @param userId
   * @param purpose
   * @param duration
   * @return
   */
  public String createTokenWithPurpose(String userId, String purpose, Duration duration) {
    return generateTokenWithPurpose(userId, purpose, duration);
  }

  /**
   * validate Token
   *
   * @param token
   * @return
   */
  /*TODO 분리*/
  public boolean validateToken(String token) {

    try {
      Jwts.parserBuilder()
          .setSigningKey(generateSigningKey())
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
   * jwt에서 claim:userId 추출
   *
   * @param token
   * @return
   */
  public String getUserId(String token) {
    return
        getClaims(token).get("userId", String.class);
  }

  /**
   * jwt에서 claim:userType 추출
   *
   * @param token
   * @return
   */
  public String getUserType(String token) {
    return getClaims(token).get("userType", String.class);
  }

  /**
   * jwt에서 claim:userStatusType 추출
   *
   * @param token
   * @return
   */
  public String getUserStatusType(String token) {
    return getClaims(token).get("userStatusType", String.class);
  }


  private Claims getClaims(String token) {
    Claims body = Jwts.parserBuilder()
        .setSigningKey(generateSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return body;
  }

  /**
   * SiginingKey 생성
   */
  private SecretKey generateSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * accessToken 생성
   *
   * @param userId
   * @param userType
   * @param userStatusType
   * @return
   */
  private String generateAccessToken(String userId, String userType, String userStatusType) {
    Date now = new Date();
    Instant instant = now.toInstant();

    return
        Jwts.builder()
            .claim("userId", userId)
            .claim("userType", userType)
            .claim("userStatusType", userStatusType)
            .setId(UUID.randomUUID().toString().substring(0, 8))
            .setIssuedAt(now)
            .setExpiration(
                Date.from(Instant.from(instant.plus(ttlProperties.getAccessToken())))
            )
            .signWith(generateSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * refreshToken 생성
   *
   * @return
   */
  private String generateRefreshToken(String userId) {
    Date now = new Date();
    Instant instant = now.toInstant();

    return
        Jwts.builder()
            .claim("userId", userId)
            .setId(UUID.randomUUID().toString().substring(0, 8))
            .setIssuedAt(now)
            .setExpiration(
                Date.from(Instant.from(instant.plus(ttlProperties.getRefreshToken())))
            )
            .signWith(generateSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * 목적이 있는 토큰 생성
   *
   * @return
   */
  private String generateTokenWithPurpose(String userId, String purpose, Duration duration) {
    Date now = new Date();
    Instant instant = now.toInstant();

    return
        Jwts.builder()
            .claim("userId", userId)
            .claim("purpose", purpose)
            .setId(UUID.randomUUID().toString().substring(0, 8))
            .setIssuedAt(now)
            .setExpiration(
                Date.from(Instant.from(instant.plus(duration)))
            )
            .signWith(generateSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * accessToken에서 남은 ttl 추출
   *
   * @return
   */
  public Duration getAccessTokenTtl(String accessToken) {
    Claims claims = getClaims(accessToken);

    /*만료 시간*/
    Date expiration = claims.getExpiration();

    /*현재 시간*/
    Date now = new Date();

    long ttlMillis = expiration.getTime() - now.getTime();

    return
        Duration.ofMillis(ttlMillis);
  }

}
