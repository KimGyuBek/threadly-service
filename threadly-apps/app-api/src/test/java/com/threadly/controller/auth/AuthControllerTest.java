package com.threadly.controller.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.ErrorCode;
import com.threadly.controller.auth.request.UserLoginRequest;
import com.threadly.user.UserService;
import com.threadly.user.command.UserRegistrationCommand;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/truncate_all.sql",
    executionPhase = ExecutionPhase.AFTER_TEST_METHOD
)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  public void setupTestUser() {
    String email = "test@test.com";
    String password = "1234";
    String userName = "testUser";
    String phone = "123-1234-1234";

    userService.register(
        UserRegistrationCommand.builder()
            .email(email)
            .userName(userName)
            .password(passwordEncoder.encode(password))
            .phone(phone)
            .build()
    );
  }

  /* login 테스트  */
  /* [Case #1] 로그인 성공  */
  /* [Case #2] 로그인 실패 - 사용자가 없는 경우  */
  @DisplayName("로그인 실패 - 사용자가 없는 경우")
  @Test
  public void login_shouldFail_whenUserNotExists() throws Exception {
    //given
    MvcResult result = sendPostRequest("user@test.com", "1234", "/api/auth/login",
        status().isUnauthorized());

    //when
    //then
    String resultJson = result.getResponse().getContentAsString();
    CommonResponse commonResponse = objectMapper.readValue(resultJson, CommonResponse.class);

    assertError(commonResponse, ErrorCode.USER_AUTHENTICATION_FAILED);
  }


  /* [Case #3] 로그인 실패 - password가 일치하지 않음 */
  @DisplayName("로그인 실패 - 비밀번호가 일치하지 않는 경우")
  @Test
  public void login_shouldFail_whenPasswordNotCorrect() throws Exception {
    //given
    MvcResult result = sendPostRequest("test@test.com", "12345", "/api/auth/login",
        status().isUnauthorized());

    //when
    //then
    String resultJson = result.getResponse().getContentAsString();
    CommonResponse commonResponse = objectMapper.readValue(resultJson, CommonResponse.class);

    assertError(commonResponse, ErrorCode.USER_AUTHENTICATION_FAILED);
  }

  /* [Case #4] 로그인 실패 - email 인증 필요 경우 */
  @DisplayName("로그인 실패 - 이메일 인증이 되지 않은 경우")
  @Test
  public void login_shouldFail_whenEmailNotVerified() throws Exception {
    //given
    MvcResult result = sendPostRequest("test@test.com", "1234", "/api/auth/login",
        status().isUnauthorized());

    // when
    //then
    String resultJson = result.getResponse().getContentAsString();

    CommonResponse<?> response = objectMapper.readValue(resultJson, CommonResponse.class);

    assertError(response, ErrorCode.EMAIL_NOT_VERIFIED);

  }

  private MvcResult sendPostRequest(String email, String password, String url,
      ResultMatcher expectedStatus) throws Exception {

    UserLoginRequest request = UserLoginRequest.builder()
        .email(email)
        .password(password)
        .build();

    String requestJson = objectMapper.writeValueAsString(request);

    MvcResult result = mockMvc.perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
        .andExpect(expectedStatus)
        .andReturn();
    return result;
  }

  private static void assertError(CommonResponse<?> response, ErrorCode errorCode) {
    assertAll(
        () -> assertNotNull(response),
        () -> assertFalse(response.isSuccess()),
        () -> assertThat(response.getCode()).isEqualTo(errorCode.getCode())
    );
  }

  @Getter
  @Setter
  private static class CommonResponse<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

  }
}