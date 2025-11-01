package com.threadly.core.service.post.like.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.core.domain.notification.NotificationType;
import com.threadly.core.domain.notification.metadata.PostLikeMeta;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostLike;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.port.post.in.like.post.command.dto.LikePostApiResponse;
import com.threadly.core.port.post.in.like.post.command.dto.LikePostCommand;
import com.threadly.core.port.post.out.PostQueryPort;
import com.threadly.core.port.post.out.like.post.PostLikeCommandPort;
import com.threadly.core.port.post.out.like.post.PostLikeQueryPort;
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
 * PostLikeCommandService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostLikeCommandServiceTest {

  @InjectMocks
  private PostLikeCommandService postLikeCommandService;

  @Mock
  private PostQueryPort postQueryPort;

  @Mock
  private PostLikeQueryPort postLikeQueryPort;

  @Mock
  private PostLikeCommandPort postLikeCommandPort;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @Order(1)
  @Nested
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 좋아요 테스트")
  class LikePostTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 좋아요하지 않은 게시글 좋아요 요청*/
      @Order(1)
      @DisplayName("1. 사용자가 처음 좋아요하는 경우 좋아요가 저장되는지 검증")
      @Test
      void likePost_shouldCreateLike_whenUserNotLikedBefore() throws Exception {
        //given
        LikePostCommand command = new LikePostCommand("post-1", "user-1");
        Post post = Post.newTestPost("post-1", "owner-1", "content", 0, PostStatus.ACTIVE);

        when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.of(post));
        when(postLikeQueryPort.existsByPostIdAndUserId(command.getPostId(), command.getUserId()))
            .thenReturn(false);
        when(postLikeQueryPort.fetchLikeCountByPostId(command.getPostId())).thenReturn(5L);

        ArgumentCaptor<PostLike> likeCaptor = ArgumentCaptor.forClass(PostLike.class);
        ArgumentCaptor<NotificationPublishCommand> notificationCaptor =
            ArgumentCaptor.forClass(NotificationPublishCommand.class);

        //when
        LikePostApiResponse response = postLikeCommandService.likePost(command);

        //then
        verify(postLikeCommandPort).createPostLike(likeCaptor.capture());
        PostLike savedLike = likeCaptor.getValue();
        assertThat(savedLike.getPostId()).isEqualTo(command.getPostId());
        assertThat(savedLike.getUserId()).isEqualTo(command.getUserId());

        verify(applicationEventPublisher).publishEvent(notificationCaptor.capture());
        NotificationPublishCommand notification = notificationCaptor.getValue();
        assertThat(notification.receiverId()).isEqualTo(post.getUserId());
        assertThat(notification.actorId()).isEqualTo(command.getUserId());
        assertThat(notification.notificationType()).isEqualTo(NotificationType.POST_LIKE);
        assertThat(((PostLikeMeta) notification.notificationMetaData()).postId())
            .isEqualTo(command.getPostId());

        assertThat(response.postId()).isEqualTo(command.getPostId());
        assertThat(response.likeCount()).isEqualTo(5L);
      }

      /*[Case #2] 이미 좋아요한 게시글 좋아요 요청*/
      @Order(2)
      @DisplayName("2. 이미 좋아요한 경우 중복 저장되지 않는지 검증")
      @Test
      void likePost_shouldSkipSaving_whenUserAlreadyLiked() throws Exception {
        //given
        LikePostCommand command = new LikePostCommand("post-1", "user-1");
        Post post = Post.newTestPost("post-1", "owner-1", "content", 0, PostStatus.ACTIVE);

        when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.of(post));
        when(postLikeQueryPort.existsByPostIdAndUserId(command.getPostId(), command.getUserId()))
            .thenReturn(true);
        when(postLikeQueryPort.fetchLikeCountByPostId(command.getPostId())).thenReturn(3L);

        //when
        LikePostApiResponse response = postLikeCommandService.likePost(command);

        //then
        verify(postLikeCommandPort, never()).createPostLike(any(PostLike.class));
        verify(applicationEventPublisher, never()).publishEvent(any(NotificationPublishCommand.class));
        assertThat(response.postId()).isEqualTo(command.getPostId());
        assertThat(response.likeCount()).isEqualTo(3L);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 존재하지 않는 게시글 좋아요 요청*/
      @Order(1)
      @DisplayName("1. 게시글이 없는 경우 예외가 발생하는지 검증")
      @Test
      void likePost_shouldThrow_whenPostNotFound() throws Exception {
        //given
        LikePostCommand command = new LikePostCommand("post-unknown", "user-1");
        when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> postLikeCommandService.likePost(command))
            .isInstanceOf(PostException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_NOT_FOUND);
      }

      /*[Case #2] 좋아요가 불가능한 상태의 게시글*/
      @Order(2)
      @DisplayName("2. 좋아요가 불가능한 게시글인 경우 예외가 발생하는지 검증")
      @Test
      void likePost_shouldThrow_whenPostNotLikable() throws Exception {
        //given
        LikePostCommand command = new LikePostCommand("post-1", "user-1");
        Post post = Post.newTestPost("post-1", "owner-1", "content", 0, PostStatus.DELETED);
        when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.of(post));

        //when & then
        assertThatThrownBy(() -> postLikeCommandService.likePost(command))
            .isInstanceOf(PostException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_LIKE_NOT_ALLOWED);
      }
    }
  }

  @Order(2)
  @Nested
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 좋아요 취소 테스트")
  class CancelLikePostTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 좋아요한 게시글 좋아요 취소*/
      @Order(1)
      @DisplayName("1. 좋아요한 상태에서 취소 시 삭제되는지 검증")
      @Test
      void cancelLikePost_shouldDeleteLike_whenUserLikedBefore() throws Exception {
        //given
        LikePostCommand command = new LikePostCommand("post-1", "user-1");
        Post post = Post.newTestPost("post-1", "owner-1", "content", 0, PostStatus.ACTIVE);

        when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.of(post));
        when(postLikeQueryPort.existsByPostIdAndUserId(command.getPostId(), command.getUserId()))
            .thenReturn(true);
        when(postLikeQueryPort.fetchLikeCountByPostId(command.getPostId())).thenReturn(1L);

        //when
        LikePostApiResponse response = postLikeCommandService.cancelLikePost(command);

        //then
        verify(postLikeCommandPort).deleteByPostIdAndUserId(command.getPostId(), command.getUserId());
        assertThat(response.postId()).isEqualTo(command.getPostId());
        assertThat(response.likeCount()).isEqualTo(1L);
      }

      /*[Case #2] 좋아요하지 않은 게시글 좋아요 취소*/
      @Order(2)
      @DisplayName("2. 좋아요하지 않은 상태에서 취소 시 삭제가 호출되지 않는지 검증")
      @Test
      void cancelLikePost_shouldSkipDeletion_whenUserNotLiked() throws Exception {
        //given
        LikePostCommand command = new LikePostCommand("post-1", "user-1");
        Post post = Post.newTestPost("post-1", "owner-1", "content", 0, PostStatus.ACTIVE);

        when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.of(post));
        when(postLikeQueryPort.existsByPostIdAndUserId(command.getPostId(), command.getUserId()))
            .thenReturn(false);
        when(postLikeQueryPort.fetchLikeCountByPostId(command.getPostId())).thenReturn(0L);

        //when
        LikePostApiResponse response = postLikeCommandService.cancelLikePost(command);

        //then
        verify(postLikeCommandPort, never())
            .deleteByPostIdAndUserId(command.getPostId(), command.getUserId());
        assertThat(response.postId()).isEqualTo(command.getPostId());
        assertThat(response.likeCount()).isEqualTo(0L);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 존재하지 않는 게시글 좋아요 취소*/
      @Order(1)
      @DisplayName("1. 게시글이 없는 경우 예외가 발생하는지 검증")
      @Test
      void cancelLikePost_shouldThrow_whenPostNotFound() throws Exception {
        //given
        LikePostCommand command = new LikePostCommand("post-unknown", "user-1");
        when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> postLikeCommandService.cancelLikePost(command))
            .isInstanceOf(PostException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_NOT_FOUND);
      }

      /*[Case #2] 좋아요 취소가 불가능한 게시글*/
      @Order(2)
      @DisplayName("2. 좋아요 취소가 불가능한 게시글인 경우 예외가 발생하는지 검증")
      @Test
      void cancelLikePost_shouldThrow_whenPostNotLikable() throws Exception {
        //given
        LikePostCommand command = new LikePostCommand("post-1", "user-1");
        Post post = Post.newTestPost("post-1", "owner-1", "content", 0, PostStatus.DELETED);
        when(postQueryPort.fetchById(command.getPostId())).thenReturn(Optional.of(post));

        //when & then
        assertThatThrownBy(() -> postLikeCommandService.cancelLikePost(command))
            .isInstanceOf(PostException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_LIKE_NOT_ALLOWED);
      }
    }
  }
}
