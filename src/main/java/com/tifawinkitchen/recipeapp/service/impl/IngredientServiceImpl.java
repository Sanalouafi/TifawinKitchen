package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.IngredientDto;
import com.tifawinkitchen.recipeapp.exception.AppException;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.Ingredient;
import com.tifawinkitchen.recipeapp.model.enums.IngredientCategory;
import com.tifawinkitchen.recipeapp.repository.IngredientRepository;
import com.tifawinkitchen.recipeapp.service.IngredientService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final MapperUtil mapperUtil;

    public IngredientServiceImpl(IngredientRepository ingredientRepository, MapperUtil mapperUtil) {
        this.ingredientRepository = ingredientRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(mapperUtil::mapIngredientToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IngredientDto getIngredientById(Long id) throws ResourceNotFoundException {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
        return mapperUtil.mapIngredientToDto(ingredient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientDto> getIngredientsByCategory(IngredientCategory category) {
        return ingredientRepository.findByCategory(category).stream()
                .map(mapperUtil::mapIngredientToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientDto> searchIngredients(String query) {
        return ingredientRepository.findByNameContainingIgnoreCase(query).stream()
                .map(mapperUtil::mapIngredientToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IngredientDto createIngredient(IngredientDto ingredientDto) throws AppException {
        if (ingredientRepository.findByNameIgnoreCase(ingredientDto.getName()).isPresent()) {
            throw new AppException("Ingredient with name " + ingredientDto.getName() + " already exists", HttpStatus.BAD_REQUEST);
        }

        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientDto.getName());
        ingredient.setCategory(ingredientDto.getCategory());

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return mapperUtil.mapIngredientToDto(savedIngredient);
    }

    @Override
    @Transactional
    public IngredientDto updateIngredient(Long id, IngredientDto ingredientDto) throws ResourceNotFoundException, AppException {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));

        if (!ingredient.getName().equalsIgnoreCase(ingredientDto.getName()) &&
                ingredientRepository.findByNameIgnoreCase(ingredientDto.getName()).isPresent()) {
            throw new AppException("Ingredient with name " + ingredientDto.getName() + " already exists", HttpStatus.BAD_REQUEST);
        }

        ingredient.setName(ingredientDto.getName());
        ingredient.setCategory(ingredientDto.getCategory());

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);
        return mapperUtil.mapIngredientToDto(updatedIngredient);
    }

    @Override
    @Transactional
    public void deleteIngredient(Long id) throws ResourceNotFoundException {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));

        ingredientRepository.delete(ingredient);
    }

    @Override
    @Transactional
    public Ingredient getOrCreateIngredient(String name, IngredientCategory category) {
        return ingredientRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(name);
                    newIngredient.setCategory(category);
                    return ingredientRepository.save(newIngredient);
                });
    }
}
