package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.MealPlanDto;
import com.tifawinkitchen.recipeapp.service.MealPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MealPlanDto>> getUserMealPlans(@PathVariable Long userId) {
        List<MealPlanDto> mealPlans = mealPlanService.getUserMealPlans(userId);
        return ResponseEntity.ok(mealPlans);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<MealPlanDto> getMealPlanById(@PathVariable Long planId, @RequestParam Long userId) {
        MealPlanDto mealPlanDto = mealPlanService.getMealPlanById(planId, userId);
        return ResponseEntity.ok(mealPlanDto);
    }

    @PostMapping
    public ResponseEntity<MealPlanDto> createMealPlan(@RequestBody MealPlanDto mealPlanDto, @RequestParam Long userId) {
        MealPlanDto createdMealPlan = mealPlanService.createMealPlan(mealPlanDto, userId);
        return ResponseEntity.ok(createdMealPlan);
    }

    @PutMapping("/{planId}")
    public ResponseEntity<MealPlanDto> updateMealPlan(
            @PathVariable Long planId,
            @RequestBody MealPlanDto mealPlanDto,
            @RequestParam Long userId) {
        MealPlanDto updatedMealPlan = mealPlanService.updateMealPlan(planId, mealPlanDto, userId);
        return ResponseEntity.ok(updatedMealPlan);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable Long planId, @RequestParam Long userId) {
        mealPlanService.deleteMealPlan(planId, userId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{planId}/recipes")
    public ResponseEntity<Void> addRecipesToMealPlan(
            @PathVariable Long planId,
            @RequestBody List<Long> recipeIds,
            @RequestParam Long userId) {
        mealPlanService.addRecipesToMealPlan(planId, recipeIds, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{planId}/recipes")
    public ResponseEntity<Void> removeRecipesFromMealPlan(
            @PathVariable Long planId,
            @RequestBody List<Long> recipeIds,
            @RequestParam Long userId) {
        mealPlanService.removeRecipesFromMealPlan(planId, recipeIds, userId);
        return ResponseEntity.noContent().build();
    }
}