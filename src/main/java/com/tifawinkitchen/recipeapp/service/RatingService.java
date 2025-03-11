package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.RatingDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;

public interface RatingService {
    Double getAverageRatingForRecipe(Long recipeId);
    Long getTotalRatingsForRecipe(Long recipeId);
    RatingDto getUserRatingForRecipe(Long recipeId, Long userId);
    RatingDto rateRecipe(RatingDto ratingDto, Long userId) throws ResourceNotFoundException;
    void deleteRating(Long recipeId, Long userId) throws ResourceNotFoundException;
}