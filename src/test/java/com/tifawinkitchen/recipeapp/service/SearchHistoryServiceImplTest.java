package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.SearchHistoryDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.SearchHistory;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.repository.SearchHistoryRepository;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.impl.SearchHistoryServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchHistoryServiceImplTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MapperUtil mapperUtil;

    @InjectMocks
    private SearchHistoryServiceImpl searchHistoryService;

    private SearchHistory searchHistory;
    private SearchHistoryDto searchHistoryDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        searchHistory = new SearchHistory();
        searchHistory.setId(1L);
        searchHistory.setUser(user);
        searchHistory.setSearchQuery("pasta");
        searchHistory.setTimestamp(LocalDateTime.now());

        searchHistoryDto = new SearchHistoryDto();
        searchHistoryDto.setId(1L);
        searchHistoryDto.setSearchQuery("pasta");
    }

    @Test
    void getUserSearchHistory_Success() {
        Page<SearchHistory> page = new PageImpl<>(Collections.singletonList(searchHistory));
        when(searchHistoryRepository.findByUserIdOrderByTimestampDesc(anyLong(), any(Pageable.class)))
                .thenReturn(page);
        when(mapperUtil.mapSearchHistoryToDto(any(SearchHistory.class))).thenReturn(searchHistoryDto);

        List<SearchHistoryDto> result = searchHistoryService.getUserSearchHistory(1L, 0, 10);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void saveSearchQuery_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(searchHistoryRepository.save(any(SearchHistory.class))).thenReturn(searchHistory);
        when(mapperUtil.mapSearchHistoryToDto(any(SearchHistory.class))).thenReturn(searchHistoryDto);

        SearchHistoryDto result = searchHistoryService.saveSearchQuery("pasta", 1L);

        assertNotNull(result);
        assertEquals("pasta", result.getSearchQuery());
    }

    @Test
    void saveSearchQuery_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> searchHistoryService.saveSearchQuery("pasta", 1L));
    }

    @Test
    void clearUserSearchHistory_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        Page<SearchHistory> page = new PageImpl<>(Collections.singletonList(searchHistory));
        when(searchHistoryRepository.findByUserIdOrderByTimestampDesc(anyLong(), any(Pageable.class)))
                .thenReturn(page);

        searchHistoryService.clearUserSearchHistory(1L);

        verify(searchHistoryRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void clearUserSearchHistory_UserNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> searchHistoryService.clearUserSearchHistory(1L));
    }
}