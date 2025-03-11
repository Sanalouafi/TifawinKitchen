package com.tifawinkitchen.recipeapp.repository;

import com.tifawinkitchen.recipeapp.model.Recommendation;
import com.tifawinkitchen.recipeapp.model.enums.RecommendationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Page<Recommendation> findByUserIdAndType(Long userId, RecommendationType type, Pageable pageable);
}