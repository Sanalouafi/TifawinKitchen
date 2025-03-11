package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.RatingDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.Rating;
import com.tifawinkitchen.recipeapp.model.Recipe;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.repository.RatingRepository;
import com.tifawinkitchen.recipeapp.repository.RecipeRepository;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.RatingService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final MapperUtil mapperUtil;

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingForRecipe(Long recipeId) {
        return ratingRepository.findAverageRatingByRecipeId(recipeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalRatingsForRecipe(Long recipeId) {
        return ratingRepository.findAll().stream()
                .filter(rating -> rating.getRecipe().getId().equals(recipeId))
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public RatingDto getUserRatingForRecipe(Long recipeId, Long userId) {
        Optional<Rating> ratingOpt = ratingRepository.findByUserIdAndRecipeId(userId, recipeId);
        return ratingOpt.map(mapperUtil::mapRatingToDto).orElse(null);
    }

    @Override
    @Transactional
    public RatingDto rateRecipe(RatingDto ratingDto, Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Recipe recipe = recipeRepository.findById(ratingDto.getRecipeId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", ratingDto.getRecipeId()));

        Optional<Rating> existingRating = ratingRepository.findByUserIdAndRecipeId(userId, ratingDto.getRecipeId());

        Rating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setStars(ratingDto.getStars());
        } else {
            rating = new Rating();
            rating.setUser(user);
            rating.setRecipe(recipe);
            rating.setStars(ratingDto.getStars());
        }

        Rating savedRating = ratingRepository.save(rating);
        return mapperUtil.mapRatingToDto(savedRating);
    }

    @Override
    @Transactional
    public void deleteRating(Long recipeId, Long userId) throws ResourceNotFoundException {
        Optional<Rating> existingRating = ratingRepository.findByUserIdAndRecipeId(userId, recipeId);

        if (existingRating.isPresent()) {
            ratingRepository.delete(existingRating.get());
        } else {
            throw new ResourceNotFoundException("Rating", "userId and recipeId", userId + " and " + recipeId);
        }
    }
}