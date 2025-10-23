package com.threadly.core.port.post.in.search;

/*게시글 검색 관련 UseCase*/

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.search.dto.PostSearchView;
import com.threadly.core.port.post.in.search.dto.SearchPostQuery;

public interface PostSearchQueryUseCase {

  /**
   * 게시글 검색
   * @param query
   * @return
   */
  CursorPageApiResponse<PostSearchView> searchByKeyword(SearchPostQuery query);


}
