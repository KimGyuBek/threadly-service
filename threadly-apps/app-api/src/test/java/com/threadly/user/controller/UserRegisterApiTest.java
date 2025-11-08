package com.threadly.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.threadly.CommonResponse;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.core.port.user.in.account.command.dto.RegisterUserApiResponse;
import com.threadly.testsupport.fixture.users.UserFixtureLoader;
import com.threadly.user.BaseUserApiTest;
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
 * 사용자 회원가입 관련 API 테스트
 * <p>
 * 테스트 데이터 {/test/resources/fixtures/users/register/}
 */
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class UserRegisterApiTest extends BaseUserApiTest {

  @Autowired
  private UserFixtureLoader userFixtureLoader;

  // 테스트용 사용자 정보
  public static final String NEW_USER_EMAIL = "newuser@threadly.com";
  public static final String NEW_USER_NAME = "new_user";
  public static final String NEW_USER_PASSWORD = "password123!";
  public static final String NEW_USER_PHONE = "010-9999-8888";

  // 중복 이메일 테스트용
  public static final String DUPLICATE_EMAIL = "duplicate@threadly.com";
  public static final String DUPLICATE_USER_NAME = "duplicate_user";

  @BeforeEach
  void setUp() {
    userFixtureLoader.load("/users/register/duplicate-email-user.json");
  }

  /**
   * register() 테스트
   */
  @Order(1)
  @TestClassOrder(ClassOrderer.OrderAnnotation.class)
  @DisplayName("회원가입 테스트")
  @Nested
  class registerTest {

    @Order(1)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("성공")
    class success {

      /*[Case #1] register - 정상적인 회원가입 요청 시 성공해야 한다*/
      @Order(1)
      @DisplayName("1. 정상 회원가입 요청 시 사용자 정보 반환 검증")
      @Test
      public void register_shouldReturnUserInfo_whenValidRequest() throws Exception {
        //given
        //when
        /*회원가입 요청 전송*/
        CommonResponse<RegisterUserApiResponse> registerResponse = sendRegisterUserRequest(
            NEW_USER_EMAIL,
            NEW_USER_NAME,
            NEW_USER_PASSWORD,
            NEW_USER_PHONE,
            new TypeReference<>() {
            },
            status().isOk()
        );

        //then
        assertThat(registerResponse.isSuccess()).isTrue();
        assertThat(registerResponse.getData().getEmail()).isEqualTo(NEW_USER_EMAIL);
        assertThat(registerResponse.getData().getUserName()).isEqualTo(NEW_USER_NAME);
        assertThat(registerResponse.getData().isEmailVerified()).isFalse();
      }
    }

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("실패")
    class fail {

      /*[Case #1] register - 이미 존재하는 이메일로 회원가입 요청 시 409 Conflict 반환해야 한다*/
      @Order(1)
      @DisplayName("1. 이메일 중복 시 409 Conflict 반환")
      @Test
      public void register_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        //given
        //when
        /*중복 이메일로 회원가입 요청 전송*/
        CommonResponse<RegisterUserApiResponse> registerResponse = sendRegisterUserRequest(
            DUPLICATE_EMAIL,
            DUPLICATE_USER_NAME,
            NEW_USER_PASSWORD,
            NEW_USER_PHONE,
            new TypeReference<>() {
            },
            status().isConflict()
        );

        //then
        assertThat(registerResponse.isSuccess()).isFalse();
        assertThat(registerResponse.getCode()).isEqualTo(ErrorCode.USER_ALREADY_EXISTS.getCode());
      }

      /*[Case #2] register - 이메일 중복 검증이 멱등하게 동작해야 한다*/
      @Order(2)
      @DisplayName("2. 이메일 중복 검증 멱등성 확인")
      @Test
      public void register_shouldBeIdempotent_whenDuplicateEmail() throws Exception {
        //given
        //when
        //then
        /*중복 이메일로 여러번 회원가입 요청*/
        for (int i = 0; i < 3; i++) {
          CommonResponse<RegisterUserApiResponse> registerResponse = sendRegisterUserRequest(
              DUPLICATE_EMAIL,
              DUPLICATE_USER_NAME,
              NEW_USER_PASSWORD,
              NEW_USER_PHONE,
              new TypeReference<>() {
              },
              status().isConflict()
          );
          assertThat(registerResponse.isSuccess()).isFalse();
          assertThat(registerResponse.getCode()).isEqualTo(
              ErrorCode.USER_ALREADY_EXISTS.getCode());
        }
      }
    }
  }
}
