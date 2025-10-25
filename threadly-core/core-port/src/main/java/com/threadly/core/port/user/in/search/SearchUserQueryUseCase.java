package com.threadly.core.port.user.in.search;

import com.threadly.commons.response.CursorPageApiResponse;
import com.threadly.core.port.user.in.search.dto.UserSearchQuery;
import com.threadly.core.port.user.in.search.dto.UserSearchItem;

/**
 * 사용자 검색 관련 usecase
 */
public interface SearchUserQueryUseCase {


  /**
   * 주어진 query에 해당하는 데이터 검색
   *
   * @param query
   * @return
   */
  CursorPageApiResponse<UserSearchItem> searchByKeyword(UserSearchQuery query);


}
