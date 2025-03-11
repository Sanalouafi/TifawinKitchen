package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.SearchHistoryDto;
import java.util.List;

public interface SearchHistoryService {
    List<SearchHistoryDto> getUserSearchHistory(Long userId, int page, int size);
    SearchHistoryDto saveSearchQuery(String searchQuery, Long userId);
    void clearUserSearchHistory(Long userId);
}