package com.threadly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    httpSecurity.cors(AbstractHttpConfigurer::disable);

    /*모든 요청 허용*/
    httpSecurity.authorizeHttpRequests(
        auth -> auth.anyRequest().permitAll()
    );

    return httpSecurity.build();

  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


}
