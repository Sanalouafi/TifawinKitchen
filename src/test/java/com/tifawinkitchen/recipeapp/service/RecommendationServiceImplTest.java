package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.RecommendationDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.*;
import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.model.enums.RecommendationType;
import com.tifawinkitchen.recipeapp.repository.*;
import com.tifawinkitchen.recipeapp.service.RatingService;
import com.tifawinkitchen.recipeapp.service.impl.RecommendationServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SearchHistoryRepository searchHistoryRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private MapperUtil mapperUtil;
    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private User user;
    private Recipe recipe;
    private Recommendation recommendation;
    private RecommendationDto recommendationDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setCulinaryPreferences(Set.of(DietType.VEGETARIAN));

        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setDietTypes(Set.of(DietType.VEGETARIAN));

        recommendation = new Recommendation();
        recommendation.setId(1L);
        recommendation.setUser(user);
        recommendation.setRecipe(recipe);
        recommendation.setType(RecommendationType.BY_PREFERENCES);

        recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
    }

    @Test
    void getRecommendationsForUser_Success() {
        Page<Recommendation> page = new PageImpl<>(Collections.singletonList(recommendation));
        when(recommendationRepository.findByUserIdAndType(anyLong(), isNull(), any(Pageable.class)))
                .thenReturn(page);
        when(mapperUtil.mapRecommendationToDto(any(Recommendation.class), anyDouble(), anyInt()))
                .thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getRecommendationsForUser(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void generateRecommendations_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findAll()).thenReturn(Collections.singletonList(recipe));
        when(ratingRepository.findAverageRatingByRecipeId(anyLong())).thenReturn(4.5);

        Page<Recommendation> emptyPage = new PageImpl<>(Collections.emptyList());
        when(recommendationRepository.findByUserIdAndType(anyLong(), isNull(), any(Pageable.class)))
                .thenReturn(emptyPage);

        recommendationService.generateRecommendations(1L);

        verify(recommendationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void generateRecommendations_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recommendationService.generateRecommendations(1L));
    }

}