package com.threadly.auth;

import com.threadly.exception.token.TokenErrorType;
import com.threadly.exception.token.TokenException;
import com.threadly.token.FetchTokenUseCase;
import com.threadly.token.response.TokenResponse;
import com.threadly.token.response.UpdateTokenUseCase;
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
public class JwtTokenProvider {

  private final FetchTokenUseCase fetchTokenUseCase;
  private final UpdateTokenUseCase updateTokenUseCase;

  private final FetchUserUseCase fetchUserUseCase;

  @Value("${jwt.secret}")
  private String secretKey;

  /**
   * accessToken에서 userId 추출
   *
   * @param accessToken
   * @return
   */
  private String getUserId(String accessToken) {
    Claims body = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(accessToken)
        .getBody();

    return
        body.get("userId", String.class);
  }

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
   * upsert Token
   *
   * @param userId
   * @return
   */
  public TokenResponse upsertToken(String userId) {
    String accessToken = getToken(userId, Duration.ofHours(3));
    String refreshToken = getToken(userId, Duration.ofHours(12));

    TokenResponse tokenResponse = updateTokenUseCase.upsertToken(userId, accessToken, refreshToken);

    return tokenResponse;
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
  private String getToken(String userId, Duration expireAt) {
    Date now = new Date();
    Instant instant = now.toInstant();

    return
        Jwts.builder()
            .claim("userId", userId)
            .claim("userType", "USER")
            .setIssuedAt(now)
            .setExpiration(
                Date.from(Instant.from(instant.plus(expireAt)))
            )
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * validate Token
   *
   * @param accessToken
   * @return
   */
  public boolean validateToken(String accessToken) {

    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(accessToken);

      System.out.println("검증됨");

      return true;

      /*토큰 만료*/
    } catch (ExpiredJwtException e) {
      System.out.println("토큰 만료됨");

      throw new TokenException(TokenErrorType.EXPIRED);

      /*기타 예외*/
    } catch (Exception e) {
      System.out.println("검증 안 됨");

      throw new TokenException(TokenErrorType.INVALID);
    }

  }

}
