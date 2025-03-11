package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.IngredientDto;
import com.tifawinkitchen.recipeapp.model.enums.IngredientCategory;
import com.tifawinkitchen.recipeapp.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
        List<IngredientDto> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable Long id) {
        IngredientDto ingredientDto = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ingredientDto);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<IngredientDto>> getIngredientsByCategory(@PathVariable IngredientCategory category) {
        List<IngredientDto> ingredients = ingredientService.getIngredientsByCategory(category);
        return ResponseEntity.ok(ingredients);
    }

    @GetMapping("/search")
    public ResponseEntity<List<IngredientDto>> searchIngredients(@RequestParam String query) {
        List<IngredientDto> ingredients = ingredientService.searchIngredients(query);
        return ResponseEntity.ok(ingredients);
    }

    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient(@RequestBody IngredientDto ingredientDto) {
        IngredientDto createdIngredient = ingredientService.createIngredient(ingredientDto);
        return ResponseEntity.ok(createdIngredient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDto> updateIngredient(@PathVariable Long id, @RequestBody IngredientDto ingredientDto) {
        IngredientDto updatedIngredient = ingredientService.updateIngredient(id, ingredientDto);
        return ResponseEntity.ok(updatedIngredient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}