package com.threadly.core.service.validator.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.core.port.post.out.like.comment.PostCommentLikeQueryPort;
import com.threadly.core.service.post.validator.PostCommentLikeValidator;
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
 * PostCommentLikeValidator 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostCommentLikeValidatorTest {

  @InjectMocks
  private PostCommentLikeValidator postCommentLikeValidator;

  @Mock
  private PostCommentLikeQueryPort postCommentLikeQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("사용자 댓글 좋아요 여부 확인")
  class IsUserLikedTest {

    /*[Case #1] 사용자가 댓글에 좋아요를 누른 경우*/
    @Order(1)
    @DisplayName("1. 사용자가 댓글에 좋아요를 누른 경우 true 반환")
    @Test
    void isUserLiked_shouldReturnTrue_whenUserLiked() throws Exception {
      //given
      String commentId = "comment-1";
      String userId = "user-1";
      when(postCommentLikeQueryPort.existsByCommentIdAndUserId(commentId, userId)).thenReturn(true);

      //when
      boolean result = postCommentLikeValidator.isUserLiked(commentId, userId);

      //then
      assertThat(result).isTrue();
      verify(postCommentLikeQueryPort).existsByCommentIdAndUserId(commentId, userId);
    }

    /*[Case #2] 사용자가 댓글에 좋아요를 누르지 않은 경우*/
    @Order(2)
    @DisplayName("2. 사용자가 댓글에 좋아요를 누르지 않은 경우 false 반환")
    @Test
    void isUserLiked_shouldReturnFalse_whenUserNotLiked() throws Exception {
      //given
      String commentId = "comment-1";
      String userId = "user-1";
      when(postCommentLikeQueryPort.existsByCommentIdAndUserId(commentId, userId)).thenReturn(
          false);

      //when
      boolean result = postCommentLikeValidator.isUserLiked(commentId, userId);

      //then
      assertThat(result).isFalse();
      verify(postCommentLikeQueryPort).existsByCommentIdAndUserId(commentId, userId);
    }
  }
}
