package com.threadly.repository.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.threadly.token.UpsertRefreshToken;
import java.time.Duration;
import net.bytebuddy.utility.dispatcher.JavaDispatcher.Container;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

  @BeforeEach
  void clearRedis() {
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }

  /* upsertRefreshToken 테스트 */
  /* [Case #1] 이미 저장된 토큰이 있는 경우 */
  @DisplayName("upsertRefreshToken - 이미 저장된 토큰이 있는 경우")
  @Test
  public void upsertRefreshToken_shouldStoreNewToken_whenOldTokenExists() throws Exception {
    //given
    String userId = "user1";
    String oldRefreshToken = "oldRefreshToken";
    String newRefreshToken = "newRefreshToken";

    String key = "token:refresh:" + userId;

    redisTemplate.opsForValue().set(key, oldRefreshToken);

    //when
    tokenRepository.upsertRefreshToken(
        UpsertRefreshToken.builder()
            .userId(userId)
            .refreshToken(newRefreshToken)
            .duration(duration)
            .build()
    );

    //then
    assertEquals(redisTemplate.opsForValue().get(key), newRefreshToken);
  }

  /* [Case #2] 저장 된 토큰이 없는 경우*/
  @DisplayName("upsertRefreshToken - 이미 저장된 토큰이 없는 경우")
  @Test
  public void upsertToken_shouldStoreNewToken_whenOldTokenNotExists
  () throws Exception {
    //given
    String userId = "user1";
    String refreshToken = "newRefreshToken";

    String key = "token:refresh:" + userId;

    //when
    tokenRepository.upsertRefreshToken(
        UpsertRefreshToken.builder()
            .userId(userId)
            .refreshToken(refreshToken)
            .duration(duration)
            .build()
    );

    //then
    assertThat(redisTemplate.opsForValue().get(key)).isEqualTo(refreshToken);
  }

  /*existsRefreshToken 테스트*/
  /*[Case #1] userId로 저장된 토큰이 있을 경우 true 리턴*/
  @DisplayName("existsRefreshToken - userId가 존재할 경우, true 리턴")
  @Test
  public void existsRefreshToken_shouldReturnTrue_whenUserIdExists() throws Exception {
    //given
    String userId = "user1";
    String refreshToken = "refreshToken";
    String key = "token:refresh:" + userId;

    redisTemplate.opsForValue().set(key, refreshToken);

    //when
    boolean result = tokenRepository.existsRefreshTokenByUserId(userId);

    //then
    assertTrue(result);
  }

  /*[Case #2] userId로 저장된 토큰이 있을 경우 false 리턴*/
  @DisplayName("existsRefreshToken - userId가 존재하지 않을 경우, false 리턴")
  @Test
  public void existsRefreshToken_shouldReturnFalse_whenUserIdNotExists() throws Exception {
    //given
    String userId = "user1";
    String key = "token:refresh:" + userId;

    //when
    boolean result = tokenRepository.existsRefreshTokenByUserId(userId);

    //then
    assertFalse(result);

  }

}


