package com.threadly.user.follow;

import com.threadly.commons.dto.UserPreview;
import com.threadly.user.follow.get.FollowQueryUseCase;
import com.threadly.user.follow.get.GetFollowRequestsApiResponse;
import com.threadly.user.follow.get.GetFollowRequestsApiResponse.FollowRequestDetails;
import com.threadly.user.follow.get.GetFollowRequestsApiResponse.NextCursor;
import com.threadly.user.follow.get.GetFollowRequestsQuery;
import com.threadly.user.follow.get.GetFollowersApiResponse;
import com.threadly.user.follow.get.GetFollowersApiResponse.FollowerDetails;
import com.threadly.user.follow.get.GetFollowersQuery;
import com.threadly.user.follow.get.GetFollowingsApiResponse;
import com.threadly.user.follow.get.GetFollowingsApiResponse.FollowingDetails;
import com.threadly.user.follow.get.GetFollowingsQuery;
import com.threadly.validator.follow.FollowAccessValidator;
import java.time.LocalDateTime;
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

  @Transactional(readOnly = true)
  @Override
  public GetFollowRequestsApiResponse getFollowRequestsByCursor(GetFollowRequestsQuery query) {
    /*팔로우 요청 목록 조회*/
    List<FollowRequestDetails> allFollowRequestList = followQueryPort.findFollowRequestsByCursor(
        query.userId(),
        query.cursorFollowRequestedAt(),
        query.cursorFollowId(),
        query.limit() + 1
    ).stream().map(
        projection -> new FollowRequestDetails(
            projection.getFollowId(),
            new UserPreview(
                projection.getRequesterId(),
                projection.getRequesterNickname(),
                projection.getRequesterProfileImageUrl()
            ),
            projection.getFollowRequestedAt()
        )
    ).toList();

    /*다음 페이지가 있는지 검증*/
    boolean hasNext = allFollowRequestList.size() > query.limit();

    /*리스트 분할*/
    List<FollowRequestDetails> pagedList =
        hasNext ? allFollowRequestList.subList(0, query.limit()) : allFollowRequestList;

    /*커서 지정*/
    LocalDateTime cursorFollowRequestedAt = hasNext ? pagedList.getLast()
        .followRequestedAt() : null;
    String cursorFollowId = hasNext ? pagedList.getLast().followId() : null;

    return new GetFollowRequestsApiResponse(
        pagedList,
        new NextCursor(cursorFollowRequestedAt, cursorFollowId)
    );
  }

  @Transactional(readOnly = true)
  @Override
  public GetFollowersApiResponse getFollowers(GetFollowersQuery query) {
    /*접근 가능 여부 검증*/
    followAccessValidator.validateProfileAccessible(query.userId(), query.targetUserId());

    /*팔로워 목록 조회*/
    List<FollowerDetails> allFollowerList = followQueryPort.findFollowersByCursor(
        query.targetUserId(),
        query.cursorFollowedAt(),
        query.cursorFollowerId(),
        query.limit() + 1
    ).stream().map(
        projection -> new FollowerDetails(
            new UserPreview(
                projection.getFollowerId(),
                projection.getFollowerNickname(),
                projection.getFollowerProfileImageUrl()
            ),
            projection.getFollowedAt()
        )
    ).toList();

    /*다음 페이지가 있는지 검증*/
    boolean hasNext = allFollowerList.size() > query.limit();

    /*리스트 분할*/
    List<FollowerDetails> pagedList =
        hasNext ? allFollowerList.subList(0, query.limit()) : allFollowerList;

    /*커서 지정*/
    LocalDateTime cursorFollowedAt = hasNext ? pagedList.getLast().followedAt() : null;
    String cursorFollowerId = hasNext ? pagedList.getLast().follower().userId() : null;

    return new GetFollowersApiResponse(
        pagedList,
        new com.threadly.user.follow.get.GetFollowersApiResponse.NextCursor(
            cursorFollowedAt,
            cursorFollowerId));
  }

  @Override
  public GetFollowingsApiResponse getFollowings(GetFollowingsQuery query) {
    /*접근 가능 여부 검증*/
    followAccessValidator.validateProfileAccessible(query.userId(), query.targetUserId());

    /*팔로워 목록 조회*/
    List<FollowingDetails> allFollowerList = followQueryPort.findFollowingsByCursor(
        query.targetUserId(),
        query.cursorFollowedAt(),
        query.cursorFollowingId(),
        query.limit() + 1
    ).stream().map(
        projection -> new FollowingDetails(
            new UserPreview(
                projection.getFollowingId(),
                projection.getFollowingNickname(),
                projection.getFollowingProfileImageUrl()
            ),
            projection.getFollowedAt()
        )
    ).toList();

    /*다음 페이지가 있는지 검증*/
    boolean hasNext = allFollowerList.size() > query.limit();

    /*리스트 분할*/
    List<FollowingDetails> pagedList =
        hasNext ? allFollowerList.subList(0, query.limit()) : allFollowerList;

    /*커서 지정*/
    LocalDateTime cursorFollowedAt = hasNext ? pagedList.getLast().followedAt() : null;
    String cursorFollowerId = hasNext ? pagedList.getLast().following().userId() : null;

    return new GetFollowingsApiResponse(
        pagedList,
        new com.threadly.user.follow.get.GetFollowingsApiResponse.NextCursor(
            cursorFollowedAt,
            cursorFollowerId));
  }
}
