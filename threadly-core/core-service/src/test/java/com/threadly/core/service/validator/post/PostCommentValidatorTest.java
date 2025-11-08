package com.threadly.core.service.validator.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.comment.PostComment;
import com.threadly.core.port.post.out.comment.PostCommentQueryPort;
import java.util.Optional;
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
 * PostCommentValidator 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostCommentValidatorTest {

  @InjectMocks
  private PostCommentValidator postCommentValidator;

  @Mock
  private PostCommentQueryPort postCommentQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("댓글 조회")
  class GetPostCommentOrThrowTest {

    /*[Case #1] 댓글 조회 성공*/
    @Order(1)
    @DisplayName("1. commentId로 댓글 조회 시 정상적으로 반환되는지 검증")
    @Test
    void getPostCommentOrThrow_shouldReturnComment_whenCommentExists() throws Exception {
      //given
      String commentId = "comment-1";
      PostComment comment = PostComment.newComment("post-1", "user-1", "content");
      when(postCommentQueryPort.fetchById(commentId)).thenReturn(Optional.of(comment));

      //when
      PostComment result = postCommentValidator.getPostCommentOrThrow(commentId);

      //then
      assertThat(result).isNotNull();
      assertThat(result.getUserId()).isEqualTo("user-1");
      verify(postCommentQueryPort).fetchById(commentId);
    }

    /*[Case #2] 댓글 조회 실패 - 미존재*/
    @Order(2)
    @DisplayName("2. commentId로 댓글 조회 시 존재하지 않으면 예외 발생")
    @Test
    void getPostCommentOrThrow_shouldThrow_whenCommentNotExists() throws Exception {
      //given
      String commentId = "nonexistent-comment";
      when(postCommentQueryPort.fetchById(commentId)).thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> postCommentValidator.getPostCommentOrThrow(commentId))
          .isInstanceOf(PostCommentException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_COMMENT_NOT_FOUND);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("댓글 접근 가능 상태 검증")
  class ValidateAccessibleStatusTest {

    /*[Case #1] ACTIVE 상태 - 정상*/
    @Order(1)
    @DisplayName("1. ACTIVE 상태인 댓글인 경우 예외가 발생하지 않음")
    @Test
    void validateAccessibleStatus_shouldPass_whenActive() throws Exception {
      //given
      String commentId = "comment-1";
      PostComment comment = PostComment.newComment("post-1", "user-1", "content");
      when(postCommentQueryPort.fetchById(commentId)).thenReturn(Optional.of(comment));

      //when & then
      assertThatCode(() -> postCommentValidator.validateAccessibleStatus(commentId))
          .doesNotThrowAnyException();
      verify(postCommentQueryPort).fetchById(commentId);
    }

    /*[Case #2] DELETED 상태 - 예외 발생*/
    @Order(2)
    @DisplayName("2. DELETED 상태인 댓글인 경우 예외 발생")
    @Test
    void validateAccessibleStatus_shouldThrow_whenDeleted() throws Exception {
      //given
      String commentId = "comment-1";
      PostComment comment = PostComment.newComment("post-1", "user-1", "content");
      comment.markAsDeleted();
      when(postCommentQueryPort.fetchById(commentId)).thenReturn(Optional.of(comment));

      //when & then
      assertThatThrownBy(() -> postCommentValidator.validateAccessibleStatus(commentId))
          .isInstanceOf(PostCommentException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_COMMENT_NOT_ACCESSIBLE);
    }

    /*[Case #3] 댓글 미존재 - 예외 발생 (getPostCommentOrThrow에서)*/
    @Order(3)
    @DisplayName("3. 댓글이 존재하지 않는 경우 예외 발생")
    @Test
    void validateAccessibleStatus_shouldThrow_whenCommentNotExists() throws Exception {
      //given
      String commentId = "nonexistent-comment";
      when(postCommentQueryPort.fetchById(commentId)).thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> postCommentValidator.validateAccessibleStatus(commentId))
          .isInstanceOf(PostCommentException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_COMMENT_NOT_FOUND);
    }
  }
}
