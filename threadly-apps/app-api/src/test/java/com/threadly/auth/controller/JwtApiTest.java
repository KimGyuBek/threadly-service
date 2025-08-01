package com.threadly.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.exception.ErrorCode;
import com.threadly.utils.TestConstants;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("Jwt 인증 관련 API 테스트")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class JwtApiTest extends BaseApiTest {

  @TestClassOrder(OrderAnnotation.class)
  @DisplayName("사용자 프로필 초기 설정 관련 테스트")
  @Nested
  class UserProfileCompleteTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] 사용자 프로필을 설정한 사용자는 인증에 성공해야 한다*/
      @Order(1)
      @DisplayName("1. 사용자 프로필을 설정한 사용자는 인증 경로에 접근 가능해야한다")
      @Test
      public void userProfileCompleteTest_shouldSuccess_whenUserSetProfile() throws Exception {
        //given
        String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

        //when
        CommonResponse response = sendGetRequest(accessToken, "/", status().isNotFound());

        //then
        assertThat(response.isSuccess()).isTrue();
      }

    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] 사용자 프로필을 설정하지 않는  사용자는 */
      @Order(1)
      @DisplayName("")
      @Test
      public void userProfileCompleteTest_shouldSuccess_whenUserSetProfile() throws Exception {
        //given
        String accessToken = getAccessToken(TestConstants.PROFILE_NOT_SET_USER_1);

        //when
        CommonResponse response = sendGetRequest(accessToken, "/", status().isForbidden());

        //then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ErrorCode.USER_PROFILE_NOT_SET.getCode());
      }
    }
  }


}
