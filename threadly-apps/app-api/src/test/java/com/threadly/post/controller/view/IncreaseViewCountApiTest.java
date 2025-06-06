package com.threadly.post.controller.view;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.post.get.GetPostDetailApiResponse;
import com.threadly.properties.TtlProperties;
import com.threadly.repository.auth.TestRedisHelper;
import com.threadly.testsupport.fixture.posts.PostFixtureLoader;
import java.util.List;
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
 * 게시글 조회 수 증가 관련 테스트
 * <p>
 * 테스트 데이터 = {/posts/post-view/}
 */

public class IncreaseViewCountApiTest extends BasePostApiTest {

  @Autowired
  private PostFixtureLoader postFixtureLoader;

  @Autowired
  private TtlProperties ttlProperties;

  @Autowired
  private TestRedisHelper testRedisHelper;


  @BeforeEach
  void setUp() {
    super.setUpDefaultUser();
    postFixtureLoader.load("/posts/post-view/user.json", "/posts/post-view/post.json");

    /*레디스 초기화*/
    testRedisHelper.clearRedis();
  }

  // 게시글 ID (viewCount = 0)
  public static final String VIEW_COUNT_ZERO_POST_ID = "active_post_id";

  // 게시글 작성자 이메일
  public static final String POST_WRITER_EMAIL = "writer@threadly.com";

  // 게시글을 조회할 사용자 이메일 목록
  public static final List<String> POST_VIEW_USER_EMAILS = List.of("sunset_gazer1@threadly.com",
      "sky_gazer2@threadly.com", "book_worm3@threadly.com", "beach_bum4@threadly.com",
      "early_bird5@threadly.com", "mountain_hiker6@threadly.com");

  /**
   * 게시글 조회 수 증가 테스트
   */
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("게시글 조회 수 증가 테스트")
  @Nested
  class IncreasePostViewTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 사용자가 게시글 조회 시 조회 수가 증가하는지 검증*/
      @Order(1)
      @DisplayName("1. 사용자가 게시글 조회 시 조회 수가 증가하는지 검증")
      @Test
      public void increaseViewCount_shouldIncrease_whenUserGetPost() throws Exception {
        //given
        /*로그인 */
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        /*게시글 조회 요청 */
        CommonResponse<GetPostDetailApiResponse> getPostResponse1 = sendGetPostRequest(accessToken,
            VIEW_COUNT_ZERO_POST_ID, status().isOk());

        CommonResponse<GetPostDetailApiResponse> getPostResponse2 = sendGetPostRequest(accessToken,
            VIEW_COUNT_ZERO_POST_ID, status().isOk());
        int viewCount1 = getPostResponse1.getData().viewCount();
        int viewCount2 = getPostResponse2.getData().viewCount();

        assertThat(viewCount1).isNotEqualTo(viewCount2);
        assertThat(viewCount2).isEqualTo(viewCount1 + 1);
      }

      /*[Case #2] 사용자가 게시글을 여러번 조회 시 멱등한지 검증 */
      @Order(2)
      @DisplayName("2. 사용자가 게시글을 여러번 조회 시 멱등한지 검증")
      @Test
      public void increaseViewCount_shouldIdempotent_whenUserMultipleRequest() throws Exception {
        //given
        /*로그인 */
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        /*최초 조회*/
        CommonResponse<GetPostDetailApiResponse> getPostResponse1 = sendGetPostRequest(accessToken,
            VIEW_COUNT_ZERO_POST_ID, status().isOk());

        //then
        /*게시글 중복 조회 요청 */
        for (int i = 0; i < 3; i++) {
          CommonResponse<GetPostDetailApiResponse> getPostResponse = sendGetPostRequest(accessToken,
              VIEW_COUNT_ZERO_POST_ID, status().isOk());

          assertThat(getPostResponse.getData().viewCount()).isEqualTo(1);
        }
      }

      /*[Case #3] 사용자가 게시글을 조회 후 시간이 지난 후  다시 조회 시  조회 수가 증가하는지 검증*/
      @Order(3)
      @DisplayName("3. 사용자가 게시글을 조회 후 시간이 지난 후 다시 조회 시 조회 수가 증가하는지 검증")
      @Test
      public void increaseViewCount_shouldIncreaseViewCount_whenUserRequestAfterFewTimes()
          throws Exception {
        //given
        /*로그인 */
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        /*조회 요청 1*/
        CommonResponse<GetPostDetailApiResponse> getPostResponse1 = sendGetPostRequest(accessToken,
            VIEW_COUNT_ZERO_POST_ID, status().isOk());

        /*ttl 만큼 대기*/
        Thread.sleep(ttlProperties.getPostViewSeconds().toMillis());

        //then
        /*시간 경과 후 조회 요청*/
        CommonResponse<GetPostDetailApiResponse> getPostResponse2 = sendGetPostRequest(accessToken,
            VIEW_COUNT_ZERO_POST_ID, status().isOk());

        int viewCount1 = getPostResponse1.getData().viewCount();
        int viewCount2 = getPostResponse2.getData().viewCount();

        assertThat(viewCount1).isNotEqualTo(viewCount2);
        assertThat(viewCount2).isEqualTo(viewCount1 + 1);
      }

      /*[Case #4] 여러 사용자가 게시글을 조회 시 조회 수가 증가하는지 검증*/
      @Order(4)
      @DisplayName("4. 여러 사용자가 게시글 조회 시 조회 수가 증가하는지 검증")
      @Test
      public void increaseViewCount_shouldIncreaseViewCount_whenMultipleUserRequest()
          throws Exception {
        //given
        //when
        /*사용자 수 만큼 반복 요청*/
        for (int i = 0; i < POST_VIEW_USER_EMAILS.size(); i++) {
          sendGetPostRequest(
              getAccessToken(POST_VIEW_USER_EMAILS.get(i)),
              VIEW_COUNT_ZERO_POST_ID,
              status().isOk()
          );
        }

        //then
        CommonResponse<GetPostDetailApiResponse> getPostRequest = sendGetPostRequest(
            getAccessToken(EMAIL_VERIFIED_USER_1),
            VIEW_COUNT_ZERO_POST_ID,
            status().isOk()
        );

        assertThat(getPostRequest.getData().viewCount()).isEqualTo(POST_VIEW_USER_EMAILS.size());
      }

    }
  }
}

