package com.threadly.config;

import com.threadly.filter.CustomAuthenticationEntryPoint;
import com.threadly.filter.JwtAuthenticationFilter;
import com.threadly.filter.VerificationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final VerificationFilter verificationFilter;

  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.httpBasic(AbstractHttpConfigurer::disable);
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(AbstractHttpConfigurer::disable);
    http.formLogin(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(
        auth -> auth.requestMatchers(
                "/api/users",
                "/api/auth/verify-email",
                "/api/auth/login"
            ).permitAll()
            .anyRequest().authenticated()
    );



    /*VerificationFilter 등록*/
    http.addFilterBefore(verificationFilter, UsernamePasswordAuthenticationFilter.class);

    /*jwt authentication filter 등록*/
    http.addFilterBefore(jwtAuthenticationFilter,
        VerificationFilter.class);

    /*Exception Handling*/
    http.exceptionHandling(
        exception -> exception.authenticationEntryPoint(
            customAuthenticationEntryPoint
        )
    );

    return http.build();
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


}
