package com.threadly.core.port.post.in.search;

/*게시글 검색 관련 UseCase*/

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.post.in.search.dto.PostSearchItem;
import com.threadly.core.port.post.in.search.dto.PostSearchQuery;

public interface SearchPostQueryUseCase {

  /**
   * 게시글 검색
   * @param query
   * @return
   */
  CursorPageApiResponse<PostSearchItem> searchByKeyword(PostSearchQuery query);


}
