package com.group4.chatapp.services;

import com.group4.chatapp.dtos.search.SearchHistoryDto;
import com.group4.chatapp.dtos.search.TopSearchDto;
import com.group4.chatapp.models.SearchHistory;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class SearchHistoryService {
    private SearchHistoryRepository repository;
    private UserService userService;

    public SearchHistory save(User authUser, String keyword) {
        SearchHistory searchHistory = repository.findByKeyword(keyword);
        if (searchHistory == null) {
            SearchHistory newSearchHistory = SearchHistory.builder()
                    .keyword(keyword)
                    .user(authUser)
                    .build();
            return repository.save(newSearchHistory);
        } else {
            searchHistory.setFrequency(searchHistory.getFrequency() + 1);

            return repository.save(searchHistory);
        }
    }

    public List<SearchHistoryDto> getSearchHistories() {
        User authUser = userService.getUserOrThrows();

        return repository.findByUser_Id(authUser.getId()).stream().map(searchHistory -> new SearchHistoryDto(searchHistory.getKeyword(), searchHistory.getFrequency())).toList();
    }

    public List<TopSearchDto> getTopSearch(int page) {
        return repository.getTopSearch(PageRequest.of(page, 5));
    }
}
