package com.threadly.user;

import com.threadly.user.response.UserPortResponse;

/**
 * 사용자 저장 관련 Port
 */
public interface SaveUserPort {

  UserPortResponse save(User user);
}
