package com.threadly.post.like.post;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.post.fetch.FetchPostPort;
import com.threadly.post.like.post.GetPostLikersApiResponse.PostLiker;
import java.time.LocalDateTime;
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
  public GetPostLikersApiResponse getPostLikers(GetPostLikersQuery query) {
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
            projection.getLikerId(),
            projection.getLikerNickname(),
            projection.getLikerProfileImageUrl(),
            projection.getLikerBio(),
            projection.getLikedAt()
        )
    ).toList();

    /*다음 페이지가 있는지 검증*/
    boolean hasNext = allLikerList.size() > query.getLimit();

    /*리스트 분할*/
    List<PostLiker> pagedLikerList = hasNext
        ? allLikerList.subList(0, query.getLimit())
        : allLikerList;

    /*커서 지정*/
    LocalDateTime cursorLikedAt = hasNext
        ? pagedLikerList.getLast().likedAt()
        : null;
    String cursorLikerId = hasNext
        ? pagedLikerList.getLast().likerId()
        : null;

    return new GetPostLikersApiResponse(
        pagedLikerList,
        cursorLikedAt,
        cursorLikerId
    );
  }
}
