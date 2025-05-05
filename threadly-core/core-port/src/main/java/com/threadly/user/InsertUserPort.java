package com.threadly.user;

import com.threadly.user.response.UserPortResponse;
import java.util.Optional;

public interface InsertUserPort {

  UserPortResponse create(User user);

  void saveUserProfile(User user, UserProfile userProfile);

  void saveUser(User user);


}
