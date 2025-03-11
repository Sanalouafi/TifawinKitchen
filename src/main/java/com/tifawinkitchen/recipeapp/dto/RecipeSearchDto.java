package com.tifawinkitchen.recipeapp.dto;

import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.model.enums.DishType;
import com.tifawinkitchen.recipeapp.model.enums.RecipeComplexity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSearchDto {
    private String keyword;
    private List<Long> includeIngredients;
    private List<Long> excludeIngredients;
    private DishType dishType;
    private Integer maxPrepTime;
    private RecipeComplexity complexity;
    private DietType dietType;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "id";
    private Boolean ascending = true;
}