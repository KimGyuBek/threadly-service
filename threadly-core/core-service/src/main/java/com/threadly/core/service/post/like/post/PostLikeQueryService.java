package com.threadly.core.service.post.like.post;

import com.threadly.core.port.post.like.post.FetchPostLikePort;
import com.threadly.core.usecase.commons.dto.UserPreview;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.core.port.post.fetch.FetchPostPort;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.usecase.post.like.post.GetPostLikersQuery;
import com.threadly.core.usecase.post.like.post.GetPostLikersUseCase;
import com.threadly.core.usecase.post.like.post.PostLiker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 좋아요 관련 쿼리 서비스
 */
@Service
@RequiredArgsConstructor
public class PostLikeQueryService implements GetPostLikersUseCase {

  private final FetchPostLikePort fetchPostLikePort;

  private final FetchPostPort fetchPostPort;

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<PostLiker> getPostLikers(GetPostLikersQuery query) {
    /*게시글 유효성 검증*/
    if (!fetchPostPort.existsById(query.getPostId())) {
      throw new PostException(ErrorCode.POST_NOT_FOUND);
    }

    /*게시글 좋아요 목록 조회*/
    List<PostLiker> allLikerList = fetchPostLikePort.fetchPostLikersBeforeCreatedAt(
        query.getPostId(),
        query.getCursorLikedAt(),
        query.getCursorLikerId(),
        query.getLimit() + 1
    ).stream().map(
        projection -> new PostLiker(
            new UserPreview(
                projection.getLikerId(),
                projection.getLikerNickname(),
                projection.getLikerProfileImageUrl()
            ),
            projection.getLikedAt()
        )
    ).toList();

    return CursorPageApiResponse.from(allLikerList, query.getLimit());
  }
}
