package com.threadly.core.service.post.like.comment;

import com.threadly.core.port.post.like.comment.FetchPostCommentLikePort;
import com.threadly.core.usecase.commons.dto.UserPreview;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.port.post.comment.fetch.FetchPostCommentPort;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.usecase.post.like.comment.GetPostCommentLikersQuery;
import com.threadly.core.usecase.post.like.comment.GetPostCommentLikersUseCase;
import com.threadly.core.usecase.post.like.comment.PostCommentLiker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommentLikeQueryService implements GetPostCommentLikersUseCase {

  private final FetchPostCommentLikePort fetchPostCommentLikePort;

  private final FetchPostCommentPort fetchPostCommentPort;

  @Override
  public CursorPageApiResponse<PostCommentLiker> getPostCommentLikers(
      GetPostCommentLikersQuery query) {

    /*댓글 상태 검증*/
    PostCommentStatus commentStatus = fetchPostCommentPort.fetchCommentStatus(
        query.commentId()).orElseThrow(() -> new PostCommentException(
        ErrorCode.POST_COMMENT_NOT_FOUND));

    if (!commentStatus.equals(PostCommentStatus.ACTIVE)) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_NOT_ACCESSIBLE);
    }

    /*리스트 조회*/
    List<PostCommentLiker> allLikerList = fetchPostCommentLikePort.fetchCommentLikerListByCommentIdWithCursor(
        query.commentId(), query.cursorLikedAt(), query.cursorLikerId(),
        query.limit() + 1
    ).stream().map(
        projection -> new PostCommentLiker(
            new UserPreview(
                projection.getLikerId(),
                projection.getLikerNickname(),
                projection.getLikerProfileImageUrl()
            ),
            projection.getLikedAt()
        )
    ).toList();
    return CursorPageApiResponse.from(allLikerList, query.limit());

  }
}
