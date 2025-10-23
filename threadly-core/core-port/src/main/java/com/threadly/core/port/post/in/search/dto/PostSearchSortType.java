package com.threadly.core.port.post.in.search.dto;

/**
 * 게시글 검색 정렬 조건
 */
public enum PostSearchSortType {
  RECENT, // 최신순
  POPULAR, // 인기순
  RELEVANCE; // 유사도순

  /**
   * 주어진 sortType이 지원하는 타입인지 검증
   * @param sortType
   * @return
   */
  public static boolean isSupported(PostSearchSortType sortType) {
    return sortType == RECENT || sortType == POPULAR;
  }
}
