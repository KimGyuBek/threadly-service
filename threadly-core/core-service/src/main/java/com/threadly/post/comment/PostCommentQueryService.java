package com.threadly.post.comment;

import com.threadly.commons.dto.UserPreview;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.post.PostStatus;
import com.threadly.post.comment.fetch.FetchPostCommentPort;
import com.threadly.post.comment.get.GetPostCommentApiResponse;
import com.threadly.post.comment.get.GetPostCommentDetailQuery;
import com.threadly.post.comment.get.GetPostCommentListQuery;
import com.threadly.post.comment.get.GetPostCommentUseCase;
import com.threadly.post.fetch.FetchPostPort;
import com.threadly.response.CursorPageApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 댓글 조회 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PostCommentQueryService implements GetPostCommentUseCase {

  private final FetchPostCommentPort fetchPostCommentPort;

  private final FetchPostPort fetchPostPort;

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<GetPostCommentApiResponse> getPostCommentDetailListForUser(
      GetPostCommentListQuery query
  ) {
    /*게시글 유효성 검증*/
    PostStatus posStatus = fetchPostPort.fetchPostStatusByPostId(query.postId())
        .orElseThrow(() -> new PostException(
            ErrorCode.POST_NOT_FOUND));
    if (!posStatus.equals(PostStatus.ACTIVE)) {
      throw new PostException(ErrorCode.POST_NOT_ACCESSIBLE);
    }

    /*게시글 댓글 목록 조회*/
    List<GetPostCommentApiResponse> allCommentList = fetchPostCommentPort.fetchCommentListByPostIdWithCursor(
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
                projection.getCommenterProfileImageUrl()
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

