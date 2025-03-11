package com.tifawinkitchen.recipeapp.repository;

import com.tifawinkitchen.recipeapp.model.Ingredient;
import com.tifawinkitchen.recipeapp.model.enums.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByNameIgnoreCase(String name);
    List<Ingredient> findByCategory(IngredientCategory category);
    List<Ingredient> findByNameContainingIgnoreCase(String partialName);
}