package com.threadly.core.port.user;

import com.threadly.core.port.user.response.UserPortResponse;
import com.threadly.core.domain.user.User;

/**
 * 사용자 저장 관련 Port
 */
public interface SaveUserPort {

  UserPortResponse save(User user);
}
