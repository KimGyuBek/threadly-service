package com.threadly.core.service.post.like.post;

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.core.port.post.in.like.post.query.PostLikeQueryUseCase;
import com.threadly.core.port.post.in.like.post.query.dto.GetPostLikersQuery;
import com.threadly.core.port.post.in.like.post.query.dto.PostLiker;
import com.threadly.core.port.post.out.like.post.PostLikeQueryPort;
import com.threadly.core.service.validator.post.PostValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 좋아요 관련 쿼리 서비스
 */
@Service
@RequiredArgsConstructor
public class PostLikeQueryService implements PostLikeQueryUseCase {

  private final PostValidator postValidator;

  private final PostLikeQueryPort postLikeQueryPort;

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<PostLiker> getPostLikers(GetPostLikersQuery query) {
    /*게시글 유효성 검증*/
    postValidator.validateAccessibleStatusById(query.getPostId());

    /*게시글 좋아요 목록 조회*/
    List<PostLiker> allLikerList = postLikeQueryPort.fetchPostLikersBeforeCreatedAt(
        query.getPostId(),
        query.getCursorLikedAt(),
        query.getCursorLikerId(),
        query.getLimit() + 1
    ).stream().map(
        projection -> new PostLiker(
            new UserPreview(
                projection.getLikerId(),
                projection.getLikerNickname(),
                projection.getLikerProfileImageUrl() == null ? "/"
                    : projection.getLikerProfileImageUrl()
            ),
            projection.getLikedAt()
        )
    ).toList();

    return CursorPageApiResponse.from(allLikerList, query.getLimit());
  }
}
