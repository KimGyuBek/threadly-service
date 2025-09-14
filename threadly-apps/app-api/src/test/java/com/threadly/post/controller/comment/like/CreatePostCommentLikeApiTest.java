package com.threadly.post.controller.comment.like;

import static com.threadly.utils.TestConstants.EMAIL_VERIFIED_USER_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.post.controller.BasePostApiTest;
import com.threadly.core.port.post.in.like.comment.LikePostCommentApiResponse;
import com.threadly.testsupport.fixture.posts.PostCommentFixtureLoader;
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
 * 댓글 좋아요 생성 관련 테스트
 * <p>
 * 테스트 데이터 = { /posts/comments/likes/create-comment-like/}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class CreatePostCommentLikeApiTest extends BasePostApiTest {

  @Autowired
  private PostCommentFixtureLoader postCommentFixtureLoader;

  @BeforeEach
  void setUp() throws Exception {
    postCommentFixtureLoader.load(
        "/posts/comments/likes/create-comment-like/user.json",
        "/posts/comments/likes/create-comment-like/post.json",
        "/posts/comments/likes/create-comment-like/post-comment.json"
    );
  }

  /**
   * likePostComment() 테스트
   */
  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("게시글 댓글 좋아요 테스트")
  @Nested
  class likePostCommentTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] likePostComment - 이메일 인증된 사용자가 ACTIVE 상태의 댓글에 좋아요를 누를경우 성공해야한다*/
      @Order(1)
      @DisplayName("1. 정상적으로 댓글에 좋아요를 요청할 경우 응답 검증")
      @Test
      public void likePostComment_shouldReturnCreated_whenUserLikesActiveComment()
          throws Exception {
        //given
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
            accessToken, ACTIVE_POST_ID, ACTIVE_COMMENT_ID, status().isOk());

        assertThat(likePostCommentResponse.getData().commentId()).isEqualTo(ACTIVE_COMMENT_ID);
        assertThat(likePostCommentResponse.getData().likeCount()).isEqualTo(1);
      }

      /*[Case #2] likePostComment - 여러 사용자가 댓글에 좋아요를 누를 경우 likeCount가 누적되어 증가해야 한다*/
      @Order(2)
      @DisplayName("2. 여러 사용자가 댓글에 좋아요를 눌렀을 경우 응답 검증")
      @Test
      public void likePostComment_shouldAccumulateLikeCount_whenMultipleUserLike()
          throws Exception {
        //given
        //when
        //then
        /*여러명의 사용자 로그인 후 좋아요 요청 전송*/
        for (int i = 0; i < USER_EMAILS.size(); i++) {
          assertThat(
              sendLikePostCommentRequest(
                  getAccessToken(USER_EMAILS.get(i)), ACTIVE_POST_ID, ACTIVE_COMMENT_ID,
                  status().isOk()).isSuccess()).isTrue();
        }
        CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
            getAccessToken(EMAIL_VERIFIED_USER_1), ACTIVE_POST_ID, ACTIVE_COMMENT_ID,
            status().isOk());

        assertThat(likePostCommentResponse.getData().commentId()).isEqualTo(ACTIVE_COMMENT_ID);
        assertThat(likePostCommentResponse.getData().likeCount()).isEqualTo(
            Long.valueOf(USER_EMAILS.size()));
      }

      /*[Case #3] likePostComment - 동일 사용자가 같은 댓글에 여러번 좋아요를 눌러도 멱등하게 처리되어야 함*/
      @Order(3)
      @DisplayName("3. 동일한 사용자가 여러번 요청을 보내도 멱등하는지 검증")
      @Test
      public void likePostComment_shouldBeIdempotent_whenUserLikesSameCommentMultipleTimes()
          throws Exception {
        //given
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        for (int i = 0; i < 2; i++) {
          assertThat(
              sendLikePostCommentRequest(
                  accessToken, ACTIVE_POST_ID, ACTIVE_COMMENT_ID,
                  status().isOk()).isSuccess()).isTrue();
        }
        CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
            accessToken, ACTIVE_POST_ID, ACTIVE_COMMENT_ID, status().isOk());

        assertThat(likePostCommentResponse.getData().commentId()).isEqualTo(ACTIVE_COMMENT_ID);
        assertThat(likePostCommentResponse.getData().likeCount()).isEqualTo(1);
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] likePostComment - DELETED 상태의 댓글에 좋아요 요청 시 400 BadRequest*/
      @Order(1)
      @DisplayName("1. DELETED 상태의 댓글에 좋아요 요청 시 400 BadRequest")
      @Test
      public void likePostComment_shouldReturnBadRequest_whenCommentIsDeleted() throws Exception {
        //given
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
            accessToken, ACTIVE_POST_ID, DELETED_COMMENT_ID, status().isBadRequest());

        assertThat(likePostCommentResponse.isSuccess()).isFalse();
        assertThat(likePostCommentResponse.getCode()).isEqualTo(
            ErrorCode.POST_COMMENT_LIKE_NOT_ALLOWED.getCode());
      }

      /*[Case #2] likePostComment - BLOCKED 상태의 댓글에 좋아요 요청 시 400 BadRequest*/
      @Order(2)
      @DisplayName("2. BLOCKED 상태의 댓글에 좋아요 요청 시 400 BadRequest")
      @Test
      public void likePostComment_shouldReturnBadRequest_whenCommentIsBlocked() throws Exception {
        //given
        String accessToken = getAccessToken(EMAIL_VERIFIED_USER_1);

        //when
        //then
        CommonResponse<LikePostCommentApiResponse> likePostCommentResponse = sendLikePostCommentRequest(
            accessToken, ACTIVE_POST_ID, BLOCKED_COMMENT_ID, status().isBadRequest());

        assertThat(likePostCommentResponse.isSuccess()).isFalse();
        assertThat(likePostCommentResponse.getCode()).isEqualTo(
            ErrorCode.POST_COMMENT_LIKE_NOT_ALLOWED.getCode());
      }
    }
  }


  // 게시글 ID
  public static final String ACTIVE_POST_ID = "active_post_id";

  // 댓글 ID (상태별 분류)
  public static final String ACTIVE_COMMENT_ID = "cmt_active_001";
  public static final String DELETED_COMMENT_ID = "cmt_deleted_001";
  public static final String BLOCKED_COMMENT_ID = "cmt_blocked_001";

  // 전체 사용자 수
  public static final int USER_COUNT = 101;

  // 사용자 이메일 목록
  public static final List<String> USER_EMAILS = List.of(
      "writer@threadly.com",
      "sunset_gazer1@threadly.com",
      "sky_gazer2@threadly.com",
      "book_worm3@threadly.com",
      "beach_bum4@threadly.com",
      "early_bird5@threadly.com",
      "mountain_hiker6@threadly.com",
      "dream_chaser7@threadly.com",
      "night_owl8@threadly.com",
      "mountain_hiker9@threadly.com",
      "mountain_hiker10@threadly.com",
      "dream_chaser11@threadly.com",
      "breeze_seeker12@threadly.com",
      "early_bird13@threadly.com",
      "silent_walker14@threadly.com",
      "beach_bum15@threadly.com",
      "sky_gazer16@threadly.com",
      "city_runner17@threadly.com",
      "beach_bum18@threadly.com",
      "wave_rider19@threadly.com",
      "tea_addict20@threadly.com",
      "sunset_gazer21@threadly.com",
      "night_owl22@threadly.com",
      "lazy_sunday23@threadly.com",
      "tea_addict24@threadly.com",
      "coffee_lover25@threadly.com",
      "sky_gazer26@threadly.com",
      "rainy_day27@threadly.com",
      "breeze_seeker28@threadly.com",
      "sunset_gazer29@threadly.com",
      "silent_walker30@threadly.com",
      "lazy_sunday31@threadly.com",
      "tea_addict32@threadly.com",
      "lazy_sunday33@threadly.com",
      "rainy_day34@threadly.com",
      "book_worm35@threadly.com",
      "bike_rider36@threadly.com",
      "sunset_gazer37@threadly.com",
      "art_junkie38@threadly.com",
      "mountain_hiker39@threadly.com",
      "beach_bum40@threadly.com",
      "silent_walker41@threadly.com",
      "wave_rider42@threadly.com",
      "coffee_lover43@threadly.com",
      "coffee_lover44@threadly.com",
      "dream_chaser45@threadly.com",
      "dream_chaser46@threadly.com",
      "book_worm47@threadly.com",
      "dream_chaser48@threadly.com",
      "art_junkie49@threadly.com",
      "lazy_sunday50@threadly.com",
      "tea_addict51@threadly.com",
      "dream_chaser52@threadly.com",
      "art_junkie53@threadly.com",
      "night_owl54@threadly.com",
      "night_owl55@threadly.com",
      "coffee_lover56@threadly.com",
      "forest_soul57@threadly.com",
      "rainy_day58@threadly.com",
      "gallery_goer59@threadly.com",
      "rainy_day60@threadly.com",
      "early_bird61@threadly.com",
      "rainy_day62@threadly.com",
      "dream_chaser63@threadly.com",
      "breeze_seeker64@threadly.com",
      "forest_soul65@threadly.com",
      "city_runner66@threadly.com",
      "city_runner67@threadly.com",
      "dream_chaser68@threadly.com",
      "city_runner69@threadly.com",
      "early_bird70@threadly.com",
      "coffee_lover71@threadly.com",
      "gallery_goer72@threadly.com",
      "breeze_seeker73@threadly.com",
      "forest_soul74@threadly.com",
      "breeze_seeker75@threadly.com",
      "city_runner76@threadly.com",
      "wave_rider77@threadly.com",
      "dream_chaser78@threadly.com",
      "early_bird79@threadly.com",
      "night_owl80@threadly.com",
      "sunset_gazer81@threadly.com",
      "dream_chaser82@threadly.com",
      "silent_walker83@threadly.com",
      "forest_soul84@threadly.com",
      "gallery_goer85@threadly.com",
      "forest_soul86@threadly.com",
      "night_owl87@threadly.com",
      "beach_bum88@threadly.com",
      "art_junkie89@threadly.com",
      "city_runner90@threadly.com",
      "lazy_sunday91@threadly.com",
      "bike_rider92@threadly.com",
      "coffee_lover93@threadly.com",
      "early_bird94@threadly.com",
      "lazy_sunday95@threadly.com",
      "mountain_hiker96@threadly.com",
      "rainy_day97@threadly.com",
      "tea_addict98@threadly.com",
      "early_bird99@threadly.com",
      "breeze_seeker100@threadly.com"
  );
}
