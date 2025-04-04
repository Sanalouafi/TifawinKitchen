package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.RecipeDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.Recipe;
import com.tifawinkitchen.recipeapp.model.UserFavoriteRecipe;
import com.tifawinkitchen.recipeapp.repository.RecipeRepository;
import com.tifawinkitchen.recipeapp.repository.UserFavoriteRecipeRepository;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.impl.FavoriteRecipeServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteRecipeServiceImplTest {

    @Mock
    private UserFavoriteRecipeRepository favoriteRecipeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private MapperUtil mapperUtil;

    @InjectMocks
    private FavoriteRecipeServiceImpl favoriteRecipeService;

    private final Long userId = 1L;
    private final Long recipeId = 1L;
    private RecipeDto recipeDto;

    @BeforeEach
    void setUp() {
        recipeDto = new RecipeDto();
        recipeDto.setId(recipeId);
    }

    @Test
    void saveRecipeToFavorites_Success() throws ResourceNotFoundException {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(recipeRepository.existsById(recipeId)).thenReturn(true);
        when(favoriteRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(false);

        favoriteRecipeService.saveRecipeToFavorites(userId, recipeId);

        verify(favoriteRecipeRepository, times(1)).save(any(UserFavoriteRecipe.class));
    }

    @Test
    void saveRecipeToFavorites_UserNotFound_ThrowsException() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteRecipeService.saveRecipeToFavorites(userId, recipeId));
    }

    @Test
    void saveRecipeToFavorites_RecipeNotFound_ThrowsException() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(recipeRepository.existsById(recipeId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteRecipeService.saveRecipeToFavorites(userId, recipeId));
    }

    @Test
    void saveRecipeToFavorites_AlreadyExists_DoesNothing() throws ResourceNotFoundException {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(recipeRepository.existsById(recipeId)).thenReturn(true);
        when(favoriteRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(true);

        favoriteRecipeService.saveRecipeToFavorites(userId, recipeId);

        verify(favoriteRecipeRepository, never()).save(any(UserFavoriteRecipe.class));
    }

    @Test
    void removeRecipeFromFavorites_Success() {
        favoriteRecipeService.removeRecipeFromFavorites(userId, recipeId);
        verify(favoriteRecipeRepository, times(1)).deleteByUserIdAndRecipeId(userId, recipeId);
    }

    @Test
    void getUserFavoriteRecipes_Success() {
        UserFavoriteRecipe favorite = new UserFavoriteRecipe();
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        favorite.setRecipe(recipe);

        when(favoriteRecipeRepository.findByUserId(userId)).thenReturn(Collections.singletonList(favorite));
        when(mapperUtil.mapRecipeToDto(any(), any(), any())).thenReturn(recipeDto);
        when(recipeRepository.findAverageRatingByRecipeId(recipeId)).thenReturn(4.5);

        List<RecipeDto> result = favoriteRecipeService.getUserFavoriteRecipes(userId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(recipeId, result.get(0).getId());
    }

    @Test
    void isRecipeFavorite_ReturnsTrue() {
        when(favoriteRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(true);
        assertTrue(favoriteRecipeService.isRecipeFavorite(userId, recipeId));
    }

    @Test
    void isRecipeFavorite_ReturnsFalse() {
        when(favoriteRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(false);
        assertFalse(favoriteRecipeService.isRecipeFavorite(userId, recipeId));
    }
}