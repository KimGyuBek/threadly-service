package com.threadly.core.service.post.comment;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.post.PostException;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.core.port.post.in.comment.query.PostCommendQueryUseCase;
import com.threadly.core.port.post.in.comment.query.dto.GetPostCommentApiResponse;
import com.threadly.core.port.post.in.comment.query.dto.GetPostCommentDetailQuery;
import com.threadly.core.port.post.in.comment.query.dto.GetPostCommentListQuery;
import com.threadly.core.port.post.out.comment.PostCommentQueryPort;
import com.threadly.core.service.post.validator.PostValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 댓글 조회 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PostCommentQueryService implements PostCommendQueryUseCase {

  private final PostValidator postValidator;

  private final PostCommentQueryPort postCommentQueryPort;

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<GetPostCommentApiResponse> getPostCommentDetailListForUser(
      GetPostCommentListQuery query
  ) {
    /*게시글 유효성 검증*/
    try {
      postValidator.validateAccessibleStatusById(query.postId());
    } catch (PostException ex) {
      /*게시글이 존재하지 않는 경우*/
      if (ex.getErrorCode() == ErrorCode.POST_NOT_FOUND) {
        throw ex;
      }

      throw new PostException(ErrorCode.POST_NOT_ACCESSIBLE);
    }

    /*게시글 댓글 목록 조회*/
    List<GetPostCommentApiResponse> allCommentList = postCommentQueryPort.fetchCommentListByPostIdWithCursor(
        query.postId(),
        query.userId(),
        query.cursorCommentedAt(),
        query.cursorCommentId(),
        query.limit() + 1
    ).stream().map(
        projection -> new GetPostCommentApiResponse(
            projection.getPostId(),
            projection.getCommentId(),
            new UserPreview(
                projection.getCommenterId(),
                projection.getCommenterNickname(),
                projection.getCommenterProfileImageUrl() == null ? "/"
                    : projection.getCommenterProfileImageUrl()
            ),
            projection.getCommentedAt(),
            projection.getLikeCount(),
            projection.getContent(),
            projection.isLiked()
        )
    ).toList();

    return CursorPageApiResponse.from(allCommentList, query.limit());
  }

  @Override
  public GetPostCommentApiResponse getPostCommentDetailForUser(
      GetPostCommentDetailQuery query) {

    /*게시글 상태 검증*/

    /*게시글 댓글 상태 검증*/

    /*대댓글 목록 조회*/

    return null;
  }
}

