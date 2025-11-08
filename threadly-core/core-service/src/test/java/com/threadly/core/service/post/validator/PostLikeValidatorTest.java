package com.threadly.core.service.validator.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.core.port.post.out.like.post.PostLikeQueryPort;
import com.threadly.core.service.post.validator.PostLikeValidator;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PostLikeValidator 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostLikeValidatorTest {

  @InjectMocks
  private PostLikeValidator postLikeValidator;

  @Mock
  private PostLikeQueryPort postLikeQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("사용자 좋아요 여부 확인")
  class IsUserLikedTest {

    /*[Case #1] 사용자가 좋아요를 누른 경우*/
    @Order(1)
    @DisplayName("1. 사용자가 게시글에 좋아요를 누른 경우 true 반환")
    @Test
    void isUserLiked_shouldReturnTrue_whenUserLiked() throws Exception {
      //given
      String postId = "post-1";
      String userId = "user-1";
      when(postLikeQueryPort.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

      //when
      boolean result = postLikeValidator.isUserLiked(postId, userId);

      //then
      assertThat(result).isTrue();
      verify(postLikeQueryPort).existsByPostIdAndUserId(postId, userId);
    }

    /*[Case #2] 사용자가 좋아요를 누르지 않은 경우*/
    @Order(2)
    @DisplayName("2. 사용자가 게시글에 좋아요를 누르지 않은 경우 false 반환")
    @Test
    void isUserLiked_shouldReturnFalse_whenUserNotLiked() throws Exception {
      //given
      String postId = "post-1";
      String userId = "user-1";
      when(postLikeQueryPort.existsByPostIdAndUserId(postId, userId)).thenReturn(false);

      //when
      boolean result = postLikeValidator.isUserLiked(postId, userId);

      //then
      assertThat(result).isFalse();
      verify(postLikeQueryPort).existsByPostIdAndUserId(postId, userId);
    }
  }
}
