package com.threadly.user.profile;

import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.user.profile.fetch.FetchUserProfilePort;
import com.threadly.user.profile.fetch.UserProfileProjection;
import com.threadly.user.profile.get.GetUserProfileApiResponse;
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

  @Override
  public void validateNicknameUnique(String nickname) {
    if (fetchUserProfilePort.existsByNickname(nickname)) {
      throw new UserException(ErrorCode.USER_NICKNAME_DUPLICATED);
    }
  }

  @Override
  public GetUserProfileApiResponse getUserProfile(String userId) {
    UserProfileProjection userProfileProjection = fetchUserProfilePort.findUserProfileByUserId(
        userId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
    );

    return new GetUserProfileApiResponse(
        userProfileProjection.getUserId(),
        userProfileProjection.getNickname(),
        userProfileProjection.getStatusMessage(),
        userProfileProjection.getBio(),
        userProfileProjection.getProfileImageUrl()
    );
  }
}
