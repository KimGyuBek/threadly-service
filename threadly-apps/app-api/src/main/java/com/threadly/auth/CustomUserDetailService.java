package com.threadly.auth;

import com.threadly.user.FetchUserUseCase;
import com.threadly.user.response.UserResponse;
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

  private final FetchUserUseCase fetchUserUseCase;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserResponse result = fetchUserUseCase.findUserByUserId(username);

    return
        new AuthUser(
            result.getUserId(),
            result.getPassword(),
            List.of(
                new SimpleGrantedAuthority("ROLE_USER")
            ),
            result.getEmail(),
            result.getPhone()
        );
  }
}
