package com.threadly.repository.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.threadly.token.UpsertRefreshToken;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * TokenRepository 통합 테스트
 */
@ActiveProfiles("test")
@SpringBootTest(classes = {RedisTestApplication.class})
class TokenRepositoryTest {

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private final Duration duration = Duration.ofSeconds(5);

  /**
   * RefreshToken upserting 테스트
   * [Case #1] 이미 저장된 토큰이 있는 경우 
   * [Case #2] 저장 된 토큰이 없는 경우
   * @throws Exception
   */
  @Test
  public void upsertRefreshToken_deletesOldReverseKey_andStoresNewToken_whenOldTokenExists() throws Exception {
    //given
    String userId = "user1";
    String refreshToken = "refreshToken";
    String newRefreshToken = "newRefreshToken";

    String key = "token:refresh:" + userId;
    String reverseKey = "token:reverse:" + newRefreshToken;

    redisTemplate.opsForValue().set(key, refreshToken);
    redisTemplate.opsForValue().set(reverseKey, userId);

    //when
    tokenRepository.upsertRefreshToken(
        UpsertRefreshToken.builder()
            .userId(userId)
            .refreshToken(newRefreshToken)
            .duration(duration)
            .build()
    );

    //then
    assertAll(
        () -> assertEquals(redisTemplate.opsForValue().get(key), newRefreshToken),
        () -> assertEquals(redisTemplate.opsForValue().get(reverseKey), userId),
        () -> assertThat(redisTemplate.hasKey("token:reverse:" + refreshToken)).isFalse()
    );
  }

  @Test
  public void upsertToken_storesNewToken_andReverseKey_whenOldTokenNotExists
      () throws Exception {
    //given
    String userId = "user1";
    String refreshToken = "newRefreshToken";

    String key = "token:refresh:" + userId;
    String reverseKey = "token:reverse:" + refreshToken;

    //when
    tokenRepository.upsertRefreshToken(
        UpsertRefreshToken.builder()
            .userId(userId)
            .refreshToken(refreshToken)
            .duration(duration)
            .build()
    );

    //then
    assertAll(
        () -> assertThat(redisTemplate.opsForValue().get(key)).isEqualTo(refreshToken),
        () -> assertThat(redisTemplate.opsForValue().get(reverseKey)).isEqualTo(userId)
    );

  }


}


