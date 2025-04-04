package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.MealPlanDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;

import java.util.List;

public interface MealPlanService {
    List<MealPlanDto> getUserMealPlans(Long userId);
    MealPlanDto getMealPlanById(Long planId, Long userId) throws ResourceNotFoundException;
    MealPlanDto createMealPlan(MealPlanDto mealPlanDto, Long userId) throws ResourceNotFoundException;
    MealPlanDto updateMealPlan(Long planId, MealPlanDto mealPlanDto, Long userId) throws ResourceNotFoundException;
    void deleteMealPlan(Long planId, Long userId) throws ResourceNotFoundException;
    void addRecipesToMealPlan(Long planId, List<Long> recipeIds, Long userId) throws ResourceNotFoundException;
    void removeRecipesFromMealPlan(Long planId, List<Long> recipeIds, Long userId) throws ResourceNotFoundException;
}
