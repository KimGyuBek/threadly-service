package com.threadly.user;

import com.threadly.user.command.UserSetProfileCommand;
import com.threadly.user.response.UserProfileSetupApiResponse;

public interface UpdateUserUseCase {

  void upsertUserProfile(UserSetProfileCommand userSetProfileCommand);


}
