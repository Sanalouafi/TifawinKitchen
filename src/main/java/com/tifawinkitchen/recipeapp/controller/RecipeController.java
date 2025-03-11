package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.RecipeCreateDto;
import com.tifawinkitchen.recipeapp.dto.RecipeDto;
import com.tifawinkitchen.recipeapp.dto.RecipeSearchDto;
import com.tifawinkitchen.recipeapp.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<List<RecipeDto>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        List<RecipeDto> recipes = recipeService.getAllRecipes(page, size, sortBy, ascending);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long id) {
        RecipeDto recipeDto = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipeDto);
    }

    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(@RequestBody RecipeCreateDto recipeCreateDto, @RequestParam Long userId) {
        RecipeDto createdRecipe = recipeService.createRecipe(recipeCreateDto, userId);
        return ResponseEntity.ok(createdRecipe);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDto> updateRecipe(@PathVariable Long id, @RequestBody RecipeCreateDto recipeUpdateDto) {
        RecipeDto updatedRecipe = recipeService.updateRecipe(id, recipeUpdateDto);
        return ResponseEntity.ok(updatedRecipe);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<RecipeDto>> searchRecipes(@RequestBody RecipeSearchDto searchDto, @RequestParam(required = false) Long userId) {
        List<RecipeDto> recipes = recipeService.searchRecipes(searchDto, userId);
        return ResponseEntity.ok(recipes);
    }
}