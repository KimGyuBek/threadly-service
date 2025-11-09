package com.threadly.core.service.post.like.comment;

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.core.port.post.in.like.comment.query.PostCommentLikeQueryUseCase;
import com.threadly.core.port.post.in.like.comment.query.dto.GetPostCommentLikersQuery;
import com.threadly.core.port.post.in.like.comment.query.dto.PostCommentLiker;
import com.threadly.core.port.post.out.like.comment.PostCommentLikeQueryPort;
import com.threadly.core.service.post.validator.PostCommentValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommentLikeQueryService implements PostCommentLikeQueryUseCase {

  private final PostCommentValidator postCommentValidator;

  private final PostCommentLikeQueryPort postCommentLikeQueryPort;

  @Override
  public CursorPageApiResponse<PostCommentLiker> getPostCommentLikers(
      GetPostCommentLikersQuery query) {

    /*댓글 상태 검증*/
    postCommentValidator.validateAccessibleStatus(query.commentId());

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
