package com.threadly.core.service.post.like.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.CommentLikeMeta;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.comment.CommentLike;
import com.threadly.core.domain.post.comment.PostComment;
import com.threadly.core.port.post.in.like.comment.command.dto.LikePostCommentApiResponse;
import com.threadly.core.port.post.in.like.comment.command.dto.LikePostCommentCommand;
import com.threadly.core.port.post.out.comment.PostCommentQueryPort;
import com.threadly.core.port.post.out.like.comment.PostCommentLikeQueryPort;
import com.threadly.core.port.post.out.like.comment.PostCommentLikerCommandPort;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

/**
 * PostCommentLikeCommandService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostCommentLikeCommandServiceTest {

  @InjectMocks
  private PostCommentLikeCommandService postCommentLikeCommandService;

  @Mock
  private PostCommentQueryPort postCommentQueryPort;

  @Mock
  private PostCommentLikeQueryPort postCommentLikeQueryPort;

  @Mock
  private PostCommentLikerCommandPort postCommentLikerCommandPort;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @Order(1)
  @Nested
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("댓글 좋아요 테스트")
  class LikePostCommentTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 좋아요하지 않은 댓글 좋아요 요청*/
      @Order(1)
      @DisplayName("1. 처음 좋아요하는 경우 좋아요가 저장되는지 검증")
      @Test
      void likePostComment_shouldCreateLike_whenUserNotLikedBefore() throws Exception {
        //given
        LikePostCommentCommand command = new LikePostCommentCommand("comment-1", "user-1");
        PostComment comment = PostComment.newTestComment("comment-1", "post-1", "writer-1",
            "content", PostCommentStatus.ACTIVE);

        when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(Optional.of(comment));
        when(postCommentLikeQueryPort.existsByCommentIdAndUserId(command.getCommentId(),
            command.getUserId())).thenReturn(false);
        when(postCommentLikeQueryPort.fetchLikeCountByCommentId(command.getCommentId()))
            .thenReturn(7L);

        ArgumentCaptor<CommentLike> likeCaptor = ArgumentCaptor.forClass(CommentLike.class);
        ArgumentCaptor<NotificationPublishCommand> notificationCaptor =
            ArgumentCaptor.forClass(NotificationPublishCommand.class);

        //when
        LikePostCommentApiResponse response = postCommentLikeCommandService.likePostComment(command);

        //then
        verify(postCommentLikerCommandPort).createPostCommentLike(likeCaptor.capture());
        CommentLike savedLike = likeCaptor.getValue();
        assertThat(savedLike.getCommentId()).isEqualTo(command.getCommentId());
        assertThat(savedLike.getUserId()).isEqualTo(command.getUserId());

        verify(applicationEventPublisher).publishEvent(notificationCaptor.capture());
        NotificationPublishCommand notification = notificationCaptor.getValue();
        assertThat(notification.receiverId()).isEqualTo(comment.getUserId());
        assertThat(notification.actorId()).isEqualTo(command.getUserId());
        assertThat(notification.notificationType()).isEqualTo(NotificationType.COMMENT_LIKE);
        CommentLikeMeta meta = (CommentLikeMeta) notification.notificationMetaData();
        assertThat(meta.postId()).isEqualTo(comment.getPostId());
        assertThat(meta.commentId()).isEqualTo(comment.getCommentId());
        assertThat(meta.commentExcerpt()).contains(comment.getContent());

        assertThat(response.commentId()).isEqualTo(command.getCommentId());
        assertThat(response.likeCount()).isEqualTo(7L);
      }

      /*[Case #2] 이미 좋아요한 댓글 좋아요 요청*/
      @Order(2)
      @DisplayName("2. 이미 좋아요한 경우 중복 저장되지 않는지 검증")
      @Test
      void likePostComment_shouldSkipSaving_whenUserAlreadyLiked() throws Exception {
        //given
        LikePostCommentCommand command = new LikePostCommentCommand("comment-1", "user-1");
        PostComment comment = PostComment.newTestComment("comment-1", "post-1", "writer-1",
            "content", PostCommentStatus.ACTIVE);

        when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(Optional.of(comment));
        when(postCommentLikeQueryPort.existsByCommentIdAndUserId(command.getCommentId(),
            command.getUserId())).thenReturn(true);
        when(postCommentLikeQueryPort.fetchLikeCountByCommentId(command.getCommentId()))
            .thenReturn(9L);

        //when
        LikePostCommentApiResponse response = postCommentLikeCommandService.likePostComment(command);

        //then
        verify(postCommentLikerCommandPort, never()).createPostCommentLike(any(CommentLike.class));
        verify(applicationEventPublisher).publishEvent(any(NotificationPublishCommand.class));
        assertThat(response.commentId()).isEqualTo(command.getCommentId());
        assertThat(response.likeCount()).isEqualTo(9L);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 존재하지 않는 댓글 좋아요 요청*/
      @Order(1)
      @DisplayName("1. 댓글이 없는 경우 예외가 발생하는지 검증")
      @Test
      void likePostComment_shouldThrow_whenCommentNotFound() throws Exception {
        //given
        LikePostCommentCommand command = new LikePostCommentCommand("comment-unknown", "user-1");
        when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> postCommentLikeCommandService.likePostComment(command))
            .isInstanceOf(PostCommentException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_COMMENT_NOT_FOUND);
      }

      /*[Case #2] 좋아요가 불가능한 상태의 댓글*/
      @Order(2)
      @DisplayName("2. 좋아요가 불가능한 댓글인 경우 예외가 발생하는지 검증")
      @Test
      void likePostComment_shouldThrow_whenCommentNotLikeable() throws Exception {
        //given
        LikePostCommentCommand command = new LikePostCommentCommand("comment-1", "user-1");
        PostComment comment = PostComment.newTestComment("comment-1", "post-1", "writer-1",
            "content", PostCommentStatus.DELETED);
        when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(Optional.of(comment));

        //when & then
        assertThatThrownBy(() -> postCommentLikeCommandService.likePostComment(command))
            .isInstanceOf(PostCommentException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_COMMENT_LIKE_NOT_ALLOWED);
      }
    }
  }

  @Order(2)
  @Nested
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("댓글 좋아요 취소 테스트")
  class CancelPostCommentLikeTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 좋아요한 댓글 좋아요 취소*/
      @Order(1)
      @DisplayName("1. 좋아요한 상태에서 취소 시 삭제되는지 검증")
      @Test
      void cancelPostCommentLike_shouldDelete_whenUserLiked() throws Exception {
        //given
        LikePostCommentCommand command = new LikePostCommentCommand("comment-1", "user-1");
        PostComment comment = PostComment.newTestComment("comment-1", "post-1", "writer-1",
            "content", PostCommentStatus.ACTIVE);

        when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(Optional.of(comment));
        when(postCommentLikeQueryPort.existsByCommentIdAndUserId(command.getCommentId(),
            command.getUserId())).thenReturn(true);
        when(postCommentLikeQueryPort.fetchLikeCountByCommentId(command.getCommentId()))
            .thenReturn(4L);

        //when
        LikePostCommentApiResponse response =
            postCommentLikeCommandService.cancelPostCommentLike(command);

        //then
        verify(postCommentLikerCommandPort)
            .deletePostCommentLike(command.getCommentId(), command.getUserId());
        assertThat(response.commentId()).isEqualTo(command.getCommentId());
        assertThat(response.likeCount()).isEqualTo(4L);
      }

      /*[Case #2] 좋아요하지 않은 댓글 좋아요 취소*/
      @Order(2)
      @DisplayName("2. 좋아요하지 않은 상태에서 취소 시 삭제가 호출되지 않는지 검증")
      @Test
      void cancelPostCommentLike_shouldSkipDeletion_whenUserNotLiked() throws Exception {
        //given
        LikePostCommentCommand command = new LikePostCommentCommand("comment-1", "user-1");
        PostComment comment = PostComment.newTestComment("comment-1", "post-1", "writer-1",
            "content", PostCommentStatus.ACTIVE);

        when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(Optional.of(comment));
        when(postCommentLikeQueryPort.existsByCommentIdAndUserId(command.getCommentId(),
            command.getUserId())).thenReturn(false);
        when(postCommentLikeQueryPort.fetchLikeCountByCommentId(command.getCommentId()))
            .thenReturn(0L);

        //when
        LikePostCommentApiResponse response =
            postCommentLikeCommandService.cancelPostCommentLike(command);

        //then
        verify(postCommentLikerCommandPort, never())
            .deletePostCommentLike(command.getCommentId(), command.getUserId());
        assertThat(response.commentId()).isEqualTo(command.getCommentId());
        assertThat(response.likeCount()).isEqualTo(0L);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 존재하지 않는 댓글 좋아요 취소*/
      @Order(1)
      @DisplayName("1. 댓글이 없는 경우 예외가 발생하는지 검증")
      @Test
      void cancelPostCommentLike_shouldThrow_whenCommentNotFound() throws Exception {
        //given
        LikePostCommentCommand command = new LikePostCommentCommand("comment-unknown", "user-1");
        when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> postCommentLikeCommandService.cancelPostCommentLike(command))
            .isInstanceOf(PostCommentException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_COMMENT_NOT_FOUND);
      }

      /*[Case #2] 좋아요 취소가 불가능한 댓글*/
      @Order(2)
      @DisplayName("2. 좋아요 취소가 불가능한 댓글인 경우 예외가 발생하는지 검증")
      @Test
      void cancelPostCommentLike_shouldThrow_whenCommentNotLikeable() throws Exception {
        //given
        LikePostCommentCommand command = new LikePostCommentCommand("comment-1", "user-1");
        PostComment comment = PostComment.newTestComment("comment-1", "post-1", "writer-1",
            "content", PostCommentStatus.DELETED);
        when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(Optional.of(comment));

        //when & then
        assertThatThrownBy(() -> postCommentLikeCommandService.cancelPostCommentLike(command))
            .isInstanceOf(PostCommentException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_COMMENT_LIKE_NOT_ALLOWED);
      }
    }
  }
}
