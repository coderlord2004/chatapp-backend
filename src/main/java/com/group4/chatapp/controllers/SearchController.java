package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.search.SearchHistoryDto;
import com.group4.chatapp.dtos.search.TopSearchDto;
import com.group4.chatapp.services.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchHistoryService searchHistoryService;

    @GetMapping("/top/")
    public List<TopSearchDto> getTopSearch(@RequestParam("page") int page) {
        return searchHistoryService.getTopSearch(page-1);
    }

    @GetMapping("/histories/")
    public List<SearchHistoryDto> getSearchHistories() {
        return searchHistoryService.getSearchHistories();
    }
}
