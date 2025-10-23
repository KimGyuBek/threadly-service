package com.threadly.core.port.post.out.sesarch;


import com.threadly.core.port.post.in.search.dto.PostSearchSortType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 검색 keyword port
 */
public interface PostSearchQueryPort {

  /**
   * 주어진 파라미터에 해당하는 게시글 검색
   * @return
   */
  List<PostSearchProjection> searchPostByKeyword(
      String userId,
      String keyword,
      PostSearchSortType sortType,
      String cursorPostId,
      LocalDateTime cursorPostedAt,
      int limit
  );

}
