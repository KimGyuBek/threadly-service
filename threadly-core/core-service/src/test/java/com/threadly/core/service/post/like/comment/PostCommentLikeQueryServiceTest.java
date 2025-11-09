package com.threadly.core.service.post.like.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.like.comment.query.dto.GetPostCommentLikersQuery;
import com.threadly.core.port.post.in.like.comment.query.dto.PostCommentLiker;
import com.threadly.core.port.post.out.like.comment.PostCommentLikeQueryPort;
import com.threadly.core.port.post.out.like.comment.PostCommentLikerProjection;
import com.threadly.core.service.post.validator.PostCommentValidator;
import java.time.LocalDateTime;
import java.util.List;
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
 * PostCommentLikeQueryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostCommentLikeQueryServiceTest {

  @InjectMocks
  private PostCommentLikeQueryService postCommentLikeQueryService;

  @Mock
  private PostCommentValidator postCommentValidator;

  @Mock
  private PostCommentLikeQueryPort postCommentLikeQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 댓글 좋아요 사용자 목록 조회*/
    @Order(1)
    @DisplayName("1. 댓글 좋아요 사용자 목록이 커서 기반으로 조회되는지 검증")
    @Test
    void getPostCommentLikers_shouldReturnPagedResponse_whenCommentActive() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();
      GetPostCommentLikersQuery query = new GetPostCommentLikersQuery(
          "post-1", "comment-1", now, "cursor-1", 1
      );

      PostCommentLikerProjection projection1 = new PostCommentLikerProjection() {
        @Override
        public String getLikerId() {
          return "liker-1";
        }

        @Override
        public String getLikerNickname() {
          return "댓글러1";
        }

        @Override
        public String getLikerProfileImageUrl() {
          return null;
        }

        @Override
        public String getLikerBio() {
          return "bio";
        }

        @Override
        public LocalDateTime getLikedAt() {
          return now.minusSeconds(5);
        }
      };

      PostCommentLikerProjection projection2 = new PostCommentLikerProjection() {
        @Override
        public String getLikerId() {
          return "liker-2";
        }

        @Override
        public String getLikerNickname() {
          return "댓글러2";
        }

        @Override
        public String getLikerProfileImageUrl() {
          return "/commenter.png";
        }

        @Override
        public String getLikerBio() {
          return "bio";
        }

        @Override
        public LocalDateTime getLikedAt() {
          return now.minusSeconds(10);
        }
      };

      doNothing().when(postCommentValidator).validateAccessibleStatus(query.commentId());
      when(postCommentLikeQueryPort.fetchCommentLikerListByCommentIdWithCursor(
          query.commentId(),
          query.cursorLikedAt(),
          query.cursorLikerId(),
          query.limit() + 1
      )).thenReturn(List.of(projection1, projection2));

      //when
      CursorPageApiResponse<PostCommentLiker> response =
          postCommentLikeQueryService.getPostCommentLikers(query);

      //then
      verify(postCommentValidator).validateAccessibleStatus(query.commentId());
      verify(postCommentLikeQueryPort).fetchCommentLikerListByCommentIdWithCursor(
          query.commentId(),
          query.cursorLikedAt(),
          query.cursorLikerId(),
          query.limit() + 1
      );

      assertThat(response.content()).hasSize(1);
      PostCommentLiker liker = response.content().getFirst();
      assertThat(liker.liker().userId()).isEqualTo("liker-1");
      assertThat(liker.liker().profileImageUrl()).isEqualTo("default");

      CursorPageApiResponse.NextCursor nextCursor = response.nextCursor();
      assertThat(nextCursor.cursorId()).isEqualTo("liker-1");
      assertThat(nextCursor.cursorTimestamp()).isEqualTo(projection1.getLikedAt());
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 존재하지 않는 댓글 좋아요 목록 조회*/
    @Order(1)
    @DisplayName("1. 댓글이 존재하지 않는 경우 예외가 발생하는지 검증")
    @Test
    void getPostCommentLikers_shouldThrow_whenCommentNotFound() throws Exception {
      //given
      GetPostCommentLikersQuery query = new GetPostCommentLikersQuery(
          "post-1", "comment-unknown", null, null, 10
      );
      doThrow(new PostCommentException(ErrorCode.POST_COMMENT_NOT_FOUND))
          .when(postCommentValidator).validateAccessibleStatus(query.commentId());

      //when & then
      assertThatThrownBy(() -> postCommentLikeQueryService.getPostCommentLikers(query))
          .isInstanceOf(PostCommentException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_COMMENT_NOT_FOUND);
    }

    /*[Case #2] 접근할 수 없는 상태의 댓글 좋아요 목록 조회*/
    @Order(2)
    @DisplayName("2. 댓글이 비활성화된 경우 예외가 발생하는지 검증")
    @Test
    void getPostCommentLikers_shouldThrow_whenCommentNotAccessible() throws Exception {
      //given
      GetPostCommentLikersQuery query = new GetPostCommentLikersQuery(
          "post-1", "comment-1", null, null, 10
      );
      doThrow(new PostCommentException(ErrorCode.POST_COMMENT_NOT_ACCESSIBLE))
          .when(postCommentValidator).validateAccessibleStatus(query.commentId());

      //when & then
      assertThatThrownBy(() -> postCommentLikeQueryService.getPostCommentLikers(query))
          .isInstanceOf(PostCommentException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_COMMENT_NOT_ACCESSIBLE);
    }
  }
}
