package com.tifawinkitchen.recipeapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListDto {
    private Long id;
    private Long userId;
    private String name;
    private List<IngredientQuantityDto> items;
    private Set<Long> recipeIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}