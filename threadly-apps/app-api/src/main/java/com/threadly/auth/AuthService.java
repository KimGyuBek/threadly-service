package com.threadly.auth;

import com.threadly.controller.request.UserLoginRequest;
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

  /**
   * login
   *
   * @param request
   * @return
   */
  /*TODO*/
  /*return 정보 부족함*/
  /*jwt 추가 */
  public boolean login(UserLoginRequest request) {

    String email = request.getEmail();
    String password = request.getPassword();

    try {
      /*인증용 토큰 생성*/
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          email, password);

      /*인증 시도*/
      Authentication authenticate = authenticationManagerBuilder.getObject()
          .authenticate(authenticationToken);

      /*SecurityContext에 인증 정보 저장*/
      SecurityContextHolder.getContext().setAuthentication(authenticate);
      return true;

    } catch (Exception e) {
      return false;
    }

  }
}
