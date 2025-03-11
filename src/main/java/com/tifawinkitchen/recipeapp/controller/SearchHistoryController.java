package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.SearchHistoryDto;
import com.tifawinkitchen.recipeapp.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SearchHistoryDto>> getUserSearchHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<SearchHistoryDto> searchHistory = searchHistoryService.getUserSearchHistory(userId, page, size);
        return ResponseEntity.ok(searchHistory);
    }

    @PostMapping
    public ResponseEntity<SearchHistoryDto> saveSearchQuery(
            @RequestParam String searchQuery,
            @RequestParam Long userId) {
        SearchHistoryDto savedSearchHistory = searchHistoryService.saveSearchQuery(searchQuery, userId);
        return ResponseEntity.ok(savedSearchHistory);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearUserSearchHistory(@PathVariable Long userId) {
        searchHistoryService.clearUserSearchHistory(userId);
        return ResponseEntity.noContent().build();
    }
}