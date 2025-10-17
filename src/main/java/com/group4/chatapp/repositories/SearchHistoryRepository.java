package com.group4.chatapp.repositories;

import com.group4.chatapp.dtos.search.TopSearchDto;
import com.group4.chatapp.models.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    SearchHistory findByKeyword(String keyword);

    List<SearchHistory> findByUser_Id(Long userId);
    @Query("""
        SELECT new com.group4.chatapp.dtos.search.TopSearchDto(
            sh.keyword,
            COUNT(sh.id)
        )
        FROM SearchHistory sh
        GROUP BY sh.keyword
        ORDER BY COUNT(sh.id) DESC
    """)
    List<TopSearchDto> getTopSearch(Pageable pageable);

}
