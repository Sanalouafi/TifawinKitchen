package com.tifawinkitchen.recipeapp.dto;

import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.model.enums.DishType;
import com.tifawinkitchen.recipeapp.model.enums.RecipeComplexity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateDto {
    @NotBlank(message = "Recipe name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotEmpty(message = "At least one ingredient is required")
    private List<IngredientQuantityDto> ingredients;

    @NotEmpty(message = "At least one step is required")
    private List<String> steps;

    @NotNull(message = "Dish type is required")
    private DishType dishType;

    @NotNull(message = "Preparation time is required")
    @Positive(message = "Preparation time must be positive")
    private Integer preparationTime;

    @NotNull(message = "Complexity is required")
    private RecipeComplexity complexity;

    private MultipartFile imageFile;
    private String imageURL;
    private Set<DietType> dietTypes;
}