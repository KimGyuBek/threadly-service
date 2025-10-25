package com.threadly.core.service.post.search;

import com.threadly.commons.exception.ErrorCode;
import com.threadly.commons.exception.search.SearchException;
import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.commons.dto.UserPreview;
import com.threadly.core.port.post.in.search.SearchPostQueryUseCase;
import com.threadly.core.port.post.in.search.dto.PostSearchItem;
import com.threadly.core.port.post.in.search.dto.PostSearchItem.PostImageItem;
import com.threadly.core.port.post.in.search.dto.PostSearchQuery;
import com.threadly.core.port.post.in.search.dto.PostSearchSortType;
import com.threadly.core.port.post.out.image.PostImageQueryPort;
import com.threadly.core.port.post.out.image.projection.PostImageProjection;
import com.threadly.core.port.post.out.sesarch.PostSearchProjection;
import com.threadly.core.port.post.out.sesarch.SearchPostQueryPort;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*게시글 검색 관련 서비스*/
@Service
@RequiredArgsConstructor
public class SearchPostQueryService implements SearchPostQueryUseCase {

  private final SearchPostQueryPort searchPostQueryPort;

  private final PostImageQueryPort postImageQueryPort;

  @Transactional(readOnly = true)
  @Override
  public CursorPageApiResponse<PostSearchItem> searchByKeyword(PostSearchQuery query) {
    /*1. sortType 검증*/
    if (!(PostSearchSortType.isSupported(query.sortType()))) {
      throw new SearchException(ErrorCode.POST_SEARCH_SORT_TYPE_INVALID);
    }

    /*1. 조회*/
    List<PostSearchProjection> postSearchProjections = searchPostQueryPort.searchPostByKeyword(
        query.userId(),
        query.keyword(),
        query.sortType(),
        query.cursorPostId(),
        query.cursorPostedAt(),
        query.limit() + 1
    );

    /*2. postIds 추출*/
    List<String> postIds = postSearchProjections.stream().map(PostSearchProjection::getPostId)
        .toList();


    /*3. postId별 postImage 추출*/
    var imagesByPostId = postImageQueryPort.findVisibleByPostIds(
        postIds).stream().collect(
        Collectors.groupingBy(PostImageProjection::getPostId));

    /*2. 응답 리턴*/
    return CursorPageApiResponse.from(
        postSearchProjections.stream().map(
            projection -> {
              UserPreview author = new UserPreview(
                  projection.getUserId(),
                  projection.getUserNickname(),
                  projection.getUserProfileImageUrl() == null ? "/"
                      : projection.getUserProfileImageUrl()
              );

              var images = imagesByPostId.getOrDefault(projection.getPostId(), List.of()).stream()
                  .map(img -> new PostImageItem(
                      img.getImageUrl(), img.getImageOrder()))
                  .toList();

              return
                  new PostSearchItem(
                      projection.getPostId(),
                      author,
                      projection.getContent(),
                      images,
                      projection.getLikeCount(),
                      projection.getCommentCount(),
                      projection.isLiked(),
                      projection.getPostedAt()
                  );
            }
        ).toList(),
        query.limit()
    );
  }
}
