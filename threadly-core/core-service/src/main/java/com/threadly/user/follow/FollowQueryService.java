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

  @Transactional
  @Override
  public GetFollowersApiResponse getFollowers(GetFollowersQuery query) {
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

}
