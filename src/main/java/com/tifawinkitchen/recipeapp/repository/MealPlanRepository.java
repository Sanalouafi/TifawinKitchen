package com.tifawinkitchen.recipeapp.repository;

import com.tifawinkitchen.recipeapp.model.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByUserId(Long userId);

    Optional<MealPlan> findByIdAndUserId(Long id, Long userId);

    List<MealPlan> findByUserIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            Long userId, LocalDate startDate, LocalDate endDate);
}