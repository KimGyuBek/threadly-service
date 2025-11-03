package com.threadly.core.service.post.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.commons.exception.post.PostException;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.domain.post.comment.PostComment;
import com.threadly.core.port.post.in.comment.command.dto.CreatePostCommentApiResponse;
import com.threadly.core.port.post.in.comment.command.dto.CreatePostCommentCommand;
import com.threadly.core.port.post.in.comment.command.dto.DeletePostCommentCommand;
import com.threadly.core.port.post.out.PostQueryPort;
import com.threadly.core.port.post.out.comment.PostCommentCommandPort;
import com.threadly.core.port.post.out.comment.PostCommentQueryPort;
import com.threadly.core.port.post.out.like.comment.PostCommentLikerCommandPort;
import com.threadly.core.port.user.out.profile.UserProfileQueryPort;
import com.threadly.core.port.user.out.profile.projection.UserPreviewProjection;
import com.threadly.core.service.notification.dto.NotificationPublishCommand;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

/**
 * PostCommentCommandService 테스트
 */
@ExtendWith(MockitoExtension.class)
class PostCommentCommandServiceTest {

  @InjectMocks
  private PostCommentCommandService postCommentCommandService;

  @Mock
  private PostQueryPort postQueryPort;

  @Mock
  private PostCommentQueryPort postCommentQueryPort;

  @Mock
  private PostCommentCommandPort postCommentCommandPort;

  @Mock
  private PostCommentLikerCommandPort postCommentLikerCommandPort;

  @Mock
  private UserProfileQueryPort userProfileQueryPort;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @Nested
  @DisplayName("댓글 생성 테스트")
  class CreatePostCommentTest {

    /*[Case #1] 댓글 생성 성공 - 새로운 댓글 생성*/
    @DisplayName("댓글 생성 성공 - 새로운 댓글이 생성되어야 한다")
    @Test
    public void createPostComment_shouldCreateComment_whenPostIsActive() throws Exception {
      //given
      CreatePostCommentCommand command = new CreatePostCommentCommand(
          "post1",
          "user1",
          "comment content"
      );

      Post post = Post.newPost("user2", "content");
      when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.of(post));

      UserPreviewProjection userPreview = new UserPreviewProjection() {

        @Override
        public String getNickname() {
          return "nickname";
        }

        @Override
        public String getProfileImageUrl() {
          return "/image.jpg";
        }
      };

      when(userProfileQueryPort.findUserPreviewByUserId(anyString())).thenReturn(userPreview);

      //when
      CreatePostCommentApiResponse result = postCommentCommandService.createPostComment(command);

      //then
      assertAll(
          () -> assertThat(result.userId()).isEqualTo("user1"),
          () -> assertThat(result.content()).isEqualTo("comment content")
      );

      verify(postCommentCommandPort).savePostComment(any(PostComment.class));
      verify(applicationEventPublisher).publishEvent(any(NotificationPublishCommand.class));
    }

    /*[Case #2] 댓글 생성 실패 - 삭제된 게시글*/
    @DisplayName("댓글 생성 실패 - 삭제된 게시글인 경우 예외가 발생해야 한다")
    @Test
    public void createPostComment_shouldThrowException_whenPostIsDeleted() throws Exception {
      //given
      CreatePostCommentCommand command = new CreatePostCommentCommand(
          "post1",
          "user1",
          "comment content"
      );

      Post post = Post.newPost("user2", "content");
      post.markAsDeleted();
      when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.of(post));

      //when & then
      assertThrows(PostException.class,
          () -> postCommentCommandService.createPostComment(command));
    }

    /*[Case #3] 댓글 생성 실패 - 존재하지 않는 게시글*/
    @DisplayName("댓글 생성 실패 - 존재하지 않는 게시글인 경우 예외가 발생해야 한다")
    @Test
    public void createPostComment_shouldThrowException_whenPostNotFound() throws Exception {
      //given
      CreatePostCommentCommand command = new CreatePostCommentCommand(
          "post1",
          "user1",
          "comment content"
      );

      when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.empty());

      //when & then
      assertThrows(PostException.class,
          () -> postCommentCommandService.createPostComment(command));
    }
  }

  @Nested
  @DisplayName("댓글 삭제 테스트")
  class SoftDeletePostCommentTest {

    /*[Case #1] 댓글 삭제 성공 - DELETED 상태로 변경*/
    @DisplayName("댓글 삭제 성공 - 댓글 상태가 DELETED로 변경되어야 한다")
    @Test
    public void softDeletePostComment_shouldChangeStatusToDeleted() throws Exception {
      //given
      DeletePostCommentCommand command = new DeletePostCommentCommand(
          "user1",
          "post1",
          "comment1"
      );

      PostComment comment = PostComment.newComment("post1", "user1", "content");
      when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(
          Optional.of(comment));

      when(postQueryPort.fetchPostStatusByPostId(command.getPostId())).thenReturn(
          Optional.of(PostStatus.ACTIVE));

      //when
      postCommentCommandService.softDeletePostComment(command);

      //then
      verify(postCommentCommandPort).updatePostCommentStatus(anyString(),
          any(PostCommentStatus.class));
    }

    /*[Case #2] 댓글 삭제 실패 - 존재하지 않는 댓글*/
    @DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글인 경우 예외가 발생해야 한다")
    @Test
    public void softDeletePostComment_shouldThrowException_whenCommentNotFound() throws Exception {
      //given
      DeletePostCommentCommand command = new DeletePostCommentCommand(
          "post1",
          "comment1",
          "user1"
      );

      when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(Optional.empty());

      //when & then
      assertThrows(PostCommentException.class,
          () -> postCommentCommandService.softDeletePostComment(command));
    }

    /*[Case #3] 댓글 삭제 실패 - 작성자가 아닌 경우*/
    @DisplayName("댓글 삭제 실패 - 작성자가 아닌 경우 예외가 발생해야 한다")
    @Test
    public void softDeletePostComment_shouldThrowException_whenUserNotWriter() throws Exception {
      //given
      DeletePostCommentCommand command = new DeletePostCommentCommand(
          "post1",
          "comment1",
          "user2"
      );

      PostComment comment = PostComment.newComment("post1", "user1", "content");
      when(postCommentQueryPort.fetchById(command.getCommentId())).thenReturn(
          Optional.of(comment));

      when(postQueryPort.fetchPostStatusByPostId(command.getPostId())).thenReturn(
          Optional.of(PostStatus.ACTIVE));

      //when & then
      assertThrows(PostCommentException.class,
          () -> postCommentCommandService.softDeletePostComment(command));
    }
  }

  @Nested
  @DisplayName("게시글 댓글 일괄 삭제 테스트")
  class DeleteAllCommentsTest {

    /*[Case #1] 게시글의 모든 댓글 삭제*/
    @DisplayName("게시글의 모든 댓글이 삭제되어야 한다")
    @Test
    public void deleteAllCommentsAndLikesByPostId_shouldDeleteAllComments() throws Exception {
      //given
      String postId = "post1";

      //when
      postCommentCommandService.deleteAllCommentsAndLikesByPostId(postId);

      //then
      verify(postCommentCommandPort).updateAllCommentStatusByPostId(postId,
          PostCommentStatus.DELETED);
      verify(postCommentLikerCommandPort).deleteAllByPostId(postId);
    }
  }
}
