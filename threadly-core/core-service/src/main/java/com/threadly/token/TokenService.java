package com.threadly.token;

import com.threadly.token.response.TokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TokenService
 */
@Service
@RequiredArgsConstructor
public class TokenService implements FetchTokenUseCase {

  private final FetchTokenPort fetchTokenPort;
  private final InsertTokenPort insertTokenPort;
  private final UpdateTokenPort updateTokenPort;

  private final TokenPort tokenPort;


  @Value("${jwt.secret}")
  private String secretKey;

  /**
   * SiginingKey 생성
   *
   * @return
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Token 생성
   *
   * @param userId
   * @param expireAt
   * @return
   */
  private String getToken(String userId, Duration expireAt) {
    Date now = new Date();
    Instant instant = now.toInstant();

    return
        Jwts.builder()
            .claim("userId", userId)
            .claim("userType", "USER")
            .issuedAt(now)
            .expiration(Date.from(Instant.from(instant.plus(expireAt))))
            .signWith(getSigningKey())
            .compact();
  }

  @Transactional
  @Override
  public TokenResponse upsertToken(String userId) {

    /*Token 생성*/
    String accessToken = getToken(userId, Duration.ofHours(3));
    String refreshToken = getToken(userId, Duration.ofHours(24));

    Token token = Token.upsertToken(userId, accessToken, refreshToken);
    tokenPort.upsertToken(token);

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();

  }


}
