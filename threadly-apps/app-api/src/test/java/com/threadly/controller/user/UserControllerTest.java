package com.threadly.controller.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.BaseApiTest;
import com.threadly.CommonResponse;
import com.threadly.auth.token.response.LoginTokenResponse;
import com.threadly.controller.user.request.UserProfileRequest;
import com.threadly.user.UserGenderType;
import com.threadly.user.response.UserProfileApiResponse;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * UserController Test
 */
class UserControllerTest extends BaseApiTest {

  @Test
  public void setUserProfile_shouldCreateOrUpdateProfile_whenAuthenticatedUserRequests() throws Exception {
    //given
    /*로그인 요청 전송*/
    CommonResponse<LoginTokenResponse> loginResponse = sendLoginRequest(
        VERIFIED_USER_EMAILS.getFirst(), PASSWORD, new TypeReference<CommonResponse<LoginTokenResponse>>() {
        }, status().isOk()
    );

    String accessToken = loginResponse.getData().accessToken();

    //when
    String nickname = "nickname";
    String statusMessage = "statusMessage";
    String bio = "bio";
    UserGenderType gender = UserGenderType.MALE;
    String profileImageUrl = "";
    String requestBody = generateRequestBody(
        new UserProfileRequest(
            nickname,
            statusMessage,
            bio,
            gender,
            profileImageUrl
        )
    );

    /*프로필 변경 요청 전송*/
    CommonResponse<UserProfileApiResponse> response = sendPostRequest(
        requestBody,
        "/api/users/profile",
        status().isCreated(),
        new TypeReference<CommonResponse<UserProfileApiResponse>>() {
        },
        Map.of("Authorization", "Bearer " + accessToken)
    );

    //then
    /*로그인 응답 검증*/
    assertAll(
        () -> assertTrue(loginResponse.isSuccess()),
        () -> assertNotNull(loginResponse.getData().accessToken())
    );

    /*프로필 응답 검증*/
    assertAll(
        () -> assertTrue(response.isSuccess()),
        () -> assertThat(response.getData().nickname()).isEqualTo(nickname),
        () -> assertThat(response.getData().statusMessage()).isEqualTo(statusMessage),
        () -> assertThat(response.getData().bio()).isEqualTo(bio),
        () -> assertThat(response.getData().gender()).isEqualTo(gender.name()),
        () -> assertThat(response.getData().profileImageUrl()).isEqualTo(profileImageUrl)
    );

  }

}