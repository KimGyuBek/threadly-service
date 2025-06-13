package com.threadly.post;

import static com.threadly.post.PostStatusType.ARCHIVE;
import static com.threadly.post.PostStatusType.BLOCKED;
import static com.threadly.post.PostStatusType.DELETED;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.post.engagement.GetPostEngagementUseCase;
import com.threadly.post.fetch.FetchPostPort;
import com.threadly.post.fetch.PostDetailProjection;
import com.threadly.post.get.GetPostUseCase;
import com.threadly.post.engagement.GetPostEngagementQuery;
import com.threadly.post.get.GetPostListQuery;
import com.threadly.post.get.GetPostQuery;
import com.threadly.post.get.GetPostDetailApiResponse;
import com.threadly.post.get.GetPostDetailListApiResponse;
import com.threadly.post.get.GetPostDetailListApiResponse.NextCursor;
import com.threadly.post.engagement.GetPostEngagementApiResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 조회 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PostQueryService implements GetPostUseCase, GetPostEngagementUseCase {

  private final FetchPostPort fetchPostPort;

  @Transactional(readOnly = true)
  @Override
  public GetPostDetailListApiResponse getUserVisiblePostListByCursor(GetPostListQuery query) {

    List<GetPostDetailApiResponse> allPostList = fetchPostPort.fetchUserVisiblePostListByCursor(
            query.getUserId(), query.getCursorPostedAt(), query.getCursorPostId(), query.getLimit() + 1)
        .stream().map(
            projection -> new GetPostDetailApiResponse(
                projection.getPostId(),
                projection.getUserId(),
                projection.getUserProfileImageUrl(),
                projection.getUserNickname(),
                projection.getContent(),
                projection.getViewCount(),
                projection.getPostedAt(),
                projection.getLikeCount(),
                projection.getCommentCount(),
                projection.isLiked())).toList();

    /*다음 페이지가 있는지 검증*/
    boolean hasNext = allPostList.size() > query.getLimit();

    /*리스트 분할*/
    List<GetPostDetailApiResponse> pagedPostList =
        hasNext
            ? allPostList.subList(0, query.getLimit())
            : allPostList;

    /*커서 지정*/
    LocalDateTime cursorPostedAt =
        hasNext ? pagedPostList.getLast().postedAt() : null;
    String cursorPostId = hasNext ? pagedPostList.getLast().postId() : null;

    return new GetPostDetailListApiResponse(pagedPostList,
        new NextCursor(cursorPostedAt, cursorPostId));

  }

  @Transactional(readOnly = true)
  @Override
  public GetPostDetailApiResponse getPost(GetPostQuery query) {
    PostDetailProjection postDetailProjection = fetchPostPort.fetchPostDetailsByPostIdAndUserId(
            query.getPostId(), query.getUserId())
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));


    /*TODO 도메인 로직으로 변경*/
    PostStatusType status = postDetailProjection.getPostStatus();
    if (status == DELETED) {
      throw new PostException(ErrorCode.POST_ALREADY_DELETED);
    } else if (status == ARCHIVE) {
      throw new PostException(ErrorCode.POST_NOT_FOUND);
    } else if (status == BLOCKED) {
      throw new PostException(ErrorCode.POST_BLOCKED);
    }

    return new GetPostDetailApiResponse(postDetailProjection.getPostId(),
        postDetailProjection.getUserId(),
        postDetailProjection.getUserProfileImageUrl(), postDetailProjection.getUserNickname(),
        postDetailProjection.getContent(), postDetailProjection.getViewCount(),
        postDetailProjection.getPostedAt(), postDetailProjection.getLikeCount(),
        postDetailProjection.getCommentCount(), postDetailProjection.isLiked());
  }

  @Transactional(readOnly = true)
  @Override
  public GetPostEngagementApiResponse getPostEngagement(GetPostEngagementQuery query) {
    return
        fetchPostPort.fetchPostEngagementByPostIdAndUserId(
            query.getPostId(), query.getUserId()
        ).map(projection -> new GetPostEngagementApiResponse(
            projection.getPostId(),
            projection.getAuthorId(),
            projection.getAuthorNickname(),
            projection.getAuthorProfileImageUrl(),
            projection.getContent(),
            projection.getLikeCount(),
            projection.isLiked()
        )).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
  }
}
