package com.threadly.adapter.persistence.user.adapter;

import com.threadly.adapter.persistence.user.repository.UserJpaRepository;
import com.threadly.core.port.user.out.search.SearchUserQueryPort;
import com.threadly.core.port.user.out.search.UserSearchProjection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 사용자 검색 관련 Persistence Adapter
 */
@Repository
@RequiredArgsConstructor
public class SearchUserPersistenceAdapter implements SearchUserQueryPort {

  private final UserJpaRepository userJpaRepository;

  @Override
  public List<UserSearchProjection> searchByKeyword(String userId, String keyword,
      String cursorNickname, int limit) {
    return userJpaRepository.searchUserByKeywordWithCursor(
        userId, keyword, cursorNickname, limit);
  }
}
