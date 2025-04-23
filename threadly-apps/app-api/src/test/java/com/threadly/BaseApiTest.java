package com.threadly;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threadly.controller.auth.request.UserLoginRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseApiTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * login 요청
   * @param invalidEmail
   * @param password
   * @return
   * @throws Exception
   */
  public <T> CommonResponse<T> sendLoginRequest(String invalidEmail, String password, TypeReference<CommonResponse<T>> typeRef)
      throws Exception {
    String loginRequest = getLoginRequest(invalidEmail, password);
    CommonResponse<Object> loginResponse = sendPostRequest(loginRequest, "/api/auth/login",
        status().isUnauthorized(),
        new TypeReference<CommonResponse<Object>>() {
        },
        Map.of());
    return (CommonResponse<T>) loginResponse;
  }

  /**
   * get 요청 전송
   *
   * @param accessToken
   * @param path
   * @param expectedStatus
   * @return
   * @throws Exception
   */
  public CommonResponse sendGetRequest(String accessToken, String path,
      ResultMatcher expectedStatus) throws Exception {
    String bearerToken = "Bearer " + accessToken;

    MvcResult result = mockMvc.perform(
        get(path)
            .header("Authorization", bearerToken)
    ).andExpect(expectedStatus).andReturn();

    return
        getResponse(result, new TypeReference<>() {
        });
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

    HttpHeaders httpHeaders = new HttpHeaders();
    headers.forEach((key, value) -> httpHeaders.add(key, value));

    MvcResult result = mockMvc.perform(
        post(path)
            .headers(httpHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson)
    ).andExpect(expectedStatus).andReturn();

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
   * 사용자 등록 후 loginRequest 생성
   *
   * @param email
   * @param password
   * @return
   * @throws JsonProcessingException
   */
  public String getLoginRequest(String email, String password) throws JsonProcessingException {

    String requestJson = objectMapper.writeValueAsString(
        UserLoginRequest.builder()
            .email(email)
            .password(password)
            .build()
    );
    return requestJson;
  }

}
