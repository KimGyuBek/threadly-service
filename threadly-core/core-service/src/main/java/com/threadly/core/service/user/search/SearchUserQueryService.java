package com.threadly.core.service.user.search;

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.domain.follow.FollowStatus;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.core.port.user.in.search.SearchUserQueryUseCase;
import com.threadly.core.port.user.in.search.dto.UserSearchItem;
import com.threadly.core.port.user.in.search.dto.UserSearchQuery;
import com.threadly.core.port.user.out.search.SearchUserQueryPort;
import com.threadly.core.port.user.out.search.UserSearchProjection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 검색 관련 query service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchUserQueryService implements SearchUserQueryUseCase {

  private final SearchUserQueryPort searchUserQueryPort;

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<UserSearchItem> searchByKeyword(UserSearchQuery query) {
    /*1. 조회*/
    List<UserSearchProjection> userSearchProjections = searchUserQueryPort.searchByKeyword(
        query.userId(),
        query.keyword(),
        query.cursorNickname(),
        query.limit() + 1
    );

    /*2. 응답 생성*/
    return
        CursorPageApiResponse.from(
            userSearchProjections.stream().map(
                projection -> new UserSearchItem(
                    new UserPreview(
                        projection.getUserId(),
                        projection.getUserNickname(),
                        projection.getUserProfileImageUrl()
                    ),
                    (query.userId().equals(projection.getUserId()))
                        ? FollowStatus.SELF
                        : projection.getFollowStatus()
                )
            ).toList(),
            query.limit()
        );
  }
}
