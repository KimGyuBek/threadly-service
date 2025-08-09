package com.threadly.global.filter;

import com.google.common.base.Objects;
import com.threadly.auth.JwtAuthenticationUser;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.user.UserException;
import com.threadly.global.exception.UserAuthenticationException;
import com.threadly.core.domain.user.UserStatusType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 사용자 UserStatusType 검증 필터
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserStatusTypeValidationFilter extends OncePerRequestFilter {

  private final AuthenticationEntryPoint authenticationEntryPoint;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return FilterBypassMatcher.shouldBypass(request.getRequestURI());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    JwtAuthenticationUser principal = (JwtAuthenticationUser) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    UserStatusType userStatusType = principal.getUserStatusType();

    /*UserStatusType 검증*/
    try {
      /*INCOMPLETE_PROFILE*/
      if (Objects.equal(userStatusType,
          UserStatusType.INCOMPLETE_PROFILE) && !isWhiteListedForProfileIncomplete(request)) {
        log.warn("UserStatusValidationFilter: userStatusType is INCOMPLETE_PROFILE");
        throw new UserException(ErrorCode.USER_PROFILE_NOT_SET);

        /*INACTIVE*/
      } else if (Objects.equal(userStatusType, UserStatusType.INACTIVE)) {
        log.warn("UserStatusValidationFilter: userStatusType is USER INACTIVE");
        throw new UserException(ErrorCode.USER_INACTIVE);
      }
    } catch (UserException e) {
      UserAuthenticationException exception = new UserAuthenticationException(
          e.getErrorCode());
      authenticationEntryPoint.commence(request, response, exception);
      return;
    }
    filterChain.doFilter(request, response);
  }

  /**
   * 프로필 설정용 white list
   *
   * @param request
   * @return
   */
  private boolean isWhiteListedForProfileIncomplete(HttpServletRequest request) {
    return request.getRequestURI().equals("/api/me/profile") && request.getMethod()
        .equalsIgnoreCase("POST");
  }
}
