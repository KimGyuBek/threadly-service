package com.threadly.core.port.user.out.search;

import java.util.List;

public interface SearchUserQueryPort {


  /**
   * 주어진 파라미터에 해당하는 데이터 검색
   *
   * @return
   */
  List<UserSearchProjection> searchByKeyword(
      String userId,
      String keyword,
      String cursorNickname,
      int limit
  );

}
