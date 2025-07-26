package com.threadly.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.repository.TestUserRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * Base User Api test
 */
public abstract class BaseUserApiTest extends BaseApiTest {

  @Autowired
  private TestUserRepository testUserRepository;

  /**
   * 회원 탈퇴 요청
   *
   * @return
   */
  public CommonResponse<Void> sendWithDrawUserRequest(String accessToken,
      ResultMatcher expectedStatus) throws Exception {
    return
        sendDeleteRequest(
            "", "/api/user/me", expectedStatus, new TypeReference<>() {
            },
            Map.of("Authorization", "Bearer " + accessToken)
        );
  }


  /**
   * email로 user statusType 검증
   *
   * @param email
   * @param expectedStatus
   */
  public void validateUserStatusType(String email, UserStatusType expectedStatus) {
    UserStatusType statusByEmail = testUserRepository.findStatusByEmail(
        email);
    assertThat(statusByEmail).isEqualTo(expectedStatus);
  }
}
