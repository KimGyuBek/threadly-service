package com.threadly.adapter.redis.repository.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.core.port.token.out.command.dto.UpsertRefreshTokenCommand;
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
  private TokenCommandPortRepository tokenRepository;

  @Mock
  private ValueOperations<String, String> valueOperations;

  /**
   * findUserByRefreshToken 테스트
   * [Case #1] userId가 있는 경우
   * [Case #2] userId가 없는 경우
   */
  @Test
  public void existsRefreshTokenByUserId_shouldReturnTrue_ifUserIdExists() throws Exception {
    //given
    String userId = "userId";
    String key = "token:refresh:" + userId;

    when(redisTemplate.hasKey(key)).thenReturn(true);

    //when
    boolean result = tokenRepository.existsRefreshTokenByUserId(userId);

    //then
    verify(redisTemplate).hasKey(key);
    assertThat(result).isTrue();

  }

  @Test
  public void existsRefreshToken_shouldReturnFalse_ifUserNotExists() throws Exception {
    //given
    String userId = "userId";
    String key = "token:refresh:" + userId;

    when(redisTemplate.hasKey(key)).thenReturn(false);

    //when
    boolean result = tokenRepository.existsRefreshTokenByUserId(userId);

    //then
    verify(redisTemplate).hasKey(key);
    assertFalse(result);

  }

  /**
   * upsertRefreshToken 테스트
   * [Case #1] 이미 저장된 토큰이 있는 경우
   * [Case #2] 저장 된 토큰이 없는 경우
   *
   * @throws Exception
   */
  @Test
  public void upsertToken_shouldStoreRefreshToken_whenOldTokenExists
  () throws Exception {
    //given
    String userId = "user1";
    String newRefreshToken = "newRefreshToken";
    Duration duration = Duration.ofMillis(5);

    String key = "token:refresh:" + userId;

    UpsertRefreshTokenCommand request = UpsertRefreshTokenCommand.builder()
        .userId(userId)
        .refreshToken(newRefreshToken)
        .duration(duration)
        .build();

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    //when
    tokenRepository.upsertRefreshToken(request);

    //then
    verify(redisTemplate.opsForValue()).set(key,newRefreshToken, duration);
  }

  @Test
  public void upsertToken_shouldStoreRefreshToken_whenOldTokenNotExits()
      throws Exception {
    //given
    String userId = "user1";
    String newRefreshToken = "newRefreshToken";
    Duration duration = Duration.ofMillis(5);

    String key = "token:refresh:" + userId;

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    //when
    tokenRepository.upsertRefreshToken(
        UpsertRefreshTokenCommand.builder()
            .userId(userId)
            .refreshToken(newRefreshToken)
            .duration(duration)
            .build()
    );

    //then
    verify(redisTemplate.opsForValue()).set(key, newRefreshToken, duration);
  }
}

