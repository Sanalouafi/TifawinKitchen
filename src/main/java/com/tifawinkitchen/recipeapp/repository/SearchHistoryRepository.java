package com.tifawinkitchen.recipeapp.repository;

import com.tifawinkitchen.recipeapp.model.SearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Page<SearchHistory> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

}
