package com.threadly;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.auth.request.UserLoginRequest;
import com.threadly.testsupport.fixture.users.UserFixtureLoader;
import com.threadly.utils.TestLogUtils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseApiTest {

  @Autowired
  private UserFixtureLoader userFixtureLoader;

  @BeforeEach
  public void setUpDefaultUser() {
    userFixtureLoader.load("/users/user-email-verified.json");
  }

  @Autowired
  public MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  public static final String PASSWORD = "1234";

  /**
   * login 요청
   *
   * @param expectedStatus
   * @param email
   * @param password
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendLoginRequest(String email, String password,
      TypeReference<CommonResponse<T>> typeRef, ResultMatcher expectedStatus)
      throws Exception {

    String loginRequestBody = generateRequestBody(UserLoginRequest.builder()
        .email(email).password(password).build());
    CommonResponse<T> loginResponse = sendPostRequest(loginRequestBody, "/api/auth/login",
        expectedStatus,
        typeRef,
        Map.of());

    return loginResponse;
  }

  /**
   * 로그아웃 요청
   *
   * @param typeRef
   * @param expectedStatus
   * @param headers
   * @param <T>
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendLogoutRequest(
      TypeReference<CommonResponse<T>> typeRef, ResultMatcher expectedStatus,
      Map<String, String> headers)
      throws Exception {

    CommonResponse<T> loginResponse = sendPostRequest("", "/api/auth/logout",
        expectedStatus,
        typeRef,
        headers
    );
    return loginResponse;
  }

  /**
   * get 요청 전송
   */
  public CommonResponse sendGetRequest(String accessToken, String path,
      ResultMatcher expectedStatus) throws Exception {

    TestLogUtils.log(path + " 요청 전송");

    String bearerToken = "Bearer " + accessToken;

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", bearerToken);
    headers.set("Accept-Charset", "utf-8");

    MvcResult result = mockMvc.perform(
        get(path)
            .headers(headers)
    ).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);

    return
        getResponse(result, new TypeReference<>() {
        });
  }

  public <T> CommonResponse<T> sendGetRequest(String accessToken, String path,
      ResultMatcher expectedStatus, TypeReference<CommonResponse<T>> typeRef) throws Exception {

    TestLogUtils.log(path + " 요청 전송");

    String bearerToken = "Bearer " + accessToken;

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", bearerToken);
    headers.set("Accept-Charset", "utf-8");

    MvcResult result = mockMvc.perform(
        get(path)
            .headers(headers)
    ).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);

    return
        getResponse(result, typeRef);
  }

  /**
   * post 요청 전송
   *
   * @param requestJson
   * @param path
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendPostRequest(String requestJson, String path,
      ResultMatcher expectedStatus, TypeReference<CommonResponse<T>> typeRef,
      Map<String, String> headers) throws Exception {
    TestLogUtils.log(path + " 요청 전송");

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.set("Accept-Charset", "utf-8");
    headers.forEach((key, value) -> httpHeaders.add(key, value));

    MvcResult result = mockMvc.perform(
        post(path)
            .headers(httpHeaders)
            .content(requestJson)
    ).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);

    return getResponse(result, typeRef);
  }

  /**
   * patch 요청 전송
   *
   * @param requestJson
   * @param path
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendPatchRequest(String requestJson, String path,
      ResultMatcher expectedStatus, TypeReference<CommonResponse<T>> typeRef,
      Map<String, String> headers) throws Exception {
    TestLogUtils.log(path + " 요청 전송");

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.set("Accept-Charset", "utf-8");
    headers.forEach((key, value) -> httpHeaders.add(key, value));

    MvcResult result = mockMvc.perform(
        patch(path)
            .headers(httpHeaders)
            .content(requestJson)
    ).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);

    return getResponse(result, typeRef);
  }

  /**
   * delete 요청 전송
   *
   * @param requestJson
   * @param path
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendDeleteRequest(String requestJson, String path,
      ResultMatcher expectedStatus, TypeReference<CommonResponse<T>> typeRef,
      Map<String, String> headers) throws Exception {
    TestLogUtils.log(path + " 요청 전송");

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.set("Accept-Charset", "utf-8");
    headers.forEach((key, value) -> httpHeaders.add(key, value));

    MvcResult result = mockMvc.perform(
        delete(path)
            .headers(httpHeaders)
            .content(requestJson)
    ).andExpect(expectedStatus).andReturn();
    TestLogUtils.log(result);

    return getResponse(result, typeRef);
  }

  /**
   * response -> CommonResponse<T>
   *
   * @param result
   * @return
   * @throws UnsupportedEncodingException
   * @throws JsonProcessingException
   */
  public <T> CommonResponse<T> getResponse(MvcResult result,
      TypeReference<CommonResponse<T>> typeRef)
      throws UnsupportedEncodingException, JsonProcessingException {
    String resultAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    CommonResponse response = objectMapper.readValue(resultAsString, typeRef);
    return response;
  }

  /**
   * request body 생성
   *
   * @param data
   * @param <T>
   * @return
   * @throws JsonProcessingException
   */
  public <T> String generateRequestBody(T data) throws JsonProcessingException {
    return
        objectMapper.writeValueAsString(data);
  }

}
