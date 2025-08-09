package com.threadly.user.controller.me;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.utils.TestConstants;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 사용자 프로필  검증 관련 테스트
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class ValidateMyProfileApiTest extends BaseMyProfileApiTest {

  @Order(1)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("성공")
  class success {

    /*[Case #1] 중복되지 않는 닉네임일 경우 성공 검증*/
    @Order(1)
    @DisplayName("1. 중복되지 않는 닉네임일경우 성공 검증")
    @Test
    public void checkNickname_shouldSuccess_whenNicknameIsUnique() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      CommonResponse<Void> checkNicknameResponse = sendCheckNicknameRequest(accessToken,
          "NotExistsNickname", status().isOk());

      //then
      /*응답 검증*/
      assertThat(checkNicknameResponse.isSuccess()).isTrue();
    }
  }


  @Order(2)
  @Nested
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  @DisplayName("실패")
  class fail {

    /*[Case #1] 이미 존재하는 닉네임일경우 409 Conflict*/
    @Order(1)
    @DisplayName("1. 이미 존재하는 닉네임의 경우")
    @Test
    public void checkNickname_should409Conflict_whenNicknameAlreadyExists() throws Exception {
      //given
      /*로그인*/
      String accessToken = getAccessToken(TestConstants.EMAIL_VERIFIED_USER_1);

      //when
      CommonResponse<Void> checkNicknameResponse = sendCheckNicknameRequest(accessToken,
          USER_PROFILE.get("nickname"), status().isConflict());

      //then
      /*응답 검증*/
      assertThat(checkNicknameResponse.isSuccess()).isFalse();
      assertThat(checkNicknameResponse.getCode()).isEqualTo(ErrorCode.USER_NICKNAME_DUPLICATED.getCode());
    }
  }
}