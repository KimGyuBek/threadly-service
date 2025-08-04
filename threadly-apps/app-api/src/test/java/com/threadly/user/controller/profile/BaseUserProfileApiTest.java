package com.threadly.user.controller.profile;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.testsupport.fixture.users.UserFixtureLoader;
import com.threadly.user.profile.get.GetUserProfileApiResponse;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * 사용자 프로필 관련 Base 테스트
 */
public abstract class BaseUserProfileApiTest extends BaseApiTest {

  @Autowired
  public UserFixtureLoader userFixtureLoader;

//  @BeforeEach
//  void setUp() {
//    userFixtureLoader.load(
//        "/users/profile/user.json"
//    );
//  }

  //userId
  public static final String USER_ID = "user_with_profile_test";
  public static final String USER2_ID = "user_with_profile_test2";

  //user email
  public static final String USER_EMAIL = "user_with_profile_test@threadly.com";
  public static final String USER2_EMAIL = "user_with_profile_test2@threadly.com";

  //user password
  public static final String USER_PASSWORD = "1234";

  //user profile
  public static final Map<String, String> USER_PROFILE = Map.of(
      "userId", "user_with_profile_test",
      "nickname", "usr1_nickname",
      "statusMessage", "상태 메세지",
      "bio", "나는 사용자이다",
      "gender", "MALE",
      "profileType", "USER"
  );

  public static final Map<String, String> USER2_PROFILE = Map.of(
      "userId", "user_with_profile_test2",
      "nickname", "usr1_nickname2",
      "statusMessage", "상태 메세지",
      "bio", "나는 사용자이다2",
      "gender", "MALE",
      "profileType", "USER"
  );

  /**
   * 사용자 프로필 정보 조회 요청
   *
   * @param accessToken
   * @param userId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<GetUserProfileApiResponse> sendGetUserProfileRequest(String accessToken,
      String userId, ResultMatcher expectedStatus) throws Exception {

    return
        sendGetRequest(
            accessToken, "/api/users/profile/" + userId, expectedStatus,
            new TypeReference<>() {
            });
  }

  /**
   * 닉네임 중복 검증 요청
   *
   * @param accessToken
   * @param nickname
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<Void> sendCheckNicknameRequest(String accessToken, String nickname,
      ResultMatcher expectedStatus) throws Exception {
    return sendGetRequest(accessToken, "/api/me/profile/check?nickname=" + nickname,
        expectedStatus);
  }

  /**
   * 사용자 프로필 응답 검증
   */
  public void assertUserProfileResponse(GetUserProfileApiResponse actual,
      Map<String, String> expected) {
    assertThat(actual.user().userId()).isEqualTo(expected.get("userId"));
    assertThat(actual.user().nickname()).isEqualTo(expected.get("nickname"));
    assertThat(actual.statusMessage()).isEqualTo(expected.get("statusMessage"));
    assertThat(actual.bio()).isEqualTo(expected.get("bio"));
    assertThat(actual.user().profileImageUrl()).isEqualTo(expected.get("profileImageUrl"));
  }

}
