package com.threadly.core.service.post.like.comment;

import com.threadly.core.port.post.out.like.comment.PostCommentLikeQueryPort;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostCommentException;
import com.threadly.core.domain.post.PostCommentStatus;
import com.threadly.core.port.post.out.comment.PostCommentQueryPort;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.like.comment.query.dto.GetPostCommentLikersQuery;
import com.threadly.core.port.post.in.like.comment.query.PostCommentLikeQueryUseCase;
import com.threadly.core.port.post.in.like.comment.query.dto.PostCommentLiker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommentLikeQueryService implements PostCommentLikeQueryUseCase {

  private final PostCommentLikeQueryPort postCommentLikeQueryPort;

  private final PostCommentQueryPort postCommentQueryPort;

  @Override
  public CursorPageApiResponse<PostCommentLiker> getPostCommentLikers(
      GetPostCommentLikersQuery query) {

    /*댓글 상태 검증*/
    PostCommentStatus commentStatus = postCommentQueryPort.fetchCommentStatus(
        query.commentId()).orElseThrow(() -> new PostCommentException(
        ErrorCode.POST_COMMENT_NOT_FOUND));

    if (!commentStatus.equals(PostCommentStatus.ACTIVE)) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_NOT_ACCESSIBLE);
    }

    /*리스트 조회*/
    List<PostCommentLiker> allLikerList = postCommentLikeQueryPort.fetchCommentLikerListByCommentIdWithCursor(
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
