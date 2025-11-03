package com.threadly.core.service.post;

import static org.mockito.Mockito.verify;

import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.port.post.in.command.dto.PostCascadeCleanupPublishCommand;
import com.threadly.core.port.post.out.comment.PostCommentCommandPort;
import com.threadly.core.port.post.out.image.PostImageCommandPort;
import com.threadly.core.port.post.out.like.comment.PostCommentLikerCommandPort;
import com.threadly.core.port.post.out.like.post.PostLikeCommandPort;
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
 * PostCleanupCommandService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostCleanupCommandServiceTest {

  @InjectMocks
  private PostCleanupCommandService postCleanupCommandService;

  @Mock
  private PostImageCommandPort postImageCommandPort;

  @Mock
  private PostLikeCommandPort postLikeCommandPort;

  @Mock
  private PostCommentCommandPort postCommentCommandPort;

  @Mock
  private PostCommentLikerCommandPort postCommentLikerCommandPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 게시글 연관 데이터 정리*/
    @Order(1)
    @DisplayName("1. 게시글 연관 데이터가 모두 정리되는지 검증")
    @Test
    void cleanupAssociation_shouldCleanupAllResources() throws Exception {
      //given
      PostCascadeCleanupPublishCommand command = new PostCascadeCleanupPublishCommand("post-1");

      //when
      postCleanupCommandService.cleanupAssociation(command);

      //then
      verify(postImageCommandPort).updateStatus(command.postId(), ImageStatus.DELETED);
      verify(postLikeCommandPort).deleteAllByPostId(command.postId());
      verify(postCommentCommandPort).updateAllCommentStatusByPostId(command.postId(),
          PostCommentStatus.DELETED);
      verify(postCommentLikerCommandPort).deleteAllByPostId(command.postId());
    }
  }
}
