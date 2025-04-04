package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.RecipeDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;

import java.util.List;

public interface FavoriteRecipeService {
    void saveRecipeToFavorites(Long userId, Long recipeId) throws ResourceNotFoundException;
    void removeRecipeFromFavorites(Long userId, Long recipeId) throws ResourceNotFoundException;
    List<RecipeDto> getUserFavoriteRecipes(Long userId);
    boolean isRecipeFavorite(Long userId, Long recipeId);
}
