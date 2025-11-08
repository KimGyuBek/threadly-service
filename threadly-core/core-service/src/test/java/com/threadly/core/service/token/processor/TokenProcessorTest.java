package com.threadly.core.service.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.properties.TtlProperties;
import com.threadly.core.port.token.out.TokenCommandPort;
import com.threadly.core.port.token.out.command.InsertBlackListTokenCommand;
import com.threadly.core.service.token.processor.TokenProcessor;
import java.time.Duration;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TokenProcessor 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class TokenProcessorTest {

  @InjectMocks
  private TokenProcessor tokenProcessor;

  @Mock
  private TokenCommandPort tokenCommandPort;

  @Mock
  private TtlProperties ttlProperties;

  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("토큰 무효화 테스트")
  @Nested
  class AddBlackListTokenAndDeleteRefreshTokenTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 블랙리스트 토큰 등록 및 refreshToken 삭제 성공*/
      @Order(1)
      @DisplayName("1. Bearer 토큰에서 accessToken 추출 후 블랙리스트 등록 및 refreshToken 삭제 검증")
      @Test
      public void addBlackListTokenAndDeleteRefreshToken_shouldSuccess() throws Exception {
        //given
        String userId = "user1";
        String accessToken = "valid-access-token";
        String bearerToken = "Bearer " + accessToken;
        Duration blacklistTtl = Duration.ofSeconds(3600);

        when(ttlProperties.getBlacklistToken()).thenReturn(blacklistTtl);

        //when
        tokenProcessor.addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);

        //then
        /*블랙리스트 토큰 등록 검증*/
        ArgumentCaptor<InsertBlackListTokenCommand> commandCaptor = ArgumentCaptor.forClass(
            InsertBlackListTokenCommand.class);
        verify(tokenCommandPort).saveBlackListToken(commandCaptor.capture());

        InsertBlackListTokenCommand capturedCommand = commandCaptor.getValue();
        org.assertj.core.api.Assertions.assertThat(capturedCommand.getUserId()).isEqualTo(userId);
        org.assertj.core.api.Assertions.assertThat(capturedCommand.getAccessToken()).isEqualTo(
            accessToken);
        org.assertj.core.api.Assertions.assertThat(capturedCommand.getDuration()).isEqualTo(
            blacklistTtl);

        /*refreshToken 삭제 검증*/
        verify(tokenCommandPort).deleteRefreshToken(userId);
      }

      /*[Case #2] Bearer 접두사가 있는 토큰 처리*/
      @Order(2)
      @DisplayName("2. Bearer 접두사가 포함된 토큰에서 정상적으로 accessToken 추출되는지 검증")
      @Test
      public void addBlackListTokenAndDeleteRefreshToken_shouldExtractToken_whenBearerPrefix()
          throws Exception {
        //given
        String userId = "user1";
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        String bearerToken = "Bearer " + accessToken;
        Duration blacklistTtl = Duration.ofSeconds(7200);

        when(ttlProperties.getBlacklistToken()).thenReturn(blacklistTtl);

        //when
        tokenProcessor.addBlackListTokenAndDeleteRefreshToken(userId, bearerToken);

        //then
        ArgumentCaptor<InsertBlackListTokenCommand> commandCaptor = ArgumentCaptor.forClass(
            InsertBlackListTokenCommand.class);
        verify(tokenCommandPort).saveBlackListToken(commandCaptor.capture());

        InsertBlackListTokenCommand capturedCommand = commandCaptor.getValue();
        org.assertj.core.api.Assertions.assertThat(capturedCommand.getAccessToken()).isEqualTo(
            accessToken);
      }
    }
  }
}
