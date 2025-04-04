package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.MealPlanDto;
import com.tifawinkitchen.recipeapp.dto.RecipeDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.*;
import com.tifawinkitchen.recipeapp.repository.MealPlanRepository;
import com.tifawinkitchen.recipeapp.repository.RecipeRepository;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.RatingService;
import com.tifawinkitchen.recipeapp.service.impl.MealPlanServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealPlanServiceImplTest {

    @Mock
    private MealPlanRepository mealPlanRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private RatingService ratingService;
    @Mock
    private MapperUtil mapperUtil;

    @InjectMocks
    private MealPlanServiceImpl mealPlanService;

    private MealPlan mealPlan;
    private MealPlanDto mealPlanDto;
    private User user;
    private Recipe recipe;
    private RecipeDto recipeDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        recipe = new Recipe();
        recipe.setId(1L);

        recipeDto = new RecipeDto();
        recipeDto.setId(1L);

        mealPlan = new MealPlan();
        mealPlan.setId(1L);
        mealPlan.setUser(user);
        mealPlan.setName("Test Plan");
        mealPlan.setStartDate(LocalDate.now());
        mealPlan.setEndDate(LocalDate.now().plusDays(7));
        mealPlan.setRecipes(Collections.singletonList(recipe));

        mealPlanDto = new MealPlanDto();
        mealPlanDto.setName("Test Plan");
        mealPlanDto.setStartDate(LocalDate.now());
        mealPlanDto.setEndDate(LocalDate.now().plusDays(7));
        mealPlanDto.setRecipes(Collections.singletonList(recipeDto));
    }

    @Test
    void getUserMealPlans_Success() {
        when(mealPlanRepository.findByUserId(1L)).thenReturn(Collections.singletonList(mealPlan));
        when(mapperUtil.mapMealPlanToDto(any(MealPlan.class))).thenReturn(mealPlanDto);
        when(ratingService.getAverageRatingForRecipe(anyLong())).thenReturn(4.5);
        when(ratingService.getTotalRatingsForRecipe(anyLong())).thenReturn(10L);

        List<MealPlanDto> result = mealPlanService.getUserMealPlans(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getMealPlanById_Success() throws ResourceNotFoundException {
        when(mealPlanRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mealPlan));
        when(mapperUtil.mapMealPlanToDto(any(MealPlan.class))).thenReturn(mealPlanDto);
        when(ratingService.getAverageRatingForRecipe(anyLong())).thenReturn(4.5);
        when(ratingService.getTotalRatingsForRecipe(anyLong())).thenReturn(10L);

        MealPlanDto result = mealPlanService.getMealPlanById(1L, 1L);

        assertNotNull(result);
    }

    @Test
    void getMealPlanById_NotFound_ThrowsException() {
        when(mealPlanRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mealPlanService.getMealPlanById(1L, 1L));
    }

    @Test
    void createMealPlan_Success() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(mealPlanRepository.save(any(MealPlan.class))).thenReturn(mealPlan);
        when(mapperUtil.mapMealPlanToDto(any(MealPlan.class))).thenReturn(mealPlanDto);

        MealPlanDto result = mealPlanService.createMealPlan(mealPlanDto, 1L);

        assertNotNull(result);
    }

    @Test
    void updateMealPlan_Success() throws ResourceNotFoundException {
        when(mealPlanRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mealPlan));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(mealPlanRepository.save(any(MealPlan.class))).thenReturn(mealPlan);
        when(mapperUtil.mapMealPlanToDto(any(MealPlan.class))).thenReturn(mealPlanDto);

        MealPlanDto result = mealPlanService.updateMealPlan(1L, mealPlanDto, 1L);

        assertNotNull(result);
    }

    @Test
    void deleteMealPlan_Success() throws ResourceNotFoundException {
        when(mealPlanRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mealPlan));

        mealPlanService.deleteMealPlan(1L, 1L);

        verify(mealPlanRepository, times(1)).delete(any(MealPlan.class));
    }

    @Test
    void addRecipesToMealPlan_Success() throws ResourceNotFoundException {
        when(mealPlanRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mealPlan));
        when(recipeRepository.findAllById(anyList())).thenReturn(Collections.singletonList(recipe));

        mealPlanService.addRecipesToMealPlan(1L, Collections.singletonList(1L), 1L);

        verify(mealPlanRepository, times(1)).save(any(MealPlan.class));
    }

    @Test
    void removeRecipesFromMealPlan_Success() throws ResourceNotFoundException {
        when(mealPlanRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mealPlan));

        mealPlanService.removeRecipesFromMealPlan(1L, Collections.singletonList(1L), 1L);

        verify(mealPlanRepository, times(1)).save(any(MealPlan.class));
    }
}