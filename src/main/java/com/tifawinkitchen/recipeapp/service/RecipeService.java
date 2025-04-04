package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.*;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {
    List<RecipeDto> getAllRecipes(int page, int size, String sortBy, boolean ascending);
    RecipeDto getRecipeById(Long id) throws ResourceNotFoundException;
    RecipeDto createRecipe(RecipeCreateDto recipeCreateDto, Long userId) throws ResourceNotFoundException;
    RecipeDto updateRecipe(Long id, RecipeCreateDto recipeUpdateDto) throws ResourceNotFoundException;
    void deleteRecipe(Long id) throws ResourceNotFoundException;
    List<RecipeDto> searchRecipes(RecipeSearchDto searchDto, Long userId);
    List<RecipeDto> getSimilarRecipes(Long recipeId, int count);
    void saveRecipeToUserFavorites(Long userId, Long recipeId) throws ResourceNotFoundException;
    void removeRecipeFromUserFavorites(Long userId, Long recipeId) throws ResourceNotFoundException;
    List<RecipeDto> getUserFavoriteRecipes(Long userId);
    boolean isRecipeFavoriteForUser(Long userId, Long recipeId);
}