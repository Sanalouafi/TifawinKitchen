package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.IngredientDto;
import com.tifawinkitchen.recipeapp.exception.AppException;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.Ingredient;
import com.tifawinkitchen.recipeapp.model.enums.IngredientCategory;

import java.util.List;

public interface IngredientService {
    List<IngredientDto> getAllIngredients();
    IngredientDto getIngredientById(Long id) throws ResourceNotFoundException;
    List<IngredientDto> getIngredientsByCategory(IngredientCategory category);
    List<IngredientDto> searchIngredients(String query);
    IngredientDto createIngredient(IngredientDto ingredientDto) throws AppException;
    IngredientDto updateIngredient(Long id, IngredientDto ingredientDto) throws ResourceNotFoundException, AppException;
    void deleteIngredient(Long id) throws ResourceNotFoundException;
    Ingredient getOrCreateIngredient(String name, IngredientCategory category);
}