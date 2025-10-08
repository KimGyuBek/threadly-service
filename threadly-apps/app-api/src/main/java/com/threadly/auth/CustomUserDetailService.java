package com.threadly.auth;

import com.threadly.core.port.user.in.query.UserQueryUseCase;
import com.threadly.core.port.user.in.shared.UserResponse;
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

  private final UserQueryUseCase userQueryUseCase;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserResponse user = userQueryUseCase.findUserByUserId(username);

    List<SimpleGrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority(user.getUserRoleType().name())
    );

    return new LoginAuthenticationUser(
        user.getUserId(),
        user.getPassword(),
        authorities
    );
  }
}
