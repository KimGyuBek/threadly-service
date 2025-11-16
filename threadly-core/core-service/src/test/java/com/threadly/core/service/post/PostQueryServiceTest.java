package com.threadly.core.service.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.image.ImageStatus;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementApiResponse;
import com.threadly.core.port.post.in.query.dto.GetPostEngagementQuery;
import com.threadly.core.port.post.in.query.dto.GetPostQuery;
import com.threadly.core.port.post.in.query.dto.GetPostsQuery;
import com.threadly.core.port.post.in.query.dto.PostDetails;
import com.threadly.core.port.post.out.PostQueryPort;
import com.threadly.core.port.post.out.image.PostImageQueryPort;
import com.threadly.core.port.post.out.image.projection.PostImageProjection;
import com.threadly.core.port.post.out.projection.PostDetailProjection;
import com.threadly.core.port.post.out.projection.PostEngagementProjection;
import com.threadly.core.service.follow.validator.FollowValidator;
import com.threadly.core.service.post.validator.PostValidator;
import com.threadly.core.service.user.validator.UserValidator;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

/**
 * PostQueryService 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostQueryServiceTest {

  @InjectMocks
  private PostQueryService postQueryService;

  @Mock
  private PostValidator postValidator;

  @Mock
  private UserValidator userValidator;

  @Mock
  private PostQueryPort postQueryPort;

  @Mock
  private PostImageQueryPort postImageQueryPort;

  @Mock
  private FollowValidator followValidator;

  @Order(1)
  @Nested
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 목록 조회 테스트")
  class GetUserVisiblePostListTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 사용자 노출 게시글 목록 조회*/
      @Order(1)
      @DisplayName("1. 게시글 목록이 커서 기반으로 조회되는지 검증")
      @Test
      void getUserVisiblePostListByCursor_shouldReturnPagedResponse() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        GetPostsQuery query = new GetPostsQuery(
            "user-1", now, "post-cursor", 1
        );

        PostDetailProjection projection1 = new PostDetailProjection() {
          @Override
          public String getPostId() {
            return "post-1";
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

          @Override
          public String getContent() {
            return "첫번째 게시글";
          }

          @Override
          public long getViewCount() {
            return 11L;
          }

          @Override
          public LocalDateTime getPostedAt() {
            return now.minusSeconds(5);
          }

          @Override
          public PostStatus getPostStatus() {
            return PostStatus.ACTIVE;
          }

          @Override
          public long getLikeCount() {
            return 2L;
          }

          @Override
          public long getCommentCount() {
            return 3L;
          }

          @Override
          public boolean isLiked() {
            return true;
          }
        };

        PostDetailProjection projection2 = new PostDetailProjection() {
          @Override
          public String getPostId() {
            return "post-2";
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
            return "/author2.png";
          }

          @Override
          public String getContent() {
            return "두번째 게시글";
          }

          @Override
          public long getViewCount() {
            return 22L;
          }

          @Override
          public LocalDateTime getPostedAt() {
            return now.minusSeconds(10);
          }

          @Override
          public PostStatus getPostStatus() {
            return PostStatus.ACTIVE;
          }

          @Override
          public long getLikeCount() {
            return 4L;
          }

          @Override
          public long getCommentCount() {
            return 5L;
          }

          @Override
          public boolean isLiked() {
            return false;
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
            return "/post-1-1.png";
          }

          @Override
          public int getImageOrder() {
            return 0;
          }
        };

        PostImageProjection image2 = new PostImageProjection() {
          @Override
          public String getPostId() {
            return "post-1";
          }

          @Override
          public String getImageId() {
            return "image-2";
          }

          @Override
          public String getImageUrl() {
            return "/post-1-2.png";
          }

          @Override
          public int getImageOrder() {
            return 1;
          }
        };

        when(postQueryPort.fetchUserVisiblePostsByCursor(
            query.getUserId(),
            query.getCursorPostedAt(),
            query.getCursorPostId(),
            query.getLimit() + 1
        )).thenReturn(List.of(projection1, projection2));
        when(postImageQueryPort.findAllByPostIdAndStatus("post-1", ImageStatus.CONFIRMED))
            .thenReturn(List.of(image1, image2));
        when(postImageQueryPort.findAllByPostIdAndStatus("post-2", ImageStatus.CONFIRMED))
            .thenReturn(List.of());

        //when
        CursorPageApiResponse<PostDetails> response =
            postQueryService.getUserVisiblePosts(query);

        //then
        verify(postQueryPort).fetchUserVisiblePostsByCursor(
            query.getUserId(),
            query.getCursorPostedAt(),
            query.getCursorPostId(),
            query.getLimit() + 1
        );

        assertThat(response.content()).hasSize(1);
        PostDetails firstPost = response.content().getFirst();
        assertThat(firstPost.postId()).isEqualTo("post-1");
        assertThat(firstPost.author().userId()).isEqualTo("author-1");
        assertThat(firstPost.author().profileImageUrl()).isEqualTo("default");
        assertThat(firstPost.images()).hasSize(2);
        assertThat(firstPost.images().getFirst().imageUrl()).isEqualTo("/post-1-1.png");

        CursorPageApiResponse.NextCursor nextCursor = response.nextCursor();
        assertThat(nextCursor.cursorId()).isEqualTo("post-1");
        assertThat(nextCursor.cursorTimestamp()).isEqualTo(projection1.getPostedAt());
      }
    }
  }

  @Order(2)
  @Nested
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 상세 조회 테스트")
  class GetPostTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 게시글 상세 정보 조회*/
      @Order(1)
      @DisplayName("1. 게시글 상세 정보가 정상 반환되는지 검증")
      @Test
      void getPost_shouldReturnDetails_whenPostActive() throws Exception {
        //given
        LocalDateTime postedAt = LocalDateTime.now();
        GetPostQuery query = new GetPostQuery("post-1", "viewer-1");

        PostDetailProjection projection = new PostDetailProjection() {
          @Override
          public String getPostId() {
            return "post-1";
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

          @Override
          public String getContent() {
            return "게시글 내용";
          }

          @Override
          public long getViewCount() {
            return 101L;
          }

          @Override
          public LocalDateTime getPostedAt() {
            return postedAt;
          }

          @Override
          public PostStatus getPostStatus() {
            return PostStatus.ACTIVE;
          }

          @Override
          public long getLikeCount() {
            return 20L;
          }

          @Override
          public long getCommentCount() {
            return 5L;
          }

          @Override
          public boolean isLiked() {
            return true;
          }
        };

        PostImageProjection imageProjection = new PostImageProjection() {
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
            return "/post.png";
          }

          @Override
          public int getImageOrder() {
            return 0;
          }
        };

        when(postValidator.getPostDetailsProjectionOrElseThrow(query.getPostId(), query.getUserId()))
            .thenReturn(projection);
        doNothing().when(postValidator).validateAccessibleStatus(PostStatus.ACTIVE);
        when(postImageQueryPort.findAllByPostIdAndStatus(query.getPostId(), ImageStatus.CONFIRMED))
            .thenReturn(List.of(imageProjection));

        //when
        PostDetails details = postQueryService.getPost(query);

        //then
        verify(postValidator).getPostDetailsProjectionOrElseThrow(query.getPostId(),
            query.getUserId());
        verify(postValidator).validateAccessibleStatus(PostStatus.ACTIVE);
        verify(postImageQueryPort).findAllByPostIdAndStatus(query.getPostId(),
            ImageStatus.CONFIRMED);

        assertThat(details.postId()).isEqualTo("post-1");
        assertThat(details.author().userId()).isEqualTo("author-1");
        assertThat(details.author().profileImageUrl()).isEqualTo("default");
        assertThat(details.images()).hasSize(1);
        assertThat(details.images().getFirst().imageUrl()).isEqualTo("/post.png");
        assertThat(details.viewCount()).isEqualTo(101L);
        assertThat(details.likeCount()).isEqualTo(20L);
        assertThat(details.commentCount()).isEqualTo(5L);
        assertThat(details.liked()).isTrue();
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 존재하지 않는 게시글 조회*/
      @Order(1)
      @DisplayName("1. 게시글이 존재하지 않는 경우 예외가 발생하는지 검증")
      @Test
      void getPost_shouldThrow_whenPostNotFound() throws Exception {
        //given
        GetPostQuery query = new GetPostQuery("post-unknown", "viewer-1");
        when(postValidator.getPostDetailsProjectionOrElseThrow(query.getPostId(), query.getUserId()))
            .thenThrow(new PostException(ErrorCode.POST_NOT_FOUND));

        //when & then
        assertThatThrownBy(() -> postQueryService.getPost(query))
            .isInstanceOf(PostException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_NOT_FOUND);
      }

      /*[Case #2] 삭제된 게시글 조회*/
      @Order(2)
      @DisplayName("2. 삭제된 게시글인 경우 예외가 발생하는지 검증")
      @Test
      void getPost_shouldThrow_whenPostDeleted() throws Exception {
        //given
        GetPostQuery query = new GetPostQuery("post-1", "viewer-1");
        PostDetailProjection projection = newStatusProjection(PostStatus.DELETED);
        when(postValidator.getPostDetailsProjectionOrElseThrow(query.getPostId(), query.getUserId()))
            .thenReturn(projection);
        doThrow(new PostException(ErrorCode.POST_ALREADY_DELETED))
            .when(postValidator).validateAccessibleStatus(PostStatus.DELETED);

        //when & then
        assertThatThrownBy(() -> postQueryService.getPost(query))
            .isInstanceOf(PostException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_ALREADY_DELETED);
      }

      /*[Case #3] 아카이브된 게시글 조회*/
      @Order(3)
      @DisplayName("3. 보관된 게시글인 경우 예외가 발생하는지 검증")
      @Test
      void getPost_shouldThrow_whenPostArchived() throws Exception {
        //given
        GetPostQuery query = new GetPostQuery("post-1", "viewer-1");
        PostDetailProjection projection = newStatusProjection(PostStatus.ARCHIVE);
        when(postValidator.getPostDetailsProjectionOrElseThrow(query.getPostId(), query.getUserId()))
            .thenReturn(projection);
        doThrow(new PostException(ErrorCode.POST_NOT_FOUND))
            .when(postValidator).validateAccessibleStatus(PostStatus.ARCHIVE);

        //when & then
        assertThatThrownBy(() -> postQueryService.getPost(query))
            .isInstanceOf(PostException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_NOT_FOUND);
      }

      /*[Case #4] 차단된 게시글 조회*/
      @Order(4)
      @DisplayName("4. 차단된 게시글인 경우 예외가 발생하는지 검증")
      @Test
      void getPost_shouldThrow_whenPostBlocked() throws Exception {
        //given
        GetPostQuery query = new GetPostQuery("post-1", "viewer-1");
        PostDetailProjection projection = newStatusProjection(PostStatus.BLOCKED);
        when(postValidator.getPostDetailsProjectionOrElseThrow(query.getPostId(), query.getUserId()))
            .thenReturn(projection);
        doThrow(new PostException(ErrorCode.POST_BLOCKED))
            .when(postValidator).validateAccessibleStatus(PostStatus.BLOCKED);

        //when & then
        assertThatThrownBy(() -> postQueryService.getPost(query))
            .isInstanceOf(PostException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_BLOCKED);
      }

      private PostDetailProjection newStatusProjection(PostStatus status) {
        return new PostDetailProjection() {
          @Override
          public String getPostId() {
            return "post-1";
          }

          @Override
          public String getUserId() {
            return "author-1";
          }

          @Override
          public String getUserNickname() {
            return "작성자";
          }

          @Override
          public String getUserProfileImageUrl() {
            return "/profile.png";
          }

          @Override
          public String getContent() {
            return "내용";
          }

          @Override
          public long getViewCount() {
            return 0;
          }

          @Override
          public LocalDateTime getPostedAt() {
            return LocalDateTime.now();
          }

          @Override
          public PostStatus getPostStatus() {
            return status;
          }

          @Override
          public long getLikeCount() {
            return 0;
          }

          @Override
          public long getCommentCount() {
            return 0;
          }

          @Override
          public boolean isLiked() {
            return false;
          }
        };
      }
    }
  }

  @Order(3)
  @Nested
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 참여도 조회 테스트")
  class GetPostEngagementTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 게시글 참여도 조회*/
      @Order(1)
      @DisplayName("1. 게시글 참여도 정보가 조회되는지 검증")
      @Test
      void getPostEngagement_shouldReturnProjection() throws Exception {
        //given
        GetPostEngagementQuery query = new GetPostEngagementQuery("post-1", "viewer-1");
        PostEngagementProjection projection = new PostEngagementProjection() {
          @Override
          public String getPostId() {
            return "post-1";
          }

          @Override
          public String getAuthorId() {
            return "author-1";
          }

          @Override
          public String getAuthorNickname() {
            return "작성자";
          }

          @Override
          public String getAuthorProfileImageUrl() {
            return "/profile.png";
          }

          @Override
          public String getContent() {
            return "내용";
          }

          @Override
          public long getLikeCount() {
            return 10L;
          }

          @Override
          public boolean isLiked() {
            return true;
          }
        };

        when(postQueryPort.fetchPostEngagementByPostIdAndUserId(query.getPostId(),
            query.getUserId()))
            .thenReturn(Optional.of(projection));

        //when
        GetPostEngagementApiResponse response = postQueryService.getPostEngagement(query);

        //then
        verify(postQueryPort)
            .fetchPostEngagementByPostIdAndUserId(query.getPostId(), query.getUserId());
        assertThat(response.postId()).isEqualTo("post-1");
        assertThat(response.authorId()).isEqualTo("author-1");
        assertThat(response.likeCount()).isEqualTo(10L);
        assertThat(response.liked()).isTrue();
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 존재하지 않는 게시글 참여도 조회*/
      @Order(1)
      @DisplayName("1. 게시글이 존재하지 않는 경우 예외가 발생하는지 검증")
      @Test
      void getPostEngagement_shouldThrow_whenPostNotFound() throws Exception {
        //given
        GetPostEngagementQuery query = new GetPostEngagementQuery("post-unknown", "viewer-1");
        when(postQueryPort.fetchPostEngagementByPostIdAndUserId(query.getPostId(),
            query.getUserId()))
            .thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> postQueryService.getPostEngagement(query))
            .isInstanceOf(PostException.class)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.POST_NOT_FOUND);
      }
    }
  }
}
