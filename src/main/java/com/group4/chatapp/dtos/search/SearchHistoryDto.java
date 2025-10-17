package com.group4.chatapp.dtos.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryDto {
    private String keyword;
    private int frequency;
}
