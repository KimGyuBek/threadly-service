package com.threadly.user;

import java.util.Optional;

public interface InsertUserPort {

  Optional<UserPortResponse> create(CreateUser createUser);


}
