package com.threadly.auth;

import com.threadly.controller.auth.request.UserLoginRequest;
import com.threadly.token.response.TokenResponse;
import com.threadly.user.FetchUserUseCase;
import com.threadly.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  private final FetchUserUseCase fetchUserUseCase;

  private final JwtTokenProvider jwtTokenProvider;

  /**
   * login
   *
   * @param request
   * @return
   */
  public TokenResponse login(UserLoginRequest request) {

    String email = request.getEmail();
    String password = request.getPassword();

    try {
      /*사용자 조회*/
      UserResponse findUser = fetchUserUseCase.findUserByEmail(email);

      String userId = findUser.getUserId();

      /*인증용 토큰 생성*/
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          userId, password);

      /*인증 시도*/
      Authentication authenticate = authenticationManagerBuilder.getObject()
          .authenticate(authenticationToken);

      /*토큰 생성*/
      TokenResponse tokenResponse = jwtTokenProvider.upsertToken(userId);

      /*SecurityContextHolder에 인증 정보 저장*/
      SecurityContextHolder.getContext().setAuthentication(authenticate);

      return tokenResponse;

    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new RuntimeException("login error");
    }

  }
}
