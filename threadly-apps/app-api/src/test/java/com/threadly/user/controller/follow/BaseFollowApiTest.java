package com.threadly.user.controller.follow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.user.controller.profile.BaseUserProfileApiTest;
import com.threadly.user.follow.FollowUserApiResponse;
import com.threadly.user.request.follow.FollowRequest;
import java.util.Map;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Follow 관련 Base 테스트 클래스
 */
public abstract class BaseFollowApiTest extends BaseUserProfileApiTest {

  /**
   * follow 요청 전송
   *
   * @param accessToken
   * @param targetUserId
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse<FollowUserApiResponse> sendFollowUserRequest(String accessToken,
      String targetUserId, ResultMatcher expectedStatus) throws Exception {
    String requestBody = generateRequestBody(
        new FollowRequest(targetUserId)
    );
    return
        sendPostRequest(
            requestBody,
            "/api/follows",
            expectedStatus,
            new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );

  }

}
