package com.threadly.user.profile;

import com.threadly.user.profile.fetch.FetchUserProfilePort;
import com.threadly.user.profile.get.GetUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * userprofile 관련 조회 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileQueryService implements GetUserProfileUseCase {

  private final FetchUserProfilePort fetchUserProfilePort;

  @Override
  public boolean existsUserProfile(String userId) {
    return fetchUserProfilePort.existsUserProfileByUserId(userId);
  }
}
