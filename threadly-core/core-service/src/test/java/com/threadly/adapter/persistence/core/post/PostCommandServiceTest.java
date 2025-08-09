package com.threadly.adapter.persistence.core.post;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.core.port.post.update.UpdatePostPort;
import com.threadly.core.port.post.view.RecordPostViewPort;
import com.threadly.commons.properties.TtlProperties;
import com.threadly.core.service.post.PostCommandService;
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
 * PostCommandService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostCommandServiceTest {

  @InjectMocks
  private PostCommandService postCommandService;

  @Mock
  private RecordPostViewPort recordPostViewPort;

  @Mock
  private UpdatePostPort updatePostPort;

  @Mock
  private TtlProperties ttlProperties;

  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 조회수 증가 테스트")
  @Nested
  class IncreaseViewCountTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 게시글 조회 수 증가 - 사용자의 조회 기록이 없을 경우 요청 시 검증*/
      @Order(1)
      @DisplayName("1. 사용자의 조회 기록이 없을 경우 요청 시 실행되는지 검증")
      @Test
      public void increaseViewCount_shouldSuccess_whenUserRecordNotExists() throws Exception {
        //given
        String postId = "post1";
        String userId = "user1";

        when(recordPostViewPort.existsPostView(postId, userId)).thenReturn(false);

        //when

        postCommandService.increaseViewCount(postId, userId);

        //then
        verify(updatePostPort).increaseViewCount(postId);
        verify(recordPostViewPort).recordPostView(postId, userId, ttlProperties.getPostViewSeconds());
      }

      @Order(2)
      @DisplayName("2. 사용자의 조회 기록이 있을 경우 요청 시 실행되지 않는지 검증")
      @Test
      public void increaseViewCount_shouldSuccess_whenUserRecordExists() throws Exception {
        //given
        String postId = "post1";
        String userId = "user1";

        when(recordPostViewPort.existsPostView(postId, userId)).thenReturn(true);

        //when

        postCommandService.increaseViewCount(postId, userId);

        //then
        verify(updatePostPort, never()).increaseViewCount(postId);
        verify(recordPostViewPort).recordPostView(postId, userId, ttlProperties.getPostViewSeconds());
      }
    }
  }


}