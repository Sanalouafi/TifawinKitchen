package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.RecipeDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.UserFavoriteRecipe;
import com.tifawinkitchen.recipeapp.repository.RecipeRepository;
import com.tifawinkitchen.recipeapp.repository.UserFavoriteRecipeRepository;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.FavoriteRecipeService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteRecipeServiceImpl implements FavoriteRecipeService {
    private final UserFavoriteRecipeRepository favoriteRecipeRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final MapperUtil mapperUtil;

    @Override
    @Transactional
    public void saveRecipeToFavorites(Long userId, Long recipeId) throws ResourceNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        if (!recipeRepository.existsById(recipeId)) {
            throw new ResourceNotFoundException("Recipe", "id", recipeId);
        }
        if (favoriteRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            return;
        }

        UserFavoriteRecipe favorite = new UserFavoriteRecipe();
        favorite.setUser(userRepository.getReferenceById(userId));
        favorite.setRecipe(recipeRepository.getReferenceById(recipeId));
        favoriteRecipeRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeRecipeFromFavorites(Long userId, Long recipeId) {
        favoriteRecipeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDto> getUserFavoriteRecipes(Long userId) {
        return favoriteRecipeRepository.findByUserId(userId).stream()
                .map(fav -> mapperUtil.mapRecipeToDto(
                        fav.getRecipe(),
                        recipeRepository.findAverageRatingByRecipeId(fav.getRecipe().getId()),
                        fav.getRecipe().getRatings() != null ? fav.getRecipe().getRatings().size() : 0
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRecipeFavorite(Long userId, Long recipeId) {
        return favoriteRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }
}