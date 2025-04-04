package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.*;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeDto> createRecipe(
            @Valid @RequestPart("recipe") RecipeCreateDto recipeCreateDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestParam Long userId) {

        if (imageFile != null && !imageFile.isEmpty()) {
            recipeCreateDto.setImageFile(imageFile);
        }

        RecipeDto createdRecipe = recipeService.createRecipe(recipeCreateDto, userId);
        return ResponseEntity.ok(createdRecipe);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRecipe(
            @PathVariable Long id,
            @RequestPart("recipe") @Valid RecipeCreateDto recipeUpdateDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {
            log.info("Received update for recipe ID: {}", id);

            if (imageFile != null && !imageFile.isEmpty()) {
                recipeUpdateDto.setImageFile(imageFile);
            } else if (recipeUpdateDto.getImageURL() == null) {
                // Preserve existing image if no new image is provided
                RecipeDto existingRecipe = recipeService.getRecipeById(id);
                recipeUpdateDto.setImageURL(existingRecipe.getImageURL());
            }

            RecipeDto updatedRecipe = recipeService.updateRecipe(id, recipeUpdateDto);
            return ResponseEntity.ok(updatedRecipe);
        } catch (ResourceNotFoundException e) {
            log.warn("Recipe not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Update failed for recipe {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Update failed",
                            "message", e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }


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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<RecipeDto>> searchRecipes(
            @Valid @RequestBody RecipeSearchDto searchDto,
            @RequestParam(required = false) Long userId) {
        List<RecipeDto> recipes = recipeService.searchRecipes(searchDto, userId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<RecipeDto>> getSimilarRecipes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") int count) {
        List<RecipeDto> similarRecipes = recipeService.getSimilarRecipes(id, count);
        return ResponseEntity.ok(similarRecipes);
    }

    @PostMapping("/{recipeId}/favorite")
    public ResponseEntity<Void> saveRecipeToFavorites(
            @PathVariable Long recipeId,
            @RequestParam Long userId) {
        recipeService.saveRecipeToUserFavorites(userId, recipeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{recipeId}/favorite")
    public ResponseEntity<Void> removeRecipeFromFavorites(
            @PathVariable Long recipeId,
            @RequestParam Long userId) {
        recipeService.removeRecipeFromUserFavorites(userId, recipeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/favorites")
    public ResponseEntity<List<RecipeDto>> getUserFavoriteRecipes(@PathVariable Long userId) {
        List<RecipeDto> favorites = recipeService.getUserFavoriteRecipes(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{recipeId}/is-favorite")
    public ResponseEntity<Boolean> isRecipeFavorite(
            @PathVariable Long recipeId,
            @RequestParam Long userId) {
        boolean isFavorite = recipeService.isRecipeFavoriteForUser(userId, recipeId);
        return ResponseEntity.ok(isFavorite);
    }
}