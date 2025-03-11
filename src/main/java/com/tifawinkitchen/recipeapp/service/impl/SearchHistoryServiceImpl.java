package com.tifawinkitchen.recipeapp.service.impl;


import com.tifawinkitchen.recipeapp.dto.SearchHistoryDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.SearchHistory;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.repository.SearchHistoryRepository;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.SearchHistoryService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchHistoryServiceImpl implements SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;

    @Override
    @Transactional(readOnly = true)
    public List<SearchHistoryDto> getUserSearchHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return searchHistoryRepository.findByUserIdOrderByTimestampDesc(userId, pageable)
                .stream()
                .map(mapperUtil::mapSearchHistoryToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SearchHistoryDto saveSearchQuery(String searchQuery, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUser(user);
        searchHistory.setSearchQuery(searchQuery);
        searchHistory.setTimestamp(LocalDateTime.now());

        SearchHistory savedSearchHistory = searchHistoryRepository.save(searchHistory);
        return mapperUtil.mapSearchHistoryToDto(savedSearchHistory);
    }

    @Override
    @Transactional
    public void clearUserSearchHistory(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Pageable unpaged = Pageable.unpaged();
        Page<SearchHistory> userSearchHistory = searchHistoryRepository.findByUserIdOrderByTimestampDesc(userId, unpaged);
        searchHistoryRepository.deleteAll(userSearchHistory.getContent());
    }
}