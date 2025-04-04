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

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Recipe> findByDishType(DishType dishType, Pageable pageable);

    Page<Recipe> findByComplexity(RecipeComplexity complexity, Pageable pageable);

    Page<Recipe> findByPreparationTimeLessThanEqual(Integer maxTime, Pageable pageable);

    Page<Recipe> findByDietTypesContaining(DietType dietType, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.id <> :recipeId AND r.dishType = :dishType ORDER BY SIZE(r.recipeIngredients) DESC")
    List<Recipe> findSimilarRecipes(@Param("recipeId") Long recipeId, @Param("dishType") DishType dishType, Pageable pageable);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.recipe.id = :recipeId")
    Double findAverageRatingByRecipeId(@Param("recipeId") Long recipeId);
}