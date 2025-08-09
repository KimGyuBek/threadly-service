package com.threadly.post.controller.like;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.post.engagement.GetPostEngagementApiResponse;
import com.threadly.post.like.post.PostLiker;
import com.threadly.response.CursorPageApiResponse;
import com.threadly.testsupport.fixture.posts.PostLikeFixtureLoader;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 게시글 좋아요 조회 관련 API 테스트
 * <p>
 * 테스트 데이터 = {/posts/likes/post-like-list/}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class GetPostLikeApiTest extends BasePostApiTest {

  @Autowired
  private PostLikeFixtureLoader postLikeFixtureLoader;

  @BeforeEach
  void setUp() throws Exception {
    postLikeFixtureLoader.load(
        "/posts/likes/post-like-list/post-like-user.json",
        "/posts/likes/post-like-list/post.json",
        "/posts/likes/post-like-list/post-likes.json"
    );
  }

  /**
   * 좋아요가 있는 게시글 ID
   */
  public static final String POST_LIKE_TARGET_ID = "post_like_target";

  /**
   * 좋아요가 없는 게시글 ID
   */
  public static final String POST_NO_LIKE_TARGET_ID = "post_no_like_target";


  /**
   * 게시글 전체 좋아요 수
   */
  public static final int POST_LIKE_COUNT = 25;


  /**
   * 좋아요 누른 사용자 id
   */
  public static final List<String> POST_LIKE_USER_IDS = List.of(
      "usr101", "usr102", "usr103", "usr104", "usr105",
      "usr106", "usr107", "usr108", "usr109", "usr110",
      "usr111", "usr112", "usr113", "usr114", "usr115",
      "usr116", "usr117", "usr118", "usr119", "usr120",
      "usr121", "usr122", "usr123", "usr124", "usr125"
  );

  /**
   * getPostLikers - 테스트
   *
   * @throws Exception
   */

  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 좋아요 목록 조회 테스트")
  @Nested
  class getPostLikersTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] getPostLikers - 게시글에 좋아요를 누른 사람이 있으면 그 목록을 조회해야한다*/
      @Order(1)
      @DisplayName("1. 좋아요가 존재하는 게시글에 요청을 보내면 좋아요 목록의 사이즈가 일치하는지 검증")
      @Test
      public void getPostLikers_shouldReturnLikers_whenPostHasLikers() throws Exception {
        //given
        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);
        LocalDateTime cursorTimestamp = null;
        String cursorId = null;
        int limit = 10;
        long size = 0;

        //when
        //then
        /*조회 요청*/
        while (true) {
          CommonResponse<CursorPageApiResponse<PostLiker>> getPostLikersResponse = sendGetPostLikersRequest(
              accessToken, POST_LIKE_TARGET_ID, cursorTimestamp, cursorId, limit, status().isOk()
          );
          size += getPostLikersResponse.getData().content().size();

          /*마지막 페이지 일 경우*/
          if (getPostLikersResponse.getData().nextCursor().cursorId() == null
              || getPostLikersResponse.getData().nextCursor().cursorTimestamp() == null) {
            break;
          }

          cursorTimestamp = getPostLikersResponse.getData().nextCursor().cursorTimestamp();
          cursorId = getPostLikersResponse.getData().nextCursor().cursorId();
        }
        assertThat(size).isEqualTo(POST_LIKE_COUNT);
      }

      /*[Case #2] getPostLikers - 좋아요가 없는 게시글에 대해 좋아요 목록 요청 시 빈 리스트가 반환 되어야 한다*/
      @Order(2)
      @DisplayName("2. 좋아요가 없는 게시글에 요청 시 빈 리스트가 반환되는지 검증")
      @Test
      public void getPostLikers_shouldReturnEmptyList_whenPostHasNotLikes() throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        int likeCount = 0;
        LocalDateTime cursorTimestamp = null;
        String cursorId = null;
        int limit = 10;
        int size = 0;

        //when
        //then
        /*조회 요청*/
        while (true) {
          CommonResponse<CursorPageApiResponse<PostLiker>> getPostLikersResponse = sendGetPostLikersRequest(
              accessToken, POST_NO_LIKE_TARGET_ID, cursorTimestamp, cursorId, limit,
              status().isOk()
          );
          size += getPostLikersResponse.getData().content().size();

          /*마지막 페이지 일 경우*/
          if (getPostLikersResponse.getData().nextCursor().cursorId() == null
              || getPostLikersResponse.getData().nextCursor().cursorId() == null) {
            break;
          }

          cursorTimestamp = getPostLikersResponse.getData().nextCursor().cursorTimestamp();
          cursorId = getPostLikersResponse.getData().nextCursor().cursorId();
        }
        assertThat(size).isEqualTo(likeCount);
      }

    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] getPostLikers - 존재하지 않는 게시글에 대해 좋아요 목록 요청 시 404 Not Found, 실패해야 한다*/
      @Order(1)
      @DisplayName("1. 존재하지 않는 게시글에 대해 요청 시 404 Not Found")
      @Test
      public void getPostLikers_shouldReturnNotFound_whenPostNotFound() throws Exception {
        //given

        /*로그인 요청*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        String postId = "post_not_exists";
        LocalDateTime cursorLikedAt = null;
        String cursorLikerId = null;
        int limit = 10;

        CommonResponse<CursorPageApiResponse<PostLiker>> getPostLikersResponse = sendGetPostLikersRequest(
            accessToken, postId, cursorLikedAt, cursorLikerId, limit, status().isNotFound()
        );
        assertThat(getPostLikersResponse.getCode()).isEqualTo(ErrorCode.POST_NOT_FOUND.getCode());
      }
    }
  }

  /**
   * getPostEngagement() - 테스트
   */
  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("게시글 좋아요 요약 조회 테스트")
  @Nested
  class getPostEngagementTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] getPostEngagement - 게시글 좋아요 요약 조회시 성공해야 한다*/
      @DisplayName("1. 좋아요가 존재하는 게시글에 요청을 보내면 likeCount가 정확하게 반환되는지 검증")
      @Test
      public void getPostEngagement_shouldReturnCorrectLikeCount_whenPostHasLikes()
          throws Exception {
        //given
        /*로그인*/
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        /*조회 요청*/
        CommonResponse<GetPostEngagementApiResponse> postEngagementResponse = sendGetPostEngagementRequest(
            accessToken, POST_LIKE_TARGET_ID, status().isOk()
        );
        assertThat(postEngagementResponse.getData().likeCount()).isEqualTo(POST_LIKE_COUNT);
      }
    }
  }
}
