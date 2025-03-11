package com.tifawinkitchen.recipeapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientQuantityDto {
    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;

    @NotBlank(message = "Ingredient name is required")
    private String name;

    @NotBlank(message = "Quantity is required")
    private String quantity;

    @NotBlank(message = "Unit is required")
    private String unit;
}