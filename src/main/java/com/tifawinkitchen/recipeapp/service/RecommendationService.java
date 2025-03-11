package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.RecommendationDto;
import java.util.List;

public interface RecommendationService {
    List<RecommendationDto> getRecommendationsForUser(Long userId);
    void generateRecommendations(Long userId);
}