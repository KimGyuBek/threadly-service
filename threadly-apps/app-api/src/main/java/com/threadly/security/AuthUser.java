package com.threadly.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class AuthUser extends User {

//  private String userName;
  private String email;
//  private String password;
  private String phone;

  public AuthUser(String username, String password,
      Collection<? extends GrantedAuthority> authorities, String email, String phone) {
    super(username, password, authorities);
//    this.userName = username;
//    this.password = password;
    this.email = email;
    this.phone = phone;
  }
}
