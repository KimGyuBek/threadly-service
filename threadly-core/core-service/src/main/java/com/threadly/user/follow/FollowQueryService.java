package com.threadly.user.follow;

import com.threadly.commons.dto.UserPreview;
import com.threadly.follow.query.FollowQueryUseCase;
import com.threadly.follow.query.dto.FollowRequestResponse;
import com.threadly.follow.query.dto.FollowerResponse;
import com.threadly.follow.query.dto.FollowingApiResponse;
import com.threadly.follow.query.dto.GetFollowRequestsQuery;
import com.threadly.follow.query.dto.GetFollowersQuery;
import com.threadly.follow.query.dto.GetFollowingsQuery;
import com.threadly.follow.query.dto.GetUserFollowStatsApiResponse;
import com.threadly.response.CursorPageApiResponse;
import com.threadly.validator.follow.FollowAccessValidator;
import com.threadly.validator.user.UserStatusValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 팔로우 조회 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class FollowQueryService implements FollowQueryUseCase {

  private final FollowQueryPort followQueryPort;

  private final FollowAccessValidator followAccessValidator;
  private final UserStatusValidator userStatusValidator;


  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<FollowRequestResponse> getFollowRequestsByCursor(
      GetFollowRequestsQuery query) {
    /*팔로우 요청 목록 조회*/
    List<FollowRequestResponse> allFollowRequestList = followQueryPort.findFollowRequestsByCursor(
        query.userId(),
        query.cursorTimestamp(),
        query.cursorId(),
        query.limit() + 1
    ).stream().map(
        projection -> new FollowRequestResponse(
            projection.getFollowId(),
            new UserPreview(
                projection.getRequesterId(),
                projection.getRequesterNickname(),
                projection.getRequesterProfileImageUrl()
            ),
            projection.getFollowRequestedAt()
        )
    ).toList();

    return CursorPageApiResponse.from(allFollowRequestList, query.limit());
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<FollowerResponse> getFollowers(GetFollowersQuery query) {
    /*접근 가능 여부 검증*/
    followAccessValidator.validateProfileAccessibleWithException(query.userId(),
        query.targetUserId());

    /*팔로워 목록 조회*/
    List<FollowerResponse> allFollowerList = followQueryPort.findFollowersByCursor(
        query.targetUserId(),
        query.cursorTimestamp(),
        query.cursorId(),
        query.limit() + 1
    ).stream().map(
        projection -> new FollowerResponse(
            new UserPreview(
                projection.getFollowerId(),
                projection.getFollowerNickname(),
                projection.getFollowerProfileImageUrl()
            ),
            projection.getFollowedAt()
        )
    ).toList();

    return
        CursorPageApiResponse.from(
            allFollowerList,
            query.limit()
        );
  }

  @Override
  public CursorPageApiResponse<FollowingApiResponse> getFollowings(GetFollowingsQuery query) {
    /*접근 가능 여부 검증*/
    followAccessValidator.validateProfileAccessibleWithException(query.userId(),
        query.targetUserId());

    /*팔로워 목록 조회*/
    List<FollowingApiResponse> allFollowerList = followQueryPort.findFollowingsByCursor(
        query.targetUserId(),
        query.cursorTimestamp(),
        query.cursorId(),
        query.limit() + 1
    ).stream().map(
        projection -> new FollowingApiResponse(
            new UserPreview(
                projection.getFollowingId(),
                projection.getFollowingNickname(),
                projection.getFollowingProfileImageUrl()
            ),
            projection.getFollowedAt()
        )
    ).toList();

    return CursorPageApiResponse.from(allFollowerList, query.limit());
  }

  @Transactional(readOnly = true)
  @Override
  public GetUserFollowStatsApiResponse getUserFollowStats(String userId) {
    /*사용자 statusType 검증*/
    userStatusValidator.validateUserStatusWithException(userId);

    UserFollowStatsProjection userFollowStatsProjection = followQueryPort.getUserFollowStatusByUserId(
        userId);
    return new GetUserFollowStatsApiResponse(
        userFollowStatsProjection.getFollowerCount(),
        userFollowStatsProjection.getFollowingCount()
    );
  }
}
