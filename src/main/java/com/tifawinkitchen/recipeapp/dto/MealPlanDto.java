package com.tifawinkitchen.recipeapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDto {
    private Long id;
    private Long userId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Map<String, RecipeDto>> meals; // Map<Date, Map<MealType, Recipe>>
    private List<RecipeDto> recipes;
}