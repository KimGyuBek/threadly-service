package com.threadly.core.service.post.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.search.SearchException;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.search.dto.PostSearchItem;
import com.threadly.core.port.post.in.search.dto.PostSearchQuery;
import com.threadly.core.port.post.in.search.dto.PostSearchSortType;
import com.threadly.core.port.post.out.image.PostImageQueryPort;
import com.threadly.core.port.post.out.image.projection.PostImageProjection;
import com.threadly.core.port.post.out.sesarch.PostSearchProjection;
import com.threadly.core.port.post.out.sesarch.SearchPostQueryPort;
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
 * SearchPostQueryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class SearchPostQueryServiceTest {

  @InjectMocks
  private SearchPostQueryService searchPostQueryService;

  @Mock
  private SearchPostQueryPort searchPostQueryPort;

  @Mock
  private PostImageQueryPort postImageQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 게시글 검색*/
    @Order(1)
    @DisplayName("1. 게시글 검색 결과가 커서 기반으로 조회되는지 검증")
    @Test
    void searchByKeyword_shouldReturnPagedResponse_whenSortTypeSupported() throws Exception {
      //given
      LocalDateTime now = LocalDateTime.now();
      PostSearchQuery query = new PostSearchQuery(
          "user-1",
          "threadly",
          PostSearchSortType.RECENT,
          "cursor-post",
          now,
          1
      );

      PostSearchProjection projection1 = new PostSearchProjection() {
        @Override
        public String getPostId() {
          return "post-1";
        }

        @Override
        public String getContent() {
          return "게시글1";
        }

        @Override
        public long getLikeCount() {
          return 10;
        }

        @Override
        public long getCommentCount() {
          return 3;
        }

        @Override
        public boolean isLiked() {
          return true;
        }

        @Override
        public long getViewCount() {
          return 100;
        }

        @Override
        public LocalDateTime getPostedAt() {
          return now.minusSeconds(1);
        }

        @Override
        public String getUserId() {
          return "author-1";
        }

        @Override
        public String getUserNickname() {
          return "작성자1";
        }

        @Override
        public String getUserProfileImageUrl() {
          return null;
        }
      };

      PostSearchProjection projection2 = new PostSearchProjection() {
        @Override
        public String getPostId() {
          return "post-2";
        }

        @Override
        public String getContent() {
          return "게시글2";
        }

        @Override
        public long getLikeCount() {
          return 5;
        }

        @Override
        public long getCommentCount() {
          return 1;
        }

        @Override
        public boolean isLiked() {
          return false;
        }

        @Override
        public long getViewCount() {
          return 50;
        }

        @Override
        public LocalDateTime getPostedAt() {
          return now.minusSeconds(2);
        }

        @Override
        public String getUserId() {
          return "author-2";
        }

        @Override
        public String getUserNickname() {
          return "작성자2";
        }

        @Override
        public String getUserProfileImageUrl() {
          return "/author-2.png";
        }
      };

      PostImageProjection image1 = new PostImageProjection() {
        @Override
        public String getPostId() {
          return "post-1";
        }

        @Override
        public String getImageId() {
          return "image-1";
        }

        @Override
        public String getImageUrl() {
          return "/post-1.png";
        }

        @Override
        public int getImageOrder() {
          return 0;
        }
      };

      when(searchPostQueryPort.searchPostByKeyword(
          query.userId(),
          query.keyword(),
          query.sortType(),
          query.cursorPostId(),
          query.cursorPostedAt(),
          query.limit() + 1
      )).thenReturn(List.of(projection1, projection2));
      when(postImageQueryPort.findVisibleByPostIds(List.of("post-1", "post-2")))
          .thenReturn(List.of(image1));

      //when
      CursorPageApiResponse<PostSearchItem> response = searchPostQueryService.searchByKeyword(query);

      //then
      verify(searchPostQueryPort).searchPostByKeyword(
          query.userId(),
          query.keyword(),
          query.sortType(),
          query.cursorPostId(),
          query.cursorPostedAt(),
          query.limit() + 1
      );
      verify(postImageQueryPort).findVisibleByPostIds(List.of("post-1", "post-2"));

      assertThat(response.content()).hasSize(1);
      PostSearchItem first = response.content().getFirst();
      assertThat(first.postId()).isEqualTo("post-1");
      assertThat(first.author().profileImageUrl()).isEqualTo("/");
      assertThat(first.images()).hasSize(1);
      assertThat(first.images().getFirst().imageUrl()).isEqualTo("/post-1.png");
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 지원하지 않는 정렬 타입*/
    @Order(1)
    @DisplayName("1. 지원하지 않는 정렬 방식이면 예외가 발생하는지 검증")
    @Test
    void searchByKeyword_shouldThrow_whenSortTypeUnsupported() throws Exception {
      //given
      PostSearchQuery query = new PostSearchQuery(
          "user-1",
          "threadly",
          PostSearchSortType.RELEVANCE,
          null,
          null,
          10
      );

      //when & then
      assertThatThrownBy(() -> searchPostQueryService.searchByKeyword(query))
          .isInstanceOf(SearchException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_SEARCH_SORT_TYPE_INVALID);
    }
  }
}
