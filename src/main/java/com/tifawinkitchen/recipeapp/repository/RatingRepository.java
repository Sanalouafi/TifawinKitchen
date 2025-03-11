package com.tifawinkitchen.recipeapp.repository;

import com.tifawinkitchen.recipeapp.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndRecipeId(Long userId, Long recipeId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.recipe.id = :recipeId")
    Double findAverageRatingByRecipeId(@Param("recipeId") Long recipeId);
}