package com.threadly.user.profile;

import com.threadly.commons.dto.UserPreview;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.user.UserException;
import com.threadly.user.FollowStatusType;
import com.threadly.user.UserStatusType;
import com.threadly.user.profile.fetch.FetchUserProfilePort;
import com.threadly.user.profile.fetch.MyProfileDetailsProjection;
import com.threadly.user.profile.fetch.UserProfileProjection;
import com.threadly.user.profile.get.GetMyProfileDetailsApiResponse;
import com.threadly.user.profile.get.GetMyProfileUseCase;
import com.threadly.user.profile.get.GetUserProfileApiResponse;
import com.threadly.user.profile.get.GetUserProfileUseCase;
import com.threadly.validator.follow.FollowAccessValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * userprofile 관련 조회 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileQueryService implements GetUserProfileUseCase, GetMyProfileUseCase {

  private final FetchUserProfilePort fetchUserProfilePort;

  private final FollowAccessValidator followAccessValidator;

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

  @Transactional(readOnly = true)
  @Override
  public GetUserProfileApiResponse getUserProfile(String userId, String targetUserId) {
    UserProfileProjection userProfileProjection = fetchUserProfilePort.findUserProfileByUserId(
        targetUserId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
    );

    /*사용자 상태 검증*/
    if (userProfileProjection.getUserStatus().equals(UserStatusType.DELETED)) {
      throw new UserException(ErrorCode.USER_ALREADY_DELETED);
    } else if (userProfileProjection.getUserStatus().equals(UserStatusType.INACTIVE)) {
      throw new UserException(ErrorCode.USER_INACTIVE);
    }

    /*팔로우 유무 검증*/
    FollowStatusType followStatusType = followAccessValidator.validateProfileAccessible(userId,
        targetUserId);

    return new GetUserProfileApiResponse(
        new UserPreview(
            userProfileProjection.getUserId(),
            userProfileProjection.getNickname(),
            userProfileProjection.getProfileImageUrl()
        ),
        userProfileProjection.getStatusMessage(),
        userProfileProjection.getBio(),
        followStatusType
    );
  }


  @Override
  public GetMyProfileDetailsApiResponse getMyProfileDetails(String userId) {
    /*사용자 상태 검증*/
    MyProfileDetailsProjection myProfileDetailsProjection = fetchUserProfilePort.findMyProfileDetailsByUserId(
        userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    /*사용자 상태 검증*/
    if (myProfileDetailsProjection.getStatus().equals(UserStatusType.DELETED)) {
      throw new UserException(ErrorCode.USER_ALREADY_DELETED);
    }

    /*응답 생성 후 리턴*/
    return new GetMyProfileDetailsApiResponse(
        userId,
        myProfileDetailsProjection.getNickname(),
        myProfileDetailsProjection.getStatusMessage(),
        myProfileDetailsProjection.getBio(),
        myProfileDetailsProjection.getPhone(),
        myProfileDetailsProjection.getProfileImageId(),
        myProfileDetailsProjection.getProfileImageUrl()
    );
  }
}
