package com.threadly.adapter.persistence.post.adapter;

import com.threadly.adapter.persistence.post.repository.PostJpaRepository;
import com.threadly.core.port.post.in.search.dto.PostSearchSortType;
import com.threadly.core.port.post.out.sesarch.PostSearchProjection;
import com.threadly.core.port.post.out.sesarch.SearchPostQueryPort;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 게시글 검색 관련 Persistence Adapter
 */
@Repository
@RequiredArgsConstructor
public class SearchPostPersistenceAdapter implements SearchPostQueryPort {

  private final PostJpaRepository postJpaRepository;

  @Override
  public List<PostSearchProjection> searchPostByKeyword(String userId, String keyword,
      PostSearchSortType sortType, String cursorPostId,
      LocalDateTime cursorPostedAt, int limit) {
    return postJpaRepository.searchVisiblePostsByKeywordWithCursor(
        userId, keyword, cursorPostedAt, cursorPostId, limit, sortType.name());
  }
}
