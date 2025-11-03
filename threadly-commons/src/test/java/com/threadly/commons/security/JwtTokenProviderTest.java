package com.threadly.commons.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.token.TokenException;
import com.threadly.commons.properties.TtlProperties;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * JwtTokenProvider 테스트
 */
class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;
  private TtlProperties ttlProperties;
  private String testSecretKey;

  @BeforeEach
  void setUp() {
    ttlProperties = new TtlProperties();
    ttlProperties.setAccessToken(3600L); // 1시간 = 3600초
    ttlProperties.setRefreshToken(604800L); // 7일 = 604800초

    // Base64로 인코딩된 256비트 이상의 테스트용 시크릿 키
    testSecretKey = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tdGVzdGluZy10aGlzLWlzLWEtbG9uZy1rZXktdG8tbWVldC1taW5pbXVtLXJlcXVpcmVtZW50cw==";

    jwtTokenProvider = new JwtTokenProvider(ttlProperties);
    ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", testSecretKey);
  }

  @Nested
  @DisplayName("토큰 생성 테스트")
  class CreateTokenTest {

    /*[Case #1] accessToken이 정상적으로 생성되어야 한다*/
    @DisplayName("createAccessToken - accessToken이 정상적으로 생성되어야 한다")
    @Test
    public void createAccessToken_shouldCreateTokenSuccessfully() throws Exception {
      //given
      String userId = "user123";
      String userType = "USER";
      String userStatusType = "ACTIVE";

      //when
      String accessToken = jwtTokenProvider.createAccessToken(userId, userType, userStatusType);

      //then
      assertThat(accessToken).isNotNull();
      assertThat(accessToken).isNotEmpty();
    }

    /*[Case #2] refreshToken이 정상적으로 생성되어야 한다*/
    @DisplayName("createRefreshToken - refreshToken이 정상적으로 생성되어야 한다")
    @Test
    public void createRefreshToken_shouldCreateTokenSuccessfully() throws Exception {
      //given
      String userId = "user123";

      //when
      String refreshToken = jwtTokenProvider.createRefreshToken(userId);

      //then
      assertThat(refreshToken).isNotNull();
      assertThat(refreshToken).isNotEmpty();
    }

    /*[Case #3] 목적이 있는 토큰이 정상적으로 생성되어야 한다*/
    @DisplayName("createTokenWithPurpose - 목적이 있는 토큰이 정상적으로 생성되어야 한다")
    @Test
    public void createTokenWithPurpose_shouldCreateTokenSuccessfully() throws Exception {
      //given
      String userId = "user123";
      String purpose = "EMAIL_VERIFICATION";
      Duration duration = Duration.ofMinutes(30);

      //when
      String token = jwtTokenProvider.createTokenWithPurpose(userId, purpose, duration);

      //then
      assertThat(token).isNotNull();
      assertThat(token).isNotEmpty();
    }
  }

  @Nested
  @DisplayName("토큰 검증 테스트")
  class ValidateTokenTest {

    /*[Case #1] 유효한 토큰은 검증에 성공해야 한다*/
    @DisplayName("validateToken - 유효한 토큰은 검증에 성공해야 한다")
    @Test
    public void validateToken_shouldReturnTrue_whenTokenIsValid() throws Exception {
      //given
      String userId = "user123";
      String userType = "USER";
      String userStatusType = "ACTIVE";
      String token = jwtTokenProvider.createAccessToken(userId, userType, userStatusType);

      //when
      boolean result = jwtTokenProvider.validateToken(token);

      //then
      assertTrue(result);
    }

    /*[Case #2] 만료된 토큰은 예외가 발생해야 한다*/
    @DisplayName("validateToken - 만료된 토큰은 예외가 발생해야 한다")
    @Test
    public void validateToken_shouldThrowException_whenTokenIsExpired() throws Exception {
      //given
      String expiredToken = createExpiredToken();

      //when & then
      TokenException exception = assertThrows(TokenException.class,
          () -> jwtTokenProvider.validateToken(expiredToken));
      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_EXPIRED);
    }

    /*[Case #3] 잘못된 토큰은 예외가 발생해야 한다*/
    @DisplayName("validateToken - 잘못된 토큰은 예외가 발생해야 한다")
    @Test
    public void validateToken_shouldThrowException_whenTokenIsInvalid() throws Exception {
      //given
      String invalidToken = "invalid.token.here";

      //when & then
      TokenException exception = assertThrows(TokenException.class,
          () -> jwtTokenProvider.validateToken(invalidToken));
      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_INVALID);
    }

    private String createExpiredToken() {
      Date now = new Date();
      Instant instant = now.toInstant();

      byte[] keyBytes = Decoders.BASE64.decode(testSecretKey);
      SecretKey key = Keys.hmacShaKeyFor(keyBytes);

      return Jwts.builder()
          .claim("userId", "user123")
          .claim("userType", "USER")
          .claim("userStatusType", "ACTIVE")
          .setId(UUID.randomUUID().toString().substring(0, 8))
          .setIssuedAt(Date.from(instant.minus(Duration.ofHours(2))))
          .setExpiration(Date.from(instant.minus(Duration.ofHours(1))))
          .signWith(key, SignatureAlgorithm.HS256)
          .compact();
    }
  }

  @Nested
  @DisplayName("토큰에서 정보 추출 테스트")
  class ExtractInfoFromTokenTest {

    /*[Case #1] 토큰에서 userId를 정상적으로 추출해야 한다*/
    @DisplayName("getUserId - 토큰에서 userId를 정상적으로 추출해야 한다")
    @Test
    public void getUserId_shouldExtractUserId_whenTokenIsValid() throws Exception {
      //given
      String userId = "user123";
      String userType = "USER";
      String userStatusType = "ACTIVE";
      String token = jwtTokenProvider.createAccessToken(userId, userType, userStatusType);

      //when
      String extractedUserId = jwtTokenProvider.getUserId(token);

      //then
      assertThat(extractedUserId).isEqualTo(userId);
    }

    /*[Case #2] 토큰에서 userType을 정상적으로 추출해야 한다*/
    @DisplayName("getUserType - 토큰에서 userType을 정상적으로 추출해야 한다")
    @Test
    public void getUserType_shouldExtractUserType_whenTokenIsValid() throws Exception {
      //given
      String userId = "user123";
      String userType = "USER";
      String userStatusType = "ACTIVE";
      String token = jwtTokenProvider.createAccessToken(userId, userType, userStatusType);

      //when
      String extractedUserType = jwtTokenProvider.getUserType(token);

      //then
      assertThat(extractedUserType).isEqualTo(userType);
    }

    /*[Case #3] 토큰에서 userStatusType을 정상적으로 추출해야 한다*/
    @DisplayName("getUserStatusType - 토큰에서 userStatusType을 정상적으로 추출해야 한다")
    @Test
    public void getUserStatusType_shouldExtractUserStatusType_whenTokenIsValid() throws Exception {
      //given
      String userId = "user123";
      String userType = "USER";
      String userStatusType = "ACTIVE";
      String token = jwtTokenProvider.createAccessToken(userId, userType, userStatusType);

      //when
      String extractedUserStatusType = jwtTokenProvider.getUserStatusType(token);

      //then
      assertThat(extractedUserStatusType).isEqualTo(userStatusType);
    }

    /*[Case #4] 모든 정보를 올바르게 추출해야 한다*/
    @DisplayName("토큰에서 모든 정보를 올바르게 추출해야 한다")
    @Test
    public void extractAllInfo_shouldExtractAllInfoCorrectly_whenTokenIsValid() throws Exception {
      //given
      String userId = "user123";
      String userType = "USER";
      String userStatusType = "ACTIVE";
      String token = jwtTokenProvider.createAccessToken(userId, userType, userStatusType);

      //when
      String extractedUserId = jwtTokenProvider.getUserId(token);
      String extractedUserType = jwtTokenProvider.getUserType(token);
      String extractedUserStatusType = jwtTokenProvider.getUserStatusType(token);

      //then
      assertAll(
          () -> assertThat(extractedUserId).isEqualTo(userId),
          () -> assertThat(extractedUserType).isEqualTo(userType),
          () -> assertThat(extractedUserStatusType).isEqualTo(userStatusType)
      );
    }
  }

  @Nested
  @DisplayName("resolveToken 테스트")
  class ResolveTokenTest {

    /*[Case #1] Authorization 헤더에서 토큰을 정상적으로 추출해야 한다*/
    @DisplayName("resolveToken - Authorization 헤더에서 토큰을 정상적으로 추출해야 한다")
    @Test
    public void resolveToken_shouldExtractToken_whenAuthorizationHeaderIsValid() throws Exception {
      //given
      String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";
      String bearerToken = "Bearer " + token;

      HttpServletRequest request = mock(HttpServletRequest.class);
      when(request.getHeader("Authorization")).thenReturn(bearerToken);

      //when
      String resolvedToken = jwtTokenProvider.resolveToken(request);

      //then
      assertThat(resolvedToken).isEqualTo(token);
    }

    /*[Case #2] Authorization 헤더가 없는 경우 예외가 발생해야 한다*/
    @DisplayName("resolveToken - Authorization 헤더가 없는 경우 예외가 발생해야 한다")
    @Test
    public void resolveToken_shouldThrowException_whenAuthorizationHeaderIsMissing()
        throws Exception {
      //given
      HttpServletRequest request = mock(HttpServletRequest.class);
      when(request.getHeader("Authorization")).thenReturn(null);

      //when & then
      TokenException exception = assertThrows(TokenException.class,
          () -> jwtTokenProvider.resolveToken(request));
      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_MISSING);
    }

    /*[Case #3] Bearer 접두사가 없는 경우 예외가 발생해야 한다*/
    @DisplayName("resolveToken - Bearer 접두사가 없는 경우 예외가 발생해야 한다")
    @Test
    public void resolveToken_shouldThrowException_whenBearerPrefixIsMissing() throws Exception {
      //given
      HttpServletRequest request = mock(HttpServletRequest.class);
      when(request.getHeader("Authorization")).thenReturn("InvalidToken");

      //when & then
      TokenException exception = assertThrows(TokenException.class,
          () -> jwtTokenProvider.resolveToken(request));
      assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.TOKEN_MISSING);
    }
  }

  @Nested
  @DisplayName("getAccessTokenTtl 테스트")
  class GetAccessTokenTtlTest {

    /*[Case #1] accessToken의 남은 TTL을 정상적으로 추출해야 한다*/
    @DisplayName("getAccessTokenTtl - accessToken의 남은 TTL을 정상적으로 추출해야 한다")
    @Test
    public void getAccessTokenTtl_shouldExtractTtl_whenTokenIsValid() throws Exception {
      //given
      String userId = "user123";
      String userType = "USER";
      String userStatusType = "ACTIVE";
      String token = jwtTokenProvider.createAccessToken(userId, userType, userStatusType);

      //when
      Duration ttl = jwtTokenProvider.getAccessTokenTtl(token);

      //then
      assertThat(ttl).isNotNull();
      assertThat(ttl.getSeconds()).isGreaterThan(0);
      // 토큰이 1시간 후 만료되므로, TTL은 대략 1시간(3600초)에 가까워야 함
      assertThat(ttl.getSeconds()).isLessThanOrEqualTo(3600);
    }
  }
}
