package com.threadly.user;

import com.threadly.user.response.UserPortResponse;
import java.util.Optional;

public interface InsertUserPort {

  Optional<UserPortResponse> create(CreateUser createUser);


}
