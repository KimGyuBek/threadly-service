package com.threadly.post.like.comment;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostCommentException;
import com.threadly.post.comment.fetch.FetchPostCommentPort;
import com.threadly.post.like.comment.GetPostCommentLikersApiResponse.NextCursor;
import com.threadly.post.like.comment.GetPostCommentLikersApiResponse.PostCommentLiker;
import com.threadly.posts.PostCommentStatusType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommentLikeQueryService implements GetPostCommentLikersUseCase {

  private final FetchPostCommentLikePort fetchPostCommentLikePort;

  private final FetchPostCommentPort fetchPostCommentPort;

  @Override
  public GetPostCommentLikersApiResponse getPostCommentLikers(GetPostCommentLikersQuery query) {

    /*댓글 상태 검증*/
    PostCommentStatusType commentStatus = fetchPostCommentPort.fetchCommentStatus(
        query.commentId()).orElseThrow(() -> new PostCommentException(
        ErrorCode.POST_COMMENT_NOT_FOUND));

    if (!commentStatus.equals(PostCommentStatusType.ACTIVE)) {
      throw new PostCommentException(ErrorCode.POST_COMMENT_NOT_ACCESSIBLE);
    }

    /*리스트 조회*/
    List<GetPostCommentLikersApiResponse.PostCommentLiker> allLikerList = fetchPostCommentLikePort.fetchCommentLikerListByCommentIdWithCursor(
        query.commentId(), query.cursorLikedAt(), query.cursorLikerId(),
        query.limit() + 1
    ).stream().map(
        projection -> new PostCommentLiker(
            projection.getLikerId(),
            projection.getLikerNickname(),
            projection.getLikerProfileImageUrl(),
            projection.getLikerBio(),
            projection.getLikedAt()
        )
    ).toList();

    /*다음 페이지가 있는지 검증*/
    boolean hasNext = allLikerList.size() > query.limit();

    List<PostCommentLiker> pagedCommentLikerList = hasNext ? allLikerList.subList(0,
        query.limit()) : allLikerList;

    /*커서 지정*/
    LocalDateTime cursorLikedAt = hasNext ? pagedCommentLikerList.getLast().likedAt() : null;
    String cursorLikerId = hasNext ? pagedCommentLikerList.getLast().likerId() : null;

    return new GetPostCommentLikersApiResponse(
        pagedCommentLikerList,
        new NextCursor(cursorLikedAt, cursorLikerId)
    );

  }
}
