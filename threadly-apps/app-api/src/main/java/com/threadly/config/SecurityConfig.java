package com.threadly.config;

import com.threadly.global.filter.CustomAuthenticationEntryPoint;
import com.threadly.global.filter.JwtAuthenticationFilter;
import com.threadly.global.filter.UserStatusTypeValidationFilter;
import com.threadly.global.filter.VerificationFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserStatusTypeValidationFilter userStatusTypeValidationFilter;
  private final VerificationFilter verificationFilter;

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    disableDefaultSecurity(http);
    configureAuthorization(http);
    configureFilters(http);
    configureExceptionHandling(http);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList(
        "https://threadly.kr",
        "http://localhost:5173",
        "http://127.0.0.1:5173"
    ));
    config.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
    config.setAllowedHeaders(
        Arrays.asList("Authorization", "Accept", "Content-Type", "Origin", "X-Requested-With"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 예외 핸들링 설정
   *
   * @param http
   * @throws Exception
   */
  private void configureExceptionHandling(HttpSecurity http) throws Exception {
    http.exceptionHandling(
        exception -> exception.authenticationEntryPoint(
            customAuthenticationEntryPoint
        ));
  }

  /**
   * 커스텀 필터 등록
   *
   * @param http
   */
  private void configureFilters(HttpSecurity http) {
    /*VerificationFilter*/
    http.addFilterBefore(verificationFilter, UsernamePasswordAuthenticationFilter.class);

    /*UserStatusTypeValidationFilter*/
    http.addFilterBefore(userStatusTypeValidationFilter, VerificationFilter.class);

    /*jwt authentication filter*/
    http.addFilterBefore(jwtAuthenticationFilter, UserStatusTypeValidationFilter.class);
  }

  /**
   * 인가 설정
   *
   * @param http
   * @throws Exception
   */
  private static void configureAuthorization(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        auth -> auth.requestMatchers(
                "/actuator/**",
                "/api/users",
                "/api/auth/verify-email",
                "/api/auth/login",
                "/api/auth/reissue",
                "/images/**",
                "/test/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            ).permitAll()

            .requestMatchers(HttpMethod.OPTIONS).permitAll()

            .anyRequest().authenticated()
    );
  }

  /**
   * 디폴트 설정 비활성화
   *
   * @param http
   * @throws Exception
   */
  private static void disableDefaultSecurity(HttpSecurity http) throws Exception {
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(Customizer.withDefaults());
//    http.cors(AbstractHttpConfigurer::disable);
    http.formLogin(AbstractHttpConfigurer::disable);
  }


}
