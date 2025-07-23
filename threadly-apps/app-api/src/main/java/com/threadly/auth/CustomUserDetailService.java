package com.threadly.auth;

import com.threadly.user.get.GetUserUseCase;
import com.threadly.user.get.UserResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

  private final GetUserUseCase getUserUseCase;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserResponse user = getUserUseCase.findUserByUserId(username);

    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority(user.getUserType().name())
    );

    return new LoginAuthenticationUser(
        user.getUserId(),
        user.getPassword(),
        authorities
    );
  }
}
