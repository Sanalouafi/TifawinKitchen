package com.tifawinkitchen.recipeapp.util;

import com.tifawinkitchen.recipeapp.dto.*;
import com.tifawinkitchen.recipeapp.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperUtil {

    public UserDto mapUserToDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCulinaryPreferences(user.getCulinaryPreferences());
        return dto;
    }

    public RecipeDto mapRecipeToDto(Recipe recipe, Double averageRating, Integer totalRatings) {
        if (recipe == null) return null;

        RecipeDto dto = new RecipeDto();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setSteps(recipe.getSteps());
        dto.setDishType(recipe.getDishType());
        dto.setPreparationTime(recipe.getPreparationTime());
        dto.setComplexity(recipe.getComplexity());
        dto.setImageURL(recipe.getImageURL());
        dto.setDietTypes(recipe.getDietTypes());
        dto.setCreatedBy(mapUserToDto(recipe.getCreatedBy()));
        dto.setAverageRating(averageRating);
        dto.setTotalRatings(totalRatings);

        // Map ingredients with quantities
        if (recipe.getRecipeIngredients() != null) {
            List<IngredientQuantityDto> ingredientQuantities = recipe.getRecipeIngredients().stream()
                    .map(ri -> new IngredientQuantityDto(
                            ri.getIngredient().getId(),
                            ri.getIngredient().getName(),
                            ri.getQuantity(),
                            ri.getUnit()))
                    .collect(Collectors.toList());
            dto.setIngredients(ingredientQuantities);
        }

        return dto;
    }

    public CommentDto mapCommentToDto(Comment comment) {
        if (comment == null) return null;

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUserName(comment.getUser().getName());
        dto.setRecipeId(comment.getRecipe().getId());
        dto.setText(comment.getText());
        dto.setTimestamp(comment.getTimestamp());
        return dto;
    }

    public IngredientDto mapIngredientToDto(Ingredient ingredient) {
        if (ingredient == null) return null;

        IngredientDto dto = new IngredientDto();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setCategory(ingredient.getCategory());
        return dto;
    }

    public RatingDto mapRatingToDto(Rating rating) {
        if (rating == null) return null;

        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setUserId(rating.getUser().getId());
        dto.setRecipeId(rating.getRecipe().getId());
        dto.setStars(rating.getStars());
        return dto;
    }

    public ShoppingListDto mapShoppingListToDto(ShoppingList shoppingList) {
        if (shoppingList == null) return null;

        ShoppingListDto dto = new ShoppingListDto();
        dto.setId(shoppingList.getId());
        dto.setUserId(shoppingList.getUser().getId());
        dto.setName(shoppingList.getName());
        dto.setCreatedAt(shoppingList.getCreatedAt());
        dto.setUpdatedAt(shoppingList.getUpdatedAt());

        // Map items with quantities
        if (shoppingList.getItems() != null) {
            List<IngredientQuantityDto> items = shoppingList.getItems().stream()
                    .map(item -> new IngredientQuantityDto(
                            item.getIngredient().getId(),
                            item.getIngredient().getName(),
                            item.getQuantity(),
                            item.getUnit()))
                    .collect(Collectors.toList());
            dto.setItems(items);
        }

        // Map recipe IDs
        if (shoppingList.getGeneratedFromRecipes() != null) {
            dto.setRecipeIds(shoppingList.getGeneratedFromRecipes().stream()
                    .map(Recipe::getId)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public RecommendationDto mapRecommendationToDto(Recommendation recommendation,
                                                    Double averageRating,
                                                    Integer totalRatings) {
        if (recommendation == null) return null;

        RecommendationDto dto = new RecommendationDto();
        dto.setId(recommendation.getId());
        dto.setUserId(recommendation.getUser().getId());
        dto.setType(recommendation.getType());
        dto.setRecipe(mapRecipeToDto(recommendation.getRecipe(), averageRating, totalRatings));
        return dto;
    }

    public SearchHistoryDto mapSearchHistoryToDto(SearchHistory searchHistory) {
        if (searchHistory == null) return null;

        SearchHistoryDto dto = new SearchHistoryDto();
        dto.setId(searchHistory.getId());
        dto.setUserId(searchHistory.getUser().getId());
        dto.setSearchQuery(searchHistory.getSearchQuery());
        dto.setTimestamp(searchHistory.getTimestamp());
        return dto;
    }

    public MealPlanDto mapMealPlanToDto(MealPlan mealPlan) {
        if (mealPlan == null) return null;

        MealPlanDto dto = new MealPlanDto();
        dto.setId(mealPlan.getId());
        dto.setUserId(mealPlan.getUser().getId());
        dto.setName(mealPlan.getName());
        dto.setStartDate(mealPlan.getStartDate());
        dto.setEndDate(mealPlan.getEndDate());

        // Map recipes with ratings
        if (mealPlan.getRecipes() != null) {
            dto.setRecipes(mealPlan.getRecipes().stream()
                    .map(recipe -> mapRecipeToDto(recipe, null, null)) // We'll fill ratings later
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}