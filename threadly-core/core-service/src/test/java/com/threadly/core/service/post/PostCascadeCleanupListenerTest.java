package com.threadly.core.service.post;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.threadly.core.port.post.in.command.PostCleanupCommandUseCase;
import com.threadly.core.port.post.in.command.dto.PostCascadeCleanupPublishCommand;
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
 * PostCascadeCleanupListener 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostCascadeCleanupListenerTest {

  @InjectMocks
  private PostCascadeCleanupListener postCascadeCleanupListener;

  @Mock
  private PostCleanupCommandUseCase postCleanupCommandUseCase;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 게시글 연관 데이터 정리 이벤트*/
    @Order(1)
    @DisplayName("1. 이벤트를 수신하면 정리 작업이 실행되는지 검증")
    @Test
    void onPostDeleted_shouldInvokeCleanup() throws Exception {
      //given
      PostCascadeCleanupPublishCommand command = new PostCascadeCleanupPublishCommand("post-1");

      //when
      postCascadeCleanupListener.onPostDeleted(command);

      //then
      verify(postCleanupCommandUseCase).cleanupAssociation(command);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 정리 작업 중 예외 발생*/
    @Order(1)
    @DisplayName("1. 정리 작업에서 예외가 발생해도 추가 동작이 없는지 검증")
    @Test
    void onPostDeleted_shouldHandleExceptionGracefully() throws Exception {
      //given
      PostCascadeCleanupPublishCommand command = new PostCascadeCleanupPublishCommand("post-1");

      doThrow(new RuntimeException("cleanup failed"))
          .when(postCleanupCommandUseCase).cleanupAssociation(command);

      //when
      postCascadeCleanupListener.onPostDeleted(command);

      //then
      verify(postCleanupCommandUseCase).cleanupAssociation(command);
      verifyNoMoreInteractions(postCleanupCommandUseCase);
    }
  }
}
