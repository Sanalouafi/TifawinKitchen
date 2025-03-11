package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.RecommendationDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.Recipe;
import com.tifawinkitchen.recipeapp.model.Recommendation;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.model.enums.RecommendationType;
import com.tifawinkitchen.recipeapp.repository.*;
import com.tifawinkitchen.recipeapp.service.RatingService;
import com.tifawinkitchen.recipeapp.service.RecommendationService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final RatingRepository ratingRepository;
    private final MapperUtil mapperUtil;
    private final RatingService ratingService;

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendationsForUser(Long userId) {
        Page<Recommendation> recommendationsPage = recommendationRepository.findByUserIdAndType(
                userId, null, PageRequest.of(0, 100, Sort.by("id").ascending()));

        List<Recommendation> recommendations = recommendationsPage.getContent();

        return recommendations.stream()
                .map(rec -> {
                    Double avgRating = ratingService.getAverageRatingForRecipe(rec.getRecipe().getId());
                    Long totalRatings = ratingService.getTotalRatingsForRecipe(rec.getRecipe().getId());
                    return mapperUtil.mapRecommendationToDto(rec, avgRating, totalRatings.intValue());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void generateRecommendations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Clear previous recommendations
        Page<Recommendation> userRecommendations = recommendationRepository.findByUserIdAndType(
                userId, null, PageRequest.of(0, 1000));
        recommendationRepository.deleteAll(userRecommendations.getContent());

        List<Recommendation> newRecommendations = new ArrayList<>();

        // Recommendations based on user preferences
        newRecommendations.addAll(generatePreferenceBasedRecommendations(user));

        // Recommendations based on trending recipes
        newRecommendations.addAll(generateTrendingRecommendations(user));

        // Save all recommendations
        recommendationRepository.saveAll(newRecommendations);
    }

    private List<Recommendation> generatePreferenceBasedRecommendations(User user) {
        List<Recommendation> recommendations = new ArrayList<>();

        Set<DietType> preferences = user.getCulinaryPreferences();

        if (preferences != null && !preferences.isEmpty()) {
            List<Recipe> allRecipes = recipeRepository.findAll();

            List<Recipe> matchingRecipes = allRecipes.stream()
                    .filter(recipe -> {
                        Set<DietType> recipeDietTypes = recipe.getDietTypes();
                        if (recipeDietTypes == null) return false;
                        return recipeDietTypes.stream().anyMatch(preferences::contains);
                    })
                    .limit(5)
                    .collect(Collectors.toList());

            for (Recipe recipe : matchingRecipes) {
                Recommendation recommendation = new Recommendation();
                recommendation.setUser(user);
                recommendation.setRecipe(recipe);
                recommendation.setType(RecommendationType.BY_PREFERENCES);
                recommendations.add(recommendation);
            }
        }

        return recommendations;
    }

    private List<Recommendation> generateTrendingRecommendations(User user) {
        List<Recommendation> recommendations = new ArrayList<>();

        List<Recipe> allRecipes = recipeRepository.findAll();

        List<Recipe> trendingRecipes = allRecipes.stream()
                .filter(recipe -> {
                    Double avgRating = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
                    return avgRating != null;
                })
                .sorted((r1, r2) -> {
                    Double rating1 = ratingRepository.findAverageRatingByRecipeId(r1.getId());
                    Double rating2 = ratingRepository.findAverageRatingByRecipeId(r2.getId());
                    return rating2.compareTo(rating1);
                })
                .limit(5)
                .collect(Collectors.toList());

        for (Recipe recipe : trendingRecipes) {
            Recommendation recommendation = new Recommendation();
            recommendation.setUser(user);
            recommendation.setRecipe(recipe);
            recommendation.setType(RecommendationType.TRENDING);
            recommendations.add(recommendation);
        }

        return recommendations;
    }
}
