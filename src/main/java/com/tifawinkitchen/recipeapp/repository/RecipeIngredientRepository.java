package com.tifawinkitchen.recipeapp.repository;

import com.tifawinkitchen.recipeapp.model.RecipeIngredient;
import com.tifawinkitchen.recipeapp.model.Recipe;
import com.tifawinkitchen.recipeapp.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    /**
     * Find all recipe ingredients by recipe
     * @param recipe The recipe to find ingredients for
     * @return List of RecipeIngredient entities
     */
    List<RecipeIngredient> findByRecipe(Recipe recipe);

    /**
     * Find all recipe ingredients by recipe id
     * @param recipeId The id of the recipe
     * @return List of RecipeIngredient entities
     */
    List<RecipeIngredient> findByRecipeId(Long recipeId);

    /**
     * Find all recipe ingredients by ingredient
     * @param ingredient The ingredient to find recipes for
     * @return List of RecipeIngredient entities
     */
    List<RecipeIngredient> findByIngredient(Ingredient ingredient);

    /**
     * Find all recipe ingredients by ingredient id
     * @param ingredientId The id of the ingredient
     * @return List of RecipeIngredient entities
     */
    List<RecipeIngredient> findByIngredientId(Long ingredientId);

    /**
     * Delete all recipe ingredients by recipe
     * @param recipe The recipe to delete ingredients for
     */
    void deleteByRecipe(Recipe recipe);

    /**
     * Delete all recipe ingredients by recipe id
     * @param recipeId The id of the recipe
     */
    void deleteByRecipeId(Long recipeId);

    /**
     * Check if a recipe contains a specific ingredient
     * @param recipeId The id of the recipe
     * @param ingredientId The id of the ingredient
     * @return true if the recipe contains the ingredient
     */
    boolean existsByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);
}