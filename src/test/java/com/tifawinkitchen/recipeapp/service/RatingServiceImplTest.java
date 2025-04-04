package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.RatingDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.*;
import com.tifawinkitchen.recipeapp.repository.*;
import com.tifawinkitchen.recipeapp.service.impl.RatingServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock private RatingRepository ratingRepository;
    @Mock private UserRepository userRepository;
    @Mock private RecipeRepository recipeRepository;
    @Mock private MapperUtil mapperUtil;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private RatingDto ratingDto;
    private User user;
    private Recipe recipe;
    private Rating rating;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        recipe = new Recipe();
        recipe.setId(1L);

        ratingDto = new RatingDto();
        ratingDto.setRecipeId(1L);
        ratingDto.setStars(4);

        rating = new Rating();
        rating.setId(1L);
        rating.setUser(user);
        rating.setRecipe(recipe);
        rating.setStars(4);
    }

    @Test
    void getAverageRatingForRecipe_Success() {
        when(ratingRepository.findAverageRatingByRecipeId(anyLong())).thenReturn(4.5);

        Double result = ratingService.getAverageRatingForRecipe(1L);

        assertEquals(4.5, result);
    }

    @Test
    void rateRecipe_NewRating_Success() throws ResourceNotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserIdAndRecipeId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(mapperUtil.mapRatingToDto(any(Rating.class))).thenReturn(ratingDto);

        RatingDto result = ratingService.rateRecipe(ratingDto, 1L);

        assertNotNull(result);
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void rateRecipe_UpdateExisting_Success() throws ResourceNotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(ratingRepository.findByUserIdAndRecipeId(anyLong(), anyLong())).thenReturn(Optional.of(rating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(mapperUtil.mapRatingToDto(any(Rating.class))).thenReturn(ratingDto);

        RatingDto result = ratingService.rateRecipe(ratingDto, 1L);

        assertNotNull(result);
        assertEquals(4, result.getStars());
    }

    @Test
    void deleteRating_Success() throws ResourceNotFoundException {
        when(ratingRepository.findByUserIdAndRecipeId(anyLong(), anyLong())).thenReturn(Optional.of(rating));

        ratingService.deleteRating(1L, 1L);

        verify(ratingRepository, times(1)).delete(any(Rating.class));
    }

    @Test
    void deleteRating_NotFound_ThrowsException() {
        when(ratingRepository.findByUserIdAndRecipeId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ratingService.deleteRating(1L, 1L));
    }
}
