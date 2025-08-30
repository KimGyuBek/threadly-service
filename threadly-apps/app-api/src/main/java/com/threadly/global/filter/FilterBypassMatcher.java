package com.threadly.global.filter;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * filter 제외 경로
 */
public class FilterBypassMatcher {

  public static final List<String> WHITE_LIST = List.of(
      "/api/users",
      "/actuator/**",
      "/api/auth/verify-email",
      "/api/auth/login",
      "/api/auth/reissue",
      "/images/**"
  );

  private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

  public static boolean shouldBypass(String uri) {
    return WHITE_LIST.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
  }


}
