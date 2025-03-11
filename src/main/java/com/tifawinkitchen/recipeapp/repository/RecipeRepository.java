package com.tifawinkitchen.recipeapp.repository;

import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.model.enums.DishType;
import com.tifawinkitchen.recipeapp.model.Recipe;
import com.tifawinkitchen.recipeapp.model.enums.RecipeComplexity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Recipe> findByDishType(DishType dishType, Pageable pageable);

    Page<Recipe> findByComplexity(RecipeComplexity complexity, Pageable pageable);

    Page<Recipe> findByPreparationTimeLessThanEqual(Integer maxTime, Pageable pageable);

    Page<Recipe> findByDietTypesContaining(DietType dietType, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.recipeIngredients ri WHERE ri.ingredient.id IN :ingredientIds GROUP BY r.id HAVING COUNT(DISTINCT ri.ingredient.id) = :count")
    Page<Recipe> findByIngredientsContainingAll(@Param("ingredientIds") List<Long> ingredientIds, @Param("count") Long count, Pageable pageable);
    @Query("SELECT r FROM Recipe r JOIN r.recipeIngredients ri WHERE ri.ingredient.id IN :ingredientIds GROUP BY r.id")
    Page<Recipe> findByIngredientsContainingAny(@Param("ingredientIds") List<Long> ingredientIds, Pageable pageable);
    @Query("SELECT r FROM Recipe r WHERE r.id NOT IN (SELECT r2.id FROM Recipe r2 JOIN r2.recipeIngredients ri WHERE ri.ingredient.id IN :excludedIngredientIds)")
    Page<Recipe> findByIngredientsNotIn(@Param("excludedIngredientIds") List<Long> excludedIngredientIds, Pageable pageable);
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.recipe.id = :recipeId")
    Double findAverageRatingByRecipeId(@Param("recipeId") Long recipeId);
}