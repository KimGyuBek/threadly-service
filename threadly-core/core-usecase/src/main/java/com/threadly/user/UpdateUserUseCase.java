package com.threadly.user;

import com.threadly.user.command.UserSetProfileCommand;
import com.threadly.user.response.UserProfileApiResponse;

public interface UpdateUserUseCase {

  UserProfileApiResponse upsertUserProfile(UserSetProfileCommand userSetProfileCommand);


}
