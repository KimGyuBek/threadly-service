package com.threadly.core.service.post.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.port.post.in.comment.query.dto.GetPostCommentApiResponse;
import com.threadly.core.port.post.in.comment.query.dto.GetPostCommentListQuery;
import com.threadly.core.port.post.out.PostQueryPort;
import com.threadly.core.port.post.out.comment.PostCommentDetailForUserProjection;
import com.threadly.core.port.post.out.comment.PostCommentQueryPort;
import java.time.LocalDateTime;
import java.util.List;
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
 * PostCommentQueryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostCommentQueryServiceTest {

  @InjectMocks
  private PostCommentQueryService postCommentQueryService;

  @Mock
  private PostCommentQueryPort postCommentQueryPort;

  @Mock
  private PostQueryPort postQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 게시글 댓글 커서 기반 조회*/
    @Order(1)
    @DisplayName("1. 게시글 댓글 목록이 커서 기반으로 조회되는지 검증")
    @Test
    void getPostCommentDetailListForUser_shouldReturnPagedResponse_whenPostActive() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();
      GetPostCommentListQuery query = new GetPostCommentListQuery(
          "post-1", "viewer-1", now, "comment-cursor", 1
      );

      PostCommentDetailForUserProjection projection1 = new PostCommentDetailForUserProjection() {
        @Override
        public String getPostId() {
          return "post-1";
        }

        @Override
        public String getCommentId() {
          return "comment-1";
        }

        @Override
        public String getCommenterId() {
          return "writer-1";
        }

        @Override
        public String getCommenterNickname() {
          return "댓글러1";
        }

        @Override
        public String getCommenterProfileImageUrl() {
          return null;
        }

        @Override
        public long getLikeCount() {
          return 5L;
        }

        @Override
        public LocalDateTime getCommentedAt() {
          return now.minusSeconds(5);
        }

        @Override
        public String getContent() {
          return "첫번째 댓글";
        }

        @Override
        public boolean isLiked() {
          return true;
        }
      };

      PostCommentDetailForUserProjection projection2 = new PostCommentDetailForUserProjection() {
        @Override
        public String getPostId() {
          return "post-1";
        }

        @Override
        public String getCommentId() {
          return "comment-2";
        }

        @Override
        public String getCommenterId() {
          return "writer-2";
        }

        @Override
        public String getCommenterNickname() {
          return "댓글러2";
        }

        @Override
        public String getCommenterProfileImageUrl() {
          return "/commenter2.png";
        }

        @Override
        public long getLikeCount() {
          return 2L;
        }

        @Override
        public LocalDateTime getCommentedAt() {
          return now.minusSeconds(10);
        }

        @Override
        public String getContent() {
          return "두번째 댓글";
        }

        @Override
        public boolean isLiked() {
          return false;
        }
      };

      when(postQueryPort.fetchPostStatusByPostId(query.postId()))
          .thenReturn(Optional.of(PostStatus.ACTIVE));
      when(postCommentQueryPort.fetchCommentListByPostIdWithCursor(
          query.postId(),
          query.userId(),
          query.cursorCommentedAt(),
          query.cursorCommentId(),
          query.limit() + 1
      )).thenReturn(List.of(projection1, projection2));

      //when
      CursorPageApiResponse<GetPostCommentApiResponse> response =
          postCommentQueryService.getPostCommentDetailListForUser(query);

      //then
      verify(postQueryPort).fetchPostStatusByPostId(query.postId());
      verify(postCommentQueryPort).fetchCommentListByPostIdWithCursor(
          query.postId(),
          query.userId(),
          query.cursorCommentedAt(),
          query.cursorCommentId(),
          query.limit() + 1
      );

      assertThat(response.content()).hasSize(1);
      GetPostCommentApiResponse firstComment = response.content().getFirst();
      assertThat(firstComment.postId()).isEqualTo("post-1");
      assertThat(firstComment.commenter().userId()).isEqualTo("writer-1");
      assertThat(firstComment.commenter().profileImageUrl()).isEqualTo("/");
      assertThat(firstComment.likeCount()).isEqualTo(5L);
      assertThat(firstComment.liked()).isTrue();

      CursorPageApiResponse.NextCursor nextCursor = response.nextCursor();
      assertThat(nextCursor.cursorId()).isEqualTo("comment-1");
      assertThat(nextCursor.cursorTimestamp()).isEqualTo(projection1.getCommentedAt());
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 존재하지 않는 게시글 댓글 조회*/
    @Order(1)
    @DisplayName("1. 게시글이 존재하지 않는 경우 예외가 발생하는지 검증")
    @Test
    void getPostCommentDetailListForUser_shouldThrow_whenPostNotFound() throws Exception {
      //given
      GetPostCommentListQuery query = new GetPostCommentListQuery(
          "post-unknown", "viewer-1", null, null, 10
      );

      when(postQueryPort.fetchPostStatusByPostId(query.postId()))
          .thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> postCommentQueryService.getPostCommentDetailListForUser(query))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    /*[Case #2] 접근할 수 없는 게시글 댓글 조회*/
    @Order(2)
    @DisplayName("2. 게시글이 비활성 상태인 경우 예외가 발생하는지 검증")
    @Test
    void getPostCommentDetailListForUser_shouldThrow_whenPostNotAccessible() throws Exception {
      //given
      GetPostCommentListQuery query = new GetPostCommentListQuery(
          "post-1", "viewer-1", null, null, 10
      );

      when(postQueryPort.fetchPostStatusByPostId(query.postId()))
          .thenReturn(Optional.of(PostStatus.DELETED));

      //when & then
      assertThatThrownBy(() -> postCommentQueryService.getPostCommentDetailListForUser(query))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_NOT_ACCESSIBLE);
    }
  }
}
