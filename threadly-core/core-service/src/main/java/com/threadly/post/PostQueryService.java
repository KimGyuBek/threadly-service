package com.threadly.post;

import static com.threadly.posts.PostStatusType.ARCHIVE;
import static com.threadly.posts.PostStatusType.BLOCKED;
import static com.threadly.posts.PostStatusType.DELETED;

import com.threadly.ErrorCode;
import com.threadly.exception.post.PostException;
import com.threadly.post.query.GetPostQuery;
import com.threadly.post.response.PostDetailApiResponse;
import com.threadly.post.response.PostDetailListApiResponse;
import com.threadly.post.response.PostDetailResponse;
import com.threadly.posts.PostStatusType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 조회 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PostQueryService implements FetchPostUseCase {

  private final FetchPostPort fetchPostPort;


  @Override
  public PostDetailListApiResponse getUserVisiblePostList(String userId) {

    List<PostDetailApiResponse> postDetailList = fetchPostPort.findUserVisiblePostList(userId).stream().map(
        postDetails -> new PostDetailApiResponse(
            postDetails.getPostId(),
            postDetails.getUserId(),
            postDetails.getUserProfileImageUrl(),
            postDetails.getUserNickname(),
            postDetails.getContent(),
            postDetails.getViewCount(),
            postDetails.getPostedAt(),
            postDetails.getLikeCount(),
            postDetails.getCommentCount(),
            postDetails.isLiked())).toList();

    if (postDetailList.isEmpty()) {
      throw new PostException(ErrorCode.POST_NOT_FOUND);
    }

    return new PostDetailListApiResponse(postDetailList);
  }

  @Transactional(readOnly = true)
  @Override
  public PostDetailApiResponse getPost(GetPostQuery query) {
    PostDetailResponse postDetailResponse = fetchPostPort.findPostDetailsByPostIdAndUserId(
            query.getPostId(), query.getUserId())
        .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));


    /*TODO 도메인 로직으로 변경*/
    PostStatusType status = postDetailResponse.getPostStatus();
    if (status == DELETED) {
      throw new PostException(ErrorCode.POST_ALREADY_DELETED);
    } else if (status == ARCHIVE) {
      throw new PostException(ErrorCode.POST_NOT_FOUND);
    } else if (status == BLOCKED) {
      throw new PostException(ErrorCode.POST_BLOCKED);
    }

    return new PostDetailApiResponse(postDetailResponse.getPostId(), postDetailResponse.getUserId(),
        postDetailResponse.getUserProfileImageUrl(), postDetailResponse.getUserNickname(),
        postDetailResponse.getContent(), postDetailResponse.getViewCount(),
        postDetailResponse.getPostedAt(), postDetailResponse.getLikeCount(),
        postDetailResponse.getCommentCount(), postDetailResponse.isLiked());
  }
}
