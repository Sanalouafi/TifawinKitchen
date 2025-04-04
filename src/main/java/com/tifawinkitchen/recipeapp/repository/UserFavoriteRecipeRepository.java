package com.tifawinkitchen.recipeapp.repository;

import com.tifawinkitchen.recipeapp.model.UserFavoriteRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteRecipeRepository extends JpaRepository<UserFavoriteRecipe, Long> {
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    List<UserFavoriteRecipe> findByUserId(Long userId);
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}
