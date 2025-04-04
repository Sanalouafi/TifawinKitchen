package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.IngredientDto;
import com.tifawinkitchen.recipeapp.exception.AppException;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.Ingredient;
import com.tifawinkitchen.recipeapp.model.enums.IngredientCategory;
import com.tifawinkitchen.recipeapp.repository.IngredientRepository;
import com.tifawinkitchen.recipeapp.service.impl.IngredientServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private MapperUtil mapperUtil;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    private Ingredient ingredient;
    private IngredientDto ingredientDto;

    @BeforeEach
    void setUp() {
        ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Salt");
        ingredient.setCategory(IngredientCategory.CONDIMENT);

        ingredientDto = new IngredientDto();
        ingredientDto.setName("Salt");
        ingredientDto.setCategory(IngredientCategory.CONDIMENT);
    }

    @Test
    void getAllIngredients_Success() {
        when(ingredientRepository.findAll()).thenReturn(Collections.singletonList(ingredient));
        when(mapperUtil.mapIngredientToDto(any(Ingredient.class))).thenReturn(ingredientDto);

        List<IngredientDto> result = ingredientService.getAllIngredients();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getIngredientById_Success() throws ResourceNotFoundException {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(mapperUtil.mapIngredientToDto(any(Ingredient.class))).thenReturn(ingredientDto);

        IngredientDto result = ingredientService.getIngredientById(1L);

        assertNotNull(result);
        assertEquals("Salt", result.getName());
    }

    @Test
    void getIngredientById_NotFound_ThrowsException() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.getIngredientById(1L));
    }

    @Test
    void getIngredientsByCategory_Success() {
        when(ingredientRepository.findByCategory(any())).thenReturn(Collections.singletonList(ingredient));
        when(mapperUtil.mapIngredientToDto(any(Ingredient.class))).thenReturn(ingredientDto);

        List<IngredientDto> result = ingredientService.getIngredientsByCategory(IngredientCategory.CONDIMENT);

        assertFalse(result.isEmpty());
    }

    @Test
    void searchIngredients_Success() {
        when(ingredientRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(Collections.singletonList(ingredient));
        when(mapperUtil.mapIngredientToDto(any(Ingredient.class))).thenReturn(ingredientDto);

        List<IngredientDto> result = ingredientService.searchIngredients("salt");

        assertFalse(result.isEmpty());
    }

    @Test
    void createIngredient_Success() throws AppException {
        when(ingredientRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);
        when(mapperUtil.mapIngredientToDto(any(Ingredient.class))).thenReturn(ingredientDto);

        IngredientDto result = ingredientService.createIngredient(ingredientDto);

        assertNotNull(result);
    }

    @Test
    void createIngredient_NameExists_ThrowsException() {
        when(ingredientRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(ingredient));

        AppException exception = assertThrows(AppException.class,
                () -> ingredientService.createIngredient(ingredientDto));

        assertEquals("Ingredient with name Salt already exists", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void updateIngredient_Success() throws ResourceNotFoundException, AppException {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);
        when(mapperUtil.mapIngredientToDto(any(Ingredient.class))).thenReturn(ingredientDto);

        IngredientDto result = ingredientService.updateIngredient(1L, ingredientDto);

        assertNotNull(result);
    }

    @Test
    void deleteIngredient_Success() throws ResourceNotFoundException {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));

        ingredientService.deleteIngredient(1L);

        verify(ingredientRepository, times(1)).delete(any(Ingredient.class));
    }

    @Test
    void getOrCreateIngredient_ExistingIngredient() {
        when(ingredientRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(ingredient));

        Ingredient result = ingredientService.getOrCreateIngredient("Salt", IngredientCategory.CONDIMENT);

        assertNotNull(result);
        verify(ingredientRepository, never()).save(any(Ingredient.class));
    }

    @Test
    void getOrCreateIngredient_NewIngredient() {
        when(ingredientRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);

        Ingredient result = ingredientService.getOrCreateIngredient("Salt", IngredientCategory.CONDIMENT);

        assertNotNull(result);
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }
}