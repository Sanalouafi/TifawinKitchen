package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.MealPlanDto;
import com.tifawinkitchen.recipeapp.dto.RecipeDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.MealPlan;
import com.tifawinkitchen.recipeapp.model.Recipe;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.repository.MealPlanRepository;
import com.tifawinkitchen.recipeapp.repository.RecipeRepository;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.MealPlanService;
import com.tifawinkitchen.recipeapp.service.RatingService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final RatingService ratingService;
    private final MapperUtil mapperUtil;

    @Override
    @Transactional(readOnly = true)
    public List<MealPlanDto> getUserMealPlans(Long userId) {
        List<MealPlan> mealPlans = mealPlanRepository.findByUserId(userId);
        return mealPlans.stream()
                .map(mealPlan -> {
                    MealPlanDto dto = mapperUtil.mapMealPlanToDto(mealPlan);

                    if (dto.getRecipes() != null) {
                        for (RecipeDto recipeDto : dto.getRecipes()) {
                            recipeDto.setAverageRating(ratingService.getAverageRatingForRecipe(recipeDto.getId()));
                            recipeDto.setTotalRatings(ratingService.getTotalRatingsForRecipe(recipeDto.getId()).intValue());
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MealPlanDto getMealPlanById(Long planId, Long userId) throws ResourceNotFoundException {
        MealPlan mealPlan = mealPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        MealPlanDto dto = mapperUtil.mapMealPlanToDto(mealPlan);

        if (dto.getRecipes() != null) {
            for (RecipeDto recipeDto : dto.getRecipes()) {
                recipeDto.setAverageRating(ratingService.getAverageRatingForRecipe(recipeDto.getId()));
                recipeDto.setTotalRatings(ratingService.getTotalRatingsForRecipe(recipeDto.getId()).intValue());
            }
        }

        return dto;
    }

    @Override
    @Transactional
    public MealPlanDto createMealPlan(MealPlanDto mealPlanDto, Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        MealPlan mealPlan = new MealPlan();
        mealPlan.setUser(user);
        mealPlan.setName(mealPlanDto.getName());
        mealPlan.setStartDate(mealPlanDto.getStartDate());
        mealPlan.setEndDate(mealPlanDto.getEndDate());

        if (mealPlanDto.getRecipes() != null && !mealPlanDto.getRecipes().isEmpty()) {
            Set<Recipe> recipeSet = mealPlanDto.getRecipes().stream()
                    .map(recipeDto -> recipeRepository.findById(recipeDto.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeDto.getId())))
                    .collect(Collectors.toSet());

            List<Recipe> recipes = new ArrayList<>(recipeSet);
            mealPlan.setRecipes(recipes);
        }

        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);
        return mapperUtil.mapMealPlanToDto(savedMealPlan);
    }

    @Override
    @Transactional
    public MealPlanDto updateMealPlan(Long planId, MealPlanDto mealPlanDto, Long userId) throws ResourceNotFoundException {
        MealPlan mealPlan = mealPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        mealPlan.setName(mealPlanDto.getName());
        mealPlan.setStartDate(mealPlanDto.getStartDate());
        mealPlan.setEndDate(mealPlanDto.getEndDate());

        if (mealPlanDto.getRecipes() != null) {
            Set<Recipe> recipeSet = mealPlanDto.getRecipes().stream()
                    .map(recipeDto -> recipeRepository.findById(recipeDto.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeDto.getId())))
                    .collect(Collectors.toSet());

            List<Recipe> recipes = new ArrayList<>(recipeSet);
            mealPlan.setRecipes(recipes);
        }

        MealPlan updatedMealPlan = mealPlanRepository.save(mealPlan);
        return mapperUtil.mapMealPlanToDto(updatedMealPlan);
    }

    @Override
    @Transactional
    public void deleteMealPlan(Long planId, Long userId) throws ResourceNotFoundException {
        MealPlan mealPlan = mealPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        mealPlanRepository.delete(mealPlan);
    }
    @Override
    @Transactional
    public void addRecipesToMealPlan(Long planId, List<Long> recipeIds, Long userId) throws ResourceNotFoundException {
        MealPlan mealPlan = mealPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        List<Recipe> recipes = recipeRepository.findAllById(recipeIds);
        if (recipes.size() != recipeIds.size()) {
            throw new ResourceNotFoundException("One or more recipes not found");
        }

        Set<Recipe> existingRecipes = new HashSet<>(mealPlan.getRecipes());
        recipes.stream()
                .filter(recipe -> !existingRecipes.contains(recipe))
                .forEach(mealPlan.getRecipes()::add);

        mealPlanRepository.save(mealPlan);
    }

    @Override
    @Transactional
    public void removeRecipesFromMealPlan(Long planId, List<Long> recipeIds, Long userId) throws ResourceNotFoundException {
        MealPlan mealPlan = mealPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", planId));

        mealPlan.setRecipes(
                mealPlan.getRecipes().stream()
                        .filter(recipe -> !recipeIds.contains(recipe.getId()))
                        .collect(Collectors.toList())
        );

        mealPlanRepository.save(mealPlan);
    }
}
