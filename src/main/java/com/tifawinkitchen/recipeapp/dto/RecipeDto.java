package com.tifawinkitchen.recipeapp.dto;

import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.model.enums.DishType;
import com.tifawinkitchen.recipeapp.model.enums.RecipeComplexity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {
    private Long id;
    private String name;
    private String description;
    private List<IngredientQuantityDto> ingredients;
    private List<String> steps;
    private DishType dishType;
    private Integer preparationTime;
    private RecipeComplexity complexity;
    private String imageURL;
    private Set<DietType> dietTypes;
    private UserDto createdBy;
    private Double averageRating;
    private Integer totalRatings;
}