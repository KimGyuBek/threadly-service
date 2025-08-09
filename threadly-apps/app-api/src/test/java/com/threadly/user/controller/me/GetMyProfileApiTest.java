package com.threadly.user.controller.me;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.user.profile.query.dto.GetMyProfileDetailsApiResponse;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("내 프로필 조회 관련 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class GetMyProfileApiTest extends BaseMyProfileApiTest {

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 내 프로필 정보 조회 성공 검증*/
    @DisplayName("1. ")
    @Test
    public void getMyProfileDetails_shouldSuccess() throws Exception {
      //given
      String accessToken = getAccessToken(USER_EMAIL);

      //when
      CommonResponse<GetMyProfileDetailsApiResponse> getMyProfileDetailsResponse = sendGetMyProfileDetailsRequest(
          accessToken, status().isOk());

      //then
      validateMyProfileDetailsResponse(getMyProfileDetailsResponse.getData(), USER_PROFILE);
    }

  }

  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

  }
}


