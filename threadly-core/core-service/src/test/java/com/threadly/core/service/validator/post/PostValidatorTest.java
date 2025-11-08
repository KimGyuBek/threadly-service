package com.threadly.core.service.validator.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.core.domain.post.Post;
import com.threadly.core.domain.post.PostStatus;
import com.threadly.core.port.post.out.PostQueryPort;
import com.threadly.core.port.post.out.projection.PostDetailProjection;
import java.time.LocalDateTime;
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
 * PostValidator 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class PostValidatorTest {

  @InjectMocks
  private PostValidator postValidator;

  @Mock
  private PostQueryPort postQueryPort;

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("게시글 조회")
  class GetPostOrThrowTest {

    /*[Case #1] 게시글 조회 성공*/
    @Order(1)
    @DisplayName("1. postId로 게시글 조회 시 정상적으로 반환되는지 검증")
    @Test
    void getPostOrThrow_shouldReturnPost_whenPostExists() throws Exception {
      //given
      String postId = "post-1";
      Post post = Post.newPost("user-1", "content");
      when(postQueryPort.fetchById(postId)).thenReturn(Optional.of(post));

      //when
      Post result = postValidator.getPostOrThrow(postId);

      //then
      assertThat(result).isNotNull();
      assertThat(result.getUserId()).isEqualTo("user-1");
      verify(postQueryPort).fetchById(postId);
    }

    /*[Case #2] 게시글 조회 실패 - 미존재*/
    @Order(2)
    @DisplayName("2. postId로 게시글 조회 시 존재하지 않으면 예외 발생")
    @Test
    void getPostOrThrow_shouldThrow_whenPostNotExists() throws Exception {
      //given
      String postId = "nonexistent-post";
      when(postQueryPort.fetchById(postId)).thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> postValidator.getPostOrThrow(postId))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }
  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("게시글 상태 조회")
  class GetPostStatusOrThrowTest {

    /*[Case #1] 게시글 상태 조회 성공*/
    @Order(1)
    @DisplayName("1. postId로 게시글 상태 조회 시 정상적으로 반환되는지 검증")
    @Test
    void getPostStatusOrThrow_shouldReturnStatus_whenPostExists() throws Exception {
      //given
      String postId = "post-1";
      when(postQueryPort.fetchPostStatusByPostId(postId))
          .thenReturn(Optional.of(PostStatus.ACTIVE));

      //when
      PostStatus result = postValidator.getPostStatusOrThrow(postId);

      //then
      assertThat(result).isEqualTo(PostStatus.ACTIVE);
      verify(postQueryPort).fetchPostStatusByPostId(postId);
    }

    /*[Case #2] 게시글 상태 조회 실패 - 미존재*/
    @Order(2)
    @DisplayName("2. postId로 게시글 상태 조회 시 존재하지 않으면 예외 발생")
    @Test
    void getPostStatusOrThrow_shouldThrow_whenPostNotExists() throws Exception {
      //given
      String postId = "nonexistent-post";
      when(postQueryPort.fetchPostStatusByPostId(postId)).thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> postValidator.getPostStatusOrThrow(postId))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }
  }

  @Order(3)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("게시글 수정 권한 검증")
  class ValidateUpdatableByTest {

    /*[Case #1] 작성자와 요청자 일치 - 정상*/
    @Order(1)
    @DisplayName("1. 작성자와 요청자가 일치하는 경우 예외가 발생하지 않음")
    @Test
    void validateUpdatableBy_shouldPass_whenAuthorMatchesRequester() throws Exception {
      //given
      String authorId = "user-1";
      String requesterId = "user-1";

      //when & then
      assertThatCode(() -> postValidator.validateUpdatableBy(authorId, requesterId))
          .doesNotThrowAnyException();
    }

    /*[Case #2] 작성자와 요청자 불일치 - 예외 발생*/
    @Order(2)
    @DisplayName("2. 작성자와 요청자가 일치하지 않는 경우 예외 발생")
    @Test
    void validateUpdatableBy_shouldThrow_whenAuthorNotMatchesRequester() throws Exception {
      //given
      String authorId = "user-1";
      String requesterId = "user-2";

      //when & then
      assertThatThrownBy(() -> postValidator.validateUpdatableBy(authorId, requesterId))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_UPDATE_FORBIDDEN);
    }
  }

  @Order(4)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("게시글 상세 조회")
  class GetPostDetailsProjectionOrElseThrowTest {

    /*[Case #1] 게시글 상세 조회 성공*/
    @Order(1)
    @DisplayName("1. postId와 userId로 게시글 상세 조회 시 정상적으로 반환되는지 검증")
    @Test
    void getPostDetailsProjectionOrElseThrow_shouldReturnProjection_whenPostExists()
        throws Exception {
      //given
      String postId = "post-1";
      String userId = "user-1";
      PostDetailProjection projection = createMockPostDetailProjection(postId, userId);

      when(postQueryPort.fetchPostDetailsByPostIdAndUserId(postId, userId))
          .thenReturn(Optional.of(projection));

      //when
      PostDetailProjection result = postValidator.getPostDetailsProjectionOrElseThrow(postId,
          userId);

      //then
      assertThat(result).isNotNull();
      assertThat(result.getPostId()).isEqualTo(postId);
      verify(postQueryPort).fetchPostDetailsByPostIdAndUserId(postId, userId);
    }

    /*[Case #2] 게시글 상세 조회 실패 - 미존재*/
    @Order(2)
    @DisplayName("2. postId와 userId로 게시글 상세 조회 시 존재하지 않으면 예외 발생")
    @Test
    void getPostDetailsProjectionOrElseThrow_shouldThrow_whenPostNotExists() throws Exception {
      //given
      String postId = "nonexistent-post";
      String userId = "user-1";
      when(postQueryPort.fetchPostDetailsByPostIdAndUserId(postId, userId))
          .thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> postValidator.getPostDetailsProjectionOrElseThrow(postId, userId))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    private PostDetailProjection createMockPostDetailProjection(String postId, String userId) {
      return new PostDetailProjection() {
        @Override
        public String getPostId() {
          return postId;
        }

        @Override
        public String getUserId() {
          return "author-1";
        }

        @Override
        public String getUserNickname() {
          return "authorNick";
        }

        @Override
        public String getUserProfileImageUrl() {
          return "/author.jpg";
        }

        @Override
        public String getContent() {
          return "content";
        }

        @Override
        public long getViewCount() {
          return 100;
        }

        @Override
        public LocalDateTime getPostedAt() {
          return LocalDateTime.now();
        }

        @Override
        public PostStatus getPostStatus() {
          return PostStatus.ACTIVE;
        }

        @Override
        public long getLikeCount() {
          return 10;
        }

        @Override
        public long getCommentCount() {
          return 5;
        }

        @Override
        public boolean isLiked() {
          return false;
        }
      };
    }
  }

  @Order(5)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("게시글 상태 검증 (삭제/블록)")
  class ValidatePostStatusTest {

    /*[Case #1] ACTIVE 상태 - 정상*/
    @Order(1)
    @DisplayName("1. ACTIVE 상태인 경우 예외가 발생하지 않음")
    @Test
    void validatePostStatus_shouldPass_whenActive() throws Exception {
      //given
      String postId = "post-1";
      PostStatus status = PostStatus.ACTIVE;

      //when & then
      assertThatCode(() -> postValidator.validatePostStatus(postId, status))
          .doesNotThrowAnyException();
    }

    /*[Case #2] ARCHIVE 상태 - 정상*/
    @Order(2)
    @DisplayName("2. ARCHIVE 상태인 경우 예외가 발생하지 않음")
    @Test
    void validatePostStatus_shouldPass_whenArchive() throws Exception {
      //given
      String postId = "post-1";
      PostStatus status = PostStatus.ARCHIVE;

      //when & then
      assertThatCode(() -> postValidator.validatePostStatus(postId, status))
          .doesNotThrowAnyException();
    }

    /*[Case #3] DELETED 상태 - 예외 발생*/
    @Order(3)
    @DisplayName("3. DELETED 상태인 경우 예외 발생")
    @Test
    void validatePostStatus_shouldThrow_whenDeleted() throws Exception {
      //given
      String postId = "post-1";
      PostStatus status = PostStatus.DELETED;

      //when & then
      assertThatThrownBy(() -> postValidator.validatePostStatus(postId, status))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_ALREADY_DELETED_ACTION);
    }

    /*[Case #4] BLOCKED 상태 - 예외 발생*/
    @Order(4)
    @DisplayName("4. BLOCKED 상태인 경우 예외 발생")
    @Test
    void validatePostStatus_shouldThrow_whenBlocked() throws Exception {
      //given
      String postId = "post-1";
      PostStatus status = PostStatus.BLOCKED;

      //when & then
      assertThatThrownBy(() -> postValidator.validatePostStatus(postId, status))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_DELETE_BLOCKED);
    }
  }

  @Order(6)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("게시글 접근 가능 상태 검증")
  class ValidateAccessibleStatusTest {

    /*[Case #1] ACTIVE 상태 - 정상*/
    @Order(1)
    @DisplayName("1. ACTIVE 상태인 경우 예외가 발생하지 않음")
    @Test
    void validateAccessibleStatus_shouldPass_whenActive() throws Exception {
      //given
      PostStatus status = PostStatus.ACTIVE;

      //when & then
      assertThatCode(() -> postValidator.validateAccessibleStatus(status))
          .doesNotThrowAnyException();
    }

    /*[Case #2] DELETED 상태 - 예외 발생*/
    @Order(2)
    @DisplayName("2. DELETED 상태인 경우 예외 발생")
    @Test
    void validateAccessibleStatus_shouldThrow_whenDeleted() throws Exception {
      //given
      PostStatus status = PostStatus.DELETED;

      //when & then
      assertThatThrownBy(() -> postValidator.validateAccessibleStatus(status))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_ALREADY_DELETED);
    }

    /*[Case #3] ARCHIVE 상태 - 예외 발생*/
    @Order(3)
    @DisplayName("3. ARCHIVE 상태인 경우 예외 발생")
    @Test
    void validateAccessibleStatus_shouldThrow_whenArchive() throws Exception {
      //given
      PostStatus status = PostStatus.ARCHIVE;

      //when & then
      assertThatThrownBy(() -> postValidator.validateAccessibleStatus(status))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    /*[Case #4] BLOCKED 상태 - 예외 발생*/
    @Order(4)
    @DisplayName("4. BLOCKED 상태인 경우 예외 발생")
    @Test
    void validateAccessibleStatus_shouldThrow_whenBlocked() throws Exception {
      //given
      PostStatus status = PostStatus.BLOCKED;

      //when & then
      assertThatThrownBy(() -> postValidator.validateAccessibleStatus(status))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_BLOCKED);
    }
  }

  @Order(7)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("ID로 게시글 접근 가능 상태 검증")
  class ValidateAccessibleStatusByIdTest {

    /*[Case #1] ACTIVE 상태 - 정상*/
    @Order(1)
    @DisplayName("1. ACTIVE 상태인 게시글인 경우 상태가 반환됨")
    @Test
    void validateAccessibleStatusById_shouldReturnStatus_whenActive() throws Exception {
      //given
      String postId = "post-1";
      when(postQueryPort.fetchPostStatusByPostId(postId))
          .thenReturn(Optional.of(PostStatus.ACTIVE));

      //when
      PostStatus result = postValidator.validateAccessibleStatusById(postId);

      //then
      assertThat(result).isEqualTo(PostStatus.ACTIVE);
      verify(postQueryPort).fetchPostStatusByPostId(postId);
    }

    /*[Case #2] 게시글 미존재 - 예외 발생*/
    @Order(2)
    @DisplayName("2. 게시글이 존재하지 않는 경우 예외 발생")
    @Test
    void validateAccessibleStatusById_shouldThrow_whenPostNotExists() throws Exception {
      //given
      String postId = "nonexistent-post";
      when(postQueryPort.fetchPostStatusByPostId(postId)).thenReturn(Optional.empty());

      //when & then
      assertThatThrownBy(() -> postValidator.validateAccessibleStatusById(postId))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    /*[Case #3] DELETED 상태 - 예외 발생*/
    @Order(3)
    @DisplayName("3. DELETED 상태인 게시글인 경우 예외 발생")
    @Test
    void validateAccessibleStatusById_shouldThrow_whenDeleted() throws Exception {
      //given
      String postId = "post-1";
      when(postQueryPort.fetchPostStatusByPostId(postId))
          .thenReturn(Optional.of(PostStatus.DELETED));

      //when & then
      assertThatThrownBy(() -> postValidator.validateAccessibleStatusById(postId))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_ALREADY_DELETED);
    }

    /*[Case #4] ARCHIVE 상태 - 예외 발생*/
    @Order(4)
    @DisplayName("4. ARCHIVE 상태인 게시글인 경우 예외 발생")
    @Test
    void validateAccessibleStatusById_shouldThrow_whenArchive() throws Exception {
      //given
      String postId = "post-1";
      when(postQueryPort.fetchPostStatusByPostId(postId))
          .thenReturn(Optional.of(PostStatus.ARCHIVE));

      //when & then
      assertThatThrownBy(() -> postValidator.validateAccessibleStatusById(postId))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    /*[Case #5] BLOCKED 상태 - 예외 발생*/
    @Order(5)
    @DisplayName("5. BLOCKED 상태인 게시글인 경우 예외 발생")
    @Test
    void validateAccessibleStatusById_shouldThrow_whenBlocked() throws Exception {
      //given
      String postId = "post-1";
      when(postQueryPort.fetchPostStatusByPostId(postId))
          .thenReturn(Optional.of(PostStatus.BLOCKED));

      //when & then
      assertThatThrownBy(() -> postValidator.validateAccessibleStatusById(postId))
          .isInstanceOf(PostException.class)
          .extracting("errorCode")
          .isEqualTo(ErrorCode.POST_BLOCKED);
    }
  }
}
