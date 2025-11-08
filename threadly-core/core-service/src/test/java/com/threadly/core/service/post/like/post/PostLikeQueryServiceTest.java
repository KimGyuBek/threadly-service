package com.threadly.core.service.post.like.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.port.post.in.like.post.query.dto.GetPostLikersQuery;
import com.threadly.core.port.post.in.like.post.query.dto.PostLiker;
import com.threadly.core.port.post.out.like.post.PostLikeQueryPort;
import com.threadly.core.port.post.out.like.post.PostLikerProjection;
import com.threadly.core.service.post.validator.PostValidator;
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
 * PostLikeQueryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostLikeQueryServiceTest {

  @InjectMocks
  private PostLikeQueryService postLikeQueryService;

  @Mock
  private PostValidator postValidator;

  @Mock
  private PostLikeQueryPort postLikeQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 게시글 좋아요 사용자 목록 조회*/
    @Order(1)
    @DisplayName("1. 게시글 좋아요 사용자 목록이 커서 기반으로 조회되는지 검증")
    @Test
    void getPostLikers_shouldReturnPagedResponse_whenPostExists() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();
      GetPostLikersQuery query = new GetPostLikersQuery("post-1", now, "cursor-1", 1);

      PostLikerProjection projection1 = new PostLikerProjection() {
        @Override
        public String getLikerId() {
          return "liker-1";
        }

        @Override
        public String getLikerNickname() {
          return "사용자1";
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
          return now.minusSeconds(10);
        }
      };

      PostLikerProjection projection2 = new PostLikerProjection() {
        @Override
        public String getLikerId() {
          return "liker-2";
        }

        @Override
        public String getLikerNickname() {
          return "사용자2";
        }

        @Override
        public String getLikerProfileImageUrl() {
          return "/user-2.png";
        }

        @Override
        public String getLikerBio() {
          return "bio";
        }

        @Override
        public LocalDateTime getLikedAt() {
          return now.minusSeconds(20);
        }
      };

      when(postValidator.validateAccessibleStatusById(query.getPostId()))
          .thenReturn(PostStatus.ACTIVE);
      when(postLikeQueryPort.fetchPostLikersBeforeCreatedAt(
          query.getPostId(),
          query.getCursorLikedAt(),
          query.getCursorLikerId(),
          query.getLimit() + 1
      )).thenReturn(List.of(projection1, projection2));

      //when
      CursorPageApiResponse<PostLiker> response =
          postLikeQueryService.getPostLikers(query);

      //then
      verify(postValidator).validateAccessibleStatusById(query.getPostId());
      verify(postLikeQueryPort).fetchPostLikersBeforeCreatedAt(
          query.getPostId(),
          query.getCursorLikedAt(),
          query.getCursorLikerId(),
          query.getLimit() + 1
      );

      assertThat(response.content()).hasSize(1);
      PostLiker liker = response.content().getFirst();
      assertThat(liker.liker().userId()).isEqualTo("liker-1");
      assertThat(liker.liker().profileImageUrl()).isEqualTo("/");

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

    /*[Case #1] 존재하지 않는 게시글 좋아요 목록 조회*/
    @Order(1)
    @DisplayName("1. 게시글이 존재하지 않는 경우 예외가 발생하는지 검증")
    @Test
    void getPostLikers_shouldThrow_whenPostNotFound() throws Exception {
      //given
      GetPostLikersQuery query = new GetPostLikersQuery("post-unknown", null, null, 10);
      doThrow(new PostException(ErrorCode.POST_NOT_FOUND))
          .when(postValidator).validateAccessibleStatusById(query.getPostId());

      //when & then
      assertThatThrownBy(() -> postLikeQueryService.getPostLikers(query))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }
  }
}
