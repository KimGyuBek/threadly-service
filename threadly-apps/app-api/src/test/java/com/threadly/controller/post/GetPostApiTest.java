package com.threadly.controller.post;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.ErrorCode;
import com.threadly.post.create.CreatePostApiResponse;
import com.threadly.post.engagement.GetPostEngagementApiResponse;
import com.threadly.post.get.GetPostDetailApiResponse;
import com.threadly.post.get.GetPostDetailListApiResponse;
import com.threadly.post.update.UpdatePostApiResponse;
import com.threadly.testsupport.fixture.posts.PostFixtureLoader;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 게시글 조회 관련 API 테스트
 * <p>
 * 테스트 데이터 {/test/resources/fixtures/users/user-fixture.json,
 * /test/resources/fixtures/posts/post-fixture.json/}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class GetPostApiTest extends BasePostApiTest {

  @Autowired
  private PostFixtureLoader postFixtureLoader;

  public static final int POST_COUNT_ACTIVE = 95;

  @BeforeEach
  void setUp() {
    postFixtureLoader.load("/users/user-fixture.json", "/posts/post-fixture.json");
  }

  /**
   * getPost() - 게시글 조회 테스트
   */
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 조회 테스트")
  @Nested
  class getPostTest {

    @Order(1)
    @DisplayName("성공")
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class success {

      /*[Case #1] getPost()  존재하는 게시글을 조회하면 200 ok 응답을 반환한다.*/
      @Order(1)
      @DisplayName("1. 존재하는 게시글을 조회할 경우")
      @Test
      public void getPost_shouldReturnOk_whenGetExistingPost() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        /*게시글 조회*/
        String postId = "post1";
        CommonResponse<GetPostDetailApiResponse> postDetailResponse = sendGetPostRequest(
            accessToken, postId, status().isOk());

        assertThat(postDetailResponse.getData().postId()).isEqualTo(postId);
      }

      /*[Case #2]  게시글 작성 후 조회시 작성한 내용과 동일해야함*/
      @Order(2)
      @DisplayName("2. 게시글 작성 후 조회 시 작성한 내용과 postId가 동일한지 검증")
      @Test
      public void getPost_shouldReturnSameContentAndId_whenGetPostAfterCreation() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        /*게시글 작성*/
        String content = "content";
        CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(
            accessToken,
            content,
            status().isCreated());

        //when
        /*게시글 조회*/
        String postId = createPostResponse.getData().postId();
        CommonResponse<GetPostDetailApiResponse> postDetailResponse = sendGetPostRequest(
            accessToken, postId, status().isOk());

        //then
        assertThat(postDetailResponse.getData().postId()).isEqualTo(postId);
        assertThat(postDetailResponse.getData().content()).isEqualTo(content);
      }

      /*[Case #3]  게시글 작성 후 조회, 내용 수정 시 작성한 내용과 동일해야함*/
      @Order(3)
      @DisplayName("3.  게시글 작성 후 수정 시, 조회하면 수정된 내용이 조회되는지 검증")
      @Test
      public void getPost_shouldReturnSameContentAndId_whenGetPostAfterUpdate() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        /*게시글 작성*/
        String content = "content";
        CommonResponse<CreatePostApiResponse> createPostResponse = sendCreatePostRequest(
            accessToken,
            content,
            status().isCreated());

        /*게시글 수정*/
        String postId = createPostResponse.getData().postId();
        /*게시글 수정 요청 전송*/
        String modifiedContent = "modifiedContent";
        CommonResponse<UpdatePostApiResponse> updatedPostResponse = sendUpdatePostRequest(
            accessToken, modifiedContent, postId, status().isOk());

        //when
        /*게시글 조회*/
        CommonResponse<GetPostDetailApiResponse> postDetailResponse = sendGetPostRequest(
            accessToken, postId, status().isOk());

        //then
        assertThat(postDetailResponse.getData().postId()).isEqualTo(postId);
        assertThat(postDetailResponse.getData().content()).isEqualTo(modifiedContent);
      }
    }

    @Order(2)
    @DisplayName("실패")
    @Nested
    class fail {

      /*[Case #1] 존재하지 않는 게시글을 조회하면 404 NOT FOUND 응답을 반환한다.*/
      @Order(1)
      @DisplayName("게시글 조회 - 존재하지 않는 게시글을 조회하면 404 Not Found")
      @Test
      public void getPost_shouldReturnNotFound_whenGetNotExistingPost() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        /*게시글 조회*/
        String postId = "post_not_exist_id";
        CommonResponse<GetPostDetailApiResponse> postDetailResponse = sendGetPostRequest(
            accessToken, postId, status().isNotFound());

        assertThat(postDetailResponse.isSuccess()).isFalse();
        assertThat(postDetailResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
      }
    }
  }

  /**
   * getPostList() - 테스트
   */
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 목록 조회 테스트")
  @Nested
  class getPostListTest {

    @Order(1)
    @DisplayName("성공")
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class success {

      /*[Case #1] getPostList()  게시글 리스트 조회 시 200 OK 반환*/
      @Order(1)
      @DisplayName("1. 게시글 리스트  조회")
      @Test
      public void getPostList_shouldReturnOk_whenGetExistingPost() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        /*게시글 목록 조회*/
        LocalDateTime cursorPostedAt = null;
        String cursorPostId = null;

        CommonResponse<GetPostDetailListApiResponse> postListResponse1 = sendGetPostListRequest(
            accessToken, cursorPostedAt, cursorPostId, 10, status().isOk());

        cursorPostedAt = postListResponse1.getData().nextCursor().postedAt();
        cursorPostId = postListResponse1.getData().nextCursor().postId();

        CommonResponse<GetPostDetailListApiResponse> postListResponse2 = sendGetPostListRequest(
            accessToken, cursorPostedAt, cursorPostId, 10, status().isOk());

        assertThat(postListResponse1.getData().posts()).hasSize(10);

        assertThat(postListResponse2.getData().posts()).hasSize(10);
        assertThat(postListResponse1.getData().posts().getLast().postId()).isEqualTo(
            postListResponse1.getData().nextCursor().postId());
      }

      /*[Case #2] getPostList - 게시글 목록 전체 조회 - 커서 기반 페이징 방식으로 전체 페이지를 순회하면서 마지막 페이지까지 반복 조회하는 테스트
       * nextCursor가 null이 되는 시점까지 반복적으로 조회 요청을 전송함으로써 페이징 커서의 정확성과 순회 종료 조건을 검증한다
       * */
      @Order(2)
      @DisplayName("2. 커서 기반 페이징으로 전체 게시글 마지막 페이지까지 순회하며 조회")
      @Test
      public void getPostList_shouldIterateAllPages_usingCursorPagination() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        /*게시글 전체 조회 요청*/
        LocalDateTime cursorPostedAt = null;
        String cursorPostId = null;
        int limit = 10;
        int size = 0;

        while (true) {
          CommonResponse<GetPostDetailListApiResponse> getPostListResponse = sendGetPostListRequest(
              accessToken, cursorPostedAt, cursorPostId, limit, status().isOk());
          size += getPostListResponse.getData().posts().size();

          /*마지막 페이지면 */
          if (getPostListResponse.getData().nextCursor().postedAt() == null) {
            break;
          }

          cursorPostedAt = getPostListResponse.getData().nextCursor().postedAt();
          cursorPostId = getPostListResponse.getData().nextCursor().postId();
        }

        assertThat(size).isEqualTo(POST_COUNT_ACTIVE);
      }

    }

    @Order(2)
    @DisplayName("실패")
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class fail {

      /*[Case #1] getPostEngagement - 존재하지 않는 게시글 조회 시 404 Not Found, 실패해야 한다*/
      @Order(1)
      @DisplayName("1. 존재하지 않는 게시글 조회 시 404 Not Found")
      @Test
      public void getPostEngagement_shouldNotFound_whenPostNotExists() throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);
        String postId = "post_not_exists";

        long likeCount = POST_LIKES.get("post10");

        //when
        //then
        /*조회 요청*/
        CommonResponse<GetPostEngagementApiResponse> postEngagementResponse = sendGetPostEngagementRequest(
            accessToken, postId, status().isNotFound()
        );
        assertThat(postEngagementResponse.isSuccess()).isFalse();
        assertThat(postEngagementResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
      }
    }
  }
}