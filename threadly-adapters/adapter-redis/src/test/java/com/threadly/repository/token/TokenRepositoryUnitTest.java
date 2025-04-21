package com.threadly.repository.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.token.UpsertRefreshToken;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * TokenRepository 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class TokenRepositoryUnitTest {


  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @InjectMocks
  private TokenRepository tokenRepository;

  @Mock
  private ValueOperations<String, String> valueOperations;

  /**
   * findUserByRefreshToken 테스트
   * [Case #1] userId가 있는 경우
   * [Case #2] userId가 없는 경우
   */
  @Test
  public void findUserByRefreshToken_shouldReturnUserId_ifUserIdExists() throws Exception {
    //given
    String refreshToken = "refreshToken";
    String key = "token:reverse:" + refreshToken;
    String userId = "userId";

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(key)).thenReturn(userId);

    //when
    String result = tokenRepository.findUserIdByRefreshToken(refreshToken);

    //then
    verify(redisTemplate.opsForValue()).get(key);
    assertThat(result).isEqualTo(userId);

  }

  @Test
  public void findUserByRefreshToken_shouldReturnNull_ifUserNotExists() throws Exception {
    //given
    String refreshToken = "refreshToken";
    String key = "token:reverse:" + refreshToken;
    String userId = "userId";

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(key)).thenReturn(null);

    //when
    String result = tokenRepository.findUserIdByRefreshToken(refreshToken);

    //then
    verify(redisTemplate.opsForValue()).get(key);
    assertThat(result).isNull();

  }

  /**
   * upsertRefreshToken 테스트
   * [Case #1] 이미 저장된 토큰이 있는 경우
   * [Case #2] 저장 된 토큰이 없는 경우
   *
   * @throws Exception
   */
  @Test
  public void upsertToken_deleteOldReverseKey_andStoresNewToken_andRefreshToken_whenOldTokenExists
  () throws Exception {
    //given
    String userId = "user1";
    String oldRefreshToken = "oldRefreshToken";
    String newRefreshToken = "newRefreshToken";
    Duration duration = Duration.ofMillis(5);

    String key = "token:refresh:" + userId;
    String oldReverseKey = "token:reverse:" + oldRefreshToken;
    String newReverseKey = "token:reverse:" + newRefreshToken;

    UpsertRefreshToken request = UpsertRefreshToken.builder()
        .userId(userId)
        .refreshToken(newRefreshToken)
        .duration(duration)
        .build();

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(key)).thenReturn(oldRefreshToken);

    //when
    tokenRepository.upsertRefreshToken(request);

    //then
    verify(redisTemplate).delete(oldReverseKey);
    verify(redisTemplate.opsForValue()).set(key, newRefreshToken, duration);
    verify(redisTemplate.opsForValue()).set(newReverseKey, userId, duration);
  }

  @Test
  public void upsertToken_storesNewRefreshToken_andReverseMap_whenOldTokenNotExits()
      throws Exception {
    //given
    String userId = "user1";
    String newRefreshToken = "newRefreshToken";
    Duration duration = Duration.ofMillis(5);

    String key = "token:refresh:" + userId;
    String reverseKey = "token:reverse:" + newRefreshToken;

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(key)).thenReturn(null);
    //when
    tokenRepository.upsertRefreshToken(
        UpsertRefreshToken.builder()
            .userId(userId)
            .refreshToken(newRefreshToken)
            .duration(duration)
            .build()
    );

    //then
    verify(redisTemplate, never()).delete(reverseKey);
    verify(redisTemplate.opsForValue()).set(key, newRefreshToken, duration);
    verify(redisTemplate.opsForValue()).set(reverseKey, userId, duration);

  }
}

