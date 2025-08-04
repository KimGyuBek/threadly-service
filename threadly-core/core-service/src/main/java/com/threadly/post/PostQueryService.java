package com.threadly.post;

import static com.threadly.post.PostStatus.ARCHIVE;
import static com.threadly.post.PostStatus.BLOCKED;
import static com.threadly.post.PostStatus.DELETED;

import com.threadly.commons.dto.UserPreview;
import com.threadly.exception.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.image.ImageStatus;
import com.threadly.post.engagement.GetPostEngagementApiResponse;
import com.threadly.post.engagement.GetPostEngagementQuery;
import com.threadly.post.engagement.GetPostEngagementUseCase;
import com.threadly.post.fetch.FetchPostPort;
import com.threadly.post.fetch.PostDetailProjection;
import com.threadly.post.get.GetPostDetailApiResponse;
import com.threadly.post.get.GetPostDetailsApiResponse;
import com.threadly.post.get.GetPostDetailsApiResponse.NextCursor;
import com.threadly.post.get.GetPostListQuery;
import com.threadly.post.get.GetPostQuery;
import com.threadly.post.get.GetPostUseCase;
import com.threadly.post.image.fetch.FetchPostImagePort;
import com.threadly.post.image.fetch.PostImageProjection;
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

  private final FetchPostImagePort fetchPostImagePort;

  @Transactional(readOnly = true)
  @Override
  public GetPostDetailsApiResponse getUserVisiblePostListByCursor(GetPostListQuery query) {

    /*게시글 상세 정보 조회*/
    List<GetPostDetailApiResponse> allPostList = fetchPostPort.fetchUserVisiblePostListByCursor(
            query.getUserId(), query.getCursorPostedAt(), query.getCursorPostId(), query.getLimit() + 1)
        .stream().map(
            projection -> new GetPostDetailApiResponse(
                projection.getPostId(),
                new UserPreview(
                    projection.getUserId(),
                    projection.getUserNickname(),
                    projection.getUserProfileImageUrl()
                ),
                projection.getContent(),
                fetchPostImagePort.findAllByPostIdAndStatus(
                    projection.getPostId(),
                    ImageStatus.CONFIRMED
                ).stream().map(
                    image -> new GetPostDetailApiResponse.PostImage(
                        image.getImageId(),
                        image.getImageUrl(),
                        image.getImageOrder()
                    )).toList(),
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

    return new GetPostDetailsApiResponse(pagedPostList,
        new NextCursor(cursorPostedAt, cursorPostId));
  }

  @Transactional(readOnly = true)
  @Override
  public GetPostDetailApiResponse getPost(GetPostQuery query) {
    /*게시글 상세 정보 조회*/
    PostDetailProjection postDetailProjection = fetchPostPort.fetchPostDetailsByPostIdAndUserId(
            query.getPostId(), query.getUserId())
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

    /*게시글 이미지 조회*/
    List<PostImageProjection> postImageProjections = fetchPostImagePort.findAllByPostIdAndStatus(
        query.getPostId(), ImageStatus.CONFIRMED);

    /*TODO 도메인 로직으로 변경*/
    PostStatus status = postDetailProjection.getPostStatus();
    if (status == DELETED) {
      throw new PostException(ErrorCode.POST_ALREADY_DELETED);
    } else if (status == ARCHIVE) {
      throw new PostException(ErrorCode.POST_NOT_FOUND);
    } else if (status == BLOCKED) {
      throw new PostException(ErrorCode.POST_BLOCKED);
    }

    return new GetPostDetailApiResponse(postDetailProjection.getPostId(),
        new UserPreview(
            postDetailProjection.getUserId(),
            postDetailProjection.getUserNickname(),
            postDetailProjection.getUserProfileImageUrl()
        ),
        postDetailProjection.getContent(),
        postImageProjections.stream().map(
            projection ->
                new GetPostDetailApiResponse.PostImage(
                    projection.getImageId(),
                    projection.getImageUrl(),
                    projection.getImageOrder()
                )
        ).toList(),
        postDetailProjection.getViewCount(),
        postDetailProjection.getPostedAt(),
        postDetailProjection.getLikeCount(),
        postDetailProjection.getCommentCount(),
        postDetailProjection.isLiked());
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
