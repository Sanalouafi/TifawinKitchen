package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.*;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.*;
import com.tifawinkitchen.recipeapp.model.enums.*;
import com.tifawinkitchen.recipeapp.repository.*;
import com.tifawinkitchen.recipeapp.service.impl.RecipeServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

    @Mock private RecipeRepository recipeRepository;
    @Mock private UserRepository userRepository;
    @Mock private IngredientRepository ingredientRepository;
    @Mock private RatingRepository ratingRepository;
    @Mock private SearchHistoryRepository searchHistoryRepository;
    @Mock private RecipeIngredientRepository recipeIngredientRepository;
    @Mock private MapperUtil mapperUtil;
    @Mock private FavoriteRecipeService favoriteRecipeService;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    private RecipeCreateDto recipeCreateDto;
    private Recipe recipe;
    private RecipeDto recipeDto;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        recipeCreateDto = new RecipeCreateDto();
        recipeCreateDto.setName("Test Recipe");
        recipeCreateDto.setDescription("Test Description");
        recipeCreateDto.setSteps(List.of("step1", "step2", "step3"));
        recipeCreateDto.setDishType(DishType.MAIN_COURSE);
        recipeCreateDto.setPreparationTime(30);
        recipeCreateDto.setComplexity(RecipeComplexity.EASY);
        recipeCreateDto.setDietTypes(new HashSet<>(Collections.singletonList(DietType.VEGETARIAN)));

        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");
        recipe.setCreatedBy(user);

        recipeDto = new RecipeDto();
        recipeDto.setId(1L);
        recipeDto.setName("Test Recipe");
    }

    @Test
    void getAllRecipes_Success() {
        Page<Recipe> page = new PageImpl<>(Collections.singletonList(recipe));
        when(recipeRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapperUtil.mapRecipeToDto(any(Recipe.class), anyDouble(), anyInt())).thenReturn(recipeDto);

        List<RecipeDto> result = recipeService.getAllRecipes(0, 10, "name", true);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getRecipeById_Success() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(mapperUtil.mapRecipeToDto(any(Recipe.class), anyDouble(), anyInt())).thenReturn(recipeDto);

        RecipeDto result = recipeService.getRecipeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getRecipeById_NotFound_ThrowsException() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipeService.getRecipeById(1L));
    }

    @Test
    void createRecipe_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        when(mapperUtil.mapRecipeToDto(any(Recipe.class), anyDouble(), anyInt())).thenReturn(recipeDto);

        RecipeDto result = recipeService.createRecipe(recipeCreateDto, 1L);

        assertNotNull(result);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void updateRecipe_Success() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        when(mapperUtil.mapRecipeToDto(any(Recipe.class), anyDouble(), anyInt())).thenReturn(recipeDto);

        RecipeDto result = recipeService.updateRecipe(1L, recipeCreateDto);

        assertNotNull(result);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void deleteRecipe_Success() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(1L);

        verify(recipeRepository, times(1)).delete(any(Recipe.class));
    }

    @Test
    void searchRecipes_Success() {
        when(recipeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(recipe)));
        when(mapperUtil.mapRecipeToDto(any(Recipe.class), anyDouble(), anyInt())).thenReturn(recipeDto);

        RecipeSearchDto searchDto = new RecipeSearchDto();
        searchDto.setKeyword("test");

        List<RecipeDto> result = recipeService.searchRecipes(searchDto, 1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
