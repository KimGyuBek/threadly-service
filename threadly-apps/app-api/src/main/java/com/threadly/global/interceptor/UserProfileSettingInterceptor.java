package com.threadly.global.interceptor;

import com.google.common.base.Objects;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.commons.security.JwtTokenProvider;
import com.threadly.core.domain.user.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserProfileSettingInterceptor implements HandlerInterceptor {


  private final JwtTokenProvider jwtTokenProvider;

  public UserProfileSettingInterceptor(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    /*요청 검증*/
    if (!request.getRequestURI().equals("/api/me/profile") || !request.getMethod()
        .equalsIgnoreCase("POST")) {
      return true;
    }

    /*인증 되지 않은 요청일경우*/
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return true;
    }

    String userStatusType = jwtTokenProvider.getUserStatusType(
        jwtTokenProvider.resolveToken(request));

    if (!Objects.equal(userStatusType,
        UserStatus.INCOMPLETE_PROFILE.name())) {
      throw new UserException(ErrorCode.USER_PROFILE_ALREADY_SET);
    }

    return true;
  }

}
