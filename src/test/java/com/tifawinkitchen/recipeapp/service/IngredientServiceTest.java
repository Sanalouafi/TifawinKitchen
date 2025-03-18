package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.IngredientDto;
import com.tifawinkitchen.recipeapp.exception.AppException;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.Ingredient;
import com.tifawinkitchen.recipeapp.repository.IngredientRepository;
import com.tifawinkitchen.recipeapp.service.impl.IngredientServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private MapperUtil mapperUtil;
    @InjectMocks
    private IngredientServiceImpl ingredientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllIngredients_Success() {
        when(ingredientRepository.findAll()).thenReturn(Collections.emptyList());

        List<IngredientDto> ingredients = ingredientService.getAllIngredients();

        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
        verify(ingredientRepository, times(1)).findAll();
    }

    @Test
    void getIngredientById_Success() throws ResourceNotFoundException {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Test Ingredient");

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));

        // Add this mock
        IngredientDto mockDto = new IngredientDto();
        mockDto.setName(ingredient.getName());
        when(mapperUtil.mapIngredientToDto(any(Ingredient.class))).thenReturn(mockDto);

        IngredientDto ingredientDto = ingredientService.getIngredientById(1L);

        assertNotNull(ingredientDto);
        assertEquals(ingredient.getName(), ingredientDto.getName());
        verify(ingredientRepository, times(1)).findById(1L);
    }
}